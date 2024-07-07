package org.luismore.hlvsapi.domain.dtos;

import lombok.Data;

@Data
public class CreateHouseDTO {
    private String houseNumber;
    private String address;
    private String residentNumber;
    private String leaderEmail;
}
