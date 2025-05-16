package org.example.petproject.model;

import java.time.LocalDate;

public class NotificationDto {
    private final LocalDate dateTime;
    private final String petName;
    private final String message;

    public NotificationDto(LocalDate dateTime, String petName, String message) {
        this.dateTime = dateTime;
        this.petName = petName;
        this.message = message;
    }

    public LocalDate getDateTime() { return dateTime; }
    public String getPetName()    { return petName; }
    public String getMessage()    { return message; }
}
