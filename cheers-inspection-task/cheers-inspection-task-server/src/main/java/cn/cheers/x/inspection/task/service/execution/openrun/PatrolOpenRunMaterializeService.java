package cn.cheers.x.inspection.task.service.execution.openrun;

import cn.cheers.x.device.protocolgateway.api.mission.DispatchAction;
import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.task.model.task.ExecutionDeviceBinding;
import cn.cheers.x.inspection.task.service.execution.steptree.PatrolTaskStepTreeReader;
import cn.cheers.x.inspection.task.service.execution.steptree.InspectedHostPackIndex;
import cn.cheers.x.inspection.task.service.execution.steptree.TaskStepNode;
import cn.cheers.x.inspection.task.service.execution.steptree.TaskStepTreeAssembler;
import cn.cheers.x.inspection.task.service.execution.steptree.TaskStepTreeParser;
import cn.cheers.x.inspection.task.service.task.PatrolTaskDraft;
import cn.cheers.x.inspection.task.service.task.PatrolTaskEntityStore;
import cn.cheers.x.module.dynamicbusiness.api.entity.EntityRpcApi;
import com.fasterxml.jackson.databind.ObjectMapper;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.TaskExecutionSessionApi;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStartReqDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStartRespDTO;
import cn.cheers.x.module.platform.contract.dto.slot.ScheduleSlotDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

import static cn.cheers.x.inspection.task.service.execution.impl.InspectionDeviceUplinkServiceImpl.PATROL_RECORD_TYPE;
import static cn.cheers.x.inspection.task.service.execution.impl.InspectionTaskStartExecutionServiceImpl.PATROL_EXEC_MODEL_CODE;
import static cn.cheers.x.inspection.task.service.execution.openrun.PatrolOpenRunSnapshotSupport.SOURCE_MATERIALIZE;

/**
 * commitOrchestration（生成任务）占窗定稿后写「待执行」：在此解析动作参数并写入执行账快照；开跑只读快照。
 * <p>负责：assemble（读步骤图 + 被检参数包）；为每个计划点建 pending 执行记录；草稿袋记录 slot→recordId。
 * <p>不负责：protocol_mapping 翻译、500104 线包（网关开跑时做）。
 * <p>禁止：开跑时再读 host pack 重算参数；把协议 opcode 写进步骤树；
 * 待执行账所属场站从请求头猜，不写总任务已有的场站。
 */
@Service
@RequiredArgsConstructor
public class PatrolOpenRunMaterializeService {

    /** 与 dynamicbusiness 执行账 custom_fields.execution_status 取值对齐，禁止跨模块依赖 server 实现类 */
    private static final String EXECUTION_STATUS_PENDING = "pending";
    /** 与 {@link PatrolScheduleSlotExecutionWritebackService} 写入字段一致 */
    private static final String PENDING_EXECUTION_ID_FIELD = "pending_execution_id";

    private final PatrolTaskEntityStore patrolTaskEntityStore;
    private final PatrolTaskStepTreeReader taskStepTreeCatalog;
    private final TaskExecutionSessionApi taskExecutionSessionApi;
    private final EntityRpcApi entityRpcApi;
    private final ObjectMapper objectMapper;

    /**
     * 生成任务成功后，为每个计划时刻写 pending 执行账（动作参数在此冻结）。
     */
    public void materializeAllSlots(PatrolTaskDraft task, List<ScheduleSlotDTO> slots) {
        if (task == null || task.id() == null || CollectionUtils.isEmpty(slots)) {
            return;
        }
        TaskStepTreeAssembler.AssembledOpenRun assembled = assembleForTask(task);
        ExecutionDeviceBinding binding = requireCompleteBinding(task);
        for (ScheduleSlotDTO slot : slots) {
            if (slot == null || !StringUtils.hasText(slot.getSlotId())) {
                continue;
            }
            materializePendingRecord(task, binding, assembled, slot.getSlotId().trim());
        }
    }

    /**
     * 开跑前读取已物化的 pending 快照；缺快照则显式报错（不现场 assemble）。
     */
    public FrozenOpenRun requireFrozen(PatrolTaskDraft task, String slotId) {
        if (task == null || task.id() == null) {
            throw ServiceExceptionUtil.invalidParamException("任务不存在");
        }
        if (!StringUtils.hasText(slotId)) {
            throw ServiceExceptionUtil.invalidParamException("开跑必须指定计划点，或请先生成任务");
        }
        Long recordId = patrolTaskEntityStore.pendingExecutionRecordId(task.id(), slotId.trim());
        if (recordId == null) {
            throw ServiceExceptionUtil.invalidParamException(
                    "该计划点尚未准备好待执行记录，请先生成任务");
        }
        EntityRespDTO record = entityRpcApi.getEntity(recordId, PATROL_RECORD_TYPE).getCheckedData();
        if (record == null || record.getCustomFields() == null) {
            throw ServiceExceptionUtil.invalidParamException("待执行执行账不存在：" + recordId);
        }
        String status = text(record.getCustomFields().get("execution_status"));
        if (!EXECUTION_STATUS_PENDING.equalsIgnoreCase(status)) {
            throw ServiceExceptionUtil.invalidParamException(
                    "该计划点对应的执行账已不是待执行状态，不能重复开跑");
        }
        Object pendingRef = record.getCustomFields().get(PENDING_EXECUTION_ID_FIELD);
        if (pendingRef != null && !slotId.trim().equals(String.valueOf(pendingRef).trim())) {
            throw ServiceExceptionUtil.invalidParamException("待执行执行账与计划点不匹配");
        }
        Object standardSnapshot = record.getCustomFields().get("standard_snapshot");
        List<DispatchAction> dispatchActions = PatrolOpenRunSnapshotSupport.parseDispatchActions(standardSnapshot);
        ExecutionDeviceBinding binding = requireCompleteBinding(task);
        return new FrozenOpenRun(recordId, binding, dispatchActions, standardSnapshot);
    }

