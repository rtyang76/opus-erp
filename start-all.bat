@echo off
chcp 65001 >nul
title Opus ERP - Startup Manager
color 0A

echo.
echo  ██████╗ ██████╗ ██╗   ██╗███████╗    ███████╗██████╗ ███████╗
echo ██╔═══██╗██╔══██╗██║   ██║██╔════╝    ██╔════╝██╔══██╗██╔════╝
echo ██║   ██║██████╔╝██║   ██║███████╗    █████╗  ██████╔╝█████╗
echo ██║   ██║██╔═══╝ ██║   ██║╚════██║    ██╔══╝  ██╔═══╝ ██╔══╝
echo ╚██████╔╝██║     ╚██████╔╝███████║    ███████╗██║     ███████╗
echo  ╚═════╝ ╚═╝      ╚═════╝ ╚══════╝    ╚══════╝╚═╝     ╚══════╝
echo.
echo ========================================
echo   Opus ERP One-Click Startup Script
echo ========================================
echo.

set PROJECT_ROOT=%~dp0
set REDIS_DIR=D:\Redis
set BACKEND_DIR=%PROJECT_ROOT%backend\opus-erp-app
set FRONTEND_DIR=%PROJECT_ROOT%frontend

:: Set environment variables for backend
set DB_PASSWORD=LC_svr1
set JWT_SECRET=opus-erp-jwt-secret-key-at-least-256-bits-long-for-hmac-sha256

echo [1/3] Starting Redis Server...
echo.

:: Check if Redis is already running
tasklist /FI "IMAGENAME eq redis-server.exe" 2>NUL | find /I "redis-server.exe" >NUL
if %ERRORLEVEL% == 0 (
    echo Redis is already running!
) else (
    start "Redis Server" /MIN "%REDIS_DIR%\redis-server.exe" "%REDIS_DIR%\redis.conf"
    timeout /t 2 /nobreak >NUL
    echo Redis started successfully!
)

:: Test Redis connection
"%REDIS_DIR%\redis-cli.exe" ping >NUL 2>&1
if %ERRORLEVEL% == 0 (
    echo Redis connection: OK
) else (
    echo ERROR: Redis connection failed!
    pause
    exit /b 1
)

echo.
echo [2/3] Starting Backend (Spring Boot)...
echo.
echo Environment:
echo   DB_PASSWORD=LC_svr1
echo   JWT_SECRET=opus-erp-jwt-secret-***
echo.

:: Start backend in new window with environment variables
start "Opus ERP Backend" cmd /k "cd /d %BACKEND_DIR% && set DB_PASSWORD=%DB_PASSWORD% && set JWT_SECRET=%JWT_SECRET% && mvn spring-boot:run"

echo Backend starting... (check the new window)
echo.
echo [3/3] Starting Frontend (Vue 3)...
echo.

:: Wait a moment for backend to initialize
timeout /t 5 /nobreak >NUL

:: Start frontend in new window
start "Opus ERP Frontend" cmd /k "cd /d %FRONTEND_DIR% && npm install && npm run dev"

echo Frontend starting... (check the new window)
echo.
echo ========================================
echo   All services are starting!
echo ========================================
echo.
echo   Redis:      localhost:6379
echo   Backend:    http://localhost:8080
echo   Frontend:   http://localhost:5173
echo.
echo   Default Login:
echo   Username: admin
echo   Password: admin123
echo.
echo   Press any key to open browser...
pause >NUL

:: Open browser
start http://localhost:5173

echo.
echo ========================================
echo   Services are running in separate windows.
echo   Close those windows to stop services.
echo ========================================
echo.
pause
