package org.luismore.hlvsapi.repositories;

import org.luismore.hlvsapi.domain.entities.State;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StateRepository extends JpaRepository<State, String> {
}
