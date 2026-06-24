@echo off
chcp 65001 >nul
title Opus ERP - Stop Services
color 0C

echo.
echo ========================================
echo   Opus ERP - Stop All Services
echo ========================================
echo.

echo [1/3] Stopping Frontend...
taskkill /FI "WINDOWTITLE eq Opus ERP Frontend*" /F >NUL 2>&1
taskkill /FI "IMAGENAME eq node.exe" /F >NUL 2>&1
echo Frontend stopped.

echo.
echo [2/3] Stopping Backend...
taskkill /FI "WINDOWTITLE eq Opus ERP Backend*" /F >NUL 2>&1
taskkill /FI "IMAGENAME eq java.exe" /F >NUL 2>&1
echo Backend stopped.

echo.
echo [3/3] Stopping Redis...
taskkill /FI "IMAGENAME eq redis-server.exe" /F >NUL 2>&1
echo Redis stopped.

echo.
echo ========================================
echo   All services stopped!
echo ========================================
echo.
pause
