package org.example.petproject.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class HibernateUtil {
    // Registry để later destroy
    private static StandardServiceRegistry registry;
    private static SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            // 1. Load cấu hình từ hibernate.cfg.xml
            registry = new StandardServiceRegistryBuilder()
                    .configure() // mặc định tìm hibernate.cfg.xml ở classpath root
                    .build();

            // 2. Tạo Metadata từ registry
            MetadataSources sources = new MetadataSources(registry);
            Metadata metadata = sources.getMetadataBuilder().build();

            // 3. Xây SessionFactory từ metadata
            return metadata.getSessionFactoryBuilder().build();
        } catch (Exception e) {
            // Nếu build lỗi, destroy registry để tránh leak
            if (registry != null) {
                StandardServiceRegistryBuilder.destroy(registry);
            }
            throw new ExceptionInInitializerError("SessionFactory creation failed: " + e);
        }
    }

    /** Trả về SessionFactory singleton */
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /**
     * Với hibernate.current_session_context_class=thread, mỗi thread sẽ
     * có một Session gắn kèm. Lần đầu gọi nó sẽ tự open,
     * bạn chỉ cần beginTransaction()/commit(), không phải close().
     */
    public static Session getCurrentSession() {
        return getSessionFactory().getCurrentSession();
    }

    /** Shutdown Hibernate, gọi khi ứng dụng đóng */
    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
        if (registry != null) {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
}
