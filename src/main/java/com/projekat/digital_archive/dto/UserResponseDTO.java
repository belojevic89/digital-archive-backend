package com.projekat.digital_archive.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDTO {

    private Long id;
    private String ime;
    private String prezime;
    private String email;
    private String role;
}