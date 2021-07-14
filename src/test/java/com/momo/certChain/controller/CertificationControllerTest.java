package com.momo.certChain.controller;

import com.momo.certChain.Utils.TestUtils;
import com.momo.certChain.jwt.JwtProvider;
import com.momo.certChain.model.Type;
import com.momo.certChain.model.data.*;
import com.momo.certChain.model.dto.request.CreateLienDTO;
import com.momo.certChain.model.dto.request.CreateProprietaireLienDTO;
import com.momo.certChain.repositories.LienRepository;
import com.momo.certChain.repositories.UserRepository;
import com.momo.certChain.services.CertificationService;
import com.momo.certChain.services.InstitutionService;
import com.momo.certChain.services.security.EncryptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
    private LienRepository lienRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtProvider jwtProvider;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    private Certification studentCertification;

    private final String encKey = "superSecure";

    private final String lienEncKey = "superSecureLien";

    private final String lienTitre = "Entrevue IBM";

    private final Long anneeEnMilliseconde = 31536000000L;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(certificationController).build();
    }

    @Test
    public void testGetCertificationWithLien() throws Exception {
        uploadEncryptedCertificate();
        mockMvc.perform(MockMvcRequestBuilders.get("/certification/fetchCertificate/{certificateId}/{lienId}/{key}", studentCertification.getId(), createLien(Type.UTILISATEUR_EXTERNE).getId(), lienEncKey)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    @Test
    public void testGetCertificationWithEncKey() throws Exception {
        uploadEncryptedCertificate();
        mockMvc.perform(MockMvcRequestBuilders.get("/certification/fetchCertificate/{certificateId}/{key}", studentCertification.getId(), encKey)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreatePropriaitaireLien() throws Exception {
        uploadEncryptedCertificate();
        Student student = createUserWithCertification();
        mockMvc.perform(MockMvcRequestBuilders.post("/certification/createProprietaireLien")
                .header("Authorization", jwtProvider.generate(student))
                .content(objectMapper.writeValueAsString(new CreateProprietaireLienDTO(student.getCertifications().get(0).getId(),"password",encKey)))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreatePropriaitaireUserAldreadyHasLien() throws Exception {
        uploadEncryptedCertificate();

        Student student = createUserWithCertification();
        List<Lien> lienList = new ArrayList<>();
        lienList.add(createLien(Type.PROPRIETAIRE_CERTIFICAT));

        student.getCertifications().get(0).setLiens(lienList);
        userRepository.save(student);

        mockMvc.perform(MockMvcRequestBuilders.post("/certification/createProprietaireLien")
                .header("Authorization", jwtProvider.generate(student))
                .content(objectMapper.writeValueAsString(new CreateProprietaireLienDTO(student.getCertifications().get(0).getId(),"password",encKey)))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testcreatePropriaitaireUserNotOwnerOfCertificateLien() throws Exception {
        uploadEncryptedCertificate();
        Student student = createUserWithCertification();
        mockMvc.perform(MockMvcRequestBuilders.post("/certification/createProprietaireLien")
                .header("Authorization", jwtProvider.generate(student))
                .content(objectMapper.writeValueAsString(new CreateProprietaireLienDTO("123456","password",encKey)))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @Test
    public void testGetCertificationWrongKey() throws Exception {
        uploadEncryptedCertificate();
        mockMvc.perform(MockMvcRequestBuilders.get("/certification/fetchCertificate/{certificateId}/{lienId}/{key}", studentCertification.getId(), createLien(Type.UTILISATEUR_EXTERNE).getId(), "encKey")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateCertificateLienExpirationDansLeFuture() throws Exception {
        uploadEncryptedCertificate();
        mockMvc.perform(MockMvcRequestBuilders.post("/certification/createLien")
                .content(objectMapper.writeValueAsString(new CreateLienDTO(studentCertification.getId(), encKey, lienTitre, new Date(System.currentTimeMillis() + anneeEnMilliseconde))))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateCertificateLienExpirationDansLePass4e() throws Exception {
        uploadEncryptedCertificate();
        mockMvc.perform(MockMvcRequestBuilders.post("/certification/createLien")
                .content(objectMapper.writeValueAsString(new CreateLienDTO(studentCertification.getId(), encKey, lienTitre, new Date(System.currentTimeMillis() - anneeEnMilliseconde))))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteCertfication() throws Exception {
        Student student = createUserWithCertification();
        mockMvc.perform(MockMvcRequestBuilders.delete("/certification/forgetCertificate/{id}", student.getCertifications().get(0).getId())
                .header("Authorization", jwtProvider.generate(student))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteCertificationNotFoundThrowException() throws Exception {
        Student student = createUserWithCertification();
        mockMvc.perform(MockMvcRequestBuilders.delete("/certification/forgetCertificate/{id}", "5648979")
                .header("Authorization", jwtProvider.generate(student))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testPayCertfication() throws Exception {
        Student student = createUserWithCertification();
        mockMvc.perform(MockMvcRequestBuilders.put("/certification/payCertificate/{id}", student.getCertifications().get(0).getId())
                .header("Authorization", jwtProvider.generate(student))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testPayCertficationStudentNotAllowed() throws Exception {
        Student student = createUserWithCertification();
        Student secondStudent = createUserWithCertification();
        mockMvc.perform(MockMvcRequestBuilders.put("/certification/payCertificate/{id}", student.getCertifications().get(0).getId())
                .header("Authorization", jwtProvider.generate(secondStudent))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }


    @Test
    public void testDeleteCertificationUserNotAllowedThrowException() throws Exception {
        Student student = createUserWithCertification();
        Student secondStudent = createUserWithCertification();
        mockMvc.perform(MockMvcRequestBuilders.delete("/certification/forgetCertificate/{id}", student.getCertifications().get(0).getId())
                .header("Authorization", jwtProvider.generate(secondStudent))
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
        institutionService.approveInstitution(institution.getId());
        String privateKey = encryptionService.decryptData(walletPassword, institution.getInstitutionWallet().getPrivateKey(), institution.getInstitutionWallet().getSalt());
        String publicKey = encryptionService.decryptData(walletPassword, institution.getInstitutionWallet().getPublicKey(), institution.getInstitutionWallet().getSalt());
        ECKeyPair ecKeyPair = new ECKeyPair(new BigInteger(privateKey), new BigInteger(publicKey));

        institution = institutionService.uploadCertificateContract(institution.getId(), walletPassword);

        createCertification();

        certificationService.uploadCertificationToBlockChain(studentCertification, initCertificationTemplate(institution), institution.getContractAddress(), ecKeyPair, encKey);

    }

    private Lien createLien(Type type) {
        Lien lien = new Lien();
        lien.setSalt(encryptionService.generateSalt());
        lien.setCertificateEncKey(encryptionService.encryptData(lienEncKey, encKey, lien.getSalt()));
        lien.setType(type);
        return lienRepository.save(lien);
    }

    private Certification initCertificationTemplate(Institution institution) throws IOException {
        Certification certificationTemplate = TestUtils.createCertificationTemplate();
        certificationTemplate.setId(null);
        certificationTemplate.setInstitution(institution);
        return certificationTemplate;
    }

    private Student createUserWithCertification() {
        Student student = new Student();
        student.setCertifications(new ArrayList<>(Collections.singletonList(createCertification())));
        return userRepository.save(student);
    }

    private Certification createCertification() {
        studentCertification = TestUtils.createCertification();
        studentCertification.setId(null);
        studentCertification.setSalt(KeyGenerators.string().generateKey());
        return studentCertification;
    }
}