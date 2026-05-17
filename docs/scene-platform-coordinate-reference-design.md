# Scene Platform 坐标系 / 投影系 / 转换体系设计

## 1. 目标

本文档用于完整梳理 `scene-platform` 中：

- 坐标系（Coordinate Reference System, CRS）
- 投影系（Projected CRS / Projection）
- 参考框架（Reference Frame）
- 本地工程坐标系（Local / Engine Frame）
- 后端多坐标系转换能力
- 用户如何清晰理解并配置不同坐标系

并作为后续后端实现与前端编辑器交互的统一设计依据。

---

## 2. 先区分几个容易混淆的概念

### 2.1 地理坐标系 Geographic CRS

用于表达：

- 经度 longitude
- 纬度 latitude
- 椭球高 height / altitude

典型示例：

- `EPSG:4326`（WGS84）
- `EPSG:4490`（CGCS2000）

特点：

- 单位通常为角度 + 米
- 适合表达真实地理位置
- 不适合直接作为大多数三维引擎内部笛卡尔运算坐标

### 2.2 投影坐标系 Projected CRS

将地理坐标投影到平面坐标系，形成：

- Easting / X
- Northing / Y
- Height / Z

典型示例：

- Web Mercator
- UTM
- 高斯-克吕格 / CGCS2000 3-degree zone

特点：

- 单位通常为米
- 适合 GIS 平面量测与工程图层叠加
- 受投影带和畸变影响

### 2.3 地心地固坐标系 ECEF

Earth-Centered, Earth-Fixed：

- 原点在地心
- X/Y/Z 为全球笛卡尔坐标
- 单位为米

特点：

- 是 Cesium / 大地测量 / 局部切平面推导的重要中间坐标
- 非常适合作为跨 CRS 的统一计算中间层

### 2.4 局部切平面 Local Tangent Frame

以场景某个参考点为原点建立局部坐标系，常见：

- ENU（East, North, Up）
- NED（North, East, Down）
- NWU / NUE 等

特点：

- 非常适合引擎和编辑器
- 可以近似线性表达小范围场景
- 是 `Three / UE / Hybrid` 最适合直接消费的本地工程坐标

### 2.5 引擎坐标系 Engine Frame

引擎自己的坐标定义，例如：

- UE 世界坐标
- Three 场景本地坐标

特点：

- 本质上是“局部工程坐标”的一种实现
- 必须和某个地理参考建立映射，否则只是纯虚拟坐标

---

## 3. 投影系和坐标系之间的关系

可以把关系理解成一条链：

`Geographic CRS <-> Projected CRS <-> ECEF <-> Local Tangent Frame <-> Engine Frame`

更准确地说：

### 3.1 Geographic 与 Projected

- 同属 CRS 范畴
- Geographic 是经纬高
- Projected 是投影平面坐标
- 两者可通过投影算法互转

### 3.2 Geographic 与 ECEF

- Geographic 依赖椭球模型
- ECEF 是椭球上的全球笛卡尔表达
- 两者互转是大地测量基础能力

### 3.3 ECEF 与 Local Tangent Frame

- 以某个原点建立 ENU/NED 等局部切平面
- ECEF 到 ENU 是 Cesium/UE 非常核心的能力

### 3.4 Local Tangent Frame 与 Engine Frame

- Engine Frame 通常直接复用 Local Frame 的轴向和单位
- 也可能存在轴重排、正负翻转、单位缩放
- 例如 UE/Three 的轴方向不完全相同，需要额外轴映射

### 3.5 Projected 与 Local Tangent Frame

- 小范围工程中，Projected CRS 常被直接近似作为 Local
- 但严格来说应通过统一的参考变换定义，不应隐式混用

---

## 4. 参考 UE 与 Cesium 的实现思想

## 4.1 UE GeoReferencing 的核心思想

参考 `UE_5.5/Engine/Plugins/Runtime/GeoReferencing`：

UE 明确提供以下转换通道：

