# 前端 Dockerfile
# 多阶段构建

# 阶段1: 构建
FROM node:20-alpine AS builder
WORKDIR /app

# 复制 package.json 并安装依赖
COPY package.json package-lock.json ./
RUN npm ci

# 复制源代码并构建
COPY . .
RUN npm run build

# 阶段2: 运行
FROM nginx:alpine
WORKDIR /usr/share/nginx/html

# 清空默认页面
RUN rm -rf ./*

# 复制构建产物
COPY --from=builder /app/dist .

# 复制 nginx 配置
COPY nginx.conf /etc/nginx/conf.d/default.conf

# 暴露端口
EXPOSE 80

# 启动 nginx
CMD ["nginx", "-g", "daemon off;"]
