package org.example.petproject.dao;

import org.example.petproject.model.Service;

public class ServiceDAO extends BaseDAO<Service, String> {

    @Override
    protected Class<Service> getEntityClass() {
        return Service.class;
    }

    /**
     * Tìm duy nhất Service theo enum Type.
     */
    public Service findByType(Service.Type type) {
        return getSession()
                .createQuery("FROM Service s WHERE s.type = :t", Service.class)
                .setParameter("t", type)
                .uniqueResult(); // trả về Service hoặc null
    }
}
