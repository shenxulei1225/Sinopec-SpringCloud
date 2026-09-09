package cn.cheers.x.module.dynamicbusiness.controller.admin.model;

import cn.cheers.x.framework.apilog.core.annotation.ApiAccessLog;
import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.CategoryIdGroupReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelAvailableFieldRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelCloneReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelDomainChangePreviewRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelFacilityFootprintRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelPageReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelPromoteLocalPackageReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelSortSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelFromEntityCategoryGroupsReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelFromModelCategoryGroupsReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelFieldFilterReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelListBySceneReqVO;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityService;
import cn.cheers.x.module.dynamicbusiness.service.model.ModelFacilityFootprintQueryService;
import cn.cheers.x.module.dynamicbusiness.service.model.ModelService;
import cn.cheers.x.module.dynamicbusiness.service.model.governance.LocalPackagePromotionService;
import cn.cheers.x.module.dynamicbusiness.service.model.relation.ModelCategoryRelationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static cn.cheers.x.framework.apilog.core.enums.OperateTypeEnum.CREATE;
import static cn.cheers.x.framework.apilog.core.enums.OperateTypeEnum.UPDATE;
import static cn.cheers.x.framework.apilog.core.enums.OperateTypeEnum.DELETE;
import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 业务模型 Controller
 * 
 * Model（模型/品类）用于定义"一类东西长什么样"，是字段规则和结构的承载者。
 * Model 的 code 字段由系统自动生成（格式：MODEL-{UUID}），name 在同一租户内必须唯一。
 * Model 创建后，需要分配字段才能用于 Entity。
 * Model 可以关联多个 Category，用于归类与筛选，但 Category 不影响字段规则。
 * 
 * @author yudao
 */
@Tag(name = "管理后台 - 业务模型管理", description = "提供模型的创建、更新、删除、查询等功能。模型用于定义一类东西的字段规则和结构，是实体的模板")
@RestController
@RequestMapping("/dynamicbusiness/business/models")
@Validated
public class ModelController {

    @Resource
    private ModelService modelService;

    @Resource
    private ModelCategoryRelationService modelCategoryRelationService;

    @Resource
    private EntityService entityService;

    @Resource
    private LocalPackagePromotionService localPackagePromotionService;

    @Resource
    private ModelFacilityFootprintQueryService modelFacilityFootprintQueryService;

    @PostMapping("/create")
    @Operation(
        summary = "创建业务模型",
        description = "创建一个新的业务模型/品类，用于定义一类东西长什么样，是字段规则和结构的承载者。\n" +
            "- Model 的 code 字段由系统自动生成（格式：MODEL-{UUID}）\n" +
            "- name 在同一租户内必须唯一\n" +
            "- Model 创建后，需要分配字段才能用于 Entity\n" +
            "- 可以通过 ModelFieldAssignmentController 为模型字段"
    )
    @ApiAccessLog(operateType = CREATE)
    @PreAuthorize("@ss.hasPermission('system:model:create')")
    /**
     * 用途：创建模型。
     * Service 映射：{@link ModelService#createModel(ModelCreateReqVO)}。
     * 边界：只负责创建模型主数据，不负责字段分配与分类关系维护。
     */
    public CommonResult<Long> createModel(@Valid @RequestBody ModelCreateReqVO reqVO) {
        return success(modelService.createModel(reqVO));
    }

    @PostMapping("/clone")
    @Operation(
        summary = "复制业务模型",
        description = "基于已有模型复制一份新模型：拷贝字段分组、字段分配（含规则）与分类挂接，并重建 CRUD 表单。\n" +
            "- 不复制实体实例\n" +
            "- 不复制模型间关联定义\n" +
            "- 新模型 code 重新生成；名称须在同业务类型下唯一"
    )
    @ApiAccessLog(operateType = CREATE)
    @PreAuthorize("@ss.hasPermission('system:model:create')")
    public CommonResult<Long> cloneModel(@Valid @RequestBody ModelCloneReqVO reqVO) {
        return success(modelService.cloneModel(reqVO));
    }

    @PutMapping("/update")
    @Operation(
        summary = "更新业务模型",
        description = "更新业务模型的基本信息（名称、描述、状态等）。\n" +
            "- 更新不会影响已分配的字段\n" +
            "- 更新不会影响已创建的实体\n" +
            "- 支持绑定分类（多对多关系，一个模型可以绑定多个分类）：\n" +
            "  - 如果传入 categoryIds 列表，会先删除该模型的所有分类关联，然后批量创建新的关联关系（完全替换）\n" +
            "  - 如果传入 categoryId（兼容旧版），会添加该分类绑定（不删除现有绑定，增量添加）"
    )
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:model:update')")
    /**
     * 用途：更新模型基本信息。
     * Service 映射：{@link ModelService#updateModel(ModelUpdateReqVO)}。
     * 边界：以 Service 的更新策略为准（包括分类关系同步策略）。
     */
    public CommonResult<Boolean> updateModel(@Valid @RequestBody ModelUpdateReqVO reqVO) {
        modelService.updateModel(reqVO);
        return success(true);
    }

