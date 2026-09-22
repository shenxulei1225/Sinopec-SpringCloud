# inspection-task Flyway

- 路径：`src/main/resources/db/migration/`
- **V10**：退役 `inspection_route_plan` 及 `inspection_task` 上 V2 路线快照列；路线权威为动态业务总任务 `plannedRoute`。
- 已执行库升级后：重启 `inspection-task-server`；若 checksum 问题按仓库根 `scripts/flyway-repair-local.sh`（若已配置本模块）执行 repair。
