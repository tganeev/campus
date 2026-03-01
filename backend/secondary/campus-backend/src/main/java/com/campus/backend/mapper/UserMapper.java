package com.campus.backend.mapper;

import com.campus.backend.dto.UserCreateDTO;
import com.campus.backend.dto.UserDTO;
import com.campus.backend.model.User;
import com.campus.backend.repository.PresenceRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
@Slf4j
public abstract class UserMapper {

    @Autowired
    protected PresenceRepository presenceRepository;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", constant = "USER")
    @Mapping(target = "clubs", ignore = true)
    @Mapping(target = "presenceHistory", ignore = true)
    @Mapping(target = "school21Login", source = "school21Login")  // Добавляем маппинг
    public abstract User toEntity(UserCreateDTO dto);

    @Mapping(target = "clubs", expression = "java(mapClubs(user))")
    @Mapping(target = "presenceStatus", expression = "java(getUserStatus(user))")
    public abstract UserDTO toDto(User user);

    protected List<String> mapClubs(User user) {
        if (user.getClubs() == null) {
            log.debug("User {} has no clubs", user.getId());
            return List.of();
        }
        log.debug("User {} has {} clubs", user.getId(), user.getClubs().size());
        return user.getClubs().stream()
                .map(club -> {
                    log.debug("  - Club: {} (id: {})", club.getName(), club.getId());
                    return club.getName();
                })
                .collect(Collectors.toList());
    }

    protected String getUserStatus(User user) {
        return presenceRepository.findByUser(user)
                .map(presence -> presence.getStatus().name())
                .orElse("OFFLINE");
    }
}