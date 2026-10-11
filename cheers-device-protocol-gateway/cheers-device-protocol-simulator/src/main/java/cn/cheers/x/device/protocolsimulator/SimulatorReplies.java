package cn.cheers.x.device.protocolsimulator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 按协议组应答。平台发来一条，先回执，再回同号业务答卷。
 * <p>负责：500101 点名、500102 开关定时状态、500104 收下指令包、500201 启动、500202 步骤结果、500203 任务状态、500205 终止、500301 取包、500302 删包、500303 列出已收下的任务模板。
 * <p>无人机不回心跳。机器人收到 500105 时原包加上时间回 500105。
 * <p>禁止：把无人机步骤结果的外层字段改成 packages。老模拟器写在 result 上，厂商确认前保持这样。机器人按协议书写在 packages 上。
 */
@RequiredArgsConstructor
public class SimulatorReplies {

    private final ObjectMapper objectMapper;
    private final SimulatorState state;
    private final String deviceId;
    private final String role;

    /**
     * 立刻要发出去的报文。启动任务时，步骤结果不在这个列表里，由 {@link #runTask} 稍后发。
     */
    public List<String> immediate(String raw, long nowMillis) {
        JsonNode incoming;
        try {
            incoming = objectMapper.readTree(raw);
        } catch (Exception ex) {
            return List.of();
        }
        if (incoming == null || !incoming.isObject() || !incoming.path("opcode").isNumber()) {
            return List.of();
        }
        int opcode = incoming.path("opcode").asInt();
        if (opcode == 500106) {
            return List.of();
        }
        if (opcode == 500105) {
            if (!robot()) {
                return List.of();
            }
            ObjectNode echo = ((ObjectNode) incoming).deepCopy();
            echo.put("opcode", 500105);
            echo.put("time", nowMillis);
            return List.of(write(echo));
        }
        String msgId = text(incoming, "msgId");
        List<String> outbound = new ArrayList<>();
        outbound.add(write(ack(msgId, opcode, nowMillis)));
        outbound.add(write(business(incoming, opcode, msgId, nowMillis)));
        return outbound;
    }

    /**
     * 指令包已经收下之后，按步上报。先报任务启动，再逐步报结果，最后报任务完成。
     */
    public List<String> runTask(String taskId, String packagesJson, long nowMillis) {
        List<String> outbound = new ArrayList<>();
        outbound.add(write(taskStatus(taskId, 300101, "任务启动成功", nowMillis)));
        ArrayNode steps = readSteps(packagesJson);
        for (JsonNode step : steps) {
            ObjectNode result = stepResult(taskId, step, nowMillis);
            if (result != null) {
                outbound.add(write(result));
            }
        }
        outbound.add(write(taskStatus(taskId, 300105, "任务已完成", nowMillis + 1)));
        return outbound;
    }

    /**
     * 定时状态。外层字段仍是 result，和老模拟器一致。
     */
    public String timedStatus(String reportedDeviceId, int sequence, long nowMillis) {
        ObjectNode root = objectMapper.createObjectNode();
        root.put("opcode", 500103);
        root.put("msgId", String.valueOf(nowMillis));
        root.put("deviceId", reportedDeviceId);
        ObjectNode body = root.putObject("result");
        body.put("opcode", "200501");
        body.put("sequence", sequence);
        body.put("deviceId", reportedDeviceId);
        point(body.putObject("reportPoint"), nowMillis);
        ObjectNode status = body.putObject("result");
        status.put("deviceId", reportedDeviceId);
        status.put("sequence", sequence);
        status.put("time", nowMillis);
        status.put("status", 400102);
        status.put("fault", 100000);
        status.put("electricity", "80");
        status.put("lat", "31.0383416830");
        status.put("lng", "121.2668456836");
        status.put("distance", 12);
        return write(root);
    }

    private ObjectNode business(JsonNode incoming, int opcode, String msgId, long nowMillis) {
        return switch (opcode) {
            case 500101 -> ok(opcode, msgId).set("data", location(nowMillis));
            case 500102 -> {
                state.saveReportSwitch(
                        text(incoming, "deviceId"),
                        incoming.path("enable").asBoolean(false),
                        incoming.path("frequency").isNumber() ? incoming.path("frequency").asInt() : null,
                        incoming.path("total").isNumber() ? incoming.path("total").asInt() : null);
                yield ok(opcode, msgId);
            }
            case 500104 -> acceptPacket(opcode, msgId, incoming, nowMillis);
            case 500201 -> acceptStart(opcode, msgId, incoming);
            case 500205 -> withData(ok(opcode, msgId));
            case 500301 -> readPacket(opcode, msgId, text(incoming, "templateId"));
            case 500302 -> deletePacket(opcode, msgId, text(incoming, "templateId"));
            case 500303 -> listTemplates(opcode, msgId);
            default -> fail(opcode, msgId, 900103, "接口参数格式错误");
        };
    }

