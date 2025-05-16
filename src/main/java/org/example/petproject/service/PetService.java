package org.example.petproject.service;

import org.example.petproject.dao.HibernateUtil;
import org.example.petproject.model.Pet;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.UUID;

public class PetService {
    // Sử dụng SessionFactory chung
    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    /**
     * Lấy danh sách tất cả Pet của owner.
     */
    public List<Pet> findByOwnerId(String ownerId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            List<Pet> pets = session.createQuery(
                    "FROM Pet p WHERE p.owner.userId = :oid", Pet.class)
                    .setParameter("oid", ownerId)
                    .list();
            tx.commit();
            return pets;
        }
    }

    /**
     * Tìm Pet theo ID.
     */
    public Pet findById(String petId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            Pet p = session.get(Pet.class, petId);
            tx.commit();
            return p;
        }
    }

    /**
     * Tạo mới hoặc cập nhật Pet.
     * Nếu pet.petId == null -> sinh UUID + persist,
     * Ngược lại -> merge.
     */
    public void save(Pet pet) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            if (pet.getPetId() == null || pet.getPetId().isBlank()) {
                pet.setPetId(UUID.randomUUID().toString());
                session.persist(pet);
            } else {
                session.merge(pet);
            }
            tx.commit();
        }
    }

    /**
     * Cập nhật Pet có sẵn.
     */
    public void update(Pet pet) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(pet);
            tx.commit();
        }
    }

    /**
     * Tìm Pet theo tên (like) và ownerId.
     */
    public List<Pet> searchByName(String name, String ownerId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            List<Pet> results = session.createQuery(
                    "FROM Pet p WHERE p.owner.userId = :ownerId AND lower(p.name) like :nm", Pet.class)
                    .setParameter("ownerId", ownerId)
                    .setParameter("nm", "%" + name.toLowerCase() + "%")
                    .getResultList();
            tx.commit();
            return results;
        }
    }

    /**
     * Xóa Pet theo đối tượng.
     */
    public void delete(Pet pet) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.remove(pet);
            tx.commit();
        }
    }

    /**
     * Xóa Pet theo ID.
     */
    public void deleteById(String petId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            Pet p = session.get(Pet.class, petId);
            if (p != null) {
                session.remove(p);
            }
            tx.commit();
        }
    }
}