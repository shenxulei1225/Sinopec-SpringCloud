# scene-platform 与 icegl-three-vue-tres 借鉴分析

## 一、分析对象

本次分析主要围绕以下对象展开：

1. `icegl-three-vue-tres`
2. `oss.icegl.cn / zone3Deditor` 三维场景编辑器思路
3. 用户提供的知乎文章方向（核心主题可归纳为 Vue3 + TresJS + 编辑器/插件化方式快速落地数字孪生场景）

目标不是直接照搬开源项目，而是分析哪些能力适合借鉴到当前 `scene-platform` 独立服务和前端三维/GIS平台建设中。

---

## 二、总体结论

### 1. 适合借鉴的本质

`icegl-three-vue-tres` 不应被视为项目最终平台框架，而应被视为：

- 一个三维前端能力平台样本
- 一个插件化场景组织样本
- 一个编辑器导出工作流样本
- 一个前端场景工具箱样本

### 2. 不建议直接照搬

不建议直接复制其：

- 整体仓库结构
- 全量插件市场模式
- Three-only GIS 主路线
- 前端主导场景真源的方式
- 导出 Vue 源码作为长期正式发布形态

### 3. 推荐借鉴方向

建议重点借鉴：

- 场景插件化组织方式
- 编辑器导出场景产物的思路
- 运行时与编辑器分层
- 视角 gizmo 与操作工具体系
- 动态插件注册与远程加载
- 资源管理与运行时装配能力

---

## 三、可借鉴能力分级

## A. 第一优先级：建议优先吸收

### 1. `src/plugins/useViewportGizmo`

价值：

- 提供视角方位球/方位盒
- 支持点击切换相机朝向
- 支持视角动画
- 适合三维浏览和编辑场景

建议：

- 抽象为项目自己的 `ViewportGizmo` 工具组件
- 服务于 Three runtime 和 Three 编辑器
- 不直接绑定具体业务页面

### 2. `src/plugins/operationTool`

价值：

- 包含标注、导航、图例、框选、爆炸图、箭头等能力
- 更接近工业项目工作台工具层，而不是单纯特效 demo

建议：

拆解成项目自己的工具层服务：

- `selection-service`
- `annotation-service`
- `navigation-service`
- `legend-service`
- `explode-service`

### 3. `src/plugins/tresEditor`

价值：

- 场景导出机制成熟
- 能导出 scene、geometry、images、event script、config、page entry
- 本质是在做“编辑器产物模型”

建议：

不要直接导出 Vue 源码作为最终目标，而应改造为导出：

- `scene-template.json`
- `scene-layout.json`
- `asset-manifest.json`
- `interaction-config.json`
- `runtime-descriptor.json`

### 4. `src/plugins/zone3Deditor`

价值：

- 体现出编辑器不是查看器，而是场景生产工具
- 支持拖拽、装配、导出、接模型服务
- 和当前项目“模板化场景平台”的目标高度一致

建议：

借鉴其产品思路：

- 对象树
- 属性面板
- 场景资源面板
- 场景导出/发布流程
- 模型服务接入

### 5. `src/plugins/loadDynamicComponent`

价值：

- 提供动态 remote 注册与远程插件加载思路
- 对未来插件市场、远程能力装配、模板化场景下发很有帮助

建议：

在项目中预留：

- `plugin-registry`
- `runtime-plugin-loader`
- `scene-template-loader`

---

## B. 第二优先级：有条件借鉴

### 6. `src/plugins/resourceManager`

价值：

- 封装资源加载、进度、生命周期
- 适合提炼成统一资源加载层

建议：

前端建设：

- `asset-loader`
- `model-loader`
- `texture-loader`
- `resource-cache`

### 7. `src/plugins/simpleGIS`

价值：

- 提供瓦片、3DTiles、倾斜摄影、卫星地图等轻 GIS 组织方式
- 适合作为 GIS 资源结构和轻量组件化参考

