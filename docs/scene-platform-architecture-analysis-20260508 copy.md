# Scene-Platform 场景管理与前后端渲染协议分析（更新版）

> 更新日期: 2026-05-11
> 分析范围: `cheers-scene-3d` 当前代码、`sinopec-ecs-vue3` Three.js 实现场景、既有架构文档差异修正
> 目的: 统一当前 Scene / ActorInstance / ComponentTree / Asset / 前后端接口认知，为 3D 场景加载与渲染落地提供最新基线

---

## 1. 本次更新结论摘要

这份文档用于替换旧版 UE 映射分析中的过时结论。当前阶段最重要的结论如下：

1. **后端不应专门输出 Three.js 私有渲染结构**。后端应输出**引擎中立的场景协议**，由前端实现 Three.js 适配。
2. **ActorInstance 已经不是“只有 transform 的简单实例”**，而是场景中的统一实例载体，后续渲染、能力、层级、资源引用都应围绕 ActorInstance 展开。
3. **ComponentTree 是当前场景渲染的核心结构之一**。它不只是“组件树展示结构”，而是承载子组件、Mesh、材质、纹理、构造参数、能力组件等信息的统一表达。
4. **SceneLoadRespVO 中 `actors` 命名存在歧义，应统一为 `actorInstances`**，并且不应直接暴露 `ActorInstanceDO`，而应返回面向前端契约的 `ActorInstanceRespVO`。
5. **前后端接口必须完全统一**。路径、字段命名、返回结构都要以后端真实契约为准，前端严格对齐，避免“能编译但访问不到/解析不到”。
6. **旧版 animation 顶层字段不再保留**。动画能力后续应下沉为 `animationComponent` 或同类 capability component，而不是继续保留 `animationEnabled / animationDuration` 这种拍平字段。
7. **模型加载性能必须靠“资源缓存 + 实例化渲染”解决**。单纯每个实例各自加载模型，在大场景下不可行。
8. **资产管理是中长期方向，但短期可以先不做完整前端资产管理面板**。优先目标是先让场景正常加载、正常渲染、性能可接受。

---

## 2. 当前推荐的总体分层

### 2.1 推荐职责边界

#### 后端职责

后端负责输出**引擎中立的场景领域协议**，主要包括：

- Scene（场景定义）
- ActorInstance（场景实例）
- SceneComponent（场景级全局组件）
- ComponentTree（实例/模板的组件树）
- Asset 引用信息（模型、纹理、材质等）
- Runtime 状态数据

后端**不负责**：

- 输出 Three.js Mesh/Material/Object3D 私有结构
- 输出前端具体渲染树
- 直接替前端做 Three.js 专用坐标/材质/渲染优化逻辑

#### 前端职责（以 `sinopec-ecs-vue3` 为例）

前端负责具体引擎适配，这里是 Three.js：

- `Scene / ActorInstance / ComponentTree` → Three.js 可渲染对象
- Asset 引用 → GLTF / Texture / Material 加载
- 相同资源缓存
- 相同对象实例化渲染（后续可演进到 `InstancedMesh`）
- 交互层（拾取、拖拽、变换控制、显示隐藏等）

### 2.2 为什么不建议后端专门适配 Three.js

如果后端直接输出 Three.js 私有结构，会产生以下问题：

1. **后端绑定前端引擎**
   - 当前项目是 Three.js
   - 其他项目可能是 Cesium、Unity Web、UE Web、BIM 引擎或其他渲染层
   - 后端一旦 Three.js 化，协议失去复用能力

2. **后端承担渲染职责**
   - Mesh、Material、Texture 组合是引擎适配逻辑，不是业务领域逻辑
   - 放在业务后端会让 VO 不断膨胀，失去稳定性

3. **后续扩展困难**
   - LOD、Instancing、材质覆盖、阴影、动画、骨骼、Shader 参数都属于前端/引擎层问题
   - 不应在业务 Controller/VO 中拍平表达

