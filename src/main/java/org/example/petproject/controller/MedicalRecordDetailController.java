package org.example.petproject.controller;

import org.example.petproject.dao.PetDAO;
import org.example.petproject.dao.UserDAO;
import org.example.petproject.model.MedicalRecord;
import org.example.petproject.model.Pet;
import org.example.petproject.model.User;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MedicalRecordDetailController {
    @FXML private Label petNameLabel;
    @FXML private Label ownerNameLabel;
    @FXML private Label speciesLabel;
    @FXML private Label genderLabel;
    @FXML private Label birthDateLabel;
    @FXML private Label weightLabel;
    @FXML private Label healthNotesLabel;
    @FXML private Label typeLabel;
    @FXML private Label examDateLabel;
    @FXML private Label symptomsLabel;
    @FXML private Label diagnosisLabel;
    @FXML private Label prescriptionLabel;
    @FXML private Label treatmentLabel;
    @FXML private Label followUpDateLabel;
    @FXML private Label noteLabel;

    private final PetDAO petDAO = new PetDAO();
    private final UserDAO userDAO = new UserDAO();

    public void setMedicalRecord(MedicalRecord record) {
        if (record == null) return;
        // Medical record info
        typeLabel.setText(record.getType() != null ? record.getType() : "");
        examDateLabel.setText(record.getExamDate() != null ? record.getExamDate().toString() : "");
        symptomsLabel.setText(record.getSymptoms() != null ? record.getSymptoms() : "");
        diagnosisLabel.setText(record.getDiagnosis() != null ? record.getDiagnosis() : "");
        prescriptionLabel.setText(record.getPrescription() != null ? record.getPrescription() : "");
        treatmentLabel.setText(record.getTreatment() != null ? record.getTreatment() : "");
        followUpDateLabel.setText(record.getFollowUpDate() != null ? record.getFollowUpDate().toString() : "");
        noteLabel.setText(record.getNote() != null ? record.getNote() : "");

        // Pet info
        Pet pet = record.getPet();
        if (pet == null && record.getAppointment() != null) {
            pet = record.getAppointment().getPet();
        }
        if (pet != null) {
            petNameLabel.setText(pet.getName() != null ? pet.getName() : "");
            speciesLabel.setText(pet.getSpecies() != null ? pet.getSpecies() : "");
            genderLabel.setText(pet.getGender() != null ? pet.getGender().getLabel() : "");
            birthDateLabel.setText(pet.getBirthDate() != null ? pet.getBirthDate().toString() : "");
            weightLabel.setText(pet.getWeight() != null ? pet.getWeight().toString() : "");
            healthNotesLabel.setText(pet.getHealthNotes() != null ? pet.getHealthNotes() : "");
            // Owner info
            User owner = pet.getOwner();
            ownerNameLabel.setText(owner != null && owner.getFullName() != null ? owner.getFullName() : "");
        } else {
            petNameLabel.setText("");
            speciesLabel.setText("");
            genderLabel.setText("");
            birthDateLabel.setText("");
            weightLabel.setText("");
            healthNotesLabel.setText("");
            ownerNameLabel.setText("");
        }
    }
} 