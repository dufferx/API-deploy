package org.luismore.hlvsapi.domain.dtos;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PendingRequestSummaryDTO {
    private String id;
    private LocalDate entryDate;
    private String resident;
    private String visitor;
    private Integer multipleCount;
}
