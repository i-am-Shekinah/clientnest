package com.clientnest.security.refresh_token;

import com.clientnest.user.User;
import com.clientnest.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Value("${security.jwt.refresh-expiration}")
    private Long refreshExpiration;

    public RefreshTokenDto createRefreshToken(User user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .refreshToken(UUID.randomUUID().toString())
                .user(user)
                .expiryDate(Instant.now().plusMillis(refreshExpiration))
                .revoked(false)
                .createdAt(Instant.now())
                .build();

        final var saved = refreshTokenRepository.save(refreshToken);
        return RefreshTokenMapper.INSTANCE.toDto(saved);
    }

    public RefreshTokenDto verifyRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (refreshToken.isRevoked()) {
            throw new RuntimeException("Refresh token is revoked");
        }

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshToken.setRevoked(true);
            refreshTokenRepository.save(refreshToken);
            throw new RuntimeException("Refresh token is expired");
        }

        return RefreshTokenMapper.INSTANCE.toDto(refreshToken);
    }

    // Revoke (logout)
    public void revokeByUser(User user) {
        var tokens = refreshTokenRepository.findByUser(user);

        tokens.forEach(t -> t.setRevoked(true));
    }

    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }
}
