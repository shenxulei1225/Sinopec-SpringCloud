package cn.cheers.x.inspection.task.service.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("动作参数槽：设备已填优先，没填才用动作库默认")
class ActionParamSlotsSupportTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("默认值不含动作耗时，空默认不返回")
    void defaultValues_excludesActionDurationAndBlank() {
        Map<String, Object> raw = Map.of("methods", List.of(Map.of(
                "methodKey", "ROBOT",
                "fields", List.of(
                        Map.of("fieldCode", "dwell_duration", "defaultValue", 30),
                        Map.of("fieldCode", "action_duration", "defaultValue", 1),
                        Map.of("fieldCode", "shot_count", "defaultValue", "")))));

        Map<String, Object> defaults = ActionParamSlotsSupport.defaultValues(raw, "ROBOT", objectMapper);

        assertEquals(30, defaults.get("dwell_duration"));
        assertFalse(defaults.containsKey("action_duration"));
        assertFalse(defaults.containsKey("shot_count"));
    }

    @Test
    @DisplayName("系统 UUID 在冻结步骤时生成，不要求用户填写")
    void defaultValues_generatesSystemUuid() {
        Map<String, Object> raw = Map.of("methods", List.of(Map.of(
                "methodKey", "UAV",
                "fields", List.of(Map.of(
                        "fieldCode", "actionUUID",
                        "valueSource", "SYSTEM",
                        "generator", "UUID_V4")))));

        Map<String, Object> defaults = ActionParamSlotsSupport.defaultValues(raw, "UAV", objectMapper);

        assertDoesNotThrow(() -> java.util.UUID.fromString(String.valueOf(defaults.get("actionUUID"))));
    }

    @Test
    @DisplayName("设备已填的留下，空串才补默认")
    void fillBlankFromDefaults_keepsFilledValue() {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("dwell_duration", 10);
        params.put("focal_length", "");

        ActionParamSlotsSupport.fillBlankFromDefaults(params, Map.of(
                "dwell_duration", 30,
                "focal_length", 2));

        assertEquals(10, params.get("dwell_duration"));
        assertEquals(2, params.get("focal_length"));
    }

    @Test
    @DisplayName("步骤上的旧数不留下，没填的用动作库当前默认；点位仍留下")
    void resolveStepParams_replacesOldValueWithCurrentDefault() {
        Map<String, Object> resolved = ActionParamSlotsSupport.resolveStepParams(
                Map.of("pitch", 10, "location_ref", "P1"),
                List.of("pitch", "location_ref"),
                Map.of(),
                Map.of("pitch", 0),
                java.util.Set.of("location_ref"));

        assertEquals(0, resolved.get("pitch"));
        assertEquals("P1", resolved.get("location_ref"));
    }

    @Test
    @DisplayName("设备另填的值盖过动作库默认和步骤旧数")
    void resolveStepParams_equipmentOverrideWins() {
        Map<String, Object> resolved = ActionParamSlotsSupport.resolveStepParams(
                Map.of("pitch", 10),
                List.of("pitch"),
                Map.of("pitch", 5),
                Map.of("pitch", 0),
                java.util.Set.of());

        assertEquals(5, resolved.get("pitch"));
    }

    @Test
    @DisplayName("任务步骤不推导负载挂载序号，只保留用户选的摄像机编号")
    void resolveStepParams_keepsSelectedCameraWithoutDerivingMountIndex() {
        Map<String, Object> resolved = ActionParamSlotsSupport.resolveStepParams(
                Map.of(),
                List.of("selected_camera", "payloadPositionIndex"),
                Map.of("selected_camera", "43-0-0"),
                Map.of(),
                java.util.Set.of());

        assertEquals("43-0-0", resolved.get("selected_camera"));
        assertEquals("", resolved.get("payloadPositionIndex"));
    }

    @Test
    @DisplayName("没选摄像机时不编挂载序号")
    void resolveStepParams_leavesMountEmptyWithoutCamera() {
        Map<String, Object> resolved = ActionParamSlotsSupport.resolveStepParams(
                Map.of(),
                List.of("payloadPositionIndex"),
                Map.of(),
                Map.of(),
                java.util.Set.of());

        assertEquals("", resolved.get("payloadPositionIndex"));
    }

    @Test
    @DisplayName("未声明负载挂载位置槽时不额外写入序号")
    void resolveStepParams_doesNotAddUndeclaredMountIndex() {
        Map<String, Object> resolved = ActionParamSlotsSupport.resolveStepParams(
                Map.of(),
                List.of("selected_camera"),
                Map.of("selected_camera", "43-0-0"),
                Map.of(),
                java.util.Set.of());

        assertFalse(resolved.containsKey("payloadPositionIndex"));
    }
}
