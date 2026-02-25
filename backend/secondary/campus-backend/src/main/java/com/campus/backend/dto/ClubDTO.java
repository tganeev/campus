package com.campus.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClubDTO {

    private Long id;
    private String name;
    private String description;
    private Integer memberCount;
    private List<String> members;
}