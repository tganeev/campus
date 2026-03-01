// FILE: src/main/java/com/campus/backend/client/School21Client.java
package com.campus.backend.client;

import com.campus.backend.dto.school21.ParticipantV1DTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.function.Consumer;

@Component
@Slf4j
public class School21Client {

    private final RestClient restClient;

    public School21Client(@Value("${school21.api.base-url}") String baseUrl,
                          @Value("${school21.api.api-key}") String apiKey) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeaders(headers -> {
                    headers.set(HttpHeaders.AUTHORIZATION, apiKey);
                    headers.set(HttpHeaders.CONTENT_TYPE, "application/json");
                })
                .build();
        log.info("School21Client initialized with base URL: {}", baseUrl);
    }

    /**
     * Получает информацию об участнике по логину.
     * @param login Логин участника (например, "bibikov-lukyan")
     * @return ParticipantV1DTO или null, если пользователь не найден или произошла ошибка
     */
    public ParticipantV1DTO getParticipantByLogin(String login) {
        log.info("Fetching participant info from School21 for login: {}", login);
        try {
            ParticipantV1DTO participant = restClient.get()
                    .uri("/v1/participants/{login}", login)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                        log.error("Client error ({}): {} for login: {}", response.getStatusCode(), response.getStatusText(), login);
                        throw new RuntimeException("User not found in School21 or bad request"); // Бросим исключение позже
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                        log.error("School21 server error: {} for login: {}", response.getStatusText(), login);
                        throw new RuntimeException("School21 service unavailable");
                    })
                    .body(ParticipantV1DTO.class);

            log.info("Successfully fetched participant: {}", login);
            return participant;

        } catch (Exception e) {
            log.error("Failed to fetch participant {} from School21: {}", login, e.getMessage());
            // Здесь можно либо вернуть null, либо выбросить кастомное исключение
            return null;
        }
    }
}