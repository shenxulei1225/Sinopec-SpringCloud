package cn.cheers.x.module.platform.orchestration.service;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.maintenance.api.MaintenanceApi;
import cn.cheers.x.maintenance.api.dto.BindingResolveReqDTO;
import cn.cheers.x.module.platform.capability.api.MappingProfileApi;
import cn.cheers.x.module.platform.capability.api.ProcessCapabilityBindingApi;
import cn.cheers.x.module.platform.capability.api.dto.ProcessCapabilityBindingRespDTO;
import cn.cheers.x.module.platform.capability.api.dto.ResolveWorkItemsReqDTO;
import cn.cheers.x.module.platform.contract.ContractVersions;
import cn.cheers.x.module.platform.contract.dto.runtime.RuntimeJobDTO;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleRunRequest;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleRunResponse;
import cn.cheers.x.module.platform.contract.dto.schedule.SchedulingSpecDTO;
import cn.cheers.x.module.platform.contract.dto.slot.ScheduleSlotDTO;
import cn.cheers.x.module.platform.contract.dto.work.SourceInstanceRefDTO;
import cn.cheers.x.module.platform.contract.dto.work.WorkItemDTO;
import cn.cheers.x.module.platform.contract.enums.RuntimeJobStatus;
import cn.cheers.x.module.platform.orchestration.api.dto.OrchestrationRunRequest;
import cn.cheers.x.module.platform.orchestration.api.dto.OrchestrationRunResponse;
import cn.cheers.x.module.platform.orchestration.enums.OrchestrationRefs;
import cn.cheers.x.module.platform.orchestration.phase.OrchestrationPhase;
import cn.cheers.x.module.platform.orchestration.phase.PhaseContext;
import cn.cheers.x.module.platform.orchestration.phase.PhaseHandler;
import cn.cheers.x.module.platform.orchestration.phase.PhaseHandlerRegistry;
import cn.cheers.x.module.platform.orchestration.template.OrchestrationTemplate;
import cn.cheers.x.module.platform.orchestration.template.OrchestrationTemplateRegistry;
import cn.cheers.x.module.platform.policy.api.PolicyResolveApi;
import cn.cheers.x.module.platform.policy.api.dto.PolicyResolveForRunReqDTO;
import cn.cheers.x.module.platform.policy.api.dto.PolicyRunContextDTO;
import cn.cheers.x.module.platform.policy.api.dto.PolicySnapshotRespDTO;
import cn.cheers.x.module.platform.runtime.api.RuntimePersistApi;
import cn.cheers.x.module.platform.runtime.api.dto.RuntimePersistReqDTO;
import cn.cheers.x.module.platform.scheduling.engine.SchedulingEngine;
import cn.cheers.x.workorder.api.WorkOrderApi;
import cn.cheers.x.workorder.api.dto.WorkOrderCreateReqDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.cheers.x.module.platform.orchestration.enums.ErrorCodeConstants.ORCHESTRATION_PHASE_HANDLER_MISSING;
import static cn.cheers.x.module.platform.orchestration.enums.ErrorCodeConstants.SCHEDULE_RUN_MAPPING_PROFILE_REQUIRED;
import static cn.cheers.x.module.platform.orchestration.enums.ErrorCodeConstants.SCHEDULE_RUN_SCHEDULING_SPEC_REQUIRED;

/**
 * 按编排模板顺序执行阶段（内建逻辑或已注册 PhaseHandler）。
 */
@Slf4j
@Component
public class OrchestrationRunner {

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
    @Resource
    private WorkOrderApi workOrderApi;
    @Resource
    private MaintenanceApi maintenanceApi;
    @Resource
    private OrchestrationTemplateRegistry templateRegistry;
    @Resource
    private PhaseHandlerRegistry phaseHandlerRegistry;


