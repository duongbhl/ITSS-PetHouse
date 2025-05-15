package org.example.petproject.service;

import org.example.petproject.dao.HibernateUtil;
import org.example.petproject.model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.UUID;

public class UserService {

    /**
     * Thử đăng nhập với email & mật khẩu.
     */
    public User login(String email, String password) {
        Session session = HibernateUtil.getCurrentSession();
        Transaction tx = session.beginTransaction();

        User u = session.createQuery(
                "FROM User u WHERE u.email = :e", User.class)
                .setParameter("e", email)
                .uniqueResult();

        tx.commit();

        if (u != null && u.getPassword().equals(password)) {
            return u;
        }
        return null;
    }

    /**
     * Đăng ký user mới:
     * - Nếu userId chưa set, sẽ cấp UUID.
     * - Role, email, password… đều đã set sẵn trước khi gọi.
     */
    public void register(User user) {
        // 1) Nếu chưa có PK, cấp UUID
        if (user.getUserId() == null || user.getUserId().isBlank()) {
            user.setUserId(UUID.randomUUID().toString());
        }

        // 2) Bắt transaction, persist rồi commit
        Session session = HibernateUtil.getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            session.persist(user);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        }
    }

    /**
     * Kiểm tra xem email đã có trong DB chưa.
     */
    public boolean isEmailTaken(String email) {
        Session session = HibernateUtil.getCurrentSession();
        Transaction tx = session.beginTransaction();

        Long count = session.createQuery(
                "SELECT count(u.userId) FROM User u WHERE u.email = :email", Long.class)
                .setParameter("email", email)
                .uniqueResult();

        tx.commit();
        return count != null && count > 0;
    }

    /**
     * Cập nhật thông tin User đã tồn tại.
     */
    public void update(User user) {
        // Nếu chưa có ID, không cho cập nhật
        if (user.getUserId() == null) {
            throw new IllegalArgumentException("User chưa có ID, không thể update.");
        }
        Session session = HibernateUtil.getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            session.merge(user); // hoặc session.update(user);
            tx.commit();
        } catch (Exception ex) {
            if (tx.isActive())
                tx.rollback();
            throw ex;
        }
    }

    public boolean changePassword(String userId, String oldPwd, String newPwd) {
        Session session = HibernateUtil.getCurrentSession();
        Transaction tx = session.beginTransaction();
        User u = session.get(User.class, userId);
        if (u == null || !u.getPassword().equals(oldPwd)) {
            tx.commit();
            return false;
        }
        u.setPassword(newPwd);
        session.merge(u);
        tx.commit();
        return true;
    }

}
