package cn.cheers.x.module.platform.orchestration.service;

import cn.cheers.x.module.platform.capability.api.MappingProfileApi;
import cn.cheers.x.module.platform.capability.api.ProcessCapabilityBindingApi;
import cn.cheers.x.module.platform.capability.api.dto.ProcessCapabilityBindingRespDTO;
import cn.cheers.x.module.platform.capability.api.dto.ResolveWorkItemsReqDTO;
import cn.cheers.x.module.platform.contract.dto.work.SourceInstanceRefDTO;
import cn.cheers.x.module.platform.contract.ContractVersions;
import cn.cheers.x.module.platform.contract.dto.runtime.RuntimeJobDTO;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleRunRequest;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleRunResponse;
import cn.cheers.x.module.platform.contract.dto.schedule.SchedulingSpecDTO;
import cn.cheers.x.module.platform.contract.dto.slot.ScheduleSlotDTO;
import cn.cheers.x.module.platform.contract.dto.work.WorkItemDTO;
import cn.cheers.x.module.platform.contract.enums.RuntimeJobStatus;
import cn.cheers.x.module.platform.orchestration.enums.OrchestrationRefs;
import cn.cheers.x.module.platform.policy.api.PolicyResolveApi;
import cn.cheers.x.module.platform.policy.api.dto.PolicyResolveForRunReqDTO;
import cn.cheers.x.module.platform.policy.api.dto.PolicyRunContextDTO;
import cn.cheers.x.module.platform.policy.api.dto.PolicySnapshotRespDTO;
import cn.cheers.x.module.platform.runtime.api.RuntimePersistApi;
import cn.cheers.x.module.platform.runtime.api.dto.RuntimePersistReqDTO;
import cn.cheers.x.module.platform.scheduling.engine.SchedulingEngine;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static cn.cheers.x.module.platform.orchestration.enums.ErrorCodeConstants.SCHEDULE_RUN_MAPPING_PROFILE_REQUIRED;
import static cn.cheers.x.module.platform.orchestration.enums.ErrorCodeConstants.SCHEDULE_RUN_ORCHESTRATION_UNKNOWN;
import static cn.cheers.x.module.platform.orchestration.enums.ErrorCodeConstants.SCHEDULE_RUN_SCHEDULING_SPEC_REQUIRED;
import static cn.cheers.x.module.platform.orchestration.enums.ErrorCodeConstants.SCHEDULE_RUN_WORK_OR_SOURCE_REQUIRED;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;

@Service
public class ScheduleOrchestrationServiceImpl implements ScheduleOrchestrationService {

    @Resource
    private SchedulingEngine schedulingEngine;
    @Resource
    private RuntimePersistApi runtimePersistApi;
    @Resource
    private PolicyResolveApi policyResolveApi;
    @Resource
    private ProcessCapabilityBindingApi processCapabilityBindingApi;
    @Resource
    private MappingProfileApi mappingProfileApi;

