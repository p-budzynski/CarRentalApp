--liquibase formatted sql
--changeset CarRentalApp:1


CREATE TABLE cars (
    id BIGSERIAL PRIMARY KEY,
    producer VARCHAR(255) NOT NULL,
    model VARCHAR(255) NOT NULL,
    year_of_production INTEGER NOT NULL,
    registration_number VARCHAR(10) NOT NULL UNIQUE,
    price_per_day NUMERIC(6,2) NOT NULL
);


CREATE TABLE customers (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    e_mail VARCHAR(255) NOT NULL UNIQUE,
    phone_number VARCHAR(255) NOT NULL UNIQUE,
    driving_license_number VARCHAR(255) NOT NULL UNIQUE
);


CREATE TABLE positions (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);


CREATE TABLE employees (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    position_id BIGINT NOT NULL,
    phone_number VARCHAR(255) NOT NULL UNIQUE,
    e_mail VARCHAR(255) NOT NULL UNIQUE,
    CONSTRAINT fk_position
        FOREIGN KEY (position_id)
        REFERENCES positions(id)
        ON DELETE RESTRICT
);


CREATE TABLE statuses (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);


CREATE TABLE reservations (
    id BIGSERIAL PRIMARY KEY,
    car_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    total_amount NUMERIC(8,2) NOT NULL,
    status_id BIGINT NOT NULL,
    CONSTRAINT fk_car
        FOREIGN KEY (car_id)
        REFERENCES cars(id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_customer
        FOREIGN KEY (customer_id)
        REFERENCES customers(id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_status
        FOREIGN KEY (status_id)
        REFERENCES statuses(id)
        ON DELETE RESTRICT
);
