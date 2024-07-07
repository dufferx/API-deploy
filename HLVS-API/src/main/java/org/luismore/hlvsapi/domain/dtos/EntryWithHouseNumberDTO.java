package org.luismore.hlvsapi.domain.dtos;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class EntryWithHouseNumberDTO {
    private UUID id;
    private LocalDate date;
    private LocalTime entryTime;
    private String userName;
    private String houseAddress;
    private String houseNumber;
    private String dui;
    private String comment;
    private String entryType;
    private String headline;
}
