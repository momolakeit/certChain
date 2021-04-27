package com.momo.certChain.services;

import com.momo.certChain.TestUtils;
import com.momo.certChain.exception.ObjectNotFoundException;
import com.momo.certChain.exception.PasswordNotMatchingException;
import com.momo.certChain.model.data.Employee;
import com.momo.certChain.model.data.HumanUser;
import com.momo.certChain.model.data.Student;
import com.momo.certChain.repositories.HumanUserRepository;
import com.momo.certChain.services.messaging.MessageService;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HumanUserServiceTest {

    @InjectMocks
    private HumanUserService humanUserService;

    @Mock
    private HumanUserRepository humanUserRepository;

    @Mock
    private MessageService messageService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    public void createStudentUserTest() {
        when(humanUserRepository.save(any(HumanUser.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        Student student = TestUtils.createStudent();
        Student returnValue = (Student) humanUserService.createHumanUser(student);
        verify(messageService, times(1)).sendEmail(any(HumanUser.class));
        TestUtils.assertBaseUser(returnValue);
        TestUtils.assertInstitution(returnValue.getInstitution());
    }

    @Test
    public void createEmployeeUserTest() {
        when(humanUserRepository.save(any(HumanUser.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        Employee employe = TestUtils.createEmploye();
        Employee returnValue = (Employee) humanUserService.createHumanUser(employe);
        verify(messageService, times(1)).sendEmail(any(HumanUser.class));
        TestUtils.assertBaseUser(returnValue);
        TestUtils.assertInstitution(returnValue.getInstitution());
    }

    @Test
    public void setUpPasswordTest() {
        String encodedString = "encoded";
        Student student = TestUtils.createStudent();
        when(humanUserRepository.save(any(HumanUser.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        when(humanUserRepository.findById(anyString())).thenReturn(Optional.of(student));
        when(passwordEncoder.encode(anyString())).thenReturn(encodedString);
        HumanUser user = humanUserService.setUpPassword("123456", "salut", "salut");
        assertEquals(encodedString, user.getPassword());
    }

    @Test
    public void setUpPasswordNotMatchingThrowsExeptionTest() {
        Student student = TestUtils.createStudent();
        when(humanUserRepository.findById(anyString())).thenReturn(Optional.of(student));
        Assertions.assertThrows(PasswordNotMatchingException.class, () -> {
            humanUserService.setUpPassword("123456", "salut", "mec");
        });
    }

    @Test
    public void getStudentUserTest(){
        Student student = TestUtils.createStudent();
        when(humanUserRepository.findById(anyString())).thenReturn(Optional.of(student));
        Student returnValue = (Student) humanUserService.getUser(student.getId());
        TestUtils.assertBaseUser(returnValue);
        TestUtils.assertInstitution(returnValue.getInstitution());
    }

    @Test
    public void getEmployeeUserTest(){
        Employee employee= TestUtils.createEmploye();
        when(humanUserRepository.findById(anyString())).thenReturn(Optional.of(employee));
        Employee returnValue = (Employee) humanUserService.getUser(employee.getId());
        TestUtils.assertBaseUser(returnValue);
        TestUtils.assertInstitution(returnValue.getInstitution());
    }
    @Test
    public void humanUserNotFound(){
        when(humanUserRepository.findById(anyString())).thenReturn(Optional.empty());
        Assertions.assertThrows(ObjectNotFoundException.class,()->{
           humanUserService.getUser("123456");
        });
    }


}