package com.momo.certChain.services.messaging;

import com.momo.certChain.model.data.HumanUser;
import com.momo.certChain.model.data.Institution;

import javax.mail.MessagingException;
import java.io.IOException;

public interface MessageService {
    public void sendEmailToHumanUser(HumanUser humanUser, String privateKey, String password) throws MessagingException, IOException;

    public void sendApprouvalEmail(Institution institution) throws MessagingException;
}
