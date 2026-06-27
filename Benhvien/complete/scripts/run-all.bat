@echo off
setlocal

cd /d "%~dp0\.."

set "JMETER_CMD=jmeter"
where jmeter >nul 2>nul
if errorlevel 1 (
    set "JMETER_CMD=E:\apache-jmeter-5.6.3\apache-jmeter-5.6.3\bin\jmeter.bat"
    if not exist "E:\apache-jmeter-5.6.3\apache-jmeter-5.6.3\bin\jmeter.bat" (
        echo Khong tim thay JMeter.
        echo Kiem tra lai duong dan: E:\apache-jmeter-5.6.3\apache-jmeter-5.6.3\bin\jmeter.bat
        pause
        exit /b 1
    )
)

if not exist reports mkdir reports

if exist reports\hospital.jtl del /f /q reports\hospital.jtl
if exist reports\hospital rmdir /s /q reports\hospital

echo Dang chay tat ca kich ban JMeter...
"%JMETER_CMD%" -n -t performance\PT_All_Hospital.jmx -l reports\hospital.jtl -e -o reports\hospital

if errorlevel 1 (
    echo.
    echo Chay JMeter that bai. Hay kiem tra app localhost:8080 da chay chua.
    pause
    exit /b 1
)

echo.
echo Hoan thanh. Mo bao cao tai:
echo reports\hospital\index.html
pause
