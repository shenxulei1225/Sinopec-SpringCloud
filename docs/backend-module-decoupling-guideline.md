# Sinopec-SpringCloud 后端模块解耦规范

> 适用范围：`Sinopec-SpringCloud` 全部业务模块  
> 目标：为后续 Facility / Scene / Twin / Alarm / Runtime / Task / Inspection 等模块的持续迁移与新增开发提供统一边界约束，避免重新回到旧系统的强耦合模式。  
> 约束级别：架构规范，新增功能与重构必须遵守。

---

## 1. 文档目的

新后端不是把旧项目功能原样搬运过来，而是要在**保留业务能力**的前提下，完成**模块解耦、职责下沉、接口稳定、可持续演进**的重建。

本规范解决以下问题：

1. 模块边界不清，谁都能直接访问谁的数据。
2. 页面需要什么，后端就跨模块直接拼什么，导致 service 持续膨胀。
3. 主数据、运行态、场景对象、任务流程混在一起。
4. 旧系统中常见的“一个模块拥有别的模块内部数据”的问题在新系统再次出现。
5. 后续 Task / Inspection / WorkOrder / Control 迁移进来后再次产生深层业务耦合。

---

## 2. 核心设计原则

### 2.1 原则一：先划分领域，再实现功能

开发任何新功能前，必须先回答：

- 这份数据属于哪个领域？
- 谁是这份数据的拥有者？
- 其他模块是“拥有”它，还是“引用”它？
- 这次实现是主数据能力、运行态能力、空间能力，还是业务流程能力？

如果这些问题答不清楚，禁止直接写代码。

---

### 2.2 原则二：不要按页面拆系统，要按职责拆系统

禁止以下思路：

- 页面要展示什么，后端就把所有数据都塞到一个 service 里查询。
- 一个 Controller / Service 同时负责设施、场景、告警、任务、巡检、实时数据。
- 为了“方便前端”，把多个领域的数据长期揉在一个模块里。

必须采用以下思路：

- 主数据归主数据模块。
- 空间对象归 Scene 模块。
- 映射关系归 Twin 模块。
- 运行态归 Runtime / Telemetry / Alarm 模块。
- 任务流程归 Task / Inspection / WorkOrder 模块。
- 页面聚合需求由**应用层编排服务 / Facade / QueryService**承担，而不是污染领域模块本身。

---

### 2.3 原则三：拥有者负责写，引用者只允许查

任何一类数据，必须明确唯一拥有者：

| 数据类型 | 拥有模块 |
|---|---|
| 站点、设施主数据 | facility-management |
| 场景、Actor、ActorInstance | scene-platform |
| Facility 与 ActorInstance 映射 | twin |
| 分类树 | system/category |
| 实时遥测、状态缓存、推送帧 | runtime / nats / telemetry |
| 告警规则、告警状态、告警记录 | alarm |
| 任务、巡检、工单 | 未来 task / inspection / work-order 模块 |

要求：

- **只有拥有者模块可以直接写自己的表。**
- 其他模块只能通过 API / Facade / QueryService 调用。
- 不允许“顺手维护别人的数据”。

---

### 2.4 原则四：静态主数据、动态运行态、业务过程必须分离

以下三类数据不能混在同一张主表或同一领域里：

#### A. 静态主数据
例如：
- facility 编码、名称、分类、所属 site / area
- 型号、厂商、启用时间
- 设施说明、业务属性

#### B. 动态运行态
例如：
- 最新温度、压力、液位
- 在线状态
- 最新告警态
- 实时位置 / transform

#### C. 业务过程数据
例如：
- 巡检任务
- 执行记录
- 工单状态
- 告警处理流转
- 控制命令记录

规范要求：

- `facility` 主表中不得承载最新遥测值、实时在线态、任务状态等运行/流程字段。
- 运行态数据进入 runtime / telemetry / alarm state store。
- 业务过程数据进入 task / inspection / work-order 领域。

---

## 3. 模块职责矩阵

### 3.1 category 模块

负责：
- 系统级分类树
- 多级分类能力
- 分类编码、父子关系、树结构查询

不负责：
- 具体某个业务对象实例的数据
- 设施详情、场景详情、任务详情

说明：
- Facility 的分类统一使用系统 `category` 能力。
- 不允许 Facility 自己再造一套并行分类体系。

---

