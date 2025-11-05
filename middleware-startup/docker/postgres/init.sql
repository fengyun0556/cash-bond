CREATE DATABASE "trade-link";
CREATE DATABASE "tps2";

GRANT ALL PRIVILEGES ON DATABASE "trade-link" TO postgres;
GRANT ALL PRIVILEGES ON DATABASE tps2 TO postgres;

\c trade-link

CREATE TABLE order_detail (
    order_id BIGSERIAL PRIMARY KEY,
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
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE rule_check_detail (
    id BIGSERIAL PRIMARY KEY,
    rule_id VARCHAR(50),
    order_id BIGINT NOT NULL,
    rule_check_result VARCHAR(20),
    rule_describe VARCHAR(500),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE order_execution_detail (
    execution_id BIGSERIAL PRIMARY KEY,
    bbg_execution_id VARCHAR(255),
    trade_link_id BIGINT,
    executed_quantity INTEGER,
    executed_price NUMERIC(20, 4),
    create_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    update_time TIMESTAMP WITHOUT TIME ZONE
);

\c tps2
CREATE TABLE tps2_order_detail (
    tps2_id BIGSERIAL PRIMARY KEY,
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
    quantity INTEGER NOT NULL,
    price DECIMAL(20,4),
    create_time TIMESTAMP,
    update_time TIMESTAMP
);


CREATE TABLE bbg_execution_detail (
    bbg_execution_id BIGSERIAL PRIMARY KEY,
    bbg_message_id VARCHAR(255),
    trade_link_id BIGINT,
    account_key VARCHAR(255),
    executed_quantity INTEGER,
    executed_price DECIMAL(19, 2),
    create_time TIMESTAMP,
    update_time TIMESTAMP
);

CREATE TABLE ptb_detail (
    ptb_id BIGSERIAL PRIMARY KEY,
    bbg_execution_id BIGINT,
    account_key VARCHAR(50),
    account_name VARCHAR(100),
    member_key VARCHAR(50),
    member_name VARCHAR(100),
    cash_account VARCHAR(50),
    isin VARCHAR(12),
    executed_quantity INTEGER,
    executed_price DECIMAL,
    settlement_id BIGINT,
    create_time TIMESTAMP,
    update_time TIMESTAMP
);
