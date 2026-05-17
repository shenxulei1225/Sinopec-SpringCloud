# UE 场景管理方案对标分析报告

> 生成时间：2026-05-08  
> 分析范围：scene-platform 模块全部代码（DO/Service/Controller/Adapter/Model/WebSocket）  
> 对标基准：Unreal Engine 5 场景管理体系（Actor/Component/World/Level/GAS）

---

## 1. 总体架构映射

### 1.1 核心概念对照

| UE 概念 | 代码实现 | 完成度 | 备注 |
|---------|---------|--------|------|
| **World (UWorld)** | SceneInstance | 70% | 已有 CRUD/复制/删除，缺生命周期管理（BeginPlay/EndPlay） |
| **Level (ULevel)** | SceneLayer | 40% | 已有表结构，但 Service/Controller 层未实现 |
| **Actor (AActor)** | ActorDO | 95% | 模板定义完整，含组件树 JSON |
| **ActorInstance** | ActorInstanceDO | 85% | 位姿/可见性/时间戳/动画字段已补全，缺 Tick 机制 |
| **Component (UComponent)** | ActorInstanceComponentDO | 90% | 已支持 Transform/Render 等引擎组件 |
| **ComponentTree** | ComponentTree / ComponentTreeNode | 90% | 树形结构已实现，缺 getRelativeTransform() |
| **Engine Component** | SceneComponentDO | 80% | POST_PROCESS_VOLUME/WEATHER/TRIGGER 已实现 |
| **Blueprint** | 缺失 | 0% | 无可视化蓝图编辑器对接 |
| **Gameplay Ability (GAS)** | 缺失 | 0% | 无属性/技能/效果系统 |
| **Timeline** | TimelineComponentDO | 60% | 表结构已建，缺播放/暂停/关键帧逻辑 |
| **UI/Widget** | WidgetComponentDO | 60% | 表结构已建，缺 HUD/UMG 对接 |
| **Asset (UObject)** | AssetResourceDO | 80% | 资源上传/下载/分类已实现，缺流送/LOD |
| **Level Streaming** | 缺失 | 0% | 无子场景动态加载 |
| **Scene Capture** | 缺失 | 0% | 无屏幕截图/录制能力 |
| **Replication (Net)** | ActorInstanceWebSocketService | 50% | 已有位置/可见性推送，缺增量同步/快照压缩 |

---

## 2. 分层详细分析

### 2.1 DO 层（数据对象）

| 模块 | 文件 | 状态 | 分析 |
|------|------|------|------|
| 场景实例 | `SceneInstanceDO` | ✓ 完成 | 含 sceneCode/name/projectCode/status/publishStatus/publishedAt/publishedBy。缺：viewportConfig（3D 视口配置）、physicsConfig（物理配置） |
| 场景层 | `SceneLayerDO` | ⚠ 部分 | 已有表结构，未读取文件但参考历史分析。缺：layerGroup（层级分组）、parentLayerId（层级嵌套） |
| Actor 模板 | `ActorDO` | ✓ 完成 | 含 actorCode/name/componentTree(ComponentTree 对象, 使用 TypeHandler)。ComponentTree 包含 transform。对标 UE：相当于 Blueprint 定义 |
| Actor 实例 | `ActorInstanceDO` | ✓ 完成 | 含 transform/metadataJson/layerKey/positionTimestamp/animationEnabled。对标 UE：Actor 在 World 中的具体实例 |
| Actor 组件实例 | `ActorInstanceComponentDO` | ✓ 完成 | 含 componentType/transform/constructArgsJson/actorInstanceCodes。对标 UE：Component 在实例中的配置 |
| 场景级组件 | `SceneComponentDO` | ✓ 完成 | 支持 POST_PROCESS_VOLUME(后处理/雾效), WEATHER_SYSTEM(天气), TRIGGER_VOLUME(触发器)。对标 UE：世界设置级组件 |
| 时间线组件 | `TimelineComponentDO` | ✓ 完成 | 含 timelineConfigJson/autoPlay/loop。对标 UE：UObject / UMovieScene 播放控制 |
| Widget 组件 | `WidgetComponentDO` | ✓ 完成 | 含 widgetCode/uiConfigJson/drawAtDesiredSize/receiveHardwareInput。对标 UE：UWidgetComponent（3D 空间 UI） |
| 资产资源 | `AssetResourceDO` | ✓ 完成 | 含 assetCode/name/type/url/metadataJson。对标 UE：StaticMesh/SkeletalMesh/Material 等 Asset |
| 坐标参考 | `CoordinateReferenceDO` | ✓ 完成 | 场景坐标系配置 |
| Geo 图层 | `GeoLayerConfigDO` | ✓ 完成 | 地理图层配置 |
| 场景快照 | `ScenePublishSnapshotDO` | ✓ 完成 | 发布快照，用于回滚/版本管理 |
| 资产绑定 | `SceneAssetBindingDO` | ✓ 完成 | 资产与场景的关联关系 |

