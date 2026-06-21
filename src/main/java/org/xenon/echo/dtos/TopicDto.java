package org.xenon.echo.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class TopicDto {
    private Long id;

    private String name;

    private String description;

    private LocalDateTime createdAt;

    private UserDto user;

}
