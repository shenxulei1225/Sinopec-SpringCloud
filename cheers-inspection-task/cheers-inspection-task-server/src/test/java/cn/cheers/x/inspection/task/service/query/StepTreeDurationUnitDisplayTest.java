package cn.cheers.x.inspection.task.service.query;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.task.service.execution.steptree.PatrolTaskStepTreeReader;
import cn.cheers.x.module.dynamicbusiness.api.entity.EntityRpcApi;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StepTreeDurationUnitDisplayTest {

    private static final String LOCATION = "F-e49f76bdca3e4e1393e8e2f1cc0e7867";

    @Test
    void withDisplayUnits_batchLoadsPointNamesOntoTheReturnedCopy() {
        EntityRpcApi api = mock(EntityRpcApi.class);
        when(api.listEntitiesByIds(anyList(), eq("point")))
                .thenReturn(CommonResult.success(List.of(point(94L, "北罐组西罐1底", "PN-94"))));
        StepTreeDurationUnitDisplay display = display(api);
        Map<String, Object> stored = new LinkedHashMap<>();
        stored.put(LOCATION, 94);
        Map<String, Object> tree = Map.of(
                "version", 1,
                "nodes", List.of(
                        action("a1", stored),
                        action("a2", Map.of(LOCATION, 94)),
                        action("aim", Map.of())));

        @SuppressWarnings("unchecked")
        Map<String, Object> returned = (Map<String, Object>) display.withDisplayUnits(tree, "ROBOT", 44L);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> nodes = (List<Map<String, Object>>) returned.get("nodes");
        @SuppressWarnings("unchecked")
        Map<String, Object> place = (Map<String, Object>) ((Map<String, Object>) nodes.get(0).get("params")).get(LOCATION);

        assertEquals(94L, ((Number) place.get("id")).longValue());
        assertEquals("北罐组西罐1底", place.get("name"));
        assertEquals("PN-94", place.get("code"));
        assertEquals(94, stored.get(LOCATION));
        verify(api, times(1)).listEntitiesByIds(anyList(), eq("point"));
    }

    @Test
    void withDisplayUnits_unknownPointIdStaysANumber() {
        EntityRpcApi api = mock(EntityRpcApi.class);
        when(api.listEntitiesByIds(anyList(), eq("point"))).thenReturn(CommonResult.success(List.of()));
        StepTreeDurationUnitDisplay display = display(api);
        Map<String, Object> tree = Map.of(
                "version", 1,
                "nodes", List.of(action("a1", Map.of(LOCATION, 95))));

        @SuppressWarnings("unchecked")
        Map<String, Object> returned = (Map<String, Object>) display.withDisplayUnits(tree, null, null);
        @SuppressWarnings("unchecked")
        Map<String, Object> params = (Map<String, Object>) ((Map<String, Object>) ((List<?>) returned.get("nodes")).get(0))
                .get("params");

        assertEquals(95, params.get(LOCATION));
    }

    @Test
    void withDisplayUnits_listsAimAndShootSlotsFromTheActionLibrary() {
        EntityRpcApi api = mock(EntityRpcApi.class);
        when(api.getEntityByCode(eq("act-robot-aim"), eq("action")))
                .thenReturn(CommonResult.success(actionSlots("act-robot-aim", List.of(
                        field("yaw", 0),
                        field("pitch", 0),
                        field("roll", 0),
                        field("focal_length", 30),
                        field("action_duration", 20)))));
        when(api.getEntityByCode(eq("act-robot-shoot"), eq("action")))
                .thenReturn(CommonResult.success(actionSlots("act-robot-shoot", List.of(
                        field("shot_count", 3),
                        field("action_duration", 30)))));
        StepTreeDurationUnitDisplay display = display(api);
        Map<String, Object> aim = action("aim", null);
        aim.put("refCode", "act-robot-aim");
        aim.remove("params");
        Map<String, Object> shoot = action("shoot", null);
        shoot.put("refCode", "act-robot-shoot");
        shoot.remove("params");
        Map<String, Object> tree = new LinkedHashMap<>();
        tree.put("version", 1);
        tree.put("nodes", List.of(aim, shoot));

        @SuppressWarnings("unchecked")
        Map<String, Object> returned = (Map<String, Object>) display.withDisplayUnits(tree, "ROBOT", null);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> nodes = (List<Map<String, Object>>) returned.get("nodes");
        @SuppressWarnings("unchecked")
        Map<String, Object> aimParams = (Map<String, Object>) nodes.get(0).get("params");
        @SuppressWarnings("unchecked")
        Map<String, Object> shootParams = (Map<String, Object>) nodes.get(1).get("params");

        assertEquals(0, aimParams.get("yaw"));
        assertEquals(0, aimParams.get("pitch"));
        assertEquals(0, aimParams.get("roll"));
        assertEquals(30, aimParams.get("focal_length"));
        assertEquals(3, shootParams.get("shot_count"));
        assertEquals(false, aim.containsKey("params"));
        assertEquals(false, shoot.containsKey("params"));
    }

    @Test
    void withDisplayUnits_fillsPhotoDurationFromTheOnlyMountedCamera() {
        EntityRpcApi api = mock(EntityRpcApi.class);
        EntityRespDTO aircraft = new EntityRespDTO();
        aircraft.setBaseFields(Map.of(
                "modelId", 1867,
                "payload_mounts", List.of(
                        Map.of("mountPoint", "0", "equipmentId", 900294),
                        Map.of("mountPoint", "7", "equipmentId", 900293))));
        EntityRespDTO gimbal = new EntityRespDTO();
        gimbal.setBaseFields(Map.of("modelId", 1895));
        EntityRespDTO body = new EntityRespDTO();
        body.setBaseFields(Map.of("modelId", 1917));
        when(api.getEntity(900158L, "equipment")).thenReturn(CommonResult.success(aircraft));
        when(api.getEntity(900294L, "equipment")).thenReturn(CommonResult.success(gimbal));
        when(api.getEntity(900293L, "equipment")).thenReturn(CommonResult.success(body));
        when(api.getEntityByCode(eq("orientedShoot"), eq("action")))
                .thenReturn(CommonResult.success(photoAction()));
        StepTreeDurationUnitDisplay display = display(api);
        Map<String, Object> stored = new LinkedHashMap<>();
        stored.put("action_duration", "");
        Map<String, Object> node = action("photo", stored);
        node.put("refCode", "orientedShoot");
        Map<String, Object> tree = new LinkedHashMap<>();
        tree.put("version", 1);
        tree.put("nodes", List.of(node));

        @SuppressWarnings("unchecked")
        Map<String, Object> returned = (Map<String, Object>) display.withDisplayUnits(tree, "UAV", null, 900158L);
        @SuppressWarnings("unchecked")
        Map<String, Object> params = (Map<String, Object>) ((Map<String, Object>) ((List<?>) returned.get("nodes")).get(0))
                .get("params");

        assertEquals(new java.math.BigDecimal("10"), params.get("action_duration"));
        assertEquals("", stored.get("action_duration"));
    }

    private static StepTreeDurationUnitDisplay display(EntityRpcApi api) {
        return new StepTreeDurationUnitDisplay(api, new ObjectMapper(), mock(PatrolTaskStepTreeReader.class));
    }

    private static EntityRespDTO photoAction() {
        EntityRespDTO entity = new EntityRespDTO();
        entity.setBaseFields(Map.of(
                "code", "orientedShoot",
                "param_slots_json", Map.of(
                        "methods", List.of(
                                Map.of("executionMeans", "UAV", "paramsPerEquipmentModel", true,
                                        "equipmentModelId", 1895,
                                        "fields", List.of(field("action_duration", 10))),
                                Map.of("executionMeans", "UAV", "paramsPerEquipmentModel", true,
                                        "equipmentModelId", 1885,
                                        "fields", List.of(field("action_duration", 10)))))));
        return entity;
    }

    private static EntityRespDTO actionSlots(String code, List<Map<String, Object>> fields) {
        EntityRespDTO entity = new EntityRespDTO();
        entity.setBaseFields(Map.of(
                "code", code,
                "param_slots_json", Map.of(
                        "methods", List.of(Map.of(
                                "executionMeans", "ROBOT",
                                "fields", fields)))));
        return entity;
    }

    private static Map<String, Object> field(String code, Object defaultValue) {
        Map<String, Object> field = new LinkedHashMap<>();
        field.put("fieldCode", code);
        field.put("defaultValue", defaultValue);
        if ("action_duration".equals(code)) {
            field.put("durationUnit", "s");
        }
        return field;
    }

    private static Map<String, Object> action(String key, Map<String, Object> params) {
        Map<String, Object> node = new LinkedHashMap<>();
        node.put("nodeKey", key);
        node.put("hangTypeCode", "action");
        node.put("refCode", "act-arrive");
        node.put("params", params);
        return node;
    }

    private static EntityRespDTO point(long id, String name, String code) {
        EntityRespDTO entity = new EntityRespDTO();
        entity.setId(id);
        entity.setBaseFields(Map.of("name", name, "code", code));
        return entity;
    }
}
