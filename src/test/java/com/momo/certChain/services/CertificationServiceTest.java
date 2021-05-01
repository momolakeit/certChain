package com.momo.certChain.services;

import com.momo.certChain.TestUtils;
import com.momo.certChain.exception.ObjectNotFoundException;
import com.momo.certChain.mapping.CertificationMapper;
import com.momo.certChain.model.data.Certification;
import com.momo.certChain.model.data.ImageFile;
import com.momo.certChain.model.data.Signature;
import com.momo.certChain.repositories.CertificationRepository;
import com.momo.certChain.services.blockChain.ContractService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Captor
    private ArgumentCaptor<Certification> certificationArgumentCaptor;

    @Captor
    private ArgumentCaptor<String> addressArgumentCaptor;

    @Captor
    private ArgumentCaptor<String> privateKeyArgumentCaptor;

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
        String privateKey = "privateKey";
        when(certificationRepository.save(any(Certification.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        Certification studentCertification = TestUtils.createCertification();
        Certification certificationTemplate = TestUtils.createCertificationTemplate();


        certificationService.uploadCertificationToBlockChain(studentCertification, certificationTemplate, contractAddress, privateKey);
        verify(contractService).uploadCertificate(certificationArgumentCaptor.capture(), addressArgumentCaptor.capture(), privateKeyArgumentCaptor.capture());
        Certification uploadedCertificate = certificationArgumentCaptor.getValue();
        String uploadedAddress = addressArgumentCaptor.getValue();
        String uploadedPrivateKey = privateKeyArgumentCaptor.getValue();


        assertEquals(certificationTemplate.getUniversityLogo().getId(),uploadedCertificate.getUniversityLogo().getId());
        assertNull(uploadedCertificate.getUniversityLogo().getBytes());
        assertEquals(certificationTemplate.getUniversityStamp().getId(),uploadedCertificate.getUniversityStamp().getId());
        assertNull(uploadedCertificate.getUniversityStamp().getBytes());
        assertEquals(contractAddress,uploadedAddress);
        assertEquals(privateKey,uploadedPrivateKey);

        for(Signature signature: uploadedCertificate.getSignatures()){
            TestUtils.assertSignature(signature);
            assertNull(signature.getSignatureImage().getBytes());
        }

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