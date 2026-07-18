package cn.cheers.x.alarm.controller.admin;

import cn.cheers.x.framework.apilog.core.annotation.ApiAccessLog;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.common.pojo.PageParam;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.excel.core.util.ExcelUtils;
import cn.cheers.x.alarm.controller.admin.vo.alarm.*;
import cn.cheers.x.alarm.controller.admin.vo.statistics.*;
import cn.cheers.x.alarm.convert.AlarmConvert;
import cn.cheers.x.alarm.dal.dataobject.AlarmDO;
import cn.cheers.x.alarm.service.alarm.AlarmService;
import cn.cheers.x.alarm.service.statistics.AlarmStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static cn.cheers.x.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 告警管理 Controller
 * 
 * <p>提供告警的查询、上报、确认、处理、关闭等功能。</p>
 * 
 * <p>核心功能：
 * <ul>
 *   <li>实时告警查询（FR-004）</li>
 *   <li>历史告警查询（FR-005）</li>
 *   <li>人工上报告警（FR-002）</li>
 *   <li>告警生命周期管理：确认、处理、关闭（FR-009）</li>
 *   <li>告警统计（FR-007）</li>
 *   <li>告警导出（FR-005）</li>
 * </ul>
 * </p>
 *
 * @author 告警管理模块
 */
@Tag(name = "管理后台 - 告警管理")
@RestController
@RequestMapping("/alarm/alarms")
@Validated
public class AlarmController {

    @Resource
    private AlarmService alarmService;

    @Resource
    private AlarmStatisticsService alarmStatisticsService;


    // ========== 告警查询相关 ==========

    @GetMapping("/real-time/page")
    @Operation(summary = "获取实时告警分页列表", description = "查询当前所有未关闭的告警，按创建时间倒序排列")
    @PreAuthorize("@ss.hasPermission('alarm:alarm:query')")
    public CommonResult<PageResult<AlarmRespVO>> getRealTimeAlarmPage(@Valid AlarmPageReqVO pageReqVO) {
        // 强制设置为只查询实时告警
        pageReqVO.setRealTimeOnly(true);
        PageResult<AlarmRespVO> pageResult = alarmService.getRealTimeAlarmPage(pageReqVO);
        return success(pageResult);
    }

    @GetMapping("/history/page")
    @Operation(summary = "获取历史告警分页列表", description = "查询所有告警（包括已关闭），支持多条件筛选")
    @PreAuthorize("@ss.hasPermission('alarm:alarm:query')")
    public CommonResult<PageResult<AlarmRespVO>> getHistoricalAlarmPage(@Valid AlarmPageReqVO pageReqVO) {
        // 历史告警查询不限制状态
        pageReqVO.setRealTimeOnly(false);
        PageResult<AlarmRespVO> pageResult = alarmService.getHistoricalAlarmPage(pageReqVO);
        return success(pageResult);
    }

    @GetMapping("/get")
    @Operation(summary = "获取告警详情")
    @Parameter(name = "id", description = "告警ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('alarm:alarm:query')")
    public CommonResult<AlarmDetailRespVO> getAlarmDetail(@RequestParam("id") Long id) {
        AlarmDetailRespVO detail = alarmService.getAlarmDetail(id);
        return success(detail);
    }

    @GetMapping("/get-by-code")
    @Operation(summary = "根据告警编码获取告警详情")
    @Parameter(name = "code", description = "告警编码", required = true, example = "ALM-20260104-00001")
    @PreAuthorize("@ss.hasPermission('alarm:alarm:query')")
    public CommonResult<AlarmDetailRespVO> getAlarmDetailByCode(@RequestParam("code") String code) {
        AlarmDO alarm = alarmService.getAlarmByCode(code);
        if (alarm == null) {
            return success(null);
        }
        AlarmDetailRespVO detail = alarmService.getAlarmDetail(alarm.getId());
        return success(detail);
    }

