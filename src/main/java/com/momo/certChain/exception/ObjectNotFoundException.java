package com.momo.certChain.exception;

public class ObjectNotFoundException extends ValidationException {
    public ObjectNotFoundException(String entity){
        super(entity+" not found");
    }
}
