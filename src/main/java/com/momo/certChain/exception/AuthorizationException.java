package com.momo.certChain.exception;

public class AuthorizationException extends RuntimeException {
    public AuthorizationException(String msg){
        super(msg);
    }
}
