package cn.iocoder.yudao.module.alarm.service;

import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.alarm.config.AlarmTestConfiguration;
import cn.iocoder.yudao.module.alarm.config.AlarmTestDataBuilder;
import cn.iocoder.yudao.module.alarm.controller.admin.vo.alarm.*;
import cn.iocoder.yudao.module.alarm.controller.admin.vo.type.AlarmTypeEntityVO;
import cn.iocoder.yudao.module.alarm.dal.dataobject.AlarmDO;
import cn.iocoder.yudao.module.alarm.dal.mysql.AlarmAttachmentMapper;
import cn.iocoder.yudao.module.alarm.dal.mysql.AlarmMapper;
import cn.iocoder.yudao.module.alarm.enums.*;
import cn.iocoder.yudao.module.alarm.service.alarm.AlarmServiceImpl;
import cn.iocoder.yudao.module.alarm.service.audit.AlarmAuditService;
import cn.iocoder.yudao.module.alarm.service.notify.NotificationService;
import cn.iocoder.yudao.module.alarm.service.type.AlarmTypeService;
import cn.iocoder.yudao.module.alarm.service.websocket.AlarmWebSocketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * AlarmService 单元测试
 * 
 * <p>测试告警触发、抑制、状态转换等核心逻辑</p>
 * 
 * <p>覆盖需求：FR-001（告警触发）、FR-009（告警抑制）</p>
 * <p>覆盖业务规则：BR-BIZ-001（5分钟抑制窗口）、BR-STA-001（状态流转）</p>
 *
 * @author 告警管理模块
 */
@Import({AlarmTestConfiguration.class, AlarmServiceImpl.class})
@DisplayName("AlarmService 单元测试")
public class AlarmServiceTest extends BaseDbUnitTest {

    @Resource
    private AlarmServiceImpl alarmService;

    @Resource
    private AlarmMapper alarmMapper;

    @Resource
    private AlarmAttachmentMapper alarmAttachmentMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @MockitoBean
    private AlarmTypeService alarmTypeService;

    @MockitoBean
    private AlarmAuditService alarmAuditService;

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
        when(alarmTypeService.getAlarmTypePath(anyLong())).thenReturn("环境告警 > 水位告警");
        
