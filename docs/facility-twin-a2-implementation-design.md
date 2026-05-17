# Facility ↔ ActorInstance 严格 A2 落地实施设计稿

> 版本：v1
> 日期：2026-05-11
> 适用范围：`facility-management`、`twin`、`scene-platform`、前端 `sinopec-ecs-vue3`
> 目标：将 Facility 与 ActorInstance 的关联能力按“方案 A2：架构严格且职责彻底下沉”落地，先完成映射链路，再为后续 Scene 前端展现、运行态订阅、告警叠加提供稳定基础。

---

## 1. 设计目标

本阶段只落地一个清晰目标：

**让用户可以在两个业务入口中完成 Facility 与 ActorInstance 的双向关联，并由 Twin 模块统一持久化映射关系。**

不在本阶段落地：

- 运行态遥测订阅
- 告警推送
- 任务/巡检联动
- 复杂空间区域建模
- 3D 页面完整运行态叠加

---

## 2. 必须坚持的 A2 架构边界

### 2.1 Facility 模块负责什么

Facility 模块只负责：

- Site / Facility 主数据管理
- 设施分类归属（统一复用 system/category）
- Facility 台账、详情、导入导出
- 面向前端提供 Facility 树、列表、详情、简单引用数据

Facility 模块不负责：

- 保存 `actorInstanceId`
- 保存 `sceneId` / `sceneCode` 作为刚性主关系
- 保存 3D 选择状态
- 管理 Twin 映射主表

### 2.2 Scene 模块负责什么

Scene 模块只负责：

- Scene / Actor / ActorInstance 空间对象能力
- 场景内对象查询、定位、选中、高亮所需空间信息
- 提供 ActorInstance 简要信息给 Twin / Frontend 使用

Scene 模块不负责：

- 保存 Facility 业务字段
- 保存 Facility 与 ActorInstance 的业务映射主关系
- 负责 Facility 树或 Facility 分类规则

### 2.3 Twin 模块负责什么

Twin 模块只负责：

- Facility ↔ ActorInstance 的映射建立、替换、解除、查询
- 映射合法性校验
- 映射生命周期事件发布
- 为前端/聚合接口提供映射视图

Twin 模块是唯一映射拥有者。

### 2.4 Frontend 负责什么

前端负责：

- 在 3D 中选中 ActorInstance
- 在 Facility 树/表中选中 Facility
- 发起绑定/换绑/解绑请求
- 展示当前映射状态

前端不负责：

- 猜测映射关系
- 通过名称自动推断绑定
- 在本地长期缓存映射真相

---

## 3. 为什么必须这样设计

### 3.1 ActorInstance 命名天然不可靠

不能依赖：

- `actorInstanceName == facilityName`
- `actorCode == facilityCode`
- 场景搭建人员一定会先正确重命名

因此，**映射必须来自“视觉选择 + 人工确认”**，而不是命名约定。

### 3.2 映射是桥接关系，不是任一主表字段

Facility 与 ActorInstance 分属两个领域：

- Facility 是业务主数据
- ActorInstance 是空间对象

二者关系本质是桥接关系，必须单独建模到 Twin。

### 3.3 后续扩展必须可持续

若今天把 `actorInstanceId` 直接塞进 facility，后续会遇到：

- 一设施多空间表达
- 一空间对象先解绑再重绑
- 多场景下同一设施不同表达
- 将来扩展 Area ↔ Actor、Device ↔ Actor 时结构失控

Twin 单独建模后，这些问题都可在映射层演进。

---

## 4. 本阶段用户场景

## 4.1 场景一：在 3D 场景中选 Actor，再去关联 Facility

适用场景：

- 现场/实施人员已经打开场景
- 能看出 3D 模型对应哪个真实设施
- 希望快速逐个建立绑定

用户流程：

1. 用户进入 Scene 编辑/配置态页面
2. 在 3D 视图中点击一个 ActorInstance
3. 右侧编辑区或弹窗显示该 ActorInstance 简要信息
4. 用户在 Facility 树/表中选择对应 Facility
5. 点击“关联设施”
6. 后端调用 Twin 绑定接口
7. Twin 完成映射持久化并返回最新状态
8. 前端刷新该 Actor 的“已绑定/未绑定”状态

