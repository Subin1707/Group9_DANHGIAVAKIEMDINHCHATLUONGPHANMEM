# Selenium UI Test - Toàn bộ chức năng

Ứng dụng phải chạy tại `http://localhost:8080` trước khi thực hiện kiểm thử.

Module đã cấu hình dùng dependency cache tại `.m2-cache/repository`, vì vậy VS Code
có thể nhận diện các import Selenium/JUnit mà không cần tải lại từ Maven Central.

```powershell
cd E:\UDPT\Benhvien\complete\selenium-tests
mvn clean test surefire-report:report
```

Nếu Maven trên Windows báo lỗi chứng chỉ PKIX, chạy bằng Docker:

```powershell
docker run -d --rm --name hospital-selenium -p 4444:4444 --shm-size=2g selenium/standalone-chrome:latest
docker run --rm -e SELENIUM_BASE_URL=http://host.docker.internal:8080 -e SELENIUM_REMOTE_URL=http://host.docker.internal:4444 -v "${PWD}:/workspace" -w /workspace maven:3.9.9-eclipse-temurin-21 mvn clean test org.apache.maven.plugins:maven-surefire-report-plugin:3.5.2:report
docker stop hospital-selenium
```

Kết quả:

- Source code: `src\test\java\com\example\selenium\AllFunctionsSeleniumTest.java`
- Selenium report: `target\reports\surefire.html`
- Screenshots: `target\selenium-screenshots\*.png`

Bộ kiểm thử gồm 55 test (`SEL001` đến `SEL055`) cho các nhóm:

- Đăng nhập, đăng xuất và CSRF
- Dashboard
- Bệnh nhân
- Bệnh án
- Phòng điều trị
- Lịch cấp thuốc
- Chatbot và các dữ liệu đầu vào nguy hiểm
