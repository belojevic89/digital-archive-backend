package com.projekat.digital_archive.service;

import com.projekat.digital_archive.dto.UserRequestDTO;
import com.projekat.digital_archive.dto.UserResponseDTO;
import com.projekat.digital_archive.entity.Role;
import com.projekat.digital_archive.entity.User;
import com.projekat.digital_archive.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            BCryptPasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseDTO createUser(UserRequestDTO dto) {

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("User with this email already exists");
        }

        User user = new User();

        user.setIme(dto.getIme());
        user.setPrezime(dto.getPrezime());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.valueOf(dto.getRole().toUpperCase()));

        User savedUser = userRepository.save(user);

        return mapToDTO(savedUser);
    }

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    private UserResponseDTO mapToDTO(User user) {

        UserResponseDTO dto = new UserResponseDTO();

        dto.setId(user.getId());
        dto.setIme(user.getIme());
        dto.setPrezime(user.getPrezime());
        dto.setEmail(user.getEmail());

        if (user.getRole() != null) {
            dto.setRole(user.getRole().name());
        }

        return dto;
    }
}