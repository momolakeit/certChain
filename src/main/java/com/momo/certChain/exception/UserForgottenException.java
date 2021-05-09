package com.momo.certChain.exception;

public class UserForgottenException extends ValidationException{
    public UserForgottenException() {
        super("L'utilisateur a fait le choix d'être oublié");
    }
}
