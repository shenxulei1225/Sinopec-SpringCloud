package cn.cheers.x.inspection.task.service.task;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.task.controller.admin.vo.task.InspectionTaskCreateReqVO;
import cn.cheers.x.inspection.task.controller.admin.vo.task.InspectionTaskUpdateReqVO;
import cn.cheers.x.inspection.task.model.task.ExecutionDeviceBinding;
import cn.cheers.x.inspection.task.model.task.InspectionContent;
import cn.cheers.x.inspection.task.model.task.ResourcePolicy;
import cn.cheers.x.module.dynamicbusiness.api.entity.EntityRpcApi;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRpcDtoSupport;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityWriteReqDTO;
import cn.cheers.x.module.platform.contract.dto.slot.ScheduleSlotDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 巡检任务草稿只写总任务（巡检任务目录 → 底座任务 → patrol_task）。
 * <p>保存草稿把勾选的被巡检设备和检查项写入 FLD-TSK-019（JSON 文本），不新写步骤图。
 * 更新草稿必须把已有步骤图和已保存路线原样写回，禁止冲掉第 3 步已生成的树。
 * 步骤图按 LONG_TEXT 存 JSON 文本，禁止把 Map 直接塞进列。
 * <p>所属设施 FLD-TSK-024 是引用字段，必须写成 {entityTypeCode,id}，禁止写裸数字。
 * <p>起点终点、进度、执行设备、试排、已保存路线、步骤图按巡检方式分格，互不覆盖。
 * 当前巡检方式只表示现在在看、生成时用哪一格。对象勾选和排期模板仍是全任务一份。
 * <p>禁止：再写入固定表 inspection_task；读路径猜巡检方式；换方式时冲掉另一方式已写下的格子。
 */
@Component
@RequiredArgsConstructor
public class PatrolTaskEntityStore {

    public static final String TASK_TYPE = "task";
    public static final String MODEL_PATROL = "patrol_task";
    public static final String FIELD_DOMAIN = "FLD-TSK-003";
    public static final String FIELD_STATUS = "FLD-TSK-022";
    public static final String FIELD_SCHEDULE = "FLD-TSK-018";
    public static final String FIELD_CONTENT = "FLD-TSK-019";
    public static final String FIELD_RESOURCE = "FLD-TSK-020";
    public static final String FIELD_FACILITY = "FLD-TSK-024";
    public static final String FACILITY_TYPE = "facility";
    public static final String FIELD_ROUTE = "FLD-TSK-027";
    public static final String FIELD_ITEMS = "FLD-TSK-031";
    public static final String FIELD_DRAFT = "FLD-TSK-033";
    public static final String DRAFT_KEY_UNLOCKED = "createUnlockedStep";
    public static final String DRAFT_KEY_START = "startStopId";
    public static final String DRAFT_KEY_END = "endStopId";
    /** previewOrchestration 试排草稿；generateTask 确认后写入 runtimeJobId。 */
    public static final String DRAFT_KEY_RUNTIME_JOB = "runtimeJobId";
    /** generateTask（生成任务）成功后为 true：任务已生成并排期（非排期模板是否填好）。 */
    public static final String DRAFT_KEY_ORCHESTRATION_COMMITTED = "orchestrationCommitted";
    /**
     * 生成任务时用的那种巡检方式。详情和已占窗只认它。
     * 换顶栏方式不改这份；再点生成任务才换成当时选中的那种。
     */
    public static final String DRAFT_KEY_COMMITTED_MEANS = "committedPatrolExecutionMode";
    /** 智能编排试排计划点（JSON 列表）；未确认前不占 runtime。 */
    public static final String DRAFT_KEY_ORCHESTRATION_PREVIEW_SLOTS = "orchestrationPreviewSlots";
    /** 试排摘要文案。 */
    public static final String DRAFT_KEY_ORCHESTRATION_PREVIEW_SUMMARY = "orchestrationPreviewPlainSummary";
    /** 排期策略 id；与 FLD-TSK-018 模板配置分开存草稿袋 */
    public static final String DRAFT_KEY_SCHEDULE_POLICY = "schedulePolicyId";
    /** 排期策略·冲突处理：defer_slot / reject_batch / priority_preempt */
    public static final String DRAFT_KEY_CONFLICT_STRATEGY = "conflictStrategy";
    /** 排期策略·任务间隔（分钟） */
    public static final String DRAFT_KEY_TASK_GAP_MINUTES = "taskGapMinutes";
    /** 排期策略：空闲不够时是否允许挪已有任务排期 */
    public static final String DRAFT_KEY_ALLOW_SHIFT_EXISTING = "allowShiftExisting";
    /** 排期策略：已有任务单侧最多挪动分钟数 */
    public static final String DRAFT_KEY_MAX_SHIFT_MINUTES = "maxShiftMinutes";
    /** 计划点 id → pending 执行账 id；排期物化后写入，开跑只读。 */
    public static final String DRAFT_KEY_PENDING_EXECUTION_BY_SLOT = "pendingExecutionBySlot";
    public static final String FIELD_STEP_TREE = "step_tree_json";
    public static final String EMPTY_SCHEDULE = "{\"items\":[],\"version\":1}";

    private final EntityRpcApi entityRpcApi;
    private final ObjectMapper objectMapper;

