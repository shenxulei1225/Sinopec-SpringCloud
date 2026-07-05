package cn.cheers.x.module.platform.policy.api;

import cn.cheers.x.module.platform.policy.api.dto.PolicyResolveForRunReqDTO;
import cn.cheers.x.module.platform.policy.api.dto.PolicyRunContextDTO;
import cn.cheers.x.module.platform.policy.api.dto.PolicySnapshotRespDTO;
import cn.cheers.x.module.platform.policy.enums.ApiConstants;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 平台策略中心")
public interface PolicyResolveApi {

    String PREFIX = ApiConstants.PREFIX;

    @PostMapping(PREFIX + "/resolve-for-run")
    @Operation(summary = "为排程运行解析策略集并生成快照")
    CommonResult<PolicyRunContextDTO> resolveForRun(@Valid @RequestBody PolicyResolveForRunReqDTO request);

    @GetMapping(PREFIX + "/snapshots/{policySnapshotId}")
    @Operation(summary = "读取策略快照")
    CommonResult<PolicySnapshotRespDTO> getSnapshot(@PathVariable("policySnapshotId") String policySnapshotId);
}
