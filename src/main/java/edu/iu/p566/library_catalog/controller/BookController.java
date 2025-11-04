package edu.iu.p566.library_catalog.controller;

import org.springframework.beans.factory.annotation.Value;
import java.time.LocalDate;
import java.time.LocalDateTime;

import edu.iu.p566.library_catalog.model.Book;
import edu.iu.p566.library_catalog.model.WaitlistEntry;
import edu.iu.p566.library_catalog.repository.BookRepository;
import edu.iu.p566.library_catalog.repository.WaitlistRepository;

//import jakarta.transaction.Transactional;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;
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
    private final WaitlistRepository waitlistRepository;

    public BookController(BookRepository bookRepository, WaitlistRepository waitlistRepository) {
        this.bookRepository = bookRepository;
        this.waitlistRepository = waitlistRepository;
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
        
        long queueSize = waitlistRepository.countByBookId(id);
        Integer myPosition = null;
        if (principal != null && queueSize > 0) {
            List<WaitlistEntry> list = waitlistRepository.findByBookIdOrderByCreatedAtAsc(id);
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getUsername().equals(principal.getName())) {
                    myPosition = i + 1;
                    break;
                }
            }
        }
        model.addAttribute("queueSize", queueSize);
        model.addAttribute("myPosition", myPosition);

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
    @Transactional
    public String requestBook(@PathVariable Long id, @RequestParam(value="q", required=false) String q, Principal principal) {
        
        if (principal == null) {
            String cont = "/books/" + id + (q != null ? ("?q=" + q) : "");
            return "redirect:/login?continue=" + cont;
        }
        Book book = bookRepository.findById(id).orElseThrow();
        if (book.isAvailable()) {
            return "redirect:/books/" + id + (q != null ? ("?q=" + q) : "");
        }
        if (!waitlistRepository.existsByBookIdAndUsername(id, principal.getName())) {
            waitlistRepository.save(WaitlistEntry.builder()
                    .bookId(id)
                    .username(principal.getName())
                    .createdAt(LocalDateTime.now())
                    .build());
        }
        return "redirect:/books/" + id + (q != null ? ("?q=" + q + "&msg=requested") : "?msg=requested");
    }
    @PostMapping("/books/{id}/unrequest")
    @Transactional
    public String unrequestBook(@PathVariable Long id,
                                @RequestParam(value="q", required=false) String q,
                                Principal principal) {
        if (principal != null) {
            waitlistRepository.deleteByBookIdAndUsername(id, principal.getName());
        }
        return "redirect:/books/" + id + (q != null ? ("?q=" + q + "&msg=unrequested") : "?msg=unrequested");
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

        // Book book = bookRepository.findById(id).orElseThrow();
        // boolean notRequested = waitlistRepository.countByBookId(id) == 0;

        // // must be the current renter
        // if (!principal.getName().equals(book.getRentedBy())) {
        //     ra.addFlashAttribute("error", "You can only extend a book you rented.");
        //     return "redirect:/books/" + id + (q != null ? ("?q=" + q) : "");
        // }

        // // do not extend if someone has requested it
        // if (book.getRequestedBy() != null && !book.getRequestedBy().isBlank()) {
        //     ra.addFlashAttribute("error", "Extension not allowed: another user has requested this book.");
        //     return "redirect:/books/" + id + (q != null ? ("?q=" + q) : "");
        // }

        // // honor maxExtensions
        // if (book.getExtensions() >= maxExtensions) {
        //     ra.addFlashAttribute("error", "You’ve already used your extension.");
        //     return "redirect:/books/" + id + (q != null ? ("?q=" + q) : "");
        // }

        // // extend from current due date (or today if missing)
        // var base = (book.getDueDate() != null ? book.getDueDate() : LocalDate.now());
        // book.setDueDate(base.plusDays(loanDays));
        // book.setExtensions(book.getExtensions() + 1);
        // bookRepository.save(book);

        // ra.addFlashAttribute("success", "Extension successful. New due date: " + book.getDueDate() + ".");
        // return "redirect:/books/" + id + (q != null ? ("?q=" + q) : "");
        Book b = bookRepository.findById(id).orElseThrow();
        boolean isRenter = principal.getName().equals(b.getRentedBy());
        boolean notRequested = waitlistRepository.countByBookId(id) == 0;
        if (!b.isAvailable() && isRenter && notRequested && b.getExtensions() < maxExtensions) {
            b.setDueDate(b.getDueDate().plusDays(loanDays));
            b.setExtensions(b.getExtensions() + 1);
            bookRepository.save(b);
            return "redirect:/books/" + id + (q != null ? ("?q=" + q + "&msg=extended") : "?msg=extended");
        }
        return "redirect:/books/" + id + (q != null ? ("?q=" + q + "&msg=extend_unavailable") : "?msg=extend_unavailable");
    }


    @PostMapping("/books/{id}/return")
    @Transactional
    public String returnBook(@PathVariable Long id, @RequestParam(value = "q", required = false) String q, Principal principal) {
        Book book = bookRepository.findById(id).orElseThrow();
        if (!book.isAvailable() && principal != null && principal.getName().equals(book.getRentedBy())) {
            book.setAvailable(true);
            book.setRentedBy(null);
            book.setRentedAt(null);
            book.setDueDate(null);
            book.setExtensions(0);
            bookRepository.save(book);
            
            var next = waitlistRepository.findFirstByBookIdOrderByCreatedAtAsc(id);
            if (next.isPresent()) {
                Book b = bookRepository.findById(id).orElseThrow();
                b.setAvailable(false);
                b.setRentedBy(next.get().getUsername());
                b.setRentedAt(LocalDate.now());
                b.setDueDate(LocalDate.now().plusDays(loanDays));
                b.setExtensions(0);
                bookRepository.save(b);
                waitlistRepository.deleteById(next.get().getId());
                // optional: add a message parameter to indicate reassignment
                return "redirect:/books/" + id + (q != null ? ("?q=" + q + "&msg=returned_reassigned") : "?msg=returned_reassigned");
            }
            return "redirect:/books/" + id + (q != null ? ("?q=" + q + "&msg=returned") : "?msg=returned");
        }
        return "redirect:/books/" + id + (q != null ? ("?q=" + q) : "");
    }

    @GetMapping("/my-books")
    public String myBooks(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login?continue=/my-books";
        }
        String user = principal.getName();
        model.addAttribute("user", user);
        model.addAttribute("rentals", bookRepository.findByRentedBy(user));
        var entries = waitlistRepository.findByBookIdOrderByCreatedAtAsc(null);
        record MyRequest(Book book, Integer position) {}
        var myRequests = new java.util.ArrayList<MyRequest>();
        for (Book b : bookRepository.findAll()) {
            List<edu.iu.p566.library_catalog.model.WaitlistEntry> queue =
                waitlistRepository.findByBookIdOrderByCreatedAtAsc(b.getId());
            for (int i=0; i<queue.size(); i++) {
                if (queue.get(i).getUsername().equals(user)) {
                    myRequests.add(new MyRequest(b, i+1));
                    break;
                }
            }
        }
        model.addAttribute("requests", myRequests);
        model.addAttribute("maxExtensions", maxExtensions);
        return "my_books"; // This will look for a template named "mybooks.html"
    }
}
