package cn.cheers.x.module.dynamicbusiness.controller.admin.relation;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.service.relation.RelationDataRepairService;
import cn.cheers.x.module.dynamicbusiness.service.relation.RelationDataRepairService.RepairResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 关联数据修复 Controller
 * 
 * 提供关联数据修复的管理接口，用于修复历史数据中缺失的关联信息。
 * 
 * 需求：5.1, 5.2, 5.3
 * 
 * @author yudao
 */
@Tag(name = "管理后台 - 关联数据修复")
@RestController
@RequestMapping("/dynamicbusiness/relation-data-repair")
@Validated
public class RelationDataRepairController {

    @Resource
    private RelationDataRepairService relationDataRepairService;

    @PostMapping("/repair-all")
    @Operation(summary = "修复所有关联字段数据", 
            description = "修复所有 ENTITY_REF 类型字段的 ModelFieldAssignment 记录中缺失的关联信息")
    @PreAuthorize("@ss.hasPermission('system:relation-data-repair:repair')")
    public CommonResult<RepairResultVO> repairAllRelationFieldData() {
        RepairResult result = relationDataRepairService.repairRelationFieldData();
        return success(convertToVO(result));
    }

    @PostMapping("/repair-by-model")
    @Operation(summary = "修复指定模型的关联字段数据",
            description = "修复指定模型的 ENTITY_REF 类型字段的 ModelFieldAssignment 记录中缺失的关联信息")
    @Parameter(name = "modelId", description = "模型ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('system:relation-data-repair:repair')")
    public CommonResult<RepairResultVO> repairRelationFieldDataByModel(
            @RequestParam("modelId") Long modelId) {
        RepairResult result = relationDataRepairService.repairRelationFieldDataByModelId(modelId);
        return success(convertToVO(result));
    }

    /**
     * 将 RepairResult 转换为 VO
     */
    private RepairResultVO convertToVO(RepairResult result) {
        return new RepairResultVO(
                result.getTotalCount(),
                result.getSuccessCount(),
                result.getFailedCount(),
                result.getSkippedCount(),
                result.getDetails()
        );
    }

    /**
     * 修复结果 VO
     */
    public record RepairResultVO(
            int totalCount,
            int successCount,
            int failedCount,
            int skippedCount,
            String details
    ) {}
}
