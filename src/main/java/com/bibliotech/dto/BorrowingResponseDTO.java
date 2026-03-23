package com.bibliotech.dto;

import com.bibliotech.entity.BorrowingStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BorrowingResponseDTO {

    private Long id;
    private String bookTitle;
    private String userName;
    private LocalDateTime borrowDate;
    private LocalDateTime returnDate;
    private BorrowingStatus status;
}
