package org.luismore.hlvsapi.services;

import org.luismore.hlvsapi.domain.dtos.TimeLimitDTO;

import java.util.List;

public interface TimeLimitService {
    List<TimeLimitDTO> getAllTimeLimits();
}
