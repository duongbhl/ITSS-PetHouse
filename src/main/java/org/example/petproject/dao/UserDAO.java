package org.example.petproject.dao;

import org.example.petproject.model.User;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class UserDAO extends BaseDAO<User, Long> {
    @Override
    protected Class<User> getEntityClass() {
        return User.class;
    }

    public User getUserByOwnerID(String ownerID) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery("from User t where t.userId=:ownerID", User.class);
            query.setParameter("ownerID", ownerID);
            return query.uniqueResult();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}
