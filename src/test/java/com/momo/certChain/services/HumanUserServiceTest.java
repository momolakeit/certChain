package com.momo.certChain.services;

import com.momo.certChain.Utils.TestUtils;
import com.momo.certChain.exception.BadPasswordException;
import com.momo.certChain.exception.CustomMessagingException;
import com.momo.certChain.exception.PasswordNotMatchingException;
import com.momo.certChain.model.data.*;
import com.momo.certChain.model.dto.EmployeesDTO;
import com.momo.certChain.model.dto.StudentDTO;
import com.momo.certChain.repositories.HumanUserRepository;
import com.momo.certChain.services.messaging.MessageServiceImpl;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.mail.MessagingException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Collections;

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
    private UserService userService;

    @Mock
    private MessageServiceImpl messageServiceImpl;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private LienService lienService;

    @Captor
    private ArgumentCaptor<String> passwordCaptor;

    @Captor
    private ArgumentCaptor<String> lienEncKeyCaptor;

    MockedStatic<RandomStringUtils> randomStringUtilsMockedStatic;

    @BeforeEach
    public void init() {
        randomStringUtilsMockedStatic = mockStatic(RandomStringUtils.class);
    }

    @AfterEach
    public void destroy() {
        randomStringUtilsMockedStatic.closeOnDemand();
    }

    @Test
    public void createStudentUserTest() throws MessagingException, IOException {
        Student student = TestUtils.createStudent();
        String password = "password";

        randomStringUtilsMockedStatic.when(() -> RandomStringUtils.randomAlphanumeric(11)).thenReturn(password);
        when(userService.createUser(any(User.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        when(passwordEncoder.encode(anyString())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        Student returnValue = (Student) humanUserService.createHumanUser(student);

        verify(messageServiceImpl, times(1)).sendUserCreatedEmail(any(HumanUser.class), passwordCaptor.capture());

        assertEquals(password, passwordCaptor.getValue());
        assertFalse(returnValue.isPasswordResseted());
        TestUtils.assertBaseUser(returnValue);
        TestUtils.assertInstitution(returnValue.getInstitution());
    }

    @Test
    public void createEmployeeUserTest() throws MessagingException, IOException {
        Employee employe = TestUtils.createEmploye();
        String password = "password";

        randomStringUtilsMockedStatic.when(() -> RandomStringUtils.randomAlphanumeric(11)).thenReturn(password);
        when(userService.createUser(any(User.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        when(passwordEncoder.encode(anyString())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));


        Employee returnValue = (Employee) humanUserService.createHumanUser(employe);
        verify(messageServiceImpl, times(1)).sendUserCreatedEmail(any(HumanUser.class), passwordCaptor.capture());

        assertEquals(password, passwordCaptor.getValue());
        assertFalse(returnValue.isPasswordResseted());
        TestUtils.assertBaseUser(returnValue);
        TestUtils.assertInstitution(returnValue.getInstitution());
    }

    @Test
    public void createStudentUserThrowMessagingExceptionTest() throws MessagingException, IOException {
        Student student = TestUtils.createStudent();
        String password = "password";

        randomStringUtilsMockedStatic.when(() -> RandomStringUtils.randomAlphanumeric(11)).thenReturn(password);
        when(passwordEncoder.encode(anyString())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        doThrow(new MessagingException()).when(messageServiceImpl).sendUserCreatedEmail(any(HumanUser.class), anyString());

        Assertions.assertThrows(CustomMessagingException.class, () -> {
            humanUserService.createHumanUser(student);
        });
    }

    @Test
    public void createStudentUserThrowIOExceptionTest() throws MessagingException, IOException {
        Student student = TestUtils.createStudent();
        String password = "password";

        randomStringUtilsMockedStatic.when(() -> RandomStringUtils.randomAlphanumeric(11)).thenReturn(password);
        when(passwordEncoder.encode(anyString())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        doThrow(new IOException()).when(messageServiceImpl).sendUserCreatedEmail(any(HumanUser.class), anyString());

        Assertions.assertThrows(CustomMessagingException.class, () -> {
            humanUserService.createHumanUser(student);
        });
    }

    @Test
    public void setUpPasswordTest() throws ParseException {
        String encodedString = "encoded";
        String password = "salut";
        String passwordConfirmation = "salut";
        String oldPassword = "password";
        String id = "123456";
        String certEncKey = "certKey";
        Student student = TestUtils.createStudent();
        student.setCertifications(Collections.singletonList(TestUtils.createCertification()));

        when(humanUserRepository.save(any(HumanUser.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        when(userService.getUser(anyString())).thenReturn(student);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn(encodedString);

        HumanUser user = humanUserService.modifyPassword(id, oldPassword, password, passwordConfirmation, certEncKey);

        verify(lienService,times(1)).createLienAccesPourPropriataireCertificat(passwordCaptor.capture(),lienEncKeyCaptor.capture(),any(Certification.class));

        assertEquals(encodedString, user.getPassword());
        assertEquals(password,passwordCaptor.getValue());
        assertEquals(certEncKey,lienEncKeyCaptor.getValue());
    }

    @Test
    public void setUpPasswordNotMatchingThrowsExeptionTest() {
        Student student = TestUtils.createStudent();

        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(userService.getUser(anyString())).thenReturn(student);

        Assertions.assertThrows(PasswordNotMatchingException.class, () -> {
            humanUserService.modifyPassword("123456", "password", "salut", "mec", "certKey");
        });
    }

    @Test
    public void setBadOldPasswordThrowsExeptionTest() {
        Student student = TestUtils.createStudent();

        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        when(userService.getUser(anyString())).thenReturn(student);

        Assertions.assertThrows(BadPasswordException.class, () -> {
            humanUserService.modifyPassword("123456", "password", "salut", "mec", "certKey");
        });
    }

    @Test
    public void studentToDTO() {
        Student student = TestUtils.createStudent();

        StudentDTO studentDTO = (StudentDTO) humanUserService.toDTO(student);
        assertEquals(studentDTO.getNom(), student.getNom());
        assertEquals(studentDTO.getPrenom(), student.getPrenom());
        assertEquals(studentDTO.getUsername(), student.getUsername());
        assertEquals(studentDTO.getId(), student.getId());
        assertEquals(studentDTO.getPrenom(), student.getPrenom());
    }

    @Test
    public void employeeToDTO() {
        Employee employee = TestUtils.createEmploye();

        EmployeesDTO employeeDTO = (EmployeesDTO) humanUserService.toDTO(employee);
        assertEquals(employeeDTO.getNom(), employee.getNom());
        assertEquals(employeeDTO.getPrenom(), employee.getPrenom());
        assertEquals(employeeDTO.getUsername(), employee.getUsername());
        assertEquals(employeeDTO.getId(), employee.getId());
        assertEquals(employeeDTO.getPrenom(), employee.getPrenom());
    }

}