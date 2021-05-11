package com.momo.certChain.controller;

import com.momo.certChain.TestUtils;
import com.momo.certChain.jwt.JwtProvider;
import com.momo.certChain.mapping.CertificationMapper;
import com.momo.certChain.model.data.*;
import com.momo.certChain.repositories.CertificationRepository;
import com.momo.certChain.repositories.UserRepository;
import com.momo.certChain.repositories.WalletRepository;
import com.momo.certChain.services.CertificationService;
import com.momo.certChain.services.InstitutionService;
import com.momo.certChain.services.messaging.MessageService;
import com.momo.certChain.services.security.EncryptionService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.web3j.crypto.ECKeyPair;

import java.io.IOException;
import java.math.BigInteger;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@Transactional
class CertificationControllerTest {
    @Autowired
    private CertificationController certificationController;

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private CertificationService certificationService;

    @Autowired
    private EncryptionService encryptionService;

    @Autowired
    private CertificationRepository certificationRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @MockBean
    private MessageService messageService;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    Certification studentCertification;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(certificationController).build();
    }

    @Test
    public void testGetCertification() throws Exception {
        uploadEncryptedCertificate();
        mockMvc.perform(MockMvcRequestBuilders.get("/certification/fetchCertificate/{id}/{key}", studentCertification.getId(), "walletPassword")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteCertfication() throws Exception {
        saveCertificationInBD();
        mockMvc.perform(MockMvcRequestBuilders.delete("/certification/forgetCertificate/{id}", studentCertification.getId())
                .header("Authorization",jwtProvider.generate(TestUtils.createStudent()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteCertificationNotFoundThrowException() throws Exception {
        saveCertificationInBD();
        mockMvc.perform(MockMvcRequestBuilders.delete("/certification/forgetCertificate/{id}", "5648979")
                .header("Authorization",jwtProvider.generate(TestUtils.createStudent()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteCertificationUserNotAllowedThrowException() throws Exception {
        saveCertificationInBD();
        Student student = TestUtils.createStudent();
        student.setId("789466");
        mockMvc.perform(MockMvcRequestBuilders.delete("/certification/forgetCertificate/{id}", studentCertification.getId())
                .header("Authorization",jwtProvider.generate(student))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }


    private void uploadEncryptedCertificate() throws Exception {
        String walletPassword = "walletPassword";
        Address address = TestUtils.createAddress();
        Institution institution = institutionService.createInstitution(address.getStreet(),
                address.getCity(),
                address.getProvince(),
                address.getPostalCode(),
                address.getCountry(),
                "name",
                walletPassword,
                "username",
                "password",
                "password");
        String privateKey = encryptionService.decryptData(walletPassword, institution.getInstitutionWallet().getPrivateKey(), institution.getInstitutionWallet().getSalt());
        String publicKey = encryptionService.decryptData(walletPassword, institution.getInstitutionWallet().getPublicKey(), institution.getInstitutionWallet().getSalt());
        ECKeyPair ecKeyPair = new ECKeyPair(new BigInteger(privateKey), new BigInteger(publicKey));

        institution = institutionService.uploadCertificateContract(institution.getId(), walletPassword);

        saveCertificationInBD();

        certificationService.uploadCertificationToBlockChain(studentCertification, initCertificationTemplate(institution), institution.getContractAddress(), ecKeyPair, "walletPassword");

    }

    private Certification initCertificationTemplate(Institution institution) throws IOException {
        Certification certificationTemplate = TestUtils.createCertificationTemplate();
        certificationTemplate.setId(null);
        certificationTemplate.setInstitution(institution);
        return certificationTemplate;
    }

    private void saveCertificationInBD() {
        studentCertification = TestUtils.createCertification();
        studentCertification.setId(null);
        studentCertification.setSalt(KeyGenerators.string().generateKey());
        studentCertification = certificationRepository.save(studentCertification);
    }
}