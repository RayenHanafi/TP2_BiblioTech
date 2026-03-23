package com.bibliotech.mapper;

import com.bibliotech.dto.BorrowingResponseDTO;
import com.bibliotech.entity.Borrowing;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BorrowingMapper {

    @Mapping(source = "book.title", target = "bookTitle")
    @Mapping(source = "user.username", target = "userName")
    BorrowingResponseDTO toResponseDTO(Borrowing borrowing);
}
