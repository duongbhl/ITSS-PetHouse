package org.example.petproject.dao;

import java.time.LocalDate;
import java.util.List;

import org.example.petproject.model.Appointment;

import jakarta.persistence.EntityManager;

public class AppointmentDAO extends BaseDAO<Appointment, Long> {
//    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("petproject");

    @Override
    protected Class<Appointment> getEntityClass() {
        return Appointment.class;
    }

//    private EntityManager getEntityManager() {
//        return emf.createEntityManager();
//    }

    // Use the EntityManager from BaseDAO instead of creating a new one
    public List<Appointment> findAll() {
        EntityManager em = entityManager;
        try {
            return em.createQuery("SELECT a FROM Appointment a", Appointment.class)
                    .getResultList();
        } finally {
            // Don't close the EntityManager here if it's managed by BaseDAO
        }
    }

    public List<Appointment> findByFilters(String status, LocalDate fromDate, LocalDate toDate) {
        EntityManager em = entityManager;
        try {
            StringBuilder jpql = new StringBuilder("SELECT a FROM Appointment a WHERE 1=1");

            if (status != null && !status.isEmpty()) {
                jpql.append(" AND a.status = :status");
            }
            if (fromDate != null) {
                jpql.append(" AND a.appointmentTime >= :fromDate");
            }
            if (toDate != null) {
                jpql.append(" AND a.appointmentTime <= :toDate");
            }

            var query = em.createQuery(jpql.toString(), Appointment.class);

            if (status != null && !status.isEmpty()) {
                query.setParameter("status", Appointment.Status.valueOf(status.toLowerCase()));
            }
            if (fromDate != null) {
                query.setParameter("fromDate", fromDate);
            }
            if (toDate != null) {
                query.setParameter("toDate", toDate);
            }

            return query.getResultList();
        } finally {
            // Don't close the EntityManager here if it's managed by BaseDAO
        }
    }

    public List<Appointment> findByDoctorId(String doctorId, LocalDate fromDate) {
        EntityManager em = entityManager;
        try {
            StringBuilder jpql = new StringBuilder("SELECT a FROM Appointment a WHERE a.doctor.userId = :doctorId");
            
            if (fromDate != null) {
                jpql.append(" AND a.appointmentTime >= :fromDate");
            }
            
            jpql.append(" ORDER BY a.appointmentTime ASC");

            var query = em.createQuery(jpql.toString(), Appointment.class);
            query.setParameter("doctorId", doctorId);
            
            if (fromDate != null) {
                query.setParameter("fromDate", fromDate);
            }

            return query.getResultList();
        } finally {
            // Don't close the EntityManager here if it's managed by BaseDAO
        }
    }

    public List<Appointment> findAvailableForMedicalRecord(String doctorId, LocalDate date) {
        EntityManager em = entityManager;
        try {
            // Lấy các appointment của bác sĩ trong ngày, chưa completed và chưa có medical record
            String jpql = "SELECT a FROM Appointment a WHERE a.doctor.userId = :doctorId " +
                         "AND FUNCTION('DATE', a.appointmentTime) = :date " +
                         "AND a.status <> 'completed' " +
                         "AND NOT EXISTS (SELECT m FROM MedicalRecord m WHERE m.appointment.appointmentId = a.appointmentId) " +
                         "ORDER BY a.appointmentTime ASC";
            var query = em.createQuery(jpql, Appointment.class);
            query.setParameter("doctorId", doctorId);
            query.setParameter("date", date);
            return query.getResultList();
        } finally {
            // Don't close the EntityManager here if it's managed by BaseDAO
        }
    }

    public List<Appointment> findUpcomingByDoctorId(String doctorId, LocalDate fromDate) {
        EntityManager em = entityManager;
        try {
            String jpql = "SELECT a FROM Appointment a WHERE a.doctor.userId = :doctorId AND a.appointmentTime >= :fromDate AND a.status <> 'completed' ORDER BY a.appointmentTime ASC";
            var query = em.createQuery(jpql, Appointment.class);
            query.setParameter("doctorId", doctorId);
            query.setParameter("fromDate", fromDate);
            return query.getResultList();
        } finally {
            // Don't close the EntityManager here if it's managed by BaseDAO
        }
    }
}