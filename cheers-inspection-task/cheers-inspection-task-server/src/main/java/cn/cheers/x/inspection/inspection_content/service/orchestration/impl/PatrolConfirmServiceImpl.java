package cn.cheers.x.inspection.inspection_content.service.orchestration.impl;

import cn.cheers.x.inspection.inspection_content.dal.dataobject.route.InspectionRoutePlanDO;
import cn.cheers.x.inspection.inspection_content.dal.mysql.route.InspectionRoutePlanMapper;
import cn.cheers.x.inspection.inspection_content.service.orchestration.PatrolConfirmService;
import cn.cheers.x.inspection.orchestration.dto.PatrolConfirmReqDTO;
import cn.cheers.x.inspection.orchestration.dto.PatrolConfirmRespDTO;
import cn.cheers.x.inspection.task.dal.dataobject.task.InspectionTaskDO;
import cn.cheers.x.inspection.task.dal.mysql.task.InspectionTaskMapper;
import cn.cheers.x.module.platform.contract.dto.work.WorkItemDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.cheers.x.inspection.inspection_content.enums.ErrorCodeConstants.PATROL_CONFIRM_DURATION_REQUIRED;
import static cn.cheers.x.inspection.inspection_content.enums.ErrorCodeConstants.PATROL_CONFIRM_FACILITY_ID_REQUIRED;
import static cn.cheers.x.inspection.inspection_content.enums.ErrorCodeConstants.PATROL_CONFIRM_INSPECTION_TYPE_REQUIRED;
import static cn.cheers.x.inspection.inspection_content.enums.ErrorCodeConstants.PATROL_CONFIRM_NETWORK_REF_REQUIRED;
import static cn.cheers.x.inspection.inspection_content.enums.ErrorCodeConstants.PATROL_CONFIRM_PLANNED_ROUTE_REQUIRED;
import static cn.cheers.x.inspection.inspection_content.enums.ErrorCodeConstants.PATROL_CONFIRM_TASK_ID_REQUIRED;
import static cn.cheers.x.inspection.inspection_content.enums.ErrorCodeConstants.PATROL_CONFIRM_TASK_NOT_FOUND;
import static cn.cheers.x.inspection.inspection_content.enums.ErrorCodeConstants.PATROL_CONFIRM_WORK_ITEMS_EMPTY;

/**
 * 确认路线：校验工作项快照，dryRun 不写库，否则写入路线方案并更新任务。
 */
@Service
@RequiredArgsConstructor
public class PatrolConfirmServiceImpl implements PatrolConfirmService {

    static final String NETWORK_REF = "networkRef";
    static final String STOP_IDS = "stopIds";
    static final String INSPECTION_TYPE = "inspectionType";
    static final String PLANNED_ROUTE = "plannedRoute";

    private final InspectionRoutePlanMapper routePlanMapper;
    private final InspectionTaskMapper taskMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PatrolConfirmRespDTO confirm(PatrolConfirmReqDTO request) {
        WorkItemDTO workItem = resolvePrimaryWorkItem(request);
        ConfirmSnapshot snapshot = extractSnapshot(workItem);
        validateSnapshot(snapshot);

        if (Boolean.TRUE.equals(request.getDryRun())) {
            return PatrolConfirmRespDTO.builder()
                    .taskId(request.getTaskId())
                    .dryRun(true)
                    .build();
        }

        if (request.getTaskId() == null) {
            throw exception(PATROL_CONFIRM_TASK_ID_REQUIRED);
        }
        Long facilityId = request.getFacilityId();
        if (facilityId == null) {
            throw exception(PATROL_CONFIRM_FACILITY_ID_REQUIRED);
        }

        InspectionTaskDO task = taskMapper.selectById(request.getTaskId());
        if (task == null) {
            throw exception(PATROL_CONFIRM_TASK_NOT_FOUND);
        }

        InspectionRoutePlanDO plan = new InspectionRoutePlanDO();
        plan.setFacilityId(facilityId);
        plan.setName(resolvePlanName(request));
        plan.setNetworkRef(snapshot.networkRef());
        plan.setInspectionType(snapshot.inspectionType());
        plan.setStopIds(writeJson(snapshot.stopIds()));
        plan.setPlannedRoute(writePlannedRoute(snapshot.plannedRoute()));
        plan.setDurationEstimateMinutes(snapshot.durationEstimateMinutes());
        plan.setTaskId(request.getTaskId());
        routePlanMapper.insert(plan);

        task.setRoutePlanId(plan.getId());
        task.setNetworkRef(snapshot.networkRef());
        task.setPlannedRoute(plan.getPlannedRoute());
        task.setDurationEstimateMinutes(snapshot.durationEstimateMinutes());
        task.setInspectionType(snapshot.inspectionType());
        taskMapper.updateById(task);

        return PatrolConfirmRespDTO.builder()
                .routePlanId(plan.getId())
                .taskId(request.getTaskId())
                .dryRun(false)
                .build();
    }

