package com.momo.certChain.services;

import com.momo.certChain.TestUtils;
import com.momo.certChain.exception.ObjectNotFoundException;
import com.momo.certChain.mapping.CertificationMapper;
import com.momo.certChain.model.data.Certification;
import com.momo.certChain.model.data.ImageFile;
import com.momo.certChain.model.data.Signature;
import com.momo.certChain.repositories.CertificationRepository;
import com.momo.certChain.services.blockChain.ContractService;
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
    private ContractService contractService;

    @Mock
    private EncryptionService encryptionService;

    @Captor
    private ArgumentCaptor<Certification> certificationArgumentCaptor;

    @Captor
    private ArgumentCaptor<String> addressArgumentCaptor;

    @Captor
    private ArgumentCaptor<ECKeyPair> keyPairArgumentCaptor;


    private String authorName = "John Doe";

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
        Certification returnValueCertification = certificationService.createCertificationTemplate(CertificationMapper.instance.toDTO(certification));

        assertEquals(certification.getCertificateText(), returnValueCertification.getCertificateText());
        assertEquals(signaturesList.get(0).getAuthorName(), returnValueCertification.getSignatures().get(0).getAuthorName());
        assertEquals(signaturesList.get(1).getAuthorName(), returnValueCertification.getSignatures().get(1).getAuthorName());
        assertEquals(signaturesList.get(2).getAuthorName(), returnValueCertification.getSignatures().get(2).getAuthorName());
    }

    @Test
    public void addCertificateUniversityLogo() throws IOException {
        ImageFile imageFile = TestUtils.createImageFile();
        initAddImageFilesMocks(imageFile);

        Certification returnValueCertification = certificationService.addCertificationUniversityLogo("123456", TestUtils.getExcelByteArray());
        TestUtils.assertCertification(returnValueCertification);
        assertEquals(imageFile.getBytes(), returnValueCertification.getUniversityLogo().getBytes());
    }

    @Test
    public void addCertificateUniversityStamp() throws IOException {
        ImageFile imageFile = TestUtils.createImageFile();
        initAddImageFilesMocks(imageFile);

        Certification returnValueCertification = certificationService.addCertificationUniversityStamp("123456", TestUtils.getExcelByteArray());
        TestUtils.assertCertification(returnValueCertification);
        assertEquals(imageFile.getBytes(), returnValueCertification.getUniversityStamp().getBytes());
    }

    @Test
    public void addCertificateUniversityStampCertificateNotFound() throws IOException {
        when(certificationRepository.findById(anyString())).thenReturn(Optional.empty());
        Assertions.assertThrows(ObjectNotFoundException.class, () -> {
            certificationService.addCertificationUniversityStamp("123456", TestUtils.getExcelByteArray());
        });
    }

    @Test
    public void uploadCertificateToBlockchain() throws Exception {
        String contractAddress = "address";
        when(certificationRepository.save(any(Certification.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        Certification studentCertification = TestUtils.createCertification();
        Certification certificationTemplate = TestUtils.createCertificationTemplate();


        certificationService.uploadCertificationToBlockChain(studentCertification, certificationTemplate, contractAddress, new ECKeyPair(BigInteger.ONE,BigInteger.TWO));

        verify(contractService).uploadCertificate(certificationArgumentCaptor.capture(), addressArgumentCaptor.capture(), keyPairArgumentCaptor.capture());
        Certification uploadedCertificate = certificationArgumentCaptor.getValue();
        String uploadedAddress = addressArgumentCaptor.getValue();
        ECKeyPair keyPair = keyPairArgumentCaptor.getValue();


        assertEquals(certificationTemplate.getUniversityLogo().getId(),uploadedCertificate.getUniversityLogo().getId());
        assertNull(uploadedCertificate.getUniversityLogo().getBytes());
        assertEquals(certificationTemplate.getUniversityStamp().getId(),uploadedCertificate.getUniversityStamp().getId());
        assertNull(uploadedCertificate.getUniversityStamp().getBytes());
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


    private void initAddImageFilesMocks(ImageFile imageFile) {
        when(certificationRepository.findById(anyString())).thenReturn(Optional.of(TestUtils.createCertification()));
        when(certificationRepository.save(any(Certification.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        when(imageFileService.createImageFile(any(byte[].class))).thenReturn(imageFile);
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