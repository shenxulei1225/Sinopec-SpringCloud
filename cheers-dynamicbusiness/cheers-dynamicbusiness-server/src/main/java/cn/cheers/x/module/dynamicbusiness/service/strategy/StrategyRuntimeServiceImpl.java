package cn.cheers.x.module.dynamicbusiness.service.strategy;

import cn.cheers.x.alarm.api.AlarmNotifyApi;
import cn.cheers.x.alarm.api.AlarmTriggerApi;
import cn.cheers.x.alarm.api.dto.AlarmNotifyReqDTO;
import cn.cheers.x.alarm.api.dto.AlarmNotifyRespDTO;
import cn.cheers.x.alarm.api.dto.AlarmTriggerReqDTO;
import cn.cheers.x.alarm.api.dto.AlarmTriggerRespDTO;
import cn.cheers.x.device.protocolgateway.api.DeviceProtocolMissionApi;
import cn.cheers.x.device.protocolgateway.api.dto.MissionStartRespDTO;
import cn.cheers.x.device.protocolgateway.api.mission.DeviceMissionPlan;
import cn.cheers.x.device.protocolgateway.api.mission.DispatchAction;
import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionAppendProcessReqDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionAppendProcessRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStartReqDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStartRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionWritebackReqDTO;
import cn.cheers.x.module.dynamicbusiness.api.strategy.dto.StrategyHandleRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.strategy.dto.StrategyItemDTO;
import cn.cheers.x.module.dynamicbusiness.api.strategy.dto.StrategyTriggerEventDTO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.strategy.ConditionStrategyDO;
import cn.cheers.x.module.dynamicbusiness.service.execution.TaskExecutionSessionService;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 按库里已启用策略匹配事件，并调用已登记动作。
 * <p>上一条的账本编号、告警编号传给后面的策略。发给设备失败则停，后面不得当已经开始。
 * <p>禁止：表空时再偷偷插一条；没有账本编号还去猜设备；未命中却假装已记账或已告警。
 */
@Service
public class StrategyRuntimeServiceImpl implements StrategyRuntimeService {

    @Resource
    private ConditionStrategyService conditionStrategyService;

    @Resource
    private TaskExecutionSessionService taskExecutionSessionService;

    @Resource
    private AlarmTriggerApi alarmTriggerApi;

    @Resource
    private AlarmNotifyApi alarmNotifyApi;

    @Resource
    private DeviceProtocolMissionApi deviceProtocolMissionApi;

    @Override
    public StrategyHandleRespDTO handle(StrategyTriggerEventDTO event) {
        if (event == null || !StringUtils.hasText(event.getEventType())) {
            throw new ServiceException(400, "策略处理必须带事件类型");
        }
        List<ConditionStrategyDO> rows = conditionStrategyService.listVisibleEnabled(event.getEventType().trim());
        if (rows == null || rows.isEmpty()) {
            return skipped("没有已启用的策略");
        }
        StrategyHandleRespDTO acc = null;
        for (ConditionStrategyDO row : rows) {
            if (!StrategyConditionMatcher.matches(row.getConditionJson(), event)) {
                continue;
            }
            StrategyHandleRespDTO hit = dispatch(row, event);
            acc = merge(acc, hit);
            if (hit.getExecutionRecordId() != null) {
                event.setExecutionRecordId(hit.getExecutionRecordId());
            }
            if (hit.getAlarmId() != null) {
                event.setAlarmId(hit.getAlarmId());
            }
            if (Boolean.FALSE.equals(hit.getDispatchSuccess())) {
                return acc;
            }
        }
        if (acc == null) {
            return skipped("没有一条策略的条件成立");
        }
        return acc;
    }

    @Override
    public List<StrategyItemDTO> listPublished() {
        List<StrategyItemDTO> items = new ArrayList<>();
        for (var vo : conditionStrategyService.listVisible()) {
            if (!vo.isEnabled()) {
                continue;
            }
            StrategyItemDTO item = new StrategyItemDTO();
            item.setId(String.valueOf(vo.getId()));
            item.setName(vo.getName());
            item.setEnabled(true);
            item.setEventType(vo.getEventType());
            item.setConditionText(vo.getConditionText());
            item.setActionName(vo.getActionName());
            items.add(item);
        }
        return items;
    }

