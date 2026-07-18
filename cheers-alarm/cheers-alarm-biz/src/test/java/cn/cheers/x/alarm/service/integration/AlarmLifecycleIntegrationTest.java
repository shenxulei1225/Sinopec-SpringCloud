package cn.cheers.x.alarm.service.integration;

import cn.cheers.x.framework.test.core.ut.BaseDbUnitTest;
import cn.cheers.x.alarm.config.AlarmTestConfiguration;
import cn.cheers.x.alarm.config.AlarmTestDataBuilder;
import cn.cheers.x.alarm.controller.admin.vo.alarm.*;
import cn.cheers.x.alarm.controller.admin.vo.type.AlarmTypeEntityVO;
import cn.cheers.x.alarm.dal.dataobject.AlarmAuditLogDO;
import cn.cheers.x.alarm.dal.dataobject.AlarmDO;
import cn.cheers.x.alarm.dal.mysql.AlarmAuditLogMapper;
import cn.cheers.x.alarm.dal.mysql.AlarmMapper;
import cn.cheers.x.alarm.enums.*;
import cn.cheers.x.alarm.service.alarm.AlarmServiceImpl;
import cn.cheers.x.alarm.service.audit.AlarmAuditServiceImpl;
import cn.cheers.x.alarm.service.notify.NotificationService;
import cn.cheers.x.alarm.service.type.AlarmTypeService;
import cn.cheers.x.alarm.service.websocket.AlarmWebSocketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import jakarta.annotation.Resource;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 告警生命周期集成测试
 * 
 * <p>测试告警从触发到关闭的完整流程，验证各阶段状态转换和审计日志记录</p>
 * 
 * <p>覆盖需求：FR-009（告警生命周期管理）</p>
 * <p>覆盖业务规则：</p>
 * <ul>
 *   <li>BR-STA-001：告警状态只能按顺序流转 PENDING → ACKNOWLEDGED → HANDLING → CLOSED</li>
 *   <li>BR-STA-002：告警关闭前必须填写关闭原因</li>
 *   <li>BR-BIZ-001：5分钟内相同告警被抑制</li>
 *   <li>BR-BIZ-008：所有告警操作必须记录审计日志</li>
 * </ul>
 *
 * @author 告警管理模块
 */
@Import({
    AlarmTestConfiguration.class, 
    AlarmServiceImpl.class,
    AlarmAuditServiceImpl.class
})
@DisplayName("告警生命周期集成测试")
public class AlarmLifecycleIntegrationTest extends BaseDbUnitTest {

    @Resource
    private AlarmServiceImpl alarmService;

    @Resource
    private AlarmMapper alarmMapper;

    @Resource
    private AlarmAuditLogMapper alarmAuditLogMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @MockitoBean
    private AlarmTypeService alarmTypeService;

    @MockitoBean
    private NotificationService notificationService;

    @MockitoBean
    private AlarmWebSocketService alarmWebSocketService;

    @BeforeEach
    void setUp() {
        // 配置 AlarmTypeService Mock
        when(alarmTypeService.existsAlarmType(anyLong())).thenReturn(true);
        
        AlarmTypeEntityVO typeEntity = new AlarmTypeEntityVO();
        typeEntity.setId(AlarmTestDataBuilder.DEFAULT_ALARM_TYPE_ID);
        typeEntity.setCategoryId(AlarmTestDataBuilder.DEFAULT_ALARM_CATEGORY_ID);
        typeEntity.setModelId(AlarmTestDataBuilder.DEFAULT_ALARM_MODEL_ID);
        when(alarmTypeService.getAlarmTypeEntity(anyLong())).thenReturn(typeEntity);
        when(alarmTypeService.getAlarmTypePath(anyLong())).thenReturn("环境告警 > 水位告警 > 水位超标");
        
        // 配置 Redis Mock - 返回递增序号
        ValueOperations<String, String> valueOps = stringRedisTemplate.opsForValue();
        when(valueOps.increment(anyString())).thenReturn(1L);
    }

    // ========== 完整生命周期测试 ==========

