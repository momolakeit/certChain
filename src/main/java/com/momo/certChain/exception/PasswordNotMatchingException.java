package com.momo.certChain.exception;

public class PasswordNotMatchingException extends RuntimeException{
    public PasswordNotMatchingException() {
        super("Password not matching");
    }
}
