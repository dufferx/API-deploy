package org.luismore.hlvsapi.domain.dtos;

import lombok.Data;

@Data
public class DeviceDTO {
    private String id;
    private String serialNumber;
    private String location;
    private String securityGuardEmail;
    private String securityNameGuard;
}
