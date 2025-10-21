package com.example.railhub.service;

import com.example.railhub.dto.UserDTO;
import com.example.railhub.entity.User;
import com.example.railhub.mapper.UserMapper;
import com.example.railhub.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public int verifyLogin(UserDTO userDTO) {
        Optional<User> userOptional = userRepository.findByLogin(userDTO.getLogin());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(passwordEncoder.encode(userDTO.getPassword()), user.getPassword())) {
                return 200;
            }
            else {
                return 403;
            }
        }
        else {
            return 404;
        }
    }

    public void registerUser (UserDTO userDTO) {
        User user = userMapper.toEntity(userDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }
}
