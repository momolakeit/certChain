package com.momo.certChain.services;

import com.momo.certChain.exception.ObjectNotFoundException;
import com.momo.certChain.exception.PasswordNotMatchingException;
import com.momo.certChain.model.data.HumanUser;
import com.momo.certChain.repositories.HumanUserRepository;
import com.momo.certChain.services.messaging.MessageService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class HumanUserService {

    private final HumanUserRepository humanUserRepository;

    private final MessageService messageService;

    private final PasswordEncoder passwordEncoder;

    public HumanUserService(HumanUserRepository humanUserRepository, MessageService messageService, PasswordEncoder passwordEncoder) {
        this.humanUserRepository = humanUserRepository;
        this.messageService = messageService;
        this.passwordEncoder = passwordEncoder;
    }

    public HumanUser createHumanUser(HumanUser humanUser) {
        messageService.sendEmail(humanUser);
        return humanUserRepository.save(humanUser);
    }

    //set up confirmer password dans le backend aussi pour securite accrue
    public HumanUser setUpPassword(String uuid,String password,String passwordConfirmation) {
        HumanUser user = getUser(uuid);
        if(!password.equals(passwordConfirmation)){
            throw new PasswordNotMatchingException();
        }
        user.setPassword(passwordEncoder.encode(password));
        return humanUserRepository.save(user);
    }

    public HumanUser getUser(String uuid) {
        return humanUserRepository.findById(uuid).orElseThrow(this::humanUserNotFound);
    }

    private ObjectNotFoundException humanUserNotFound() {
        return new ObjectNotFoundException("User");
    }

}
