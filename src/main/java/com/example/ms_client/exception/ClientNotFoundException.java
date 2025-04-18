package com.example.ms_client.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Client not found")
public class ClientNotFoundException extends RuntimeException {
    public ClientNotFoundException(UUID id) {
        super("Client with id " + id + " not found");
    }
}
