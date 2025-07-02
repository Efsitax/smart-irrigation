package com.kadir.smartirrigation.application.service.temperature;

import com.fasterxml.jackson.databind.JsonNode;
import com.kadir.smartirrigation.common.exception.TemperatureServiceException;
import com.kadir.smartirrigation.domain.service.temperature.TemperatureService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class OpenWeatherMapTemperatureService implements TemperatureService {

    private final RestTemplate restTemplate;

    @Value("${weather.api.key}")
    private String apiKey;

    @Value("${weather.location}")
    private String location;

    @Override
    public double getCurrentTemperatureCelsius() {
        String url = String.format(
                "https://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&appid=%s",
                location, apiKey
        );

        try {
            JsonNode root = restTemplate.getForObject(url, JsonNode.class);

            if (root == null || !root.has("main") || root.path("main").path("temp").isMissingNode()) {
                throw new TemperatureServiceException("Expected 'main.temp' field is missing in OpenWeatherMap response");
            }

            return root.path("main").path("temp").asDouble();

        } catch (HttpClientErrorException.Unauthorized ex) {
            throw new TemperatureServiceException("Weather API authorization failed (401)", ex);

        } catch (HttpClientErrorException.NotFound ex) {
            throw new TemperatureServiceException(
                    String.format("Weather data not found for location '%s' (404)", location), ex
            );

        } catch (HttpClientErrorException ex) {
            throw new TemperatureServiceException("Weather API client error: " + ex.getStatusCode(), ex);

        } catch (ResourceAccessException ex) {
            throw new TemperatureServiceException("Unable to access Weather API service", ex);

        } catch (RestClientException ex) {
            throw new TemperatureServiceException("General Weather service error", ex);
        }
    }
}
