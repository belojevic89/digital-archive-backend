package com.projekat.digital_archive.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor


public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    private String ime;
    private String prezime;
    private String email;

    @JsonIgnore
    private String password;

    @Enumerated (EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "createdBy")
    @JsonIgnore
    private List<Document> documents;
}
