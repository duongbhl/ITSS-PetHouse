package org.example.petproject.dao;

import org.example.petproject.model.Pet;
import org.example.petproject.model.Service;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class ServiceDAO extends BaseDAO<Service, Long> {
    @Override
    protected Class<Service> getEntityClass() {
        return Service.class;
    }

    public Service findservicebyID(String serviceId) {
        try(Session session=HibernateUtil.getSessionFactory().openSession()) {
            Query<Service> query=session.createQuery("select t from Service t where t.serviceId=:serviceId", Service.class);
            query.setParameter("serviceId", serviceId);
            return query.uniqueResult();
        }catch (HibernateException e) {
            e.printStackTrace();
        }
        return null;
    }
}
