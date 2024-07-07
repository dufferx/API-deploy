package org.luismore.hlvsapi.domain.dtos;

import lombok.Data;

import java.util.UUID;

@Data
public class UpdateResidentDTO {
    private UUID houseId;
    private String email;
    private String name;
    private String password;
    private String dui;
}
