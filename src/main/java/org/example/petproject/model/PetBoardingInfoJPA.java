package org.example.petproject.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pet_boarding_info")
public class PetBoardingInfoJPA {

    @Id
    private String infoId;

    @Column(name = "check_in_date")
    private LocalDateTime checkInDate;

    @Column(name = "check_out_date")
    private LocalDateTime checkOutDate;

    @Column(name = "status")
    private String status;

    @Column(name = "price")
    private Double price;

    @Column(name = "note")
    private String note;

    // Relationships
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pet_id")
    private Pet pet;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "boarding_id")
    private PetBoarding petBoarding;

    @PrePersist
    public void generateId() {
        if (this.infoId == null) {
            this.infoId = UUID.randomUUID().toString();
        }
    }

    // Constructors
    public PetBoardingInfoJPA() {}

    public PetBoardingInfoJPA(LocalDateTime checkInDate, LocalDateTime checkOutDate,
                              String status, Double price, String note,
                              Pet pet, PetBoarding petBoarding) {
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.status = status;
        this.price = price;
        this.note = note;
        this.pet = pet;
        this.petBoarding = petBoarding;
    }

    // Getters and setters
    public String getInfoId() {
        return infoId;
    }

    public void setInfoId(String infoId) {
        this.infoId = infoId;
    }

    public LocalDateTime getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDateTime checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDateTime getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDateTime checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public PetBoarding getPetBoarding() {
        return petBoarding;
    }

    public void setPetBoarding(PetBoarding petBoarding) {
        this.petBoarding = petBoarding;
    }

    // Convenience methods for getting related information
    public String getPetName() {
        return pet != null ? pet.getName() : null;
    }

    public String getRoomName() {
        return petBoarding != null && petBoarding.getRoom() != null ?
                petBoarding.getRoom().getName() : null;
    }

    public Room.Type getRoomType() {
        return petBoarding != null && petBoarding.getRoom() != null ?
                petBoarding.getRoom().getType() : null;
    }

    public String getBookingId() {
        return petBoarding != null && petBoarding.getBooking() != null ?
                petBoarding.getBooking().getBookingId() : null;
    }

    public Service.Type getServiceType() {
        return petBoarding != null && petBoarding.getBooking() != null &&
                petBoarding.getBooking().getService() != null ?
                petBoarding.getBooking().getService().getType() : null;
    }
}