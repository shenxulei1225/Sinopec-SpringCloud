package cn.iocoder.yudao.module.emergency.service.report;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.emergency.dal.dataobject.event.EmergencyEventDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.event.EmergencyEventReportExternalDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.event.EmergencyEventReportInternalDO;
import cn.iocoder.yudao.module.emergency.dal.mysql.event.EmergencyEventMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.event.EmergencyEventReportExternalMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.event.EmergencyEventReportInternalMapper;
import cn.iocoder.yudao.module.emergency.enums.error.ErrorCodeConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 信息报送流程服务实现
 * 
 * 实现信息报送流程的状态跟踪、完整性校验和时限规则校验
 */
@Slf4j
@Service
public class InformationReportFlowServiceImpl implements InformationReportFlowService {

    private final EmergencyEventMapper eventMapper;
    private final EmergencyEventReportInternalMapper reportInternalMapper;
    private final EmergencyEventReportExternalMapper reportExternalMapper;

    public InformationReportFlowServiceImpl(
            EmergencyEventMapper eventMapper,
            EmergencyEventReportInternalMapper reportInternalMapper,
            EmergencyEventReportExternalMapper reportExternalMapper) {
        this.eventMapper = eventMapper;
        this.reportInternalMapper = reportInternalMapper;
        this.reportExternalMapper = reportExternalMapper;
    }

    @Override
    public boolean validateFlowCompleteness(Long eventId) {
        EmergencyEventDO event = eventMapper.selectById(eventId);
        if (event == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.EVENT_NOT_EXISTS);
        }

        ReportFlowStatus status = getFlowStatus(eventId);
        
