package org.example.petproject.service;

import org.example.petproject.model.NotificationDto;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class NotificationService {
        private static final SessionFactory factory = new Configuration().configure().buildSessionFactory();

        /**
         * Gom tất cả thông báo (medical follow-up, boarding reminder, appointment
         * reminder)
         * cho ownerId, sắp xếp mới → cũ.
         */
        public static List<NotificationDto> getNotifications(String ownerId) {
                List<NotificationDto> list = new ArrayList<>();

                try (Session session = factory.openSession()) {
                        // 1) MedicalRecord follow-up (followUpDate is LocalDate)
                        List<Object[]> med = session.createQuery(
                                        "select m.followUpDate, m.pet.name, " +
                                                        "concat('Nhắc tái khám: ', to_char(m.followUpDate, 'DD/MM/YYYY')) "
                                                        +
                                                        "from MedicalRecord m " +
                                                        "where m.pet.owner.userId = :uid and m.followUpDate is not null",
                                        Object[].class).setParameter("uid", ownerId)
                                        .list();

                        for (Object[] row : med) {
                                LocalDate d = row[0] instanceof LocalDate
                                                ? (LocalDate) row[0]
                                                : null;
                                if (d != null) {
                                        LocalDateTime dt = d.atStartOfDay();
                                        String pet = (String) row[1];
                                        String msg = (String) row[2];
                                        list.add(new NotificationDto(dt, pet, msg));
                                }
                        }

                        // 2) ServiceBooking boarding reminder (checkInTime may be LocalDateTime or
                        // LocalDate)
                        List<Object[]> book = session.createQuery(
                                        "select sb.checkInTime, sb.pet.name, " +
                                                        "concat('Nhắc lưu trú: Nhận phòng ', to_char(sb.checkInTime, 'DD/MM/YYYY HH24:MI')) "
                                                        +
                                                        "from ServiceBooking sb " +
                                                        "where sb.pet.owner.userId = :uid",
                                        Object[].class).setParameter("uid", ownerId)
                                        .list();

                        for (Object[] row : book) {
                                Object time = row[0];
                                LocalDateTime dt;
                                if (time instanceof LocalDateTime) {
                                        dt = (LocalDateTime) time;
                                } else if (time instanceof LocalDate) {
                                        dt = ((LocalDate) time).atStartOfDay();
                                } else {
                                        continue;
                                }
                                String pet = (String) row[1];
                                String msg = (String) row[2];
                                list.add(new NotificationDto(dt, pet, msg));
                        }

                        // 3) Appointment reminder (appointmentTime may be LocalDateTime or LocalDate)
                        List<Object[]> appt = session.createQuery(
                                        "select a.appointmentTime, a.pet.name, " +
                                                        "concat('Nhắc lịch khám: ', to_char(a.appointmentTime, 'DD/MM/YYYY HH24:MI')) "
                                                        +
                                                        "from Appointment a " +
                                                        "where a.pet.owner.userId = :uid",
                                        Object[].class).setParameter("uid", ownerId)
                                        .list();

                        for (Object[] row : appt) {
                                Object time = row[0];
                                LocalDateTime dt;
                                if (time instanceof LocalDateTime) {
                                        dt = (LocalDateTime) time;
                                } else if (time instanceof LocalDate) {
                                        dt = ((LocalDate) time).atStartOfDay();
                                } else {
                                        continue;
                                }
                                String pet = (String) row[1];
                                String msg = (String) row[2];
                                list.add(new NotificationDto(dt, pet, msg));
                        }
                }

                // Sắp xếp từ mới nhất → cũ nhất
                list.sort(Comparator.comparing(NotificationDto::getDateTime).reversed());
                return list;
        }
}
