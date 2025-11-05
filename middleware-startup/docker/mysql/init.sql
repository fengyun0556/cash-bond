CREATE DATABASE IF NOT EXISTS `trade-link` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS `tps2` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建用户并授权（MySQL中需要先创建用户再授权）
CREATE USER IF NOT EXISTS 'admin'@'%' IDENTIFIED BY 'password123';
GRANT ALL PRIVILEGES ON `trade-link`.* TO 'admin'@'%';
GRANT ALL PRIVILEGES ON `tps2`.* TO 'admin'@'%';
FLUSH PRIVILEGES;

-- 使用 trade-link 数据库
USE `trade-link`;

CREATE TABLE IF NOT EXISTS order_detail (
    order_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_key VARCHAR(50),
    account_name VARCHAR(100),
    member_key VARCHAR(50),
    member_name VARCHAR(100),
    cash_account VARCHAR(50),
    isin VARCHAR(12),
    isin_name VARCHAR(100),
    exchange_code VARCHAR(10),
    order_type VARCHAR(20),
    quantity INT,
    total_executed_quantity INT,
    price DECIMAL(20, 4),
    order_request_mode VARCHAR(20),
    tps2_id BIGINT,
    order_state VARCHAR(20),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_account_key (account_key),
    KEY idx_isin (isin),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS rule_check_detail (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rule_id VARCHAR(50),
    order_id BIGINT NOT NULL,
    rule_check_result VARCHAR(20),
    rule_describe VARCHAR(500),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_order_id (order_id),
    KEY idx_rule_id (rule_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS order_execution_detail (
    execution_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bbg_execution_id VARCHAR(255),
    trade_link_id BIGINT,
    executed_quantity INT,
    executed_price DECIMAL(20, 4),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_bbg_execution_id (bbg_execution_id),
    KEY idx_trade_link_id (trade_link_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 使用 tps2 数据库
USE `tps2`;

CREATE TABLE IF NOT EXISTS tps2_order_detail (
    tps2_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    trade_link_id BIGINT,
    account_key VARCHAR(50),
    account_name VARCHAR(100),
    member_key VARCHAR(50),
    member_name VARCHAR(100),
    cash_account VARCHAR(50),
    isin VARCHAR(12),
    isin_name VARCHAR(100),
    order_state VARCHAR(255),
    exchange_code VARCHAR(10),
    order_type VARCHAR(20),
    quantity INT NOT NULL,
    price DECIMAL(20,4),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_trade_link_id (trade_link_id),
    KEY idx_account_key (account_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS bbg_execution_detail (
    bbg_execution_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bbg_message_id VARCHAR(255),
    trade_link_id BIGINT,
    account_key VARCHAR(255),
    executed_quantity INT,
    executed_price DECIMAL(19, 2),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_bbg_message_id (bbg_message_id),
    KEY idx_trade_link_id (trade_link_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS ptb_detail (
    ptb_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bbg_execution_id BIGINT,
    account_key VARCHAR(50),
    account_name VARCHAR(100),
    member_key VARCHAR(50),
    member_name VARCHAR(100),
    cash_account VARCHAR(50),
    isin VARCHAR(12),
    executed_quantity INT,
    executed_price DECIMAL(19, 4),
    settlement_id BIGINT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_bbg_execution_id (bbg_execution_id),
    KEY idx_account_key (account_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
