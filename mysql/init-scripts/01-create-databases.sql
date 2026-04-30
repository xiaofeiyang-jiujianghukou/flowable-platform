CREATE DATABASE IF NOT EXISTS user_center
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci;

GRANT ALL PRIVILEGES ON user_center.* TO 'flowable'@'%';