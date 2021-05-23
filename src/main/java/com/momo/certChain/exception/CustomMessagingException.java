package com.momo.certChain.exception;

public class CustomMessagingException extends RuntimeException{
    public CustomMessagingException() {
        super("Erreur lors de l'envoie de email");
    }
}
