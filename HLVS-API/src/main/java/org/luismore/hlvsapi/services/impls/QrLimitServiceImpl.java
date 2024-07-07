package org.luismore.hlvsapi.services.impls;

import org.luismore.hlvsapi.domain.dtos.QrLimitDTO;
import org.luismore.hlvsapi.domain.entities.QRLimit;
import org.luismore.hlvsapi.repositories.QrLimitRepository;
import org.luismore.hlvsapi.services.QrLimitService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QrLimitServiceImpl implements QrLimitService {

    private final QrLimitRepository qrLimitRepository;

    public QrLimitServiceImpl(QrLimitRepository qrLimitRepository) {
        this.qrLimitRepository = qrLimitRepository;
    }

    @Override
    public List<QrLimitDTO> getAllQrLimits() {
        return qrLimitRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private QrLimitDTO convertToDTO(QRLimit qrLimit) {
        QrLimitDTO dto = new QrLimitDTO();
        dto.setId(qrLimit.getId());
        dto.setMinutesDuration(qrLimit.getMinutesDuration());
        return dto;
    }
}
