package com.toolrent.tenant.domain;

import java.util.UUID;

public record Tenant(UUID id, String name, boolean active) {

    public static Tenant create(String name) {
        return new Tenant(UUID.randomUUID(), name, true);
    }
}
