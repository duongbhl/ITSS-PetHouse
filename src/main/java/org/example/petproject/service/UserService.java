package org.example.petproject.service;

import org.example.petproject.dao.HibernateUtil;
import org.example.petproject.model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class UserService {

    /**
     * Thử đăng nhập với email & mật khẩu.
     * Sử dụng session-per-thread, mỗi lần đều begin/commit transaction.
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
     * Đăng ký user mới, mặc định role đã được set trước khi gọi.
     */
    public void register(User user) {
        Session session = HibernateUtil.getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            session.persist(user);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null)
                tx.rollback();
            throw ex;
        }
    }

    /**
     * Kiểm tra xem email đã có trong DB chưa.
     * Dùng transaction vì là read-only.
     */
    public boolean isEmailTaken(String email) {
        Session session = HibernateUtil.getCurrentSession();
        Transaction tx = session.beginTransaction();
        Long count = session.createQuery(
                "SELECT count(u.id) FROM User u WHERE u.email = :email", Long.class)
                .setParameter("email", email)
                .uniqueResult();
        tx.commit();
        return count != null && count > 0;
    }
}
