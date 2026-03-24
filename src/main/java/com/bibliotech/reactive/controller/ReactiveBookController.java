package com.bibliotech.reactive.controller;

import com.bibliotech.document.BookDocument;
import com.bibliotech.reactive.repository.ReactiveBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/reactive/books")
@RequiredArgsConstructor
public class ReactiveBookController {

    private final ReactiveBookRepository reactiveBookRepository;

    @GetMapping
    public Flux<BookDocument> getAllBooks() {
        return reactiveBookRepository.findAll();
    }

    @GetMapping("/{id}")
    public Mono<BookDocument> getBookById(@PathVariable String id) {
        return reactiveBookRepository.findById(id);
    }

    @GetMapping("/isbn/{isbn}")
    public Mono<BookDocument> getBookByIsbn(@PathVariable String isbn) {
        return reactiveBookRepository.findByIsbn(isbn);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<BookDocument> createBook(@RequestBody BookDocument book) {
        return reactiveBookRepository.save(book);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteBook(@PathVariable String id) {
        return reactiveBookRepository.deleteById(id);
    }
}
