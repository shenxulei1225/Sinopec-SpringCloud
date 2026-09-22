package cn.cheers.x.module.platform.orchestration.handler.patrol;

import cn.cheers.x.inspection.orchestration.dto.PatrolScheduleMapReqDTO;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleRunRequest;
import cn.cheers.x.module.platform.contract.dto.work.WorkItemDTO;
import cn.cheers.x.module.platform.orchestration.phase.PhaseContext;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.cheers.x.module.platform.orchestration.enums.ErrorCodeConstants.SCHEDULE_RUN_WORK_ITEMS_EMPTY;

/**
 * 从排程模板作业项 payload 解析 PatrolScheduleMapReqDTO。
 */
final class PatrolScheduleMapHandlerSupport {

    static final String FACILITY_ID = "facilityId";
    static final String OBJECT_IDS = "objectIds";
    static final String PREFERRED_NETWORK_REF = "preferredNetworkRef";
    static final String TASK_ID = "taskId";
    static final String FROM_SAVED_ROUTE_SNAPSHOT = "fromSavedRouteSnapshot";
    static final String START_STOP_ID = "startStopId";
    static final String END_STOP_ID = "endStopId";
    static final String RETURN_TO_START = "returnToStart";
    static final String STOP_IDS = "stopIds";
    static final String INSPECTION_TYPE = "inspectionType";

    private PatrolScheduleMapHandlerSupport() {
    }

    static PatrolScheduleMapReqDTO toReq(PhaseContext context) {
        WorkItemDTO template = resolveTemplateWorkItem(context);
        Map<String, Object> payload = template.getPayload();
        if (payload == null) {
            throw exception(SCHEDULE_RUN_WORK_ITEMS_EMPTY);
        }
        PatrolScheduleMapReqDTO req = new PatrolScheduleMapReqDTO();
        req.setFacilityId(asLong(payload.get(FACILITY_ID)));
        req.setObjectIds(asLongList(payload.get(OBJECT_IDS)));
        req.setPreferredNetworkRef(asString(payload.get(PREFERRED_NETWORK_REF)));
        req.setTaskId(asLong(payload.get(TASK_ID)));
        req.setFromSavedRouteSnapshot(asBoolean(payload.get(FROM_SAVED_ROUTE_SNAPSHOT)));
        req.setTemplateWorkItemId(template.getWorkId());
        req.setStartStopId(asString(payload.get(START_STOP_ID)));
        req.setEndStopId(asString(payload.get(END_STOP_ID)));
        req.setReturnToStart(asBoolean(payload.get(RETURN_TO_START)));
        req.setStopIds(asStringList(payload.get(STOP_IDS)));
        req.setInspectionType(asString(payload.get(INSPECTION_TYPE)));
        return req;
    }

    private static WorkItemDTO resolveTemplateWorkItem(PhaseContext context) {
        List<WorkItemDTO> fromContext = context.getWorkItems();
        if (!CollectionUtils.isEmpty(fromContext)) {
            return fromContext.get(0);
        }
        ScheduleRunRequest request = context.getRequest();
        if (request != null && !CollectionUtils.isEmpty(request.getWorkItems())) {
            return request.getWorkItems().get(0);
        }
        throw exception(SCHEDULE_RUN_WORK_ITEMS_EMPTY);
    }

    private static String asString(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private static Long asLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.parseLong(String.valueOf(value));
    }

    private static Boolean asBoolean(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Boolean bool) {
            return bool;
        }
        return Boolean.parseBoolean(String.valueOf(value));
    }

    @SuppressWarnings("unchecked")
    private static List<Long> asLongList(Object value) {
        if (value == null) {
            return null;
        }
        if (!(value instanceof List<?> list)) {
            return null;
        }
        List<Long> out = new ArrayList<>(list.size());
        for (Object item : list) {
            if (item == null) {
                continue;
            }
            if (item instanceof Number number) {
                out.add(number.longValue());
            } else {
                out.add(Long.parseLong(String.valueOf(item)));
            }
        }
        return out;
    }

    @SuppressWarnings("unchecked")
    private static List<String> asStringList(Object value) {
        if (value == null) {
            return null;
        }
        if (!(value instanceof List<?> list)) {
            return null;
        }
        List<String> out = new ArrayList<>(list.size());
        for (Object item : list) {
            if (item == null) {
                continue;
            }
            String text = String.valueOf(item).trim();
            if (!text.isEmpty()) {
                out.add(text);
            }
        }
        return out;
    }
}
