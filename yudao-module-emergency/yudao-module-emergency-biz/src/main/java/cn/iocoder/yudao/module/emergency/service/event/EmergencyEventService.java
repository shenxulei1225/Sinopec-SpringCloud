package cn.iocoder.yudao.module.emergency.service.event;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.common.pojo.PageParam;
import cn.iocoder.yudao.module.emergency.controller.admin.event.vo.*;

public interface EmergencyEventService {

    EventRespVO create(EventCreateReqVO reqVO);

    void addReport(Long eventId, EventReportReqVO reqVO);

    EventExternalReportRespVO addExternalReport(Long eventId, EventExternalReportReqVO reqVO);

    void confirm(Long eventId, EventConfirmReqVO reqVO);

    EventAssessRespVO assess(Long eventId, EventAssessReqVO reqVO);

    /**
     * 关闭事件
     * 关闭前会校验所有关键任务是否已完成
     * @param eventId 事件ID
     * @param reason 关闭原因
     */
    void close(Long eventId, String reason);

    EventRespVO getEvent(Long id);

    PageResult<EventRespVO> getEventPage(PageParam pageReqVO);

    /**
     * 获取事件时间线
     * 通过聚合查询多个业务表（事件表、任务表、资源调度表、上报记录表、评估表、处置表）生成时间线
     * @param eventId 事件ID
     * @param pageReqVO 分页参数
     * @return 时间线分页结果
     */
    PageResult<cn.iocoder.yudao.module.emergency.controller.admin.response.vo.TimelineItemRespVO> getTimeline(Long eventId, cn.iocoder.yudao.module.emergency.controller.admin.response.vo.TimelinePageReqVO pageReqVO);

    /**
     * 获取事件状态变更历史
     * @param eventId 事件ID
     * @param pageReqVO 分页参数
     * @return 状态变更历史分页结果
     */
    PageResult<EventStatusHistoryRespVO> getStatusHistory(Long eventId, PageParam pageReqVO);

    /**
     * 获取事件研判记录列表
     * @param eventId 事件ID
     * @return 研判记录列表
     */
    java.util.List<EventAssessListRespVO> getAssessments(Long eventId);

    /**
     * 更新事件状态
     * 使用状态机校验状态转换是否合法
     * @param eventId 事件ID
     * @param targetStatus 目标状态
     * @param reason 操作原因
     */
    void updateStatus(Long eventId, String targetStatus, String reason);
}


