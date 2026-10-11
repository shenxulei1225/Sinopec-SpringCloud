package cn.cheers.x.inspection.task.service.task;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.task.model.task.InspectionContent;
import cn.cheers.x.inspection.task.service.execution.steptree.ActionGroupMemberParser;
import cn.cheers.x.module.dynamicbusiness.api.entity.EntityRpcApi;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatrolItemActionDurationCalculatorTest {

    @Mock
    private EntityRpcApi entityRpcApi;
    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();
    @Spy
    private ActionGroupMemberParser actionGroupMemberParser =
            new ActionGroupMemberParser(objectMapper);
    @InjectMocks
    private PatrolItemActionDurationCalculator calculator;

    @Test
    void computeLiveMinutes_sumsEachSelectedItemOccurrence() {
        InspectionContent content = contentWithItems(10L, 10L);
        when(entityRpcApi.getEntity(10L, "inspection_item"))
                .thenReturn(CommonResult.success(itemEntity(10L, "ROBOT", "act-robot-shoot")));
        when(entityRpcApi.getEntityByCode("act-robot-shoot", "action"))
                .thenReturn(CommonResult.success(actionEntity("act-robot-shoot", 180, false, List.of())));

        assertEquals(new BigDecimal("360"), calculator.computeLiveSeconds(content, "ROBOT").orElseThrow());
    }

    @Test
    void computeLiveMinutes_compositeWithoutOwnDuration_sumsChildren() {
        InspectionContent content = contentWithItems(11L);
        when(entityRpcApi.getEntity(11L, "inspection_item"))
                .thenReturn(CommonResult.success(itemEntity(11L, "UAV", "act-uav-leak-inspect")));
        when(entityRpcApi.getEntityByCode("act-uav-leak-inspect", "action"))
                .thenReturn(CommonResult.success(actionEntity(
                        "act-uav-leak-inspect", null, true, List.of("act-arrive", "act-hover"))));
        when(entityRpcApi.getEntityByCode("act-arrive", "action"))
                .thenReturn(CommonResult.success(actionEntity("act-arrive", 60, false, List.of())));
        when(entityRpcApi.getEntityByCode("act-hover", "action"))
                .thenReturn(CommonResult.success(actionEntity("act-hover", 120, false, List.of())));

        assertEquals(new BigDecimal("180"), calculator.computeLiveSeconds(content, "UAV").orElseThrow());
    }

    @Test
    void computeLiveMinutes_stepWithoutDurationField_doesNotDropTheItem() {
        InspectionContent content = contentWithItems(14L);
        when(entityRpcApi.getEntity(14L, "inspection_item"))
                .thenReturn(CommonResult.success(itemWithActions(14L, "ROBOT", "act-arrive", "act-robot-hold")));
        when(entityRpcApi.getEntityByCode("act-arrive", "action"))
                .thenReturn(CommonResult.success(actionWithoutDurationField("act-arrive")));
        when(entityRpcApi.getEntityByCode("act-robot-hold", "action"))
                .thenReturn(CommonResult.success(actionEntity("act-robot-hold", new BigDecimal("60"), false, List.of())));

        assertEquals(new BigDecimal("60"), calculator.computeLiveSeconds(content, "ROBOT").orElseThrow());
    }

    @Test
    void computeLiveMinutes_missingDuration_isEmpty() {
        InspectionContent content = contentWithItems(12L);
        when(entityRpcApi.getEntity(12L, "inspection_item"))
                .thenReturn(CommonResult.success(itemEntity(12L, "UAV", "act-aim")));
        when(entityRpcApi.getEntityByCode("act-aim", "action"))
                .thenReturn(CommonResult.success(actionEntity("act-aim", null, false, List.of())));

        assertTrue(calculator.computeLiveSeconds(content, "UAV").isEmpty());
    }

    @Test
    void computeLiveMinutes_modelSplitWithoutEquipment_isEmpty() {
        InspectionContent content = contentWithItems(15L);
        when(entityRpcApi.getEntity(15L, "inspection_item"))
                .thenReturn(CommonResult.success(itemEntity(15L, "ROBOT", "act-robot-hold")));
        when(entityRpcApi.getEntityByCode("act-robot-hold", "action"))
                .thenReturn(CommonResult.success(modelSplitAction("act-robot-hold")));

        assertTrue(calculator.computeLiveSeconds(content, "ROBOT").isEmpty());
    }

    @Test
    void computeLiveMinutes_modelSplitUsesEquipmentModel() {
        InspectionContent content = contentWithItems(16L);
        when(entityRpcApi.getEntity(16L, "inspection_item"))
                .thenReturn(CommonResult.success(itemEntity(16L, "ROBOT", "act-robot-hold")));
        when(entityRpcApi.getEntityByCode("act-robot-hold", "action"))
                .thenReturn(CommonResult.success(modelSplitAction("act-robot-hold")));
        EntityRespDTO equipment = new EntityRespDTO();
        equipment.setBaseFields(Map.of("modelId", 1773));
        when(entityRpcApi.getEntity(90L, "equipment")).thenReturn(CommonResult.success(equipment));

        assertEquals(
                new BigDecimal("40"),
                calculator.computeLiveSecondsForEquipment(content, "ROBOT", 90L).orElseThrow());
    }

    @Test
    void computeLiveMinutes_keepsFractionUnderOneMinute() {
        InspectionContent content = contentWithItems(13L, 13L);
        when(entityRpcApi.getEntity(13L, "inspection_item"))
                .thenReturn(CommonResult.success(itemEntity(13L, "ROBOT", "act-robot-shoot")));
        when(entityRpcApi.getEntityByCode("act-robot-shoot", "action"))
                .thenReturn(CommonResult.success(actionEntity("act-robot-shoot", new BigDecimal("30"), false, List.of())));

        assertEquals(new BigDecimal("60"), calculator.computeLiveSeconds(content, "ROBOT").orElseThrow());
    }

    private static InspectionContent contentWithItems(Long... itemIds) {
        InspectionContent.ObjectContent object = new InspectionContent.ObjectContent();
        object.setObjectId(1L);
        object.setItems(java.util.Arrays.stream(itemIds).map(id -> {
            InspectionContent.ItemContent item = new InspectionContent.ItemContent();
            item.setItemId(id);
            return item;
        }).toList());
        InspectionContent content = new InspectionContent();
        content.setCustomObjects(List.of(object));
        return content;
    }

    private static EntityRespDTO itemEntity(Long id, String means, String actionCode) {
        return itemWithActions(id, means, actionCode);
    }

    private static EntityRespDTO itemWithActions(Long id, String means, String... actionCodes) {
        EntityRespDTO entity = new EntityRespDTO();
        entity.setId(id);
        entity.setBaseFields(Map.of(
                "step_tree_json", Map.of(
                        "version", 1,
                        "methods", List.of(Map.of(
                                "methodKey", means,
                                "stepTree", java.util.Arrays.stream(actionCodes).map(code -> Map.of(
                                        "nodeType", "STEP",
                                        "actionId", code)).toList())))));
        return entity;
    }

    private static EntityRespDTO actionWithoutDurationField(String code) {
        EntityRespDTO entity = new EntityRespDTO();
        entity.setId(code.hashCode() & 0xffffL);
        entity.setBaseFields(Map.of("code", code));
        entity.setCustomFields(Map.of(
                "param_slots_json", Map.of(
                        "version", 2,
                        "methods", List.of(Map.of(
                                "executionMeans", "ROBOT",
                                "fields", List.of(Map.of("fieldCode", "location_ref", "defaultValue", "p1")))))));
        return entity;
    }

    private static EntityRespDTO modelSplitAction(String code) {
        EntityRespDTO entity = new EntityRespDTO();
        entity.setId(code.hashCode() & 0xffffL);
        entity.setBaseFields(Map.of("code", code));
        entity.setCustomFields(Map.of(
                "param_slots_json", Map.of(
                        "version", 2,
                        "methods", List.of(
                                Map.of("executionMeans", "ROBOT", "equipmentModelId", 1772, "fields", List.of(
                                        Map.of("fieldCode", "action_duration", "defaultValue", 100))),
                                Map.of("executionMeans", "ROBOT", "equipmentModelId", 1773, "fields", List.of(
                                        Map.of("fieldCode", "action_duration", "defaultValue", 40)))))));
        return entity;
    }

    private static EntityRespDTO actionEntity(
            String code, Number minutes, boolean composite, List<String> children
    ) {
        EntityRespDTO entity = new EntityRespDTO();
        entity.setId(code.hashCode() & 0xffffL);
        entity.setBaseFields(Map.of("code", code));
        Map<String, Object> field = new java.util.LinkedHashMap<>();
        field.put("fieldCode", "action_duration");
        field.put("defaultValue", minutes);
        Map<String, Object> slots = new java.util.LinkedHashMap<>();
        slots.put("version", 1);
        slots.put("fields", List.of(field));
        Map<String, Object> custom = new java.util.LinkedHashMap<>();
        custom.put("param_slots_json", slots);
        if (composite) {
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
        return entity;
    }
}
