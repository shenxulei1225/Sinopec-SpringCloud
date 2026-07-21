# EmergencyPlanCategoryService 使用示例

## 概述

`EmergencyPlanCategoryService` 展示了如何在业务模块中使用 System 模块的统一分类功能。

通过调用 `CategoryApi`，应急模块可以管理自己的分类数据，而无需重复实现分类功能。

## 架构设计

```
应急模块 (Emergency Module)
    ↓ (通过 Feign RPC 调用)
System 模块的 CategoryApi
    ↓
CategoryApiImpl (RPC 实现)
    ↓
CategoryService (业务逻辑)
    ↓
数据库 (通过 businessTypeCode 区分不同业务)
```

## 业务类型编码

- **应急预案分类**: `emergency_plan`
- **应急事件分类**: `emergency_event` (已在 EventCategoryValidationService 中使用)

## 使用示例

### 1. 创建分类

```java
@Autowired
private EmergencyPlanCategoryService categoryService;

// 创建根分类
Long rootCategoryId = categoryService.createCategory(
    "自然灾害预案",  // 分类名称
    null,           // 父分类ID（null表示根分类）
    1,              // 排序
    "自然灾害类应急预案" // 描述
);

// 创建子分类
Long subCategoryId = categoryService.createCategory(
    "地震预案",
    rootCategoryId, // 父分类ID
    1,
    "地震应急预案"
);
```

### 2. 查询分类

```java
// 获取分类信息
Map<String, Object> category = categoryService.getCategory(categoryId);

// 获取分类树（全部）
List<Map<String, Object>> tree = categoryService.getCategoryTree(null);

// 获取分类树（只显示启用的）
List<Map<String, Object>> enabledTree = categoryService.getCategoryTree(1);

// 验证分类是否存在
boolean exists = categoryService.existsCategory(categoryId);
```

### 3. 更新分类

```java
categoryService.updateCategory(
    categoryId,
    "更新后的分类名称",
    parentId,
    2,    // 排序
    1,    // 状态（1启用，0禁用）
    "更新后的描述"
);
```

### 4. 删除分类

```java
// 普通删除（如果存在子分类会失败）
categoryService.deleteCategory(categoryId, false);

// 级联删除（同时删除所有子分类）
categoryService.deleteCategory(categoryId, true);
```

### 5. 移动和排序

```java
// 移动分类到另一个父分类下
categoryService.moveCategory(categoryId, newParentId);

// 移动分类到根
categoryService.moveCategory(categoryId, null);

// 排序分类
List<Long> categoryIds = Arrays.asList(3L, 1L, 2L); // 按此顺序排序
categoryService.sortCategories(categoryIds);
```

### 6. 启用/禁用分类

```java
// 启用分类
categoryService.enableCategory(categoryId);

// 禁用分类
categoryService.disableCategory(categoryId);
```

### 7. 搜索分类

```java
// 搜索分类
List<Map<String, Object>> results = categoryService.searchCategories("地震");
```

### 8. 获取分类路径和子分类

```java
// 获取分类路径（从根到当前分类）
List<Map<String, Object>> path = categoryService.getCategoryPath(categoryId);

// 获取子分类列表
List<Map<String, Object>> children = categoryService.getChildren(categoryId, null);

// 获取启用的子分类
List<Map<String, Object>> enabledChildren = categoryService.getChildren(categoryId, 1);
```

## 在其他业务模块中使用

如果其他业务模块也需要分类功能，可以按照以下步骤：

### 1. 配置 Feign 客户端

在 `RpcConfiguration` 中添加 `CategoryApi`：

```java
@Configuration
@EnableFeignClients(
    clients = {
        CategoryApi.class  // 添加分类服务API
    },
    basePackages = {}
)
public class RpcConfiguration {
}
```

### 2. 创建分类服务类

参考 `EmergencyPlanCategoryService`，创建自己的分类服务类：

```java
@Service
public class YourBusinessCategoryService {
    
    private static final String BUSINESS_TYPE_CODE = "your_business_type";
    
    @Autowired
    private CategoryApi categoryApi;
    
    // 实现分类管理方法...
}
```

### 3. 使用分类服务

在业务代码中注入并使用：

```java
@Autowired
private YourBusinessCategoryService categoryService;

// 使用分类功能...
```

## 注意事项

1. **业务类型编码唯一性**: 每个业务模块必须使用唯一的 `businessTypeCode`
2. **服务可用性**: 如果 System 服务不可用，会抛出 `CATEGORY_SERVICE_UNAVAILABLE` 异常
3. **数据隔离**: 不同 `businessTypeCode` 的分类数据完全隔离
4. **缓存**: System 模块会自动缓存分类树，提高查询性能

## 优势

1. **代码复用**: 分类功能只需在 System 模块实现一次
2. **统一管理**: 所有分类数据统一存储和管理
3. **易于扩展**: 新业务模块只需配置 Feign 客户端即可使用
4. **数据隔离**: 通过 `businessTypeCode` 实现数据隔离
5. **性能优化**: 统一的缓存策略

