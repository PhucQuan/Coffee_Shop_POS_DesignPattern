@echo off
chcp 65001 > nul
echo ===================================================
echo   CHẠY KIỂM THỬ HỘP ĐEN PLAYWRIGHT (COFFEE SHOP POS)
echo ===================================================
cd /d "%~dp0e2e-tests"

if not exist "node_modules\@playwright" (
    echo [INFO] Đang cài đặt thư viện Playwright lần đầu...
    call npm install
    call npx playwright install chromium
)

echo [INFO] Đang thực thi các kịch bản kiểm thử Black-box...
call npx playwright test --headed

echo.
echo [XONG] Đang mở báo cáo kết quả kiểm thử...
call npx playwright show-report
pause
