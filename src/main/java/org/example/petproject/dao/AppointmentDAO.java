package org.example.petproject.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.example.petproject.model.Appointment;

import java.time.LocalDate;
import java.util.List;

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
}