package cn.cheers.x.inspection.task.controller.admin.scheduleboard;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.task.controller.admin.vo.scheduleboard.ScheduleBoardExecutionDetailRespVO;
import cn.cheers.x.inspection.task.controller.admin.vo.scheduleboard.ScheduleBoardPlanPointRespVO;
import cn.cheers.x.inspection.task.service.query.scheduleboard.InspectionScheduleBoardQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 巡检排期看板读接口：计划点列表 + 执行详情。
 */
@Tag(name = "管理后台 - 巡检排期看板")
@RestController
@RequestMapping("/inspection-task/schedule-board")
@Validated
public class InspectionScheduleBoardController {

    @Resource
    private InspectionScheduleBoardQueryService inspectionScheduleBoardQueryService;

    @GetMapping("/plan-points")
    @Operation(summary = "查询可见日期范围内的执行计划点")
    public CommonResult<List<ScheduleBoardPlanPointRespVO>> listPlanPoints(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to,
            @RequestParam("facilityId") Long facilityId) {
        return success(inspectionScheduleBoardQueryService.listPlanPoints(from, to, facilityId));
    }

    @GetMapping("/plan-points/{slotId}/execution-detail")
    @Operation(summary = "计划点执行详情（过程时间轴 + 步骤树）")
    public CommonResult<ScheduleBoardExecutionDetailRespVO> getExecutionDetail(
            @PathVariable("slotId") String slotId) {
        return success(inspectionScheduleBoardQueryService.getExecutionDetail(slotId));
    }
}
