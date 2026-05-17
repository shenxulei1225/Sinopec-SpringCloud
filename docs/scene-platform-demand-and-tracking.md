# Scene Platform 需求分析与实现跟踪

> 生成时间：2026-05-08  
> 状态：需求分析完成，实现中

---

## 一、核心架构原则

### 1.1 服务边界

- **`facility-management`**：只保留业务主数据（设施树、设备主档、告警、巡检）
- **`scene-platform`**：统一负责三维/GIS 平台能力（场景、Actor、资产、组件、图层、坐标）

### 1.2 渲染与持久化分离

| 阶段 | 职责 | 位置 |
|------|------|------|
| **编辑态交互** | 前端实时渲染（Threejs） | 前端 SPA |
| **运行时渲染** | 前端实时渲染（Threejs / Cesium / Hybrid） | 前端 SPA |
| **模板管理** | 后端持久化 Actor 蓝图 | `scene-platform` 后端 |
| **实例持久化** | 后端持久化场景实例 | `scene-platform` 后端 |
| **场景同步** | 后端→前端推送 Actor 实例变化 | WebSocket |

### 1.3 混合交互模式

```
用户拖拽 Actor 到场景：
  1. 前端 Threejs 立即 spawn 3D 对象（即时反馈，不等后端）
  2. 前端 POST /scene-platform/actor-instances（异步持久化）
  3. 后端存入 ActorInstanceDO
  4. 后端通过 WebSocket 推送变化给其他客户端

页面刷新/打开场景：
  1. 前端 GET /scene-platform/scenes/{code}/descriptor（场景配置）
  2. 前端 GET /scene-platform/actor-instances?sceneId={id}（所有实例）
  3. 前端从数据重建 3D 场景
```

---

## 二、DO 层设计分析与对齐

### 2.1 Actor 蓝图（模板）

```
ActorDO (actor 表)
├── actorCode        -- 蓝图编号
├── actorName        -- 蓝图名称
├── actorClass       -- UE 类名 (如 "BP_Pump")
├── componentTree    -- SceneComponent 树根（JSON/ComponentTree 对象）
├── metadataJson     -- 扩展元数据
└── lifecycleStatus  -- 生命周期状态
```

**UE 概念映射**：
- `ActorDO` = UE 中的 Blueprint（蓝图定义）
- `ActorDO.componentTree` = Blueprint 内部的 SceneComponent 树
  - 包含：StaticMeshComponent、BoxComponent、LightComponent 等
  - 对应 UE 编辑器中的"组件面板"层级结构

**关键决策**：`componentTree` 存储在 `ActorDO` 中，作为 JSON 对象通过 `ComponentTreeTypeHandler` 读写。

### 2.2 Actor 实例（运行时）

```
ActorInstanceDO (actor_instance 表)
├── sceneId            -- 所属场景
├── actorCode          -- 引用 ActorDO (蓝图)
├── instanceCode       -- 实例唯一编号
├── instanceName       -- 实例显示名称
├── parentInstanceCode -- 父实例（层级关系）
├── transformJson      -- 实例独立变换（位置/旋转/缩放）
├── visibleFlag        -- 可见性
├── instanceStatus     -- 运行状态
└── metadataJson       -- 扩展元数据
```

**UE 概念映射**：
- `ActorInstanceDO` = UE 场景中放置的 Blueprint 实例
- `transformJson` = 实例运行时独立的位置/旋转/缩放值
- `parentInstanceCode` = Actor 的父子层级关系（继承 SceneComponent 的父子 Transform）

### 2.3 场景（合并后）

```
SceneDO (scene 表)  [合并 SceneDO + SceneInstanceDO]
├── sceneCode        -- 场景编号
├── sceneName        -- 场景名称
├── sceneType        -- 场景类型 (indoor/outdoor/hybrid)
├── engineProfile    -- 引擎配置 (threejs/cesium/hybrid)
├── status           -- 可见性 (draft/published)
├── coordinateRef    -- 坐标系参考
├── geoLayers        -- 地理图层配置
├── actorInstanceCodes -- 场景内 Actor 实例引用列表
└── metadataJson     -- 扩展元数据
```

**关键决策**（第 1037 章）：
- 合并 `SceneDO` 与 `SceneInstanceDO` 为单一场景实体
- 移除"模板 vs 实例"区分（场景本身无模板/实例之分）
- 保留 `status` 字段控制前端可见性
- `actorInstanceCodes` 保存场景内的 Actor 实例编号列表

### 2.4 场景级全局组件

