
CREATE DATABASE inventorydb;
USE inventorydb;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE inventory (
    id INT AUTO_INCREMENT PRIMARY KEY,
    item_name VARCHAR(100),
    item_code VARCHAR(50),
    quantity INT,
    price_per_unit DOUBLE,
    supplier_details TEXT
);
