package org.luismore.hlvsapi.domain.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
@Data
public class ChangeRoleDTO {
    @NotBlank
    private String identifier;
    @NotNull
    private List<@NotBlank String> roles;
}
