-- USER
INSERT INTO "users" (userid, email, password, fullname, phone, avatarurl, role) VALUES
('U001', 'admin@pethouse.com', 'hashedpassword1', 'Admin Pethouse', '0123456789', NULL, 'admin'),
('U002', 'owner1@gmail.com', 'hashedpassword2', 'John Doe', '0909123456', NULL, 'owner'),
('U003', 'doctor1@gmail.com', 'hashedpassword3', 'Dr. Lily Vet', '0911123456', NULL, 'doctor'),
('U004', 'staff1@gmail.com', 'hashedpassword4', 'Emily Staff', '0933123456', NULL, 'staff');

-- PET
INSERT INTO Pet (petid, owner_id, name, species, gender, birthdate, weight, diet, healthnotes) VALUES
('P001', 'U002', 'Buddy', 'Dog', 'male', '2021-01-01', 18.5, 'High protein diet', 'Allergic to peanuts'),
('P002', 'U002', 'Mittens', 'Cat', 'female', '2022-03-10', 4.2, 'Grain-free cat food', NULL);

-- SERVICE
INSERT INTO Service (serviceid, name, price, type) VALUES
('S001', 'Grooming', 200000, 'làm đẹp & vệ sinh'),
('S002', 'Pet Hotel', 300000, 'lưu trú');

-- SERVICE BOOKING
INSERT INTO ServiceBooking (bookingid, pet_id, service_id, checkintime, checkouttime, status, note, handled_by) VALUES
('B001', 'P001', 'S001', '2025-05-10 09:00:00', '2025-05-10 10:00:00', 'done', 'Standard grooming', 'U004'),
('B002', 'P002', 'S002', '2025-05-09 15:00:00', '2025-05-12 10:00:00', 'in_progress', '3-night boarding', 'U004');

-- ROOM
INSERT INTO Room (roomid, name, type, priceperday) VALUES
('R001', 'Room 1A', 'thường', 100000),
('R002', 'VIP Suite', 'vip', 200000);

-- PET BOARDING
INSERT INTO PetBoarding (boardingid, booking_id, room_id) VALUES
('PB001', 'B002', 'R001');

-- APPOINTMENT
INSERT INTO Appointment (appointmentid, pet_id, appointmenttime, type, status, note, confirmed_by, confirmedat) VALUES
('A001', 'P001', '2025-05-08 14:30:00', 'General Checkup', 'completed', 'Yearly health check', 'U004', '2025-05-08 10:00:00');

-- MEDICAL RECORD
INSERT INTO MedicalRecord (
  recordid, appointment_id, pet_id, examdate, doctor_id,
  type, symptoms, diagnosis, prescription, treatment, followupdate, note
) VALUES
('MR001', 'A001', 'P001', '2025-05-08 14:45:00', 'U003',
 'Checkup', 'None', 'Healthy', 'Vitamins', 'None required', NULL, 'Good condition');