- Engine <-> Projected
- Engine <-> ECEF
- Engine <-> Geographic
- Projected <-> Geographic
- Projected <-> ECEF
- Geographic <-> ECEF

并提供：

- ENU 向量获取
- Tangent Transform
- Flat Planet / Round Planet 模式

这说明 UE 的关键思想是：

### 原则 A：显式暴露“参考坐标空间”

而不是只给一个黑盒 `lng/lat -> xyz`。

### 原则 B：以 ECEF 和 Local Tangent Frame 为中间桥梁

这对大范围场景与高精度都非常重要。

### 原则 C：让用户知道自己当前工作在哪个空间

例如：

- Engine
- Projected
- Geographic
- ECEF

这对调试、编辑器展示、工程配置非常关键。

## 4.2 Cesium 的核心思想

Cesium 公开的 `Transforms` / `Cartographic` / `Cartesian3` / `eastNorthUpToFixedFrame` 体系说明：

### 原则 D：固定参考框架优先

Cesium 中地球固定框架（ECEF / Fixed Frame）是核心枢纽。

### 原则 E：本地局部参考系通过 Fixed Frame 动态生成

如：

- `eastNorthUpToFixedFrame`
- `northEastDownToFixedFrame`
- `headingPitchRollToFixedFrame`

### 原则 F：局部姿态、旋转、朝向不直接绑死在经纬度上

而是通过：

- local frame
- fixed frame
- rotation matrix / quaternion
进行规范化表达。

## 4.3 对我们后端的直接启发

后端不能只存：

- `crsCode`
- `originLng`
- `originLat`

还必须补齐：

- geographic CRS
- projected CRS
- fixed frame / ECEF 作为标准中间层
- local frame 定义（ENU/NED/NWU...）
- engine frame 定义（轴向 / 缩放 / handedness）
- transform pipeline 配置

---

## 5. 当前 scene-platform 的缺口

当前 `coordinate_reference` 只包含：

- `crsCode`
- `originLng`
- `originLat`
- `originHeight`
- `axisMode`
- `unit`

这还只是“最小原点参考”，还不能完整表达：

### 缺口 1：缺少 geographic / projected 的显式拆分

目前 `crsCode` 语义不清：

- 是地理 CRS？
- 还是投影 CRS？

### 缺口 2：缺少 fixed frame / ECEF 作为统一中间层

没有标准中间层，后续多 CRS 转换会变复杂且不稳定。

### 缺口 3：缺少 local frame 详细定义

仅有 `axisMode` 不够，需要：

- frame type
- axis order
- handedness
- up axis
- linear unit

### 缺口 4：缺少 engine frame 配置

前端不同运行时需要明确：

- Three 使用何种轴
- UE 使用何种轴
- 是否 Y-up / Z-up
- 是否 left-handed / right-handed

### 缺口 5：缺少用户可理解的配置视图

目前用户只能看到字段，无法理解：

- 数据来自哪个坐标系
- 转换到哪个坐标系
- 哪条链路在生效

---

## 6. 后端应采用的统一转换模型

建议采用“标准中间层 + 明确空间类型”的转换架构：

`Geographic CRS <-> Projected CRS`
`Geographic CRS <-> ECEF`
`ECEF <-> Local Tangent Frame`
`Local Tangent Frame <-> Engine Frame`

其中：

### 6.1 ECEF 作为标准中间空间

这是最重要的设计决策。

原因：

- 与 Cesium 天然一致
- 与 UE GeoReferencing 的思路一致
- 便于局部切平面计算
- 便于不同 Geographic CRS / Projected CRS 统一汇聚
- 便于 Hybrid 场景对接

### 6.2 Local Tangent Frame 作为标准场景空间

场景内部的：

- localX
- localY
- localZ
应明确属于某个 Local Frame：
- ENU
- NED
- NWU
- 自定义 Axis Map

### 6.3 Engine Frame 作为可配置消费空间

