package org.luismore.hlvsapi.domain.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class CreateRequestDTO {
    @NotBlank
    private String dui;
    @NotNull
    private Date entryDate;
    @NotNull
    private Date entryTime;
    @NotNull
    private Date limitTime;
    @NotBlank
    private String stateId;
}
