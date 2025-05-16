package org.example.petproject.dao;

import org.example.petproject.model.PetBoarding;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class PetBoardingDAO extends BaseDAO<PetBoarding, Long> {
    @Override
    protected Class<PetBoarding> getEntityClass() {
        return PetBoarding.class;
    }

    public PetBoarding findPetBoardingByServiceId(String serviceId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<PetBoarding> query = session.createQuery("from PetBoarding s where s.booking.bookingId=:serviceId",
                    PetBoarding.class);
            query.setParameter("serviceId", serviceId);
            return query.uniqueResult();
        } catch (HibernateException e) {
            e.printStackTrace();
        }
        return null;
    }
}