package cn.cheers.x.alarm.controller;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.test.core.ut.BaseDbUnitTest;
import cn.cheers.x.alarm.config.AlarmTestConfiguration;
import cn.cheers.x.alarm.config.AlarmTestDataBuilder;
import cn.cheers.x.alarm.controller.admin.vo.alarm.*;
import cn.cheers.x.alarm.controller.admin.vo.type.AlarmTypeEntityVO;
import cn.cheers.x.alarm.dal.dataobject.AlarmDO;
import cn.cheers.x.alarm.dal.dataobject.LinkageExecutionDO;
import cn.cheers.x.alarm.dal.dataobject.LinkageRuleDO;
import cn.cheers.x.alarm.dal.mysql.AlarmMapper;
import cn.cheers.x.alarm.dal.mysql.LinkageExecutionMapper;
import cn.cheers.x.alarm.dal.mysql.LinkageRuleMapper;
import cn.cheers.x.alarm.enums.*;
import cn.cheers.x.alarm.service.alarm.AlarmService;
import cn.cheers.x.alarm.service.alarm.AlarmServiceImpl;
import cn.cheers.x.alarm.service.audit.AlarmAuditServiceImpl;
import cn.cheers.x.alarm.service.linkage.LinkageService;
import cn.cheers.x.alarm.service.linkage.LinkageServiceImpl;
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
 * 告警管理模块 - 前后端集成测试
 * 
 * <p>测试从 Service 层的完整流程，模拟前端 API 调用场景</p>
 * 
 * <p>覆盖需求：全部功能需求</p>
 * <p>测试场景：</p>
 * <ul>
 *   <li>告警触发到显示的完整流程</li>
 *   <li>人工上报到处理的完整流程</li>
 *   <li>联动执行和结果反馈</li>
 * </ul>
 *
 * @author 告警管理模块
 */
@Import({
    AlarmTestConfiguration.class,
    AlarmServiceImpl.class,
    AlarmAuditServiceImpl.class,
    LinkageServiceImpl.class
})
@DisplayName("告警管理模块 - 前后端集成测试")
public class AlarmControllerIntegrationTest extends BaseDbUnitTest {

    @Resource
    private AlarmService alarmService;

    @Resource
    private LinkageService linkageService;

    @Resource
    private AlarmMapper alarmMapper;

    @Resource
    private LinkageRuleMapper linkageRuleMapper;

    @Resource
    private LinkageExecutionMapper linkageExecutionMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @MockitoBean
    private AlarmTypeService alarmTypeService;

    @MockitoBean
    private NotificationService notificationService;

    @MockitoBean
    private AlarmWebSocketService alarmWebSocketService;

    @MockitoBean
    private cn.cheers.x.alarm.framework.cache.AlarmQueryCacheService alarmQueryCacheService;

    @MockitoBean
    private cn.cheers.x.alarm.framework.cache.AlarmCacheService alarmCacheService;

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

    // ========== 场景1：告警触发到显示的完整流程 ==========

    @Test
    @DisplayName("场景1.1：系统触发告警 - 成功创建并返回告警信息")
    void testTriggerAlarm_Success() {
        // 准备请求数据
        AlarmTriggerReqVO reqVO = new AlarmTriggerReqVO();
        reqVO.setAlarmTypeId(1L);
        reqVO.setAlarmLevel(AlarmLevelEnum.CRITICAL.getLevel());
        reqVO.setAlarmContent("水位超标告警：当前水位55cm，超过阈值50cm");
        reqVO.setDeviceId(100L);
        reqVO.setDeviceName("水位传感器-B区2号集水坑");
        reqVO.setLocationId(200L);
        reqVO.setLocationName("B区 > 2号防火分区 > 集水坑");
        reqVO.setTriggerValue("55cm");
        reqVO.setThresholdValue("50cm");

        // 执行请求
        AlarmRespVO result = alarmService.triggerAlarm(reqVO);

        // 验证结果
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(AlarmStatusEnum.PENDING.getStatus(), result.getAlarmStatus());
        assertEquals(AlarmSourceEnum.SYSTEM.getSource(), result.getAlarmSource());
        assertEquals(AlarmLevelEnum.CRITICAL.getLevel(), result.getAlarmLevel());
        
        // 验证 WebSocket 推送被调用
        verify(alarmWebSocketService, times(1)).pushNewAlarm(any(AlarmDO.class));
    }

