package com.bibliotech.repository;

import com.bibliotech.document.BookDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ReactiveBookRepository extends ReactiveMongoRepository<BookDocument, String> {

    Mono<BookDocument> findByIsbn(String isbn);
}
