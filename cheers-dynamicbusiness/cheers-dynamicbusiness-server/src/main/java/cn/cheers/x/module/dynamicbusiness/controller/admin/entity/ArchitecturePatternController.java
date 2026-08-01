package cn.cheers.x.module.dynamicbusiness.controller.admin.entity;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryTreeRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityCategoryAssociationRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityCategoryDisassociationReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityCategoryQueryReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntitySceneQueryRespVO;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryService;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityService;
import cn.cheers.x.module.dynamicbusiness.service.entity.query.CategoryModelEntityQueryService;
import cn.cheers.x.module.dynamicbusiness.service.entity.relation.EntityCategoryRelationService;
import cn.cheers.x.module.dynamicbusiness.enums.entity.EntityQueryScene;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.category.CategoryMapper;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryDO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 4 种架构模式通用 API Controller
 * 
 * <p>提供 4 种架构模式的通用查询 API,实现零代码查询能力。</p>
 * 
 * <h3>4 种架构模式</h3>
 * <ul>
 *   <li>模式A:灵活分类视图 - 分类和实体之间的关联是灵活的、可配置的,不依赖模型</li>
 *   <li>模式B:模型分类视图 - 通过模型自动分类,实体通过model_id关联到模型,模型通过ModelCategoryRelation关联到分类</li>
 *   <li>模式C:分类实体视图 - 分类本身也是实体(isEntity=true),可以展示分类详情和关联实体</li>
 *   <li>模式D:实体关联视图 - 实体之间的直接关联关系,支持跨业务类型关联</li>
 * </ul>
 * 
 * <h3>需求引用</h3>
 * <ul>
 *   <li>FR-056: 模式A - 系统必须提供按分类编码获取分类树的 API</li>
 *   <li>FR-057: 模式B - 系统必须提供按分类获取 Model 列表的 API</li>
 *   <li>FR-059: 模式C - 系统必须提供获取 Entity 树结构的 API</li>
 *   <li>FR-060: 模式D - 系统必须提供获取 Entity 关联关系的 API</li>
 * </ul>
 * 
 * @author 扩展字段查询服务
 */
@Tag(name = "管理后台 - 架构模式通用 API", description = "提供 4 种架构模式的通用查询 API,实现零代码查询能力")
@RestController
@RequestMapping("/dynamicbusiness/architecture")
@Validated
public class ArchitecturePatternController {

    @Resource
    private CategoryService categoryService;

    @Resource
    private EntityService entityService;

    @Resource
    private EntityCategoryRelationService entityCategoryRelationService;

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private CategoryModelEntityQueryService categoryModelEntityQueryService;

    // ==================== Category 分类树(通用) ====================

    @GetMapping("/category/tree")
    @Operation(
        summary = "获取分类树",
        description = """
            获取指定分类维度下的分类树结构。

            **使用场景**:
            - 模式A(灵活分类视图):左侧分类树
            - 模式B(模型分类视图):左侧分类树
            - 模式C(分类实体视图):左侧分类树(分类本身也是实体)
            - 通用场景:设备类型分类树、区域分类树等

            **返回结构**:
            - 树形结构,包含所有层级的分类
            - 每个节点包含 id、name、code、children 等信息
            """
    )
    @Parameter(name = "categoryTypeCode", description = "分类类型编码(分类维度)", required = true, example = "equipment")
    @Parameter(name = "status", description = "状态(可选,1-启用,0-禁用)", example = "1")
    @PreAuthorize("@ss.hasPermission('system:category:query')")
    public CommonResult<List<CategoryTreeRespVO>> getCategoryTree(
            @RequestParam("categoryTypeCode") String categoryTypeCode,
            @RequestParam(value = "status", required = false) Integer status) {
        return success(categoryService.getCategoryTreeByType(categoryTypeCode, status));
    }

    // ==================== 模式B:Category-Model-Entity ====================

    // ==================== 实体查询接口已迁移至 EntityController ====================

    // ==================== 模式D:Entity-Entity 关联 ====================

    // 注意:模式D 的关联查询功能需要 EntityRelationService,
    // 该服务在业务关联管理模块(任务 15)中实现。
    // 这里先提供基于 customFields 中 ENTITY_REF 字段的简单关联查询。

    // ==================== Entity-Category 关联(支持模式A/C) ====================

