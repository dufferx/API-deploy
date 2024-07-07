package org.luismore.hlvsapi.controllers;

import jakarta.validation.Valid;
import org.luismore.hlvsapi.domain.dtos.*;
import org.luismore.hlvsapi.domain.entities.Request;
import org.luismore.hlvsapi.domain.entities.User;
import org.luismore.hlvsapi.repositories.TimeLimitRepository;
import org.luismore.hlvsapi.services.PendingRequestService;
import org.luismore.hlvsapi.services.RequestService;
import org.luismore.hlvsapi.services.UserService;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/requests")
public class RequestController {

    private final RequestService requestService;
    private final UserService userService;
    private final PendingRequestService pendingRequestService;
    private final TimeLimitRepository limitTimeRepository;

    public RequestController(RequestService requestService, UserService userService, PendingRequestService pendingRequestService, TimeLimitRepository limitTimeRepository) {
        this.requestService = requestService;
        this.userService = userService;
        this.pendingRequestService = pendingRequestService;

        this.limitTimeRepository = limitTimeRepository;
    }

    @GetMapping("/AllPending")
    @PreAuthorize("hasAuthority('ROLE_main resident') or hasAuthority('ROLE_admin')")
    public ResponseEntity<GeneralResponse> getAllPendingRequestsByHouseId(@AuthenticationPrincipal User user) {
        UUID houseId = user.getHouse().getId();
        List<PendingRequestSummaryDTO> pendingRequests = pendingRequestService.getAllPendingRequestsByHouseId(houseId);
        if (pendingRequests.isEmpty()) {
            return GeneralResponse.getResponse(HttpStatus.NOT_FOUND, "No pending requests found for the specified house.");
        }
        return GeneralResponse.getResponse(HttpStatus.OK, pendingRequests);
    }

    @GetMapping("/AllApproved")
    @PreAuthorize("hasAuthority('ROLE_main resident') or hasAuthority('ROLE_admin')")
    public ResponseEntity<GeneralResponse> getAllApprovedRequestsByHouseId(@AuthenticationPrincipal User user) {
        UUID houseId = user.getHouse().getId();
        List<PendingRequestSummaryDTO> approvedRequests = pendingRequestService.getAllApprovedRequestsByHouseId(houseId);
        if (approvedRequests.isEmpty()) {
            return GeneralResponse.getResponse(HttpStatus.NOT_FOUND, "No approved requests found for the specified house.");
        }
        return GeneralResponse.getResponse(HttpStatus.OK, approvedRequests);
    }


    @GetMapping("/home/{homeId}")
    @PreAuthorize("hasAuthority('ROLE_main resident') or hasAuthority('ROLE_admin')")
    public ResponseEntity<GeneralResponse> getRequestsByHomeId(@PathVariable UUID homeId) {
        List<Request> requests = requestService.getRequestsByHomeIdAndStatus(homeId, "PEND");
        if (requests.isEmpty()) {
            return GeneralResponse.getResponse(HttpStatus.NOT_FOUND, "Pending requests not found for the specified home.");
        }
        return GeneralResponse.getResponse(HttpStatus.OK, requests);
    }

    @PostMapping("/create/single")
    @PreAuthorize("hasAuthority('ROLE_main resident') or hasAuthority('ROLE_admin') or hasAuthority('ROLE_resident')")
    public ResponseEntity<GeneralResponse> createSingleRequest(@RequestBody @Valid CreateSingleRequestWithEmailDTO createRequestDTO, @AuthenticationPrincipal User user) {
        Request request = requestService.createSingleRequest(createRequestDTO, user);
        RequestDTO requestDTO = requestService.convertToDTO(request);
        return GeneralResponse.getResponse(HttpStatus.CREATED, requestDTO);
    }

    @PostMapping("/create/multiple")
    @PreAuthorize("hasAuthority('ROLE_main resident') or hasAuthority('ROLE_admin') or hasAuthority('ROLE_resident')")
    public ResponseEntity<GeneralResponse> createMultipleRequests(@RequestBody @Valid CreateMultipleRequestWithEmailDTO createRequestDTO, @AuthenticationPrincipal User user) {
        List<Request> requests = requestService.createMultipleRequests(createRequestDTO, user);
        List<RequestDTO> requestDTOs = requests.stream().map(requestService::convertToDTO).collect(Collectors.toList());
        return GeneralResponse.getResponse(HttpStatus.CREATED, requestDTOs);
    }

