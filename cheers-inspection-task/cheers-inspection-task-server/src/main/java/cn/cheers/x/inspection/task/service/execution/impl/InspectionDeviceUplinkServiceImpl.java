package cn.cheers.x.inspection.task.service.execution.impl;

import cn.cheers.x.device.protocolgateway.api.channel.AccessChannelCodes;
import cn.cheers.x.device.protocolgateway.api.datacollection.CollectionSample;
import cn.cheers.x.device.protocolgateway.api.datacollection.ProtocolQualifyStatus;
import cn.cheers.x.device.protocolgateway.api.opcode.DeviceTaskStatusCode;
import cn.cheers.x.device.protocolgateway.api.opcode.TransportOpcode;
import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.task.api.dto.ReplayPoseReqDTO;
import cn.cheers.x.inspection.task.api.dto.TaskExchangeTranscriptReqDTO;
import cn.cheers.x.inspection.task.service.execution.ExecutionEventLedger;
import cn.cheers.x.inspection.task.service.execution.InspectionDeviceUplinkService;
import cn.cheers.x.inspection.task.service.execution.monitor.TaskExchangeTranscriptService;
import cn.cheers.x.inspection.task.service.execution.analysis.PatrolLiveStreamWatch;
import cn.cheers.x.inspection.task.service.execution.pagepush.InspectionExecutionPagePublisher;
import cn.cheers.x.module.dynamicbusiness.api.push.InspectionPushTopics;
import cn.cheers.x.inspection.task.service.execution.analysis.PatrolPhotoAnalysisService;
import cn.cheers.x.inspection.task.service.execution.scheduleboard.PatrolScheduleSlotExecutionWritebackService;
import cn.cheers.x.inspection.task.service.execution.steptree.TaskStepTreeAssembler;
import cn.cheers.x.module.dynamicbusiness.api.strategy.StrategyRuntimeApi;
import cn.cheers.x.module.dynamicbusiness.api.strategy.dto.StrategyHandleRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.strategy.dto.StrategyTriggerEventDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 巡检采集适配：只处理标准事件。
 * <p>标准事件按报文种类处理：轨迹记点，步骤按序号和内层操作码对回，整次状态按状态码。
 * 设备实时状态只发给页面，不记过程账。带解析结果的标准事件另发一条上报给指令监控，不并进下发那一行。
 * 轨迹先按名字规则发给页面，再另记点。改某一步、改整次状态仍由条件策略做。
 * 操作码样本不进这里。
 * 策略把步骤写上之后，把这次步骤结果交给页面推送。推送失败不回滚这次上报。
 * 拍照步写上之后，跑冒滴漏检查项若配了视频智能分析，交给分析提交去送图；送图失败不回滚这次上报。
 * 任务报成正在执行后，记下收流名称，供流媒体网关抽当前画面。这里不解码视频。
 * 设备故障上报另外记一条异常，挂在对上的那台设备上，不进分类。
 * <p>不负责：拆原始报文、按字段说明核对、工业通道、从设备路径读纬度。
 * <p>禁止：执行记录 id 空了猜设备；不合格还把某一步标成完成；用上报点位对步骤；按操作码再拆一遍报文。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InspectionDeviceUplinkServiceImpl implements InspectionDeviceUplinkService {

    public static final String PATROL_RECORD_TYPE = "task_record_patrol";

    private final StrategyRuntimeApi strategyRuntimeApi;
    private final ObjectMapper objectMapper;
    private final PatrolScheduleSlotExecutionWritebackService scheduleSlotExecutionWritebackService;
    private final DeviceCollectionAnomalyRecorder deviceCollectionAnomalyRecorder;
    private final PatrolPhotoAnalysisService patrolPhotoAnalysisService;
    private final PatrolLiveStreamWatch patrolLiveStreamWatch;
    private final InspectionExecutionPagePublisher pagePublisher;
    private final TaskExchangeTranscriptService taskExchangeTranscriptService;
    private final ExecutionEventLedger executionEventLedger;

    @Override
    public void applyCollection(CollectionSample sample) {
        if (sample == null) {
            return;
        }
        if (!AccessChannelCodes.INSPECTION.equals(sample.channelCode())) {
            log.error("[patrol-uplink] 非巡检通道不得写入巡检回写 channel={} deviceId={}",
                    sample.channelCode(), sample.deviceId());
            return;
        }
        if (!isStandardEvent(sample.messageKind())) {
            log.error("[patrol-uplink] 上报没有收成标准事件，巡检不处理 messageKind={} deviceId={}",
                    sample.messageKind(), sample.deviceId());
            return;
        }
        // 指令监控里这一行是上报。字段用网关已经按说明书解析好的那一份。没有执行记录或没有字段就不发。
        publishReportedResult(sample);
        if (InspectionPushTopics.DEVICE_STATUS.code().equals(sample.messageKind())) {
            pagePublisher.publishDeviceStatus(sample.deviceId(), sample.fields());
            return;
        }
        if (InspectionPushTopics.TRAJECTORY.code().equals(sample.messageKind())) {
            recordTrajectory(sample);
        }
        try {
            executionEventLedger.record(sample);
        } catch (RuntimeException ex) {
            log.error("[patrol-uplink] 标准事件没有记入过程 executionRecordId={} reason={}",
                    sample.executionRecordId(), ex.getMessage());
            throw ex;
        }
        StrategyTriggerEventDTO event = buildCollectionEvent(sample);
        // 过程行只由执行账写。策略再记一条时没有中文名称，监控里会多出英文主题。
        event.setProcessAlreadyOnLedger(true);
        final StrategyTriggerEventDTO handledEvent = event;
        CommonResult<StrategyHandleRespDTO> handled = strategyRuntimeApi.handle(event);
        if (handled == null || !handled.isSuccess()) {
            throw new ServiceException(
                    handled != null ? handled.getCode() : 500,
                    handled != null && StringUtils.hasText(handled.getMsg())
                            ? handled.getMsg()
                            : "采集结果交给策略失败");
        }
        StrategyHandleRespDTO data = handled.getData();
        if (data != null && data.isMatched()) {
            log.info("[patrol-uplink] 策略已处理 executionRecordId={} action={}",
                    sample.executionRecordId(), data.getActionName());
            try {
                scheduleSlotExecutionWritebackService.syncAfterStrategyHandle(handledEvent);
            } catch (Exception ex) {
                log.error("[patrol-uplink] 计划点写回失败 executionRecordId={} reason={}",
                        sample.executionRecordId(), ex.getMessage());
                throw ex;
            }
            try {
                pagePublisher.publishSteps(handledEvent);
            } catch (RuntimeException ex) {
                log.error("[patrol-uplink] 步骤结果没有推到页面 executionRecordId={} reason={}",
                        sample.executionRecordId(), ex.getMessage());
            }
            try {
                patrolPhotoAnalysisService.submitAfterReported(handledEvent);
            } catch (RuntimeException ex) {
                log.error("[patrol-uplink] 跑冒滴漏分析提交失败 executionRecordId={} reason={}",
                        sample.executionRecordId(), ex.getMessage());
            }
            try {
                patrolLiveStreamWatch.onExecutionStatus(
                        handledEvent.getExecutionRecordId(), handledEvent.getExecutionStatus());
            } catch (RuntimeException ex) {
                log.info("[patrol-uplink] 收流抽帧没有挂上 executionRecordId={} reason={}",
                        sample.executionRecordId(), ex.getMessage());
            }
        } else {
            log.debug("[patrol-uplink] 策略未命中 skipReason={}",
                    data != null ? data.getSkipReason() : null);
        }
        deviceCollectionAnomalyRecorder.record(sample);
    }

    /**
     * 带结果的反馈在指令监控里单独一行，状态是上报。
     * 悬浮用这些已经解析的字段。没有执行记录编号，或字段是空的，这一行不发。
     */
    private void publishReportedResult(CollectionSample sample) {
        if (sample.executionRecordId() == null || sample.executionRecordId() <= 0L) {
            return;
        }
        if (sample.fields() == null || sample.fields().isEmpty()) {
            return;
        }
        try {
            pagePublisher.publishProcessNotice(
                    sample.messageKind(),
                    sample.executionRecordId(),
                    sample.deviceId(),
                    InspectionPushTopics.name(sample.messageKind()),
                    "上报",
                    null,
                    sample.msgId(),
                    TaskExchangeTranscriptReqDTO.INBOUND,
                    sample.receivedAtEpochMs(),
                    sample.fields());
        } catch (RuntimeException ex) {
            log.error("[patrol-uplink] 上报没有推到指令监控 executionRecordId={} reason={}",
                    sample.executionRecordId(), ex.getMessage());
        }
    }

    /**
     * 轨迹只认转发名 lat、lng、height。没有执行记录编号就不发、不记，不用设备编号去猜。
     * 先按已经存好的名字规则发给页面，再把点记到这次执行上。记点失败不收回已经发出的这一帧。
     */
    private void recordTrajectory(CollectionSample sample) {
        if (!sample.isProtocolQualified()) {
            return;
        }
        Double lat = decimal(sample.fields().get("lat"));
        Double lng = decimal(sample.fields().get("lng"));
        Double height = decimal(sample.fields().get("height"));
        if (lat == null || lng == null || height == null) {
            log.error("[patrol-uplink] 轨迹事件缺纬度、经度或高度，不记点 deviceId={}", sample.deviceId());
            return;
        }
        if (sample.executionRecordId() == null || sample.executionRecordId() <= 0) {
            log.error("[patrol-uplink] 轨迹事件没有执行记录编号，不记点 deviceId={}", sample.deviceId());
            return;
        }
        ReplayPoseReqDTO pose = new ReplayPoseReqDTO();
        pose.setExecutionRecordId(sample.executionRecordId());
        pose.setX(lng);
        pose.setY(lat);
        pose.setZ(height);
        pose.setT(epochMillis(sample.fields().get("time"), sample.receivedAtEpochMs()));
        pose.setFrameRef("wgs84");
        pagePublisher.publishTrajectory(sample.executionRecordId(), pose);
        taskExchangeTranscriptService.recordPose(pose);
    }

    private void fillStandardEvent(StrategyTriggerEventDTO event, CollectionSample sample) {
        if ("inspection_execution_run".equals(sample.messageKind())) {
            String status = mapTaskStatusCode(intValue(sample.fields().get("status")));
            if (StringUtils.hasText(status)) {
                event.setExecutionStatus(status);
            }
            return;
        }
        if ("inspection_device_fault".equals(sample.messageKind()) && sample.executionRecordId() != null) {
            event.setExecutionStatus("fault");
            return;
        }
        if (!"inspection_execution_step".equals(sample.messageKind())) {
            return;
        }
        List<Map<String, Object>> updates = standardStepUpdates(sample);
        if (!updates.isEmpty()) {
            event.setStepUpdates(updates);
        }
    }

    private List<Map<String, Object>> standardStepUpdates(CollectionSample sample) {
        Object raw = sample.fields().get("steps");
        if (!(raw instanceof List<?> steps)) {
            log.error("[patrol-uplink] 步骤事件没有步骤列表，无法对回检查步骤");
            return List.of();
        }
        List<Map<String, Object>> updates = new ArrayList<>();
        for (Object item : steps) {
            if (!(item instanceof Map<?, ?> step)) {
                continue;
            }
            Integer sequence = intValue(step.get("sequence"));
            Integer innerOpcode = intValue(step.get("opcode"));
            if (sequence == null || innerOpcode == null) {
                log.error("[patrol-uplink] 步骤事件缺少序号或内层操作码，无法对回检查步骤");
                continue;
            }
            Map<String, Object> update = new LinkedHashMap<>();
            update.put("stepCode", TaskStepTreeAssembler.sequenceStepCode(sequence));
            update.put("status", failedCode(step.get("resultCode")) ? "failed" : "completed");
            update.put("resultPayload", standardSnapshot(sample, step, innerOpcode));
            updates.add(update);
        }
        return updates;
    }

    private Map<String, Object> standardSnapshot(CollectionSample sample, Map<?, ?> step, int innerOpcode) {
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("receivedAtEpochMs", sample.receivedAtEpochMs());
        snapshot.put("messageKind", sample.messageKind());
        snapshot.put("protocolQualify", sample.protocolQualify().name());
        snapshot.put("opcode", innerOpcode);
        Map<String, Object> point = new LinkedHashMap<>();
        point.put("lat", step.get("lat"));
        point.put("lng", step.get("lng"));
        point.put("height", step.get("height"));
        snapshot.put("operatePoint", point);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("electricity", step.get("electricity"));
        if (step.get("photos") != null) {
            result.put("photos", step.get("photos"));
        }
        snapshot.put("result", result);
        return snapshot;
    }

    private static boolean isStandardEvent(String messageKind) {
        return messageKind != null && messageKind.startsWith("inspection_");
    }

    private static boolean failedCode(Object raw) {
        Integer code = intValue(raw);
        return code != null && code != 0 && code != 200;
    }

    private static Long epochMillis(Object raw, long receivedAt) {
        Long value = longValue(raw);
        if (value == null) {
            return receivedAt;
        }
        if (value < 10_000_000_000L) {
            return value * 1000L;
        }
        return value;
    }

    private static Double decimal(Object raw) {
        if (raw instanceof Number number) {
            return number.doubleValue();
        }
        if (raw instanceof String text && !text.isBlank()) {
            try {
                return Double.parseDouble(text.trim());
            } catch (NumberFormatException ex) {
                return null;
            }
        }
        return null;
    }

    private static Long longValue(Object raw) {
        if (raw instanceof Number number) {
            return number.longValue();
        }
        if (raw instanceof String text && !text.isBlank()) {
            try {
                return Long.parseLong(text.trim());
            } catch (NumberFormatException ex) {
                return null;
            }
        }
        return null;
    }

    private static Integer intValue(Object raw) {
        Long value = longValue(raw);
        if (value == null || value > Integer.MAX_VALUE || value < Integer.MIN_VALUE) {
            return null;
        }
        return value.intValue();
    }

    /**
     * 协议不合格只带过程和错误，不带某一步完成、不带整次状态。
     */
    private StrategyTriggerEventDTO buildCollectionEvent(CollectionSample sample) {
        StrategyTriggerEventDTO event = new StrategyTriggerEventDTO();
        event.setEventType(StrategyTriggerEventDTO.EVENT_COLLECTION_RECEIVED);
        event.setExecutionRecordId(sample.executionRecordId());
        event.setEntityTypeCode(PATROL_RECORD_TYPE);
        event.setReceivedAtEpochMs(sample.receivedAtEpochMs());
        event.setMessageKind(sample.messageKind());
        event.setProtocolQualify(sample.protocolQualify().name());
        event.setQualifyErrors(sample.qualifyErrors());
        event.setFields(sample.fields());
        if (sample.protocolQualify() == ProtocolQualifyStatus.UNQUALIFIED) {
            return event;
        }
        fillStandardEvent(event, sample);
        return event;
    }

    static int readOpcode(CollectionSample sample) {
        try {
            return Integer.parseInt(sample.messageKind());
        } catch (NumberFormatException ex) {
            Object raw = sample.fields().get("opcode");
            if (raw instanceof Number number) {
                return number.intValue();
            }
            throw new IllegalArgumentException("采集样本报文种类不是操作码: " + sample.messageKind());
        }
    }

    static String resolveExecutionStatus(int opcode, JsonNode root) {
        TransportOpcode transport;
        try {
            transport = TransportOpcode.fromCode(opcode);
        } catch (IllegalArgumentException ex) {
            return null;
        }
        return switch (transport) {
            case FAULT_REPORT -> "fault";
            case TASK_STATUS -> mapTaskStatusCode(intOrNull(root, "status"));
            case COMMAND_RESULT -> null;
            default -> null;
        };
    }

    static String mapTaskStatusCode(Integer statusCode) {
        if (statusCode == null) {
            return "in_progress";
        }
        return switch (statusCode) {
            case DeviceTaskStatusCode.START_SUCCESS -> "in_progress";
            case DeviceTaskStatusCode.TASK_COMPLETED, DeviceTaskStatusCode.STOP_SUCCESS -> "completed";
            case DeviceTaskStatusCode.START_FAILURE, DeviceTaskStatusCode.STOP_FAILURE -> "fault";
            default -> "in_progress";
        };
    }

    /**
     * 对上哪一步：包内序号 + 内层操作码。组好后交给「更新某一步的状态」，本方法不写账。
     */
    private List<Map<String, Object>> resolveStepUpdates(
            int opcode, JsonNode root, CollectionSample sample) {
        List<Map<String, Object>> updates = new ArrayList<>();
        TransportOpcode transport;
        try {
            transport = TransportOpcode.fromCode(opcode);
        } catch (IllegalArgumentException ex) {
            return updates;
        }
        if (transport != TransportOpcode.COMMAND_RESULT) {
            return updates;
        }
        for (JsonNode pkg : extractPackageNodes(root)) {
            Integer sequence = intOrNull(pkg, "sequence");
            Integer innerOpcode = intOrNull(pkg, "opcode");
            if (sequence == null || innerOpcode == null) {
                log.error("[patrol-uplink] 步骤结果缺少序号或指令编码，无法对回检查步骤");
                continue;
            }
            Map<String, Object> update = new LinkedHashMap<>();
            update.put("stepCode", TaskStepTreeAssembler.sequenceStepCode(sequence));
            update.put("status", isFailedResult(pkg.path("result")) ? "failed" : "completed");
            update.put("resultPayload", receivedSnapshot(sample, pkg));
            updates.add(update);
        }
        return updates;
    }

    /**
     * 步骤结果留下上报时刻，并带上这一步自己的 result。
     * 电量、照片、视频都在 result 里，执行记录按步骤标题展示，不在这里猜是哪一种动作。
     */
    private Map<String, Object> receivedSnapshot(CollectionSample sample, JsonNode pkg) {
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("receivedAtEpochMs", sample.receivedAtEpochMs());
        snapshot.put("messageKind", sample.messageKind());
        snapshot.put("protocolQualify", sample.protocolQualify().name());
        Integer opcode = intOrNull(pkg, "opcode");
        if (opcode != null) {
            snapshot.put("opcode", opcode);
        }
        JsonNode result = pkg == null ? null : pkg.get("result");
        if (result != null && !result.isNull() && !result.isMissingNode()) {
            snapshot.put("result", objectMapper.convertValue(result, Object.class));
        }
        JsonNode point = pkg == null ? null : pkg.get("operatePoint");
        if (point != null && point.isObject()) {
            snapshot.put("operatePoint", objectMapper.convertValue(point, Object.class));
        }
        return snapshot;
    }

    private static List<JsonNode> extractPackageNodes(JsonNode root) {
        List<JsonNode> nodes = new ArrayList<>();
        JsonNode packages = root == null ? null : root.get("packages");
        if (packages == null || packages.isNull()) {
            return nodes;
        }
        if (packages.isArray()) {
            packages.forEach(nodes::add);
        } else if (packages.isObject()) {
            nodes.add(packages);
        }
        return nodes;
    }

    static boolean isFailedResult(JsonNode result) {
        if (result == null || result.isNull() || result.isMissingNode()) {
            return false;
        }
        if (result.has("success") && result.get("success").isBoolean() && !result.get("success").asBoolean()) {
            return true;
        }
        return result.has("code") && result.get("code").isNumber() && result.get("code").asInt() != 0;
    }

    private static Integer intOrNull(JsonNode node, String field) {
        if (node == null || !node.has(field) || node.get(field).isNull()) {
            return null;
        }
        JsonNode value = node.get(field);
        if (value.isNumber()) {
            return value.asInt();
        }
        if (value.isTextual()) {
            try {
                return Integer.parseInt(value.asText().trim());
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }
}
