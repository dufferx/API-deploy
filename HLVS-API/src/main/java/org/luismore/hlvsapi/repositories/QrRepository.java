package org.luismore.hlvsapi.repositories;

import org.apache.catalina.connector.Request;
import org.luismore.hlvsapi.domain.entities.QR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QrRepository extends JpaRepository<QR, UUID> {
    Optional<QR> findByToken(String token);

}
