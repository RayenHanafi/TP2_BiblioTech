package com.bibliotech.scheduler;

import com.bibliotech.entity.Borrowing;
import com.bibliotech.entity.BorrowingStatus;
import com.bibliotech.repository.BorrowingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BorrowingScheduler {

    private final BorrowingRepository borrowingRepository;

    /**
     * Runs every night at midnight.
     * Checks for ONGOING borrowings older than 14 days and marks them as OVERDUE.
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void markOverdueBorrowings() {
        LocalDateTime fourteenDaysAgo = LocalDateTime.now().minusDays(14);

        List<Borrowing> overdueBorrowings = borrowingRepository
                .findByStatusAndBorrowDateBefore(BorrowingStatus.ONGOING, fourteenDaysAgo);

        for (Borrowing borrowing : overdueBorrowings) {
            borrowing.setStatus(BorrowingStatus.OVERDUE);
            borrowingRepository.save(borrowing);
            log.info("Marked borrowing {} as OVERDUE (book: {}, user: {})",
                    borrowing.getId(),
                    borrowing.getBook().getTitle(),
                    borrowing.getUser().getUsername());
        }

        if (!overdueBorrowings.isEmpty()) {
            log.info("Total borrowings marked as OVERDUE: {}", overdueBorrowings.size());
        }
    }
}
