/**
 * 告警管理模块集成测试包
 * 
 * <p>包含告警管理模块的集成测试，测试多个服务之间的协作和完整业务流程。</p>
 * 
 * <h3>测试类</h3>
 * <ul>
 *   <li>{@link cn.cheers.x.alarm.service.integration.AlarmLifecycleIntegrationTest} - 告警生命周期集成测试</li>
 *   <li>{@link cn.cheers.x.alarm.service.integration.AlarmLinkageIntegrationTest} - 告警联动集成测试</li>
 * </ul>
 * 
 * <h3>覆盖需求</h3>
 * <ul>
 *   <li>FR-009：告警生命周期管理</li>
 *   <li>FR-011：联动控制</li>
 * </ul>
 * 
 * <h3>覆盖业务规则</h3>
 * <ul>
 *   <li>BR-STA-001：告警状态只能按顺序流转</li>
 *   <li>BR-STA-002：告警关闭前必须填写关闭原因</li>
 *   <li>BR-BIZ-001：5分钟内相同告警被抑制</li>
 *   <li>BR-BIZ-004：消防设施控制优先</li>
 *   <li>BR-BIZ-005：联动重试机制</li>
 *   <li>BR-BIZ-006：人工介入标记</li>
 *   <li>BR-BIZ-008：告警操作审计日志</li>
 *   <li>BR-BIZ-009：联动控制审计日志</li>
 * </ul>
 *
 * @author 告警管理模块
 */
package cn.cheers.x.alarm.service.integration;
