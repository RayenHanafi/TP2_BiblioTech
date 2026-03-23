package com.bibliotech.service;

import com.bibliotech.dto.BorrowingRequestDTO;
import com.bibliotech.dto.BorrowingResponseDTO;
import com.bibliotech.entity.*;
import com.bibliotech.exception.EntityNotFoundException;
import com.bibliotech.mapper.BorrowingMapper;
import com.bibliotech.repository.BookRepository;
import com.bibliotech.repository.BorrowingRepository;
import com.bibliotech.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BorrowingServiceTest {

    @Mock
    private BorrowingRepository borrowingRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BorrowingMapper borrowingMapper;

    @InjectMocks
    private BorrowingService borrowingService;

    @Test
    @DisplayName("Case 1: Successful borrowing - stock decremented and borrowing saved")
    void processBorrowing_success() {
        // Arrange
        Long bookId = 1L;
        Long userId = 1L;

        Book book = Book.builder()
                .id(bookId)
                .isbn("978-3-16-148410-0")
                .title("Clean Code")
                .stockDisponible(5)
                .build();

        User user = User.builder()
                .id(userId)
                .username("john")
                .build();

        BorrowingRequestDTO request = BorrowingRequestDTO.builder()
                .bookId(bookId)
                .userId(userId)
                .build();

        Borrowing savedBorrowing = Borrowing.builder()
                .id(1L)
                .book(book)
                .user(user)
                .borrowDate(LocalDateTime.now())
                .status(BorrowingStatus.ONGOING)
                .build();

        BorrowingResponseDTO expectedResponse = BorrowingResponseDTO.builder()
                .id(1L)
                .bookTitle("Clean Code")
                .userName("john")
                .status(BorrowingStatus.ONGOING)
                .build();

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(borrowingRepository.countByUserIdAndStatus(userId, BorrowingStatus.ONGOING)).thenReturn(0L);
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(borrowingRepository.save(any(Borrowing.class))).thenReturn(savedBorrowing);
        when(borrowingMapper.toResponseDTO(any(Borrowing.class))).thenReturn(expectedResponse);

        // Act
        BorrowingResponseDTO result = borrowingService.processBorrowing(request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getBookTitle()).isEqualTo("Clean Code");
        assertThat(result.getStatus()).isEqualTo(BorrowingStatus.ONGOING);
        assertThat(book.getStockDisponible()).isEqualTo(4); // Stock decremented

        verify(bookRepository).save(book);
        verify(borrowingRepository).save(any(Borrowing.class));
    }

    @Test
    @DisplayName("Case 2: Fail - Book out of stock throws IllegalStateException")
    void processBorrowing_outOfStock_throwsException() {
        // Arrange
        Long bookId = 1L;
        Long userId = 1L;

        Book book = Book.builder()
                .id(bookId)
                .isbn("978-3-16-148410-0")
                .title("Clean Code")
                .stockDisponible(0) // No stock!
                .build();

        User user = User.builder()
                .id(userId)
                .username("john")
                .build();

        BorrowingRequestDTO request = BorrowingRequestDTO.builder()
                .bookId(bookId)
                .userId(userId)
                .build();

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act & Assert
        assertThatThrownBy(() -> borrowingService.processBorrowing(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("out of stock");

        verify(borrowingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Case 3: Fail - User has 3+ ongoing borrowings throws IllegalStateException")
    void processBorrowing_maxBorrowingsReached_throwsException() {
        // Arrange
        Long bookId = 1L;
        Long userId = 1L;

        Book book = Book.builder()
                .id(bookId)
                .isbn("978-3-16-148410-0")
                .title("Clean Code")
                .stockDisponible(5)
                .build();

        User user = User.builder()
                .id(userId)
                .username("john")
                .build();

        BorrowingRequestDTO request = BorrowingRequestDTO.builder()
                .bookId(bookId)
                .userId(userId)
                .build();

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(borrowingRepository.countByUserIdAndStatus(userId, BorrowingStatus.ONGOING)).thenReturn(3L);

        // Act & Assert
        assertThatThrownBy(() -> borrowingService.processBorrowing(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("3 ongoing borrowings");

        verify(borrowingRepository, never()).save(any());
    }
}