    @PostMapping("/entity/categories/query")
    @Operation(
        summary = "【模式A/C】获取 Entity 关联的分类",
        description = """
            获取指定 Entity 关联的所有分类。

            **使用场景**:
            - 模式A:分类-实体模式,查看实体属于哪些分类
            - 模式C:分类实体视图,查看实体关联的分类
            - 一个 Entity 可以关联多个 Category

            **返回信息**:
            - 关联的分类列表
            - 每个分类包含 id、name、code、path 等信息
            """
    )
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<List<CategoryRespVO>> getEntityCategories(
            @Valid @RequestBody EntityCategoryQueryReqVO reqVO) {
        Long id = reqVO.getEntityId();
        // 获取 Entity 关联的分类ID列表
        List<Long> categoryIds = entityCategoryRelationService.listCategoryIdsByEntityId(id, null);
        if (categoryIds.isEmpty()) {
            return success(List.of());
        }
        
        // 批量查询分类详情(通过分类ID,从DO中获取categoryTypeCode后调用getCategoryVO)
        List<CategoryRespVO> categories = new ArrayList<>();
        for (Long categoryId : categoryIds) {
            try {
                // 通过分类ID查询分类DO获取categoryTypeCode
                CategoryDO categoryDO = categoryMapper.selectById(categoryId);
                if (categoryDO != null && categoryDO.getCategoryTypeCode() != null) {
                    CategoryRespVO vo = categoryService.getCategoryVO(categoryId);
                    categories.add(vo);
                }
            } catch (Exception e) {
                // 如果查询失败,跳过该分类(可能已被删除)
            }
        }
        return success(categories);
    }

    @PostMapping("/entity/category-ids/query")
    @Operation(
        summary = "【模式A/C】获取 Entity 关联的分类ID列表",
        description = """
            获取指定 Entity 关联的所有分类ID。

            **使用场景**:
            - 前端表单回显时获取已关联的分类ID
            - 批量操作前获取当前关联状态
            """
    )
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<List<Long>> getEntityCategoryIds(
            @Valid @RequestBody EntityCategoryQueryReqVO reqVO) {
        return success(entityCategoryRelationService.listCategoryIdsByEntityId(reqVO.getEntityId(), null));
    }

    @PostMapping("/entity/categories/associate")
    @Operation(
        summary = "【模式A】批量关联 Entity 到多个分类(追加模式)",
        description = """
            将指定 Entity 关联到多个分类。

            **使用场景**:
            - 模式A:为实体添加多个分类标签
            - 已存在的关联会被跳过,不会重复创建

            **说明**:
            - 追加模式:保留实体原有的分类关联,新增分类关联
            - 适用于模式A:实体可以同时属于多个分类

            **返回值**:返回详细的操作结果,包括成功/失败数量和失败原因
            """
    )
    @Parameter(name = "entityId", description = "实体ID(必填)", required = true, example = "1001")
    @Parameter(name = "entityTypeCode", description = "业务类型编码(必填,如 'equipment'、'region'),用于验证实体存在性", required = true, example = "equipment")
    @PreAuthorize("@ss.hasPermission('system:entity:update')")
    public CommonResult<EntityCategoryAssociationRespVO> batchAssociateEntityToCategories(
            @RequestParam("entityId") Long entityId,
            @RequestParam("entityTypeCode") String entityTypeCode,
            @RequestBody List<Long> categoryIds) {
        return success(entityCategoryRelationService.batchAssociateEntityToCategories(
                entityId, 
                categoryIds, 
                entityTypeCode));
    }

    @PostMapping("/entity/categories/disassociate")
    @Operation(
        summary = "【模式A/C】批量取消 Entity 与多个分类的关联",
        description = """
            取消指定 Entity 与多个分类的关联。

            **使用场景**:
            - 移除实体的多个分类标签
            """
    )
    @PreAuthorize("@ss.hasPermission('system:entity:update')")
    public CommonResult<Boolean> batchDisassociateEntityFromCategories(
            @RequestParam("entityTypeCode") String entityTypeCode,
            @Valid @RequestBody EntityCategoryDisassociationReqVO reqVO) {
        entityCategoryRelationService.batchDisassociateEntityFromCategories(
                reqVO.getEntityId(), 
                reqVO.getCategoryIds(),
                entityTypeCode);
        return success(true);
    }

