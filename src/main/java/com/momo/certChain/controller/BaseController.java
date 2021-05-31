package com.momo.certChain.controller;

import com.momo.certChain.exception.*;
import com.momo.certChain.services.messaging.MessageService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.logging.Logger;

public abstract class BaseController {
    private final Logger LOGGER = Logger.getLogger(BaseController.class.getName());
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Object> handleNotFoundException(ValidationException exception){
        exception.printStackTrace();
        return new ResponseEntity<Object>(exception.getMessage(),new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<Object> handleNotFoundException(AuthorizationException exception){
        exception.printStackTrace();
        return new ResponseEntity<Object>(exception.getMessage(),new HttpHeaders(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleNotFoundException(Exception exception){
        exception.printStackTrace();
        return new ResponseEntity<Object>(exception.getMessage(),new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