    public Long createDraft(InspectionTaskCreateReqVO req) {
        if (req == null || !StringUtils.hasText(req.getTaskName())) {
            throw ServiceExceptionUtil.invalidParamException("请先填写任务名称");
        }
        if (req.getFacilityId() == null) {
            throw ServiceExceptionUtil.invalidParamException("请先选择所属设施");
        }
        if (!StringUtils.hasText(req.getPatrolExecutionMode())) {
            throw ServiceExceptionUtil.invalidParamException("请先选择巡检方式");
        }
        EntityWriteReqDTO write = new EntityWriteReqDTO();
        write.setEntityTypeCode(TASK_TYPE);
        write.setModelCode(MODEL_PATROL);
        write.setName(req.getTaskName().trim());
        write.setFields(toFields(req.getDomain(), req.getFacilityId(), req.getPatrolExecutionMode(),
                req.getStartStopId(), req.getEndStopId(),
                req.getInspectionContent(), req.getExecutionDeviceBinding(), req.getResourcePolicy(),
                req.getScheduleConfig(), req.getSchedulePolicyId(),
                req.getConflictStrategy(), req.getTaskGapMinutes(),
                req.getAllowShiftExisting(), req.getMaxShiftMinutes(),
                null, null, null));
        return requireId(entityRpcApi.createEntity(write), "创建总任务失败");
    }

    public void updateDraft(InspectionTaskUpdateReqVO req) {
        if (req == null || req.getId() == null) {
            throw ServiceExceptionUtil.invalidParamException("任务 id 不能为空");
        }
        EntityRespDTO existing = optional(entityRpcApi.getEntity(req.getId(), TASK_TYPE));
        if (existing == null || existing.getId() == null) {
            throw ServiceExceptionUtil.invalidParamException("任务不存在");
        }
        EntityWriteReqDTO write = new EntityWriteReqDTO();
        write.setId(req.getId());
        write.setEntityTypeCode(TASK_TYPE);
        write.setModelCode(MODEL_PATROL);
        if (StringUtils.hasText(req.getTaskName())) {
            write.setName(req.getTaskName().trim());
        }
        Map<String, Object> existingBag = bagOf(existing);
        Object scheduleToWrite = req.getScheduleConfig() != null
                ? req.getScheduleConfig()
                : existingBag.get(FIELD_SCHEDULE);
        Map<String, Object> existingDraft = asMap(existingBag.get(FIELD_DRAFT));
        Long policyToWrite = req.getSchedulePolicyId() != null
                ? req.getSchedulePolicyId()
                : asLong(existingDraft.get(DRAFT_KEY_SCHEDULE_POLICY));
        String conflictToWrite = req.getConflictStrategy() != null
                ? req.getConflictStrategy()
                : firstText(existingDraft.get(DRAFT_KEY_CONFLICT_STRATEGY));
        Integer gapToWrite = req.getTaskGapMinutes() != null
                ? req.getTaskGapMinutes()
                : asInteger(existingDraft.get(DRAFT_KEY_TASK_GAP_MINUTES));
        Boolean allowShiftToWrite = req.getAllowShiftExisting() != null
                ? req.getAllowShiftExisting()
                : asBoolean(existingDraft.get(DRAFT_KEY_ALLOW_SHIFT_EXISTING));
        Integer maxShiftToWrite = req.getMaxShiftMinutes() != null
                ? req.getMaxShiftMinutes()
                : asInteger(existingDraft.get(DRAFT_KEY_MAX_SHIFT_MINUTES));
        write.setFields(toFields(req.getDomain(), req.getFacilityId(), req.getPatrolExecutionMode(),
                req.getStartStopId(), req.getEndStopId(),
                req.getInspectionContent(), req.getExecutionDeviceBinding(), req.getResourcePolicy(),
                scheduleToWrite, policyToWrite, conflictToWrite, gapToWrite,
                allowShiftToWrite, maxShiftToWrite,
                existingBag.get(FIELD_STEP_TREE), existingBag.get(FIELD_ROUTE), existingDraft));
        requireOk(entityRpcApi.updateEntityFields(write), "更新总任务失败");
    }

    /**
     * 写入 generateTask / abortOrchestration 后的草稿编排状态。
     * <p>{@code runtimeJobId == null} 表示不修改作业号，仅更新 orchestrationCommitted。
     * 生成成功时同时记下当前巡检方式：详情和已占窗只认这一套，换顶栏方式不能把占窗带到另一套。
     */
    public void writeOrchestrationCommittedState(Long taskId, String runtimeJobId, Boolean orchestrationCommitted) {
        Map<String, Object> patch = new LinkedHashMap<>();
        if (runtimeJobId != null) {
            patch.put(DRAFT_KEY_RUNTIME_JOB, runtimeJobId);
            patch.put(DRAFT_KEY_PENDING_EXECUTION_BY_SLOT, new LinkedHashMap<>());
            String means = currentMeansOf(taskId);
            if (StringUtils.hasText(means)) {
                patch.put(DRAFT_KEY_COMMITTED_MEANS, means);
            }
        }
        if (orchestrationCommitted != null) {
            patch.put(DRAFT_KEY_ORCHESTRATION_COMMITTED, orchestrationCommitted);
        }
        if (patch.isEmpty()) {
            throw ServiceExceptionUtil.invalidParamException("编排状态补丁不能为空");
        }
        mergeDraftFields(taskId, patch);
    }

    /**
     * 生成任务时记下的那种巡检方式。没有这份（旧数据）则返回空，由查询侧按「当前方式」决定是否读已占窗。
     */
    public String readCommittedPatrolExecutionMode(Long taskId) {
        EntityRespDTO existing = optional(entityRpcApi.getEntity(taskId, TASK_TYPE));
        if (existing == null || existing.getId() == null) {
            return null;
        }
        return firstText(asMap(bagOf(existing).get(FIELD_DRAFT)).get(DRAFT_KEY_COMMITTED_MEANS));
    }

