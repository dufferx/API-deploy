package org.luismore.hlvsapi.domain.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class CreateMultipleRequestDTO {
    @NotBlank
    private String dui;
    @NotNull
    private List<LocalDate> entryDates;
    @NotNull
    private LocalTime hour1;
    @NotNull
    private LocalTime hour2;
}
