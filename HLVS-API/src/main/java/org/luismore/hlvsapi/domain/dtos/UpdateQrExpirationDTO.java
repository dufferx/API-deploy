package org.luismore.hlvsapi.domain.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateQrExpirationDTO {
    @NotNull
    private Integer duration;
}
