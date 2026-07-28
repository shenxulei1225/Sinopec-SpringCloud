package cn.cheers.x.module.platform.orchestration.service;

import cn.cheers.x.maintenance.api.MaintenanceApi;
import cn.cheers.x.maintenance.api.dto.BindingResolveReqDTO;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleRunRequest;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleRunResponse;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.cheers.x.module.platform.orchestration.enums.ErrorCodeConstants.SCHEDULE_DISPATCH_STANDARD_REQUIRED;
import static cn.cheers.x.module.platform.orchestration.enums.ErrorCodeConstants.SCHEDULE_RUN_WORK_OR_SOURCE_REQUIRED;

@Service
public class ScheduleOrchestrationServiceImpl implements ScheduleOrchestrationService {

    @Resource
    private OrchestrationRunner orchestrationRunner;
    @Resource
    private MaintenanceApi maintenanceApi;

    @Override
    public ScheduleRunResponse runSchedule(ScheduleRunRequest request, Long facilityId) {
        validateBasic(request);
        validateDispatchPreconditions(request);
        return orchestrationRunner.run(request, facilityId);
    }

    private void validateBasic(ScheduleRunRequest request) {
        if (request == null) {
            throw exception(SCHEDULE_RUN_WORK_OR_SOURCE_REQUIRED);
        }
        boolean hasWorkItems = !CollectionUtils.isEmpty(request.getWorkItems());
        boolean hasSourceInstances = !CollectionUtils.isEmpty(request.getSourceInstances());
        if (!hasWorkItems && !hasSourceInstances) {
            throw exception(SCHEDULE_RUN_WORK_OR_SOURCE_REQUIRED);
        }
    }

    /**
     * 派工前置校验：必须在 runtime 落库之前完成，避免“已 persist 却因缺标准失败”。
     * 显式标准 id，或具备可绑定解析的 scope（绑定失败将在派工阶段抛出）。
     */
    private void validateDispatchPreconditions(ScheduleRunRequest request) {
        if (!Boolean.TRUE.equals(request.getDispatchWorkOrders())) {
            return;
        }
        if (request.getFieldWorkStandardId() != null) {
            return;
        }
        if (!StringUtils.hasText(request.getScope())) {
            throw exception(SCHEDULE_DISPATCH_STANDARD_REQUIRED);
        }
        resolveDispatchStandardId(request, request.getScope());
    }

    private Long resolveDispatchStandardId(ScheduleRunRequest request, String scope) {
        if (request.getFieldWorkStandardId() != null) {
            return request.getFieldWorkStandardId();
        }
        return maintenanceApi.resolveBinding(BindingResolveReqDTO.builder()
                .assetId(request.getAssetId())
                .assetTypeCode(request.getAssetTypeCode())
                .frequencyCode(request.getFrequencyCode())
                .scope(scope)
                .build()).getCheckedData().getFieldStandardId();
    }
}
