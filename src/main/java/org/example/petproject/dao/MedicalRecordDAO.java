package org.example.petproject.dao;

import org.example.petproject.model.MedicalRecord;
import java.time.LocalDate;
import java.util.List;
import jakarta.persistence.EntityManager;

public class MedicalRecordDAO extends BaseDAO<MedicalRecord, Long> {
    @Override
    protected Class<MedicalRecord> getEntityClass() {
        return MedicalRecord.class;
    }

    public List<MedicalRecord> findByDoctorIdAndDateRange(String doctorId, LocalDate fromDate, LocalDate toDate) {
        EntityManager em = entityManager;
        try {
            String jpql = "SELECT m FROM MedicalRecord m WHERE m.doctor.userId = :doctorId AND m.examDate >= :fromDate AND m.examDate <= :toDate ORDER BY m.examDate DESC";
            var query = em.createQuery(jpql, MedicalRecord.class);
            query.setParameter("doctorId", doctorId);
            query.setParameter("fromDate", fromDate);
            query.setParameter("toDate", toDate);
            return query.getResultList();
        } finally {
            // Don't close the EntityManager here if it's managed by BaseDAO
        }
    }
}