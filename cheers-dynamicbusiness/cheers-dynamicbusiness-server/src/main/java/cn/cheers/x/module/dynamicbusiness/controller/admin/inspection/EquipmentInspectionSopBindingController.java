package cn.cheers.x.module.dynamicbusiness.controller.admin.inspection;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.inspection.vo.EquipmentInspectionSopBindingRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.inspection.vo.EquipmentInspectionSopCreateInstanceReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.inspection.vo.EquipmentInspectionSopUpsertReqVO;
import cn.cheers.x.module.dynamicbusiness.service.inspection.EquipmentInspectionSopBindingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 设备检查绑定：设备 + 检查项 + 手段 → 独占 SOP 实例。
 * 权威表 V75 {@code dynamic_equipment_inspection_sop_binding}。
 */
@Tag(name = "管理后台 - 设备检查 SOP 绑定")
@RestController
@RequestMapping("/dynamicbusiness/equipment-inspection-sop")
@Validated
public class EquipmentInspectionSopBindingController {

    @Resource
    private EquipmentInspectionSopBindingService equipmentInspectionSopBindingService;

    @GetMapping("/binding")
    @Operation(summary = "读单条绑定；无则 data=null")
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<EquipmentInspectionSopBindingRespVO> getBinding(
            @RequestParam("equipmentId") @NotNull Long equipmentId,
            @RequestParam("inspectionItemId") @NotNull Long inspectionItemId,
            @RequestParam("executionMeans") @NotBlank String executionMeans) {
        return success(equipmentInspectionSopBindingService.getBinding(
                equipmentId, inspectionItemId, executionMeans));
    }

    @GetMapping("/bindings")
    @Operation(summary = "列出设备×检查项下各手段绑定")
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<List<EquipmentInspectionSopBindingRespVO>> listBindings(
            @RequestParam("equipmentId") @NotNull Long equipmentId,
            @RequestParam("inspectionItemId") @NotNull Long inspectionItemId) {
        return success(equipmentInspectionSopBindingService.listByEquipmentAndItem(
                equipmentId, inspectionItemId));
    }

    @PutMapping("/binding")
    @Operation(summary = "upsert 绑定；禁止同一实例绑两台设备")
    @PreAuthorize("@ss.hasPermission('system:entity:update')")
    public CommonResult<Boolean> upsertBinding(@Valid @RequestBody EquipmentInspectionSopUpsertReqVO reqVO) {
        equipmentInspectionSopBindingService.upsertBinding(reqVO);
        return success(true);
    }

    @PostMapping("/create-instance-from-template")
    @Operation(summary = "从 SOP 模板新建独占实例并绑定")
    @PreAuthorize("@ss.hasPermission('system:entity:create')")
    public CommonResult<Map<String, Long>> createInstanceFromTemplate(
            @Valid @RequestBody EquipmentInspectionSopCreateInstanceReqVO reqVO) {
        long sopInstanceId = equipmentInspectionSopBindingService.createInstanceFromTemplate(reqVO);
        Map<String, Long> body = new LinkedHashMap<>();
        body.put("sopInstanceId", sopInstanceId);
        return success(body);
    }
}
