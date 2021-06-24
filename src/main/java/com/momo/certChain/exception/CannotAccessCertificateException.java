package com.momo.certChain.exception;

public class CannotAccessCertificateException extends AuthorizationException {
    public CannotAccessCertificateException() {
        super("Vous ne pouvez pas effectuer cette action sur le certificat car vous n'êtes pas le propriataire");
    }
}