    @Test
    @DisplayName("测试告警完整生命周期：触发 → 确认 → 处理 → 关闭")
    void testAlarmFullLifecycle() {
        // ========== 阶段1：告警触发 ==========
        AlarmTriggerReqVO triggerReqVO = AlarmTestDataBuilder.buildAlarmTriggerReqVO();
        AlarmRespVO triggeredAlarm = alarmService.triggerAlarm(triggerReqVO);
        
        // 验证触发结果
        assertNotNull(triggeredAlarm);
        assertNotNull(triggeredAlarm.getId());
        assertEquals(AlarmStatusEnum.PENDING.getStatus(), triggeredAlarm.getAlarmStatus());
        assertEquals(AlarmSourceEnum.SYSTEM.getSource(), triggeredAlarm.getAlarmSource());
        
        Long alarmId = triggeredAlarm.getId();
        
        // 验证审计日志（创建）
        List<AlarmAuditLogDO> createLogs = alarmAuditLogMapper.selectList(
            new cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX<AlarmAuditLogDO>()
                .eq(AlarmAuditLogDO::getAlarmId, alarmId)
                .eq(AlarmAuditLogDO::getOperationType, AlarmAuditOperationTypeEnum.CREATE.getType())
        );
        assertEquals(1, createLogs.size());
        
        // ========== 阶段2：告警确认 ==========
        AlarmAcknowledgeReqVO acknowledgeReqVO = AlarmTestDataBuilder.buildAlarmAcknowledgeReqVO();
        alarmService.acknowledgeAlarm(alarmId, acknowledgeReqVO);
        
        // 验证确认结果
        AlarmDO acknowledgedAlarm = alarmMapper.selectById(alarmId);
        assertEquals(AlarmStatusEnum.ACKNOWLEDGED.getStatus(), acknowledgedAlarm.getAlarmStatus());
        assertNotNull(acknowledgedAlarm.getAcknowledgeTime());
        assertEquals(acknowledgeReqVO.getAcknowledgeRemark(), acknowledgedAlarm.getAcknowledgeRemark());
        
        // 验证审计日志（确认）
        List<AlarmAuditLogDO> acknowledgeLogs = alarmAuditLogMapper.selectList(
            new cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX<AlarmAuditLogDO>()
                .eq(AlarmAuditLogDO::getAlarmId, alarmId)
                .eq(AlarmAuditLogDO::getOperationType, AlarmAuditOperationTypeEnum.ACKNOWLEDGE.getType())
        );
        assertEquals(1, acknowledgeLogs.size());
        
        // ========== 阶段3：告警处理 ==========
        AlarmHandleReqVO handleReqVO = AlarmTestDataBuilder.buildAlarmHandleReqVO();
        alarmService.handleAlarm(alarmId, handleReqVO);
        
        // 验证处理结果
        AlarmDO handledAlarm = alarmMapper.selectById(alarmId);
        assertEquals(AlarmStatusEnum.HANDLING.getStatus(), handledAlarm.getAlarmStatus());
        assertNotNull(handledAlarm.getHandleTime());
        assertEquals(handleReqVO.getHandleMeasure(), handledAlarm.getHandleMeasure());
        assertEquals(handleReqVO.getHandleResult(), handledAlarm.getHandleResult());
        
        // 验证审计日志（处理）
        List<AlarmAuditLogDO> handleLogs = alarmAuditLogMapper.selectList(
            new cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX<AlarmAuditLogDO>()
                .eq(AlarmAuditLogDO::getAlarmId, alarmId)
                .eq(AlarmAuditLogDO::getOperationType, AlarmAuditOperationTypeEnum.HANDLE.getType())
        );
        assertEquals(1, handleLogs.size());
        
        // ========== 阶段4：告警关闭 ==========
        AlarmCloseReqVO closeReqVO = AlarmTestDataBuilder.buildAlarmCloseReqVO();
        alarmService.closeAlarm(alarmId, closeReqVO);
        
        // 验证关闭结果
        AlarmDO closedAlarm = alarmMapper.selectById(alarmId);
        assertEquals(AlarmStatusEnum.CLOSED.getStatus(), closedAlarm.getAlarmStatus());
        assertNotNull(closedAlarm.getCloseTime());
        assertEquals(closeReqVO.getCloseReason(), closedAlarm.getCloseReason());
        assertEquals(closeReqVO.getCloseRemark(), closedAlarm.getCloseRemark());
        assertNotNull(closedAlarm.getDurationSeconds());
        assertTrue(closedAlarm.getDurationSeconds() >= 0);
        
        // 验证审计日志（关闭）
        List<AlarmAuditLogDO> closeLogs = alarmAuditLogMapper.selectList(
            new cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX<AlarmAuditLogDO>()
                .eq(AlarmAuditLogDO::getAlarmId, alarmId)
                .eq(AlarmAuditLogDO::getOperationType, AlarmAuditOperationTypeEnum.CLOSE.getType())
        );
        assertEquals(1, closeLogs.size());
        
        // ========== 验证完整审计日志链 ==========
        List<AlarmAuditLogDO> allLogs = alarmAuditLogMapper.selectList(
            new cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX<AlarmAuditLogDO>()
                .eq(AlarmAuditLogDO::getAlarmId, alarmId)
                .orderByAsc(AlarmAuditLogDO::getOperationTime)
        );
        assertEquals(4, allLogs.size()); // CREATE, ACKNOWLEDGE, HANDLE, CLOSE
        
        // 验证 WebSocket 推送被调用
        verify(alarmWebSocketService, times(1)).pushNewAlarm(any(AlarmDO.class));
        verify(alarmWebSocketService, times(3)).pushAlarmStatusUpdate(any(AlarmDO.class), anyString(), any(), any());
    }

