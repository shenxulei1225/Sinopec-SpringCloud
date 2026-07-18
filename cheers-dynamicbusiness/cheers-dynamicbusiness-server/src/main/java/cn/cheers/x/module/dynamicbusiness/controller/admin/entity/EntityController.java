package cn.cheers.x.module.dynamicbusiness.controller.admin.entity;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.excel.core.util.ExcelUtils;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.*;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONException;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.enums.entity.EntityQueryScene;
import cn.cheers.x.module.dynamicbusiness.enums.entity.EntityQueryResultDetail;
import cn.cheers.x.module.dynamicbusiness.enums.entity.EntityQueryResultShape;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityDataExportService;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityService;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelRespVO;
import cn.cheers.x.module.dynamicbusiness.service.model.ModelService;
import org.springframework.util.StringUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import cn.cheers.x.framework.apilog.core.annotation.ApiAccessLog;
import static cn.cheers.x.framework.apilog.core.enums.OperateTypeEnum.*;
import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 实体 Controller
 * 
 * Entity（实体/实例）表示现实世界中的"一个具体对象"（instance），
 * 例如某一台具体的灭火器（SN 唯一）。
 * Entity 必须归属于一个 Model（modelId），以决定有哪些字段、如何校验。
 * Entity 通过 modelId 关联到 Model，Model 可以关联多个 Category，因此 Entity 间接关联到 Category。
 * Category 只用于归类与筛选，**不影响字段规则**（字段规则由 modelId 决定）。
 * 
 * @author yudao
 */
@Tag(name = "管理后台 - 实体管理", description = "提供实体的创建、更新、删除、查询、导入导出等功能。实体是业务系统中的具体实例对象，必须归属于一个模型（Model）")
@RestController
@RequestMapping("/dynamicbusiness/business/entities")
@Validated
public class EntityController {

    @Resource
    private EntityService entityService;

    @Resource
    private ModelService modelService;

    @Resource
    private EntityDataExportService entityDataExportService;

    @Resource
    private cn.cheers.x.module.dynamicbusiness.service.entity.modelchange.EntityModelChangeService entityModelChangeService;

    @PostMapping("/change-model/preview")
    @Operation(
        summary = "变更模型 - 预览字段迁移",
        description = "按 fieldCode 交集计算保留/需补填/归档字段，不写入数据库。"
    )
    @PreAuthorize("@ss.hasPermission('system:entity:update')")
    public CommonResult<EntityChangeModelPreviewRespVO> previewChangeModel(
            @Valid @RequestBody EntityChangeModelPreviewReqVO reqVO) {
        return success(entityModelChangeService.preview(reqVO));
    }

    @PostMapping("/change-model")
    @Operation(
        summary = "变更模型 - 提交",
        description = """
            将实体迁移至目标模型：共有 fieldCode 保留原值；源专有字段写入 _modelChangeArchive。
            目标必填缺值须通过 patchFields 补填。
            """
    )
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:entity:update')")
    public CommonResult<EntityChangeModelCommitRespVO> commitChangeModel(
            @Valid @RequestBody EntityChangeModelCommitReqVO reqVO) {
        return success(entityModelChangeService.commit(reqVO));
    }

    @PostMapping("/create")
    @Operation(
        summary = "创建实体",
        description = """
            创建一个新的实体/实例。
            - Entity 必须归属于一个 Model（modelId），以决定有哪些字段、如何校验
            - Entity 通过 modelId 关联到 Model，Model 可以关联多个 Category，因此 Entity 间接关联到 Category
            - Category 只用于归类与筛选，**不影响字段规则**（字段规则由 modelId 决定）
            - 创建时会根据 Model 的字段分配配置验证自定义字段数据
            """
    )
    @ApiAccessLog(operateType = CREATE)
    @PreAuthorize("@ss.hasPermission('system:entity:create')")
    public CommonResult<Long> create(@Valid @RequestBody EntityCreateReqVO reqVO) {
        return success(entityService.create(reqVO));
    }

