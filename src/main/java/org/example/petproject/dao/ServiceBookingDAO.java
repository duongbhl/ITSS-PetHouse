package org.example.petproject.dao;

import org.example.petproject.model.ServiceBooking;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.Query;

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

    public List<ServiceBooking> getBookingsByOwnerId(String ownerId, ServiceBooking.Status status,String serviceId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<ServiceBooking> query = session.createQuery("SELECT sb FROM ServiceBooking sb JOIN sb.pet p WHERE p.owner.userId = :ownerId and sb.status=:status and sb.service.serviceId=:serviceId");
            query.setParameter("ownerId", ownerId);
            query.setParameter("status", status);
            query.setParameter("serviceId", serviceId);
            return query.list();
        }catch (HibernateException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
}