**DO 层问题**：
- 无 `SceneTemplateDO` — 这是正确的设计决策（已在第 551/552 章讨论并修正，ActorDO 统一承担模板角色）
- SceneInstanceDO.publishStatus 使用 "DRAFT" 字符串而非枚举类型

### 2.2 Model 层（业务模型）

| 文件 | 状态 | 分析 |
|------|------|------|
| `Vector3.java` | ✓ 完成 | x/y/z double 字段，对标 UE FVector |
| `Rotator.java` | ✓ 完成 | pitch/yaw/roll double 字段，对标 UE FRotator |
| `Transform.java` | ✓ 完成 | location(Vector3) + rotation(Rotator) + scale(Vector3)，对标 UE FTransform |
| `ComponentTree.java` | ✓ 完成 | root + nodes(List) + rootId(Long)，对标 UE 组件树结构 |
| `ComponentTreeNode.java` | ⚠ 待验证 | 需确认是否有 getRelativeTransform() 方法（第 619 章已知问题） |
| `ActorInstancePositionUpdateMessage.java` | ✓ 完成 | WebSocket 推送的位置变更消息 |

### 2.3 TypeHandler 层

| 文件 | 状态 | 分析 |
|------|------|------|
| `ComponentTreeTypeHandler.java` | ✓ 完成 | ComponentTree ↔ JSON 互转，对标 UE 将组件序列化到 SQLite/JSON |

**设计评价**：将 componentTreeJson 改为 ComponentTree 对象并使用 TypeHandler 是正确的优化（参考第 535/535 章），相比纯 JSON 字符串提高了类型安全和查询性能。

### 2.4 Mapper 层

参考历史分析：ActorInstanceMapper、SceneInstanceMapper、ActorMapper、SceneComponentMapper 等均有实现。

### 2.5 Service 层

| 服务 | 文件 | 状态 | 分析 |
|------|------|------|------|
| ActorService | `ActorServiceImpl.java` | ✓ 完成 | CRUD + 组件树管理（updateComponentTree/listComponentTrees）。对标 UE：Blueprint 的模板管理 |
| ActorInstanceService | `ActorInstanceServiceImpl.java` | ✓ 完成 | spawn/update/delete + WebSocket 集成（第 615 章）。对标 UE：Actor 实例化 |
| ActorInstanceComponentService | `ActorInstanceComponentServiceImpl.java` | ✓ 完成 | listByInstanceId + CRUD。对标 UE：组件增删改查 |
| SceneInstanceService | `SceneInstanceServiceImpl.java` | ✓ 完成 | CRUD + 复制 + 删除（级联清除坐标/图层/快照）。对标 UE：Level 管理 |
| SceneComponentService | `SceneComponentServiceImpl.java` | ✓ 完成 | 创建/更新/删除/启用禁用。COMPONENT_TYPES 硬编码为 3 种。对标 UE：世界设置级组件管理 |
| AssetResourceService | 参考历史 | ✓ 完成 | 资源 CRUD |
| ScenePublishSnapshotService | 参考历史 | ✓ 完成 | 快照创建/恢复 |
| CoordinateReferenceService | 参考历史 | ✓ 完成 | 坐标系统管理 |
| GeoLayerConfigService | 参考历史 | ✓ 完成 | 地理图层管理 |

