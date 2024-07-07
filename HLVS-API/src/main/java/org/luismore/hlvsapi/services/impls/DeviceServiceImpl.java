package org.luismore.hlvsapi.services.impls;

import jakarta.transaction.Transactional;
import org.luismore.hlvsapi.domain.dtos.CreateDeviceDTO;
import org.luismore.hlvsapi.domain.dtos.DeviceDTO;
import org.luismore.hlvsapi.domain.dtos.UpdateDeviceDTO;
import org.luismore.hlvsapi.domain.entities.Tablet;
import org.luismore.hlvsapi.domain.entities.User;
import org.luismore.hlvsapi.repositories.TabletRepository;
import org.luismore.hlvsapi.repositories.UserRepository;
import org.luismore.hlvsapi.services.DeviceService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DeviceServiceImpl implements DeviceService {

    private final TabletRepository tabletRepository;
    private final UserRepository userRepository;

    public DeviceServiceImpl(TabletRepository tabletRepository, UserRepository userRepository) {
        this.tabletRepository = tabletRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<DeviceDTO> getAllDevices() {
        return tabletRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void createDevice(CreateDeviceDTO createDeviceDTO) {
        Optional<Tablet> existingTablet = tabletRepository.findBySecurityGuard_Email(createDeviceDTO.getSecurityGuardEmail());
        if (existingTablet.isPresent()) {
            throw new IllegalArgumentException("Security guard is already assigned to another device.");
        }

        Tablet tablet = new Tablet();
        tablet.setSerialNumber(createDeviceDTO.getSerialNumber());
        tablet.setLocation(createDeviceDTO.getLocation());
        User guard = userRepository.findByEmail(createDeviceDTO.getSecurityGuardEmail())
                .orElseThrow(() -> new IllegalArgumentException("Security guard cannot be found with email: " + createDeviceDTO.getSecurityGuardEmail()));
        tablet.setSecurityGuard(guard);
        tabletRepository.save(tablet);
    }

    @Override
    @Transactional
    public void updateDevice(UpdateDeviceDTO updateDeviceDTO) {
        Tablet tablet = tabletRepository.findBySerialNumber(updateDeviceDTO.getSerialNumber())
                .orElseThrow(() -> new IllegalArgumentException("Tablet cannot be found with serial number: " + updateDeviceDTO.getSerialNumber()));
        if (updateDeviceDTO.getLocation() != null) {
            tablet.setLocation(updateDeviceDTO.getLocation());
        }
        if (updateDeviceDTO.getSecurityGuardEmail() != null) {
            User guard = userRepository.findByEmail(updateDeviceDTO.getSecurityGuardEmail())
                    .orElseThrow(() -> new IllegalArgumentException("Security guard cannot be found with email: " + updateDeviceDTO.getSecurityGuardEmail()));
            tablet.setSecurityGuard(guard);
        }
        tabletRepository.save(tablet);
    }

    @Override
    public List<DeviceDTO> getDevicesByGuardEmail(String email) {
        User guard = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Security guard cannot be found with email: " + email));
        return tabletRepository.findBySecurityGuard(guard).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public DeviceDTO getDeviceBySerialOrId(String serialNumber, UUID id) {
        Optional<Tablet> tabletOptional = tabletRepository.findBySerialNumber(serialNumber);
        if (tabletOptional.isEmpty()) {
            tabletOptional = tabletRepository.findById(id);
        }
        Tablet tablet = tabletOptional.orElseThrow(() -> new IllegalArgumentException("Device cannot be found with given serial number or ID"));
        return convertToDTO(tablet);
    }

    private DeviceDTO convertToDTO(Tablet tablet) {
        DeviceDTO dto = new DeviceDTO();
        dto.setId(tablet.getId().toString());
        dto.setSerialNumber(tablet.getSerialNumber());
        dto.setLocation(tablet.getLocation());
        dto.setSecurityGuardEmail(tablet.getSecurityGuard() != null ? tablet.getSecurityGuard().getEmail() : null);
        dto.setSecurityNameGuard(tablet.getSecurityGuard() != null ? tablet.getSecurityGuard().getName() : null); // Agregar el nombre del guardia de seguridad
        return dto;
    }
}
