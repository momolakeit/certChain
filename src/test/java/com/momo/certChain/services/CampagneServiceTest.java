package com.momo.certChain.services;

import com.momo.certChain.Utils.TestUtils;
import com.momo.certChain.exception.AuthorizationException;
import com.momo.certChain.exception.ObjectNotFoundException;
import com.momo.certChain.model.data.*;
import com.momo.certChain.repositories.CampagneRepository;
import com.momo.certChain.services.request.HeaderCatcherService;
import com.momo.certChain.services.security.KeyPairService;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.ECKeyPair;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.*;

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
    private HeaderCatcherService headerCatcherService;

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

    private final Long dateLong = 1575176400000L;


    @Test
    public void runCampagne() throws Exception {
        int nbDeStudents = 100;
        String randomString = "random";


        Institution institution = createInstitutionWithWallet();

        Campagne campagne = createCampagne(nbDeStudents, institution);

        when(keyPairService.createKeyPair(anyString(), anyString(), anyString(),anyString())).thenReturn(TestUtils.createKeyPair(institution.getInstitutionWallet().getPrivateKey(),
                                                                                                                                    institution.getInstitutionWallet().getPublicKey()));
        when(campagneRepository.findById(anyString())).thenReturn(Optional.of(campagne));
        when(headerCatcherService.getUserId()).thenReturn("123456");
        randomStringUtilsMockedStatic.when(() -> RandomStringUtils.randomAlphanumeric(11)).thenReturn(randomString);

        campagneService.runCampagne("123456","walletPassword");

        verify(certificationService, times(nbDeStudents)).uploadCertificationToBlockChain(studentCertificateArgumentCaptor.capture(),
                institutionTemplateCertificateArgumentCaptor.capture(),
                addressArgumentCaptor.capture(),
                KeyPairArgumentCaptor.capture(),
                encKeyPrivateKeyCaptor.capture());

        List<Certification> studentsCertifications = studentCertificateArgumentCaptor.getAllValues();
        List<Certification> institutionCertificationTemplates = institutionTemplateCertificateArgumentCaptor.getAllValues();
        List<String> encKeys = encKeyPrivateKeyCaptor.getAllValues();
        List<String> encKeySentByEmail = this.encKeySentByEmailCaptor.getAllValues();

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
    public void runCampagneNotAllowed() throws Exception {
        int nbDeStudents = 100;
        Institution institution = createInstitutionWithWallet();

        Campagne campagne = createCampagne(nbDeStudents, institution);

        when(campagneRepository.findById(anyString())).thenReturn(Optional.of(campagne));
        when(headerCatcherService.getUserId()).thenReturn("654321");

        Assertions.assertThrows(AuthorizationException.class,()->{
            campagneService.runCampagne("123456","walletPassword");
        });
    }

    @Test
    public void createCampagne(){
        int nbDeStudents = 100;
        List<HumanUser> listeOfStudents = initStudentsList(nbDeStudents);
        String campagneName = "Genie informatique concordia";

        when(campagneRepository.save(any(Campagne.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        when(userService.createHumanUser(any(HumanUser.class),anyString())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        Campagne campagne = campagneService.createCampagne(listeOfStudents,campagneName,TestUtils.createInstitution(), new Date(dateLong));

        assertEquals(campagneName,campagne.getName());
        assertEquals(nbDeStudents,campagne.getStudentList().size());
        assertEquals(new Date(dateLong),campagne.getDate());
        for(HumanUser humanUser: campagne.getStudentList()){
            Student student = (Student)humanUser;
            TestUtils.assertBaseUser(student);
            TestUtils.assertCertification(student.getCertifications().get(0));
        };
    }

    @Test
    public void getCampagne() throws IOException {
        when(campagneRepository.findById(anyString())).thenReturn(Optional.of(TestUtils.createCampagne()));

        Campagne returnCampagneValue = campagneService.getCampagne("123456");

        TestUtils.assertCampagne(returnCampagneValue);
    }

    @Test
    public void getCampagneNotFound() throws IOException {
        when(campagneRepository.findById(anyString())).thenReturn(Optional.empty());

        Assertions.assertThrows(ObjectNotFoundException.class,()->{
            campagneService.getCampagne("123456");
        });
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

    private Campagne createCampagne(int nbDeStudents, Institution institution) {
        Campagne campagne = TestUtils.createCampagne();
        campagne.setStudentList(initStudentsList(nbDeStudents));
        campagne.setInstitution(institution);
        return campagne;
    }

    private Institution createInstitutionWithWallet() throws NoSuchAlgorithmException, CipherException, InvalidAlgorithmParameterException, NoSuchProviderException, IOException {
        Institution institution = TestUtils.createInstitutionWithWallet();
        institution.setCertificationTemplate(TestUtils.createCertificationTemplate());
        institution.getInstitutionWallet().setSalt("salt");
        return institution;
    }
}