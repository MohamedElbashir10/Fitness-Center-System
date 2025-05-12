-- Initialize the fcsDatabase for the Fitness Center System
-- Drops existing database and tables to ensure a clean setup
DROP DATABASE IF EXISTS fcsDatabase;
CREATE DATABASE fcsDatabase;
USE fcsDatabase;

-- Table: users

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('Member', 'Trainer', 'Admin') NOT NULL
);

-- Table: rooms

CREATE TABLE rooms (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    capacity INT NOT NULL,
    description TEXT
);

-- Table: session_types

CREATE TABLE session_types (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT
);

-- Table: availability

CREATE TABLE availability (
    id INT AUTO_INCREMENT PRIMARY KEY,
    trainer_id INT NOT NULL,
    date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    FOREIGN KEY (trainer_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Table: sessions

CREATE TABLE sessions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    type_id INT NOT NULL,
    trainer_id INT NOT NULL,
    room_id INT NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    FOREIGN KEY (type_id) REFERENCES session_types(id) ON DELETE RESTRICT,
    FOREIGN KEY (trainer_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE RESTRICT
);

-- Table: bookings

CREATE TABLE bookings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    session_id INT NOT NULL,
    user_id INT NOT NULL,
    booking_time DATETIME NOT NULL,
    FOREIGN KEY (session_id) REFERENCES sessions(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Demo Data: Users

INSERT INTO users (name, username, password, role) VALUES
    ('Admin One', 'admin1', 'admin123', 'Admin'),
    ('Trainer One', 'trainer1', 'trainer123', 'Trainer'),
    ('Trainer Two', 'trainer2', 'trainer123', 'Trainer'),
    ('Member One', 'member1', 'member123', 'Member'),
    ('Member Two', 'member2', 'member123', 'Member');

-- Demo Data: Rooms

INSERT INTO rooms (name, capacity, description) VALUES
    ('Room 1', 20, 'Main fitness room with yoga mats'),
    ('Room 2', 15, 'Small room for group classes'),
    ('Room 3', 25, 'Large room with weight equipment');

-- Demo Data: Session Types

INSERT INTO session_types (name, description) VALUES
    ('Yoga', 'Relaxation and flexibility training'),
    ('Zumba', 'High-energy dance fitness'),
    ('Strength Training', 'Weightlifting and resistance exercises');

-- Demo Data: Availability

INSERT INTO availability (trainer_id, date, start_time, end_time) VALUES
    ((SELECT id FROM users WHERE username = 'trainer1'), '2025-05-15', '08:00:00', '12:00:00'),
    ((SELECT id FROM users WHERE username = 'trainer1'), '2025-05-15', '14:00:00', '18:00:00'),
    ((SELECT id FROM users WHERE username = 'trainer2'), '2025-05-16', '09:00:00', '13:00:00'),
    ((SELECT id FROM users WHERE username = 'trainer2'), '2025-05-16', '15:00:00', '19:00:00');

-- Demo Data: Sessions

INSERT INTO sessions (type_id, trainer_id, room_id, start_time, end_time) VALUES
    ((SELECT id FROM session_types WHERE name = 'Yoga'), 
     (SELECT id FROM users WHERE username = 'trainer1'), 
     (SELECT id FROM rooms WHERE name = 'Room 1'), 
     '2025-05-15 10:00:00', '2025-05-15 11:00:00'),
    ((SELECT id FROM session_types WHERE name = 'Zumba'), 
     (SELECT id FROM users WHERE username = 'trainer2'), 
     (SELECT id FROM rooms WHERE name = 'Room 2'), 
     '2025-05-16 11:00:00', '2025-05-16 12:00:00'),
    ((SELECT id FROM session_types WHERE name = 'Strength Training'), 
     (SELECT id FROM users WHERE username = 'trainer1'), 
     (SELECT id FROM rooms WHERE name = 'Room 3'), 
     '2025-05-15 14:00:00', '2025-05-15 15:00:00');

-- Demo Data: Bookings

INSERT INTO bookings (session_id, user_id, booking_time) VALUES
    ((SELECT id FROM sessions WHERE start_time = '2025-05-15 10:00:00'), 
     (SELECT id FROM users WHERE username = 'member1'), 
     '2025-05-12 09:00:00'),
    ((SELECT id FROM sessions WHERE start_time = '2025-05-16 11:00:00'), 
     (SELECT id FROM users WHERE username = 'member2'), 
     '2025-05-12 10:00:00');