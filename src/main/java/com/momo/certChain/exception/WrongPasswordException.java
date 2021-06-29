package com.momo.certChain.exception;

public class WrongPasswordException extends ValidationException{
    public WrongPasswordException(String message) {
        super(message);
    }
}
