package org.luismore.hlvsapi.services.impls;

import org.luismore.hlvsapi.domain.entities.State;
import org.luismore.hlvsapi.repositories.StateRepository;
import org.luismore.hlvsapi.services.StateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StateServiceImpl implements StateService {

    private final StateRepository stateRepository;

    @Autowired
    public StateServiceImpl(StateRepository stateRepository) {
        this.stateRepository = stateRepository;
    }

    @Override
    public Optional<State> getStateById(String id) {
        return stateRepository.findById(id);
    }
}
