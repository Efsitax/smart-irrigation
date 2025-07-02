package com.kadir.smartirrigation.infrastructure.repository.notification;

import com.kadir.smartirrigation.domain.model.notification.DeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {
    @Query("SELECT dt.token FROM DeviceToken dt")
    List<String> findAllTokens();
    Optional<DeviceToken> findByToken(String token);
    void deleteByToken(String token);
}
