package edu.iu.p566.library_catalog.controller;

import org.springframework.beans.factory.annotation.Value;
import java.time.LocalDate;

import edu.iu.p566.library_catalog.model.Book;
import edu.iu.p566.library_catalog.repository.BookRepository;
//import jakarta.transaction.Transactional;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class BookController {

    @Value("${app.loan-days:14}")
    private int loanDays;

    @Value("${app.max-extensions:1}")
    private int maxExtensions;

    private final BookRepository bookRepository;

    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // @GetMapping("/books")
    // public String listBooks(Model model) {
    //     model.addAttribute("books", bookRepository.findAll());
    //     return "list"; // This will look for a template named "book_list.html"
    // }

    @GetMapping("/books/{id}")
    public String viewBook(@PathVariable Long id, @RequestParam(value="q", required = false) String q, Model model, Principal principal) {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid book Id:" + id));
        model.addAttribute("book", book);
        model.addAttribute("user", principal != null ? principal.getName() : null);
        model.addAttribute("q", q);
        model.addAttribute("maxExtensions", maxExtensions);
        
        return "detail"; // This will look for a template named "book_detail.html"
    }

    @GetMapping("/books/{id}/checkout")
    public String checkoutBook(@PathVariable Long id, @RequestParam(value = "q", required = false) String q, Model model, Principal principal) {
        Book book = bookRepository.findById(id).orElseThrow();
        if (!book.isAvailable()) {
            return "redirect:/books/" + id + (q != null ? ("?q=" + q + "&msg=unavailable") : "?msg=unavailable");
        }
        model.addAttribute("dueDate", LocalDate.now().plusDays(loanDays));
        model.addAttribute("book", book);
        model.addAttribute("q", q);
        model.addAttribute("maxExtensions", maxExtensions);
        model.addAttribute("user", principal != null ? principal.getName() : null);
        return "checkout"; // This will look for a template named "checkout.html"
    }

    @PostMapping("/books/{id}/rent")
    @Transactional
    public String rentBook(@PathVariable Long id, @RequestParam(value = "q", required = false) String q, Principal principal, RedirectAttributes ra) {
        if (principal == null) {
            // Safety (Security already requires auth for this route)
            String cont = "/books/" + id + "/checkout" + (q != null ? ("?q=" + q) : "");
            return "redirect:/login?continue=" + cont;
        }
        // Atomic update; returns 1 if we successfully flipped to rented
        int updated = bookRepository.rentBook(id, principal.getName());
        if (updated == 1) {
            Book b = bookRepository.findById(id).orElseThrow();
            b.setRentedAt(LocalDate.now());
            b.setDueDate(LocalDate.now().plusDays(loanDays));
            b.setExtensions(0);
            bookRepository.save(b);

            return "redirect:/books/" + id + (q != null ? ("?q=" + q + "&msg=rented") : "?msg=rented");
        } else {
            // someone else rented first
            return "redirect:/books/" + id + (q != null ? ("?q=" + q + "&msg=unavailable") : "?msg=unavailable");
        }
    }

    @PostMapping("/books/{id}/request")
    public String requestBook(@PathVariable Long id, Principal principal) {
        Book book = bookRepository.findById(id).orElseThrow();
        if (principal == null) return "redirect:/login?continue=/books/" + id;
        if (principal.getName().equals(book.getRentedBy())) {
            return "redirect:/books/" + id + "?msg=you-already-have-it";
        }
        if (!book.isAvailable() && book.getRequestedBy() == null) {
            book.setRequestedBy(principal.getName());
            bookRepository.save(book);
        }
        return "redirect:/books/" + id;
    }

    @PostMapping("/books/{id}/extend")
    @Transactional
    public String extendBook(@PathVariable Long id,
                            @RequestParam(value = "q", required = false) String q,
                            Principal principal,
                            RedirectAttributes ra) {
        if (principal == null) {
            return "redirect:/login?continue=/books/" + id + (q != null ? ("?q=" + q) : "");
        }

        Book book = bookRepository.findById(id).orElseThrow();

        // must be the current renter
        if (!principal.getName().equals(book.getRentedBy())) {
            ra.addFlashAttribute("error", "You can only extend a book you rented.");
            return "redirect:/books/" + id + (q != null ? ("?q=" + q) : "");
        }

        // do not extend if someone has requested it
        if (book.getRequestedBy() != null && !book.getRequestedBy().isBlank()) {
            ra.addFlashAttribute("error", "Extension not allowed: another user has requested this book.");
            return "redirect:/books/" + id + (q != null ? ("?q=" + q) : "");
        }

        // honor maxExtensions
        if (book.getExtensions() >= maxExtensions) {
            ra.addFlashAttribute("error", "You’ve already used your extension.");
            return "redirect:/books/" + id + (q != null ? ("?q=" + q) : "");
        }

        // extend from current due date (or today if missing)
        var base = (book.getDueDate() != null ? book.getDueDate() : LocalDate.now());
        book.setDueDate(base.plusDays(loanDays));
        book.setExtensions(book.getExtensions() + 1);
        bookRepository.save(book);

        ra.addFlashAttribute("success", "Extension successful. New due date: " + book.getDueDate() + ".");
        return "redirect:/books/" + id + (q != null ? ("?q=" + q) : "");
    }


    @PostMapping("/books/{id}/return")
    public String returnBook(@PathVariable Long id, @RequestParam(value = "q", required = false) String q, Principal principal) {
        Book book = bookRepository.findById(id).orElseThrow();
        if (!book.isAvailable() && principal != null && principal.getName().equals(book.getRentedBy())) {
            book.setAvailable(true);
            book.setRentedBy(null);
            book.setRentedAt(null);
            book.setDueDate(null);
            book.setExtensions(0);
            bookRepository.save(book);
            return "redirect:/books/" + id + (q != null ? ("?q=" + q + "&msg=returned") : "?msg=returned");
        }
        return "redirect:/books/" + id + (q != null ? ("?q=" + q) : "");
    }

    @GetMapping("/my-books")
    public String myBooks(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login?redirect=/my-books";
        }
        String user = principal.getName();
        model.addAttribute("rentals", bookRepository.findByRentedBy(user));
        model.addAttribute("requests", bookRepository.findByRequestedBy(user));
        model.addAttribute("user", user);
        return "my_books"; // This will look for a template named "mybooks.html"
    }
}
