# 应急管理系统权限点配置

本文档定义了应急管理系统所需的所有权限点，用于与yudao框架的权限系统集成。

## 权限点命名规范

权限点格式：`emergency:{模块}:{操作}`

- `emergency`：应急管理系统模块标识
- `{模块}`：具体业务模块（event、response、task、plan、command、dispatch、statistics、report、organization等）
- `{操作}`：操作类型（create、update、delete、query、submit、confirm等）

## 权限点列表

### 事件管理（event）

- `emergency:event:create` - 创建应急事件
- `emergency:event:update` - 更新应急事件
- `emergency:event:delete` - 删除应急事件
- `emergency:event:query` - 查询应急事件
- `emergency:event:report` - 事件上报
- `emergency:event:confirm` - 确认事件
- `emergency:event:assess` - 事件研判

### 响应管理（response）

- `emergency:response:create` - 创建应急响应
- `emergency:response:update` - 更新应急响应
- `emergency:response:delete` - 删除应急响应
- `emergency:response:query` - 查询应急响应
- `emergency:response:start` - 启动响应
- `emergency:response:upgrade` - 升级响应
- `emergency:response:cancel` - 取消响应

### 任务管理（task）

- `emergency:task:create` - 创建应急任务
- `emergency:task:update` - 更新应急任务
- `emergency:task:delete` - 删除应急任务
- `emergency:task:query` - 查询应急任务
- `emergency:task:start` - 启动任务
- `emergency:task:complete` - 完成任务
- `emergency:task:terminate` - 终止任务

### 预案管理（plan）

- `emergency:plan:create` - 创建应急预案
- `emergency:plan:update` - 更新应急预案
- `emergency:plan:delete` - 删除应急预案
- `emergency:plan:query` - 查询应急预案
- `emergency:plan:group:create` - 创建预案分组
- `emergency:plan:group:update` - 更新预案分组
- `emergency:plan:group:delete` - 删除预案分组
- `emergency:plan:step:create` - 创建预案步骤
- `emergency:plan:step:update` - 更新预案步骤
- `emergency:plan:step:delete` - 删除预案步骤

### 指令管理（command）

- `emergency:command:create` - 创建应急指令
- `emergency:command:update` - 更新应急指令
- `emergency:command:delete` - 删除应急指令
- `emergency:command:query` - 查询应急指令
- `emergency:command:step:create` - 创建指令步骤
- `emergency:command:step:update` - 更新指令步骤
- `emergency:command:step:delete` - 删除指令步骤
- `emergency:command:step:complete` - 完成指令步骤
- `emergency:command:template:create` - 创建指令模板
- `emergency:command:template:update` - 更新指令模板
- `emergency:command:template:delete` - 删除指令模板

### 资源调度（dispatch）

- `emergency:resource:create` - 创建资源
- `emergency:resource:update` - 更新资源
- `emergency:resource:delete` - 删除资源
- `emergency:resource:query` - 查询资源
- `emergency:dispatch:create` - 创建资源调度
- `emergency:dispatch:update` - 更新资源调度
- `emergency:dispatch:delete` - 删除资源调度
- `emergency:dispatch:query` - 查询资源调度
- `emergency:dispatch:recover` - 回收资源

### 统计分析（statistics）

- `emergency:statistics:query` - 查询统计数据

### 信息报送（report）

- `emergency:information-report:create` - 创建信息报送
- `emergency:information-report:update` - 更新信息报送
- `emergency:information-report:delete` - 删除信息报送
- `emergency:information-report:query` - 查询信息报送
- `emergency:information-report:submit` - 提交信息报送
- `emergency:information-report:confirm` - 确认信息报送

### 组织架构（organization）

- `emergency:organization:create` - 创建应急组织
- `emergency:organization:update` - 更新应急组织
- `emergency:organization:delete` - 删除应急组织
- `emergency:organization:query` - 查询应急组织

## 角色配置建议

**详细角色定义请参见**：`role/emergency-roles.md`

### 系统管理员（admin）
拥有所有权限点

### 报告人（emergency_reporter）
- 事件管理：创建、查询、上报
- 信息报送：创建、查询、提交
- 其他模块：仅查询权限

### 执行人（emergency_executor）
- 事件管理：查询
- 响应管理：查询
- 任务管理：查询、启动、完成、终止
- 指令管理：查询、完成指令步骤
- 资源调度：查询
- 信息报送：查询
- 其他模块：仅查询权限

### 指挥人员（emergency_commander）
- 事件管理：所有权限
- 响应管理：所有权限
- 任务管理：所有权限
- 预案管理：查询
- 指令管理：所有权限
- 资源调度：所有权限
- 统计分析：查询
- 信息报送：所有权限
- 组织架构：查询

### 应急管理员（emergency_admin）
- 事件管理：所有权限
- 响应管理：所有权限
- 任务管理：所有权限
- 预案管理：所有权限
- 指令管理：所有权限
- 资源调度：所有权限
- 统计分析：查询权限
- 信息报送：所有权限
- 组织架构：所有权限

### 应急操作员（emergency_operator）
- 事件管理：创建、查询、上报、确认
- 响应管理：查询、启动、升级
- 任务管理：查询、启动、完成
- 预案管理：查询
- 指令管理：查询、完成指令步骤
- 资源调度：查询
- 统计分析：查询
- 信息报送：创建、查询、提交
- 组织架构：查询

### 应急查看员（emergency_viewer）
- 所有模块：仅查询权限

## 权限集成说明

yudao框架已经提供了完整的权限管理系统，应急管理系统只需要：

1. **在Controller中使用@PreAuthorize注解**：已在所有Controller中实现
2. **配置权限点**：需要在yudao-admin系统中配置上述权限点
3. **分配角色权限**：在yudao-admin系统中为角色分配相应权限
4. **用户角色关联**：在yudao-admin系统中为用户分配相应角色

## 注意事项

1. 所有Controller方法都已添加`@PreAuthorize`注解
2. 权限点需要在yudao-admin系统中手动配置
3. 建议按照角色配置建议进行权限分配
4. 权限点命名遵循统一规范，便于管理和维护




