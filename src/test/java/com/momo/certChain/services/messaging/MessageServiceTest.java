package com.momo.certChain.services.messaging;

import com.momo.certChain.TestUtils;
import com.momo.certChain.model.data.Institution;
import com.momo.certChain.model.data.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import javax.mail.*;
import javax.mail.internet.MimeMessage;

import java.io.IOException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @InjectMocks
    private MessageService messageService;

    @Mock
    private JavaMailSender javaMailSender;

    @Captor
    private ArgumentCaptor<MimeMessage> messageArgumentCaptor;

    private String frontEndUrl = "http:localhost:9000/";

    @BeforeEach
    public void init(){
        ReflectionTestUtils.setField(messageService,"frontEndUrl",frontEndUrl);
    }


    @Test
    public void sendMessage() throws MessagingException, IOException {
        Student student = TestUtils.createStudent();
        student.setPrenom("prenom");
        student.setNom("nom");
        student.setId("123456");
        String privateKey ="superPrivate";
        String password = "password";
        String messageString = frontEndUrl + "/logIn.com.Veuillez vous connecter avec votre courriel et le ce mot de passe:"+password+
                "This is the password, save it so you can retreive your diploma:" + privateKey;
        String from =  "certChain@"+student.getInstitution().getName()+".com";

        when(javaMailSender.createMimeMessage()).thenReturn(createMimeMessage());
        messageService.sendEmailToHumanUser(student,privateKey,password);
        verify(javaMailSender).send(messageArgumentCaptor.capture());

        Message message = messageArgumentCaptor.getValue();
        Address address = message.getRecipients(Message.RecipientType.TO)[0];
        assertEquals("Receive your diploma !",message.getSubject());
        assertEquals(student.getUsername(),address.toString());
        assertEquals(from,message.getFrom()[0].toString());
        assertEquals(from,message.getFrom()[0].toString());
        assertTrue(String.valueOf(message.getContent()).contains(student.getNom()));
        assertTrue(String.valueOf(message.getContent()).contains(student.getPrenom()));
        assertTrue(String.valueOf(message.getContent()).contains(privateKey));
        assertTrue(String.valueOf(message.getContent()).contains(frontEndUrl+"logIn.com."));
    }

    @Test
    public void sendApprouvalMessage() throws MessagingException, IOException {
        Institution institution = TestUtils.createInstitution();
        String messageString ="L'institution " + institution.getName() + "a déposé une demande d'approbation";

        when(javaMailSender.createMimeMessage()).thenReturn(createMimeMessage());
        messageService.sendApprouvalEmail(institution);
        verify(javaMailSender).send(messageArgumentCaptor.capture());

        Message message = messageArgumentCaptor.getValue();
        Address address = message.getRecipients(Message.RecipientType.TO)[0];
        assertEquals("Validate institution",message.getSubject());
        assertEquals("adminEmail",address.toString());
        assertEquals(messageString,String.valueOf(message.getContent()));

    }

    private MimeMessage createMimeMessage(){
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host","localhost");
        return new MimeMessage(Session.getDefaultInstance(properties));
    }


}