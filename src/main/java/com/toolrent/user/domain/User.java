package com.toolrent.user.domain;

import java.util.UUID;

public record User(
        UUID id,
        UUID tenantId,
        int code,
        String name,
        String email,
        String passwordHash,
        Role role,
        boolean active
) {

    public static User createAdmin(UUID tenantId, String name, String email, String passwordHash) {
        return new User(UUID.randomUUID(), tenantId, 1, name, email, passwordHash, Role.ADMIN, true);
    }
}
