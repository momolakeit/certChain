package com.momo.certChain.services.messaging;

import com.momo.certChain.TestUtils;
import com.momo.certChain.model.data.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.testcontainers.shaded.org.bouncycastle.cms.Recipient;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @InjectMocks
    private MessageService messageService;

    @Captor
    private ArgumentCaptor<Message> messageArgumentCaptor;

    private String frontEndUrl = "http:localhost:9000/";

    @BeforeEach
    public void init(){
        ReflectionTestUtils.setField(messageService,"frontEndUrl",frontEndUrl);
    }

    @Test
    public void sendMessage() throws MessagingException, IOException {
        Student student = TestUtils.createStudent();
        student.setId("123456");
        String link = frontEndUrl +"createPassword/" +student.getId()+".com";
        String from =  "certChain@"+student.getInstitution().getName()+".com";
        MockedStatic<Transport> transportMockedStatic = mockStatic(Transport.class);
        messageService.sendEmail(student);
        transportMockedStatic.verify(()->Transport.send(messageArgumentCaptor.capture()));
        Message message = messageArgumentCaptor.getValue();
        Address address = message.getRecipients(Message.RecipientType.TO)[0];
        assertEquals("Receive your diploma !",message.getSubject());
        assertEquals(student.getUsername(),address.toString());
        assertEquals(from,message.getFrom()[0].toString());
        assertEquals(from,message.getFrom()[0].toString());
        assertEquals(link,String.valueOf(message.getContent()));
        transportMockedStatic.closeOnDemand();
    }

}