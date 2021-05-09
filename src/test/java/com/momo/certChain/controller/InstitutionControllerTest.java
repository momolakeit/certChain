package com.momo.certChain.controller;

import com.momo.certChain.TestUtils;
import com.momo.certChain.mapping.AddressMapper;
import com.momo.certChain.mapping.InstitutionMapper;
import com.momo.certChain.model.data.Address;
import com.momo.certChain.model.data.Certification;
import com.momo.certChain.model.data.Institution;
import com.momo.certChain.model.data.InstitutionWallet;
import com.momo.certChain.model.dto.request.CreateInstitutionDTO;
import com.momo.certChain.repositories.CertificationRepository;
import com.momo.certChain.repositories.InstitutionRepository;
import com.momo.certChain.repositories.WalletRepository;
import com.momo.certChain.services.blockChain.ContractService;
import com.momo.certChain.services.blockChain.ContractServiceImpl;
import com.momo.certChain.services.messaging.MessageService;
import com.momo.certChain.services.security.EncryptionService;
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
    private ContractService contractService;

    @Autowired
    private EncryptionService encryptionService;

    @Autowired
    private CertificationRepository certificationRepository;

    @MockBean
    private MessageService messageService;

    private String conversationId;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String encryptionKey="encrypted";

    private final String campagneName = "genie logicel session hiver";

    @BeforeEach
    public void init() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(institutionController).build();

        Institution institution = TestUtils.createInstitution();
        Certification certification = TestUtils.createCertificationTemplate();
        InstitutionWallet institutionWallet = TestUtils.createInstitutionWallet();

        certification.setId(null);

        institution.setContractAddress(contractService.uploadContract(new ECKeyPair(new BigInteger(institutionWallet.getPrivateKey()),new BigInteger(institutionWallet.getPublicKey()))));

        institutionWallet.setSalt(encryptionService.generateSalt());
        institutionWallet.setPrivateKey(encryptionService.encryptData(encryptionKey,institutionWallet.getPrivateKey(),institutionWallet.getSalt()));
        institutionWallet.setPublicKey(encryptionService.encryptData(encryptionKey,institutionWallet.getPublicKey(),institutionWallet.getSalt()));

        institution.setCertificationTemplate(certificationRepository.save(certification));
        institution.setInstitutionWallet(walletRepository.save(institutionWallet));
        conversationId = institutionRepository.save(institution).getId();
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
        mockMvc.perform(MockMvcRequestBuilders.get("/institution/{institutionId}",conversationId)
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
    public void testUploadCertificationToBlockchain() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "MOCK_DATA.xlsx", "multipart/form-data", TestUtils.getExcelByteArray());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/institution/uploadCertification/{institutionId}",conversationId)
                .file(file)
                .param("walletPassword",encryptionKey)
                .param("campagneName",campagneName)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


}