    /**
     * previewOrchestration 试排成功：计划点写入草稿袋，不占 runtime；再点「智能编排」会覆盖。
     */
    public void writeOrchestrationPreview(Long taskId, List<ScheduleSlotDTO> slots, String plainSummary) {
        if (taskId == null) {
            throw ServiceExceptionUtil.invalidParamException("任务 id 不能为空");
        }
        if (slots == null || slots.isEmpty()) {
            throw ServiceExceptionUtil.invalidParamException("试排计划点不能为空");
        }
        Map<String, Object> patch = new LinkedHashMap<>();
        patch.put(DRAFT_KEY_ORCHESTRATION_PREVIEW_SLOTS, objectMapper.convertValue(slots, List.class));
        patch.put(DRAFT_KEY_ORCHESTRATION_PREVIEW_SUMMARY,
                StringUtils.hasText(plainSummary) ? plainSummary.trim() : null);
        mergeDraftFields(taskId, patch);
    }

    /** generateTask 或 abortOrchestration 后清除试排快照。 */
    public void clearOrchestrationPreview(Long taskId) {
        require(taskId);
        EntityRespDTO existing = optional(entityRpcApi.getEntity(taskId, TASK_TYPE));
        if (existing == null || existing.getId() == null) {
            throw ServiceExceptionUtil.invalidParamException("任务不存在");
        }
        Map<String, Object> draft = asMap(bagOf(existing).get(FIELD_DRAFT));
        String means = currentMeans(draft);
        draft.put(DRAFT_KEY_ORCHESTRATION_PREVIEW_SLOTS,
                PatrolMeansKeyedSupport.putSlice(draft.get(DRAFT_KEY_ORCHESTRATION_PREVIEW_SLOTS), means, means, null));
        draft.put(DRAFT_KEY_ORCHESTRATION_PREVIEW_SUMMARY,
                PatrolMeansKeyedSupport.putSlice(draft.get(DRAFT_KEY_ORCHESTRATION_PREVIEW_SUMMARY), means, means, null));
        EntityWriteReqDTO write = new EntityWriteReqDTO();
        write.setId(taskId);
        write.setEntityTypeCode(TASK_TYPE);
        write.setModelCode(MODEL_PATROL);
        write.setFields(Map.of(FIELD_DRAFT, draft));
        requireOk(entityRpcApi.updateEntityFields(write), "清除试排快照失败");
    }

    /**
     * abortOrchestration 后清草稿：去掉 runtimeJobId、试排快照与 pending 映射，orchestrationCommitted=false。
     * <p>不负责释放 runtime 占窗本身。
     */
    public void clearOrchestrationTrialState(Long taskId) {
        require(taskId);
        EntityRespDTO existing = optional(entityRpcApi.getEntity(taskId, TASK_TYPE));
        if (existing == null || existing.getId() == null) {
            throw ServiceExceptionUtil.invalidParamException("任务不存在");
        }
        Map<String, Object> draft = asMap(bagOf(existing).get(FIELD_DRAFT));
        draft.remove(DRAFT_KEY_RUNTIME_JOB);
        draft.remove(DRAFT_KEY_ORCHESTRATION_PREVIEW_SLOTS);
        draft.remove(DRAFT_KEY_ORCHESTRATION_PREVIEW_SUMMARY);
        draft.put(DRAFT_KEY_PENDING_EXECUTION_BY_SLOT, new LinkedHashMap<>());
        draft.put(DRAFT_KEY_ORCHESTRATION_COMMITTED, Boolean.FALSE);
        EntityWriteReqDTO write = new EntityWriteReqDTO();
        write.setId(taskId);
        write.setEntityTypeCode(TASK_TYPE);
        write.setModelCode(MODEL_PATROL);
        write.setFields(Map.of(FIELD_DRAFT, draft));
        requireOk(entityRpcApi.updateEntityFields(write), "清除试排状态失败");
    }

