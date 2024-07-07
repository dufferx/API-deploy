package org.luismore.hlvsapi.repositories;

import org.luismore.hlvsapi.domain.entities.QRLimit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QrLimitRepository extends JpaRepository<QRLimit, Integer> {
}
