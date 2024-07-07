package org.luismore.hlvsapi.services.impls;

import jakarta.transaction.Transactional;
import org.luismore.hlvsapi.domain.entities.Role;
import org.luismore.hlvsapi.domain.entities.User;
import org.luismore.hlvsapi.repositories.RoleRepository;
import org.luismore.hlvsapi.repositories.UserRepository;
import org.luismore.hlvsapi.services.RoleCleanupService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RoleCleanupServiceImpl implements RoleCleanupService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public RoleCleanupServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public void removeDuplicateRoles(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        List<Role> uniqueRoles = user.getRoles().stream().distinct().collect(Collectors.toList());
        user.setRoles(uniqueRoles);
        userRepository.save(user);
    }
}



