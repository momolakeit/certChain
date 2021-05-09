package com.momo.certChain.services;

import com.momo.certChain.exception.ObjectNotFoundException;
import com.momo.certChain.exception.PasswordNotMatchingException;
import com.momo.certChain.model.data.HumanUser;
import com.momo.certChain.repositories.HumanUserRepository;
import com.momo.certChain.services.messaging.MessageService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.util.List;

@Service
@Transactional
public class HumanUserService {

    private final HumanUserRepository humanUserRepository;

    private final MessageService messageService;

    private final PasswordEncoder passwordEncoder;

    public HumanUserService(HumanUserRepository humanUserRepository, MessageService messageService, PasswordEncoder passwordEncoder) {
        this.humanUserRepository = humanUserRepository;
        this.messageService = messageService;
        this.passwordEncoder = passwordEncoder;
    }

    public HumanUser createHumanUser(HumanUser humanUser,String encryptionKey) throws MessagingException {
        humanUser.setPasswordResseted(false);
        messageService.sendEmailToHumanUser(humanUser,encryptionKey);

        return saveUser(humanUser);
    }

    //set up confirmer password dans le backend aussi pour securite accrue
    public HumanUser setUpPassword(String uuid,String password,String passwordConfirmation) {
        HumanUser user = getUser(uuid);

        if(!password.equals(passwordConfirmation)){
            throw new PasswordNotMatchingException();
        }

        user.setPassword(passwordEncoder.encode(password));

        return saveUser(user);
    }

    public HumanUser getUser(String uuid) {
        return humanUserRepository.findById(uuid).orElseThrow(this::humanUserNotFound);
    }

    public HumanUser saveUser(HumanUser user){
        return humanUserRepository.save(user);
    }

    public List<HumanUser> saveMultipleUser(List<HumanUser> user){
        return humanUserRepository.saveAll(user);
    }

    private ObjectNotFoundException humanUserNotFound() {
        return new ObjectNotFoundException("User");
    }

}
