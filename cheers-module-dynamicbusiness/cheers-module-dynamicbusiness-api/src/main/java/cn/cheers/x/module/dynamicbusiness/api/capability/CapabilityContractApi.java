package cn.cheers.x.module.dynamicbusiness.api.capability;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import cn.cheers.x.module.dynamicbusiness.api.capability.dto.CapabilityInstanceSummaryDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * 数据能力契约 RPC（供 platform-resource 等模块拉取契约并生成 Props）。
 */
@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 数据能力契约")
public interface CapabilityContractApi {

    String PREFIX = "/dynamicbusiness/capability";

    @GetMapping(PREFIX + "/instances")
    @Operation(summary = "获取实例能力摘要列表")
    CommonResult<List<CapabilityInstanceSummaryDTO>> listInstances(
            @RequestParam(value = "businessTypeCode", required = false) String businessTypeCode,
            @RequestParam(value = "domain", required = false) String domain);

    @GetMapping(PREFIX + "/instances/detail")
    @Operation(summary = "获取实例能力完整契约")
    @Parameter(name = "instanceKey", description = "如 system:dept、dynamic-model:equipment", required = true)
    CommonResult<Map<String, Object>> getContract(
            @RequestParam("instanceKey") String instanceKey,
            @RequestParam(value = "rebuildIfMissing", defaultValue = "true") boolean rebuildIfMissing);

    @PostMapping(PREFIX + "/internal/rebuild/system")
    @Operation(summary = "重建全部 System 固定资源能力契约")
    CommonResult<Boolean> rebuildAllSystemCapabilities();

    @PostMapping(PREFIX + "/internal/rebuild/dynamic")
    @Operation(summary = "重建全部动态 model/entity 能力契约")
    CommonResult<Boolean> rebuildAllDynamicCapabilities();
}