## 4.2 场景二：在 Facility 管理页中选 Facility，再去 3D 中选 Actor

适用场景：

- 台账管理人员先从设施清单出发
- 需要逐条确认某设施在场景中的映射对象

用户流程：

1. 用户进入 Facility 管理页
2. 左侧为 Facility 分类树/列表
3. 右侧或嵌入区域显示 3D 场景
4. 用户先选中一个 Facility
5. 再在 3D 中点击对应 ActorInstance
6. 点击确认建立关联
7. 后端写入 TwinMapping
8. 页面刷新 Facility 当前绑定状态

## 4.3 场景三：在 3D 中右键快捷关联

适用场景：

- 需要节省页面空间
- 需要高频快速映射

用户流程：

1. 选中 ActorInstance
2. 右键打开上下文菜单
3. 点击“关联设施”
4. 弹出轻量选择弹窗（Facility 树 + 搜索 + 列表）
5. 选择 Facility 并确认
6. Twin 写入映射

---

## 5. 本阶段前后端能力拆解

## 5.1 Facility 模块新增/提供能力

### 5.1.1 基础能力

- Facility 分类树查询
- Facility 分页/列表查询
- Facility 详情查询
- Facility 简要列表查询（给绑定弹窗用）
- Facility 搜索（名称/编码）

### 5.1.2 给 Twin/聚合层使用的轻量接口

建议提供：

- `getFacilitySimple(id)`
- `listFacilitySimpleByIds(ids)`
- `pageFacilitiesForBinding(query)`
- `treeFacilitiesForBinding(siteId, categoryId, keyword)`

返回字段建议最小化：

- `id`
- `code`
- `name`
- `categoryId`
- `categoryName`
- `siteId`
- `siteName`
- `status`

## 5.2 Scene 模块新增/提供能力

### 5.2.1 ActorInstance 轻量查询能力

建议提供：

- `getActorInstanceSimple(id)`
- `listActorInstanceSimpleByIds(ids)`
- `pageSceneActorInstancesForBinding(sceneId, keyword)`
- `listSceneActorInstancesForSelection(sceneId, filters)`

返回字段建议：

- `id`
- `sceneId`
- `sceneCode`
- `actorCode`
- `instanceCode`
- `instanceName`
- `path`
- `visibleFlag`
- `transformSummary`
- `bindingStatus`（该字段可由聚合层填充，不建议 Scene 主接口直接拥有）

### 5.2.2 3D 交互相关能力

Scene 前端或 Scene API 至少需要支持：

- 通过点击事件拿到 `actorInstanceId`
- 高亮指定 `actorInstanceId`
- 定位指定 `actorInstanceId`
- 返回选中对象基础属性供右侧面板展示

## 5.3 Twin 模块新增核心能力

Twin 模块是本阶段核心。

必须落地以下能力：

1. 绑定 Facility ↔ ActorInstance
2. 换绑 Facility ↔ ActorInstance
3. 解绑 Facility ↔ ActorInstance
4. 按 Facility 查询映射
5. 按 ActorInstance 查询映射
6. 按 sceneId / sceneCode 查询场景内映射概览
7. 校验唯一性与冲突
8. 发布绑定/解绑事件

---

## 6. 映射规则设计

## 6.1 本阶段推荐关系约束

本阶段先收敛为：

- 一个 Facility 同时最多绑定一个主 ActorInstance
- 一个 ActorInstance 同时最多绑定一个 Facility

即先按 **1:1 主绑定** 落地。

原因：

- 能满足当前绝大多数设施台账与 3D 对位需求
- 简化前端交互和冲突判断
- 给后续扩展到 1:N / N:1 留空间，但不在现在放开

## 6.2 未来扩展方式

若后续出现：

- 一个设施对应多个 3D 子部件
- 一个 3D 聚合对象代表多设施

则通过 Twin 的关系类型扩展，不回写 Facility 或 Scene 主表。

---

## 7. 映射建立时的校验规则

Twin 在绑定时必须做以下校验：

### 7.1 Facility 侧校验

