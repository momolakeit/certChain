package com.momo.certChain.services.messaging;

import com.momo.certChain.model.data.HumanUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
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

    public void sendEmail(HumanUser humanUser,String privateKey){
        String from =  "certChain@"+humanUser.getInstitution().getName()+".com";

        try{
            MimeMessage message = javaMailSender.createMimeMessage();

            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO,new InternetAddress(humanUser.getUsername()));
            message.setSubject("Receive your diploma !");
            message.setText(frontEndUrl+"createPassword/"+humanUser.getId()+".com"+
                            "This is the password, save it so you can retreive your diploma:"+privateKey);

            javaMailSender.send(message);
            LOGGER.info("EMAIL WAS SENT SUCCESSFULLY!");
        }catch (MessagingException messagingException){
            messagingException.printStackTrace();
        }
    }
}
