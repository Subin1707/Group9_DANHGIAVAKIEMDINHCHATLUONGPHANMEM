# Hệ thống quản lý bệnh nhân tích hợp AI Chatbot hỗ trợ tư vấn và quản lý điều trị

Đồ án xây dựng một hệ thống web hỗ trợ quản lý bệnh nhân, hồ sơ bệnh án, phòng điều trị, lịch khám và lịch cấp thuốc. Hệ thống tích hợp AI Chatbot để hướng dẫn người dùng sử dụng cổng bệnh nhân và cung cấp thông tin tư vấn ban đầu dựa trên kho tri thức của bệnh viện.

> AI Chatbot chỉ có vai trò hỗ trợ thông tin, không thay thế chẩn đoán hoặc tư vấn chuyên môn của bác sĩ.

## Thành viên nhóm

| STT | Họ và tên |
| --- | --- |
| 1 | Nguyễn Mạnh Quyền |
| 2 | Phạm Văn Hoàng |
| 3 | Nguyễn Quang Thọ |
| 4 | Hà Văn Đô |

## Chức năng chính

### Quản trị viên và nhân viên

- Đăng nhập, đăng xuất và phân quyền `ADMIN`, `STAFF`, `USER`.
- Quản lý tài khoản nhân viên và tài khoản người dùng.
- Quản lý thông tin bệnh nhân.
- Quản lý bệnh án và quá trình điều trị.
- Quản lý phòng điều trị và bác sĩ phụ trách.
- Quản lý lịch cấp thuốc.
- Theo dõi, xác nhận, hủy và xóa lịch khám.

### Cổng bệnh nhân

- Xem và cập nhật hồ sơ cá nhân.
- Xem bệnh án và lịch cấp thuốc của bản thân.
- Đặt, theo dõi và hủy lịch khám.
- Nhận thông báo thực tế từ lịch khám và lịch cấp thuốc.
- Đổi mật khẩu và quản lý tài khoản cá nhân.

### AI Chatbot

- Giao diện trò chuyện tích hợp trên các trang của hệ thống.
- Hướng dẫn đặt lịch khám, xem bệnh án và lịch cấp thuốc.
- Trả lời dựa trên kho tri thức nội bộ trong `src/main/resources/knowledge`.
- Kết nối Groq API và hỗ trợ cấu hình model qua biến môi trường.
- Có cơ chế bảo vệ CSRF, giới hạn nội dung đầu vào và thông báo miễn trừ y khoa.

## Công nghệ sử dụng

| Thành phần | Công nghệ |
| --- | --- |
| Backend | Java 21, Spring Boot 3.3.0, Spring MVC |
| Frontend | Thymeleaf, HTML, CSS, JavaScript |
| Cơ sở dữ liệu | MySQL 8.4, JDBC |
| AI | Groq API, OkHttp, JSON |
| Build | Maven |
| Đóng gói | Docker, Docker Compose |
| Unit/Integration Test | JUnit 5, Spring Boot Test, MockMvc |
| API Test | Postman, Newman |
| UI Test | Playwright, Selenium |
| Performance Test | Apache JMeter |
| Security Test | OWASP ZAP |
| Coverage | JaCoCo 0.8.12 |

## Kiến trúc

Ứng dụng được tổ chức theo mô hình MVC:

```text
Client
  │
  ▼
Controller ──► Service / AI Service
  │                    │
  ▼                    ▼
Database classes    Groq API
  │
  ▼
MySQL
```

- `Controller`: tiếp nhận HTTP request, kiểm tra phiên đăng nhập và điều phối view.
- `Model`: biểu diễn bệnh nhân, bệnh án, phòng, lịch thuốc, lịch khám và người dùng.
- `database`: thao tác dữ liệu bằng JDBC.
- `service`: xử lý AI Chatbot và kho tri thức.
- `security`: xác thực phiên, CSRF và HTTP security headers.
- `templates`: giao diện Thymeleaf.
- `static`: CSS, JavaScript và hình ảnh.

## Cấu trúc thư mục

```text
UDPT/
├── Benhvien/
│   └── complete/
│       ├── src/
│       │   ├── main/
│       │   │   ├── java/com/example/servingwebcontent/
│       │   │   │   ├── Controller/
│       │   │   │   ├── Model/
│       │   │   │   ├── database/
│       │   │   │   ├── security/
│       │   │   │   └── service/
│       │   │   └── resources/
│       │   │       ├── knowledge/
│       │   │       ├── static/
│       │   │       └── templates/
│       │   └── test/java/com/example/servingwebcontent/
│       │       ├── database/
│       │       ├── integration/
│       │       ├── model/
│       │       ├── security/
│       │       └── service/
│       ├── docker/mysql/init/
│       ├── performance/
│       ├── postman/
│       ├── ui-tests/
│       ├── docker-compose.yml
│       └── pom.xml
└── README.md
```

## Yêu cầu môi trường

- Java JDK 21.
- Maven 3.9 trở lên hoặc Maven Wrapper.
- Docker Desktop và Docker Compose.
- Node.js khi chạy Playwright hoặc Newman.
- Apache JMeter 5.6.3 khi chạy kiểm thử hiệu năng.

## Cấu hình

Các biến môi trường chính:

| Biến | Giá trị mặc định | Ý nghĩa |
| --- | --- | --- |
| `DB_HOST` | `localhost` | Máy chủ MySQL |
| `DB_PORT` | `3306` | Cổng MySQL |
| `DB_USERNAME` | `hospital` | Tài khoản MySQL |
| `DB_PASSWORD` | `hospital_secret` | Mật khẩu MySQL |
| `APP_PORT` | `8080` | Cổng ứng dụng khi dùng Docker |
| `GROQ_API_KEY` | trống | API key của Groq |
| `GROQ_MODEL` | `llama-3.3-70b-versatile` | Model AI |
| `GROQ_API_URL` | Groq Chat Completions API | Địa chỉ API AI |

Không đưa API key hoặc mật khẩu thật lên Git.

## Khởi chạy bằng Docker

Mở PowerShell tại thư mục ứng dụng:

```powershell
cd E:\UDPT\Benhvien\complete
docker compose up -d --build
docker compose ps
```

Truy cập:

```text
http://localhost:8080
```

Dừng hệ thống:

```powershell
docker compose down
```

Khởi tạo lại hoàn toàn cơ sở dữ liệu:

```powershell
docker compose down -v
docker compose up -d --build
```

Lệnh trên xóa dữ liệu trong Docker volume, chỉ sử dụng khi thực sự muốn tạo lại database.

## Khởi chạy bằng Maven

Khởi động MySQL trước, sau đó chạy:

```powershell
cd E:\UDPT\Benhvien\complete
mvn spring-boot:run
```

Hoặc dùng Maven Wrapper:

```powershell
..\..\mvnw.cmd spring-boot:run
```

Ứng dụng chạy thành công khi Terminal hiển thị:

```text
Tomcat started on port 8080
Started ServingWebContentApplication
```

## Tài khoản mẫu

| Vai trò | Tài khoản | Mật khẩu |
| --- | --- | --- |
| Admin | `admin` | `123456` |
| Nhân viên | `staff` | `123456` |
| Bệnh nhân | `patient1` | `123456` |
| Bệnh nhân | `patient2` | `123456` |

Các tài khoản mẫu chỉ dành cho môi trường phát triển và kiểm thử.

## Kiểm thử

### JUnit và Spring Boot Test

Chạy toàn bộ test:

```powershell
cd E:\UDPT\Benhvien\complete
mvn test
```

Chạy riêng Integration Test:

```powershell
mvn "-Dtest=*IntegrationTest" test
```

Chạy các Database Test tiêu biểu:

```powershell
mvn "-Dtest=PatientDatabaseTest,AppointmentDatabaseTest,UserDatabaseTest" test
```

Báo cáo Surefire nằm trong `target/surefire-reports`. Báo cáo coverage JaCoCo nằm trong `target/site/jacoco/index.html`.

### Postman/Newman

Các file kiểm thử API:

```text
postman/Hospital.postman_collection.json
postman/Hospital.postman_environment.json
```

Chạy bằng Terminal PowerShell:

```powershell
cd E:\UDPT\Benhvien\complete
$env:NODE_OPTIONS="--use-system-ca"
npx.cmd --yes newman run `
  postman\Hospital.postman_collection.json `
  -e postman\Hospital.postman_environment.json `
  --reporters cli,json `
  --reporter-json-export postman\Hospital.postman_result.json
```

### Playwright

```powershell
cd E:\UDPT\Benhvien\complete\ui-tests
npm.cmd install
npx.cmd playwright install
npm.cmd test
```

Mở báo cáo:

```powershell
npm.cmd run report
```

### JMeter

Chạy Test Plan không dùng giao diện:

```powershell
cd E:\UDPT\Benhvien\complete
& "E:\apache-jmeter-5.6.3\apache-jmeter-5.6.3\bin\jmeter.bat" `
  -n `
  -t "performance\PT_All_Hospital.jmx" `
  -l "performance\result-final.jtl" `
  -Jsummariser.interval=5
```

Test Plan bao gồm đăng nhập, quản lý bệnh nhân, bệnh án, phòng điều trị, lịch cấp thuốc, lịch khám và Chatbot.

### OWASP ZAP

Báo cáo bảo mật hiện có:

```text
Benhvien/complete/zap-report.html
Benhvien/complete/docker/zap/reports/baseline-report.html
```

Mở báo cáo:

```powershell
Start-Process E:\UDPT\Benhvien\complete\zap-report.html
```

## Bảo mật

- Xác thực và phân quyền dựa trên session.
- CSRF token cho các yêu cầu thay đổi dữ liệu.
- Security headers: CSP, chống clickjacking và hạn chế MIME sniffing.
- Kiểm tra dữ liệu đầu vào tại controller và Chatbot.
- Cấu hình kết nối database qua biến môi trường.
- OWASP ZAP dùng để rà quét các vấn đề bảo mật web.

## Hướng phát triển

- Mã hóa mật khẩu bằng BCrypt.
- Bổ sung trạng thái đã đọc/chưa đọc cho thông báo.
- Gửi thông báo qua email hoặc thiết bị di động.
- Thêm phân trang và bộ lọc nâng cao.
- Sử dụng migration database bằng Flyway hoặc Liquibase.
- Mở rộng kho tri thức và cơ chế truy xuất dữ liệu cho AI Chatbot.
- Triển khai CI/CD và môi trường production.

## Giấy phép và mục đích sử dụng

Dự án được phát triển phục vụ học tập và nghiên cứu. Không sử dụng AI Chatbot trong dự án để thay thế tư vấn, chẩn đoán hoặc điều trị y khoa chuyên nghiệp.
