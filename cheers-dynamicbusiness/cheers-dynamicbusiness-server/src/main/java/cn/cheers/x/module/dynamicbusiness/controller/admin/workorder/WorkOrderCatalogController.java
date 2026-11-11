package cn.cheers.x.module.dynamicbusiness.controller.admin.workorder;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.workorder.vo.WorkOrderCatalogAssignReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.workorder.vo.WorkOrderCatalogCompleteReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.workorder.vo.WorkOrderCatalogCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.workorder.vo.WorkOrderCatalogIdReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.workorder.vo.WorkOrderCatalogPageReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.workorder.vo.WorkOrderCatalogRespVO;
import cn.cheers.x.module.dynamicbusiness.service.workorder.WorkOrderCatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 工单中心读写数据目录里的工单。
 *
 * 管什么：新建、列表、开工、完工、接收、退回、转交。
 * 不管什么：报修受理页。报修派工仍走报修接口，由它来调用同一套写入。
 * 禁止：把独立工单表当成这次记录的来源。
 */
@Tag(name = "管理后台 - 工单目录")
@RestController
@RequestMapping("/dynamicbusiness/work-order-record")
@Validated
public class WorkOrderCatalogController {

    @Resource
    private WorkOrderCatalogService workOrderCatalogService;

    @PostMapping("/create")
    @Operation(summary = "在工单目录新建一条")
    @PreAuthorize("isAuthenticated()")
    public CommonResult<Long> create(@Valid @RequestBody WorkOrderCatalogCreateReqVO reqVO) {
        return success(workOrderCatalogService.create(reqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "读取一条工单目录记录")
    @PreAuthorize("isAuthenticated()")
    public CommonResult<WorkOrderCatalogRespVO> get(@RequestParam("id") Long id) {
        return success(workOrderCatalogService.get(id));
    }

    @GetMapping("/page")
    @Operation(summary = "按业务域和场站列出工单")
    @PreAuthorize("isAuthenticated()")
    public CommonResult<PageResult<WorkOrderCatalogRespVO>> page(@Valid WorkOrderCatalogPageReqVO reqVO) {
        return success(workOrderCatalogService.page(reqVO));
    }

    @PostMapping("/start")
    @Operation(summary = "开工，写下实际开工")
    @PreAuthorize("isAuthenticated()")
    public CommonResult<Boolean> start(@Valid @RequestBody WorkOrderCatalogIdReqVO reqVO) {
        workOrderCatalogService.start(reqVO.getId());
        return success(true);
    }

    @PostMapping("/complete")
    @Operation(summary = "完工，写下实际完工。维修要填原因和措施")
    @PreAuthorize("isAuthenticated()")
    public CommonResult<Boolean> complete(@Valid @RequestBody WorkOrderCatalogCompleteReqVO reqVO) {
        workOrderCatalogService.complete(reqVO);
        return success(true);
    }

    @PostMapping("/claim")
    @Operation(summary = "班组成员领走")
    @PreAuthorize("isAuthenticated()")
    public CommonResult<Boolean> claim(@Valid @RequestBody WorkOrderCatalogIdReqVO reqVO) {
        workOrderCatalogService.claim(reqVO.getId());
        return success(true);
    }

    @PostMapping("/assign")
    @Operation(summary = "指定或转交本班组成员")
    @PreAuthorize("isAuthenticated()")
    public CommonResult<Boolean> assign(@Valid @RequestBody WorkOrderCatalogAssignReqVO reqVO) {
        workOrderCatalogService.assign(reqVO.getId(), reqVO.getAssigneeUserId());
        return success(true);
    }

    @PostMapping("/release")
    @Operation(summary = "执行人退回，清空执行人")
    @PreAuthorize("isAuthenticated()")
    public CommonResult<Boolean> release(@Valid @RequestBody WorkOrderCatalogIdReqVO reqVO) {
        workOrderCatalogService.release(reqVO.getId());
        return success(true);
    }
}
