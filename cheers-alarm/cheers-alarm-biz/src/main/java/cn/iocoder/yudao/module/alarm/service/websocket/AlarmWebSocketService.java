package cn.iocoder.yudao.module.alarm.service.websocket;

import cn.iocoder.yudao.module.alarm.dal.dataobject.AlarmDO;
import cn.iocoder.yudao.module.alarm.dal.dataobject.LinkageExecutionDO;

/**
 * 告警 WebSocket 推送服务接口
 * 
 * 负责告警相关的实时消息推送
 *
 * @author 告警管理模块
 */
public interface AlarmWebSocketService {

    /**
     * 推送新告警消息
     * 
     * 当系统触发新告警或人工上报新告警时调用
     *
     * @param alarm 告警信息
     */
    void pushNewAlarm(AlarmDO alarm);

    /**
     * 推送告警状态更新消息
     * 
     * 当告警状态发生变化（确认、处理、关闭）时调用
     *
     * @param alarm 告警信息
     * @param operationType 操作类型（ACKNOWLEDGE/HANDLE/CLOSE）
     * @param operatorName 操作人姓名
     * @param operationRemark 操作备注
     */
    void pushAlarmStatusUpdate(AlarmDO alarm, String operationType, String operatorName, String operationRemark);

    /**
     * 推送告警升级消息
     * 
     * 当告警因未及时处理而升级时调用
     *
     * @param alarm 告警信息
     */
    void pushAlarmEscalation(AlarmDO alarm);

    /**
     * 推送联动执行结果消息
     * 
     * 当联动动作执行完成（成功或失败）时调用
     *
     * @param execution 联动执行记录
     * @param alarmCode 告警编码
     * @param linkageRuleName 联动规则名称
     */
    void pushLinkageExecutionResult(LinkageExecutionDO execution, String alarmCode, String linkageRuleName);

    /**
     * 推送联动需要人工介入消息
     * 
     * 当联动执行失败且需要人工介入时调用
     *
     * @param execution 联动执行记录
     * @param alarmCode 告警编码
     * @param linkageRuleName 联动规则名称
     */
    void pushLinkageManualIntervention(LinkageExecutionDO execution, String alarmCode, String linkageRuleName);

}
