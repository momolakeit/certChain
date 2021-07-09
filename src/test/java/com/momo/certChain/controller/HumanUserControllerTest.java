package com.momo.certChain.controller;

import com.momo.certChain.Utils.TestUtils;
import com.momo.certChain.model.data.Certification;
import com.momo.certChain.model.data.Student;
import com.momo.certChain.model.dto.request.ModifyPasswordDTO;
import com.momo.certChain.repositories.CertificationRepository;
import com.momo.certChain.repositories.HumanUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Collections;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@Transactional
class HumanUserControllerTest {

    @Autowired
    private HumanUserRepository humanUserRepository;

    @Autowired
    private CertificationRepository certificationRepository;


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private HumanUserController humanUserController;

    Student student;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void init(){
        student = TestUtils.createStudent();
        student.setId(null);
        student.setPassword(passwordEncoder.encode(student.getPassword()));
        student.setCertifications(new ArrayList<>(Collections.singletonList(certificationRepository.save(new Certification()))));


        student = humanUserRepository.save(student);

        mockMvc = MockMvcBuilders.standaloneSetup(humanUserController).build();
    }

    @Test
    public void modifyPasswordTest() throws Exception {
        ModifyPasswordDTO modifyPasswordDTO = new ModifyPasswordDTO(student.getId(),"password","newPassword","newPassword","encKey");

        mockMvc.perform(MockMvcRequestBuilders.post("/humanUser/modifyPassword")
            .content(objectMapper.writeValueAsString(modifyPasswordDTO))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    public void modifyWrongOldPasswordTest() throws Exception {
        ModifyPasswordDTO modifyPasswordDTO = new ModifyPasswordDTO(student.getId(),"badPassword","newPassword","newPassword","encKey");

        mockMvc.perform(MockMvcRequestBuilders.post("/humanUser/modifyPassword")
                .content(objectMapper.writeValueAsString(modifyPasswordDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void modifyPasswordNotMatchingPasswordTest() throws Exception {
        ModifyPasswordDTO modifyPasswordDTO = new ModifyPasswordDTO(student.getId(),"password","newPassword","newPassword2","encKey");

        mockMvc.perform(MockMvcRequestBuilders.post("/humanUser/modifyPassword")
                .content(objectMapper.writeValueAsString(modifyPasswordDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

}