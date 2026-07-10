SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS patient (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    dob DATE NOT NULL,
    age INT,
    gender VARCHAR(20) NOT NULL,
    address VARCHAR(255),
    phone VARCHAR(20)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS room (
    id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    doctorName VARCHAR(100) NOT NULL,
    capacity INT DEFAULT NULL,
    status VARCHAR(50) DEFAULT 'Hoat dong'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS login (
    username VARCHAR(50) PRIMARY KEY,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    patientId VARCHAR(50) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_login_patient FOREIGN KEY (patientId) REFERENCES patient(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS benhAn (
    id VARCHAR(50) PRIMARY KEY,
    patientId VARCHAR(50) NOT NULL,
    ngayKham DATE NOT NULL,
    trieuChung TEXT,
    tienSuBenh TEXT,
    chanDoan TEXT,
    roomId VARCHAR(20),
    CONSTRAINT fk_benhan_patient FOREIGN KEY (patientId) REFERENCES patient(id) ON DELETE CASCADE,
    CONSTRAINT fk_benhan_room FOREIGN KEY (roomId) REFERENCES room(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS schedule (
    id VARCHAR(50) PRIMARY KEY,
    benhanId VARCHAR(50) NOT NULL,
    patientId VARCHAR(50) NOT NULL,
    date DATE NOT NULL,
    tenthuoc VARCHAR(255),
    soluong VARCHAR(50),
    CONSTRAINT fk_schedule_benhan FOREIGN KEY (benhanId) REFERENCES benhAn(id) ON DELETE CASCADE,
    CONSTRAINT fk_schedule_patient FOREIGN KEY (patientId) REFERENCES patient(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS appointment (
    id VARCHAR(50) PRIMARY KEY,
    patientId VARCHAR(50) NOT NULL,
    roomId VARCHAR(20),
    appointmentTime DATETIME NOT NULL,
    note TEXT,
    status ENUM('PENDING', 'CONFIRMED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_appointment_patient FOREIGN KEY (patientId) REFERENCES patient(id) ON DELETE CASCADE,
    CONSTRAINT fk_appointment_room FOREIGN KEY (roomId) REFERENCES room(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT IGNORE INTO patient (id, name, dob, age, gender, address, phone) VALUES
('P001', 'Nguyen Van Thanh', '2005-02-19', 21, 'Nam', 'Ha Noi', '0987654321'),
('P002', 'Tran Thi Binh', '2000-11-02', 25, 'Nu', 'Da Nang', '0988888888');

INSERT IGNORE INTO room (id, name, doctorName, capacity, status) VALUES
('R001', 'Phong Noi tru A', 'BS. Le Minh', 20, 'Hoat dong'),
('R002', 'Phong Cap cuu', 'BS. Nguyen Lan', 10, 'Bao tri');

INSERT IGNORE INTO login (username, password, role, patientId) VALUES
('admin', '123456', 'ADMIN', NULL),
('patient1', '123456', 'USER', 'P001'),
('patient2', '123456', 'USER', 'P002');

INSERT IGNORE INTO benhAn (id, patientId, ngayKham, trieuChung, tienSuBenh, chanDoan, roomId) VALUES
('BA001', 'P001', '2025-06-01', 'Sot, ho', 'Tien su hen suyen', 'Viem phoi', 'R001');

INSERT IGNORE INTO schedule (id, benhanId, patientId, date, tenthuoc, soluong) VALUES
('BT001', 'BA001', 'P001', '2025-06-02', 'Paracetamol', '10 vien');
