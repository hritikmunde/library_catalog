package edu.iu.p566.library_catalog.model;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalViewModel {
    
    @ModelAttribute("continueUrl")
    public String continueUrl(HttpServletRequest req) {
        if (req == null) {
            return "/";
        }
        String qs = req.getQueryString();
        String path = req.getRequestURI();
        return path + (qs == null || qs.isBlank() ? "" : "?" + qs);
    }
}
