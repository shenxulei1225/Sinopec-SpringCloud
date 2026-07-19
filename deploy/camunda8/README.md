# Camunda 8（Zeebe）本地自托管（波次 2）

## 版本钉死

- 拓扑：Camunda 8 Run（一键包，含 Zeebe / Operate / Tasklist）
- 镜像标签：`camunda/camunda:8.6.9`（若拉取失败可改为当前环境可用的 8.6.x 补丁号，并回写本文件）

## 启动

```bash
cd Sinopec-SpringCloud/deploy/camunda8
docker compose up -d
```

默认端口（以 compose 为准）：

| 组件 | 端口 |
|------|------|
| Zeebe gRPC | 26500 |
| Operate | 8088 |
| Tasklist | 8089 |

## 与维护服务对接

1. 用 Modeler 部署 `bpmn/corrective-approval.bpmn` 到 Zeebe。
2. `maintenance-server` 配置：

```yaml
maintenance:
  corrective:
    skip-approval: false   # 走 Zeebe；本地无 Docker 时保持 true
    bpmn-process-id: corrective-approval
camunda:
  client:
    mode: self-managed
    zeebe:
      grpc-address: http://127.0.0.1:26500
```

3. 当前代码默认 `skip-approval: true`（`NoOpCorrectiveApprovalGateway`）。接入 SDK worker 时实现 `CorrectiveApprovalGateway`，并回调 `CorrectiveCaseService.onApprovalResult`。

## 非目标

- 不迁移 `cheers-bpm`（Flowable）存量实例
- 不在 Zeebe 内实现排程 / checklist