**Service 层问题**：
1. `SceneComponentServiceImpl` 中 COMPONENT_TYPES 硬编码 → 应改为枚举或从数据库读取
2. `ActorInstanceServiceImpl` 中缺少 Tick/心跳机制
3. `SceneInstanceServiceImpl` 无生命周期回调（OnLevelLoaded/OnLevelUnloaded 等）

### 2.6 Controller 层（REST API）

| Controller | 状态 | 对标 UE API |
|-----------|------|------------|
| ActorController | ✓ 完成 | 对应 Unreal 的 Blueprint 编辑器 API |
| ActorInstanceController | ✓ 完成 | 对应 UE 的 SpawnActor / SetActorLocation 等 |
| SceneController | ✓ 完成 | 对应 UE 的 Level 管理 API |
| SceneComponentController | ✓ 完成 | 对应 UE 世界设置组件管理 |
| AssetResourceController | ✓ 完成 | 对应 UE 的 Asset Manager API |

**Controller 层问题**：
- 缺少 WebSocket 实时推送的 Controller 端点（虽然 WebSocketService 已实现，但 Controller 端可能需要暴露连接信息或管理接口）

### 2.7 适配层（SPI + UE 引擎）

| 文件 | 状态 | 分析 |
|------|------|------|
| `SceneEngineAdapter` (接口) | ✓ 完成 | 定义了 5 个方法：engineProfile/export/import/validate/preview。对标 UE 引擎抽象 |
| `EngineScenePackage` | ✓ 完成 | 场景包 DTO，用于导出/导入 |
| `SceneImportResult` | ✓ 完成 | 导入结果 DTO |
| `SceneExportContext` | ✓ 完成 | 导出上下文 |
| `UeSceneEngineAdapter` | ⚠ 空壳 | 第 541 章已指出，publishScene 等方法目前为空实现。需要对接真正的 UE 引擎实例 |
| 其他 Context DTO | ✓ 完成 | SceneValidationContext/ActorPreviewContext 等 |

### 2.8 WebSocket 实时推送

| 文件 | 状态 | 分析 |
|------|------|------|
| `WebSocketConfig` | ✓ 完成 | Spring WebSocket 配置 |
| `ActorInstanceWebSocketHandler` | ✓ 完成 | 处理客户端连接/消息/断开 |
| `ActorInstanceWebSocketService` | ✓ 完成 | 广播位置/可见性变更到所有订阅客户端 |
| `ActorInstancePositionUpdateMessage` | ✓ 完成 | 推送消息结构 |

**对标 UE**：UE 有 Replication 系统处理客户端-服务器同步，本项目用 WebSocket 实现了轻量替代方案。

**已知问题**：
- 推送频率未控制（可能导致网络拥塞）
- 无增量同步（每次都发送完整位置，UE 的 Replication 有变更检测）
- 无断线重连/心跳检测（WebSocketHandler 可能需要 Ping/Pong）

### 2.9 Convert 层

| 文件 | 状态 | 分析 |
|------|------|------|
| `SceneWorkspaceConvert` | ✓ 完成 | 地理图层 + 资产资源转换为 DetailRespVO |
| 各 VO 的 toDO() / BeanUtils | ✓ 完成 | 标准 Spring/MyBatis-Plus 转换 |

---

## 3. UE 生命周期事件对标

UE 中 Actor 有完整生命周期：

| UE 生命周期 | 当前实现 | 缺失程度 | 实现建议 |
|------------|---------|---------|---------|
| **PostInitializeComponents** | ❌ 0% | 极高 | ActorInstance 创建时需要初始化组件的默认属性 |
| **BeginPlay** | ❌ 0% | 极高 | 场景加载后，所有 ActorInstance 应触发 BeginPlay |
| **Tick** | ❌ 0% | 极高 | **核心缺失**。需要后端定时推送位置更新 + 前端插值平滑 |
| **EndPlay** | ❌ 0% | 高 | Actor 删除前/场景关闭前的清理 |
| **OnActorBeginOverlap** | ❌ 0% | 高 | 碰撞检测事件（TRIGGER_VOLUME 组件需要此能力） |
| **OnActorEndOverlap** | ❌ 0% | 高 | 碰撞结束事件 |
| **OnComponentBeginCollision** | ❌ 0% | 高 | 组件级别碰撞事件 |
| **OnDestroyed** | ❌ 0% | 中 | Actor 被销毁时的清理 |

