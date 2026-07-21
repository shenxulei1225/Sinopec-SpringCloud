package cn.cheers.x.module.platform.orchestration.handler.patrol;

import cn.cheers.x.inspection.orchestration.dto.PatrolConfirmReqDTO;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleRunRequest;
import cn.cheers.x.module.platform.contract.dto.work.WorkItemDTO;
import cn.cheers.x.module.platform.orchestration.phase.PhaseContext;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.cheers.x.module.platform.orchestration.enums.ErrorCodeConstants.SCHEDULE_RUN_WORK_ITEMS_EMPTY;

/**
 * 从排程上下文解析 PatrolConfirmReqDTO。
 */
final class PatrolConfirmHandlerSupport {

    static final String FACILITY_ID = "facilityId";
    static final String TASK_ID = "taskId";
    static final String NAME = "name";

    private PatrolConfirmHandlerSupport() {
    }

    static PatrolConfirmReqDTO toReq(PhaseContext context) {
        List<WorkItemDTO> workItems = context.getWorkItems();
        if (CollectionUtils.isEmpty(workItems)) {
            throw exception(SCHEDULE_RUN_WORK_ITEMS_EMPTY);
        }
        Map<String, Object> seedPayload = resolveSeedPayload(context);
        return PatrolConfirmReqDTO.builder()
                .taskId(asLong(seedPayload.get(TASK_ID)))
                .facilityId(asLong(seedPayload.get(FACILITY_ID)))
                .name(asString(seedPayload.get(NAME)))
                .workItems(workItems)
                .dryRun(context.isDryRun())
                .build();
    }

    private static Map<String, Object> resolveSeedPayload(PhaseContext context) {
        ScheduleRunRequest request = context.getRequest();
        if (request != null && !CollectionUtils.isEmpty(request.getWorkItems())) {
            WorkItemDTO seed = request.getWorkItems().get(0);
            if (seed.getPayload() != null) {
                return seed.getPayload();
            }
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
}
