package cn.cheers.x.module.platform.policy.api;

import cn.cheers.x.module.platform.policy.api.dto.PolicyResolveForRunReqDTO;
import cn.cheers.x.module.platform.policy.api.dto.PolicyRunContextDTO;
import cn.cheers.x.module.platform.policy.api.dto.PolicySnapshotRespDTO;
import cn.cheers.x.module.platform.policy.service.PolicySnapshotService;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@RestController
@Validated
public class PolicyResolveApiImpl implements PolicyResolveApi {

    @Resource
    private PolicySnapshotService policySnapshotService;

    @Override
    public CommonResult<PolicyRunContextDTO> resolveForRun(PolicyResolveForRunReqDTO request) {
        return success(policySnapshotService.resolveForRun(request));
    }

    @Override
    public CommonResult<PolicySnapshotRespDTO> getSnapshot(String policySnapshotId) {
        return success(policySnapshotService.getSnapshot(policySnapshotId));
    }
}
