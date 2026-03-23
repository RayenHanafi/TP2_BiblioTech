package com.bibliotech.mapper;

import com.bibliotech.dto.BookResponseDTO;
import com.bibliotech.entity.Book;
import com.bibliotech.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface BookMapper {

    @Mapping(source = "author.name", target = "authorName")
    @Mapping(source = "categories", target = "categories", qualifiedByName = "categoriesToLabels")
    BookResponseDTO toResponseDTO(Book book);

    @Named("categoriesToLabels")
    default Set<String> categoriesToLabels(Set<Category> categories) {
        if (categories == null) return null;
        return categories.stream()
                .map(Category::getLabel)
                .collect(Collectors.toSet());
    }
}
