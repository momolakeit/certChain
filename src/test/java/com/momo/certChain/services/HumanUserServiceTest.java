package com.momo.certChain.services;

import com.momo.certChain.TestUtils;
import com.momo.certChain.exception.ObjectNotFoundException;
import com.momo.certChain.exception.PasswordNotMatchingException;
import com.momo.certChain.model.data.Employee;
import com.momo.certChain.model.data.HumanUser;
import com.momo.certChain.model.data.Student;
import com.momo.certChain.repositories.HumanUserRepository;
import com.momo.certChain.services.messaging.MessageService;
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

    @Captor
    private ArgumentCaptor<String> encKeyPrivateKey;

    MockedStatic<RandomStringUtils> randomStringUtilsMockedStatic;

    @BeforeEach
    public void init(){
        randomStringUtilsMockedStatic = mockStatic(RandomStringUtils.class);
    }

    @AfterEach
    public void destroy(){
        randomStringUtilsMockedStatic.closeOnDemand();
    }

    @Test
    public void createStudentUserTest() throws MessagingException {
        String privateKey="superPrivate";
        Student student = TestUtils.createStudent();

        randomStringUtilsMockedStatic.when(() -> RandomStringUtils.randomAlphanumeric(10)).thenReturn("password");
        when(humanUserRepository.save(any(HumanUser.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        when(passwordEncoder.encode(anyString())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        Student returnValue = (Student) humanUserService.createHumanUser(student,privateKey);

        verify(messageService, times(1)).sendEmailToHumanUser(any(HumanUser.class),encKeyPrivateKey.capture());

        assertEquals(privateKey,encKeyPrivateKey.getValue());
        assertFalse(returnValue.isPasswordResseted());
        TestUtils.assertBaseUser(returnValue);
        TestUtils.assertInstitution(returnValue.getInstitution());
    }

    @Test
    public void createEmployeeUserTest() throws MessagingException {
        Employee employe = TestUtils.createEmploye();
        String privateKey="superPrivate";

        randomStringUtilsMockedStatic.when(() -> RandomStringUtils.randomAlphanumeric(10)).thenReturn("password");
        when(humanUserRepository.save(any(HumanUser.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        when(passwordEncoder.encode(anyString())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));


        Employee returnValue = (Employee) humanUserService.createHumanUser(employe,privateKey);
        verify(messageService, times(1)).sendEmailToHumanUser(any(HumanUser.class),encKeyPrivateKey.capture());

        assertEquals(privateKey,encKeyPrivateKey.getValue());
        assertFalse(returnValue.isPasswordResseted());
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