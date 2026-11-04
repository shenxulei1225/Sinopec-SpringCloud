package cn.cheers.x.module.dynamicbusiness.service.alarmlinkageverify;

import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.alarmlinkageverify.vo.AlarmLinkageVerifyDeviceRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.alarmlinkageverify.vo.AlarmLinkageVerifySnapshotRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.EntityRepository;
import cn.cheers.x.module.dynamicbusiness.dataprocess.AlarmCatalogEnsure;
import cn.cheers.x.module.dynamicbusiness.dataprocess.PointRuleSource;
import cn.cheers.x.module.dynamicbusiness.devicehistory.DeviceCollectionPoint;
import cn.cheers.x.module.dynamicbusiness.devicehistory.DeviceCollectionPointQuery;
import cn.cheers.x.module.dynamicbusiness.devicehistory.GatewaySessionWrites;
import cn.cheers.x.module.dynamicbusiness.service.action.execution.ActionExecutionDO;
import cn.cheers.x.module.dynamicbusiness.service.action.execution.ActionExecutionMapper;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityRpcFacadeService;
import cn.cheers.x.module.dynamicbusiness.service.linkage.LinkageExecutionDO;
import cn.cheers.x.module.dynamicbusiness.service.linkage.LinkageExecutionItemDO;
import cn.cheers.x.module.dynamicbusiness.service.event.PlatformEventOutboxDO;
import cn.cheers.x.module.dynamicbusiness.service.event.PlatformEventOutboxMapper;
import cn.cheers.x.module.dynamicbusiness.service.linkage.LinkageExecutionItemMapper;
import cn.cheers.x.module.dynamicbusiness.service.linkage.LinkageExecutionMapper;
import cn.cheers.x.module.dynamicbusiness.service.strategy.PointProcessingRule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlarmLinkageVerifyQueryServiceTest {

    @Mock
    private DeviceCollectionPointQuery collectionPoints;
    @Mock
    private PointRuleSource pointRules;
    @Mock
    private EntityRpcFacadeService entityRpcFacadeService;
    @Mock
    private EntityRepository entityRepository;
    @Mock
    private AlarmCatalogEnsure alarmCatalogEnsure;
    @Mock
    private LinkageExecutionMapper linkageExecutionMapper;
    @Mock
    private LinkageExecutionItemMapper linkageExecutionItemMapper;
    @Mock
    private ActionExecutionMapper actionExecutionMapper;
    @Mock
    private GatewaySessionWrites gatewaySession;
    @Mock
    private PlatformEventOutboxMapper eventOutbox;
    @InjectMocks
    private AlarmLinkageVerifyQueryService service;

    @BeforeEach
    void setTenant() {
        TenantContextHolder.setTenantId(123L);
    }

    @AfterEach
    void clearTenant() {
        TenantContextHolder.clear();
    }

    @Test
    void devicesListsCollectionEquipmentByName() {
        when(collectionPoints.listDevices(123L)).thenReturn(List.of(
                new DeviceCollectionPointQuery.CollectionDevice(13644L, "1号二氧化碳检测仪", "二氧化碳检测仪"),
                new DeviceCollectionPointQuery.CollectionDevice(13645L, "1号氧气检测仪", "氧气检测仪")));

        List<AlarmLinkageVerifyDeviceRespVO> devices = service.devices();

        assertEquals(2, devices.size());
        assertEquals(13644L, devices.get(0).getDeviceId());
        assertEquals("1号二氧化碳检测仪", devices.get(0).getDeviceName());
        assertEquals("二氧化碳检测仪", devices.get(0).getModelName());
        assertEquals("1号氧气检测仪", devices.get(1).getDeviceName());
    }

    @Test
    void snapshotKeepsThisDeviceAlarmAndLinkage() {
        when(collectionPoints.list(123L, 13644L)).thenReturn(List.of(
                new DeviceCollectionPoint(13644L, "F-co2", "二氧化碳", "PPM")));
        EntityDO equipment = new EntityDO();
        equipment.setName("二氧化碳检测仪");
        when(entityRepository.findById(13644L, "equipment")).thenReturn(equipment);
        stubField("告警设备", "device_code");
        stubField("处理状态", "FLD-ALM-006");
        stubField("告警内容", "content_code");
        stubField("触发值", "trigger_code");
        stubField("告警阈值", "threshold_code");
        stubField("告警级别", "level_code");
        stubField("告警点位", "point_code");
        stubField("原因", "cause_code");
        stubField("后果", "consequence_code");
        stubField("复核状态", "review_code");
        stubField("处理脉络", "trace_code");
        when(entityRpcFacadeService.list("alarm", null)).thenReturn(List.of(
                alarm(14L, "13644", "未处理", LocalDateTime.of(2026, 11, 5, 1, 0)),
                alarm(13L, "999", "未处理", LocalDateTime.of(2026, 11, 5, 2, 0))));
        LinkageExecutionDO execution = new LinkageExecutionDO();
        execution.setId(60L);
        execution.setLinkageRuleId(263L);
        execution.setEventTopicId(33L);
        execution.setStatus("FAILED");
        execution.setCreateTime(LocalDateTime.of(2026, 11, 5, 1, 8));
        when(linkageExecutionMapper.selectRecentBySource(eq("equipment"), eq(13644L), anyInt()))
                .thenReturn(List.of(execution));
        EntityDO linkage = new EntityDO();
        linkage.setName("气体或温度告警打开风机");
        when(entityRepository.findById(263L, "linkage")).thenReturn(linkage);
        EntityDO topic = new EntityDO();
        topic.setName("二氧化碳浓度过高");
        when(entityRepository.findById(33L, "event_topic")).thenReturn(topic);
        LinkageExecutionItemDO item = new LinkageExecutionItemDO();
        item.setPairIndex(0L);
        item.setActionExecutionId(70L);
        item.setStatus("FAILED");
        when(linkageExecutionItemMapper.selectByLinkageExecutionId(60L)).thenReturn(List.of(item));
        ActionExecutionDO action = new ActionExecutionDO();
        action.setId(70L);
        action.setActionId(5L);
        action.setTargetEntityId(25629L);
        action.setTargetEntityTypeCode("equipment");
        action.setStatus("FAILED");
        action.setErrorMessage("OPC UA 服务器不在线，指令发送失败");
        when(actionExecutionMapper.selectById(70L)).thenReturn(action);
        EntityDO actionEntity = new EntityDO();
        actionEntity.setName("打开风机");
        when(entityRepository.findById(5L, "action")).thenReturn(actionEntity);
        EntityDO fan = new EntityDO();
        fan.setName("电力信息舱风机");
        when(entityRepository.findById(25629L, "equipment")).thenReturn(fan);

        when(pointRules.deviceModels()).thenReturn(Map.of(13644L, 2535L));
        when(pointRules.boundDevices()).thenReturn(Map.of());
        when(pointRules.load()).thenReturn(List.of(
                new PointProcessingRule(9L, "F-co2", PointProcessingRule.THRESHOLD, "GT", 1000d,
                        1L, "异常告警", null, null, 2535L, 1000d, 9L),
                new PointProcessingRule(99L, "F-co2", PointProcessingRule.THRESHOLD, "GT", 9999d,
                        1L, "异常告警", null, null, 2543L, 9999d, 99L)));
        EntityDO rule = new EntityDO();
        rule.setName("二氧化碳浓度过高");
        when(entityRepository.findById(9L, "alarm_rule")).thenReturn(rule);
        when(gatewaySession.attached()).thenReturn(false);
        PlatformEventOutboxDO cleared = new PlatformEventOutboxDO();
        cleared.setId(15L);
        cleared.setEventTopicId(34L);
        cleared.setEventCode("alarm-co2-high-cleared");
        cleared.setPhase("CLEARED");
        cleared.setStatus("PROCESSED");
        cleared.setOccurredAt(LocalDateTime.of(2026, 11, 5, 1, 8));
        cleared.setPayload(Map.of(
                "alarmRecordId", 14L,
                "content", "二氧化碳 读数越过 大于 1000.0",
                "triggerValue", "1300.0",
                "thresholdValue", "1000.0",
                "pointCode", "F-co2",
                "device", Map.of("id", 13644L, "name", "二氧化碳检测仪")));
        when(eventOutbox.selectRecentAlarmEvents(eq("equipment"), eq(13644L), anyInt()))
                .thenReturn(List.of(cleared));
        EntityDO clearedTopic = new EntityDO();
        clearedTopic.setName("二氧化碳浓度过高恢复正常");
        when(entityRepository.findById(34L, "event_topic")).thenReturn(clearedTopic);

        AlarmLinkageVerifySnapshotRespVO snapshot = service.snapshot(13644L);

        assertEquals("二氧化碳检测仪", snapshot.getDeviceName());
        assertEquals(1, snapshot.getPoints().size());
        assertEquals("F-co2", snapshot.getPoints().get(0).getPointCode());
        assertEquals(1, snapshot.getPoints().get(0).getThresholds().size());
        assertEquals("二氧化碳浓度过高", snapshot.getPoints().get(0).getThresholds().get(0).getRuleName());
        assertEquals("大于", snapshot.getPoints().get(0).getThresholds().get(0).getCompareLabel());
        assertEquals(1000d, snapshot.getPoints().get(0).getThresholds().get(0).getThreshold());
        assertEquals(1000d, snapshot.getPoints().get(0).getThresholds().get(0).getRecovery());
        assertEquals(1, snapshot.getAlarms().size());
        assertEquals(14L, snapshot.getAlarms().get(0).getId());
        assertEquals("已消除", snapshot.getAlarms().get(0).getHandling());
        assertEquals("重要", snapshot.getAlarms().get(0).getLevel());
        assertEquals("关闭。状态改为已处理。", snapshot.getAlarms().get(0).getTrace());
        assertEquals("二氧化碳浓度过高恢复正常", snapshot.getEvents().get(0).getEventTopicName());
        assertEquals("恢复正常", snapshot.getEvents().get(0).getPhaseLabel());
        assertEquals("1300.0", snapshot.getEvents().get(0).getTriggerValue());
        assertEquals("二氧化碳检测仪", snapshot.getEvents().get(0).getSourceDeviceName());
        assertEquals("气体或温度告警打开风机", snapshot.getLinkages().get(0).getLinkageName());
        assertEquals("二氧化碳浓度过高", snapshot.getLinkages().get(0).getEventTopicName());
        assertEquals("打开风机", snapshot.getLinkages().get(0).getItems().get(0).getActionName());
        assertEquals("电力信息舱风机", snapshot.getLinkages().get(0).getItems().get(0).getTargetName());
        assertEquals(Boolean.FALSE, snapshot.getOpcUaServerOnline());
    }

    private void stubField(String name, String code) {
        when(alarmCatalogEnsure.fieldCode(name)).thenReturn(code);
    }

    private static EntityRespDTO alarm(long id, String deviceId, String handling, LocalDateTime updateTime) {
        EntityRespDTO entity = new EntityRespDTO();
        entity.setId(id);
        entity.setUpdateTime(updateTime);
        entity.setBaseFields(Map.of("name", "告警" + id));
        entity.setCustomFields(Map.ofEntries(
                Map.entry("device_code", deviceId),
                Map.entry("FLD-ALM-006", handling),
                Map.entry("content_code", "越限"),
                Map.entry("trigger_code", "1200"),
                Map.entry("threshold_code", "1000"),
                Map.entry("level_code", "重要"),
                Map.entry("point_code", "F-co2"),
                Map.entry("cause_code", "浓度高于1000"),
                Map.entry("consequence_code", "窒息风险"),
                Map.entry("review_code", "未复核"),
                Map.entry("trace_code", "关闭。状态改为已处理。")));
        return entity;
    }
}
