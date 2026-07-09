package cn.cheers.x.module.dynamicbusiness.controller.admin.template;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.template.vo.TemplateCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.template.vo.TemplateFieldAssignmentReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.template.vo.TemplateFieldAssignmentRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.template.vo.TemplatePageReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.template.vo.TemplateRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.template.vo.TemplateUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.service.template.TemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.*;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 字段模板 Controller
 * 
 * Template（模板）是字段组合的预设模板，作为创建 Model 的起点。
 * Template 定义一组常用字段组合，用户创建 Model 时可选择一个 Template，
 * 系统将 Template 的字段**复制**到新 Model 中。
 * 
 * Template 与 Model 是"复制"关系而非"继承"关系：
 * - 创建 Model 时，Template 的字段被复制到 Model
 * - 之后 Model 与 Template 完全独立
 * - 修改 Template 不影响已创建的 Model
 * - 修改 Model 也不影响 Template
 * 
 * @author yudao
 */
@Tag(name = "管理后台 - 字段模板管理", description = "提供字段模板的创建、更新、删除、查询等功能。模板用于定义一组常用字段组合，作为创建 Model 的起点")
@RestController
@RequestMapping("/dynamicbusiness/business/templates")
@Validated
public class TemplateController {

    @Resource
    private TemplateService templateService;

    @PostMapping("/create")
    @Operation(
        summary = "创建字段模板",
        description = "创建一个新的字段模板，用于定义一组常用字段组合。\n" +
            "- 模板编码由系统自动生成（格式：TPL-{UUID}）\n" +
            "- 模板名称在系统内必须唯一\n" +
            "- 创建后可以通过字段分配接口为模板添加字段"
    )
    @ApiAccessLog(operateType = CREATE)
    @PreAuthorize("@ss.hasPermission('system:template:create')")
    public CommonResult<Long> createTemplate(@Valid @RequestBody TemplateCreateReqVO reqVO) {
        return success(templateService.createTemplate(reqVO));
    }

