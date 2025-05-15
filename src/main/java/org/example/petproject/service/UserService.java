package org.example.petproject.service;

import org.example.petproject.dao.*;
import org.example.petproject.model.Appointment;
import org.example.petproject.model.PetBoarding;
import org.example.petproject.model.ServiceBooking;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Stack;

public class UserService {
    UserDAO userDAO=new UserDAO();
    PetDAO petDAO=new PetDAO();
    AppointmentDAO appointmentDAO=new AppointmentDAO();
    ServiceBookingDAO serviceBookingDAO=new ServiceBookingDAO();
    PetBoardingDAO petBoardingDAO=new PetBoardingDAO();

    public UserService() {}

    public void datlichkham(String name, LocalDate appointmenttime, String type, Appointment.Status status) {
        Appointment appointment=new Appointment(name,appointmenttime,type,status);
        appointmentDAO.save(appointment);
    }

    public void dkdvvesinh(LocalDate checkInTime, String note, ServiceBooking.Status status, String petname, String service_id){
        ServiceBooking serviceBooking=new ServiceBooking(checkInTime,note, status,petname,service_id);
        serviceBookingDAO.save(serviceBooking);
    }
    public void dkdvluutru(LocalDate checkInTime,LocalDate checkoutTime ,String note, ServiceBooking.Status status, String petname, String service_id,String roomname){
        ServiceBooking serviceBooking=new ServiceBooking(checkInTime,checkoutTime,note, status,petname,service_id);
        serviceBookingDAO.save(serviceBooking);
        PetBoarding petBoarding=new PetBoarding(serviceBooking,roomname);
        petBoardingDAO.save(petBoarding);
    }
}
