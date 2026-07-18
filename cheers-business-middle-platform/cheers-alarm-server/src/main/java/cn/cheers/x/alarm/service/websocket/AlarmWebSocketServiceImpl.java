package cn.cheers.x.alarm.service.websocket;

import cn.cheers.x.framework.common.enums.UserTypeEnum;
import cn.cheers.x.alarm.controller.admin.vo.websocket.AlarmWebSocketMessage;
import cn.cheers.x.alarm.controller.admin.vo.websocket.LinkageExecutionWebSocketMessage;
import cn.cheers.x.alarm.dal.dataobject.AlarmDO;
import cn.cheers.x.alarm.dal.dataobject.LinkageExecutionDO;
import cn.cheers.x.alarm.enums.LinkageActionTypeEnum;
import cn.cheers.x.alarm.enums.LinkageExecutionStatusEnum;
import cn.cheers.x.alarm.enums.WebSocketMessageTypeConstants;
import cn.cheers.x.infra.api.websocket.WebSocketSenderApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;

/**
 * 告警 WebSocket 推送服务实现类
 * 
 * 使用 WebSocketSenderApi 进行消息推送，支持多节点部署
 *
 * @author 告警管理模块
 */
@Service
@Slf4j
public class AlarmWebSocketServiceImpl implements AlarmWebSocketService {

    @Resource
    private WebSocketSenderApi webSocketSenderApi;

    @Override
    @Async
    public void pushNewAlarm(AlarmDO alarm) {
        try {
            AlarmWebSocketMessage message = buildAlarmMessage(alarm);
            // 推送给所有管理后台用户
            webSocketSenderApi.sendObject(UserTypeEnum.ADMIN.getValue(), 
                    WebSocketMessageTypeConstants.ALARM_NEW, message);
            log.info("[pushNewAlarm][告警ID({}) 编码({}) 推送新告警消息成功]", 
                    alarm.getId(), alarm.getAlarmCode());
        } catch (Exception e) {
            log.error("[pushNewAlarm][告警ID({}) 推送新告警消息失败]", alarm.getId(), e);
        }
    }

    @Override
    @Async
    public void pushAlarmStatusUpdate(AlarmDO alarm, String operationType, 
                                       String operatorName, String operationRemark) {
        try {
            AlarmWebSocketMessage message = buildAlarmMessage(alarm);
            message.setOperationType(operationType);
            message.setOperatorName(operatorName);
            message.setOperationTime(LocalDateTime.now());
            message.setOperationRemark(operationRemark);
            
            // 推送给所有管理后台用户
            webSocketSenderApi.sendObject(UserTypeEnum.ADMIN.getValue(), 
                    WebSocketMessageTypeConstants.ALARM_STATUS_UPDATE, message);
            log.info("[pushAlarmStatusUpdate][告警ID({}) 编码({}) 操作类型({}) 推送状态更新消息成功]", 
                    alarm.getId(), alarm.getAlarmCode(), operationType);
        } catch (Exception e) {
            log.error("[pushAlarmStatusUpdate][告警ID({}) 推送状态更新消息失败]", alarm.getId(), e);
        }
    }

    @Override
    @Async
    public void pushAlarmEscalation(AlarmDO alarm) {
        try {
            AlarmWebSocketMessage message = buildAlarmMessage(alarm);
            message.setOperationType("ESCALATION");
            message.setOperationTime(alarm.getEscalationTime());
            
            // 推送给所有管理后台用户
            webSocketSenderApi.sendObject(UserTypeEnum.ADMIN.getValue(), 
                    WebSocketMessageTypeConstants.ALARM_ESCALATION, message);
            log.info("[pushAlarmEscalation][告警ID({}) 编码({}) 升级级别({}) 推送升级消息成功]", 
                    alarm.getId(), alarm.getAlarmCode(), alarm.getEscalationLevel());
        } catch (Exception e) {
            log.error("[pushAlarmEscalation][告警ID({}) 推送升级消息失败]", alarm.getId(), e);
        }
    }