后端不直接假设所有前端都一个坐标轴，而是显式存储：

- engineType
- upAxis
- forwardAxis
- rightAxis
- handedness
- unitScale

---

## 7. 建议新增的后端领域模型

## 7.1 coordinate_reference 扩展字段

建议扩展为：

- `geographic_crs_code`
- `projected_crs_code`
- `datum_code`
- `ellipsoid_code`
- `planet_shape`
- `reference_frame_type`（如 ECEF / PROJECTED / LOCAL）
- `local_frame_type`（ENU / NED / NWU / CUSTOM）
- `engine_frame_type`（THREE / UE / CUSTOM）
- `axis_order`
- `handedness`
- `linear_unit`
- `angular_unit`
- `origin_ecef_x`
- `origin_ecef_y`
- `origin_ecef_z`
- `origin_projected_x`
- `origin_projected_y`
- `origin_projected_z`
- `origin_heading`
- `origin_pitch`
- `origin_roll`
- `transform_config_json`

## 7.2 新增 coordinate_transform_profile

用于存储一个可复用转换配置：

- profileCode
- profileName
- geographicCrsCode
- projectedCrsCode
- localFrameType
- engineFrameType
- planetShape
- transformPipelineJson
- remark

用途：

- 一套站场 / 一套区域 / 一类项目复用同一转换方案

## 7.3 新增 coordinate_conversion_record

记录转换结果与调试日志：

- sourceSpaceType
- targetSpaceType
- sourcePayloadJson
- resultPayloadJson
- profileId
- precisionStatsJson
- warningMessage

用途：

- 调试
- 审计
- 帮助用户理解结果

---

## 8. 后端应支持的空间类型枚举

建议统一抽象 `SpaceType`：

- `GEOGRAPHIC`
- `PROJECTED`
- `ECEF`
- `LOCAL_TANGENT`
- `ENGINE`

前后端、接口、调试面板全部围绕该枚举。

---

## 9. 后端应支持的核心转换接口

## 9.1 标准转换接口

### 单点转换

- `POST /scene-platform/coordinate/convert`

请求：

- sceneCode / profileCode
- sourceSpaceType
- targetSpaceType
- coordinate payload
- optional orientation payload

返回：

- result coordinate
- path used
- precision info
- warnings

### 批量转换

- `POST /scene-platform/coordinate/convert:batch`

### 查询转换配置

- `GET /scene-platform/scenes/{sceneCode}/coordinate-reference`
- `GET /scene-platform/coordinate/profiles/{profileCode}`

### 查询转换预览

- `POST /scene-platform/scenes/{sceneCode}/coordinate/preview-transform`

用于让用户在 UI 中输入一个点，实时看：

- Geographic
- Projected
- ECEF
- Local
- Engine
各空间结果。

## 9.2 调试接口

- `GET /scene-platform/scenes/{sceneCode}/coordinate-pipeline`
- `POST /scene-platform/scenes/{sceneCode}/coordinate/validate`

返回：

- 当前使用哪条转换链
- 原点定义
- 轴定义
- 是否存在歧义
- 是否推荐改为 projected / ENU / hybrid

---

## 10. 用户如何清晰理解和使用不同坐标系

这是设计里最关键的一点。

不能只给用户几个字段，而要给用户一个“空间视图”。

## 10.1 推荐 UI 概念模型

在前端配置界面中明确展示 5 个层级：

1. **地理坐标系**
   - 经度/纬度/高程
   - Geographic CRS: EPSG:4490

2. **投影坐标系**
   - Easting / Northing / Height
   - Projected CRS: CGCS2000 Zone XX

3. **地心地固坐标**
   - ECEF X/Y/Z
   - 作为内部转换中间层

4. **场景局部坐标**
   - Local ENU / NED
   - 原点、轴向、单位

5. **引擎坐标**
   - Three / UE / Custom
   - Up Axis / Forward Axis / Handedness

