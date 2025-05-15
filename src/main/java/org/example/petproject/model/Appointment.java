package org.example.petproject.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Appointment")
public class Appointment {
    @Id
    private String appointmentId;

    @ManyToOne
    @JoinColumn(name = "pet_id")
    private Pet pet;

    private LocalDateTime appointmentTime;
    private String type;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String note;

    @ManyToOne
    @JoinColumn(name = "confirmed_by")
    private User confirmedBy;

    private LocalDateTime confirmedAt;

    public enum Status {
        pending, confirmed, completed, cancelled
    }

    // Getters, setters, and constructors

    public Appointment(String appointmentId, Pet pet, LocalDateTime appointmentTime, String type, Status status,
            String note, User confirmedBy, LocalDateTime confirmedAt) {
        this.appointmentId = appointmentId;
        this.pet = pet;
        this.appointmentTime = appointmentTime;
        this.type = type;
        this.status = status;
        this.note = note;
        this.confirmedBy = confirmedBy;
        this.confirmedAt = confirmedAt;
    }

    public Appointment() {
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public User getConfirmedBy() {
        return confirmedBy;
    }

    public void setConfirmedBy(User confirmedBy) {
        this.confirmedBy = confirmedBy;
    }

    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(LocalDateTime confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "appointmentId='" + appointmentId + '\'' +
                ", pet=" + pet +
                ", appointmentTime=" + appointmentTime +
                ", type='" + type + '\'' +
                ", status=" + status +
                ", note='" + note + '\'' +
                ", confirmedBy=" + confirmedBy +
                ", confirmedAt=" + confirmedAt +
                '}';
    }
}