因此本阶段建议保持：

> **后端输出稳定的领域协议，前端做 Three.js adapter。**

---

## 3. 模块结构更新认知

### 3.1 当前包结构（保留）

```text
scene-platform/
├── controller/admin/
│   ├── actor/          # Actor 模板 + ActorInstance Controller
│   ├── component/      # SceneComponent Controller
│   ├── runtime/        # SceneRuntime Controller
│   └── scene/          # Scene Controller
├── service/
│   ├── actor/          # Actor / ActorInstance / ActorInstanceComponent 服务
│   ├── component/      # SceneComponent 服务
│   ├── library/scene/  # 历史 SceneLibrary 逻辑
│   └── scene/          # SceneService / 场景加载/发布逻辑
├── dal/
│   ├── dataobject/
│   └── mysql/
├── model/              # Transform / Vector3 / Rotator 等模型
├── convert/
├── adapter/
│   └── spi/            # 引擎适配 SPI
└── websocket/
```

### 3.2 当前真正的前后端主链路

当前更应关注的是这条主链路：

```text
SceneController.load(sceneCode)
  -> SceneService.loadScene(sceneCode)
    -> SceneDO
    -> ActorInstance 列表
    -> SceneComponent 列表
    -> Runtime 数据
  -> SceneLoadRespVO
  -> 前端 adapter
  -> Three.js SceneCanvas
```

也就是说，真正支撑场景加载的核心不是旧文档里的 `SceneLibraryService`，而是：

- `SceneController`
- `SceneService.loadScene`
- `ActorInstanceService`
- 前端 `SceneCanvas / entityManager / modelFactory`

---

## 4. 数据模型更新分析

## 4.1 SceneDO - 场景定义

`SceneDO` 仍然是场景主定义对象，负责：

- `sceneCode`
- `sceneName`
- `sceneType`
- `engineProfile`
- `capabilitiesJson`
- `layerConfigJson`
- `defaultViewpointJson`
- `projectCode`
- `businessKey`
- `publishStatus`

### 当前建议

- 中长期仍建议统一场景定义与场景实例管理语义，减少历史 `SceneDO / SceneInstanceDO` 重叠。
- 短期先不以数据库表合并为阻塞项，而是优先统一**Controller / VO / 前端契约**。

---

## 4.2 ActorDO - Actor 模板

`ActorDO` 依然代表模板/定义层，承担：

- Actor 编码、名称、类型、分类
- 引擎 profile
- 能力声明
- `componentTree`
- 与资源的关联关系

### 与旧文档不同的关键点

旧文档对 `ActorDO` 的理解偏向“Blueprint 类 + modelUrl”。

当前应更新为：

> `ActorDO` 不只是一个简单的模型模板，而是 **实例构建蓝图**。它的关键不只是 `modelUrl`，而是 `componentTree` 中包含的组件结构与资源引用关系。

换言之：

- `modelUrl` 只是一个可能存在的快捷入口
- 更完整、可扩展的表达应来自 `componentTree`

---

## 4.3 ActorInstance - 当前核心实例载体

旧文档对 `ActorInstanceDO` 的描述已经不够准确。

当前更新认知如下：

### 旧认知

- ActorInstance 主要存位置、旋转、缩放
- 组件靠额外表拼装
- 动画字段挂在实例顶层

### 新认知

ActorInstance 应被视为：

> 场景中的统一实例节点，负责承载场景实例身份、层级、空间信息、组件树、能力组件、资源覆盖与运行时状态。

当前至少包含这些重要信息：

- `id`
- `sceneId`
- `actorCode`
- `instanceCode`
- `instanceName`
- `parentInstanceCode`
- `instanceStatus`
- `visibleFlag`
- `transform`
- `path`
- `layerKeys`
- `metadataJson`
- 组件信息 / `componentTree` / 组件列表

### 当前原则

- **实例自身通用属性**放在顶层
- **渲染能力与扩展能力**进入 `componentTree` 或 capability component

