package org.luismore.hlvsapi.domain.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class CreateSingleRequestDTO {
    @NotBlank
    private String dui;
    @NotNull
    private LocalDate entryDate;
    @NotNull
    private LocalTime entryTime;
}
