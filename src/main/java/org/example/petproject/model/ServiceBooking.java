package org.example.petproject.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ServiceBooking")
public class ServiceBooking {
    @Id
    private String bookingId;

    @ManyToOne
    @JoinColumn(name = "pet_id")
    private Pet pet;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private Service service;

    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;

    @Enumerated(EnumType.STRING)
    private Status status = Status.pending;

    private String note;

    @ManyToOne
    @JoinColumn(name = "handled_by")
    private User handledBy;

    public enum Status {
        pending, in_progress, done
    }

    // Getters, setters, and constructors

    public ServiceBooking(String bookingId, Pet pet, Service service, LocalDateTime checkInTime, LocalDateTime checkOutTime, Status status, String note, User handledBy) {
        this.bookingId = bookingId;
        this.pet = pet;
        this.service = service;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.status = status;
        this.note = note;
        this.handledBy = handledBy;
    }

    public ServiceBooking() {}

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(LocalDateTime checkInTime) {
        this.checkInTime = checkInTime;
    }

    public LocalDateTime getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(LocalDateTime checkOutTime) {
        this.checkOutTime = checkOutTime;
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

    public User getHandledBy() {
        return handledBy;
    }

    public void setHandledBy(User handledBy) {
        this.handledBy = handledBy;
    }

    @Override
    public String toString() {
        return "ServiceBooking{" +
                "bookingId='" + bookingId + '\'' +
                ", pet=" + pet +
                ", org.example.petproject.service=" + service +
                ", checkInTime=" + checkInTime +
                ", checkOutTime=" + checkOutTime +
                ", status=" + status +
                ", note='" + note + '\'' +
                ", handledBy=" + handledBy +
                '}';
    }
}
