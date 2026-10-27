package cn.cheers.x.inspection.task.service.execution.monitor;

import cn.cheers.x.device.protocolgateway.api.opcode.TransportOpcode;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.task.api.dto.ReplayPoseReqDTO;
import cn.cheers.x.inspection.task.api.dto.TaskExchangeTranscriptReqDTO;
import cn.cheers.x.inspection.task.api.dto.TaskExchangeTranscriptRespDTO;
import cn.cheers.x.inspection.task.service.execution.impl.InspectionDeviceUplinkServiceImpl;
import cn.cheers.x.inspection.task.service.execution.pagepush.CommandWindowPageNotice;
import cn.cheers.x.module.dynamicbusiness.api.execution.TaskExecutionSessionApi;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionAppendPoseReqDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionAppendProcessReqDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionAppendProcessRespDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

/**
 * 把一次发出或收回的报文原文记进任务执行记录，供回放。
 * <p>报文里的任务 id，或大疆飞行编号，是巡检平台要执行的这次记录。有它就记到这条。
 * 没有时，发送侧已经用事务编号对上的执行记录编号可以用。
 * 再没有：报文编号先对到设备编号。这台设备一条正在执行就记到它。否则只有一条已经到点的待执行才记到它。
 * 过程上写下说明书里的指令名称和消息编号。指令监控用名称显示这一行，回执用消息编号改回这一行。
 * 账写上之后，按网关带来的转发主题把这一行发给正在看这次执行的页面。没有转发主题就不发。
 * <p>不负责：改步骤完成、改整次执行状态、电量不够时让用户选择。
 * <p>禁止：用任务模板 id 或指令报文 id 选执行记录；多条正在执行或多条已经到点时挑一条；还没到点的待执行拿来挂。
 */
@Service
public class TaskExchangeTranscriptService {

    /** 这条过程是往来原文，不是字段说明核对后的合格结论。 */
    static final String QUALIFY_TRANSCRIPT = "TRANSCRIPT";

    private final PatrolGeneratedTaskLookup patrolGeneratedTaskLookup;
    private final TaskExecutionSessionApi taskExecutionSessionApi;
    private final ObjectMapper objectMapper;
    private final CommandWindowPageNotice commandWindowPageNotice;

    public TaskExchangeTranscriptService(
            PatrolGeneratedTaskLookup patrolGeneratedTaskLookup,
            TaskExecutionSessionApi taskExecutionSessionApi,
            ObjectMapper objectMapper,
            CommandWindowPageNotice commandWindowPageNotice) {
        this.patrolGeneratedTaskLookup = patrolGeneratedTaskLookup;
        this.taskExecutionSessionApi = taskExecutionSessionApi;
        this.objectMapper = objectMapper;
        this.commandWindowPageNotice = commandWindowPageNotice;
    }

    /**
     * 记入成功时 recorded 为真。没有唯一进行中记录，或写出失败时，recorded 为假并带上原因。
     */
    public TaskExchangeTranscriptRespDTO record(TaskExchangeTranscriptReqDTO req) {
        if (req == null || req.getDeviceId() == null || req.getDeviceId().isBlank()) {
            return skipped("要先选定一台设备");
        }
        if (!TaskExchangeTranscriptReqDTO.INBOUND.equals(req.getDirection())
                && !TaskExchangeTranscriptReqDTO.OUTBOUND.equals(req.getDirection())) {
            return skipped("往来方向无法识别，未记入任务");
        }
        if (req.getPayloadJson() == null || req.getPayloadJson().isBlank()) {
            return skipped("缺少报文原文，未记入任务");
        }
        JsonNode root = root(req.getPayloadJson());
        Long recordId = readExplicitRecordId(root);
        if (recordId == null && req.getExecutionRecordId() != null && req.getExecutionRecordId() > 0) {
            recordId = req.getExecutionRecordId();
        }
        if (recordId == null) {
            PatrolGeneratedTaskSelector.RunningMatch match = patrolGeneratedTaskLookup.forExchange(req.getDeviceId());
            if (!match.found()) {
                return skipped(match.skipReason());
            }
            recordId = match.recordId();
        }
        TaskExecutionAppendProcessReqDTO append = new TaskExecutionAppendProcessReqDTO();
        append.setExecutionRecordId(recordId);
        append.setEntityTypeCode(InspectionDeviceUplinkServiceImpl.PATROL_RECORD_TYPE);
        append.setReceivedAtEpochMs(req.getReceivedAtEpochMs() == null
                ? System.currentTimeMillis()
                : req.getReceivedAtEpochMs());
        append.setMessageKind(messageKindOf(root));
        append.setProtocolQualify(QUALIFY_TRANSCRIPT);
        append.setDirection(req.getDirection());
        append.setPayloadJson(req.getPayloadJson());
        // 指令监控左侧写这个名称。回执按消息编号改回这一行，所以编号要落在过程上，不能只留在原文里。
        append.setSummary(instructionName(root));
        append.setMsgId(textOrNull(root.path("msgId")));
        append.setMediaFileUrl(mediaUrl(root));
        try {
            CommonResult<TaskExecutionAppendProcessRespDTO> written = taskExecutionSessionApi.appendProcess(append);
            if (written == null || !written.isSuccess()) {
                String message = written == null ? null : written.getMsg();
                return skipped(message == null || message.isBlank() ? "记入任务失败" : message);
            }
        } catch (RuntimeException ex) {
            String message = ex.getMessage();
            return skipped(message == null || message.isBlank() ? "记入任务失败" : message);
        }
        // 页面这一行用账上同一套名称和消息编号。推送失败不改刚才写下的账。
        commandWindowPageNotice.publish(req, recordId, root);
        TaskExchangeTranscriptRespDTO resp = new TaskExchangeTranscriptRespDTO();
        resp.setRecorded(true);
        return resp;
    }

