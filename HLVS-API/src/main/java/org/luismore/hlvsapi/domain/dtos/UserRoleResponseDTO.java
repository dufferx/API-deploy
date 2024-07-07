package org.luismore.hlvsapi.domain.dtos;

import lombok.Data;

import java.util.List;

@Data
public class UserRoleResponseDTO {
    private String email;
    private List<String> roles;
}
