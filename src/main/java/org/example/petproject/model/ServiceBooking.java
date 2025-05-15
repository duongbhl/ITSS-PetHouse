package org.example.petproject.model;

import jakarta.persistence.*;
import org.example.petproject.dao.PetDAO;
import org.example.petproject.dao.ServiceDAO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ServiceBooking")
public class ServiceBooking {
    @Id
    private String bookingId;
    @PrePersist
    public void generateId() {
        if (this.bookingId == null) {
            this.bookingId = UUID.randomUUID().toString();
        }
    }

    @ManyToOne
    @JoinColumn(name = "pet_id")
    private Pet pet;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private Service service;

    private LocalDate checkInTime;
    private LocalDate checkOutTime;

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

    public ServiceBooking(String bookingId, Pet pet, Service service, LocalDate checkInTime, LocalDate checkOutTime, Status status, String note, User handledBy) {
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

    public ServiceBooking(LocalDate checkInTime, String note,Status status,String petname,String service_id) {
        this.checkInTime = checkInTime;
        this.note = note;
        this.status = status;
        this.pet = new PetDAO().findpetbyName(petname);
        this.service = new ServiceDAO().findservicebyID(service_id);
    }

    public ServiceBooking(LocalDate checkInTime,LocalDate checkOutTime ,String note, ServiceBooking.Status status, String petname, String service_id){
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.note = note;
        this.status = status;
        this.pet = new PetDAO().findpetbyName(petname);
        this.service = new ServiceDAO().findservicebyID(service_id);
    }

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

    public LocalDate getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(LocalDate checkInTime) {
        this.checkInTime = checkInTime;
    }

    public LocalDate getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(LocalDate checkOutTime) {
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
