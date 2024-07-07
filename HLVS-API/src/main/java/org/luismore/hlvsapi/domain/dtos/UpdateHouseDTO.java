package org.luismore.hlvsapi.domain.dtos;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class UpdateHouseDTO {
    private UUID id;
    private String houseNumber;
    private String address;
    private String residentNumber;
    private String leaderEmail;
    private List<UpdateResidentDTO> residents;
}
