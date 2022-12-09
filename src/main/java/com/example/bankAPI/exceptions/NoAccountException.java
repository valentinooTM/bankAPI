package com.example.bankAPI.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoAccountException extends RuntimeException {
    public NoAccountException(String errorMSG){
        super(errorMSG);
    }
}
