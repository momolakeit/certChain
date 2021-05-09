package com.momo.certChain.exception;

public class WrongKeyException  extends ValidationException{
    public WrongKeyException() {
        super("Le certificat n'a pas pu etre décrypter , veuillez verifier l'URL");
    }
}
