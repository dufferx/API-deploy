package org.luismore.hlvsapi.controllers;


import io.jsonwebtoken.io.IOException;
import org.luismore.hlvsapi.services.QrService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.luismore.hlvsapi.domain.dtos.*;
import org.luismore.hlvsapi.domain.entities.QR;
import org.luismore.hlvsapi.domain.entities.User;
import org.luismore.hlvsapi.services.QrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CopyOnWriteArraySet;

@RestController
@RequestMapping("/api/qr")
public class QrController {

    private final QrService qrService;
    private static final CopyOnWriteArraySet<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

    public QrController(QrService qrService) {
        this.qrService = qrService;
    }

    @PostMapping("/generate")
    @PreAuthorize("hasAuthority('ROLE_admin') or hasAuthority('ROLE_security guard')")
    public ResponseEntity<QR> generateQrToken(@RequestBody CreateQrDTO createQrDTO) {
        QR qr = qrService.generateQrToken(createQrDTO);
        return ResponseEntity.ok(qr);
    }

    @PostMapping("/generate-for-role")
    @PreAuthorize("hasAuthority('ROLE_admin') or hasAuthority('ROLE_security guard') or hasAuthority('ROLE_main resident') or hasAuthority('ROLE_resident')")
    public ResponseEntity<QRResponseDTO> generateQrTokenForRole(@AuthenticationPrincipal User user, @RequestBody CreateQrForRoleDTO createQrForRoleDTO) {
        QR qr = qrService.generateQrTokenForRole(user, createQrForRoleDTO);
        QRResponseDTO responseDTO = new QRResponseDTO();
        responseDTO.setUniqueID(qr.getUniqueID());
        responseDTO.setToken(qr.getToken());
        responseDTO.setDuration(qr.getQrLimit().getMinutesDuration());
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/generate-by-user")
    @PreAuthorize("hasAuthority('ROLE_visitant')")
    public ResponseEntity<QRResponseDTO> generateQrTokenByUser(@AuthenticationPrincipal User user, @RequestBody CreateQrForUserDTO createQrDTO) {
        QR qr = qrService.generateQrTokenByUser(user, createQrDTO);
        QRResponseDTO responseDTO = new QRResponseDTO();
        responseDTO.setUniqueID(qr.getUniqueID());
        responseDTO.setToken(qr.getToken());
        responseDTO.setDuration(qr.getQrLimit().getMinutesDuration());
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/expiration")
    @PreAuthorize("hasAuthority('ROLE_admin') or hasAuthority('ROLE_security guard')")
    public ResponseEntity<GeneralResponse> updateQrExpiration(@RequestParam int duration) {
        qrService.updateQrExpiration(duration);
        return GeneralResponse.getResponse(HttpStatus.OK, "Limit time updated successfully.");
    }

    @GetMapping("/scan")
    @PreAuthorize("hasAuthority('ROLE_admin') or hasAuthority('ROLE_security guard')")
    public ResponseEntity<QR> scanQrToken(@RequestParam String token, @AuthenticationPrincipal User user) {
        QR qr = qrService.scanQrToken(token, user.getEmail());
        return ResponseEntity.ok(qr);
    }


}
