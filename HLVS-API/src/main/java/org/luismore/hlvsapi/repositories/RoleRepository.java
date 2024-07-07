package org.luismore.hlvsapi.repositories;

import org.luismore.hlvsapi.domain.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, String> {
Optional<Role> findById(String id);
Optional<Role> findAllById(String id);
   
}
