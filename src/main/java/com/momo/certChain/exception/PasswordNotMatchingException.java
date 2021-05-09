package com.momo.certChain.exception;

public class PasswordNotMatchingException extends AuthorizationException{
    public PasswordNotMatchingException() {
        super("Password not matching");
    }
}
