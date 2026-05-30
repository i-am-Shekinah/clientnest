package com.clientnest.business;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

public record BusinessDto(
        UUID businessId,
        String businessName,
        String businessDescription,
        String ownerFirstName,
        String ownerLastName,
        String businessEmail,
        boolean isDeleted,
        Instant createdAt,
        Instant updatedAt
) {
    public record AddBusinessDto(
            @NotBlank
            @Size(max = 50) String businessName,

            @Size(max = 1000)
            String businessDescription,

            @NotBlank
            String ownerFirstName,

            @NotBlank
            String ownerLastName,

            @NotBlank(message = "Business email is required")
            @Email(message = "Invalid email format")
            String businessEmail
    ) {}

    public record UpdateBusinessDto(
            @NotBlank
            @Size(max = 50)
            String businessName,

            @Size(max = 1000)
            String businessDescription,

            @NotBlank
            @Email
            String businessEmail
    ) {}
}
