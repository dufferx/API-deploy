package org.luismore.hlvsapi.domain.dtos;

import lombok.Data;

import java.util.UUID;

@Data
public class CreateQrDTO {
    private String token;
    private UUID requestId;
}
