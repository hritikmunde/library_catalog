package edu.iu.p566.library_catalog.controller;

import edu.iu.p566.library_catalog.model.Book;
import edu.iu.p566.library_catalog.repository.BookRepository;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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
    public String viewBook(@PathVariable Long id, Model model, Principal principal) {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid book Id:" + id));
        model.addAttribute("book", book);
        model.addAttribute("user", principal != null ? principal.getName() : null);
        
        return "detail"; // This will look for a template named "book_detail.html"
    }

    @PostMapping("/books/{id}/rent")
    public String rentBook(@PathVariable Long id, Principal principal) {
        Book book = bookRepository.findById(id).orElseThrow();
        if (book.isAvailable() && principal != null) {
            book.setAvailable(false);
            book.setRentedBy(principal.getName());
            bookRepository.save(book);
        }
        return "redirect:/books/" + id;
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
    public String returnBook(@PathVariable Long id, Principal principal) {
        Book book = bookRepository.findById(id).orElseThrow();
        if (!book.isAvailable() && principal != null && principal.getName().equals(book.getRentedBy())) {
            book.setAvailable(true);
            book.setRentedBy(null);
            bookRepository.save(book);
        }
        return "redirect:/books/" + id;
    }
}
