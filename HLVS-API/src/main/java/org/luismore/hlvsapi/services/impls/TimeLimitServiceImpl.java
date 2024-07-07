package org.luismore.hlvsapi.services.impls;

import org.luismore.hlvsapi.domain.dtos.TimeLimitDTO;
import org.luismore.hlvsapi.domain.entities.LimitTime;
import org.luismore.hlvsapi.repositories.TimeLimitRepository;
import org.luismore.hlvsapi.services.TimeLimitService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TimeLimitServiceImpl implements TimeLimitService {

    private final TimeLimitRepository timeLimitRepository;

    public TimeLimitServiceImpl(TimeLimitRepository timeLimitRepository) {
        this.timeLimitRepository = timeLimitRepository;
    }

    @Override
    public List<TimeLimitDTO> getAllTimeLimits() {
        return timeLimitRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private TimeLimitDTO convertToDTO(LimitTime timeLimit) {
        TimeLimitDTO dto = new TimeLimitDTO();
        dto.setId(timeLimit.getId());
        dto.setDuration(timeLimit.getLimit());
        return dto;
    }
}
