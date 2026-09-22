package cn.cheers.x.inspection.task.service.execution.steptree;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.task.model.task.InspectionContent;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

        when(patrolTaskEntityStore.require(1L)).thenReturn(new PatrolTaskDraft(
                1L, "巡检", "patrol", 44L, "ROBOT", content,
                null, null, null,
                Map.of("startStopId", "pad-a", "endStopId", "pad-b"),
                "draft", 1, "pad-a", "pad-b", null, null, null, null, null, null));

        EntityRespDTO itemEntity = new EntityRespDTO();
        itemEntity.setId(2354L);
        itemEntity.setBaseFields(Map.of(
                "name", "罐体无渗漏变形（原油储罐）",
                "step_tree_json", Map.of(
                        "version", 1,
                        "methods", List.of(Map.of(
                                "executionMeans", "ROBOT",
                                "actionTree", List.of(
                                        step("n1", "action-arrive", "到达指定位置",
                                                List.of("F-e49f76bdca3e4e1393e8e2f1cc0e7867")),
                                        step("n2", "act-robot-aim", "调整拍摄角度", List.of()),
                                        step("n3", "act-robot-shoot", "拍照", List.of("shot_count"))))))));
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
        stubAction(3L, "act-robot-self-check", "开机自检", List.of(), List.of(), 2);
        stubAction(4L, "act-robot-battery-check", "电量检查", List.of(), List.of(), 1);
        stubAction(2L, "act-robot-tail", "机器人任务尾", List.of("act-robot-return-charge"), List.of(), 0);
        stubAction(5L, "act-robot-return-charge", "返回充电", List.of(), List.of("location_ref"), 2);
        stubAction(41L, "action-arrive", "到达指定位置", List.of(),
                List.of("F-e49f76bdca3e4e1393e8e2f1cc0e7867"), 1);
        stubAction(11L, "act-robot-aim", "调整拍摄角度", List.of(),
                List.of("yaw", "pitch", "roll", "focal_length"), 1);
        stubAction(22L, "act-robot-shoot", "拍摄取证", List.of(), List.of("shot_count"), 1);
        when(entityRpcApi.getEntityByCode("pad-a", "point"))
                .thenReturn(CommonResult.success(point("起飞坪")));
        when(entityRpcApi.getEntityByCode("pad-b", "point"))
                .thenReturn(CommonResult.success(point("充电点")));

        service.generate(1L, List.of(10L), "pad-a", "pad-b");

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> tree = ArgumentCaptor.forClass(Map.class);
        verify(patrolTaskEntityStore).writeStepTree(eq(1L), tree.capture());
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> nodes = (List<Map<String, Object>>) tree.getValue().get("nodes");
        assertEquals("起点 · 起飞坪", titleOf(nodes, "stop-start"));
        assertEquals("开机自检", titleOf(nodes, "head-1"));
        assertEquals("电量检查", titleOf(nodes, "head-2"));
        assertEquals("北罐区1#储罐", titleOf(nodes, "stop-10"));
        assertEquals("equipment", hangTypeOf(nodes, "stop-10"));
        assertEquals("inspection_item", hangTypeOf(nodes, "item-2354-10"));
        assertEquals("罐体无渗漏变形（原油储罐）", titleOf(nodes, "item-2354-10"));
        assertEquals("到达指定位置", titleOf(nodes, "act-2354-1"));
        assertEquals("北罐区停靠点", paramsOf(nodes, "act-2354-1").get("F-e49f76bdca3e4e1393e8e2f1cc0e7867"));
        assertEquals("调整拍摄角度", titleOf(nodes, "act-2354-2"));
        assertEquals(30, paramsOf(nodes, "act-2354-2").get("yaw"));
        assertEquals(-15, paramsOf(nodes, "act-2354-2").get("pitch"));
        assertEquals(0, paramsOf(nodes, "act-2354-2").get("roll"));
        assertEquals(8, paramsOf(nodes, "act-2354-2").get("focal_length"));
        assertEquals("拍照", titleOf(nodes, "act-2354-3"));
        assertEquals(2, paramsOf(nodes, "act-2354-3").get("shot_count"));
        assertEquals(3, paramsOf(nodes, "stop-10").get("action_duration"));
        assertEquals(3, paramsOf(nodes, "item-2354-10").get("action_duration"));
        assertEquals(2, paramsOf(nodes, "head-1").get("action_duration"));
        assertEquals("北罐区停靠点", ((Map<?, ?>) tree.getValue().get("pointNames")).get("tank-north"));
        assertEquals("终点 · 充电点", titleOf(nodes, "stop-end"));
        assertEquals("返回充电", titleOf(nodes, "tail-1"));
        assertTrue(nodes.stream().noneMatch(node -> "机器人任务头".equals(node.get("title"))));
    }

    private void stubAction(
            long id, String code, String name, List<String> children, List<String> extraSlots, int durationMinutes
    ) {
        when(catalog.resolveActionId(code)).thenReturn(Optional.of(id));
        EntityRespDTO entity = new EntityRespDTO();
        entity.setId(id);
        entity.setBaseFields(Map.of("code", code, "name", name));
        List<Map<String, Object>> fields = new ArrayList<>();
        for (String slot : extraSlots) {
            fields.add(Map.of("fieldCode", slot));
        }
        fields.add(Map.of("fieldCode", "action_duration", "defaultValue", durationMinutes));
        entity.setCustomFields(Map.of(
                "is_composite", !children.isEmpty(),
                "child_action_ids_json", children,
                "param_slots_json", Map.of("version", 1, "fields", fields)));
        when(entityRpcApi.getEntity(id, "action")).thenReturn(CommonResult.success(entity));
    }

    private static Map<String, Object> step(String nodeKey, String actionId, String title, List<String> slots) {
        return Map.of(
                "nodeKey", nodeKey,
                "actionId", actionId,
                "stepTitle", title,
                "nodeType", "STEP",
                "paramSlots", slots);
    }

    private static EntityRespDTO point(String name) {
        EntityRespDTO entity = new EntityRespDTO();
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
