package edu.iu.p566.library_catalog.config;

import edu.iu.p566.library_catalog.model.Book;
import edu.iu.p566.library_catalog.repository.BookRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoader {
    
    @Bean
    CommandLineRunner loadData(BookRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                System.out.println("Loading initial data...");
                repository.save(Book.builder()
                    .title("1984")
                    .author("George Orwell")
                    .isbn("9780451524935")
                    .description("A dystopian social science fiction novel and cautionary tale about the dangers of totalitarianism.")
                    .imageUrl("https://covers.openlibrary.org/b/id/7222246-L.jpg")
                    .available(true)
                    .build());
                repository.save(Book.builder()
                    .title("To Kill a Mockingbird")
                    .author("Harper Lee")
                    .isbn("9780061120084")
                    .description("A novel about the serious issues of rape and racial inequality, but it is also full of warmth and humor.")
                    .imageUrl("https://covers.openlibrary.org/b/id/15089752-L.jpg")
                    //https://covers.openlibrary.org/b/id/15089752-L.jpg
                    .available(true)
                    .build());
                repository.save(Book.builder()
                    .title("The Great Gatsby")
                    .author("F. Scott Fitzgerald")
                    .isbn("9780743273565")
                    .description("A novel that critiques the disillusionment and moral decay of the period following WWI.")
                    .imageUrl("https://covers.openlibrary.org/b/id/8238786-L.jpg")
                    //https://covers.openlibrary.org/b/id/8238786-L.jpg
                    .available(false)
                    .build());
            } else {
                System.out.println("Data already exists. Skipping data loading.");
                return;
            }           
        };
    }
}