---

## 4.4 ComponentTree - 当前渲染协议核心之一

这是旧文档最需要修正的部分。

旧文档中，`componentTree` 更多被描述为模板组件树；当前实际应视为：

> 一个可承载渲染组件、构造参数、资源引用、能力组件、子节点层级关系的统一协议结构。

### 当前应认为 `componentTree` 可以承载

- Mesh / StaticMesh 类组件
- 材质与纹理引用
- 子组件层级
- 相对变换
- 构造参数
- 覆盖参数（override）
- 能力组件（如 animation、trigger、particle、light 等）

### 这意味着什么

这意味着后续渲染流程中，Three.js 前端更不应该依赖单独拍平字段，而应优先消费：

- `ActorInstance`
- `componentTree`
- asset 引用

来还原前端的 Object3D 层级。

---

## 4.5 ActorInstanceComponentDO 的地位

`ActorInstanceComponentDO` 仍然有价值，但它的角色应重新界定：

- 它可以是 `componentTree` 持久化后的展开结果
- 也可以是实例级覆盖、查询、审计、编辑的存储对象
- 但前后端协议不应长期停留在“只返回平铺组件表”这一层

更合理的方向是：

- 对外返回 `ActorInstanceRespVO`
- 其中包含 `componentTree` 或等价结构
- 平铺组件列表可作为辅助字段，而非唯一结构

---

## 4.6 Asset 资产管理

后端已经开始做资产管理，这是一条正确方向。

### 资产范围

- 模型
- 纹理
- 材质
- 其他资源（后续可扩展动画、音频、粒子配置等）

### 当前建议

短期目标：

- 不要求前端先做完整资产管理页面
- 先让前端能够消费后端资产引用并正常加载

中长期目标：

- 资产库
- 资产预览
- 资源依赖分析
- 替换/升级资产
- 预加载策略
- LOD/压缩/缓存策略

---

## 5. 当前后端接口基线（以真实代码为准）

## 5.1 SceneController 当前主接口

当前 `SceneController` 路径前缀为：

```java
@RequestMapping("/scene-platform/scene")
```

当前关键接口：

```text
POST   /scene-platform/scene/create
PUT    /scene-platform/scene/update
DELETE /scene-platform/scene/delete?id=...
GET    /scene-platform/scene/get?id=...
GET    /scene-platform/scene/list-all
GET    /scene-platform/scene/load?sceneCode=...
POST   /scene-platform/scene/publish?sceneCode=...
POST   /scene-platform/scene/unpublish?sceneCode=...
```

### 结论

前端必须按这套路径调用。旧文档和部分前端代码中出现的：

- `/scene-platform/scenes/...`
- `/scene-platform/scene-platform/...`
- `/scene-platform/actor-instance/...`

都必须统一清理，避免访问失败。

---

## 5.2 ActorInstanceController 当前接口

当前路径前缀为：

```java
@RequestMapping("/scene-platform/actor-instances")
```

关键接口：

```text
GET    /scene-platform/actor-instances/list?sceneId=...
GET    /scene-platform/actor-instances/{id}
POST   /scene-platform/actor-instances
PUT    /scene-platform/actor-instances/{id}
DELETE /scene-platform/actor-instances/{id}
POST   /scene-platform/actor-instances/spawn
```

### 结论

前端必须统一使用：

- `actor-instances`

而不是旧代码中出现的：

- `actor-instance`

---

## 5.3 SceneRuntimeController 当前接口

当前路径：

```java
@RequestMapping("/scene-platform/scenes/{sceneCode}/runtime")
```

接口：

```text
GET /scene-platform/scenes/{sceneCode}/runtime/package
```

### 说明

这里的 runtime package 更偏向运行时工作台包，而 `SceneController.load` 更偏向场景加载主入口。

两者可以并存，但语义要分清：

- `scene/load`：场景定义 + ActorInstance + SceneComponent 主加载入口
- `scenes/{sceneCode}/runtime/package`：运行时工作台补充包/高级编辑包

