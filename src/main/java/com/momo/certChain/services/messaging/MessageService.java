package com.momo.certChain.services.messaging;

import com.momo.certChain.model.data.HumanUser;
import com.momo.certChain.model.data.Institution;
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

    public void sendEmailToHumanUser(HumanUser humanUser, String privateKey) throws MessagingException {
        String from = "certChain@" + humanUser.getInstitution().getName() + ".com";

        String subject = "Receive your diploma !";
        String to = humanUser.getUsername();
        String text = frontEndUrl + "createPassword/" + humanUser.getId() + ".com" +
                "This is the password, save it so you can retreive your diploma:" + privateKey;

        sendEmail(from, subject, to, text);
    }


    //on send un email a moi , le maitre du trucs pour que je review et approuved le institutions
    public void sendApprouvalEmail(Institution institution) throws MessagingException {
        String from = "certChain@institutionValidation.com";


        String subject = "Validate institution";
        //todo utiliser le email de l'admin en le cherchant dans le adminService
        String to = "adminEmail";
        String text = "L'institution " + institution.getName() + "a déposé une demande d'approbation";

        sendEmail(from, subject, to, text);
    }

    private void sendEmail(String from, String subject, String to, String text) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();

        message.setFrom(new InternetAddress(from));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject(subject);
        message.setText(text);

        javaMailSender.send(message);

        LOGGER.info("EMAIL WAS SENT SUCCESSFULLY!");
    }
}
