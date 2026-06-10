@echo off
set ROOT=%~dp0
set OUT=%ROOT%build\classes
set SOURCES=%ROOT%build\run-sources.tmp
if not exist "%OUT%" mkdir "%OUT%"
if exist "%SOURCES%" del "%SOURCES%"
for /R "%ROOT%src\main\java" %%f in (*.java) do echo %%f>> "%SOURCES%"
javac -encoding UTF-8 -d "%OUT%" @"%SOURCES%"
if errorlevel 1 exit /b 1
del "%SOURCES%"
java -cp "%OUT%" com.coffeeshop.Main
