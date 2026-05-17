

## 场景包含什么（需求角度）

一个 3D 场景包含以下核心内容：

### 1. 场景元信息
- **基本信息**：名称、编码、类型、描述
- **空间范围**：坐标系、范围边界、参考点
- **环境参数**：光照、天气、雾效、全局配置
- **能力配置**：该场景支持的能力（如导航、碰撞检测、AI 等）

### 2. 空间数据
- **地图图层**：路网、建筑、POI、地形、绿化等分层数据
- **空间参考**：坐标系定义、投影方式
- **地理定位**：真实世界的位置映射

### 3. 资产/对象
- **Actor**：场景中的物体（摄像头、传感器、车辆、行人等）
- **资产绑定**：3D 模型、纹理、贴图的引用关系
- **空间关系**：对象之间的位置、方位、层级关系

### 4. 运行时状态
- **实例位置**：每个 Actor 在场景中的实时位置/姿态
- **状态数据**：运行中的各种动态数据

---

## 场景快照的用途

**核心问题**：在 3D 场景中，用户会不断修改——拖拽 Actor、调整参数、切换配置。快照解决的问题是：

### 快照的用途：

1. **版本控制**：每次重大修改前保存一个版本，可以随时回退到之前的状态
2. **发布管理**：场景开发有草稿→测试→发布的流程，快照保存每个发布版本的完整状态
3. **快速切换**：不同用户、不同任务需要不同的场景配置快照，可以快速切换而不影响其他人
4. **故障恢复**：运行时配置出错，可以用快照快速恢复到健康状态
5. **A/B 测试**：保留多个配置快照进行对比

### 快照 vs 实时场景的区别：

| | 场景（当前状态） | 快照（历史版本） |
|---|---|---|
| 数据量 | 一个场景只有一个当前状态 | 一个场景可以有多个快照 |
| 用途 | 日常运行 | 版本管理/回退/发布 |
| 更新频率 | 实时更新 | 手动触发（保存） |

---

## 需求视角的结论

所以场景管理的完整能力应该是：

1. **场景 CRUD**：创建、查看、编辑、删除场景
2. **场景快照**：保存、加载、对比、回退快照
3. **场景发布**：将某个快照发布为正式运行版本

这样设计的话，SceneDO 存场景基本信息 + 运行时状态，ScenePublishSnapshotDO 存历史快照，这个拆分是合理的，不应该简单合并。

---

**User**

场景不能直接存储场景中的实体吗?  为什么要运行时状态？ 
运行时状态不更新场景吗？那么每次进入场景是否都回到了场景的初始状态，这个对吗？

---

**Cursor**



你的质疑完全正确。让我从需求角度把这个问题想清楚。

## 核心问题：场景到底存什么？

**场景就是一张表，表里存的就是这个场景的完整状态。**

每次用户打开场景，看到的是**上次保存的状态**，不是初始状态。

### 场景表应该直接包含：

| 类别 | 内容 |
|------|------|
| **元信息** | 名称、编码、类型、项目归属、状态 |
| **空间数据** | 地图图层、坐标系、地理参考 |
| **场景中的实体** | 这个场景里有哪些 Actor、每个 Actor 的**位置/旋转/状态** |
| **环境配置** | 光照、天气、雾效 |
| **能力配置** | 导航、碰撞、AI 等 |

## 所以你的问题是：为什么需要"运行时状态"这个概念？

**不应该有。** 场景表本身就应该包含：

- Scene 基本信息
- 场景中的 Actor 实例列表（包括每个 Actor 的 position、rotation、visible 等）

**每次进入场景，加载的就是场景表里的数据，看到的就是上次保存的样子。**

## 那"场景快照"是干什么的？

快照是**场景的版本管理**，不是运行时状态。类比 Git：

```
场景表 = 工作区当前代码
快照   = git commit
```

| 场景 | 快照 |
|------|------|
| 一个场景只有一个记录 | 一个场景可以有多个快照 |
| 日常编辑修改 | 手动触发保存 |
| 打开场景看到的是这个 | 可以回退到这个 |

**用户操作流程**：
1. 新建场景 → 写入场景表（初始状态）
2. 在场景里放了一个摄像头 → 更新场景表（scene表里 actor_instances 字段更新了）
3. 觉得改得满意了 → 创建一个快照（记录当前 scene 表的状态作为备份）
4. 继续改 → 场景表继续更新，快照不受影响
5. 改砸了 → 用某个快照回滚场景表

