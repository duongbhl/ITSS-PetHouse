package org.example.petproject.dao;

import org.example.petproject.model.Appointment;

public class AppointmentDAO extends BaseDAO<Appointment, Long> {
    @Override
    protected Class<Appointment> getEntityClass() {
        return Appointment.class;
    }
}