    /**
     * 通用编排运行（非排程占窗）：按模板 handlerIds 执行阶段。
     * 应急启动响应等走此入口，不走 schedule/run。
     */
    public OrchestrationRunResponse run(OrchestrationRunRequest request) {
        if (request == null || !StringUtils.hasText(request.getOrchestrationRef())) {
            throw exception(ORCHESTRATION_PHASE_HANDLER_MISSING);
        }
        OrchestrationTemplate template = templateRegistry.require(request.getOrchestrationRef());
        PhaseContext context = PhaseContext.builder()
                .orchestrationRunRequest(request)
                .orchestrationRef(request.getOrchestrationRef())
                .siteId(request.getSiteId())
                .dryRun(Boolean.TRUE.equals(request.getDryRun()))
                .attributes(new HashMap<>())
                .workItems(new ArrayList<>())
                .slots(new ArrayList<>())
                .build();

        for (OrchestrationPhase phase : template.getPhases()) {
            Map<OrchestrationPhase, String> handlerIds = template.getHandlerIds() == null
                    ? Collections.emptyMap() : template.getHandlerIds();
            String handlerId = handlerIds.get(phase);
            if (!StringUtils.hasText(handlerId)) {
                throw exception(ORCHESTRATION_PHASE_HANDLER_MISSING);
            }
            phaseHandlerRegistry.require(handlerId).execute(context);
        }

        Map<String, Object> result = new HashMap<>();
        Object expand = context.getAttr("expandResult");
        if (expand instanceof Map<?, ?> map) {
            map.forEach((k, v) -> result.put(String.valueOf(k), v));
        }
        result.putIfAbsent("orchestrationRef", request.getOrchestrationRef());

        return OrchestrationRunResponse.builder()
                .orchestrationRef(request.getOrchestrationRef())
                .status("COMPLETED")
                .result(result)
                .build();
    }

    public ScheduleRunResponse run(ScheduleRunRequest request, Long siteId) {
        RunContext resolved = resolveRunContext(request);
        OrchestrationTemplate template = templateRegistry.require(resolved.orchestrationRef());

        boolean dryRun = Boolean.TRUE.equals(request.getDryRun());
        OrchestrationPhase stopAfter = parseStopAfterPhase(request.getStopAfterPhase());

        String runtimeJobId = UUID.randomUUID().toString();
        PhaseContext context = PhaseContext.builder()
                .request(request)
                .schedulingSpec(resolved.schedulingSpec())
                .orchestrationRef(resolved.orchestrationRef())
                .runtimeJobId(runtimeJobId)
                .workItems(new ArrayList<>())
                .slots(new ArrayList<>())
                .dryRun(dryRun)
                .stopAfterPhase(stopAfter)
                .policySnapshotId(resolved.policySnapshotId())
                .siteId(siteId)
                .build();

        List<Long> workOrderIds = new ArrayList<>();
        for (OrchestrationPhase phase : template.getPhases()) {
            executePhase(phase, template, context, workOrderIds);
            if (stopAfter != null && phase == stopAfter) {
                break;
            }
        }

        // 模板未声明 DISPATCH 时，仍按请求标志在非 dryRun 下派工（兼容现网 schedule/run）
        if (!template.getPhases().contains(OrchestrationPhase.DISPATCH)
                && Boolean.TRUE.equals(request.getDispatchWorkOrders())
                && !dryRun) {
            runDispatch(context, workOrderIds);
        }

        return ScheduleRunResponse.builder()
                .contractVersion(ContractVersions.MVP)
                .runtimeJobId(runtimeJobId)
                .status(RuntimeJobStatus.SCHEDULED)
                .slots(context.getSlots())
                .decisionTraceId(null)
                .plainSummary(buildSummary(context.getSlots(), resolved.schedulingSpec()))
                .workOrderIds(workOrderIds.isEmpty() ? null : workOrderIds)
                .build();
    }