    private ObjectNode acceptPacket(int opcode, String msgId, JsonNode incoming, long nowMillis) {
        String templateId = text(incoming, "templateId");
        String packagesJson = packagesText(incoming.get("packages"));
        if (templateId == null || packagesJson == null) {
            return fail(opcode, msgId, 900103, "Api接口操作失败");
        }
        String lineId = String.valueOf(nowMillis);
        state.savePacket(templateId, lineId, packagesJson);
        ObjectNode ok = ok(opcode, msgId);
        ok.putObject("data").put("lineId", lineId);
        return ok;
    }

    private ObjectNode acceptStart(int opcode, String msgId, JsonNode incoming) {
        String templateId = text(incoming, "templateId");
        if (state.packet(templateId).isEmpty()) {
            return fail(opcode, msgId, 900202, "指令包不存在");
        }
        return withData(ok(opcode, msgId).put("msg", "启动成功"));
    }

    private ObjectNode listTemplates(int opcode, String msgId) {
        ObjectNode ok = ok(opcode, msgId);
        ArrayNode templates = ok.putObject("data").putArray("templates");
        for (SimulatorState.SavedPacket saved : state.packets()) {
            ObjectNode row = templates.addObject();
            row.put("templateId", saved.templateId());
            row.put("lineId", saved.lineId());
        }
        return ok;
    }

    private ObjectNode readPacket(int opcode, String msgId, String templateId) {
        return state.packet(templateId)
                .map(saved -> {
                    ObjectNode ok = ok(opcode, msgId);
                    ObjectNode data = ok.putObject("data");
                    data.put("templateId", saved.templateId());
                    data.put("lineId", saved.lineId());
                    data.set("packages", packagesNode(saved.packagesJson()));
                    return ok;
                })
                .orElseGet(() -> fail(opcode, msgId, 900103, "Api接口操作失败"));
    }

    private ObjectNode deletePacket(int opcode, String msgId, String templateId) {
        state.deletePacket(templateId);
        return withData(ok(opcode, msgId));
    }

    private ObjectNode withData(ObjectNode root) {
        root.putObject("data");
        return root;
    }

    private ObjectNode location(long nowMillis) {
        ObjectNode data = objectMapper.createObjectNode();
        data.put("deviceId", deviceId);
        data.put("time", nowMillis);
        data.put("status", 400101);
        data.put("fault", 100000);
        // 故障码 100000 是设备正常。原因和数据链路按点名样例补上：1 表示链路已连接。
        data.put("faultReasion", "no error");
        data.put("dataLink", 1);
        data.put("electricity", "82.9");
        data.put("lat", "31.0383416830");
        data.put("lng", "121.2668456836");
        data.put("height", "17.874");
        data.put("distance", 12);
        return data;
    }

    /**
     * 任务状态上报。消息编号按字段说明用文字。上报点带纬度、经度、高度、点位、操作时间和电压。
     */
    private ObjectNode taskStatus(String taskId, int status, String content, long nowMillis) {
        ObjectNode root = objectMapper.createObjectNode();
        root.put("opcode", 500203);
        root.put("msgId", String.valueOf(nowMillis));
        root.put("taskId", taskId);
        root.put("deviceId", deviceId);
        root.put("status", status);
        root.put("content", content);
        ObjectNode report = root.putObject("reportPoint");
        report.put("lat", "39.1277217676");
        report.put("lng", "117.0190597072");
        report.put("height", "17.874");
        report.put("pointId", "222222");
        report.put("optime", nowMillis / 1000);
        report.put("electric", "100.00");
        return root;
    }

