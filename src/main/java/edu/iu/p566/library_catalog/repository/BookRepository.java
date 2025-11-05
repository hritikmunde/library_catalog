package edu.iu.p566.library_catalog.repository;

import edu.iu.p566.library_catalog.model.Book;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface BookRepository extends CrudRepository<Book, Long> {
    @Query("""
            SELECT b FROM Book b
            WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(b.isbn) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    List<Book> search(@Param("keyword") String keyword);

    List<Book> findByRentedBy(String username);
    List<Book> findByRequestedBy(String username);

    @Transactional
    @Modifying
    @Query("""
            UPDATE Book b
            SET b.available = false,
                b.rentedBy = :username
            WHERE b.id = :id
                AND b.available = true
                AND (b.rentedBy IS NULL OR b.rentedBy = '')
    """)
    int rentBook(@Param("id") Long id, @Param("username") String username);

    @Query("""
            SELECT b FROM Book b
            WHERE (
                :keyword IS NULL OR
                LOWER(b.title)  LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                LOWER(b.isbn)   LIKE LOWER(CONCAT('%', :keyword, '%'))
            )
            AND (:author IS NULL OR b.author = :author)
            AND (:availability IS NULL OR b.available = :availability)
            ORDER BY b.title
    """)
    List<Book> searchAdvanced(@Param("keyword") String keyword,
                            @Param("author") String author,
                            @Param("availability") Boolean availability);

    @Query("""
            SELECT DISTINCT b.author FROM Book b
            WHERE (
                :keyword IS NULL OR
                LOWER(b.title)  LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                LOWER(b.isbn)   LIKE LOWER(CONCAT('%', :keyword, '%'))
            )
            ORDER BY b.author
    """)
    List<String> facetAuthors(@Param("keyword") String keyword);
}
