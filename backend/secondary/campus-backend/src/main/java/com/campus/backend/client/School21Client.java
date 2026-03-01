package com.campus.backend.client;

import com.campus.backend.dto.school21.ParticipantV1DTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class School21Client {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String apiKey;

    public School21Client(
            @Value("${school21.api.base-url}") String baseUrl,
            @Value("${school21.api.api-key}") String apiKey) {

        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.restTemplate = new RestTemplate();

        log.info("School21Client initialized with baseUrl: {}", baseUrl);
        log.info("API Key provided: {}", apiKey != null && !apiKey.isEmpty() ? "YES" : "NO");
    }

    public ParticipantV1DTO getParticipantByLogin(String login) {
        log.info("Fetching participant info from School21 for login: {}", login);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", apiKey);
            headers.set("Content-Type", "application/json");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            String url = baseUrl + "/v1/participants/" + login;

            ResponseEntity<ParticipantV1DTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    ParticipantV1DTO.class
            );

            return response.getBody();

        } catch (Exception e) {
            log.error("Failed to fetch participant {} from School21: {}", login, e.getMessage());
            // Для разработки возвращаем мок-данные
            ParticipantV1DTO mock = new ParticipantV1DTO();
            mock.setLogin(login);
            return mock;
        }
    }
}