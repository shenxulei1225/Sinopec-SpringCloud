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
import cn.cheers.x.module.platform.contract.enums.SlotStatus;
import cn.cheers.x.module.platform.orchestration.api.dto.OrchestrationRunRequest;
import cn.cheers.x.module.platform.orchestration.api.dto.OrchestrationRunResponse;
import cn.cheers.x.module.platform.orchestration.enums.OrchestrationRefs;
import cn.cheers.x.module.platform.orchestration.phase.OrchestrationPhase;
import cn.cheers.x.module.platform.orchestration.phase.PhaseContext;
import cn.cheers.x.module.platform.orchestration.phase.PhaseHandler;
import cn.cheers.x.module.platform.orchestration.phase.PhaseHandlerRegistry;
import cn.cheers.x.module.platform.orchestration.route.RoutePayloadKeys;
import cn.cheers.x.module.platform.orchestration.template.OrchestrationTemplate;
import cn.cheers.x.module.platform.orchestration.template.OrchestrationTemplateRegistry;
import cn.cheers.x.module.platform.policy.api.PolicyResolveApi;
import cn.cheers.x.module.platform.policy.api.dto.PolicyResolveForRunReqDTO;
import cn.cheers.x.module.platform.policy.api.dto.PolicyRunContextDTO;
import cn.cheers.x.module.platform.policy.api.dto.PolicySnapshotRespDTO;
import cn.cheers.x.module.platform.runtime.api.RuntimePersistApi;
import cn.cheers.x.module.platform.runtime.api.RuntimeQueryApi;
import cn.cheers.x.module.platform.runtime.api.RuntimeSlotWriteApi;
import cn.cheers.x.module.platform.runtime.api.dto.RuntimePersistReqDTO;
import cn.cheers.x.module.platform.runtime.api.dto.RuntimeSlotReleaseReqDTO;
import cn.cheers.x.module.platform.runtime.enums.RuntimeSlotReleaseMode;
import cn.cheers.x.module.platform.scheduling.engine.SchedulingEngine;
import cn.cheers.x.workorder.api.WorkOrderApi;
import cn.cheers.x.workorder.api.dto.WorkOrderCreateReqDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.cheers.x.module.platform.orchestration.enums.ErrorCodeConstants.ORCHESTRATION_PHASE_HANDLER_MISSING;
import static cn.cheers.x.module.platform.orchestration.enums.ErrorCodeConstants.SCHEDULE_REPLAN_REMAINING_STOPS_REQUIRED;
import static cn.cheers.x.module.platform.orchestration.enums.ErrorCodeConstants.SCHEDULE_REPLAN_SOURCE_JOB_REQUIRED;
import static cn.cheers.x.module.platform.orchestration.enums.ErrorCodeConstants.SCHEDULE_RUN_MAPPING_PROFILE_REQUIRED;
import static cn.cheers.x.module.platform.orchestration.enums.ErrorCodeConstants.SCHEDULE_RUN_SCHEDULING_SPEC_REQUIRED;

/**
 * 按编排模板顺序执行阶段（内建逻辑或已注册 PhaseHandler）。
 */
@Slf4j
@Component
public class OrchestrationRunner {

    private static final ZoneId DEFAULT_ZONE = ZoneId.systemDefault();
    private static final String PAYLOAD_TASK_ENABLED = "taskEnabled";
    /** 同级已排未启用降权：priority 为主，未启用在同 priority 下弱于已启用 */
    private static final int DISABLED_PRIORITY_PENALTY = 10_000;
    private static final String STRATEGY_PRIORITY = "priority_preempt";

    @Resource
    private SchedulingEngine schedulingEngine;
    @Resource
    private RuntimePersistApi runtimePersistApi;
    @Resource
    private RuntimeQueryApi runtimeQueryApi;
    @Resource
    private RuntimeSlotWriteApi runtimeSlotWriteApi;
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
                .facilityId(request.getFacilityId())
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

