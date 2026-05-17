# scene-platform 前端平台架构设计稿

## 一、目标

基于 `scene-platform` 独立后端服务，前端不再按单一业务页面扩展，而要建设成可复用的三维/GIS平台内核，支持：

- 3D only
- GIS only
- Hybrid
- 模板化场景
- 编辑态 / 运行态分离
- 插件化能力扩展

---

## 二、总体分层

### 1. App / Workbench 层

职责：

- 页面框架
- 路由
- 权限
- 工具栏
- 左右侧面板
- 与 `scene-platform` 后端交互

### 2. Scene Runtime 层

职责：

- 场景加载
- 相机管理
- 图层管理
- 实体渲染
- 运行时交互
- 事件分发

建议拆分：

- `scene-runtime-core`
- `scene-runtime-three`
- `scene-runtime-cesium`
- `scene-runtime-hybrid`

### 3. Scene Editor 层

职责：

- 对象树
- 属性编辑
- 选中态
- gizmo 操作
- 拖拽摆放
- 草稿保存
- 发布

建议拆分：

- `scene-editor-core`
- `scene-editor-three`

### 4. Plugin / Capability 层

职责：

- 能力插件注册
- 能力启停
- 工具插件与图层插件管理

建议插件类型：

- `tool`
- `layer`
- `effect`
- `editor-panel`
- `scene-template`

### 5. Asset / Resource 层

职责：

- 模型加载
- 贴图加载
- tileset 加载
- 缓存
- 进度管理
- 错误处理

### 6. Adapter 层

职责：

- Three 适配
- Cesium 适配
- Hybrid 坐标与对象桥接

---

## 三、推荐目录结构

```text
src/
  platform/
    app/
    scene-runtime/
      core/
      three/
      cesium/
      hybrid/
    scene-editor/
      core/
      three/
    plugins/
      registry/
      annotation/
      selection/
      navigation/
      viewport-gizmo/
      layer-manager/
    assets/
      loaders/
      cache/
      manifest/
    adapters/
      three/
      cesium/
    contracts/
      scene-descriptor/
      entity-descriptor/
      plugin-contract/
    services/
      scene-platform-api/
      asset-api/
      gis-api/
    stores/
      scene/
      editor/
      runtime/
      plugin/
```

---

## 四、前后端协作方式

### 1. 后端是真源

前端不再持有场景的最终真源，后端 `scene-platform` 负责：

- 场景模板
- 场景实例
- 坐标参考
- 实体布局
- 资源绑定
- 草稿/发布

### 2. 前端消费统一 descriptor

建议以后端聚合接口为入口，例如：

- `GET /scene-platform/scenes/{sceneCode}/descriptor`

返回：

- scene
- capabilities
- layers
- coordinateReference
- entities
- assets

### 3. 编辑器只保存草稿与操作结果

例如：

- `POST /scene-platform/scenes/{sceneCode}/entities/layout:batchSave`

---

## 五、Three / Cesium / Hybrid 边界

### 1. Three

适合：

- 精细模型编辑
- 本地空间操作
- gizmo
- 局部三维场景
- 编辑器工作流

### 2. Cesium

适合：

- 经纬度
- GIS 地图底座
- 地形
- 影像
- 3D Tiles
- 园区/城市级浏览

### 3. Hybrid

建议长期模式：

- 宏观浏览：Cesium
- 精细编辑：Three
- 坐标与状态同步：`scene-platform + coordinateReference`

---

## 六、插件协议建议

### 1. 插件清单协议

```ts
interface ScenePluginManifest {
  pluginCode: string
  pluginName: string
  pluginType: 'tool' | 'layer' | 'effect' | 'editor-panel' | 'scene-template'
  engineSupport: Array<'three' | 'cesium' | 'hybrid'>
  requiredCapabilities: string[]
  version: string
  preview?: string[]
}
```

### 2. 运行时插件接口

```ts
interface SceneRuntimePlugin {
  install(ctx: SceneRuntimeContext): void
  activate(): void
  deactivate(): void
  dispose(): void
}
```

### 3. 编辑器插件接口

```ts
interface SceneEditorPlugin {
  install(ctx: SceneEditorContext): void
  onSelectionChange?(selection: any): void
  onTransformChange?(payload: any): void
}
```

---

## 七、建议优先建设的能力包

### 1. `scene-plugin-registry`

来源借鉴：

- `config.js`
- `loadDynamicComponent`

### 2. `scene-editor-exporter`

来源借鉴：

- `tresEditor/common/makePlugin.js`

### 3. `scene-toolkit`

来源借鉴：

- `operationTool`
- `useViewportGizmo`

### 4. `scene-resource-loader`

来源借鉴：

- `resourceManager`

### 5. `scene-gis-lite`

来源借鉴：

- `simpleGIS`

说明：仅作为轻 GIS 过渡层，正式 GIS runtime 长期以 Cesium 为主。

---

## 八、阶段性实施建议

### Phase 1

先建设：

- `scene-runtime-three`
- `scene-toolkit`
- `scene-resource-loader`

### Phase 2

再建设：

- `scene-editor-three`
- `scene-editor-exporter`
- 与 `scene-platform` 草稿/发布接口联调

### Phase 3

建设：

- `scene-plugin-registry`
- 模板装配能力
- 场景模板管理

### Phase 4

正式接入：

- `scene-runtime-cesium`
- `scene-runtime-hybrid`

---

## 九、结论

前端平台建设方向应从“设施工作台页面”升级为“场景平台运行时 + 编辑器 + 插件体系”。

核心原则：

- 后端场景真源
- 前端运行时解释 descriptor
- 编辑器与运行态分离
- Three 和 Cesium 职责清晰
- 模板化与插件化并行推进
