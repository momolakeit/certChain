package com.momo.certChain.services.authentification;

import com.momo.certChain.TestUtils;
import com.momo.certChain.exception.BadPasswordException;
import com.momo.certChain.exception.ObjectNotFoundException;
import com.momo.certChain.jwt.JwtProvider;
import com.momo.certChain.model.data.User;
import com.momo.certChain.model.dto.JWTResponse;
import com.momo.certChain.model.dto.request.LogInDTO;
import com.momo.certChain.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @Test
    public void testLogInUser(){
        String token = "token";

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(TestUtils.createStudent()));
        when(passwordEncoder.matches(anyString(),anyString())).thenReturn(true);
        when(jwtProvider.generate(any(User.class))).thenReturn(token);

        JWTResponse jwtResponse = authService.logInUser(new LogInDTO("email","password"));

        assertEquals(token,jwtResponse.getToken());
    }

    @Test
    public void testLogInUserPasswordNotMatchingThrowsException(){

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(TestUtils.createStudent()));
        when(passwordEncoder.matches(anyString(),anyString())).thenReturn(false);

        Assertions.assertThrows(BadPasswordException.class,()->{
            authService.logInUser(new LogInDTO("email","password"));
        });

    }

    @Test
    public void testLogInUserNotFoundThrowsException(){

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        Assertions.assertThrows(ObjectNotFoundException.class,()->{
            authService.logInUser(new LogInDTO("email","password"));
        });

    }
}