# 设施管理与场景管理架构说明

## 1. 目标

本方案用于梳理设施管理（`facility-management`）与场景管理（`scene-platform`）的职责边界，并明确基于 UE 式 `Actor / Component` 的后端与前端实现方式。

核心目标：

- 避免设施业务与场景底座混在一起
- 避免 `entity`、`facility`、`scene`、`actor` 等概念重复
- 支持从模板库生成实例
- 支持对场景中的实例进行编辑
- 支持场景布局持久化与设施业务回写
- 支持 Three.js 前端直接消费后端语义
- 用目录分层避免 controller / service / api / dal 混淆

---

## 2. 总体原则

### 2.1 命名原则

统一使用 `scene`、`actor`、`component`、`asset` 作为核心语义。

原因：

- `scene` 表达场景容器与空间上下文
- `actor` 表达可实例化对象，避免和业务实体 `entity` 混淆
- `component` 表达能力，不把所有字段堆到 Actor 主表
- `asset` 表达资源，不和实例、模板、绑定混在一起

建议保留的核心命名：

- `scene_definition`
- `scene_instance`
- `scene_layout`
- `scene_layer`
- `coordinate_reference`
- `actor`
- `actor_instance`
- `actor_instance_component`
- `asset_resource`

### 2.2 分层原则

系统按以下 6 层拆分：

1. 场景层
2. 模板层
3. 实例层
4. 组件层
5. 资产层
6. 业务归属层

其中：

- `scene-platform` 主要负责 1、2、3、4、5 中的空间与能力语义
- `facility-management` 主要负责 6，以及设施业务的主数据与工作台查询

---

## 3. UE 结构映射

UE 的本质是：

- `World / Level` 承载场景上下文
- `Blueprint` 承载默认结构
- `Actor` 承载实例壳与生命周期
- `Component` 承载具体能力（分两套体系：引擎组件 + 能力组件）
- `Asset` 承载资源
- `PostProcessVolume / WeatherSystem` 等 Scene 级全局组件独立于 Actor

映射到本系统：

- `Scene` 对应场景上下文
- `ActorDO` 对应模板定义（统一蓝图角色）
- `ActorInstance` 对应实例壳
- `ActorInstanceComponent` 对应能力组件（Transform / Render / Interaction 等，独立表）
- `ActorDO.componentTree` 对应引擎组件树（SceneComponent 树，JSON 嵌入 ActorDO）
- `SceneComponentDO` 对应场景级全局组件（雾效/天气/触发器，独立表）
- `AssetResource` 对应资源

---

## 4. 模块职责边界

### 4.1 `scene-platform`

职责：

- 场景定义
- 场景实例
- 坐标参考系
- 图层管理
- Actor 模板库
- Actor 实例管理
- Actor 组件定义与覆盖
- 场景中实例的空间布局
- 实例编辑后的空间保存
- 资源绑定与加载语义

不负责：

- 设施台账
- 站场管理
- 设施业务状态
- 设施统计汇总

### 4.2 `facility-management`

职责：

- 站场管理
- 设施主数据
- 设施业务查询
- 设施工作台
- 设施与场景实例的业务绑定

不负责：

- 场景定义
- 坐标参考系
- 图层底座
- 实例布局底层持久化
- Actor 组件能力定义

---

## 5. UE 式核心对象

### 5.1 场景层

#### `Scene`
场景定义，表示一个场景的结构、规则与基础配置（对应 SceneDO）。

#### `SceneInstance`
场景实例，表示某个场景定义在运行时或编辑态下的具体载体。

#### `SceneLayer`
场景图层，用于组织场景中的对象分组、显示与编辑分层。

#### `SceneLayout`
场景布局，用于保存场景中实例的整体布局结果、草稿、基线、版本。

#### `SceneSnapshot`
场景快照，用于保存某一时点的布局与状态。

---

### 5.2 模板层

#### `Actor`（ActorDO）
Actor 模板，统一承载 Blueprint 角色，定义 Actor 的默认结构、默认能力与层级关系。

对应数据库表：`actor`

