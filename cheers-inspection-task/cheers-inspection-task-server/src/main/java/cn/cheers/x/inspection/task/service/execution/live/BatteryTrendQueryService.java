package cn.cheers.x.inspection.task.service.execution.live;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.task.controller.admin.vo.execution.BatteryTrendPointVO;
import cn.cheers.x.inspection.task.controller.admin.vo.execution.BatteryTrendRespVO;
import cn.cheers.x.inspection.task.model.task.ExecutionDeviceBinding;
import cn.cheers.x.inspection.task.service.execution.impl.InspectionTaskStartExecutionServiceImpl;
import cn.cheers.x.inspection.task.service.execution.openrun.BatteryHeadGate;
import cn.cheers.x.inspection.task.service.execution.openrun.EquipmentFlightBudgetQuery;
import cn.cheers.x.inspection.task.service.execution.openrun.PatrolOpenRunSnapshotSupport;
import cn.cheers.x.inspection.task.service.task.PatrolTaskDraft;
import cn.cheers.x.inspection.task.service.task.PatrolTaskDurationSupport;
import cn.cheers.x.inspection.task.service.task.PatrolTaskEntityStore;
import cn.cheers.x.module.dynamicbusiness.api.entity.EntityRpcApi;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 场站实时监控侧栏底部电量走势图的查询。
 * <p>管什么：按这笔执行账读出预计线和实际线。预计读任务总耗时和设备最长飞行时间。这一台空着时读型号默认值。没有曲线时按最长飞行时间匀速下降。起点读这笔账快照上那台设备刚报上的当前电量。已经有步骤回传时用最早那一点。实际读步骤回传。
 * <p>不管什么：不开跑，不改步骤结果，不写电量。
 * <p>禁止：设备和型号都没有最长飞行时间时编满电秒数。不编一条曲线写回设备。没报到当前电量时不从满电起算。
 */
@Service
public class BatteryTrendQueryService {

    private final EntityRpcApi entityRpcApi;
    private final PatrolTaskEntityStore patrolTaskEntityStore;
    private final BatteryLiveElectricityQuery batteryLiveElectricityQuery;
    private final EquipmentFlightBudgetQuery equipmentFlightBudgetQuery;

    public BatteryTrendQueryService(
            EntityRpcApi entityRpcApi,
            PatrolTaskEntityStore patrolTaskEntityStore,
            BatteryLiveElectricityQuery batteryLiveElectricityQuery,
            EquipmentFlightBudgetQuery equipmentFlightBudgetQuery) {
        this.entityRpcApi = entityRpcApi;
        this.patrolTaskEntityStore = patrolTaskEntityStore;
        this.batteryLiveElectricityQuery = batteryLiveElectricityQuery;
        this.equipmentFlightBudgetQuery = equipmentFlightBudgetQuery;
    }

    /**
     * 点中一条巡检账之后读这两条线。账不存在就停。
     */
    public BatteryTrendRespVO load(Long executionRecordId) {
        if (executionRecordId == null || executionRecordId <= 0) {
            throw ServiceExceptionUtil.invalidParamException("要先点一条巡检");
        }
        EntityRespDTO record = requireRecord(executionRecordId);
        Map<String, Object> fields = record.getCustomFields() == null ? Map.of() : record.getCustomFields();
        BatteryTrendRespVO resp = new BatteryTrendRespVO();
        List<BatteryTrendPointVO> actual = BatteryTrendActual.fromStepResults(fields.get("step_results"));
        resp.setActual(actual);
        resp.setAbnormal(abnormal(fields.get("execution_status")));
        if (actual.isEmpty()) {
            resp.setActualMissingReason("这次还没有步骤回传电量。");
        }
        fillEstimated(resp, fields, actual);
        return resp;
    }

