package cn.cheers.x.alarm.service.notify;

import cn.cheers.x.alarm.dal.dataobject.AlarmDO;
import cn.cheers.x.alarm.dal.dataobject.LinkageExecutionDO;

import java.util.List;

/**
 * 告警通知服务接口
 * 
 * <p>负责告警相关通知的发送，支持多种通知渠道。</p>
 * 
 * <p>核心功能：
 * <ul>
 *   <li>告警通知：新告警触发时发送通知（FR-016）</li>
 *   <li>联动失败通知：联动执行失败时通知值班员（BR-BIZ-006）</li>
 *   <li>告警升级通知：告警超时未确认时发送升级通知（BR-BIZ-002）</li>
 * </ul>
 * </p>
 * 
 * <p>支持的通知渠道：
 * <ul>
 *   <li>系统消息：站内消息通知</li>
 *   <li>短信：预留接口</li>
 *   <li>邮件：预留接口</li>
 *   <li>微信：预留接口</li>
 *   <li>钉钉：预留接口</li>
 * </ul>
 * </p>
 * 
 * <p>业务规则：
 * <ul>
 *   <li>BR-TIM-003：告警通知发送延迟不超过10秒</li>
 *   <li>BR-BIZ-006：联动重试3次后仍失败，需要人工介入，系统发送通知给值班员</li>
 *   <li>BR-BIZ-002：告警升级时通知更高级别人员</li>
 * </ul>
 * </p>
 *
 * @author 告警管理模块
 */
public interface NotificationService {

    // ========== 告警通知 ==========

    /**
     * 发送告警通知
     * 
     * <p>当新告警触发时，向指定接收人发送通知。
     * 通知延迟不超过10秒（BR-TIM-003）。</p>
     *
     * @param alarm      告警信息
     * @param recipients 接收人列表（用户ID列表）
     */
    void sendAlarmNotification(AlarmDO alarm, List<Long> recipients);

    /**
     * 发送告警通知（使用默认接收人）
     * 
     * <p>根据告警级别自动确定通知接收人。</p>
     *
     * @param alarm 告警信息
     */
    void sendAlarmNotification(AlarmDO alarm);

    // ========== 联动失败通知 ==========

    /**
     * 发送联动执行失败通知
     * 
     * <p>当联动动作执行失败且需要人工介入时，通知值班员。
     * 联动重试3次后仍失败，系统必须发送通知给值班员（BR-BIZ-006）。</p>
     *
     * @param execution 联动执行记录
     */
    void sendLinkageFailureNotification(LinkageExecutionDO execution);

    /**
     * 发送联动执行失败通知（指定接收人）
     *
     * @param execution  联动执行记录
     * @param recipients 接收人列表（用户ID列表）
     */
    void sendLinkageFailureNotification(LinkageExecutionDO execution, List<Long> recipients);

    // ========== 告警升级通知 ==========

    /**
     * 发送告警升级通知
     * 
     * <p>当告警超时未确认时，发送升级通知给更高级别人员。
     * 告警升级时保持原有告警级别不变，但通过更强烈的提醒方式通知（BR-BIZ-002）。</p>
     *
     * @param alarm 告警信息
     */
    void sendEscalationNotification(AlarmDO alarm);

    /**
     * 发送告警升级通知（指定接收人）
     *
     * @param alarm      告警信息
     * @param recipients 接收人列表（用户ID列表）
     */
    void sendEscalationNotification(AlarmDO alarm, List<Long> recipients);

    // ========== 批量通知 ==========

    /**
     * 批量发送告警通知
     * 
     * <p>批量发送多个告警的通知。</p>
     *
     * @param alarms     告警列表
     * @param recipients 接收人列表（用户ID列表）
     */
    void batchSendAlarmNotifications(List<AlarmDO> alarms, List<Long> recipients);

    // ========== 通知渠道管理 ==========

    /**
     * 发送系统消息通知
     * 
     * <p>通过站内消息发送通知。</p>
     *
     * @param title      消息标题
     * @param content    消息内容
     * @param recipients 接收人列表（用户ID列表）
     */
    void sendSystemMessage(String title, String content, List<Long> recipients);

    /**
     * 发送短信通知（预留接口）
     * 
     * <p>通过短信渠道发送通知。</p>
     *
     * @param templateCode 短信模板编码
     * @param params       模板参数
     * @param mobiles      手机号列表
     */
    void sendSmsNotification(String templateCode, java.util.Map<String, Object> params, List<String> mobiles);

    /**
     * 发送邮件通知（预留接口）
     * 
     * <p>通过邮件渠道发送通知。</p>
     *
     * @param subject 邮件主题
     * @param content 邮件内容
     * @param emails  邮箱列表
     */
    void sendEmailNotification(String subject, String content, List<String> emails);

    /**
     * 发送微信通知（预留接口）
     * 
     * <p>通过微信公众号/企业微信发送通知。</p>
     *
     * @param templateId 模板ID
     * @param params     模板参数
     * @param openIds    微信OpenID列表
     */
    void sendWechatNotification(String templateId, java.util.Map<String, Object> params, List<String> openIds);

    /**
     * 发送钉钉通知（预留接口）
     * 
     * <p>通过钉钉发送通知。</p>
     *
     * @param templateId 模板ID
     * @param params     模板参数
     * @param userIds    钉钉用户ID列表
     */
    void sendDingTalkNotification(String templateId, java.util.Map<String, Object> params, List<String> userIds);

    // ========== 通知配置查询 ==========

    /**
     * 获取告警级别对应的默认通知接收人
     * 
     * <p>根据告警级别获取应该通知的用户列表。</p>
     *
     * @param alarmLevel 告警级别
     * @return 接收人用户ID列表
     */
    List<Long> getDefaultRecipientsByAlarmLevel(String alarmLevel);

    /**
     * 获取值班员列表
     * 
     * <p>获取当前值班的用户列表，用于联动失败通知。</p>
     *
     * @return 值班员用户ID列表
     */
    List<Long> getOnDutyOperators();

    /**
     * 获取升级通知接收人
     * 
     * <p>获取告警升级时应该通知的更高级别人员。</p>
     *
     * @param alarmLevel 告警级别
     * @return 升级通知接收人用户ID列表
     */
    List<Long> getEscalationRecipients(String alarmLevel);

}
