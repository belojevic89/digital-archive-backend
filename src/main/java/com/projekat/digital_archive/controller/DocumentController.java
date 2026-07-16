package com.projekat.digital_archive.controller;

import com.projekat.digital_archive.dto.DocumentRequestDTO;
import com.projekat.digital_archive.dto.DocumentResponseDTO;
import com.projekat.digital_archive.service.DocumentService;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "http://localhost:4200")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(
            DocumentService documentService
    ) {
        this.documentService = documentService;
    }

    @PostMapping
    public DocumentResponseDTO createDocument(
            @Valid @RequestBody DocumentRequestDTO dto,
            Authentication authentication
    ) {
        return documentService.createDocument(
                dto,
                authentication.getName()
        );
    }

    @GetMapping
    public Page<DocumentResponseDTO> getAllDocuments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return documentService.getAllDocuments(page, size);
    }

    @GetMapping("/{id}")
    public DocumentResponseDTO getDocumentById(
            @PathVariable Long id
    ) {
        return documentService.getDocumentById(id);
    }

    @GetMapping("/category/{category}")
    public List<DocumentResponseDTO> getDocumentsByCategory(
            @PathVariable String category
    ) {
        return documentService
                .getDocumentsByCategory(category);
    }

    @GetMapping("/box/{boxNumber}")
    public List<DocumentResponseDTO> getDocumentsByBox(
            @PathVariable String boxNumber
    ) {
        return documentService
                .getDocumnetByBoxNumber(boxNumber);
    }

    @GetMapping("/search")
    public Page<DocumentResponseDTO> searchDocuments(

            @RequestParam(required = false)
            String category,

            @RequestParam(required = false)
            String boxNumber,

            @RequestParam(required = false)
            String title,

            @RequestParam(required = false)
            String referenceNumber,

            @RequestParam(required = false)
            String description,

            @RequestParam(required = false)
            Integer year,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "5")
            int size
    ) {
        return documentService.searchDocuments(
                category,
                boxNumber,
                title,
                referenceNumber,
                description,
                year,
                page,
                size
        );
    }

    @GetMapping("/reference/{referenceNumber}")
    public DocumentResponseDTO getByReferenceNumber(
            @PathVariable String referenceNumber
    ) {
        return documentService
                .getByReferenceNumber(referenceNumber);
    }

    @DeleteMapping("/{id}")
    public void deleteDocument(
            @PathVariable Long id
    ) {
        documentService.deleteDocument(id);
    }

    @PutMapping("/{id}")
    public DocumentResponseDTO updateDocument(
            @PathVariable Long id,
            @Valid @RequestBody DocumentRequestDTO dto
    ) {
        return documentService.updateDocument(id, dto);
    }

    @PostMapping("/upload")
    public DocumentResponseDTO uploadPdf(
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ) {
        return documentService.uploadPdf(
                file,
                authentication.getName()
        );
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<Resource> openPdf(
            @PathVariable Long id
    ) {
        return documentService.openPdf(id);
    }
}