package org.luismore.hlvsapi.domain.dtos;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class UserInfoDTO {
    private UUID id;
    private String email;
    private String name;
    private List<String> roles;
}
