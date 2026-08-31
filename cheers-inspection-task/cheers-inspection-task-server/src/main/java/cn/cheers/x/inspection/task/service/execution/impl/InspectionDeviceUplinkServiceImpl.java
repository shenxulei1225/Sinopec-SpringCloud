package cn.cheers.x.inspection.task.service.execution.impl;

import cn.cheers.x.device.protocolgateway.api.dto.DeviceUplinkEventDTO;
import cn.cheers.x.device.protocolgateway.api.opcode.DeviceTaskStatusCode;
import cn.cheers.x.device.protocolgateway.api.opcode.TransportOpcode;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.task.service.execution.InspectionDeviceUplinkService;
import cn.cheers.x.module.dynamicbusiness.api.execution.TaskExecutionSessionApi;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionWritebackReqDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 巡检域外部通道适配器：地面站上行 → 任务模块执行回写。
 * <p>匹配权威 = 报文 taskId（执行记录 id）。禁止扫任务定义表猜设备。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InspectionDeviceUplinkServiceImpl implements InspectionDeviceUplinkService {

    public static final String PATROL_RECORD_TYPE = "task_record_patrol";

    private final TaskExecutionSessionApi taskExecutionSessionApi;
    private final ObjectMapper objectMapper;

    @Override
    public void applyUplink(DeviceUplinkEventDTO event) {
        if (event == null) {
            return;
        }
        JsonNode root = parsePayload(event.payloadJson());
        Long executionRecordId = parseLongId(text(root, "taskId"));
        if (executionRecordId == null) {
            log.debug("[patrol-uplink] 缺少可解析的执行记录 taskId opcode={}", event.opcode());
            return;
        }

        TaskExecutionWritebackReqDTO writeback = new TaskExecutionWritebackReqDTO();
        writeback.setExecutionRecordId(executionRecordId);
        writeback.setEntityTypeCode(PATROL_RECORD_TYPE);

        String status = resolveExecutionStatus(event.opcode(), root);
        if (StringUtils.hasText(status)) {
            writeback.setExecutionStatus(status);
        }
        List<TaskExecutionWritebackReqDTO.StepUpdate> stepUpdates =
                resolveStepUpdates(event.opcode(), root);
        if (!stepUpdates.isEmpty()) {
            writeback.setStepUpdates(stepUpdates);
        }
        if (!StringUtils.hasText(writeback.getExecutionStatus())
                && (writeback.getStepUpdates() == null || writeback.getStepUpdates().isEmpty())) {
            return;
        }

        CommonResult<Boolean> rpc = taskExecutionSessionApi.writeback(writeback);
        if (rpc == null || !rpc.isSuccess()) {
            log.warn("[patrol-uplink] writeback 失败 executionRecordId={} msg={}",
                    executionRecordId, rpc != null ? rpc.getMsg() : null);
            return;
        }
        log.info("[patrol-uplink] writeback ok executionRecordId={} opcode={} status={}",
                executionRecordId, event.opcode(), writeback.getExecutionStatus());
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

    private List<TaskExecutionWritebackReqDTO.StepUpdate> resolveStepUpdates(int opcode, JsonNode root) {
        List<TaskExecutionWritebackReqDTO.StepUpdate> updates = new ArrayList<>();
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
            String pointId = extractPointId(pkg);
            if (!StringUtils.hasText(pointId)) {
                continue;
            }
            TaskExecutionWritebackReqDTO.StepUpdate update = new TaskExecutionWritebackReqDTO.StepUpdate();
            update.setStepCode(pointId);
            update.setStatus(isFailedResult(pkg.path("result")) ? "failed" : "completed");
            updates.add(update);
        }
        return updates;
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

    private static String extractPointId(JsonNode pkg) {
        if (pkg == null) {
            return null;
        }
        JsonNode reportPoint = pkg.get("reportPoint");
        if (reportPoint != null && !reportPoint.isNull()) {
            String fromReport = text(reportPoint, "pointId");
            if (StringUtils.hasText(fromReport)) {
                return fromReport;
            }
        }
        return text(pkg, "pointId");
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

    private JsonNode parsePayload(String payloadJson) {
        if (!StringUtils.hasText(payloadJson)) {
            return objectMapper.createObjectNode();
        }
        try {
            return objectMapper.readTree(payloadJson);
        } catch (Exception ex) {
            log.warn("[patrol-uplink] payload JSON 无效: {}", ex.getMessage());
            return objectMapper.createObjectNode();
        }
    }

    private static String text(JsonNode node, String field) {
        if (node == null || !node.has(field) || node.get(field).isNull()) {
            return null;
        }
        String value = node.get(field).asText(null);
        return StringUtils.hasText(value) ? value.trim() : null;
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

    private static Long parseLongId(String raw) {
        if (!StringUtils.hasText(raw)) {
            return null;
        }
        try {
            return Long.parseLong(raw.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
