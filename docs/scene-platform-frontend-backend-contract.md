# Scene Platform 前后端对接契约文档

## 1. 目标

本文档用于定义 `scene-platform` 与前端 Three / Cesium / Hybrid 运行时、编辑器的最小对接契约。

目标：

- 后端作为场景真源
- 前端区分编辑态与运行态
- 统一 descriptor 契约
- 支持 `Three / Cesium / Hybrid` 演进

---

## 2. 推荐前端接入顺序

### 2.1 运行态

前端运行态建议调用顺序：

1. `GET /scene-platform/scenes/{sceneCode}/descriptor`
2. 根据 `engineProfile` 决定运行时
3. 根据 `coordinateReference` 初始化空间参考
4. 根据 `geoLayers` 初始化 GIS 图层
5. 根据 `assets` 加载模型/影像/地形/tileset 资源
6. 根据 `entities` 构建场景实体

### 2.2 编辑态

前端编辑器建议调用顺序：

1. `GET /scene-platform/scenes/{sceneCode}/descriptor`
2. `GET /scene-platform/scenes/{sceneCode}/assets`
3. 编辑器内调整布局 / 绑定资源 / 图层控制
4. `POST /scene-platform/scenes/{sceneCode}/entities/layout:batchSave`
5. 资源调整时调用场景资源绑定接口

---

## 3. 核心 descriptor 契约

接口：

- `GET /scene-platform/scenes/{sceneCode}/descriptor`

核心字段：

### scene 基本信息

- `sceneCode`
- `sceneName`
- `templateCode`
- `templateName`
- `sceneType`
- `engineProfile`

### scene 配置

- `capabilitiesJson`
- `layerConfigJson`
- `defaultViewpointJson`

### geo 配置

- `coordinateReference`
- `geoLayers`

### asset / spatial

- `assets`
- `entities`
- `hybridMode`

---

## 4. coordinateReference 契约

字段：

- `crsCode`
- `originLng`
- `originLat`
- `originHeight`
- `axisMode`
- `unit`

用途：

- `Three`：建立本地坐标原点
- `Cesium`：建立经纬度与本地场景映射
- `Hybrid`：同步 Three 与 Cesium 坐标桥接

---

## 5. geoLayers 契约

每个图层包含：

- `layerKey`
- `layerName`
- `layerType`
- `providerType`
- `engineProfile`
- `enabledFlag`
- `sortNo`
- `configJson`
- `metadataJson`

建议 `layerType`：

- `IMAGERY`
- `TERRAIN`
- `TILESET`
- `VECTOR`

建议 `providerType`：

- `URL_TEMPLATE`
- `CESIUM_TERRAIN`
- `THREE_TILESET`
- `WMTS`
- `WMS`

前端解释规则：

- `engineProfile = THREE`：仅作为轻 GIS / 参考图层处理
- `engineProfile = CESIUM`：按 GIS 原生图层处理
- `engineProfile = HYBRID`：GIS 图层由 Cesium 处理，精细模型由 Three 处理

---

## 6. assets 契约

每个资源包含：

- `assetCode`
- `assetName`
- `assetType`
- `assetUrl`
- `previewUrl`
- `format`
- `engineProfile`
- `bindingType`
- `bindingKey`
- `bindingMetadataJson`
- `assetMetadataJson`

建议 `assetType`：

- `MODEL`
- `IMAGERY`
- `TERRAIN`
- `TILESET`
- `MATERIAL`

建议 `bindingType`：

- `SCENE_RESOURCE`
- `GEO_LAYER`
- `ENTITY_RESOURCE`

前端解释：

- `MODEL` → Three runtime 资源
- `IMAGERY/TERRAIN/TILESET` → GIS runtime 资源
- `bindingKey` 用于和图层、实体、场景槽位对齐

---

## 7. entities 契约

每个实体布局包含：

- `entityId`
- `entityType`
- `entityName`
- `geoLng`
- `geoLat`
- `geoHeight`
- `localX`
- `localY`
- `localZ`
- `rotationX`
- `rotationY`
- `rotationZ`
- `scaleX`
- `scaleY`
- `scaleZ`
- `layerKey`
- `visibleFlag`
- `modelUrl`
- `spatialSource`
- `metadataJson`
- `versionNo`

前端用途：

- `Three`：直接按 local 坐标渲染
- `Cesium`：按 geo 坐标加载/定位
- `Hybrid`：Cesium 做地理定位，Three 做精细局部模型

---

## 8. 编辑器写回契约

接口：

- `POST /scene-platform/scenes/{sceneCode}/entities/layout:batchSave`

建议前端提交：

- 仅提交改动实体或当前场景全部实体
- `versionNo` 用于后续并发控制
- `spatialSource` 用于区分：
  - `MANUAL`
  - `GIS_IMPORT`
  - `EDITOR`

---

## 9. 管理接口清单

### 模板

- `GET /scene-platform/templates`
- `POST /scene-platform/templates`

### 场景

- `GET /scene-platform/scenes`
- `GET /scene-platform/scenes/{sceneCode}`
- `POST /scene-platform/scenes`

### 工作台

- `GET /scene-platform/scenes/{sceneCode}/descriptor`
- `GET /scene-platform/scenes/{sceneCode}/entities`
- `POST /scene-platform/scenes/{sceneCode}/entities/layout:batchSave`

### 资源

- `GET /scene-platform/assets`
- `POST /scene-platform/assets`

### 场景资源绑定

- `GET /scene-platform/scenes/{sceneCode}/assets`
- `POST /scene-platform/scenes/{sceneCode}/assets/bind`
- `DELETE /scene-platform/scenes/{sceneCode}/assets/{bindingId}`

---

## 10. Three / Cesium / Hybrid 约定

### THREE

适用于：

- 精细模型
- 编辑器
- 设施级三维工作台

### CESIUM

适用于：

- 影像图层
- 地形
- 3D Tiles
- GIS 浏览

### HYBRID

适用于：

- 宏观 GIS 浏览 + 微观 Three 精细模型

推荐约定：

- `coordinateReference` 始终由后端统一返回
- `geoLayers` 优先由 Cesium 消费
- `assets + entities` 优先由 Three 消费
- 场景最终运行时由 `engineProfile` 决定主引擎
- `hybridMode` 用于前端快速判断 `THREE_ONLY / CESIUM_ONLY / CESIUM_THREE`

---

## 11. 当前最小联调场景

当前种子数据中可直接联调：

- `sceneCode = north-station-scene`

联调重点：

1. 拉取 descriptor
2. 渲染两个设施实体
3. 读取 `gis-imagery` 图层配置
4. 读取 `MODEL-TANK-A` 与 `GIS-IMAGERY-001` 资源
5. 调整布局并回写

---

## 12. 后续扩展建议

后续可继续补充：

- 场景发布快照
- 图层组
- 资源版本
- GIS 图层管理接口
- 模板实例化接口
- 编辑器草稿态
