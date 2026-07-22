package cn.cheers.x.module.platform.orchestration.phase;

import cn.cheers.x.module.platform.contract.dto.route.RoutePreviewDTO;
import cn.cheers.x.module.platform.contract.dto.route.RouteRequestDTO;
import cn.cheers.x.module.platform.contract.dto.work.WorkItemDTO;
import cn.cheers.x.module.platform.orchestration.route.RoutePayloadKeys;
import cn.cheers.x.module.platform.routing.api.RoutePlanApi;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.cheers.x.module.platform.orchestration.enums.ErrorCodeConstants.ORCHESTRATION_PAYLOAD_INVALID;
import static cn.cheers.x.module.platform.orchestration.enums.ErrorCodeConstants.ORCHESTRATION_ROUTE_DURATION_UNAVAILABLE;
import static cn.cheers.x.module.platform.orchestration.enums.ErrorCodeConstants.SCHEDULE_RUN_WORK_ITEMS_EMPTY;

/**
 * 内建 ROUTE 阶段：调用路径规划，回写预览与工作项时长。
 */
@Component
public class BuiltinRoutePhaseHandler implements PhaseHandler {

    public static final String HANDLER_ID = "platform.route.plan_v1";

    private static final Map<String, Integer> DEFAULT_SPEED_M_PER_MIN = Map.of(
            "HUMAN", 60,
            "GROUND_ROBOT", 40,
            "UAV", 80);

    private static final Map<String, String> INSPECTION_TO_MOBILITY = Map.of(
            "HUMAN", "person_walk",
            "GROUND_ROBOT", "ground_robot",
            "UAV", "uav_low");

    @Resource
    private RoutePlanApi routePlanApi;

    @Override
    public String handlerId() {
        return HANDLER_ID;
    }

    @Override
    public void execute(PhaseContext context) {
        List<WorkItemDTO> workItems = context.getWorkItems();
        if (CollectionUtils.isEmpty(workItems)) {
            throw exception(SCHEDULE_RUN_WORK_ITEMS_EMPTY);
        }
        RoutePreviewDTO lastPreview = null;
        for (WorkItemDTO item : workItems) {
            lastPreview = planOne(item);
        }
        context.setRoutePreview(lastPreview);
    }

    private RoutePreviewDTO planOne(WorkItemDTO item) {
        Map<String, Object> payload = item.getPayload();
        if (payload == null) {
            throw exception(ORCHESTRATION_PAYLOAD_INVALID);
        }
        String networkRef = asString(payload.get(RoutePayloadKeys.NETWORK_REF));
        List<String> stopIds = asStringList(payload.get(RoutePayloadKeys.STOP_IDS));
        if (!StringUtils.hasText(networkRef) || CollectionUtils.isEmpty(stopIds)) {
            throw exception(ORCHESTRATION_PAYLOAD_INVALID);
        }

        String inspectionType = normalizeInspectionType(asString(payload.get(RoutePayloadKeys.INSPECTION_TYPE)));
        String mobilityProfileId = INSPECTION_TO_MOBILITY.getOrDefault(
                inspectionType, INSPECTION_TO_MOBILITY.get("HUMAN"));

        RouteRequestDTO request = RouteRequestDTO.builder()
                .networkRef(networkRef.trim())
                .stopIds(stopIds)
                .mobilityProfileId(mobilityProfileId)
                .strategy(asString(payload.get(RoutePayloadKeys.STRATEGY)))
                .startStopId(asString(payload.get(RoutePayloadKeys.START_STOP_ID)))
                .returnToStart(asBoolean(payload.get(RoutePayloadKeys.RETURN_TO_START)))
                .build();

        RoutePreviewDTO preview = routePlanApi.plan(request).getCheckedData();
        if (preview == null) {
            throw exception(ORCHESTRATION_PAYLOAD_INVALID);
        }

        Integer workMinutes = asInteger(payload.get(RoutePayloadKeys.WORK_MINUTES));
        Integer speed = resolveSpeedMetersPerMinute(payload, inspectionType);
        item.setDurationEstimateMinutes(estimateDurationMinutes(preview.getTotalDistanceMeters(), speed, workMinutes));

        Map<String, Object> planned = new LinkedHashMap<>();
        planned.put("networkRef", preview.getNetworkRef() != null ? preview.getNetworkRef() : networkRef);
        planned.put("totalDistanceMeters", preview.getTotalDistanceMeters());
        planned.put("mobilityProfileId", mobilityProfileId);
        planned.put("stopIds", stopIds);
        String startStopId = asString(payload.get(RoutePayloadKeys.START_STOP_ID));
        if (StringUtils.hasText(startStopId)) {
            planned.put(RoutePayloadKeys.START_STOP_ID, startStopId);
        }
        Boolean returnToStart = asBoolean(payload.get(RoutePayloadKeys.RETURN_TO_START));
        if (returnToStart != null) {
            planned.put(RoutePayloadKeys.RETURN_TO_START, returnToStart);
        }
        if (preview.getDecisionTraceId() != null) {
            planned.put("decisionTraceId", preview.getDecisionTraceId());
        }
        payload.put(RoutePayloadKeys.PLANNED_ROUTE, planned);
        item.setPayload(payload);
        return preview;
    }

    static int estimateDurationMinutes(Long totalDistanceMeters, Integer speedMetersPerMinute,
                                       Integer workMinutes) {
        Integer travelCeil = null;
        if (totalDistanceMeters != null && totalDistanceMeters >= 0
                && speedMetersPerMinute != null && speedMetersPerMinute > 0) {
            travelCeil = (int) Math.ceil(totalDistanceMeters / (double) speedMetersPerMinute);
        }
        if (travelCeil == null) {
            if (workMinutes == null) {
                throw exception(ORCHESTRATION_ROUTE_DURATION_UNAVAILABLE);
            }
            return workMinutes;
        }
        return travelCeil + (workMinutes != null ? workMinutes : 0);
    }

    private static Integer resolveSpeedMetersPerMinute(Map<String, Object> payload, String inspectionType) {
        Integer override = asInteger(payload.get(RoutePayloadKeys.SPEED_METERS_PER_MINUTE));
        if (override != null && override > 0) {
            return override;
        }
        if (!StringUtils.hasText(inspectionType)) {
            return null;
        }
        return DEFAULT_SPEED_M_PER_MIN.get(inspectionType);
    }

    private static String normalizeInspectionType(String raw) {
        if (!StringUtils.hasText(raw)) {
            return null;
        }
        return raw.trim().toUpperCase(Locale.ROOT);
    }

    private static String asString(Object v) {
        return v == null ? null : String.valueOf(v);
    }

    private static Boolean asBoolean(Object v) {
        if (v == null) {
            return null;
        }
        if (v instanceof Boolean b) {
            return b;
        }
        return Boolean.parseBoolean(String.valueOf(v));
    }

    private static Integer asInteger(Object v) {
        if (v == null) {
            return null;
        }
        if (v instanceof Number n) {
            return n.intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(v));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private static List<String> asStringList(Object v) {
        if (v == null) {
            return null;
        }
        if (v instanceof List<?> list) {
            List<String> out = new ArrayList<>(list.size());
            for (Object o : list) {
                if (o != null) {
                    out.add(String.valueOf(o));
                }
            }
            return out;
        }
        return null;
    }
}
