package cn.cheers.x.module.dynamicbusiness.controller.admin.model;

import cn.cheers.x.framework.apilog.core.annotation.ApiAccessLog;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.CustomRelationFieldCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.FieldRulesUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelFieldBatchAssignReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelFieldAssignmentRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelFieldUnassignReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelFieldBatchUnassignReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelFilterFieldMetaRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelFieldBatchOperationRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelFieldAssignReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelFieldRulesUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.service.model.ModelFieldAssignmentService;
import cn.cheers.x.module.dynamicbusiness.service.model.ModelFieldRulesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;

import static cn.cheers.x.framework.apilog.core.enums.OperateTypeEnum.CREATE;
import static cn.cheers.x.framework.apilog.core.enums.OperateTypeEnum.UPDATE;
import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 模型字段分配 Controller
 * 
 * 用于管理 Model 与 Field 的关联关系，以及字段在 Model 中的业务规则配置。
 * 字段定义（Field）只挂在 Model 上，Model 决定「这一类东西有哪些字段」「字段类型与校验规则」。
 * 业务规则包括：必填、默认值、验证规则等。
 * 
 * @author yudao
 */
@Tag(name = "管理后台 - 模型字段分配", description = "提供模型字段的分配、解除、查询、规则更新等功能。用于管理模型与字段的关联关系及业务规则配置")
@RestController
@RequestMapping("/dynamicbusiness/business/models")
@Validated
public class ModelFieldAssignmentController {

    @Resource
    private ModelFieldAssignmentService modelFieldAssignmentService;

    @Resource
    private ModelFieldRulesService modelFieldRulesService;

    @PostMapping("/fields/assign")
    @Operation(
        summary = "为模型分配单个字段",
        description = "为业务模型分配单个字段，并配置业务规则（必填、默认值、验证规则等）。\n" +
            "- 字段定义（Field）只挂在 Model 上，Model 决定「这一类东西有哪些字段」「字段类型与校验规则」\n" +
            "- 如果字段已分配，会更新业务规则配置\n" +
            "- 业务规则包括：required（必填）、defaultValue（默认值）、validationRules（验证规则）"
    )
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:model-field-assignment:update')")
    public CommonResult<Boolean> assignField(@Valid @RequestBody ModelFieldAssignReqVO reqVO) {
        modelFieldAssignmentService.assignFieldToModel(
                reqVO.getModelId(),
                reqVO.getFieldId(),
                reqVO.getRequired(),
                reqVO.getIsSearchable(),
                reqVO.getIsFilterable(),
                reqVO.getIsSortable(),
                reqVO.getDefaultValue(),
                reqVO.getValidationRules());
        return success(true);
    }

    @PostMapping("/fields/batch-assign")
    @Operation(
        summary = "批量为模型分配字段",
        description = "为业务模型批量分配字段，并配置业务规则（必填、默认值、验证规则等）。\n" +
            "- 支持一次为模型分配多个字段\n" +
            "- 每个字段可以配置独立的业务规则\n" +
            "- 如果字段已分配，会更新业务规则配置"
    )
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:model-field-assignment:update')")
    public CommonResult<ModelFieldBatchOperationRespVO> batchAssignFields(@Valid @RequestBody ModelFieldBatchAssignReqVO reqVO) {
        int successCount = modelFieldAssignmentService.assignFieldsToModel(reqVO);
        return success(ModelFieldBatchOperationRespVO.of(reqVO.getFieldAssignments().size(), successCount));
    }

    @DeleteMapping("/fields/unassign")
    @Operation(
        summary = "解除模型与字段的关联（单个）",
        description = "解除模型与单个字段的关联关系。\n" +
            "- 解除前会检查是否有业务实体使用该字段，如果存在则提示用户\n" +
            "- 解除关联后，使用该模型的实体将不再包含此字段"
    )
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:model-field-assignment:update')")
    public CommonResult<Boolean> unassignField(@Valid @RequestBody ModelFieldUnassignReqVO reqVO) {
        modelFieldAssignmentService.unassignFieldFromModel(reqVO.getModelId(), reqVO.getFieldId());
        return success(true);
    }

