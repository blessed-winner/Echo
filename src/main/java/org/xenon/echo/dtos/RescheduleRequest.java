package org.xenon.echo.dtos;

import lombok.Getter;
import lombok.Setter;
import org.xenon.echo.enums.RescheduleType;

@Getter
@Setter
public class RescheduleRequest {
    private RescheduleType type;
}
