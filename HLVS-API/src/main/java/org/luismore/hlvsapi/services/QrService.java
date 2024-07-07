package org.luismore.hlvsapi.services;

import org.luismore.hlvsapi.domain.dtos.CreateQrDTO;
import org.luismore.hlvsapi.domain.dtos.CreateQrForRoleDTO;
import org.luismore.hlvsapi.domain.dtos.CreateQrForUserDTO;
import org.luismore.hlvsapi.domain.entities.QR;
import org.luismore.hlvsapi.domain.entities.User;

public interface QrService {
    QR generateQrToken(CreateQrDTO createQrDTO);
    QR generateQrTokenForRole(User user, CreateQrForRoleDTO createQrForRoleDTO);
//  QR scanQrToken(String token, String serialNumber);
    QR scanQrToken(String token, String email);
    void updateQrExpiration(int duration);
    QR generateQrTokenByUser(User user, CreateQrForUserDTO createQrDTO);
    boolean shouldOpenServo(String email);
    boolean shouldOpenServoP(String email);

}
