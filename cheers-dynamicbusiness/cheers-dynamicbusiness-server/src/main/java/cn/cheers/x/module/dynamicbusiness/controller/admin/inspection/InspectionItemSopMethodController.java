package cn.cheers.x.module.dynamicbusiness.controller.admin.inspection;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.inspection.vo.InspectionItemSopMethodRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.inspection.vo.InspectionItemSopMethodUpsertReqVO;
import cn.cheers.x.module.dynamicbusiness.service.inspection.InspectionItemSopMethodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 检查项方法：检查项 × 执行手段 → SOP 模板。
 * 权威表 V44 {@code dynamic_inspection_item_sop}；不替代设备实例绑定。
 */
@Tag(name = "管理后台 - 检查项 SOP 方法")
@RestController
@RequestMapping("/dynamicbusiness/inspection-item")
@Validated
public class InspectionItemSopMethodController {

    @Resource
    private InspectionItemSopMethodService inspectionItemSopMethodService;

    @GetMapping("/{inspectionItemId}/sop-methods")
    @Operation(summary = "列出检查项的方法行（各手段默认 SOP 模板）")
    @Parameter(name = "inspectionItemId", description = "检查项实体 id", required = true)
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<List<InspectionItemSopMethodRespVO>> listMethods(
            @PathVariable("inspectionItemId") Long inspectionItemId) {
        return success(inspectionItemSopMethodService.listByInspectionItemId(inspectionItemId));
    }

    @PutMapping("/sop-methods")
    @Operation(summary = "upsert 检查项方法：同检查项同手段更新模板引用")
    @PreAuthorize("@ss.hasPermission('system:entity:update')")
    public CommonResult<Long> upsertMethod(@Valid @RequestBody InspectionItemSopMethodUpsertReqVO reqVO) {
        return success(inspectionItemSopMethodService.upsert(reqVO));
    }
}
