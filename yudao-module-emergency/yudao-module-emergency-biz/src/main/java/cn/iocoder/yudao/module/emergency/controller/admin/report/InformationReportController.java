package cn.iocoder.yudao.module.emergency.controller.admin.report;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emergency.controller.admin.report.vo.*;
import cn.iocoder.yudao.module.emergency.service.report.InformationReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 信息报送流程管理
 *
 * @author 芋道源码
 */
@Tag(name = "管理后台 - 信息报送流程管理")
@RestController
@RequestMapping("/emergency/information-report")
@Validated
public class InformationReportController {

    @Resource
    private InformationReportService informationReportService;

    @PostMapping("/create")
    @Operation(summary = "创建信息报送记录")
    @PreAuthorize("@ss.hasPermission('emergency:information-report:create')")
    public CommonResult<Long> createInformationReport(@Valid @RequestBody InformationReportCreateReqVO createReqVO) {
        return success(informationReportService.createInformationReport(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新信息报送记录")
    @PreAuthorize("@ss.hasPermission('emergency:information-report:update')")
    public CommonResult<Boolean> updateInformationReport(@Valid @RequestBody InformationReportUpdateReqVO updateReqVO) {
        informationReportService.updateInformationReport(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除信息报送记录")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('emergency:information-report:delete')")
    public CommonResult<Boolean> deleteInformationReport(@RequestParam("id") Long id) {
        informationReportService.deleteInformationReport(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得信息报送记录")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('emergency:information-report:query')")
    public CommonResult<InformationReportRespVO> getInformationReport(@RequestParam("id") Long id) {
        InformationReportRespVO informationReport = informationReportService.getInformationReport(id);
        return success(informationReport);
    }

    @GetMapping("/page")
    @Operation(summary = "获得信息报送记录分页")
    @PreAuthorize("@ss.hasPermission('emergency:information-report:query')")
    public CommonResult<PageResult<InformationReportRespVO>> getInformationReportPage(@Valid InformationReportPageReqVO pageReqVO) {
        PageResult<InformationReportRespVO> pageResult = informationReportService.getInformationReportPage(pageReqVO);
        return success(pageResult);
    }

    @PostMapping("/{id}/submit")
    @Operation(summary = "提交报送", description = "将报送状态从待报送更新为已报送")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('emergency:information-report:submit')")
    public CommonResult<Boolean> submitReport(@PathVariable("id") Long id,
                                               @Valid @RequestBody InformationReportSubmitReqVO submitReqVO) {
        informationReportService.submitReport(id, submitReqVO);
        return success(true);
    }

    @PostMapping("/{id}/confirm")
    @Operation(summary = "确认报送", description = "将报送状态从已报送更新为已确认")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('emergency:information-report:confirm')")
    public CommonResult<Boolean> confirmReport(@PathVariable("id") Long id,
                                                @Valid @RequestBody InformationReportConfirmReqVO confirmReqVO) {
        informationReportService.confirmReport(id, confirmReqVO);
        return success(true);
    }

    @GetMapping("/check-timeout")
    @Operation(summary = "检查报送时限", description = "检查指定事件和报送类型是否超时")
    @Parameter(name = "eventId", description = "事件ID", required = true)
    @Parameter(name = "reportType", description = "报送类型", required = true)
    @PreAuthorize("@ss.hasPermission('emergency:information-report:query')")
    public CommonResult<Boolean> checkReportTimeout(@RequestParam("eventId") Long eventId,
                                                    @RequestParam("reportType") String reportType) {
        boolean timeout = informationReportService.checkReportTimeout(eventId, reportType);
        return success(timeout);
    }
}