    @GetMapping("/preview-domain-change")
    @Operation(
        summary = "预览型号业务域迁移影响",
        description = "把型号拖到另一个业务域分组后、提交之前调用。\n" +
            "- 返回该型号下会被连带改写业务域的实体数与分类关联数\n" +
            "- 有存量实体时前端须展示数量并二次确认\n" +
            "- targetDomain 留空表示移出业务域（未划域）"
    )
    @Parameter(name = "modelId", description = "型号编号", required = true, example = "1")
    @Parameter(name = "targetDomain", description = "目标业务域；留空表示未划域", example = "巡检")
    @PreAuthorize("@ss.hasPermission('system:model:update')")
    /**
     * 用途：型号业务域迁移前的影响预览。
     * Service 映射：{@link ModelService#previewDomainChange(Long, String)}。
     * 边界：只读，不改动任何数据。
     */
    public CommonResult<ModelDomainChangePreviewRespVO> previewDomainChange(
            @RequestParam("modelId") Long modelId,
            @RequestParam(value = "targetDomain", required = false) String targetDomain) {
        return success(modelService.previewDomainChange(modelId, targetDomain));
    }

    @PutMapping("/change-domain")
    @Operation(
        summary = "迁移型号到目标业务域",
        description = "在同一事务内更新型号、该型号下全部实体、以及这些实体的全部分类关联的业务域。\n" +
            "- 业务域权威链为「型号 → 实体 → 分类关联」，任一步失败整体回滚\n" +
            "- targetDomain 留空表示移出业务域（未划域）\n" +
            "- 返回本次实际影响的实体数与分类关联数"
    )
    @Parameter(name = "modelId", description = "型号编号", required = true, example = "1")
    @Parameter(name = "targetDomain", description = "目标业务域；留空表示未划域", example = "巡检")
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:model:update')")
    /**
     * 用途：型号业务域迁移（含级联）。
     * Service 映射：{@link ModelService#changeDomain(Long, String)}。
     * 边界：不改型号其它字段；业务域是否登记由 Service 校验。
     */
    public CommonResult<ModelDomainChangePreviewRespVO> changeDomain(
            @RequestParam("modelId") Long modelId,
            @RequestParam(value = "targetDomain", required = false) String targetDomain) {
        return success(modelService.changeDomain(modelId, targetDomain));
    }

    @DeleteMapping("/delete")
    @Operation(
        summary = "删除业务模型",
        description = "删除业务模型（软删）。\n" +
            "- 公司规格与本地型号均可删除\n" +
            "- 删除前检查是否有业务实体占用，存在则禁止删除\n" +
            "- 删除会同时删除模型与字段的分配关系、模型与分类的关联关系\n" +
            "- 本地型号须传 effectiveFacilityId（发起站场）"
    )
    @Parameter(name = "id", description = "模型编号", required = true, example = "1")
    @Parameter(name = "effectiveFacilityId", description = "当前有效站场（删除本地型号时必填）", example = "10")
    @ApiAccessLog(operateType = DELETE)
    @PreAuthorize("@ss.hasPermission('system:model:delete')")
    /**
     * 用途：删除模型（软删）。
     * Service 映射：{@link ModelService#deleteModel(Long, Long)}。
     * 边界：是否允许删除由 Service 内部校验（实体占用、治理权限、关联清理）决定。
     */
    public CommonResult<Boolean> deleteModel(
            @RequestParam("id") Long id,
            @RequestParam(value = "effectiveFacilityId", required = false) Long effectiveFacilityId) {
        modelService.deleteModel(id, effectiveFacilityId);
        return success(true);
    }

    /**
     * @deprecated 停用已废弃，请使用 {@link #deleteModel(Long, Long)}。
     */
    @Deprecated
    @PutMapping("/deactivate-company")
    @Operation(summary = "停用公司规格（已废弃）", description = "已废弃：请改用删除接口。调用将返回业务错误。", deprecated = true)
    @Parameter(name = "id", description = "公司规格型号编号", required = true, example = "1")
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:model:update')")
    public CommonResult<Boolean> deactivateCompanyModel(@RequestParam("id") Long id) {
        modelService.deactivateCompanyModel(id);
        return success(true);
    }

    @PostMapping("/promote-local-package")
    @Operation(
        summary = "晋升本地型号包",
        description = "在同一事务内晋升本地型号及其本地字段。字段可直接晋升为公司字段，"
            + "也可按 fieldMergeMap 合并到既有公司字段；任一步失败整体回滚。"
    )
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:model:update')")
    public CommonResult<Boolean> promoteLocalPackage(
            @Valid @RequestBody ModelPromoteLocalPackageReqVO reqVO) {
        localPackagePromotionService.promoteLocalPackage(
                reqVO.getModelId(), reqVO.getFieldMergeMap());
        return success(true);
    }

    @GetMapping("/get")
    @Operation(
        summary = "获取业务模型详情",
        description = "根据模型ID获取业务模型的详细信息，包含：\n" +
            "- 基本信息（名称、描述、状态、业务类型编码等）\n" +
            "- 关联的分类信息\n" +
            "- 已分配的字段数量统计"
    )
    @Parameter(name = "id", description = "模型编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('system:model:query')")
    /**
     * 用途：查询单个模型详情。
     * Service 映射：{@link ModelService#getModel(Long, Long)}。
     */
    public CommonResult<ModelRespVO> getModel(
            @RequestParam("id") Long id,
            @RequestParam(value = "effectiveFacilityId", required = false) Long effectiveFacilityId) {
        return success(modelService.getModel(id, effectiveFacilityId));
    }