核心字段说明：

| 字段 | 类型 | 说明 |
|------|------|------|
| `actorCode` | String | Actor 编码（唯一标识） |
| `actorName` | String | Actor 名称 |
| `actorClass` | String | Actor 类名（如 `EquipmentActor`、`BuildingActor`） |
| `parentActorCode` | String | 父 Actor 编码，支持 Actor 层级嵌套 |
| `actorCategory` | String | Actor 分类（如 `facility`、`building`、`pipeline`） |
| `engineProfile` | String | 引擎配置文件引用（指向 UE Blueprint 或 Three.js 渲染配置） |
| `abstractFlag` | Boolean | 是否为抽象模板（`true` 表示不可直接实例化，仅作为父类） |
| `lifecycleStatus` | String | 生命周期状态（如 `draft`、`published`、`archived`） |
| `componentTree` | ComponentTree | Actor 默认组件树，对应 UE 的 SceneComponent 树，JSON 格式 |
| `metadataJson` | String | 扩展元数据 |

**ComponentTree（组件树）**：

`componentTree` 字段通过 `ComponentTreeTypeHandler` 自动将 JSON 转换为 `ComponentTree` 对象，存储 Actor 的默认组件树结构。

对应 UE 的 `SceneComponent` 树，一个 Actor 可以嵌套多层组件，每个组件负责一种能力：

```
ActorTemplate
└── RootComponent (SceneComponent)
    ├── TransformComponent (位置/旋转/缩放)
    ├── RenderComponent (渲染配置)
    ├── InteractionComponent (交互声明)
    └── SubComponent_01 (子组件，可再嵌套)
        ├── AudioComponent (音效)
        └── TriggerComponent (触发器)
```

**模板参数、组件槽、资产绑定的设计决策**：

- **模板参数**：通过 `componentTree` 中每个节点的 `configJson` 字段定义
- **组件槽**：通过 `componentTree` 的层级结构定义，每个节点声明组件类型与启用状态
- **资产绑定**：通过 `componentTree` 中 `RenderComponent` 的 `configJson`（含 `modelUrl`、`materialJson` 等）管理
- **扩展参数**：通过 `metadataJson` 自由扩展

这种设计的优势：

1. 避免多表 JOIN 带来的查询复杂度
2. `componentTree` 整体作为蓝图结构，支持深拷贝实例化
3. JSON 格式天然支持层级嵌套，与 UE 的 SceneComponent 树结构一致
4. 通过 `ComponentTreeTypeHandler` 自动序列化/反序列化，查询性能不影响

---

### 5.3 实例层

#### `ActorInstance`
场景中的具体对象，表示模板实例化后在某个场景中的实体壳。

`ActorInstance` 只保留：

- 身份
- 归属关系
- 生命周期
- 图层归属
- 可见性
- 版本
- 父子关系
- 运行态元信息

`ActorInstance` 不应直接堆放：

- 三维变换字段
- GIS 字段
- 渲染字段
- 交互字段
- 资产字段

这些应进入组件层。

---

## 6. Component 设计

### 6.1 Component 总原则

Component 表达的是“能力”，不是随手加字段。

组件应遵循：

- 先定义标准组件集合
- 再允许按模板选择启用
- 实例只能覆盖或启用已有模板能力
- 不允许实例无中生有新增能力键

---

### 6.2 标准组件集合

### A. `ActorInstanceTransformComponent`
三维变换基础组件。

字段：

- `localX`
- `localY`
- `localZ`
- `rotationX`
- `rotationY`
- `rotationZ`
- `scaleX`
- `scaleY`
- `scaleZ`

职责：

- 三维位置
- 旋转
- 缩放
- 层级变换

说明：

- 这是基础组件
- 几乎所有 Actor 都需要
- 不应放入 Actor 主表

### B. `ActorInstanceGeoComponent`
GIS 扩展组件。

字段：

- `geoLng`
- `geoLat`
- `geoHeight`
- 可选：`crsCode`、`referenceFrame`

职责：

- 地理定位
- GIS 坐标语义
- 地图 / 地球 / 坐标系联动

