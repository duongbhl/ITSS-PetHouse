package org.example.petproject.dao;

import org.example.petproject.model.Room;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.Collections;
import java.util.List;

public class RoomDAO extends BaseDAO<Room, Long> {
    @Override
    protected Class<Room> getEntityClass() {
        return Room.class;
    }

    public List<String> findAllNames() {
        try(Session session=HibernateUtil.getSessionFactory().openSession()) {
            Query<String> query=session.createQuery("select t.name from Room t",String.class);
            return query.getResultList();
        }
        catch (HibernateException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public Room findByName(String name) {
        try(Session session=HibernateUtil.getSessionFactory().openSession()) {
            Query<Room> query=session.createQuery("from Room t where t.name=:name",Room.class);
            query.setParameter("name", name);
            return query.uniqueResult();
        }
        catch (HibernateException e) {
            e.printStackTrace();
        }
        return findByName(name);
    }
}