    @PutMapping("/entity/categories/replace")
    @Operation(
        summary = "【模式C】替换 Entity 的所有分类关联",
        description = """
            替换指定 Entity 的所有分类关联(先删除旧关联,再添加新关联)。

            **使用场景**:
            - 模式C:分类实体视图的关联操作
            - 编辑实体时重新设置所有分类
            - 传入空列表会清除所有分类关联

            **说明**:
            - 替换模式:先删除实体原有的所有分类关联,再添加新的分类关联
            - 适用于模式C:实体只属于一个分类的场景

            **返回值**:返回详细的操作结果,包括成功/失败数量和失败原因
            """
    )
    @Parameter(name = "entityId", description = "实体ID(必填)", required = true, example = "1001")
    @Parameter(name = "entityTypeCode", description = "业务类型编码(必填,如 'equipment')", required = true, example = "equipment")
    @PreAuthorize("@ss.hasPermission('system:entity:update')")
    public CommonResult<EntityCategoryAssociationRespVO> replaceEntityCategories(
            @RequestParam("entityId") Long entityId,
            @RequestParam("entityTypeCode") String entityTypeCode,
            @RequestBody List<Long> categoryIds) {
        return success(entityCategoryRelationService.updateAssociation(
                entityId, 
                categoryIds, 
                entityTypeCode));
    }

    @GetMapping("/entity/by-category")
    @Operation(
        summary = "【模式A/C】按单个分类查询 Entity(支持分页)",
        description = """
            查询指定分类及其所有子分类下的所有 Entity,支持按 Model 过滤和分页。

            **核心特性**:
            - ✅ 单个分类查询(categoryId)
            - ✅ 自动包含所有子分类
            - ✅ 支持按 Model 过滤(modelId)
            - ✅ 支持分页查询(pageNo、pageSize)
            - ✅ 支持跨业务类型查询(contentEntityTypeCode)

            **使用场景**:
            - 模式A(灵活分类视图):点击左侧分类树节点,右侧显示该分类下的实体列表(分页)
            - 模式C(分类实体视图):点击分类节点,显示关联的实体列表(分页)
            - 需要分页展示大量实体数据的场景
            - 需要按 Model 进一步过滤实体的场景

            **与 /entity/by-categories 的区别**:
            - 本接口:单分类查询 + 分页支持 + Model 过滤
            - /entity/by-categories:多分类查询 + AND/OR 逻辑 + 不支持分页

            **子分类处理**:
            - 自动递归包含该分类的所有子分类
            - 查询这些分类(包括子分类)关联的所有实体

            **分页说明**:
            - 默认页码:1
            - 默认每页条数:10
            - 分页在内存中处理,适用于中小数据量场景
            """
    )
    @Parameter(name = "categoryId", description = "分类 ID(必填,会自动包含所有子分类)", required = true, example = "100")
    @Parameter(name = "contentEntityTypeCode", description = "内容业务类型编码(必填,用于跨业务类型查询)", required = true, example = "equipment")
    @Parameter(name = "modelId", description = "Model ID(可选,用于进一步过滤实体)", example = "1")
    @Parameter(name = "pageNo", description = "页码(可选,默认1)", example = "1")
    @Parameter(name = "pageSize", description = "每页条数(可选,默认10)", example = "10")
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<List<EntityRespVO>> getEntityByCategory(
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("contentEntityTypeCode") String contentEntityTypeCode,
            @RequestParam(value = "modelId", required = false) Long modelId,
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        EntitySceneQueryRespVO resp = entityService.queryEntities(
                EntityQueryScene.ENTITIES_BY_CATEGORY,
                "PAGE",
                "FULL",
                contentEntityTypeCode,
                contentEntityTypeCode,
                modelId == null ? null : java.util.List.of(modelId),
                null,
                List.of(categoryId),
                null,
                null,
                null,
                null,
                contentEntityTypeCode,
                pageNo,
                pageSize,
                null,
                null,
                null,
                null,
                null,
                null
        );
        return success(resp.getPage() == null ? java.util.List.of() : resp.getPage().getList());
    }