---

## 6. SceneLoadRespVO 当前问题与建议

## 6.1 当前实现

当前代码：

```java
private SceneDetailRespVO scene;
private List<ActorInstanceDO> actors;
private List<SceneComponentRespVO> components;
private Integer onlineCount;
```

## 6.2 存在的问题

### 问题 1：`actors` 命名歧义

`actors` 容易被理解为：

- Actor 模板列表
- Actor 定义列表
- Actor 蓝图类列表

但真实含义是：

- 场景中的 Actor 实例列表

### 问题 2：直接暴露 `ActorInstanceDO`

DO 不适合作为前端长期契约，原因：

- 持久化对象会随数据库结构演进
- 不能稳定表达前端语义
- 不适合承接 `componentTree / assetRef / capability` 的对外协议扩展

### 问题 3：`components` 语义不清晰

这里应明确它是：

- `sceneComponents`

即场景级全局组件，不是 Actor 实例组件。

## 6.3 建议改为

```java
private SceneDetailRespVO scene;
private List<ActorInstanceRespVO> actorInstances;
private List<SceneComponentRespVO> sceneComponents;
private Integer onlineCount;
```

### 建议说明

- `actorInstances`：明确表达场景实例
- `sceneComponents`：明确表达场景全局组件
- 统一返回 VO，不直接暴露 DO

---

## 7. ActorInstanceRespVO 的推荐方向

## 7.1 当前实现特点

当前 `ActorInstanceRespVO` 已包含：

- id / sceneId / actorCode / instanceCode / instanceName
- parentInstanceCode
- instanceStatus / visibleFlag / versionNo
- transform
- metadataJson / path / layerKeys
- components

这已经比旧文档中“只有位置”的理解前进很多。

## 7.2 当前不足

当前不足不在字段数量不够，而在于：

1. 命名和协议还没有完全统一
2. 还没有把 `componentTree` 提升为一等公民
3. 还没有明确资源引用来自哪一层
4. 动画/能力类字段未来应避免继续拍平到顶层

## 7.3 推荐方向

推荐 `ActorInstanceRespVO` 按下面原则演进：

### 顶层保留通用实例属性

- 身份字段
- 场景关联字段
- 层级字段
- 基础 transform
- 显隐状态
- metadata

### 渲染/能力信息进入组件层

- mesh
- texture
- material
- animation
- particle
- light
- trigger

都优先进入：

- `componentTree`
- 或 capability component 列表

### 不再保留旧式拍平动画字段

以下字段不建议继续保留：

- `animationEnabled`
- `animationDuration`
- `animationType`

应改为：

- `animationComponent`
- 或同类 capability component

---

## 8. modelUrl / 资产引用问题分析

这是当前最重要的渲染设计问题之一。

## 8.1 需要回答的问题

### 问题 1：模型从哪里来？

可能来源包括：

1. `ActorDO.modelUrl` 直接提供快捷模型地址
2. `componentTree` 中的 mesh 组件引用 asset
3. 后端资产系统通过 `assetCode / assetId` 提供统一资源引用

### 建议

不要把 `modelUrl` 简化理解为“实例上一个固定字段就够了”。更合理的顺序是：

```text
ActorInstance
  -> componentTree
    -> mesh component
      -> assetRef / assetCode / resourceUrl
```

如果当前为了快速落地，也可以临时支持：

- `ActorDO.modelUrl`

但中长期应回归 asset/componentTree 体系。

---

## 8.2 模型加载性能问题

### 结论

必须同时考虑：

1. **资源缓存**
2. **实例化渲染**

否则大场景一定卡。

### 资源缓存

同一个模型资源不能每个实例都重新下载、重新解析。

需要做到：

- 相同 asset / URL 只加载一次
- 之后从缓存复用

这部分主要由前端负责。

### 实例化渲染

ActorInstance 的本质就是实例化语义。

如果多个实例：