    @GetMapping("/page")
    @Operation(summary = "获取告警分页列表", description = "通用告警分页查询，支持多条件筛选")
    @PreAuthorize("@ss.hasPermission('alarm:alarm:query')")
    public CommonResult<PageResult<AlarmRespVO>> getAlarmPage(@Valid AlarmPageReqVO pageReqVO) {
        PageResult<AlarmRespVO> pageResult = alarmService.getHistoricalAlarmPage(pageReqVO);
        return success(pageResult);
    }

    // ========== 人工上报相关 ==========

    @PostMapping("/report")
    @Operation(summary = "人工上报告警", description = "值班员手动上报告警，支持上传附件")
    @PreAuthorize("@ss.hasPermission('alarm:alarm:report')")
    public CommonResult<AlarmRespVO> reportAlarm(@Valid @RequestBody AlarmReportReqVO reportReqVO) {
        AlarmRespVO alarm = alarmService.reportAlarm(reportReqVO);
        return success(alarm);
    }

    // ========== 系统触发告警相关 ==========

    @PostMapping("/trigger")
    @Operation(summary = "系统触发告警", description = "系统自动触发告警，用于设备监测数据超标时自动创建告警")
    @PreAuthorize("@ss.hasPermission('alarm:alarm:trigger')")
    public CommonResult<AlarmRespVO> triggerAlarm(@Valid @RequestBody AlarmTriggerReqVO triggerReqVO) {
        AlarmRespVO alarm = alarmService.triggerAlarm(triggerReqVO);
        return success(alarm);
    }

    // ========== 告警生命周期管理 ==========

    @PostMapping("/{id}/acknowledge")
    @Operation(summary = "确认告警", description = "将告警状态从待确认更新为已确认")
    @Parameter(name = "id", description = "告警ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('alarm:alarm:acknowledge')")
    public CommonResult<Boolean> acknowledgeAlarm(@PathVariable("id") Long id,
                                                   @Valid @RequestBody AlarmAcknowledgeReqVO acknowledgeReqVO) {
        alarmService.acknowledgeAlarm(id, acknowledgeReqVO);
        return success(true);
    }

    @PostMapping("/{id}/handle")
    @Operation(summary = "处理告警", description = "将告警状态从已确认更新为处理中")
    @Parameter(name = "id", description = "告警ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('alarm:alarm:handle')")
    public CommonResult<Boolean> handleAlarm(@PathVariable("id") Long id,
                                              @Valid @RequestBody AlarmHandleReqVO handleReqVO) {
        alarmService.handleAlarm(id, handleReqVO);
        return success(true);
    }

    @PostMapping("/{id}/close")
    @Operation(summary = "关闭告警", description = "将告警状态从处理中更新为已关闭")
    @Parameter(name = "id", description = "告警ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('alarm:alarm:close')")
    public CommonResult<Boolean> closeAlarm(@PathVariable("id") Long id,
                                             @Valid @RequestBody AlarmCloseReqVO closeReqVO) {
        alarmService.closeAlarm(id, closeReqVO);
        return success(true);
    }

    // ========== 批量操作相关 ==========

    @PostMapping("/batch-acknowledge")
    @Operation(summary = "批量确认告警")
    @Parameters({
            @Parameter(name = "ids", description = "告警ID列表", required = true, example = "[1, 2, 3]")
    })
    @PreAuthorize("@ss.hasPermission('alarm:alarm:acknowledge')")
    public CommonResult<Boolean> batchAcknowledgeAlarms(@RequestParam("ids") List<Long> ids,
                                                         @Valid @RequestBody AlarmAcknowledgeReqVO acknowledgeReqVO) {
        alarmService.batchAcknowledgeAlarms(ids, acknowledgeReqVO);
        return success(true);
    }

    @PostMapping("/batch-close")
    @Operation(summary = "批量关闭告警")
    @Parameters({
            @Parameter(name = "ids", description = "告警ID列表", required = true, example = "[1, 2, 3]")
    })
    @PreAuthorize("@ss.hasPermission('alarm:alarm:close')")
    public CommonResult<Boolean> batchCloseAlarms(@RequestParam("ids") List<Long> ids,
                                                   @Valid @RequestBody AlarmCloseReqVO closeReqVO) {
        alarmService.batchCloseAlarms(ids, closeReqVO);
        return success(true);
    }


