# Playwright Black-box Test Runner & Video Recorder
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

Write-Host "=======================================================================" -ForegroundColor Cyan
Write-Host "   CHẠY KIỂM THỬ HỘP ĐEN PLAYWRIGHT VÀ QUAY VIDEO TỰ ĐỘNG" -ForegroundColor Yellow
Write-Host "   Dự án: Coffee Shop POS (Áp dụng Design Patterns)" -ForegroundColor White
Write-Host "=======================================================================" -ForegroundColor Cyan
Write-Host ""

$e2eDir = Join-Path $PSScriptRoot "e2e-tests"
Set-Location $e2eDir

if (-not (Test-Path "node_modules\@playwright")) {
    Write-Host "[1/4] Đang cài đặt thư viện Playwright lần đầu..." -ForegroundColor Green
    npm install
    npx playwright install chromium
}

Write-Host "[2/4] Đang thực thi 5 kịch bản kiểm thử Black-box và ghi hình HD..." -ForegroundColor Green
npx playwright test --headed

Write-Host ""
Write-Host "[3/4] Đang trích xuất và đặt tên 5 video kiểm thử vào thư mục videos/..." -ForegroundColor Green
node organize-videos.js

Write-Host ""
Write-Host "[4/4] Mở thư mục chứa Video kết quả..." -ForegroundColor Green
$videosDir = Join-Path $e2eDir "videos"
if (Test-Path $videosDir) {
    Start-Process explorer.exe $videosDir
}

Write-Host ""
Write-Host "=======================================================================" -ForegroundColor Cyan
Write-Host " Đang mở Báo cáo Kiểm thử trực quan (Playwright HTML Report)..." -ForegroundColor Yellow
Write-Host "=======================================================================" -ForegroundColor Cyan
npx playwright show-report
