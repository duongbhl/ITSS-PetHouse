package org.example.petproject.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Service")
public class Service {
    @Id
    private String serviceId;

    private String name;
    private Double price;

    @Enumerated(EnumType.STRING)
    private Type type;

    public enum Type {
        luu_tru, lam_dep_va_ve_sinh
    }

    // Getters, setters, and constructors

    public Service(String serviceId, Type type, Double price, String name) {
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}