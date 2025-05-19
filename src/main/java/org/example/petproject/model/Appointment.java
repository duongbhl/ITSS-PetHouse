package org.example.petproject.model;

import jakarta.persistence.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.example.petproject.dao.PetDAO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Appointment")
public class Appointment {
    @Id
    private String appointmentId;

    @PrePersist
    public void generateId() {
        if (this.appointmentId == null) {
            this.appointmentId = UUID.randomUUID().toString();
        }
    }

    @ManyToOne
    @JoinColumn(name = "pet_id")
    private Pet pet;

    private LocalDate appointmentTime;
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

    @Transient // Mark as non-persistent
    private BooleanProperty selected = new SimpleBooleanProperty(false);

    // Getters, setters, and constructors

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public boolean isSelected() {
        return selected.get();
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    public Appointment(String appointmentId, Pet pet, LocalDate appointmentTime, String type, Status status,
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

    public Appointment(String pet_name, LocalDate appointmentTime, String type, Status status) {
        this.pet = new PetDAO().findpetbyName(pet_name);
        this.appointmentTime = appointmentTime;
        this.type = type;
        this.status = status;
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

    public LocalDate getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalDate appointmentTime) {
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

}