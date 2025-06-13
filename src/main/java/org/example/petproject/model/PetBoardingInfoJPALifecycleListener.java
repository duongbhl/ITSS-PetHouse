package org.example.petproject.model;

import jakarta.persistence.PostUpdate;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PreUpdate;
import org.example.petproject.dao.ServiceBookingDAO;

public class PetBoardingInfoJPALifecycleListener {

    @PreUpdate
    @PostUpdate
    @PostPersist
    public void syncServiceBookingStatus(PetBoardingInfoJPA info) {
        if (info.getPetBoarding() != null && info.getPetBoarding().getBooking() != null) {
            ServiceBooking booking = info.getPetBoarding().getBooking();
            try {
                ServiceBooking.Status newStatus = ServiceBooking.Status.valueOf(info.getStatus().toLowerCase());
                booking.setStatus(newStatus);
                new ServiceBookingDAO().update(booking);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}