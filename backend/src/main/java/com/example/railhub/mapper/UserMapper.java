package com.example.railhub.mapper;

import com.example.railhub.dto.UserDTO;
import com.example.railhub.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserDTO userDTO);
    UserDTO toDTO(User user);
}
