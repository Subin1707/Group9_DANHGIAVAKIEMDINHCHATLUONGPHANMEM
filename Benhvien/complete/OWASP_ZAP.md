# OWASP ZAP Security Testing

## Khoi dong he thong

```powershell
docker compose pull mysql zap
docker compose up -d --build
docker compose ps
```

Cho den khi `hospital-app`, `hospital-mysql` va `hospital-zap` deu dang chay.

## Baseline Scan

Baseline Scan la quet thu dong, nen chay truoc:

```powershell
.\docker\zap\scripts\baseline-scan.bat
```

Mo bao cao:

```powershell
Start-Process ".\docker\zap\reports\baseline-report.html"
```

## Full Scan

Full Scan la quet chu dong va co the gui cac payload tan cong. Chi chay tren moi truong local/test:

```powershell
.\docker\zap\scripts\full-scan.bat
```

Mo bao cao:

```powershell
Start-Process ".\docker\zap\reports\full-report.html"
```

ZAP quet dia chi `http://app:8080` vi hai container nam trong cung Docker Compose network. Khong dung `localhost` tu ben trong container ZAP.

ZAP co the tra ma thoat khac `0` khi phat hien canh bao. Dieu nay khong co nghia container bi loi; hay mo bao cao HTML va xem muc High, Medium, Low va Informational.

Script Baseline dung tuy chon `-I`, vi vay cac muc WARN van duoc ghi vao bao cao nhung khong lam lenh bi danh dau that bai. Full Scan van giu ma thoat nghiem ngat.
