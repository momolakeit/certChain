package com.momo.certChain.controller;

import com.momo.certChain.Utils.TestUtils;
import com.momo.certChain.model.data.Admin;
import com.momo.certChain.model.data.Signature;
import com.momo.certChain.model.data.Student;
import com.momo.certChain.model.dto.request.CreateUserDTO;
import com.momo.certChain.model.dto.request.LogInDTO;
import com.momo.certChain.repositories.SignatureRepository;
import com.momo.certChain.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@Transactional
class SignatureControllerTest {


    @Autowired
    private SignatureController signatureController;

    @Autowired
    private SignatureRepository signatureRepository;


    private MockMvc mockMvc;

    private Signature signature;


    @BeforeEach
    public void init() throws IOException {
        mockMvc = MockMvcBuilders.standaloneSetup(signatureController).build();

        Signature signatureToSave = TestUtils.createSignature();
        signatureToSave.setSignatureImage(null);
        signatureToSave.setId(null);

        signature = signatureRepository.save(signatureToSave);
    }

    @Test
    public void addSignatureImageTest() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "MOCK_DATA.xlsx", "multipart/form-data", TestUtils.getExcelByteArray());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/signature/addImage/{signatureId}",signature.getId())
                .file(file)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}