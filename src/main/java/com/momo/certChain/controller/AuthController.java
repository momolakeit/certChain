package com.momo.certChain.controller;

import com.momo.certChain.model.dto.JWTResponse;
import com.momo.certChain.model.dto.request.LogInDTO;
import com.momo.certChain.services.authentification.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController extends BaseController{
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping
    public JWTResponse logIn(@RequestBody LogInDTO logInDTO){
        return authService.logInUser(logInDTO.getEmailAdress(),logInDTO.getPassword());
    }
}
