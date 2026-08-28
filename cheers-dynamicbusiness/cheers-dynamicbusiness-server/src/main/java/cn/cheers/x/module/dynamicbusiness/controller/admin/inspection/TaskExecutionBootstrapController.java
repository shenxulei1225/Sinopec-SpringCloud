package cn.cheers.x.module.dynamicbusiness.controller.admin.inspection;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.inspection.vo.TaskExecutionBootstrapReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.inspection.vo.TaskExecutionBootstrapRespVO;
import cn.cheers.x.module.dynamicbusiness.service.inspection.TaskExecutionBootstrapService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 任务开跑：锁定 SOP 快照 + 批量步骤实例。不触发算路。
 */
@Tag(name = "管理后台 - 任务执行开跑")
@RestController
@RequestMapping("/dynamicbusiness/task-execution")
@Validated
public class TaskExecutionBootstrapController {

    @Resource
    private TaskExecutionBootstrapService taskExecutionBootstrapService;

    @PostMapping("/bootstrap-steps")
    @Operation(summary = "锁定 merge 快照并批量创建步骤实例；有 gapCodes 拒绝")
    @PreAuthorize("@ss.hasPermission('system:entity:create')")
    public CommonResult<TaskExecutionBootstrapRespVO> bootstrapSteps(
            @Valid @RequestBody TaskExecutionBootstrapReqVO reqVO) {
        return success(taskExecutionBootstrapService.bootstrap(reqVO));
    }
}
