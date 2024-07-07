package org.luismore.hlvsapi.domain.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class CreateAnonymousEntryDTO {
    @NotNull
    private Date date;
    @NotNull
    private Date entryTime;
    @NotBlank
    private String comment;
    @NotBlank
    private String headline;
}