    TaskStepTreeAssembler.AssembledOpenRun assembleForTask(PatrolTaskDraft task) {
        // previewOrchestration 刚 generate 并 writeStepTree 后传入的 draft，优先消费内存态，避免二次 RPC 读不到刚写入的树
        List<TaskStepNode> nodes = TaskStepTreeParser.parse(task.stepTree(), objectMapper);
        if (nodes.isEmpty()) {
            nodes = taskStepTreeCatalog.loadPatrolTaskStepTree(task.id());
        }
        InspectedHostPackIndex packs = InspectedHostPackIndex.from(
                task.inspectionContent(), taskStepTreeCatalog::loadHostPack);
        return TaskStepTreeAssembler.assemble(
                nodes, packs::packForItem, taskStepTreeCatalog::resolveActionId);
    }

    private void materializePendingRecord(
            PatrolTaskDraft task,
            ExecutionDeviceBinding binding,
            TaskStepTreeAssembler.AssembledOpenRun assembled,
            String slotId
    ) {
        Long existing = patrolTaskEntityStore.pendingExecutionRecordId(task.id(), slotId);
        if (existing != null) {
            EntityRespDTO record = optionalRecord(existing);
            if (record != null && isPending(record)) {
                return;
            }
        }

        if (task.facilityId() == null) {
            throw ServiceExceptionUtil.invalidParamException("任务没有所属场站，不能生成待执行");
        }
        TaskExecutionStartReqDTO req = new TaskExecutionStartReqDTO();
        req.setTaskDefinitionId(task.id());
        req.setEntityTypeCode(PATROL_RECORD_TYPE);
        req.setModelCode(PATROL_EXEC_MODEL_CODE);
        req.setPendingRef(slotId);
        req.setFacilityId(task.facilityId());
        req.setName(buildExecutionName(task, slotId));
        req.setStandardSnapshot(PatrolOpenRunSnapshotSupport.buildStandardSnapshot(
                task.id(), binding, assembled, SOURCE_MATERIALIZE));
        req.setSteps(assembled.sessionSteps());

        CommonResult<TaskExecutionStartRespDTO> started = taskExecutionSessionApi.start(req);
        if (started == null || !started.isSuccess() || started.getData() == null
                || started.getData().getExecutionRecordId() == null) {
            throw ServiceExceptionUtil.invalidParamException(
                    started != null && StringUtils.hasText(started.getMsg())
                            ? started.getMsg()
                            : "物化待执行执行账失败");
        }
        patrolTaskEntityStore.putPendingExecutionRecordId(
                task.id(), slotId, started.getData().getExecutionRecordId());
    }

    private static String buildExecutionName(PatrolTaskDraft task, String slotId) {
        String base = StringUtils.hasText(task.name()) ? task.name().trim() : "巡检执行-" + task.id();
        return base + "-待执行-" + slotId;
    }

    private EntityRespDTO optionalRecord(Long recordId) {
        CommonResult<EntityRespDTO> result = entityRpcApi.getEntity(recordId, PATROL_RECORD_TYPE);
        if (result == null || !result.isSuccess()) {
            return null;
        }
        return result.getData();
    }

    private static boolean isPending(EntityRespDTO record) {
        if (record.getCustomFields() == null) {
            return false;
        }
        return EXECUTION_STATUS_PENDING.equalsIgnoreCase(text(record.getCustomFields().get("execution_status")));
    }

    private static ExecutionDeviceBinding requireCompleteBinding(PatrolTaskDraft task) {
        ExecutionDeviceBinding binding = task.executionDeviceBinding();
        if (binding == null
                || binding.getEquipmentId() == null
                || !StringUtils.hasText(binding.getProtocolCode())
                || !StringUtils.hasText(binding.getLogicalDeviceId())) {
            throw ServiceExceptionUtil.invalidParamException(
                    "任务未绑定执行设备（须含 equipmentId、protocolCode、logicalDeviceId）");
        }
        return binding;
    }

    private static String text(Object raw) {
        return raw == null ? null : String.valueOf(raw).trim();
    }

    /**
     * @param executionRecordId  物化时已建的 pending 执行账
     * @param dispatchActions    冻结的动作 + 业务参数（网关开跑时再翻译）
     * @param standardSnapshot   执行账 custom_fields.standard_snapshot 原文
     */
    public record FrozenOpenRun(
            Long executionRecordId,
            ExecutionDeviceBinding binding,
            List<DispatchAction> dispatchActions,
            Object standardSnapshot
    ) {
    }
}
