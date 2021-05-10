package com.momo.certChain.exception;

public class CannotDeleteCertificateException extends AuthorizationException {
    public CannotDeleteCertificateException() {
        super("Vous n'êtes pas le propriataire de ce certificat");
    }
}