    /**
     * 读取计划点对应的 pending 执行账 id；未物化返回 null。
     */
    public Long pendingExecutionRecordId(Long taskId, String slotId) {
        if (taskId == null || !StringUtils.hasText(slotId)) {
            return null;
        }
        Map<String, Object> map = pendingExecutionBySlot(taskId);
        Object raw = map.get(slotId.trim());
        if (raw instanceof Number number) {
            return number.longValue();
        }
        if (raw instanceof String text && StringUtils.hasText(text)) {
            try {
                return Long.parseLong(text.trim());
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    /**
     * 排期物化成功后写入 slot → pending 执行账映射。
     */
    public void putPendingExecutionRecordId(Long taskId, String slotId, Long executionRecordId) {
        if (taskId == null || !StringUtils.hasText(slotId) || executionRecordId == null) {
            throw ServiceExceptionUtil.invalidParamException("写入待执行映射参数不完整");
        }
        Map<String, Object> map = new LinkedHashMap<>(pendingExecutionBySlot(taskId));
        map.put(slotId.trim(), executionRecordId);
        mergeDraftFields(taskId, Map.of(DRAFT_KEY_PENDING_EXECUTION_BY_SLOT, map));
    }

    private Map<String, Object> pendingExecutionBySlot(Long taskId) {
        EntityRespDTO existing = optional(entityRpcApi.getEntity(taskId, TASK_TYPE));
        if (existing == null) {
            return new LinkedHashMap<>();
        }
        Map<String, Object> draft = asMap(bagOf(existing).get(FIELD_DRAFT));
        Object raw = draft.get(DRAFT_KEY_PENDING_EXECUTION_BY_SLOT);
        if (raw instanceof Map<?, ?> map) {
            Map<String, Object> copy = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (entry.getKey() != null) {
                    copy.put(String.valueOf(entry.getKey()), entry.getValue());
                }
            }
            return copy;
        }
        return new LinkedHashMap<>();
    }

    /**
     * 只改草稿袋里的键，其它草稿键原样留下。
     * <p>用来写建任务进度；禁止借这里重写巡检方式或勾选对象。
     */
    public void mergeDraftFields(Long taskId, Map<String, Object> patch) {
        if (patch == null || patch.isEmpty()) {
            throw ServiceExceptionUtil.invalidParamException("草稿补丁不能为空");
        }
        EntityRespDTO existing = optional(entityRpcApi.getEntity(taskId, TASK_TYPE));
        if (existing == null || existing.getId() == null) {
            throw ServiceExceptionUtil.invalidParamException("任务不存在");
        }
        Map<String, Object> draft = asMap(bagOf(existing).get(FIELD_DRAFT));
        String means = currentMeans(draft);
        for (Map.Entry<String, Object> entry : patch.entrySet()) {
            if (isMeansScopedDraftKey(entry.getKey())) {
                draft.put(entry.getKey(),
                        PatrolMeansKeyedSupport.putSlice(draft.get(entry.getKey()), means, means, entry.getValue()));
            } else if (entry.getValue() == null) {
                draft.remove(entry.getKey());
            } else {
                draft.put(entry.getKey(), entry.getValue());
            }
        }
        EntityWriteReqDTO write = new EntityWriteReqDTO();
        write.setId(taskId);
        write.setEntityTypeCode(TASK_TYPE);
        write.setModelCode(MODEL_PATROL);
        write.setFields(Map.of(FIELD_DRAFT, draft));
        requireOk(entityRpcApi.updateEntityFields(write), "写入建任务进度失败");
    }

    /**
     * 只改巡检内容（FLD-TSK-019），用来写检查项动作耗时。不冲步骤图、不冲路线。
     */
    public void writeInspectionContent(Long taskId, InspectionContent content) {
        require(taskId);
        if (content == null) {
            throw ServiceExceptionUtil.invalidParamException("巡检内容不能为空");
        }
        EntityWriteReqDTO write = new EntityWriteReqDTO();
        write.setId(taskId);
        write.setEntityTypeCode(TASK_TYPE);
        write.setModelCode(MODEL_PATROL);
        write.setFields(Map.of(FIELD_CONTENT, writeJson(content)));
        requireOk(entityRpcApi.updateEntityFields(write), "写入检查项动作耗时失败");
    }

    /**
     * 后面步骤算出的试排和步骤图作废。不改排期模板、不改执行设备。
     */
    public void clearLaterComputedResults(Long taskId) {
        clearOrchestrationPreview(taskId);
        clearStepTree(taskId);
    }

    /**
     * 步骤图作废。写成空数组，读出来没有节点。
     */
    public void clearStepTree(Long taskId) {
        EntityRespDTO existing = optional(entityRpcApi.getEntity(taskId, TASK_TYPE));
        if (existing == null || existing.getId() == null) {
            throw ServiceExceptionUtil.invalidParamException("任务不存在");
        }
        Map<String, Object> bag = bagOf(existing);
        String means = currentMeans(asMap(bag.get(FIELD_DRAFT)));
        Object next = PatrolMeansKeyedSupport.putSlice(normalizeStepTreeField(bag.get(FIELD_STEP_TREE)), means, means, null);
        EntityWriteReqDTO write = new EntityWriteReqDTO();
        write.setId(taskId);
        write.setEntityTypeCode(TASK_TYPE);
        write.setModelCode(MODEL_PATROL);
        write.setFields(Map.of(FIELD_STEP_TREE, next == null || ((Map<?, ?>) next).isEmpty() ? "[]" : writeJson(next)));
        requireOk(entityRpcApi.updateEntityFields(write), "作废步骤图失败");
    }

    /**
     * 前面步骤改了，已保存路线作废。写成空对象，读出来没有停靠点就不算已保存。
     */
    public void clearPlannedRoute(Long taskId) {
        EntityRespDTO existing = optional(entityRpcApi.getEntity(taskId, TASK_TYPE));
        if (existing == null || existing.getId() == null) {
            throw ServiceExceptionUtil.invalidParamException("任务不存在");
        }
        Map<String, Object> bag = bagOf(existing);
        String means = currentMeans(asMap(bag.get(FIELD_DRAFT)));
        Object next = PatrolMeansKeyedSupport.putSlice(bag.get(FIELD_ROUTE), means, means, null);
        EntityWriteReqDTO write = new EntityWriteReqDTO();
        write.setId(taskId);
        write.setEntityTypeCode(TASK_TYPE);
        write.setModelCode(MODEL_PATROL);
        write.setFields(Map.of(FIELD_ROUTE, next));
        requireOk(entityRpcApi.updateEntityFields(write), "作废已保存路线失败");
    }

    public void writeStepTree(Long taskId, Map<String, Object> tree) {
        EntityRespDTO existing = optional(entityRpcApi.getEntity(taskId, TASK_TYPE));
        if (existing == null || existing.getId() == null) {
            throw ServiceExceptionUtil.invalidParamException("任务不存在");
        }
        Map<String, Object> bag = bagOf(existing);
        String means = currentMeans(asMap(bag.get(FIELD_DRAFT)));
        Object next = PatrolMeansKeyedSupport.putSlice(normalizeStepTreeField(bag.get(FIELD_STEP_TREE)), means, means, tree);
        EntityWriteReqDTO write = new EntityWriteReqDTO();
        write.setId(taskId);
        write.setEntityTypeCode(TASK_TYPE);
        write.setModelCode(MODEL_PATROL);
        write.setFields(Map.of(FIELD_STEP_TREE, writeJson(next)));
        requireOk(entityRpcApi.updateEntityFields(write), "写入任务步骤图失败");
    }

    public void writePlannedRoute(Long taskId, Object plannedRoute) {
        EntityRespDTO existing = optional(entityRpcApi.getEntity(taskId, TASK_TYPE));
        if (existing == null || existing.getId() == null) {
            throw ServiceExceptionUtil.invalidParamException("任务不存在");
        }
        Map<String, Object> bag = bagOf(existing);
        String means = currentMeans(asMap(bag.get(FIELD_DRAFT)));
        Object next = PatrolMeansKeyedSupport.putSlice(bag.get(FIELD_ROUTE), means, means, plannedRoute);
        EntityWriteReqDTO write = new EntityWriteReqDTO();
        write.setId(taskId);
        write.setEntityTypeCode(TASK_TYPE);
        write.setModelCode(MODEL_PATROL);
        write.setFields(Map.of(FIELD_ROUTE, next));
        requireOk(entityRpcApi.updateEntityFields(write), "写入路径规划结果失败");
    }

    public PatrolTaskDraft require(Long taskId) {
        if (taskId == null) {
            throw ServiceExceptionUtil.invalidParamException("任务 id 不能为空");
        }
        EntityRespDTO entity = optional(entityRpcApi.getEntity(taskId, TASK_TYPE));
        if (entity == null || entity.getId() == null) {
            throw ServiceExceptionUtil.invalidParamException("任务不存在");
        }
        return fromEntity(entity);
    }

    public void delete(Long taskId) {
        require(taskId);
        requireOk(entityRpcApi.deleteEntity(taskId, TASK_TYPE), "删除总任务失败");
    }

    public List<PatrolTaskDraft> listAll() {
        Long modelId = requireId(entityRpcApi.getModelIdByCode(MODEL_PATROL), "读不到巡检任务型号");
        CommonResult<List<EntityRespDTO>> result = entityRpcApi.listEntities(TASK_TYPE, modelId);
        if (result == null || !result.isSuccess() || result.getData() == null) {
            throw ServiceExceptionUtil.invalidParamException(
                    result != null && StringUtils.hasText(result.getMsg())
                            ? result.getMsg()
                            : "读不到巡检任务列表");
        }
        List<PatrolTaskDraft> drafts = new ArrayList<>();
        for (EntityRespDTO entity : result.getData()) {
            drafts.add(fromEntity(entity));
        }
        return drafts;
    }

    private Map<String, Object> toFields(
            String domain,
            Long facilityId,
            String means,
            String startStopId,
            String endStopId,
            InspectionContent content,
            ExecutionDeviceBinding binding,
            ResourcePolicy resourcePolicy,
            Object scheduleConfig,
            Long schedulePolicyId,
            String conflictStrategy,
            Integer taskGapMinutes,
            Boolean allowShiftExisting,
            Integer maxShiftMinutes,
            Object stepTree,
            Object plannedRoute,
            Map<String, Object> existingDraft
    ) {
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put(FIELD_DOMAIN, StringUtils.hasText(domain) ? domain.trim() : "巡检");
        fields.put(FIELD_STATUS, "draft");
        fields.put(FIELD_SCHEDULE, resolveScheduleField(scheduleConfig));
        if (facilityId != null) {
            fields.put(FIELD_FACILITY, facilityRef(facilityId));
        }
        if (content != null) {
            // LONG_TEXT 只收 JSON 文本；禁止塞 POJO，否则勾选设备落不进总任务
            fields.put(FIELD_CONTENT, writeJson(content));
            fields.put(FIELD_ITEMS, itemIdsOf(content));
        }
        if (resourcePolicy != null) {
            fields.put(FIELD_RESOURCE, resourcePolicy);
        }
        Map<String, Object> draft = existingDraft == null
                ? new LinkedHashMap<>()
                : new LinkedHashMap<>(existingDraft);
        String ownerMeans = firstText(draft.get("patrolExecutionMode"));
        String writeMeans = StringUtils.hasText(means) ? means.trim() : ownerMeans;
        String wrapOwner = StringUtils.hasText(ownerMeans) ? ownerMeans : writeMeans;
        if (StringUtils.hasText(writeMeans)
                && StringUtils.hasText(wrapOwner)
                && !writeMeans.equals(wrapOwner)
                && Boolean.TRUE.equals(readOrchestrationCommittedFromDraft(draft))
                && !StringUtils.hasText(firstText(draft.get(DRAFT_KEY_COMMITTED_MEANS)))) {
            draft.put(DRAFT_KEY_COMMITTED_MEANS, wrapOwner);
        }
        if (StringUtils.hasText(writeMeans)) {
            draft.put("patrolExecutionMode", writeMeans);
        }
        wrapMeansScopedDraftKeys(draft, wrapOwner);
        if (startStopId != null) {
            draft.put(DRAFT_KEY_START, PatrolMeansKeyedSupport.putSlice(
                    draft.get(DRAFT_KEY_START), wrapOwner, writeMeans,
                    StringUtils.hasText(startStopId) ? startStopId.trim() : null));
        }
        if (endStopId != null) {
            draft.put(DRAFT_KEY_END, PatrolMeansKeyedSupport.putSlice(
                    draft.get(DRAFT_KEY_END), wrapOwner, writeMeans,
                    StringUtils.hasText(endStopId) ? endStopId.trim() : null));
        }
        if (binding != null) {
            draft.put("executionDeviceBinding", PatrolMeansKeyedSupport.putSlice(
                    draft.get("executionDeviceBinding"), wrapOwner, writeMeans, binding));
        }
        if (schedulePolicyId != null) {
            draft.put(DRAFT_KEY_SCHEDULE_POLICY, schedulePolicyId);
        }
        applyDraftText(draft, DRAFT_KEY_CONFLICT_STRATEGY, conflictStrategy);
        if (taskGapMinutes != null) {
            draft.put(DRAFT_KEY_TASK_GAP_MINUTES, taskGapMinutes);
        }
        if (allowShiftExisting != null) {
            draft.put(DRAFT_KEY_ALLOW_SHIFT_EXISTING, allowShiftExisting);
        }
        if (maxShiftMinutes != null) {
            draft.put(DRAFT_KEY_MAX_SHIFT_MINUTES, maxShiftMinutes);
        }
        // 已有路线收进当时那种方式那一格。这次请求不写路线，不改另一格。
        Object routeRaw = unwrap(plannedRoute);
        Map<String, Object> routeField = PatrolMeansKeyedSupport.ensureMap(routeRaw, wrapOwner);
        if (!routeField.isEmpty()) {
            fields.put(FIELD_ROUTE, routeField);
        }
        fields.put(FIELD_DRAFT, draft);
        Object treeNormalized = normalizeStepTreeField(stepTree);
        Map<String, Object> treeField = PatrolMeansKeyedSupport.ensureMap(treeNormalized, wrapOwner);
        if (!treeField.isEmpty()) {
            fields.put(FIELD_STEP_TREE, writeJson(treeField));
        }
        return fields;
    }

    /**
     * 读当前巡检方式那一格：进度、起点终点、设备、路线、试排、步骤图。
     * 这一格没有就是空/第 0 步。旧的一份只属于当初写下的那种方式。
     * 禁止按「已经有路线」再砍一步，也禁止把另一格的内容读给当前方式。
     */
    private PatrolTaskDraft fromEntity(EntityRespDTO entity) {
        Map<String, Object> bag = bagOf(entity);
        Map<String, Object> draft = asMap(bag.get(FIELD_DRAFT));
        String means = currentMeans(draft);
        String unwrappedOwner = firstText(PatrolMeansKeyedSupport.primaryMeansOf(draft.get(DRAFT_KEY_UNLOCKED)));
        if (!StringUtils.hasText(unwrappedOwner)) {
            unwrappedOwner = means;
        }
        Object routeSlice = PatrolMeansKeyedSupport.readSlice(bag.get(FIELD_ROUTE), means, unwrappedOwner);
        Object previewSlice = PatrolMeansKeyedSupport.readSlice(
                readOrchestrationPreviewRawFromDraft(draft), means, unwrappedOwner);
        return new PatrolTaskDraft(
                entity.getId(),
                EntityRpcDtoSupport.readName(entity),
                text(bag.get(FIELD_DOMAIN)),
                asLong(bag.get(FIELD_FACILITY)),
                means,
                asContent(bag.get(FIELD_CONTENT)),
                asBinding(PatrolMeansKeyedSupport.readSlice(draft.get("executionDeviceBinding"), means, unwrappedOwner)),
                asResource(bag.get(FIELD_RESOURCE)),
                normalizeStepTreeField(PatrolMeansKeyedSupport.readSlice(
                        normalizeStepTreeField(bag.get(FIELD_STEP_TREE)), means, unwrappedOwner)),
                routeSlice,
                text(bag.get(FIELD_STATUS)),
                asUnlockedStep(PatrolMeansKeyedSupport.readSlice(draft.get(DRAFT_KEY_UNLOCKED), means, unwrappedOwner)),
                firstText(PatrolMeansKeyedSupport.readSlice(draft.get(DRAFT_KEY_START), means, unwrappedOwner)),
                firstText(PatrolMeansKeyedSupport.readSlice(draft.get(DRAFT_KEY_END), means, unwrappedOwner)),
                firstText(draft.get(DRAFT_KEY_RUNTIME_JOB)),
                readOrchestrationCommittedFromDraft(draft),
                parseScheduleConfig(bag.get(FIELD_SCHEDULE)),
                asLong(draft.get(DRAFT_KEY_SCHEDULE_POLICY)),
                previewSlice,
                firstText(PatrolMeansKeyedSupport.readSlice(
                        readOrchestrationPreviewSummaryFromDraft(draft), means, unwrappedOwner))
        );
    }

    /**
     * 草稿袋试排计划点转契约 DTO；缺或格式不对返回空列表。
     */
    public List<ScheduleSlotDTO> readOrchestrationPreviewSlots(Long taskId) {
        PatrolTaskDraft draft = require(taskId);
        return parseOrchestrationPreviewSlots(draft.orchestrationPreviewSlots());
    }

    /**
     * 排期策略里的智能编排处理方法：冲突怎么处理、任务间隔多久。
     * 权威在总任务草稿袋，不在请求里硬编码。
     */
    public ArrangePolicy readArrangePolicy(Long taskId) {
        EntityRespDTO existing = optional(entityRpcApi.getEntity(taskId, TASK_TYPE));
        if (existing == null || existing.getId() == null) {
            throw ServiceExceptionUtil.invalidParamException("任务不存在");
        }
        Map<String, Object> draft = asMap(bagOf(existing).get(FIELD_DRAFT));
        return new ArrangePolicy(
                firstText(draft.get(DRAFT_KEY_CONFLICT_STRATEGY)),
                asInteger(draft.get(DRAFT_KEY_TASK_GAP_MINUTES)),
                asBoolean(draft.get(DRAFT_KEY_ALLOW_SHIFT_EXISTING)),
                asInteger(draft.get(DRAFT_KEY_MAX_SHIFT_MINUTES)));
    }

    public record ArrangePolicy(
            String conflictStrategy,
            Integer taskGapMinutes,
            Boolean allowShiftExisting,
            Integer maxShiftMinutes
    ) {
    }

    private List<ScheduleSlotDTO> parseOrchestrationPreviewSlots(Object raw) {
        if (raw == null) {
            return List.of();
        }
        try {
            return objectMapper.convertValue(raw, new TypeReference<List<ScheduleSlotDTO>>() {
            });
        } catch (IllegalArgumentException ex) {
            return List.of();
        }
    }

    private Object resolveScheduleField(Object scheduleConfig) {
        if (scheduleConfig == null) {
            return EMPTY_SCHEDULE;
        }
        if (scheduleConfig instanceof String text && StringUtils.hasText(text)) {
            return text.trim();
        }
        return writeJson(scheduleConfig);
    }

    private Object parseScheduleConfig(Object raw) {
        if (raw == null) {
            return null;
        }
        if (raw instanceof Map<?, ?> map) {
            return map;
        }
        if (raw instanceof String text && StringUtils.hasText(text)) {
            return convert(text, Object.class);
        }
        return raw;
    }

    private List<Long> itemIdsOf(InspectionContent content) {
        List<Long> ids = new ArrayList<>();
        if (content.getCustomObjects() != null) {
            for (InspectionContent.ObjectContent object : content.getCustomObjects()) {
                if (object.getItems() == null) {
                    continue;
                }
                for (InspectionContent.ItemContent item : object.getItems()) {
                    if (item.getItemId() != null) {
                        ids.add(item.getItemId());
                    }
                }
            }
        }
        return ids;
    }

    /**
     * 巡检内容按 LONG_TEXT 存 JSON。缺勾选也要写成空对象，读出来才能判断「没选设备」。
     */
    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception ex) {
            throw ServiceExceptionUtil.invalidParamException("总任务字段无法写成文本");
        }
    }