    // ========== 人工上报生命周期测试 ==========

    @Test
    @DisplayName("测试人工上报告警完整生命周期")
    void testManualReportAlarmLifecycle() {
        // ========== 阶段1：人工上报 ==========
        AlarmReportReqVO reportReqVO = AlarmTestDataBuilder.buildAlarmReportReqVO();
        AlarmRespVO reportedAlarm = alarmService.reportAlarm(reportReqVO);
        
        // 验证上报结果
        assertNotNull(reportedAlarm);
        assertNotNull(reportedAlarm.getId());
        assertEquals(AlarmStatusEnum.PENDING.getStatus(), reportedAlarm.getAlarmStatus());
        assertEquals(AlarmSourceEnum.MANUAL.getSource(), reportedAlarm.getAlarmSource());
        
        Long alarmId = reportedAlarm.getId();
        
        // ========== 阶段2-4：确认 → 处理 → 关闭 ==========
        alarmService.acknowledgeAlarm(alarmId, AlarmTestDataBuilder.buildAlarmAcknowledgeReqVO());
        alarmService.handleAlarm(alarmId, AlarmTestDataBuilder.buildAlarmHandleReqVO());
        alarmService.closeAlarm(alarmId, AlarmTestDataBuilder.buildAlarmCloseReqVO());
        
        // 验证最终状态
        AlarmDO closedAlarm = alarmMapper.selectById(alarmId);
        assertEquals(AlarmStatusEnum.CLOSED.getStatus(), closedAlarm.getAlarmStatus());
        assertEquals(AlarmSourceEnum.MANUAL.getSource(), closedAlarm.getAlarmSource());
    }

    // ========== 状态转换约束测试 ==========

    @Test
    @DisplayName("测试非法状态转换：待确认 → 处理（跳过确认）")
    void testInvalidStateTransition_PendingToHandling() {
        // 准备：创建待确认状态的告警
        AlarmDO alarm = AlarmTestDataBuilder.createPendingAlarmDO();
        alarmMapper.insert(alarm);
        
        // 执行：尝试直接处理（跳过确认）
        AlarmHandleReqVO handleReqVO = AlarmTestDataBuilder.buildAlarmHandleReqVO();
        
        // 验证：应该抛出异常
        assertThrows(Exception.class, () -> alarmService.handleAlarm(alarm.getId(), handleReqVO));
        
        // 验证状态未改变
        AlarmDO unchangedAlarm = alarmMapper.selectById(alarm.getId());
        assertEquals(AlarmStatusEnum.PENDING.getStatus(), unchangedAlarm.getAlarmStatus());
    }

    @Test
    @DisplayName("测试非法状态转换：待确认 → 关闭（跳过确认和处理）")
    void testInvalidStateTransition_PendingToClose() {
        // 准备：创建待确认状态的告警
        AlarmDO alarm = AlarmTestDataBuilder.createPendingAlarmDO();
        alarmMapper.insert(alarm);
        
        // 执行：尝试直接关闭
        AlarmCloseReqVO closeReqVO = AlarmTestDataBuilder.buildAlarmCloseReqVO();
        
        // 验证：应该抛出异常
        assertThrows(Exception.class, () -> alarmService.closeAlarm(alarm.getId(), closeReqVO));
    }

