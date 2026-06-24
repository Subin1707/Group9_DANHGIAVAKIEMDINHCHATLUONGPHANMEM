# MySQL bằng Docker

Chạy toàn bộ hệ thống bằng Docker trong thư mục `Benhvien/complete`:

```powershell
docker compose up -d
```

Ứng dụng chạy tại `http://localhost:8080`.

MySQL mặc định:

- Host: `localhost:3306`
- Database: `hospital`
- Username: `hospital`
- Password: `hospital_secret`

Tài khoản mẫu của ứng dụng:

- Admin: `admin` / `123456`
- Bệnh nhân 1: `patient1` / `123456` (hồ sơ `P001`)
- Bệnh nhân 2: `patient2` / `123456` (hồ sơ `P002`)

Xem trạng thái hoặc log:

```powershell
docker compose ps
docker compose logs -f app
docker compose logs -f mysql
```

Dừng hệ thống, dữ liệu vẫn được giữ trong volume:

```powershell
docker compose down
```

Xóa database và khởi tạo lại từ `docker/mysql/init/01-schema.sql`:

```powershell
docker compose down -v
docker compose up -d
```

Nếu chạy Java ngoài Docker Compose, dùng:

```powershell
mvn spring-boot:run
```

Khi đó app sẽ kết nối MySQL Docker qua `DB_HOST=localhost`, `DB_PORT=3306`, `DB_USERNAME=hospital`, `DB_PASSWORD=hospital_secret`.