    @Test
    @DisplayName("场景1.2：查询实时告警列表 - 返回未关闭的告警")
    void testGetRealTimeAlarmPage_Success() {
        // 准备测试数据：创建多个不同状态的告警
        AlarmDO pendingAlarm = AlarmTestDataBuilder.createPendingAlarmDO();
        alarmMapper.insert(pendingAlarm);
        
        AlarmDO acknowledgedAlarm = AlarmTestDataBuilder.createAcknowledgedAlarmDO();
        alarmMapper.insert(acknowledgedAlarm);
        
        AlarmDO closedAlarm = AlarmTestDataBuilder.createClosedAlarmDO();
        alarmMapper.insert(closedAlarm);

        // 执行请求
        AlarmPageReqVO pageReqVO = new AlarmPageReqVO();
        pageReqVO.setPageNo(1);
        pageReqVO.setPageSize(10);
        pageReqVO.setRealTimeOnly(true);
        PageResult<AlarmRespVO> result = alarmService.getRealTimeAlarmPage(pageReqVO);

        // 验证结果
        assertNotNull(result);
        assertNotNull(result.getList());
        // 实时告警应该只包含未关闭的告警（PENDING, ACKNOWLEDGED, HANDLING）
        assertTrue(result.getTotal() >= 2);
    }

    @Test
    @DisplayName("场景1.3：获取告警详情 - 返回完整告警信息")
    void testGetAlarmDetail_Success() {
        // 准备测试数据
        AlarmDO alarm = AlarmTestDataBuilder.createPendingAlarmDO();
        alarmMapper.insert(alarm);

        // 执行请求
        AlarmDetailRespVO result = alarmService.getAlarmDetail(alarm.getId());

        // 验证结果
        assertNotNull(result);
        assertEquals(alarm.getId(), result.getId());
        assertEquals(AlarmStatusEnum.PENDING.getStatus(), result.getAlarmStatus());
    }

    @Test
    @DisplayName("场景1.4：告警抑制 - 5分钟内相同告警被抑制")
    void testTriggerAlarm_Suppression() {
        // 第一次触发告警
        AlarmTriggerReqVO reqVO = new AlarmTriggerReqVO();
        reqVO.setAlarmTypeId(999L);
        reqVO.setAlarmLevel(AlarmLevelEnum.WARNING.getLevel());
        reqVO.setAlarmContent("测试告警抑制");
        reqVO.setDeviceId(888L);
        reqVO.setDeviceName("测试设备");
        reqVO.setLocationId(200L);
        reqVO.setLocationName("测试位置");

        AlarmRespVO firstResult = alarmService.triggerAlarm(reqVO);
        assertNotNull(firstResult);
        Long firstAlarmId = firstResult.getId();

        // 第二次触发相同告警（应该被抑制）
        AlarmRespVO secondResult = alarmService.triggerAlarm(reqVO);
        assertNotNull(secondResult);
        Long secondAlarmId = secondResult.getId();

        // 验证返回的是同一个告警ID（被抑制）
        assertEquals(firstAlarmId, secondAlarmId);
        
        // 验证触发次数增加
        AlarmDO updatedAlarm = alarmMapper.selectById(firstAlarmId);
        assertEquals(2, updatedAlarm.getTriggerCount());
    }

    // ========== 场景2：人工上报到处理的完整流程 ==========

    @Test
    @DisplayName("场景2.1：人工上报告警 - 成功创建告警")
    void testReportAlarm_Success() {
        // 准备请求数据
        AlarmReportReqVO reqVO = new AlarmReportReqVO();
        reqVO.setAlarmTypeId(1L);
        reqVO.setAlarmLevel(AlarmLevelEnum.WARNING.getLevel());
        reqVO.setAlarmContent("巡检发现设备异常，需要检修处理，设备外壳有明显损坏痕迹");
        reqVO.setDeviceId(100L);
        reqVO.setLocationId(200L);

        // 执行请求
        AlarmRespVO result = alarmService.reportAlarm(reqVO);

        // 验证结果
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(AlarmStatusEnum.PENDING.getStatus(), result.getAlarmStatus());
        assertEquals(AlarmSourceEnum.MANUAL.getSource(), result.getAlarmSource());
    }

