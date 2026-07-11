# Lenh test day du theo tung nhom

Tat ca lenh duoi day chay trong PowerShell. Moi nhom duoc viet rieng de copy dan la chay duoc.

## 0. Chuan bi chung

Chay MySQL local bang Docker va build project:

```powershell
cd E:\UDPT\Benhvien\complete
docker compose up -d mysql
mvn package -DskipTests
```

Neu cong `8080` dang bi chiem:

```powershell
Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue | ForEach-Object { Stop-Process -Id $_.OwningProcess -Force }
```

## 1. Chay tat ca Maven test

```powershell
cd E:\UDPT\Benhvien\complete
mvn test
```

## 2. Test Model

```powershell
cd E:\UDPT\Benhvien\complete
mvn -Dtest="*ModelTest,RoleTest" test
```

## 3. Test Database

Can MySQL Docker dang chay.

```powershell
cd E:\UDPT\Benhvien\complete
docker compose up -d mysql
mvn -Dtest="*DatabaseTest,DatabaseConnectionTest" test
```

## 4. Test Controller

```powershell
cd E:\UDPT\Benhvien\complete
mvn -Dtest="*ControllerTest" test
```

## 5. Test Service

```powershell
cd E:\UDPT\Benhvien\complete
mvn -Dtest="*ServiceTest" test
```

## 6. Test Security

```powershell
cd E:\UDPT\Benhvien\complete
mvn -Dtest="*SecurityTest" test
```

## 7. Test Integration

```powershell
cd E:\UDPT\Benhvien\complete
mvn -Dtest="*IntegrationTest" test
```

## 8. Test CRUD / ListChung

```powershell
cd E:\UDPT\Benhvien\complete
mvn -Dtest="ListChungTest" test
```

## 9. Chay app local de test UI

Mo Terminal 1 va giu terminal nay dang chay:

```powershell
cd E:\UDPT\Benhvien\complete
mvn spring-boot:run
```

Mo web local:

```powershell
Start-Process "http://localhost:8080"
```

Tai khoan test:

```text
admin / 123456
staff / 123456
patient1 / 123456
patient2 / 123456
```

## 10. Playwright UI Test

Can app local dang chay o `http://localhost:8080`.

Chay test:

```powershell
cd E:\UDPT\Benhvien\complete\ui-tests
npm test
```

Chay test co mo trinh duyet:

```powershell
cd E:\UDPT\Benhvien\complete\ui-tests
npm run test:headed
```

Mo report Playwright:

```powershell
cd E:\UDPT\Benhvien\complete\ui-tests
npm run report
```

## 11. Selenium UI Test

Can app local dang chay o `http://localhost:8080`.

```powershell
cd E:\UDPT\Benhvien\complete\selenium-tests
mvn clean test surefire-report:report
```

Mo report Selenium:

```powershell
Start-Process "E:\UDPT\Benhvien\complete\selenium-tests\target\reports\surefire.html"
```

Neu Maven loi chung chi PKIX, chay Selenium bang Docker:

```powershell
cd E:\UDPT\Benhvien\complete\selenium-tests
docker run -d --rm --name hospital-selenium -p 4444:4444 --shm-size=2g selenium/standalone-chrome:latest
docker run --rm -e SELENIUM_BASE_URL=http://host.docker.internal:8080 -e SELENIUM_REMOTE_URL=http://host.docker.internal:4444 -v "${PWD}:/workspace" -w /workspace maven:3.9.9-eclipse-temurin-21 mvn clean test org.apache.maven.plugins:maven-surefire-report-plugin:3.5.2:report
docker stop hospital-selenium
```

## 12. JMeter Performance Test

Can app local dang chay o `http://localhost:8080`.

Cach nhanh nhat:

```powershell
cd E:\UDPT\Benhvien\complete
.\scripts\run-all.bat
```

Hoac chay JMeter truc tiep:

```powershell
cd E:\UDPT\Benhvien\complete
& "E:\apache-jmeter-5.6.3\apache-jmeter-5.6.3\bin\jmeter.bat" -n -t "performance\PT_All_Hospital.jmx" -l "reports\hospital.jtl" -e -o "reports\hospital"
```

Mo report JMeter:

```powershell
Start-Process "E:\UDPT\Benhvien\complete\reports\hospital\index.html"
```

Ket qua dat yeu cau khi `Error % = 0.00%`.

Mo JMeter GUI de xem hoac sua kich ban:

```powershell
& "E:\apache-jmeter-5.6.3\apache-jmeter-5.6.3\bin\jmeter.bat" -t "E:\UDPT\Benhvien\complete\performance\PT_All_Hospital.jmx"
```

## 13. OWASP ZAP Security Test

Khoi dong app, MySQL va ZAP bang Docker:

```powershell
cd E:\UDPT\Benhvien\complete
docker compose up -d --build
docker compose ps
```

Chay Baseline Scan:

```powershell
cd E:\UDPT\Benhvien\complete
.\docker\zap\scripts\baseline-scan.bat
Start-Process ".\docker\zap\reports\baseline-report.html"
```

Chay Full Scan:

```powershell
cd E:\UDPT\Benhvien\complete
.\docker\zap\scripts\full-scan.bat
Start-Process ".\docker\zap\reports\full-report.html"
```

Baseline Scan la quet thu dong. Full Scan co active scan va co the gui payload tan cong den ung dung local/test.

## 14. Test nhanh website da deploy

Mo website chinh:

```powershell
Start-Process "https://qlphongkham.id.vn"
```

Mo link Railway du phong:

```powershell
Start-Process "https://peaceful-victory-production-8866.up.railway.app"
```

Checklist test thu cong:

```text
1. Mo trang chu
2. Dang nhap admin / 123456
3. Dang nhap staff / 123456
4. Dang nhap patient1 / 123456
5. Xem Dashboard
6. Xem danh sach benh nhan
7. Them benh nhan
8. Sua benh nhan
9. Xoa benh nhan
10. Tim kiem benh nhan
11. Quan ly benh an
12. Quan ly phong dieu tri
13. Quan ly lich cap thuoc
14. Dat lich kham
15. Huy lich kham
16. Test chatbot
17. Dang xuat
```

## 15. Dung he thong sau khi test

Dung Spring Boot local bang `Ctrl + C` trong terminal dang chay app, sau do dung Docker:

```powershell
cd E:\UDPT\Benhvien\complete
docker compose down
```

## 16. Lenh test nhanh de nop bao cao

Neu chi can chay nhanh nhom test code:

```powershell
cd E:\UDPT\Benhvien\complete
docker compose up -d mysql
mvn test
```

Neu can test UI/performance, chay app local bang lenh nay trong Terminal 1:

```powershell
cd E:\UDPT\Benhvien\complete
mvn spring-boot:run
```

Sau do mo Terminal 2 va chon chay Playwright, Selenium hoac JMeter theo cac nhom o tren.