    private StrategyHandleRespDTO dispatch(ConditionStrategyDO row, StrategyTriggerEventDTO event) {
        String code = row.getActionCode();
        if (RegisteredActions.CREATE_EXECUTION.equals(code)) {
            return dispatchCreateExecution(row, event);
        }
        if (RegisteredActions.DISPATCH_TO_DEVICE.equals(code)) {
            return dispatchToDevice(row, event);
        }
        if (RegisteredActions.UPDATE_EXECUTION_STATUS.equals(code)) {
            return dispatchUpdateExecutionStatus(row, event);
        }
        if (RegisteredActions.UPDATE_STEP_STATUS.equals(code)) {
            return dispatchUpdateStepStatus(row, event);
        }
        if (RegisteredActions.APPEND_PROCESS.equals(code)) {
            return dispatchAppendProcess(row, event);
        }
        if (RegisteredActions.RAISE_ALARM.equals(code)) {
            return dispatchRaiseAlarm(row, event);
        }
        if (RegisteredActions.SEND_NOTIFICATION.equals(code)) {
            return dispatchSendNotification(row, event);
        }
        throw new ServiceException(400, "策略勾了未登记的动作：" + code);
    }

    private StrategyHandleRespDTO dispatchCreateExecution(ConditionStrategyDO row, StrategyTriggerEventDTO event) {
        if (event.getTaskDefinitionId() == null || event.getStandardSnapshot() == null
                || event.getSteps() == null || event.getSteps().isEmpty()) {
            throw new ServiceException(400, "新建这次执行的账必须带是哪条任务、按什么标准做、步骤清单");
        }
        if (!StringUtils.hasText(event.getModelCode())) {
            throw new ServiceException(400, "新建这次执行的账必须带用哪种执行账型号");
        }
        TaskExecutionStartReqDTO req = new TaskExecutionStartReqDTO();
        req.setTaskDefinitionId(event.getTaskDefinitionId());
        req.setEntityTypeCode(event.getEntityTypeCode());
        req.setModelCode(event.getModelCode());
        req.setName(event.getExecutionName());
        req.setStandardSnapshot(event.getStandardSnapshot());
        req.setSteps(parseSteps(event.getSteps()));
        TaskExecutionStartRespDTO started = taskExecutionSessionService.start(req);
        if (started == null || started.getExecutionRecordId() == null) {
            throw new ServiceException(500, "新建这次执行的账没有返回账本编号");
        }
        StrategyHandleRespDTO resp = matched(row);
        resp.setExecutionRecordId(started.getExecutionRecordId());
        return resp;
    }

    private StrategyHandleRespDTO dispatchToDevice(ConditionStrategyDO row, StrategyTriggerEventDTO event) {
        if (!StringUtils.hasText(event.getProtocolVersion()) || !StringUtils.hasText(event.getLogicalDeviceId())) {
            throw new ServiceException(400, "把任务发给设备必须带用哪份协议、哪台设备");
        }
        if (event.getExecutionRecordId() == null) {
            throw new ServiceException(400, "把任务发给设备必须带这次执行的账本编号，不能猜");
        }
        if (event.getDispatchActions() == null || event.getDispatchActions().isEmpty()) {
            throw new ServiceException(400, "把任务发给设备必须带已排好的动作清单");
        }
        String recordId = String.valueOf(event.getExecutionRecordId());
        DeviceMissionPlan plan = new DeviceMissionPlan(
                event.getProtocolVersion().trim(),
                event.getLogicalDeviceId().trim(),
                recordId,
                recordId,
                parseDispatchActions(event.getDispatchActions())
        );
        CommonResult<MissionStartRespDTO> rpc = deviceProtocolMissionApi.dispatchAndStartup(plan);
        if (rpc == null || !rpc.isSuccess() || rpc.getData() == null) {
            throw new ServiceException(
                    rpc != null ? rpc.getCode() : 500,
                    rpc != null && StringUtils.hasText(rpc.getMsg()) ? rpc.getMsg() : "把任务发给设备失败");
        }
        MissionStartRespDTO dispatched = rpc.getData();
        StrategyHandleRespDTO resp = matched(row);
        resp.setExecutionRecordId(event.getExecutionRecordId());
        resp.setDispatchSuccess(dispatched.success());
        resp.setDispatchFailureReason(dispatched.failureReason());
        resp.setDispatchOnline(dispatched.online());
        resp.setDispatchCommandSent(dispatched.commandSent());
        resp.setDispatchStartupSent(dispatched.startupSent());
        resp.setDispatchCommandWireJson(dispatched.commandWireJson());
        return resp;
    }

