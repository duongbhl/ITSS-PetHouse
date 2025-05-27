package org.example.petproject.dao;

import org.example.petproject.model.User;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.Collections;
import java.util.List;

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

    public List<User> findUsersByRole(User.Role role) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery("FROM User u WHERE u.role = :role", User.class);
            query.setParameter("role", role);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            // Return an empty list in case of an error
            return Collections.emptyList();
        }
    }
}