# 后端 Dockerfile
# 多阶段构建

# 阶段1: 构建
FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app

# 复制 pom.xml 并下载依赖
COPY pom.xml .
COPY opus-erp-common/pom.xml opus-erp-common/
COPY opus-erp-system/pom.xml opus-erp-system/
COPY opus-erp-master/pom.xml opus-erp-master/
COPY opus-erp-inventory/pom.xml opus-erp-inventory/
COPY opus-erp-purchase/pom.xml opus-erp-purchase/
COPY opus-erp-sales/pom.xml opus-erp-sales/
COPY opus-erp-production/pom.xml opus-erp-production/
COPY opus-erp-finance/pom.xml opus-erp-finance/
COPY opus-erp-report/pom.xml opus-erp-report/
COPY opus-erp-app/pom.xml opus-erp-app/

RUN mvn dependency:go-offline -B

# 复制源代码并构建
COPY . .
RUN mvn clean package -DskipTests -B

# 阶段2: 运行
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# 安装 curl 用于健康检查
RUN apk add --no-cache curl

# 复制构建产物
COPY --from=builder /app/opus-erp-app/target/*.jar app.jar

# 暴露端口
EXPOSE 8080

# 启动应用
ENTRYPOINT ["java", "-jar", "app.jar"]