        // 配置 Redis Mock - 返回递增序号
        ValueOperations<String, String> valueOps = stringRedisTemplate.opsForValue();
        when(valueOps.increment(anyString())).thenReturn(1L);
    }

    // ========== 告警触发测试 ==========

    @Test
    @DisplayName("测试告警触发成功")
    void testTriggerAlarm_Success() {
        // 准备测试数据
        AlarmTriggerReqVO reqVO = AlarmTestDataBuilder.buildAlarmTriggerReqVO();
        
        // 执行
        AlarmRespVO result = alarmService.triggerAlarm(reqVO);
        
        // 验证
        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getAlarmCode());
        assertEquals(AlarmStatusEnum.PENDING.getStatus(), result.getAlarmStatus());
        assertEquals(AlarmSourceEnum.SYSTEM.getSource(), result.getAlarmSource());
        assertEquals(reqVO.getAlarmLevel(), result.getAlarmLevel());
        assertEquals(reqVO.getDeviceId(), result.getDeviceId());
        
        // 验证数据库
        AlarmDO savedAlarm = alarmMapper.selectById(result.getId());
        assertNotNull(savedAlarm);
        assertEquals(1, savedAlarm.getTriggerCount());
        
        // 验证审计日志被调用
        verify(alarmAuditService).logAlarmCreate(any(AlarmDO.class), any());
        
        // 验证 WebSocket 推送被调用
        verify(alarmWebSocketService).pushNewAlarm(any(AlarmDO.class));
    }

    @Test
    @DisplayName("测试告警抑制 - 5分钟内同设备同类型告警被抑制")
    void testTriggerAlarm_Suppressed() {
        // 准备：先创建一个告警
        AlarmDO existingAlarm = AlarmTestDataBuilder.buildAlarmDO()
                .deviceId(100L)
                .alarmTypeId(1L)
                .alarmStatus(AlarmStatusEnum.PENDING.getStatus())
                .build();
        existingAlarm.setCreateTime(LocalDateTime.now().minusMinutes(2)); // 2分钟前创建
        alarmMapper.insert(existingAlarm);
        
        // 准备触发请求（同设备同类型）
        AlarmTriggerReqVO reqVO = AlarmTestDataBuilder.buildAlarmTriggerReqVO();
        reqVO.setDeviceId(100L);
        reqVO.setAlarmTypeId(1L);
        
        // 执行
        AlarmRespVO result = alarmService.triggerAlarm(reqVO);
        
        // 验证：返回已存在的告警
        assertNotNull(result);
        assertEquals(existingAlarm.getId(), result.getId());
        
        // 验证触发次数增加
        AlarmDO updatedAlarm = alarmMapper.selectById(existingAlarm.getId());
        assertEquals(2, updatedAlarm.getTriggerCount());
    }

    @Test
    @DisplayName("测试告警触发 - 告警类型不存在")
    void testTriggerAlarm_TypeNotExists() {
        // 配置 Mock
        when(alarmTypeService.existsAlarmType(999L)).thenReturn(false);
        
        // 准备测试数据
        AlarmTriggerReqVO reqVO = AlarmTestDataBuilder.buildAlarmTriggerReqVO();
        reqVO.setAlarmTypeId(999L);
        
        // 执行并验证异常
        assertThrows(Exception.class, () -> alarmService.triggerAlarm(reqVO));
    }

    @Test
    @DisplayName("测试告警触发 - 无效告警级别")
    void testTriggerAlarm_InvalidLevel() {
        // 准备测试数据
        AlarmTriggerReqVO reqVO = AlarmTestDataBuilder.buildAlarmTriggerReqVO();
        reqVO.setAlarmLevel("INVALID_LEVEL");
        
        // 执行并验证异常
        assertThrows(Exception.class, () -> alarmService.triggerAlarm(reqVO));
    }

    // ========== 人工上报测试 ==========

    @Test
    @DisplayName("测试人工上报告警成功")
    void testReportAlarm_Success() {
        // 准备测试数据
        AlarmReportReqVO reqVO = AlarmTestDataBuilder.buildAlarmReportReqVO();
        
        // 执行
        AlarmRespVO result = alarmService.reportAlarm(reqVO);
        
        // 验证
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(AlarmStatusEnum.PENDING.getStatus(), result.getAlarmStatus());
        assertEquals(AlarmSourceEnum.MANUAL.getSource(), result.getAlarmSource());
    }

    // ========== 状态转换测试 ==========

    @Test
    @DisplayName("测试告警确认成功")
    void testAcknowledgeAlarm_Success() {
        // 准备：创建待确认状态的告警
        AlarmDO alarm = AlarmTestDataBuilder.createPendingAlarmDO();
        alarmMapper.insert(alarm);
        
        // 准备确认请求
        AlarmAcknowledgeReqVO reqVO = AlarmTestDataBuilder.buildAlarmAcknowledgeReqVO();
        
        // 执行
        alarmService.acknowledgeAlarm(alarm.getId(), reqVO);
        
        // 验证
        AlarmDO updatedAlarm = alarmMapper.selectById(alarm.getId());
        assertEquals(AlarmStatusEnum.ACKNOWLEDGED.getStatus(), updatedAlarm.getAlarmStatus());
        assertNotNull(updatedAlarm.getAcknowledgeTime());
        
        // 验证审计日志被调用
        verify(alarmAuditService).logAlarmAcknowledge(any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("测试告警确认失败 - 告警已关闭")
    void testAcknowledgeAlarm_AlreadyClosed() {
        // 准备：创建已关闭状态的告警
        AlarmDO alarm = AlarmTestDataBuilder.createClosedAlarmDO();
        alarmMapper.insert(alarm);
        
        // 执行并验证异常
        AlarmAcknowledgeReqVO reqVO = AlarmTestDataBuilder.buildAlarmAcknowledgeReqVO();
        assertThrows(Exception.class, () -> alarmService.acknowledgeAlarm(alarm.getId(), reqVO));
    }

    @Test
    @DisplayName("测试告警确认失败 - 状态不允许确认")
    void testAcknowledgeAlarm_InvalidStatus() {
        // 准备：创建已确认状态的告警（不能再次确认）
        AlarmDO alarm = AlarmTestDataBuilder.createAcknowledgedAlarmDO();
        alarmMapper.insert(alarm);
        
        // 执行并验证异常
        AlarmAcknowledgeReqVO reqVO = AlarmTestDataBuilder.buildAlarmAcknowledgeReqVO();
        assertThrows(Exception.class, () -> alarmService.acknowledgeAlarm(alarm.getId(), reqVO));
    }

    @Test
    @DisplayName("测试告警处理成功")
    void testHandleAlarm_Success() {
        // 准备：创建已确认状态的告警
        AlarmDO alarm = AlarmTestDataBuilder.createAcknowledgedAlarmDO();
        alarmMapper.insert(alarm);
        
        // 准备处理请求
        AlarmHandleReqVO reqVO = AlarmTestDataBuilder.buildAlarmHandleReqVO();
        
        // 执行
        alarmService.handleAlarm(alarm.getId(), reqVO);
        
        // 验证
        AlarmDO updatedAlarm = alarmMapper.selectById(alarm.getId());
        assertEquals(AlarmStatusEnum.HANDLING.getStatus(), updatedAlarm.getAlarmStatus());
        assertNotNull(updatedAlarm.getHandleTime());
        assertEquals(reqVO.getHandleMeasure(), updatedAlarm.getHandleMeasure());
        assertEquals(reqVO.getHandleResult(), updatedAlarm.getHandleResult());
    }

    @Test
    @DisplayName("测试告警处理失败 - 状态不允许处理")
    void testHandleAlarm_InvalidStatus() {
        // 准备：创建待确认状态的告警（必须先确认才能处理）
        AlarmDO alarm = AlarmTestDataBuilder.createPendingAlarmDO();
        alarmMapper.insert(alarm);
        
        // 执行并验证异常
        AlarmHandleReqVO reqVO = AlarmTestDataBuilder.buildAlarmHandleReqVO();
        assertThrows(Exception.class, () -> alarmService.handleAlarm(alarm.getId(), reqVO));
    }

    @Test
    @DisplayName("测试告警关闭成功")
    void testCloseAlarm_Success() {
        // 准备：创建处理中状态的告警
        AlarmDO alarm = AlarmTestDataBuilder.createHandlingAlarmDO();
        alarmMapper.insert(alarm);
        
        // 准备关闭请求
        AlarmCloseReqVO reqVO = AlarmTestDataBuilder.buildAlarmCloseReqVO();
        
        // 执行
        alarmService.closeAlarm(alarm.getId(), reqVO);
        
        // 验证
        AlarmDO updatedAlarm = alarmMapper.selectById(alarm.getId());
        assertEquals(AlarmStatusEnum.CLOSED.getStatus(), updatedAlarm.getAlarmStatus());
        assertNotNull(updatedAlarm.getCloseTime());
        assertEquals(reqVO.getCloseReason(), updatedAlarm.getCloseReason());
        assertNotNull(updatedAlarm.getDurationSeconds());
    }

    @Test
    @DisplayName("测试告警关闭失败 - 状态不允许关闭")
    void testCloseAlarm_InvalidStatus() {
        // 准备：创建已确认状态的告警（必须先处理才能关闭）
        AlarmDO alarm = AlarmTestDataBuilder.createAcknowledgedAlarmDO();
        alarmMapper.insert(alarm);
        
        // 执行并验证异常
        AlarmCloseReqVO reqVO = AlarmTestDataBuilder.buildAlarmCloseReqVO();
        assertThrows(Exception.class, () -> alarmService.closeAlarm(alarm.getId(), reqVO));
    }

    @Test
    @DisplayName("测试告警关闭失败 - 缺少关闭原因")
    void testCloseAlarm_NoReason() {
        // 准备：创建处理中状态的告警
        AlarmDO alarm = AlarmTestDataBuilder.createHandlingAlarmDO();
        alarmMapper.insert(alarm);
        
        // 准备关闭请求（无关闭原因）
        AlarmCloseReqVO reqVO = new AlarmCloseReqVO();
        reqVO.setCloseReason(null);
        
        // 执行并验证异常
        assertThrows(Exception.class, () -> alarmService.closeAlarm(alarm.getId(), reqVO));
    }

    // ========== 告警编码生成测试 ==========

    @Test
    @DisplayName("测试告警编码生成")
    void testGenerateAlarmCode() {
        // 执行
        String code = alarmService.generateAlarmCode();
        
        // 验证格式：ALM-YYYYMMDD-XXXXX
        assertNotNull(code);
        assertTrue(code.startsWith("ALM-"));
        assertEquals(18, code.length()); // ALM-YYYYMMDD-XXXXX = 4+8+1+5 = 18 字符
    }

    // ========== 告警查询测试 ==========

    @Test
    @DisplayName("测试获取告警详情")
    void testGetAlarmDetail() {
        // 准备：创建告警
        AlarmDO alarm = AlarmTestDataBuilder.createPendingAlarmDO();
        alarmMapper.insert(alarm);
        
        // 执行
        AlarmDetailRespVO detail = alarmService.getAlarmDetail(alarm.getId());
        
        // 验证
        assertNotNull(detail);
        assertEquals(alarm.getId(), detail.getId());
        assertEquals(alarm.getAlarmCode(), detail.getAlarmCode());
    }

    @Test
    @DisplayName("测试获取告警详情 - 告警不存在")
    void testGetAlarmDetail_NotExists() {
        // 执行并验证异常
        assertThrows(Exception.class, () -> alarmService.getAlarmDetail(999L));
    }

}
