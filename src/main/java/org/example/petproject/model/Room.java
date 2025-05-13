package org.example.petproject.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "Room")
public class Room {
    @Id
    private String roomId;

    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomType type;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerDay;

    // Constructors
    public Room() {
    }

    public Room(String roomId, String name, RoomType type, BigDecimal pricePerDay) {
        this.roomId = roomId;
        this.name = name;
        this.type = type;
        this.pricePerDay = pricePerDay;
    }

    // Getters and setters
    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RoomType getType() {
        return type;
    }

    public void setType(RoomType type) {
        this.type = type;
    }

    public BigDecimal getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(BigDecimal pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    @Override
    public String toString() {
        return "Room{" +
                "roomId='" + roomId + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", pricePerDay=" + pricePerDay +
                '}';
    }

    public enum RoomType {
        STANDARD("Thường"),
        VIP("VIP");

        private final String label;

        RoomType(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }
}
