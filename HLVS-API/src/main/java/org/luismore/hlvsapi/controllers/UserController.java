package org.luismore.hlvsapi.controllers;

import jakarta.validation.Valid;
import org.luismore.hlvsapi.domain.dtos.*;
import org.luismore.hlvsapi.domain.entities.User;
import org.luismore.hlvsapi.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PatchMapping("/change-password")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> changePassword(@AuthenticationPrincipal User user, @RequestBody  @Valid ChangePasswordRequestDTO request) {
        if (!userService.checkPassword(user, request.getOldPassword())) {
            return GeneralResponse.getResponse(HttpStatus.CONFLICT, "Current password is incorrect");
        }

        try {
            userService.updatePassword(user.getEmail(), request.getNewPassword());
            return GeneralResponse.getResponse(HttpStatus.OK, "Password changed successfully");
        } catch (Exception e) {
            return GeneralResponse.getResponse(HttpStatus.EXPECTATION_FAILED, "You Can(not) update password");
        }
    }

    @PostMapping("/change-roles")
 //   @PreAuthorize("hasAuthority('ROLE_sysadmin')")
    public ResponseEntity<GeneralResponse> changeRoles(@RequestBody @Valid ChangeRoleDTO info) {
        User user = userService.findByIdentifier(info.getIdentifier());

        if (user == null) {
            return GeneralResponse.getResponse(HttpStatus.NOT_FOUND);
        }

        userService.changeRoles(user, info.getRoles());

        return GeneralResponse.getResponse("Roles changed");
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ROLE_receptionist') and hasAuthority('ROLE_sysadmin')")
    public ResponseEntity<GeneralResponse> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return GeneralResponse.getResponse(HttpStatus.OK, users);
    }

    @GetMapping("/roles/{email}")
    //@PreAuthorize("hasAuthority('ROLE_sysadmin')")
    public ResponseEntity<GeneralResponse> getUserRoles(@PathVariable String email) {
        List<String> roles = userService.getUserRolesByEmail(email);
        if (roles.isEmpty()) {
            return GeneralResponse.getResponse(HttpStatus.NOT_FOUND, "Roles Can(not) be found for user with email: " + email);
        }

        UserRoleResponseDTO responseDTO = new UserRoleResponseDTO();
        responseDTO.setEmail(email);
        responseDTO.setRoles(roles);

        return GeneralResponse.getResponse(HttpStatus.OK, responseDTO);
    }

    @GetMapping("/info")
    public ResponseEntity<UserInfoDTO> getUserInfo(@AuthenticationPrincipal User user) {
        UserInfoDTO userInfo = new UserInfoDTO();
        userInfo.setId(user.getId());
        userInfo.setEmail(user.getEmail());
        userInfo.setName(user.getName());
        userInfo.setRoles(user.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.toList()));

        return new ResponseEntity<>(userInfo, HttpStatus.OK);
    }
}

