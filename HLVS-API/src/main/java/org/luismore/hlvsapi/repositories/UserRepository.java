package org.luismore.hlvsapi.repositories;

import org.luismore.hlvsapi.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByNameOrEmail(String name, String email);
    Optional<User> findByEmail(String email);
    List<User> findByEmailIn(@Param("emails") List<String> emails);
    List<User> findByHouseId(UUID houseId);
    Optional<User> findByDui(String dui);
}
