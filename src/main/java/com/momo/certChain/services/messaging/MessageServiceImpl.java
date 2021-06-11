package com.momo.certChain.services.messaging;

import com.momo.certChain.model.data.HumanUser;
import com.momo.certChain.model.data.Institution;
import org.jetbrains.annotations.NotNull;
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
@Profile({"!test & !local"})
public class MessageServiceImpl implements MessageService {

    @Value("${front-end-url}")
    private String frontEndUrl;

    private final JavaMailSender javaMailSender;

    private final Logger LOGGER = Logger.getLogger(MessageServiceImpl.class.getName());

    public MessageServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendUserCreatedEmail(HumanUser humanUser, String password) throws MessagingException, IOException {
        String from = getFromField(humanUser);
        String subject = "Receive your diploma !";
        String to = humanUser.getUsername();
        String contentType = "text/html";

        String content = Files.readString(Path.of("./src/main/resources/templates/humanUserEmailTemplate/userCreated.html"), StandardCharsets.UTF_8);
        String text  = replaceValuesForUserCreated(humanUser,  password, content);
        sendTextEmail(from, subject, to, text, contentType);
    }

    public void sendCertificatePrivateKey(HumanUser humanUser, String encKey) throws MessagingException, IOException {
        String from = getFromField(humanUser);
        String subject = "Here is your diploma !";
        String to = humanUser.getUsername();
        String contentType = "text/html";

        String content = Files.readString(Path.of("./src/main/resources/templates/humanUserEmailTemplate/certificateKey.html"), StandardCharsets.UTF_8);
        String text  = replaceValuesCertificatePrivateKey(humanUser,  encKey, content);
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


    private String replaceValuesForUserCreated(HumanUser humanUser, String password, String content) {
        String text;
        text = content.replace("${studentPassword}", password);
        text = replaceValues(humanUser, text);
        return text;
    }

    private String replaceValuesCertificatePrivateKey(HumanUser humanUser, String password, String content) {
        String text;
        text = content.replace("${privateKey}", password);
        text = replaceValues(humanUser, text);
        return text;
    }

    private String replaceValues(HumanUser humanUser, String text) {
        text = text.replace("${studentName}", humanUser.getPrenom() + " " + humanUser.getNom());
        text = text.replace("${linkUrl}", frontEndUrl + "logIn.com.");
        return text;
    }

    private String getFromField(HumanUser humanUser) {
        String from = "certChain@" + humanUser.getInstitution().getName() + ".com";
        from = from.replaceAll("\\s", "-");
        return from;
    }
}
