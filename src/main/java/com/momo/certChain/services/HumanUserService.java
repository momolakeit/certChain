package com.momo.certChain.services;

import com.momo.certChain.exception.*;
import com.momo.certChain.mapping.EmployeeMapper;
import com.momo.certChain.mapping.StudentMapper;
import com.momo.certChain.model.data.Certification;
import com.momo.certChain.model.data.Employee;
import com.momo.certChain.model.data.HumanUser;
import com.momo.certChain.model.data.Student;
import com.momo.certChain.model.dto.HumanUserDTO;
import com.momo.certChain.repositories.HumanUserRepository;
import com.momo.certChain.services.messaging.MessageService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Optional;

@Service
@Transactional
public class HumanUserService {

    private final HumanUserRepository humanUserRepository;

    private final MessageService messageService;

    private final PasswordEncoder passwordEncoder;

    private final UserService userService;

    private final CertificationService certificationService;

    public HumanUserService(HumanUserRepository humanUserRepository,
                            MessageService messageService,
                            PasswordEncoder passwordEncoder,
                            UserService userService,
                            CertificationService certificationService) {
        this.humanUserRepository = humanUserRepository;
        this.messageService = messageService;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.certificationService = certificationService;
    }


    //set up confirmer password dans le backend aussi pour securite accrue

    public HumanUser modifyPassword(String uuid, String oldPassword, String password, String passwordConfirmation) {
        HumanUser user = (HumanUser) userService.getUser(uuid);

        verifyPasswordConditions(oldPassword, password, passwordConfirmation, user);

        encodeUserPassword(user, password);

        return saveUser(user);
    }

    //todo test that
    public HumanUser createHumanUser(HumanUser humanUser) {

        Optional<HumanUser> humanUserOptional = humanUserRepository.findByUsername(humanUser.getUsername());

        if (humanUserOptional.isPresent() && humanUserOptional.get() instanceof Student) {
            return ajouterCertificatAStudentExistant((Student) humanUserOptional.get(), ((Student) humanUser).getCertifications().get(0));
        } else {
            sendEmailMessage(humanUser);
            return (HumanUser) userService.createUser(humanUser);
        }
    }

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

    private void sendEmailMessage(HumanUser humanUser) {
        String generatedPassword = createPasswordForUser(humanUser);

        try {
            messageService.sendUserCreatedEmail(humanUser, generatedPassword);
        } catch (MessagingException | IOException e) {
            throw new CustomMessagingException();
        }
    }

    private HumanUser ajouterCertificatAStudentExistant(Student student, Certification certification) {
        certification.setStudent(student);

        student.getCertifications().add(certificationService.saveCertification(certification));

        return (HumanUser) userService.saveUser(student);
    }
}
