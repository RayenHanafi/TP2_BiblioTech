package com.bibliotech.controller;

import com.bibliotech.dto.BorrowingRequestDTO;
import com.bibliotech.dto.BorrowingResponseDTO;
import com.bibliotech.service.BorrowingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/borrowings")
@RequiredArgsConstructor
public class BorrowingController {

    private final BorrowingService borrowingService;

    @PostMapping("/checkout")
    public ResponseEntity<BorrowingResponseDTO> checkout(@Valid @RequestBody BorrowingRequestDTO request) {
        BorrowingResponseDTO response = borrowingService.processBorrowing(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/return/{id}")
    public ResponseEntity<BorrowingResponseDTO> returnBook(@PathVariable Long id) {
        return ResponseEntity.ok(borrowingService.returnBook(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BorrowingResponseDTO>> getUserBorrowings(@PathVariable Long userId) {
        return ResponseEntity.ok(borrowingService.getUserBorrowings(userId));
    }
}