- 使用同一模型
- 使用同一几何和材质
- 只有 transform 不同

则后续应进入 `InstancedMesh` 或同类实例化渲染路径。

### 实施建议

#### 第一阶段

- 同 URL / asset 只加载一次
- 后续 clone 使用
- 先跑通主链路

#### 第二阶段

- 对满足条件的对象做渲染实例化
- 合并为 `THREE.InstancedMesh`
- 进一步优化大场景性能

---

## 9. 前端适配建议（以 Three.js 项目为例）

## 9.1 前端不应直接要求后端输出 Three.js 对象

前端应该维护一层 adapter：

```text
SceneLoadRespVO / ActorInstanceRespVO / ComponentTree
  -> Scene adapter
  -> Three.js SceneEntity / Object3D tree
```

## 9.2 前端适配层职责

- 解析 `actorInstances`
- 解析 `componentTree`
- 解析 asset 引用
- 将 transform 转成 Three.js 使用的结构
- 构造模型节点
- 做缓存与实例化优化

## 9.3 当前前端需要统一的点

### 接口路径统一

必须改成与后端一致：

- `/scene-platform/scene/load`
- `/scene-platform/scene/list-all`
- `/scene-platform/scene/update`
- `/scene-platform/actor-instances/list`
- `/scene-platform/scenes/{sceneCode}/runtime/package`

### 字段名统一

必须统一为：

- `actorInstances`
- `sceneComponents`
- `layerKeys`
- `componentTree`

而不是前端自己再造一套：

- `actors`
- `actorInstances` 混用
- `layerKey`
- `components` 含义不清

---

## 10. UE / Three.js / 引擎中立的映射更新

## 10.1 当前推荐映射表

| 概念 | 当前推荐映射 | 说明 |
|------|-------------|------|
| Actor 模板 | ActorDO | 实例蓝图/模板定义 |
| Actor 实例 | ActorInstance / ActorInstanceRespVO | 场景中的统一实例载体 |
| 子组件层级 | ComponentTree | 渲染与能力组件树 |
| 场景全局组件 | SceneComponentDO | 不属于任何 Actor 的场景级组件 |
| 模型/纹理/材质资源 | AssetResourceDO / assetRef | 通过资产系统统一管理 |
| 前端渲染节点 | Three.js Object3D / SceneEntity | 前端引擎适配结果 |

## 10.2 更新后的关键原则

### 原则 1

`ActorInstance != 只是 transform`

### 原则 2

`componentTree != 只是树控件结构`

### 原则 3

`asset != 单纯一个 modelUrl`

### 原则 4

后端返回的是**领域协议**，前端输出的是**引擎对象**。

---

## 11. 现阶段优先级建议

## P0：统一协议与接口

1. `SceneLoadRespVO.actors` → `actorInstances`
2. `SceneLoadRespVO.components` → `sceneComponents`
3. `SceneLoadRespVO` 不再直接返回 `ActorInstanceDO`，统一返回 `ActorInstanceRespVO`
4. 前端 API 路径完全与后端一致
5. 前后端字段命名完全一致
6. 去掉旧 animation 顶层字段保留计划

## P1：以 ActorInstance + componentTree 跑通场景加载

1. 前端基于 `SceneLoadRespVO` 加载场景
2. 前端 adapter 解析 `actorInstances`
3. 基于 `componentTree` 和 assetRef 构建渲染树
4. 先实现缓存 + clone 模式

## P2：性能优化

1. 资源缓存
2. 模型缓存
3. 纹理缓存
4. 批量实例化渲染（InstancedMesh）
5. LOD / 大场景优化

## P3：资产管理完善

1. 前端资产面板
2. 资源预览与替换
3. 依赖分析
4. 预加载与版本管理

---

## 12. 对旧文档中需要废弃或降级的结论

以下结论在当前版本中应降级或废弃：

1. **“ActorInstance 主要就是位置与旋转”**
   - 已过时

