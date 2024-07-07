package org.luismore.hlvsapi.services.impls;

import org.luismore.hlvsapi.domain.dtos.CreateQrDTO;
import org.luismore.hlvsapi.domain.dtos.CreateQrForRoleDTO;
import org.luismore.hlvsapi.domain.dtos.CreateQrForUserDTO;
import org.luismore.hlvsapi.domain.dtos.GeneralResponse;
import org.luismore.hlvsapi.domain.entities.*;
import org.luismore.hlvsapi.repositories.*;
import org.luismore.hlvsapi.services.QrService;
import org.luismore.hlvsapi.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class QrServiceImpl implements QrService {

    private final QrRepository qrRepository;
    private final QrLimitRepository qrLimitRepository;
    private final RequestRepository requestRepository;
    private final TabletRepository tabletRepository;
    private final EntryRepository entryRepository;
    private final EntryTypeRepository entryTypeRepository;
    private final UserService userService;

    public QrServiceImpl(QrRepository qrRepository, QrLimitRepository qrLimitRepository, RequestRepository requestRepository, TabletRepository tabletRepository, EntryRepository entryRepository, EntryTypeRepository entryTypeRepository, UserService userService) {
        this.qrRepository = qrRepository;
        this.qrLimitRepository = qrLimitRepository;
        this.requestRepository = requestRepository;
        this.tabletRepository = tabletRepository;
        this.entryRepository = entryRepository;
        this.entryTypeRepository = entryTypeRepository;
        this.userService = userService;
    }

    @Override
    public QR generateQrToken(CreateQrDTO createQrDTO) {
        User user = getUserFromRequest(createQrDTO.getRequestId());

        if (isSpecialRole(user)) {
            return generateQrForSpecialRole(createQrDTO.getToken(), user);
        } else {
            return generateQrForVisitor(createQrDTO);
        }
    }

    @Override
    public QR generateQrTokenForRole(User user, CreateQrForRoleDTO createQrForRoleDTO) {
        return generateQrForSpecialRole(createQrForRoleDTO.getToken(), user);
    }

    @Override
    public QR generateQrTokenByUser(User user, CreateQrForUserDTO createQrDTO) {
        List<Request> approvedRequests = requestRepository.findApprovedRequestsByUser(user.getId());

        if (approvedRequests.isEmpty()) {
            throw new RuntimeException("User has no approved requests.");
        }

        for (Request request : approvedRequests) {
            if (isRequestValidForQr(request)) {
                return generateQrForRequest(createQrDTO.getToken(), request, user);
            }
        }

        throw new RuntimeException("No valid requests found for generating QR.");
    }

    private User getUserFromRequest(UUID requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow(() -> new RuntimeException("Request not found"));
        return request.getVisitor();
    }

    private boolean isSpecialRole(User user) {
        Set<String> specialRoles = Set.of("ADMI", "RESI", "MAIN", "SECU");
        Set<String> userRoles = user.getRoles().stream()
                .map(role -> role.getId().toUpperCase())
                .collect(Collectors.toSet());

        boolean hasSpecialRole = userRoles.stream().anyMatch(specialRoles::contains);

        System.out.println("Roles del usuario: " + userRoles);
        System.out.println("Has special role: " + hasSpecialRole);

        return hasSpecialRole;
    }

    private QR generateQrForSpecialRole(String token, User user) {
        QR qr = new QR();
        qr.setToken(token);
        qr.setUsed(false);

        QRLimit qrLimit = qrLimitRepository.findById(1).orElseThrow(() -> new RuntimeException("QR Limit not found"));
        qr.setQrLimit(qrLimit);

        LocalTime now = LocalTime.now();
        LocalDate today = LocalDate.now();

        qr.setExpDate(today);
        qr.setExpTime(now.plusMinutes(qrLimit.getMinutesDuration()));
        qr.setUser(user);

        return qrRepository.save(qr);
    }

    private QR generateQrForVisitor(CreateQrDTO createQrDTO) {
        QR qr = new QR();
        qr.setToken(createQrDTO.getToken());
        qr.setUsed(false);

        QRLimit qrLimit = qrLimitRepository.findById(1).orElseThrow(() -> new RuntimeException("QR Limit not found"));
        qr.setQrLimit(qrLimit);

        Request request = requestRepository.findById(createQrDTO.getRequestId()).orElseThrow(() -> new RuntimeException("Request not found"));

        if (!"APPR".equals(request.getState().getId())) {
            throw new RuntimeException("Request is not approved");
        }

        qr.setRequest(request);

        LocalTime now = LocalTime.now();
        LocalDate today = LocalDate.now();

        if (today.isEqual(request.getEntryDate()) && now.isAfter(request.getBeforeTime()) && now.isBefore(request.getAfterTime())) {
            qr.setExpDate(today);
            qr.setExpTime(now.plusMinutes(qrLimit.getMinutesDuration()));
            qr.setUser(request.getVisitor());
            return qrRepository.save(qr);
        } else {
            throw new RuntimeException("QR cannot be generated outside the allowed time range");
        }
    }

    @Override
    public QR scanQrToken(String token, String email) {
        Optional<QR> qrOptional = qrRepository.findByToken(token);
        if (qrOptional.isPresent()) {
            QR qr = qrOptional.get();

            if (qr.getUsed()) {
                throw new RuntimeException("QR code has already been used");
            }

            if (!qr.getUsed() && qr.getExpDate().isEqual(LocalDate.now()) && qr.getExpTime().isAfter(LocalTime.now())) {
                qr.setUsed(true);

                Tablet tablet = tabletRepository.findBySecurityGuard_Email(email).orElseThrow(() -> new RuntimeException("Tablet not found"));
                Entry entry = new Entry();
                entry.setDate(LocalDate.now());
                entry.setEntryTime(LocalTime.now());
                entry.setUser(qr.getUser());
                entry.setHouse(qr.getRequest() != null ? qr.getRequest().getHouse() : null);
                entry.setDui(qr.getRequest() != null ? qr.getRequest().getDUI() : qr.getUser().getDui());

                EntryType entryType = entryTypeRepository.findById(tablet.getLocation().equalsIgnoreCase("Vehicle") ? "VEHI" : "PEDE")
                        .orElseThrow(() -> new RuntimeException("Entry type not found"));
                entry.setEntryType(entryType);

                entry.setComment(String.format("Usuario %s, entro a las %s el dia %s por la entrada %s",
                        qr.getUser().getName(),
                        entry.getEntryTime(),
                        entry.getDate(),
                        entryType.getId().equals("VEHI") ? "vehicular" : "peatonal"));

                entryRepository.save(entry);
                qrRepository.save(qr);

                // Si la entrada es por vehículo, activar el servo
                if (shouldOpenServo(email)) {
                    sendWebSocketCommand("http://localhost:8080/api/servo/move");
                } else if (shouldOpenServoP(email)) { // Si la entrada es peatonal, activar el otro servo
                    sendWebSocketCommand("http://localhost:8080/api/servo/moveP");
                }

                return qr;
            }
        }
        return null;
    }

    private void sendWebSocketCommand(String url) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Servo activation response: " + response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean shouldOpenServo(String email) {
        Tablet tablet = tabletRepository.findBySecurityGuard_Email(email).orElseThrow(() -> new RuntimeException("Tablet not found"));
        return "Vehicle".equalsIgnoreCase(tablet.getLocation());
    }

    @Override
    public boolean shouldOpenServoP(String email) {
        Tablet tablet = tabletRepository.findBySecurityGuard_Email(email).orElseThrow(() -> new RuntimeException("Tablet not found"));
        return "Pedestrian".equalsIgnoreCase(tablet.getLocation());
    }

    @Override
    public void updateQrExpiration(int duration) {
        QRLimit qrLimit = qrLimitRepository.findById(1).orElseThrow(() -> new RuntimeException("QR Limit not found"));
        qrLimit.setMinutesDuration(duration);
        qrLimitRepository.save(qrLimit);
    }


    private boolean isRequestValidForQr(Request request) {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        return today.isEqual(request.getEntryDate()) && now.isAfter(request.getBeforeTime()) && now.isBefore(request.getAfterTime());
    }

    private QR generateQrForRequest(String token, Request request, User user) {
        QR qr = new QR();
        qr.setToken(token);
        qr.setUsed(false);

        QRLimit qrLimit = qrLimitRepository.findById(1).orElseThrow(() -> new RuntimeException("QR Limit not found"));
        qr.setQrLimit(qrLimit);

        qr.setRequest(request);

        LocalTime now = LocalTime.now();
        LocalDate today = LocalDate.now();

        qr.setExpDate(today);
        qr.setExpTime(now.plusMinutes(qrLimit.getMinutesDuration()));
        qr.setUser(user);
        return qrRepository.save(qr);
    }

}
