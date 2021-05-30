package com.momo.certChain.services.authentification;

import com.momo.certChain.exception.BadPasswordException;
import com.momo.certChain.exception.ObjectNotFoundException;
import com.momo.certChain.jwt.JwtProvider;
import com.momo.certChain.model.data.User;
import com.momo.certChain.model.dto.response.JWTResponse;
import com.momo.certChain.repositories.UserRepository;
import com.momo.certChain.services.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    private UserService userService;

    private final PasswordEncoder passwordEncoder;

    private final JwtProvider jwtProvider;

    public AuthService(UserService userService, PasswordEncoder passwordEncoder, JwtProvider jwtProvider) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }

    public JWTResponse logInUser(String username, String password){
        User user = userService.findUserByEmail(username);
        if(!passwordEncoder.matches(password,user.getPassword())){
            throw new BadPasswordException();
        }

        return createJwtResponse(user);
    }

    private JWTResponse createJwtResponse(User user) {
        JWTResponse jwtResponse = new JWTResponse();
        jwtResponse.setToken(jwtProvider.generate(user));
        return jwtResponse;
    }

}
