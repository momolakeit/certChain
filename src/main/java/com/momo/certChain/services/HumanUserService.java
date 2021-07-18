package com.momo.certChain.services;

import com.momo.certChain.exception.*;
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

import javax.mail.MessagingException;
import java.io.IOException;

@Service
public class HumanUserService {

    private final HumanUserRepository humanUserRepository;

    private final MessageService messageService;

    private final PasswordEncoder passwordEncoder;

    private final UserService userService;

    public HumanUserService(HumanUserRepository humanUserRepository,
                            MessageService messageService,
                            PasswordEncoder passwordEncoder,
                            UserService userService) {
        this.humanUserRepository = humanUserRepository;
        this.messageService = messageService;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    //todo tester le cas ou on lance la custom messaging exception
    public HumanUser createHumanUser(HumanUser humanUser) {
        String generatedPassword = createPasswordForUser(humanUser);

        try {
            messageService.sendUserCreatedEmail(humanUser, generatedPassword);
        } catch (MessagingException | IOException e) {
            throw new CustomMessagingException();
        }

        return createUser(humanUser);
    }


    //set up confirmer password dans le backend aussi pour securite accrue
    public HumanUser modifyPassword(String uuid, String oldPassword, String password, String passwordConfirmation) {
        HumanUser user = (HumanUser) userService.getUser(uuid);

        verifyPasswordConditions(oldPassword, password, passwordConfirmation, user);

        encodeUserPassword(user, password);

        return saveUser(user);
    }

    //todo test that

    public HumanUserDTO toDTO(HumanUser humanUser) {
        if (humanUser instanceof Student) {
            return StudentMapper.instance.toDTO((Student) humanUser);
        } else if (humanUser instanceof Employee) {
            return EmployeeMapper.instance.toDTO((Employee) humanUser);
        }
        throw new ClassCastException();

    }

    public HumanUser saveUser(HumanUser user) {
        return humanUserRepository.save(user);
    }

    private void verifyPasswordConditions(String oldPassword, String password, String passwordConfirmation, HumanUser user) {
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BadPasswordException();
        }

        if (!password.equals(passwordConfirmation)) {
            throw new PasswordNotMatchingException();
        }
    }

    private String createPasswordForUser(HumanUser humanUser) {
        String generatedPassword = RandomStringUtils.randomAlphanumeric(11);

        humanUser.setPasswordResseted(false);

        encodeUserPassword(humanUser, generatedPassword);

        return generatedPassword;
    }

    private void encodeUserPassword(HumanUser humanUser, String password) {
        humanUser.setPassword(passwordEncoder.encode(password));
    }

    private HumanUser createUser(HumanUser humanUser) {
        try {
            return (HumanUser) userService.createUser(humanUser);

        } catch (ValidationException e) {
            if (humanUser instanceof Student) {
                return ajouterCertificatAStudentExistant((Student) humanUser);
            } else {
                throw e;
            }

        }
    }

    private HumanUser ajouterCertificatAStudentExistant(Student student) {
        Student studentEntity = (Student) userService.findUserByEmail(student.getUsername());

        studentEntity.getCertifications().add(student.getCertifications().get(0));

        return (HumanUser) userService.saveUser(studentEntity);
    }
}
