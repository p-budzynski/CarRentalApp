--liquibase formatted sql
--changeset CarRentalApp:2


INSERT INTO cars (producer, model, year_of_production, registration_number, price_per_day) VALUES
('Toyota', 'Corolla', 2022, 'WA12345', 150.00),
('BMW', 'X3', 2021, 'KR67890', 320.00),
('Volkswagen', 'Golf', 2020, 'GD11111', 180.00),
('Audi', 'A4', 2023, 'WR22222', 280.00),
('Ford', 'Focus', 2019, 'PO33333', 140.00),
('Mercedes', 'C-Class', 2022, 'WA44444', 350.00),
('Skoda', 'Octavia', 2021, 'KR55555', 160.00),
('Opel', 'Astra', 2020, 'GD66666', 130.00),
('Renault', 'Megane', 2019, 'WR77777', 125.00),
('Peugeot', '308', 2023, 'PO88888', 170.00);


INSERT INTO positions (name) VALUES
('Manager'),
('Rental Consultant'),
('Mechanic'),
('Accountant'),
('Receptionist');


INSERT INTO employees (first_name, last_name, position_id, phone_number, e_mail) VALUES
('Marek', 'Kowalski', 1, '501234567', 'marek.kowalski@carrent.pl'),
('Anna', 'Nowak', 2, '502345678', 'anna.nowak@carrent.pl'),
('Piotr', 'Wiśniewski', 2, '503456789', 'piotr.wisniewski@carrent.pl'),
('Katarzyna', 'Dąbrowska', 4, '504567890', 'katarzyna.dabrowska@carrent.pl');


INSERT INTO customers (first_name, last_name, e_mail, phone_number, driving_license_number) VALUES
('Jan', 'Kowalczyk', 'jan.kowalczyk@email.com', '600123456', 'PKK123456789'),
('Maria', 'Wójcik', 'maria.wojcik@gmail.com', '601234567', 'WAW987654321'),
('Tomasz', 'Lewandowski', 'tomasz.lewandowski@onet.pl', '602345678', 'KRK555666777'),
('Agnieszka', 'Zielińska', 'agnieszka.zielinska@wp.pl', '603456789', 'GDA111222333'),
('Michał', 'Szymański', 'michal.szymanski@interia.pl', '604567890', 'POZ444555666');


INSERT INTO reservations (car_id, customer_id, start_date, end_date, total_amount, status) VALUES
(1, 1, '2025-06-10', '2025-06-13', 450.00, 'FINISHED'),
(2, 2, '2025-06-12', '2025-06-15', 960.00, 'FINISHED'),
(3, 3, '2025-06-08', '2025-06-10', 360.00, 'CANCELED'),
(5, 4, '2025-06-15', '2025-06-18', 420.00, 'FINISHED'),
(6, 5, '2025-06-20', '2025-06-25', 1750.00, 'FINISHED'),
(7, 1, '2025-06-25', '2025-06-27', 320.00, 'FINISHED'),
(9, 2, '2025-06-18', '2025-06-20', 250.00, 'CANCELED'),
(10, 3, '2025-07-22', '2025-06-24', 340.00, 'RESERVED'),
(1, 4, '2025-07-28', '2025-07-30', 300.00, 'RESERVED'),
(3, 5, '2025-07-05', '2025-07-15', 920.00, 'RENTED'),
(4, 5, '2025-08-12', '2025-08-16', 720.00, 'RESERVED');
