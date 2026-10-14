package cn.cheers.x.inspection.task.service.execution.steptree;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.task.model.task.ExecutionDeviceBinding;
import cn.cheers.x.inspection.task.model.task.InspectionContent;
import cn.cheers.x.device.protocolgateway.api.protocol.ProtocolDispatchProfile;
import cn.cheers.x.inspection.task.service.task.PatrolTaskDraft;
import cn.cheers.x.inspection.task.service.task.PatrolTaskEntityStore;
import cn.cheers.x.module.dynamicbusiness.api.entity.EntityRpcApi;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskStepTreeGenerateServiceTest {

    @Mock
    private PatrolTaskEntityStore patrolTaskEntityStore;
    @Mock
    private PatrolTaskStepTreeReader catalog;
    @Mock
    private EntityRpcApi entityRpcApi;
    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();
    @Spy
    private ActionGroupMemberParser actionGroupMemberParser =
            new ActionGroupMemberParser(objectMapper);
    @InjectMocks
    private TaskStepTreeGenerateService service;

    @Test
    void generate_walksObjectThenNamedItemThenNamedActions() {
        InspectionContent.ItemContent item = new InspectionContent.ItemContent();
        item.setItemId(2354L);
        item.setItemName("罐体无渗漏变形（原油储罐）");
        InspectionContent.ObjectContent object = new InspectionContent.ObjectContent();
        object.setObjectId(10L);
        object.setObjectName("北罐区1#储罐");
        object.setItems(List.of(item));
        InspectionContent content = new InspectionContent();
        content.setCustomObjects(List.of(object));

        ExecutionDeviceBinding robotBinding = new ExecutionDeviceBinding();
        robotBinding.setProtocolCode("robot-ws");
        when(patrolTaskEntityStore.require(1L)).thenReturn(new PatrolTaskDraft(
                1L, "巡检", "patrol", 44L, "ROBOT", content,
                robotBinding, null, null,
                Map.of("startStopId", "pad-a", "endStopId", "pad-b"),
                "draft", 1, "pad-a", "pad-b", null, null, null, null, null, null, null, null));

        EntityRespDTO itemEntity = new EntityRespDTO();
        itemEntity.setId(2354L);
        itemEntity.setBaseFields(Map.of(
                "name", "罐体无渗漏变形（原油储罐）",
                "step_tree_json", Map.of(
                        "version", 1,
                        "methods", List.of(Map.of(
                                "executionMeans", "ROBOT",
                                "actionTree", List.of(
                                        step("n1", "action-arrive",
                                                List.of("F-e49f76bdca3e4e1393e8e2f1cc0e7867")),
                                        step("n2", "act-robot-aim", List.of()),
                                        step("n3", "act-robot-shoot", List.of("shot_count"))))))));
        when(entityRpcApi.getEntity(2354L, "inspection_item"))
                .thenReturn(CommonResult.success(itemEntity));
        when(catalog.loadHostPackOrEmpty(10L)).thenReturn(HostSopParamPack.parse(
                Map.of(
                        "version", 1,
                        "entries", List.of(
                                Map.of(
                                        "subjectId", 2354,
                                        "dimensionValue", "UAV",
                                        "paramsByNode", Map.of()),
                                Map.of(
                                        "subjectId", 2354,
                                        "dimensionValue", "ROBOT",
                                        "paramsByNode", Map.of(
                                                "n1", Map.of(
                                                        "F-e49f76bdca3e4e1393e8e2f1cc0e7867",
                                                        Map.of("id", 99, "code", "tank-north", "name", "北罐区停靠点")),
                                                "n2", Map.of(
                                                        "yaw", 30,
                                                        "pitch", -15,
                                                        "roll", 0,
                                                        "focal_length", 8),
                                                "n3", Map.of("shot_count", 2))))),
                objectMapper));
        stubAction(1L, "act-robot-head", "机器人任务头", List.of("act-robot-self-check", "act-robot-battery-check"), List.of(), 0);
        stubAction(3L, "act-robot-self-check", "开机自检", List.of(), List.of(), 120);
        stubAction(4L, "act-robot-battery-check", "电量检查", List.of(), List.of(), 60);
        stubAction(201L, ProtocolDispatchProfile.ACTION_TASK_RECORD_START, "开始录像", List.of(), List.of(), 0);
        stubAction(202L, ProtocolDispatchProfile.ACTION_TASK_RECORD_STOP, "结束录像", List.of(), List.of(), 0);
        stubAction(41L, "action-arrive", "到达指定位置", List.of(),
                List.of("F-e49f76bdca3e4e1393e8e2f1cc0e7867"), 60);
        stubAction(11L, "act-robot-aim", "调整拍摄角度", List.of(),
                List.of("yaw", "pitch", "roll", "focal_length"), 60);
        stubAction(22L, "act-robot-shoot", "拍摄取证", List.of(), List.of("shot_count"), 60);
        when(entityRpcApi.getEntityByCode("PN-44-pad-a", "point"))
                .thenReturn(CommonResult.success(point(501L, "起飞坪")));
        when(entityRpcApi.getEntityByCode("PN-44-pad-b", "point"))
                .thenReturn(CommonResult.success(point(502L, "充电点")));
        when(entityRpcApi.getEntityByCode("PN-44-tank-north", "point"))
                .thenReturn(CommonResult.success(point(99L, "北罐区停靠点")));

        service.generate(1L, List.of(10L), "pad-a", "pad-b");

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> tree = ArgumentCaptor.forClass(Map.class);
        verify(patrolTaskEntityStore).writeStepTree(eq(1L), tree.capture());
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> nodes = (List<Map<String, Object>>) tree.getValue().get("nodes");
        assertEquals("起点 · 起飞坪", titleOf(nodes, "stop-start"));
        assertEquals(501, ((Number) nodeOf(nodes, "stop-start").get("refId")).intValue());
        assertEquals("起飞坪", nodeOf(nodes, "stop-start").get("name"));
        assertEquals("开机自检", titleOf(nodes, "head-1"));
        assertEquals("电量检查", titleOf(nodes, "head-2"));
        assertEquals("开始录像", titleOf(nodes, "head-3"));
        assertEquals("结束录像", titleOf(nodes, "tail-1"));
        assertEquals("北罐区停靠点", titleOf(nodes, "point-tank-north"));
        assertEquals("point", hangTypeOf(nodes, "point-tank-north"));
        assertEquals("point-tank-north", nodeOf(nodes, "act-2354-10-1").get("parentNodeKey"));
        assertEquals("point-tank-north", nodeOf(nodes, "item-2354-10").get("parentNodeKey"));
        assertEquals("item-2354-10", nodeOf(nodes, "act-2354-10-2").get("parentNodeKey"));
        assertEquals("inspection_item", hangTypeOf(nodes, "item-2354-10"));
        assertEquals("罐体无渗漏变形（原油储罐）", titleOf(nodes, "item-2354-10"));
        assertEquals("到达指定位置", titleOf(nodes, "act-2354-10-1"));
        Map<String, Object> location = paramsOf(nodes, "act-2354-10-1");
        @SuppressWarnings("unchecked")
        Map<String, Object> place = (Map<String, Object>) location.get("F-e49f76bdca3e4e1393e8e2f1cc0e7867");
        assertEquals(99, ((Number) place.get("id")).intValue());
        assertEquals("tank-north", place.get("code"));
        assertEquals("北罐区停靠点", place.get("name"));
        assertEquals("调整拍摄角度", titleOf(nodes, "act-2354-10-2"));
        assertEquals(30, paramsOf(nodes, "act-2354-10-2").get("yaw"));
        assertEquals(-15, paramsOf(nodes, "act-2354-10-2").get("pitch"));
        assertEquals(0, paramsOf(nodes, "act-2354-10-2").get("roll"));
        assertEquals(8, paramsOf(nodes, "act-2354-10-2").get("focal_length"));
        assertEquals("拍摄取证", titleOf(nodes, "act-2354-10-3"));
        assertEquals(2, paramsOf(nodes, "act-2354-10-3").get("shot_count"));
        assertEquals(new BigDecimal("180"), paramsOf(nodes, "point-tank-north").get("action_duration"));
        assertEquals(new BigDecimal("120"), paramsOf(nodes, "item-2354-10").get("action_duration"));
        assertEquals(new BigDecimal("120"), paramsOf(nodes, "head-1").get("action_duration"));
        assertEquals("北罐区停靠点", ((Map<?, ?>) tree.getValue().get("pointNames")).get("tank-north"));
        assertEquals("终点 · 充电点", titleOf(nodes, "stop-end"));
        assertEquals(502, ((Number) nodeOf(nodes, "stop-end").get("refId")).intValue());
        assertTrue(nodes.stream().noneMatch(node -> "返回充电".equals(node.get("title"))));
        assertTrue(nodes.stream().noneMatch(node -> "机器人任务头".equals(node.get("title"))));
    }

    @Test
    void generate_djiPilot_opensWithBatteryThenWayline() {
        when(patrolTaskEntityStore.require(1L)).thenReturn(uavDraft("dji-pilot-1.16.1", new BigDecimal("120"), 60));
        stubEmptyItem();
        stubAction(64L, ProtocolDispatchProfile.ACTION_UAV_BATTERY_CHECK, "电量检查", List.of(), List.of(), 0);
        stubAction(190L, ProtocolDispatchProfile.ACTION_UAV_WAYLINE_PREPARE, "准备并启动航线", List.of(),
                List.of("security_takeoff_height", "max_speed", "rth_altitude", "finish_action"), 0);
        stubAction(201L, ProtocolDispatchProfile.ACTION_TASK_RECORD_START, "开始录像", List.of(), List.of(), 0);
        stubAction(202L, ProtocolDispatchProfile.ACTION_TASK_RECORD_STOP, "结束录像", List.of(), List.of(), 0);
        when(entityRpcApi.getEntityByCode("PN-44-pad-a", "point"))
                .thenReturn(CommonResult.success(point(501L, "起飞坪")));
        when(entityRpcApi.getEntityByCode("PN-44-pad-b", "point"))
                .thenReturn(CommonResult.success(point(502L, "充电点")));

        service.generate(1L, List.of(10L), "pad-a", "pad-b");

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> tree = ArgumentCaptor.forClass(Map.class);
        verify(patrolTaskEntityStore).writeStepTree(eq(1L), tree.capture());
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> nodes = (List<Map<String, Object>>) tree.getValue().get("nodes");
        assertEquals("电量检查", titleOf(nodes, "head-1"));
        assertEquals(new BigDecimal("180"), paramsOf(nodes, "head-1").get("required_duration"));
        assertEquals("开始录像", titleOf(nodes, "head-2"));
        assertEquals("准备并启动航线", titleOf(nodes, "head-3"));
        assertEquals("结束录像", titleOf(nodes, "tail-1"));
        assertEquals("20", paramsOf(nodes, "head-3").get("security_takeoff_height"));
        assertEquals("goHome", paramsOf(nodes, "head-3").get("finish_action"));
        assertTrue(nodes.stream().noneMatch(node -> "act-uav-head".equals(node.get("refCode"))));
    }

    @Test
    void generate_donggu_opensWithBatteryOnly() {
        when(patrolTaskEntityStore.require(1L)).thenReturn(uavDraft("uav-ws", new BigDecimal("120"), 60));
        stubEmptyItem();
        stubAction(64L, ProtocolDispatchProfile.ACTION_UAV_BATTERY_CHECK, "电量检查", List.of(), List.of(), 0);
        stubAction(201L, ProtocolDispatchProfile.ACTION_TASK_RECORD_START, "开始录像", List.of(), List.of(), 0);
        stubAction(202L, ProtocolDispatchProfile.ACTION_TASK_RECORD_STOP, "结束录像", List.of(), List.of(), 0);
        when(entityRpcApi.getEntityByCode("PN-44-pad-a", "point"))
                .thenReturn(CommonResult.success(point(501L, "起飞坪")));
        when(entityRpcApi.getEntityByCode("PN-44-pad-b", "point"))
                .thenReturn(CommonResult.success(point(502L, "充电点")));

        service.generate(1L, List.of(10L), "pad-a", "pad-b");

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> tree = ArgumentCaptor.forClass(Map.class);
        verify(patrolTaskEntityStore).writeStepTree(eq(1L), tree.capture());
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> nodes = (List<Map<String, Object>>) tree.getValue().get("nodes");
        assertEquals("电量检查", titleOf(nodes, "head-1"));
        assertEquals(new BigDecimal("180"), paramsOf(nodes, "head-1").get("required_duration"));
        assertEquals("开始录像", titleOf(nodes, "head-2"));
        assertEquals("结束录像", titleOf(nodes, "tail-1"));
        assertTrue(nodes.stream().noneMatch(node -> "准备并启动航线".equals(node.get("title"))));
    }

    @Test
    void generate_skipsRequiredDurationWhenTravelSecondsMissing() {
        when(patrolTaskEntityStore.require(1L)).thenReturn(uavDraft("dji-dock-1.16.1", new BigDecimal("120"), null));
        stubEmptyItem();
        stubAction(64L, ProtocolDispatchProfile.ACTION_UAV_BATTERY_CHECK, "电量检查", List.of(), List.of(), 0);
        stubAction(190L, ProtocolDispatchProfile.ACTION_UAV_WAYLINE_PREPARE, "准备并启动航线", List.of(), List.of(), 0);
        stubAction(201L, ProtocolDispatchProfile.ACTION_TASK_RECORD_START, "开始录像", List.of(), List.of(), 0);
        stubAction(202L, ProtocolDispatchProfile.ACTION_TASK_RECORD_STOP, "结束录像", List.of(), List.of(), 0);
        when(entityRpcApi.getEntityByCode("PN-44-pad-a", "point"))
                .thenReturn(CommonResult.success(point(501L, "起飞坪")));
        when(entityRpcApi.getEntityByCode("PN-44-pad-b", "point"))
                .thenReturn(CommonResult.success(point(502L, "充电点")));

        service.generate(1L, List.of(10L), "pad-a", "pad-b");

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> tree = ArgumentCaptor.forClass(Map.class);
        verify(patrolTaskEntityStore).writeStepTree(eq(1L), tree.capture());
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> nodes = (List<Map<String, Object>>) tree.getValue().get("nodes");
        assertFalse(paramsOf(nodes, "head-1").containsKey("required_duration"));
    }

    @Test
    void generate_rejectsUavWithoutProtocol() {
        when(patrolTaskEntityStore.require(1L)).thenReturn(uavDraft(null, new BigDecimal("120"), 60));
        Exception ex = assertThrows(Exception.class, () -> service.generate(1L, List.of(10L), "pad-a", "pad-b"));
        assertTrue(ex.getMessage().contains("对接协议"));
    }

    @Test
    void compose_rejectsActionWithoutDomain() {
        stubCameraItem("act-missing-domain");
        stubActionEntity(81L, "act-missing-domain", "无归属动作", Map.of());

        Exception ex = assertThrows(Exception.class,
                () -> service.compose(cameraDraft(), List.of(10L), null, null));

        assertTrue(ex.getMessage().contains("actionDomain"));
    }

    @Test
    void compose_rejectsAnalysisActionWithoutCapability() {
        stubCameraItem("act-analysis");
        stubActionEntity(82L, "act-analysis", "智能分析", Map.of(
                "action_domain", "INTELLIGENT_ANALYSIS"));

        Exception ex = assertThrows(Exception.class,
                () -> service.compose(cameraDraft(), List.of(10L), null, null));

        assertTrue(ex.getMessage().contains("analysisCapabilityId"));
    }

    @Test
    void compose_deviceActionDoesNotWriteProtocolRole() {
        stubCameraItem("act-device");
        stubActionEntity(83L, "act-device", "设备动作", Map.of("action_domain", "DEVICE"));

        Map<String, Object> tree = service.compose(cameraDraft(), List.of(10L), null, null);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> nodes = (List<Map<String, Object>>) tree.get("nodes");
        assertFalse(nodeOf(nodes, "act-2354-10-1").containsKey("protocolDispatchRole"));
    }

    @Test
    void compose_snapshotsAnalysisCapability() {
        stubCameraItem("act-analysis");
        stubActionEntity(84L, "act-analysis", "跑冒滴漏分析", Map.of(
                "action_domain", "INTELLIGENT_ANALYSIS",
                "analysis_capability_id", 41L,
                "protocol_dispatch_role", Map.of("camera-http", "PLATFORM_CHECK")));

        Map<String, Object> tree = service.compose(cameraDraft(), List.of(10L), null, null);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> nodes = (List<Map<String, Object>>) tree.get("nodes");
        Map<String, Object> action = nodeOf(nodes, "act-2354-10-1");
        assertEquals("INTELLIGENT_ANALYSIS", action.get("actionDomain"));
        assertEquals(41L, action.get("analysisCapabilityId"));
        assertFalse(action.containsKey("protocolDispatchRole"));
    }

    private PatrolTaskDraft uavDraft(String protocol, BigDecimal actionSeconds, Integer travelSeconds) {
        InspectionContent.ItemContent item = new InspectionContent.ItemContent();
        item.setItemId(2354L);
        item.setItemName("罐体无渗漏变形（原油储罐）");
        InspectionContent.ObjectContent object = new InspectionContent.ObjectContent();
        object.setObjectId(10L);
        object.setObjectName("北罐区1#储罐");
        object.setItems(List.of(item));
        InspectionContent content = new InspectionContent();
        content.setCustomObjects(List.of(object));
        content.setItemActionDurationMinutes(actionSeconds);
        ExecutionDeviceBinding binding = new ExecutionDeviceBinding();
        binding.setProtocolCode(protocol);
        Map<String, Object> route = new java.util.LinkedHashMap<>();
        route.put("startStopId", "pad-a");
        route.put("endStopId", "pad-b");
        route.put("securityTakeoffHeight", "20");
        route.put("flightSpeed", "5");
        route.put("rthAltitude", "20");
        route.put("waylineFinish", "goHome");
        if (travelSeconds != null) {
            route.put("estimatedTravelDuration", travelSeconds);
        }
        return new PatrolTaskDraft(
                1L, "巡检", "patrol", 44L, "UAV", content,
                binding, null, null, route,
                "draft", 1, "pad-a", "pad-b", null, null, null, null, null, null, null, null);
    }

    private PatrolTaskDraft cameraDraft() {
        InspectionContent.ItemContent item = new InspectionContent.ItemContent();
        item.setItemId(2354L);
        item.setItemName("跑冒滴漏检查");
        InspectionContent.ObjectContent object = new InspectionContent.ObjectContent();
        object.setObjectId(10L);
        object.setObjectName("北罐区1#储罐");
        object.setItems(List.of(item));
        InspectionContent content = new InspectionContent();
        content.setCustomObjects(List.of(object));
        ExecutionDeviceBinding binding = new ExecutionDeviceBinding();
        binding.setProtocolCode("camera-http");
        return new PatrolTaskDraft(
                1L, "巡检", "patrol", 44L, "FIXED_CAMERA", content,
                binding, null, null, Map.of(),
                "draft", 1, null, null, null, null, null, null, null, null, null, null);
    }

    private void stubCameraItem(String actionCode) {
        EntityRespDTO itemEntity = new EntityRespDTO();
        itemEntity.setId(2354L);
        itemEntity.setBaseFields(Map.of(
                "name", "跑冒滴漏检查",
                "step_tree_json", Map.of(
                        "version", 1,
                        "methods", List.of(Map.of(
                                "methodKey", "FIXED_CAMERA",
                                "stepTree", List.of(step("n1", actionCode, List.of())))))));
        when(entityRpcApi.getEntity(2354L, "inspection_item"))
                .thenReturn(CommonResult.success(itemEntity));
        when(catalog.loadHostPackOrEmpty(10L)).thenReturn(HostSopParamPack.empty());
    }

    private void stubActionEntity(long id, String code, String name, Map<String, Object> customFields) {
        when(catalog.resolveActionId(code)).thenReturn(Optional.of(id));
        EntityRespDTO entity = new EntityRespDTO();
        entity.setId(id);
        entity.setBaseFields(Map.of("code", code, "name", name));
        entity.setCustomFields(customFields);
        when(entityRpcApi.getEntityByCode(code, "action")).thenReturn(CommonResult.success(entity));
    }

    private void stubEmptyItem() {
        EntityRespDTO itemEntity = new EntityRespDTO();
        itemEntity.setId(2354L);
        itemEntity.setBaseFields(Map.of(
                "name", "罐体无渗漏变形（原油储罐）",
                "step_tree_json", Map.of(
                        "version", 1,
                        "methods", List.of(Map.of(
                                "executionMeans", "UAV",
                                "actionTree", List.of(step("n1", "act-sample", List.of()))
                        ))
                )
        ));
        when(entityRpcApi.getEntity(2354L, "inspection_item"))
                .thenReturn(CommonResult.success(itemEntity));
        when(catalog.loadHostPackOrEmpty(10L)).thenReturn(HostSopParamPack.parse(Map.of(), objectMapper));
        stubAction(11L, "act-sample", "示例动作", List.of(), List.of(), 0);
    }

    private void stubAction(
            long id, String code, String name, List<String> children, List<String> extraSlots, int durationSeconds
    ) {
        when(catalog.resolveActionId(code)).thenReturn(Optional.of(id));
        EntityRespDTO entity = new EntityRespDTO();
        entity.setId(id);
        entity.setBaseFields(Map.of("code", code, "name", name));
        List<Map<String, Object>> fields = new ArrayList<>();
        for (String slot : extraSlots) {
            fields.add(Map.of("fieldCode", slot));
        }
        fields.add(Map.of("fieldCode", "action_duration", "defaultValue", durationSeconds));
        Map<String, Object> custom = new LinkedHashMap<>();
        custom.put("param_slots_json", Map.of("version", 1, "fields", fields));
        custom.put("action_domain", "DEVICE");
        if (!children.isEmpty()) {
            custom.put("action_tree_json", Map.of(
                    "version", 1,
                    "members", java.util.stream.IntStream.range(0, children.size())
                            .mapToObj(index -> Map.of(
                                    "memberKey", code + ":" + index,
                                    "actionCode", children.get(index),
                                    "order", index,
                                    "params", Map.of(),
                                    "required", true))
                            .toList()));
        }
        entity.setCustomFields(custom);
        when(entityRpcApi.getEntityByCode(code, "action")).thenReturn(CommonResult.success(entity));
    }

    private static Map<String, Object> step(String nodeKey, String actionId, List<String> slots) {
        return Map.of(
                "nodeKey", nodeKey,
                "actionId", actionId,
                "nodeType", "STEP",
                "paramSlots", slots);
    }

    private static EntityRespDTO point(long id, String name) {
        EntityRespDTO entity = new EntityRespDTO();
        entity.setId(id);
        entity.setBaseFields(Map.of("name", name));
        return entity;
    }

    private static String titleOf(List<Map<String, Object>> nodes, String nodeKey) {
        return String.valueOf(nodeOf(nodes, nodeKey).get("title"));
    }

    private static String hangTypeOf(List<Map<String, Object>> nodes, String nodeKey) {
        return String.valueOf(nodeOf(nodes, nodeKey).get("hangTypeCode"));
    }

    private static Map<String, Object> nodeOf(List<Map<String, Object>> nodes, String nodeKey) {
        return nodes.stream()
                .filter(node -> nodeKey.equals(node.get("nodeKey")))
                .findFirst()
                .orElseThrow();
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> paramsOf(List<Map<String, Object>> nodes, String nodeKey) {
        Object raw = nodeOf(nodes, nodeKey).get("params");
        return raw instanceof Map<?, ?> map ? (Map<String, Object>) map : Map.of();
    }
}
