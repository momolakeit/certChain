package com.momo.certChain.services;

import com.momo.certChain.TestUtils;
import com.momo.certChain.exception.CannotDeleteCertificateException;
import com.momo.certChain.exception.UserForgottenException;
import com.momo.certChain.model.data.Certification;
import com.momo.certChain.model.data.ImageFile;
import com.momo.certChain.model.data.Signature;
import com.momo.certChain.repositories.CertificationRepository;
import com.momo.certChain.services.blockChain.ContractServiceImpl;
import com.momo.certChain.services.request.HeaderCatcherService;
import com.momo.certChain.services.security.EncryptionService;
import org.checkerframework.checker.nullness.Opt;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private HeaderCatcherService headerCatcherService;

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


    @BeforeEach
    public void init() throws IOException {
        createListOfSignatures();
    }

    @Test
    public void createCertificationTemplate() throws IOException {
        Certification certification = TestUtils.createCertification();
        certification.setSignatures(new ArrayList<>());
        addSignatureToCertification(certification);
        List<Signature> signaturesList = createListOfSignatures();

        for (int i = 0; i < 3; i++) {
            when(signatureService.createSignature(certification.getSignatures().get(i).getAuthorName())).thenReturn(signaturesList.get(i));
        }
        when(certificationRepository.save(any(Certification.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        when(imageFileService.createImageFile(any(byte[].class))).thenReturn(TestUtils.createImageFile());

        Certification returnValueCertification = certificationService.createCertificationTemplate(certification,TestUtils.getExcelByteArray(),TestUtils.getExcelByteArray(),TestUtils.createInstitution());

        assertEquals(certification.getCertificateText(), returnValueCertification.getCertificateText());
        assertEquals(signaturesList.get(0).getAuthorName(), returnValueCertification.getSignatures().get(0).getAuthorName());
        assertEquals(signaturesList.get(1).getAuthorName(), returnValueCertification.getSignatures().get(1).getAuthorName());
        assertEquals(signaturesList.get(2).getAuthorName(), returnValueCertification.getSignatures().get(2).getAuthorName());
        assertNotNull(returnValueCertification.getUniversityStamp().getBytes());
        assertNotNull(returnValueCertification.getUniversityLogo().getBytes());
        TestUtils.assertInstitution(returnValueCertification.getInstitution());
    }

    @Test
    public void uploadCertificateToBlockchain() throws Exception {
        String contractAddress = "address";
        String encryptionKey = "encryptionKey";

        Certification studentCertification = TestUtils.createCertification();
        Certification certificationTemplate = TestUtils.createCertificationTemplate();

        when(certificationRepository.save(any(Certification.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        certificationService.uploadCertificationToBlockChain(studentCertification, certificationTemplate, contractAddress, new ECKeyPair(BigInteger.ONE,BigInteger.TWO),encryptionKey);

        verify(contractServiceImpl).uploadCertificate(certificationArgumentCaptor.capture(), addressArgumentCaptor.capture(), keyPairArgumentCaptor.capture(), encryptionPrivateKeyCaptor.capture());

        Certification uploadedCertificate = certificationArgumentCaptor.getValue();
        String uploadedAddress = addressArgumentCaptor.getValue();
        ECKeyPair keyPair = keyPairArgumentCaptor.getValue();
        String encKey = encryptionPrivateKeyCaptor.getValue();


        assertEquals(certificationTemplate.getUniversityLogo().getId(),uploadedCertificate.getUniversityLogo().getId());
        assertNull(uploadedCertificate.getUniversityLogo().getBytes());
        assertEquals(certificationTemplate.getUniversityStamp().getId(),uploadedCertificate.getUniversityStamp().getId());
        assertNull(uploadedCertificate.getUniversityStamp().getBytes());
        assertEquals(encryptionKey,encKey);
        assertEquals(contractAddress,uploadedAddress);
        assertEquals(BigInteger.ONE,keyPair.getPrivateKey());
        assertEquals(BigInteger.TWO,keyPair.getPublicKey());
        for(Signature signature: uploadedCertificate.getSignatures()){
            TestUtils.assertSignature(signature);
            assertNull(signature.getSignatureImage().getBytes());
        }

    }

    @Test
    public void testSaveAvecSaltCertification(){
        Certification certification = TestUtils.createCertification();

        when(certificationRepository.save(any(Certification.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        Certification returnValueCertification = certificationService.saveCertification(certification);

        verify(encryptionService,times(0)).generateSalt();

        TestUtils.assertCertification(returnValueCertification);
    }

    @Test
    public void testSaveSansSaltCertification(){
        String salt = "salt";
        Certification certification = TestUtils.createCertification();
        certification.setSalt(null);

        when(encryptionService.generateSalt()).thenReturn(salt);
        when(certificationRepository.save(any(Certification.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        Certification returnValueCertification = certificationService.saveCertification(certification);

        verify(encryptionService,times(1)).generateSalt();

        TestUtils.assertCertification(returnValueCertification);
        assertEquals(salt,returnValueCertification.getSalt());
    }

    @Test
    public void forgetCertificateTest(){
        Certification certification =  TestUtils.createCertification();
        certification.setSalt("salty");

        when(certificationRepository.findById(anyString())).thenReturn(Optional.of(certification));
        when(headerCatcherService.getUserId()).thenReturn(certification.getStudent().getId());

        certificationService.forgetCertificate("123456");

        verify(certificationRepository).save(certificationArgumentCaptor.capture());

        Certification returnValCertification = certificationArgumentCaptor.getValue();

        assertNotNull(returnValCertification);
        assertNull(returnValCertification.getSalt());
    }

    @Test
    public void forgetNotOwnerThrowsExceptionTest(){
        Certification certification =  TestUtils.createCertification();
        certification.setSalt("salty");

        when(certificationRepository.findById(anyString())).thenReturn(Optional.of(certification));
        when(headerCatcherService.getUserId()).thenReturn("badId");

        Assertions.assertThrows(CannotDeleteCertificateException.class,()->{
            certificationService.forgetCertificate("123456");
        });

    }


    @Test
    public void getUploadedCertificate() throws Exception {
        String privateKey = "privateKey";
        String salt = "salt";
        Certification certification =  TestUtils.createCertification();
        certification.setInstitution(TestUtils.createInstitution());

        when(certificationRepository.findById(anyString())).thenReturn(Optional.of(certification));
        when(contractServiceImpl.getCertificate(anyString(),
                                                anyString(),
                                                any(ECKeyPair.class),
                                                anyString(),
                                                anyString()))
                                                .thenReturn(certification);

        certificationService.getUploadedCertification("123456",privateKey);

        verify(contractServiceImpl).getCertificate(anyString(),
                                                   addressArgumentCaptor.capture(),
                                                   keyPairArgumentCaptor.capture(),
                                                   encryptionPrivateKeyCaptor.capture(),
                                                   saltArgumentCaptor.capture());

        assertEquals(certification.getInstitution().getContractAddress(),addressArgumentCaptor.getValue());
        assertNotNull(keyPairArgumentCaptor.getValue());
        assertEquals(privateKey,encryptionPrivateKeyCaptor.getValue());
        assertEquals(salt,saltArgumentCaptor.getValue());
    }

    @Test
    public void getUploadedCertificateNoSaltThrowsException() throws Exception {
        String privateKey = "privateKey";
        Certification certification =  TestUtils.createCertification();
        certification.setSalt(null);
        certification.setInstitution(TestUtils.createInstitution());

        when(certificationRepository.findById(anyString())).thenReturn(Optional.of(certification));

        Assertions.assertThrows(UserForgottenException.class,()->{
            certificationService.getUploadedCertification("123456",privateKey);
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
        for (int i = 0; i < 3; i++) {
            Signature signature = getSignatureWithModifiedAuthorName(i);
            signature.setAuthorName(signature.getAuthorName() + " wash");
            signature.setId(null);
            certification.getSignatures().add(signature);
        }
    }

    private Signature getSignatureWithModifiedAuthorName(int i) throws IOException {
        Signature signature = TestUtils.createSignature();
        signature.setAuthorName(signature.getAuthorName() + i);
        return signature;
    }
}