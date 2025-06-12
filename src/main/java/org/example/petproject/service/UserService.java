package org.example.petproject.service;

import org.example.petproject.dao.HibernateUtil;
import org.example.petproject.dao.AppointmentDAO;
import org.example.petproject.dao.PetBoardingDAO;
import org.example.petproject.dao.PetBoardingInfoJPADAO;
import org.example.petproject.dao.PetDAO;
import org.example.petproject.dao.ServiceBookingDAO;
import org.example.petproject.dao.ServiceDAO;
import org.example.petproject.model.Appointment;
import org.example.petproject.model.Pet;
import org.example.petproject.model.PetBoarding;
import org.example.petproject.model.Service;
import org.example.petproject.model.ServiceBooking;
import org.example.petproject.model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.time.LocalDate;
import java.util.UUID;

public class UserService {

    private final PetDAO petDAO = new PetDAO();
    private final ServiceDAO serviceDAO = new ServiceDAO();
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final ServiceBookingDAO serviceBookingDAO = new ServiceBookingDAO();
    private final PetBoardingDAO petBoardingDAO = new PetBoardingDAO();
    private final PetBoardingInfoJPADAO petBoardingInfoJPADAO = new PetBoardingInfoJPADAO();

    public UserService() {
    }

    /**
     * Thử đăng nhập với email & mật khẩu.
     */
    public User login(String email, String password) {
        System.out.println("=== DEBUG LOGIN ===");
        System.out.println("Email: " + email);
        System.out.println("Input password: " + password);
        System.out.println("Input password length: " + password.length());

        Session session = HibernateUtil.getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            User u = session.createQuery(
                    "FROM User u WHERE u.email = :e", User.class)
                    .setParameter("e", email)
                    .uniqueResult();

            tx.commit();

            if (u != null) {
                System.out.println("Found user: " + u.getFullName());
                System.out.println("DB password: " + u.getPassword());
                System.out.println("DB password length: " + u.getPassword().length());
                System.out.println("Password match: " + u.getPassword().equals(password));

                if (u.getPassword().equals(password)) {
                    return u;
                }
            } else {
                System.out.println("No user found with email: " + email);
            }
            return null;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            System.err.println("Login error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi đăng nhập: " + e.getMessage(), e);
        }
    }

    /**
     * Hash mật khẩu sử dụng SHA-256
     */
    @SuppressWarnings("unused")
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Không thể hash mật khẩu", e);
        }
    }

    /**
     * Đăng ký user mới; cấp UUID nếu cần.
     */
    public void register(User user) {
        if (user.getUserId() == null || user.getUserId().isBlank()) {
            user.setUserId(UUID.randomUUID().toString());
        }
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
     * Đặt lịch khám cho thú cưng.
     */
    public void datlichkham(String name, LocalDate appointmentTime,
            String type, Appointment.Status status) {
        Appointment appointment = new Appointment(name, appointmentTime, type, status);
        appointmentDAO.save(appointment);
    }

    /**
     * Đăng ký dịch vụ vệ sinh cho thú cưng.
     */
    // UserService.java
    public void dkdvvesinh(LocalDate checkInDate,
            String note,
            ServiceBooking.Status status,
            String petId,
            String serviceId) {
        // 1) Lấy entity Pet và Service từ DB
        Pet pet = petDAO.findById(petId);
        Service svc = serviceDAO.findById(serviceId);

        // 2) Khởi tạo booking
        ServiceBooking sb = new ServiceBooking();
        sb.setBookingId(UUID.randomUUID().toString());
        sb.setPet(pet);
        sb.setService(svc);
        // Lưu LocalDate (không atStartOfDay nữa!)
        sb.setCheckInTime(checkInDate);
        sb.setNote(note);
        sb.setStatus(status);

        // 3) Lưu
        serviceBookingDAO.save(sb);
    }

    /**
     * Đăng ký dịch vụ lưu trú và tạo bản ghi boarding.
     */
    public void dkdvluutru(LocalDate checkInTime, LocalDate checkOutTime,
            String note, ServiceBooking.Status status,
            String petName, String serviceId, String roomName) {
        ServiceBooking serviceBooking = new ServiceBooking(checkInTime, checkOutTime, note, status, petName, serviceId);
        serviceBookingDAO.save(serviceBooking);
        PetBoarding petBoarding = new PetBoarding(serviceBooking, roomName);
        petBoardingDAO.save(petBoarding);
        
        // Create PetBoardingInfoJPA immediately
        try {
            petBoardingInfoJPADAO.createOrUpdateFromPetBoarding(petBoarding);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Cập nhật thông tin User đã tồn tại.
     */
    public void update(User user) {
        if (user.getUserId() == null) {
            throw new IllegalArgumentException("User chưa có ID, không thể update.");
        }
        Session session = HibernateUtil.getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            session.merge(user);
            tx.commit();
        } catch (Exception ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        }
    }

    /**
     * Đổi mật khẩu của user.
     */
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