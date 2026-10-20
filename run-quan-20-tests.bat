@echo off
chcp 65001 >nul
echo =====================================================================
echo  KỊCH BẢN KIỂM THỬ PLAYWRIGHT TỰ ĐỘNG - 20 TEST CASES CỦA BẠN QUÂN
echo  Đồ án: Coffee Shop POS (Hệ Thống Bán Hàng & Quản Trị Quán Cà Phê)
echo =====================================================================
echo.

cd /d "%~dp0\e2e-tests"

echo [1/3] Đang dọn dẹp thư mục kết quả test cũ...
if exist test-results rmdir /s /q test-results

echo [2/3] Đang thực thi 20 Test Cases và quay video thao tác (Chromium Headed)...
call npx playwright test tests/quan_20_testcases.spec.js --headed

echo.
echo [3/3] Đang trích xuất và lưu trữ trọn bộ Video HD...
node organize-quan-videos.js

echo.
echo =====================================================================
echo  HOÀN TẤT KIỂM THỬ! 
echo  Video đã được lưu tại: e2e-tests\videos\quan-testcases\
echo =====================================================================
echo.
pause