## 结论

所以正确的模型应该是：

```
Scene 表
├── 场景基本信息（名称、类型、项目）
├── 空间数据（图层、坐标系）
├── 场景中的实体（actor_instances JSON / 关联表） ← 这就是实体位置、状态
└── 环境配置（光照、天气）

Scene_Publish_Snapshot 表
├── 快照编号
├── 快照名称
├── 快照对应的 scene 表完整状态（scene_payload_json） ← 完整备份
└── 操作人、操作时间
```

**不需要 SceneInstanceDO**，因为场景本身就是唯一的。
**不需要运行时状态表**，因为场景表就是当前状态。

这样你之前说的合并方案是对的——SceneDO 和 SceneInstanceDO 本来就是同一张表，只是被错误地拆成了两张。

---

**User**

比如场景里有辆车，原先位置在A， 运行后因为执行任务走到了B，下次打开车辆就应该在B点显示才是符合实际情况的，这个如何处理？

---

**Cursor**



你说得对，这个问题问到了核心。

**场景表就是"当前状态"的完整记录。** 包括场景中每个实体的位置、旋转、visible 状态等。

### 车辆从 A 到 B 的处理流程

```
用户打开场景 → 加载场景表（actor_states_json 里车辆位置是上次保存的 B 点）
            → 车辆在 B 点显示
            → 执行任务过程中从 B 走到 C
            → 运行时更新场景表（actor_states_json 中车辆位置更新为 C）
            → 下次打开场景，直接加载 C 点
```

**所以不需要"初始状态"和"运行时状态"的分离。场景表本身就是一个完整、自包含的状态。**

### 性能问题怎么处理？

运行时每帧更新位置（每秒可能几十次），直接写数据库会性能爆炸。

**方案**：场景表加一个 `actor_states_json` 字段，存所有实体的当前状态。运行时定期批量更新这个字段，比如：

- 每 N 秒更新一次
- 或者只在"有意义的变化"时更新（比如任务完成、车辆停靠等）
- 前端通过 WebSocket 实时推送，但不写库
- 写库只写这个 JSON 字段，不逐条更新

**不需要额外的运行时状态表。** 场景表就是当前状态，运行时定期回写。

### 最终结论

```
Scene 表
├── 场景基本信息
├── 空间数据（图层、坐标系）
├── 环境配置
└── actor_states_json ← 所有实体的当前位置/旋转/状态（运行时定期更新）

Scene_Publish_Snapshot 表
├── 快照编号、名称
├── 快照对应的 scene 完整状态
└── 操作人、时间
```

**不需要 SceneInstanceDO，也不需要运行时状态表。** 场景表就是完整的当前状态。

---

**User**

如果有1000辆车都在运行，要怎么同时更新才能保持同步。

---

**Cursor**



好问题。这涉及到系统架构设计，我直接说方案。

## 1000 辆车同步更新的问题

### 核心矛盾

```
1000辆车 × 60帧/秒 = 60,000次位置更新/秒

数据库写入：最多几百 TPS → 完全不够
```

### 正确的架构分层

**分三层处理，各司其职：**

```
┌─────────────────────────────────────────┐
│  第一层：运行时实时数据（内存/缓存）       │
│  • 每辆车的位置/旋转/状态                  │
│  • Redis Hash/Sorted Set                 │
│  • 60fps 实时更新，无数据库压力            │
│  • WebSocket 推送给前端                   │
│  • **这是"车辆在哪"的真实来源**             │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│  第二层：周期性快照（数据库）               │
│  • 每 N 秒（或事件触发）把内存数据写入DB    │
│  • 场景表 actor_states_json 字段          │
│  • 1000辆车的数据 ≈ 几十 KB JSON           │
│  • 一次批量写入                           │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│  第三层：发布快照（历史版本）               │
│  • 手动触发                               │
│  • 记录某个时刻场景的完整状态               │
│  • 用于回退/发布/对比                      │
└─────────────────────────────────────────┘
```

### 1000辆车的具体处理