    @GetMapping("/request/{id}")
    @PreAuthorize("hasAuthority('ROLE_main resident') or hasAuthority('ROLE_admin')")
    public ResponseEntity<GeneralResponse> getRequestDetails(
            @PathVariable String id,
            @RequestParam String residentName,
            @RequestParam String visitorName,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(name = "per_page", defaultValue = "1") int size) {

        Pageable pageable = PageRequest.of(page - 1, size);

        if (id.equals("multiple")) {
            Page<Request> requests = requestService.getRequestsByResidentAndVisitorNames(residentName, visitorName, pageable);
            if (requests.isEmpty()) {
                return GeneralResponse.getResponse(HttpStatus.NOT_FOUND, "No multiple requests found for the specified resident and visitor.");
            }
            Page<RequestDetailsDTO> requestDetails = requests.map(request -> {
                RequestDetailsDTO dto = new RequestDetailsDTO();
                dto.setId(request.getId().toString());
                dto.setResidentEmail(request.getCreator().getEmail());
                dto.setVisitorEmail(request.getVisitor().getEmail());
                dto.setDUI(request.getDUI());
                dto.setEntryDate(request.getEntryDate());
                dto.setEntryTime(request.getEntryTime());
                dto.setBeforeTime(request.getBeforeTime());
                dto.setAfterTime(request.getAfterTime());
                dto.setHour1(request.getHour1());
                dto.setHour2(request.getHour2());
                dto.setResident(request.getCreator().getName());
                dto.setVisitor(request.getVisitor().getName());
                return dto;
            });
            return GeneralResponse.getResponse(HttpStatus.OK, requestDetails);
        } else {
            Optional<Request> requestOptional = requestService.getRequestById(UUID.fromString(id));
            if (requestOptional.isEmpty()) {
                return GeneralResponse.getResponse(HttpStatus.NOT_FOUND, "Request not found.");
            }
            Request request = requestOptional.get();
            RequestDetailsDTO dto = new RequestDetailsDTO();
            dto.setResidentEmail(request.getCreator().getEmail());
            dto.setVisitorEmail(request.getVisitor().getEmail());
            dto.setDUI(request.getDUI());
            dto.setEntryDate(request.getEntryDate());
            dto.setEntryTime(request.getEntryTime());
            dto.setResident(request.getCreator().getName());
            dto.setVisitor(request.getVisitor().getName());

            Page<RequestDetailsDTO> pagedResponse = new PageImpl<>(Collections.singletonList(dto), pageable, 1);

            return GeneralResponse.getResponse(HttpStatus.OK, pagedResponse);
        }
    }



    @GetMapping("/history")
    @PreAuthorize("hasAuthority('ROLE_main resident') or hasAuthority('ROLE_resident')")
    public ResponseEntity<GeneralResponse> getRequestHistory(@AuthenticationPrincipal User user) {
        List<Request> requests = requestService.getRequestHistoryByUserHouse(user);
        if (requests.isEmpty()) {
            return GeneralResponse.getResponse(HttpStatus.NOT_FOUND, "Request history not found for the user's house.");
        }
        return GeneralResponse.getResponse(HttpStatus.OK, requests);
    }

    @PatchMapping("/update")
    @PreAuthorize("hasAuthority('ROLE_main resident') or hasAuthority('ROLE_admin')")
    public ResponseEntity<GeneralResponse> updateRequest(@RequestBody @Valid UpdateRequestDTO updateRequestDTO) {
        Optional<Request> requestOptional = requestService.getRequestById(updateRequestDTO.getRequestId());
        if (requestOptional.isEmpty()) {
            return GeneralResponse.getResponse(HttpStatus.NOT_FOUND, "Request not found.");
        }
        Request request = requestOptional.get();
        requestService.updateRequestState(request, updateRequestDTO.getState());
        return GeneralResponse.getResponse(HttpStatus.OK, "Request updated successfully.");
    }

    @PatchMapping("/request/{id}")
    @PreAuthorize("hasAuthority('ROLE_main resident') or hasAuthority('ROLE_admin')")
    public ResponseEntity<GeneralResponse> updateRequestState(
            @PathVariable String id,
            @RequestParam String residentName,
            @RequestParam String visitorName,
            @RequestParam String status) {

        try {
            requestService.updateRequestState(id, residentName, visitorName, status);
            return GeneralResponse.getResponse(HttpStatus.OK, "Request(s) updated to " + status + " successfully.");
        } catch (IllegalArgumentException e) {
            return GeneralResponse.getResponse(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/updateLimitTime")
    @PreAuthorize("hasAuthority('ROLE_admin')")
    public ResponseEntity<GeneralResponse> updateLimitTime(@RequestParam int newLimit) {
        try {
            LimitTimeDTO updatedLimitTime = requestService.updateLimitTime(newLimit);
            return GeneralResponse.getResponse(HttpStatus.OK, updatedLimitTime);
        } catch (IllegalArgumentException e) {
            return GeneralResponse.getResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/user-requests")
    @PreAuthorize("hasAuthority('ROLE_resident')")
    public ResponseEntity<GeneralResponse> getAllRequestsByUser(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(name = "per_page", defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<UserRequestSummaryDTO> userRequests = requestService.getAllRequestsByUser(user, pageable);

        if (userRequests.isEmpty()) {
            return GeneralResponse.getResponse(HttpStatus.NOT_FOUND, "No requests found for the user.");
        }

        return GeneralResponse.getResponse(HttpStatus.OK, userRequests);
    }



}
