CREATE TABLE user_credentials (
    user_id       BIGINT PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    password_hash VARCHAR(255) NOT NULL,
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 初期管理ユーザー（bcrypt rounds=10、平文: "admin"）
INSERT INTO users (name, email, created_at, updated_at)
VALUES ('admin', 'admin@example.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO user_credentials (user_id, password_hash, created_at, updated_at)
SELECT id,
       '$2a$10$pcUixkg46sBY9IxlYdz1UemalgvlwEkUi5T7hofcqVY3DdZsknzvO',
       CURRENT_TIMESTAMP,
       CURRENT_TIMESTAMP
FROM users
WHERE email = 'admin@example.com';