    @Override
    @Async
    public void pushLinkageExecutionResult(LinkageExecutionDO execution, 
                                            String alarmCode, String linkageRuleName) {
        try {
            LinkageExecutionWebSocketMessage message = buildLinkageExecutionMessage(
                    execution, alarmCode, linkageRuleName);
            
            // 推送给所有管理后台用户
            webSocketSenderApi.sendObject(UserTypeEnum.ADMIN.getValue(), 
                    WebSocketMessageTypeConstants.LINKAGE_EXECUTION_RESULT, message);
            log.info("[pushLinkageExecutionResult][执行记录ID({}) 告警编码({}) 状态({}) 推送联动执行结果成功]", 
                    execution.getId(), alarmCode, execution.getExecutionStatus());
        } catch (Exception e) {
            log.error("[pushLinkageExecutionResult][执行记录ID({}) 推送联动执行结果失败]", 
                    execution.getId(), e);
        }
    }

    @Override
    @Async
    public void pushLinkageManualIntervention(LinkageExecutionDO execution, 
                                               String alarmCode, String linkageRuleName) {
        try {
            LinkageExecutionWebSocketMessage message = buildLinkageExecutionMessage(
                    execution, alarmCode, linkageRuleName);
            
            // 推送给所有管理后台用户
            webSocketSenderApi.sendObject(UserTypeEnum.ADMIN.getValue(), 
                    WebSocketMessageTypeConstants.LINKAGE_MANUAL_INTERVENTION, message);
            log.info("[pushLinkageManualIntervention][执行记录ID({}) 告警编码({}) 推送人工介入消息成功]", 
                    execution.getId(), alarmCode);
        } catch (Exception e) {
            log.error("[pushLinkageManualIntervention][执行记录ID({}) 推送人工介入消息失败]", 
                    execution.getId(), e);
        }
    }

    /**
     * 构建告警 WebSocket 消息
     */
    private AlarmWebSocketMessage buildAlarmMessage(AlarmDO alarm) {
        return new AlarmWebSocketMessage()
                .setId(alarm.getId())
                .setAlarmCode(alarm.getAlarmCode())
                .setAlarmTypePath(alarm.getAlarmTypePath())
                .setAlarmLevel(alarm.getAlarmLevel())
                .setAlarmStatus(alarm.getAlarmStatus())
                .setAlarmSource(alarm.getAlarmSource())
                .setAlarmContent(alarm.getAlarmContent())
                .setDeviceName(alarm.getDeviceName())
                .setLocationName(alarm.getLocationName())
                .setTriggerValue(alarm.getTriggerValue())
                .setThresholdValue(alarm.getThresholdValue())
                .setTriggerCount(alarm.getTriggerCount())
                .setEscalationLevel(alarm.getEscalationLevel())
                .setDurationSeconds(alarm.getDurationSeconds())
                .setCreateTime(alarm.getCreateTime())
                .setUpdateTime(alarm.getUpdateTime());
    }

    /**
     * 构建联动执行 WebSocket 消息
     */
    private LinkageExecutionWebSocketMessage buildLinkageExecutionMessage(
            LinkageExecutionDO execution, String alarmCode, String linkageRuleName) {
        LinkageExecutionWebSocketMessage message = new LinkageExecutionWebSocketMessage()
                .setId(execution.getId())
                .setAlarmId(execution.getAlarmId())
                .setAlarmCode(alarmCode)
                .setLinkageRuleId(execution.getLinkageRuleId())
                .setLinkageRuleName(linkageRuleName)
                .setActionType(execution.getActionType())
                .setTargetDeviceId(execution.getTargetDeviceId())
                .setTargetDeviceName(execution.getTargetDeviceName())
                .setExecutionStatus(execution.getExecutionStatus())
                .setRetryCount(execution.getRetryCount())
                .setExecutionResult(execution.getExecutionResult())
                .setErrorMessage(execution.getErrorMessage())
                .setStartTime(execution.getStartTime())
                .setEndTime(execution.getEndTime())
                .setDurationMs(execution.getDurationMs())
                .setManualIntervention(execution.getManualIntervention())
                .setCreateTime(execution.getCreateTime());
        
        // 设置动作类型名称
        LinkageActionTypeEnum actionTypeEnum = LinkageActionTypeEnum.getByValue(execution.getActionType());
        if (actionTypeEnum != null) {
            message.setActionTypeName(actionTypeEnum.getDescription());
        }
        
        // 设置执行状态名称
        LinkageExecutionStatusEnum statusEnum = LinkageExecutionStatusEnum.getByValue(execution.getExecutionStatus());
        if (statusEnum != null) {
            message.setExecutionStatusName(statusEnum.getDescription());
        }
        
        return message;
    }

}
