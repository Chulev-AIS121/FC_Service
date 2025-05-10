package com.example.ms_client.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

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

    @JsonCreator
    public static ClientType fromString(String value) {
        for (ClientType type : ClientType.values()) {
            if (type.name().equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown enum value: " + value);
    }

    @JsonValue
    public String toJson() {
        return this.name();
    }
}