        // 根据事件级别判断是否需要完整的报送流程
        // Ⅰ级和Ⅱ级事件需要完整的报送流程（接报、呈报、上报）
        // Ⅲ级、Ⅳ级、Ⅴ级事件可能只需要接报和呈报
        String eventLevel = event.getEventLevel();
        if ("Ⅰ级".equals(eventLevel) || "Ⅱ级".equals(eventLevel)) {
            // 高级别事件需要完整的报送流程
            return status.getReceiveTime() != null 
                    && status.getSubmitTime() != null 
                    && status.getReportTime() != null;
        } else {
            // 低级别事件至少需要接报和呈报
            return status.getReceiveTime() != null 
                    && status.getSubmitTime() != null;
        }
    }

    @Override
    public boolean validateTimeLimit(EmergencyEventDO event, String reportType, LocalDateTime reportTime) {
        if (event == null || reportType == null || reportTime == null) {
            return false;
        }

        LocalDateTime eventTime = event.getOccurredAt() != null ? event.getOccurredAt() : event.getCreateTime();
        if (eventTime == null) {
            return false;
        }

        Duration duration = Duration.between(eventTime, reportTime);
        long minutes = duration.toMinutes();

        String eventLevel = event.getEventLevel();
        
        // 根据事件级别和报送类型判断时限
        if ("初报".equals(reportType)) {
            // 初报时限规则
            if ("Ⅰ级".equals(eventLevel)) {
                // Ⅰ级事件：事发单位5分钟内电话报告，30分钟内书面报告
                // 公司向集团公司：15分钟内电话报告，1小时内书面报告
                return minutes <= 60; // 1小时内书面报告
            } else if ("Ⅱ级".equals(eventLevel)) {
                // Ⅱ级事件：30分钟内上报
                return minutes <= 30;
            } else if ("Ⅲ级".equals(eventLevel)) {
                // Ⅲ级事件：1小时内上报
                return minutes <= 60;
            } else {
                // Ⅳ级和Ⅴ级事件：2小时内上报
                return minutes <= 120;
            }
        } else if ("续报".equals(reportType)) {
            // 续报时限规则：初报后3-4小时内续报
            ReportFlowStatus status = getFlowStatus(event.getId());
            if (status.getReceiveTime() == null) {
                return false; // 没有初报，无法续报
            }
            Duration fromFirstReport = Duration.between(status.getReceiveTime(), reportTime);
            return fromFirstReport.toHours() <= 4; // 初报后4小时内续报
        } else if ("终报".equals(reportType)) {
            // 终报时限规则：应急处置结束后及时报告（这里简化处理，只要事件已关闭即可）
            return "closed".equals(event.getStatus());
        }

        return true;
    }

    @Override
    public ReportFlowStatus getFlowStatus(Long eventId) {
        ReportFlowStatus status = new ReportFlowStatus();

        // 获取内部上报记录，最早的记录时间作为接报时间
        List<EmergencyEventReportInternalDO> internalReports = reportInternalMapper.selectList(
                new LambdaQueryWrapperX<EmergencyEventReportInternalDO>()
                        .eq(EmergencyEventReportInternalDO::getEmergencyEventId, eventId)
                        .orderByAsc(EmergencyEventReportInternalDO::getReportTime));
        if (!internalReports.isEmpty()) {
            status.setReceiveTime(internalReports.get(0).getReportTime());
        }

        // 获取外部上报记录
        List<EmergencyEventReportExternalDO> externalReports = reportExternalMapper.selectList(
                new LambdaQueryWrapperX<EmergencyEventReportExternalDO>()
                        .eq(EmergencyEventReportExternalDO::getEmergencyEventId, eventId)
                        .orderByAsc(EmergencyEventReportExternalDO::getReportTime));
        if (!externalReports.isEmpty()) {
            // 呈报时间：外部上报记录中最早的记录时间
            status.setSubmitTime(externalReports.stream()
                    .map(EmergencyEventReportExternalDO::getReportTime)
                    .min(LocalDateTime::compareTo)
                    .orElse(null));

            // 上报时间：外部上报记录中已发送的记录时间
            status.setReportTime(externalReports.stream()
                    .filter(report -> "sent".equals(report.getAuditStatus()))
                    .map(EmergencyEventReportExternalDO::getReportTime)
                    .min(LocalDateTime::compareTo)
                    .orElse(null));
        }

        // 判断流程是否完整
        status.setComplete(validateFlowCompleteness(eventId));

        return status;
    }

    @Override
    public void recordReceiveTime(Long eventId, LocalDateTime reportTime) {
        // 接报时间由内部上报记录自动记录，这里主要用于显式标记
        // 实际接报时间已经在addReport时记录到EmergencyEventReportInternalDO中
        log.info("记录接报时间：事件ID={}, 接报时间={}", eventId, reportTime);
    }

    @Override
    public void recordSubmitTime(Long eventId, LocalDateTime reportTime) {
        // 呈报时间由外部上报记录自动记录，这里主要用于显式标记
        // 实际呈报时间已经在外部上报时记录到EmergencyEventReportExternalDO中
        log.info("记录呈报时间：事件ID={}, 呈报时间={}", eventId, reportTime);
    }

    @Override
    public void recordReportTime(Long eventId, LocalDateTime reportTime) {
        // 上报时间由外部上报记录的发送状态自动记录，这里主要用于显式标记
        log.info("记录上报时间：事件ID={}, 上报时间={}", eventId, reportTime);
    }

    @Override
    public List<EmergencyEventReportInternalDO> getInternalReports(Long eventId) {
        return reportInternalMapper.selectList(
                new LambdaQueryWrapperX<EmergencyEventReportInternalDO>()
                        .eq(EmergencyEventReportInternalDO::getEmergencyEventId, eventId)
                        .orderByDesc(EmergencyEventReportInternalDO::getReportTime));
    }

    @Override
    public List<EmergencyEventReportExternalDO> getExternalReports(Long eventId) {
        return reportExternalMapper.selectList(
                new LambdaQueryWrapperX<EmergencyEventReportExternalDO>()
                        .eq(EmergencyEventReportExternalDO::getEmergencyEventId, eventId)
                        .orderByDesc(EmergencyEventReportExternalDO::getReportTime));
    }
}

