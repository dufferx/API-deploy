package org.luismore.hlvsapi.domain.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class CreateMultipleRequestWithEmailDTO {
    @NotBlank
    private String dui;
    @NotBlank
    @Email
    private String email;
    @NotNull
    private List<LocalDate> entryDates;
    @NotNull
    private LocalTime hour1;
    @NotNull
    private LocalTime hour2;
}