限制：

- 不适合成为项目长期 GIS 主路线
- 不建议用 Three-only 承担最终 GIS 架构

建议：

- 用作轻 GIS 过渡方案和能力参考
- 正式 GIS 主底座仍建议采用 Cesium

### 8. `src/plugins/goView`

价值：

- 大屏 UI 编排
- 属性配置化
- 图表组件化
- 工作台外围界面组织思路

建议：

作为三维场景外层工作台 UI 编排参考，而不是直接嵌入三维内核。

### 9. `src/plugins/basic`

价值：

- 提供大量基础 Three 能力示例
- 包含 controls、材质、HTML、outline、clipping、postprocessing 等

建议：

提炼为项目自己的基础三维能力库，不直接整体复制。

---

## C. 第三优先级：更多作为案例参考

### 10. `digitalCity / digitalPark / industry4 / zoneFreeScene`

价值：

- 提供行业案例、场景模板、视觉参考
- 可用于后续行业模板沉淀

建议：

只作为：

- 模板案例参考
- 特效展示参考
- 场景构图与交互灵感来源

不直接作为平台基础架构代码引入。

---

## 四、对当前项目的架构启发

## 1. 后端层

继续坚持：

- 三维与 GIS 做成独立 `scene-platform` 服务
- 不耦合 `facility` 域
- 以后端作为场景真源

后端核心职责：

- 场景模板
- 场景实例
- 空间布局
- 资源绑定
- 坐标参考
- GIS 配置
- 草稿/发布

## 2. 前端层

前端不应继续按“单个设施工作台页面”扩展，而应升级成平台化分层：

- `scene-runtime-core`
- `scene-runtime-three`
- `scene-runtime-cesium`
- `scene-runtime-hybrid`
- `scene-editor-core`
- `scene-editor-three`
- `scene-plugin-registry`
- `scene-toolkit`
- `scene-resource-loader`

## 3. 编辑器与运行时分离

必须明确区分：

- 编辑态：拖拽、摆放、标注、属性编辑、草稿保存
- 运行态：只消费 descriptor，展示和交互发布场景

## 4. 插件化与模板化统一

结合 icegl 的经验，项目应统一两个概念：

- 后端：`scene_template / scene_instance`
- 前端：`plugin-manifest / runtime-plugin / editor-plugin`

模板不只是 UI 模板，而是：

- 场景结构模板
- 图层模板
- 资源绑定模板
- 交互模板
- 视角模板

---

## 五、建议建立的前端能力包

基于本次分析，建议逐步建立以下前端能力包：

### 1. `scene-plugin-registry`

借鉴来源：

- 各插件 `config.js`
- `loadDynamicComponent`

### 2. `scene-editor-exporter`

借鉴来源：

- `tresEditor/common/makePlugin.js`

### 3. `scene-toolkit`

借鉴来源：

- `operationTool`
- `useViewportGizmo`

### 4. `scene-resource-loader`

借鉴来源：

- `resourceManager`

### 5. `scene-gis-lite`

借鉴来源：

- `simpleGIS`

说明：`scene-gis-lite` 仅作为轻 GIS 过渡层，正式 GIS runtime 仍建议使用 Cesium。

---

## 六、最终结论

### 适合借鉴的方向

- 插件元数据协议
- 编辑器导出产物模型
- 运行时/编辑器分离
- 视角 gizmo
- 操作工具体系
- 动态插件注册与远程装配
- 资源加载与运行时装配

### 不适合直接借鉴的方向

- 仓库级大而全结构
- Three-only GIS 主架构
- 前端作为场景主数据中心
- 长期以导出源码代替平台发布

### 对项目的建议

项目应继续推进为：

- 独立 `scene-platform` 后端服务
- 前端平台化分层运行时
- Three 负责精细编辑
- Cesium 负责 GIS 底座
- 通过 descriptor 契约和模板体系支撑未来三维/GIS能力可替换
