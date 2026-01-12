
use link_management;
-- 创建数据库
CREATE DATABASE IF NOT EXISTS link_management DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE link_management;

# drop table if exists link_info;
-- 创建链路信息表
CREATE TABLE IF NOT EXISTS link_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '链路ID',
    link_type ENUM('PRIMARY', 'SECONDARY') NOT NULL COMMENT '链路类型：一级/多级',
    ip_address VARCHAR(45) NULL COMMENT 'IP地址',
    ip_address_2 VARCHAR(45) NULL COMMENT '多级跳IP地址',
    region_path VARCHAR(255) NULL COMMENT '跳转地区',
    purchase_time DATETIME NOT NULL COMMENT '购买时间',
    expire_time DATETIME NOT NULL COMMENT '有效时间',
    price DECIMAL(10, 2) NOT NULL COMMENT '价格',
    vps_provider VARCHAR(100) NOT NULL COMMENT 'VPS商家信息',
    purchaser VARCHAR(50) NOT NULL COMMENT '采购者',
    jump_count INT NOT NULL COMMENT '跳转次数（一级:1, 多级:2-4）',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除'
) COMMENT '链路信息表';

-- 创建用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    nickname VARCHAR(50) NOT NULL COMMENT '昵称',
    email VARCHAR(100) NOT NULL COMMENT '邮箱',
    password VARCHAR(100) NOT NULL COMMENT '密码（加密存储）',
    role ENUM('ADMIN', 'USER', 'OPERATOR') NOT NULL COMMENT '角色：管理员/普通用户/操作员',
    status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：启用/禁用',
    last_login_at DATETIME NULL COMMENT '最后登录时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除'
) COMMENT '用户信息表';

-- 创建操作日志表
CREATE TABLE IF NOT EXISTS operation_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
    user_id BIGINT NOT NULL COMMENT '操作用户ID',
    operation_type VARCHAR(50) NOT NULL COMMENT '操作类型',
    link_id BIGINT NULL COMMENT '关联链路ID',
    description TEXT NULL COMMENT '操作描述',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_operation_type (operation_type),
    INDEX idx_created_at (created_at)
) COMMENT '操作日志表';

-- 插入初始数据
INSERT INTO sys_user (username, nickname, email, password, role, status) VALUES
('admin', '系统管理员', 'admin@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'ADMIN', 'ACTIVE'),
('test', '测试用户', 'test@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'USER', 'ACTIVE')
ON DUPLICATE KEY UPDATE updated_at = CURRENT_TIMESTAMP;

-- 插入测试链路数据
INSERT INTO link_info (link_type, purchase_time, expire_time, price, vps_provider, purchaser, jump_count, ip_address, ip_address_2, region_path) VALUES
('PRIMARY', '2024-01-15 10:30:00', '2025-01-15 10:30:00', 299.99, '阿里云', '张三', 1, '192.168.1.100', NULL, '北京-东京'),
('SECONDARY', '2024-02-20 14:20:00', '2025-02-20 14:20:00', 199.99, '腾讯云', '李四', 2, '192.168.1.200', '192.168.2.200', '上海-首尔-洛杉矶'),
('PRIMARY', '2024-03-10 09:15:00', '2025-03-10 09:15:00', 399.99, '华为云', '王五', 1, '192.168.1.300', NULL, '广州-新加坡'),
('SECONDARY', '2024-04-05 16:45:00', '2025-04-05 16:45:00', 249.99, 'AWS', '赵六', 3, '192.168.1.400', '192.168.2.400', '香港-伦敦-纽约-多伦多'),
('PRIMARY', '2024-05-12 11:30:00', '2025-05-12 11:30:00', 349.99, 'Azure', '钱七', 1, '192.168.1.500', NULL, '深圳-法兰克福')
ON DUPLICATE KEY UPDATE updated_at = CURRENT_TIMESTAMP;

ALTER TABLE sys_user
    ADD COLUMN nickname VARCHAR(50) NOT NULL DEFAULT '用户' COMMENT '昵称',
    ADD COLUMN email VARCHAR(100) NOT NULL DEFAULT '' COMMENT '邮箱',
    ADD COLUMN status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：启用/禁用',
    ADD COLUMN last_login_at DATETIME NULL COMMENT '最后登录时间';

DESC sys_user;

-- 重新创建表（使用我们更新后的结构）
ALTER TABLE sys_user
    MODIFY COLUMN role ENUM('ADMIN', 'USER', 'OPERATOR') NOT NULL COMMENT '角色：管理员/普通用户/操作员';

SELECT username, role, status FROM sys_user WHERE username = 'admin';

INSERT INTO sys_user (username, nickname, email, password, role, status) VALUES
    ('zhangsan', '张三', 'zhangsan@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'USER', 'ACTIVE');

INSERT INTO sys_user (username, nickname, email, password, role, status) VALUES
    ('operator2', '操作员2', 'operator1@example.com', 'op2123', 'OPERATOR', 'ACTIVE');

INSERT INTO sys_user (username, nickname, email, password, role, status) VALUES
    ('admin123', '管理员', 'operator1@example.com', 'admin123', 'ADMIN', 'ACTIVE');

# select * from sys_user;
#
 TRUNCATE TABLE sys_user;
# TRUNCATE table link_info;
