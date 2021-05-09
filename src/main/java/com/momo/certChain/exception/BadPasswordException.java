package com.momo.certChain.exception;

public class BadPasswordException extends AuthorizationException {
    public BadPasswordException() {
        super("Mauvais mot de passe");
    }
}
