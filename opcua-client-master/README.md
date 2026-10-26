# opcua-client（待整合）

独立 Spring Boot OPC UA 采集服务（Eclipse Milo），当前**未**接入 ECS 网关 / Nacos。另一台机器整合前，可先单独运行验证。

## 能力

- 连接 OPC UA 服务器，读/写/浏览节点（`OpcUaClientService`）
- REST：`/api/opcua/*`
- MyBatis-Plus：PLC / Area / Tag（MySQL）
- 默认 HTTP 端口：**8081**

## 本地运行

要求：**Java 11+**、**Maven 3.6+**、可访问的 **MySQL**（库名默认 `plc`）。

```bash
cd opcua-client-master
cp application-local.example.yml src/main/resources/application-local.yml
# 编辑 application-local.yml 填写数据源与 opcua.server-url
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

也可用环境变量：`OPCUA_DATASOURCE_URL`、`OPCUA_DATASOURCE_USERNAME`、`OPCUA_DATASOURCE_PASSWORD`、`OPCUA_SERVER_URL`。

## 整合说明（后续）

- 与主工程（Java 17、Spring Boot 3.5、PostgreSQL、Nacos）栈不一致，整合时需迁模块或做 sidecar 部署。
- 勿将含密码的 `application-local.yml` 提交到 Git。
