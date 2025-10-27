package edu.iu.p566.library_catalog.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String author;
    private String isbn;

    @Builder.Default
    private boolean available = true;

    private String rentedBy;
    private String requestedBy;

    @Column(length = 2000)
    private String description;

    private String imageUrl;

    // Rental dates
    private LocalDate rentedAt;
    private LocalDate dueDate;
}
