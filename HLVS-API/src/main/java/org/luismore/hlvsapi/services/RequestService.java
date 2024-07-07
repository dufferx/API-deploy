package org.luismore.hlvsapi.services;

import jakarta.transaction.Transactional;
import org.luismore.hlvsapi.domain.dtos.*;
import org.luismore.hlvsapi.domain.entities.Request;
import org.luismore.hlvsapi.domain.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RequestService {
    List<Request> getRequestsByHomeIdAndStatus(UUID homeId, String status);
    Request createSingleRequest(CreateSingleRequestDTO createRequestDTO, User user);
    Request createSingleRequest(CreateSingleRequestWithEmailDTO createRequestDTO, User user);
    List<Request> createMultipleRequests(CreateMultipleRequestDTO createRequestDTO, User user);
    List<Request> createMultipleRequests(CreateMultipleRequestWithEmailDTO createRequestDTO, User user);
    List<Request> getRequestHistoryByUserHouse(User user);
    Optional<Request> getRequestById(UUID requestId);
    void save(Request request);
    void updateRequestState(Request request, String stateId);
    RequestDTO convertToDTO(Request request);
    List<PendingRequestDTO> getAllPendingRequestsForMainResident(User mainResident);
    RequestDetailsDTO getSingleRequestDetails(UUID requestId);
    List<RequestDTO> getMultipleRequestsByResidentAndVisitor(String residentName, String visitorName);
    List<Request> getRequestsByResidentAndVisitorNames(String residentName, String visitorName);
    void updateRequestState(String id, String residentName, String visitorName, String status);
    void updateMultipleRequestsState(String residentName, String visitorName, String stateId);
    LimitTimeDTO updateLimitTime(int newLimit);
    @Transactional
    Page<Request> getRequestsByResidentAndVisitorNames(String residentName, String visitorName, Pageable pageable);
    Page<UserRequestSummaryDTO> getAllRequestsByUser(User user, Pageable pageable);

}

