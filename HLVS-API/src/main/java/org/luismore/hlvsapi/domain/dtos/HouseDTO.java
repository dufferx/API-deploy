package org.luismore.hlvsapi.domain.dtos;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class HouseDTO {
    private UUID id;
    private String houseNumber;
    private String address;
    private String residentNumber;
    private String leader;
    private String nameLeader;
    private List<UserDTO> residents;
}