说明：

- 只在 GIS 场景或 GIS Actor 上启用
- 不应放入 Actor 主表

### C. `ActorInstanceRenderComponent`
渲染组件。

字段示例：

- `modelUrl`
- `materialJson`
- `textureJson`
- `renderMode`
- `shadowEnabled`
- `lodLevel`

职责：

- 模型
- 材质
- 纹理
- 渲染配置

### D. `ActorInstanceInteractionComponent`
交互能力声明组件。

字段示例：

- `clickable`
- `draggable`
- `rotatable`
- `scalable`
- `hoverable`
- `selectable`
- `editable`

职责：

- 声明此 Actor 是否支持某种交互
- 前端 Three.js 根据声明决定是否启用交互逻辑

说明：

- 后端只负责语义声明
- 前端负责具体交互实现

### E. `ActorInstanceStateComponent`
运行态与业务态组件。

字段示例：

- `stateCode`
- `status`
- `alarmFlag`
- `onlineFlag`
- `healthLevel`

职责：

- 运行状态
- 业务状态
- 设备状态映射

### F. `ActorInstanceBindingComponent`
业务绑定组件。

绑定对象可能包括：

- 设施主数据
- 资产
- 外部系统 ID
- 业务编码

职责：

- 表达 Actor 与外部业务对象的绑定关系

说明：

- 不能再使用泛化的 `SceneAssetBinding` 作为唯一绑定模型
- 必须明确绑定双方是谁

---

## 7. Transform 与 GIS 的边界

### 7.1 Transform

`localX/localY/localZ`、`rotationX/rotationY/rotationZ`、`scaleX/scaleY/scaleZ` 是三维场景的基础变换能力，应作为基础组件独立出来。

### 7.2 GIS

`geoLng/geoLat/geoHeight` 是 GIS 独有语义，应作为可选组件独立出来。

### 7.3 设计结论

- `Transform` 是基础能力
- `GIS` 是可选扩展
- 两者不应混在 Actor 主表
- 前端和运行时应以组件聚合方式读取

---

## 8. 编辑层与保存层

### 8.1 编辑层

实例可以被编辑，但编辑的对象不是模板本身，而是实例覆盖层。

可编辑内容：

- 位置
- 旋转
- 缩放
- 可见性
- 图层
- 组件开关
- 组件参数

### 8.2 保存层

场景平台保存的是实例布局结果：

- 当前布局
- 草稿
- 基线
- 版本

保存时建议：

- 批量保存
- 事务写入
- 不每次小改动都立即刷库

---

## 9. Three.js 前端消费方式

前端 Three.js 不应直接依赖一坨大对象，而应消费后端的组件语义。

### 9.1 前端消费对象

- `ActorInstance`
- `ActorInstanceTransformComponent`
- `ActorInstanceGeoComponent`
- `ActorInstanceRenderComponent`
- `ActorInstanceInteractionComponent`
- `ActorInstanceStateComponent`
- `ActorInstanceBindingComponent`

### 9.2 前端职责

前端负责：

- 拖拽
- 选中
- 高亮
- 旋转
- 缩放
- 视角控制
- 场景渲染

后端负责：

- 能力声明
- 组件配置
- 模板继承
- 实例覆盖
- 保存与版本

---

## 10. 建议目录结构

为避免混淆，建议按层级拆分目录，并进一步按能力拆分子目录。

### 10.1 `scene-platform` 建议目录

#### `api/`

- `api/scene/`
- `api/template/`
- `api/instance/`
- `api/component/`
- `api/asset/`
- `api/layout/`
- `api/layer/`
- `api/coordinate/`

#### `controller/admin/`

- `controller/admin/scene/`
- `controller/admin/template/`
- `controller/admin/instance/`
- `controller/admin/component/`
- `controller/admin/asset/`
- `controller/admin/layout/`
- `controller/admin/layer/`
- `controller/admin/coordinate/`

#### `service/`

