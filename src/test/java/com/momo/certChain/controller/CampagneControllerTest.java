package com.momo.certChain.controller;

import com.momo.certChain.Utils.InitEnvService;
import com.momo.certChain.Utils.TestUtils;
import com.momo.certChain.mapping.SimpleStudentMapper;
import com.momo.certChain.mapping.StudentMapper;
import com.momo.certChain.model.data.*;
import com.momo.certChain.model.dto.request.RunCampagneDTO;
import com.momo.certChain.repositories.CampagneRepository;
import com.momo.certChain.repositories.HumanUserRepository;
import com.momo.certChain.repositories.UserRepository;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@Transactional
class CampagneControllerTest {

    @Autowired
    private CampagneRepository campagneRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CampagneController campagneController;

    @Autowired
    private InitEnvService initEnvService;

    private MockMvc mockMvc;

    private String campagneId;

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void init() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(campagneController).build();

        List<HumanUser> students  = userRepository.saveAll(Arrays.asList(createStudent(),createStudent()));

        Institution institution = (Institution) userRepository.findById(initEnvService.initEnv()).get();

        Campagne campagne = TestUtils.createCampagne();
        campagne.setStudentList(students);
        campagne.setInstitution(institution);

        campagneId =  campagneRepository.save(campagne).getId();
    }

    @Test
    public void fetchCampagneTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/campagne/{campagneId}",campagneId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());
    }

    @Test
    public void fetchCampagneNotFoundTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/campagne/{campagneId}","123456")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void runCampagne() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/campagne/runCampagne")
                .content(objectMapper.writeValueAsString(new RunCampagneDTO(campagneId,InitEnvService.encryptionKey)))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    private Student createStudent(){
        Student student = SimpleStudentMapper.instance.toSimple(TestUtils.createStudent());
        student.setCertifications(Collections.singletonList(createCertification()));
        return student;
    }

    private Certification createCertification() {
        Certification certification = TestUtils.createCertification();
        certification.setId(null);
        return certification;
    }

}