    private StrategyHandleRespDTO dispatchUpdateExecutionStatus(ConditionStrategyDO row, StrategyTriggerEventDTO event) {
        if (event.getExecutionRecordId() == null) {
            throw new ServiceException(400, "更新这次执行的状态必须带这次执行的账本编号");
        }
        JSONObject params = parseParams(row);
        String status = params != null && StringUtils.hasText(params.getString("executionStatus"))
                ? params.getString("executionStatus").trim()
                : event.getExecutionStatus();
        if (!StringUtils.hasText(status)) {
            throw new ServiceException(400, "更新这次执行的状态必须写明改成待执行、进行中、已完成还是故障");
        }
        TaskExecutionWritebackReqDTO req = new TaskExecutionWritebackReqDTO();
        req.setExecutionRecordId(event.getExecutionRecordId());
        req.setEntityTypeCode(event.getEntityTypeCode());
        req.setExecutionStatus(status.trim());
        taskExecutionSessionService.writeback(req);
        StrategyHandleRespDTO resp = matched(row);
        resp.setExecutionRecordId(event.getExecutionRecordId());
        return resp;
    }

    private StrategyHandleRespDTO dispatchUpdateStepStatus(ConditionStrategyDO row, StrategyTriggerEventDTO event) {
        if (event.getExecutionRecordId() == null) {
            throw new ServiceException(400, "更新某一步的状态必须带这次执行的账本编号");
        }
        if (event.getStepUpdates() == null || event.getStepUpdates().isEmpty()) {
            throw new ServiceException(400, "更新某一步的状态必须带至少一步：步骤编号和新状态");
        }
        TaskExecutionWritebackReqDTO req = new TaskExecutionWritebackReqDTO();
        req.setExecutionRecordId(event.getExecutionRecordId());
        req.setEntityTypeCode(event.getEntityTypeCode());
        req.setStepUpdates(parseStepUpdates(event.getStepUpdates()));
        taskExecutionSessionService.writeback(req);
        StrategyHandleRespDTO resp = matched(row);
        resp.setExecutionRecordId(event.getExecutionRecordId());
        return resp;
    }

    private StrategyHandleRespDTO dispatchAppendProcess(ConditionStrategyDO row, StrategyTriggerEventDTO event) {
        if (event.getExecutionRecordId() == null) {
            throw new ServiceException(400, "往执行账里记一条过程必须带这次执行的账本编号");
        }
        if (!StringUtils.hasText(event.getMessageKind()) || event.getReceivedAtEpochMs() == null
                || !StringUtils.hasText(event.getProtocolQualify())) {
            throw new ServiceException(400, "记过程必须带何时收到、哪类报文、合不合格");
        }
        TaskExecutionAppendProcessReqDTO req = new TaskExecutionAppendProcessReqDTO();
        req.setExecutionRecordId(event.getExecutionRecordId());
        req.setEntityTypeCode(event.getEntityTypeCode());
        req.setReceivedAtEpochMs(event.getReceivedAtEpochMs());
        req.setMessageKind(event.getMessageKind());
        req.setProtocolQualify(event.getProtocolQualify());
        req.setQualifyErrors(event.getQualifyErrors());
        req.setSummary(event.getSummary());
        TaskExecutionAppendProcessRespDTO written = taskExecutionSessionService.appendProcess(req);

        StrategyHandleRespDTO resp = matched(row);
        resp.setExecutionRecordId(event.getExecutionRecordId());
        resp.setProcessEntryCount(written.getProcessEntryCount());
        return resp;
    }

