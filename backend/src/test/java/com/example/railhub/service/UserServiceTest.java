package com.example.railhub.service;

import com.example.railhub.dto.UserDTO;
import com.example.railhub.entity.User;
import com.example.railhub.mapper.UserMapper;
import com.example.railhub.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.railhub.entity.Role;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.example.railhub.entity.Role;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void verifyLogin() {
    }

    @Test
    void should_registerUser() {
        // given
        UserDTO userToRegister = new UserDTO("JohnParadox", "passwd", "john@paradox.com", Role.ADMIN);
        User userEntity = new User();
        userEntity.setLogin("JohnParadox");
        userEntity.setPassword("passwd");
        userEntity.setRole(Role.ADMIN);

        when(userMapper.toEntity(userToRegister)).thenReturn(userEntity);
        when(passwordEncoder.encode("passwd")).thenReturn("encoded_passwd");

        // when
        userService.registerUser(userToRegister);

        // then
        verify(userRepository).save(userEntity);
    }

    @Test
    void shouldReturn200_whenLoginAndPasswordMatch() {
        // given
        UserDTO userDTO = new UserDTO("John", "passwd", null, null);
        User user = new User();
        user.setLogin("John");
        user.setPassword("encoded_passwd");

        when(userRepository.findByLogin("John")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), eq("encoded_passwd"))).thenReturn(true);
        when(passwordEncoder.encode("passwd")).thenReturn("encoded_passwd");

        // when
        int result = userService.verifyLogin(userDTO);

        // then
        assertEquals(200, result);
    }

    @Test
    void shouldReturn403_whenLoginExistsButPasswordDoesNotMatch() {
        // given
        UserDTO userDTO = new UserDTO("John", "wrongPassword", null, null);
        User user = new User();
        user.setLogin("John");
        user.setPassword("encoded_passwd");

        when(userRepository.findByLogin("John")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), eq("encoded_passwd"))).thenReturn(false);
        when(passwordEncoder.encode("wrongPassword")).thenReturn("encoded_passwd");

        // when
        int result = userService.verifyLogin(userDTO);

        // then
        assertEquals(403, result);
    }

    @Test
    void shouldReturn404_whenLoginDoesNotExist() {
        // given
        UserDTO userDTO = new UserDTO("unknown", "anyPassword", null, null);

        when(userRepository.findByLogin("unknown")).thenReturn(Optional.empty());

        // when
        int result = userService.verifyLogin(userDTO);

        // then
        assertEquals(404, result);
    }
}