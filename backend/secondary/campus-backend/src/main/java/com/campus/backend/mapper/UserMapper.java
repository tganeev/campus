package com.campus.backend.mapper;

import com.campus.backend.dto.UserCreateDTO;
import com.campus.backend.dto.UserDTO;
import com.campus.backend.model.User;
import com.campus.backend.repository.PresenceRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class UserMapper {

    @Autowired
    protected PresenceRepository presenceRepository;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", constant = "USER")
    @Mapping(target = "clubs", ignore = true)
    @Mapping(target = "presenceHistory", ignore = true)
    public abstract User toEntity(UserCreateDTO dto);

    @Mapping(target = "clubs", expression = "java(mapClubs(user))")
    @Mapping(target = "presenceStatus", expression = "java(getUserStatus(user))")
    public abstract UserDTO toDto(User user);

    protected List<String> mapClubs(User user) {
        if (user.getClubs() == null) {
            return List.of();
        }
        return user.getClubs().stream()
                .map(club -> club.getName())
                .collect(Collectors.toList());
    }

    protected String getUserStatus(User user) {
        return presenceRepository.findByUser(user)
                .map(presence -> presence.getStatus().name())
                .orElse("OFFLINE");
    }
}