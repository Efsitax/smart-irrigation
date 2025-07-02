package com.kadir.smartirrigation.application.service.notification;

import com.kadir.smartirrigation.domain.model.notification.DeviceToken;
import com.kadir.smartirrigation.domain.service.notification.DeviceTokenService;
import com.kadir.smartirrigation.infrastructure.repository.notification.DeviceTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceTokenServiceImpl implements DeviceTokenService {
    private final DeviceTokenRepository repository;

    @Override
    public void saveToken(String token) {
        repository.findByToken(token).ifPresentOrElse(
                existing -> {
                    existing.setCreatedAt(LocalDateTime.now());
                    repository.save(existing);
                },
                () -> {
                    repository.save(DeviceToken.builder()
                            .token(token)
                            .createdAt(LocalDateTime.now())
                            .build());
                }
        );
    }

    @Override
    public List<String> getAllTokens() {
        return repository.findAllTokens();
    }

    @Override
    public void deleteToken(String token) {
        repository.deleteByToken(token);
    }
}
