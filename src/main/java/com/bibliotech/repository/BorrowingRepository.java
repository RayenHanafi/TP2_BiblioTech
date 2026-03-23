package com.bibliotech.repository;

import com.bibliotech.entity.Borrowing;
import com.bibliotech.entity.BorrowingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BorrowingRepository extends JpaRepository<Borrowing, Long> {

    List<Borrowing> findByUserIdAndStatus(Long userId, BorrowingStatus status);

    long countByUserIdAndStatus(Long userId, BorrowingStatus status);

    List<Borrowing> findByStatusAndBorrowDateBefore(BorrowingStatus status, LocalDateTime date);
}
