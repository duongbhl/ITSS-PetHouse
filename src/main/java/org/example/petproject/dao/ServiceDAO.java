package org.example.petproject.dao;

import org.example.petproject.model.Service;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import java.util.List;

public class ServiceDAO extends BaseDAO<Service, String> { // <Service, String>
    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    @Override
    protected Class<Service> getEntityClass() {
        return Service.class;
    }

    /** Tìm Service theo tên chính xác */
    public Service findServiceByName(String name) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "FROM Service s WHERE s.name = :n", Service.class)
                    .setParameter("n", name)
                    .uniqueResult();
        }
    }

    /** Tìm danh sách Service theo enum Type */
    public List<Service> findByType(Service.Type type) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "FROM Service s WHERE s.type = :t", Service.class)
                    .setParameter("t", type)
                    .list();
        }
    }

    // Nếu bạn vẫn muốn lookup bằng ID (không bắt buộc):
    public Service findById(String serviceId) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Service.class, serviceId);
        } catch (HibernateException e) {
            e.printStackTrace();
            return null;
        }
    }
}
