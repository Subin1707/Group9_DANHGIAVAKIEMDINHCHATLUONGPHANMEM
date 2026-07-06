# Lệnh chạy kiểm thử hiệu năng bằng JMeter

Mở VS Code Terminal (PowerShell), sau đó chạy lần lượt:

```powershell
Set-Location E:\UDPT\Benhvien\complete

if (Test-Path -LiteralPath "performance\report") {
    Remove-Item -LiteralPath "performance\report" -Recurse -Force
}

if (Test-Path -LiteralPath "performance\result.jtl") {
    Remove-Item -LiteralPath "performance\result.jtl" -Force
}

& "E:\apache-jmeter-5.6.3\apache-jmeter-5.6.3\bin\jmeter.bat" `
    -n `
    -t "performance\PT_All_Hospital.jmx" `
    -l "performance\result.jtl" `
    -e `
    -o "performance\report"

Get-ChildItem "performance"
Get-ChildItem "performance\report"

Get-Item `
    "performance\result.jtl", `
    "performance\report\statistics.json", `
    "performance\report\index.html"
```

## Các file kết quả chính

- `performance\result.jtl`
- `performance\report\statistics.json`
- `performance\report\index.html`

File nên dùng để lấy số liệu từng chức năng là `performance\report\statistics.json`.