    // ========== 告警统计相关 ==========

    @GetMapping("/statistics")
    @Operation(summary = "获取实时告警统计", description = "获取当前活跃告警数、今日新增/处理/关闭数、各级别告警数等统计数据")
    @PreAuthorize("@ss.hasPermission('alarm:alarm:query')")
    public CommonResult<AlarmStatisticsRespVO> getRealTimeStatistics() {
        AlarmStatisticsRespVO statistics = alarmStatisticsService.getRealTimeStatistics();
        return success(statistics);
    }

    @GetMapping("/statistics/trend")
    @Operation(summary = "获取告警趋势数据", description = "按日期统计告警数量变化趋势")
    @PreAuthorize("@ss.hasPermission('alarm:alarm:query')")
    public CommonResult<List<AlarmTrendRespVO>> getAlarmTrend(@Valid AlarmTrendReqVO reqVO) {
        List<AlarmTrendRespVO> trend = alarmStatisticsService.getAlarmTrend(reqVO);
        return success(trend);
    }

    @GetMapping("/statistics/distribution")
    @Operation(summary = "获取告警分布统计", description = "支持按类型、级别、位置、状态等维度统计告警分布")
    @PreAuthorize("@ss.hasPermission('alarm:alarm:query')")
    public CommonResult<List<AlarmDistributionRespVO>> getAlarmDistribution(@Valid AlarmDistributionReqVO reqVO) {
        List<AlarmDistributionRespVO> distribution = alarmStatisticsService.getAlarmDistribution(reqVO);
        return success(distribution);
    }

    @GetMapping("/statistics/efficiency")
    @Operation(summary = "获取告警处理效率指标", description = "获取平均响应/处理/关闭时间、处理率、关闭率等效率指标")
    @PreAuthorize("@ss.hasPermission('alarm:alarm:query')")
    public CommonResult<AlarmEfficiencyRespVO> getProcessingEfficiency(@Valid AlarmEfficiencyReqVO reqVO) {
        AlarmEfficiencyRespVO efficiency = alarmStatisticsService.getProcessingEfficiency(reqVO);
        return success(efficiency);
    }

    @GetMapping("/statistics/count-by-level")
    @Operation(summary = "获取实时告警数量统计（按级别）", description = "按告警级别统计当前未关闭的告警数量")
    @PreAuthorize("@ss.hasPermission('alarm:alarm:query')")
    public CommonResult<AlarmService.AlarmCountByLevelVO> getRealTimeAlarmCountByLevel() {
        AlarmService.AlarmCountByLevelVO countByLevel = alarmService.getRealTimeAlarmCountByLevel();
        return success(countByLevel);
    }

    @GetMapping("/statistics/today")
    @Operation(summary = "获取今日告警统计", description = "统计今日新增告警数量和已处理告警数量")
    @PreAuthorize("@ss.hasPermission('alarm:alarm:query')")
    public CommonResult<AlarmService.TodayAlarmStatisticsVO> getTodayAlarmStatistics() {
        AlarmService.TodayAlarmStatisticsVO todayStatistics = alarmService.getTodayAlarmStatistics();
        return success(todayStatistics);
    }

    // ========== 告警导出相关 ==========

    @GetMapping("/export-excel")
    @Operation(summary = "导出告警数据", description = "导出告警数据为Excel文件")
    @PreAuthorize("@ss.hasPermission('alarm:alarm:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportAlarmExcel(HttpServletResponse response, @Valid AlarmPageReqVO pageReqVO) throws IOException {
        // 设置不分页，导出全部数据
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        PageResult<AlarmRespVO> pageResult = alarmService.getHistoricalAlarmPage(pageReqVO);
        // 导出 Excel
        ExcelUtils.write(response, "告警数据.xls", "告警列表", AlarmRespVO.class, pageResult.getList());
    }

}
