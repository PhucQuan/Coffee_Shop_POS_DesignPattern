# PowerShell Script - Kiểm thử 40 Testcases của Tú & Xuất Video
$ErrorActionPreference = "Stop"
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

Write-Host "=====================================================================" -ForegroundColor Cyan
Write-Host " KỊCH BẢN KIỂM THỬ PLAYWRIGHT TỰ ĐỘNG - 40 TEST CASES CỦA BẠN TÚ" -ForegroundColor Yellow
Write-Host " Đồ án: Coffee Shop POS (Hệ Thống Bán Hàng & Quản Trị Quán Cà Phê)" -ForegroundColor Cyan
Write-Host "=====================================================================" -ForegroundColor Cyan
Write-Host ""

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location "$scriptDir\e2e-tests"

Write-Host "[1/3] Đang dọn dẹp thư mục test-results..." -ForegroundColor Green
if (Test-Path "test-results") {
    Remove-Item -Recurse -Force "test-results"
}

Write-Host "[2/3] Đang chạy Playwright kiểm thử 40 test cases & ghi hình video..." -ForegroundColor Green
npx playwright test tests/tu_40_testcases.spec.js

Write-Host ""
Write-Host "[3/3] Trích xuất và đặt tên chuẩn cho toàn bộ Video..." -ForegroundColor Green
node organize-tu-videos.js

Write-Host ""
Write-Host "=====================================================================" -ForegroundColor Cyan
Write-Host " HOÀN TẤT! Video lưu tại: e2e-tests\videos\tu-testcases\" -ForegroundColor Yellow
Write-Host "=====================================================================" -ForegroundColor Cyan
