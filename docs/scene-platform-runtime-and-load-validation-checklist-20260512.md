# Scene-Platform 场景加载 / Runtime / Spawn 联调校验清单

> 日期：2026-05-12
> 目的：校验后端 `componentTree` 主链路、前端 `three.js adapter`、runtime package 语义统一是否生效。

---

## 1. 联调目标

本轮联调需要同时验证三件事：

1. `/scene/load` 已返回 `actorInstances[].componentTree`
2. 前端工作台优先消费 `componentTree`，不是再依赖平铺 `components`
3. runtime package 语义已经从旧 `actors/components` 统一到 `actorInstances/sceneComponents`

---

## 2. 后端接口校验

### 2.1 `/scene-platform/scene/load?sceneCode=...`

确认返回：

- `scene`
- `actorInstances`
- `sceneComponents`
- `onlineCount`

并重点检查每个 `actorInstances[i]`：

- `instanceCode`
- `actorCode`
- `transform`
- `componentTree.root`
- `components`（兼容字段，可保留）

### 2.2 `componentTree` 节点校验

至少校验根节点和一层 children：

- `componentCode`
- `componentTypeName`
- `parentComponentCode`
- `relativeTransform`
- `propertiesJson`
- `overrideJson`
- `constructArgsJson`
- `children`

### 2.3 `/scene-platform/actor-instances/{id}`

确认详情接口也返回：

- `componentTree`
- `components`

结构与 `/scene/load` 中单个实例保持一致。

### 2.4 `/scene-platform/actor-instances/spawn`

spawn 后返回对象需校验：

- `instanceCode`
- `transform`
- `componentTree.root`
- `components`

---

## 3. 前端工作台校验

### 3.1 进入路径

验证两种入口：

1. `/scene-workbench`
2. `/scene-workbench?sceneCode=xxx`

### 3.2 场景列表跳转

从场景管理页点击：

- `进入工作台`

确认：

- 跳转带 `sceneCode`
- 工作台自动加载对应场景

### 3.3 three.js adapter 校验

前端当前必须优先从 `componentTree` 提取：

- `componentTypes`
- `resolvedModelUrl`
- `resolvedMeshCode`
- `renderSource`

目标行为：

- 优先 `actor-instance-component-tree`
- 次级 `actor-definition-component-tree`
- 最后才 fallback 到 `instance-components`

### 3.4 视口渲染校验

检查：

- 场景实例数显示正常
- 详情面板显示 `ActorInstance`
- 组件类型统计来自 `componentTree`
- 模型地址解析来自 `componentTree` 或资产引用

---

## 4. Runtime Package 校验

### 4.1 `/scene-platform/scenes/{sceneCode}/runtime/package`

确认语义字段改为：

- `actorInstances`
- `sceneComponents`

不再使用：

- `actors`
- `components`

### 4.2 Runtime 响应结构

检查：

- `sceneCode`
- `sceneName`
- `actorInstances`
- `sceneComponents`
- `envVars`
- `runtimeConfig`
- `resourceUrl`
- `version`

---

## 5. 回归风险检查

### 5.1 脏树数据

检查实例组件表中是否存在：

- 多 root
- `parentComponentCode` 指向不存在
- `sortNo` 混乱

### 5.2 兼容字段回归

虽然前端已优先吃 `componentTree`，但当前仍需确认：

- `components` 未丢失
- 旧逻辑未直接报错

### 5.3 前后端命名一致性

再次确认：

- `actorInstances`
- `sceneComponents`
- `componentTree`

不再混入旧字段：

- `actors`
- `components`（runtime package 语义）

---

## 6. 联调通过标准

满足以下全部条件即可视为本轮通过：

1. `/scene/load` 返回 `actorInstances[].componentTree`
2. `/actor-instances/{id}` 返回 `componentTree`
3. `/actor-instances/spawn` 返回 `componentTree`
4. 前端工作台能正常加载并显示场景
5. 前端 metadata 中 `renderSource` 优先显示 `actor-instance-component-tree`
6. runtime package 字段统一为 `actorInstances/sceneComponents`
