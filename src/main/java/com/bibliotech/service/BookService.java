package com.bibliotech.service;

import com.bibliotech.dto.BookRequestDTO;
import com.bibliotech.dto.BookResponseDTO;
import com.bibliotech.entity.Author;
import com.bibliotech.entity.Book;
import com.bibliotech.entity.Category;
import com.bibliotech.exception.EntityNotFoundException;
import com.bibliotech.mapper.BookMapper;
import com.bibliotech.repository.AuthorRepository;
import com.bibliotech.repository.BookRepository;
import com.bibliotech.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final BookMapper bookMapper;

    @Transactional(readOnly = true)
    public Page<BookResponseDTO> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable)
                .map(bookMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public BookResponseDTO getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book", id));
        return bookMapper.toResponseDTO(book);
    }

    @Transactional
    public BookResponseDTO createBook(BookRequestDTO request) {
        if (bookRepository.existsByIsbn(request.getIsbn())) {
            throw new IllegalStateException("A book with ISBN " + request.getIsbn() + " already exists");
        }

        Book book = Book.builder()
                .isbn(request.getIsbn())
                .title(request.getTitle())
                .stockDisponible(request.getStockDisponible())
                .build();

        // Set author if provided
        if (request.getAuthorId() != null) {
            Author author = authorRepository.findById(request.getAuthorId())
                    .orElseThrow(() -> new EntityNotFoundException("Author", request.getAuthorId()));
            book.setAuthor(author);
        }

        // Set categories if provided
        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            Set<Category> categories = new HashSet<>(
                    categoryRepository.findAllById(request.getCategoryIds())
            );
            book.setCategories(categories);
        }

        Book saved = bookRepository.save(book);
        return bookMapper.toResponseDTO(saved);
    }
}