    @PutMapping("/update")
    @Operation(
        summary = "更新字段模板",
        description = "更新字段模板的基本信息（名称、描述、状态等）。\n" +
            "- 业务类型编码不可修改\n" +
            "- 系统预设模板标记不可修改\n" +
            "- 更新不会影响已分配的字段"
    )
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:template:update')")
    public CommonResult<Boolean> updateTemplate(@Valid @RequestBody TemplateUpdateReqVO reqVO) {
        templateService.updateTemplate(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(
        summary = "删除字段模板",
        description = "删除字段模板。\n" +
            "- 系统预设模板不允许删除\n" +
            "- 删除会同时删除模板与字段的分配关系\n" +
            "- 删除不会影响已基于该模板创建的 Model（因为是复制关系）"
    )
    @Parameter(name = "id", description = "模板编号", required = true, example = "1")
    @ApiAccessLog(operateType = DELETE)
    @PreAuthorize("@ss.hasPermission('system:template:delete')")
    public CommonResult<Boolean> deleteTemplate(@RequestParam("id") Long id) {
        templateService.deleteTemplate(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(
        summary = "获取字段模板详情",
        description = "根据模板ID获取字段模板的详细信息，包含：\n" +
            "- 基本信息（名称、描述、状态、业务类型编码等）\n" +
            "- 字段数量统计"
    )
    @Parameter(name = "id", description = "模板编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('system:template:query')")
    public CommonResult<TemplateRespVO> getTemplate(@RequestParam("id") Long id) {
        return success(templateService.getTemplate(id));
    }

    @GetMapping("/list")
    @Operation(
        summary = "查询字段模板列表（不分页）",
        description = "支持按业务类型编码查询模板列表。\n" +
            "- 如果不指定业务类型编码，则返回所有模板\n" +
            "- 适用于下拉选择等场景"
    )
    @Parameter(name = "entityTypeCode", description = "业务类型编码（可选）", example = "equipment")
    @PreAuthorize("@ss.hasPermission('system:template:query')")
    public CommonResult<List<TemplateRespVO>> listTemplates(
            @RequestParam(value = "entityTypeCode", required = false) String entityTypeCode) {
        return success(templateService.listTemplates(entityTypeCode));
    }

    @GetMapping("/page")
    @Operation(
        summary = "分页查询字段模板",
        description = "支持按业务类型编码、关键词（模板名称/描述）、状态进行分页查询。\n" +
            "- 关键词会同时匹配模板名称和描述字段（模糊查询）"
    )
    @PreAuthorize("@ss.hasPermission('system:template:query')")
    public CommonResult<PageResult<TemplateRespVO>> pageTemplate(@Valid TemplatePageReqVO reqVO) {
        return success(templateService.pageTemplate(reqVO));
    }

    @GetMapping("/search")
    @Operation(
        summary = "搜索字段模板（不分页）",
        description = "根据关键词搜索字段模板（按模板名称、描述进行模糊搜索）。\n" +
            "- 适用于模板选择器等场景"
    )
    @Parameter(name = "keyword", description = "关键词（必填）", required = true, example = "设备")
    @Parameter(name = "entityTypeCode", description = "业务类型编码（可选）", example = "equipment")
    @PreAuthorize("@ss.hasPermission('system:template:query')")
    public CommonResult<List<TemplateRespVO>> searchTemplates(
            @RequestParam("keyword") String keyword,
            @RequestParam(value = "entityTypeCode", required = false) String entityTypeCode) {
        return success(templateService.searchTemplates(keyword, entityTypeCode));
    }

    @PostMapping("/copy")
    @Operation(
        summary = "复制字段模板",
        description = "基于现有模板创建新模板，复制所有字段分配。\n" +
            "- 新模板不是系统预设模板\n" +
            "- 新模板名称必须唯一"
    )
    @Parameter(name = "sourceTemplateId", description = "源模板ID", required = true, example = "1")
    @Parameter(name = "newName", description = "新模板名称", required = true, example = "设备基础模板-副本")
    @ApiAccessLog(operateType = CREATE)
    @PreAuthorize("@ss.hasPermission('system:template:create')")
    public CommonResult<Long> copyTemplate(
            @RequestParam("sourceTemplateId") Long sourceTemplateId,
            @RequestParam("newName") String newName) {
        return success(templateService.copyTemplate(sourceTemplateId, newName));
    }

    // ========== 字段分配相关 ==========

    @PostMapping("/fields/assign")
    @Operation(
        summary = "为模板分配字段",
        description = "将一个字段分配到模板中。\n" +
            "- 同一字段不能重复分配到同一模板\n" +
            "- 可以设置排序值、是否必填、默认值等配置"
    )
    @Parameter(name = "templateId", description = "模板ID", required = true, example = "1")
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:template:update')")
    public CommonResult<Long> assignFieldToTemplate(
            @RequestParam("templateId") Long templateId,
            @Valid @RequestBody TemplateFieldAssignmentReqVO reqVO) {
        return success(templateService.assignFieldToTemplate(templateId, reqVO));
    }

    @PostMapping("/fields/batch-assign")
    @Operation(
        summary = "批量为模板分配字段",
        description = "批量将多个字段分配到模板中。\n" +
            "- 已存在的分配会被跳过\n" +
            "- 不存在的字段会被跳过"
    )
    @Parameter(name = "templateId", description = "模板ID", required = true, example = "1")
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:template:update')")
    public CommonResult<Boolean> batchAssignFieldsToTemplate(
            @RequestParam("templateId") Long templateId,
            @Valid @RequestBody List<TemplateFieldAssignmentReqVO> reqVOList) {
        templateService.batchAssignFieldsToTemplate(templateId, reqVOList);
        return success(true);
    }

    @DeleteMapping("/fields/unassign")
    @Operation(
        summary = "取消模板的字段分配",
        description = "从模板中移除一个字段。"
    )
    @Parameter(name = "templateId", description = "模板ID", required = true, example = "1")
    @Parameter(name = "fieldId", description = "字段ID", required = true, example = "1")
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:template:update')")
    public CommonResult<Boolean> unassignFieldFromTemplate(
            @RequestParam("templateId") Long templateId,
            @RequestParam("fieldId") Long fieldId) {
        templateService.unassignFieldFromTemplate(templateId, fieldId);
        return success(true);
    }

    @PutMapping("/fields/update")
    @Operation(
        summary = "更新模板字段分配的配置",
        description = "更新模板中某个字段的配置（排序值、是否必填、默认值）。"
    )
    @Parameter(name = "templateId", description = "模板ID", required = true, example = "1")
    @Parameter(name = "fieldId", description = "字段ID", required = true, example = "1")
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:template:update')")
    public CommonResult<Boolean> updateFieldAssignment(
            @RequestParam("templateId") Long templateId,
            @RequestParam("fieldId") Long fieldId,
            @Valid @RequestBody TemplateFieldAssignmentReqVO reqVO) {
        templateService.updateFieldAssignment(templateId, fieldId, reqVO);
        return success(true);
    }

    @GetMapping("/fields/list")
    @Operation(
        summary = "获取模板已分配的字段列表",
        description = "获取指定模板已分配的所有字段，包含字段的基本信息和分配配置。"
    )
    @Parameter(name = "templateId", description = "模板ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('system:template:query')")
    public CommonResult<List<TemplateFieldAssignmentRespVO>> getTemplateFields(
            @RequestParam("templateId") Long templateId) {
        return success(templateService.getTemplateFields(templateId));
    }
}
