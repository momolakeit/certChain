package com.momo.certChain.services;

import com.momo.certChain.TestUtils;
import com.momo.certChain.model.data.*;
import com.momo.certChain.repositories.CampagneRepository;
import com.momo.certChain.services.security.KeyPairService;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.web3j.crypto.ECKeyPair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class CampagneServiceTest {

    @InjectMocks
    private CampagneService campagneService;

    @Mock
    private CampagneRepository campagneRepository;

    @Mock
    private HumanUserService userService;

    @Mock
    private KeyPairService keyPairService;

    @Mock
    private CertificationService certificationService;

    @Captor
    private ArgumentCaptor<Certification> studentCertificateArgumentCaptor;

    @Captor
    private ArgumentCaptor<Certification> institutionTemplateCertificateArgumentCaptor;

    @Captor
    private ArgumentCaptor<String> addressArgumentCaptor;

    @Captor
    private ArgumentCaptor<ECKeyPair> KeyPairArgumentCaptor;

    @Captor
    private ArgumentCaptor<String> encKeyPrivateKeyCaptor;

    @Captor
    private ArgumentCaptor<String> encKeySentByEmailCaptor;

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
    public void uploadCertification() throws Exception {
        int nbDeStudents = 100;
        String randomString = "random";
        String campagneName = "Genie informatique concordia";
        List<HumanUser> listeOfStudents = initStudentsList(nbDeStudents);
        Institution institution = TestUtils.createInstitutionWithWallet();
        institution.setCertificationTemplate(TestUtils.createCertificationTemplate());
        institution.getInstitutionWallet().setSalt("salt");

        when(keyPairService.createKeyPair(anyString(), anyString(), anyString(),anyString())).thenReturn(TestUtils.createKeyPair(institution.getInstitutionWallet().getPrivateKey(),
                                                                                                                                    institution.getInstitutionWallet().getPublicKey()));

        when(userService.createHumanUser(any(HumanUser.class), anyString())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        when(campagneRepository.save(any(Campagne.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        randomStringUtilsMockedStatic.when(() -> RandomStringUtils.randomAlphanumeric(10)).thenReturn(randomString);

        Campagne campagne = campagneService.runCampagne(campagneName,listeOfStudents,institution,"walletPassword");

        verify(certificationService, times(nbDeStudents)).uploadCertificationToBlockChain(studentCertificateArgumentCaptor.capture(),
                institutionTemplateCertificateArgumentCaptor.capture(),
                addressArgumentCaptor.capture(),
                KeyPairArgumentCaptor.capture(),
                encKeyPrivateKeyCaptor.capture());
        verify(userService, times(nbDeStudents)).createHumanUser(any(HumanUser.class), encKeySentByEmailCaptor.capture());

        List<Certification> studentsCertifications = studentCertificateArgumentCaptor.getAllValues();
        List<Certification> institutionCertificationTemplates = institutionTemplateCertificateArgumentCaptor.getAllValues();
        List<String> encKeys = encKeyPrivateKeyCaptor.getAllValues();
        List<String> encKeySentByEmail = this.encKeySentByEmailCaptor.getAllValues();

        assertEquals(nbDeStudents,campagne.getStudentList().size());
        assertEquals(campagneName,campagne.getName());
        TestUtils.assertInstitution(campagne.getInstitution());
        assertFalse(campagne.isRunned());

        for(HumanUser humanUser: campagne.getStudentList()){
           Student student = (Student)humanUser;
           TestUtils.assertBaseUser(student);
           TestUtils.assertCertification(student.getCertifications().get(0));
        }
        for (Certification cert : studentsCertifications) {
            TestUtils.assertCertification(cert);
        }
        for (Certification cert : institutionCertificationTemplates) {
            TestUtils.assertCertificationInstitution(cert);
        }
        for (String val : encKeys) {
            assertEquals(randomString, val);
        }
        for (String val : encKeySentByEmail) {
            assertEquals(randomString, val);
        }
    }

    @Test
    public void createCampagne(){
        int nbDeStudents = 100;
        List<HumanUser> listeOfStudents = initStudentsList(nbDeStudents);
        String campagneName = "campagne1";

        when(campagneRepository.save(any(Campagne.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        when(userService.createHumanUser(any(HumanUser.class),anyString())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        Campagne campagne = campagneService.createCampagne(listeOfStudents,campagneName,TestUtils.createInstitution());

        assertEquals(campagneName,campagne.getName());
        assertEquals(nbDeStudents,campagne.getStudentList().size());
        for(HumanUser humanUser: campagne.getStudentList()){
            Student student = (Student)humanUser;
            TestUtils.assertBaseUser(student);
            TestUtils.assertCertification(student.getCertifications().get(0));
        };
    }

    private List<HumanUser> initStudentsList(int nbDeStudents) {
        List<HumanUser> students = new ArrayList<>();
        for (int i = 0; i < nbDeStudents; i++) {
            Student singleStudent = TestUtils.createStudent();
            singleStudent.setCertifications(Collections.singletonList(TestUtils.createCertification()));
            students.add(singleStudent);
        }
        return students;
    }
}