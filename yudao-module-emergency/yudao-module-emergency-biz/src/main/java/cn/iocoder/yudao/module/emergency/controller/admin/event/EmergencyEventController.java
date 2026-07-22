package cn.iocoder.yudao.module.emergency.controller.admin.event;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.common.pojo.PageParam;
import cn.iocoder.yudao.module.emergency.controller.admin.event.vo.*;
import cn.iocoder.yudao.module.emergency.service.event.EmergencyEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@RestController
@RequestMapping("/emergency/events")
@Tag(name = "管理后台 - 应急事件")
public class EmergencyEventController {

    @Resource
    private EmergencyEventService eventService;

    @PostMapping
    @Operation(summary = "创建应急事件", description = "上报新的应急事件")
    public CommonResult<EventRespVO> create(@Valid @RequestBody EventCreateReqVO reqVO) {
        return success(eventService.create(reqVO));
    }

    @PostMapping("/from-alert")
    @Operation(summary = "告警转应急事件",
            description = "写入告警关联 id 并建事件；调用方须带齐描述/发现时间等必填，服务端不编造告警或坐标")
    public CommonResult<EventRespVO> createFromAlert(@Valid @RequestBody EventFromAlertReqVO reqVO) {
        return success(eventService.createFromAlert(reqVO));
    }

    @PostMapping("/{id}/reports")
    @Operation(summary = "追加事件上报", description = "为已存在的事件追加新的上报信息（内部上报）")
    @Parameter(name = "id", description = "事件ID", required = true)
    public CommonResult<Boolean> addReport(@PathVariable("id") Long id,
                                           @Valid @RequestBody EventReportReqVO reqVO) {
        eventService.addReport(id, reqVO);
        return success(true);
    }

    @PostMapping("/{id}/external-reports")
    @Operation(summary = "追加外部事件上报", description = "为已存在的事件追加外部机构的上报信息")
    @Parameter(name = "id", description = "事件ID", required = true)
    public CommonResult<EventExternalReportRespVO> addExternalReport(@PathVariable("id") Long id,
                                                                      @Valid @RequestBody EventExternalReportReqVO reqVO) {
        return success(eventService.addExternalReport(id, reqVO));
    }

    @PostMapping("/{id}/confirm")
    @Operation(summary = "确认事件", description = "确认事件真实性（真实事件/误报/忽略）")
    @Parameter(name = "id", description = "事件ID", required = true)
    public CommonResult<Boolean> confirm(@PathVariable("id") Long id,
                                         @Valid @RequestBody EventConfirmReqVO reqVO) {
        eventService.confirm(id, reqVO);
        return success(true);
    }

    @PostMapping("/{id}/assess")
    @Operation(summary = "事件研判", description = "对事件进行研判，推荐响应级别和预案")
    @Parameter(name = "id", description = "事件ID", required = true)
    public CommonResult<EventAssessRespVO> assess(@PathVariable("id") Long id,
                                                  @Valid @RequestBody EventAssessReqVO reqVO) {
        return success(eventService.assess(id, reqVO));
    }

    @PostMapping("/{id}/close")
    @Operation(summary = "关闭事件", description = "关闭事件，关闭前会校验所有关键任务是否已完成")
    @Parameter(name = "id", description = "事件ID", required = true)
    public CommonResult<Boolean> close(@PathVariable("id") Long id,
                                       @RequestParam(value = "reason", required = false) String reason) {
        eventService.close(id, reason);
        return success(true);
    }

    @PostMapping("/{id}/status")
    @Operation(summary = "更新事件状态", description = "更新事件状态，会校验状态转换是否合法")
    @Parameter(name = "id", description = "事件ID", required = true)
    public CommonResult<Boolean> updateStatus(@PathVariable("id") Long id,
                                             @Valid @RequestBody EventStatusUpdateReqVO reqVO) {
        eventService.updateStatus(id, reqVO.getTargetStatus(), reqVO.getReason());
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得应急事件", description = "根据ID获得应急事件详情")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<EventRespVO> getEvent(@RequestParam("id") Long id) {
        return success(eventService.getEvent(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得应急事件分页")
    public CommonResult<PageResult<EventRespVO>> getEventPage(@Valid PageParam pageReqVO) {
        return success(eventService.getEventPage(pageReqVO));
    }

    @GetMapping("/{id}/timeline")
    @Operation(summary = "获取事件时间轴", description = "通过聚合查询多个业务表（事件表、任务表、资源调度表、上报记录表、评估表、处置表）生成时间线，按时间排序")
    @Parameter(name = "id", description = "事件ID", required = true)
    public CommonResult<PageResult<cn.iocoder.yudao.module.emergency.controller.admin.response.vo.TimelineItemRespVO>> getTimeline(
            @PathVariable("id") Long id,
            @Valid cn.iocoder.yudao.module.emergency.controller.admin.response.vo.TimelinePageReqVO reqVO) {
        return success(eventService.getTimeline(id, reqVO));
    }

    @GetMapping("/{id}/status-history")
    @Operation(summary = "获取事件状态变更历史", description = "查询事件的状态变更历史记录")
    @Parameter(name = "id", description = "事件ID", required = true)
    public CommonResult<PageResult<EventStatusHistoryRespVO>> getStatusHistory(
            @PathVariable("id") Long id,
            @Valid PageParam pageReqVO) {
        return success(eventService.getStatusHistory(id, pageReqVO));
    }

    @GetMapping("/assessments")
    @Operation(summary = "获取事件研判记录列表", description = "查询事件的研判记录列表")
    @Parameter(name = "eventId", description = "事件ID", required = true)
    public CommonResult<java.util.List<EventAssessListRespVO>> getAssessments(
            @RequestParam("eventId") Long eventId) {
        return success(eventService.getAssessments(eventId));
    }
}