    private StrategyHandleRespDTO dispatchRaiseAlarm(ConditionStrategyDO row, StrategyTriggerEventDTO event) {
        JSONObject params = parseParams(row);
        Long alarmTypeId = params == null ? null : params.getLong("alarmTypeId");
        String alarmLevel = params == null ? null : params.getString("alarmLevel");
        Long deviceId = params == null ? null : params.getLong("deviceId");
        Long locationId = params == null ? null : params.getLong("locationId");
        if (alarmTypeId == null || !StringUtils.hasText(alarmLevel) || deviceId == null || locationId == null) {
            throw new ServiceException(400, "新增一条告警必须在策略上写明告警类型、级别、设备和位置，不能从设备逻辑编号猜");
        }
        AlarmTriggerReqDTO req = new AlarmTriggerReqDTO();
        req.setAlarmTypeId(alarmTypeId);
        req.setAlarmLevel(alarmLevel.trim());
        req.setDeviceId(deviceId);
        req.setLocationId(locationId);
        req.setAlarmContent(resolveAlarmContent(event, row));
        req.setTriggerValue(firstFieldValue(event));
        req.setRuleId(row.getId());
        CommonResult<AlarmTriggerRespDTO> rpc = alarmTriggerApi.trigger(req);
        if (rpc == null || !rpc.isSuccess() || rpc.getData() == null || rpc.getData().getAlarmId() == null) {
            throw new ServiceException(
                    rpc != null ? rpc.getCode() : 500,
                    rpc != null && StringUtils.hasText(rpc.getMsg()) ? rpc.getMsg() : "新增一条告警失败");
        }
        StrategyHandleRespDTO resp = matched(row);
        resp.setAlarmId(rpc.getData().getAlarmId());
        return resp;
    }

    private StrategyHandleRespDTO dispatchSendNotification(ConditionStrategyDO row, StrategyTriggerEventDTO event) {
        Long alarmId = event.getAlarmId();
        if (alarmId == null) {
            throw new ServiceException(400, "给相关人员发通知必须带告警编号，或先做「新增一条告警」");
        }
        List<Long> recipients = resolveRecipients(row, event);
        AlarmNotifyReqDTO req = new AlarmNotifyReqDTO();
        req.setAlarmId(alarmId);
        req.setRecipientUserIds(recipients);
        CommonResult<AlarmNotifyRespDTO> rpc = alarmNotifyApi.notifyRecipients(req);
        if (rpc == null || !rpc.isSuccess() || rpc.getData() == null || rpc.getData().getAlarmId() == null) {
            throw new ServiceException(
                    rpc != null ? rpc.getCode() : 500,
                    rpc != null && StringUtils.hasText(rpc.getMsg()) ? rpc.getMsg() : "给相关人员发通知失败");
        }
        StrategyHandleRespDTO resp = matched(row);
        resp.setAlarmId(rpc.getData().getAlarmId());
        return resp;
    }

    private static List<Long> resolveRecipients(ConditionStrategyDO row, StrategyTriggerEventDTO event) {
        if (event.getRecipientUserIds() != null && !event.getRecipientUserIds().isEmpty()) {
            return event.getRecipientUserIds();
        }
        JSONObject params = parseParams(row);
        if (params == null || !StringUtils.hasText(params.getString("recipientUserIds"))) {
            return List.of();
        }
        List<Long> ids = new ArrayList<>();
        for (String part : params.getString("recipientUserIds").split(",")) {
            if (!StringUtils.hasText(part)) {
                continue;
            }
            try {
                ids.add(Long.parseLong(part.trim()));
            } catch (NumberFormatException ex) {
                throw new ServiceException(400, "接收人用户编号必须是数字：" + part);
            }
        }
        return ids;
    }