    @DeleteMapping("/fields/batch-unassign")
    @Operation(
        summary = "批量解除模型与字段的关联",
        description = "批量解除模型与多个字段的关联关系。\n" +
            "- 支持一次解除多个字段的关联\n" +
            "- 解除前会检查是否有业务实体使用该模型，如果存在则提示用户\n" +
            "- 解除关联后，使用该模型的实体将不再包含这些字段"
    )
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:model-field-assignment:update')")
    public CommonResult<ModelFieldBatchOperationRespVO> batchUnassignFields(@Valid @RequestBody ModelFieldBatchUnassignReqVO reqVO) {
        int successCount = modelFieldAssignmentService.batchUnassignFieldsFromModel(reqVO.getModelId(), reqVO.getFieldIds());
        return success(ModelFieldBatchOperationRespVO.of(reqVO.getFieldIds().size(), successCount));
    }

    @GetMapping("/fields")
    @Operation(
        summary = "获取模型已分配的字段列表",
        description = "获取模型已分配的所有字段，包含：\n" +
            "- 字段定义信息（字段名称、类型、单位等）\n" +
            "- 业务规则配置（必填、默认值、验证规则等）\n" +
            "- 适用于模型字段管理、实体创建等场景"
    )
    @Parameter(name = "modelId", description = "模型编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('system:model-field-assignment:query')")
    public CommonResult<List<ModelFieldAssignmentRespVO>> getModelFields(@RequestParam("modelId") Long modelId) {
        return success(modelFieldAssignmentService.getModelFields(modelId));
    }

    @GetMapping("/fields/filter-meta")
    @Operation(
        summary = "获取模型筛选字段元信息",
        description = "用于数据管理页面动态渲染筛选面板：返回可筛选字段、类型、支持操作符、是否可搜索/排序等元信息。"
    )
    @Parameter(name = "modelId", description = "模型编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('system:model-field-assignment:query')")
    public CommonResult<List<ModelFilterFieldMetaRespVO>> getModelFilterFieldMeta(@RequestParam("modelId") Long modelId) {
        return success(modelFieldAssignmentService.getModelFilterFieldMeta(modelId));
    }

    @PutMapping("/fields/rules")
    @Operation(
        summary = "更新字段业务规则",
        description = "更新模型字段分配的业务规则（必填、范围、默认值等）。\n" +
            "- 字段定义中只包含基础规则（数据类型、格式）\n" +
            "- 业务规则在字段分配到 Model 时配置\n" +
            "- 更新规则会影响使用该模型的所有实体\n" +
            "- 注意：分组关联应通过 /admin-api/system/model-field-group-assignment 接口管理"
    )
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:model-field-assignment:update')")
    public CommonResult<Boolean> updateFieldRules(@Valid @RequestBody ModelFieldRulesUpdateReqVO reqVO) {
        FieldRulesUpdateReqVO rulesReqVO = new FieldRulesUpdateReqVO();
        rulesReqVO.setRequired(reqVO.getRequired());
        rulesReqVO.setIsSearchable(reqVO.getIsSearchable());
        rulesReqVO.setIsSortable(reqVO.getIsSortable());
        rulesReqVO.setIsFilterable(reqVO.getIsFilterable());
        rulesReqVO.setDefaultValue(reqVO.getDefaultValue());
        rulesReqVO.setValidationRules(reqVO.getValidationRules());
        // 注意：fieldGroupId 已移除，分组关联应通过 ModelFieldGroupAssignmentController 管理
        modelFieldRulesService.updateFieldRules(reqVO.getModelId(), reqVO.getFieldId(), rulesReqVO);
        return success(true);
    }

    // ========== 关联字段（模型侧不依赖关联字段库；仅自定义创建与查询）==========

    @PostMapping("/relation-fields/create-custom")
    @Operation(
        summary = "创建自定义关联字段",
        description = "在模型上创建自定义 ENTITY_REF 类字段，并写入 modelRelationId 等关联信息。\n" +
            "（不再与关联字段库联动；库表与服务保留供后续扩展。）"
    )
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:model-field-assignment:update')")
    public CommonResult<Long> createCustomRelationField(@Valid @RequestBody CustomRelationFieldCreateReqVO reqVO) {
        return success(modelFieldAssignmentService.createCustomRelationField(reqVO));
    }

    @GetMapping("/relation-fields")
    @Operation(
        summary = "获取模型的关联字段列表",
        description = "获取模型已分配的所有关联字段，包括：\n" +
            "- 从字段库选用的关联字段\n" +
            "- 自定义创建的关联字段\n" +
            "- 继承自模板的关联字段"
    )
    @Parameter(name = "modelId", description = "模型编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('system:model-field-assignment:query')")
    public CommonResult<List<ModelFieldAssignmentRespVO>> getModelRelationFields(
            @RequestParam("modelId") Long modelId) {
        return success(modelFieldAssignmentService.getModelRelationFields(modelId));
    }

