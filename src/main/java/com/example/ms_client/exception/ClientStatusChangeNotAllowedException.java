package com.example.ms_client.exception;

public class ClientStatusChangeNotAllowedException extends RuntimeException {
    public ClientStatusChangeNotAllowedException(String message) {
        super(message);
    }
}
