-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    role VARCHAR(20) DEFAULT 'user'
);

-- Insert test accounts (BCrypt hashed passwords)

-- Password: admin123
INSERT INTO users (username, password, email, role) VALUES
('admin', '$2a$10$H3r4U6r1ZRMxHtkobaN6ueT0uE43W2qFio9pBKYVYf/3idH7g8S1G', 'admin@example.com', 'admin');

-- Password: user12345
INSERT INTO users (username, password, email, role) VALUES
('user1', '$2a$10$w8ZGavGk6scPhqcdJXSSXek14JbVbvvHJkNjOnxRVrO9V6vQcmawc', 'user1@example.com', 'user');
