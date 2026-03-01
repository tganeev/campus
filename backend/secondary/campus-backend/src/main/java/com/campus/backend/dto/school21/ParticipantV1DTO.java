package com.campus.backend.dto.school21;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) // Игнорируем поля, которые нам не нужны
public class ParticipantV1DTO {
    private String login;
    private Integer level;
    private String status;
    private ParticipantCampusV1DTO campus;
    // Добавьте другие поля, если они нужны
}