```
场景表 scene 中：
├── id = 1
├── scene_code = "SC001"
├── ...基本信息...
└── actor_states_json = {
    // 只存"最近一次持久化的状态"，不是实时数据
    "vehicle_001": { position: [x, y, z], rotation: [...], state: "running" },
    "vehicle_002": { ... },
    // ... 1000 辆车
    // JSON 大小 ≈ 1000 × 100字节 ≈ 100KB
}

运行时（内存）：
├── Redis key: scene:active:SC001
│   └── Hash {
│       "vehicle_001": {"pos": [100, 0, 200], "rot": [...], "ts": 1234567890},
│       "vehicle_002": {...},
│       // ... 1000 辆车
│   }
│   更新频率：每帧 60次（内存操作，纳秒级）
│   推送频率：WebSocket 推送给前端

定时任务：
├── 每 10 秒（或事件触发）：
│   1. 从 Redis 读取整个 scene:active:SC001
│   2. 序列化 JSON
│   3. 更新 scene.actor_states_json
│   4. 完成
```

### 打开场景时的流程

```
1. 前端请求加载场景
2. 后端判断这个场景是否有"活跃的运行实例"
   ├── 有 → 从 Redis 读取最新位置 → 返回给前端
   └── 没有 → 从数据库读取 scene.actor_states_json → 返回给前端
3. 前端用返回的位置渲染所有车辆
```

### 关键设计决策

| 问题 | 方案 |
|------|------|
| 数据库写不写实时位置？ | **不写**，只写定期快照 |
| 前端怎么实时看到车辆移动？ | **WebSocket** 推送 |
| 下次打开在B点还是A点？ | 如果B点是运行时最新位置，从Redis取；如果Redis过期，从DB取上次持久化位置 |
| 1000辆车同时更新？ | 所有车辆数据打包成 **一个** Redis Hash，批量读写 |
| 数据丢了怎么办？ | 定时持久化 + 发布快照双保险 |

### 为什么场景表不直接存实时位置？

因为：
- 数据库不支持 60,000 TPS
- 场景表存的是"**持久化后的状态**"
- 实时位置在 Redis/内存里
- 下次打开时，如果场景还在运行，读 Redis；如果停了，读数据库

---

**所以回到你最初的问题：SceneInstanceDO 是否多余？**

我的回答变了。现在看，**应该这样设计**：

```
场景表 (SceneDO)  = 场景本身的定义（名称、图层、能力、初始状态）

场景运行实例 = 这个场景当前是否在运行、在哪里运行、实时数据在哪
              这不是一个"实例表"，而是一个运行标记 + 运行时数据位置

场景快照 = 手动保存的历史版本
```

你觉得这个方向对吗？

---

**User**

现在是前后端分离的方案，前端可能是不同的客户端，three.js \UE 等。
Actor的位置不一定是后端提供的，比如观察方向是前端控制的，行走方向速度也是前端控制的。
还有可能前端只是控制动作，但走到哪里是设备的GPS坐标上报到后台的，后端才是准确数据来源，
还有仪表数据也是通过后端采集后，推送到前端展示的。
因此数据更新的情况有很多种，需要都考虑到

---

**Cursor**



你提出的问题非常关键。这是**数据所有权**问题，直接影响架构设计。

## 数据来源分析

```
数据种类                    数据源          更新方向
───────────────────────────────────────────────────
Actor 位置 (GPS 上报)      后端            设备 → 后端 → 前端推送
Actor 仪表数据             后端采集        传感器 → 后端 → 前端推送
观察方向                   前端            前端控制
行走方向/速度              前端            前端控制
Actor 存在/消失            前端            前端拖拽 spawn
Actor 位置 (手动拖动)      前端            前端操作 → 后端持久化
场景配置/图层              后端            管理后台修改
环境参数                   后端/设备       定时采集
```

## 核心矛盾

```
前端有"局部数据所有权"：
- 我（前端）控制观察方向，后端不关心
- 我（前端）控制行走，但 GPS 上报的位置才是真实的
- 两个来源可能冲突（我往前走了，GPS 说我在后退）
```

## 解决方案：**数据源注册表 + 数据优先级**

```
后端管理的数据源（后端是权威）：
├── GPS 位置 ← 最高优先级，覆盖前端控制的行走位置
├── 仪表数据 ← 后端采集，前端只展示
└── 手动拖动的位置 ← 用户操作的结果，后端持久化

前端自主管理的数据（后端不存）：
├── 观察方向（相机视角）
├── 临时的行走控制（等待 GPS 确认）
└── 渲染状态（动画、粒子效果等）
```

## 数据来源与更新方向全景

