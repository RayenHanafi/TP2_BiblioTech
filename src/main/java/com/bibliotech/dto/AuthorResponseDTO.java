package com.bibliotech.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuthorResponseDTO {

    private Long id;
    private String name;
    private String biography;
}
