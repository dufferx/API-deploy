package org.luismore.hlvsapi.domain.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateDeviceLocationDTO {
    @NotBlank
    private String location;
}
