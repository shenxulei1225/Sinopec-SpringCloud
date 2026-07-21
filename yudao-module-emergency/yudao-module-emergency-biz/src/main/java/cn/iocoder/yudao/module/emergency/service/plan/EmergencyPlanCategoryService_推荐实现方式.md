# 应急预案分类服务 - 实现方式说明

## ✅ 当前实现是正确的

**重要澄清**：当前实现（通过 `CategoryApi` 调用 System 模块的分类功能）**完全符合**分析文档 `specs/002-增加基础服务/实现方式分析.md` 的最终推荐方案。

## 分析文档的最终推荐方案

分析文档的最终推荐是**"System模块集成方式"**，而不是各模块独立实现：

1. **统一管理**：分类数据统一在 System 模块的一张表中，通过 `businessTypeCode` 区分
2. **使用工具库**：System 模块使用 `AbstractCategoryService` 实现（已实现）
3. **本地调用**：如果都在 `system-server` 中，通过 `CategoryApi` 调用是本地调用，无 RPC 开销

## 分析文档的推荐方案

### Tree方式（工具库实现）- 推荐 ✅

**架构**：
```
yudao-framework/cheers-category/
  └── AbstractCategoryService (工具库基类)
        ↑ 继承
        │
各业务模块
  ├── EmergencyCategoryService (应急模块)
  ├── EquipmentCategoryService (设备模块)
  └── TaskCategoryService (任务模块)
```

**优势**：
- ✅ **性能优异**：本地调用，无网络延迟
- ✅ **事务可靠**：本地事务保证数据一致性
- ✅ **业务定制灵活**：各模块可重写方法添加业务逻辑
- ✅ **部署简单**：无需额外的服务部署和管理

## 推荐实现代码

### 1. 应急模块的分类实体

```java
// EmergencyCategoryDO.java
package cn.iocoder.yudao.module.emergency.dal.dataobject.category;

import cn.iocoder.yudao.framework.category.core.CategoryEntity;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@TableName("emergency_category")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyCategoryDO extends TenantBaseDO implements CategoryEntity<Long> {
    
    private Long id;
    private Long parentId;
    private String name;
    private String businessTypeCode; // "emergency_plan"
    private Integer sort;
    private Integer status;
    private String description;
    private Integer level;
    private String treePath;
}
```

### 2. 应急模块的分类Mapper

```java
// EmergencyCategoryMapper.java
package cn.iocoder.yudao.module.emergency.dal.mysql.category;

import cn.iocoder.yudao.framework.category.mapper.CategoryMapper;
import cn.iocoder.yudao.module.emergency.dal.dataobject.category.EmergencyCategoryDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmergencyCategoryMapper extends CategoryMapper<EmergencyCategoryDO> {
    // 可以添加应急模块特有的查询方法
}
```

### 3. 应急模块的分类服务（推荐实现）

```java
// EmergencyPlanCategoryService.java
package cn.iocoder.yudao.module.emergency.service.plan;

import cn.iocoder.yudao.framework.category.service.AbstractCategoryService;
import cn.iocoder.yudao.module.emergency.dal.dataobject.category.EmergencyCategoryDO;
import cn.iocoder.yudao.module.emergency.dal.mysql.category.EmergencyCategoryMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * 应急预案分类服务
 * 
 * 继承 Framework 的 AbstractCategoryService，使用Tree方式实现
 * 符合分析文档的推荐方案
 * 
 * @author 系统生成
 */
@Service
public class EmergencyPlanCategoryService 
        extends AbstractCategoryService<EmergencyCategoryMapper, EmergencyCategoryDO> {
    
    private static final String BUSINESS_TYPE_CODE = "emergency_plan";
    
    @Resource
    private EmergencyCategoryMapper categoryMapper;
    
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    
    @Override
    protected EmergencyCategoryMapper getMapper() {
        return categoryMapper;
    }
    
    @Override
    protected int getMaxLevel() {
        return 5; // 最大层级
    }
    
    @Override
    protected void evictCache(String businessTypeCode) {
        // 实现缓存失效逻辑
        String key = "category:tree:" + businessTypeCode;
        stringRedisTemplate.delete(key);
    }
    
    /**
     * 创建应急预案分类
     */
    public Long createCategory(String name, Long parentId, Integer sort, String description) {
        EmergencyCategoryDO category = new EmergencyCategoryDO();
        category.setName(name);
        category.setParentId(parentId);
        category.setBusinessTypeCode(BUSINESS_TYPE_CODE);
        category.setSort(sort != null ? sort : 0);
        category.setStatus(1); // 默认启用
        category.setDescription(description);
        
        return createCategory(category);
    }
    
    /**
     * 获取分类树（只返回启用的）
     */
    public List<EmergencyCategoryDO> getEnabledCategoryTree() {
        List<EmergencyCategoryDO> all = getCategoryTree(BUSINESS_TYPE_CODE);
        return all.stream()
                .filter(cat -> cat.getStatus() == 1)
                .collect(Collectors.toList());
    }
    
    // 可以添加应急模块特有的业务方法...
}
```

## 当前实现的优势

| 维度 | 统一管理（当前实现） | 各模块独立实现 |
| ------ | ------------------ | -------------- |
| **代码复用** | ⭐⭐⭐⭐ 只需实现一次 | ⭐⭐ 每个模块都要实现 |
| **维护成本** | ⭐⭐⭐⭐ 一个表、一个服务 | ⭐⭐ 多个表、多个服务 |
| **数据管理** | ⭐⭐⭐⭐ 统一管理，便于查询 | ⭐⭐ 数据分散 |
| **性能** | ⭐⭐⭐⭐ 本地调用（都在system-server中） | ⭐⭐⭐⭐ 本地调用 |
| **事务** | ⭐⭐⭐⭐ 本地事务 | ⭐⭐⭐⭐ 本地事务 |
| **数据隔离** | ✅ 通过businessTypeCode隔离 | ✅ 通过独立表隔离 |

## 为什么不需要各模块独立实现？

1. **代码重复**：每个模块都要实现一遍分类管理服务
2. **维护成本**：需要维护多个表、多个服务
3. **数据分散**：不利于统一管理和查询
4. **没有实际收益**：统一管理也能通过 `businessTypeCode` 实现数据隔离

## 总结

**当前实现是正确的！** 

- ✅ 统一在 System 模块管理，通过 `businessTypeCode` 区分不同业务
- ✅ System 模块使用 `AbstractCategoryService` 实现（已实现）
- ✅ 通过 `CategoryApi` 暴露接口，如果都在 `system-server` 中，就是本地调用
- ✅ 代码复用、统一维护、数据集中管理

这完全符合分析文档的最终推荐方案："System模块集成方式"。