```
SceneComponentDO (scene_component 表)
├── componentType   -- POST_PROCESS_VOLUME / WEATHER_SYSTEM / TRIGGER_VOLUME
├── sceneId         -- 所属场景
├── componentName   -- 组件实例名
├── relativeTransform -- 相对变换
├── configJson      -- 组件配置（雾效参数、天气参数等）
└── associatedActorIds -- 关联的 Actor 实例
```

**UE 概念映射**：
- 对应 UE 中**不属于任何 Actor** 的全局组件：后处理体积、天气系统、触发器 Volume
- 这些组件直接挂载在 Scene 上，独立于 Actor

### 2.5 引擎组件 DO（16 个派生类）

```
component/ 目录
├── ComponentDO.java              -- 基类
├── SceneComponentDO.java         -- 场景组件基类
├── SceneComponentInstanceDO.java -- 场景组件实例
├── StaticMeshComponentDO.java    -- 静态网格体
├── SkeletalMeshComponentDO.java  -- 骨架网格体
├── BoxComponentDO.java           -- 碰撞盒
├── SphereComponentDO.java        -- 碰撞球
├── CapsuleComponentDO.java       -- 碰撞胶囊
├── LightComponentDO.java         -- 光源
├── CameraComponentDO.java        -- 相机
├── PrimitiveComponentDO.java     -- 原始几何体
├── ChildActorComponentDO.java    -- 子 Actor 组件
├── SplineComponentDO.java        -- 样条线
├── SplineMeshComponentDO.java    -- 样条网格
├── WidgetComponentDO.java        -- UI 组件
├── TimelineComponentDO.java      -- 时间线
└── AudioComponentDO.java         -- 音频
```

**关键设计决策**（第 550 章）：
- 这 16 个组件 DO 是 **UE 引擎原生 SceneComponent 的派生类映射**
- 它们**不应该作为独立数据库表管理**
- 它们应该作为 `ActorDO.componentTree` 中组件树的子节点（JSON 嵌套）
- 独立表仅用于**场景级全局组件**（雾效/天气/触发器），即 `SceneComponentDO`

---

## 三、前后端交互契约

### 3.1 模板管理（Actor CRUD）

| 接口 | 方法 | 用途 |
|------|------|------|
| `/scene-platform/actors` | GET | 获取 Actor 蓝图列表 |
| `/scene-platform/actors/{code}` | GET | 获取 Actor 蓝图详情（含 componentTree） |
| `/scene-platform/actors` | POST | 创建 Actor 蓝图 |
| `/scene-platform/actors/{code}` | PUT | 更新 Actor 蓝图 |
| `/scene-platform/actors/{code}` | DELETE | 删除 Actor 蓝图 |

### 3.2 场景管理

| 接口 | 方法 | 用途 |
|------|------|------|
| `/scene-platform/scenes` | GET | 场景列表（按 status 过滤） |
| `/scene-platform/scenes/{code}` | GET | 场景详情（含 descriptor、坐标系、图层） |
| `/scene-platform/scenes` | POST | 创建场景 |
| `/scene-platform/scenes/{code}` | PUT | 更新场景 |
| `/scene-platform/scenes/{code}` | DELETE | 删除场景 |

### 3.3 Actor 实例管理

| 接口 | 方法 | 用途 |
|------|------|------|
| `/scene-platform/actor-instances?sceneId={id}` | GET | 获取场景内所有 Actor 实例 |
| `/scene-platform/actor-instances` | POST | 前端 spawn 后持久化 |
| `/scene-platform/actor-instances/{id}` | PUT | 更新实例（位置/状态等） |

### 3.4 场景快照（保存/加载/回滚）

| 接口 | 方法 | 用途 |
|------|------|------|
| `/scene-platform/scenes/{code}/snapshots` | GET | 获取快照列表 |
| `/scene-platform/scenes/{code}/snapshots` | POST | 手动创建快照 |
| `/scene-platform/scenes/{code}/snapshots/restore/{id}` | POST | 恢复到指定快照 |

**快照内容**：
- 所有 Actor 实例及其组件数据
- SceneComponent 全局组件配置
- GeoLayer 图层配置
- 坐标系参考

### 3.5 WebSocket 推送

- `ActorInstancePositionUpdateMessage`：Actor 实例位置/可见性变更广播
- 用于多用户协同编辑时的实时同步

---

## 四、实现状态跟踪

### 4.1 已完成

