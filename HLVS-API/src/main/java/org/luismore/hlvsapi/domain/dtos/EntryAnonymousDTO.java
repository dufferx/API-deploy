package org.luismore.hlvsapi.domain.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.luismore.hlvsapi.domain.entities.House;

@Data
public class EntryAnonymousDTO {
    @NotNull
    private String headline;
    @NotNull
    private String comment;
}