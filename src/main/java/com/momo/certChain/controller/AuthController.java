package com.momo.certChain.controller;

import com.momo.certChain.model.data.Admin;
import com.momo.certChain.model.dto.JWTResponse;
import com.momo.certChain.model.dto.request.CreateUserDTO;
import com.momo.certChain.model.dto.request.LogInDTO;
import com.momo.certChain.services.AdminService;
import com.momo.certChain.services.authentification.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController extends BaseController {
    private final AuthService authService;

    private final AdminService adminService;

    public AuthController(AuthService authService, AdminService adminService) {
        this.authService = authService;
        this.adminService = adminService;
    }

    @PostMapping
    public JWTResponse logIn(@RequestBody LogInDTO logInDTO) {
        return authService.logInUser(logInDTO.getEmailAddress(), logInDTO.getPassword());
    }

    @PostMapping("/admin")
    public JWTResponse createAdmin(@RequestBody CreateUserDTO createUserDTO) {
        Admin admin = adminService.createAdmin(createUserDTO.getEmail(),
                createUserDTO.getPassword(),
                createUserDTO.getPasswordConfirmation());
        return authService.logInUser(admin.getUsername(), createUserDTO.getPassword());
    }
}
