package com.momo.certChain.services;

import com.momo.certChain.Utils.TestUtils;
import com.momo.certChain.exception.CannotAccessCertificateException;
import com.momo.certChain.exception.UserForgottenException;
import com.momo.certChain.exception.ValidationException;
import com.momo.certChain.model.CreatedLien;
import com.momo.certChain.model.data.Certification;
import com.momo.certChain.model.data.Lien;
import com.momo.certChain.model.data.Signature;
import com.momo.certChain.model.data.Student;
import com.momo.certChain.repositories.CertificationRepository;
import com.momo.certChain.services.blockChain.ContractServiceImpl;
import com.momo.certChain.services.request.HeaderCatcherService;
import com.momo.certChain.services.security.EncryptionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.web3j.crypto.ECKeyPair;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CertificationServiceTest {
    @InjectMocks
    private CertificationService certificationService;

    @Mock
    private CertificationRepository certificationRepository;

    @Mock
    private ImageFileService imageFileService;

    @Mock
    private SignatureService signatureService;

    @Mock
    private ContractServiceImpl contractServiceImpl;

    @Mock
    private EncryptionService encryptionService;

    @Mock
    private LienService lienService;

    @Mock
    private HeaderCatcherService headerCatcherService;

    @Mock
    private UserService userService;

    @Captor
    private ArgumentCaptor<Certification> certificationArgumentCaptor;

    @Captor
    private ArgumentCaptor<String> addressArgumentCaptor;

    @Captor
    private ArgumentCaptor<ECKeyPair> keyPairArgumentCaptor;

    @Captor
    private ArgumentCaptor<String> encryptionPrivateKeyCaptor;

    @Captor
    private ArgumentCaptor<String> saltArgumentCaptor;

    @Captor
    private ArgumentCaptor<String> userPasswordCaptor;

    @BeforeEach
    public void init() throws IOException {
        createListOfSignatures();
    }

    @Test
    public void createCertificationTemplate() throws IOException {
        Certification certification = TestUtils.createCertificationWithNoStudent();

        addSignatureToCertification(certification);
        List<Signature> signaturesList = createListOfSignatures();

        for (int i = 0; i < 3; i++) {
            when(signatureService.createSignature(certification.getSignatures().get(i).getAuthorName())).thenReturn(signaturesList.get(i));
        }
        when(certificationRepository.save(any(Certification.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        when(imageFileService.createImageFile(any(byte[].class))).thenReturn(TestUtils.createImageFile());

        Certification returnValueCertification = certificationService.createCertificationTemplate(certification, TestUtils.getExcelByteArray(), TestUtils.getExcelByteArray(), TestUtils.createInstitution());

        assertEquals(certification.getCertificateText(), returnValueCertification.getCertificateText());
        assertEquals(signaturesList.get(0).getAuthorName(), returnValueCertification.getSignatures().get(0).getAuthorName());
        assertEquals(signaturesList.get(1).getAuthorName(), returnValueCertification.getSignatures().get(1).getAuthorName());
        assertEquals(signaturesList.get(2).getAuthorName(), returnValueCertification.getSignatures().get(2).getAuthorName());
        assertNotNull(returnValueCertification.getUniversityStamp().getBytes());
        assertNotNull(returnValueCertification.getUniversityLogo().getBytes());
        TestUtils.assertInstitution(returnValueCertification.getInstitution());
    }

    @Test
    public void createCertificationTemplateLienNotNullThrowsException() throws IOException {
        Certification certification = TestUtils.createCertificationWithNoStudent();
        certification.setLiens(Collections.singletonList(TestUtils.createLien()));

        Assertions.assertThrows(ValidationException.class, () -> {
            certificationService.createCertificationTemplate(certification, TestUtils.getExcelByteArray(), TestUtils.getExcelByteArray(), TestUtils.createInstitution());
        });
    }

    @Test
    public void createCertificationTemplateStudentNotNullThrowsException() throws IOException {
        Certification certification = TestUtils.createCertification();

        Assertions.assertThrows(ValidationException.class, () -> {
            certificationService.createCertificationTemplate(certification, TestUtils.getExcelByteArray(), TestUtils.getExcelByteArray(), TestUtils.createInstitution());
        });
    }


    @Test
    public void uploadCertificateToBlockchain() throws Exception {
        String contractAddress = "address";
        String encryptionKey = "encryptionKey";

        Certification studentCertification = TestUtils.createCertification();
        Certification certificationTemplate = TestUtils.createCertificationTemplate();

        when(certificationRepository.save(any(Certification.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        certificationService.uploadCertificationToBlockChain(studentCertification, certificationTemplate, contractAddress, new ECKeyPair(BigInteger.ONE, BigInteger.TWO), encryptionKey);

        verify(contractServiceImpl).uploadCertificate(certificationArgumentCaptor.capture(), addressArgumentCaptor.capture(), keyPairArgumentCaptor.capture(), encryptionPrivateKeyCaptor.capture());

        Certification uploadedCertificate = certificationArgumentCaptor.getValue();
        String uploadedAddress = addressArgumentCaptor.getValue();
        ECKeyPair keyPair = keyPairArgumentCaptor.getValue();
        String encKey = encryptionPrivateKeyCaptor.getValue();


        assertEquals(certificationTemplate.getUniversityLogo().getId(), uploadedCertificate.getUniversityLogo().getId());
        assertNull(uploadedCertificate.getUniversityLogo().getBytes());
        assertEquals(certificationTemplate.getUniversityStamp().getId(), uploadedCertificate.getUniversityStamp().getId());
        assertNull(uploadedCertificate.getUniversityStamp().getBytes());
        assertEquals(encryptionKey, encKey);
        assertEquals(contractAddress, uploadedAddress);
        assertEquals(BigInteger.ONE, keyPair.getPrivateKey());
        assertEquals(BigInteger.TWO, keyPair.getPublicKey());
        for (Signature signature : uploadedCertificate.getSignatures()) {
            TestUtils.assertSignature(signature);
            assertNull(signature.getSignatureImage().getBytes());
        }

    }

    @Test
    public void testSaveAvecSaltCertification() {
        Certification certification = TestUtils.createCertification();

        when(certificationRepository.save(any(Certification.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        Certification returnValueCertification = certificationService.saveCertificationWithSalt(certification);

        verify(encryptionService, times(0)).generateSalt();

        TestUtils.assertCertification(returnValueCertification);
    }

    @Test
    public void testSaveSansSaltCertification() {
        String salt = "salt";
        Certification certification = TestUtils.createCertification();
        certification.setSalt(null);

        when(encryptionService.generateSalt()).thenReturn(salt);
        when(certificationRepository.save(any(Certification.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        Certification returnValueCertification = certificationService.saveCertificationWithSalt(certification);

        verify(encryptionService, times(1)).generateSalt();

        TestUtils.assertCertification(returnValueCertification);
        assertEquals(salt, returnValueCertification.getSalt());
    }

    @Test
    public void forgetCertificateTest() {
        Certification certification = TestUtils.createCertification();
        certification.setSalt("salty");
        String certId = "123456";

        when(userService.getUser(anyString())).thenReturn(createStudentWithCertification(certId));
        when(certificationRepository.findById(anyString())).thenReturn(Optional.of(certification));
        when(headerCatcherService.getUserId()).thenReturn(certification.getStudent().getId());

        certificationService.forgetCertificate(certId);

        verify(certificationRepository).save(certificationArgumentCaptor.capture());

        Certification returnValCertification = certificationArgumentCaptor.getValue();

        assertNotNull(returnValCertification);
        assertNull(returnValCertification.getSalt());
    }

    @Test
    public void forgetNotOwnerThrowsExceptionTest() {
        Certification certification = TestUtils.createCertification();
        certification.setSalt("salty");
        String certId = "otherId";

        when(userService.getUser(anyString())).thenReturn(createStudentWithCertification(certId));
        when(certificationRepository.findById(anyString())).thenReturn(Optional.of(certification));
        when(headerCatcherService.getUserId()).thenReturn(certification.getStudent().getId());

        Assertions.assertThrows(CannotAccessCertificateException.class, () -> {
            certificationService.forgetCertificate("123456");
        });

    }

    @Test
    public void payCertification() {
        Certification certification = TestUtils.createCertification();
        String certId = "123456";

        when(userService.getUser(anyString())).thenReturn(createStudentWithCertification(certId));
        when(certificationRepository.findById(anyString())).thenReturn(Optional.of(certification));
        when(headerCatcherService.getUserId()).thenReturn(certification.getStudent().getId());
        when(certificationRepository.save(any(Certification.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        Certification returnCertification = certificationService.payCertificate("123456");

        TestUtils.assertCertification(returnCertification);
        assertTrue(returnCertification.isPayed());
    }

    @Test
    public void payCertificationUserNotAllowed() {
        Certification certification = TestUtils.createCertification();
        String certId = "badId";

        when(userService.getUser(anyString())).thenReturn(createStudentWithCertification(certId));
        when(certificationRepository.findById(anyString())).thenReturn(Optional.of(certification));
        when(headerCatcherService.getUserId()).thenReturn(certification.getStudent().getId());

        Assertions.assertThrows(CannotAccessCertificateException.class, () -> {
            certificationService.payCertificate("123456");
        });

    }


    @Test
    public void getUploadedCertificateWithLien() throws Exception {
        String privateKey = "privateKey";
        String salt = "salt";

        Certification certification = TestUtils.createCertification();
        certification.setInstitution(TestUtils.createInstitution());

        Lien lien = TestUtils.createLien();
        lien.setCertificateEncKey(privateKey);

        when(lienService.getLien(anyString(), anyString())).thenReturn(lien);
        when(certificationRepository.findById(anyString())).thenReturn(Optional.of(certification));
        when(contractServiceImpl.getCertificate(anyString(),
                anyString(),
                any(ECKeyPair.class),
                anyString(),
                anyString()))
                .thenReturn(certification);

        certificationService.getUploadedCertificationWithLien("123456", privateKey, "123456");

        verify(contractServiceImpl).getCertificate(anyString(),
                addressArgumentCaptor.capture(),
                keyPairArgumentCaptor.capture(),
                encryptionPrivateKeyCaptor.capture(),
                saltArgumentCaptor.capture());

        assertEquals(certification.getInstitution().getContractAddress(), addressArgumentCaptor.getValue());
        assertNotNull(keyPairArgumentCaptor.getValue());
        assertEquals(privateKey, encryptionPrivateKeyCaptor.getValue());
        assertEquals(salt, saltArgumentCaptor.getValue());
    }

    @Test
    public void getUploadedCertificateWithPrivateKey() throws Exception {
        String privateKey = "privateKey";
        String salt = "salt";

        Certification certification = TestUtils.createCertification();
        certification.setInstitution(TestUtils.createInstitution());

        Lien lien = TestUtils.createLien();
        lien.setCertificateEncKey(privateKey);

        when(certificationRepository.findById(anyString())).thenReturn(Optional.of(certification));
        when(contractServiceImpl.getCertificate(anyString(),
                anyString(),
                any(ECKeyPair.class),
                anyString(),
                anyString()))
                .thenReturn(certification);

        certificationService.getUploadedCertificationWithPrivateKey("123456", privateKey);

        verify(contractServiceImpl).getCertificate(anyString(),
                addressArgumentCaptor.capture(),
                keyPairArgumentCaptor.capture(),
                encryptionPrivateKeyCaptor.capture(),
                saltArgumentCaptor.capture());

        assertEquals(certification.getInstitution().getContractAddress(), addressArgumentCaptor.getValue());
        assertNotNull(keyPairArgumentCaptor.getValue());
        assertEquals(privateKey, encryptionPrivateKeyCaptor.getValue());
        assertEquals(salt, saltArgumentCaptor.getValue());
    }

    @Test
    public void getUploadedCertificateNoSaltThrowsException() throws Exception {
        String privateKey = "privateKey";
        Certification certification = TestUtils.createCertification();
        certification.setSalt(null);
        certification.setInstitution(TestUtils.createInstitution());

        when(certificationRepository.findById(anyString())).thenReturn(Optional.of(certification));

        Assertions.assertThrows(UserForgottenException.class, () -> {
            certificationService.getUploadedCertificationWithLien("123456", privateKey, "123456");
        });
    }

    @Test
    public void createLienTest() throws Exception {
        String generatedLienPassword = "generatedPassword";

        when(certificationRepository.findById(anyString())).thenReturn(Optional.of(TestUtils.createCertification()));
        when(contractServiceImpl.getCertificate(anyString(), anyString(), any(ECKeyPair.class), anyString(), anyString())).thenReturn(TestUtils.createCertification());
        when(lienService.createLien(anyString(), any(Date.class), anyString(), any(Certification.class))).thenReturn(new CreatedLien(TestUtils.createLien(), generatedLienPassword));

        String generatedPasswordResponse = certificationService.createLien("123456", "password", TestUtils.createLien().getTitre(), new Date()).getGeneratedPassword();

        verify(certificationRepository).save(certificationArgumentCaptor.capture());

        assertEquals(generatedLienPassword, generatedPasswordResponse);
        TestUtils.assertLien(certificationArgumentCaptor.getValue().getLiens().get(0));
    }

    @Test
    public void createPropriaitaireLienTest() throws Exception {
        String certificateId = "123456";
        String userPassword = "password";
        String certEncKey = "certEncKey";

        when(certificationRepository.findById(anyString())).thenReturn(Optional.of(TestUtils.createCertification()));
        when(headerCatcherService.getUserId()).thenReturn("123456");
        when(userService.getUser(anyString())).thenReturn(createStudentWithCertification(certificateId));

        certificationService.createProprietaireLien(certificateId, userPassword, certEncKey);

        verify(lienService, times(1)).createLienAccesPourProprietaireCertificat(userPasswordCaptor.capture(), encryptionPrivateKeyCaptor.capture(), certificationArgumentCaptor.capture());

        TestUtils.assertCertification(certificationArgumentCaptor.getValue());
        assertEquals(userPassword, userPasswordCaptor.getValue());
        assertEquals(certEncKey, encryptionPrivateKeyCaptor.getValue());

    }

    @Test
    public void createPropriaitaireLienUserNotAllowedAtCertificateTest() {
        String certificateId = "123456";
        String userPassword = "password";
        String certEncKey = "certEncKey";

        when(certificationRepository.findById(anyString())).thenReturn(Optional.of(TestUtils.createCertification()));
        when(headerCatcherService.getUserId()).thenReturn("123456");
        when(userService.getUser(anyString())).thenReturn(createStudentWithCertification("654321"));

        Assertions.assertThrows(CannotAccessCertificateException.class,()->{
            certificationService.createProprietaireLien(certificateId, userPassword, certEncKey);
        });

    }


    private List<Signature> createListOfSignatures() throws IOException {
        List<Signature> signatureList = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            Signature signature = getSignatureWithModifiedAuthorName(i);
            signature.setId(String.valueOf(i));
            signatureList.add(signature);

        }
        return signatureList;
    }

    private void addSignatureToCertification(Certification certification) throws IOException {
        certification.setSignatures(new ArrayList<>());

        for (int i = 0; i < 3; i++) {
            Signature signature = getSignatureWithModifiedAuthorName(i);
            signature.setId(null);
            certification.getSignatures().add(signature);
        }
    }

    private Signature getSignatureWithModifiedAuthorName(int i) throws IOException {
        Signature signature = TestUtils.createSignature();
        signature.setAuthorName(signature.getAuthorName() + i + " wash");

        return signature;
    }

    private Student createStudentWithCertification(String certId) {
        Student student = TestUtils.createStudent();
        student.setCertifications(Collections.singletonList(createCertificationWithId(certId)));

        return student;
    }

    private Certification createCertificationWithId(String certId) {
        Certification certification = TestUtils.createCertification();
        certification.setId(certId);

        return certification;
    }

}