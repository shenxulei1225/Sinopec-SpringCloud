# Facility ↔ ActorInstance 严格 A2 表设计稿

> 版本：v1
> 日期：2026-05-11
> 目标：为 Facility ↔ ActorInstance 映射能力提供独立、可扩展、可审计、低耦合的数据库设计。
> 设计原则：映射关系归 Twin 模块，Facility / Scene 主表零回写耦合字段。

---

## 1. 表设计范围

本稿只覆盖当前映射落地所需的最小表集合：

1. `twin_mapping`
2. `twin_mapping_history`（推荐）
3. 未来扩展占位说明（不要求本阶段立即实现）

不在本稿内设计：

- Facility 主表
- ActorInstance 主表
- Runtime 订阅配置表
- Alarm 规则表
- Task / Inspection 业务表

---

## 2. 总体原则

### 2.1 Twin 独立建表

映射关系必须独立存储在 Twin 模块中。

禁止：

- 给 `facility` 加 `actor_instance_id`
- 给 `actor_instance` 加 `facility_id`
- 在任一主表中存对方主键作为长期主关系

### 2.2 先支持 1:1 主绑定，结构上兼容未来扩展

本阶段业务约束：

- 一个 Facility 最多一个有效主绑定
- 一个 ActorInstance 最多一个有效主绑定

但表结构应预留：

- 多类型映射
- 多目标映射
- 历史保留
- 逻辑失效

### 2.3 必须可审计

映射建立、换绑、解绑都属于关键业务动作，必须保留：

- 创建人
- 更新人
- 操作时间
- 解绑原因/备注
- 历史轨迹

### 2.4 必须服务查询场景

至少支持高效查询：

- 按 facilityId 查当前绑定
- 按 actorInstanceId 查当前绑定
- 按 sceneId 查场景内所有绑定
- 按 siteId / categoryId 做聚合过滤（若主表不冗余，则由查询层 join/接口聚合）

---

## 3. 主表：twin_mapping

## 3.1 表职责

保存当前有效或历史保留的 Facility ↔ ActorInstance 映射记录。

推荐以“逻辑失效 + 历史表补充”的方式设计，而不是只保留一张覆盖表。

## 3.2 建议字段

| 字段 | 类型 | 必填 | 说明 |
|---|---|---:|---|
| id | bigint | 是 | 主键 |
| mapping_code | varchar(64) | 是 | 映射编码，可用于审计/排查 |
| mapping_type | tinyint | 是 | 映射类型：1=FACILITY_ACTOR |
| relation_mode | tinyint | 是 | 关系模式：1=ONE_TO_ONE_PRIMARY |
| facility_id | bigint | 是 | Facility ID |
| actor_instance_id | bigint | 是 | ActorInstance ID |
| scene_id | bigint | 否 | 冗余保存场景ID，便于高效查询 |
| scene_code | varchar(64) | 否 | 冗余保存场景编码，便于联查/展示 |
| bind_source | varchar(32) | 否 | 绑定来源：SCENE_EDITOR / FACILITY_PAGE / CONTEXT_MENU |
| status | tinyint | 是 | 1=ACTIVE，0=INACTIVE |
| version | int | 是 | 乐观锁版本号 |
| remark | varchar(500) | 否 | 备注 |
| ext_json | json | 否 | 预留扩展 |
| creator | varchar(64) | 否 | 创建者 |
| create_time | datetime | 是 | 创建时间 |
| updater | varchar(64) | 否 | 更新者 |
| update_time | datetime | 是 | 更新时间 |
| deleted | bit | 是 | 逻辑删除 |
| tenant_id | bigint | 否 | 租户ID（按项目现有规范） |

## 3.3 字段设计说明

### 3.3.1 mapping_type

虽然当前只有 Facility ↔ ActorInstance，仍建议保留 `mapping_type`。

原因：

未来 Twin 很可能扩展：

- Area ↔ ActorInstance
- Device ↔ ActorInstance
- Point ↔ ActorInstance

保留类型字段后，Twin 可演化成统一映射中心，但不会影响当前接口语义。

