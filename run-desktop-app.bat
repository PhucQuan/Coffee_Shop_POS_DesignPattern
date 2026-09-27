@echo off
echo ===================================================
echo   Starting PurrCoffee POS as a Native Desktop App...
echo ===================================================

cd /d "%~dp0frontend-react"
start "" /b npm run dev

echo Waiting for server to initialize...
timeout /t 2 /nobreak >nul

:: Launch as a Standalone Desktop Window (no browser URL bar, no tabs)
if exist "%ProgramFiles(x86)%\Microsoft\Edge\Application\msedge.exe" (
    start "" "%ProgramFiles(x86)%\Microsoft\Edge\Application\msedge.exe" --app="http://localhost:3000" --window-size=1280,820
) else if exist "%ProgramFiles%\Microsoft\Edge\Application\msedge.exe" (
    start "" "%ProgramFiles%\Microsoft\Edge\Application\msedge.exe" --app="http://localhost:3000" --window-size=1280,820
) else if exist "%ProgramFiles%\Google\Chrome\Application\chrome.exe" (
    start "" "%ProgramFiles%\Google\Chrome\Application\chrome.exe" --app="http://localhost:3000" --window-size=1280,820
) else (
    start http://localhost:3000
)
