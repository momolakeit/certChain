package com.momo.certChain.controller;

import com.momo.certChain.model.data.Certification;
import com.momo.certChain.model.data.Lien;
import com.momo.certChain.repositories.CertificationRepository;
import com.momo.certChain.repositories.LienRepository;
import org.junit.Before;
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

import javax.print.attribute.standard.Media;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@Transactional
class LienControllerTest {

    @Autowired
    private LienController lienController;

    @Autowired
    private LienRepository lienRepository;

    @Autowired
    private CertificationRepository certificationRepository;

    private Certification certification;

    private MockMvc mockMvc;


    @BeforeEach
    public void init() {
        certification = certificationRepository.save(new Certification());

        createLienWithCertification();

        createLienWithCertification();

        mockMvc = MockMvcBuilders.standaloneSetup(lienController).build();
    }

    @Test
    public void fetchAllLienForCertification() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/lien/findAllByCertification/{certificationId}", certification.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    private void createLienWithCertification() {
        Lien lien = new Lien();
        lien.setCertification(certification);
        lienRepository.save(lien);
    }
}