    @GetMapping("/entity/by-categories")
    @Operation(
        summary = "【模式A/C】按多个分类查询 Entity(多维度筛选,AND/OR 逻辑)",
        description = """
            按多个分类ID查询 Entity,支持 AND/OR 逻辑组合,每个分类会自动包含其所有子分类。

            **核心特性**:
            - ✅ 多个分类查询(categoryIds 列表)
            - ✅ 每个分类自动包含所有子分类
            - ✅ 支持 AND/OR 逻辑(matchAll 参数)
            - ✅ 支持跨业务类型查询(contentEntityTypeCode)
            - ❌ 不支持分页(返回全部结果)

            **使用场景**:
            - 多维度筛选:同时按设备类型、品牌、区域等多个分类维度筛选实体
            - 复杂查询条件:需要实体同时满足多个分类条件(AND)或满足任一条件(OR)
            - 数据量较小的场景:由于不支持分页,适合返回结果较少的场景

            **AND/OR 逻辑说明**:
            - **matchAll=false(OR 模式,默认)**:实体关联任意一个分类即可
              - 示例:查询分类 [100, 101, 102] 下的实体
              - 结果:返回关联分类 100 或 101 或 102 的所有实体(并集)
            - **matchAll=true(AND 模式)**:实体必须同时关联所有指定分类
              - 示例:查询分类 [100, 101, 102] 下的实体
              - 结果:返回同时关联分类 100 和 101 和 102 的实体(交集)

            **与 /entity/by-category 的区别**:
            - 本接口:多分类查询 + AND/OR 逻辑 + 不支持分页
            - /entity/by-category:单分类查询 + 分页支持 + Model 过滤

            **子分类处理**:
            - 每个分类ID会自动递归展开为其所有子分类
            - 查询这些分类(包括子分类)关联的所有实体
            - AND/OR 逻辑基于展开后的分类集合进行计算

            **性能提示**:
            - 由于不支持分页,建议在分类数量较少或预期结果较少的场景使用
            - 如果结果集较大,建议使用 /entity/by-category 接口进行分页查询
            """
    )
    @Parameter(name = "categoryIds", description = "分类 ID 列表(必填,多个分类ID,每个分类会自动包含其所有子分类)", required = true, example = "100,101,102")
    @Parameter(name = "contentEntityTypeCode", description = "内容业务类型编码(必填,用于跨业务类型查询)", required = true, example = "equipment")
    @Parameter(name = "matchAll", description = "是否要求匹配所有分类(可选,默认false=OR模式,true=AND模式)", example = "false")
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<List<EntityRespVO>> getEntitiesByCategoryIds(
            @RequestParam("categoryIds") List<Long> categoryIds,
            @RequestParam(value = "matchAll", defaultValue = "false") Boolean matchAll,
            @RequestParam("contentEntityTypeCode") String contentEntityTypeCode) {
        java.util.Set<Long> allCategoryIds = expandCategoryIdsIncludingChildren(categoryIds);
        java.util.List<java.util.List<Long>> entityIdsPerCategory = allCategoryIds.stream()
                .map(id -> entityCategoryRelationService.listEntityIdsByCategoryIdOnly(id, contentEntityTypeCode))
                .toList();
        java.util.Set<Long> entityIds = aggregateEntityIds(entityIdsPerCategory, Boolean.TRUE.equals(matchAll));
        java.util.List<EntityRespVO> entities = entityIds.stream()
                .map(id -> entityService.get(id, contentEntityTypeCode))
                .filter(java.util.Objects::nonNull)
                .toList();
        return success(entities);
    }

    private java.util.Set<Long> expandCategoryIdsIncludingChildren(java.util.List<Long> categoryIds) {
        java.util.Set<Long> allCategoryIds = new java.util.HashSet<>();
        for (Long categoryId : categoryIds) {
            CategoryDO categoryDO = categoryMapper.selectById(categoryId);
            if (categoryDO != null && categoryDO.getCategoryTypeCode() != null) {
                java.util.List<Long> categoryIdsWithChildren = categoryService.getAllCategoryIdsIncludingChildren(
                        categoryId, categoryDO.getCategoryTypeCode());
                allCategoryIds.addAll(categoryIdsWithChildren);
            } else {
                allCategoryIds.add(categoryId);
            }
        }
        return allCategoryIds;
    }

    private java.util.Set<Long> aggregateEntityIds(java.util.List<java.util.List<Long>> entityIdsPerCategory, boolean matchAll) {
        if (entityIdsPerCategory.isEmpty()) {
            return java.util.Set.of();
        }
        java.util.Set<Long> entityIds = new java.util.HashSet<>();
        if (matchAll) {
            entityIds.addAll(entityIdsPerCategory.get(0));
            for (int i = 1; i < entityIdsPerCategory.size(); i++) {
                entityIds.retainAll(entityIdsPerCategory.get(i));
            }
            return entityIds;
        }
        for (java.util.List<Long> ids : entityIdsPerCategory) {
            entityIds.addAll(ids);
        }
        return entityIds;
    }
}