    private void executePhase(OrchestrationPhase phase, OrchestrationTemplate template,
                              PhaseContext context, List<Long> workOrderIds) {
        Map<OrchestrationPhase, String> handlerIds = template.getHandlerIds() == null
                ? Collections.emptyMap() : template.getHandlerIds();
        String handlerId = handlerIds.get(phase);
        if (StringUtils.hasText(handlerId)) {
            PhaseHandler handler = phaseHandlerRegistry.require(handlerId);
            handler.execute(context);
            return;
        }
        runBuiltin(phase, context, workOrderIds);
    }

    private void runBuiltin(OrchestrationPhase phase, PhaseContext context, List<Long> workOrderIds) {
        switch (phase) {
            case VALIDATE -> log.debug("VALIDATE phase skipped (MVP no-op), ref={}",
                    context.getOrchestrationRef());
            case EXPAND -> runExpand(context);
            case ROUTE -> log.info("ROUTE phase skipped until BuiltinRoutePhaseHandler (Task 5), ref={}",
                    context.getOrchestrationRef());
            case SOLVE -> runSolve(context);
            case PERSIST -> runPersist(context);
            case DISPATCH -> {
                if (!context.isDryRun() && Boolean.TRUE.equals(context.getRequest().getDispatchWorkOrders())) {
                    runDispatch(context, workOrderIds);
                }
            }
            case CONFIRM -> log.debug("CONFIRM phase no-op until business handler, ref={}",
                    context.getOrchestrationRef());
            default -> log.warn("Unhandled orchestration phase {}, skipped", phase);
        }
    }

    private void runExpand(PhaseContext context) {
        List<WorkItemDTO> workItems = resolveWorkItems(context.getRequest());
        context.setWorkItems(new ArrayList<>(workItems));
    }

    private void runSolve(PhaseContext context) {
        List<ScheduleSlotDTO> slots = schedulingEngine.solve(
                context.getWorkItems(),
                context.getSchedulingSpec(),
                context.getRuntimeJobId(),
                List.of());
        if (StringUtils.hasText(context.getPolicySnapshotId())) {
            for (ScheduleSlotDTO slot : slots) {
                slot.setPolicySnapshotId(context.getPolicySnapshotId());
            }
        }
        context.setSlots(slots);
    }

    private void runPersist(PhaseContext context) {
        if (context.isDryRun()) {
            log.info("PERSIST skipped for dryRun, runtimeJobId={}", context.getRuntimeJobId());
            return;
        }
        ScheduleRunRequest request = context.getRequest();
        RuntimeJobDTO job = RuntimeJobDTO.builder()
                .contractVersion(ContractVersions.MVP)
                .runtimeJobId(context.getRuntimeJobId())
                .entityTypeCode(request.getEntityTypeCode())
                .triggerAction("schedule.run")
                .status(RuntimeJobStatus.SCHEDULED)
                .sourceWorkIds(context.getWorkItems().stream().map(WorkItemDTO::getWorkId).collect(Collectors.toList()))
                .policySnapshotId(context.getPolicySnapshotId())
                .orchestrationRef(context.getOrchestrationRef())
                .createdAt(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(OffsetDateTime.now()))
                .build();

        runtimePersistApi.persist(RuntimePersistReqDTO.builder()
                .job(job)
                .slots(context.getSlots())
                .siteId(context.getSiteId())
                .build()).checkError();
    }