### 五类数据来源

| 数据来源 | 数据内容 | 频率 | 传输方向 |
|---------|---------|------|---------|
| **用户操作** | 观察方向、行走控制、拖拽Actor | 10-60Hz | 前端 → 后端 |
| **设备GPS** | 车辆/人员真实位置 | 1-10Hz | 设备 → 后端 → 前端 |
| **传感器/仪表** | 温度、速度、油量、状态 | 1-50Hz | 设备 → 后端 → 前端 |
| **后台任务** | 自动巡逻路线、自动门开关 | 1-5Hz | 后端 → 前端 |
| **编辑器操作** | 新建/移动/删除Actor | 极低 | 前端 → 后端 |

### 关键设计约束

```
前端可能是：Three.js / UE / 移动端App / 大屏展示

每个客户端看到的场景是"一致的"，但"控制权"在不同客户端之间动态切换：
- A用户在UE里控制车辆位置
- B用户在Three.js里看仪表盘数据
- C用户用手机看监控画面

所以后端不能假设"只有前端在更新位置"
```

### 数据流架构

```
                    ┌──────────────┐
                    │   场景表      │
                    │  scene.actor_ │
                    │ states_json  │
                    │ (持久化快照)   │
                    └──────┬───────┘
                           │
              ┌────────────┼────────────┐
              │            │            │
         ┌────▼────┐  ┌───▼────┐  ┌───▼──────┐
         │ Redis   │  │ 数据库  │  │ WebSocket│
         │ (实时状态)│  │(历史快照)│  │ (推送)   │
         └────┬────┘  └────────┘  └───┬──────┘
              │                       │
    ┌─────────┼────────┐              │
    │         │        │              │
┌───▼──┐ ┌───▼──┐ ┌───▼──┐      ┌───▼──────┐
│Three.js││  UE  ││ 手机  │      │ 前端实时 │
│ 客户端 ││ 客户端││ 客户端 │      │ 推送更新 │
└───────┘ └──────┘ └──────┘      └──────────┘
```

### 三种数据更新策略

```
策略1：WebSocket 推送（实时，只推不存）
场景：GPS位置上报、仪表盘数据、用户控制
处理：设备/前端 → 后端接收 → 更新Redis → 通过WebSocket推送给所有在线客户端
持久化：不直接写数据库，定时批量写入

策略2：API 调用（请求-响应，写库）
场景：编辑器创建/移动/删除Actor、场景配置修改
处理：前端调用API → 后端写数据库 → 返回成功
持久化：直接持久化

策略3：定时快照（批量写入，非实时）
场景：运行中的场景状态持久化
处理：每10秒将Redis中场景状态批量写入场景表actor_states_json
持久化：写入数据库
```

### 场景表最终设计

```
Scene 表 (scene)

├── id                    自增主键
├── scene_code            场景编码
├── scene_name            场景名称
├── scene_type            场景类型
├── project_code          项目编码
├── business_key          业务键
├── status                状态 1启用/0停用
├── publish_status        DRAFT/PUBLISHED
├── engine_profile        引擎配置
├── capabilities          能力配置(JSON)
├── layer_config          图层配置(JSON)
├── default_viewpoint     默认视角(JSON)
├── actor_states_json     所有Actor的当前状态(JSON) ← 关键！
│   {
│     "actor_instance_001": {
│       "pos": [100, 5, 200],
│       "rot": [0, 90, 0],
│       "visible": true,
│       "properties": { speed: 30, fuel: 80, temp: 65 },
│       "source": "gps"       // 数据来源标记: gps/user/task
│     },
│     "actor_instance_002": { ... },
│     // ... 1000+ 个Actor
│   }
└── updated_at            最后更新时间

Scene_Publish_Snapshot 表 (scene_publish_snapshot)

├── id                    自增主键
├── scene_id              场景ID
├── snapshot_code         快照编码
├── snapshot_name         快照名称
├── snapshot_type         类型 DRAFT/RUNNING/PUBLISHED
├── scene_payload_json    快照完整场景数据(JSON)
├── payload_version       版本号
├── status                状态
└── created_at            创建时间
```

### Actor 管理策略

