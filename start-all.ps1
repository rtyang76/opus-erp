# Opus ERP One-Click Startup Script (PowerShell)

Write-Host ""
Write-Host "  ██████╗ ██████╗ ██╗   ██╗███████╗    ███████╗██████╗ ███████╗" -ForegroundColor Cyan
Write-Host " ██╔═══██╗██╔══██╗██║   ██║██╔════╝    ██╔════╝██╔══██╗██╔════╝" -ForegroundColor Cyan
Write-Host " ██║   ██║██████╔╝██║   ██║███████╗    █████╗  ██████╔╝█████╗  " -ForegroundColor Cyan
Write-Host " ██║   ██║██╔═══╝ ██║   ██║╚════██║    ██╔══╝  ██╔═══╝ ██╔══╝  " -ForegroundColor Cyan
Write-Host " ╚██████╔╝██║     ╚██████╔╝███████║    ███████╗██║     ███████╗" -ForegroundColor Cyan
Write-Host "  ╚═════╝ ╚═╝      ╚═════╝ ╚══════╝    ╚══════╝╚═╝     ╚══════╝" -ForegroundColor Cyan
Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "  Opus ERP One-Click Startup Script" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""

$ProjectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$RedisDir = "D:\Redis"
$BackendDir = Join-Path $ProjectRoot "backend\opus-erp-app"
$FrontendDir = Join-Path $ProjectRoot "frontend"

# Set environment variables for backend (must match start-all.bat)
# 注意：敏感配置必须从环境变量读取，禁止硬编码到 application.yml
$env:DB_PASSWORD = "LC_svr1"
$env:JWT_SECRET = "opus-erp-jwt-secret-key-at-least-256-bits-long-for-hmac-sha256"

# Step 1: Start Redis
Write-Host "[1/3] Starting Redis Server..." -ForegroundColor Yellow
Write-Host ""

$redisProcess = Get-Process -Name "redis-server" -ErrorAction SilentlyContinue
if ($redisProcess) {
    Write-Host "Redis is already running (PID: $($redisProcess.Id))" -ForegroundColor Green
} else {
    Start-Process -FilePath "$RedisDir\redis-server.exe" -ArgumentList "$RedisDir\redis.conf" -WindowStyle Minimized
    Start-Sleep -Seconds 2
    Write-Host "Redis started successfully!" -ForegroundColor Green
}

# Test Redis connection
try {
    $pingResult = & "$RedisDir\redis-cli.exe" ping 2>&1
    if ($pingResult -eq "PONG") {
        Write-Host "Redis connection: OK" -ForegroundColor Green
    } else {
        Write-Host "ERROR: Redis connection failed!" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "ERROR: Redis connection failed! $_" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "[2/3] Starting Backend (Spring Boot)..." -ForegroundColor Yellow
Write-Host ""

# Start backend in new window (环境变量需在子shell中显式设置，因为Start-Process新窗口不继承当前PS变量的env)
$dbPwd = $env:DB_PASSWORD
$jwtSecret = $env:JWT_SECRET
Start-Process -FilePath "cmd.exe" -ArgumentList "/k", "cd /d $BackendDir && set DB_PASSWORD=$dbPwd && set JWT_SECRET=$jwtSecret && mvn spring-boot:run" -WindowStyle Normal
Write-Host "Backend starting... (check the new window)" -ForegroundColor Green

Write-Host ""
Write-Host "[3/3] Starting Frontend (Vue 3)..." -ForegroundColor Yellow
Write-Host ""

# Wait for backend to initialize
Start-Sleep -Seconds 3

# Start frontend in new window
Start-Process -FilePath "cmd.exe" -ArgumentList "/k", "cd /d $FrontendDir && npm install && npm run dev" -WindowStyle Normal
Write-Host "Frontend starting... (check the new window)" -ForegroundColor Green

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "  All services are starting!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "  Redis:      localhost:6379" -ForegroundColor Cyan
Write-Host "  Backend:    http://localhost:8080" -ForegroundColor Cyan
Write-Host "  Frontend:   http://localhost:5173" -ForegroundColor Cyan
Write-Host ""
Write-Host "  Default Login:" -ForegroundColor Yellow
Write-Host "  Username: admin" -ForegroundColor White
Write-Host "  Password: admin123" -ForegroundColor White
Write-Host ""

# Open browser after delay
Write-Host "Press Enter to open browser..." -ForegroundColor Yellow
Read-Host
Start-Process "http://localhost:5173"

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "  Services are running in separate windows." -ForegroundColor Green
Write-Host "  Close those windows to stop services." -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Read-Host "Press Enter to exit"
