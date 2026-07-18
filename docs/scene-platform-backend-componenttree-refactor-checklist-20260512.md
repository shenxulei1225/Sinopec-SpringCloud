# Scene-Platform 后端 ComponentTree 改造清单

> 日期：2026-05-12
> 目标：将 Scene load 主链路升级为 `ActorInstanceRespVO + ActorInstanceComponentTreeRespVO`，让后端直接输出实例组件树结构，供 three.js 前端直接消费。

---

## 1. 改造目标

当前后端主链路已经完成：

- `SceneLoadRespVO.scene`
- `SceneLoadRespVO.actorInstances`
- `SceneLoadRespVO.sceneComponents`
- `SceneLoadRespVO.onlineCount`

但 `ActorInstanceRespVO` 仍然只返回：

- 基础实例字段
- 平铺 `components`

尚未返回：

- `componentTree`

本次改造的目标是：

1. 新增专门的实例树响应 VO
2. 给 `ActorInstanceRespVO` 增加 `componentTree`
3. 让 `SceneServiceImpl.loadScene()` 直接返回实例树
4. 让 `ActorInstanceController` 的详情 / 列表 / spawn 输出与 load 主链路一致
5. 平铺 `components` 短期保留为兼容字段

---

## 2. 新增类

### 2.1 ActorInstanceComponentTreeRespVO

路径：

`cheers-scene-3d-server/src/main/java/cn/cheers/x/scene/platform/controller/admin/actor/vo/ActorInstanceComponentTreeRespVO.java`

职责：

- 实例组件树根结构
- 仅承载 `root`

### 2.2 ActorInstanceComponentTreeNodeRespVO

路径：

`cheers-scene-3d-server/src/main/java/cn/cheers/x/scene/platform/controller/admin/actor/vo/ActorInstanceComponentTreeNodeRespVO.java`

职责：

- 实例组件树节点
- 用于渲染、层级恢复、运行时能力表达

关键字段：

- `id`
- `instanceCode`
- `componentCode`
- `componentTypeName`
- `enabledFlag`
- `sortNo`
- `parentComponentCode`
- `relativeTransform`
- `propertiesJson`
- `overrideJson`
- `constructArgsJson`
- `metadataJson`
- `children`

---

## 3. 修改文件

### 3.1 ActorInstanceRespVO.java

改造点：

- 新增 `componentTree` 字段
- 保留 `components` 字段

目标：

- `componentTree` 作为主输出
- `components` 作为兼容辅助结构

### 3.2 SceneServiceImpl.java

改造点：

- 在 `convertToActorInstanceRespVO()` 中同时回填：
  - `components`
  - `componentTree`
- 新增：
  - `buildActorInstanceComponentTree(...)`
  - `convertTreeNode(...)`

目标：

- `/scene/load` 直接返回组件树

### 3.3 ActorInstanceController.java

改造点：

- `convertActorInstanceWithComponents()` 同时回填：
  - `components`
  - `componentTree`
- 新增：
  - `buildActorInstanceComponentTree(...)`
  - `convertTreeNode(...)`

目标：

- 详情 / 列表 / spawn 输出结构与 `/scene/load` 一致

---

## 4. 构树规则

输入：

- `List<ActorInstanceComponentDO>`

输出：

- `ActorInstanceComponentTreeRespVO`

规则：

1. 按 `sortNo`、`id` 排序
2. 将每个组件转换为 `ActorInstanceComponentTreeNodeRespVO`
3. 建立 `componentCode -> node` 索引
4. 根据 `parentComponentCode` 组装 `children`
5. root 优先选择 `parentComponentCode` 为空的节点
6. 如果出现脏数据：
   - 父节点缺失
   - 多 root
   - 空树
   则做兜底处理，保证前端至少能拿到一个可用 root

---

## 5. 推荐实施顺序

### Step 1
新增响应 VO，并扩展 `ActorInstanceRespVO`

### Step 2
先改 `SceneServiceImpl.loadScene()` 主链路

### Step 3
再改 `ActorInstanceController`

### Step 4
前端切换优先消费 `componentTree`

### Step 5
后续清理 `SceneRuntimePackageRespVO` 中旧的 `actors/components` 语义

---

## 6. 当前风险

1. 历史实例组件数据可能存在：
   - `parentComponentCode` 为空
   - 多 root
   - 脏层级
2. `components` 与 `componentTree` 双轨期会有短暂冗余
3. `SceneRuntimePackageRespVO` 仍然未统一，后续仍需补齐

---

## 7. 验收标准

### 后端验收

- `/scene/load` 返回 `actorInstances[].componentTree`
- `/actor-instances/{id}` 返回 `componentTree`
- `/actor-instances/spawn` 返回 `componentTree`
- 所有接口仍兼容现有 `components`

### 前端联调验收

- 前端优先从 `componentTree` 恢复对象层级
- 能够从节点中读取：
  - mesh / texture / material 相关参数
  - relativeTransform
  - overrideJson / constructArgsJson
- 平铺 `components` 可逐步降级为调试字段
