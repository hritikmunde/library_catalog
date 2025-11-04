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

@Controller
public class BookController {
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
        model.addAttribute(q, q);
        model.addAttribute("user", principal != null ? principal.getName() : null);
        return "checkout"; // This will look for a template named "checkout.html"
    }

    @PostMapping("/books/{id}/rent")
    @Transactional
    public String rentBook(@PathVariable Long id, @RequestParam(value = "q", required = false) String q, Principal principal) {
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
        if (!book.isAvailable() && book.getRequestedBy() == null) {
            book.setRequestedBy(principal.getName());
            bookRepository.save(book);
        }
        return "redirect:/books/" + id;
    }

    @PostMapping("/books/{id}/return")
    public String returnBook(@PathVariable Long id, @RequestParam(value = "q", required = false) String q, Principal principal) {
        Book book = bookRepository.findById(id).orElseThrow();
        if (!book.isAvailable() && principal != null && principal.getName().equals(book.getRentedBy())) {
            book.setAvailable(true);
            book.setRentedBy(null);
            book.setRentedAt(null);
            book.setDueDate(null);
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

    @Value("${app.loan-days:14}")
    private int loanDays;
}
