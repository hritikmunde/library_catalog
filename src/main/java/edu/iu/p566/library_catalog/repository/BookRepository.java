package edu.iu.p566.library_catalog.repository;

import edu.iu.p566.library_catalog.model.Book;
import org.springframework.data.repository.CrudRepository;

public interface BookRepository extends CrudRepository<Book, Long> {
}
