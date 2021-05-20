package com.momo.certChain.services;

import com.momo.certChain.exception.PasswordNotMatchingException;
import com.momo.certChain.exception.ValidationException;
import com.momo.certChain.model.data.Admin;
import com.momo.certChain.repositories.AdminRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {
    @InjectMocks
    private AdminService adminService;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    public void createAdminTest(){
        when(adminRepository.findAll()).thenReturn(new ArrayList<>());
        when(adminRepository.save(any(Admin.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        when(passwordEncoder.encode(anyString())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        String email="email@mail.com";
        String password = "superSecret";
        String passwordConfirmation = "superSecret";

        Admin admin = adminService.createAdmin(email,password,passwordConfirmation);

        assertEquals(email,admin.getUsername());
        assertEquals(password,admin.getPassword());
    }

    @Test
    public void createAdminpasswordNotMatching(){
        when(adminRepository.findAll()).thenReturn(new ArrayList<>());
        String email="email@mail.com";
        String password = "superSecret";
        String passwordConfirmation = "notMatching";

        Assertions.assertThrows(PasswordNotMatchingException.class,()->{
            adminService.createAdmin(email,password,passwordConfirmation);
        });
    }

    @Test
    public void createAdminAlreadyExists(){
        when(adminRepository.findAll()).thenReturn(Collections.singletonList(new Admin()));
        String email="email@mail.com";
        String password = "superSecret";
        String passwordConfirmation = "notMatching";

        Assertions.assertThrows(ValidationException.class,()->{
            adminService.createAdmin(email,password,passwordConfirmation);
        });
    }
}