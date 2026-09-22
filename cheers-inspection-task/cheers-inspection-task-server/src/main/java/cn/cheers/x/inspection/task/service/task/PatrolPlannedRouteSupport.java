package cn.cheers.x.inspection.task.service.task;

import cn.cheers.x.module.platform.orchestration.route.RoutePayloadKeys;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 读总任务 FLD-TSK-027 上的路线快照；与详情回显、编排 expand 口径一致。
 * <p>第 2 步 saveRoute 只写 stopIds / networkRef / {@link RoutePayloadKeys#ESTIMATED_TRAVEL_DURATION} 等。
 * 检查项动作耗时不在本快照，见巡检内容 {@code itemActionDurationMinutes}。
 */
public final class PatrolPlannedRouteSupport {

    private static final Map<String, Integer> DEFAULT_SPEED_M_PER_MIN = Map.of(
            "HUMAN", 60,
            "GROUND_ROBOT", 40,
            "UAV", 80);

    private PatrolPlannedRouteSupport() {
    }

    public static boolean hasSavedRoute(Object plannedRoute) {
        Map<String, Object> planned = asPlannedMap(plannedRoute);
        if (planned == null || planned.isEmpty()) {
            return false;
        }
        return !CollectionUtils.isEmpty(asStringList(planned.get(RoutePayloadKeys.STOP_IDS)));
    }

    public static boolean hasEstimatedActionDuration(Object plannedRoute) {
        Integer value = estimatedActionDuration(plannedRoute);
        return value != null && value > 0;
    }

    public static String networkRef(Object plannedRoute) {
        Map<String, Object> planned = asPlannedMap(plannedRoute);
        return planned == null ? null : asText(planned.get(RoutePayloadKeys.NETWORK_REF));
    }

    public static Integer estimatedActionDuration(Object plannedRoute) {
        Map<String, Object> planned = asPlannedMap(plannedRoute);
        return planned == null ? null : asInteger(planned.get(RoutePayloadKeys.ESTIMATED_ACTION_DURATION));
    }

    public static Integer estimatedTravelDuration(Object plannedRoute) {
        Map<String, Object> planned = asPlannedMap(plannedRoute);
        return planned == null ? null : asInteger(planned.get(RoutePayloadKeys.ESTIMATED_TRAVEL_DURATION));
    }

    /**
     * 只算路径耗时：已写下的行驶分钟，没有则按距离估。不含检查项动作耗时。
     */
    public static Integer resolveTravelMinutesOnly(Object plannedRoute, String patrolExecutionMode) {
        Integer travel = estimatedTravelDuration(plannedRoute);
        if (travel != null) {
            return travel;
        }
        Map<String, Object> planned = asPlannedMap(plannedRoute);
        if (planned == null || planned.isEmpty()) {
            return null;
        }
        return estimateTravelMinutesFromDistance(planned, patrolExecutionMode);
    }

    private static Integer estimateTravelMinutesFromDistance(Map<String, Object> planned, String patrolExecutionMode) {
        Long distance = asLong(planned.get("totalDistanceMeters"));
        String inspectionType = normalizeInspectionType(patrolExecutionMode);
        Integer speed = inspectionType == null ? null : DEFAULT_SPEED_M_PER_MIN.get(inspectionType);
        if (distance == null || distance < 0 || speed == null || speed <= 0) {
            return null;
        }
        return (int) Math.ceil(distance / (double) speed);
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
                return new com.fasterxml.jackson.databind.ObjectMapper()
                        .readValue(json.trim(), new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {
                        });
            } catch (Exception ignored) {
                return null;
            }
        }
        return null;
    }

    public static String asText(Object raw) {
        return raw == null ? null : String.valueOf(raw).trim();
    }

    public static String normalizeInspectionType(String raw) {
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
