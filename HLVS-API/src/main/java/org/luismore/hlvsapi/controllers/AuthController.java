package org.luismore.hlvsapi.controllers;

import jakarta.validation.Valid;
import org.luismore.hlvsapi.domain.dtos.*;
import org.luismore.hlvsapi.domain.entities.Token;
import org.luismore.hlvsapi.domain.entities.User;
import org.luismore.hlvsapi.services.RoleCleanupService;
import org.luismore.hlvsapi.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final RoleCleanupService roleCleanupService;

    public AuthController(UserService userService, RoleCleanupService roleCleanupService) {
        this.userService = userService;
        this.roleCleanupService = roleCleanupService;
    }



    @PostMapping("/register")
    public ResponseEntity<GeneralResponse> register(@RequestBody @Valid UserRegisterDTO info){
        User user = userService.findByNameOrEmail(info.getName(), info.getEmail());
        if(user != null){
            return GeneralResponse.getResponse(HttpStatus.CONFLICT, "User already exists");
        }

        userService.create(info);
        return GeneralResponse.getResponse(HttpStatus.CREATED, "User registered successfully");
    }

    @PreAuthorize("permitAll()")
    @PostMapping("/login")
    public ResponseEntity<GeneralResponse> login(@RequestBody @Valid LoginDTO info, BindingResult validations) throws Exception {
        User user = userService.findByIdentifier(info.getIdentifier());
        if(user == null){
            return GeneralResponse.getResponse(HttpStatus.CONFLICT, "User Can(not) be found");
        }

        if(!userService.checkPassword(user, info.getPassword()) || !userService.isActive(user)){
            return GeneralResponse.getResponse(HttpStatus.NOT_FOUND, "User Can(not) be found");
        }

        Token token = userService.registerToken(user);
        return GeneralResponse.getResponse(HttpStatus.OK, new TokenDTO(token));

    }

    @PreAuthorize("permitAll()")
    @PostMapping("/register-or-login")
    public ResponseEntity<GeneralResponse> registerOrLogin(@RequestBody @Valid UserRegisterDTO info) throws Exception {
        User user = userService.findByEmail(info.getEmail());
        if (user == null) {
            userService.create(info);
            user = userService.findByEmail(info.getEmail());
        }

        if(!userService.checkPassword(user, info.getPassword()) || !userService.isActive(user)){
            return GeneralResponse.getResponse(HttpStatus.NOT_FOUND, "User Can(not) be found");
        }

        roleCleanupService.removeDuplicateRoles(user.getId());

        Token token = userService.registerToken(user);
        return GeneralResponse.getResponse(HttpStatus.OK, new TokenDTO(token));
    }

}
