package com.bibliotech.service;

import com.bibliotech.dto.BorrowingRequestDTO;
import com.bibliotech.dto.BorrowingResponseDTO;
import com.bibliotech.entity.*;
import com.bibliotech.exception.EntityNotFoundException;
import com.bibliotech.mapper.BorrowingMapper;
import com.bibliotech.repository.BookRepository;
import com.bibliotech.repository.BorrowingRepository;
import com.bibliotech.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BorrowingService {

    private final BorrowingRepository borrowingRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final BorrowingMapper borrowingMapper;

    /**
     * Process a new borrowing with transactional atomicity.
     * Rules:
     * 1. Book must have stock > 0
     * 2. User must not have more than 3 ongoing borrowings
     * 3. Borrowing creation and stock decrement are atomic
     */
    @Transactional
    public BorrowingResponseDTO processBorrowing(BorrowingRequestDTO request) {
        // Fetch book
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new EntityNotFoundException("Book", request.getBookId()));

        // Fetch user
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User", request.getUserId()));

        // Rule 1: Check stock
        if (book.getStockDisponible() <= 0) {
            throw new IllegalStateException("Book '" + book.getTitle() + "' is out of stock");
        }

        // Rule 2: Check max 3 ongoing borrowings
        long ongoingCount = borrowingRepository.countByUserIdAndStatus(user.getId(), BorrowingStatus.ONGOING);
        if (ongoingCount >= 3) {
            throw new IllegalStateException("User already has 3 ongoing borrowings. Cannot borrow more.");
        }

        // Rule 3: Atomic borrowing creation + stock decrement
        book.setStockDisponible(book.getStockDisponible() - 1);
        bookRepository.save(book);

        Borrowing borrowing = Borrowing.builder()
                .book(book)
                .user(user)
                .borrowDate(LocalDateTime.now())
                .status(BorrowingStatus.ONGOING)
                .build();

        Borrowing saved = borrowingRepository.save(borrowing);
        return borrowingMapper.toResponseDTO(saved);
    }

    @Transactional
    public BorrowingResponseDTO returnBook(Long borrowingId) {
        Borrowing borrowing = borrowingRepository.findById(borrowingId)
                .orElseThrow(() -> new EntityNotFoundException("Borrowing", borrowingId));

        if (borrowing.getStatus() == BorrowingStatus.RETURNED) {
            throw new IllegalStateException("Borrowing has already been returned");
        }

        borrowing.setStatus(BorrowingStatus.RETURNED);
        borrowing.setReturnDate(LocalDateTime.now());
        borrowingRepository.save(borrowing);

        // Increment stock back
        Book book = borrowing.getBook();
        book.setStockDisponible(book.getStockDisponible() + 1);
        bookRepository.save(book);

        return borrowingMapper.toResponseDTO(borrowing);
    }

    @Transactional(readOnly = true)
    public List<BorrowingResponseDTO> getUserBorrowings(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User", userId);
        }
        return borrowingRepository.findByUserIdAndStatus(userId, BorrowingStatus.ONGOING)
                .stream()
                .map(borrowingMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}
