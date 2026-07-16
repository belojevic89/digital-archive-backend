package com.projekat.digital_archive.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter

public class DocumentResponseDTO {

    private Long id ;
    private String referenceNumber ;
    private String title ;
    private String boxNumber;
    private String category;
    private String description;
    private int year;
    private String createdBy;
    private LocalDate createdDate;
    private String fileName;
}
