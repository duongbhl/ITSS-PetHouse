package org.example.petproject.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "MedicalRecord")
public class MedicalRecord {
    @Id
    private String recordId;

    @PrePersist
    public void generateId() {
        if (this.recordId == null) {
            this.recordId = UUID.randomUUID().toString();
        }
    }

    @ManyToOne
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @ManyToOne
    @JoinColumn(name = "pet_id")
    private Pet pet;

    private LocalDate examDate;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private User doctor;

    private String type;
    private String symptoms;
    private String diagnosis;
    private String prescription;
    private String treatment;
    private LocalDate followUpDate;
    private String note;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Getters, setters, and constructors

    public MedicalRecord(String recordId, Appointment appointment, Pet pet, LocalDate examDate, User doctor,
            String type, String symptoms, String diagnosis, String prescription, String treatment,
            LocalDate followUpDate, String note, LocalDateTime createdAt) {
        this.recordId = recordId;
        this.appointment = appointment;
        this.pet = pet;
        this.examDate = examDate;
        this.doctor = doctor;
        this.type = type;
        this.symptoms = symptoms;
        this.diagnosis = diagnosis;
        this.prescription = prescription;
        this.treatment = treatment;
        this.followUpDate = followUpDate;
        this.note = note;
        this.createdAt = createdAt;
    }

    public MedicalRecord() {
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public LocalDate getExamDate() {
        return examDate;
    }

    public void setExamDate(LocalDate examDate) {
        this.examDate = examDate;
    }

    public User getDoctor() {
        return doctor;
    }

    public void setDoctor(User doctor) {
        this.doctor = doctor;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getPrescription() {
        return prescription;
    }

    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public LocalDate getFollowUpDate() {
        return followUpDate;
    }

    public void setFollowUpDate(LocalDate followUpDate) {
        this.followUpDate = followUpDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}