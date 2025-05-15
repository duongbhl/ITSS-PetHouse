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
    private Type type;

    private BigDecimal pricePerDay;

    public enum Type {
        thuong, vip
    }

    // Getters, setters, and constructors

    public Room(String roomId, String name, Type type, BigDecimal pricePerDay) {
        this.roomId = roomId;
        this.name = name;
        this.type = type;
        this.pricePerDay = pricePerDay;
    }

    public Room() {
    }

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

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
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
}
