package org.luismore.hlvsapi.services;

import java.util.UUID;

public interface RoleCleanupService {
    void removeDuplicateRoles(UUID userId);
}
