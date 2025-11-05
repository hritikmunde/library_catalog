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
    public String search(@RequestParam(name = "q", required = false) String q,
                        @RequestParam(name = "author", required = false) String author,
                        @RequestParam(name = "available", required = false, defaultValue = "all") String available,
                        Model model) {

        String keyword = (q == null || q.isBlank()) ? null : q.trim();

        // Map "all" -> null, "true"/"false" -> Boolean
        Boolean availability = switch (available) {
            case "true"  -> Boolean.TRUE;
            case "false" -> Boolean.FALSE;
            default      -> null;
        };

        var results = (keyword == null)
                ? java.util.List.<edu.iu.p566.library_catalog.model.Book>of()
                : books.searchAdvanced(keyword,
                        (author == null || author.isBlank()) ? null : author,
                        availability);

        var authors = books.facetAuthors(keyword);

        model.addAttribute("query", q == null ? "" : q);
        model.addAttribute("books", results);
        model.addAttribute("authors", authors);
        model.addAttribute("selectedAuthor", author == null ? "" : author);
        model.addAttribute("selectedAvail", available);
        return "search";
    }
}
