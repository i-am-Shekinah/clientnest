package com.clientnest.auth.refresh_token;

import com.clientnest.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByRefreshToken(String refreshToken);

    List<RefreshToken> findByUser(User user);

    void deleteByUser(User user);
}
