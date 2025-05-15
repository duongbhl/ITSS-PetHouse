package org.example.petproject.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "Service")
public class Service {
    @Id
    private String serviceId;

    private String name;
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private Type type;

    public enum Type {
        lưu_trú, làm_đẹp_vệ_sinh
    }

    // Getters, setters, and constructors

    public Service(String serviceId, Type type, BigDecimal price, String name) {
        this.serviceId = serviceId;
        this.type = type;
        this.price = price;
        this.name = name;
    }

    public Service() {
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Service{" +
                "serviceId='" + serviceId + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", type=" + type +
                '}';
    }
}