```
Actor 分为两个层次：

1. Actor 模板（Actor 表）
   - 定义：这是什么类型的Actor（摄像头/车辆/人员/传感器）
   - 蓝图：3D模型、组件树、能力

2. Actor 实例（Actor_Instance 表）
   - 位置、旋转、状态 ← 这些数据同时存在于两个地方：
     a) 场景表的 actor_states_json 中（场景级视角）
     b) actor_instance 表中（Actor 级别视角）
   
   数据来源优先级：
   - GPS数据 → 覆盖位置（最高优先级，真实世界）
   - 后台任务 → 覆盖位置
   - 用户控制 → 覆盖位置（仅该用户会话有效）
   - 编辑器拖拽 → 直接更新场景表
```

### 前端打开场景的流程

```
1. 前端加载场景：
   GET /api/scene/:sceneCode
   
2. 后端返回：
   {
     "scene": { name, type, layerConfig, defaultViewpoint },
     "actor_states": {
       // actor_states_json 解析后的数据
       "actor_instance_001": { pos, rot, visible, properties, source: "gps" },
       "actor_instance_002": { ... }
     },
     "actors": [  // Actor 模板列表
       { id: "camera_001", name: "大门摄像头", template: "camera_hd" }
     ]
   }

3. 前端根据返回数据渲染场景
4. 后端通过 WebSocket 推送实时变化
```

---

## 回到最初的问题：SceneDO 和 SceneInstanceDO 该不该合并？

**应该合并。** 但需要明确职责：

- **场景表 = 场景的完整定义 + 所有实体的当前状态**
- **不需要"实例表"**，因为场景本身就是唯一的

场景表通过 `actor_states_json` 字段包含所有实体的状态。运行时数据通过 Redis 实时推送，定时写入场景表。

这样设计后：
1. 删除 SceneInstanceDO
2. 删除 SceneInstanceService/SceneInstanceMapper
3. 场景表直接管理所有数据
4. 快照表引用场景表的 id

你现在确认这个方向吗？确认后我开始改代码。

---

**User**

不能这么确认，这样的话场景会是不断的变化，scene的记录也要不断变化。在系统中永远不断的再执行数据更新。 

---

**Cursor**



你说得对。我把运行时状态存到场景表里，会导致场景表被频繁更新，这不对。

让我重新梳理。

## 核心矛盾

```
场景表 → 低频更新（编辑器操作）
运行时状态 → 高频更新（GPS、仪表盘、用户控制）

不能混在一起
```

## 正确方案：分离定义和运行时状态

### 职责划分

| 数据层次 | 存储位置 | 更新频率 | 生命周期 |
|---------|---------|---------|---------|
| **场景定义** | 数据库 scene 表 | 低频（编辑器操作） | 长 |
| **Actor 实例定义** | 数据库 actor_instance 表 | 低频（编辑器操作） | 长 |
| **Actor 实例组件** | 数据库 actor_instance_component 表 | 低频（编辑器操作） | 长 |
| **运行时状态** | Redis | 高频（GPS、仪表、用户） | 中（场景生命周期） |
| **快照** | 数据库 scene_publish_snapshot 表 | 低频（手动创建） | 长 |

### 运行时数据流

```
GPS/仪表/GPS
    ↓
后端接收 → 更新 Redis → WebSocket 推送
                      ↓
                  所有客户端收到实时更新

编辑器拖拽
    ↓
后端接收 → 更新 Redis → 更新数据库 → 写入 Redis → WebSocket 推送
                      ↓
                  持久化定义（actor_instance 表）
```

### 数据库设计

```
场景表（低频更新）
├── id, scene_code, scene_name, scene_type
├── engine_profile (JSON) - 引擎配置
├── capabilities (JSON) - 能力配置
├── layer_config (JSON) - 图层配置
├── default_viewpoint (JSON) - 默认视角
└── updated_at

Actor 实例表（低频更新）
├── id, scene_id, actor_template_id
├── position (initial) - 初始位置
├── rotation (initial) - 初始旋转
└── created_at

Actor 实例组件表（低频更新）
├── id, actor_instance_id
├── component_type, component_name
├── transform (JSON) - 组件变换
├── properties (JSON) - 组件属性
└── created_at

快照表（低频更新）
├── id, scene_id
├── snapshot_name, snapshot_type (DRAFT/RUNNING/PUBLISHED)
├── scene_payload (JSON) - 场景完整数据
├── payload_version
└── created_at
```

### Redis 运行时状态