- `service/scene/`
- `service/template/`
- `service/instance/`
- `service/component/`
- `service/asset/`
- `service/layout/`
- `service/layer/`
- `service/coordinate/`

#### `dal/dataobject/`

- `dal/dataobject/scene/`
- `dal/dataobject/template/`
- `dal/dataobject/instance/`
- `dal/dataobject/component/`
- `dal/dataobject/asset/`
- `dal/dataobject/layout/`
- `dal/dataobject/layer/`
- `dal/dataobject/coordinate/`

#### `dal/mysql/`

- `dal/mysql/scene/`
- `dal/mysql/template/`
- `dal/mysql/instance/`
- `dal/mysql/component/`
- `dal/mysql/asset/`
- `dal/mysql/layout/`
- `dal/mysql/layer/`
- `dal/mysql/coordinate/`

### 10.2 `facility-management` 建议目录

#### `api/`

- `api/site/`
- `api/facility/`
- `api/workbench/`
- `api/binding/`

#### `controller/admin/`

- `controller/admin/site/`
- `controller/admin/facility/`
- `controller/admin/workbench/`
- `controller/admin/category/`
- `controller/admin/status/`
- `controller/admin/binding/`

#### `service/`

- `service/site/`
- `service/facility/`
- `service/category/`
- `service/status/`
- `service/binding/`
- `service/workbench/`

#### `dal/dataobject/`

- `dal/dataobject/site/`
- `dal/dataobject/facility/`
- `dal/dataobject/category/`
- `dal/dataobject/status/`
- `dal/dataobject/binding/`

#### `dal/mysql/`

- `dal/mysql/site/`
- `dal/mysql/facility/`
- `dal/mysql/category/`
- `dal/mysql/status/`
- `dal/mysql/binding/`

---

## 11. 推荐对象命名

### 11.1 场景平台

- `SceneTemplate`
- `SceneInstance`
- `SceneLayout`
- `SceneLayer`
- `CoordinateReference`
- `ActorTemplate`
- `ActorInstance`
- `ActorInstanceTransformComponent`
- `ActorInstanceGeoComponent`
- `ActorInstanceRenderComponent`
- `ActorInstanceInteractionComponent`
- `ActorInstanceStateComponent`
- `ActorInstanceBindingComponent`
- `AssetResource`

### 11.2 设施管理

- `Site`
- `Facility`
- `FacilityCategory`
- `FacilityStatus`
- `FacilityActorInstanceBinding`

---

## 12. UE 式最终类图草案

### 12.1 场景层

```text
SceneTemplate
└── SceneInstance
    ├── SceneLayout
    ├── SceneLayer
    └── CoordinateReference
```

关系说明：

- `SceneTemplate`：场景模板
- `SceneInstance`：场景运行 / 编辑实例，引用 `SceneTemplate`
- `SceneLayout`：保存场景布局结果
- `SceneLayer`：场景中的图层组织
- `CoordinateReference`：坐标参考系

### 12.2 模板层

```text
ActorTemplate
├── ActorTemplateParam
├── ActorTemplateComponentSlot
└── ActorTemplateAssetBinding
```

关系说明：

- `ActorTemplate`：可实例化原型，定义 Actor 的默认结构与默认能力
- `ActorTemplateParam`：模板参数
- `ActorTemplateComponentSlot`：模板组件槽，定义模板默认有哪些组件能力，以及每个组件是否启用、是否必选、是否可覆盖
- `ActorTemplateAssetBinding`：模板资产绑定，定义模板默认绑定的模型、贴图、材质、配置等资源

### 12.3 实例层

```text
ActorInstance
├── ActorInstanceTransformComponent
├── ActorInstanceGeoComponent
├── ActorInstanceRenderComponent
├── ActorInstanceInteractionComponent
├── ActorInstanceStateComponent
└── ActorInstanceBindingComponent
```

关系说明：

- `ActorInstance`：场景中的具体对象，表示模板实例化后在某个场景中的实体壳
- 所有“能力”都下沉到组件
- 主表只保留身份、关系、状态、图层等轻量字段

### 12.4 组件层

