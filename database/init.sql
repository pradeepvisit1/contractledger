-- Run this ONCE after Spring Boot starts for the first time
-- Spring Boot auto-creates all tables via ddl-auto=update

-- Insert default users (password = "Admin@2024" for all)
-- BCrypt hash of "Admin@2024"
INSERT IGNORE INTO users (username, password, full_name, phone, role, active, created_at) VALUES
('admin',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Project Admin',  '9999999999', 'ADMIN', 1, NOW()),
('pradeep', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Pradeep Kumar',  '8888888888', 'USER',  1, NOW()),
('user2',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'User Two',       '7777777777', 'USER',  1, NOW()),
('user3',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'User Three',     '6666666666', 'USER',  1, NOW()),
('user4',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'User Four',      '5555555555', 'USER',  1, NOW());

-- Default password for all users: Admin@2024
-- CHANGE PASSWORDS after first login!
