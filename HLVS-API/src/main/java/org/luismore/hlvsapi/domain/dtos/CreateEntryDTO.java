package org.luismore.hlvsapi.domain.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class CreateEntryDTO {
    @NotNull
    private Date date;
    @NotNull
    private Date entryTime;
    @NotBlank
    private String comment;
    @NotBlank
    private String dui;
    @NotNull
    private UUID houseId;
    @NotNull
    private UUID userId;
    @NotBlank
    private String entryTypeId;

}