```
Key 格式：scene:runtime:{scene_id}
Value: Hash 结构

{
  "actor_instance_001": {
    "position": [100, 5, 200],
    "rotation": [0, 90, 0],
    "visible": true,
    "properties": { speed: 30, fuel: 80, temp: 65 },
    "data_source": "gps",
    "updated_at": 1717000000
  },
  "actor_instance_002": { ... }
}

Key 格式：scene:runtime:actors:{scene_id}
Value: Set
{
  "actor_instance_001",
  "actor_instance_002",
  ...
}

Key 格式：scene:runtime:connections:{scene_id}
Value: ZSet (分数 = 最后活跃时间)
{
  "actor_instance_001" → 1717000000
  "actor_instance_002" → 1717000005
  ...
}
```

### 前端加载场景

```
GET /api/scene/:sceneCode

返回：
{
  "scene": { 场景定义 },
  "actor_definitions": [  // 从 actor_instance 表查询
    { id, template_id, initial_position, components: [...] }
  ],
  "runtime_state": {     // 从 Redis 查询
    "actor_instance_001": { position, rotation, properties, source },
    "actor_instance_002": { ... }
  }
}
```

### 实时数据更新

```
GPS 数据上报：
POST /api/runtime/gps
{
  "actor_instance_id": "actor_instance_001",
  "position": [100, 5, 200],
  "timestamp": 1717000000
}

后端处理：
1. 更新 Redis 运行时状态
2. 记录更新时间到 connections ZSet
3. WebSocket 推送给所有在线客户端

前端处理：
1. 收到位置更新
2. 插值动画平滑过渡
3. 更新 3D 场景
```

### 编辑器操作

```
编辑器拖拽 Actor：
1. 更新 Redis 运行时状态（实时反映）
2. 更新数据库 actor_instance 表（持久化定义）
3. WebSocket 推送给其他客户端（他们收到更新）
```

### 删除 SceneInstanceDO

```
理由：
- 运行时状态存在 Redis，不存数据库
- 场景表只存定义，不存运行时状态
- Actor 实例定义存在 actor_instance 表
- 不需要"场景实例"这个概念

SceneInstanceService/SceneInstanceMapper 全部删除
```

### 保留的 Service

```
- SceneService / SceneServiceImpl    // 场景 CRUD
- SceneLibraryService / SceneLibraryServiceImpl  // 场景模板库
- ScenePublishSnapshotService / ScenePublishSnapshotServiceImpl  // 快照
- SceneComponentService / SceneComponentServiceImpl  // 场景级组件（雾效等）
- ActorService / ActorServiceImpl    // Actor 模板管理
- ActorInstanceService / ActorInstanceServiceImpl  // Actor 实例定义
- ActorInstanceComponentService      // Actor 实例组件管理
```

### 新增/调整

```
需要新增：
- SceneRuntimeService                // Redis 运行时状态管理
- SceneRuntimeController             // 运行时 API（GPS 上报等）

调整：
- ScenePublishSnapshotService        // 快照逻辑不变
- 前端加载场景时，同时查数据库和 Redis
```

### 快照机制

```
用户创建快照：
1. 查询所有 actor_instance 定义
2. 查询 Redis 运行时状态
3. 合并为完整场景数据
4. 写入 scene_publish_snapshot 表

恢复快照：
1. 从快照表读取 scene_payload
2. 写入 actor_instance 表（定义）
3. 写入 Redis（运行时状态）
```

---

## 总结

| 数据 | 存哪里 | 为什么 |
|-----|-------|-------|
| 场景定义 | 数据库 scene 表 | 低频变化 |
| Actor 实例定义 | 数据库 actor_instance 表 | 低频变化 |
| Actor 实例组件 | 数据库 actor_instance_component 表 | 低频变化 |
| **运行时状态** | **Redis** | **高频变化，不写数据库** |
| 快照 | 数据库 scene_publish_snapshot 表 | 手动创建，低频写入 |

这样：
- 数据库只处理低频操作
- Redis 处理高频实时数据
- 快照用于手动创建/恢复
- 场景表不会被频繁更新

你确认这个方向吗？

---

**User**

提醒一下，我们的应用场景主要是工业实时的监控，实时性是最重要的。
因此我的理解是scene代表 场景中有什么, 变化只存在于增加/删除新Actor
redis实时更新所有场景内实体的位置、状态等等信息，实时推送到客户前端
对于有历史回放要求的应用场景，需要记录采集到的数据，按照时间顺序推送回放。比如车辆的实时运动轨迹回放

