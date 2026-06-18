@echo off
set ROOT=%~dp0
set OUT=%ROOT%build\test-classes
set SOURCES=%ROOT%build\test-sources.tmp
if not exist "%OUT%" mkdir "%OUT%"
if exist "%SOURCES%" del "%SOURCES%"
for /R "%ROOT%src\main\java" %%f in (*.java) do echo %%f>> "%SOURCES%"
for /R "%ROOT%src\test\java" %%f in (*.java) do echo %%f>> "%SOURCES%"
javac -encoding UTF-8 -cp "libs\sqlite-jdbc-3.46.1.3.jar" -d "%OUT%" @"%SOURCES%"
if errorlevel 1 exit /b 1
del "%SOURCES%"
if exist "%ROOT%src\main\resources" xcopy "%ROOT%src\main\resources\*" "%OUT%\" /E /I /Y >nul
java -cp "%OUT%;libs\sqlite-jdbc-3.46.1.3.jar" com.coffeeshop.TestRunner
