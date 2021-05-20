package com.momo.certChain.services;

import com.momo.certChain.exception.PasswordNotMatchingException;
import com.momo.certChain.exception.ValidationException;
import com.momo.certChain.model.data.Admin;
import com.momo.certChain.repositories.AdminRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
    private final AdminRepository adminRepository;

    private final PasswordEncoder passwordEncoder;

    public AdminService(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Admin createAdmin(String email,String password,String passwordConfirmation) {
        if(adminRepository.findAll().isEmpty()){
            checkIfPasswordMatching(password, passwordConfirmation);
            return adminRepository.save(createAdmin(email, password));
        }
        else{
            throw new ValidationException("Un admin existe déja");
        }
    }

    private void checkIfPasswordMatching(String password, String passwordConfirmation) {
        if(!password.equals(passwordConfirmation)){
            throw new PasswordNotMatchingException();
        }
    }

    private Admin createAdmin(String email, String password) {
        Admin admin = new Admin();
        admin.setUsername(email);
        admin.setPassword(passwordEncoder.encode(password));
        return admin;
    }
}