    // ========== 批量查询接口（性能优化）==========

    @GetMapping("/fields/batch")
    @Operation(
        summary = "批量获取多个模型的字段列表",
        description = "根据模型ID列表批量获取多个模型已分配的字段。\n" +
            "- 用于前端一次性获取多个模型的字段信息，减少 N+1 查询问题\n" +
            "- 返回 Map 结构，key 为模型ID，value 为该模型的字段列表"
    )
    @Parameter(name = "modelIds", description = "模型ID列表，逗号分隔", required = true, example = "1,2,3")
    @PreAuthorize("@ss.hasPermission('system:model-field-assignment:query')")
    public CommonResult<java.util.Map<Long, List<ModelFieldAssignmentRespVO>>> getModelFieldsBatch(
            @RequestParam("modelIds") List<Long> modelIds) {
        return success(modelFieldAssignmentService.getModelFieldsBatch(modelIds));
    }

    // ========== 业务关联流程优化：级联删除与数据修复 ==========

    @DeleteMapping("/maintenance/delete-assignments-by-model-relation-id")
    @Operation(
        summary = "按模型关联ID级联删除字段分配",
        description = """
            适用场景：删除模型关联（ModelRelation）后，级联清理其派生出的字段分配与字段定义。
            业务范围：指定业务（通过 modelRelationId 归属的模型间接限定业务）。
            说明：该接口属于后端编排/运维修复能力，前端普通交互不建议直接暴露。
            """
    )
    @Parameter(name = "modelRelationId", description = "模型关联ID", required = true, example = "1001")
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:model-field-assignment:update')")
    public CommonResult<Integer> deleteByModelRelationId(@RequestParam("modelRelationId") Long modelRelationId) {
        return success(modelFieldAssignmentService.deleteByModelRelationId(modelRelationId));
    }

    @GetMapping("/maintenance/get-field-by-code")
    @Operation(
        summary = "按字段编码查询字段定义（修复辅助）",
        description = """
            适用场景：数据修复脚本或排障时，先确认字段编码是否存在。
            业务范围：全业务（fieldCode 全局唯一时可直接定位）。
            说明：返回字段原始数据对象，建议仅用于后台工具链路。
            """
    )
    @Parameter(name = "fieldCode", description = "字段编码", required = true, example = "ref_task")
    @PreAuthorize("@ss.hasPermission('system:model-field-assignment:query')")
    public CommonResult<cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO> findFieldByCode(
            @RequestParam("fieldCode") String fieldCode) {
        return success(modelFieldAssignmentService.findFieldByCode(fieldCode));
    }

    @GetMapping("/maintenance/get-assignment-by-model-and-field")
    @Operation(
        summary = "按模型与字段查询分配记录（修复辅助）",
        description = """
            适用场景：排查“字段存在但分配缺失/异常”时，快速确认分配记录状态。
            业务范围：指定业务（通过 modelId 归属的业务间接限定）。
            说明：返回分配原始数据对象，建议仅用于后台工具链路。
            """
    )
    @Parameter(name = "modelId", description = "模型ID", required = true, example = "1")
    @Parameter(name = "fieldId", description = "字段ID", required = true, example = "101")
    @PreAuthorize("@ss.hasPermission('system:model-field-assignment:query')")
    public CommonResult<cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelFieldAssignmentDO> findAssignmentByModelIdAndFieldId(
            @RequestParam("modelId") Long modelId,
            @RequestParam("fieldId") Long fieldId) {
        return success(modelFieldAssignmentService.findAssignmentByModelIdAndFieldId(modelId, fieldId));
    }

