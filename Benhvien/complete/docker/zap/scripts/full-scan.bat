@echo off
setlocal

cd /d "%~dp0\..\..\.."

if not exist docker\zap\reports mkdir docker\zap\reports
if exist docker\zap\reports\full-report.html del /f /q docker\zap\reports\full-report.html

echo Dang chay OWASP ZAP Full Scan tren moi truong local...
docker compose exec -T zap zap-full-scan.py -t http://app:8080 -r reports/full-report.html
set "SCAN_EXIT=%ERRORLEVEL%"

echo.
echo Bao cao: docker\zap\reports\full-report.html
exit /b %SCAN_EXIT%
