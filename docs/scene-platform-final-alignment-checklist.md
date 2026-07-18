# Scene Platform 最终对齐清单

## 1. 最终目标

按照最终讨论后的方案，三维与 GIS 平台能力必须满足以下原则：

- 独立于 `facility` 业务域实现
- 以 `scene-platform` 作为统一平台服务
- 以模板化、实例化、聚合 descriptor 为核心
- 以后端 `scene-platform` 作为场景真源
- 前端区分编辑态与运行态
- 前端运行时支持 `Three / Cesium / Hybrid` 演进

---

## 2. 服务边界

### 2.1 facility 的职责

`facility` 只保留业务主数据：

- 设施基础信息
- 设施树/站场树/业务层级
- 设备、区域、管线等业务实体主档
- 业务状态、告警、巡检、统计等领域数据

### 2.2 scene-platform 的职责

`scene-platform` 负责三维/GIS平台能力：

- 场景模板管理
- 场景实例管理
- 坐标参考与空间参考管理
- 空间布局管理
- 模型/瓦片/地图资源管理
- GIS 图层与场景能力配置
- 工作台聚合 descriptor 输出
- 编辑草稿与发布态管理

---

## 3. 子域拆分

`scene-platform` 内部按 5 个子域组织：

### 3.1 scene

- `scene_template`
- `scene_instance`
- 场景类型
- 引擎模式
- 能力配置
- 默认视角
- 图层配置

### 3.2 spatial

- `spatial_entity_layout`
- 实体位姿
- `local / geo` 双坐标
- `rotation / scale`
- 版本号
- 草稿/发布演进

### 3.3 geo

- `coordinate_reference`
- CRS / 原点 / 轴模式 / 单位
- GIS 图层配置
- imagery / terrain / tileset 引用
- 后续与 PostGIS/GeoServer 对接

### 3.4 asset

- 模型资源
- tileset 资源
- 地图资源
- 资源元数据
- 资源与模板/实体绑定

### 3.5 workspace

- 面向前端工作台的聚合接口
- `SceneWorkspaceDescriptor`
- 聚合 scene/spatial/geo/asset 四域数据

---

## 4. 当前已符合的内容

### 4.1 独立服务化

已经新增：

- `cheers-scene-3d`
- `cheers-scene-3d-api`
- `cheers-scene-3d-server`

### 4.2 根工程接入

已经加入：

- 根 `pom.xml`
- `start-microservices.sh`

### 4.3 最小核心表雏形

已经具备以下核心雏形：

- `scene_template`
- `scene_instance`
- `coordinate_reference`
- `spatial_entity_layout`

这部分可保留，作为最终方案的基础骨架。

---

## 5. 需要停止扩展的内容

### 5.1 facility 中的三维/GIS扩展

之前在 `facility-management` 中加入的 scene/spatial 相关设计，不再作为主线继续扩展。

处理原则：

- 不再新增 `facility` 内的 3D/GIS 平台表
- 不再在 `facility` 中新增场景平台接口
- 后续统一迁移/沉淀到 `scene-platform`

说明：

- 现有内容可以视为过渡验证
- 最终实现以 `scene-platform` 为准

---

## 6. 需要补齐的表

### 6.1 scene 子域

当前保留：

- `scene_template`
- `scene_instance`

建议补齐：

- `scene_layer_config` 或在模板/实例中保留标准化图层配置字段
- `scene_publish_snapshot`（后续）

### 6.2 geo 子域

当前保留：

- `coordinate_reference`

建议新增：

- `geo_layer_resource`
- `geo_scene_binding`

### 6.3 asset 子域

建议新增：

- `asset_resource`
- `scene_asset_binding`
- `entity_asset_binding`（可选）

### 6.4 workspace 子域

工作台聚合层通常不一定单独建表，但需要统一契约和服务层聚合。

---

## 7. 需要优先补齐的接口

### 7.1 工作台聚合接口

优先级最高：

- `GET /scene-platform/scenes/{sceneCode}/descriptor`

返回：

- scene 基本信息
- template 信息
- coordinateReference
- layer config
- capability config
- entity layouts
- 关联资源引用

### 7.2 空间布局接口

- `GET /scene-platform/scenes/{sceneCode}/entities`
- `POST /scene-platform/scenes/{sceneCode}/entities/layout:batchSave`

### 7.3 模板与实例接口

- `GET /scene-platform/templates`
- `POST /scene-platform/templates`
- `GET /scene-platform/scenes/{sceneCode}`
- `POST /scene-platform/scenes`

---

## 8. 前端对接原则

### 8.1 后端是真源

前端不再把场景定义为本地唯一数据源，统一以 `scene-platform` 返回的 descriptor 为准。

### 8.2 编辑态与运行态分离

编辑态：

- 拖拽
- 选中
- 改属性
- 保存草稿
- 发布

运行态：

- 读取发布配置
- 渲染场景
- 绑定业务数据
- 响应交互

### 8.3 Three / Cesium / Hybrid 边界

- `Three`：精细编辑与局部三维
- `Cesium`：GIS 地理空间底座
- `Hybrid`：Cesium 宏观 + Three 精细

---

## 9. 代码结构调整建议

建议 `scene-platform-server` 的 Java 包结构逐步调整为：

```text
cn.iocoder.yudao.module.scene.platform
  controller
    admin
      scene
      spatial
      geo
      asset
      workspace
  service
    scene
    spatial
    geo
    asset
    workspace
  convert
    scene
    spatial
    geo
    asset
    workspace
  dal
    dataobject
      scene
      spatial
      geo
      asset
    mysql
      scene
      spatial
      geo
      asset
```

说明：

- 当前已存在的 DO/Mapper 可逐步迁移到对应子包
- 不要求一步到位拆成独立微服务，但子域结构必须明确

---

## 10. 本阶段实施顺序

### Phase 1：结构收敛

- 明确 scene/spatial/geo/asset/workspace 五域目录
- 保留已有核心 DO/Mapper
- 停止 `facility` 继续承载 3D/GIS

### Phase 2：最小接口联调

- `descriptor`
- `entities`
- `layout:batchSave`

### Phase 3：资产与 GIS 补齐

- 资源表
- 图层表/资源绑定
- GIS 图层配置

### Phase 4：模板与发布机制补齐

- 模板能力模型
- 草稿/发布/快照

---

## 11. 当前决策结论

最终以 `scene-platform` 为三维/GIS 平台服务主线，后续所有相关实现都按本清单收敛执行。
