package org.example.petproject.model;

public class PetBoardingInfo {

    private String bookingId;
    private String petName;
    private String serviceType;
    private String staffName;
    private String staffPhone;
    private String roomName;
    private String checkInDate;
    private String checkOutDate;
    private String detail;
    private String price;

    public PetBoardingInfo(String bookingId,
            String petName,
            String serviceType,
            String staffName,
            String staffPhone,
            String roomName,
            String checkInDate,
            String checkOutDate,
            String detail,
            String price) {
        this.bookingId = bookingId;
        this.petName = petName;
        this.serviceType = serviceType;
        this.staffName = staffName;
        this.staffPhone = staffPhone;
        this.roomName = roomName;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.detail = detail;
        this.price = price;
    }

    // Thêm constructor cho Boarding (có room, checkIn, checkOut nhưng không có
    // detail dịch vụ con)
    public PetBoardingInfo(String bookingId,
            String petName,
            String serviceType,
            String staffName,
            String staffPhone,
            String roomName,
            String checkInDate,
            String checkOutDate,
            String price) {
        this(bookingId,
                petName,
                serviceType,
                staffName,
                staffPhone,
                roomName,
                checkInDate,
                checkOutDate,
                /* detail */ "",
                price);
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getPetName() {
        return petName;
    }

    public String getServiceType() {
        return serviceType;
    }

    public String getStaffName() {
        return staffName;
    }

    public String getStaffPhone() {
        return staffPhone;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getCheckInDate() {
        return checkInDate;
    }

    public String getCheckOutDate() {
        return checkOutDate;
    }

    public String getDetail() {
        return detail;
    }

    public String getPrice() {
        return price;
    }
}