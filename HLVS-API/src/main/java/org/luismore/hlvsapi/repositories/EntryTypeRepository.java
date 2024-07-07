package org.luismore.hlvsapi.repositories;

import org.luismore.hlvsapi.domain.entities.EntryType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntryTypeRepository extends JpaRepository<EntryType, String> {
    EntryType findTypeByType(String type);
}
