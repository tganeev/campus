package com.campus.backend.dto.school21;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ParticipantCampusV1DTO {
    private String id; // UUID кампуса
    private String shortName;
}