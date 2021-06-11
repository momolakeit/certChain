package com.momo.certChain.services.messaging;

import com.momo.certChain.model.data.HumanUser;
import com.momo.certChain.model.data.Institution;

import javax.mail.MessagingException;
import java.io.IOException;

public interface MessageService {
    public void sendUserCreatedEmail(HumanUser humanUser, String password) throws MessagingException, IOException;

    public void sendCertificatePrivateKey(HumanUser humanUser, String encKey) throws MessagingException, IOException;

    public void sendApprouvalEmail(Institution institution) throws MessagingException;
}
