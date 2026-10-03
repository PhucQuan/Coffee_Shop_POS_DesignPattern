@echo off
chcp 65001 > nul
title Coffee Shop POS - Quay Video Kiem Thu Tu Dong voi Bandicam

echo =======================================================================
echo    QUAY VIDEO KIỂM THỬ TỰ ĐỘNG - COFFEE SHOP POS
echo    (Hỗ trợ quay trực tiếp màn hình bằng Bandicam & Playwright HD)
echo =======================================================================
echo.

set BDCAM_PATH=C:\Program Files\Bandicam\bdcam.exe

if exist "%BDCAM_PATH%" (
    echo [1/3] Đang khởi động phần mềm Bandicam trên máy của bạn...
    start "" "%BDCAM_PATH%"
    echo [OK] Bandicam đã mở sẵn sàng trên màn hình!
    echo.
    echo -------------------------------------------------------------------
    echo  MẸO QUAY BẰNG BANDICAM:
    echo  - Bạn có thể chọn chế độ 'Toàn màn hình' (Fullscreen) trên Bandicam.
    echo  - Phím tắt Bắt đầu / Dừng quay mặc định của Bandicam là: [F12]
    echo    (hoặc nhấn nút REC màu đỏ trên giao diện Bandicam).
    echo -------------------------------------------------------------------
) else (
    echo [INFO] Không tìm thấy Bandicam tại C:\Program Files\Bandicam.
    echo [INFO] Playwright sẽ tự động quay video HD 720p độc lập cho bạn.
)

echo.
echo Nhấn phím bất kỳ để BẮT ĐẦU chạy kịch bản kiểm thử tự động trên màn hình...
pause > nul

echo.
echo [2/3] Đang chạy trình duyệt và thực thi kịch bản toàn cảnh (49s)...
echo       (Trình duyệt sẽ tự động thao tác: Tìm kiếm, Decorator, Giảm giá, Thanh toán QR, Bếp KDS, Admin)
cd /d "%~dp0e2e-tests"
call npx playwright test tests/coffee_pos_full_demo.spec.js --headed

echo.
echo [3/3] Đang trích xuất video kiểm thử HD...
call node organize-videos.js

echo.
echo =======================================================================
echo  ĐÃ HOÀN TẤT QUAY VIDEO KIỂM THỬ THÀNH CÔNG!
echo =======================================================================
echo.
echo Đang mở thư mục chứa video: e2e-tests\videos\
start "" "%~dp0e2e-tests\videos"

echo Đang mở video toàn bộ quy trình để bạn xem ngay...
start "" "%~dp0e2e-tests\videos\DEMO_TOAN_BO_QUY_TRINH_COFFEE_POS.webm"

pause
