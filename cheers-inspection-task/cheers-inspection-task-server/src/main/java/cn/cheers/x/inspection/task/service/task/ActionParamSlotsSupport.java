package cn.cheers.x.inspection.task.service.task;

import cn.cheers.x.module.dynamicbusiness.action.paramslots.ActionParamSlotsJsonCore;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 读动作库 {@code param_slots_json} 里当前巡检方式声明了哪些参数槽。
 * <p>设备上这项、这一档已经填了的值优先。没填的槽用动作库该档的默认值，生成步骤树和打开核对用同一规则。
 * 不沿用步骤上已经写下的旧数。动作库默认改过之后，更新任务仍把旧数写回去，打开就会一直说需要更新。
 * <p>不负责：算动作耗时。加总只读 {@code action_duration}。动作参数填进哪一个指令字段，在指令协议对接页配置。
 */
public final class ActionParamSlotsSupport {

    private ActionParamSlotsSupport() {
    }

    public static List<String> fieldCodes(Object raw, String means, ObjectMapper objectMapper) {
        return fieldCodes(raw, means, null, objectMapper);
    }

    public static List<String> fieldCodes(
            Object raw, String means, Long equipmentModelId, ObjectMapper objectMapper) {
        return ActionParamSlotsJsonCore.fieldCodes(raw, means, equipmentModelId, objectMapper);
    }

    public static Map<String, Object> defaultValues(Object raw, String means, ObjectMapper objectMapper) {
        return defaultValues(raw, means, null, objectMapper);
    }

    public static Map<String, Object> defaultValues(
            Object raw, String means, Long equipmentModelId, ObjectMapper objectMapper) {
        return ActionParamSlotsJsonCore.defaultValues(raw, means, equipmentModelId, objectMapper);
    }

    public static Map<String, Object> mergeInspectionStepParams(
            Map<String, Object> previous,
            Collection<String> declaredSlots,
            Map<String, Object> equipmentValues,
            Set<String> locationSlots
    ) {
        Map<String, Object> merged = new LinkedHashMap<>();
        if (declaredSlots != null) {
            for (String slot : declaredSlots) {
                if (slot != null && !slot.isBlank()) {
                    merged.put(slot, "");
                }
            }
        }
        if (equipmentValues != null) {
            for (Map.Entry<String, Object> entry : equipmentValues.entrySet()) {
                if (entry.getKey() != null && !entry.getKey().isBlank() && isFilled(entry.getValue())) {
                    merged.put(entry.getKey(), entry.getValue());
                }
            }
        }
        if (previous != null && locationSlots != null) {
            for (String slot : locationSlots) {
                if (slot == null || slot.isBlank()) {
                    continue;
                }
                Object current = merged.get(slot);
                Object kept = previous.get(slot);
                if (!isFilled(current) && isFilled(kept)) {
                    merged.put(slot, kept);
                }
            }
        }
        return merged;
    }

    public static Map<String, Object> resolveStepParams(
            Map<String, Object> previous,
            Collection<String> declaredSlots,
            Map<String, Object> equipmentValues,
            Map<String, Object> defaults,
            Set<String> locationSlots
    ) {
        Map<String, Object> merged = new LinkedHashMap<>();
        if (declaredSlots != null) {
            for (String slot : declaredSlots) {
                if (slot != null && !slot.isBlank()) {
                    merged.put(slot, "");
                }
            }
        }
        if (equipmentValues != null) {
            for (Map.Entry<String, Object> entry : equipmentValues.entrySet()) {
                if (entry.getKey() != null && !entry.getKey().isBlank() && isFilled(entry.getValue())) {
                    merged.put(entry.getKey(), entry.getValue());
                }
            }
        }
        fillBlankFromDefaults(merged, defaults);
        if (previous != null && locationSlots != null) {
            for (String slot : locationSlots) {
                if (slot == null || slot.isBlank()) {
                    continue;
                }
                Object current = merged.get(slot);
                Object kept = previous.get(slot);
                if (!isFilled(current) && isFilled(kept)) {
                    merged.put(slot, kept);
                }
            }
        }
        return merged;
    }

    public static void fillBlankFromDefaults(Map<String, Object> params, Map<String, Object> defaults) {
        if (params == null || defaults == null || defaults.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : defaults.entrySet()) {
            if (entry.getKey() == null || entry.getKey().isBlank() || entry.getValue() == null) {
                continue;
            }
            Object current = params.get(entry.getKey());
            if (!isFilled(current)) {
                params.put(entry.getKey(), entry.getValue());
            }
        }
    }

    static Map<?, ?> methodRow(Map<?, ?> root, String means, Long equipmentModelId) {
        return ActionParamSlotsJsonCore.methodRow(root, means, equipmentModelId);
    }

    static boolean paramsPerEquipmentModel(Map<?, ?> root, String meansUpper) {
        return ActionParamSlotsJsonCore.paramsPerEquipmentModel(root, meansUpper);
    }

    private static boolean isFilled(Object raw) {
        return ActionParamSlotsJsonCore.isFilled(raw);
    }
}
