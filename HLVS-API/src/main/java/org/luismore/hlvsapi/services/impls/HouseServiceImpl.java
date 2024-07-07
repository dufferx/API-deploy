package org.luismore.hlvsapi.services.impls;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.luismore.hlvsapi.domain.dtos.*;
import org.luismore.hlvsapi.domain.entities.House;
import org.luismore.hlvsapi.domain.entities.Role;
import org.luismore.hlvsapi.domain.entities.User;
import org.luismore.hlvsapi.repositories.HouseRepository;
import org.luismore.hlvsapi.repositories.RoleRepository;
import org.luismore.hlvsapi.repositories.UserRepository;
import org.luismore.hlvsapi.services.HouseService;
import org.luismore.hlvsapi.services.RoleCleanupService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class HouseServiceImpl implements HouseService {

    private final HouseRepository houseRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final RoleCleanupService roleCleanupService;

    public HouseServiceImpl(HouseRepository houseRepository, UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository, RoleCleanupService roleCleanupService) {
        this.houseRepository = houseRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.roleCleanupService = roleCleanupService;
    }

    @Override
    public Page<HouseDTO> getAllHouses(Pageable pageable) {
        return houseRepository.findAll(pageable).map(this::convertToDTO);
    }

    @Override
    public List<HouseDTO> getHousesByFilter(String filter) {
        List<House> houses;

        if (filter.matches("\\d+")) {
            houses = houseRepository.findByHouseNumber(filter).map(List::of).orElse(List.of());
        } else if (filter.contains("@")) {
            houses = houseRepository.findByResidents_EmailOrLeader_Email(filter, filter);
        } else {
            houses = houseRepository.findByAddress(filter);
        }

        return houses.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public HouseDTO getHouseByNumber(String houseNumber) {
        House house = houseRepository.findByHouseNumber(houseNumber)
                .orElseThrow(() -> new EntityNotFoundException("House not found with house number: " + houseNumber));
        return convertToDTO(house);
    }

    @Override
    @Transactional
    public HouseDTO createHouse(CreateHouseDTO createHouseDTO) {
        validateHouseLeader(createHouseDTO.getLeaderEmail());

        House house = new House();
        house.setHouseNumber(createHouseDTO.getHouseNumber());
        house.setAddress(createHouseDTO.getAddress());
        house.setResidentNumber(createHouseDTO.getResidentNumber());

        if (createHouseDTO.getLeaderEmail() != null) {
            User leader = userRepository.findByEmail(createHouseDTO.getLeaderEmail())
                    .orElseThrow(() -> new IllegalArgumentException("Leader not found with email: " + createHouseDTO.getLeaderEmail()));
            Role mainResidentRole = roleRepository.findById("MAIN")
                    .orElseThrow(() -> new IllegalArgumentException("Main resident role not found"));
            leader.getRoles().add(mainResidentRole);
            leader.getRoles().removeIf(role -> role.getId().equals("VISI"));
            userRepository.save(leader);
            house.setLeader(leader);
            roleCleanupService.removeDuplicateRoles(leader.getId());
        }

        house = houseRepository.save(house);
        return convertToDTO(house);
    }

    @Override
    @Transactional
    public void updateHouse(UpdateHouseDTO updateHouseDTO) {
        House house = houseRepository.findById(updateHouseDTO.getId())
                .orElseThrow(() -> new IllegalArgumentException("House not found"));

        if (updateHouseDTO.getHouseNumber() != null) {
            house.setHouseNumber(updateHouseDTO.getHouseNumber());
        }
        if (updateHouseDTO.getAddress() != null) {
            house.setAddress(updateHouseDTO.getAddress());
        }
        if (updateHouseDTO.getResidentNumber() != null) {
            house.setResidentNumber(updateHouseDTO.getResidentNumber());
        }
        if (updateHouseDTO.getLeaderEmail() != null) {
            User newLeader = userRepository.findByEmail(updateHouseDTO.getLeaderEmail())
                    .orElseThrow(() -> new IllegalArgumentException("Leader not found with email: " + updateHouseDTO.getLeaderEmail()));
            if (house.getLeader() != null && !house.getLeader().equals(newLeader)) {
                User previousLeader = house.getLeader();
                previousLeader.getRoles().removeIf(role -> role.getId().equals("MAIN"));
                userRepository.save(previousLeader);
                roleCleanupService.removeDuplicateRoles(previousLeader.getId());
            }
            Role mainResidentRole = roleRepository.findById("MAIN")
                    .orElseThrow(() -> new IllegalArgumentException("Main resident role not found"));
            newLeader.getRoles().add(mainResidentRole);
            newLeader.getRoles().removeIf(role -> role.getId().equals("VISI"));
            userRepository.save(newLeader);
            house.setLeader(newLeader);
            roleCleanupService.removeDuplicateRoles(newLeader.getId());
        }

        if (updateHouseDTO.getResidents() != null && !updateHouseDTO.getResidents().isEmpty()) {
            int currentResidents = house.getResidents() != null ? house.getResidents().size() : 0;
            int newResidents = updateHouseDTO.getResidents().size();
            int maxResidents = Integer.parseInt(house.getResidentNumber()) + 1;

            if (currentResidents + newResidents > maxResidents) {
                throw new IllegalArgumentException("Adding these residents would exceed the maximum number of residents allowed for this house");
            }

            List<String> currentResidentEmails = house.getResidents().stream()
                    .map(User::getEmail)
                    .collect(Collectors.toList());

            List<String> newResidentEmails = updateHouseDTO.getResidents().stream()
                    .map(UpdateResidentDTO::getEmail)
                    .collect(Collectors.toList());

            List<String> removedResidentEmails = currentResidentEmails.stream()
                    .filter(email -> !newResidentEmails.contains(email))
                    .collect(Collectors.toList());

            for (UpdateResidentDTO residentDTO : updateHouseDTO.getResidents()) {
                User user = userRepository.findByEmail(residentDTO.getEmail())
                        .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + residentDTO.getEmail()));

                if (user.getHouse() != null && !user.getHouse().getId().equals(house.getId())) {
                    throw new IllegalArgumentException("User already belongs to another house");
                }

                Role residentRole = roleRepository.findById("RESI")
                        .orElseThrow(() -> new IllegalArgumentException("Resident role not found"));

                user.getRoles().add(residentRole);
                user.getRoles().removeIf(role -> role.getId().equals("VISI"));
                user.setHouse(house);
                user.setDui(residentDTO.getDui());
                userRepository.save(user);
                roleCleanupService.removeDuplicateRoles(user.getId());
            }

            for (String removedEmail : removedResidentEmails) {
                User removedUser = userRepository.findByEmail(removedEmail)
                        .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + removedEmail));

                removedUser.setHouse(null);
                removedUser.getRoles().removeIf(role -> role.getId().equals("RESI"));
                userRepository.save(removedUser);
                roleCleanupService.removeDuplicateRoles(removedUser.getId());
            }
        }

        houseRepository.save(house);
    }


    @Override
    @Transactional
    public void updateResident(UpdateResidentDTO updateResidentDTO) {
        User user = userRepository.findByEmail(updateResidentDTO.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        House house = houseRepository.findById(updateResidentDTO.getHouseId())
                .orElseThrow(() -> new IllegalArgumentException("House not found"));
        if (updateResidentDTO.getName() != null) {
            user.setName(updateResidentDTO.getName());
        }
        if (updateResidentDTO.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(updateResidentDTO.getPassword()));
        }
        user.setHouse(house);
        user.setDui(updateResidentDTO.getDui());
        userRepository.save(user);
    }

    @Override
    public List<UserDTO> getResidentsByHouseId(UUID houseId) {
        List<User> residents = userRepository.findByHouseId(houseId);
        return residents.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<HouseDTO> getHouseByUserEmail(String userEmail) {
        List<House> houses = houseRepository.findByResidents_EmailOrLeader_Email(userEmail, userEmail);
        return houses.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getName());
        dto.setEmail(user.getEmail());
        dto.setDui(user.getDui());
        return dto;
    }

    private HouseDTO convertToDTO(House house) {
        HouseDTO dto = new HouseDTO();
        dto.setId(house.getId());
        dto.setHouseNumber(house.getHouseNumber());
        dto.setAddress(house.getAddress());
        dto.setResidentNumber(house.getResidentNumber());
        dto.setLeader(house.getLeader() != null ? house.getLeader().getEmail() : null);
        dto.setNameLeader(house.getLeader() != null ? house.getLeader().getName() : null);
        dto.setResidents(house.getResidents() != null ? house.getResidents().stream().map(this::convertToDTO).collect(Collectors.toList()) : null);
        return dto;
    }

    @Override
    @Transactional
    public void assignLeader(UUID houseId, String email) {
        House house = houseRepository.findById(houseId)
                .orElseThrow(() -> new EntityNotFoundException("House not found with id: " + houseId));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));

        if (!user.getHouse().getId().equals(houseId)) {
            throw new IllegalArgumentException("User does not belong to this house");
        }

        house.setLeader(user);
        houseRepository.save(house);
    }

    private void validateHouseLeader(String leaderEmail) {
        if (leaderEmail != null) {
            User leader = userRepository.findByEmail(leaderEmail)
                    .orElseThrow(() -> new IllegalArgumentException("Leader not found with email: " + leaderEmail));
            if (leader.getHouse() != null) {
                throw new IllegalArgumentException("Leader already belongs to another house");
            }
        }
    }
}
