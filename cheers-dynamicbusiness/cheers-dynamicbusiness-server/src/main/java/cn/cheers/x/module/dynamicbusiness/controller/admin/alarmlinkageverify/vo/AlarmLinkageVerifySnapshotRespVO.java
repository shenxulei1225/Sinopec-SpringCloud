package cn.cheers.x.module.dynamicbusiness.controller.admin.alarmlinkageverify.vo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 验证页一次看到的事件、采集点、告警记录和联动执行。
 */
public class AlarmLinkageVerifySnapshotRespVO {

    private Long deviceId;
    private String deviceName;
    private List<EventRow> events = new ArrayList<>();
    private List<PointRow> points = new ArrayList<>();
    private List<AlarmRow> alarms = new ArrayList<>();
    private List<LinkageRow> linkages = new ArrayList<>();
    /** 采集那条 OPC UA 会话是否连着。没连着就不能发指令。 */
    private Boolean opcUaServerOnline;

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public List<EventRow> getEvents() {
        return events;
    }

    public void setEvents(List<EventRow> events) {
        this.events = events == null ? new ArrayList<>() : events;
    }

    public List<PointRow> getPoints() {
        return points;
    }

    public void setPoints(List<PointRow> points) {
        this.points = points;
    }

    public List<AlarmRow> getAlarms() {
        return alarms;
    }

    public void setAlarms(List<AlarmRow> alarms) {
        this.alarms = alarms;
    }

    public List<LinkageRow> getLinkages() {
        return linkages;
    }

    public void setLinkages(List<LinkageRow> linkages) {
        this.linkages = linkages;
    }

    public Boolean getOpcUaServerOnline() {
        return opcUaServerOnline;
    }

    public void setOpcUaServerOnline(Boolean opcUaServerOnline) {
        this.opcUaServerOnline = opcUaServerOnline;
    }

    public static class EventRow {
        private Long id;
        private Long eventTopicId;
        private String eventTopicName;
        private String phase;
        private String phaseLabel;
        private Long alarmRecordId;
        private LocalDateTime occurredAt;
        private String deliveryStatus;
        private String eventCode;
        private String lastError;
        private String content;
        private String triggerValue;
        private String thresholdValue;
        private String pointCode;
        private String sourceDeviceName;
        private String locationName;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getEventTopicId() {
            return eventTopicId;
        }

        public void setEventTopicId(Long eventTopicId) {
            this.eventTopicId = eventTopicId;
        }

        public String getEventTopicName() {
            return eventTopicName;
        }

        public void setEventTopicName(String eventTopicName) {
            this.eventTopicName = eventTopicName;
        }

        public String getPhase() {
            return phase;
        }

        public void setPhase(String phase) {
            this.phase = phase;
        }

        public String getPhaseLabel() {
            return phaseLabel;
        }

        public void setPhaseLabel(String phaseLabel) {
            this.phaseLabel = phaseLabel;
        }

        public Long getAlarmRecordId() {
            return alarmRecordId;
        }

        public void setAlarmRecordId(Long alarmRecordId) {
            this.alarmRecordId = alarmRecordId;
        }

        public LocalDateTime getOccurredAt() {
            return occurredAt;
        }

        public void setOccurredAt(LocalDateTime occurredAt) {
            this.occurredAt = occurredAt;
        }

        public String getDeliveryStatus() {
            return deliveryStatus;
        }

        public void setDeliveryStatus(String deliveryStatus) {
            this.deliveryStatus = deliveryStatus;
        }

        public String getEventCode() {
            return eventCode;
        }

        public void setEventCode(String eventCode) {
            this.eventCode = eventCode;
        }

        public String getLastError() {
            return lastError;
        }

        public void setLastError(String lastError) {
            this.lastError = lastError;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getTriggerValue() {
            return triggerValue;
        }

        public void setTriggerValue(String triggerValue) {
            this.triggerValue = triggerValue;
        }

        public String getThresholdValue() {
            return thresholdValue;
        }

        public void setThresholdValue(String thresholdValue) {
            this.thresholdValue = thresholdValue;
        }

        public String getPointCode() {
            return pointCode;
        }

        public void setPointCode(String pointCode) {
            this.pointCode = pointCode;
        }

        public String getSourceDeviceName() {
            return sourceDeviceName;
        }

        public void setSourceDeviceName(String sourceDeviceName) {
            this.sourceDeviceName = sourceDeviceName;
        }

        public String getLocationName() {
            return locationName;
        }

        public void setLocationName(String locationName) {
            this.locationName = locationName;
        }
    }

    public static class PointRow {
        private String pointCode;
        private String pointName;
        private String unit;
        private List<ThresholdRow> thresholds = new ArrayList<>();

        public String getPointCode() {
            return pointCode;
        }

        public void setPointCode(String pointCode) {
            this.pointCode = pointCode;
        }

        public String getPointName() {
            return pointName;
        }

        public void setPointName(String pointName) {
            this.pointName = pointName;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public List<ThresholdRow> getThresholds() {
            return thresholds;
        }

        public void setThresholds(List<ThresholdRow> thresholds) {
            this.thresholds = thresholds == null ? new ArrayList<>() : thresholds;
        }
    }