    @Test
    @DisplayName("测试非法状态转换：已确认 → 关闭（跳过处理）")
    void testInvalidStateTransition_AcknowledgedToClose() {
        // 准备：创建已确认状态的告警
        AlarmDO alarm = AlarmTestDataBuilder.createAcknowledgedAlarmDO();
        alarmMapper.insert(alarm);
        
        // 执行：尝试直接关闭（跳过处理）
        AlarmCloseReqVO closeReqVO = AlarmTestDataBuilder.buildAlarmCloseReqVO();
        
        // 验证：应该抛出异常
        assertThrows(Exception.class, () -> alarmService.closeAlarm(alarm.getId(), closeReqVO));
    }

    @Test
    @DisplayName("测试非法状态转换：已关闭 → 确认")
    void testInvalidStateTransition_ClosedToAcknowledge() {
        // 准备：创建已关闭状态的告警
        AlarmDO alarm = AlarmTestDataBuilder.createClosedAlarmDO();
        alarmMapper.insert(alarm);
        
        // 执行：尝试确认已关闭的告警
        AlarmAcknowledgeReqVO acknowledgeReqVO = AlarmTestDataBuilder.buildAlarmAcknowledgeReqVO();
        
        // 验证：应该抛出异常
        assertThrows(Exception.class, () -> alarmService.acknowledgeAlarm(alarm.getId(), acknowledgeReqVO));
    }

    @Test
    @DisplayName("测试重复确认：已确认 → 再次确认")
    void testInvalidStateTransition_DoubleAcknowledge() {
        // 准备：创建已确认状态的告警
        AlarmDO alarm = AlarmTestDataBuilder.createAcknowledgedAlarmDO();
        alarmMapper.insert(alarm);
        
        // 执行：尝试再次确认
        AlarmAcknowledgeReqVO acknowledgeReqVO = AlarmTestDataBuilder.buildAlarmAcknowledgeReqVO();
        
        // 验证：应该抛出异常
        assertThrows(Exception.class, () -> alarmService.acknowledgeAlarm(alarm.getId(), acknowledgeReqVO));
    }

    // ========== 告警抑制测试 ==========

    @Test
    @DisplayName("测试告警抑制：5分钟内同设备同类型告警被抑制")
    void testAlarmSuppression() {
        // 准备：触发第一个告警
        AlarmTriggerReqVO firstTrigger = AlarmTestDataBuilder.buildAlarmTriggerReqVO();
        firstTrigger.setDeviceId(999L);
        firstTrigger.setAlarmTypeId(888L);
        AlarmRespVO firstAlarm = alarmService.triggerAlarm(firstTrigger);
        
        assertNotNull(firstAlarm);
        Long firstAlarmId = firstAlarm.getId();
        
        // 执行：在5分钟内触发相同设备相同类型的告警
        AlarmTriggerReqVO secondTrigger = AlarmTestDataBuilder.buildAlarmTriggerReqVO();
        secondTrigger.setDeviceId(999L);
        secondTrigger.setAlarmTypeId(888L);
        AlarmRespVO secondAlarm = alarmService.triggerAlarm(secondTrigger);
        
        // 验证：返回的是第一个告警（被抑制）
        assertNotNull(secondAlarm);
        assertEquals(firstAlarmId, secondAlarm.getId());
        
        // 验证触发次数增加
        AlarmDO updatedAlarm = alarmMapper.selectById(firstAlarmId);
        assertEquals(2, updatedAlarm.getTriggerCount());
    }

    @Test
    @DisplayName("测试告警不抑制：不同设备的告警")
    void testAlarmNoSuppression_DifferentDevice() {
        // 准备：触发第一个告警
        AlarmTriggerReqVO firstTrigger = AlarmTestDataBuilder.buildAlarmTriggerReqVO();
        firstTrigger.setDeviceId(111L);
        firstTrigger.setAlarmTypeId(888L);
        AlarmRespVO firstAlarm = alarmService.triggerAlarm(firstTrigger);
        
        // 执行：触发不同设备的告警
        AlarmTriggerReqVO secondTrigger = AlarmTestDataBuilder.buildAlarmTriggerReqVO();
        secondTrigger.setDeviceId(222L); // 不同设备
        secondTrigger.setAlarmTypeId(888L);
        AlarmRespVO secondAlarm = alarmService.triggerAlarm(secondTrigger);
        
        // 验证：创建了新告警
        assertNotNull(secondAlarm);
        assertNotEquals(firstAlarm.getId(), secondAlarm.getId());
    }