#### `ActorInstanceTransformComponent`
三维变换基础组件。

字段：

- `localX`
- `localY`
- `localZ`
- `rotationX`
- `rotationY`
- `rotationZ`
- `scaleX`
- `scaleY`
- `scaleZ`

职责：

- 三维位置
- 旋转
- 缩放
- 层级变换

#### `ActorInstanceGeoComponent`
GIS 扩展组件。

字段：

- `geoLng`
- `geoLat`
- `geoHeight`
- 可选：`crsCode`、`referenceFrame`

职责：

- 地理定位
- GIS 坐标语义
- 地图 / 地球 / 坐标系联动

#### `ActorInstanceRenderComponent`
渲染组件。

字段示例：

- `modelUrl`
- `materialJson`
- `textureJson`
- `renderMode`
- `shadowEnabled`
- `lodLevel`

职责：

- 模型
- 材质
- 纹理
- 渲染配置

#### `ActorInstanceInteractionComponent`
交互能力声明组件。

字段示例：

- `clickable`
- `draggable`
- `rotatable`
- `scalable`
- `hoverable`
- `selectable`
- `editable`

职责：

- 声明此 Actor 是否支持某种交互
- 前端 Three.js 根据声明决定是否启用交互逻辑

#### `ActorInstanceStateComponent`
运行态与业务态组件。

字段示例：

- `stateCode`
- `status`
- `alarmFlag`
- `onlineFlag`
- `healthLevel`

职责：

- 运行状态
- 业务状态
- 设备状态映射

#### `ActorInstanceBindingComponent`
业务绑定组件。

绑定对象可能包括：

- 设施主数据
- 资产
- 外部系统 ID
- 业务编码

职责：

- 表达 Actor 与外部业务对象的绑定关系

---

## 13. 最终命名清单

### 13.1 场景层保留

- `SceneTemplate`
- `SceneInstance`
- `SceneLayout`
- `SceneLayer`
- `CoordinateReference`
- `SceneSnapshot`

### 13.2 模板层保留

- `ActorTemplate`
- `ActorTemplateParam`
- `ActorTemplateComponentSlot`
- `ActorTemplateAssetBinding`

### 13.3 实例层保留

- `ActorInstance`
- `ActorInstanceTransformComponent`
- `ActorInstanceGeoComponent`
- `ActorInstanceRenderComponent`
- `ActorInstanceInteractionComponent`
- `ActorInstanceStateComponent`
- `ActorInstanceBindingComponent`

### 13.4 资产层保留

- `AssetResource`

### 13.5 业务绑定层保留

- `FacilityActorInstanceBinding`

---

## 14. 必须删除或替换的旧命名

### 14.1 旧场景 / 空间模型

- `SceneTemplate*` -> 保持为 `SceneTemplate*`
- `SpatialEntityLayout*` -> 替换为 `ActorInstancePlacement*` 或 `ActorInstanceTransformComponent*`
- `SceneAssetBinding*` -> 拆分为 `ActorTemplateAssetBinding*` / `ActorInstanceAssetBinding*` / `FacilityActorInstanceBinding*`

### 14.2 旧 Actor 命名残留

- `SceneActorDefinition*`
- `SceneActorInstance*`
- `SceneActor*` 中已被新命名覆盖的类

### 14.3 旧目录残留

- `controller/admin/definition/`
- `controller/admin/spatial/` 中承载旧 `SpatialEntityLayout*` 语义的内容
- 任何与新分层不一致的 `scene actor` / `definition` / `spatial` 旧目录

---

## 15. 设计结论

- `Scene` 管上下文
- `ActorTemplate` 管默认结构
- `ActorInstance` 管实例壳
- `Component` 管能力
- `Asset` 管资源
- `Binding` 管业务关系

---

## 16. 当前后端现状

### 已有基础

- 站场管理：已有
- 场景平台：已有部分底座
- 设施工作台契约：已有骨架

### 仍需补齐

- Actor 模板库
- Actor 实例生成
- 实例编辑工作流
- Actor Component 分层
- 设施与场景实例绑定
- 统一的跨模块 API 契约

