package org.luismore.hlvsapi.controllers;

import org.luismore.hlvsapi.domain.dtos.AddFamilyMemberDTO;
import org.luismore.hlvsapi.domain.dtos.GeneralResponse;
import org.luismore.hlvsapi.domain.dtos.UserDTO;
import org.luismore.hlvsapi.domain.entities.User;
import org.luismore.hlvsapi.services.FamilyService;
import org.luismore.hlvsapi.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/family")
public class FamilyController {

    private final FamilyService familyService;
    private final UserService userService;

    public FamilyController(FamilyService familyService, UserService userService) {
        this.familyService = familyService;
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_main resident')")
    public ResponseEntity<GeneralResponse> getFamilyMembers(@AuthenticationPrincipal UserDetails userDetails) {
        User mainResident = userService.findByIdentifier(userDetails.getUsername());
        List<UserDTO> familyMembers = familyService.getFamilyMembers(mainResident.getHouse().getId());
        return GeneralResponse.getResponse(HttpStatus.OK, familyMembers);
    }

//    @PostMapping
//    @PreAuthorize("hasAuthority('ROLE_main resident')")
//    public ResponseEntity<GeneralResponse> addFamilyMember(@AuthenticationPrincipal UserDetails userDetails, @RequestBody List<AddFamilyMemberDTO> addFamilyMemberDTOList) {
//        User mainResident = userService.findByIdentifier(userDetails.getUsername());
//        UUID houseId = mainResident.getHouse().getId();
//
//        if (familyService.isHouseFull(houseId)) {
//            return GeneralResponse.getResponse(HttpStatus.BAD_REQUEST, "You Can(not) add another family member, the house is already full.");
//        }
//
//        familyService.addFamilyMember(houseId, addFamilyMemberDTOList);
//        return GeneralResponse.getResponse(HttpStatus.CREATED);
//    }

    @PutMapping
    @PreAuthorize("hasAuthority('ROLE_main resident')")
    public ResponseEntity<GeneralResponse> updateFamilyMembers(@AuthenticationPrincipal UserDetails userDetails, @RequestBody List<AddFamilyMemberDTO> addFamilyMemberDTOList) {
        User mainResident = userService.findByIdentifier(userDetails.getUsername());
        UUID houseId = mainResident.getHouse().getId();

        familyService.updateFamilyMembers(houseId, addFamilyMemberDTOList);
        return GeneralResponse.getResponse(HttpStatus.OK, "Family members updated successfully");
    }
}

