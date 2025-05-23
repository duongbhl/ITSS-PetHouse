-- Database schema for PetProject
-- Improvements applied:
-- 1. Enums use ASCII values; labels handled in application layer.
-- 2. NOT NULL constraints for mandatory fields.
-- 3. CHECK constraints for enums stored as strings.
-- 4. createdAt timestamp default in DB.
-- 5. Indexes on foreign keys for performance.

-- Table Users: stores all user accounts (owners, doctors, staff, admin)
CREATE TABLE Users (
    userId        VARCHAR(255) PRIMARY KEY,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password      VARCHAR(255) NOT NULL,
    fullName      VARCHAR(255),
    phone         VARCHAR(255),
    avatarUrl     VARCHAR(255),
    role          VARCHAR(255) NOT NULL
                   CHECK (role IN ('owner','doctor','staff','admin'))
);

-- Table Pet: pets belong to users (owners)
CREATE TABLE Pet (
    petId         VARCHAR(255) PRIMARY KEY,
    owner_id      VARCHAR(255) NOT NULL REFERENCES Users(userId),
    name          VARCHAR(255) NOT NULL,
    species       VARCHAR(255),
    gender        VARCHAR(255) NOT NULL
                   CHECK (gender IN ('male','female')),
    birthDate     DATE,
    weight        REAL,
    diet          VARCHAR(255),
    healthNotes   VARCHAR(255)
);
-- Index for querying pets by owner
CREATE INDEX idx_pet_owner ON Pet(owner_id);

-- Table Appointment: booking/visting pets
CREATE TABLE Appointment (
    appointmentId     VARCHAR(255) PRIMARY KEY,
    pet_id            VARCHAR(255) NOT NULL REFERENCES Pet(petId),
    doctor_id         VARCHAR(255) NOT NULL REFERENCES Users(userId),
    appointmentTime   TIMESTAMP,
    type              VARCHAR(255),
    status            VARCHAR(255) NOT NULL
                       CHECK (status IN ('pending','confirmed','completed','cancelled')),
    note              VARCHAR(255),
    confirmed_by      VARCHAR(255) REFERENCES Users(userId),
    confirmedAt       TIMESTAMP
);
-- Index on pet reference
CREATE INDEX idx_appointment_pet ON Appointment(pet_id);

-- Table MedicalRecord: records tied to appointments and pets
CREATE TABLE MedicalRecord (
    recordId       VARCHAR(255) PRIMARY KEY,
    appointment_id VARCHAR(255) REFERENCES Appointment(appointmentId),
    pet_id         VARCHAR(255) REFERENCES Pet(petId),
    examDate       TIMESTAMP,
    doctor_id      VARCHAR(255) REFERENCES Users(userId),
    type           VARCHAR(255),
    symptoms       VARCHAR(255),
    diagnosis      VARCHAR(255),
    prescription   VARCHAR(255),
    treatment      VARCHAR(255),
    followUpDate   DATE,
    note           VARCHAR(255),
    createdAt      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
-- Index on pet reference for records
CREATE INDEX idx_medicalrecord_pet ON MedicalRecord(pet_id);

-- Table Room: boarding rooms for pets
CREATE TABLE Room (
    roomId         VARCHAR(255) PRIMARY KEY,
    name           VARCHAR(255) NOT NULL,
    type           VARCHAR(255) NOT NULL
                       CHECK (type IN ('STANDARD','VIP')),
    pricePerDay    NUMERIC(10,2) NOT NULL
);

-- Table Service: services like boarding or grooming
CREATE TABLE Service (
    serviceId      VARCHAR(255) PRIMARY KEY,
    name           VARCHAR(255) NOT NULL,
    price          NUMERIC(10,2) NOT NULL,
    type           VARCHAR(255) NOT NULL
                       CHECK (type IN ('BOARDING','GROOMING'))
);

-- Table ServiceBooking: booking for specific services
CREATE TABLE ServiceBooking (
    bookingId      VARCHAR(255) PRIMARY KEY,
    pet_id         VARCHAR(255) NOT NULL REFERENCES Pet(petId),
    service_id     VARCHAR(255) NOT NULL REFERENCES Service(serviceId),
    checkInTime    TIMESTAMP,
    checkOutTime   TIMESTAMP,
    status         VARCHAR(255) NOT NULL
                       CHECK (status IN ('pending','in_progress','done')),
    note           VARCHAR(255),
    handled_by     VARCHAR(255) REFERENCES Users(userId)
);
-- Indexes for performance
CREATE INDEX idx_servicebooking_pet ON ServiceBooking(pet_id);
CREATE INDEX idx_servicebooking_handledby ON ServiceBooking(handled_by);

-- Table PetBoarding: one-to-one link between booking and room
CREATE TABLE PetBoarding (
    boardingId     VARCHAR(255) PRIMARY KEY,
    booking_id     VARCHAR(255) UNIQUE REFERENCES ServiceBooking(bookingId),
    room_id        VARCHAR(255) REFERENCES Room(roomId)
);
-- Index on room reference
CREATE INDEX idx_petboarding_room ON PetBoarding(room_id);