### 3.2 facility-management 模块

负责：
- Site 管理
- Facility 主数据管理
- Facility 分类归属
- Facility 详情
- Facility 导入导出
- Facility 扩展属性
- Facility 台账查询
- Facility 对外主数据 API

不负责：
- 场景定义
- Actor / ActorInstance 管理
- Twin 映射主存储
- 实时订阅路由
- 告警判断
- 巡检任务流程

约束：
- Facility 不得直接依赖 Scene 的内部 DO / Mapper。
- Facility 不得在主表中保存 Actor 内部字段作为长期设计。
- Facility 页面需要显示场景/告警/任务摘要时，应通过聚合查询服务获取。

---

### 3.3 scene-platform 模块

负责：
- Scene / Actor / ActorInstance 主体能力
- 场景加载协议
- 场景层级、图层、布局
- ActorInstance 选择、定位、高亮等空间能力
- 前端 3D 场景对应的空间对象管理

不负责：
- Facility 主数据
- Twin 映射主存储
- 巡检任务、工单流程
- 告警规则与业务处理

约束：
- Scene 不得拥有 Facility 业务字段。
- Scene 中的 ActorInstance 只是空间对象，不等同于 Facility。
- Scene 点击对象后需要得到业务实体时，必须通过 Twin 映射转换。

---

### 3.4 twin 模块

负责：
- Facility ↔ ActorInstance 映射关系
- 后续可扩展的 Area ↔ Actor、Device ↔ Actor 等映射
- 映射关系校验、创建、解除、查询

不负责：
- Scene 本体管理
- Facility 本体管理
- Runtime 推送逻辑
- 告警、任务业务逻辑

约束：
- Twin 是桥，不是业务中心。
- Twin 只维护映射关系，不接管 Facility 或 Scene 的主数据生命周期。

---

### 3.5 runtime / nats / telemetry 模块

负责：
- 数据接入
- 状态缓存
- 变更聚合
- WebSocket 推送
- 订阅管理
- RuntimeFrame 组织

不负责：
- Facility 主数据
- Twin 映射主存储
- 任务流程
- Scene 实体定义

约束：
- 前端订阅的是**数据流类型**，不是对象级订阅爆炸。
- Runtime 推送以 `telemetry / transform / alarm / scene / control` 等运行通道为粒度。
- 不允许为每个 Facility / 每个 Actor 设计独立订阅主题作为前端主模式。

---

### 3.6 alarm 模块

负责：
- 告警规则
- 告警状态
- 告警记录
- 告警处理流程

不负责：
- Facility 主数据维护
- Scene 主数据维护
- Twin 主存储

约束：
- 告警模块通过 facilityId / metricKey 等外键引用业务对象。
- 不允许反向把告警规则写入 Facility 主表作为长期结构。

---

### 3.7 task / inspection / work-order 模块（未来迁移）

负责：
- 任务定义
- 巡检计划
- 执行记录
- 工单闭环
- 面向 Facility / Area 的业务流程

不负责：
- Facility 主数据维护
- Scene / ActorInstance 维护
- Twin 主存储

约束：
- 任务流程只引用 `facilityId`、`areaId`、`siteId` 等业务主键。
- 不允许让 Task / Inspection 直接依赖 Scene 的空间对象作为主业务对象。
- 场景只是入口，不是任务领域拥有者。

---

## 4. 模块间允许的依赖关系

### 4.1 允许依赖的方式

模块间通信只允许以下方式：

1. `xxx-api` 暴露跨模块能力
2. Facade / QueryService 暴露只读聚合查询
3. Domain Event / Integration Event 实现异步联动
4. DTO / Summary 对象传递必要信息

---

### 4.2 禁止依赖的方式

明确禁止：

1. 直接注入其他模块的 Mapper
2. 直接操作其他模块的 DO
3. 直接复用其他模块的内部 Service 实现类
4. 通过共享 SQL / 共享 XML / 共享内部枚举实现跨模块业务
5. Controller 层跨模块拼装大量业务逻辑
6. 为了“省事”把多个领域长期塞进一个模块

---

### 4.3 典型正确方式

#### 正确：通过 API 查 Facility 摘要

- `inspection` 需要展示设施名称
- 调用 `facility-api.getFacilitySimple(facilityId)`

#### 错误：直接查 Facility 表

