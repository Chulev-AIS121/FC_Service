package com.example.ms_client.entity;

public enum ClientType {
    IP("Индивидуальный предприниматель"),
    UL("Юридическое лицо");

    private final String description;

    // Конструктор
    ClientType(String description) {
        this.description = description;
    }

    // Геттер для description
    public String getDescription() {
        return description;
    }
}
