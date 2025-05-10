package com.example.ms_client.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Invalid client data")
public class InvalidClientDataException extends RuntimeException {
    public InvalidClientDataException(String message) {
        super(message);
    }
}
