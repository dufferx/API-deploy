package org.luismore.hlvsapi.domain.dtos;

import lombok.Data;

import java.util.UUID;

@Data
public class UpdateUserDTO {
    private UUID id;
    private String email;
    private String username;
    private String password;
}
