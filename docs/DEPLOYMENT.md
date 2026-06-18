# Opus-ERP 部署文档

> 本文档介绍如何部署 Opus-ERP 系统。

---

## 1. 环境要求

### 1.1 开发环境

| 工具 | 版本 | 说明 |
|------|------|------|
| JDK | 21 LTS | OpenJDK |
| Maven | 3.9+ | 构建工具 |
| Node.js | 20 LTS | 前端运行时 |
| Docker | 24+ | 容器化 |
| Docker Compose | 2.x | 容器编排 |

### 1.2 生产环境

| 组件 | 最低配置 | 推荐配置 |
|------|----------|----------|
| CPU | 2 核 | 4 核 |
| 内存 | 4 GB | 8 GB |
| 磁盘 | 50 GB SSD | 100 GB SSD |
| 操作系统 | Linux (Ubuntu 20.04+) | Linux (Ubuntu 22.04 LTS) |

---

## 2. 快速开始（Docker Compose）

### 2.1 克隆项目

```bash
git clone <repository-url>
cd opus-erp
```

### 2.2 配置环境变量

创建 `.env` 文件：

```bash
# 数据库配置
DB_PASSWORD=YourSecurePassword123!
DB_PORT=1433

# Redis 配置
REDIS_PORT=6379

# JWT 配置
JWT_SECRET=your-jwt-secret-key-must-be-at-least-256-bits-long

# 应用端口
BACKEND_PORT=8080
FRONTEND_PORT=80
```

### 2.3 启动服务

```bash
# 启动所有服务
docker-compose up -d

# 查看日志
docker-compose logs -f

# 查看状态
docker-compose ps
```

### 2.4 访问应用

- 前端：http://localhost
- 后端 API：http://localhost:8080
- 默认账号：admin / admin123

---

## 3. 手动部署

### 3.1 数据库准备

```sql
-- 创建数据库
CREATE DATABASE opus_erp;
GO

-- 使用数据库
USE opus_erp;
GO
```

### 3.2 后端部署

```bash
# 进入后端目录
cd backend

# 构建项目
mvn clean package -DskipTests

# 运行应用
java -jar opus-erp-app/target/opus-erp-app-1.0.0-SNAPSHOT.jar \
  --spring.profiles.active=prod \
  --spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=opus_erp;encrypt=false \
  --spring.datasource.username=sa \
  --spring.datasource.password=YourPassword \
  --spring.data.redis.host=localhost \
  --spring.data.redis.port=6379
```

### 3.3 前端部署

```bash
# 进入前端目录
cd frontend

# 安装依赖
npm install

# 构建生产版本
npm run build

# 部署到 Nginx
cp -r dist/* /usr/share/nginx/html/
```

### 3.4 Nginx 配置

```nginx
server {
    listen 80;
    server_name your-domain.com;
    root /usr/share/nginx/html;
    index index.html;

    # 前端路由
    location / {
        try_files $uri $uri/ /index.html;
    }

    # API 代理
    location /api/ {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
```

---

## 4. 生产环境配置

### 4.1 环境变量

| 变量名 | 说明 | 默认值 |
|--------|------|--------|
| `DB_HOST` | 数据库主机 | localhost |
| `DB_PORT` | 数据库端口 | 1433 |
| `DB_NAME` | 数据库名称 | opus_erp |
| `DB_USERNAME` | 数据库用户名 | sa |
| `DB_PASSWORD` | 数据库密码 | （必填） |
| `REDIS_HOST` | Redis 主机 | localhost |
| `REDIS_PORT` | Redis 端口 | 6379 |
| `JWT_SECRET` | JWT 密钥 | （必填） |

### 4.2 安全建议

1. **修改默认密码**：立即修改 admin 用户的默认密码
2. **使用 HTTPS**：配置 SSL 证书
3. **限制数据库访问**：仅允许应用服务器访问
4. **定期备份**：配置数据库自动备份
5. **监控日志**：配置日志收集和告警

---

## 5. 备份与恢复

### 5.1 数据库备份

```bash
# SQL Server 备份
/opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P "YourPassword" \
  -Q "BACKUP DATABASE [opus_erp] TO DISK = '/backup/opus_erp.bak' WITH FORMAT"

# Docker 备份
docker exec opus-erp-sqlserver /opt/mssql-tools/bin/sqlcmd \
  -S localhost -U sa -P "YourPassword" \
  -Q "BACKUP DATABASE [opus_erp] TO DISK = '/var/opt/mssql/backup/opus_erp.bak' WITH FORMAT"

# 复制备份文件
docker cp opus-erp-sqlserver:/var/opt/mssql/backup/opus_erp.bak ./backup/
```

### 5.2 数据库恢复

```bash
# SQL Server 恢复
/opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P "YourPassword" \
  -Q "RESTORE DATABASE [opus_erp] FROM DISK = '/backup/opus_erp.bak' WITH REPLACE"

# Docker 恢复
docker cp ./backup/opus_erp.bak opus-erp-sqlserver:/var/opt/mssql/backup/
docker exec opus-erp-sqlserver /opt/mssql-tools/bin/sqlcmd \
  -S localhost -U sa -P "YourPassword" \
  -Q "RESTORE DATABASE [opus_erp] FROM DISK = '/var/opt/mssql/backup/opus_erp.bak' WITH REPLACE"
```

---

## 6. 故障排除

### 6.1 常见问题

| 问题 | 原因 | 解决方案 |
|------|------|----------|
| 数据库连接失败 | 密码错误或服务未启动 | 检查密码和服务状态 |
| Redis 连接失败 | Redis 未启动 | 启动 Redis 服务 |
| 前端无法访问 | Nginx 配置错误 | 检查 Nginx 配置 |
| API 请求失败 | 后端未启动 | 启动后端服务 |

### 6.2 日志查看

```bash
# Docker 日志
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f sqlserver

# 应用日志
docker exec opus-erp-backend tail -f /app/logs/application.log
```

---

## 7. 更新升级

### 7.1 Docker Compose 更新

```bash
# 拉取最新代码
git pull

# 重新构建并启动
docker-compose up -d --build

# 查看日志
docker-compose logs -f
```

### 7.2 数据库迁移

```bash
# Flyway 会自动执行迁移脚本
# 手动执行迁移
docker exec opus-erp-backend java -jar app.jar --spring.flyway.enabled=true
```

---

## 8. 联系支持

如有问题，请联系：
- 邮箱：support@opus-erp.com
- 文档：[项目文档](docs/)
