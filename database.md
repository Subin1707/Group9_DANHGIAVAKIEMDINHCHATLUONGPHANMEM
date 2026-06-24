DROP DATABASE IF EXISTS hospital;
CREATE DATABASE hospital CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE hospital;

CREATE TABLE login (
    username   VARCHAR(50)  PRIMARY KEY,
    password   VARCHAR(100) NOT NULL,
    role       VARCHAR(20)  NOT NULL DEFAULT 'USER',
    patientId  VARCHAR(50)  NULL,
    created_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO login (username, password, role) VALUES ('admin', '123456', 'ADMIN');

CREATE TABLE patient (
    id      VARCHAR(50)  PRIMARY KEY,
    name    VARCHAR(100) NOT NULL,
    dob     DATE         NOT NULL,
    age     INT          CHECK (age >= 0),
    gender  ENUM('Nam', 'Nữ', 'Khác') NOT NULL,
    address VARCHAR(255),
    phone   VARCHAR(20)
);

INSERT INTO patient (id, name, dob, age, gender, address, phone) VALUES
('P001', 'Nguyễn Văn Thành', '2005-02-19', 20, 'Nam', 'Hà Nội', '0987654321'),
('P002', 'Trần Thị Bình',   '2000-11-02', 24, 'Nữ',  'Đà Nẵng', '0988888888');

CREATE TABLE room (
    id         VARCHAR(20)  PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    doctorName VARCHAR(100) NOT NULL,
    capacity   INT          DEFAULT NULL CHECK (capacity >= 0),
    status     VARCHAR(50)  DEFAULT 'Hoạt động'
);

INSERT INTO room (id, name, doctorName, capacity, status) VALUES
('R001', 'Phòng Nội trú A', 'BS. Lê Minh',    20, 'Hoạt động'),
('R002', 'Phòng Cấp cứu',   'BS. Nguyễn Lan', 10, 'Bảo trì'),
('R003', 'Phòng Hồi sức',   'BS. Phạm Quốc',  15, 'Hoạt động');

CREATE TABLE benhAn (
    id         VARCHAR(50) PRIMARY KEY,
    patientId  VARCHAR(50) NOT NULL,
    ngayKham   DATE        NOT NULL,
    trieuChung TEXT,
    tienSuBenh TEXT,
    chanDoan   TEXT,
    roomId     VARCHAR(50),
    CONSTRAINT fk_benhan_patient
        FOREIGN KEY (patientId) REFERENCES patient(id) ON DELETE CASCADE,
    CONSTRAINT fk_benhan_room
        FOREIGN KEY (roomId) REFERENCES room(id) ON DELETE SET NULL
);

INSERT INTO benhAn (id, patientId, ngayKham, trieuChung, tienSuBenh, chanDoan, roomId) VALUES
('BA001', 'P001', '2025-06-01', 'Sốt, ho',    'Tiền sử hen suyễn', 'Viêm phổi', 'R001'),
('BA002', 'P002', '2025-06-10', 'Đau bụng',   'Không rõ',         'Viêm ruột', 'R002');

CREATE TABLE schedule (
    id       VARCHAR(50) PRIMARY KEY,
    benhanId VARCHAR(50) NOT NULL,
    patientId VARCHAR(50) NOT NULL,
    date     DATE        NOT NULL,
    tenthuoc VARCHAR(255),
    soluong  VARCHAR(50),
    CONSTRAINT fk_schedule_benhan
        FOREIGN KEY (benhanId) REFERENCES benhAn(id) ON DELETE CASCADE,
    CONSTRAINT fk_schedule_patient
        FOREIGN KEY (patientId) REFERENCES patient(id) ON DELETE CASCADE
);

INSERT INTO schedule (id, benhanId, patientId, date, tenthuoc, soluong) VALUES
('BT001', 'BA001', 'P001', '2025-06-02', 'Paracetamol', '10 viên'),
('BT002', 'BA002', 'P002', '2025-06-11', 'Smecta',      '5 viên');

CREATE TABLE appointment (
    id VARCHAR(50) PRIMARY KEY,
    patientId VARCHAR(50) NOT NULL,
    roomId VARCHAR(20) NULL,
    appointmentTime DATETIME NOT NULL,
    note TEXT,
    status ENUM('PENDING','CONFIRMED','CANCELLED') NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_appointment_patient
        FOREIGN KEY (patientId) REFERENCES patient(id) ON DELETE CASCADE,
    CONSTRAINT fk_appointment_room
        FOREIGN KEY (roomId) REFERENCES room(id) ON DELETE SET NULL
);

INSERT INTO appointment (id, patientId, roomId, appointmentTime, note, status) VALUES
('AP001', 'P001', 'R001', '2026-04-02 09:00:00', 'Ho, sốt nhẹ', 'PENDING'),
('AP002', 'P002', 'R002', '2026-04-03 14:30:00', 'Đau bụng', 'CONFIRMED');

CREATE INDEX idx_appointment_patient ON appointment(patientId);
CREATE INDEX idx_appointment_time ON appointment(appointmentTime);
CREATE INDEX idx_appointment_status ON appointment(status);
CREATE INDEX idx_benhan_patientId ON benhAn(patientId);
CREATE INDEX idx_benhan_roomId    ON benhAn(roomId);
CREATE INDEX idx_schedule_benhanId ON schedule(benhanId);
CREATE INDEX idx_schedule_patientId ON schedule(patientId);
CREATE INDEX idx_schedule_date ON schedule(date);
