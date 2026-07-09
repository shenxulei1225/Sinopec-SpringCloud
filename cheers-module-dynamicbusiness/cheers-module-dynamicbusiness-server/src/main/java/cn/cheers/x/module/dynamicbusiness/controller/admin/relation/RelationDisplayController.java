package cn.cheers.x.module.dynamicbusiness.controller.admin.relation;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.service.relation.RelationDisplayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 关联展示 Controller
 * 
 * 提供关联字段展示值的获取功能,支持:
 * - 使用关联字段配置的展示字段(displayFieldCode)
 * - 如果未配置,则使用实体的 name 字段或 ID 作为展示值
 * 
 * 需求:FR-BDA-040~042
 * 
 * @author yudao
 */
@Tag(name = "管理后台 - 关联展示", description = "提供关联字段展示值的获取功能")
@RestController
@RequestMapping("/dynamicbusiness/relation-display")
@Validated
public class RelationDisplayController {

    @Resource
    private RelationDisplayService relationDisplayService;

    @GetMapping("/value")
    @Operation(
        summary = "获取关联实体的展示值",
        description = """
            获取指定关联实体的展示值。
            展示值获取规则:
            1. 如果指定了 displayFieldCode,使用该字段的值
            2. 如果未指定,返回实体的 name 字段值(如果存在)
            3. 如果都没有,返回实体 ID
            """
    )
    @Parameter(name = "targetEntityType", description = "目标业务类型编码", required = true, example = "personnel")
    @Parameter(name = "targetModelCode", description = "目标 Model 编码", required = true, example = "employee")
    @Parameter(name = "entityId", description = "实体 ID", required = true, example = "1")
    @Parameter(name = "displayFieldCode", description = "展示字段编码(可选,优先使用)", example = "name")
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<String> getDisplayValue(
            @RequestParam("targetEntityType") String targetEntityType,
            @RequestParam("targetModelCode") String targetModelCode,
            @RequestParam("entityId") Long entityId,
            @RequestParam(value = "displayFieldCode", required = false) String displayFieldCode) {
        return success(relationDisplayService.getDisplayValue(
                targetEntityType, targetModelCode, entityId, displayFieldCode));
    }

    @GetMapping("/values")
    @Operation(
        summary = "批量获取关联实体的展示值",
        description = "批量获取多个关联实体的展示值。"
    )
    @Parameter(name = "targetEntityType", description = "目标业务类型编码", required = true, example = "personnel")
    @Parameter(name = "targetModelCode", description = "目标 Model 编码", required = true, example = "employee")
    @Parameter(name = "entityIds", description = "实体 ID 列表(逗号分隔)", required = true, example = "1,2,3")
    @Parameter(name = "displayFieldCode", description = "展示字段编码(可选,优先使用)", example = "name")
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<Map<Long, String>> getDisplayValues(
            @RequestParam("targetEntityType") String targetEntityType,
            @RequestParam("targetModelCode") String targetModelCode,
            @RequestParam("entityIds") List<Long> entityIds,
            @RequestParam(value = "displayFieldCode", required = false) String displayFieldCode) {
        return success(relationDisplayService.getDisplayValues(
                targetEntityType, targetModelCode, entityIds, displayFieldCode));
    }

    @GetMapping("/effective-field-code")
    @Operation(
        summary = "获取实际使用的展示字段编码",
        description = """
            获取关联展示时实际使用的字段编码。
            返回规则：
            1. 如果指定了 displayFieldCode,返回该值
            2. 如果未指定,返回 null(后续会使用实体的 name 字段或 ID)
            """
    )
    @Parameter(name = "targetEntityType", description = "目标业务类型编码", required = true, example = "personnel")
    @Parameter(name = "targetModelCode", description = "目标 Model 编码", required = true, example = "employee")
    @Parameter(name = "displayFieldCode", description = "展示字段编码(可选,优先使用)", example = "name")
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<String> getEffectiveDisplayFieldCode(
            @RequestParam("targetEntityType") String targetEntityType,
            @RequestParam("targetModelCode") String targetModelCode,
            @RequestParam(value = "displayFieldCode", required = false) String displayFieldCode) {
        return success(relationDisplayService.getEffectiveDisplayFieldCode(
                targetEntityType, targetModelCode, displayFieldCode));
    }

    @GetMapping("/info")
    @Operation(
        summary = "获取关联实体的展示信息",
        description = "获取关联实体的完整展示信息,包括展示值和使用的字段编码。"
    )
    @Parameter(name = "targetEntityType", description = "目标业务类型编码", required = true, example = "personnel")
    @Parameter(name = "targetModelCode", description = "目标 Model 编码", required = true, example = "employee")
    @Parameter(name = "entityId", description = "实体 ID", required = true, example = "1")
    @Parameter(name = "displayFieldCode", description = "展示字段编码(可选,优先使用)", example = "name")
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<RelationDisplayService.RelationDisplayInfo> getDisplayInfo(
            @RequestParam("targetEntityType") String targetEntityType,
            @RequestParam("targetModelCode") String targetModelCode,
            @RequestParam("entityId") Long entityId,
            @RequestParam(value = "displayFieldCode", required = false) String displayFieldCode) {
        return success(relationDisplayService.getDisplayInfo(
                targetEntityType, targetModelCode, entityId, displayFieldCode));
    }
}
