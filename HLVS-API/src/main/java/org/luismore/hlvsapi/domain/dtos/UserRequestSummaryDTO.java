package org.luismore.hlvsapi.domain.dtos;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class UserRequestSummaryDTO {
    private String visitor;
    private LocalDate requestDay;
    private Integer multipleCount;
    private String state;
}