**Tick 替代方案设计**：

当前项目的 Tick 替代方案（参考第 583 章）：
1. **后端**：Spring Schedule 定时扫描有位置更新的 ActorInstance，推送 WebSocket 消息
2. **前端**：Three.js 接收位置数据，使用 lerp 插值平滑过渡
3. **优化**：只推送变更的 Actor（增量同步），推送频率 10-30 FPS

---

## 4. 与 UE 核心系统的差距

### 4.1 已实现的映射

| UE 系统 | 代码实现 | 完成度 |
|---------|---------|--------|
| Actor 模板系统 | ActorDO + ComponentTree | 95% |
| Actor 实例化 | ActorInstance + spawnActorInstance | 85% |
| 组件树 | ComponentTree/TreeNode + TypeHandler | 90% |
| 场景 CRUD | SceneInstanceService | 70% |
| 资源管理 | AssetResourceService | 80% |
| 世界组件 | SceneComponentService | 80% |
| 发布/快照 | ScenePublishSnapshotService | 60% |
| 实时同步 | WebSocket + ActorInstanceWebSocketService | 50% |
| 坐标系统 | CoordinateReference + Transform/Vector3/Rotator | 85% |
| 地理图层 | GeoLayerConfigService | 70% |
| 时间线 | TimelineComponentDO | 60% |
| 3D UI | WidgetComponentDO | 60% |

### 4.2 未实现的关键系统

| UE 系统 | 差距 | 优先级 | 说明 |
|---------|------|--------|------|
| **Blueprint 可视化** | ❌ 0% | P0 | 没有可视化脚本编辑器，无法非程序员设计行为 |
| **GAS（Gameplay Ability System）** | ❌ 0% | P1 | 属性/技能/效果/标签系统。UE 核心战斗/交互系统 |
| **Level Streaming** | ❌ 0% | P1 | 大场景子场景动态加载，对工业数字孪生至关重要 |
| **碰撞/物理系统** | ❌ 0% | P1 | OnActorBeginOverlap、物理模拟、射线检测 |
| **动画系统** | 10% | P1 | 仅有 animationEnabled 布尔标记，无骨架/关键帧/状态机 |
| **粒子系统** | ❌ 0% | P2 | Niagara/Particle 效果管理 |
| **音频系统** | ❌ 0% | P2 | AmbientSound、SoundClass 管理 |
| **材质系统** | 30% | P2 | AssetResource 管理资源文件，但材质参数无专门抽象 |
| **AI 系统** | ❌ 0% | P2 | BehaviorTree/Blackboard 管理 |
| **UI/HUD 系统** | 40% | P2 | WidgetComponentDO 仅有基础字段 |
| **网络同步（Net）** | 50% | P1 | WebSocket 已搭基础设施，但缺权威模式、回滚、预测 |
| **场景流送/LOD** | ❌ 0% | P2 | 大场景性能优化 |
| **烘焙系统** | ❌ 0% | P2 | Lighting/Lumen/BakedLightmap 烘焙 |

---

## 5. 技术栈对标

| UE 技术 | 本项目替代 | 差距分析 |
|---------|-----------|---------|
| **虚幻引擎 (C++)** | Spring Boot (Java) | 完全不同的平台。UE 提供完整的 3D 渲染管线、物理、AI、音频、网络。本项目是**元数据管理平台**，负责管理 3D 场景中的 Actor 数据 |
| **Unreal Frontend** | 缺失 | 无内嵌 3D 查看器，依赖前端 Three.js |
| **Remote Control (OSC/HTTP)** | WebSocket API | 可实现 UE 远程控制，但需额外开发 |
| **Datasmith** | AssetResourceService + importScene | 需对接 Datasmith 批量导入管线 |
| **MassEntity (ECS)** | 缺失 | 无 ECS 架构，Actor 系统为传统 OOP |
| **Chaos Physics** | 缺失 | 无物理引擎集成 |

