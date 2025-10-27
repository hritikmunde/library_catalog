package edu.iu.p566.library_catalog.repository;

import edu.iu.p566.library_catalog.model.Book;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends CrudRepository<Book, Long> {
    @Query("""
            SELECT b FROM Book b
            WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(b.isbn) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    List<Book> search(@Param("keyword") String keyword);

    @Modifying
    @Query("""
            UPDATE Book b
            SET b.available = false,
                b.rentedBy = :username
            WHERE b.id = :id
                AND b.available = true
                AND b.rentedBy IS NULL
    """)
    int rentBook(@Param("id") Long id, @Param("username") String username);
}
