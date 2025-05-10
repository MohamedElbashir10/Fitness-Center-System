-- Create and use the database
CREATE DATABASE IF NOT EXISTS fcsdb;
USE fcsdb;

-- Users table 
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL, -- 'Member', 'Trainer', or 'Admin'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE rooms (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    capacity INT
);

CREATE TABLE session_types (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    description TEXT
);


CREATE TABLE sessions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    type_id INT,
    trainer_id INT,
    room_id INT,
    start_time DATETIME,
    end_time DATETIME,
    FOREIGN KEY (type_id) REFERENCES session_types(id),
    FOREIGN KEY (trainer_id) REFERENCES trainers(id),
    FOREIGN KEY (room_id) REFERENCES rooms(id)
);

CREATE TABLE bookings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    session_id INT,
    user_id INT,
    booking_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES sessions(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Admin user
INSERT INTO users (name, username, password, role)
VALUES ('System Admin', 'admin', 'admin123', 'Admin');

-- Trainer user
INSERT INTO users (name, username, password, role)
VALUES ('John Trainer', 'trainer1', 'trainer123', 'Trainer');

-- Member user
INSERT INTO users (name, username, password, role)
VALUES ('Alice Member', 'member1', 'member123', 'Member');

INSERT INTO rooms (name, capacity) VALUES
('Yoga Room', 15),
('Main Gym', 30);

INSERT INTO session_types (name, description) VALUES
('Yoga', 'A relaxing yoga class'),
('HIIT', 'High Intensity Interval Training');

INSERT INTO sessions (type_id, trainer_id, room_id, start_time, end_time) VALUES
(1, 1, 1, '2025-05-10 10:00:00', '2025-05-10 11:00:00'),
(2, 1, 2, '2025-05-11 16:00:00', '2025-05-11 17:00:00');