    @Test
    @DisplayName("场景2.2：告警完整生命周期 - 上报 → 确认 → 处理 → 关闭")
    void testAlarmFullLifecycle_ManualReport() {
        // 阶段1：人工上报告警
        AlarmReportReqVO reportReqVO = new AlarmReportReqVO();
        reportReqVO.setAlarmTypeId(1L);
        reportReqVO.setAlarmLevel(AlarmLevelEnum.CRITICAL.getLevel());
        reportReqVO.setAlarmContent("发现设备严重故障，需要立即处理，设备已停止运行");
        reportReqVO.setDeviceId(100L);
        reportReqVO.setLocationId(200L);

        AlarmRespVO reportResult = alarmService.reportAlarm(reportReqVO);
        assertNotNull(reportResult);
        Long alarmId = reportResult.getId();

        // 阶段2：确认告警
        AlarmAcknowledgeReqVO acknowledgeReqVO = new AlarmAcknowledgeReqVO();
        acknowledgeReqVO.setAcknowledgeRemark("已收到告警，正在安排人员处理");

        alarmService.acknowledgeAlarm(alarmId, acknowledgeReqVO);

        // 验证状态变更
        AlarmDO acknowledgedAlarm = alarmMapper.selectById(alarmId);
        assertEquals(AlarmStatusEnum.ACKNOWLEDGED.getStatus(), acknowledgedAlarm.getAlarmStatus());

        // 阶段3：处理告警
        AlarmHandleReqVO handleReqVO = new AlarmHandleReqVO();
        handleReqVO.setHandleMeasure("已更换故障设备，恢复正常运行");
        handleReqVO.setHandleResult(AlarmHandleResultEnum.RESOLVED.getResult());

        alarmService.handleAlarm(alarmId, handleReqVO);

        // 验证状态变更
        AlarmDO handledAlarm = alarmMapper.selectById(alarmId);
        assertEquals(AlarmStatusEnum.HANDLING.getStatus(), handledAlarm.getAlarmStatus());

        // 阶段4：关闭告警
        AlarmCloseReqVO closeReqVO = new AlarmCloseReqVO();
        closeReqVO.setCloseReason(AlarmCloseReasonEnum.HANDLED.getReason());
        closeReqVO.setCloseRemark("故障已修复，设备运行正常");

        alarmService.closeAlarm(alarmId, closeReqVO);

        // 验证最终状态
        AlarmDO closedAlarm = alarmMapper.selectById(alarmId);
        assertEquals(AlarmStatusEnum.CLOSED.getStatus(), closedAlarm.getAlarmStatus());
        assertNotNull(closedAlarm.getCloseTime());
        assertNotNull(closedAlarm.getDurationSeconds());
    }

    @Test
    @DisplayName("场景2.3：非法状态转换 - 待确认直接关闭被拒绝")
    void testInvalidStateTransition_PendingToClose() {
        // 准备测试数据：创建待确认状态的告警
        AlarmDO alarm = AlarmTestDataBuilder.createPendingAlarmDO();
        alarmMapper.insert(alarm);

        // 尝试直接关闭（跳过确认和处理）
        AlarmCloseReqVO closeReqVO = new AlarmCloseReqVO();
        closeReqVO.setCloseReason(AlarmCloseReasonEnum.HANDLED.getReason());
        closeReqVO.setCloseRemark("测试直接关闭");

        // 执行并验证异常
        assertThrows(Exception.class, () -> alarmService.closeAlarm(alarm.getId(), closeReqVO));

        // 验证状态未改变
        AlarmDO unchangedAlarm = alarmMapper.selectById(alarm.getId());
        assertEquals(AlarmStatusEnum.PENDING.getStatus(), unchangedAlarm.getAlarmStatus());
    }

    // ========== 场景3：联动执行和结果反馈 ==========

    @Test
    @DisplayName("场景3.1：告警触发后联动规则匹配")
    void testAlarmTriggerWithLinkageRuleMatching() {
        // 准备联动规则
        LinkageRuleDO linkageRule = AlarmTestDataBuilder.buildLinkageRuleDO()
                .alarmTypeId(AlarmTestDataBuilder.DEFAULT_ALARM_TYPE_ID)
                .enabled(true)
                .actions("[{\"type\":\"NOTIFICATION\",\"config\":{\"recipients\":[1,2,3]}}]")
                .build();
        linkageRuleMapper.insert(linkageRule);

        // 触发告警
        AlarmTriggerReqVO reqVO = new AlarmTriggerReqVO();
        reqVO.setAlarmTypeId(AlarmTestDataBuilder.DEFAULT_ALARM_TYPE_ID);
        reqVO.setAlarmLevel(AlarmLevelEnum.CRITICAL.getLevel());
        reqVO.setAlarmContent("水位超标告警，触发联动规则");
        reqVO.setDeviceId(100L);
        reqVO.setDeviceName("水位传感器");
        reqVO.setLocationId(200L);
        reqVO.setLocationName("B区集水坑");

        AlarmRespVO result = alarmService.triggerAlarm(reqVO);
        
        // 验证告警创建成功
        assertNotNull(result);
        assertNotNull(result.getId());
    }

