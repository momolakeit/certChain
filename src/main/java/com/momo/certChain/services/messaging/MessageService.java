package com.momo.certChain.services.messaging;

import com.momo.certChain.model.data.HumanUser;
import com.momo.certChain.model.data.Student;
import jnr.ffi.annotations.In;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.logging.Logger;

@Service
public class MessageService {

    @Value("${front-end-url}")
    private String frontEndUrl;

    private final JavaMailSender javaMailSender;

    private final Logger LOGGER = Logger.getLogger(MessageService.class.getName());

    public MessageService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendEmail(HumanUser humanUser){
        String from =  "certChain@"+humanUser.getInstitution().getName()+".com";
        try{
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
            helper.setFrom(new InternetAddress(from));
            helper.setTo(humanUser.getUsername());
            helper.setSubject("Receive your diploma !");
            helper.setText(frontEndUrl+"createPassword/"+humanUser.getId()+".com");
            javaMailSender.send(message);
            LOGGER.info("EMAIL WAS SENT SUCCESSFULLY!");
        }catch (MessagingException messagingException){
            messagingException.printStackTrace();
        }
    }
}
