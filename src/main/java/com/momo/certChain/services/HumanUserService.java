package com.momo.certChain.services;

import com.momo.certChain.exception.BadPasswordException;
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
        String generatedPassword = RandomStringUtils.randomAlphanumeric(10);

        humanUser.setPasswordResseted(false);
        humanUser.setPassword(passwordEncoder.encode(generatedPassword));
        messageService.sendEmailToHumanUser(humanUser,encryptionKey,generatedPassword);

        return saveUser(humanUser);
    }

    //set up confirmer password dans le backend aussi pour securite accrue
    public HumanUser modifyPassword(String uuid, String oldPassword, String password, String passwordConfirmation) {
        HumanUser user = getUser(uuid);

        verifyPasswordConditions(oldPassword, password, passwordConfirmation, user);

        user.setPassword(passwordEncoder.encode(password));

        return saveUser(user);
    }

    public HumanUser getUser(String uuid) {
        return humanUserRepository.findById(uuid).orElseThrow(this::humanUserNotFound);
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
