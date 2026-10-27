package cn.cheers.x.inspection.task.service.execution.pagepush;

import cn.cheers.x.device.protocolgateway.api.opcode.TransportOpcode;
import cn.cheers.x.inspection.task.api.dto.TaskExchangeTranscriptReqDTO;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 执行账写上之后，把指令监控要看的这一行发给正在看这次执行的页面。
 * 下发和回执改同一条指令。带结果的反馈不从这里发，由标准事件另发成上报。
 * 主题编码由网关按说明书的转发主题带来。这里只按已经存好的名字规则发出。
 * 不负责：连接消息服务、给指令监控另开一条订阅、画轨迹。
 * 禁止：心跳或回执另编主题；名字规则读不到时自己编一条。
 */
@Slf4j
@Component
public class CommandWindowPageNotice {

    static final String ISSUED = "下发中";
    static final String RECEIVED = "设备已接收";

    private final InspectionExecutionPagePublisher publisher;

    public CommandWindowPageNotice(InspectionExecutionPagePublisher publisher) {
        this.publisher = publisher;
    }

    /**
     * 账已经写上才调用。没有转发主题时不发，也不另编。
     * 回执只改成设备已接收，名称留在原来那一行上。
     */
    public void publish(TaskExchangeTranscriptReqDTO req, Long recordId, JsonNode root) {
        try {
            send(req, recordId, root);
        } catch (RuntimeException ex) {
            log.error("[inspection-page-push] 执行账已写下，指令这一行没有发出 executionRecordId={} reason={}",
                    recordId, ex.getMessage());
        }
    }

    private void send(TaskExchangeTranscriptReqDTO req, Long recordId, JsonNode root) {
        Integer opcode = opcode(root == null ? null : root.path("opcode"));
        if (opcode != null && opcode == TransportOpcode.HEARTBEAT.code()) {
            return;
        }
        String topicCode = req == null ? null : req.getEventTopicCode();
        if (!StringUtils.hasText(topicCode)) {
            log.error("[inspection-page-push] 这一包没有转发主题，不另编 opcode={} executionRecordId={}",
                    opcode, recordId);
            return;
        }
        String eventStatus = eventStatus(req.getDirection(), opcode);
        if (!StringUtils.hasText(eventStatus)) {
            return;
        }
        String summary = RECEIVED.equals(eventStatus) ? null : instructionName(opcode);
        publisher.publishProcessNotice(
                topicCode.trim(),
                recordId,
                req.getDeviceId(),
                summary,
                eventStatus,
                null,
                text(root == null ? null : root.path("msgId")),
                req.getDirection(),
                req.getReceivedAtEpochMs(),
                null);
    }

    /**
     * 账上这一行要不要发给指令监控。
     * 下发是下发中。回执是设备已接收，并回下发那一行。
     * 带结果的反馈和设备自己报上来的，不从这里发。
     */
    static String eventStatus(String direction, Integer opcode) {
        if (opcode != null && opcode == TransportOpcode.HEARTBEAT.code()) {
            return null;
        }
        if (opcode != null && opcode == TransportOpcode.ACK.code()) {
            return RECEIVED;
        }
        if (TaskExchangeTranscriptReqDTO.OUTBOUND.equals(direction)) {
            return ISSUED;
        }
        return null;
    }

    private static String instructionName(Integer opcode) {
        if (opcode == null) {
            return null;
        }
        try {
            return TransportOpcode.fromCode(opcode).description();
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private static Integer opcode(JsonNode node) {
        if (node == null || node.isMissingNode() || node.isNull()) {
            return null;
        }
        if (node.isNumber()) {
            return node.intValue();
        }
        if (!node.isTextual()) {
            return null;
        }
        try {
            return Integer.valueOf(node.asText().trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static String text(JsonNode node) {
        if (node == null || !node.isTextual()) {
            return null;
        }
        String value = node.asText().trim();
        return value.isEmpty() ? null : value;
    }
}
