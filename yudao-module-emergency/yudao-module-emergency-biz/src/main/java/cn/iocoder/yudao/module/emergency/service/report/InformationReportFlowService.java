package cn.iocoder.yudao.module.emergency.service.report;

import cn.iocoder.yudao.module.emergency.dal.dataobject.event.EmergencyEventDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.event.EmergencyEventReportInternalDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.event.EmergencyEventReportExternalDO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 信息报送流程服务接口
 * 
 * 负责管理信息报送流程，包括：
 * - 报送流程状态跟踪（接报、呈报、上报）
 * - 报送流程完整性校验
 * - 报送时限规则校验
 */
public interface InformationReportFlowService {

    /**
     * 校验报送流程完整性
     * 
     * @param eventId 事件ID
     * @return 是否完整
     */
    boolean validateFlowCompleteness(Long eventId);

    /**
     * 校验报送时限规则
     * 
     * @param event 事件对象
     * @param reportType 报送类型（初报/续报/终报）
     * @param reportTime 报送时间
     * @return 是否在时限内
     */
    boolean validateTimeLimit(EmergencyEventDO event, String reportType, LocalDateTime reportTime);

    /**
     * 获取报送流程状态
     * 
     * @param eventId 事件ID
     * @return 报送流程状态信息
     */
    ReportFlowStatus getFlowStatus(Long eventId);

    /**
     * 记录接报时间
     * 
     * @param eventId 事件ID
     * @param reportTime 接报时间
     */
    void recordReceiveTime(Long eventId, LocalDateTime reportTime);

    /**
     * 记录呈报时间
     * 
     * @param eventId 事件ID
     * @param reportTime 呈报时间
     */
    void recordSubmitTime(Long eventId, LocalDateTime reportTime);

    /**
     * 记录上报时间
     * 
     * @param eventId 事件ID
     * @param reportTime 上报时间
     */
    void recordReportTime(Long eventId, LocalDateTime reportTime);

    /**
     * 获取事件的所有内部上报记录
     * 
     * @param eventId 事件ID
     * @return 内部上报记录列表
     */
    List<EmergencyEventReportInternalDO> getInternalReports(Long eventId);

    /**
     * 获取事件的所有外部上报记录
     * 
     * @param eventId 事件ID
     * @return 外部上报记录列表
     */
    List<EmergencyEventReportExternalDO> getExternalReports(Long eventId);

    /**
     * 报送流程状态信息
     */
    class ReportFlowStatus {
        private LocalDateTime receiveTime;  // 接报时间
        private LocalDateTime submitTime;   // 呈报时间
        private LocalDateTime reportTime;   // 上报时间
        private boolean isComplete;         // 流程是否完整

        // Getters and Setters
        public LocalDateTime getReceiveTime() {
            return receiveTime;
        }

        public void setReceiveTime(LocalDateTime receiveTime) {
            this.receiveTime = receiveTime;
        }

        public LocalDateTime getSubmitTime() {
            return submitTime;
        }

        public void setSubmitTime(LocalDateTime submitTime) {
            this.submitTime = submitTime;
        }

        public LocalDateTime getReportTime() {
            return reportTime;
        }

        public void setReportTime(LocalDateTime reportTime) {
            this.reportTime = reportTime;
        }

        public boolean isComplete() {
            return isComplete;
        }

        public void setComplete(boolean complete) {
            isComplete = complete;
        }
    }
}



