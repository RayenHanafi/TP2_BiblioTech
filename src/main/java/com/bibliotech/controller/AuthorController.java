package com.bibliotech.controller;

import com.bibliotech.dto.AuthorRequestDTO;
import com.bibliotech.dto.AuthorResponseDTO;
import com.bibliotech.entity.Author;
import com.bibliotech.repository.AuthorRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorRepository authorRepository;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthorResponseDTO> createAuthor(@Valid @RequestBody AuthorRequestDTO request) {
        Author newAuthor = Author.builder()
                .name(request.getName())
                .biography(request.getBiography())
                .build();

        Author savedAuthor = authorRepository.save(newAuthor);

        AuthorResponseDTO response = AuthorResponseDTO.builder()
                .id(savedAuthor.getId())
                .name(savedAuthor.getName())
                .biography(savedAuthor.getBiography())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