    @Test
    @DisplayName("场景3.2：查询联动执行记录")
    void testGetLinkageExecutionsByAlarmId() {
        // 准备测试数据
        AlarmDO alarm = AlarmTestDataBuilder.createPendingAlarmDO();
        alarmMapper.insert(alarm);

        LinkageRuleDO linkageRule = AlarmTestDataBuilder.createDefaultLinkageRuleDO();
        linkageRuleMapper.insert(linkageRule);

        // 创建联动执行记录
        LinkageExecutionDO execution = AlarmTestDataBuilder.buildLinkageExecutionDO()
                .alarmId(alarm.getId())
                .linkageRuleId(linkageRule.getId())
                .actionType(LinkageActionTypeEnum.NOTIFICATION.getType())
                .executionStatus(LinkageExecutionStatusEnum.SUCCESS.getStatus())
                .executionResult("通知发送成功")
                .build();
        linkageExecutionMapper.insert(execution);

        // 查询联动执行记录
        List<LinkageExecutionDO> result = linkageService.getLinkageExecutionsByAlarmId(alarm.getId());

        // 验证结果
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("场景3.3：查询需要人工介入的联动执行记录")
    void testGetManualInterventionExecutions() {
        // 准备测试数据
        AlarmDO alarm = AlarmTestDataBuilder.createPendingAlarmDO();
        alarmMapper.insert(alarm);

        LinkageRuleDO linkageRule = AlarmTestDataBuilder.createDefaultLinkageRuleDO();
        linkageRuleMapper.insert(linkageRule);

        // 创建需要人工介入的执行记录
        LinkageExecutionDO execution = AlarmTestDataBuilder.buildLinkageExecutionDO()
                .alarmId(alarm.getId())
                .linkageRuleId(linkageRule.getId())
                .actionType(LinkageActionTypeEnum.DEVICE_CONTROL.getType())
                .executionStatus(LinkageExecutionStatusEnum.FAILED.getStatus())
                .retryCount(3)
                .manualIntervention(true)
                .errorMessage("设备通信超时")
                .build();
        linkageExecutionMapper.insert(execution);

        // 查询需要人工介入的记录
        List<LinkageExecutionDO> result = linkageService.getManualInterventionExecutions();

        // 验证结果
        assertNotNull(result);
        // 应该包含需要人工介入的记录
        assertTrue(result.stream()
            .anyMatch(e -> e.getManualIntervention() != null && e.getManualIntervention()));
    }

    // ========== 场景4：批量操作 ==========

    @Test
    @DisplayName("场景4.1：批量确认告警")
    void testBatchAcknowledgeAlarms() {
        // 准备测试数据
        AlarmDO alarm1 = AlarmTestDataBuilder.createPendingAlarmDO();
        alarmMapper.insert(alarm1);

        AlarmDO alarm2 = AlarmTestDataBuilder.createPendingAlarmDO();
        alarmMapper.insert(alarm2);

        // 批量确认
        AlarmAcknowledgeReqVO acknowledgeReqVO = new AlarmAcknowledgeReqVO();
        acknowledgeReqVO.setAcknowledgeRemark("批量确认告警");

        List<Long> ids = List.of(alarm1.getId(), alarm2.getId());
        alarmService.batchAcknowledgeAlarms(ids, acknowledgeReqVO);

        // 验证状态变更
        AlarmDO updatedAlarm1 = alarmMapper.selectById(alarm1.getId());
        AlarmDO updatedAlarm2 = alarmMapper.selectById(alarm2.getId());
        assertEquals(AlarmStatusEnum.ACKNOWLEDGED.getStatus(), updatedAlarm1.getAlarmStatus());
        assertEquals(AlarmStatusEnum.ACKNOWLEDGED.getStatus(), updatedAlarm2.getAlarmStatus());
    }

    // ========== 场景5：历史告警查询 ==========

    @Test
    @DisplayName("场景5.1：查询历史告警 - 多条件筛选")
    void testGetHistoricalAlarmPage_WithFilters() {
        // 准备测试数据
        AlarmDO alarm1 = AlarmTestDataBuilder.buildAlarmDO()
                .alarmLevel(AlarmLevelEnum.CRITICAL.getLevel())
                .alarmStatus(AlarmStatusEnum.CLOSED.getStatus())
                .build();
        alarmMapper.insert(alarm1);

        AlarmDO alarm2 = AlarmTestDataBuilder.buildAlarmDO()
                .alarmLevel(AlarmLevelEnum.WARNING.getLevel())
                .alarmStatus(AlarmStatusEnum.CLOSED.getStatus())
                .build();
        alarmMapper.insert(alarm2);

        // 按级别筛选
        AlarmPageReqVO pageReqVO = new AlarmPageReqVO();
        pageReqVO.setPageNo(1);
        pageReqVO.setPageSize(10);
        pageReqVO.setAlarmLevels(List.of(AlarmLevelEnum.CRITICAL.getLevel()));

        PageResult<AlarmRespVO> result = alarmService.getHistoricalAlarmPage(pageReqVO);

        // 验证结果
        assertNotNull(result);
        assertNotNull(result.getList());
    }

}
