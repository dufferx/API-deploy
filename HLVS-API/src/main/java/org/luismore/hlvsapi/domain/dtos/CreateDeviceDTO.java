package org.luismore.hlvsapi.domain.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateDeviceDTO {
    @NotBlank
    private String serialNumber;
    @NotBlank
    private String location;
    @NotBlank
    private String securityGuardEmail;
}
