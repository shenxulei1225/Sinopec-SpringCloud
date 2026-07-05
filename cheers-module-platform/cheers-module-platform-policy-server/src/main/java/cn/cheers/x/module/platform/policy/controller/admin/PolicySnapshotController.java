package cn.cheers.x.module.platform.policy.controller.admin;

import cn.cheers.x.module.platform.policy.api.dto.PolicySnapshotRespDTO;
import cn.cheers.x.module.platform.policy.service.PolicySnapshotService;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 平台策略快照")
@RestController
@RequestMapping("/platform/policy")
public class PolicySnapshotController {

    @Resource
    private PolicySnapshotService policySnapshotService;

    @GetMapping("/snapshots/{id}")
    @Operation(summary = "读取策略快照")
    public CommonResult<PolicySnapshotRespDTO> get(@PathVariable("id") String id) {
        return success(policySnapshotService.getSnapshot(id));
    }
}
