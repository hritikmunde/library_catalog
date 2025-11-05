package edu.iu.p566.library_catalog.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import edu.iu.p566.library_catalog.model.WaitlistEntry;

public interface WaitlistRepository extends CrudRepository<WaitlistEntry, Long> {
    boolean existsByBookIdAndUsername(Long bookId, String username);
    List<WaitlistEntry> findByBookIdOrderByCreatedAtAsc(Long bookId);
    Optional<WaitlistEntry> findFirstByBookIdOrderByCreatedAtAsc(Long bookId);
    long countByBookId(Long bookId);
    void deleteByBookIdAndUsername(Long bookId, String username);
}
