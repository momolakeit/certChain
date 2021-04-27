package com.momo.certChain.services.messaging;

import com.momo.certChain.model.data.HumanUser;
import com.momo.certChain.model.data.Student;
import jnr.ffi.annotations.In;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class MessageService {

    @Value("${front-end-url}")
    private String frontEndUrl;

    public void sendEmail(HumanUser humanUser){
        String from =  "certChain@"+humanUser.getInstitution().getName()+".com";
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host","localhost");
        Session session = Session.getDefaultInstance(properties);
        try{
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO,new InternetAddress(humanUser.getUsername()));
            message.setSubject("Receive your diploma !");
            message.setText(frontEndUrl+"createPassword/"+humanUser.getId()+".com");
            Transport.send(message);
        }catch (MessagingException messagingException){
            messagingException.printStackTrace();
        }
    }
}
