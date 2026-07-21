# 应急管理系统 - 部署指南

## 概述

应急管理系统（Emergency Management System）是一个基于 Spring Cloud 的微服务应用，提供应急事件管理、响应处理、任务执行、资源调度等功能。

## 快速开始

### 前置要求

- Docker 20.10+
- Docker Compose 2.0+
- Maven 3.6+
- JDK 21+

### 构建镜像

```bash
# 在项目根目录执行
mvn clean package -DskipTests

# 构建Docker镜像
cd yudao-module-emergency-server
docker build -t yudao-module-emergency-server:latest .
```

### 使用 Docker Compose 启动

```bash
# 启动所有服务（包括数据库、Redis、Nacos）
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f emergency-server
```

### 停止服务

```bash
# 停止所有服务
docker-compose down

# 停止并删除数据卷
docker-compose down -v
```

## 配置说明

### 环境变量

- `JAVA_OPTS`: JVM参数，默认 `-Xms512m -Xmx512m`
- `SPRING_PROFILES_ACTIVE`: Spring配置环境，可选 `local`、`test`、`prod`
- `TZ`: 时区设置，默认 `Asia/Shanghai`

### 端口配置

- 应用端口：`48090`
- PostgreSQL：`5432`
- Redis：`6379`
- Nacos：`8848`

### 数据库配置

默认数据库配置（可在 `application.yaml` 中修改）：

- 数据库：`emergency`
- 用户名：`root`
- 密码：`123456`

## 生产环境部署

### 1. 修改配置

编辑 `application.yaml` 和 `docker-compose.yml`，配置生产环境参数：

- 数据库连接信息
- Redis连接信息
- Nacos配置中心地址
- 日志级别和输出路径

### 2. 安全配置

- 修改默认密码
- 配置HTTPS
- 设置防火墙规则
- 配置访问控制

### 3. 资源限制

在 `docker-compose.yml` 中添加资源限制：

```yaml
services:
  emergency-server:
    deploy:
      resources:
        limits:
          cpus: '2'
          memory: 2G
        reservations:
          cpus: '1'
          memory: 1G
```

### 4. 健康检查

添加健康检查配置：

```yaml
services:
  emergency-server:
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:48090/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s
```

## 监控和日志

### 日志查看

```bash
# 查看应用日志
docker-compose logs -f emergency-server

# 查看最近100行日志
docker-compose logs --tail=100 emergency-server
```

### 性能监控

系统集成了 Prometheus 和 Actuator，可通过以下端点监控：

- 健康检查：`http://localhost:48090/actuator/health`
- 指标：`http://localhost:48090/actuator/metrics`
- Prometheus：`http://localhost:48090/actuator/prometheus`

## 故障排查

### 常见问题

1. **服务启动失败**
   - 检查端口是否被占用
   - 检查数据库连接是否正常
   - 查看日志文件

2. **数据库连接失败**
   - 确认PostgreSQL服务已启动
   - 检查数据库配置是否正确
   - 确认网络连接正常

3. **内存不足**
   - 调整 `JAVA_OPTS` 中的内存参数
   - 检查系统资源使用情况

### 日志位置

- 应用日志：`./logs/`
- Docker日志：`docker-compose logs`

## 更新部署

```bash
# 停止服务
docker-compose down

# 重新构建镜像
docker build -t yudao-module-emergency-server:latest .

# 启动服务
docker-compose up -d
```

## 备份和恢复

### 数据库备份

```bash
# 备份数据库
docker exec emergency-postgres pg_dump -U root emergency > backup.sql

# 恢复数据库
docker exec -i emergency-postgres psql -U root emergency < backup.sql
```

### 数据卷备份

```bash
# 备份数据卷
docker run --rm -v emergency_postgres-data:/data -v $(pwd):/backup alpine tar czf /backup/postgres-backup.tar.gz /data
```

## 相关文档

- [API文档说明](../yudao-module-emergency-biz/src/main/resources/api-doc/API文档说明.md)
- [权限配置说明](../yudao-module-emergency-biz/src/main/resources/permission/emergency-permissions.md)
- [用户手册](../../../specs/001-应急管理系统/代码实现记录/用户手册.md)








