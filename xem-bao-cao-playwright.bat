@echo off
chcp 65001 >nul
echo =======================================================
echo  ĐANG MỞ BÁO CÁO KIỂM THỬ PLAYWRIGHT HTML REPORT...
echo =======================================================
echo.
cd /d "%~dp0e2e-tests"
npx playwright show-report
pause