---

## 17. 执行建议

建议后续开发按以下顺序推进：

1. 冻结旧设计残留
2. 固化场景 / 模板 / 实例 / 组件 / 资产 / 绑定分层
3. 补齐 API 契约
4. 再实现 controller
5. 再实现 service
6. 最后补数据库表与持久化

---

## 18. 备注

本说明用于后续编译、核对和分工时作为统一依据。若后续发现职责冲突，应优先调整模块归属，而不是临时堆代码。

---

## 19. UE 引擎组件 vs 能力组件（两体系并行设计）

> **关键设计决策：本系统存在两套独立的"组件"概念，必须严格区分，不能混淆。**

### 19.1 两套体系总览

| 维度 | UE 引擎组件 | 能力组件（能力声明） |
|------|-----------|-------------------|
| UE 对应 | USceneComponent 及其派生类 | 自定义 Component 子类 |
| 作用 | 描述 3D 空间结构（渲染、碰撞、层级） | 描述业务能力和交互规则 |
| 归属 | Actor 蓝图内部，作为组件树挂载 | Actor 实例上按模板启用 |
| 存储方式 | ActorDO.componentTree（JSON 对象） | 独立组件表（如 ActorInstanceRenderComponent） |
| 管理范围 | 模板级别定义 | 模板级定义 + 实例级覆盖 |

### 19.2 UE 引擎组件树

在 UE 中，**每个 Actor 内部都持有一个 SceneComponent 树**，这是 Actor 的内置属性，不是外部挂载的。

```
UE: BP_Pump (Actor 蓝图)
  └── RootSceneComponent (USceneComponent，所有 Actor 必有)
       ├── StaticMeshComponent (网格体，可见的 3D 模型)
       ├── BoxComponent (碰撞盒)
       ├── LightComponent (光源)
       └── CameraComponent (相机，可选)

后端: ActorDO
  └── componentTree (ComponentTree 对象，JSON 格式)
       └── 组件树节点
            ├── StaticMeshComponent 节点
            ├── BoxComponent 节点
            └── ...
```

**重要：** 这个 SceneComponent 树定义在 Actor 蓝图（模板）中。蓝图实例化后，每个实例继承相同的组件树结构，但每个实例有独立的 Transform 值。

### 19.3 ActorDO.componentTree 的存储方式

```java
// ActorDO.java
@TableName("actor")
public class ActorDO extends BaseDO {
    // 组件树根节点，对应 UE 的 SceneComponent 树
    // 数据库中以 JSON 格式存储，通过 ComponentTreeTypeHandler 自动转换
    @TableField(typeHandler = ComponentTreeTypeHandler.class)
    private ComponentTree componentTree;
}
```

`ComponentTree` 对象是一个 JSON 结构，包含完整的组件树层次：

```json
{
  "root": {
    "componentType": "SceneComponent",
    "name": "Root",
    "transform": { "location": [0,0,0], "rotation": [0,0,0], "scale": [1,1,1] },
    "children": [
      {
        "componentType": "StaticMeshComponent",
        "name": "BodyMesh",
        "meshAsset": "/Game/Models/Pump.Body",
        "transform": { "location": [0,0,50], "rotation": [0,0,0], "scale": [1,1,1] },
        "children": [
          {
            "componentType": "BoxComponent",
            "name": "CollisionBox",
            "boxExtent": [50,50,100],
            "transform": { "location": [0,0,50], "rotation": [0,0,0], "scale": [1,1,1] }
          }
        ]
      }
    ]
  }
}
```

### 19.4 SceneComponentDO（场景级全局组件）

`SceneComponentDO` 不是 Actor 的组件，而是 **挂载到 Scene 级别的全局组件**，独立于 Actor：

- **POST_PROCESS_VOLUME** — 后处理体积（雾效、色调映射、色彩校正）
- **WEATHER_SYSTEM** — 全局天气系统（晴天、雨天、雪天）
- **TRIGGER_VOLUME** — 场景边界/触发器（碰撞区域、触发事件）

