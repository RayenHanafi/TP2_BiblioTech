package com.bibliotech.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuthResponseDTO {

    private String token;
    private String username;
    private String role;
}
