package edu.iu.p566.library_catalog.config;

import edu.iu.p566.library_catalog.model.Book;
import edu.iu.p566.library_catalog.repository.BookRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("prod") // Only runs in production (Render)
public class ProdDataLoader {

    @Bean
    CommandLineRunner loadProdData(BookRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                System.out.println("Loading initial data into Postgres (prod)...");
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
                    .available(true)
                    .build());

                repository.save(Book.builder()
                    .title("The Great Gatsby")
                    .author("F. Scott Fitzgerald")
                    .isbn("9780743273565")
                    .description("A critique of the American Dream, exploring wealth, love, and disillusionment in the Jazz Age.")
                    .imageUrl("https://covers.openlibrary.org/b/id/8238786-L.jpg")
                    .available(true)
                    .build());

                repository.save(Book.builder()
                    .title("Pride and Prejudice")
                    .author("Jane Austen")
                    .isbn("9781503290563")
                    .description("A classic romantic comedy about the manners and matrimonial machinations among the British gentry.")
                    .imageUrl("https://covers.openlibrary.org/b/id/8091016-L.jpg")
                    .available(true)
                    .build());

                repository.save(Book.builder()
                    .title("The Catcher in the Rye")
                    .author("J.D. Salinger")
                    .isbn("9780316769488")
                    .description("The story of Holden Caulfield, a teenager navigating alienation and rebellion in 1950s New York.")
                    .imageUrl("https://covers.openlibrary.org/b/id/8226191-L.jpg")
                    .available(true)
                    .build());

                repository.save(Book.builder()
                    .title("Brave New World")
                    .author("Aldous Huxley")
                    .isbn("9780060850524")
                    .description("A visionary novel exploring a futuristic society driven by technological advancements and social conditioning.")
                    .imageUrl("https://covers.openlibrary.org/b/id/9254096-L.jpg")
                    .available(true)
                    .build());

                repository.save(Book.builder()
                    .title("Moby Dick")
                    .author("Herman Melville")
                    .isbn("9781503280786")
                    .description("An epic tale of obsession and revenge, following Captain Ahab's quest for the white whale.")
                    .imageUrl("https://covers.openlibrary.org/b/id/7222276-L.jpg")
                    .available(true)
                    .build());

                repository.save(Book.builder()
                    .title("The Hobbit")
                    .author("J.R.R. Tolkien")
                    .isbn("9780547928227")
                    .description("A fantasy adventure that follows Bilbo Baggins as he embarks on a quest to reclaim a stolen treasure guarded by a dragon.")
                    .imageUrl("https://covers.openlibrary.org/b/id/8101351-L.jpg")
                    .available(true)
                    .build());

                repository.save(Book.builder()
                    .title("Fahrenheit 451")
                    .author("Ray Bradbury")
                    .isbn("9781451673319")
                    .description("A dystopian story where books are outlawed and 'firemen' burn them to suppress independent thought.")
                    .imageUrl("https://covers.openlibrary.org/b/id/9254363-L.jpg")
                    .available(true)
                    .build());

                repository.save(Book.builder()
                    .title("The Lord of the Rings: The Fellowship of the Ring")
                    .author("J.R.R. Tolkien")
                    .isbn("9780547928210")
                    .description("The first volume of Tolkien's epic saga about the struggle between good and evil for control of Middle-earth.")
                    .imageUrl("https://covers.openlibrary.org/b/id/8231992-L.jpg")
                    .available(true)
                    .build());

                System.out.println("✅ Production data seeded successfully.");
            } else {
                System.out.println("Data already exists in Postgres. Skipping seeding.");
            }
        };
    }
}