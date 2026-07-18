package cn.cheers.x.workorder.controller.admin;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.common.util.object.BeanUtils;
import cn.cheers.x.workorder.api.dto.WorkOrderCreateReqDTO;
import cn.cheers.x.workorder.controller.admin.vo.order.WorkOrderCreateReqVO;
import cn.cheers.x.workorder.controller.admin.vo.order.WorkOrderPageReqVO;
import cn.cheers.x.workorder.controller.admin.vo.order.WorkOrderRespVO;
import cn.cheers.x.workorder.controller.admin.vo.order.WorkOrderStepCompleteReqVO;
import cn.cheers.x.workorder.service.order.WorkOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 工单生命周期
 */
@Tag(name = "管理后台 - 工单")
@RestController
@RequestMapping("/work-order/orders")
@Validated
public class WorkOrderController {

    @Resource
    private WorkOrderService workOrderService;

    @PostMapping
    @Operation(summary = "创建工单（派工快照）")
    @PreAuthorize("@ss.hasPermission('work-order:order:create')")
    public CommonResult<Long> createWorkOrder(@Valid @RequestBody WorkOrderCreateReqVO createReqVO) {
        return success(workOrderService.createFromDispatch(BeanUtils.toBean(createReqVO, WorkOrderCreateReqDTO.class)));
    }

    @GetMapping("/page")
    @Operation(summary = "工单分页")
    @PreAuthorize("@ss.hasPermission('work-order:order:query')")
    public CommonResult<PageResult<WorkOrderRespVO>> getWorkOrderPage(@Valid WorkOrderPageReqVO pageReqVO) {
        return success(workOrderService.getWorkOrderPage(pageReqVO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取工单详情")
    @Parameter(name = "id", description = "工单编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('work-order:order:query')")
    public CommonResult<WorkOrderRespVO> getWorkOrder(@PathVariable("id") Long id) {
        return success(workOrderService.getWorkOrder(id));
    }

    @PostMapping("/{id}/start")
    @Operation(summary = "开工")
    @Parameter(name = "id", description = "工单编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('work-order:order:start')")
    public CommonResult<Boolean> start(@PathVariable("id") Long id) {
        workOrderService.start(id);
        return success(true);
    }

    @PostMapping("/{id}/steps/{stepCode}/complete")
    @Operation(summary = "完成工单步骤")
    @PreAuthorize("@ss.hasPermission('work-order:order:complete-step')")
    public CommonResult<Boolean> completeStep(@PathVariable("id") Long id,
                                              @PathVariable("stepCode") String stepCode,
                                              @RequestBody(required = false) WorkOrderStepCompleteReqVO reqVO) {
        workOrderService.completeStep(id, stepCode, reqVO);
        return success(true);
    }

    @PostMapping("/{id}/complete")
    @Operation(summary = "完工（全部必填步骤完成后）")
    @Parameter(name = "id", description = "工单编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('work-order:order:complete')")
    public CommonResult<Boolean> complete(@PathVariable("id") Long id) {
        workOrderService.complete(id);
        return success(true);
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "取消工单")
    @Parameter(name = "id", description = "工单编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('work-order:order:cancel')")
    public CommonResult<Boolean> cancel(@PathVariable("id") Long id) {
        workOrderService.cancel(id);
        return success(true);
    }

}
