package com.projekat.digital_archive.service;

import com.projekat.digital_archive.dto.DocumentRequestDTO;
import com.projekat.digital_archive.dto.DocumentResponseDTO;
import com.projekat.digital_archive.entity.Document;
import com.projekat.digital_archive.entity.User;
import com.projekat.digital_archive.exception.DuplicateDocumentException;
import com.projekat.digital_archive.repository.DocumentRepository;
import com.projekat.digital_archive.repository.UserRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;

    public DocumentService(
            DocumentRepository documentRepository,
            UserRepository userRepository
    ) {
        this.documentRepository = documentRepository;
        this.userRepository = userRepository;
    }

    private User getAuthenticatedUser(String email) {

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Authenticated user not found"
                        )
                );
    }

    public DocumentResponseDTO createDocument(
            DocumentRequestDTO dto,
            String userEmail
    ) {
        Document existing =
                documentRepository
                        .findByReferenceNumberAndYear(
                                dto.getReferenceNumber(),
                                dto.getYear()
                        );

        if (existing != null) {
            throw new DuplicateDocumentException(
                    "Dokument sa ovim djelovodnim brojem " +
                            "vec postoji za ovu godinu"
            );
        }

        User user = getAuthenticatedUser(userEmail);

        Document document = new Document();

        document.setReferenceNumber(
                dto.getReferenceNumber()
        );
        document.setTitle(dto.getTitle());
        document.setBoxNumber(dto.getBoxNumber());
        document.setCategory(dto.getCategory());
        document.setDescription(dto.getDescription());
        document.setYear(dto.getYear());
        document.setCreatedBy(user);
        document.setCreatedDate(LocalDate.now());

        Document saved =
                documentRepository.save(document);

        return mapToDTO(saved);
    }

    public Page<DocumentResponseDTO> getAllDocuments(
            int page,
            int size
    ) {
        Pageable pageable =
                PageRequest.of(page, size);

        Page<Document> documents =
                documentRepository.findAll(pageable);

        return documents.map(this::mapToDTO);
    }

    public DocumentResponseDTO getDocumentById(
            Long id
    ) {
        Document document =
                documentRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Document not found"
                                )
                        );

        return mapToDTO(document);
    }

    public java.util.List<DocumentResponseDTO>
    getDocumentsByCategory(String category) {

        return documentRepository
                .findByCategory(category)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    public java.util.List<DocumentResponseDTO>
    getDocumnetByBoxNumber(String boxNumber) {

        return documentRepository
                .findByBoxNumber(boxNumber)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    public Page<DocumentResponseDTO> searchDocuments(
            String category,
            String boxNumber,
            String title,
            String referenceNumber,
            String description,
            Integer year,
            int page,
            int size
    ) {
        Pageable pageable =
                PageRequest.of(page, size);

        if (category != null &&
                category.trim().isEmpty()) {
            category = null;
        }

        if (boxNumber != null &&
                boxNumber.trim().isEmpty()) {
            boxNumber = null;
        }

        if (title != null &&
                title.trim().isEmpty()) {
            title = null;
        }

        if (referenceNumber != null &&
                referenceNumber.trim().isEmpty()) {
            referenceNumber = null;
        }

        if (description != null &&
                description.trim().isEmpty()) {
            description = null;
        }

        Page<Document> documents =
                documentRepository.searchDocuments(
                        category,
                        boxNumber,
                        title,
                        referenceNumber,
                        description,
                        year,
                        pageable
                );

        return documents.map(this::mapToDTO);
    }

    public DocumentResponseDTO getByReferenceNumber(
            String referenceNumber
    ) {
        Document document =
                documentRepository
                        .findByReferenceNumber(
                                referenceNumber
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Document not found"
                                )
                        );

        return mapToDTO(document);
    }

    private DocumentResponseDTO mapToDTO(
            Document document
    ) {
        DocumentResponseDTO dto =
                new DocumentResponseDTO();

        dto.setId(document.getId());
        dto.setReferenceNumber(
                document.getReferenceNumber()
        );
        dto.setTitle(document.getTitle());
        dto.setBoxNumber(document.getBoxNumber());
        dto.setCategory(document.getCategory());
        dto.setDescription(
                document.getDescription()
        );
        dto.setYear(document.getYear());
        dto.setCreatedDate(
                document.getCreatedDate()
        );
        dto.setFileName(document.getFileName());

        if (document.getCreatedBy() != null) {
            dto.setCreatedBy(
                    document.getCreatedBy().getIme()
            );
        } else {
            dto.setCreatedBy("N/A");
        }

        return dto;
    }

    public void deleteDocument(Long id) {

        Document document =
                documentRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Document not found"
                                )
                        );

        documentRepository.delete(document);
    }

    public DocumentResponseDTO updateDocument(
            Long id,
            DocumentRequestDTO dto
    ) {
        Document document =
                documentRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Document not found"
                                )
                        );

        Document duplicate =
                documentRepository
                        .findByReferenceNumberAndYear(
                                dto.getReferenceNumber(),
                                dto.getYear()
                        );

        if (
                duplicate != null &&
                        !duplicate.getId().equals(id)
        ) {
            throw new DuplicateDocumentException(
                    "Dokument sa ovim djelovodnim brojem " +
                            "vec postoji za ovu godinu"
            );
        }

        document.setReferenceNumber(
                dto.getReferenceNumber()
        );
        document.setTitle(dto.getTitle());
        document.setYear(dto.getYear());
        document.setCategory(dto.getCategory());
        document.setDescription(dto.getDescription());
        document.setBoxNumber(dto.getBoxNumber());

        Document updated =
                documentRepository.save(document);

        return mapToDTO(updated);
    }

    public DocumentResponseDTO uploadPdf(
            MultipartFile file,
            String userEmail
    ) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException(
                        "PDF fajl nije izabran."
                );
            }

            if (!"application/pdf".equals(
                    file.getContentType()
            )) {
                throw new RuntimeException(
                        "Dozvoljen je samo PDF fajl."
                );
            }

            User user =
                    getAuthenticatedUser(userEmail);

            String uploadDirectory = "uploads/";

            Files.createDirectories(
                    Paths.get(uploadDirectory)
            );

            String originalFileName =
                    file.getOriginalFilename();

            if (originalFileName == null ||
                    originalFileName.isBlank()) {
                originalFileName = "document.pdf";
            }

            String safeOriginalFileName =
                    Paths.get(originalFileName)
                            .getFileName()
                            .toString();

            String fileName =
                    System.currentTimeMillis()
                            + "_"
                            + safeOriginalFileName;

            Path filePath =
                    Paths.get(
                            uploadDirectory,
                            fileName
                    );

            Files.copy(
                    file.getInputStream(),
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            Document document = new Document();

            document.setFileName(fileName);
            document.setFilePath(
                    filePath.toString()
            );
            document.setCreatedDate(LocalDate.now());
            document.setCreatedBy(user);

            Document saved =
                    documentRepository.save(document);

            return mapToDTO(saved);

        } catch (IOException exception) {
            throw new RuntimeException(
                    "Upload failed: "
                            + exception.getMessage()
            );
        }
    }

    public ResponseEntity<Resource> openPdf(Long id) {

        try {
            Document document =
                    documentRepository
                            .findById(id)
                            .orElseThrow(() ->
                                    new ResponseStatusException(
                                            HttpStatus.NOT_FOUND,
                                            "Document not found"
                                    )
                            );

            if (document.getFilePath() == null) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "PDF file not found for this document"
                );
            }

            Path path =
                    Paths.get(document.getFilePath());

            Resource resource =
                    new UrlResource(path.toUri());

            if (!resource.exists()) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "PDF file does not exist on server"
                );
            }

            return ResponseEntity.ok()
                    .contentType(
                            MediaType.APPLICATION_PDF
                    )
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\""
                                    + document.getFileName()
                                    + "\""
                    )
                    .body(resource);

        } catch (MalformedURLException exception) {
            throw new RuntimeException(
                    "Could not open PDF file."
            );
        }
    }
}