| 模块 | 状态 | 详情 |
|------|------|------|
| `ActorDO` | ✓ | 模板蓝图 DO，含 `componentTree` 字段（ComponentTreeTypeHandler） |
| `ComponentTree / ComponentTreeNode` | ✓ | 组件树模型类，含 Transform |
| `ActorService` | ✓ | 模板 CRUD + 组件树 CRUD |
| `ActorController` | ✓ | 模板管理 API |
| `ActorInstanceDO` | ✓ | 实例 DO，含 transformJson、层级关系 |
| `ActorInstanceService` | ✓ | 实例 CRUD，含 spawn 方法 |
| `ActorInstanceController` | ⚠️ | 路由有 bug（GET 绑定了 @PathVariable sceneId 但 URL 没带） |
| `SceneDO` | ✓ | 场景 DO（已合并 SceneInstanceDO） |
| `SceneInstanceService` | ✓ | 场景生命周期管理 |
| `ScenePublishSnapshotService` | ✓ | 快照保存/加载/回滚 |
| `CoordinateReferenceService` | ✓ | 坐标系管理 |
| `GeoLayerConfigService` | ✓ | 地理图层管理 |
| `SceneComponentService` | ✓ | 场景级全局组件管理 |
| WebSocket 基础设施 | ✓ | Message/Handler/Service/Config |

### 4.2 进行中

| 模块 | 待办 |
|------|------|
| `ActorInstanceController` | 修复 GET 路由匹配（sceneId 作为 query param 而非 path variable） |
| `SceneDO` | 确认合并后字段完整性（coordinateRef、geoLayers 存储方式） |

### 4.3 待实现

| 模块 | 优先级 | 说明 |
|------|--------|------|
| 前端编辑器 | P0 | Threejs 场景编辑器（拖拽 spawn、移动、删除 Actor 实例） |
| 前端运行态 | P0 | 场景加载与渲染（从后端拉数据重建场景） |
| 资产上传 | P1 | GLTF/OBJ 模型上传与资源管理 |
| 协作编辑 | P2 | WebSocket 多用户同步 |
| UE 引擎适配 | P3 | 对接 UE 引擎（当前对接 Threejs） |

---

## 五、已知问题

### 5.1 ActorInstanceController 路由不匹配

**问题**：`getActorInstanceList` 使用 `@GetMapping` + `@PathVariable Long sceneId`，但请求 URL 为 `/actor-instances`，不包含 sceneId。

**修复方案**：sceneId 改为 query parameter。

```java
// 修改前
@GetMapping
public CommonResult<List<ActorInstanceRespVO>> getActorInstanceList(@PathVariable Long sceneId)

// 修改后
@GetMapping
public CommonResult<List<ActorInstanceRespVO>> getActorInstanceList(
    @RequestParam Long sceneId)
```

### 5.2 SceneComponentDO vs 引擎组件 DO 概念混淆

**问题**：目录中有 16 个引擎组件 DO（StaticMeshComponentDO、BoxComponentDO 等），容易与 `SceneComponentDO`（场景级全局组件）混淆。

**设计澄清**：
- `SceneComponentDO` = 场景级全局组件（雾效/天气/触发器）→ 独立表
- 引擎组件 DO（16 个）= Actor 蓝图内部的 SceneComponent 树节点 → JSON 嵌入 ActorDO.componentTree

### 5.3 场景数据（Actor 实例）存储方式

**决策**：
- 场景内的 Actor 实例：通过 `ActorInstanceDO` 独立表存储，`sceneId` 外键关联
- `SceneDO.actorInstanceCodes`：仅保存实例编号列表（快速索引，不存完整数据）

---

## 六、技术栈

| 层 | 技术 |
|----|------|
| 后端 | Java 17 + Spring Boot 3.x + MyBatis-Plus |
| 前端 | React + Threejs (一期) → Cesium (二期) |
| 通信 | REST + WebSocket |
| 存储 | MySQL (MyBatis-Plus) |
| 部署 | Spring Cloud 微服务 |

---

## 七、与现有文档的关系

| 文档 | 关系 |
|------|------|
| `scene-platform-ue-architecture-analysis.md` | UE 架构分析（详细技术映射） |
| `scene-platform-frontend-backend-contract.md` | 前后端接口契约（descriptor、坐标、图层） |
| `scene-platform-final-alignment-checklist.md` | 最终对齐清单（服务边界） |
| `scene-platform-frontend-architecture.md` | 前端架构设计稿 |
| `facility-scene-architecture.md` | 设施与场景架构关系 |
| **本文档** | **综合需求分析与实现跟踪** |
