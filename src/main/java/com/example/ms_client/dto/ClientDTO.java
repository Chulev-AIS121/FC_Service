package com.example.ms_client.dto;

import java.util.UUID;

public class ClientDTO {

    private UUID id;
    private String fullName;
    private String shortName;
    private String inn;
    private boolean active;
    private String createDateTime;
    private String updateDateTime;
    private String clientType;

    public ClientDTO() {}

    public ClientDTO(UUID id, String fullName, String shortName, String inn,
                     boolean active, String createDateTime, String updateDateTime, String clientType) {
        this.id = id;
        this.fullName = fullName;
        this.shortName = shortName;
        this.inn = inn;
        this.active = active;
        this.createDateTime = createDateTime;
        this.updateDateTime = updateDateTime;
        this.clientType = clientType;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getCreateDateTime() {
        return createDateTime;
    }

    public void setCreateDateTime(String createDateTime) {
        this.createDateTime = createDateTime;
    }

    public String getUpdateDateTime() {
        return updateDateTime;
    }

    public void setUpdateDateTime(String updateDateTime) {
        this.updateDateTime = updateDateTime;
    }

    public String getClientType() { return clientType; }

    public void setClientType(String clientType) { this.clientType = clientType; }

}