package com.momo.certChain.controller;

import com.momo.certChain.TestUtils;
import com.momo.certChain.mapping.CertificationMapper;
import com.momo.certChain.model.data.Certification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@Transactional
class CertificationControllerTest {
    @Autowired
    private CertificationController certificationController;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void init(){
        mockMvc = MockMvcBuilders.standaloneSetup(certificationController).build();
    }

    @Test
    public void testCreateCertificationTemplate() throws Exception {
        MockMultipartFile universityLogo = new MockMultipartFile("universityLogo", "MOCK_DATA.xlsx", "multipart/form-data", TestUtils.getExcelByteArray());
        MockMultipartFile universityStamp= new MockMultipartFile("universityStamp", "MOCK_DATA.xlsx", "multipart/form-data", TestUtils.getExcelByteArray());
        Certification certification = TestUtils.createCertificationTemplate();
        certification.setId(null);
        mockMvc.perform(MockMvcRequestBuilders.multipart("/institution/createTemplate")
                .file("universityLogo",universityLogo.getBytes())
                .file("universityStamp",universityStamp.getBytes())
                .param("certificationDTO",objectMapper.writeValueAsString(CertificationMapper.instance.toDTO(certification)))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}