package org.xenon.echo.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewIntervalsDto {
    private int againDays;
    private int hardDays;
    private int goodDays;
    private int easyDays;
}
