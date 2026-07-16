package com.projekat.digital_archive.repository;

import com.projekat.digital_archive.entity.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository
        extends JpaRepository<Document, Long> {

    List<Document> findByCategory(
            String category
    );

    List<Document> findByBoxNumber(
            String boxNumber
    );

    Page<Document> findByCategoryAndBoxNumber(
            String category,
            String boxNumber,
            Pageable pageable
    );

    Page<Document> findByBoxNumberContainingIgnoreCase(
            String boxNumber,
            Pageable pageable
    );

    Page<Document> findByCategoryContainingIgnoreCase(
            String category,
            Pageable pageable
    );

    Page<Document>
    findByCategoryContainingIgnoreCaseAndBoxNumberContainingIgnoreCase(
            String category,
            String boxNumber,
            Pageable pageable
    );

    Optional<Document> findByReferenceNumber(
            String referenceNumber
    );

    Document findByReferenceNumberAndYear(
            String referenceNumber,
            int year
    );

    @Query("""
        SELECT d FROM Document d
        WHERE
        (:category IS NULL
            OR LOWER(d.category)
            LIKE LOWER(CONCAT('%', :category, '%')))
        AND (:boxNumber IS NULL
            OR LOWER(d.boxNumber)
            LIKE LOWER(CONCAT('%', :boxNumber, '%')))
        AND (:title IS NULL
            OR LOWER(d.title)
            LIKE LOWER(CONCAT('%', :title, '%')))
        AND (:referenceNumber IS NULL
            OR LOWER(d.referenceNumber)
            LIKE LOWER(CONCAT('%', :referenceNumber, '%')))
        AND (:description IS NULL
            OR LOWER(d.description)
            LIKE LOWER(CONCAT('%', :description, '%')))
        AND (:year IS NULL OR d.year = :year)
        """)
    Page<Document> searchDocuments(
            @Param("category")
            String category,

            @Param("boxNumber")
            String boxNumber,

            @Param("title")
            String title,

            @Param("referenceNumber")
            String referenceNumber,

            @Param("description")
            String description,

            @Param("year")
            Integer year,

            Pageable pageable
    );
}