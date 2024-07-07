package org.luismore.hlvsapi.services;

import org.luismore.hlvsapi.domain.dtos.PendingRequestSummaryDTO;

import java.util.List;
import java.util.UUID;

public interface PendingRequestService {
    List<PendingRequestSummaryDTO> getAllPendingRequestsByHouseId(UUID houseId);
    List<PendingRequestSummaryDTO> getAllApprovedRequestsByHouseId(UUID houseId);


}
