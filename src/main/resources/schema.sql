-- Script de inicialización para la base de datos de FluxPay
CREATE TABLE IF NOT EXISTS transfers (
    id VARCHAR(36) PRIMARY KEY,
    origin_account VARCHAR(50) NOT NULL,
    destination_account VARCHAR(50) NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL
);