@echo off
setlocal

cd /d "%~dp0\..\..\.."

if not exist docker\zap\reports mkdir docker\zap\reports
if exist docker\zap\reports\baseline-report.html del /f /q docker\zap\reports\baseline-report.html

echo Dang chay OWASP ZAP Baseline Scan...
docker compose exec -T zap zap-baseline.py -I -t http://app:8080 -r reports/baseline-report.html
set "SCAN_EXIT=%ERRORLEVEL%"

echo.
echo Bao cao: docker\zap\reports\baseline-report.html
exit /b %SCAN_EXIT%
