    package org.luismore.hlvsapi.controllers;

    import jakarta.validation.Valid;
    import org.luismore.hlvsapi.domain.dtos.*;
    import org.luismore.hlvsapi.services.DeviceService;
    import org.luismore.hlvsapi.services.TimeLimitService;
    import org.luismore.hlvsapi.services.QrLimitService;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.access.prepost.PreAuthorize;
    import org.springframework.web.bind.annotation.*;

    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;
    import java.util.UUID;

    @RestController
    @RequestMapping("/api/devices")
    public class DevicesController {

        private final DeviceService deviceService;
        private final TimeLimitService timeLimitService;
        private final QrLimitService qrLimitService;

        public DevicesController(DeviceService deviceService, TimeLimitService timeLimitService, QrLimitService qrLimitService) {
            this.deviceService = deviceService;
            this.timeLimitService = timeLimitService;
            this.qrLimitService = qrLimitService;
        }

        @GetMapping
        @PreAuthorize("hasAuthority('ROLE_admin')")
        public ResponseEntity<GeneralResponse> getAllDevices() {
            List<DeviceDTO> devices = deviceService.getAllDevices();
            List<TimeLimitDTO> timeLimits = timeLimitService.getAllTimeLimits();
            List<QrLimitDTO> qrLimits = qrLimitService.getAllQrLimits();

            Map<String, Object> response = new HashMap<>();
            response.put("devices", devices);
            response.put("timeLimits", timeLimits);
            response.put("qrLimits", qrLimits);

            return GeneralResponse.getResponse(HttpStatus.OK, response);
        }

        @PostMapping
        @PreAuthorize("hasAuthority('ROLE_admin')")
        public ResponseEntity<GeneralResponse> createDevice(@RequestBody @Valid CreateDeviceDTO createDeviceDTO) {
            try {
                deviceService.createDevice(createDeviceDTO);
                return GeneralResponse.getResponse(HttpStatus.CREATED, "Device created successfully.");
            } catch (IllegalArgumentException e) {
                return GeneralResponse.getResponse(HttpStatus.BAD_REQUEST, e.getMessage());
            }
        }

        @PatchMapping("/update/{serialNumber}")
        @PreAuthorize("hasAuthority('ROLE_admin')")
        public ResponseEntity<GeneralResponse> updateDeviceLocation(
                @PathVariable String serialNumber,
                @RequestBody @Valid UpdateDeviceDTO updateDeviceDTO) {
            updateDeviceDTO.setSerialNumber(serialNumber);
            deviceService.updateDevice(updateDeviceDTO);
            return GeneralResponse.getResponse(HttpStatus.OK, "Device location updated successfully.");
        }

        @GetMapping("/guard/{email}")
        @PreAuthorize("hasAuthority('ROLE_admin')")
        public ResponseEntity<GeneralResponse> getDevicesByGuardEmail(@PathVariable String email) {
            List<DeviceDTO> devices = deviceService.getDevicesByGuardEmail(email);
            return GeneralResponse.getResponse(HttpStatus.OK, devices);
        }

        @GetMapping("/detail")
        @PreAuthorize("hasAuthority('ROLE_admin')")
        public ResponseEntity<GeneralResponse> getDeviceBySerialOrId(@RequestParam(required = false) String serialNumber, @RequestParam(required = false) UUID id) {
            DeviceDTO device = deviceService.getDeviceBySerialOrId(serialNumber, id);
            return GeneralResponse.getResponse(HttpStatus.OK, device);
        }
    }