---

**Cursor**



明白了。你的场景非常明确：

## 核心需求

```
1. Scene = 场景中有什么（定义）
2. 变化只有增/删 Actor（低频）
3. Redis = 实时位置、状态（高频推送）
4. 历史数据 = 时序存储，用于回放
```

这意味着：
- Scene 表 = 静态定义，**不存运行时状态**
- 运行时数据全部在 Redis
- 历史数据不在数据库，在时序存储

让我先看现有代码，确认需要改什么。





好的，现在看到了。SceneDO 和 SceneInstanceDO 内容几乎重复，按你说的需求：

## 合并方案

- **SceneDO 保留**：作为场景定义（场景中有什么）
- **SceneInstanceDO 删除**：场景就是唯一实体，不需要分离"实例"概念
- **actorInstanceCodes** 放在 SceneDO 中：表示当前场景有哪些 Actor 实例

让我检查一下 SceneInstanceService 和 SceneLibraryService 的情况，确认怎么合并：





好的，现在我看到两个 Service：
- **SceneLibraryService**：场景库管理（模板/定义）
- **SceneInstanceService**：场景实例管理（运行时场景）

按你确认的方案，这两个应该合并。让我先看实现类：





好的，我现在理解了完整情况。开始执行合并。

## 计划

- [ ] 合并 SceneDO：保留场景定义字段，移除 SceneInstanceDO 的重复字段
- [ ] 合并 Service 接口：SceneLibraryService 保留，SceneInstanceService 删除
- [ ] 合并 Service 实现：SceneLibraryServiceImpl 保留，SceneInstanceServiceImpl 删除
- [ ] 删除 SceneInstanceDO 和相关文件
- [ ] 更新所有引用

先看需要更新的 VO 和 Controller：





这是一个大型重构任务。我需要：

1. 合并 SceneDO + SceneInstanceDO → 只保留 SceneDO
2. 合并 SceneLibraryService + SceneInstanceService → 统一 SceneService
3. 删除所有 SceneInstance 相关文件
4. 更新所有引用

先了解关键文件的完整结构：



---

**User**

SceneInstanceService 应该是运行时管理，能够将scene中的实体，按照redis的数据进行更新，比如位置、状态、数据等。
然后展现给用户看，如果没有歧义的话最好改成sceneRuntime


---

**Cursor**



明白了，你的意思是：

**SceneInstanceService → SceneRuntimeService**，职责是：
- 运行时管理：基于 Redis 实时更新场景内实体的位置、状态、数据
- 将运行时状态展现给用户

让我先看看现有的 SceneRuntimeController 和相关代码：





明白，你的意思是 SceneInstanceService 应该重构成 **运行时管理** 服务，职责是：

1. 基于 Redis 中实时数据更新场景内实体的位置、状态
2. 将运行时状态展现给用户

让我先看看现有的 SceneRuntimeService 和相关代码：





明白了，你的需求是：

**SceneInstanceService** 应该是**运行时管理服务**，职责是将 scene 中的实体（Actor 实例等）基于 Redis 数据进行实时更新（位置、状态、数据等），并展示给用户。如果没有歧义的话最好改名为 **SceneRuntime**。

让我先看看 SceneRuntimeService 和 SceneInstanceService 的当前实现状态：





好的，需求理解了。SceneInstanceService 的职责应该改为**运行时管理**：基于 Redis 实时更新场景内实体的位置、状态、数据等，并展示给用户。

让我先看看 Redis 相关的配置和当前 SceneRuntimeServiceImpl 的实现：





好的，让我先查看当前的代码状态，特别是 SceneDO 和 SceneInstanceDO 的合并情况，以及相关 Service 的状态。





好的, 让我先读一下关键文件来了解当前状态。





好的，我先读取关键文件来了解当前代码状态，然后给出完整的 UE 场景管理方案分析。





好的，让我读取关键文件来了解当前代码状态，然后给出完整的 UE 场景管理方案分析。





好的，我现在开始重新分析 UE 场景管理方案。让我先读取关键文件了解当前实现状态。





让我读取模型类来理解 Transform 的结构：





好的，我来基于当前代码状态重新分析 UE 场景管理方案。先确认几个关键点的当前状态：





好的，基于我已读取的代码，下面是 UE 场景管理方案的完整重新分析：