    private static List<TaskExecutionStartReqDTO.StepDraft> parseSteps(List<Map<String, Object>> raw) {
        List<TaskExecutionStartReqDTO.StepDraft> steps = new ArrayList<>();
        for (Map<String, Object> item : raw) {
            steps.add(JSON.parseObject(JSON.toJSONString(item), TaskExecutionStartReqDTO.StepDraft.class));
        }
        return steps;
    }

    private static List<DispatchAction> parseDispatchActions(List<Map<String, Object>> raw) {
        List<DispatchAction> actions = new ArrayList<>();
        for (Map<String, Object> item : raw) {
            if (item == null || item.get("actionId") == null) {
                throw new ServiceException(400, "发给设备的每一步都必须有动作编号");
            }
            Long actionId = Long.valueOf(String.valueOf(item.get("actionId")));
            @SuppressWarnings("unchecked")
            Map<String, Object> params = item.get("params") instanceof Map<?, ?> map
                    ? new LinkedHashMap<>((Map<String, Object>) map)
                    : Map.of();
            actions.add(new DispatchAction(actionId, params));
        }
        return actions;
    }

    private static List<TaskExecutionWritebackReqDTO.StepUpdate> parseStepUpdates(List<Map<String, Object>> raw) {
        List<TaskExecutionWritebackReqDTO.StepUpdate> updates = new ArrayList<>();
        for (Map<String, Object> item : raw) {
            updates.add(JSON.parseObject(JSON.toJSONString(item), TaskExecutionWritebackReqDTO.StepUpdate.class));
        }
        return updates;
    }

    private static JSONObject parseParams(ConditionStrategyDO row) {
        return StringUtils.hasText(row.getActionParamsJson())
                ? JSON.parseObject(row.getActionParamsJson())
                : null;
    }

    private static StrategyHandleRespDTO matched(ConditionStrategyDO row) {
        StrategyHandleRespDTO resp = new StrategyHandleRespDTO();
        resp.setMatched(true);
        resp.setStrategyName(row.getName());
        resp.setActionName(RegisteredActions.displayName(row.getActionCode()));
        return resp;
    }

    private static StrategyHandleRespDTO merge(StrategyHandleRespDTO acc, StrategyHandleRespDTO hit) {
        if (acc == null) {
            return hit;
        }
        acc.setMatched(true);
        acc.setStrategyName(hit.getStrategyName());
        acc.setActionName(hit.getActionName());
        if (hit.getExecutionRecordId() != null) {
            acc.setExecutionRecordId(hit.getExecutionRecordId());
        }
        if (hit.getAlarmId() != null) {
            acc.setAlarmId(hit.getAlarmId());
        }
        if (hit.getProcessEntryCount() != null) {
            acc.setProcessEntryCount(hit.getProcessEntryCount());
        }
        if (hit.getDispatchSuccess() != null) {
            acc.setDispatchSuccess(hit.getDispatchSuccess());
            acc.setDispatchFailureReason(hit.getDispatchFailureReason());
            acc.setDispatchOnline(hit.getDispatchOnline());
            acc.setDispatchCommandSent(hit.getDispatchCommandSent());
            acc.setDispatchStartupSent(hit.getDispatchStartupSent());
            acc.setDispatchCommandWireJson(hit.getDispatchCommandWireJson());
        }
        return acc;
    }

    private static String resolveAlarmContent(StrategyTriggerEventDTO event, ConditionStrategyDO row) {
        if (StringUtils.hasText(event.getSummary())) {
            return event.getSummary().trim();
        }
        return "策略「" + row.getName() + "」命中";
    }

    private static String firstFieldValue(StrategyTriggerEventDTO event) {
        if (event.getFields() == null || event.getFields().isEmpty()) {
            return null;
        }
        Object first = event.getFields().values().iterator().next();
        return first == null ? null : String.valueOf(first);
    }

    private static StrategyHandleRespDTO skipped(String reason) {
        StrategyHandleRespDTO resp = new StrategyHandleRespDTO();
        resp.setMatched(false);
        resp.setSkipReason(reason);
        return resp;
    }
}
