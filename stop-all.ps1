# Opus ERP Stop All Services Script (PowerShell)

Write-Host ""
Write-Host "========================================" -ForegroundColor Red
Write-Host "  Opus ERP - Stop All Services" -ForegroundColor Red
Write-Host "========================================" -ForegroundColor Red
Write-Host ""

Write-Host "[1/3] Stopping Frontend..." -ForegroundColor Yellow
Get-Process -Name "node" -ErrorAction SilentlyContinue | Stop-Process -Force
Write-Host "Frontend stopped." -ForegroundColor Green

Write-Host ""
Write-Host "[2/3] Stopping Backend..." -ForegroundColor Yellow
Get-Process -Name "java" -ErrorAction SilentlyContinue | Stop-Process -Force
Write-Host "Backend stopped." -ForegroundColor Green

Write-Host ""
Write-Host "[3/3] Stopping Redis..." -ForegroundColor Yellow
Get-Process -Name "redis-server" -ErrorAction SilentlyContinue | Stop-Process -Force
Write-Host "Redis stopped." -ForegroundColor Green

Write-Host ""
Write-Host "========================================" -ForegroundColor Red
Write-Host "  All services stopped!" -ForegroundColor Red
Write-Host "========================================" -ForegroundColor Red
Write-Host ""
Read-Host "Press Enter to exit"
