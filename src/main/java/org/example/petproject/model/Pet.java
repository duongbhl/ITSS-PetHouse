package org.example.petproject.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "Pet")
public class Pet {
    @Id
    @Column(columnDefinition = "varchar(36)")
    private String petId;

    @PrePersist
    public void generateId() {
        if (this.petId == null) {
            this.petId = UUID.randomUUID().toString();
        }
    }

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    private String name;
    private String species;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private LocalDate birthDate;
    private Float weight;
    private String diet;
    private String healthNotes;

    public enum Gender {
        male("Đực"),
        female("Cái");

        private final String label;

        Gender(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    @Column(name = "photo_url")
    private String photoUrl;

    // Getters, setters, and constructors
    public Pet() {
    }

    public Pet(String petId, User owner, String name, String species, Gender gender, LocalDate birthDate, Float weight,
            String diet, String healthNotes) {
        this.petId = petId;
        this.owner = owner;
        this.name = name;
        this.species = species;
        this.gender = gender;
        this.birthDate = birthDate;
        this.weight = weight;
        this.diet = diet;
        this.healthNotes = healthNotes;
    }

    public String getPetId() {
        return petId;
    }

    public void setPetId(String petId) {
        this.petId = petId;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public String getDiet() {
        return diet;
    }

    public void setDiet(String diet) {
        this.diet = diet;
    }

    public String getHealthNotes() {
        return healthNotes;
    }

    public void setHealthNotes(String healthNotes) {
        this.healthNotes = healthNotes;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

}