package com.kadir.smartirrigation.domain.service.temperature;

import com.kadir.smartirrigation.web.dto.temperature.TemperatureConfigDto;

public interface TemperatureConfigService {
    TemperatureConfigDto getConfig();
    TemperatureConfigDto updateConfig(TemperatureConfigDto dto);
    boolean evaluateTemperature();
}
