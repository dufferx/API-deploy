//package org.luismore.hlvsapi.domain.dtos;
//
//import lombok.Data;
//import org.luismore.hlvsapi.domain.entities.EntryType;
//import org.luismore.hlvsapi.domain.entities.House;
//import org.luismore.hlvsapi.domain.entities.User;
//
//import java.time.LocalDate;
//import java.time.LocalTime;
//import java.util.UUID;
//
//@Data
//public class EntryDTO {
//    private UUID id;
//    private LocalDate date;
//    private LocalTime entryTime;
//    private User user;
//    private House house;
//    private String dui;
//    private String comment;
//    private EntryType entryType;
//    private String headline;
//}

package org.luismore.hlvsapi.domain.dtos;

import lombok.Data;
import org.luismore.hlvsapi.domain.entities.EntryType;
import org.luismore.hlvsapi.domain.entities.House;
import org.luismore.hlvsapi.domain.entities.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class EntryDTO {
    private UUID id;
    private LocalDate date;
    private LocalTime entryTime;
    private String userName;
    private String houseAddress;
    private String dui;
    private String comment;
    private String entryType;
    private String headline;
}