## 10.2 推荐用户界面交互

### 方式 A：坐标系向导 Wizard

步骤：

1. 选择数据来源坐标系
2. 选择场景目标坐标系
3. 设置原点
4. 选择局部坐标轴模式
5. 选择引擎坐标轴模式
6. 实时预览转换

### 方式 B：坐标卡片视图

同一个点，用户可以同时看到：

- Geographic
- Projected
- ECEF
- Local
- Engine

### 方式 C：转换链路图

例如展示：

`CGCS2000 Geographic -> ECEF -> Local ENU -> Three Engine`

或：

`Projected CRS -> Geographic -> ECEF -> UE Engine`

### 方式 D：精度提示与风险提示

例如：

- 当前使用 Flat Planet，大范围会产生投影误差
- 当前 projected CRS 未配置，无法精确回算平面坐标
- 当前 axisMode 与 engine frame 不一致，可能导致旋转方向错误

---

## 11. 推荐的后端实现分层

## 11.1 metadata 层

只负责描述：

- CRS
- Datum
- Ellipsoid
- Local Frame
- Engine Frame

## 11.2 pipeline 层

负责决定：

- source -> target 应走哪条转换链

示例：

- `GEOGRAPHIC -> LOCAL` 走 `GEOGRAPHIC -> ECEF -> LOCAL`
- `PROJECTED -> ENGINE` 走 `PROJECTED -> GEOGRAPHIC -> ECEF -> LOCAL -> ENGINE`

## 11.3 math 层

负责具体变换：

- geographic/ecef
- projected/geographic
- ecef/enu
- local/engine
- heading/pitch/roll 与 matrix/quaternion

## 11.4 application 层

负责：

- 场景坐标配置查询
- 批量转换
- 转换预览
- 校验与提示

---

## 12. 推荐补齐的 Java 骨架

建议新增包：

```text
service/coordinate
  CoordinateTransformService
  CoordinateTransformProfileService
  CoordinatePipelineResolver
  CoordinateValidationService

controller/admin/coordinate
  CoordinateTransformController
  CoordinateReferenceController

dal/dataobject/coordinate
  CoordinateTransformProfileDO
  CoordinateConversionRecordDO
```

建议新增 VO：

- `CoordinateConvertReqVO`
- `CoordinateConvertRespVO`
- `CoordinatePreviewRespVO`
- `CoordinatePipelineRespVO`
- `CoordinateReferenceDetailRespVO`

---

## 13. 与当前 scene-platform 的衔接方式

## 13.1 descriptor 中继续保留 `coordinateReference`

但应扩展为“详细坐标参考对象”，而不只是原点字段。

## 13.2 spatial_entity_layout 保留多空间表达

建议长期保留：

- geographic coordinates
- local coordinates
- 后续可选 projected coordinates

## 13.3 geo/hybrid 消费原则

- Cesium 优先消费 `Geographic / ECEF / Geo Layers`
- Three/UE 优先消费 `Local / Engine`
- Hybrid 通过同一 `CoordinateReference` 桥接

---

## 14. 实施优先级建议

### Phase 1：补模型与接口骨架

- 扩展 coordinate reference
- 新增 transform profile
- 新增 convert / preview / pipeline 接口

### Phase 2：补转换中间层

- ECEF
- ENU / NED
- Engine frame mapping

### Phase 3：补用户可视化配置

- 向导
- 预览
- 风险提示

### Phase 4：补精度与审计

- conversion record
- validation report
- precision stats

---

## 15. 最终原则总结

1. 不要把“投影坐标”和“局部工程坐标”混为一谈
2. 不要让 `crsCode` 承担所有语义
3. 统一用 ECEF 做全局中间层
4. 场景内部统一显式定义 Local Tangent Frame
5. 引擎坐标必须可配置，不要写死
6. 给用户看到“空间层次”和“转换链路”，而不是只给参数
7. 后端必须同时支持：配置、预览、批量转换、调试与校验
