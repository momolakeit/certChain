package com.momo.certChain.controller;

import com.momo.certChain.model.data.ImageFile;
import com.momo.certChain.repositories.ImageFileRepository;
import org.junit.jupiter.api.BeforeAll;
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
import org.testcontainers.shaded.org.bouncycastle.crypto.tls.ContentType;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@Transactional
class ImageFileControllerTest {

    @Autowired
    private ImageFileRepository imageFileRepository;

    @Autowired
    private ImageFileController imageFileController;

    private MockMvc mockMvc;

    private ImageFile imageFile;

    @BeforeEach
    public void init() {
        imageFile = new ImageFile();
        imageFile.setBytes("wesh".getBytes());
        imageFile = imageFileRepository.save(imageFile);

        mockMvc = MockMvcBuilders.standaloneSetup(imageFileController).build();
    }

    @Test
    public void testGetImageFile() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/image/{id}", imageFile.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetImageFileNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/image/{id}", "123456")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}