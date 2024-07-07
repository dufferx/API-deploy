package org.luismore.hlvsapi.domain.dtos;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class RequestSummaryDTO {
    private String id;
    private LocalDate entryDate;
    private String state;
}