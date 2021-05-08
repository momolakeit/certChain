package com.momo.certChain.services;

import com.momo.certChain.TestUtils;
import com.momo.certChain.model.data.*;
import com.momo.certChain.repositories.CampagneRepository;
import com.momo.certChain.services.security.EncryptionService;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.web3j.crypto.ECKeyPair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
    private EncryptionService encryptionService;

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


    @Test
    public void uploadCertification() throws Exception {
        int nbDeStudents = 100;
        String randomString = "random";
        String campagneName = "Genie informatique concordia";
        List<HumanUser> listeOfStudents = initStudentsList(nbDeStudents);
        Institution institution = TestUtils.createInstitutionWithWallet();
        institution.setCertificationTemplate(TestUtils.createCertificationTemplate());
        institution.getInstitutionWallet().setSalt("salt");
        MockedStatic<RandomStringUtils> randomStringUtilsMockedStatic = mockStatic(RandomStringUtils.class);


        when(encryptionService.decryptData(anyString(), anyString(), anyString())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(1));
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

        for(HumanUser humanUser: campagne.getStudentList()){
           Student student = (Student)humanUser;
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