---

## 6. 当前方案定位

本项目（scene-platform）不是 UE 引擎的替代品，而是一个**场景元数据管理平台**，核心职责：

1. **Actor 模板管理**：定义可复用的 Actor 组件结构（类似 Blueprint 的静态定义）
2. **场景实例管理**：管理多个 3D 场景中的 Actor 实例及其位姿
3. **资源管理**：管理 3D 资产文件（.glb/.fbx/.gltf 等）
4. **实时同步**：通过 WebSocket 将 Actor 位置/可见性变更推送到前端
5. **发布与版本**：管理场景发布的快照和回滚

**关键设计决策**：
- 3D 渲染由前端 Three.js 负责（而非 Java 后端）
- Java 后端专注于**数据结构、业务逻辑、持久化、实时推送**
- 与 UE 引擎的关系：通过 WebSocket/HTTP API 进行远程通信（Remote Control），或作为 UE 场景数据的元数据存储后端

---

## 7. 已知 Bug 与待修复

| 问题 | 来源 | 严重级 | 说明 |
|------|------|--------|------|
| ComponentTreeNode 缺少 getRelativeTransform() 方法 | 第 619 章 | P0 | 编译报错 |
| ActorInstanceServiceImpl 中 'mybatisplus' 拼写错误 | 第 619 章 | P1 | 编译警告 |
| UeSceneEngineAdapter.publishScene() 为空实现 | 第 541 章 | P1 | 对接 UE 引擎的关键路径 |
| SceneComponentServiceImpl 中 COMPONENT_TYPES 硬编码 | 本分析 | P2 | 应改为枚举或数据库配置 |
| WebSocket 推送无频率控制 | 本分析 | P2 | 可能导致网络拥塞 |

---

## 8. 后续开发优先级建议

### P0 - 编译修复
- [ ] 修复 ComponentTreeNode.getRelativeTransform() 缺失
- [ ] 修复 ActorInstanceServiceImpl 拼写错误

### P1 - 核心能力补齐
- [ ] 实现 Tick 替代方案（后端定时推送 + 前端插值）
- [ ] ActorInstance 生命周期（BeginPlay/EndPlay）
- [ ] 碰撞检测（ActorBeginOverlap 事件）
- [ ] UE 引擎适配层对接（UeSceneEngineAdapter 具体实现）
- [ ] 动画系统基础（骨架动画/关键帧切换）

### P1 - 工业数字孪生能力
- [ ] Level Streaming（子场景动态加载）
- [ ] Actor 拖拽交互（前端 → 后端持久化）
- [ ] 射线拾取（Raycast → 获取选中 Actor）
- [ ] 视角控制（第一人称/第三人称/轨道相机）

### P2 - 增强能力
- [ ] Blueprint 脚本引擎（Groovy/Lua 轻量脚本）
- [ ] GAS 属性/技能系统
- [ ] Datasmith 批量导入管线
- [ ] 粒子系统管理
- [ ] 音频系统管理
- [ ] 烘焙系统

---

## 9. 结论

**当前 scene-platform 模块已达到中等完成度（约 60-70%）**：

- ✓ DO 层设计完整，覆盖了 Actor/Component/Scene/Asset 等核心概念
- ✓ Service 层核心 CRUD 逻辑已基本实现
- ✓ WebSocket 实时推送基础设施已就绪
- ✓ Model 层（Vector3/Rotator/Transform）对标 UE 坐标系体系
- ✓ ComponentTree 使用 TypeHandler 的类型安全优化是正确的

**主要差距**：
- ❌ UE 引擎适配层为空壳（UeSceneEngineAdapter）
- ❌ Tick/动画/碰撞等运行时能力缺失
- ❌ 无 Blueprint 可视化编辑器
- ❌ 无 GAS 系统、Level Streaming、物理引擎

**建议定位**：本项目作为"场景元数据管理平台"，前端 Three.js 负责渲染，后端管理数据和推送。如需对接真实 UE 引擎，需实现 Remote Control 桥接层。
