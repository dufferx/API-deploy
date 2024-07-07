package org.luismore.hlvsapi.services;

import org.luismore.hlvsapi.domain.dtos.QrLimitDTO;

import java.util.List;

public interface QrLimitService {
    List<QrLimitDTO> getAllQrLimits();
}
