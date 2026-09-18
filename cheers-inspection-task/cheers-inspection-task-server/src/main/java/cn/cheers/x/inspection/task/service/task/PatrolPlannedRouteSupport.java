package cn.cheers.x.inspection.task.service.task;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 读总任务 FLD-TSK-027 上的已确认路线；与详情回显口径一致。
 * <p>权威在总任务 plannedRoute，不是旧固定表 routePlanId。
 */
public final class PatrolPlannedRouteSupport {

    private static final Map<String, Integer> DEFAULT_SPEED_M_PER_MIN = Map.of(
            "HUMAN", 60,
            "GROUND_ROBOT", 40,
            "UAV", 80);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private PatrolPlannedRouteSupport() {
    }

    public static boolean hasConfirmedRoute(Object plannedRoute) {
        Map<String, Object> planned = asPlannedMap(plannedRoute);
        if (planned == null || planned.isEmpty()) {
            return false;
        }
        return !CollectionUtils.isEmpty(asStringList(planned.get("stopIds")));
    }

    public static String networkRef(Object plannedRoute) {
        Map<String, Object> planned = asPlannedMap(plannedRoute);
        return planned == null ? null : asText(planned.get("networkRef"));
    }

    /**
     * 确认路线未单独落 duration 时，用距离 + 巡检方式默认速度估算（与 ROUTE 阶段一致）。
     */
    public static Integer resolveDurationMinutes(Object plannedRoute, String patrolExecutionMode) {
        Map<String, Object> planned = asPlannedMap(plannedRoute);
        if (planned == null || planned.isEmpty()) {
            return null;
        }
        Integer workMinutes = asInteger(planned.get("workMinutes"));
        Long distance = asLong(planned.get("totalDistanceMeters"));
        String inspectionType = normalizeInspectionType(patrolExecutionMode);
        Integer speed = inspectionType == null ? null : DEFAULT_SPEED_M_PER_MIN.get(inspectionType);
        Integer travelCeil = null;
        if (distance != null && distance >= 0 && speed != null && speed > 0) {
            travelCeil = (int) Math.ceil(distance / (double) speed);
        }
        if (travelCeil == null) {
            return workMinutes;
        }
        return travelCeil + (workMinutes != null ? workMinutes : 0);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> asPlannedMap(Object raw) {
        if (raw == null) {
            return null;
        }
        if (raw instanceof Map<?, ?> map) {
            return new LinkedHashMap<>((Map<String, Object>) map);
        }
        if (raw instanceof String json && StringUtils.hasText(json)) {
            try {
                return OBJECT_MAPPER.readValue(json.trim(), new TypeReference<Map<String, Object>>() {
                });
            } catch (Exception ignored) {
                return null;
            }
        }
        return null;
    }

    static String asText(Object raw) {
        return raw == null ? null : String.valueOf(raw).trim();
    }

    static String normalizeInspectionType(String raw) {
        if (!StringUtils.hasText(raw)) {
            return null;
        }
        String key = raw.trim().toUpperCase(Locale.ROOT);
        if ("MANUAL".equals(key) || "HUMAN".equals(key)) {
            return "HUMAN";
        }
        if ("ROBOT".equals(key) || "GROUND_ROBOT".equals(key)) {
            return "GROUND_ROBOT";
        }
        if ("UAV".equals(key)) {
            return "UAV";
        }
        return key;
    }

    private static Integer asInteger(Object raw) {
        if (raw instanceof Number number) {
            return number.intValue();
        }
        if (raw instanceof String text && StringUtils.hasText(text)) {
            try {
                return Integer.parseInt(text.trim());
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private static Long asLong(Object raw) {
        if (raw instanceof Number number) {
            return number.longValue();
        }
        if (raw instanceof String text && StringUtils.hasText(text)) {
            try {
                return Long.parseLong(text.trim());
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    public static List<String> asStringList(Object raw) {
        if (raw == null) {
            return List.of();
        }
        if (raw instanceof List<?> list) {
            List<String> out = new ArrayList<>(list.size());
            for (Object item : list) {
                if (item != null && StringUtils.hasText(String.valueOf(item))) {
                    out.add(String.valueOf(item).trim());
                }
            }
            return out;
        }
        return List.of();
    }
}
