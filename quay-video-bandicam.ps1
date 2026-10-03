# Quay Video Kiểm Thử Tự Động kết hợp Bandicam & Playwright
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

Write-Host "=======================================================================" -ForegroundColor Cyan
Write-Host "   QUAY VIDEO KIỂM THỬ TỰ ĐỘNG - COFFEE SHOP POS" -ForegroundColor Yellow
Write-Host "   (Hỗ trợ quay trực tiếp màn hình bằng Bandicam & Playwright HD)" -ForegroundColor White
Write-Host "=======================================================================" -ForegroundColor Cyan
Write-Host ""

$bdcamPath = "C:\Program Files\Bandicam\bdcam.exe"
if (Test-Path $bdcamPath) {
    Write-Host "[1/3] Đang khởi động phần mềm Bandicam trên máy..." -ForegroundColor Green
    Start-Process $bdcamPath
    Write-Host "[OK] Bandicam đã mở sẵn sàng trên màn hình!" -ForegroundColor Green
    Write-Host ""
    Write-Host "-------------------------------------------------------------------" -ForegroundColor Yellow
    Write-Host " MẸO QUAY BẰNG BANDICAM:" -ForegroundColor White
    Write-Host " - Phím tắt Bắt đầu / Dừng quay mặc định của Bandicam là: [F12]" -ForegroundColor White
    Write-Host "   (hoặc nhấn nút REC màu đỏ trên giao diện Bandicam)." -ForegroundColor White
    Write-Host "-------------------------------------------------------------------" -ForegroundColor Yellow
} else {
    Write-Host "[INFO] Playwright sẽ tự động quay video HD 720p độc lập cho bạn." -ForegroundColor Yellow
}

Write-Host ""
Write-Host "Nhấn phím bất kỳ để BẮT ĐẦU chạy kịch bản kiểm thử tự động trên màn hình..." -ForegroundColor Cyan
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")

$e2eDir = Join-Path $PSScriptRoot "e2e-tests"
Set-Location $e2eDir

Write-Host ""
Write-Host "[2/3] Đang chạy trình duyệt và thực thi kịch bản toàn cảnh (49s)..." -ForegroundColor Green
npx playwright test tests/coffee_pos_full_demo.spec.js --headed

Write-Host ""
Write-Host "[3/3] Đang trích xuất video kiểm thử HD..." -ForegroundColor Green
node organize-videos.js

$videosDir = Join-Path $e2eDir "videos"
$fullVideo = Join-Path $videosDir "DEMO_TOAN_BO_QUY_TRINH_COFFEE_POS.webm"

Write-Host ""
Write-Host "=======================================================================" -ForegroundColor Cyan
Write-Host "  ĐÃ HOÀN TẤT QUAY VIDEO KIỂM THỬ THÀNH CÔNG!" -ForegroundColor Green
Write-Host "=======================================================================" -ForegroundColor Cyan

Start-Process explorer.exe $videosDir
if (Test-Path $fullVideo) {
    Start-Process $fullVideo
}