    @Override
    public ScheduleRunResponse runSchedule(ScheduleRunRequest request, Long siteId) {
        validateBasic(request);
        RunContext ctx = resolveRunContext(request);
        List<WorkItemDTO> workItems = resolveWorkItems(request);

        // validate 阶段：MVP 跳过
        List<WorkItemDTO> expanded = expand(workItems);

        String runtimeJobId = UUID.randomUUID().toString();
        List<ScheduleSlotDTO> slots = schedulingEngine.solve(expanded, ctx.schedulingSpec(), runtimeJobId);
        if (StringUtils.hasText(ctx.policySnapshotId())) {
            for (ScheduleSlotDTO slot : slots) {
                slot.setPolicySnapshotId(ctx.policySnapshotId());
            }
        }

        RuntimeJobDTO job = RuntimeJobDTO.builder()
                .contractVersion(ContractVersions.MVP)
                .runtimeJobId(runtimeJobId)
                .businessTypeCode(request.getBusinessTypeCode())
                .triggerAction("schedule.run")
                .status(RuntimeJobStatus.SCHEDULED)
                .sourceWorkIds(expanded.stream().map(WorkItemDTO::getWorkId).collect(Collectors.toList()))
                .policySnapshotId(ctx.policySnapshotId())
                .orchestrationRef(ctx.orchestrationRef())
                .createdAt(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(OffsetDateTime.now()))
                .build();

        runtimePersistApi.persist(RuntimePersistReqDTO.builder()
                .job(job)
                .slots(slots)
                .siteId(siteId)
                .build()).checkError();

        return ScheduleRunResponse.builder()
                .contractVersion(ContractVersions.MVP)
                .runtimeJobId(runtimeJobId)
                .status(RuntimeJobStatus.SCHEDULED)
                .slots(slots)
                .decisionTraceId(null)
                .plainSummary(buildSummary(slots, ctx.schedulingSpec()))
                .build();
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

    private List<WorkItemDTO> resolveWorkItems(ScheduleRunRequest request) {
        if (!CollectionUtils.isEmpty(request.getWorkItems())) {
            return request.getWorkItems();
        }
        ProcessCapabilityBindingRespDTO binding = getPublishedBindingQuietly(request.getBusinessTypeCode());
        String mappingProfileId = firstMappingProfileId(binding);
        if (!StringUtils.hasText(mappingProfileId)) {
            throw exception(SCHEDULE_RUN_MAPPING_PROFILE_REQUIRED);
        }
        List<ResolveWorkItemsReqDTO.SourceInstanceInputDTO> instances = request.getSourceInstances().stream()
                .map(this::toSourceInstanceInput)
                .collect(Collectors.toList());
        return mappingProfileApi.resolveWorkItems(mappingProfileId, ResolveWorkItemsReqDTO.builder()
                .businessTypeCode(request.getBusinessTypeCode())
                .instances(instances)
                .build()).getCheckedData();
    }

    private ResolveWorkItemsReqDTO.SourceInstanceInputDTO toSourceInstanceInput(SourceInstanceRefDTO ref) {
        return ResolveWorkItemsReqDTO.SourceInstanceInputDTO.builder()
                .sourceInstanceId(ref.getSourceInstanceId())
                .customFields(ref.getCustomFields())
                .build();
    }

    private String firstMappingProfileId(ProcessCapabilityBindingRespDTO binding) {
        if (binding == null || CollectionUtils.isEmpty(binding.getMappingProfileIds())) {
            return null;
        }
        return binding.getMappingProfileIds().get(0);
    }

    private RunContext resolveRunContext(ScheduleRunRequest request) {
        ProcessCapabilityBindingRespDTO binding = getPublishedBindingQuietly(request.getBusinessTypeCode());

        String orchestrationRef = request.getOrchestrationRef();
        if (!StringUtils.hasText(orchestrationRef) && binding != null) {
            orchestrationRef = binding.getOrchestrationRef();
        }
        orchestrationRef = resolveOrchestrationRef(orchestrationRef);
        if (!OrchestrationRefs.STANDARD_EXPAND_SOLVE_PERSIST_V1.equals(orchestrationRef)) {
            throw exception(SCHEDULE_RUN_ORCHESTRATION_UNKNOWN);
        }

        SchedulingSpecDTO schedulingSpec = request.getSchedulingSpec();
        String policySnapshotId = request.getPolicySnapshotId();
        String policySetId = request.getPolicySetId();

        if (!StringUtils.hasText(policySetId) && binding != null && StringUtils.hasText(binding.getPolicySetId())) {
            policySetId = binding.getPolicySetId();
        }

        if (schedulingSpec == null && StringUtils.hasText(policySnapshotId)) {
            PolicySnapshotRespDTO snapshot = policyResolveApi.getSnapshot(policySnapshotId).getCheckedData();
            schedulingSpec = snapshot.getSchedulingSpec();
        } else if (schedulingSpec == null && StringUtils.hasText(policySetId)) {
            PolicyRunContextDTO runContext = policyResolveApi.resolveForRun(PolicyResolveForRunReqDTO.builder()
                    .policySetId(policySetId)
                    .businessTypeCode(request.getBusinessTypeCode())
                    .build()).getCheckedData();
            schedulingSpec = runContext.getSchedulingSpec();
            policySnapshotId = runContext.getPolicySnapshotId();
        }

        if (schedulingSpec == null) {
            throw exception(SCHEDULE_RUN_SCHEDULING_SPEC_REQUIRED);
        }
        return new RunContext(orchestrationRef, schedulingSpec, policySnapshotId);
    }

    private ProcessCapabilityBindingRespDTO getPublishedBindingQuietly(String businessTypeCode) {
        if (!StringUtils.hasText(businessTypeCode)) {
            return null;
        }
        try {
            CommonResult<ProcessCapabilityBindingRespDTO> result =
                    processCapabilityBindingApi.getPublishedBinding(businessTypeCode);
            if (result == null || !result.isSuccess() || result.getData() == null) {
                return null;
            }
            return result.getData();
        } catch (Exception ignored) {
            // 绑定未发布或 capability 服务尚未注册时，不阻断 Phase 1 直传 schedulingSpec 路径
            return null;
        }
    }

    private String resolveOrchestrationRef(String ref) {
        if (StringUtils.hasText(ref)) {
            return ref;
        }
        return OrchestrationRefs.STANDARD_EXPAND_SOLVE_PERSIST_V1;
    }

    private List<WorkItemDTO> expand(List<WorkItemDTO> workItems) {
        return new ArrayList<>(workItems);
    }

    private String buildSummary(List<ScheduleSlotDTO> slots, SchedulingSpecDTO schedulingSpec) {
        String mode = schedulingSpec.getMode() != null ? schedulingSpec.getMode() : "once";
        String strategy = schedulingSpec.getConflictStrategy() != null
                ? schedulingSpec.getConflictStrategy() : "none";
        return "已生成 " + slots.size() + " 个计划点，周期 " + mode + "，冲突策略 " + strategy;
    }

    private record RunContext(String orchestrationRef, SchedulingSpecDTO schedulingSpec, String policySnapshotId) {
    }
}
