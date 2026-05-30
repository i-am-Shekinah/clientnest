package com.clientnest.auth.auth;

import com.clientnest.auth.refresh_token.RefreshToken;
import com.clientnest.auth.refresh_token.RefreshTokenService;
import com.clientnest.security.CustomUserDetailsService;
import com.clientnest.security.JwtService;
import com.clientnest.user.User;
import com.clientnest.user.UserDto;
import com.clientnest.user.UserRepository;
import com.clientnest.user.UserType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.clientnest.auth.auth.AuthDto.LoginRequestDto;
import com.clientnest.auth.auth.AuthDto.AuthResponseDto;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthResponseDto register(AuthDto.RegisterRequestDto dto) {
        if (userRepository.findByEmail(dto.email()).isPresent()) {
            throw new RuntimeException("Email already in use");
        }

        User user = User.builder()
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .email(dto.email())
                .password(passwordEncoder.encode(dto.password()))
                .userType(UserType.CLIENT)
                .emailVerified(false)
                .deleted(false)
                .createdAt(Instant.now())
                .build();

        user = userRepository.save(user);

        var userDetails = customUserDetailsService.loadUserByUsername(user.getEmail());
        String accessToken = jwtService.generateToken(userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return new AuthResponseDto(accessToken, refreshToken.getRefreshToken(), mapToUserDto(user));
    }

    public AuthResponseDto login(LoginRequestDto dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.email(),
                        dto.password()
                )
        );

        var userDetails = customUserDetailsService.loadUserByUsername(dto.email());
        var user = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        String accessToken = jwtService.generateToken(userDetails);

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return new AuthResponseDto(accessToken, refreshToken.getRefreshToken(), mapToUserDto(user));
    }

    public AuthResponseDto refreshToken(AuthDto.RefreshTokenRequestDto dto) {
        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(dto.refreshToken());
        var user = refreshToken.getUser();
        var userDetails = customUserDetailsService.loadUserByUsername(user.getEmail());

        String accessToken = jwtService.generateToken(userDetails);

        return new AuthResponseDto(accessToken, refreshToken.getRefreshToken(), mapToUserDto(user));
    }

    private UserDto mapToUserDto(User user) {
        return new UserDto(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getUserType().name(),
                user.isEmailVerified(),
                user.isDeleted(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getDeletedAt()
        );
    }
}