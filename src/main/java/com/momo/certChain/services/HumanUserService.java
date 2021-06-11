package com.momo.certChain.services;

import com.momo.certChain.exception.BadPasswordException;
import com.momo.certChain.exception.CustomMessagingException;
import com.momo.certChain.exception.ObjectNotFoundException;
import com.momo.certChain.exception.PasswordNotMatchingException;
import com.momo.certChain.mapping.EmployeeMapper;
import com.momo.certChain.mapping.StudentMapper;
import com.momo.certChain.model.data.Employee;
import com.momo.certChain.model.data.HumanUser;
import com.momo.certChain.model.data.Student;
import com.momo.certChain.model.dto.HumanUserDTO;
import com.momo.certChain.repositories.HumanUserRepository;
import com.momo.certChain.services.messaging.MessageService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

@Service
@Transactional
public class HumanUserService {

    private final HumanUserRepository humanUserRepository;

    private final MessageService messageService;

    private final PasswordEncoder passwordEncoder;

    private final UserService userService;

    public HumanUserService(HumanUserRepository humanUserRepository, MessageService messageService, PasswordEncoder passwordEncoder, UserService userService) {
        this.humanUserRepository = humanUserRepository;
        this.messageService = messageService;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    //todo tester le cas ou on lance la custom messaging exception
    public HumanUser createHumanUser(HumanUser humanUser,String encryptionKey) {
        String generatedPassword = RandomStringUtils.randomAlphanumeric(11);

        humanUser.setPasswordResseted(false);
        humanUser.setPassword(passwordEncoder.encode(generatedPassword));
        try{
            messageService.sendEmailToHumanUser(humanUser,generatedPassword);
        }catch (MessagingException | IOException e) {
            throw new CustomMessagingException();
        }
        return (HumanUser) userService.createUser(humanUser);
    }

    //set up confirmer password dans le backend aussi pour securite accrue
    public HumanUser modifyPassword(String uuid, String oldPassword, String password, String passwordConfirmation) {
        HumanUser user = (HumanUser) userService.getUser(uuid);

        verifyPasswordConditions(oldPassword, password, passwordConfirmation, user);

        user.setPassword(passwordEncoder.encode(password));

        return saveUser(user);
    }

    //todo test that
    public HumanUserDTO toDTO (HumanUser humanUser){
        if(humanUser instanceof Student){
            return StudentMapper.instance.toDTO((Student) humanUser);
        }
        else if (humanUser instanceof Employee){
            return EmployeeMapper.instance.toDTO((Employee) humanUser);
        }
        throw new ClassCastException();

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


    private void verifyPasswordConditions(String oldPassword, String password, String passwordConfirmation, HumanUser user) {
        if(!passwordEncoder.matches(oldPassword, user.getPassword())){
            throw new BadPasswordException();
        }

        if(!password.equals(passwordConfirmation)){
            throw new PasswordNotMatchingException();
        }
    }
}
