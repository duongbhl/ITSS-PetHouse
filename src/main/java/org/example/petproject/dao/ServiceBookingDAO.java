package org.example.petproject.dao;

import org.example.petproject.model.ServiceBooking;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServiceBookingDAO extends BaseDAO<ServiceBooking, Long> {
    @Override
    protected Class<ServiceBooking> getEntityClass() {
        return ServiceBooking.class;
    }

    public ServiceBooking findBookingById(String id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(getEntityClass(), id);
        }
    }

    public List<ServiceBooking> getBookingsByOwnerId(String ownerId, ServiceBooking.Status status, String serviceId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<ServiceBooking> query = session.createQuery(
                    "SELECT sb FROM ServiceBooking sb JOIN sb.pet p WHERE p.owner.userId = :ownerId and sb.status=:status and sb.service.serviceId=:serviceId",
                    ServiceBooking.class);
            query.setParameter("ownerId", ownerId);
            query.setParameter("status", status);
            query.setParameter("serviceId", serviceId);
            return query.list();
        } catch (HibernateException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    @SuppressWarnings("unused")
    public List<ServiceBooking> findBookingsByCriteria(LocalDate fromDate, LocalDate toDate,
            ServiceBooking.Status status) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Base query
            StringBuilder queryString = new StringBuilder("FROM ServiceBooking sb WHERE 1=1");
            List<String> conditions = new ArrayList<>();
            List<Object> parameters = new ArrayList<>();
            List<String> paramNames = new ArrayList<>();

            if (fromDate != null) {
                queryString.append(" AND sb.checkInTime >= :fromDate");
                paramNames.add("fromDate");
                parameters.add(fromDate);
            }
            if (toDate != null) {
                queryString.append(" AND sb.checkInTime <= :toDate");
                paramNames.add("toDate");
                parameters.add(toDate);
            }
            if (status != null) {
                queryString.append(" AND sb.status = :status");
                paramNames.add("status");
                parameters.add(status);
            }

            Query<ServiceBooking> query = session.createQuery(queryString.toString(), ServiceBooking.class);

            for (int i = 0; i < parameters.size(); i++) {
                query.setParameter(paramNames.get(i), parameters.get(i));
            }

            return query.list();
        } catch (HibernateException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}