### 3.3.2 relation_mode

本阶段固定值可为 `1=ONE_TO_ONE_PRIMARY`。

保留该字段是为了未来支持：

- 1:N 主从映射
- 聚合映射
- 参考映射

### 3.3.3 scene_id / scene_code

这两个字段是**受控冗余**，不是耦合污染。

保留理由：

- 查询“某场景内哪些对象已绑定”非常高频
- 避免每次通过 actorInstance 再反查 scene
- 便于前端概览页面与后台管理查询

前提：

- 真正拥有者仍是 Scene 模块
- Twin 仅保存映射建立时的查询辅助信息

### 3.3.4 status 与 deleted

建议区分：

- `status`：业务有效性（ACTIVE / INACTIVE）
- `deleted`：逻辑删除标记（遵循平台公共规范）

常见做法：

- 解绑时可先改 `status=INACTIVE`
- 若需要彻底逻辑删除，再置 `deleted=1`

本阶段为了简化实现，也可统一采用：

- 有效：`status=1, deleted=0`
- 无效：`status=0, deleted=0`

历史归档后再考虑是否删除。

---

## 4. 历史表：twin_mapping_history

## 4.1 为什么需要历史表

仅依赖主表覆盖无法回答：

- 这个设施之前绑定过谁？
- 这个对象曾经属于哪个设施？
- 谁在什么时候做了换绑？
- 解绑原因是什么？

因此推荐单独保留历史表。

## 4.2 建议字段

| 字段 | 类型 | 必填 | 说明 |
|---|---|---:|---|
| id | bigint | 是 | 主键 |
| mapping_id | bigint | 是 | 对应主表ID |
| operation_type | varchar(32) | 是 | BIND / REBIND / UNBIND / DEACTIVATE |
| mapping_type | tinyint | 是 | 同主表 |
| relation_mode | tinyint | 是 | 同主表 |
| facility_id | bigint | 是 | Facility ID |
| actor_instance_id | bigint | 是 | ActorInstance ID |
| scene_id | bigint | 否 | Scene ID |
| scene_code | varchar(64) | 否 | Scene 编码 |
| bind_source | varchar(32) | 否 | 操作来源 |
| operation_remark | varchar(500) | 否 | 操作备注 |
| operator_id | bigint | 否 | 操作人ID |
| operator_name | varchar(64) | 否 | 操作人名称 |
| occurred_at | datetime | 是 | 操作发生时间 |
| snapshot_json | json | 否 | 快照信息 |
| creator | varchar(64) | 否 | 创建者 |
| create_time | datetime | 是 | 创建时间 |
| updater | varchar(64) | 否 | 更新者 |
| update_time | datetime | 是 | 更新时间 |
| deleted | bit | 是 | 逻辑删除 |
| tenant_id | bigint | 否 | 租户ID |

## 4.3 历史写入策略

建议：

- 绑定成功后写一条 `BIND`
- 换绑时：旧关系写 `UNBIND` / `DEACTIVATE`，新关系写 `REBIND` 或 `BIND`
- 解绑时写 `UNBIND`

---

## 5. 主表唯一性约束

在当前 1:1 主绑定阶段，建议增加以下唯一性控制。

## 5.1 Facility 当前有效绑定唯一

语义：同一 Facility 只能有一条有效主映射。

实现建议：

- 若数据库支持部分唯一索引，则对 `facility_id` + `status=ACTIVE` + `deleted=0` 建唯一约束
- 若不方便，则在应用层事务校验 + 普通索引组合控制

## 5.2 ActorInstance 当前有效绑定唯一

语义：同一 ActorInstance 只能有一条有效主映射。

实现方式同上。

## 5.3 建议索引

### twin_mapping

建议索引：

1. `uk_active_facility`（facility_id, relation_mode, status, deleted）
2. `uk_active_actor_instance`（actor_instance_id, relation_mode, status, deleted）
3. `idx_scene_status`（scene_id, status, deleted）
4. `idx_scene_code_status`（scene_code, status, deleted）
5. `idx_mapping_code`（mapping_code）
6. `idx_create_time`（create_time）

