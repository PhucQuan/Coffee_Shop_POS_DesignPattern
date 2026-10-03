@echo off
chcp 65001 > nul
title Coffee Shop POS - Kiem Thu Hop Den Playwright & Quay Video

echo =======================================================================
echo    CHẠY KIỂM THỬ HỘP ĐEN PLAYWRIGHT VÀ QUAY VIDEO TỰ ĐỘNG
echo    Dự án: Coffee Shop POS (Áp dụng Design Patterns)
echo =======================================================================
echo.

cd /d "%~dp0e2e-tests"

if not exist "node_modules\@playwright" (
    echo [1/4] Đang cài đặt thư viện Playwright lần đầu...
    call npm install
    call npx playwright install chromium
)

echo [2/4] Đang thực thi 5 kịch bản kiểm thử Black-box và ghi hình HD...
call npx playwright test --headed

echo.
echo [3/4] Đang trích xuất và đặt tên 5 video kiểm thử vào thư mục videos/...
call node organize-videos.js

echo.
echo [4/4] Mở thư mục chứa Video kết quả...
start "" "%~dp0e2e-tests\videos"

echo.
echo =======================================================================
echo  Đang mở Báo cáo Kiểm thử trực quan (Playwright HTML Report)...
echo  (Bạn có thể xem lại từng bước, log và video tích hợp ngay trên web)
echo =======================================================================
call npx playwright show-report

pause
