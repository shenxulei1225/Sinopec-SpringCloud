package cn.cheers.x.inspection.inspection_content.service.orchestration.impl;

import cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration.PatrolRouteRunReqVO;
import cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration.PatrolScheduleEnableReqVO;
import cn.cheers.x.inspection.inspection_content.service.orchestration.PatrolRouteFacadeService;
import cn.cheers.x.module.platform.contract.ContractVersions;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleRunRequest;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleRunResponse;
import cn.cheers.x.module.platform.contract.dto.schedule.SchedulingSpecDTO;
import cn.cheers.x.module.platform.contract.dto.work.WorkItemDTO;
import cn.cheers.x.module.platform.orchestration.api.ScheduleRunApi;
import cn.cheers.x.module.platform.orchestration.enums.OrchestrationRefs;
import cn.cheers.x.module.platform.orchestration.phase.OrchestrationPhase;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 巡检组路线门面：组装种子工作项并调用 {@link ScheduleRunApi}。
 */
@Service
@Validated
public class PatrolRouteFacadeServiceImpl implements PatrolRouteFacadeService {

    static final String FACILITY_ID = "facilityId";
    static final String OBJECT_IDS = "objectIds";
    static final String PREFERRED_NETWORK_REF = "preferredNetworkRef";
    static final String TASK_ID = "taskId";
    static final String FROM_CONFIRMED_SNAPSHOT = "fromConfirmedSnapshot";

    private static final SchedulingSpecDTO PLACEHOLDER_SCHEDULING_SPEC = SchedulingSpecDTO.builder()
            .mode("once")
            .conflictStrategy("none")
            .build();

    @Resource
    private ScheduleRunApi scheduleRunApi;

    @Override
    public ScheduleRunResponse previewRoute(PatrolRouteRunReqVO reqVO) {
        return scheduleRunApi.run(ScheduleRunRequest.builder()
                .contractVersion(ContractVersions.MVP)
                .orchestrationRef(OrchestrationRefs.PATROL_ROUTE_PREVIEW_V1)
                .dryRun(true)
                .stopAfterPhase(OrchestrationPhase.ROUTE.name())
                .schedulingSpec(PLACEHOLDER_SCHEDULING_SPEC)
                .workItems(List.of(seedWorkItem(reqVO, null, false)))
                .build()).getCheckedData();
    }

    @Override
    public ScheduleRunResponse confirmRoute(PatrolRouteRunReqVO reqVO) {
        return scheduleRunApi.run(ScheduleRunRequest.builder()
                .contractVersion(ContractVersions.MVP)
                .orchestrationRef(OrchestrationRefs.PATROL_ROUTE_CONFIRM_V1)
                .dryRun(false)
                .schedulingSpec(PLACEHOLDER_SCHEDULING_SPEC)
                .workItems(List.of(seedWorkItem(reqVO, null, false)))
                .build()).getCheckedData();
    }

    @Override
    public ScheduleRunResponse enableSchedule(PatrolScheduleEnableReqVO reqVO) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put(TASK_ID, reqVO.getTaskId());
        payload.put(FROM_CONFIRMED_SNAPSHOT, true);

        WorkItemDTO seed = WorkItemDTO.builder()
                .contractVersion(ContractVersions.MVP)
                .workId("patrol-enable-" + reqVO.getTaskId())
                .payload(payload)
                .build();

        return scheduleRunApi.run(ScheduleRunRequest.builder()
                .contractVersion(ContractVersions.MVP)
                .orchestrationRef(OrchestrationRefs.PATROL_SCHEDULE_ENABLE_V1)
                .dryRun(false)
                .schedulingSpec(reqVO.getSchedulingSpec())
                .workItems(List.of(seed))
                .build()).getCheckedData();
    }

    private static WorkItemDTO seedWorkItem(PatrolRouteRunReqVO reqVO, Long taskId, boolean fromConfirmedSnapshot) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put(FACILITY_ID, reqVO.getFacilityId());
        payload.put(OBJECT_IDS, reqVO.getObjectIds());
        if (reqVO.getPreferredNetworkRef() != null) {
            payload.put(PREFERRED_NETWORK_REF, reqVO.getPreferredNetworkRef());
        }
        if (taskId != null) {
            payload.put(TASK_ID, taskId);
        }
        payload.put(FROM_CONFIRMED_SNAPSHOT, fromConfirmedSnapshot);

        return WorkItemDTO.builder()
                .contractVersion(ContractVersions.MVP)
                .workId("patrol-seed-" + UUID.randomUUID())
                .payload(payload)
                .build();
    }
}