### twin_mapping_history

建议索引：

1. `idx_mapping_id`（mapping_id）
2. `idx_facility_id`（facility_id）
3. `idx_actor_instance_id`（actor_instance_id）
4. `idx_scene_id`（scene_id）
5. `idx_occurred_at`（occurred_at）

---

## 6. 典型操作如何落表

## 6.1 首次绑定

输入：

- facilityId=1001
- actorInstanceId=2001

处理：

1. 校验 Facility 存在
2. 校验 ActorInstance 存在
3. 校验双方均无有效绑定
4. 插入 `twin_mapping` 一条 ACTIVE 记录
5. 插入 `twin_mapping_history` 一条 BIND 记录

## 6.2 Facility 换绑到新 Actor

输入：

- facilityId=1001
- newActorInstanceId=2002

处理：

1. 查到旧 ACTIVE 记录：1001 ↔ 2001
2. 将旧记录改为 INACTIVE
3. 写历史 `UNBIND/DEACTIVATE`
4. 插入新 ACTIVE 记录：1001 ↔ 2002
5. 写历史 `REBIND`

## 6.3 Actor 被其他 Facility 占用时换绑

输入：

- facilityId=1001
- actorInstanceId=2002
- 但 2002 当前绑定了 1008

处理：

默认不静默覆盖，先返回冲突。

若用户确认换绑：

1. 失效 1008 ↔ 2002
2. 失效 1001 的旧绑定（若存在）
3. 建立 1001 ↔ 2002 新有效绑定
4. 分别记录历史

## 6.4 解绑

处理：

1. 查 ACTIVE 记录
2. 改为 INACTIVE
3. 写历史 `UNBIND`

---

## 7. 为什么当前不把 dataChannel 放进 twin_mapping

当前阶段建议**不要**把 `dataChannel` 作为 TwinMapping 的核心字段。

原因：

1. 当前任务是解决“谁对应谁”的主映射问题
2. `dataChannel` 属于运行态订阅/展示策略，不是主映射本身
3. 将订阅策略塞入 Twin 会让 Twin 重新膨胀成业务中心

建议边界：

- Twin：只管对象映射
- Runtime/Scene 展示配置：后续单独建模

如果短期前端需要少量展示标记，可用 `ext_json` 过渡，但不建议固化为核心字段。

---

## 8. Area 为什么不并入当前表

Area 是否独立模块，是另一个架构话题。

当前结论：

- 不要因为未来可能有 `Area ↔ ActorInstance`，就把 Facility 映射和 Area 映射混在一张含义不清的大表里
- 若未来明确 Area 成为独立主数据模块，可在 Twin 通过 `mapping_type` 扩展

也就是说：

- 表结构可以兼容扩展
- 当前语义仍然聚焦 Facility ↔ ActorInstance

---

## 9. Flyway 迁移建议

由于项目已明确采用“模块独立 Flyway 管理”，Twin 模块应拥有自己的 migration 目录与历史。

建议 Twin 本阶段至少有：

- `V1__create_twin_mapping_table.sql`
- `V2__create_twin_mapping_history_table.sql`

若已有 Twin V1，则按实际版本顺延。

### 9.1 建表顺序

1. 先建主表 `twin_mapping`
2. 再建历史表 `twin_mapping_history`
3. 最后补索引与唯一性约束

### 9.2 命名建议

统一命名：

- 表名：`twin_mapping`
- 历史表：`twin_mapping_history`
- 不使用 `facility_actor_map` 这种会把 Twin 语义锁死在单一业务上的命名

---

## 10. 推荐 MySQL DDL 草案

> 以下为建议草案，实际字段类型需与项目统一基类、审计字段、租户字段规范对齐。

### 10.1 twin_mapping

