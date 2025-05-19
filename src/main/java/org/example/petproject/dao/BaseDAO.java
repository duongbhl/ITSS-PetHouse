package org.example.petproject.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.io.Serializable;
import java.util.List;

/**
 * Generic BaseDAO handling basic CRUD operations.
 * Uses try-with-resources for session management and throws RuntimeException on
 * failure.
 */
public abstract class BaseDAO<T, ID extends Serializable> {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("petproject");
    protected EntityManager entityManager;

    public BaseDAO() {
        this.entityManager = emf.createEntityManager();
    }



    // Other BaseDAO methods

    // Close resources when done
    public void close() {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
    }
    /**
     * Opens a new Hibernate session.
     */
    protected Session getSession() {
        return HibernateUtil.getSessionFactory().openSession();
    }

    /**
     * Returns the class of the entity managed by this DAO.
     */
    protected abstract Class<T> getEntityClass();

    /**
     * Persists a new entity and returns it.
     */
    public T save(T entity) {
        try (Session session = getSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(entity);
            tx.commit();
            return entity;
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Merges changes of an existing entity and returns the managed instance.
     */
    public T update(T entity) {
        try (Session session = getSession()) {
            Transaction tx = session.beginTransaction();
            T merged = (T) session.merge(entity);
            tx.commit();
            return merged;
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Removes an entity.
     */
    public void delete(T entity) {
        try (Session session = getSession()) {
            Transaction tx = session.beginTransaction();
            session.remove(entity);
            tx.commit();
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Finds an entity by its identifier.
     */
    public T findById(ID id) {
        try (Session session = getSession()) {
            return session.get(getEntityClass(), id);
        }
    }

    /**
     * Returns all entities of this type.
     */
    public List<T> findAll() {
        try (Session session = getSession()) {
            Query<T> query = session.createQuery(
                    "FROM " + getEntityClass().getSimpleName(),
                    getEntityClass());
            return query.getResultList();
        }
    }
}
