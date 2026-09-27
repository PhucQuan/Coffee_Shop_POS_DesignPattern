# Script chạy kiểm thử Playwright Black-box cho Coffee Shop POS
$root = $PSScriptRoot
$e2eDir = Join-Path $root "e2e-tests"

Set-Location $e2eDir

if (-not (Test-Path "node_modules\@playwright")) {
    Write-Host "[INFO] Đang cài đặt thư viện Playwright lần đầu..." -ForegroundColor Cyan
    npm install
    npx playwright install chromium
}

Write-Host "[INFO] Đang thực thi kịch bản kiểm thử Black-box (Headed Mode)..." -ForegroundColor Green
npx playwright test --headed

Write-Host "`n[XONG] Mở báo cáo kết quả kiểm thử..." -ForegroundColor Yellow
npx playwright show-report
