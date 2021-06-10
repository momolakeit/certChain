package com.momo.certChain.services;

import com.momo.certChain.Utils.TestUtils;
import com.momo.certChain.exception.ObjectNotFoundException;
import com.momo.certChain.exception.ValidationException;
import com.momo.certChain.model.data.Admin;
import com.momo.certChain.model.data.Employee;
import com.momo.certChain.model.data.Student;
import com.momo.certChain.model.data.User;
import com.momo.certChain.model.dto.EmployeesDTO;
import com.momo.certChain.model.dto.InstitutionDTO;
import com.momo.certChain.model.dto.StudentDTO;
import com.momo.certChain.model.dto.UserDTO;
import com.momo.certChain.repositories.UserRepository;
import com.momo.certChain.services.request.HeaderCatcherService;
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

    @Mock
    private HeaderCatcherService headerCatcherService;

    @Test
    public void findUserByEmailTest() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(TestUtils.createStudent()));

        User user = userService.findUserByEmail("yioo@mail.com");

        TestUtils.assertBaseUser(user);

    }

    @Test
    public void getStudentUserTest() {
        Student student = TestUtils.createStudent();

        when(userRepository.findById(anyString())).thenReturn(Optional.of(student));

        Student returnValue = (Student) userService.getUser(student.getId());

        TestUtils.assertBaseUser(returnValue);
        TestUtils.assertInstitution(returnValue.getInstitution());
    }

    @Test
    public void getEmployeeUserTest() {
        Employee employee = TestUtils.createEmploye();

        when(userRepository.findById(anyString())).thenReturn(Optional.of(employee));

        Employee returnValue = (Employee) userService.getUser(employee.getId());

        TestUtils.assertBaseUser(returnValue);
        TestUtils.assertInstitution(returnValue.getInstitution());
    }

    @Test
    public void humanUserNotFound() {
        when(userRepository.findById(anyString())).thenReturn(Optional.empty());

        Assertions.assertThrows(ObjectNotFoundException.class, () -> {
            userService.getUser("123456");
        });
    }

    @Test
    public void getLoggedUser() {
        when(userRepository.findById(anyString())).thenReturn(Optional.of(TestUtils.createStudent()));
        when(headerCatcherService.getUserId()).thenReturn("123456");

        Student returnValue = (Student) userService.getLoggedUser();

        TestUtils.assertBaseUser(returnValue);
    }

    @Test
    public void findUserByEmailNotFoundTest() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        Assertions.assertThrows(ObjectNotFoundException.class, () -> {
            userService.findUserByEmail("yioo@mail.com");
        });

    }

    @Test
    public void createUserTest() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        User user = userService.createUser(TestUtils.createStudent());

        TestUtils.assertBaseUser(user);
    }

    @Test
    public void createUserWithEmailAlreadyExistsTest() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(TestUtils.createStudent()));

        Assertions.assertThrows(ValidationException.class, () -> {
            userService.createUser(TestUtils.createStudent());
        });
    }

    @Test
    public void convertInstitutionToDto() {
        UserDTO userDTO = userService.toDto(TestUtils.createInstitution());

        assertTrue(userDTO instanceof InstitutionDTO);
    }

    @Test
    public void convertStudentToDto() {
        UserDTO userDTO = userService.toDto(TestUtils.createStudent());

        assertTrue(userDTO instanceof StudentDTO);
    }

    @Test
    public void convertEmployeeToDto() {
        UserDTO userDTO = userService.toDto(TestUtils.createEmploye());

        assertTrue(userDTO instanceof EmployeesDTO);
    }

    @Test
    public void convertAdminToDtoThrowsException() {
        Assertions.assertThrows(IllegalArgumentException.class,()->{
            userService.toDto(new Admin());
        });
    }
}