    @Test
    @DisplayName("测试告警不抑制：不同类型的告警")
    void testAlarmNoSuppression_DifferentType() {
        // 准备：触发第一个告警
        AlarmTriggerReqVO firstTrigger = AlarmTestDataBuilder.buildAlarmTriggerReqVO();
        firstTrigger.setDeviceId(111L);
        firstTrigger.setAlarmTypeId(888L);
        AlarmRespVO firstAlarm = alarmService.triggerAlarm(firstTrigger);
        
        // 执行：触发不同类型的告警
        AlarmTriggerReqVO secondTrigger = AlarmTestDataBuilder.buildAlarmTriggerReqVO();
        secondTrigger.setDeviceId(111L);
        secondTrigger.setAlarmTypeId(999L); // 不同类型
        AlarmRespVO secondAlarm = alarmService.triggerAlarm(secondTrigger);
        
        // 验证：创建了新告警
        assertNotNull(secondAlarm);
        assertNotEquals(firstAlarm.getId(), secondAlarm.getId());
    }

    // ========== 关闭原因验证测试 ==========

    @Test
    @DisplayName("测试关闭告警必须填写关闭原因")
    void testCloseAlarm_RequiresCloseReason() {
        // 准备：创建处理中状态的告警
        AlarmDO alarm = AlarmTestDataBuilder.createHandlingAlarmDO();
        alarmMapper.insert(alarm);
        
        // 执行：尝试不填写关闭原因关闭告警
        AlarmCloseReqVO closeReqVO = new AlarmCloseReqVO();
        closeReqVO.setCloseReason(null); // 不填写关闭原因
        
        // 验证：应该抛出异常
        assertThrows(Exception.class, () -> alarmService.closeAlarm(alarm.getId(), closeReqVO));
    }

    @Test
    @DisplayName("测试关闭告警使用无效关闭原因")
    void testCloseAlarm_InvalidCloseReason() {
        // 准备：创建处理中状态的告警
        AlarmDO alarm = AlarmTestDataBuilder.createHandlingAlarmDO();
        alarmMapper.insert(alarm);
        
        // 执行：使用无效的关闭原因
        AlarmCloseReqVO closeReqVO = new AlarmCloseReqVO();
        closeReqVO.setCloseReason("INVALID_REASON");
        
        // 验证：应该抛出异常
        assertThrows(Exception.class, () -> alarmService.closeAlarm(alarm.getId(), closeReqVO));
    }

    // ========== 告警详情查询测试 ==========

    @Test
    @DisplayName("测试获取告警详情")
    void testGetAlarmDetail() {
        // 准备：创建告警并完成生命周期
        AlarmTriggerReqVO triggerReqVO = AlarmTestDataBuilder.buildAlarmTriggerReqVO();
        AlarmRespVO triggeredAlarm = alarmService.triggerAlarm(triggerReqVO);
        Long alarmId = triggeredAlarm.getId();
        
        alarmService.acknowledgeAlarm(alarmId, AlarmTestDataBuilder.buildAlarmAcknowledgeReqVO());
        alarmService.handleAlarm(alarmId, AlarmTestDataBuilder.buildAlarmHandleReqVO());
        alarmService.closeAlarm(alarmId, AlarmTestDataBuilder.buildAlarmCloseReqVO());
        
        // 执行：获取告警详情
        AlarmDetailRespVO detail = alarmService.getAlarmDetail(alarmId);
        
        // 验证
        assertNotNull(detail);
        assertEquals(alarmId, detail.getId());
        assertEquals(AlarmStatusEnum.CLOSED.getStatus(), detail.getAlarmStatus());
        assertNotNull(detail.getAcknowledgeTime());
        assertNotNull(detail.getHandleTime());
        assertNotNull(detail.getCloseTime());
        assertNotNull(detail.getDurationSeconds());
    }

    @Test
    @DisplayName("测试获取不存在的告警详情")
    void testGetAlarmDetail_NotExists() {
        // 执行并验证异常
        assertThrows(Exception.class, () -> alarmService.getAlarmDetail(999999L));
    }

}
