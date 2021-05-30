package com.momo.certChain.services.authentification;

import com.momo.certChain.Utils.TestUtils;
import com.momo.certChain.exception.BadPasswordException;
import com.momo.certChain.exception.ObjectNotFoundException;
import com.momo.certChain.jwt.JwtProvider;
import com.momo.certChain.model.data.User;
import com.momo.certChain.model.dto.response.JWTResponse;
import com.momo.certChain.services.UserService;
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
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @Test
    public void testLogInUser(){
        String token = "token";

        when(userService.findUserByEmail(anyString())).thenReturn(TestUtils.createStudent());
        when(passwordEncoder.matches(anyString(),anyString())).thenReturn(true);
        when(jwtProvider.generate(any(User.class))).thenReturn(token);

        JWTResponse jwtResponse = authService.logInUser("email","password");

        assertEquals(token,jwtResponse.getToken());
    }

    @Test
    public void testLogInUserPasswordNotMatchingThrowsException(){

        when(userService.findUserByEmail(anyString())).thenReturn(TestUtils.createStudent());
        when(passwordEncoder.matches(anyString(),anyString())).thenReturn(false);

        Assertions.assertThrows(BadPasswordException.class,()->{
            authService.logInUser("email","password");
        });

    }
}