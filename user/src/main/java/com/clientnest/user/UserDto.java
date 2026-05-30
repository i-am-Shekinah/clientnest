package com.clientnest.user;

import java.time.Instant;
import java.util.UUID;

public record UserDto(
    UUID userId,
    String firstName,
    String lastName,
    String email,
    String userType,
    boolean emailVerified,
    boolean deleted,
    Instant createdAt,
    Instant updatedAt,
    Instant deletedAt
) {}
