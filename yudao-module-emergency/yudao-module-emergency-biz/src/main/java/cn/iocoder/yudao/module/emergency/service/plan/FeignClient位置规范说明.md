# FeignClient 位置规范说明

## 📋 项目中的两种模式

### 模式1：Framework中的CommonApi（框架层通用API）

**位置**：`yudao-framework/yudao-common/src/main/java/cn/iocoder/yudao/framework/common/biz/`

**示例**：
- `DictDataCommonApi` - 字典数据通用API
- `PermissionCommonApi` - 权限通用API
- `TenantCommonApi` - 租户通用API
- `OAuth2TokenCommonApi` - OAuth2令牌通用API

**特点**：
- 框架层定义，供所有模块使用
- 定义在 `yudao-common` 模块中
- 使用 `RpcConstants.SYSTEM_NAME` 作为服务名

**示例代码**：
```java
// Framework中的CommonApi
@FeignClient(name = RpcConstants.SYSTEM_NAME, primary = false)
public interface DictDataCommonApi {
    @GetMapping(PREFIX + "/list")
    CommonResult<List<DictDataRespDTO>> getDictDataList(@RequestParam("dictType") String dictType);
}
```

### 模式2：System模块中的Api（模块特定API）

**位置**：`yudao-module-system/yudao-module-system-api/src/main/java/cn/iocoder/yudao/module/system/api/`

**示例**：
- `CategoryApi` - 分类API（当前实现）
- `DictDataApi` - 字典数据API（继承CommonApi）
- `AdminUserApi` - 管理员用户API

**特点**：
- 模块特定API，可以继承CommonApi，也可以独立定义
- 定义在 `xxx-module-xxx-api` 模块中
- 使用 `ApiConstants.NAME` 作为服务名

**示例代码**：
```java
// System模块中的Api（继承CommonApi）
@FeignClient(name = ApiConstants.NAME)
public interface DictDataApi extends DictDataCommonApi {
    // 扩展方法
    @GetMapping(PREFIX + "/valid")
    CommonResult<Boolean> validateDictDataList(...);
}

// System模块中的Api（独立定义）
@FeignClient(name = ApiConstants.NAME)
public interface CategoryApi {
    // 所有方法都在这里定义
}
```

## 🎯 当前实现分析

### CategoryApi 的当前实现

**位置**：`yudao-module-system/yudao-module-system-api/`

**实现方式**：独立定义，没有继承CommonApi

**优点**：
- ✅ 简单直接，所有方法都在一个接口中
- ✅ 模块内聚，分类功能完全在System模块中

**缺点**：
- ⚠️ 如果分类功能是框架层通用的，应该定义CommonApi

## 💡 推荐方案

### 方案1：保持当前实现（推荐）✅

**理由**：
1. 分类功能虽然通用，但实现和业务逻辑都在System模块中
2. 其他模块通过System模块的CategoryApi调用即可
3. 符合"System模块集成方式"的架构设计

**当前结构**：
```
yudao-module-system/
  ├── yudao-module-system-api/        ← FeignClient接口在这里
  │   └── api/category/CategoryApi.java
  └── yudao-module-system-server/     ← 实现类在这里
      └── api/category/CategoryApiImpl.java
```

### 方案2：迁移到Framework（如果分类是框架层通用功能）

**如果分类功能是框架层通用的**，可以考虑：

1. **在Framework中定义CommonApi**：
```java
// yudao-framework/yudao-common/src/main/java/.../CategoryCommonApi.java
@FeignClient(name = RpcConstants.SYSTEM_NAME, primary = false)
public interface CategoryCommonApi {
    @GetMapping(PREFIX + "/get")
    CommonResult<Map<String, Object>> getCategory(...);
    
    @PostMapping(PREFIX + "/create")
    CommonResult<Long> createCategory(...);
    // ... 基础方法
}
```

2. **System模块的Api继承CommonApi**：
```java
// yudao-module-system/yudao-module-system-api/.../CategoryApi.java
@FeignClient(name = ApiConstants.NAME)
public interface CategoryApi extends CategoryCommonApi {
    // 可以扩展System模块特定的方法
}
```

## 📊 两种方案对比

| 维度 | 方案1：System模块Api | 方案2：Framework CommonApi |
| ------ | ------------------- | ------------------------- |
| **位置** | `system-api` 模块 | `yudao-common` 模块 |
| **适用场景** | 模块特定功能 | 框架层通用功能 |
| **依赖关系** | 其他模块依赖system-api | 所有模块依赖yudao-common |
| **实现复杂度** | 简单 | 需要定义两层接口 |
| **当前实现** | ✅ 已实现 | ❌ 需要重构 |

## ✅ 结论

**推荐使用方案1（保持当前实现）**：

1. **符合架构设计**：分类功能在System模块中实现，通过System模块的Api暴露
2. **简单直接**：不需要定义两层接口
3. **模块内聚**：分类相关的所有代码都在System模块中
4. **符合规范**：根据开发规范，FeignClient应该放在 `xxx-api` 模块中

**当前实现是正确的**：
- ✅ FeignClient接口在 `system-api` 模块中
- ✅ 实现类在 `system-server` 模块中
- ✅ 其他模块通过依赖 `system-api` 模块使用

## 📝 总结

**回答你的问题**：

1. **System模块要实现FeignClient吗？**
   - ✅ 是的，当前实现是正确的
   - FeignClient接口在 `system-api` 模块中
   - 实现类在 `system-server` 模块中

2. **还是把FeignClient放在Framework中？**
   - ❌ 不需要
   - 除非分类功能是框架层通用的（需要所有模块都依赖）
   - 当前实现（放在System模块）更符合架构设计

**当前实现完全正确，无需修改！** ✅