    @PutMapping("/update")
    @Operation(
        summary = "更新实体",
        description = """
            更新实体的信息。
            - 支持 modelId 修改（需要根据新的 modelId 重新验证字段）
            - 如果修改了 modelId，会根据新 Model 的字段分配配置重新验证自定义字段数据
            - Entity 通过 modelId 关联到 Model，Model 可以关联多个 Category
            """
    )
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:entity:update')")
    public CommonResult<Boolean> update(@Valid @RequestBody EntityUpdateReqVO reqVO) {
        entityService.update(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(
        summary = "删除实体",
        description = """
            删除实体。
            - 删除前会检查是否有关联关系
            - 如果存在关联关系且 forceDelete=false，则禁止删除并返回关联关系数量
            - 如果 forceDelete=true，则同时删除所有关联关系

            **重要**：entityTypeCode 是必填参数，用于路由到正确的存储策略。
            """
    )
    @Parameter(name = "id", description = "实体编号", required = true, example = "1")
    @Parameter(name = "entityTypeCode", description = "业务类型编码（必填，用于路由到对应存储策略）", required = true, example = "equipment")
    @Parameter(name = "forceDelete", description = "是否强制删除（同时删除所有关联关系）", example = "false")
    @ApiAccessLog(operateType = DELETE)
    @PreAuthorize("@ss.hasPermission('system:entity:delete')")
    public CommonResult<Boolean> delete(@RequestParam("id") Long id,
                                        @RequestParam("entityTypeCode") String entityTypeCode,
                                        @RequestParam(value = "forceDelete", required = false, defaultValue = "false") Boolean forceDelete) {
        EntityDeleteReqVO reqVO = new EntityDeleteReqVO();
        reqVO.setId(id);
        reqVO.setEntityTypeCode(entityTypeCode);
        reqVO.setForceDelete(forceDelete);
        entityService.delete(reqVO);
        return success(true);
    }

    @GetMapping({"/detail", "/get-by-id"})
    @Operation(
        summary = "获取实体详情",
        description = """
            根据实体ID获取详细信息，包含：
            - 基本信息（名称、状态、业务类型编码等）
            - modelId（关联的模型ID）
            - 自定义字段数据（customFields，已解密）

            **重要**：entityTypeCode 是必填参数，用于路由到正确的存储策略。

            - includeAssociations=true 时填充 associations（各 REF/REFMulti 关联块）
            - associationCategoryViews：JSON 数组字符串，元素为 {fieldCode, categoryTypeCode}，见 entity-detail-associations-design.md
            """
    )
    @Parameter(name = "id", description = "实体编号", required = true, example = "1")
    @Parameter(name = "entityTypeCode", description = "业务类型编码（必填，用于路由到对应存储策略）", required = true, example = "equipment")
    @Parameter(name = "includeAssociations", description = "是否包含关联字段展示数据（默认 false）")
    @Parameter(name = "associationCategoryViews", description = "可选，JSON 数组：[{fieldCode,categoryTypeCode},...]")
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<EntityRespVO> get(@RequestParam("id") Long id,
                                          @RequestParam("entityTypeCode") String entityTypeCode,
                                          @RequestParam(value = "includeAssociations", required = false, defaultValue = "false") Boolean includeAssociations,
                                          @RequestParam(value = "associationCategoryViews", required = false) String associationCategoryViewsJson) {
        List<AssociationCategoryViewReqVO> views = null;
        if (associationCategoryViewsJson != null && !associationCategoryViewsJson.isBlank()) {
            try {
                views = JSON.parseArray(associationCategoryViewsJson, AssociationCategoryViewReqVO.class);
            } catch (JSONException e) {
                throw new ServiceException(400, "associationCategoryViews 须为合法 JSON 数组");
            }
        }
        return success(entityService.get(id, entityTypeCode, Boolean.TRUE.equals(includeAssociations), views));
    }

    @GetMapping("/exists-by-id")
    @Operation(
        summary = "检查实体是否存在",
        description = """
            检查指定业务下实体是否存在。
            - 适用场景：前端提交前校验、按钮态控制
            - 业务范围：指定业务（必须传 entityTypeCode）
            """
    )
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<Boolean> exists(@RequestParam("id") Long id,
                                        @RequestParam("entityTypeCode") String entityTypeCode) {
        return success(entityService.get(id, entityTypeCode) != null);
    }

    @GetMapping("/check-field-unique")
    @Operation(
        summary = "校验实体字段值是否可用",
        description = """
            CRUD 弹窗异步校验。当前支持 fieldKey=name：同 entityTypeCode + modelId 下名称唯一。
            excludeId 用于编辑时排除自身。
            """
    )
    @Parameter(name = "entityTypeCode", required = true, example = "equipment")
    @Parameter(name = "modelId", required = true, example = "157")
    @Parameter(name = "fieldKey", required = true, example = "name")
    @Parameter(name = "value", required = true, example = "测试设备")
    @Parameter(name = "excludeId", description = "编辑时排除的实体 id")
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<EntityFieldAvailabilityRespVO> checkFieldUnique(
            @RequestParam("entityTypeCode") String entityTypeCode,
            @RequestParam("modelId") Long modelId,
            @RequestParam("fieldKey") String fieldKey,
            @RequestParam("value") String value,
            @RequestParam(value = "excludeId", required = false) Long excludeId) {
        return success(entityService.checkFieldUnique(entityTypeCode, modelId, fieldKey, value, excludeId));
    }

    @GetMapping("/page-by-filters")
    @Operation(
        summary = "分页查询实体列表",
        description = """
            支持按业务类型编码、模型ID、状态、关键词进行分页查询。
            - entityTypeCode 是必填参数，用于路由到正确的存储策略
            - 如需按分类筛选，请先通过 ModelController 查询该分类下的模型，再使用 modelId 参数查询实体
            - 关键词会匹配实体名称（模糊查询）
            - 返回结果中的自定义字段数据已自动解密
            """
    )
    @Parameter(name = "entityTypeCode", description = "业务类型编码（必填）", required = true, example = "equipment")
    @Parameter(name = "modelId", description = "模型ID（可选，用于查询特定模型下的实体）", example = "1")
    @Parameter(name = "status", description = "状态（可选，0-禁用，1-启用）", example = "1")
    @Parameter(name = "keyword", description = "关键词（可选，模糊匹配实体名称）", example = "设备")
    @Parameter(name = "pageNo", description = "页码（默认1）", example = "1")
    @Parameter(name = "pageSize", description = "每页条数（默认10）", example = "10")
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<PageResult<EntityRespVO>> page(@Valid EntityPageReqVO reqVO) {
        return success(entityService.pageSearchEntities(reqVO));
    }

    @PutMapping("/move-by-id")
    @Operation(
        summary = "移动实体到新的父实体",
        description = """
            调整实体树层级关系（仅修改 parentId/treePath 相关语义）。
            - 适用场景：树结构拖拽调整
            - 业务范围：指定业务（必须传 entityTypeCode）
            """
    )
    @PreAuthorize("@ss.hasPermission('system:entity:update')")
    public CommonResult<Boolean> moveEntity(@RequestParam("entityId") Long entityId,
                                            @RequestParam("entityTypeCode") String entityTypeCode,
                                            @RequestParam(value = "newParentId", required = false) Long newParentId) {
        entityService.moveEntity(entityId, entityTypeCode, newParentId);
        return success(true);
    }

    @GetMapping("/path-by-entity-id")
    @Operation(
        summary = "获取实体路径",
        description = """
            获取实体从根到当前节点的路径（名称链路）。
            - 适用场景：面包屑展示、定位上下文
            - 业务范围：指定业务（必须传 entityTypeCode）
            """
    )
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<List<String>> getEntityPath(@RequestParam("entityId") Long entityId,
                                                     @RequestParam("entityTypeCode") String entityTypeCode) {
        return success(entityService.getEntityPath(entityId, entityTypeCode));
    }

    @GetMapping("/get-by-category-link")
    @Operation(
        summary = "查询分类绑定实体详情（1对1）",
        description = """
            Pattern C 独立接口：先读取 dynamic_category_entity_link 获取绑定 entityId，再查询实体详情。
            - 该接口为 1 对 1 语义，不走分页，不返回数组
            - categoryId 为必填
            - entityTypeCode 为必填（用于实体路由）
            """
    )
    @Parameter(name = "categoryId", description = "分类ID（必填）", required = true, example = "1")
    @Parameter(name = "entityTypeCode", description = "业务类型编码（必填）", required = true, example = "equipment")
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<EntityRespVO> getCategoryLinkedEntity(@RequestParam("categoryId") Long categoryId,
                                                                @RequestParam("entityTypeCode") String entityTypeCode) {
        return success(entityService.getCategoryLinkedEntity(categoryId, entityTypeCode));
    }

    // ==================== 搜索和过滤相关 API ====================

    @PostMapping("/search")
    @Operation(
        summary = "高级搜索实体",
        description = """
            支持全文搜索、高级过滤、多字段排序的综合搜索功能。
            - 适用场景：复杂条件检索页
            - 业务范围：由请求参数中的业务条件决定（建议明确传业务维度）
            """
    )
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<EntitySearchRespVO> search(@Valid @RequestBody EntitySearchReqVO reqVO) {
        return success(entityService.searchAdvanced(reqVO));
    }

    @GetMapping("/query-by-scene")
    @Operation(
        summary = "统一实体查询（按场景）",
        description = """
            通过 scene 参数统一处理多种实体查询场景。
            - 适用场景：模式A/B/C/D统一入口
            - 业务范围：多数场景为指定业务（建议传 entityTypeCode）
            - modelIds / categoryIds 支持重复 query 参数（modelIds=1&modelIds=2）或逗号分隔单参数（modelIds=1,2,3）
            - 多选 ID 较多时建议使用 POST /query-by-scene + JSON body
            - DATA_MGMT_ENTITIES_BY_CATEGORY_MODEL：数据管理；选中分类节点（含子孙）+ 可选 modelIds
            - DATA_MGMT_ENTITIES_ALL_IN_CATEGORY_TYPE：数据管理；当前 categoryTypeCode 下全部有关联实体 + 可选 modelIds
            - DATA_MGMT_ENTITIES_UNCATEGORIZED：数据管理；当前 categoryTypeCode 下未挂接分类的实体 + 可选 modelIds
            """
    )
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<EntitySceneQueryRespVO> queryEntities(
            @RequestParam("scene") EntityQueryScene scene,
            @RequestParam(value = "resultShape", required = false, defaultValue = "PAGE") String resultShape,
            @RequestParam(value = "resultDetail", required = false, defaultValue = "FULL") String resultDetail,
            @RequestParam(value = "categoryTypeCode", required = false) String categoryTypeCode,
            @RequestParam(value = "entityTypeCode", required = false) String entityTypeCode,
            @RequestParam(value = "modelIds", required = false) List<String> modelIds,
            @RequestParam(value = "categoryIds", required = false) List<String> categoryIds,
            @RequestParam(value = "entityId", required = false) Long entityId,
            @RequestParam(value = "rootEntityId", required = false) Long rootEntityId,
            @RequestParam(value = "entitySourceEntityType", required = false) String entitySourceEntityType,
            @RequestParam(value = "pageNo", required = false) Integer pageNo,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "dataScope", required = false) String dataScope,
            @RequestBody(required = false) List<FieldFilterReqVO> filters) {
        return queryEntitiesInternal(scene, resultShape, resultDetail, categoryTypeCode, entityTypeCode,
                parseFlexibleIdList(modelIds), parseFlexibleIdList(categoryIds),
                entityId, rootEntityId, entitySourceEntityType, pageNo, pageSize, keyword, dataScope, filters);
    }

    @PostMapping("/query-by-scene")
    @Operation(
        summary = "统一实体查询（按场景，JSON body）",
        description = """
            与 GET /query-by-scene 语义一致，通过 JSON body 传 scene、modelIds[]、categoryIds[] 等。
            - 适用场景：型号/分类多选、fieldFilters 较多，避免超长 query string
            - body 示例：{"scene":"PATTERN_B_ENTITIES_BY_MODEL","entityTypeCode":"equipment","modelIds":[48,47],"pageNo":1,"pageSize":10}
            """
    )
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<EntitySceneQueryRespVO> queryEntitiesByBody(@Valid @RequestBody EntitySceneQueryReqVO reqVO) {
        return queryEntitiesInternal(reqVO.getScene(), reqVO.getResultShape(), reqVO.getResultDetail(),
                reqVO.getCategoryTypeCode(), reqVO.getEntityTypeCode(),
                reqVO.getModelIds(), reqVO.getCategoryIds(),
                reqVO.getEntityId(), reqVO.getRootEntityId(), reqVO.getEntitySourceEntityType(),
                reqVO.getPageNo(), reqVO.getPageSize(), reqVO.getKeyword(), reqVO.getDataScope(), reqVO.getFieldFilters());
    }

    private CommonResult<EntitySceneQueryRespVO> queryEntitiesInternal(
            EntityQueryScene scene,
            String resultShape,
            String resultDetail,
            String categoryTypeCode,
            String entityTypeCode,
            List<Long> modelIds,
            List<Long> categoryIds,
            Long entityId,
            Long rootEntityId,
            String entitySourceEntityType,
            Integer pageNo,
            Integer pageSize,
            String keyword,
            String dataScope,
            List<FieldFilterReqVO> filters) {
        List<Long> effectiveModelIds = mergeModelIdsByDataScope(entityTypeCode, modelIds, dataScope);
        return success(entityService.queryEntities(scene, EntityQueryResultShape.ofNullable(resultShape).getCode(),
                EntityQueryResultDetail.ofNullable(resultDetail).getCode(),
                categoryTypeCode, entityTypeCode,
                effectiveModelIds, categoryIds, entityId, rootEntityId, entitySourceEntityType, pageNo, pageSize, keyword, filters));
    }

    private List<Long> mergeModelIdsByDataScope(String entityTypeCode, List<Long> modelIds, String dataScope) {
        if (!StringUtils.hasText(dataScope) || !StringUtils.hasText(entityTypeCode)) {
            return modelIds;
        }
        if (modelIds != null && !modelIds.isEmpty()) {
            return modelIds;
        }
        List<ModelRespVO> scopedModels = modelService.listModelsByEntityType(entityTypeCode.trim(), dataScope.trim());
        if (scopedModels == null || scopedModels.isEmpty()) {
            return List.of(-1L);
        }
        return scopedModels.stream()
                .map(ModelRespVO::getId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 解析 modelIds / categoryIds：支持 modelIds=1&modelIds=2 与 modelIds=1,2,3 两种写法。
     */
    private static List<Long> parseFlexibleIdList(List<String> raw) {
        if (raw == null || raw.isEmpty()) {
            return null;
        }
        List<Long> out = new ArrayList<>();
        for (String token : raw) {
            if (token == null || token.isBlank()) {
                continue;
            }
            for (String part : token.split(",")) {
                String trimmed = part.trim();
                if (trimmed.isEmpty()) {
                    continue;
                }
                try {
                    out.add(Long.parseLong(trimmed));
                } catch (NumberFormatException ignored) {
                    // 跳过非法片段，避免整批请求失败
                }
            }
        }
        return out.isEmpty() ? null : out;
    }

    // ==================== 导入导出相关 API ====================

    @GetMapping("/export-excel")
    @Operation(
        summary = "导出实体数据（Excel格式）",
        description = """
            将业务实体数据导出为 Excel 文件。
            - 支持按业务类型编码、模型ID、状态、关键词过滤
            - 自定义字段以 JSON 格式导出
            """
    )
    @ApiAccessLog(operateType = EXPORT)
    @PreAuthorize("@ss.hasPermission('system:entity:export')")
    public void exportExcel(@Valid EntityExportReqVO reqVO, HttpServletResponse response) throws IOException {
        List<EntityExportExcelVO> list = entityDataExportService.exportEntityList(reqVO);
        ExcelUtils.write(response, "业务实体.xls", "数据", EntityExportExcelVO.class, list);
    }

    @GetMapping("/export-csv")
    @Operation(
        summary = "导出实体数据（CSV格式）",
        description = """
            将业务实体数据导出为 CSV 文件。
            - 支持按业务类型编码、模型ID、状态、关键词过滤
            - 自定义字段以 JSON 格式导出
            """
    )
    @ApiAccessLog(operateType = EXPORT)
    @PreAuthorize("@ss.hasPermission('system:entity:export')")
    public void exportCsv(@Valid EntityExportReqVO reqVO, HttpServletResponse response) throws IOException {
        String csvContent = entityDataExportService.exportToCsv(reqVO);
        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=entities.csv");
        response.getOutputStream().write(csvContent.getBytes(StandardCharsets.UTF_8));
    }

    @GetMapping("/export-json")
    @Operation(
        summary = "导出实体数据（JSON格式）",
        description = """
            将业务实体数据导出为 JSON 文件。
            - 支持按业务类型编码、模型ID、状态、关键词过滤
            """
    )
    @ApiAccessLog(operateType = EXPORT)
    @PreAuthorize("@ss.hasPermission('system:entity:export')")
    public void exportJson(@Valid EntityExportReqVO reqVO, HttpServletResponse response) throws IOException {
        String jsonContent = entityDataExportService.exportToJson(reqVO);
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=entities.json");
        response.getOutputStream().write(jsonContent.getBytes(StandardCharsets.UTF_8));
    }

    @GetMapping("/import-template")
    @Operation(
        summary = "获取导入模板",
        description = """
            下载业务实体导入模板 Excel 文件。
            - entityTypeCode 是必填参数，用于路由到正确的存储策略
            - 可选传入 modelId 参数，生成带字段说明的模板
            - 模板包含示例数据和字段说明
            """
    )
    @Parameter(name = "entityTypeCode", description = "业务类型编码（必填）", required = true, example = "equipment")
    @Parameter(name = "modelId", description = "模型ID（可选，用于生成带字段说明的模板）", example = "1")
    @PreAuthorize("@ss.hasPermission('system:entity:import')")
    public void getImportTemplate(@RequestParam("entityTypeCode") String entityTypeCode,
                                    @RequestParam(value = "modelId", required = false) Long modelId,
                                    HttpServletResponse response) throws IOException {
        List<EntityImportExcelVO> list = entityDataExportService.getImportTemplate(entityTypeCode, modelId);
        ExcelUtils.write(response, "业务实体导入模板.xls", "实体列表", EntityImportExcelVO.class, list);
    }

    @PostMapping("/import")
    @Operation(
        summary = "导入实体数据",
        description = """
            从 Excel 文件导入业务实体数据。
            - 支持创建新实体和更新已存在的实体
            - 导入时会验证数据格式和业务规则
            - 返回导入结果，包含成功和失败的详细信息
            """
    )
    @Parameter(name = "file", description = "Excel 文件", required = true)
    @Parameter(name = "updateSupport", description = "是否支持更新已存在的数据", example = "false")
    @ApiAccessLog(operateType = IMPORT)
    @PreAuthorize("@ss.hasPermission('system:entity:import')")
    public CommonResult<EntityImportRespVO> importExcel(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "updateSupport", required = false, defaultValue = "false") Boolean updateSupport) throws IOException {
        List<EntityImportExcelVO> list = ExcelUtils.read(file, EntityImportExcelVO.class);
        return success(entityDataExportService.importEntityList(list, updateSupport));
    }

    // ==================== 批量操作相关 API ====================

    @PutMapping("/batch-update")
    @Operation(
        summary = "批量更新实体",
        description = """
            批量修改多个实体的字段值。
            - 单次操作最多支持1000条记录
            - 支持更新状态和自定义字段
            - 返回操作结果，包含成功数量、失败数量和失败详情
            """
    )
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:entity:update')")
    public CommonResult<EntityBatchOperationRespVO> batchUpdate(@Valid @RequestBody EntityBatchUpdateReqVO reqVO) {
        return success(entityService.batchUpdate(reqVO));
    }

    @PostMapping("/batch-create")
    @Operation(
        summary = "批量创建实体",
        description = """
            批量创建多个实体。
            - 适用场景：批量导入前的应用层创建、任务化批量建模
            - 业务范围：指定业务（由请求体中的 entityTypeCode 决定）
            """
    )
    @ApiAccessLog(operateType = CREATE)
    @PreAuthorize("@ss.hasPermission('system:entity:create')")
    public CommonResult<List<Long>> batchCreate(@Valid @RequestBody EntityBatchCreateReqVO reqVO) {
        return success(entityService.batchCreate(reqVO));
    }

    @DeleteMapping("/batch-delete")
    @Operation(
        summary = "批量删除实体",
        description = """
            批量删除多个实体。
            - 单次操作最多支持1000条记录
            - 删除前会检查关联关系
            - 如果存在关联关系且 forceDelete=false，则该实体删除失败
            - 如果 forceDelete=true，则同时删除所有关联关系
            - 返回操作结果，包含成功数量、失败数量和失败详情
            """
    )
    @ApiAccessLog(operateType = DELETE)
    @PreAuthorize("@ss.hasPermission('system:entity:delete')")
    public CommonResult<EntityBatchOperationRespVO> batchDelete(@Valid @RequestBody EntityBatchDeleteReqVO reqVO) {
        return success(entityService.batchDelete(reqVO));
    }

    @PostMapping("/batch-relocate-category")
    @Operation(
        summary = "批量调整实体分类关联",
        description = """
            批量将多个实体关联到目标分类。
            - 单次操作最多支持1000条记录
            - 支持替换现有分类关联或追加新的分类关联
            - 仅调整实体-分类关系，不修改实体树 parentId/treePath
            - 返回操作结果，包含成功数量、失败数量和失败详情
            """
    )
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:entity:update')")
    public CommonResult<EntityBatchOperationRespVO> batchRelocateCategory(@Valid @RequestBody EntityBatchMoveReqVO reqVO) {
        return success(entityService.batchRelocateCategory(reqVO));
    }

    @PostMapping("/batch-append-category")
    @Operation(
        summary = "批量追加实体分类关联",
        description = """
            批量将实体追加关联到目标分类（保留原有关联）。
            - 适用场景：批量标签化、增量挂载分类
            - 业务范围：指定业务（必须在请求体中提供 entityTypeCode）
            """
    )
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:entity:update')")
    public CommonResult<BatchEntityCategoryAssociationRespVO> batchAppendCategory(
            @Valid @RequestBody EntityBatchCategoryRelationReqVO reqVO) {
        return success(entityService.batchAppendCategory(reqVO));
    }

    @PostMapping("/batch-remove-category")
    @Operation(
        summary = "批量解除实体分类关联",
        description = """
            批量解除实体与目标分类的关联（不影响其它分类关联）。
            - 适用场景：批量取消标签、分类关系清理
            - 业务范围：指定业务（必须在请求体中提供 entityTypeCode）
            """
    )
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:entity:update')")
    public CommonResult<BatchEntityCategoryAssociationRespVO> batchRemoveCategory(
            @Valid @RequestBody EntityBatchCategoryRelationReqVO reqVO) {
        return success(entityService.batchRemoveCategory(reqVO));
    }

    @PostMapping("/batch-replace-categories")
    @Operation(
        summary = "批量覆盖实体分类集合",
        description = """
            批量用目标分类集合覆盖实体当前分类集合。
            - 适用场景：导入覆盖、规则重算后的关系重建
            - 业务范围：指定业务（必须在请求体中提供 entityTypeCode）
            """
    )
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:entity:update')")
    public CommonResult<BatchEntityCategoryAssociationRespVO> batchReplaceCategories(
            @Valid @RequestBody EntityBatchReplaceCategoriesReqVO reqVO) {
        return success(entityService.batchReplaceCategories(reqVO));
    }

    @PostMapping("/batch-preview")
    @Operation(
        summary = "获取批量操作预览",
        description = """
            获取批量操作的预览信息。
            - entityTypeCode 是必填参数，用于路由到正确的存储策略
            - 返回受影响的实体数量和详情
            - 检查实体是否存在、是否有关联关系等
            - 用于在执行批量操作前向用户展示影响范围
            """
    )
    @Parameter(name = "entityTypeCode", description = "业务类型编码（必填）", required = true, example = "equipment")
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<EntityBatchOperationRespVO> batchPreview(
            @RequestParam("entityTypeCode") String entityTypeCode,
            @RequestBody List<Long> ids) {
        return success(entityService.getBatchOperationPreview(entityTypeCode, ids));
    }

}
