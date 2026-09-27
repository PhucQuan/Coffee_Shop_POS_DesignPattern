@echo off
set ROOT=%~dp0
set OUT=%ROOT%build\classes
set SOURCES=%ROOT%build\run-sources.tmp
if not exist "%OUT%" mkdir "%OUT%"
if exist "%SOURCES%" del "%SOURCES%"
for /R "%ROOT%src\main\java" %%f in (*.java) do echo %%f>> "%SOURCES%"
javac --release 17 -encoding UTF-8 -cp "libs\*" -d "%OUT%" @"%SOURCES%"
if errorlevel 1 exit /b 1
del "%SOURCES%"
if exist "%ROOT%src\main\resources" xcopy "%ROOT%src\main\resources\*" "%OUT%\" /E /I /Y >nul
echo Starting Coffee Shop POS Web Server on http://localhost:8088 ...
start http://localhost:8088
java -cp "%OUT%;libs\*" com.coffeeshop.api.WebServer
