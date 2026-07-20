# PlanGroup 重构计划

## 问题说明

PlanGroup 已经使用系统的 `system_category` 表实现，但代码中仍保留了完整的 PlanGroup 实现，造成代码冗余。应该直接使用 `CategoryCommonApi` 来操作分类数据。

## 业务类型编码

- **预案分组**: `emergency_plan_group` (已在 PlanGroupServiceImpl 中使用)

## 需要删除的文件

### 1. Service 层
- ✅ `PlanGroupService.java` - 接口
- ✅ `PlanGroupServiceImpl.java` - 实现类

### 2. Mapper 层
- ✅ `PlanGroupMapper.java` - Mapper 接口
- ✅ `PlanGroupMapper.xml` - MyBatis XML 映射文件

### 3. DO 层
- ✅ `PlanGroupDO.java` - 数据对象（使用 system_category 表）

### 4. Controller 层
- ✅ `PlanGroupController.java` - 控制器

### 5. VO 层
- ✅ `PlanGroupCreateReqVO.java` - 创建请求 VO
- ✅ `PlanGroupUpdateReqVO.java` - 更新请求 VO
- ✅ `PlanGroupRespVO.java` - 响应 VO
- ✅ `PlanGroupTreeRespVO.java` - 树形响应 VO

### 6. 测试文件
- ✅ `PlanGroupServiceIntegrationTest.java` - 集成测试

## 需要修改的文件

### 1. EmergencyPlanServiceImpl.java
- 移除 `PlanGroupService` 依赖
- 使用 `CategoryCommonApi` 替代 `planGroupService.getPlanGroup()`
- 使用 `EmergencyPlanCategoryService` 或直接使用 `CategoryCommonApi`

### 2. EmergencyPlanController.java
- 检查是否有 PlanGroup 相关的接口（应该没有）

### 3. PlanVersionServiceImpl.java
- 检查是否有 PlanGroup 相关引用（应该没有）

### 4. 其他测试文件
- `EmergencyPlanServiceOptionalFieldsTest.java` - 移除 PlanGroup 相关测试
- `EmergencyEventServiceImplTest.java` - 检查是否有 PlanGroup 引用

## 重构步骤

1. ✅ 修改 `EmergencyPlanServiceImpl.java`，使用 `CategoryCommonApi` 替代 `PlanGroupService`
2. ✅ 删除所有 PlanGroup 相关的文件
3. ✅ 更新测试文件（修复 `EmergencyEventServiceImplTest.java` 中的引用）
4. ✅ 验证编译无错误

## 重构完成总结

### 已删除的文件（11个）

1. ✅ `PlanGroupService.java` - Service 接口
2. ✅ `PlanGroupServiceImpl.java` - Service 实现
3. ✅ `PlanGroupMapper.java` - Mapper 接口
4. ✅ `PlanGroupMapper.xml` - MyBatis XML 映射
5. ✅ `PlanGroupDO.java` - 数据对象
6. ✅ `PlanGroupController.java` - 控制器
7. ✅ `PlanGroupCreateReqVO.java` - 创建请求 VO
8. ✅ `PlanGroupUpdateReqVO.java` - 更新请求 VO
9. ✅ `PlanGroupRespVO.java` - 响应 VO
10. ✅ `PlanGroupTreeRespVO.java` - 树形响应 VO
11. ✅ `PlanGroupServiceIntegrationTest.java` - 集成测试

### 已修改的文件（2个）

1. ✅ `EmergencyPlanServiceImpl.java`
   - 移除 `PlanGroupService` 依赖
   - 添加 `CategoryCommonApi` 依赖
   - 使用 `CategoryCommonApi.getCategory()` 替代 `planGroupService.getPlanGroup()`
   - 使用 `CategoryCommonApi.existsCategory()` 验证分组存在

2. ✅ `EmergencyEventServiceImplTest.java`
   - 移除 `PlanGroupServiceImpl.class` 的导入引用

### 重构后的代码示例

**EmergencyPlanServiceImpl.java 关键修改：**

```java
// 修改前
@Resource
private PlanGroupService planGroupService;

PlanGroupDO planGroup = planGroupService.getPlanGroup(plan.getPlanType().longValue());
String planTypeName = planGroup != null ? planGroup.getName() : null;

// 修改后
@Resource
private CategoryCommonApi categoryApi;

private static final String PLAN_GROUP_BUSINESS_TYPE = "emergency_plan_group";

CommonResult<Map<String, Object>> categoryResult = categoryApi.getCategory(
    plan.getPlanType().longValue(), 
    PLAN_GROUP_BUSINESS_TYPE
);
String planTypeName = null;
if (categoryResult.isSuccess() && categoryResult.getData() != null) {
    planTypeName = (String) categoryResult.getData().get("name");
}
```

### 注意事项

1. **业务类型编码**：使用 `emergency_plan_group` 作为业务类型编码
2. **数据兼容性**：`EmergencyPlanDO.planGroupId` 字段保持不变，它引用的是 `system_category` 表的 ID
3. **API 调用**：通过 `CategoryCommonApi` 调用 System 模块的分类功能
4. **错误处理**：添加了适当的错误处理和日志记录

## 重构代码示例

### EmergencyPlanServiceImpl.java 修改示例

**修改前：**
```java
@Resource
private PlanGroupService planGroupService;

// 使用
PlanGroupDO planGroup = planGroupService.getPlanGroup(plan.getPlanType().longValue());
String planTypeName = planGroup != null ? planGroup.getName() : null;
```

**修改后：**
```java
@Resource
private CategoryCommonApi categoryApi;

private static final String PLAN_GROUP_BUSINESS_TYPE = "emergency_plan_group";

// 使用
Map<String, Object> category = categoryApi.getCategory(
    plan.getPlanType().longValue(), 
    PLAN_GROUP_BUSINESS_TYPE
).getData();
String planTypeName = category != null ? (String) category.get("name") : null;
```

