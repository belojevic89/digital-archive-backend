package com.projekat.digital_archive.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class DocumentRequestDTO {

@NotBlank(message = "Reference number is required")
private String referenceNumber;

@NotBlank(message = "Title is required")
private String title ;

@NotBlank(message = "Box number is required")
    private String boxNumber;
@NotBlank(message = "Category is required")
    private String category;

    private String description;

    @NotNull(message = "Year is required")
    private int year;
}