这些组件在 UE 中是 Scene 中独立放置的 Actor（如 PostProcessVolume 蓝图实例），不属于任何业务 Actor 的内部组件树。

```
Scene 级别
├── Actor_1 (泵)
│   └── 内部组件树：StaticMeshComponent, BoxComponent, ...
├── Actor_2 (阀门)
│   └── 内部组件树：StaticMeshComponent, ...
├── PostProcessVolume（SceneComponentDO: POST_PROCESS_VOLUME）
├── WeatherSystem（SceneComponentDO: WEATHER_SYSTEM）
└── TriggerVolume（SceneComponentDO: TRIGGER_VOLUME）
```

### 19.5 引擎组件 DO 的设计定位

项目 `component/` 目录下有 16 个引擎组件 DO：

```
AudioComponentDO
BoxComponentDO
CameraComponentDO
CapsuleComponentDO
ChildActorComponentDO
ComponentDO
LightComponentDO
MeshComponentDO
PrimitiveComponentDO
SceneComponentDO          ← 场景级全局组件（见 19.4）
SceneComponentInstanceDO
SkeletalMeshComponentDO
SphereComponentDO
SplineComponentDO
SplineMeshComponentDO
StaticMeshComponentDO
TimelineComponentDO
WidgetComponentDO
```

**这些 DO 的定位：**

1. **不应作为独立数据库表管理** — 它们是 Actor 蓝图内部组件树的节点类型定义
2. **作为 ComponentTree 节点类型的枚举/模板** — 提供组件类型的配置 schema
3. **当 Actor 从 UE 导出时，这些 DO 帮助解析 JSON 中的组件节点**
4. **可选：作为编辑器端的组件配置面板的元数据参考**

**为什么不需要独立表？**
- 引擎组件树是 Actor 的**内部结构**，每个 Actor 有自己的实例树
- 与能力组件（Transform、Render 等）不同，引擎组件不跨 Actor 复用
- 存储为 JSON 嵌入 ActorDO 即可，查询粒度到 Actor 级别，不需要对单个引擎组件做跨 Actor 查询

### 19.6 两套体系的关系图

```
ActorDO（蓝图/模板）
├── actorClass = "BP_Pump"
│
├── componentTree (UE 引擎组件树)          ← 描述"这个 Actor 长什么样"
│   └── StaticMeshComponent                ← 模型
│   ├── BoxComponent                       ← 碰撞
│   └── LightComponent                     ← 光源
│
└── metadataJson
     └── 能力组件引用列表                    ← 描述"这个 Actor 能做什么"
          ├── ActorInstanceTransformComponent
          ├── ActorInstanceRenderComponent
          ├── ActorInstanceInteractionComponent
          └── ...

ActorInstanceDO（实例）
├── parentInstanceCode → 父实例
├── 继承 componentTree 结构                ← 从模板复制引擎组件树
└── 能力组件实例（覆盖模板配置）             ← 实例级能力覆盖
```

### 19.7 关键设计结论

| 决策 | 说明 |
|------|------|
| 引擎组件不独立建表 | 嵌入 ActorDO.componentTree JSON，按 Actor 粒度管理 |
| SceneComponentDO 独立表 | 管理场景级全局组件（雾效/天气/触发器），独立于 Actor |
| 能力组件独立表 | 每个能力组件类型对应独立表（Transform、Render 等） |
| 两套体系互不隶属 | 引擎组件描述 3D 空间结构，能力组件描述业务行为，通过 metadataJson 关联 |
| 引擎组件 DO = 元数据 | component/ 目录下的 16 个 DO 是组件类型元数据，不是实例数据 |

### 19.8 实施建议

1. 冻结 SceneComponentDO 作为场景级全局组件的定义
2. 引擎组件 DO（StaticMeshComponentDO 等）作为类型元数据保留，不创建数据库表
3. ActorDO.componentTree 完整存储引擎组件树 JSON
4. 能力组件通过独立表和模板槽管理
5. Actor 模板导入功能从 UE 导出 JSON 解析时，正确区分引擎组件和能力组件
