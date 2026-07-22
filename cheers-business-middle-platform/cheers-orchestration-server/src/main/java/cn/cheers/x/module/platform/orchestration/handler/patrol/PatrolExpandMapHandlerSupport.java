package cn.cheers.x.module.platform.orchestration.handler.patrol;

import cn.cheers.x.inspection.orchestration.dto.PatrolExpandReqDTO;
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
 * 从排程种子工作项 payload 解析 PatrolExpandReqDTO。
 */
final class PatrolExpandMapHandlerSupport {

    static final String FACILITY_ID = "facilityId";
    static final String OBJECT_IDS = "objectIds";
    static final String PREFERRED_NETWORK_REF = "preferredNetworkRef";
    static final String TASK_ID = "taskId";
    static final String FROM_CONFIRMED_SNAPSHOT = "fromConfirmedSnapshot";
    static final String START_STOP_ID = "startStopId";
    static final String RETURN_TO_START = "returnToStart";

    private PatrolExpandMapHandlerSupport() {
    }

    static PatrolExpandReqDTO toReq(PhaseContext context) {
        WorkItemDTO seed = resolveSeedWorkItem(context);
        Map<String, Object> payload = seed.getPayload();
        if (payload == null) {
            throw exception(SCHEDULE_RUN_WORK_ITEMS_EMPTY);
        }
        return PatrolExpandReqDTO.builder()
                .facilityId(asLong(payload.get(FACILITY_ID)))
                .objectIds(asLongList(payload.get(OBJECT_IDS)))
                .preferredNetworkRef(asString(payload.get(PREFERRED_NETWORK_REF)))
                .taskId(asLong(payload.get(TASK_ID)))
                .fromConfirmedSnapshot(asBoolean(payload.get(FROM_CONFIRMED_SNAPSHOT)))
                .seedWorkId(seed.getWorkId())
                .startStopId(asString(payload.get(START_STOP_ID)))
                .returnToStart(asBoolean(payload.get(RETURN_TO_START)))
                .build();
    }

    private static WorkItemDTO resolveSeedWorkItem(PhaseContext context) {
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
}
