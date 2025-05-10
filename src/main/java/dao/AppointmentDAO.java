package dao;

import model.Appointment;

public class AppointmentDAO extends BaseDAO<Appointment, Long> {
    @Override
    protected Class<Appointment> getEntityClass() {
        return Appointment.class;
    }
}
