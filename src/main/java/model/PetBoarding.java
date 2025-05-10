package model;

import jakarta.persistence.*;

@Entity
@Table(name = "PetBoarding")
public class PetBoarding {
    @Id
    private String boardingId;

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
    public PetBoarding() {}

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

    @Override
    public String toString() {
        return "PetBoarding{" +
                "boardingId='" + boardingId + '\'' +
                ", booking=" + booking +
                ", room=" + room +
                '}';
    }
}
