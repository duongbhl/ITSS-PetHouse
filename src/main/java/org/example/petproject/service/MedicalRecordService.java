package org.example.petproject.service;

import org.example.petproject.dao.HibernateUtil;
import org.example.petproject.model.MedicalRecord;
import org.hibernate.Session;

import java.util.List;

public class MedicalRecordService {

    /**
     * Lấy tất cả hồ sơ khám của pet, sắp xếp theo examDate giảm dần
     */
    public static List<MedicalRecord> getByPet(String petId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "from MedicalRecord m where m.pet.petId = :petId order by m.examDate desc",
                            MedicalRecord.class)
                    .setParameter("petId", petId)
                    .getResultList();
        }
    }
}