    /**
     * 步骤结果里的任务编号、内层操作码和序号都抄下发指令包。缺序号或操作码时不编一条。
     */
    private ObjectNode stepResult(String taskId, JsonNode step, long nowMillis) {
        if (step == null || !step.path("opcode").isNumber() || !step.path("sequence").isNumber()) {
            return null;
        }
        int inner = step.path("opcode").asInt();
        int sequence = step.path("sequence").asInt();
        JsonNode request = step.get("request");
        ObjectNode root = objectMapper.createObjectNode();
        root.put("opcode", 500202);
        root.put("msgId", String.valueOf(nowMillis));
        root.put("taskId", taskId);
        root.put("deviceId", deviceId);
        ObjectNode body = root.putObject(robot() ? "packages" : "result");
        body.put("opcode", inner);
        body.put("sequence", sequence);
        body.put("deviceId", deviceId);
        ObjectNode report = body.putObject("reportPoint");
        point(report, nowMillis);
        copyPoint(report, request, "lng");
        copyPoint(report, request, "lat");
        copyPoint(report, request, "height");
        if (robot()) {
            report.put("pointId", report.path("pointId").asText());
            report.put("electric", "100.00");
            report.put("distance", "0");
        }
        ObjectNode outcome = body.putObject("result");
        outcome.put("resultCode", 200);
        outcome.put("msg", "操作成功");
        ObjectNode operate = outcome.putObject("operatePoint");
        operate.put("optime", nowMillis / 1000);
        if (robot()) {
            operate.put("pointId", report.path("pointId").asText());
        } else {
            operate.put(pointKey(), report.path(pointKey()).asInt());
        }
        operate.put("lng", report.path("lng").asText(""));
        operate.put("lat", report.path("lat").asText(""));
        operate.put("height", report.path("height").asText("0"));
        operate.put("electric", "100.00");
        operate.put("distance", "0");
        if (inner == 200301) {
            ArrayNode photos = outcome.putArray("photos");
            ObjectNode photo = photos.addObject();
            photo.put("photoTime", nowMillis / 1000);
            photo.put("type", "jpg");
            photo.put("size", 110102);
            photo.put("data", "");
        }
        return root;
    }

    /**
     * 老模拟器把点位写在带空格的 pointId 上。这里照旧，避免迁完以后报文和原来不一样。
     */
    private void copyPoint(ObjectNode report, JsonNode request, String field) {
        if (request == null || !request.has(field)) {
            return;
        }
        String value = request.path(field).asText();
        if (value == null || value.isBlank()) {
            return;
        }
        report.put(field, value);
    }

    private void point(ObjectNode report, long nowMillis) {
        report.put("reportTime", nowMillis / 1000);
        report.put(pointKey(), 222222);
        report.put("lng", "117.0190597072");
        report.put("lat", "39.1277217676");
        report.put("height", "17.874");
        report.put("electric", 100.0);
        report.put("distance", 0);
    }

    private ArrayNode readSteps(String packagesJson) {
        try {
            JsonNode node = objectMapper.readTree(packagesJson);
            if (node.isArray()) {
                return (ArrayNode) node;
            }
        } catch (Exception ignored) {
            // 指令包不是数组时，启动应答已经发出，这里不再补步骤。
        }
        return objectMapper.createArrayNode();
    }

    /**
     * 取包应答里的动作集合按列表写回。存的是一段数组文字时先解析，页面才能读到序号和操作码。
     */
    private JsonNode packagesNode(String packagesJson) {
        if (packagesJson == null || packagesJson.isBlank()) {
            return objectMapper.getNodeFactory().textNode(packagesJson == null ? "" : packagesJson);
        }
        try {
            JsonNode node = objectMapper.readTree(packagesJson);
            if (node.isArray()) {
                return node;
            }
        } catch (Exception ignored) {
            // 存下来的不是数组时，原样放回，不编一个空列表冒充。
        }
        return objectMapper.getNodeFactory().textNode(packagesJson);
    }

    private String packagesText(JsonNode packages) {
        if (packages == null || packages.isNull()) {
            return null;
        }
        if (packages.isTextual()) {
            return packages.asText();
        }
        return write(packages);
    }

    private ObjectNode ack(String msgId, int opcode, long nowMillis) {
        ObjectNode root = objectMapper.createObjectNode();
        root.put("opcode", 500106);
        root.put("msgId", msgId);
        ObjectNode data = root.putObject("data");
        data.put("opcode", opcode);
        data.put("time", nowMillis);
        return root;
    }

    private ObjectNode ok(int opcode, String msgId) {
        ObjectNode root = objectMapper.createObjectNode();
        root.put("opcode", opcode);
        root.put("msgId", msgId);
        root.put("code", 200);
        root.put("msg", "操作成功");
        return root;
    }

    private ObjectNode fail(int opcode, String msgId, int code, String msg) {
        ObjectNode root = ok(opcode, msgId);
        root.put("code", code);
        root.put("msg", msg);
        return root;
    }

    /**
     * 无人机沿用老模拟器带空格的点位键。机器人按协议写 pointId。
     */
    private String pointKey() {
        return robot() ? "pointId" : "pointId ";
    }

    private boolean robot() {
        return "robot".equals(role);
    }

    private static String text(JsonNode node, String field) {
        JsonNode value = node.get(field);
        if (value == null || value.isNull()) {
            return null;
        }
        String text = value.asText();
        return text == null || text.isBlank() ? null : text;
    }

    private String write(JsonNode node) {
        try {
            return objectMapper.writeValueAsString(node);
        } catch (Exception ex) {
            throw new IllegalStateException("模拟器组包失败", ex);
        }
    }
}
