package com.momo.certChain.controller;

import com.momo.certChain.jwt.JwtProvider;
import com.momo.certChain.model.data.Student;
import com.momo.certChain.model.data.User;
import com.momo.certChain.repositories.UserRepository;
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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@Transactional
class UserControllerTest {

    @Autowired
    private UserController userController;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtProvider jwtProvider;

    private MockMvc mockMvc;

    private User user;

    private String authToken;

    private final String AUTHORIZATION = "Authorization";

    private final String BEARER = "Bearer ";

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        user = userRepository.save(new Student());

        authToken =  jwtProvider.generate(user);
    }

    @Test
    public void fetchUserPasTypeAccepteLanceException() throws Exception {
        user = userRepository.save(new User());
        authToken =  jwtProvider.generate(user);

        mockMvc.perform(MockMvcRequestBuilders.get("/user/getLoggedUser")
                .header(AUTHORIZATION,BEARER+ authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void fetchUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user/getLoggedUser")
                .header(AUTHORIZATION,BEARER+ authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}