package cn.cheers.x.inspection.task.controller.admin.execution;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.task.service.execution.live.PatrolLayerDayListQueryService;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionDayAccountPageDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 场站实时监控 · 巡检图层侧栏。
 *
 * <p>管什么：按当前站场和所选日期列出这一天的巡检账。只含名称、计划时刻和执行状态。
 * <p>不管什么：不读步骤树，不改执行账。点中一条后再读那一条的步骤。
 */
@Tag(name = "管理后台 - 巡检图层当天名单")
@RestController
@RequestMapping("/inspection-task/execution-record")
@Validated
@RequiredArgsConstructor
public class PatrolLayerDayListController {

    private final PatrolLayerDayListQueryService patrolLayerDayListQueryService;

    @GetMapping("/day-accounts")
    @Operation(summary = "当前站场、所选日期的巡检账")
    public CommonResult<TaskExecutionDayAccountPageDTO> dayAccounts(
            @RequestParam("facilityId") Long facilityId,
            @RequestParam("day") String day,
            @RequestParam(value = "pageNo", required = false) Integer pageNo,
            @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return success(patrolLayerDayListQueryService.page(facilityId, day, pageNo, pageSize));
    }
}
