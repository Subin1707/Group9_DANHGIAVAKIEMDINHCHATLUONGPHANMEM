# Lenh chay test JMeter

Tat ca lenh duoi day chay trong PowerShell cua VS Code.

## 1. Di chuyen vao project

```powershell
cd E:\UDPT\Benhvien\complete
```

## 2. Kiem tra Java va JMeter

```powershell
java -version
& "E:\apache-jmeter-5.6.3\apache-jmeter-5.6.3\bin\jmeter.bat" -v
```

Neu da them JMeter vao bien `PATH`, co the dung:

```powershell
jmeter -v
```

## 3. Khoi dong co so du lieu

```powershell
docker compose up -d
docker compose ps
```

Neu container ung dung Docker dang chiem cong `8080`, dung:

```powershell
docker stop hospital-app
```

## 4. Kiem tra va giai phong cong 8080

Xem tien trinh dang dung cong:

```powershell
netstat -ano | findstr :8080
```

Dung tien trinh theo PID tim duoc, vi du PID la `25496`:

```powershell
taskkill /PID 25496 /F
```

Hoac tu dong dung moi tien trinh dang lang nghe tren cong `8080`:

```powershell
Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue | ForEach-Object { Stop-Process -Id $_.OwningProcess -Force }
```

## 5. Chay ung dung Spring Boot

Mo Terminal thu nhat va chay:

```powershell
cd E:\UDPT\Benhvien\complete
mvn spring-boot:run
```

Cho den khi hien thong bao Tomcat da chay tren cong `8080`, sau do kiem tra:

```powershell
Start-Process "http://localhost:8080/login"
```

## 6. Chay tat ca test JMeter

Mo Terminal thu hai va chay cach nhanh nhat:

```powershell
cd E:\UDPT\Benhvien\complete
.\scripts\run-all.bat
```

Script se tu dong xoa ket qua `reports\hospital.jtl` va `reports\hospital` cua lan chay truoc.

Hoac chay JMeter truc tiep:

```powershell
cd E:\UDPT\Benhvien\complete
& "E:\apache-jmeter-5.6.3\apache-jmeter-5.6.3\bin\jmeter.bat" -n -t "performance\PT_All_Hospital.jmx" -l "reports\hospital.jtl" -e -o "reports\hospital"
```

## 7. Chay lai test

Script `run-all.bat` da tu dong xoa ket qua cu, vi vay chi can chay:

```powershell
cd E:\UDPT\Benhvien\complete
.\scripts\run-all.bat
```

## 8. Mo bao cao HTML

```powershell
Start-Process "E:\UDPT\Benhvien\complete\reports\hospital\index.html"
```

Ket qua dat yeu cau khi `Error % = 0.00%`. Dong tong ket cuoi cung se cho biet tong request, thoi gian chay, throughput, thoi gian phan hoi trung binh, nho nhat, lon nhat va so loi.

## 9. Mo JMeter GUI de xem hoac sua kich ban

```powershell
& "E:\apache-jmeter-5.6.3\apache-jmeter-5.6.3\bin\jmeter.bat" -t "E:\UDPT\Benhvien\complete\performance\PT_All_Hospital.jmx"
```

Chi dung GUI de sua va kiem tra kich ban. Khi chay performance test chinh thuc, nen dung che do command line `-n`.

## 10. Dung he thong sau khi test

Dung Spring Boot bang `Ctrl + C` tai Terminal thu nhat, sau do dung Docker:

```powershell
cd E:\UDPT\Benhvien\complete
docker compose down
```
