package com.momo.certChain.controller;

import com.momo.certChain.model.data.Campagne;
import com.momo.certChain.repositories.CampagneRepository;
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
    private CampagneController campagneController;

    private MockMvc mockMvc;

    private String campagneId;

    @BeforeEach
    public void init(){
        mockMvc = MockMvcBuilders.standaloneSetup(campagneController).build();

        campagneId =  campagneRepository.save(new Campagne()).getId();
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

}