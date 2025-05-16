package org.example.petproject.model;

import java.time.LocalDateTime;

public class NotificationDto {
    private final LocalDateTime dateTime;
    private final String petName;
    private final String message;

    public NotificationDto(LocalDateTime dateTime, String petName, String message) {
        this.dateTime = dateTime;
        this.petName = petName;
        this.message = message;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getPetName() {
        return petName;
    }

    public String getMessage() {
        return message;
    }
}