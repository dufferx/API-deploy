package org.luismore.hlvsapi.repositories;

import org.luismore.hlvsapi.domain.entities.Tablet;
import org.luismore.hlvsapi.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TabletRepository extends JpaRepository<Tablet, UUID> {
    Optional<Tablet> findBySerialNumber(String serialNumber);
    List<Tablet> findBySecurityGuard(User securityGuard);
    Optional<Tablet> findBySecurityGuard_Email(String email);
}
