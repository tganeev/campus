package com.campus.backend.client;

import com.campus.backend.dto.school21.ParticipantV1DTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class School21Client {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String apiKey;

    public School21Client(
            @Value("${school21.api.base-url:https://platform.21-school.ru/services/21-school/api}") String baseUrl,
            @Value("${school21.api.api-key:default-key}") String apiKey) {

        this.restTemplate = new RestTemplate();
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;

        log.info("School21Client initialized");
        log.info("Base URL: {}", baseUrl);
        log.info("API Key exists: {}", apiKey != null && !apiKey.isEmpty() && !apiKey.equals("default-key"));
    }

    public ParticipantV1DTO getParticipantByLogin(String login) {
        log.info("Getting participant info for login: {}", login);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            String url = baseUrl + "/v1/participants/" + login;

            ResponseEntity<ParticipantV1DTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    ParticipantV1DTO.class
            );

            log.info("Successfully got participant: {}", login);
            return response.getBody();

        } catch (Exception e) {
            log.warn("Failed to get participant from School21 API: {}", e.getMessage());
            log.info("Returning mock participant for login: {}", login);

            // Возвращаем мок-данные для разработки
            return createMockParticipant(login);
        }
    }

    private ParticipantV1DTO createMockParticipant(String login) {
        ParticipantV1DTO mock = new ParticipantV1DTO();
        mock.setLogin(login);
        return mock;
    }
}