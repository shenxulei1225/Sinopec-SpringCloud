package cn.cheers.x.inspection.task.service.task;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.task.model.task.InspectionContent;
import cn.cheers.x.module.dynamicbusiness.api.entity.EntityRpcApi;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

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
    @InjectMocks
    private PatrolItemActionDurationCalculator calculator;

    @Test
    void computeLiveMinutes_sumsEachSelectedItemOccurrence() {
        InspectionContent content = contentWithItems(10L, 10L);
        when(entityRpcApi.getEntity(10L, "inspection_item"))
                .thenReturn(CommonResult.success(itemEntity(10L, "ROBOT", "act-robot-shoot")));
        when(entityRpcApi.getEntityByCode("act-robot-shoot", "action"))
                .thenReturn(CommonResult.success(actionEntity("act-robot-shoot", 3, false, List.of())));

        assertEquals(6, calculator.computeLiveMinutes(content, "ROBOT").orElseThrow());
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
                .thenReturn(CommonResult.success(actionEntity("act-arrive", 1, false, List.of())));
        when(entityRpcApi.getEntityByCode("act-hover", "action"))
                .thenReturn(CommonResult.success(actionEntity("act-hover", 2, false, List.of())));

        assertEquals(3, calculator.computeLiveMinutes(content, "UAV").orElseThrow());
    }

    @Test
    void computeLiveMinutes_missingDuration_isEmpty() {
        InspectionContent content = contentWithItems(12L);
        when(entityRpcApi.getEntity(12L, "inspection_item"))
                .thenReturn(CommonResult.success(itemEntity(12L, "UAV", "act-aim")));
        when(entityRpcApi.getEntityByCode("act-aim", "action"))
                .thenReturn(CommonResult.success(actionEntity("act-aim", null, false, List.of())));

        assertTrue(calculator.computeLiveMinutes(content, "UAV").isEmpty());
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
        EntityRespDTO entity = new EntityRespDTO();
        entity.setId(id);
        entity.setBaseFields(Map.of(
                "step_tree_json", Map.of(
                        "version", 1,
                        "methods", List.of(Map.of(
                                "methodKey", means,
                                "stepTree", List.of(Map.of(
                                        "nodeType", "STEP",
                                        "actionId", actionCode)))))));
        return entity;
    }

    private static EntityRespDTO actionEntity(
            String code, Integer minutes, boolean composite, List<String> children
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
        entity.setCustomFields(Map.of(
                "param_slots_json", slots,
                "is_composite", composite,
                "child_action_ids_json", children));
        return entity;
    }
}
