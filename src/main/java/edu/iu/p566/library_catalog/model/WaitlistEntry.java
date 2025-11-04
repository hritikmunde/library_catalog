package edu.iu.p566.library_catalog.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "waitlist", uniqueConstraints = {
    @UniqueConstraint(name = "uq_waitlist_book_user", columnNames = {"book_id", "username"})
})
public class WaitlistEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="book_id", nullable=false)
    private Long bookId;

    @Column(nullable=false)
    private String username;

    @Column(name="created_at", nullable=false)
    private LocalDateTime createdAt;

    @Version
    private Long version;
}