    /**
     * 这个采集点上、这台设备型号会套到的告警规则限值。
     * 数字来自告警规则目录，验证页只给人看，不另编。
     */
    public static class ThresholdRow {
        private String ruleName;
        private String signalKind;
        private String compareOp;
        private String compareLabel;
        private Double threshold;
        private Double recovery;

        public String getRuleName() {
            return ruleName;
        }

        public void setRuleName(String ruleName) {
            this.ruleName = ruleName;
        }

        public String getSignalKind() {
            return signalKind;
        }

        public void setSignalKind(String signalKind) {
            this.signalKind = signalKind;
        }

        public String getCompareOp() {
            return compareOp;
        }

        public void setCompareOp(String compareOp) {
            this.compareOp = compareOp;
        }

        public String getCompareLabel() {
            return compareLabel;
        }

        public void setCompareLabel(String compareLabel) {
            this.compareLabel = compareLabel;
        }

        public Double getThreshold() {
            return threshold;
        }

        public void setThreshold(Double threshold) {
            this.threshold = threshold;
        }

        public Double getRecovery() {
            return recovery;
        }

        public void setRecovery(Double recovery) {
            this.recovery = recovery;
        }
    }

    public static class AlarmRow {
        private Long id;
        private String name;
        private String handling;
        private String content;
        private String triggerValue;
        private String thresholdValue;
        private String level;
        private String point;
        private String cause;
        private String consequence;
        private String review;
        private String trace;
        private LocalDateTime updateTime;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getHandling() {
            return handling;
        }

        public void setHandling(String handling) {
            this.handling = handling;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getTriggerValue() {
            return triggerValue;
        }

        public void setTriggerValue(String triggerValue) {
            this.triggerValue = triggerValue;
        }

        public String getThresholdValue() {
            return thresholdValue;
        }

        public void setThresholdValue(String thresholdValue) {
            this.thresholdValue = thresholdValue;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public String getPoint() {
            return point;
        }

        public void setPoint(String point) {
            this.point = point;
        }

        public String getCause() {
            return cause;
        }

        public void setCause(String cause) {
            this.cause = cause;
        }

        public String getConsequence() {
            return consequence;
        }

        public void setConsequence(String consequence) {
            this.consequence = consequence;
        }

        public String getReview() {
            return review;
        }

        public void setReview(String review) {
            this.review = review;
        }

        public String getTrace() {
            return trace;
        }

        public void setTrace(String trace) {
            this.trace = trace;
        }

        public LocalDateTime getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(LocalDateTime updateTime) {
            this.updateTime = updateTime;
        }
    }

    public static class LinkageRow {
        private Long id;
        private String linkageName;
        private Long eventTopicId;
        private String eventTopicName;
        private String status;
        private String lastError;
        private LocalDateTime createTime;
        private List<ItemRow> items = new ArrayList<>();

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getLinkageName() {
            return linkageName;
        }

        public void setLinkageName(String linkageName) {
            this.linkageName = linkageName;
        }

        public Long getEventTopicId() {
            return eventTopicId;
        }

        public void setEventTopicId(Long eventTopicId) {
            this.eventTopicId = eventTopicId;
        }

        public String getEventTopicName() {
            return eventTopicName;
        }

        public void setEventTopicName(String eventTopicName) {
            this.eventTopicName = eventTopicName;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getLastError() {
            return lastError;
        }

        public void setLastError(String lastError) {
            this.lastError = lastError;
        }

        public LocalDateTime getCreateTime() {
            return createTime;
        }

        public void setCreateTime(LocalDateTime createTime) {
            this.createTime = createTime;
        }

        public List<ItemRow> getItems() {
            return items;
        }

        public void setItems(List<ItemRow> items) {
            this.items = items;
        }
    }

    public static class ItemRow {
        private Long pairIndex;
        private Long actionExecutionId;
        private Long actionId;
        private Long targetEntityId;
        private String actionName;
        private String targetName;
        private String status;
        private String lastError;

        public Long getPairIndex() {
            return pairIndex;
        }

        public void setPairIndex(Long pairIndex) {
            this.pairIndex = pairIndex;
        }

        public Long getActionExecutionId() {
            return actionExecutionId;
        }

        public void setActionExecutionId(Long actionExecutionId) {
            this.actionExecutionId = actionExecutionId;
        }

        public Long getActionId() {
            return actionId;
        }

        public void setActionId(Long actionId) {
            this.actionId = actionId;
        }

        public Long getTargetEntityId() {
            return targetEntityId;
        }

        public void setTargetEntityId(Long targetEntityId) {
            this.targetEntityId = targetEntityId;
        }

        public String getActionName() {
            return actionName;
        }

        public void setActionName(String actionName) {
            this.actionName = actionName;
        }

        public String getTargetName() {
            return targetName;
        }

        public void setTargetName(String targetName) {
            this.targetName = targetName;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getLastError() {
            return lastError;
        }

        public void setLastError(String lastError) {
            this.lastError = lastError;
        }
    }
}
