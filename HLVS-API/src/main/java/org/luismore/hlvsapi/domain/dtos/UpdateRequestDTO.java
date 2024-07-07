package org.luismore.hlvsapi.domain.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class UpdateRequestDTO {
    @NotNull
    private UUID requestId;
    @NotBlank
    private String state;
}