- Facility 必须存在
- Facility 不能已删除
- Facility 状态必须允许绑定（如启用/可用）

### 7.2 ActorInstance 侧校验

- ActorInstance 必须存在
- ActorInstance 必须属于有效 Scene
- ActorInstance 不能已删除

### 7.3 唯一性校验

在 1:1 主绑定阶段：

- 若 Facility 已绑定其他 ActorInstance，则默认返回冲突，前端提示“是否换绑”
- 若 ActorInstance 已绑定其他 Facility，则默认返回冲突，前端提示“是否换绑”

### 7.4 场景一致性校验

建议校验：

- Facility 若已声明所属 Site/Area，则 ActorInstance 所属 Scene 应能映射到该 Site/Area 的业务范围

若现阶段无完整 Site-Scene 约束，可先不做强阻断，只保留可扩展字段。

---

## 8. 映射操作语义

## 8.1 bind

语义：当前无绑定或用户显式换绑时建立新映射。

输入：

- facilityId
- actorInstanceId
- sceneId 或 sceneCode（冗余校验/检索用）
- bindSource（SCENE_EDITOR / FACILITY_PAGE / CONTEXT_MENU）
- remark（可选）

输出：

- mappingId
- facilitySimple
- actorInstanceSimple
- bindStatus
- timestamps

## 8.2 rebind

语义：

- Facility 改绑到另一个 ActorInstance
- ActorInstance 改绑到另一个 Facility

要求：

- 明确保留一条历史还是覆盖当前主映射
- 本阶段建议：旧记录逻辑失效，新记录成为有效主映射

## 8.3 unbind

语义：解除当前有效映射。

要求：

- 默认逻辑失效，不建议物理删除
- 必须记录操作人、时间、原因

---

## 9. 聚合查询设计

为了不让 Facility 或 Scene Controller 互相串调用拼装，建议新增聚合查询层。

## 9.1 TwinWorkbenchQueryService

职责：

- 返回 Facility + Twin 状态列表
- 返回 Scene ActorInstance + Twin 状态列表
- 返回映射弹窗所需聚合数据

### 9.1.1 典型查询一：Facility 绑定视图

返回：

- Facility 基础信息
- 是否已绑定
- 绑定的 ActorInstance 简要信息
- 所属 Scene 简要信息

### 9.1.2 典型查询二：Scene 绑定视图

返回：

- ActorInstance 基础信息
- 是否已绑定
- 绑定的 Facility 简要信息
- 当前可执行动作（bind/rebind/unbind）

---

## 10. 前端页面实施方案

## 10.1 Facility 管理页改造

页面目标：在现有资产管理页中补齐新后端映射能力。

建议布局：

- 左：Facility 分类树
- 中：Facility 列表/详情
- 右：映射信息区或嵌入 3D 选择区

最少功能：

- 查看是否已绑定
- 打开“选择 3D 对象”模式
- 绑定/换绑/解绑
- 搜索 Facility
- 显示当前绑定的 Scene / ActorInstance 摘要

## 10.2 Scene 编辑页改造

最少功能：

- 点击 ActorInstance 得到选中对象
- 右侧出现“关联设施”区域
- 能搜索 Facility
- 能查看当前是否已绑定
- 能执行绑定/换绑/解绑

## 10.3 右键快捷菜单改造

最少功能：

- 右键菜单项：关联设施 / 更换关联 / 解除关联
- 弹窗内展示 Facility 树 + 搜索列表
- 确认后局部刷新当前对象状态

---

## 11. 后端接口建议

以下为逻辑接口，不限定最终 Controller 命名。

## 11.1 Twin 绑定接口

### 11.1.1 建立绑定

`POST /admin-api/twin/mapping/facility-actor/bind`

请求：

```json
{
  "facilityId": 1001,
  "actorInstanceId": 20001,
  "sceneId": 3001,
  "bindSource": "SCENE_EDITOR",
  "remark": "首次映射"
}
```

### 11.1.2 解除绑定

`POST /admin-api/twin/mapping/facility-actor/unbind`

### 11.1.3 查询 Facility 当前绑定

`GET /admin-api/twin/mapping/facility/{facilityId}`

