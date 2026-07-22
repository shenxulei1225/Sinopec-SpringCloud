package cn.iocoder.yudao.module.emergency.controller.admin.statistics;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.emergency.controller.admin.statistics.vo.*;
import cn.iocoder.yudao.module.emergency.service.statistics.EmergencyStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import java.time.LocalDateTime;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 应急统计分析
 */
@Tag(name = "管理后台 - 应急统计分析")
@RestController
@RequestMapping("/emergency/statistics")
@Validated
@Slf4j
public class EmergencyStatisticsController {

    @Resource
    private EmergencyStatisticsService emergencyStatisticsService;

    @GetMapping("/overview")
    @Operation(summary = "获取统计概览", description = "获取指定时间范围内的统计概览数据")
    @Parameter(name = "startTime", description = "开始时间", example = "2023-01-01T00:00:00")
    @Parameter(name = "endTime", description = "结束时间", example = "2023-12-31T23:59:59")
    @PreAuthorize("@ss.hasPermission('emergency:statistics:query')")
    public CommonResult<StatisticsOverviewRespVO> getStatisticsOverview(
            @RequestParam("startTime") LocalDateTime startTime,
            @RequestParam("endTime") LocalDateTime endTime) {
        return success(emergencyStatisticsService.getStatisticsOverview(startTime, endTime));
    }

    @GetMapping("/events")
    @Operation(summary = "获取事件统计", description = "获取事件相关的统计数据")
    @PreAuthorize("@ss.hasPermission('emergency:statistics:query')")
    public CommonResult<EventStatisticsRespVO> getEventStatistics(@Valid EventStatisticsReqVO reqVO) {
        return success(emergencyStatisticsService.getEventStatistics(reqVO));
    }

    @GetMapping("/responses")
    @Operation(summary = "获取响应统计", description = "获取响应相关的统计数据")
    @PreAuthorize("@ss.hasPermission('emergency:statistics:query')")
    public CommonResult<ResponseStatisticsRespVO> getResponseStatistics(@Valid ResponseStatisticsReqVO reqVO) {
        return success(emergencyStatisticsService.getResponseStatistics(reqVO));
    }

    @GetMapping("/resources")
    @Operation(summary = "获取资源统计", description = "获取资源使用相关的统计数据")
    @PreAuthorize("@ss.hasPermission('emergency:statistics:query')")
    public CommonResult<ResourceStatisticsRespVO> getResourceStatistics(@Valid ResourceStatisticsReqVO reqVO) {
        return success(emergencyStatisticsService.getResourceStatistics(reqVO));
    }

    @GetMapping("/effectiveness")
    @Operation(summary = "获取处置效果统计", description = "获取处置效果相关的统计数据")
    @PreAuthorize("@ss.hasPermission('emergency:statistics:query')")
    public CommonResult<EffectivenessStatisticsRespVO> getEffectivenessStatistics(@Valid EffectivenessStatisticsReqVO reqVO) {
        return success(emergencyStatisticsService.getEffectivenessStatistics(reqVO));
    }
}




