package org.example.petproject.dao;

import org.example.petproject.model.Pet;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.Collections;
import java.util.List;

public class PetDAO extends BaseDAO<Pet, Long> {
    @Override
    protected Class<Pet> getEntityClass() {
        return Pet.class;
    }

    public List<String> findAllByOwnerId(String ownerId) {
        try(Session session=HibernateUtil.getSessionFactory().openSession()) {
            Query<String> query=session.createQuery("select t.name from Pet t where t.owner.userId=:ownerId",String.class);
            query.setParameter("ownerId", ownerId);
            return query.getResultList();
        }
        catch (HibernateException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public Pet findpetbyName(String name) {
        try(Session session=HibernateUtil.getSessionFactory().openSession()) {
            Query<Pet>query=session.createQuery("select t from Pet t where t.name=:name", Pet.class);
            query.setParameter("name", name);
            return query.uniqueResult();
        }catch (HibernateException e) {
            e.printStackTrace();
        }
        return null;
    }
}
