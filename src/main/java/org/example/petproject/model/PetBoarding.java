package org.example.petproject.model;

import jakarta.persistence.*;
import org.example.petproject.dao.RoomDAO;
import java.util.UUID;

@Entity
@Table(name = "PetBoarding")
public class PetBoarding {
    @Id
    private String boardingId;

    @PrePersist
    public void generateId() {
        if (this.boardingId == null) {
            this.boardingId = UUID.randomUUID().toString();
        }
    }

    @OneToOne
    @JoinColumn(name = "booking_id")
    private ServiceBooking booking;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    // Getters, setters, and constructors

    public PetBoarding(String boardingId, ServiceBooking booking, Room room) {
        this.boardingId = boardingId;
        this.booking = booking;
        this.room = room;
    }

    public PetBoarding() {
    }

    public PetBoarding(ServiceBooking serviceBooking, String roomname) {
        this.booking = serviceBooking;
        this.room = new RoomDAO().findByName(roomname);
    }

    public String getBoardingId() {
        return boardingId;
    }

    public void setBoardingId(String boardingId) {
        this.boardingId = boardingId;
    }

    public ServiceBooking getBooking() {
        return booking;
    }

    public void setBooking(ServiceBooking booking) {
        this.booking = booking;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

}