package org.luismore.hlvsapi.domain.dtos;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class QrDTO {
    private UUID uniqueID;
    private String token;
    private LocalDate expDate;
    private LocalTime expTime;
    private Boolean used;
    private UUID userId;
    private UUID requestId;
}
