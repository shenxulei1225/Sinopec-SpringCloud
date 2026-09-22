package cn.cheers.x.inspection.inspection_content.service.orchestration.impl;

import cn.cheers.x.inspection.inspection_content.service.orchestration.PatrolSaveRouteService;
import cn.cheers.x.inspection.orchestration.dto.PatrolSaveRouteReqDTO;
import cn.cheers.x.inspection.orchestration.dto.PatrolSaveRouteRespDTO;
import cn.cheers.x.inspection.task.service.task.PatrolItemActionDurationSupport;
import cn.cheers.x.inspection.task.service.task.PatrolPlannedRouteSupport;
import cn.cheers.x.inspection.task.service.task.PatrolTaskEntityStore;
import cn.cheers.x.module.platform.contract.dto.work.WorkItemDTO;
import cn.cheers.x.module.platform.orchestration.route.RoutePayloadKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.cheers.x.inspection.inspection_content.enums.ErrorCodeConstants.PATROL_SAVE_ROUTE_FACILITY_ID_REQUIRED;
import static cn.cheers.x.inspection.inspection_content.enums.ErrorCodeConstants.PATROL_SAVE_ROUTE_PLANNED_ROUTE_REQUIRED;
import static cn.cheers.x.inspection.inspection_content.enums.ErrorCodeConstants.PATROL_SAVE_ROUTE_TASK_ID_REQUIRED;
import static cn.cheers.x.inspection.inspection_content.enums.ErrorCodeConstants.PATROL_SAVE_ROUTE_WORK_ITEMS_EMPTY;

/**
 * 保存路线：把算路阶段回写的 plannedRoute 写入总任务 FLD-TSK-027。
 */
@Service
@RequiredArgsConstructor
public class PatrolSaveRouteServiceImpl implements PatrolSaveRouteService {

    private final PatrolTaskEntityStore patrolTaskEntityStore;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PatrolSaveRouteRespDTO saveRoute(PatrolSaveRouteReqDTO request) {
        WorkItemDTO workItem = requireWorkItem(request);

        if (Boolean.TRUE.equals(request.getDryRun())) {
            requirePlannedRoute(workItem);
            return PatrolSaveRouteRespDTO.builder()
                    .taskId(request.getTaskId())
                    .dryRun(true)
                    .build();
        }

        if (request.getTaskId() == null) {
            throw exception(PATROL_SAVE_ROUTE_TASK_ID_REQUIRED);
        }
        if (request.getFacilityId() == null) {
            throw exception(PATROL_SAVE_ROUTE_FACILITY_ID_REQUIRED);
        }

        Map<String, Object> planned = requirePlannedRoute(workItem);
        PatrolItemActionDurationSupport.stripFromPlannedRoute(planned);
        patrolTaskEntityStore.writePlannedRoute(request.getTaskId(), planned);
        return PatrolSaveRouteRespDTO.builder()
                .taskId(request.getTaskId())
                .dryRun(false)
                .build();
    }

    private static WorkItemDTO requireWorkItem(PatrolSaveRouteReqDTO request) {
        if (request == null || CollectionUtils.isEmpty(request.getWorkItems())) {
            throw exception(PATROL_SAVE_ROUTE_WORK_ITEMS_EMPTY);
        }
        return request.getWorkItems().get(0);
    }

    private static Map<String, Object> requirePlannedRoute(WorkItemDTO workItem) {
        Map<String, Object> payload = workItem.getPayload();
        if (payload == null) {
            throw exception(PATROL_SAVE_ROUTE_PLANNED_ROUTE_REQUIRED);
        }
        Map<String, Object> planned = PatrolPlannedRouteSupport.asPlannedMap(payload.get(RoutePayloadKeys.PLANNED_ROUTE));
        if (planned == null || planned.isEmpty()) {
            throw exception(PATROL_SAVE_ROUTE_PLANNED_ROUTE_REQUIRED);
        }
        if (!PatrolPlannedRouteSupport.hasSavedRoute(planned)) {
            throw exception(PATROL_SAVE_ROUTE_PLANNED_ROUTE_REQUIRED);
        }
        return new LinkedHashMap<>(planned);
    }
}
