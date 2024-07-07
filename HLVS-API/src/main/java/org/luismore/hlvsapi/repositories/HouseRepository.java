package org.luismore.hlvsapi.repositories;

import org.luismore.hlvsapi.domain.entities.House;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HouseRepository extends JpaRepository<House, UUID> {
    Optional<House> findByHouseNumber(String houseNumber);
    List<House> findByResidents_EmailOrLeader_Email(String residentEmail, String leaderEmail);
    List<House> findByAddress(String address);
    Page<House> findAll(Pageable pageable);
}