    public ScheduleRunResponse run(ScheduleRunRequest request, Long facilityId) {
        RunContext resolved = resolveRunContext(request);
        OrchestrationTemplate template = templateRegistry.require(resolved.orchestrationRef());

        boolean dryRun = Boolean.TRUE.equals(request.getDryRun());
        OrchestrationPhase stopAfter = parseStopAfterPhase(request.getStopAfterPhase());
        boolean replan = OrchestrationRefs.PATROL_REPLAN_V1.equals(resolved.orchestrationRef());
        if (replan) {
            if (!StringUtils.hasText(request.getSourceRuntimeJobId())) {
                throw exception(SCHEDULE_REPLAN_SOURCE_JOB_REQUIRED);
            }
        }

        String runtimeJobId = replan ? request.getSourceRuntimeJobId() : UUID.randomUUID().toString();
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
                .facilityId(facilityId)
                .build();

        List<Long> workOrderIds = new ArrayList<>();
        for (OrchestrationPhase phase : template.getPhases()) {
            executePhase(phase, template, context, workOrderIds);
            if (phase == OrchestrationPhase.EXPAND && replan) {
                applyReplanRemainingStops(context);
            }
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
                .routePreview(context.getRoutePreview())
                .workItems(context.getWorkItems())
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
            case ROUTE -> log.info("ROUTE phase: no handler registered, skipping, ref={}",
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
        List<WorkItemDTO> workItems = context.getWorkItems();
        SchedulingSpecDTO schedulingSpec = context.getSchedulingSpec();
        if (usesOccupiedSlots(context.getOrchestrationRef())
                && STRATEGY_PRIORITY.equals(normalizeConflictStrategy(schedulingSpec))) {
            // 产品规则：priority 为主；同级已排未启用（taskEnabled=false）弱于已启用
            workItems = deprioritizeDisabledWorkItems(workItems);
        }

        List<ScheduleSlotDTO> occupiedSlots = List.of();
        if (usesOccupiedSlots(context.getOrchestrationRef())) {
            occupiedSlots = loadOccupiedSlots(context);
        }

        List<ScheduleSlotDTO> slots = schedulingEngine.solve(
                workItems,
                schedulingSpec,
                context.getRuntimeJobId(),
                occupiedSlots);
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
        if (OrchestrationRefs.PATROL_REPLAN_V1.equals(context.getOrchestrationRef())) {
            runReplanPersist(context);
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
                .facilityId(context.getFacilityId())
                .build()).checkError();
    }

    private void runReplanPersist(PhaseContext context) {
        String runtimeJobId = context.getRuntimeJobId();
        runtimeSlotWriteApi.releaseUnfinished(RuntimeSlotReleaseReqDTO.builder()
                .runtimeJobId(runtimeJobId)
                .mode(RuntimeSlotReleaseMode.ABORT)
                .reason("replan")
                .facilityId(context.getFacilityId())
                .build()).checkError();

        runtimePersistApi.persist(RuntimePersistReqDTO.builder()
                .appendSlotsOnly(true)
                .job(RuntimeJobDTO.builder().runtimeJobId(runtimeJobId).build())
                .slots(context.getSlots())
                .facilityId(context.getFacilityId())
                .build()).checkError();
    }

    private void applyReplanRemainingStops(PhaseContext context) {
        List<String> remainingStopIds = resolveRemainingStopIds(context);
        if (CollectionUtils.isEmpty(remainingStopIds)) {
            throw exception(SCHEDULE_REPLAN_REMAINING_STOPS_REQUIRED);
        }
        List<WorkItemDTO> filtered = filterWorkItemsToRemainingStops(context.getWorkItems(), remainingStopIds);
        context.setWorkItems(filtered);
        context.putAttr("remainingStopIds", remainingStopIds);
    }

    private List<String> resolveRemainingStopIds(PhaseContext context) {
        ScheduleRunRequest request = context.getRequest();
        if (!CollectionUtils.isEmpty(request.getRemainingStopIds())) {
            return new ArrayList<>(request.getRemainingStopIds());
        }
        if (!CollectionUtils.isEmpty(request.getCompletedSlotIds())) {
            List<String> allStopIds = resolveAllStopIds(context);
            Set<String> completedStops = deriveCompletedStopIds(request.getCompletedSlotIds(), context);
            return allStopIds.stream()
                    .filter(stopId -> !completedStops.contains(stopId))
                    .collect(Collectors.toList());
        }
        throw exception(SCHEDULE_REPLAN_REMAINING_STOPS_REQUIRED);
    }

    @SuppressWarnings("unchecked")
    private List<String> resolveAllStopIds(PhaseContext context) {
        Object fromExpand = context.getAttr("expandResult");
        if (fromExpand instanceof Map<?, ?> map && map.get("stopIds") instanceof List<?> stopIds) {
            return stopIds.stream().map(String::valueOf).collect(Collectors.toList());
        }
        for (WorkItemDTO workItem : context.getWorkItems()) {
            if (workItem.getPayload() == null) {
                continue;
            }
            Object stopIds = workItem.getPayload().get(RoutePayloadKeys.STOP_IDS);
            if (stopIds instanceof List<?> list && !list.isEmpty()) {
                return list.stream().map(String::valueOf).collect(Collectors.toList());
            }
        }
        return List.of();
    }

    private Set<String> deriveCompletedStopIds(List<String> completedSlotIds, PhaseContext context) {
        Set<String> completedStops = new HashSet<>();
        List<ScheduleSlotDTO> jobSlots = runtimeQueryApi.listSlotsByJobId(context.getRuntimeJobId()).getCheckedData();
        Set<String> completedSlotIdSet = new HashSet<>(completedSlotIds);
        Map<String, Integer> stopIndexByWorkId = buildStopIndexByWorkId(context.getWorkItems());
        for (ScheduleSlotDTO slot : jobSlots) {
            if (slot == null || !completedSlotIdSet.contains(slot.getSlotId())) {
                continue;
            }
            String workId = slot.getWorkId();
            Integer stopIndex = stopIndexByWorkId.get(workId);
            List<String> allStops = resolveAllStopIds(context);
            if (stopIndex != null && stopIndex >= 0 && stopIndex < allStops.size()) {
                completedStops.add(allStops.get(stopIndex));
            }
        }
        return completedStops;
    }

    private Map<String, Integer> buildStopIndexByWorkId(List<WorkItemDTO> workItems) {
        Map<String, Integer> indexByWorkId = new HashMap<>();
        int index = 0;
        for (WorkItemDTO workItem : workItems) {
            if (workItem != null && StringUtils.hasText(workItem.getWorkId())) {
                indexByWorkId.put(workItem.getWorkId(), index++);
            }
        }
        return indexByWorkId;
    }

    private List<WorkItemDTO> filterWorkItemsToRemainingStops(List<WorkItemDTO> workItems,
                                                              List<String> remainingStopIds) {
        if (CollectionUtils.isEmpty(workItems)) {
            return workItems;
        }
        List<WorkItemDTO> result = new ArrayList<>(workItems.size());
        Set<String> remaining = new HashSet<>(remainingStopIds);
        for (WorkItemDTO workItem : workItems) {
            WorkItemDTO copy = copyWorkItem(workItem);
            Map<String, Object> payload = copy.getPayload();
            if (payload != null && payload.get(RoutePayloadKeys.STOP_IDS) instanceof List<?> stopIds) {
                List<String> filteredStops = stopIds.stream()
                        .map(String::valueOf)
                        .filter(remaining::contains)
                        .collect(Collectors.toList());
                payload.put(RoutePayloadKeys.STOP_IDS, filteredStops);
            }
            result.add(copy);
        }
        return result;
    }

    private WorkItemDTO copyWorkItem(WorkItemDTO source) {
        Map<String, Object> payloadCopy = source.getPayload() == null
                ? null : new HashMap<>(source.getPayload());
        return WorkItemDTO.builder()
                .contractVersion(source.getContractVersion())
                .workId(source.getWorkId())
                .entityTypeCode(source.getEntityTypeCode())
                .sourceModelCode(source.getSourceModelCode())
                .sourceInstanceId(source.getSourceInstanceId())
                .durationEstimateMinutes(source.getDurationEstimateMinutes())
                .priority(source.getPriority())
                .resourceRequirements(source.getResourceRequirements())
                .timePreferences(source.getTimePreferences())
                .predecessorWorkIds(source.getPredecessorWorkIds())
                .payload(payloadCopy)
                .build();
    }

    private List<ScheduleSlotDTO> loadOccupiedSlots(PhaseContext context) {
        SchedulingSpecDTO spec = context.getSchedulingSpec();
        OffsetDateTime from = horizonStart(spec);
        OffsetDateTime to = horizonEnd(spec);
        List<SlotStatus> statuses = List.of(
                SlotStatus.PLANNED, SlotStatus.IN_PROGRESS, SlotStatus.COMPLETED);
        List<ScheduleSlotDTO> occupied = runtimeQueryApi.listSlots(
                from, to, null, context.getRequest().getEntityTypeCode(), context.getFacilityId(), statuses)
                .getCheckedData();
        return excludeReplanUnfinishedSelf(context, occupied);
    }

    /**
     * Replan convention: exclude non-COMPLETED slots of sourceRuntimeJobId — those windows will be replaced;
     * keep COMPLETED and foreign occupied slots for conflict resolution.
     */
    private List<ScheduleSlotDTO> excludeReplanUnfinishedSelf(PhaseContext context,
                                                              List<ScheduleSlotDTO> occupied) {
        String sourceJobId = context.getRequest().getSourceRuntimeJobId();
        if (!StringUtils.hasText(sourceJobId)) {
            return occupied == null ? List.of() : occupied;
        }
        if (occupied == null || occupied.isEmpty()) {
            return List.of();
        }
        return occupied.stream()
                .filter(slot -> !sourceJobId.equals(slot.getRuntimeJobId())
                        || SlotStatus.COMPLETED.equals(slot.getSlotStatus()))
                .collect(Collectors.toList());
    }

    private List<WorkItemDTO> deprioritizeDisabledWorkItems(List<WorkItemDTO> workItems) {
        List<WorkItemDTO> adjusted = new ArrayList<>(workItems.size());
        for (WorkItemDTO item : workItems) {
            WorkItemDTO copy = copyWorkItem(item);
            if (isTaskDisabled(copy)) {
                int base = copy.getPriority() != null ? copy.getPriority() : 0;
                copy.setPriority(base - DISABLED_PRIORITY_PENALTY);
            }
            adjusted.add(copy);
        }
        adjusted.sort(Comparator
                .comparingInt((WorkItemDTO w) -> w.getPriority() != null ? w.getPriority() : 0).reversed()
                .thenComparing(w -> w.getWorkId() != null ? w.getWorkId() : ""));
        return adjusted;
    }

    private boolean isTaskDisabled(WorkItemDTO workItem) {
        if (workItem.getPayload() == null) {
            return false;
        }
        Object enabled = workItem.getPayload().get(PAYLOAD_TASK_ENABLED);
        if (enabled instanceof Boolean bool) {
            return !bool;
        }
        if (enabled != null) {
            return !Boolean.parseBoolean(String.valueOf(enabled));
        }
        return false;
    }

    private boolean usesOccupiedSlots(String orchestrationRef) {
        return OrchestrationRefs.STANDARD_EXPAND_SOLVE_PERSIST_V1.equals(orchestrationRef)
                || OrchestrationRefs.PATROL_SCHEDULE_ENABLE_V1.equals(orchestrationRef)
                || OrchestrationRefs.PATROL_REPLAN_V1.equals(orchestrationRef);
    }

    private String normalizeConflictStrategy(SchedulingSpecDTO schedulingSpec) {
        if (schedulingSpec == null || !StringUtils.hasText(schedulingSpec.getConflictStrategy())) {
            return "defer_slot";
        }
        return schedulingSpec.getConflictStrategy().trim().toLowerCase();
    }

    private OffsetDateTime horizonStart(SchedulingSpecDTO spec) {
        LocalDate start = parseHorizonDate(spec != null ? spec.getHorizonStart() : null);
        if (start == null) {
            start = LocalDate.now();
        }
        return start.atStartOfDay(DEFAULT_ZONE).toOffsetDateTime();
    }

    private OffsetDateTime horizonEnd(SchedulingSpecDTO spec) {
        LocalDate end = parseHorizonDate(spec != null ? spec.getHorizonEnd() : null);
        if (end == null) {
            LocalDate start = parseHorizonDate(spec != null ? spec.getHorizonStart() : null);
            end = start != null ? start.plusWeeks(4) : LocalDate.now().plusWeeks(4);
        }
        return end.plusDays(1).atStartOfDay(DEFAULT_ZONE).toOffsetDateTime();
    }

    private LocalDate parseHorizonDate(String text) {
        if (!StringUtils.hasText(text)) {
            return null;
        }
        return LocalDate.parse(text.substring(0, Math.min(text.length(), 10)));
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
