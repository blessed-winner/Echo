package org.xenon.echo.mappers;

import org.mapstruct.Mapper;
import org.xenon.echo.dtos.ReviewDto;
import org.xenon.echo.entities.Review;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    ReviewDto toDto(Review review);
}