    private static WorkItemDTO resolvePrimaryWorkItem(PatrolConfirmReqDTO request) {
        if (request == null || CollectionUtils.isEmpty(request.getWorkItems())) {
            throw exception(PATROL_CONFIRM_WORK_ITEMS_EMPTY);
        }
        return request.getWorkItems().get(0);
    }

    private ConfirmSnapshot extractSnapshot(WorkItemDTO workItem) {
        Map<String, Object> payload = workItem.getPayload();
        if (payload == null) {
            throw exception(PATROL_CONFIRM_PLANNED_ROUTE_REQUIRED);
        }
        return new ConfirmSnapshot(
                workItem.getDurationEstimateMinutes(),
                payload.get(PLANNED_ROUTE),
                asString(payload.get(NETWORK_REF)),
                asString(payload.get(INSPECTION_TYPE)),
                asStringList(payload.get(STOP_IDS)));
    }

    private static void validateSnapshot(ConfirmSnapshot snapshot) {
        if (snapshot.durationEstimateMinutes() == null) {
            throw exception(PATROL_CONFIRM_DURATION_REQUIRED);
        }
        if (snapshot.plannedRoute() == null) {
            throw exception(PATROL_CONFIRM_PLANNED_ROUTE_REQUIRED);
        }
        if (!StringUtils.hasText(snapshot.networkRef())) {
            throw exception(PATROL_CONFIRM_NETWORK_REF_REQUIRED);
        }
        if (!StringUtils.hasText(snapshot.inspectionType())) {
            throw exception(PATROL_CONFIRM_INSPECTION_TYPE_REQUIRED);
        }
    }

    private static String resolvePlanName(PatrolConfirmReqDTO request) {
        if (StringUtils.hasText(request.getName())) {
            return request.getName().trim();
        }
        return "patrol-route-task-" + request.getTaskId();
    }

    private String writePlannedRoute(Object plannedRoute) {
        if (plannedRoute instanceof String str) {
            if (!StringUtils.hasText(str)) {
                throw exception(PATROL_CONFIRM_PLANNED_ROUTE_REQUIRED);
            }
            return str;
        }
        try {
            return objectMapper.writeValueAsString(plannedRoute);
        } catch (JsonProcessingException ex) {
            throw exception(PATROL_CONFIRM_PLANNED_ROUTE_REQUIRED);
        }
    }

    private String writeJson(List<String> values) {
        try {
            return objectMapper.writeValueAsString(values != null ? values : List.of());
        } catch (JsonProcessingException ex) {
            return "[]";
        }
    }

    private static String asString(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    @SuppressWarnings("unchecked")
    private static List<String> asStringList(Object raw) {
        if (raw == null) {
            return List.of();
        }
        if (raw instanceof List<?> list) {
            List<String> out = new ArrayList<>(list.size());
            for (Object item : list) {
                if (item != null) {
                    out.add(String.valueOf(item));
                }
            }
            return out;
        }
        return List.of();
    }

    private record ConfirmSnapshot(
            Integer durationEstimateMinutes,
            Object plannedRoute,
            String networkRef,
            String inspectionType,
            List<String> stopIds) {
    }
}
