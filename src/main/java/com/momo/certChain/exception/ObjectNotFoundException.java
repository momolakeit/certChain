package com.momo.certChain.exception;

public class ObjectNotFoundException extends RuntimeException {
    public ObjectNotFoundException(String entity){
        super(entity+" not found");
    }
}
