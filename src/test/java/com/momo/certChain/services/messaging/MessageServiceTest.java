package com.momo.certChain.services.messaging;

import com.momo.certChain.TestUtils;
import com.momo.certChain.model.data.Student;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.org.bouncycastle.cms.Recipient;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @InjectMocks
    private MessageService messageService;

    @Captor
    private ArgumentCaptor<Message> messageArgumentCaptor;

    @Test
    public void sendMessage() throws MessagingException {
        String link="clickHere";
        Student student = TestUtils.createStudent();
        String from =  "certChain@"+student.getInstitution().getName()+".com";
        MockedStatic<Transport> transportMockedStatic = mockStatic(Transport.class);
        messageService.sendEmail(student,link);
        transportMockedStatic.verify(()->Transport.send(messageArgumentCaptor.capture()));
        Message message = messageArgumentCaptor.getValue();
        Address address = message.getRecipients(Message.RecipientType.TO)[0];
        assertEquals("Receive your diploma !",message.getSubject());
        assertEquals(student.getUsername(),address.toString());
        assertEquals(from,message.getFrom()[0].toString());
    }

}