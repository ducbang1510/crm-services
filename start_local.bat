@echo off
setlocal enabledelayedexpansion

REM ==========================================================
REM  CRM Services - Local Startup Script (Windows)
REM ==========================================================

REM [0/6] Set Java 17 path (WARNING: Adjust your absolute path to Jdk 17)
set "JAVA_HOME=C:\Program Files\Java\jdk-17"
set "PATH=%JAVA_HOME%\bin;%PATH%"

REM MySQL connection info (adjust if needed)
set "DB_HOST=localhost"
set "DB_PORT=3306"
set "DB_USER=root"
set "DB_PASS=root125p"
set "DB_NAME=crm_db"
set "POPULATE_FILE=..\populate_data.sql"

REM Checking flag
set "SKIP_MAILDEV=false"
if "%~1"=="--no-maildev" (
    set "SKIP_MAILDEV=true"
)

echo ----------------------------------------------------------
echo Using Java version:
java -version
echo ----------------------------------------------------------

REM Ask user which mode to run
echo.
echo Choose an option:
echo   [1] Run only (use existing target jar)
echo   [2] Build and Run (mvn clean install skip tests then run)
set /p choice="Enter choice (1 or 2): "

REM Ensure we’re in project root
cd /d "%~dp0"

REM [A] Optionally create schema and populate data
echo.
echo Do you want to create or verify the database schema before startup?
echo   [Y] Yes
echo   [N] No (skip)
set /p createDb="Enter choice (Y/N): "

if /I "%createDb%"=="Y" (
    echo.
    echo ----------------------------------------------------------
    echo [DB-1] Checking database and creating schema if not exists...
    echo ----------------------------------------------------------
    mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% --password="%DB_PASS%" -e "CREATE DATABASE IF NOT EXISTS %DB_NAME%;"
    if %errorlevel% neq 0 (
        echo ERROR: Failed to connect or create database.
        exit /b 1
    )
) else (
    echo Skipping schema setup
)

echo.
echo Do you want to populate sample data after app starts?
echo   [Y] Yes
echo   [N] No (skip)
set /p populateData="Enter choice (Y/N): "

REM Save flag for later
set "RUN_POPULATE_DATA=%populateData%"

REM If user chose Build & Run
if "%choice%"=="2" (
    echo [1/6] Building package...
    call mvn clean install -DskipTests
    if %errorlevel% neq 0 (
        echo ERROR: Maven build failed. Exiting...
        exit /b %errorlevel%
    )
) else (
    echo [1/6] Skipping build. Using existing target JAR...
)

REM Move into target folder
if not exist target (
    echo ERROR: target folder not found. Try option 2 to build first.
    exit /b 1
)
cd target

REM Copy properties file
echo [2/6] Copying crm-services.properties...
if exist ..\src\main\resources\crm-services.properties (
    copy /Y ..\src\main\resources\crm-services.properties >nul
) else (
    echo WARNING:  crm-services.properties not found at src\main\resources.
)

REM Start MailDev (optional)
if "%SKIP_MAILDEV%"=="true" (
    echo [3/6] Skipping MailDev startup (flag --no-maildev used)
) else (
    echo [3/6] Checking MailDev installation
    where maildev >nul 2>nul
    if %errorlevel%==0 (
        echo "Starting MailDev (global)"
        start "MailDev" maildev
    ) else (
        where npx >nul 2>nul
        if %errorlevel%==0 (
            echo "Starting MailDev (via npx)"
            start "MailDev" npx maildev
        ) else (
            echo WARNING:  MailDev not installed. Skipping this step.
        )
    )
)

REM Kill processes on ports 8080 and 9092
echo [4/6] Checking and freeing ports 8080 and 9092...
for %%P in (8080 9092) do (
    for /f "tokens=5" %%a in ('netstat -ano ^| findstr :%%P ^| findstr LISTENING') do (
        echo Port %%P is in use by PID %%a, killing it...
        taskkill /PID %%a /F >nul 2>nul
    )
)

REM Find JAR file dynamically
echo [5/6] Searching for JAR file...
set "JAR_FILE="
for %%f in (crm-services-*-SNAPSHOT.jar) do (
    set JAR_FILE=%%f
)
if not defined JAR_FILE (
    echo ERROR: No JAR file found in target folder.
    exit /b 1
)

REM Start Spring Boot app
echo [6/6] Starting Spring Boot app...
echo Running: java -jar %JAR_FILE%
start "CRM Services" cmd /c "java -jar %JAR_FILE%"

REM Optional: populate data after app starts
if /I "%RUN_POPULATE_DATA%"=="Y" (
    echo Waiting 10 seconds for app to fully start...
    timeout /t 10 >nul
    if exist "%POPULATE_FILE%" (
        echo Importing sample data from %POPULATE_FILE% ...
        mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASS% %DB_NAME% < "%POPULATE_FILE%"
    ) else (
        echo WARNING: Populate file not found at %POPULATE_FILE%.
    )
)

echo ----------------------------------------------------------
echo SUCCESS: Application started successfully!
echo App:     http://localhost:8080
if "%SKIP_MAILDEV%"=="false" (
    echo MailDev: http://localhost:1080 ^(if installed^)
)
echo Socket:  ws://localhost:9092
echo ----------------------------------------------------------

endlocal
pause