    private void runDispatch(PhaseContext context, List<Long> workOrderIds) {
        ScheduleRunRequest request = context.getRequest();
        if (!Boolean.TRUE.equals(request.getDispatchWorkOrders()) || context.isDryRun()) {
            return;
        }
        String scope = StringUtils.hasText(request.getScope()) ? request.getScope() : "inspection";
        Long standardId = resolveDispatchStandardId(request, scope);
        for (ScheduleSlotDTO slot : context.getSlots()) {
            WorkOrderCreateReqDTO dto = new WorkOrderCreateReqDTO();
            dto.setScope(scope);
            dto.setTitle("WO-" + slot.getSlotId());
            dto.setStandardId(standardId);
            dto.setAssetId(request.getAssetId());
            dto.setAssetTypeCode(request.getAssetTypeCode());
            dto.setFrequencyCode(request.getFrequencyCode());
            dto.setRuntimeJobId(context.getRuntimeJobId());
            dto.setScheduleSlotId(slot.getSlotId());
            dto.setBusinessKey(slot.getWorkId());
            workOrderIds.add(workOrderApi.create(dto).getCheckedData());
        }
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

    private List<WorkItemDTO> resolveWorkItems(ScheduleRunRequest request) {
        if (!CollectionUtils.isEmpty(request.getWorkItems())) {
            return request.getWorkItems();
        }
        ProcessCapabilityBindingRespDTO binding = getPublishedBindingQuietly(request.getEntityTypeCode());
        String mappingProfileId = firstMappingProfileId(binding);
        if (!StringUtils.hasText(mappingProfileId)) {
            throw exception(SCHEDULE_RUN_MAPPING_PROFILE_REQUIRED);
        }
        List<ResolveWorkItemsReqDTO.SourceInstanceInputDTO> instances = request.getSourceInstances().stream()
                .map(this::toSourceInstanceInput)
                .collect(Collectors.toList());
        return mappingProfileApi.resolveWorkItems(mappingProfileId, ResolveWorkItemsReqDTO.builder()
                .entityTypeCode(request.getEntityTypeCode())
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
        ProcessCapabilityBindingRespDTO binding = getPublishedBindingQuietly(request.getEntityTypeCode());

        String orchestrationRef = request.getOrchestrationRef();
        if (!StringUtils.hasText(orchestrationRef) && binding != null) {
            orchestrationRef = binding.getOrchestrationRef();
        }
        orchestrationRef = resolveOrchestrationRef(orchestrationRef);
        templateRegistry.require(orchestrationRef);

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
                    .entityTypeCode(request.getEntityTypeCode())
                    .build()).getCheckedData();
            schedulingSpec = runContext.getSchedulingSpec();
            policySnapshotId = runContext.getPolicySnapshotId();
        }

        if (schedulingSpec == null) {
            throw exception(SCHEDULE_RUN_SCHEDULING_SPEC_REQUIRED);
        }
        return new RunContext(orchestrationRef, schedulingSpec, policySnapshotId);
    }

    private ProcessCapabilityBindingRespDTO getPublishedBindingQuietly(String entityTypeCode) {
        if (!StringUtils.hasText(entityTypeCode)) {
            return null;
        }
        try {
            CommonResult<ProcessCapabilityBindingRespDTO> result =
                    processCapabilityBindingApi.getPublishedBinding(entityTypeCode);
            if (result == null || !result.isSuccess() || result.getData() == null) {
                return null;
            }
            return result.getData();
        } catch (Exception ignored) {
            return null;
        }
    }

    private String resolveOrchestrationRef(String ref) {
        if (StringUtils.hasText(ref)) {
            return ref;
        }
        return OrchestrationRefs.STANDARD_EXPAND_SOLVE_PERSIST_V1;
    }

    private OrchestrationPhase parseStopAfterPhase(String stopAfterPhase) {
        if (!StringUtils.hasText(stopAfterPhase)) {
            return null;
        }
        return OrchestrationPhase.valueOf(stopAfterPhase.trim());
    }

    private String buildSummary(List<ScheduleSlotDTO> slots, SchedulingSpecDTO schedulingSpec) {
        int size = slots == null ? 0 : slots.size();
        String mode = schedulingSpec.getMode() != null ? schedulingSpec.getMode() : "once";
        String strategy = schedulingSpec.getConflictStrategy() != null
                ? schedulingSpec.getConflictStrategy() : "none";
        return "已生成 " + size + " 个计划点，周期 " + mode + "，冲突策略 " + strategy;
    }

    private record RunContext(String orchestrationRef, SchedulingSpecDTO schedulingSpec, String policySnapshotId) {
    }
}
