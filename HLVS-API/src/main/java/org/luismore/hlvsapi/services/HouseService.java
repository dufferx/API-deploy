package org.luismore.hlvsapi.services;

import org.luismore.hlvsapi.domain.dtos.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface HouseService {
    Page<HouseDTO> getAllHouses(Pageable pageable);
    List<HouseDTO> getHousesByFilter(String filter);
    HouseDTO createHouse(CreateHouseDTO createHouseDTO);
    void updateHouse(UpdateHouseDTO updateHouseDTO);
    void updateResident(UpdateResidentDTO updateResidentDTO);
    List<UserDTO> getResidentsByHouseId(UUID houseId);
    void assignLeader(UUID houseId, String email);
    HouseDTO getHouseByNumber(String houseNumber);
    List<HouseDTO> getHouseByUserEmail(String userEmail);
}
