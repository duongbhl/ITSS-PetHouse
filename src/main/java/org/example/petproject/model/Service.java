package org.example.petproject.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "Service")
public class Service {
    @Id
    private String serviceId;

    private String name;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceType type;

    // Constructors
    public Service() {
    }

    public Service(String serviceId, String name, BigDecimal price, ServiceType type) {
        this.serviceId = serviceId;
        this.name = name;
        this.price = price;
        this.type = type;
    }

    // Getters and setters
    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public ServiceType getType() {
        return type;
    }

    public void setType(ServiceType type) {
        this.type = type;
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

    public enum ServiceType {
        BOARDING("Lưu trú"),
        GROOMING("Làm đẹp & Vệ sinh");

        private final String label;

        ServiceType(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }
}
