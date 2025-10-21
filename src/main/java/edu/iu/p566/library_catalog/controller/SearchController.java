package edu.iu.p566.library_catalog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.iu.p566.library_catalog.repository.BookRepository;;

@Controller
public class SearchController {
    
    private final BookRepository books;

    public SearchController(BookRepository books) {
        this.books = books;
    }

    @GetMapping("/search")
    public String search(@RequestParam(name = "q", required = false) String q, Model model) {
        String query = (q == null) ? "" : q.trim();
        model.addAttribute("query", query);
        model.addAttribute("books", query.isBlank() ? java.util.List.of() : books.search(query));
        return "search";
    }
}
