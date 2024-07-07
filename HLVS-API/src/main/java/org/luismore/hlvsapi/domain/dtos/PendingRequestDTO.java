package org.luismore.hlvsapi.domain.dtos;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PendingRequestDTO {
    private String residentName;
    private LocalDate requestDay;
    private String visitorName;
    private String req;
}