package com.example.railhub.dto;

import com.example.railhub.entity.Role;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String login;
    private String password;
    private String email;
    private Role role;
}
