package com.bibliotech.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuthorRequestDTO {

    @NotBlank(message = "Name is required")
    private String name;

    private String biography;
}
