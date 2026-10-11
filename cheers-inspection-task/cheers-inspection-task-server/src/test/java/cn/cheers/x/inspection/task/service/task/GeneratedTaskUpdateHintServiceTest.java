package cn.cheers.x.inspection.task.service.task;

import cn.cheers.x.inspection.task.api.TaskUpdateHintMarkReqDTO;
import cn.cheers.x.inspection.task.model.task.InspectionContent;
import cn.cheers.x.inspection.task.service.execution.steptree.HostSopParamPack;
import cn.cheers.x.inspection.task.service.execution.steptree.PatrolTaskStepTreeReader;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("保存动作或检查项时，只给用到这次改动的已生成任务记需要更新")
class GeneratedTaskUpdateHintServiceTest {

    private static final long ACTION_ID = 88L;
    private static final String ACTION_CODE = "act-dwell";
    private static final long ITEM_ID = 2354L;

    private PatrolTaskEntityStore store;
    private PatrolTaskStepTreeReader stepTreeReader;
    private GeneratedTaskUpdateHintService service;

    @BeforeEach
    void setUp() {
        store = mock(PatrolTaskEntityStore.class);
        stepTreeReader = mock(PatrolTaskStepTreeReader.class);
        when(stepTreeReader.loadHostPackOrEmpty(any())).thenReturn(HostSopParamPack.empty());
        service = new GeneratedTaskUpdateHintService(store, stepTreeReader, new ObjectMapper());
    }

    @Test
    @DisplayName("动作耗时变了，记下用到这个动作的已生成任务")
    void durationChange_marksTaskThatUsesAction() {
        PatrolTaskDraft using = task(1L, "1-5# 储罐 每周巡检", ACTION_CODE, ACTION_ID, ITEM_ID);
        PatrolTaskDraft other = task(2L, "进油阀专项巡检", "act-other", 99L, 9999L);
        when(store.listAll()).thenReturn(List.of(using, other));

        service.mark(actionReq(slots(1, 30), slots(2, 30)));

        verify(store).mergeDraftFields(eq(1L), eq(Map.of(
                PatrolTaskEntityStore.DRAFT_KEY_UPDATE_HINT, "动作「停留观察」的动作耗时已变化")));
        verify(store, never()).mergeDraftFields(eq(2L), any());
    }

    @Test
    @DisplayName("只改停留时长默认值，设备上已经填过的任务不记")
    void defaultOnly_skipsWhenEquipmentFilled() {
        when(store.listAll()).thenReturn(List.of(task(1L, "1-5# 储罐 每周巡检", ACTION_CODE, ACTION_ID, ITEM_ID)));
        when(stepTreeReader.loadHostPackOrEmpty(900156L)).thenReturn(filledDwell());

        service.mark(actionReq(slots(1, 30), slots(1, 10)));

        verify(store, never()).mergeDraftFields(any(), any());
    }

    @Test
    @DisplayName("只改停留时长默认值，当时这一格是空的任务要记")
    void defaultOnly_marksWhenEquipmentLeftBlank() {
        when(store.listAll()).thenReturn(List.of(task(1L, "1-5# 储罐 每周巡检", ACTION_CODE, ACTION_ID, ITEM_ID)));

        service.mark(actionReq(slots(1, 30), slots(1, 10)));

        verify(store).mergeDraftFields(eq(1L), eq(Map.of(
                PatrolTaskEntityStore.DRAFT_KEY_UPDATE_HINT,
                "动作「停留观察」的参数默认值已变化，有的步骤当时用的是默认值")));
    }

    @Test
    @DisplayName("检查步骤顺序变了，记下勾了这项的已生成任务")
    void stepReorder_marksTaskThatCheckedItem() {
        PatrolTaskDraft checked = task(1L, "1-5# 储罐 每周巡检", ACTION_CODE, ACTION_ID, ITEM_ID);
        PatrolTaskDraft skipped = task(2L, "进油阀专项巡检", "act-other", 99L, 9999L);
        when(store.listAll()).thenReturn(List.of(checked, skipped));

        service.mark(itemReq(steps(1, 2), steps(2, 1)));

        verify(store).mergeDraftFields(eq(1L), eq(Map.of(
                PatrolTaskEntityStore.DRAFT_KEY_UPDATE_HINT, "检查项「罐体无渗漏变形」的检查步骤已变化")));
        verify(store, never()).mergeDraftFields(eq(2L), any());
    }

