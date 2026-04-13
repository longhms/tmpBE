CREATE TABLE IF NOT EXISTS departments (
    department_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    department_name VARCHAR(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
INSERT INTO departments (department_name) VALUES
('IT'),
('HR'),
('Accounting'),
('Sales'),
('Admin');

CREATE TABLE IF NOT EXISTS employees (
    employee_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    department_id BIGINT NOT NULL,
    employee_name VARCHAR(255) NOT NULL,
    employee_name_kana VARCHAR(255),
    employee_birth_date DATE,
    employee_email VARCHAR(255) NOT NULL,
    employee_telephone VARCHAR(50),
    employee_login_id VARCHAR(50) NOT NULL,
    employee_login_password VARCHAR(100),

    CONSTRAINT fk_employee_department
        FOREIGN KEY (department_id)
        REFERENCES departments(department_id)
);
INSERT INTO employees (department_id, employee_name, employee_email, employee_login_id, employee_login_password)
VALUES (1, 'Administrator', 'la@luvina.net', 'admin', '$2a$10$r.XIN4K9vTioiuYQwaTop.UVQ5r5FvrKk2V5Orm9Hc6n4i9Tvjthy');
INSERT INTO employees
(department_id, employee_name, employee_name_kana, employee_birth_date, employee_email, employee_telephone, employee_login_id, employee_login_password)
VALUES
(1, 'Nguyen Van A', 'グエン・ヴァン・ア', '2000-01-01', 'a@gmail.com', '0901234567', 'user_a', '$2a$10$7QJ3h8K2wTnF9mV8z1KkUeP7xZ8F6dQ1YlGzv9J0Y2ZQk3YwZ8e6K'),
(2, 'Tran Thi B', 'チャン・ティ・ビー', '1999-05-10', 'b@gmail.com', '0912345678', 'user_b', '$2a$10$7QJ3h8K2wTnF9mV8z1KkUeP7xZ8F6dQ1YlGzv9J0Y2ZQk3YwZ8e6K'),
(1, 'Le Van C', 'レ・ヴァン・シー', '1998-07-20', 'c@gmail.com', '0923456789', 'user_c', '$2a$10$7QJ3h8K2wTnF9mV8z1KkUeP7xZ8F6dQ1YlGzv9J0Y2ZQk3YwZ8e6K'),
(1, 'Pham Van D', 'ファム・ヴァン・ズン', '1997-03-12', 'd@gmail.com', '0931111111', 'user_d', '$2a$10$hash'),
(2, 'Hoang Thi E', 'ホアン・ティ・エ', '1996-08-22', 'e@gmail.com', '0932222222', 'user_e', '$2a$10$hash'),
(3, 'Do Van F', 'ド・ヴァン・エフ', '1995-11-05', 'f@gmail.com', '0933333333', 'user_f', '$2a$10$hash'),
(4, 'Bui Thi G', 'ブイ・ティ・ジー', '1998-02-14', 'g@gmail.com', '0934444444', 'user_g', '$2a$10$hash'),
(5, 'Nguyen Van H', 'グエン・ヴァン・エイチ', '1999-09-09', 'h@gmail.com', '0935555555', 'user_h', '$2a$10$hash'),
(1, 'Tran Van I', 'チャン・ヴァン・アイ', '1994-12-01', 'i@gmail.com', '0936666666', 'user_i', '$2a$10$hash'),
(2, 'Le Thi K', 'レ・ティ・ケー', '1997-07-17', 'k@gmail.com', '0937777777', 'user_k', '$2a$10$hash'),
(3, 'Phan Van L', 'ファン・ヴァン・エル', '1993-05-25', 'l@gmail.com', '0938888888', 'user_l', '$2a$10$hash'),
(4, 'Dang Thi M', 'ダン・ティ・エム', '1996-10-30', 'm@gmail.com', '0939999999', 'user_m', '$2a$10$hash'),
(5, 'Vu Van N', 'ヴ・ヴァン・エヌ', '1992-01-19', 'n@gmail.com', '0940000000', 'user_n', '$2a$10$hash');

CREATE TABLE IF NOT EXISTS certifications (
    certification_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    certification_name VARCHAR(50) NOT NULL,
    certification_level INT NOT NULL
);
INSERT INTO certifications (certification_name, certification_level) VALUES
('Trình độ tiếng nhật cấp 1', 1),
('Trình độ tiếng nhật cấp 2', 2),
('Trình độ tiếng nhật cấp 3', 3),
('Trình độ tiếng nhật cấp 4', 4),
('Trình độ tiếng nhật cấp 5', 5);

CREATE TABLE IF NOT EXISTS employees_certifications (
    employee_certification_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    certification_id BIGINT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    score DECIMAL(5,2) NOT NULL,

    CONSTRAINT fk_emp_cert_employee
        FOREIGN KEY (employee_id)
        REFERENCES employees(employee_id),

    CONSTRAINT fk_emp_cert_certification
        FOREIGN KEY (certification_id)
        REFERENCES certifications(certification_id)
);
INSERT INTO employees_certifications
(employee_id, certification_id, start_date, end_date, score)
VALUES
(1, 3, '2022-01-01', '2025-01-01', 85.50),
(2, 4, '2021-03-15', '2024-03-15', 70.25),
(3, 5, '2020-06-10', '2023-06-10', 60.00);

INSERT INTO employees_certifications
(employee_id, certification_id, start_date, end_date, score)
VALUES
(12, 2, '2022-05-01', '2025-05-01', 88.00),
(14, 1, '2021-07-01', '2024-07-01', 92.50),
(6, 3, '2023-01-10', '2026-01-10', 76.75),
(7, 4, '2020-09-09', '2023-09-09', 65.00),
(8, 2, '2022-11-11', '2025-11-11', 81.25),
(9, 5, '2021-12-12', '2024-12-12', 55.50),
(10, 3, '2023-03-03', '2026-03-03', 79.00);