- `inspection` 直接依赖 `FacilityMapper`
- 直接拿 `FacilityDO`
- 直接 JOIN facility 内部扩展表

---

## 5. 数据建模规范

### 5.1 主表只存本领域稳定语义

以 Facility 为例，主表只允许放：

- 基础标识
- 分类归属
- 空间归属（site / area）
- 业务基础属性
- 生命周期状态
- 扩展属性引用

禁止放：

- latestTemperature
- latestPressure
- onlineStatus
- currentAlarmLevel
- currentTaskStatus
- actorName / actorPath 这类场景内部长期字段

这些都应分散到各自领域。

---

### 5.2 映射关系单独建模

以下关系不能直接塞到主表里：

- Facility ↔ ActorInstance
- Facility ↔ MonitoringProfile
- Facility ↔ AlarmRule
- Facility ↔ InspectionPlan

必须根据关系性质决定：

- 是从属关系：建从属表
- 是桥接关系：建 mapping 表
- 是聚合视图：由 QueryService 组合

Twin 场景下：

- `facility` 不直接保存 `actorInstanceId` 作为唯一长期耦合结构
- 映射写入 `twin_mapping`

---

### 5.3 删除语义必须分层处理

删除 Facility 时，必须区分：

#### 可清理的从属数据
- Facility 扩展属性实例
- 展示配置
- 监控配置
- Twin 映射

#### 需要失效化的运行态数据
- runtime cache
- 实时推送路由
- 活动告警上下文

#### 需要保留历史的业务过程数据
- 已完成任务
- 巡检记录
- 历史告警
- 审计日志

规范要求：
- 不允许简单“一删到底”。
- 默认优先采用逻辑删除 / 退役，而不是全量物理删除。
- 删除必须先做影响分析。

---

## 6. 接口设计规范

### 6.1 跨模块 API 只暴露必要能力

推荐暴露：

- `getById`
- `getSimpleById`
- `listSimpleByIds`
- `exists`
- `validate`
- `countActiveReferences`

不推荐暴露：

- 大而全的内部分页查询结构
- 直接暴露内部 DO
- 直接透出内部条件对象 / 内部 Mapper 查询结构

---

### 6.2 跨模块传输只用 DTO / Summary，不传 DO

允许：
- `FacilitySimpleRespDTO`
- `ActorInstanceSimpleRespDTO`
- `TwinMappingRespDTO`

禁止：
- `FacilityDO`
- `ActorInstanceDO`
- `AlarmRecordDO`
- `InspectionTaskDO`

原因：
- DO 属于内部持久化结构，不是稳定契约。
- 直接传 DO 会导致内部表结构变更向外扩散。

---

### 6.3 聚合查询必须下沉到 QueryService / Facade

当页面要同时展示：

- Facility 基础信息
- Twin 映射状态
- 最新遥测
- 当前告警
- 最近任务摘要

不得在 Controller 里直接串 5 个 Service 并拼 VO。

必须使用：

- `FacilityWorkbenchQueryService`
- `FacilitySceneFacade`
- `TwinWorkbenchFacade`
- 其他聚合应用服务

这些服务的职责是：
- 编排查询
- 裁剪字段
- 形成页面契约

领域 Service 只负责自己的领域逻辑。

---

## 7. 事件驱动规范

### 7.1 什么场景必须使用事件

以下场景优先使用领域事件 / 集成事件：

- Facility 删除 / 退役后联动 Twin / Runtime / Alarm / Task
- Twin 映射变更后刷新 Workbench 聚合状态
- 告警触发后更新运行态摘要
- 任务完成后更新业务统计

---

### 7.2 事件不能替代主数据查询

事件适合表达：
- 发生了什么
- 哪些模块需要联动收尾

事件不适合表达：
- 主数据的完整实时读取
- 页面每次查询时的主视图

因此：
- 页面查询走 QueryService / API
- 业务联动走 Event

---

### 7.3 推荐事件示例

- `FacilityCreatedEvent`
- `FacilityUpdatedEvent`
- `FacilityRetiredEvent`
- `FacilityDeletedEvent`
- `TwinMappingBoundEvent`
- `TwinMappingUnboundEvent`
- `AlarmTriggeredEvent`
- `InspectionTaskCompletedEvent`

---

## 8. 典型场景设计规范

### 8.1 Facility 与 ActorInstance 绑定

