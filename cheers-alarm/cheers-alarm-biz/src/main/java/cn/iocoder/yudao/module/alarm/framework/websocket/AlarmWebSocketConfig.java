package cn.iocoder.yudao.module.alarm.framework.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 告警模块 WebSocket 配置
 * 
 * 配置告警模块的 WebSocket 推送功能
 * 
 * <p>本模块使用 infra 模块提供的 WebSocketSenderApi 进行消息推送，
 * 支持多节点部署场景下的消息广播。</p>
 * 
 * <h3>消息类型说明：</h3>
 * <ul>
 *   <li>alarm-new: 新告警推送</li>
 *   <li>alarm-status-update: 告警状态更新推送</li>
 *   <li>alarm-escalation: 告警升级推送</li>
 *   <li>linkage-execution-result: 联动执行结果推送</li>
 *   <li>linkage-manual-intervention: 联动需要人工介入推送</li>
 * </ul>
 * 
 * <h3>前端订阅示例：</h3>
 * <pre>
 * // 连接 WebSocket
 * const ws = new WebSocket('ws://localhost:8080/ws?token=xxx');
 * 
 * // 监听消息
 * ws.onmessage = (event) => {
 *   const message = JSON.parse(event.data);
 *   switch (message.type) {
 *     case 'alarm-new':
 *       // 处理新告警
 *       handleNewAlarm(message.content);
 *       break;
 *     case 'alarm-status-update':
 *       // 处理告警状态更新
 *       handleAlarmStatusUpdate(message.content);
 *       break;
 *     case 'alarm-escalation':
 *       // 处理告警升级
 *       handleAlarmEscalation(message.content);
 *       break;
 *     case 'linkage-execution-result':
 *       // 处理联动执行结果
 *       handleLinkageResult(message.content);
 *       break;
 *     case 'linkage-manual-intervention':
 *       // 处理联动需要人工介入
 *       handleManualIntervention(message.content);
 *       break;
 *   }
 * };
 * </pre>
 *
 * @author 告警管理模块
 */
@Configuration
@EnableAsync
@Slf4j
public class AlarmWebSocketConfig {

    /**
     * 配置说明：
     * 
     * 1. WebSocket 连接路径：/ws（由 cheers.websocket.path 配置）
     * 2. 认证方式：通过 URL 参数传递 token，如 ws://localhost:8080/ws?token=xxx
     * 3. 消息格式：JSON 格式，包含 type 和 content 两个字段
     * 4. 推送范围：默认推送给所有管理后台用户（UserTypeEnum.ADMIN）
     * 
     * 如需修改 WebSocket 配置，请在 application.yaml 中配置：
     * 
     * yudao:
     *   websocket:
     *     enable: true
     *     path: /ws
     *     sender-type: local  # 可选：local, redis, rocketmq, kafka, rabbitmq
     */

}
