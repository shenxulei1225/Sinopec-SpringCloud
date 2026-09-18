package cn.cheers.x.inspection.task.service.execution.impl;

import cn.cheers.x.device.protocolgateway.api.channel.AccessChannelCodes;
import cn.cheers.x.device.protocolgateway.api.datacollection.CollectionSample;
import cn.cheers.x.device.protocolgateway.api.datacollection.ProtocolQualifyStatus;
import cn.cheers.x.device.protocolgateway.api.opcode.DeviceTaskStatusCode;
import cn.cheers.x.device.protocolgateway.api.opcode.TransportOpcode;
import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.task.service.execution.InspectionDeviceUplinkService;
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
 * 巡检采集适配：把样本收成「采集结果到了」，并组好要对回的步/整次状态。
 * <p>记过程、改某一步、改整次状态都由条件策略做。本方法不对账写。
 * <p>不负责：拆原始报文、按字段说明核对、工业通道。
 * <p>禁止：执行记录 id 空了猜设备；不合格还把某一步标成完成；用上报点位对步骤。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InspectionDeviceUplinkServiceImpl implements InspectionDeviceUplinkService {

    public static final String PATROL_RECORD_TYPE = "task_record_patrol";

    private final StrategyRuntimeApi strategyRuntimeApi;
    private final ObjectMapper objectMapper;

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
        StrategyTriggerEventDTO event = buildCollectionEvent(sample);
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
        } else {
            log.debug("[patrol-uplink] 策略未命中 skipReason={}",
                    data != null ? data.getSkipReason() : null);
        }
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
        JsonNode fields = objectMapper.valueToTree(sample.fields());
        int opcode = readOpcode(sample);
        String status = resolveExecutionStatus(opcode, fields);
        if (StringUtils.hasText(status)) {
            event.setExecutionStatus(status);
        }
        List<Map<String, Object>> stepUpdates = resolveStepUpdates(opcode, fields, sample);
        if (!stepUpdates.isEmpty()) {
            event.setStepUpdates(stepUpdates);
        }
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
            update.put("resultPayload", receivedSnapshot(sample));
            updates.add(update);
        }
        return updates;
    }

    private static Map<String, Object> receivedSnapshot(CollectionSample sample) {
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("receivedAtEpochMs", sample.receivedAtEpochMs());
        snapshot.put("messageKind", sample.messageKind());
        snapshot.put("protocolQualify", sample.protocolQualify().name());
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
