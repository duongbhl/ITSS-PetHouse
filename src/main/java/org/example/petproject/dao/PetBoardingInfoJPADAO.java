package org.example.petproject.dao;

import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.example.petproject.model.PetBoarding;
import org.example.petproject.model.PetBoardingInfoJPA;
import org.example.petproject.model.ServiceBooking;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PetBoardingInfoJPADAO extends BaseDAO<PetBoardingInfoJPA, String> {

    @Override
    protected Class<PetBoardingInfoJPA> getEntityClass() {
        return PetBoardingInfoJPA.class;
    }

    /**
     * Find boarding info by filters - status and date range
     */
    public List<PetBoardingInfoJPA> findByFilters(String status, LocalDate fromDate, LocalDate toDate) {
        try (Session session = getSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<PetBoardingInfoJPA> cq = cb.createQuery(PetBoardingInfoJPA.class);
            Root<PetBoardingInfoJPA> root = cq.from(PetBoardingInfoJPA.class);

            List<Predicate> predicates = new ArrayList<>();

            if (status != null && !status.isEmpty()) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (fromDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("checkInDate"), fromDate.atStartOfDay()));
            }

            if (toDate != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("checkInDate"), toDate.plusDays(1).atStartOfDay()));
            }

            if (!predicates.isEmpty()) {
                cq.where(cb.and(predicates.toArray(new Predicate[0])));
            }

            TypedQuery<PetBoardingInfoJPA> query = session.createQuery(cq);
            return query.getResultList();
        } catch (HibernateException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Find boarding info by pet ID
     */
    public List<PetBoardingInfoJPA> findByPetId(String petId) {
        try (Session session = getSession()) {
            Query<PetBoardingInfoJPA> query = session.createQuery(
                    "FROM PetBoardingInfoJPA p WHERE p.pet.petId = :petId ORDER BY p.checkInDate DESC",
                    PetBoardingInfoJPA.class);
            query.setParameter("petId", petId);
            return query.getResultList();
        } catch (HibernateException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Find active boardings (status = 'in_progress')
     */
    public List<PetBoardingInfoJPA> findActiveBoarding() {
        try (Session session = getSession()) {
            Query<PetBoardingInfoJPA> query = session.createQuery(
                    "FROM PetBoardingInfoJPA p WHERE p.status = 'in_progress'",
                    PetBoardingInfoJPA.class);
            return query.getResultList();
        } catch (HibernateException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Synchronize status between PetBoardingInfoJPA and ServiceBooking
     */
    private void syncStatusWithServiceBooking(String status, ServiceBooking booking) {
        if (status == null || booking == null) return;

        try (Session session = getSession()) {
            Transaction tx = session.beginTransaction();
            try {
                // Convert status string to enum
                ServiceBooking.Status bookingStatus = ServiceBooking.Status.valueOf(status.toUpperCase());
                
                // Update ServiceBooking status
                booking.setStatus(bookingStatus);
                
                // Save changes
                session.merge(booking);
                session.flush();
                
                tx.commit();
            } catch (Exception e) {
                if (tx != null) tx.rollback();
                e.printStackTrace();
            }
        }
    }

    @Override
    public PetBoardingInfoJPA update(PetBoardingInfoJPA entity) {
        Session session = null;
        Transaction tx = null;
        try {
            session = getSession();
            tx = session.beginTransaction();

            // Get the original entity to check status change
            PetBoardingInfoJPA originalEntity = session.get(PetBoardingInfoJPA.class, entity.getInfoId());
            String originalStatus = originalEntity != null ? originalEntity.getStatus() : null;
            String newStatus = entity.getStatus();

            // Update PetBoardingInfoJPA
            PetBoardingInfoJPA updatedEntity = (PetBoardingInfoJPA) session.merge(entity);
            session.flush();

            // If status has changed, update ServiceBooking
            if (newStatus != null && !newStatus.equals(originalStatus)) {
                PetBoarding petBoarding = updatedEntity.getPetBoarding();
                if (petBoarding != null) {
                    ServiceBooking booking = petBoarding.getBooking();
                    if (booking != null) {
                        // Sync status with ServiceBooking
                        syncStatusWithServiceBooking(newStatus, booking);
                    }
                }
            }

            tx.commit();
            return updatedEntity;
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            throw new RuntimeException("Error updating pet boarding info", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public PetBoardingInfoJPA createOrUpdateFromPetBoarding(PetBoarding petBoarding) {
        if (petBoarding == null || petBoarding.getBooking() == null) {
            throw new IllegalArgumentException("PetBoarding or ServiceBooking cannot be null");
        }

        Session session = null;
        Transaction tx = null;
        try {
            session = getSession();
            tx = session.beginTransaction();

            // Check if entry already exists
            Query<PetBoardingInfoJPA> query = session.createQuery(
                    "FROM PetBoardingInfoJPA p WHERE p.petBoarding.boardingId = :boardingId",
                    PetBoardingInfoJPA.class);
            query.setParameter("boardingId", petBoarding.getBoardingId());

            PetBoardingInfoJPA boardingInfo = query.uniqueResult();
            boolean isNew = false;

            if (boardingInfo == null) {
                boardingInfo = new PetBoardingInfoJPA();
                boardingInfo.setPetBoarding(petBoarding);
                boardingInfo.setPet(petBoarding.getBooking().getPet());
                isNew = true;
            }

            // Update basic information
            ServiceBooking booking = petBoarding.getBooking();
            if (booking.getCheckInTime() != null) {
                    boardingInfo.setCheckInDate(((LocalDate) booking.getCheckInTime()).atStartOfDay());
            }
            if (booking.getCheckOutTime() != null) {
                    boardingInfo.setCheckOutDate(((LocalDate) booking.getCheckOutTime()).atStartOfDay());
            }

            // Set status from ServiceBooking for new entries
            if (isNew && booking.getStatus() != null) {
                boardingInfo.setStatus(booking.getStatus().toString().toLowerCase());
            }

            boardingInfo.setNote(booking.getNote());

            // Calculate price
            if (petBoarding.getRoom() != null && booking.getCheckInTime() != null && booking.getCheckOutTime() != null) {
                LocalDateTime checkIn = boardingInfo.getCheckInDate();
                LocalDateTime checkOut = boardingInfo.getCheckOutDate();

                if (checkIn != null && checkOut != null) {
                    long days = java.time.Duration.between(checkIn, checkOut).toDays() + 1;
                    if (days < 1) days = 1;

                    BigDecimal pricePerDay = petBoarding.getRoom().getPricePerDay();
                    BigDecimal totalPrice = pricePerDay.multiply(BigDecimal.valueOf(days));

                    boardingInfo.setPrice(totalPrice.doubleValue());
                }
            }

            // Save the entity
            if (isNew) {
                session.persist(boardingInfo);
            } else {
                session.merge(boardingInfo);
            }
            session.flush();

            // Ensure ServiceBooking status matches
            if (boardingInfo.getStatus() != null) {
                syncStatusWithServiceBooking(boardingInfo.getStatus(), booking);
            }

            tx.commit();
            return boardingInfo;
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            throw new RuntimeException("Error creating/updating pet boarding info", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public void synchronizeAllFromPetBoarding() {
        PetBoardingDAO boardingDAO = new PetBoardingDAO();
        List<PetBoarding> allBoardings = boardingDAO.findAll();

        for (PetBoarding boarding : allBoardings) {
            try {
                createOrUpdateFromPetBoarding(boarding);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}