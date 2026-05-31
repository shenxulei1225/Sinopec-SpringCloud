package cn.cheers.x.module.dynamicbusiness.controller.admin.capability;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.capability.vo.InstanceCapabilitySummaryRespVO;
import cn.cheers.x.module.dynamicbusiness.service.capability.CapabilityRegistryRebuildService;
import cn.cheers.x.module.dynamicbusiness.service.capability.CapabilityRegistryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.UPDATE;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 实例能力注册表")
@RestController
@RequestMapping("/dynamicbusiness/capability")
@Validated
public class CapabilityController {

    @Resource
    private CapabilityRegistryService capabilityRegistryService;
    @Resource
    private CapabilityRegistryRebuildService capabilityRegistryRebuildService;

    @GetMapping("/instances")
    @Operation(summary = "获取实例能力摘要列表")
    @PreAuthorize("@ss.hasPermission('system:model-field-assignment:query')")
    public CommonResult<List<InstanceCapabilitySummaryRespVO>> listInstances(
            @RequestParam(value = "businessTypeCode", required = false) String businessTypeCode,
            @RequestParam(value = "domain", required = false) String domain) {
        return success(capabilityRegistryService.listInstances(businessTypeCode, domain));
    }

    @GetMapping("/instances/detail")
    @Operation(summary = "获取实例能力完整契约")
    @PreAuthorize("@ss.hasPermission('system:model-field-assignment:query')")
    public CommonResult<Map<String, Object>> getContract(
            @RequestParam("instanceKey") String instanceKey,
            @RequestParam(value = "rebuildIfMissing", defaultValue = "true") boolean rebuildIfMissing) {
        if (rebuildIfMissing) {
            return success(capabilityRegistryService.getContractOrRebuild(instanceKey));
        }
        return success(capabilityRegistryService.getContract(instanceKey));
    }

    @GetMapping("/instances/filters")
    @Operation(summary = "获取实例 filter 能力列表")
    @PreAuthorize("@ss.hasPermission('system:model-field-assignment:query')")
    public CommonResult<List<Map<String, Object>>> getFilters(@RequestParam("instanceKey") String instanceKey) {
        return success(capabilityRegistryService.getFilters(instanceKey));
    }

    @PostMapping("/internal/rebuild/model/{modelId}")
    @Operation(summary = "重建模型实体能力")
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:model-field-assignment:update')")
    public CommonResult<Boolean> rebuildModel(@PathVariable("modelId") Long modelId) {
        capabilityRegistryRebuildService.rebuildAfterModelFieldChange(modelId);
        return success(true);
    }

    @PostMapping("/internal/rebuild/business-type/{businessTypeCode}")
    @Operation(summary = "重建业务类型下全部实例能力")
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:model-field-assignment:update')")
    public CommonResult<Boolean> rebuildBusinessType(@PathVariable("businessTypeCode") String businessTypeCode) {
        capabilityRegistryRebuildService.rebuildAllForBusinessType(businessTypeCode);
        return success(true);
    }

    @PostMapping("/internal/rebuild/system")
    @Operation(summary = "重建 System 模块全部固定资源能力（dept/user/role/…）")
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:model-field-assignment:update')")
    public CommonResult<Boolean> rebuildAllSystemCapabilities() {
        capabilityRegistryRebuildService.rebuildAllSystemCapabilities();
        return success(true);
    }

    @PostMapping("/internal/rebuild/system/{resourceCode}")
    @Operation(summary = "重建单个 System 资源能力", description = "resourceCode 如 dept、user、role；instanceKey = system:{resourceCode}")
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:model-field-assignment:update')")
    public CommonResult<Boolean> rebuildSystemCapability(@PathVariable("resourceCode") String resourceCode) {
        capabilityRegistryRebuildService.rebuildSystemCapability(resourceCode);
        return success(true);
    }
}