### 11.1.4 查询 Actor 当前绑定

`GET /admin-api/twin/mapping/actor-instance/{actorInstanceId}`

### 11.1.5 查询场景内绑定概览

`GET /admin-api/twin/mapping/scene/{sceneId}/overview`

## 11.2 Facility 绑定辅助接口

### 11.2.1 绑定弹窗用 Facility 树

`GET /admin-api/facility/binding/tree`

### 11.2.2 绑定弹窗用 Facility 列表

`GET /admin-api/facility/binding/page`

## 11.3 Scene 绑定辅助接口

### 11.3.1 场景对象选择列表

`GET /admin-api/scene/actor-instance/binding/page`

---

## 12. 映射生命周期事件

Twin 成功处理后建议发布：

- `TwinMappingBoundEvent`
- `TwinMappingReboundEvent`
- `TwinMappingUnboundEvent`

事件最少字段：

- mappingId
- facilityId
- actorInstanceId
- sceneId
- operationType
- operatorId
- occurredAt

事件用途：

- 刷新缓存
- 刷新聚合视图
- 为后续 runtime / task / audit 联动留口

---

## 13. 错误处理策略

### 13.1 绑定冲突

场景：

- Facility 已绑定其他 Actor
- Actor 已绑定其他 Facility

处理：

- 返回明确冲突码
- 前端提示用户是否执行换绑
- 不做静默覆盖

### 13.2 目标不存在

处理：

- 返回对象不存在错误
- 前端刷新当前列表/场景状态

### 13.3 并发绑定

处理：

- 通过唯一索引 + 事务控制保证最终一致
- 失败时返回友好错误，提示用户刷新

---

## 14. 本阶段不落地但必须预留的能力

本阶段先不实现，但结构上要预留：

- 映射关系类型（主映射 / 辅助映射 / 聚合映射）
- 逻辑失效与历史追踪
- Site / Area 范围校验
- Mapping 标签和备注
- 与运行态订阅配置的解耦接入

---

## 15. 与后续表设计的衔接原则

表设计必须满足以下要求：

1. Twin 表独立，不回写 Facility / Scene 主表
2. 必须支持逻辑失效/历史留痕
3. 必须能支撑 1:1 当前约束与将来扩展
4. 必须保留操作审计字段
5. 必须支持按 facilityId、actorInstanceId、sceneId 高效查询

---

## 16. 本阶段完成标准

满足以下条件即可认为“Facility ↔ ActorInstance 映射落地完成”：

1. Facility 模块可以正常提供分类树、列表、详情、绑定弹窗数据
2. Scene 页面可以选中 ActorInstance 并发起绑定
3. Facility 页面可以选中 Facility 并在 3D 中建立绑定
4. Twin 模块可完成 bind / rebind / unbind / query
5. 绑定关系独立存储于 Twin 模块
6. 前端可以正确展示“已绑定/未绑定/换绑/解绑”状态
7. 不在 Facility / Scene 主表里引入对方的耦合字段

---

## 17. 实施顺序建议

### 第一步：Twin 表与 API

先完成：

- TwinMapping 表
- Mapper / DO / Service / Controller
- 唯一性校验与绑定语义

### 第二步：Facility 绑定辅助接口

补齐：

- Facility 树
- Facility 绑定列表
- Facility 简要 DTO

### 第三步：Scene 绑定辅助接口

补齐：

- ActorInstance 简要查询
- 场景内对象绑定概览

### 第四步：前端两个入口改造

实现：

- Facility 管理页入口
- Scene 编辑页入口
- 右键快捷入口

### 第五步：聚合查询与状态展示优化

实现：

- TwinWorkbenchQueryService
- 绑定状态统一回显
- 换绑/解绑交互优化

---

## 18. 结论

A2 方案下，Facility 与 ActorInstance 的关联必须被明确建模为 Twin 领域中的桥接关系。

它不能属于 Facility，也不能属于 Scene。

只有先把这条边界做干净，后续的场景展现、数据订阅、告警叠加、任务联动、区域建模，才不会再次回到旧系统那种“哪里方便就把逻辑塞哪里”的耦合方式。