#### 正确流程

1. 用户在 3D 场景中选择 `actorInstance`
2. 或者在 Facility 管理页选择 Facility
3. 通过 Twin 绑定弹窗完成对应关系选择
4. Twin 模块写入 `twin_mapping`
5. Scene 与 Facility 分别保持自身主数据不变

#### 规范要求

- 不以 Actor 名称准确命名作为绑定前提
- 不依赖“Actor 命名正确”来自动识别 Facility
- 绑定必须支持**从场景选 Actor，再去选 Facility** 的交互模式

---

### 8.2 前端订阅管理

#### 错误方式

- 每个设备一个订阅
- 每个 Actor 一个订阅
- 每个测点一个 Subject，前端直接订阅海量主题

#### 正确方式

前端订阅运行通道：

- `telemetry`
- `transform`
- `alarm`
- `scene`
- `control`

由后端聚合：

- NATS 内部事件
- EntityStateStore
- FrameAggregator
- RuntimeFrame WebSocket 推送

规范要求：
- 前端是 Runtime Renderer，不是 NATS Object Consumer。
- WebSocket 推送的是运行态变更帧，不是对象逐条订阅模型。

---

### 8.3 删除 Facility 的联动规则

删除 Facility 时：

1. 先做影响分析
2. 默认采用逻辑删除 / 退役
3. Twin 映射解除
4. 从属配置清理
5. Runtime 状态失效
6. 活动任务关闭，历史任务保留
7. 历史告警、审计日志保留

规范要求：
- 不能直接数据库级联物理删除所有关联记录。
- 必须区分从属配置、运行态、历史过程数据。

---

## 9. 开发实施规范

### 9.1 新增功能前必须完成的架构检查

每个新需求开发前，至少确认以下问题：

1. 数据属于哪个模块？
2. 是否已有拥有者模块？
3. 是否在主表中放入了不属于本领域的字段？
4. 是否跨模块直接依赖了 Mapper / DO？
5. 是否应该通过 Twin / Event / Facade 解耦？
6. 页面聚合是不是错误地下沉到了领域 Service？

---

### 9.2 Code Review 必查项

PR / 提交审核时必须检查：

- 是否新增了跨模块 Mapper 依赖
- 是否把运行态字段写进了 Facility / Scene 主表
- 是否直接把 DO 暴露给其他模块
- 是否在 Controller 里编排复杂业务逻辑
- 是否错误地把 Scene 当成业务主对象
- 是否错误地把 Facility 当成实时态承载对象

存在以上任一情况，应视为架构违规。

---

### 9.3 迁移旧系统功能时的处理原则

迁移旧系统功能时必须遵守：

1. 只迁移业务能力，不迁移旧耦合结构。
2. 旧系统的页面结构可参考，后端实现必须重新按新边界设计。
3. 若旧功能同时依赖 Facility / Scene / Task / Alarm，先拆职责，再实现。
4. 不能因为旧接口方便就把多个域重新揉回一个模块。

---

## 10. 后续迁移建议顺序

推荐顺序：

1. Site / Area / Facility 主数据稳定
2. Twin 映射稳定
3. Scene 工作台与 3D 绑定能力稳定
4. Runtime 聚合推送稳定
5. Alarm 规则与状态稳定
6. Task / Inspection / WorkOrder 分域迁移
7. 聚合工作台按 Facade 补齐

说明：
- 先立主骨架，再叠业务流程。
- 先解耦主数据与空间，再接入运行态与任务域。

---

## 11. 一句话架构总纲

后续开发必须长期遵守以下总纲：

> **主数据归主数据，空间归空间，映射归 Twin，运行态归 Runtime，业务流程归 Task/Inspection，页面聚合不等于领域归属。**

如果一个实现让模块边界重新模糊，就说明设计方向错了，需要先改设计再写代码。

---

## 12. 与现有文档的关系

本规范作为总纲，与以下文档配合使用：

- `docs/scene-platform-architecture-analysis-20260508 copy.md`
- `docs/facility-scene-architecture.md`
- 后续 Facility / Twin / Runtime / Alarm / Inspection 专项设计文档

使用方式：

- 本文档负责**模块边界与解耦规则**
- 专项设计文档负责**某一模块的详细建模与接口实现**

后续若专项设计与本规范冲突，以本规范为准，先修正专项设计再开发。
