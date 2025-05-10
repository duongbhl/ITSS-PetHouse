package dao;


import org.hibernate.Session;

public class ConnectDB {
    public static void main(String[] args) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            System.out.println("✅ Kết nối thành công tới CSDL PostgreSQL!");
            System.out.println("Session: " + session);
        } catch (Exception e) {
            System.err.println("❌ Kết nối thất bại: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