    @PostMapping("/maintenance/create-assignment-for-existing-field")
    @Operation(
        summary = "为已存在字段补建分配记录",
        description = """
            适用场景：历史数据修复中，字段已存在但 ModelFieldAssignment 缺失时补建记录。
            业务范围：指定业务（通过 modelId 归属的业务间接限定）。
            说明：通常由修复任务调用，不建议前端常规页面直接使用。
            """
    )
    @Parameter(name = "modelId", description = "模型ID", required = true, example = "1")
    @Parameter(name = "fieldId", description = "字段ID", required = true, example = "101")
    @Parameter(name = "modelRelationId", description = "模型关联ID（可选）", required = false, example = "1001")
    @ApiAccessLog(operateType = CREATE)
    @PreAuthorize("@ss.hasPermission('system:model-field-assignment:update')")
    public CommonResult<Long> createAssignmentForExistingField(
            @RequestParam("modelId") Long modelId,
            @RequestParam("fieldId") Long fieldId,
            @RequestParam(value = "modelRelationId", required = false) Long modelRelationId) {
        return success(modelFieldAssignmentService.createAssignmentForExistingField(modelId, fieldId, modelRelationId));
    }

    @PutMapping("/fields/update-assignment-relation")
    @Operation(
        summary = "更新字段分配的关联信息",
        description = """
            适用场景：数据修复中补齐/纠正字段分配记录上的 modelRelationId。
            业务范围：指定业务（通过 assignmentId 归属的模型间接限定）。
            说明：该接口属于修复能力，建议结合审计日志和二次确认后执行。
            """
    )
    @Parameter(name = "assignmentId", description = "字段分配ID", required = true, example = "5001")
    @Parameter(name = "modelRelationId", description = "模型关联ID（可选，传空表示清空）", required = false, example = "1001")
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:model-field-assignment:update')")
    public CommonResult<Boolean> updateAssignmentRelationInfo(
            @RequestParam("assignmentId") Long assignmentId,
            @RequestParam(value = "modelRelationId", required = false) Long modelRelationId) {
        modelFieldAssignmentService.updateAssignmentRelationInfo(assignmentId, modelRelationId);
        return success(true);
    }

    // ========== 批量关联字段分配（性能优化）==========

    @PostMapping("/associations/batch-assign-field-to-models")
    @Operation(
        summary = "批量分配关联字段到多个模型",
        description = """
            适用场景：视图创建/业务初始化时，需一次性把同一个关联字段分配到多个模型。
            业务范围：指定业务（调用方必须保证 modelIds 属于同一业务，建议同时传 targetEntityTypeCode）。
            说明：前端若无“批量操作”入口，建议由导入任务或后端编排任务触发。
            """
    )
    @Parameter(name = "modelIds", description = "模型ID列表", required = true, example = "1,2,3")
    @Parameter(name = "fieldId", description = "关联字段ID", required = true, example = "101")
    @Parameter(name = "targetEntityTypeCode", description = "目标业务类型编码（必填）", required = true, example = "task")
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:model-field-assignment:update')")
    public CommonResult<ModelFieldBatchOperationRespVO> batchAssignAssociationFieldToModels(
            @RequestParam("modelIds") List<Long> modelIds,
            @RequestParam("fieldId") Long fieldId,
            @RequestParam("targetEntityTypeCode") String targetEntityTypeCode) {
        int successCount = modelFieldAssignmentService.batchAssignAssociationFieldToModels(modelIds, fieldId, targetEntityTypeCode);
        return success(ModelFieldBatchOperationRespVO.of(modelIds.size(), successCount));
    }

    @PostMapping("/associations/smart-assign-field-to-models")
    @Operation(
        summary = "智能分配关联字段到多个模型",
        description = """
            适用场景：模型数量不确定时，自动选择合适策略进行关联字段分配。
            业务范围：指定业务（调用方必须保证 modelIds 属于同一业务，建议同时传 targetEntityTypeCode）。
            说明：本接口用于性能优化编排，行为与批量分配一致但包含策略选择。
            """
    )
    @Parameter(name = "modelIds", description = "模型ID列表", required = true, example = "1,2,3")
    @Parameter(name = "fieldId", description = "关联字段ID", required = true, example = "101")
    @Parameter(name = "targetEntityTypeCode", description = "目标业务类型编码（必填）", required = true, example = "task")
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:model-field-assignment:update')")
    public CommonResult<ModelFieldBatchOperationRespVO> smartAssignAssociationFieldToModels(
            @RequestParam("modelIds") List<Long> modelIds,
            @RequestParam("fieldId") Long fieldId,
            @RequestParam("targetEntityTypeCode") String targetEntityTypeCode) {
        int successCount = modelFieldAssignmentService.smartAssignAssociationFieldToModels(modelIds, fieldId, targetEntityTypeCode);
        return success(ModelFieldBatchOperationRespVO.of(modelIds.size(), successCount));
    }
}

