package com.kadir.smartirrigation.application.service.temperature;

import com.kadir.smartirrigation.common.exception.TemperatureConfigNotFoundException;
import com.kadir.smartirrigation.domain.model.temperature.TemperatureConfig;
import com.kadir.smartirrigation.domain.service.temperature.TemperatureConfigService;
import com.kadir.smartirrigation.domain.service.temperature.TemperatureService;
import com.kadir.smartirrigation.infrastructure.repository.temperature.TemperatureConfigRepository;
import com.kadir.smartirrigation.web.dto.temperature.TemperatureConfigDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TemperatureConfigServiceImpl implements TemperatureConfigService {
    private final TemperatureConfigRepository temperatureConfigRepository;
    private final TemperatureService temperatureService;

    @Override
    public TemperatureConfigDto getConfig() {
        TemperatureConfig config = getTemperatureConfigOrThrow();
        return toDto(config);
    }

    @Override
    public TemperatureConfigDto updateConfig(TemperatureConfigDto dto) {
        TemperatureConfig config = getTemperatureConfigOrThrow();

        config.setThreshold(dto.threshold());
        config.setExtraSeconds(dto.extraSeconds());
        config.setActive(dto.active());
        config.setUpdatedAt(LocalDateTime.now());

        return toDto(temperatureConfigRepository.save(config));
    }

    @Override
    public boolean evaluateTemperature() {
        TemperatureConfig config = getTemperatureConfigOrThrow();
        double temperature = temperatureService.getCurrentTemperatureCelsius();
        return temperature >= config.getThreshold() && config.isActive();
    }

    private TemperatureConfig getTemperatureConfigOrThrow() {
        return temperatureConfigRepository.findById(1L)
                .orElseThrow(() -> new TemperatureConfigNotFoundException("Temperature config not found"));
    }

    public static TemperatureConfigDto toDto(TemperatureConfig entity) {
        return new TemperatureConfigDto(
                entity.getThreshold(),
                entity.getExtraSeconds(),
                entity.isActive(),
                entity.getUpdatedAt()
        );
    }
}
