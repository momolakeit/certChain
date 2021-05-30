package com.momo.certChain.services;

import com.momo.certChain.Utils.TestUtils;
import com.momo.certChain.exception.ObjectNotFoundException;
import com.momo.certChain.exception.ValidationException;
import com.momo.certChain.model.data.User;
import com.momo.certChain.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    public UserService userService;

    @Mock
    private UserRepository userRepository;

    @Test
    public void findUserByEmailTest(){
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(TestUtils.createStudent()));

        User user = userService.findUserByEmail("yioo@mail.com");

        TestUtils.assertBaseUser(user);

    }

    @Test
    public void findUserByEmailNotFoundTest(){
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        Assertions.assertThrows(ObjectNotFoundException.class,()->{
            userService.findUserByEmail("yioo@mail.com");
        });

    }

    @Test
    public void createUserTest(){
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        User user = userService.createUser(TestUtils.createStudent());

        TestUtils.assertBaseUser(user);
    }

    @Test
    public void createUserWithEmailAlreadyExistsTest(){
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(TestUtils.createStudent()));

        Assertions.assertThrows(ValidationException.class,()->{
            userService.createUser(TestUtils.createStudent());
        });
    }
}