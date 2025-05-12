-- Create and use the database
CREATE DATABASE IF NOT EXISTS fcsDatabase;
USE fcsDatabase;

-- Users table 
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL, -- 'Member', 'Trainer', or 'Admin'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Rooms table
CREATE TABLE rooms (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    capacity INT
);

-- Session types table
CREATE TABLE session_types (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    description TEXT
);

-- Availability table
CREATE TABLE availability (
    id INT AUTO_INCREMENT PRIMARY KEY,
    date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    trainer_id INT,  -- Nullable if it's for a room
    room_id INT,     -- Nullable if it's for a trainer
    FOREIGN KEY (trainer_id) REFERENCES users(id),
    FOREIGN KEY (room_id) REFERENCES rooms(id)
);

-- Sessions table
CREATE TABLE sessions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    type_id INT,
    trainer_id INT,
    room_id INT,
    start_time DATETIME,
    end_time DATETIME,
    FOREIGN KEY (type_id) REFERENCES session_types(id),
    FOREIGN KEY (trainer_id) REFERENCES users(id),
    FOREIGN KEY (room_id) REFERENCES rooms(id)
);

-- Bookings table
CREATE TABLE bookings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    session_id INT,
    user_id INT,
    booking_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES sessions(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Insert admin user
INSERT INTO users (name, username, password, role)
VALUES ('System Admin', 'admin', 'admin123', 'Admin');

-- Insert trainer user
INSERT INTO users (name, username, password, role)
VALUES ('John Trainer', 'trainer1', 'trainer123', 'Trainer');

-- Insert member user
INSERT INTO users (name, username, password, role)
VALUES ('Alice Member', 'member1', 'member123', 'Member');

-- Insert rooms
INSERT INTO rooms (name, capacity) VALUES
('Yoga Room', 15),
('Main Gym', 30);

-- Insert session types
INSERT INTO session_types (name, description) VALUES
('Yoga', 'A relaxing yoga class'),
('HIIT', 'High Intensity Interval Training');

-- Insert sessions (trainer_id = 2 refers to John Trainer)
INSERT INTO sessions (type_id, trainer_id, room_id, start_time, end_time) VALUES
(1, 2, 1, '2025-05-10 10:00:00', '2025-05-10 11:00:00'),
(2, 2, 2, '2025-05-11 16:00:00', '2025-05-11 17:00:00');