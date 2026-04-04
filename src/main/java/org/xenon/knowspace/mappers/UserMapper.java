package org.xenon.knowspace.mappers;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import org.xenon.knowspace.dtos.RegisterUserRequest;
import org.xenon.knowspace.dtos.UserDto;
import org.xenon.knowspace.entities.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
    User toEntity(RegisterUserRequest request);
}