2. **“动画字段应该挂在 ActorInstance 顶层”**
   - 不建议继续沿用

3. **“后端可以按前端引擎输出专用结构”**
   - 不建议

4. **“modelUrl 足以表达实例渲染资源”**
   - 只适合临时快捷模式，不适合作为长期协议核心

5. **“前端只拿场景列表和实例列表就够了”**
   - 对简单占位体渲染成立；对真实场景渲染不够，需要 `componentTree + assetRef`

---

## 13. 当前推荐的契约方向（草案）

以下是推荐方向，不代表现有代码已全部完成：

### SceneLoadRespVO（目标）

```java
class SceneLoadRespVO {
    SceneDetailRespVO scene;
    List<ActorInstanceRespVO> actorInstances;
    List<SceneComponentRespVO> sceneComponents;
    Integer onlineCount;
}
```

### ActorInstanceRespVO（目标方向）

```java
class ActorInstanceRespVO {
    Long id;
    Long sceneId;
    String actorCode;
    String instanceCode;
    String instanceName;
    String parentInstanceCode;
    String instanceStatus;
    Boolean visibleFlag;
    Integer versionNo;
    Transform transform;
    String metadataJson;
    String path;
    String layerKeys;

    ComponentTreeVO componentTree;   // 推荐补齐/提升为主结构
    List<ActorInstanceComponentRespVO> components; // 可保留为辅助结构
}
```

### 前端适配目标

```text
ActorInstanceRespVO + componentTree + assetRef
  -> ThreeSceneAdapter
  -> Cached Renderable
  -> Clone / InstancedMesh
```

---

## 14. 当前前端落地情况（2026-05-12 补充）

`sinopec-ecs-vue3` 已按本文方案完成第一轮前端落地，当前状态如下：

### 14.1 已落地内容

1. **场景工作台路由已接入**
   - 路径：`/scene-workbench`
   - 页面：`SceneWorkbenchPage.vue`

2. **场景管理页已加入显式入口**
   - 页面头部提供“场景工作台”按钮
   - 场景列表每一行提供“进入工作台”按钮

3. **支持按 sceneCode 直达加载**
   - 前端支持：`/scene-workbench?sceneCode=xxx`
   - 从场景列表点击“进入工作台”时，会自动带上 `sceneCode`
   - 工作台页会读取 query 中的 `sceneCode` 并自动执行加载

4. **前端已按 ActorInstance 语义接入主加载链路**
   - 使用 `SceneLoadRespVO.scene`
   - 使用 `SceneLoadRespVO.actorInstances`
   - 使用 `SceneLoadRespVO.sceneComponents`
   - 不再以旧的 `actors/components` 歧义命名作为主协议

5. **已增加前端适配层**
   - 通过 scene workbench adapter 将后端中立协议转换为 Three.js 工作台运行时结构
   - 当前优先读取：
     - `ActorInstance`
     - `componentTree`
     - instance components
     - `modelUrl / meshCode / assetRef` 等信息

### 14.2 当前仍未完成的部分

1. ActorInstance Transform 持久化保存接口仍待与后端统一
2. group 拖拽 / 实例归组的真实写接口仍待后端补齐
3. 资产管理当前仍以“先可加载”为目标，尚未建设完整前端资产管理面板
4. Twin 映射、设施联动、告警/遥测订阅仍属于后续阶段

### 14.3 现阶段结论

当前 scene-platform 的正确演进方向不是“后端为 Three.js 单独做一套协议”，而是：

> **后端统一输出稳定的场景领域协议，前端在各项目中按各自渲染引擎做适配。**

对于当前 `sinopec-ecs-vue3` 项目：

- 可以明确采用 Three.js adapter
- 可以先不做完整前端资产管理
- 但必须先统一接口路径、字段命名、SceneLoadRespVO 结构与 ActorInstance 语义
- 并将 `componentTree` 提升为渲染主链路中的关键结构

这才是当前阶段既快、又不会把后续路线做死的方案。
