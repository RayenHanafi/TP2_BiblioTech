package com.bibliotech.dto;

import lombok.*;

import java.util.Set;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BookResponseDTO {

    private Long id;
    private String isbn;
    private String title;
    private Integer stockDisponible;
    private String authorName;
    private Set<String> categories;
}
