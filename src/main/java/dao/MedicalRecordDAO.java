package dao;

import model.MedicalRecord;

public class MedicalRecordDAO extends BaseDAO<MedicalRecord, Long> {
    @Override
    protected Class<MedicalRecord> getEntityClass() {
        return MedicalRecord.class;
    }
}