    /**
     * 把飞机位置点记到这次执行上。过程列表不写这个点。
     * 页面上的轨迹由上报处理按名字规则另发，这里不发。
     * 真的写上才返回真。对不上执行记录，或写出失败，返回假。
     */
    public boolean recordPose(ReplayPoseReqDTO req) {
        if (req == null || req.getT() == null || req.getX() == null || req.getY() == null || req.getZ() == null) {
            return false;
        }
        Long recordId = req.getExecutionRecordId();
        if (recordId == null || recordId <= 0) {
            if (req.getDeviceId() == null || req.getDeviceId().isBlank()) {
                return false;
            }
            PatrolGeneratedTaskSelector.RunningMatch match = patrolGeneratedTaskLookup.forExchange(req.getDeviceId());
            if (!match.found()) {
                return false;
            }
            recordId = match.recordId();
        }
        TaskExecutionAppendPoseReqDTO pose = new TaskExecutionAppendPoseReqDTO();
        pose.setExecutionRecordId(recordId);
        pose.setEntityTypeCode(InspectionDeviceUplinkServiceImpl.PATROL_RECORD_TYPE);
        pose.setT(req.getT());
        pose.setX(req.getX());
        pose.setY(req.getY());
        pose.setZ(req.getZ());
        pose.setYaw(req.getYaw());
        pose.setFrameRef(req.getFrameRef());
        try {
            CommonResult<Boolean> written = taskExecutionSessionApi.appendPose(pose);
            return written != null && written.isSuccess();
        } catch (RuntimeException ex) {
            return false;
        }
    }

    private Long readExplicitRecordId(JsonNode root) {
        Long taskId = digits(root.path("taskId"));
        if (taskId != null) {
            return taskId;
        }
        Long nested = digits(root.path("data").path("flight_id"));
        return nested != null ? nested : digits(root.path("flight_id"));
    }

    /**
     * 外层操作码在说明书上的中文名称。
     * 指令监控用它当这一行的名称。没有操作码，或操作码不在说明书上，就不写名称。
     */
    private static String instructionName(JsonNode root) {
        Integer code = opcode(root.path("opcode"));
        if (code == null) {
            return null;
        }
        try {
            return TransportOpcode.fromCode(code).description();
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

    private static String textOrNull(JsonNode node) {
        if (node == null || !node.isTextual()) {
            return null;
        }
        String text = node.asText().trim();
        return text.isEmpty() ? null : text;
    }

    private String messageKindOf(JsonNode root) {
        String method = root.path("method").asText("").trim();
        if (!method.isEmpty()) {
            return method;
        }
        JsonNode opcode = root.path("opcode");
        if (opcode.isNumber()) {
            return String.valueOf(opcode.intValue());
        }
        if (opcode.isTextual() && !opcode.asText().isBlank()) {
            return opcode.asText().trim();
        }
        return "exchange";
    }

    /** 上报里已经给出的可播放地址。对象存储键不是播放地址，不写。 */
    private static String mediaUrl(JsonNode root) {
        String url = http(root.path("data").path("file").path("url"));
        return url != null ? url : http(root.path("data").path("url"));
    }

    private static String http(JsonNode node) {
        if (node == null || !node.isTextual()) {
            return null;
        }
        String text = node.asText().trim();
        if (text.startsWith("https://") || text.startsWith("http://")) {
            return text;
        }
        return null;
    }

    private static Long digits(JsonNode node) {
        if (node == null || node.isMissingNode() || node.isNull()) {
            return null;
        }
        if (node.isNumber()) {
            return node.longValue();
        }
        if (!node.isTextual()) {
            return null;
        }
        String text = node.asText().trim();
        if (text.isEmpty() || !text.chars().allMatch(Character::isDigit)) {
            return null;
        }
        return Long.valueOf(text);
    }

    private JsonNode root(String payloadJson) {
        try {
            return objectMapper.readTree(payloadJson);
        } catch (Exception ex) {
            return objectMapper.createObjectNode();
        }
    }

    private static TaskExchangeTranscriptRespDTO skipped(String reason) {
        TaskExchangeTranscriptRespDTO resp = new TaskExchangeTranscriptRespDTO();
        resp.setRecorded(false);
        resp.setReason(reason);
        return resp;
    }
}
