package com.momo.certChain.controller;

import com.momo.certChain.TestUtils;
import com.momo.certChain.mapping.AddressMapper;
import com.momo.certChain.mapping.CertificationMapper;
import com.momo.certChain.mapping.InstitutionMapper;
import com.momo.certChain.model.data.*;
import com.momo.certChain.model.dto.request.CreateInstitutionDTO;
import com.momo.certChain.repositories.*;
import com.momo.certChain.services.blockChain.ContractService;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.web3j.crypto.ECKeyPair;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@Transactional
class InstitutionControllerTest {

    @Autowired
    private InstitutionController institutionController;

    @Autowired
    private InstitutionRepository institutionRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private ImageFileRepository imageFileRepository;

    @Autowired
    private SignatureRepository signatureRepository;

    @Autowired
    private ContractService contractService;

    @Autowired
    private EncryptionService encryptionService;

    @Autowired
    private CertificationRepository certificationRepository;

    @MockBean
    private MessageService messageService;

    private String institutionId;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String encryptionKey="encrypted";

    private final String campagneName = "genie logicel session hiver";

    @BeforeEach
    public void init() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(institutionController).build();

        Institution institution = getInstitution();

        InstitutionWallet institutionWallet = TestUtils.createInstitutionWallet();

        Certification certification = TestUtils.createCertificationTemplate();
        certification.setInstitution(null);
        certification.setUniversityStamp(imageFileRepository.save(certification.getUniversityStamp()));
        certification.setUniversityLogo(imageFileRepository.save(certification.getUniversityLogo()));
        List<Signature> signatures = certification.getSignatures();
        certification.setSignatures(new ArrayList<>());
        for(Signature signature : signatures){
            signature.setSignatureImage(imageFileRepository.save(signature.getSignatureImage()));
            certification.getSignatures().add(signatureRepository.save(signature));
        }
        certification.setStudent(null);

        certification.setId(null);

        institution.setContractAddress(contractService.uploadContract(new ECKeyPair(new BigInteger(institutionWallet.getPrivateKey()),new BigInteger(institutionWallet.getPublicKey()))));

        institutionWallet.setSalt(encryptionService.generateSalt());
        institutionWallet.setPrivateKey(encryptionService.encryptData(encryptionKey,institutionWallet.getPrivateKey(),institutionWallet.getSalt()));
        institutionWallet.setPublicKey(encryptionService.encryptData(encryptionKey,institutionWallet.getPublicKey(),institutionWallet.getSalt()));

        institution.setCertificationTemplate(certificationRepository.save(certification));
        institution.setInstitutionWallet(walletRepository.save(institutionWallet));
        institutionId = institutionRepository.save(institution).getId();
    }

    @Test
    public void testCreateCertificationTemplate() throws Exception {
        MockMultipartFile universityLogo = new MockMultipartFile("universityLogo", "MOCK_DATA.xlsx", "multipart/form-data", TestUtils.getExcelByteArray());
        MockMultipartFile universityStamp = new MockMultipartFile("universityStamp", "MOCK_DATA.xlsx", "multipart/form-data", TestUtils.getExcelByteArray());

        Certification certification = TestUtils.createCertificationTemplate();
        certification.setId(null);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/institution/createTemplate")
                .file("universityLogo", universityLogo.getBytes())
                .file("universityStamp", universityStamp.getBytes())
                .param("certificationDTO", objectMapper.writeValueAsString(CertificationMapper.instance.toDTO(certification)))
                .param("institutionId",institutionId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateCertificationTemplateInstitutionNotApprouved() throws Exception {
        MockMultipartFile universityLogo = new MockMultipartFile("universityLogo", "MOCK_DATA.xlsx", "multipart/form-data", TestUtils.getExcelByteArray());
        MockMultipartFile universityStamp = new MockMultipartFile("universityStamp", "MOCK_DATA.xlsx", "multipart/form-data", TestUtils.getExcelByteArray());

        createInstitutionNotApprouved();

        Certification certification = TestUtils.createCertificationTemplate();
        certification.setId(null);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/institution/createTemplate")
                .file("universityLogo", universityLogo.getBytes())
                .file("universityStamp", universityStamp.getBytes())
                .param("certificationDTO", objectMapper.writeValueAsString(CertificationMapper.instance.toDTO(certification)))
                .param("institutionId",institutionId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    private void createInstitutionNotApprouved() {
        Institution institution = TestUtils.createInstitution();
        institution.setApprouved(false);
        institutionId = institutionRepository.save(institution).getId();
    }

    @Test
    public void testCreateCertificationTemplateInstitutionNotFound() throws Exception {
        MockMultipartFile universityLogo = new MockMultipartFile("universityLogo", "MOCK_DATA.xlsx", "multipart/form-data", TestUtils.getExcelByteArray());
        MockMultipartFile universityStamp = new MockMultipartFile("universityStamp", "MOCK_DATA.xlsx", "multipart/form-data", TestUtils.getExcelByteArray());

        Certification certification = TestUtils.createCertificationTemplate();
        certification.setId(null);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/institution/createTemplate")
                .file("universityLogo", universityLogo.getBytes())
                .file("universityStamp", universityStamp.getBytes())
                .param("certificationDTO", objectMapper.writeValueAsString(CertificationMapper.instance.toDTO(certification)))
                .param("institutionId","789456")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @Test
    public void testCreateInstitution() throws Exception {
        Address address = TestUtils.createAddress();
        address.setId(null);

        Institution institution = TestUtils.createInstitution();
        institution.setId(null);

        mockMvc.perform(MockMvcRequestBuilders.post("/institution")
                .content(objectMapper.writeValueAsString(new CreateInstitutionDTO(AddressMapper.instance.toDTO(address),InstitutionMapper.instance.toDTO(institution),"password","password")))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetInstitution() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/institution/{institutionId}", institutionId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetInstitutionNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/institution/{institutionId}","123456")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @Test
    public void testApprouveInstitution() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/institution/approuveInstitution/{institutionId}",institutionId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testUploadCertificationToBlockchain() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "MOCK_DATA.xlsx", "multipart/form-data", TestUtils.getExcelByteArray());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/institution/uploadCertification/{institutionId}", institutionId)
                .file(file)
                .param("walletPassword",encryptionKey)
                .param("campagneName",campagneName)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testUploadCertificationToBlockchainInstitutionNotApprouved() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "MOCK_DATA.xlsx", "multipart/form-data", TestUtils.getExcelByteArray());

        createInstitutionNotApprouved();

        mockMvc.perform(MockMvcRequestBuilders.multipart("/institution/uploadCertification/{institutionId}", institutionId)
                .file(file)
                .param("walletPassword",encryptionKey)
                .param("campagneName",campagneName)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @Test
    public void findNonApprouvedInstitutions() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/institution/getNonApprouvedInstitutions")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    private Institution getInstitution() {
        Institution institution = TestUtils.createInstitution();
        institution.setAddress(null);
        return institution;
    }
}