    private InspectionContent asContent(Object raw) {
        return convert(raw, InspectionContent.class);
    }

    private ExecutionDeviceBinding asBinding(Object raw) {
        return convert(raw, ExecutionDeviceBinding.class);
    }

    private ResourcePolicy asResource(Object raw) {
        return convert(raw, ResourcePolicy.class);
    }

    private <T> T convert(Object raw, Class<T> type) {
        if (raw == null) {
            return null;
        }
        return objectMapper.convertValue(unwrap(raw), type);
    }

    private Object unwrap(Object raw) {
        if (raw instanceof String text && StringUtils.hasText(text)) {
            try {
                return objectMapper.readValue(text, new TypeReference<Object>() {
                });
            } catch (Exception ex) {
                return raw;
            }
        }
        return raw;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> asMap(Object raw) {
        Object value = unwrap(raw);
        if (value instanceof Map<?, ?> map) {
            return new LinkedHashMap<>((Map<String, Object>) map);
        }
        return new LinkedHashMap<>();
    }

    private static String currentMeans(Map<String, Object> draft) {
        return firstText(draft.get("patrolExecutionMode"));
    }

    private String currentMeansOf(Long taskId) {
        EntityRespDTO existing = optional(entityRpcApi.getEntity(taskId, TASK_TYPE));
        if (existing == null || existing.getId() == null) {
            return null;
        }
        return currentMeans(asMap(bagOf(existing).get(FIELD_DRAFT)));
    }

    private static boolean isMeansScopedDraftKey(String key) {
        return DRAFT_KEY_START.equals(key)
                || DRAFT_KEY_END.equals(key)
                || DRAFT_KEY_UNLOCKED.equals(key)
                || "executionDeviceBinding".equals(key)
                || DRAFT_KEY_ORCHESTRATION_PREVIEW_SLOTS.equals(key)
                || DRAFT_KEY_ORCHESTRATION_PREVIEW_SUMMARY.equals(key);
    }

    /** 旧的一份收进当时那种方式，已经分格的不动。 */
    private static void wrapMeansScopedDraftKeys(Map<String, Object> draft, String ownerMeans) {
        draft.put(DRAFT_KEY_START, PatrolMeansKeyedSupport.ensureMap(draft.get(DRAFT_KEY_START), ownerMeans));
        draft.put(DRAFT_KEY_END, PatrolMeansKeyedSupport.ensureMap(draft.get(DRAFT_KEY_END), ownerMeans));
        draft.put(DRAFT_KEY_UNLOCKED, PatrolMeansKeyedSupport.ensureMap(draft.get(DRAFT_KEY_UNLOCKED), ownerMeans));
        if (draft.get("executionDeviceBinding") != null) {
            draft.put("executionDeviceBinding",
                    PatrolMeansKeyedSupport.ensureMap(draft.get("executionDeviceBinding"), ownerMeans));
        }
        if (draft.get(DRAFT_KEY_ORCHESTRATION_PREVIEW_SLOTS) != null) {
            draft.put(DRAFT_KEY_ORCHESTRATION_PREVIEW_SLOTS,
                    PatrolMeansKeyedSupport.ensureMap(draft.get(DRAFT_KEY_ORCHESTRATION_PREVIEW_SLOTS), ownerMeans));
        }
        if (draft.get(DRAFT_KEY_ORCHESTRATION_PREVIEW_SUMMARY) != null) {
            draft.put(DRAFT_KEY_ORCHESTRATION_PREVIEW_SUMMARY,
                    PatrolMeansKeyedSupport.ensureMap(draft.get(DRAFT_KEY_ORCHESTRATION_PREVIEW_SUMMARY), ownerMeans));
        }
    }

    /**
     * 草稿袋文本键：null 表示这次请求没带，留下原值；空串表示用户清掉。
     */
    private static void applyDraftText(Map<String, Object> draft, String key, String value) {
        if (value == null) {
            return;
        }
        if (StringUtils.hasText(value)) {
            draft.put(key, value.trim());
        } else {
            draft.remove(key);
        }
    }

    /**
     * 缺这个键 = 还没走过创建流程 = 第 0 步。不是按已有路线猜进度。
     */
    private static Integer asInteger(Object raw) {
        if (raw instanceof Number number) {
            return number.intValue();
        }
        if (raw instanceof String text && StringUtils.hasText(text)) {
            try {
                return Integer.parseInt(text.trim());
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private static Integer asUnlockedStep(Object raw) {
        if (raw instanceof Number number) {
            return number.intValue();
        }
        if (raw instanceof String text && StringUtils.hasText(text)) {
            try {
                return Integer.parseInt(text.trim());
            } catch (NumberFormatException ignored) {
                return 0;
            }
        }
        return 0;
    }

    /**
     * 专用列默认是空数组，不是已生成的步骤图；保存草稿时不得把这份空值再写回去盖掉真树。
     */
    static boolean isEmptyStepTreePlaceholder(Object raw) {
        if (raw == null) {
            return true;
        }
        if (raw instanceof List<?> list) {
            return list.isEmpty();
        }
        if (raw instanceof Map<?, ?> map) {
            return map.isEmpty();
        }
        if (raw instanceof String text) {
            String trimmed = text.trim();
            return !StringUtils.hasText(trimmed) || "[]".equals(trimmed) || "{}".equals(trimmed);
        }
        return false;
    }

    /**
     * LONG_TEXT 存 JSON 文本；读出来解析成对象给详情。空数组、空对象当未写入。
     */
    private Object normalizeStepTreeField(Object raw) {
        Object value = unwrap(raw);
        if (value == null) {
            return null;
        }
        if (value instanceof List<?> list) {
            return list.isEmpty() ? null : value;
        }
        if (value instanceof Map<?, ?> map && map.isEmpty()) {
            return null;
        }
        if (value instanceof String text) {
            String trimmed = text.trim();
            if (!StringUtils.hasText(trimmed) || "[]".equals(trimmed) || "{}".equals(trimmed)) {
                return null;
            }
        }
        return value;
    }

    private static Map<String, Object> bagOf(EntityRespDTO entity) {
        Map<String, Object> bag = new LinkedHashMap<>();
        if (entity.getBaseFields() != null) {
            bag.putAll(entity.getBaseFields());
        }
        if (entity.getCustomFields() != null) {
            bag.putAll(entity.getCustomFields());
        }
        return bag;
    }

    private static String text(Object raw) {
        return raw == null ? null : String.valueOf(raw).trim();
    }

    private static String firstText(Object raw) {
        if (raw instanceof Map<?, ?> || raw instanceof List<?>) {
            return null;
        }
        String value = text(raw);
        return StringUtils.hasText(value) ? value : null;
    }

    /**
     * 所属设施只认设施台账引用。写路径交给实体关联同步，读路径只取 id。
     */
    private static Map<String, Object> facilityRef(long facilityId) {
        Map<String, Object> ref = new LinkedHashMap<>(2);
        ref.put("entityTypeCode", FACILITY_TYPE);
        ref.put("id", facilityId);
        return ref;
    }

    private static Boolean asBoolean(Object raw) {
        if (raw == null) {
            return null;
        }
        if (raw instanceof Boolean bool) {
            return bool;
        }
        return Boolean.parseBoolean(String.valueOf(raw));
    }

    private static Long asLong(Object raw) {
        if (raw instanceof Number number) {
            return number.longValue();
        }
        if (raw instanceof Map<?, ?> map) {
            Object id = map.get("id");
            if (id == null) {
                id = map.get("entityId");
            }
            return asLong(id);
        }
        if (raw instanceof String text && StringUtils.hasText(text)) {
            try {
                return Long.parseLong(text.trim());
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private static Long requireId(CommonResult<Long> result, String gap) {
        if (result == null || !result.isSuccess() || result.getData() == null) {
            throw ServiceExceptionUtil.invalidParamException(
                    result != null && StringUtils.hasText(result.getMsg()) ? result.getMsg() : gap);
        }
        return result.getData();
    }

    private static void requireOk(CommonResult<Boolean> result, String gap) {
        if (result == null || !result.isSuccess()) {
            throw ServiceExceptionUtil.invalidParamException(
                    result != null && StringUtils.hasText(result.getMsg()) ? result.getMsg() : gap);
        }
    }

    private static Boolean readOrchestrationCommittedFromDraft(Map<String, Object> draft) {
        if (draft == null || draft.isEmpty()) {
            return null;
        }
        return asBoolean(draft.get(DRAFT_KEY_ORCHESTRATION_COMMITTED));
    }

    private static Object readOrchestrationPreviewRawFromDraft(Map<String, Object> draft) {
        if (draft == null || draft.isEmpty()) {
            return null;
        }
        return draft.get(DRAFT_KEY_ORCHESTRATION_PREVIEW_SLOTS);
    }

    private static String readOrchestrationPreviewSummaryFromDraft(Map<String, Object> draft) {
        if (draft == null || draft.isEmpty()) {
            return null;
        }
        return firstText(draft.get(DRAFT_KEY_ORCHESTRATION_PREVIEW_SUMMARY));
    }

    private static EntityRespDTO optional(CommonResult<EntityRespDTO> result) {
        if (result == null || !result.isSuccess()) {
            return null;
        }
        return result.getData();
    }
}
