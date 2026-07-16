package com.projekat.digital_archive.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Document {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id ;
    private String referenceNumber;
    private int year ;
    private String title;
    private String boxNumber ;
    private String category ;
    private String description ;
    private LocalDate createdDate ;
    private String fileName;
    private String filePath;


    @ManyToOne
    @JoinColumn(name="user_id")

    private User createdBy ;



}
