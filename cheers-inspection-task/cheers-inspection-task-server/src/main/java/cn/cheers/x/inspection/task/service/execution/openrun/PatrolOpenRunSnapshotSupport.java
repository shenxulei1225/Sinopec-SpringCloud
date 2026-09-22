package cn.cheers.x.inspection.task.service.execution.openrun;

import cn.cheers.x.device.protocolgateway.api.mission.DispatchAction;
import cn.cheers.x.inspection.task.model.task.ExecutionDeviceBinding;
import cn.cheers.x.inspection.task.service.execution.steptree.TaskStepTreeAssembler;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStartReqDTO;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 待执行开跑快照：任务域只存动作 + 已解析参数 + 对步绑定；不含协议线包。
 * <p>协议翻译仍在开跑时由网关做，本类禁止写入 500104 / 内层 opcode。
 */
public final class PatrolOpenRunSnapshotSupport {

    public static final String SOURCE_MATERIALIZE = "patrol-pending-materialize";
    public static final String SOURCE_START = "inspection-start-execution";

    public static final String KEY_SOURCE = "source";
    public static final String KEY_TASK_DEFINITION_ID = "taskDefinitionId";
    public static final String KEY_PROTOCOL_VERSION = "protocolVersion";
    public static final String KEY_LOGICAL_DEVICE_ID = "logicalDeviceId";
    public static final String KEY_DISPATCH_ACTION_COUNT = "dispatchActionCount";
    public static final String KEY_STEP_BINDINGS = "stepBindings";
    public static final String KEY_DISPATCH_ACTIONS = "dispatchActions";
    public static final String KEY_MATERIALIZED_AT = "materializedAtEpochMs";

    private PatrolOpenRunSnapshotSupport() {
    }

    public static Map<String, Object> buildStandardSnapshot(
            Long taskDefinitionId,
            ExecutionDeviceBinding binding,
            TaskStepTreeAssembler.AssembledOpenRun assembled,
            String source
    ) {
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put(KEY_SOURCE, source);
        snapshot.put(KEY_TASK_DEFINITION_ID, taskDefinitionId);
        snapshot.put(KEY_PROTOCOL_VERSION, binding.getProtocolCode().trim());
        snapshot.put(KEY_LOGICAL_DEVICE_ID, binding.getLogicalDeviceId().trim());
        snapshot.put(KEY_DISPATCH_ACTION_COUNT, assembled.dispatchActions().size());
        snapshot.put(KEY_STEP_BINDINGS, bindingsToMaps(assembled.bindings()));
        snapshot.put(KEY_DISPATCH_ACTIONS, toActionMaps(assembled.dispatchActions()));
        if (SOURCE_MATERIALIZE.equals(source)) {
            snapshot.put(KEY_MATERIALIZED_AT, System.currentTimeMillis());
        }
        return snapshot;
    }

    public static List<DispatchAction> parseDispatchActions(Object standardSnapshot) {
        if (!(standardSnapshot instanceof Map<?, ?> snapshot)) {
            throw new IllegalArgumentException("待执行快照缺少 standard_snapshot");
        }
        Object raw = snapshot.get(KEY_DISPATCH_ACTIONS);
        if (!(raw instanceof List<?> list) || list.isEmpty()) {
            throw new IllegalArgumentException("待执行快照缺少可下发的动作清单");
        }
        List<DispatchAction> actions = new ArrayList<>();
        for (Object item : list) {
            if (!(item instanceof Map<?, ?> map)) {
                continue;
            }
            Object actionId = map.get("actionId");
            if (!(actionId instanceof Number number)) {
                throw new IllegalArgumentException("待执行快照动作缺少 actionId");
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> params = map.get("params") instanceof Map<?, ?> paramMap
                    ? copyMap(paramMap)
                    : Map.of();
            actions.add(new DispatchAction(number.longValue(), params));
        }
        if (actions.isEmpty()) {
            throw new IllegalArgumentException("待执行快照没有可下发的动作");
        }
        return List.copyOf(actions);
    }

    public static List<Map<String, Object>> toMaps(List<TaskExecutionStartReqDTO.StepDraft> steps) {
        List<Map<String, Object>> maps = new ArrayList<>();
        for (TaskExecutionStartReqDTO.StepDraft step : steps) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("name", step.getName());
            item.put("stepCode", step.getStepCode());
            item.put("stepOrder", step.getStepOrder());
            item.put("stepTitle", step.getStepTitle());
            item.put("stepType", step.getStepType());
            item.put("stepRequired", step.getStepRequired());
            item.put("parentStepCode", step.getParentStepCode());
            item.put("source", step.getSource());
            maps.add(item);
        }
        return maps;
    }

    public static List<Map<String, Object>> toActionMaps(List<DispatchAction> actions) {
        List<Map<String, Object>> maps = new ArrayList<>();
        for (DispatchAction action : actions) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("actionId", action.actionId());
            item.put("params", action.params() == null ? Map.of() : action.params());
            maps.add(item);
        }
        return maps;
    }

    private static List<Map<String, Object>> bindingsToMaps(List<TaskStepTreeAssembler.StepBinding> bindings) {
        List<Map<String, Object>> maps = new ArrayList<>();
        for (TaskStepTreeAssembler.StepBinding binding : bindings) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("stepCode", binding.stepCode());
            item.put("sequence", binding.sequence());
            item.put("actionId", binding.actionId());
            item.put("inspectionItemId", binding.inspectionItemId());
            maps.add(item);
        }
        return maps;
    }

    private static Map<String, Object> copyMap(Map<?, ?> source) {
        Map<String, Object> copy = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : source.entrySet()) {
            if (entry.getKey() != null) {
                copy.put(String.valueOf(entry.getKey()), entry.getValue());
            }
        }
        return copy;
    }
}
