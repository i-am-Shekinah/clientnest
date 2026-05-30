package com.clientnest.auth.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthDto.AuthResponseDto> login(
            @RequestBody @Valid AuthDto.LoginRequestDto request
    ) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthDto.AuthResponseDto> register(
            @RequestBody @Valid AuthDto.RegisterRequestDto request
    ) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthDto.AuthResponseDto> refreshToken(
            @RequestBody @Valid AuthDto.RefreshTokenRequestDto request
    ) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }
}
