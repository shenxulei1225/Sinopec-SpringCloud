package cn.cheers.x.maintenance.controller.admin;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.maintenance.controller.admin.vo.calendar.*;
import cn.cheers.x.maintenance.service.calendar.CalendarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 预防性维护日历")
@RestController
@RequestMapping("/maintenance/calendar")
@Validated
public class CalendarController {

    @Resource private CalendarService calendarService;

    @PostMapping("/expand")
    @Operation(summary = "按手册频率展开日历")
    @PreAuthorize("@ss.hasPermission('maintenance:calendar:expand')")
    public CommonResult<Integer> expand(@Valid @RequestBody CalendarExpandReqVO reqVO) {
        return success(calendarService.expand(reqVO));
    }

    @GetMapping("/page")
    @PreAuthorize("@ss.hasPermission('maintenance:calendar:query')")
    public CommonResult<PageResult<CalendarEntryRespVO>> page(@Valid CalendarPageReqVO reqVO) {
        return success(calendarService.page(reqVO));
    }

    @PostMapping("/{id}/trigger")
    @Operation(summary = "触发日历条目：schedule/run + 派工")
    @PreAuthorize("@ss.hasPermission('maintenance:calendar:trigger')")
    public CommonResult<Boolean> trigger(@PathVariable("id") Long id) {
        calendarService.trigger(id);
        return success(true);
    }
}
