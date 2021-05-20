package com.momo.certChain.controller;

import com.momo.certChain.exception.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public abstract class BaseController {
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Object> handleNotFoundException(ValidationException exception){
        return new ResponseEntity<Object>(exception.getMessage(),new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<Object> handleNotFoundException(AuthorizationException exception){
        return new ResponseEntity<Object>(exception.getMessage(),new HttpHeaders(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleNotFoundException(Exception exception){
        return new ResponseEntity<Object>(exception.getMessage(),new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
