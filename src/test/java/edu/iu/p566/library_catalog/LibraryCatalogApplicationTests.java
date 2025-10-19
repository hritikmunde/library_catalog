package edu.iu.p566.library_catalog;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "testuser", roles = "USER")
class LibraryCatalogApplicationTests {

	@Test
	void contextLoads() {
	}

}
