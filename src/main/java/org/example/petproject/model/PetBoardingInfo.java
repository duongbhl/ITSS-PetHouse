package org.example.petproject.model;


public class PetBoardingInfo {

    private String petName;
    private String staffName;
    private String staffPhone;
    private String roomName;
    private String roomType;
    private String checkInDate;
    private String checkOutDate;
    private String price;
    private String bookingId; // Unique ID for the pet/boarding record

    // Constructor
    public PetBoardingInfo(String bookingId, String petName, String staffName, String staffPhone,
                           String roomName, String roomType, String checkInDate, String checkOutDate, String price) {
        this.bookingId = bookingId;
        this.petName = petName;
        this.staffName = staffName;
        this.staffPhone = staffPhone;
        this.roomName = roomName;
        this.roomType = roomType;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.price = price;
    }

    // Getters (and potentially setters if needed for updates)
    public String getbookingId() { return bookingId; }
    public String getPetName() { return petName; }
    public String getStaffName() { return staffName; }
    public String getStaffPhone() { return staffPhone; }
    public String getRoomName() { return roomName; }
    public String getRoomType() { return roomType; }
    public String getCheckInDate() { return checkInDate; }
    public String getCheckOutDate() { return checkOutDate; }
    public String getPrice() { return price; }
}
