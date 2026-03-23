package com.bibliotech.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "books")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BookDocument {

    @Id
    private String id;

    @Indexed(unique = true)
    private String isbn;

    private String title;

    private Integer stockDisponible;

    private String authorName;

    private List<String> categories;

    private LocalDateTime createdDate;
}
