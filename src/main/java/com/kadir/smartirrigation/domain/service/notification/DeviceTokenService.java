package com.kadir.smartirrigation.domain.service.notification;

import java.util.List;

public interface DeviceTokenService {
    void saveToken(String token);
    List<String> getAllTokens();
    void deleteToken(String token);
}
