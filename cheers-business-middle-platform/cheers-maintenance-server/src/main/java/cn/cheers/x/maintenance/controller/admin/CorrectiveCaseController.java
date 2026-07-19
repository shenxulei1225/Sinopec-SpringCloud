package cn.cheers.x.maintenance.controller.admin;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.maintenance.controller.admin.vo.corrective.*;
import cn.cheers.x.maintenance.service.corrective.CorrectiveCaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 故障维修主流程")
@RestController
@RequestMapping("/maintenance/corrective-cases")
@Validated
public class CorrectiveCaseController {

    @Resource private CorrectiveCaseService correctiveCaseService;

    @PostMapping
    @PreAuthorize("@ss.hasPermission('maintenance:corrective:create')")
    public CommonResult<Long> create(@Valid @RequestBody CorrectiveCaseCreateReqVO reqVO) {
        return success(correctiveCaseService.create(reqVO));
    }

    @GetMapping("/page")
    @PreAuthorize("@ss.hasPermission('maintenance:corrective:query')")
    public CommonResult<PageResult<CorrectiveCaseRespVO>> page(@Valid CorrectiveCasePageReqVO reqVO) {
        return success(correctiveCaseService.page(reqVO));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@ss.hasPermission('maintenance:corrective:query')")
    public CommonResult<CorrectiveCaseRespVO> get(@PathVariable Long id) {
        return success(correctiveCaseService.get(id));
    }

    @PostMapping("/{id}/submit-approval")
    @PreAuthorize("@ss.hasPermission('maintenance:corrective:approve')")
    public CommonResult<Boolean> submitApproval(@PathVariable Long id) {
        correctiveCaseService.submitApproval(id);
        return success(true);
    }

    @PostMapping("/{id}/approve")
    @Operation(summary = "同步审批（skip-approval 或测试）")
    @PreAuthorize("@ss.hasPermission('maintenance:corrective:approve')")
    public CommonResult<Boolean> approve(@PathVariable Long id, @RequestParam("approved") boolean approved) {
        correctiveCaseService.approve(id, approved);
        return success(true);
    }

    @PostMapping("/{id}/dispatch")
    @PreAuthorize("@ss.hasPermission('maintenance:corrective:dispatch')")
    public CommonResult<Long> dispatch(@PathVariable Long id, @RequestBody(required = false) CorrectiveDispatchReqVO reqVO) {
        return success(correctiveCaseService.dispatch(id, reqVO == null ? new CorrectiveDispatchReqVO() : reqVO));
    }

    @PostMapping("/{id}/complete-from-work-order")
    @PreAuthorize("@ss.hasPermission('maintenance:corrective:complete')")
    public CommonResult<Boolean> completeFromWorkOrder(@PathVariable Long id) {
        correctiveCaseService.completeFromWorkOrder(id);
        return success(true);
    }
}