    @GetMapping("/{id}/facility-footprint")
    @Operation(
        summary = "查询型号设施覆盖范围",
        description = "按该型号实体行上的 facility_id 去重统计；不使用型号发起设施字段。"
    )
    @Parameter(name = "id", description = "型号编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('system:model:query')")
    public CommonResult<ModelFacilityFootprintRespVO> getFacilityFootprint(
            @PathVariable("id") Long id,
            @RequestParam(value = "effectiveFacilityId", required = false) Long effectiveFacilityId) {
        return success(modelFacilityFootprintQueryService.getFacilityFootprint(id, effectiveFacilityId));
    }

    @GetMapping("/find-by-category-in-business")
    @Operation(
        summary = "查找指定业务下分类关联的模型",
        description = "根据分类ID（单个或多个）和业务类型编码，查找该业务下与分类关联的模型列表（通过 ModelCategoryRelation 关联）。\n" +
            "- entityTypeCode 必填，按业务类型命中关系表索引\n" +
            "- categoryIds 优先；为空时回退 categoryId（兼容旧调用）\n" +
            "- includeDescendants 默认 true：合并各分类含子树的模型 ID；false 时仅精确匹配传入的分类 id（场景 9 前端已与关联白名单求交后使用）\n" +
            "- 聚合链路：先查 model_category_relation 的 modelId 列表，再批量查询 model 详情"
    )
    @Parameter(name = "categoryIds", description = "分类ID列表（优先；支持重复 query 参数或逗号分隔）", example = "1,2")
    @Parameter(name = "categoryId", description = "单个分类ID（兼容旧版；categoryIds 为空时使用）", example = "1")
    @Parameter(name = "entityTypeCode", description = "业务类型编码（必填，用于关系过滤并命中索引）", required = true, example = "equipment")
    @Parameter(name = "includeDescendants", description = "是否含子树（默认 true）", example = "true")
    @PreAuthorize("@ss.hasPermission('system:model:query')")
    /**
     * 用途：查找指定业务下分类关联的模型（Controller 聚合接口）。
     * Service 映射：
     * 1) {@link ModelCategoryRelationService#listModelIdsByCategoryIdWithDescendants(Long, String)}
     *    / {@link ModelCategoryRelationService#listModelIdsByCategoryIdsOnly(List, String)} 等
     * 2) {@link ModelService#getModelsByIds(List)}
     * 边界：本接口返回模型详情；关系服务只返回 ID 序列。
     */
    public CommonResult<List<ModelRespVO>> findModelsByCategoryInBusiness(
            @RequestParam(value = "categoryIds", required = false) List<Long> categoryIds,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam("entityTypeCode") String entityTypeCode,
            @RequestParam(value = "domain", required = false) String domain,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "includeDescendants", required = false, defaultValue = "true")
                    Boolean includeDescendants) {
        List<Long> resolvedCategoryIds = new ArrayList<>();
        if (categoryIds != null) {
            for (Long id : categoryIds) {
                if (id != null && !resolvedCategoryIds.contains(id)) {
                    resolvedCategoryIds.add(id);
                }
            }
        }
        if (resolvedCategoryIds.isEmpty() && categoryId != null) {
            resolvedCategoryIds.add(categoryId);
        }
        if (resolvedCategoryIds.isEmpty()) {
            throw new ServiceException(400, "categoryIds 或 categoryId 不能为空");
        }

        boolean withDescendants = includeDescendants == null || includeDescendants;
        List<Long> modelIds;
        if (withDescendants) {
            modelIds = resolvedCategoryIds.size() == 1
                    ? modelCategoryRelationService.listModelIdsByCategoryIdWithDescendants(
                            resolvedCategoryIds.get(0), entityTypeCode)
                    : modelCategoryRelationService.listModelIdsByCategoryIdsWithDescendants(
                            resolvedCategoryIds, entityTypeCode);
        } else {
            modelIds = resolvedCategoryIds.size() == 1
                    ? modelCategoryRelationService.listModelIdsByCategoryIdOnly(
                            resolvedCategoryIds.get(0), entityTypeCode)
                    : modelCategoryRelationService.listModelIdsByCategoryIdsOnly(
                            resolvedCategoryIds, entityTypeCode);
        }
        List<ModelRespVO> models = modelIds.isEmpty() ? List.of() : modelService.getModelsByIds(modelIds);
        return success(applyKeywordFilter(modelService.filterModelsByDomain(models, domain), keyword));
    }

    @GetMapping("/list-by-entity-type")
    @Operation(
        summary = "按业务类型编码获取模型列表",
        description = "根据 entityTypeCode 返回该业务类型下的所有模型列表（扁平列表，不构建树结构）。"
            + "传入 categoryTypeCode 时按该分类体系树序分桶排序，未挂分类的型号排在末尾。"
    )
    @Parameter(name = "entityTypeCode", description = "业务类型编码（必填）", required = true, example = "equipment")
    @Parameter(name = "categoryTypeCode", description = "分类体系编码（可选；有则按分类树分桶排序）", example = "equipment")
    @PreAuthorize("@ss.hasPermission('system:model:query')")
    /**
     * 用途：按业务类型查询模型列表。
     * Service 映射：{@link ModelService#listModelsByEntityType(String, String, String)}。
     */
    public CommonResult<List<ModelRespVO>> listModelsByEntityType(
            @RequestParam("entityTypeCode") String entityTypeCode,
            @RequestParam(value = "domain", required = false) String domain,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "categoryTypeCode", required = false) String categoryTypeCode,
            @RequestParam(value = "effectiveFacilityId", required = false) Long effectiveFacilityId) {
        List<ModelRespVO> models = modelService.listModelsByEntityType(
                entityTypeCode, domain, categoryTypeCode, effectiveFacilityId);
        return success(applyKeywordFilter(models, keyword));
    }

    @GetMapping("/list-uncategorized-by-category-type")
    @Operation(
        summary = "按分类体系查询未挂分类的模型",
        description = "数据管理「未分类」：返回当前 categoryTypeCode 下未绑定任何分类节点的模型列表。"
    )
    @Parameter(name = "entityTypeCode", description = "业务类型编码（必填）", required = true, example = "equipment")
    @Parameter(name = "categoryTypeCode", description = "分类体系编码（必填）", required = true, example = "equipment")
    @PreAuthorize("@ss.hasPermission('system:model:query')")
    public CommonResult<List<ModelRespVO>> listUncategorizedModelsByCategoryType(
            @RequestParam("entityTypeCode") String entityTypeCode,
            @RequestParam("categoryTypeCode") String categoryTypeCode,
            @RequestParam(value = "domain", required = false) String domain,
            @RequestParam(value = "keyword", required = false) String keyword) {
        List<ModelRespVO> models = modelService.listUncategorizedModelsByCategoryType(
                categoryTypeCode, entityTypeCode, domain);
        return success(applyKeywordFilter(models, keyword));
    }

    @GetMapping("/list-categorized-by-category-type")
    @Operation(
        summary = "按分类体系查询已挂分类的模型",
        description = "数据管理「已分类」：返回当前 categoryTypeCode 下已绑定至少一个分类节点的模型列表。"
    )
    @Parameter(name = "entityTypeCode", description = "业务类型编码（必填）", required = true, example = "equipment")
    @Parameter(name = "categoryTypeCode", description = "分类体系编码（必填）", required = true, example = "equipment")
    @PreAuthorize("@ss.hasPermission('system:model:query')")
    public CommonResult<List<ModelRespVO>> listCategorizedModelsByCategoryType(
            @RequestParam("entityTypeCode") String entityTypeCode,
            @RequestParam("categoryTypeCode") String categoryTypeCode,
            @RequestParam(value = "domain", required = false) String domain,
            @RequestParam(value = "keyword", required = false) String keyword) {
        List<ModelRespVO> models = modelService.listCategorizedModelsByCategoryType(
                categoryTypeCode, entityTypeCode, domain);
        return success(applyKeywordFilter(models, keyword));
    }

    @GetMapping("/list-all")
    @Operation(
        summary = "查询全部业务模型列表（不分页）",
        description = "返回当前租户下全部启用（status=1）的业务模型列表，适用于门户页一次性加载后按 entityTypeCode 分组展示等场景。"
    )
    @PreAuthorize("@ss.hasPermission('system:model:query-all')")
    /**
     * 用途：查询当前租户全部启用模型（跨业务类型）。
     * Service 映射：{@link ModelService#listEnabledModelsAcrossEntityTypes()}。
     * 页面边界：模型管理页面仅读取“已排序业务树”做分组展示，不在本页面调整业务排序。
     */
    public CommonResult<List<ModelRespVO>> listAllModels() {
        return success(modelService.listEnabledModelsAcrossEntityTypes());
    }

    @GetMapping("/page-models")
    @Operation(
        summary = "分页查询业务模型",
        description = "支持按业务类型编码、关键词（模型名称/描述）、状态进行分页查询。\n" +
            "- 关键词会同时匹配模型名称和描述字段（模糊查询）\n" +
            "- 返回结果包含模型的基本信息和统计信息（字段数量、实体数量等）"
    )
    @Parameter(name = "entityTypeCode", description = "业务类型编码（可选）", example = "equipment")
    @Parameter(name = "keyword", description = "关键词（可选，模糊匹配模型名称和描述）", example = "灭火器")
    @Parameter(name = "status", description = "模型状态（可选，1-启用，0-禁用）", example = "1")
    @Parameter(name = "pageNo", description = "页码（默认1）", example = "1")
    @Parameter(name = "pageSize", description = "每页数量（默认10）", example = "10")
    @PreAuthorize("@ss.hasPermission('system:model:query')")
    /**
     * 用途：分页查询模型。
     * Service 映射：{@link ModelService#pageModel(ModelPageReqVO)}。
     */
    public CommonResult<PageResult<ModelRespVO>> pageModelByEntityTypeCode(@Valid ModelPageReqVO reqVO) {
        return success(modelService.pageModelByEntityTypeCode(reqVO));
    }

    @PutMapping("/sort")
    @Operation(
        summary = "更新模型排序（保存顺序）",
        description = "拖拽调整列表顺序后，按提交的整份有序列表重写排序。"
            + "带 categoryId 时写分类—型号关联 sort；不带时写型号主表 sort。"
    )
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:model:update')")
    public CommonResult<Boolean> saveModelSort(@Valid @RequestBody ModelSortSaveReqVO reqVO) {
        modelService.saveModelSort(reqVO);
        return success(true);
    }

    /** @deprecated 使用 {@link #saveModelSort(ModelSortSaveReqVO)}（{@code PUT /sort}） */
    @PutMapping("/sort/batch")
    @Operation(summary = "更新模型排序（保存顺序，兼容旧路径）", deprecated = true)
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:model:update')")
    public CommonResult<Boolean> saveModelSortLegacy(@Valid @RequestBody ModelSortSaveReqVO reqVO) {
        return saveModelSort(reqVO);
    }

    @GetMapping("/search")
    @Operation(
        summary = "搜索业务模型（不分页）",
        description = "根据关键词搜索业务模型（按模型名称、描述进行模糊搜索）。\n" +
            "- 与分页查询的区别是：搜索接口返回所有匹配的结果，不进行分页\n" +
            "- 适用于模型选择器等场景"
    )
    @Parameter(name = "keyword", description = "关键词（必填，模糊匹配模型名称和描述）", required = true, example = "灭火器")
    @Parameter(name = "entityTypeCode", description = "业务类型编码（可选，如果指定则只在该业务类型下搜索）", example = "equipment")
    @PreAuthorize("@ss.hasPermission('system:model:query')")
    /**
     * 用途：关键词搜索模型（不分页）。
     * Service 映射：{@link ModelService#searchModels(String, String)}。
     */
    public CommonResult<List<ModelRespVO>> searchModels(@RequestParam("keyword") String keyword,
                                                         @RequestParam(value = "entityTypeCode", required = false) String entityTypeCode) {
        return success(modelService.searchModels(keyword, entityTypeCode));
    }

    // ========== 模型字段查询（视图配置用）==========

    @GetMapping("/available-fields")
    @Operation(
        summary = "获取模型可用字段列表",
        description = "获取指定模型所有可用的字段列表（包括固定列字段和扩展字段），用于前端视图配置时选择展示字段。"
    )
    @Parameter(name = "modelId", description = "模型ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('system:model:query')")
    /**
     * 用途：查询模型可用字段（用于视图配置）。
     * Service 映射：{@link ModelService#listModelAvailableFields(Long)}。
     */
    public CommonResult<List<ModelAvailableFieldRespVO>> listModelAvailableFields(@RequestParam("modelId") Long modelId) {
        return success(modelService.listModelAvailableFields(modelId));
    }

    // ========== 批量查询接口（性能优化）==========

    @GetMapping("/batch")
    @Operation(
        summary = "批量获取业务模型详情",
        description = "根据模型ID列表批量获取业务模型的详细信息。\n" +
            "- 用于前端一次性获取多个模型的信息，减少 N+1 查询问题\n" +
            "- 返回结果包含模型的基本信息和关联的分类信息"
    )
    @Parameter(name = "ids", description = "模型ID列表，逗号分隔", required = true, example = "1,2,3")
    @PreAuthorize("@ss.hasPermission('system:model:query')")
    /**
     * 用途：批量查询模型详情，避免前端 N+1。
     * Service 映射：{@link ModelService#getModelsByIds(List)}。
     */
    public CommonResult<List<ModelRespVO>> getModelsByIds(@RequestParam("ids") List<Long> ids) {
        return success(modelService.getModelsByIds(ids));
    }

    @PostMapping("/list-from-model-category-groups")
    @Operation(
        summary = "按多组分类—型号关联求交型号列表",
        description = "数据管理级联或多分类栏：每组内按分类—型号关联取并集，组间取型号交集。"
            + "不读取分类—实体关联。"
    )
    @PreAuthorize("@ss.hasPermission('system:model:query')")
    public CommonResult<List<ModelRespVO>> listModelsFromModelCategoryGroups(
            @Valid @RequestBody ModelFromModelCategoryGroupsReqVO reqVO) {
        List<ModelRespVO> models = modelService.listModelsByIntersectingCategoryGroups(
                reqVO.getCategoryIdGroups(),
                reqVO.getEntityTypeCode(),
                reqVO.getDomain(),
                reqVO.getIncludeDescendants());
        return success(applyKeywordFilter(models, reqVO.getKeyword()));
    }

    @PostMapping("/list-from-entity-category-groups")
    @Operation(
        summary = "按分类—实体求交结果派生型号列表",
        description = "数据管理多分类栏：先按 categoryIdGroups（组间 AND）得到实体候选，再去重 model_id 返回型号。"
            + "不走分类—型号关联；与实体列多维求交底集一致。"
    )
    @PreAuthorize("@ss.hasPermission('system:model:query')")
    public CommonResult<List<ModelRespVO>> listModelsFromEntityCategoryGroups(
            @Valid @RequestBody ModelFromEntityCategoryGroupsReqVO reqVO) {
        List<Long> modelIds = entityService.listDistinctModelIdsByCategoryScope(
                reqVO.getEntityTypeCode(),
                reqVO.getCategoryIds(),
                reqVO.getCategoryIdGroups(),
                reqVO.getCategoryTypeCode(),
                reqVO.getDomain(),
                reqVO.getIncludeDescendants());
        if (modelIds.isEmpty()) {
            return success(List.of());
        }
        List<ModelRespVO> models = modelService.getModelsByIds(modelIds);
        return success(applyKeywordFilter(modelService.filterModelsByDomain(models, reqVO.getDomain()), reqVO.getKeyword()));
    }

    @PostMapping("/list-by-scene")
    @Operation(
        summary = "模型统一列表查询（数据管理）",
        description = "统一承接模型列表查询：分类节点、多栏分类求交、已分类/未分类、业务域与关键词可叠加。"
                + "关键词只做附加过滤，不改变分类语义。"
    )
    @PreAuthorize("@ss.hasPermission('system:model:query')")
    public CommonResult<List<ModelRespVO>> listModelsByScene(@Valid @RequestBody ModelListBySceneReqVO reqVO) {
        String entityTypeCode = reqVO.getEntityTypeCode().trim();
        String categoryTypeCode = StringUtils.hasText(reqVO.getCategoryTypeCode())
                ? reqVO.getCategoryTypeCode().trim()
                : entityTypeCode;
        String filterMode = normalizeFilterMode(reqVO.getFilterMode());
        boolean includeDescendants = reqVO.getIncludeDescendants() == null || reqVO.getIncludeDescendants();
        List<Long> categoryIds = normalizeCategoryIds(reqVO.getCategoryIds());
        List<CategoryIdGroupReqVO> categoryGroups = normalizeCategoryGroups(reqVO.getCategoryIdGroups());
        String categoryFilterSource = normalizeCategoryFilterSource(reqVO.getCategoryFilterSource());
        List<ModelFieldFilterReqVO> fieldFilters = normalizeModelFieldFilters(reqVO.getFieldFilters());

        List<ModelRespVO> models;
        if (!categoryGroups.isEmpty()) {
            models = resolveModelsByCategoryGroups(
                    entityTypeCode,
                    categoryTypeCode,
                    reqVO.getDomain(),
                    includeDescendants,
                    categoryFilterSource,
                    categoryGroups);
        } else if (!categoryIds.isEmpty()) {
            models = resolveModelsByCategoryIds(entityTypeCode, reqVO.getDomain(), includeDescendants, categoryIds);
        } else if ("NODE".equals(filterMode)) {
            models = List.of();
        } else if ("UNCATEGORIZED".equals(filterMode)) {
            models = modelService.listUncategorizedModelsByCategoryType(categoryTypeCode, entityTypeCode, reqVO.getDomain());
        } else if ("CATEGORIZED".equals(filterMode)) {
            models = modelService.listCategorizedModelsByCategoryType(categoryTypeCode, entityTypeCode, reqVO.getDomain());
        } else {
            models = modelService.listModelsByEntityType(entityTypeCode, reqVO.getDomain(), categoryTypeCode, null);
        }

        return success(applyKeywordFilter(applyModelFieldFilters(models, fieldFilters), reqVO.getKeyword()));
    }

    /**
     * 统一关键词过滤：模型名称 / 描述任一包含即保留。
     *
     * 查询入口会按分类语义先拿到候选集；keyword 只作为附加条件，避免“有搜索词就丢分类条件”。
     */
    private List<ModelRespVO> applyKeywordFilter(List<ModelRespVO> models, String keyword) {
        if (models == null || models.isEmpty() || !StringUtils.hasText(keyword)) {
            return models == null ? List.of() : models;
        }
        String normalized = keyword.trim().toLowerCase(Locale.ROOT);
        return models.stream()
                .filter(model -> containsIgnoreCase(model == null ? null : model.getName(), normalized)
                        || containsIgnoreCase(model == null ? null : model.getDescription(), normalized))
                .toList();
    }

    private boolean containsIgnoreCase(String value, String normalizedKeyword) {
        return StringUtils.hasText(value)
                && value.toLowerCase(Locale.ROOT).contains(normalizedKeyword);
    }

    /**
     * 模型统一查询的字段筛选：
     * 只接受模型元数据字段；关系筛选（relationField=true）在模型列表语义下直接拒绝。
     */
    private List<ModelRespVO> applyModelFieldFilters(List<ModelRespVO> models, List<ModelFieldFilterReqVO> fieldFilters) {
        if (models == null || models.isEmpty() || fieldFilters == null || fieldFilters.isEmpty()) {
            return models == null ? List.of() : models;
        }
        return models.stream()
                .filter(model -> matchesAllModelFilters(model, fieldFilters))
                .toList();
    }

    private boolean matchesAllModelFilters(ModelRespVO model, List<ModelFieldFilterReqVO> filters) {
        for (ModelFieldFilterReqVO filter : filters) {
            if (!matchesModelFieldFilter(model, filter)) {
                return false;
            }
        }
        return true;
    }

    private boolean matchesModelFieldFilter(ModelRespVO model, ModelFieldFilterReqVO filter) {
        String fieldCode = filter.getFieldCode().trim().toLowerCase(Locale.ROOT);
        String op = normalizeModelFilterOp(filter.getOp());
        Object left = resolveModelFieldValue(model, fieldCode);
        Object right = filter.getValue();
        return switch (op) {
            case "EQ" -> compareEquals(left, right);
            case "NE" -> !compareEquals(left, right);
            case "CONTAINS", "LIKE" -> containsText(left, right);
            case "IN" -> inList(left, right);
            case "NOT_IN" -> !inList(left, right);
            case "GT" -> compareAsNumber(left, right) > 0;
            case "GTE" -> compareAsNumber(left, right) >= 0;
            case "LT" -> compareAsNumber(left, right) < 0;
            case "LTE" -> compareAsNumber(left, right) <= 0;
            case "BETWEEN" -> betweenNumber(left, right);
            default -> throw new ServiceException(400, "不支持的模型筛选操作符: " + op);
        };
    }

    private Object resolveModelFieldValue(ModelRespVO model, String fieldCode) {
        return switch (fieldCode) {
            case "id" -> model == null ? null : model.getId();
            case "code" -> model == null ? null : model.getCode();
            case "name" -> model == null ? null : model.getName();
            case "description" -> model == null ? null : model.getDescription();
            case "status" -> model == null ? null : model.getStatus();
            case "domain" -> model == null ? null : model.getDomain();
            case "entitytypecode" -> model == null ? null : model.getEntityTypeCode();
            case "sort" -> model == null ? null : model.getSort();
            default -> throw new ServiceException(400, "模型字段筛选不支持字段: " + fieldCode);
        };
    }

    private List<ModelFieldFilterReqVO> normalizeModelFieldFilters(List<ModelFieldFilterReqVO> fieldFilters) {
        if (fieldFilters == null || fieldFilters.isEmpty()) {
            return List.of();
        }
        List<ModelFieldFilterReqVO> normalized = new ArrayList<>();
        for (ModelFieldFilterReqVO raw : fieldFilters) {
            if (raw == null || !StringUtils.hasText(raw.getFieldCode())) {
                continue;
            }
            if (Boolean.TRUE.equals(raw.getRelationField())) {
                throw new ServiceException(400, "模型列表不支持 relationField=true 的筛选条件");
            }
            ModelFieldFilterReqVO item = new ModelFieldFilterReqVO();
            item.setFieldCode(raw.getFieldCode().trim());
            item.setOp(normalizeModelFilterOp(raw.getOp()));
            item.setValue(raw.getValue());
            item.setRelationField(false);
            normalized.add(item);
        }
        return normalized;
    }

    private String normalizeModelFilterOp(String rawOp) {
        String op = StringUtils.hasText(rawOp) ? rawOp.trim().toUpperCase(Locale.ROOT) : "EQ";
        return switch (op) {
            case "EQ", "NE", "IN", "NOT_IN", "GTE", "LTE", "GT", "LT", "BETWEEN", "CONTAINS", "LIKE" -> op;
            default -> throw new ServiceException(400, "不支持的模型筛选操作符: " + op);
        };
    }

    private boolean compareEquals(Object left, Object right) {
        if (left == null && right == null) {
            return true;
        }
        if (left == null || right == null) {
            return false;
        }
        if (left instanceof Number || right instanceof Number) {
            Double lv = toDouble(left);
            Double rv = toDouble(right);
            if (lv != null && rv != null) {
                return Double.compare(lv, rv) == 0;
            }
        }
        return String.valueOf(left).equalsIgnoreCase(String.valueOf(right));
    }

    private boolean containsText(Object left, Object right) {
        if (left == null || right == null) {
            return false;
        }
        String leftText = String.valueOf(left).toLowerCase(Locale.ROOT);
        String rightText = String.valueOf(right).trim().toLowerCase(Locale.ROOT);
        if (rightText.isEmpty()) {
            return false;
        }
        return leftText.contains(rightText);
    }

    private boolean inList(Object left, Object right) {
        if (left == null) {
            return false;
        }
        List<Object> values = flattenToList(right);
        if (values.isEmpty()) {
            return false;
        }
        for (Object value : values) {
            if (compareEquals(left, value)) {
                return true;
            }
        }
        return false;
    }

    private int compareAsNumber(Object left, Object right) {
        Double lv = toDouble(left);
        Double rv = toDouble(right);
        if (lv == null || rv == null) {
            throw new ServiceException(400, "数字比较筛选值无效，字段值或条件值不是数字");
        }
        return Double.compare(lv, rv);
    }

    private boolean betweenNumber(Object left, Object right) {
        Double lv = toDouble(left);
        if (lv == null) {
            return false;
        }
        List<Object> values = flattenToList(right);
        if (values.size() < 2) {
            throw new ServiceException(400, "BETWEEN 条件需要两个边界值");
        }
        Double from = toDouble(values.get(0));
        Double to = toDouble(values.get(1));
        if (from == null || to == null) {
            throw new ServiceException(400, "BETWEEN 条件值必须是数字");
        }
        return lv >= Math.min(from, to) && lv <= Math.max(from, to);
    }

    private Double toDouble(Object raw) {
        if (raw == null) {
            return null;
        }
        if (raw instanceof Number number) {
            double value = number.doubleValue();
            return Double.isFinite(value) ? value : null;
        }
        if (raw instanceof String text && StringUtils.hasText(text)) {
            try {
                double value = Double.parseDouble(text.trim());
                return Double.isFinite(value) ? value : null;
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private List<Object> flattenToList(Object raw) {
        if (raw == null) {
            return List.of();
        }
        if (raw instanceof List<?> list) {
            List<Object> values = new ArrayList<>(list.size());
            for (Object item : list) {
                if (item != null) {
                    values.add(item);
                }
            }
            return values;
        }
        if (raw instanceof java.util.Collection<?> collection) {
            List<Object> values = new ArrayList<>(collection.size());
            for (Object item : collection) {
                if (item != null) {
                    values.add(item);
                }
            }
            return values;
        }
        if (raw.getClass().isArray()) {
            int len = Array.getLength(raw);
            List<Object> values = new ArrayList<>(len);
            for (int i = 0; i < len; i++) {
                Object value = Array.get(raw, i);
                if (value != null) {
                    values.add(value);
                }
            }
            return values;
        }
        if (raw instanceof String text && text.contains(",")) {
            String[] parts = text.split(",");
            List<Object> values = new ArrayList<>(parts.length);
            for (String part : parts) {
                if (StringUtils.hasText(part)) {
                    values.add(part.trim());
                }
            }
            return values;
        }
        return List.of(raw);
    }

    private String normalizeFilterMode(String raw) {
        if (!StringUtils.hasText(raw)) {
            return "UNFILTERED";
        }
        String normalized = raw.trim().toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "NODE", "CATEGORIZED", "UNCATEGORIZED", "NONE" -> normalized;
            default -> "UNFILTERED";
        };
    }

    private String normalizeCategoryFilterSource(String raw) {
        if (!StringUtils.hasText(raw)) {
            return "MODEL_CATEGORY";
        }
        return "ENTITY_CATEGORY".equalsIgnoreCase(raw.trim()) ? "ENTITY_CATEGORY" : "MODEL_CATEGORY";
    }

    private List<Long> normalizeCategoryIds(List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return List.of();
        }
        return categoryIds.stream()
                .filter(Objects::nonNull)
                .filter(id -> id > 0)
                .distinct()
                .toList();
    }

    private List<CategoryIdGroupReqVO> normalizeCategoryGroups(List<CategoryIdGroupReqVO> categoryIdGroups) {
        if (categoryIdGroups == null || categoryIdGroups.isEmpty()) {
            return List.of();
        }
        List<CategoryIdGroupReqVO> normalized = new ArrayList<>();
        for (CategoryIdGroupReqVO group : categoryIdGroups) {
            if (group == null || !StringUtils.hasText(group.getCategoryTypeCode())) {
                continue;
            }
            CategoryIdGroupReqVO item = new CategoryIdGroupReqVO();
            item.setCategoryTypeCode(group.getCategoryTypeCode().trim());
            item.setCategoryIds(normalizeCategoryIds(group.getCategoryIds()));
            normalized.add(item);
        }
        return normalized;
    }

    private List<ModelRespVO> resolveModelsByCategoryGroups(String entityTypeCode,
                                                            String categoryTypeCode,
                                                            String domain,
                                                            boolean includeDescendants,
                                                            String categoryFilterSource,
                                                            List<CategoryIdGroupReqVO> categoryGroups) {
        if ("ENTITY_CATEGORY".equals(categoryFilterSource)) {
            List<Long> modelIds = entityService.listDistinctModelIdsByCategoryScope(
                    entityTypeCode,
                    null,
                    categoryGroups,
                    categoryTypeCode,
                    domain,
                    includeDescendants);
            if (modelIds.isEmpty()) {
                return List.of();
            }
            List<ModelRespVO> models = modelService.getModelsByIds(modelIds);
            return modelService.filterModelsByDomain(models, domain);
        }
        return modelService.listModelsByIntersectingCategoryGroups(
                categoryGroups, entityTypeCode, domain, includeDescendants);
    }

    private List<ModelRespVO> resolveModelsByCategoryIds(String entityTypeCode,
                                                         String domain,
                                                         boolean includeDescendants,
                                                         List<Long> categoryIds) {
        List<Long> modelIds;
        if (includeDescendants) {
            modelIds = categoryIds.size() == 1
                    ? modelCategoryRelationService.listModelIdsByCategoryIdWithDescendants(categoryIds.get(0), entityTypeCode)
                    : modelCategoryRelationService.listModelIdsByCategoryIdsWithDescendants(categoryIds, entityTypeCode);
        } else {
            modelIds = categoryIds.size() == 1
                    ? modelCategoryRelationService.listModelIdsByCategoryIdOnly(categoryIds.get(0), entityTypeCode)
                    : modelCategoryRelationService.listModelIdsByCategoryIdsOnly(categoryIds, entityTypeCode);
        }
        if (modelIds.isEmpty()) {
            return List.of();
        }
        List<Long> deduplicated = new ArrayList<>(new LinkedHashSet<>(modelIds));
        List<ModelRespVO> models = modelService.getModelsByIds(deduplicated);
        return modelService.filterModelsByDomain(models, domain);
    }
}