    private static TaskUpdateHintMarkReqDTO actionReq(String before, String after) {
        TaskUpdateHintMarkReqDTO req = new TaskUpdateHintMarkReqDTO();
        req.setEntityTypeCode("action");
        req.setEntityId(ACTION_ID);
        req.setCode(ACTION_CODE);
        req.setName("停留观察");
        req.setBeforeJson(before);
        req.setAfterJson(after);
        return req;
    }

    private static TaskUpdateHintMarkReqDTO itemReq(String before, String after) {
        TaskUpdateHintMarkReqDTO req = new TaskUpdateHintMarkReqDTO();
        req.setEntityTypeCode("inspection_item");
        req.setEntityId(ITEM_ID);
        req.setCode("item-leak");
        req.setName("罐体无渗漏变形");
        req.setBeforeJson(before);
        req.setAfterJson(after);
        return req;
    }

    private static String slots(int durationMinutes, int dwellDefault) {
        return """
                {"methods":[{"executionMeans":"ROBOT","fields":[
                  {"fieldCode":"action_duration","defaultValue":%d,"durationUnit":"s"},
                  {"fieldCode":"dwell_duration","defaultValue":%d}
                ]}]}
                """.formatted(durationMinutes, dwellDefault);
    }

    private static String steps(int arriveOrder, int dwellOrder) {
        return """
                {"methods":[{"executionMeans":"ROBOT","actionTree":[
                  {"nodeType":"STEP","order":%d,"refCode":"arrive","name":"到达指定位置"},
                  {"nodeType":"STEP","order":%d,"refCode":"act-dwell","name":"停留观察"}
                ]}]}
                """.formatted(arriveOrder, dwellOrder);
    }

    private static HostSopParamPack filledDwell() {
        Map<String, Object> pack = new LinkedHashMap<>();
        pack.put("entries", List.of(Map.of(
                "subjectId", ITEM_ID,
                "dimensionValue", "ROBOT",
                "paramsByNode", Map.of("n2", Map.of("dwell_duration", "30")))));
        return HostSopParamPack.parse(pack, new ObjectMapper());
    }

    private static PatrolTaskDraft task(Long id, String name, String actionCode, Long actionId, Long itemId) {
        InspectionContent.ItemContent item = new InspectionContent.ItemContent();
        item.setItemId(itemId);
        InspectionContent.ObjectContent object = new InspectionContent.ObjectContent();
        object.setObjectId(900156L);
        object.setItems(List.of(item));
        InspectionContent content = new InspectionContent();
        content.setCustomObjects(List.of(object));
        Map<String, Object> equipment = new LinkedHashMap<>();
        equipment.put("nodeKey", "eq");
        equipment.put("hangTypeCode", "equipment");
        equipment.put("refId", 900156L);
        Map<String, Object> itemNode = new LinkedHashMap<>();
        itemNode.put("nodeKey", "item");
        itemNode.put("parentNodeKey", "eq");
        itemNode.put("hangTypeCode", "inspection_item");
        itemNode.put("refId", itemId);
        Map<String, Object> action = new LinkedHashMap<>();
        action.put("nodeKey", "act");
        action.put("parentNodeKey", "item");
        action.put("hangTypeCode", "action");
        action.put("refCode", actionCode);
        action.put("refId", actionId);
        action.put("sourceNodeKey", "n2");
        return new PatrolTaskDraft(
                id, name, "巡检", 44L, "ROBOT", content, null, null,
                Map.of("nodes", List.of(equipment, itemNode, action)),
                null, "enabled", 3, null, null, "job", Boolean.TRUE,
                null, null, null, null, null);
    }
}
