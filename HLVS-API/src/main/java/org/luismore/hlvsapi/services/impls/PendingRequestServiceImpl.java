package org.luismore.hlvsapi.services.impls;

import org.luismore.hlvsapi.domain.dtos.PendingRequestSummaryDTO;
import org.luismore.hlvsapi.domain.entities.Request;
import org.luismore.hlvsapi.domain.entities.User;
import org.luismore.hlvsapi.repositories.RequestRepository;
import org.luismore.hlvsapi.repositories.UserRepository;
import org.luismore.hlvsapi.services.PendingRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PendingRequestServiceImpl implements PendingRequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;

    @Autowired
    public PendingRequestServiceImpl(RequestRepository requestRepository, UserRepository userRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<PendingRequestSummaryDTO> getAllPendingRequestsByHouseId(UUID houseId) {
        List<Request> allPendingRequests = requestRepository.findByHouseIdAndState(houseId, "PEND");

        Map<String, List<Request>> groupedRequests = allPendingRequests.stream()
                .collect(Collectors.groupingBy(req -> req.getCreator().getId().toString() + "-" + req.getVisitor().getId().toString()));

        List<PendingRequestSummaryDTO> dtos = new ArrayList<>();

        for (Map.Entry<String, List<Request>> entry : groupedRequests.entrySet()) {
            List<Request> requests = entry.getValue();

            requests.stream()
                    .filter(req -> req.getHour1() == null && req.getHour2() == null)
                    .forEach(req -> {
                        PendingRequestSummaryDTO singleDto = new PendingRequestSummaryDTO();
                        singleDto.setId(req.getId().toString());
                        singleDto.setEntryDate(req.getEntryDate());
                        singleDto.setResident(req.getCreator().getName());
                        singleDto.setVisitor(req.getVisitor().getName());
                        singleDto.setMultipleCount(null);
                        dtos.add(singleDto);
                    });

            long multipleCount = requests.stream().filter(req -> req.getEntryTime() == null).count();
            if (multipleCount > 0) {
                Request representativeRequest = requests.stream().filter(req -> req.getEntryTime() == null).findFirst().orElse(null);
                if (representativeRequest != null) {
                    PendingRequestSummaryDTO multipleDto = new PendingRequestSummaryDTO();
                    multipleDto.setId("multiple");
                    multipleDto.setEntryDate(null);
                    multipleDto.setResident(representativeRequest.getCreator().getName());
                    multipleDto.setVisitor(representativeRequest.getVisitor().getName());
                    multipleDto.setMultipleCount((int) multipleCount);
                    dtos.add(multipleDto);
                }
            }
        }

        return dtos;
    }

    @Override
    public List<PendingRequestSummaryDTO> getAllApprovedRequestsByHouseId(UUID houseId) {
        List<Request> allApprovedRequests = requestRepository.findByHouseIdAndState(houseId, "APPR");

        Map<String, List<Request>> groupedRequests = allApprovedRequests.stream()
                .collect(Collectors.groupingBy(req -> req.getCreator().getId().toString() + "-" + req.getVisitor().getId().toString()));

        List<PendingRequestSummaryDTO> dtos = new ArrayList<>();

        for (Map.Entry<String, List<Request>> entry : groupedRequests.entrySet()) {
            List<Request> requests = entry.getValue();

            requests.stream()
                    .filter(req -> req.getHour1() == null && req.getHour2() == null)
                    .forEach(req -> {
                        PendingRequestSummaryDTO singleDto = new PendingRequestSummaryDTO();
                        singleDto.setId(req.getId().toString());
                        singleDto.setEntryDate(req.getEntryDate());
                        singleDto.setResident(req.getCreator().getName());
                        singleDto.setVisitor(req.getVisitor().getName());
                        singleDto.setMultipleCount(null);
                        dtos.add(singleDto);
                    });

            long multipleCount = requests.stream().filter(req -> req.getEntryTime() == null).count();
            if (multipleCount > 0) {
                Request representativeRequest = requests.stream().filter(req -> req.getEntryTime() == null).findFirst().orElse(null);
                if (representativeRequest != null) {
                    PendingRequestSummaryDTO multipleDto = new PendingRequestSummaryDTO();
                    multipleDto.setId("multiple");
                    multipleDto.setEntryDate(null);
                    multipleDto.setResident(representativeRequest.getCreator().getName());
                    multipleDto.setVisitor(representativeRequest.getVisitor().getName());
                    multipleDto.setMultipleCount((int) multipleCount);
                    dtos.add(multipleDto);
                }
            }
        }

        return dtos;
    }


}
