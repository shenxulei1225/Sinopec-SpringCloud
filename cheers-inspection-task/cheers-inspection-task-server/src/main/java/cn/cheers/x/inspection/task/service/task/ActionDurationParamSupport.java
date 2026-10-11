package cn.cheers.x.inspection.task.service.task;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 从动作库参数清单读排程用的动作耗时。
 * <p>动作库里的数字就是秒。字段库不指定单位，配置时选的秒、分、小时只用于显示。
 * <p>已经按型号分开的，只读选定执行设备那一型号的默认秒数。还没选设备时不拿另一型号的秒数。
 * <p>不负责：写巡检内容、算路径耗时、读宿主参数包。
 * <p>禁止：把停留时长当成动作耗时；缺默认值时猜秒数；把不足 1 秒截成 0。
 * 动作参数填进哪一个指令字段，在指令协议对接页配置，不在这里写死。
 */
public final class ActionDurationParamSupport {

    public static final String FIELD_CODE = "action_duration";

    private ActionDurationParamSupport() {
    }

    /**
     * 写进任务的耗时是秒。displayUnit 仍是动作上选的秒 / 分 / 小时，只给界面换算显示。
     */
    public record SlotDuration(BigDecimal seconds, String displayUnit) {
    }

    /**
     * 先读当前巡检方式、当前执行设备型号的那一档。这一档还没按型号分开时，读这一方式上没写型号的那一行。
     * 已经按型号分开、又还没选定执行设备时，不读别的型号，返回 empty。
     *
     * @return 有非负默认值 → 整数秒和配置单位；槽不存在或默认值为空 → empty
     */
    public static Optional<SlotDuration> readSlot(Object raw, String means, ObjectMapper objectMapper) {
        return readSlot(raw, means, null, objectMapper);
    }

    public static Optional<SlotDuration> readSlot(
            Object raw, String means, Long equipmentModelId, ObjectMapper objectMapper) {
        Object value = unwrap(raw, objectMapper);
        if (!(value instanceof Map<?, ?> root)) {
            return Optional.empty();
        }
        if (root.get("methods") instanceof List<?> methods && !methods.isEmpty()) {
            Map<?, ?> row = ActionParamSlotsSupport.methodRow(root, means, equipmentModelId);
            if (row != null) {
                return durationFromFields(row.get("fields"));
            }
            String meansUpper = means == null ? "" : means.trim().toUpperCase();
            if (!meansUpper.isEmpty() && ActionParamSlotsSupport.paramsPerEquipmentModel(root, meansUpper)) {
                return Optional.empty();
            }
        }
        return durationFromFields(root.get("fields"));
    }

    /**
     * 先读当前巡检方式那一档 methods，没有再读顶层 fields。
     *
     * @return 有非负默认值 → 整数秒；槽不存在或默认值为空 → empty
     */
    public static Optional<BigDecimal> secondsFromSlots(Object raw, String means, ObjectMapper objectMapper) {
        return secondsFromSlots(raw, means, null, objectMapper);
    }

    public static Optional<BigDecimal> secondsFromSlots(
            Object raw, String means, Long equipmentModelId, ObjectMapper objectMapper) {
        return readSlot(raw, means, equipmentModelId, objectMapper).map(SlotDuration::seconds);
    }

    /**
     * 当前这一档是否有动作耗时这一栏。栏在但默认值为空，仍算有这一栏。
     * 到达指定位置这类没有这一栏的步骤，返回 false，调用方按不占时间处理。
     */
    public static boolean declaresActionDuration(Object raw, String means, ObjectMapper objectMapper) {
        return declaresActionDuration(raw, means, null, objectMapper);
    }

