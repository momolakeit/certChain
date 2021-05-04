package com.momo.certChain.controller;

import com.momo.certChain.exception.ObjectNotFoundException;
import com.momo.certChain.exception.PasswordNotMatchingException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public abstract class BaseController {
    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(ObjectNotFoundException exception){
        return new ResponseEntity<Object>(exception.getMessage(),new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PasswordNotMatchingException.class)
    public ResponseEntity<Object> handleNotFoundException(PasswordNotMatchingException exception){
        return new ResponseEntity<Object>(exception.getMessage(),new HttpHeaders(), HttpStatus.UNAUTHORIZED);
    }

}
