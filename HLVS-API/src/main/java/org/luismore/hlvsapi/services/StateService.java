package org.luismore.hlvsapi.services;

import org.luismore.hlvsapi.domain.entities.State;

import java.util.Optional;

public interface StateService {
    Optional<State> getStateById(String id);
}