    private void fillEstimated(
            BatteryTrendRespVO resp, Map<String, Object> fields, List<BatteryTrendPointVO> actual) {
        Long taskId = longOf(fields.get("task_id"));
        if (taskId == null) {
            resp.setEstimatedMissingReason("这笔账没有记下所属任务，画不出预计曲线。");
            return;
        }
        PatrolTaskDraft task;
        try {
            task = patrolTaskEntityStore.require(taskId);
        } catch (RuntimeException ex) {
            resp.setEstimatedMissingReason("读不到这条任务，画不出预计曲线。");
            return;
        }
        Integer requiredSeconds = BatteryHeadGate.requiredSeconds(task.stepTree());
        if (requiredSeconds == null) {
            requiredSeconds = PatrolTaskDurationSupport.totalSeconds(
                    task.inspectionContent(), task.plannedRoute(), task.patrolExecutionMode());
        }
        if (requiredSeconds == null || requiredSeconds <= 0) {
            resp.setEstimatedMissingReason("任务没有记下总耗时。");
            return;
        }
        ExecutionDeviceBinding binding = liveDevice(fields.get("standard_snapshot"), task.executionDeviceBinding());
        Long equipmentId = binding == null ? null : binding.getEquipmentId();
        EquipmentFlightBudgetQuery.Snapshot budget = equipmentFlightBudgetQuery.load(equipmentId);
        if (budget.fullBatterySeconds() == null) {
            resp.setEstimatedMissingReason("这台设备没有最长飞行时间。");
            return;
        }
        String start = startElectricity(actual, binding, budget);
        if (!StringUtils.hasText(start)) {
            resp.setEstimatedMissingReason("还没有收到这台设备的当前电量。");
            return;
        }
        List<BatteryTrendPointVO> estimated = BatteryTrendEstimate.points(
                start, requiredSeconds, budget.fullBatterySeconds(), budget.dischargeCurve());
        if (estimated.isEmpty()) {
            resp.setEstimatedMissingReason("按耗电曲线算不出预计电量。");
            return;
        }
        resp.setEstimated(estimated);
    }

    /**
     * 已经有实际点时从最早那一点起算。还没有时读设备刚报上的电量。没报到就不画预计线。
     */
    private String startElectricity(
            List<BatteryTrendPointVO> actual,
            ExecutionDeviceBinding binding,
            EquipmentFlightBudgetQuery.Snapshot budget) {
        if (!actual.isEmpty()) {
            return String.valueOf(actual.get(0).getRemainingPercent());
        }
        return batteryLiveElectricityQuery.currentPercent(
                binding, budget.fullBatterySeconds(), budget.dischargeCurve());
    }

    /**
     * 这笔账快照上的设备优先。任务当前方式那一格可能已经换成另一台。
     */
    private static ExecutionDeviceBinding liveDevice(Object snapshotRaw, ExecutionDeviceBinding taskBinding) {
        Map<String, Object> snapshot = snapshotOf(snapshotRaw);
        String protocol = text(snapshot.get(PatrolOpenRunSnapshotSupport.KEY_PROTOCOL_VERSION));
        String deviceId = text(snapshot.get(PatrolOpenRunSnapshotSupport.KEY_LOGICAL_DEVICE_ID));
        if (!StringUtils.hasText(protocol) && !StringUtils.hasText(deviceId)) {
            return taskBinding;
        }
        ExecutionDeviceBinding live = new ExecutionDeviceBinding();
        if (taskBinding != null) {
            live.setEquipmentId(taskBinding.getEquipmentId());
            if (!StringUtils.hasText(protocol)) {
                protocol = text(taskBinding.getProtocolCode());
            }
            if (!StringUtils.hasText(deviceId)) {
                deviceId = text(taskBinding.getLogicalDeviceId());
            }
        }
        live.setProtocolCode(protocol);
        live.setLogicalDeviceId(deviceId);
        return live;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> snapshotOf(Object raw) {
        if (raw instanceof Map<?, ?> map) {
            Map<String, Object> copy = new LinkedHashMap<>();
            map.forEach((key, value) -> copy.put(String.valueOf(key), value));
            return copy;
        }
        return Map.of();
    }

    private EntityRespDTO requireRecord(Long executionRecordId) {
        CommonResult<EntityRespDTO> loaded = entityRpcApi.getEntity(
                executionRecordId, InspectionTaskStartExecutionServiceImpl.PATROL_RECORD_TYPE);
        if (loaded == null || !loaded.isSuccess() || loaded.getData() == null) {
            throw ServiceExceptionUtil.invalidParamException("这次巡检账不存在");
        }
        return loaded.getData();
    }

    private static boolean abnormal(Object status) {
        String text = text(status);
        return "fault".equalsIgnoreCase(text) || "failed".equalsIgnoreCase(text);
    }

    private static Long longOf(Object raw) {
        if (raw instanceof Number number) {
            return number.longValue();
        }
        if (raw == null) {
            return null;
        }
        try {
            return Long.valueOf(String.valueOf(raw).trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static String text(Object raw) {
        return raw == null ? "" : String.valueOf(raw).trim();
    }

}