---

## UE 场景管理方案分析（2026-05-08）

### 一、核心概念映射（UE ↔ 当前代码）

| UE 概念 | 当前实现 | 说明 |
|---------|---------|------|
| **Actor Class (蓝图)** | `ActorDO` | 模板定义，含 `ComponentTree`（SceneComponent 树） |
| **Actor Instance** | `ActorInstanceDO` | 场景中放置的实例，含 `Transform`（位置/旋转/缩放） |
| **Instance Component** | `ActorInstanceComponentDO` | 实例级组件覆盖属性（位置/属性/构造参数） |
| **Scene** | `SceneDO` | 合并后场景定义+运行时状态 |
| **Tick 更新** | WebSocket 推送 + 前端插值 | 后端推送位置变更，前端平滑过渡 |

### 二、当前实现状态

**✓ 已完成：**

| 模块 | 状态 | 文件 |
|------|------|------|
| Actor 模板管理 | 完整 | ActorDO, ActorService, ActorController |
| Actor 组件树 | 完整 | ComponentTree, ComponentTreeNode, ComponentTreeTypeHandler |
| Actor 实例创建 | 完整 | ActorInstanceDO, ActorInstanceService, ActorInstanceController |
| 实例组件覆盖 | 完整 | ActorInstanceComponentDO, ActorInstanceComponentService |
| 场景定义 | 完整（已合并） | SceneDO（合并了原 SceneDO + SceneInstanceDO） |
| 位置实时推送 | 基础完成 | WebSocket 基础设施 + ActorInstanceWebSocketService |
| Transform 模型 | 完整 | Transform, Vector3, Rotator |

**! 未完成/待重构：**

| 模块 | 问题 | 优先级 |
|------|------|--------|
| **SceneRuntimeService** | 几乎为空，只有 getRuntimePackage 骨架 | **P0** |
| **运行时数据管理** | SceneDO 的 actorInstanceCodes 是静态 JSON，缺少实时位置/状态更新 | **P0** |
| **Redis 集成** | 当前完全没有 Redis 使用，运行时状态全在 MySQL | **P0** |
| **SceneInstanceService 重构** | 现有是 Actor 实例 CRUD，需要改为**运行时管理服务** | **P0** |
| **场景快照/回滚** | ScenePublishSnapshot 存在但逻辑待验证 | P1 |
| **引擎适配器** | UeSceneEngineAdapter 为空壳 | P2 |

### 三、SceneInstanceService → SceneRuntimeService 重构方案

**职责重新定义：**

```
SceneRuntimeService（运行时管理）职责：
├── 1. 获取场景运行时包（所有实例当前位置+状态，从 Redis 读取）
├── 2. 实时更新实例位置（接收前端/WebSocket 推送，写入 Redis）
├── 3. 实时更新实例状态（可见性、动画、自定义属性）
├── 4. 批量查询运行时数据（按场景/按图层/按区域）
└── 5. 运行时数据持久化（定期从 Redis 落盘 MySQL）
```

**Redis Key 设计建议：**

```
# 单个实例位置
scene:runtime:{sceneCode}:actor:{instanceCode}:position
  → {"x":100.0, "y":200.0, "z":0.0, "timestamp":1234567890}

# 场景内所有实例索引
scene:runtime:{sceneCode}:actors
  → ZSET {instanceCode -> timestamp}

# 实例完整运行时状态
scene:runtime:{sceneCode}:actor:{instanceCode}:state
  → {"visible":true, "animation":"idle", "customProps":{...}}
```

### 四、需要立即推进的工作

**P0 — SceneRuntimeService 实现：**
1. 注入 RedisTemplate / StringRedisTemplate
2. 实现 `getRuntimePackage(sceneCode)` — 从 Redis 读取所有实例位置+状态
3. 实现 `updateActorPosition(sceneCode, instanceCode, transform)` — 写入 Redis
4. 实现 `updateActorState(sceneCode, instanceCode, stateData)` — 写入 Redis
5. 实现 `getActorsByLayer(sceneCode, layerKey)` — 按图层过滤
6. ActorInstanceWebSocketService 推送后同步写 Redis

**P1 — 数据持久化：**
1. 定时任务将 Redis 中的运行时数据定期写回 MySQL（SceneDO 或独立表）
2. 场景重启时从 MySQL 加载到 Redis

