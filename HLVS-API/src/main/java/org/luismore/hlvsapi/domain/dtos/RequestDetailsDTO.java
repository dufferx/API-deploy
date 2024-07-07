package org.luismore.hlvsapi.domain.dtos;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class RequestDetailsDTO {
    private String id;
    private String residentEmail;
    private String visitorEmail;
    private String DUI;
    private LocalDate entryDate;
    private LocalTime entryTime;
    private LocalTime beforeTime;
    private LocalTime afterTime;
    private LocalTime hour1;
    private LocalTime hour2;
    private String resident;
    private String visitor;
}
