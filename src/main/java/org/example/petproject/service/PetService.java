package org.example.petproject.service;

import org.example.petproject.dao.HibernateUtil;
import org.example.petproject.model.Pet;
import org.hibernate.Session;

import java.util.List;

public class PetService {

    /**
     * Tìm thú cưng theo tên (like) và ownerId
     */
    public static List<Pet> searchByName(String name, String ownerId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "from Pet p where p.owner.userId = :ownerId and lower(p.name) like :nm",
                            Pet.class)
                    .setParameter("ownerId", ownerId)
                    .setParameter("nm", "%" + name.toLowerCase() + "%")
                    .getResultList();
        }
    }

    /**
     * Lấy tất cả thú cưng của owner
     */
    public static List<Pet> getByOwner(String ownerId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "from Pet p where p.owner.userId = :ownerId",
                            Pet.class)
                    .setParameter("ownerId", ownerId)
                    .getResultList();
        }
    }
}
