package org.luismore.hlvsapi.domain.dtos;

import lombok.Data;

import java.util.UUID;

@Data
public class QRResponseDTO {
    private UUID uniqueID;
    private String token;
    private int duration;
}
