package org.example.petproject.dao;

import org.example.petproject.model.MedicalRecord;

public class MedicalRecordDAO extends BaseDAO<MedicalRecord, Long> {
    @Override
    protected Class<MedicalRecord> getEntityClass() {
        return MedicalRecord.class;
    }
}