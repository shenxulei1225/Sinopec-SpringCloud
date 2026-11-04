package cn.cheers.x.module.dynamicbusiness.controller.admin.alarm;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.dataprocess.AlarmFacilityStatsService;
import cn.cheers.x.module.dynamicbusiness.dataprocess.AlarmFacilityStatsView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 告警图层打开时，先取这一座站已经算好的统计。
 * 之后的变化走场站告警主题，这里不订阅。
 * 不按某个人点开的级别过滤。缺站就失败。
 */
@Tag(name = "管理后台 - 场站告警统计")
@RestController
@RequestMapping("/dynamicbusiness/alarm-facility")
@Validated
public class AlarmFacilityStatsController {

    @Resource
    private AlarmFacilityStatsService alarmFacilityStatsService;

    @GetMapping("/stats")
    @Operation(summary = "这一座站按分类和级别统计的告警")
    @PreAuthorize("@catalogAccess.allow('alarm', 'query')")
    public CommonResult<AlarmFacilityStatsView> stats(@RequestParam("facilityId") Long facilityId) {
        long stationId = facilityId == null ? 0L : facilityId;
        return success(alarmFacilityStatsService.snapshot(stationId, null));
    }
}
