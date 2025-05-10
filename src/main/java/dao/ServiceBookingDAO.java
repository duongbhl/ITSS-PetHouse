package dao;

import model.ServiceBooking;

public class ServiceBookingDAO extends BaseDAO<ServiceBooking, Long> {
    @Override
    protected Class<ServiceBooking> getEntityClass() {
        return ServiceBooking.class;
    }
}