```sql
CREATE TABLE IF NOT EXISTS twin_mapping (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    mapping_code VARCHAR(64) NOT NULL COMMENT '映射编码',
    mapping_type TINYINT NOT NULL COMMENT '映射类型：1-Facility-Actor',
    relation_mode TINYINT NOT NULL COMMENT '关系模式：1-1主绑定',
    facility_id BIGINT NOT NULL COMMENT '设施ID',
    actor_instance_id BIGINT NOT NULL COMMENT 'Actor实例ID',
    scene_id BIGINT NULL COMMENT '场景ID',
    scene_code VARCHAR(64) NULL COMMENT '场景编码',
    bind_source VARCHAR(32) NULL COMMENT '绑定来源',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1有效 0无效',
    version INT NOT NULL DEFAULT 0 COMMENT '版本号',
    remark VARCHAR(500) NULL COMMENT '备注',
    ext_json JSON NULL COMMENT '扩展字段',
    creator VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updater VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted BIT NOT NULL DEFAULT b'0' COMMENT '是否删除',
    tenant_id BIGINT NOT NULL DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (id),
    UNIQUE KEY uk_mapping_code (mapping_code),
    KEY idx_facility_status (facility_id, relation_mode, status, deleted),
    KEY idx_actor_status (actor_instance_id, relation_mode, status, deleted),
    KEY idx_scene_status (scene_id, status, deleted),
    KEY idx_scene_code_status (scene_code, status, deleted),
    KEY idx_create_time (create_time)
) COMMENT='Twin映射表';
```

### 10.2 twin_mapping_history

```sql
CREATE TABLE IF NOT EXISTS twin_mapping_history (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    mapping_id BIGINT NOT NULL COMMENT '映射主表ID',
    operation_type VARCHAR(32) NOT NULL COMMENT '操作类型',
    mapping_type TINYINT NOT NULL COMMENT '映射类型',
    relation_mode TINYINT NOT NULL COMMENT '关系模式',
    facility_id BIGINT NOT NULL COMMENT '设施ID',
    actor_instance_id BIGINT NOT NULL COMMENT 'Actor实例ID',
    scene_id BIGINT NULL COMMENT '场景ID',
    scene_code VARCHAR(64) NULL COMMENT '场景编码',
    bind_source VARCHAR(32) NULL COMMENT '绑定来源',
    operation_remark VARCHAR(500) NULL COMMENT '操作备注',
    operator_id BIGINT NULL COMMENT '操作人ID',
    operator_name VARCHAR(64) NULL COMMENT '操作人名称',
    occurred_at DATETIME NOT NULL COMMENT '发生时间',
    snapshot_json JSON NULL COMMENT '快照',
    creator VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updater VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted BIT NOT NULL DEFAULT b'0' COMMENT '是否删除',
    tenant_id BIGINT NOT NULL DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (id),
    KEY idx_mapping_id (mapping_id),
    KEY idx_facility_id (facility_id),
    KEY idx_actor_instance_id (actor_instance_id),
    KEY idx_scene_id (scene_id),
    KEY idx_occurred_at (occurred_at)
) COMMENT='Twin映射历史表';
```

---

## 11. 与应用层设计的对应关系

表设计并不是单独存在的，必须和应用层职责对齐：

- `facility-management`：不写该表，只提供 Facility 主数据校验与查询
- `scene-platform`：不写该表，只提供 ActorInstance 校验与查询
- `twin`：唯一写表模块
- `query/facade`：做聚合展示

---

## 12. 本阶段推荐最小落地集

若希望尽快进入开发，本阶段建议最少落地：

1. `twin_mapping` 主表
2. bind / unbind / query API
3. Facility / ActorInstance 双向唯一性校验
4. 按 sceneId 查询概览

若允许一步到位，建议再加：

5. `twin_mapping_history`
6. rebind 语义
7. 绑定来源和备注审计

---

## 13. 结论

这套表设计的核心不是“多建一张表”，而是把 Facility 与 Scene 的耦合关系从主数据里剥离出来，沉到 Twin 做桥接。

只有这样，Facility 模块才能保持台账主数据纯净，Scene 模块才能保持空间对象纯净，而 Twin 才真正承担数字孪生映射层的职责。
