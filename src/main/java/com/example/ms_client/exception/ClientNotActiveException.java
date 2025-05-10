package com.example.ms_client.exception;

import java.util.UUID;

public class ClientNotActiveException extends RuntimeException {
    public ClientNotActiveException(UUID id) {
        super("Client with ID " + id + " is not active and cannot be updated.");
    }
}