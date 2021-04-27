package com.momo.certChain.services.messaging;

import com.momo.certChain.model.data.Student;
import jnr.ffi.annotations.In;
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

    public void sendEmail(Student student,String passwordSetUpLink){
        String from =  "certChain@"+student.getInstitution().getName()+".com";
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host","localhost");
        Session session = Session.getDefaultInstance(properties);
        try{
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO,new InternetAddress(student.getUsername()));
            message.setSubject("Receive your diploma !");
            message.setText(passwordSetUpLink);
            Transport.send(message);
        }catch (MessagingException messagingException){
            messagingException.printStackTrace();
        }
    }
}
