package com.example.ms_client.exception;

import java.util.UUID;

public class ClientNotFoundException extends RuntimeException {
    public ClientNotFoundException(UUID id) {
        super("Клиент с ID " + id + " не найден.");
    }
}
