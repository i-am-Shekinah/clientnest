package com.clientnest.auth.auth;

import com.clientnest.user.UserDto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthDto() {
    public record LoginRequestDto(
            @Email
            @NotBlank
            String email,

            @NotBlank
            String password) {}

    public record RegisterRequestDto(
            @NotBlank
            String firstName,

            @NotBlank
            String lastName,

            @Email
            @NotBlank
            String email,

            @NotBlank
            String password
    ) {}

    public record RefreshTokenRequestDto(
            @NotBlank
            String refreshToken
    ) {}

    public record AuthResponseDto(
            String accessToken,
            String refreshToken,
            UserDto user
    ) {}
}
