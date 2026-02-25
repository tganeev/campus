package com.campus.backend.mapper;

import com.campus.backend.dto.UserCreateDTO;
import com.campus.backend.dto.UserDTO;
import com.campus.backend.model.Role;
import com.campus.backend.model.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-24T19:53:41+0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.6 (Oracle Corporation)"
)
@Component
public class UserMapperImpl extends UserMapper {

    @Override
    public User toEntity(UserCreateDTO dto) {
        if ( dto == null ) {
            return null;
        }

        User user = new User();

        user.setName( dto.getName() );
        user.setTelegramNick( dto.getTelegramNick() );
        user.setEmail( dto.getEmail() );

        user.setRole( Role.USER );

        return user;
    }

    @Override
    public UserDTO toDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserDTO.UserDTOBuilder userDTO = UserDTO.builder();

        userDTO.id( user.getId() );
        userDTO.name( user.getName() );
        userDTO.email( user.getEmail() );
        userDTO.telegramNick( user.getTelegramNick() );
        userDTO.avatarUrl( user.getAvatarUrl() );
        userDTO.role( user.getRole() );

        userDTO.clubs( mapClubs(user) );
        userDTO.presenceStatus( getUserStatus(user) );

        return userDTO.build();
    }
}
