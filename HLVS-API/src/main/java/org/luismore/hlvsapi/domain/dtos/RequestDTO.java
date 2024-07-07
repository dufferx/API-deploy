package org.luismore.hlvsapi.domain.dtos;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class RequestDTO {
    private UUID id;
    private String DUI;
    private LocalDate entryDate;
    private LocalTime entryTime;
    private LocalTime beforeTime;
    private LocalTime afterTime;
    private LocalTime hour1;
    private LocalTime hour2;
    private String houseId;
    private String stateId;
    private String visitorId;
}
