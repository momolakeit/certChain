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
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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

    public void sendEmailToHumanUser(HumanUser humanUser, String privateKey, String password) throws MessagingException, IOException {
        String from = "certChain@" + humanUser.getInstitution().getName() + ".com";
        from = from.replaceAll("\\s","-");
        String subject = "Receive your diploma !";
        String to = humanUser.getUsername();
        String contentType = "text/html";

        String content = Files.readString(Path.of("./src/main/resources/templates/humanUserEmailTemplate/bussy.html"), StandardCharsets.UTF_8);
        String text  = replaceValues(humanUser, privateKey, password, content);
        sendTextEmail(from, subject, to, text, contentType);
    }


    //on send un email a moi , le maitre du trucs pour que je review et approuved le institutions

    public void sendApprouvalEmail(Institution institution) throws MessagingException {
        String from = "certChain@institutionValidation.com";


        String subject = "Validate institution";
        //todo utiliser le email de l'admin en le cherchant dans le adminService
        String to = "adminEmail";
        String text = "L'institution " + institution.getName() + "a déposé une demande d'approbation";
        String contentType = "text/plain";

        sendTextEmail(from, subject, to, text, contentType);
    }
    private void sendTextEmail(String from, String subject, String to, String text, String contentType) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();

        message.setFrom(new InternetAddress(from));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject(subject);
        message.setContent(text, contentType);

        javaMailSender.send(message);

        LOGGER.info("EMAIL WAS SENT SUCCESSFULLY!");
    }



    private String replaceValues(HumanUser humanUser, String privateKey, String password, String content) {
        String text;
        text = content.replace("${studentName}", humanUser.getPrenom() + " " + humanUser.getNom());
        text = text.replace("${studentPassword}", password);
        text = text.replace("${certificateKey}", privateKey);
        text = text.replace("${linkUrl}", frontEndUrl + "logIn.com.");
        return text;
    }
}
