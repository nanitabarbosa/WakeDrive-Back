package com.wakedrive.backend.auth.repository;

import com.wakedrive.backend.auth.entity.RefreshToken;
import com.wakedrive.backend.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    void deleteByUser(User user);
}