    /**
     * 当前这一档是否有动作耗时这一栏。
     * 已经按型号分开、选定的型号对不上时，仍算有这一栏，调用方不把整项当成 0 秒。
     */
    public static boolean declaresActionDuration(
            Object raw, String means, Long equipmentModelId, ObjectMapper objectMapper) {
        Object value = unwrap(raw, objectMapper);
        if (!(value instanceof Map<?, ?> root)) {
            return false;
        }
        if (root.get("methods") instanceof List<?> methods && !methods.isEmpty()) {
            Map<?, ?> row = ActionParamSlotsSupport.methodRow(root, means, equipmentModelId);
            if (row != null) {
                return containsDurationField(row.get("fields"));
            }
            String meansUpper = means == null ? "" : means.trim().toUpperCase();
            if (!meansUpper.isEmpty() && ActionParamSlotsSupport.paramsPerEquipmentModel(root, meansUpper)) {
                return true;
            }
        }
        return containsDurationField(root.get("fields"));
    }

    private static Optional<SlotDuration> durationFromFields(Object fieldsRaw) {
        if (!(fieldsRaw instanceof List<?> fields)) {
            return Optional.empty();
        }
        for (Object row : fields) {
            if (!(row instanceof Map<?, ?> field)) {
                continue;
            }
            if (!FIELD_CODE.equals(text(first(field, "fieldCode", "code")))) {
                continue;
            }
            Optional<BigDecimal> stored = minutesOf(first(field, "defaultValue", "value"));
            if (stored.isEmpty()) {
                return Optional.empty();
            }
            String unit = text(field.get("durationUnit"));
            return Optional.of(new SlotDuration(stored.get(), unit.isEmpty() ? null : unit));
        }
        return Optional.empty();
    }

    private static boolean containsDurationField(Object fieldsRaw) {
        if (!(fieldsRaw instanceof List<?> fields)) {
            return false;
        }
        for (Object row : fields) {
            if (!(row instanceof Map<?, ?> field)) {
                continue;
            }
            if (FIELD_CODE.equals(text(first(field, "fieldCode", "code")))) {
                return true;
            }
        }
        return false;
    }

    static Optional<BigDecimal> minutesOf(Object raw) {
        if (raw == null) {
            return Optional.empty();
        }
        BigDecimal minutes;
        if (raw instanceof BigDecimal decimal) {
            minutes = decimal;
        } else if (raw instanceof Number number) {
            try {
                minutes = new BigDecimal(number.toString());
            } catch (NumberFormatException ex) {
                return Optional.empty();
            }
        } else {
            String text = String.valueOf(raw).trim();
            if (!StringUtils.hasText(text) || "null".equalsIgnoreCase(text)) {
                return Optional.empty();
            }
            try {
                minutes = new BigDecimal(text);
            } catch (NumberFormatException ignored) {
                return Optional.empty();
            }
        }
        if (minutes.signum() < 0) {
            return Optional.empty();
        }
        return Optional.of(plainMinutes(minutes));
    }

    /** 去掉末尾 0，避免 1.0 写成科学计数。0.5 保持 0.5。 */
    public static BigDecimal plainMinutes(BigDecimal minutes) {
        BigDecimal stripped = minutes.stripTrailingZeros();
        if (stripped.scale() < 0) {
            stripped = stripped.setScale(0);
        }
        return stripped;
    }

    private static Object unwrap(Object raw, ObjectMapper objectMapper) {
        if (raw == null || objectMapper == null) {
            return raw;
        }
        if (raw instanceof String text) {
            String trimmed = text.trim();
            if (trimmed.isEmpty()) {
                return null;
            }
            try {
                return objectMapper.readValue(trimmed, new TypeReference<Object>() {
                });
            } catch (Exception ex) {
                return null;
            }
        }
        return raw;
    }

    private static Object first(Map<?, ?> row, String... keys) {
        for (String key : keys) {
            if (row.containsKey(key) && row.get(key) != null) {
                return row.get(key);
            }
        }
        return null;
    }

    private static String text(Object raw) {
        return raw == null ? "" : String.valueOf(raw).trim();
    }
}
