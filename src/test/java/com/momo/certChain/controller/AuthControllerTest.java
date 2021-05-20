package com.momo.certChain.controller;

import com.momo.certChain.TestUtils;
import com.momo.certChain.model.data.Admin;
import com.momo.certChain.model.data.Student;
import com.momo.certChain.model.dto.request.CreateUserDTO;
import com.momo.certChain.model.dto.request.LogInDTO;
import com.momo.certChain.repositories.UserRepository;
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
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@Transactional
class AuthControllerTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthController authController;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;

    private Student student;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String username = "username";

    private final String password = "password";

    @BeforeEach
    public void init(){
        student = new Student();
        student.setUsername(username);
        student.setPassword(passwordEncoder.encode(password));

        userRepository.save(student);

        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();

    }

    @Test
    public void authUserTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/auth")
                        .content(objectMapper.writeValueAsString(new LogInDTO(username,password)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());
    }


    @Test
    public void authUserBadPasswordTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/auth")
                .content(objectMapper.writeValueAsString(new LogInDTO(username,"badPassword")))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(401));
    }

    @Test
    public void createAdminTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/admin")
                .content(objectMapper.writeValueAsString(new CreateUserDTO("email@mail.com","password","password")))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void createAdminAlreadyExistTest() throws Exception {
        userRepository.save(new Admin());
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/admin")
                .content(objectMapper.writeValueAsString(new CreateUserDTO("email@mail.com","password","password")))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createAdminBadPasswordConfirmationTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/admin")
                .content(objectMapper.writeValueAsString(new CreateUserDTO("email@mail.com","password","passwordNotMatch")))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }



}