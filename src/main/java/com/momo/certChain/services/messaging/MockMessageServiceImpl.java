package com.momo.certChain.services.messaging;

import com.momo.certChain.model.data.HumanUser;
import com.momo.certChain.model.data.Institution;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

@Service
@Profile({"test","local"})
public class MockMessageServiceImpl implements MessageService {

    private final JavaMailSender javaMailSender;

    private final Logger LOGGER = Logger.getLogger(MockMessageServiceImpl.class.getName());

    public MockMessageServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendUserCreatedEmail(HumanUser humanUser, String password) throws MessagingException, IOException {
        LOGGER.info("EMAIL WAS SENT SUCCESSFULLY!");
        LOGGER.info("user password: "+password);
    }

    public void sendCertificatePrivateKey(HumanUser humanUser, String encKey) throws MessagingException, IOException {
        LOGGER.info("EMAIL WAS SENT SUCCESSFULLY!");
        LOGGER.info("private key: "+encKey);
    }


    //on send un email a moi , le maitre du trucs pour que je review et approuved le institutions

    public void sendApprouvalEmail(Institution institution) throws MessagingException {
        LOGGER.info("EMAIL WAS SENT SUCCESSFULLY!");

    }
}
