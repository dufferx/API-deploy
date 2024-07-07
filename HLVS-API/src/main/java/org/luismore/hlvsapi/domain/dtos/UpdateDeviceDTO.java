package org.luismore.hlvsapi.domain.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateDeviceDTO {
    private String serialNumber;
    private String location;
    private String securityGuardEmail;
}
