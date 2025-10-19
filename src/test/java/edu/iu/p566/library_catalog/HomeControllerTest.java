package edu.iu.p566.library_catalog;

import org.junit.jupiter.api.Test;
import static org.hamcrest.Matchers.containsString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import edu.iu.p566.library_catalog.controller.HomeController;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest (HomeController.class)
public class HomeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testHome() throws Exception {
        mockMvc.perform(get("/"))
               .andExpect(status().isOk())
               .andExpect(view().name("home"))
               .andExpect(content().string(containsString("Welcome to the Library Catalog")));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    public void testUser() throws Exception {
        mockMvc.perform(get("/")).andExpect(status().isOk());
    }
}
