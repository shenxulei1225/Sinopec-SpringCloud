package cn.cheers.x.device.protocolgateway.instructiondispatch;

import cn.cheers.x.device.protocolgateway.api.message.CommandSendEnvelope;
import cn.cheers.x.device.protocolgateway.api.message.InstructionPackageItem;
import cn.cheers.x.device.protocolgateway.api.mission.DeviceMissionPlan;
import cn.cheers.x.device.protocolgateway.api.mission.DispatchAction;
import cn.cheers.x.device.protocolgateway.api.protocol.ProtocolCodes;
import cn.cheers.x.device.protocolgateway.api.protocol.ProtocolDispatchProfile;
import cn.cheers.x.device.protocolgateway.datacollection.CatalogLookup;
import cn.cheers.x.device.protocolgateway.datacollection.FieldDescriptionRow;
import cn.cheers.x.device.protocolgateway.datacollection.ProtocolInstructionCatalog;
import cn.cheers.x.device.protocolgateway.protocol.envelope.EnvelopeJsonCodec;
import cn.cheers.x.device.protocolgateway.service.MissionDownlink;
import cn.cheers.x.device.protocolgateway.service.MissionDownlink.DirectCommand;
import cn.cheers.x.device.protocolgateway.service.MissionDownlink.DownlinkPiece;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 指令下发：按对照表把动作填进选中的那条指令空包。
 * <p>负责：对照查找、填包。东古无人机和机器人把带 request 的步骤收进巡检指令包。
 * 大疆机场把航线动作打成一份 kmz，下发任务只带下载地址和内容签名；其余仍按对照逐条发服务报文。
 * 大疆 Pilot 的航线由 Pilot 航线库同步并由飞手启动，网关不得套用机场的自动航线启动指令。
 * 拍照报文里张数已经填上、点位编号还空时，点位编号、经度、纬度用同一次移动已经写下的到达位置。地面高度保持空包里的空字符串。
 * 字段说明标了必填而这次没有值，或这一步没有指令对照，告警并不写入指令包，序号仍占用。字段说明不要求必填的，留下空字符串并告警，这一步仍写入并占序号。
 * <p>不负责：发明头尾动作、等答卷、读巡检库。
 * <p>禁止：按厂商名写死填包；对照缺失还组东古或机器人的包；把到达位置写进拍照动作的参数；把单独报文塞进航线文件。准备并启动航线只给航线文件提供高度、速度和结束方式，不另发一条实时指令。电量检查留在任务页，大疆飞机在状态上报里带电量，不另发一条指令。
 */
@Service
public class InstructionDispatchService {

    private final ProtocolMappingCatalog catalog;
    private final EnvelopeJsonCodec envelopeJsonCodec;
    private final WaylineFileStore waylineFileStore;
    private final DispatchParamResolver paramResolver;
    private final ProtocolInstructionCatalog instructionCatalog;

    /** 测试不生成航线文件时用。真正要写文件而没有文件服务时，正式写入失败。 */
    public InstructionDispatchService(ProtocolMappingCatalog catalog, EnvelopeJsonCodec envelopeJsonCodec) {
        this(catalog, envelopeJsonCodec, (executionId, kmz, fingerprint) -> {
            throw new IllegalStateException("航线文件没有可给机场下载的地址");
        }, DispatchParamResolver.noop(), null);
    }

    /** 测试注入 WaylineFileStore 时用；参数解析用 noop。 */
    public InstructionDispatchService(
            ProtocolMappingCatalog catalog,
            EnvelopeJsonCodec envelopeJsonCodec,
            WaylineFileStore waylineFileStore
    ) {
        this(catalog, envelopeJsonCodec, waylineFileStore, DispatchParamResolver.noop(), null);
    }

    public InstructionDispatchService(
            ProtocolMappingCatalog catalog,
            EnvelopeJsonCodec envelopeJsonCodec,
            WaylineFileStore waylineFileStore,
            DispatchParamResolver paramResolver
    ) {
        this(catalog, envelopeJsonCodec, waylineFileStore, paramResolver, null);
    }

    @Autowired
    public InstructionDispatchService(
            ProtocolMappingCatalog catalog,
            EnvelopeJsonCodec envelopeJsonCodec,
            WaylineFileStore waylineFileStore,
            DispatchParamResolver paramResolver,
            ProtocolInstructionCatalog instructionCatalog
    ) {
        this.catalog = catalog;
        this.envelopeJsonCodec = envelopeJsonCodec;
        this.waylineFileStore = waylineFileStore;
        this.paramResolver = paramResolver == null ? DispatchParamResolver.noop() : paramResolver;
        this.instructionCatalog = instructionCatalog;
    }

    /**
     * 按已保存的对照填包。序号按这次动作列表从 0 递增，单独发出的指令也占一个号，
     * 这样巡检指令包里的序号仍对得上任务步骤。
     */
    public MissionDownlink translate(DeviceMissionPlan plan) {
        return translate(plan, true);
    }

    /**
     * @param persistWaylineFile 试排时为 false：仍校验 KMZ 与对照，但不写入航线目录
     */
    public MissionDownlink translate(DeviceMissionPlan plan, boolean persistWaylineFile) {
        if (plan == null || plan.deviceId() == null || plan.deviceId().isBlank()) {
            throw new IllegalArgumentException("必须提供逻辑设备标识");
        }
        if (!ProtocolCodes.isKnown(plan.protocolVersion())) {
            throw new IllegalArgumentException("协议版本须是机器人对接协议、无人机对接协议、大疆机场或大疆 Pilot");
        }
        if (plan.actions() == null || plan.actions().isEmpty()) {
            throw new IllegalArgumentException("下发动作列表不能为空；网关不发明起飞/移动顺序");
        }
        List<DownlinkPiece> pieces = new ArrayList<>();
        List<InstructionPackageItem> buffered = new ArrayList<>();
        Map<String, Object> arrivalRequest = null;
        boolean containsWaylineAction = WaylineKmzBuilder.containsFileAction(plan.actions());
        if (ProtocolCodes.DJI_PILOT.equals(plan.protocolVersion()) && containsWaylineAction) {
            throw new IllegalArgumentException(
                    "大疆 Pilot 航线须同步到 Pilot 航线库并由飞手启动；不能调用机场的 flighttask_prepare/flighttask_execute");
        }
        boolean waylineMission = ProtocolCodes.DJI_DOCK.equals(plan.protocolVersion())
                && containsWaylineAction;
        WaylineFileStore.Stored stored = null;
        double returnHomeAltitude = 0;
        boolean waylineInserted = false;
        if (waylineMission) {
            WaylineKmzBuilder.Built built = WaylineKmzBuilder.build(fillWaylineActions(plan));
            if (persistWaylineFile) {
                stored = waylineFileStore.store(plan.taskId(), built.kmz(), built.fingerprint());
            } else {
                stored = new WaylineFileStore.Stored(
                        "preview",
                        "",
                        built.fingerprint());
            }
            returnHomeAltitude = built.returnHomeAltitude();
        }
        int sequence = 0;
        List<Gap> blocking = new ArrayList<>();
        List<Gap> warnings = new ArrayList<>();
        Map<Long, String> actionNames = new LinkedHashMap<>();
        for (DispatchAction action : plan.actions()) {
            // 电量检查留在任务页。大疆飞机在状态上报里带电量，这里不要求指令对照。
            if (ProtocolCodes.isDjiCloud(plan.protocolVersion())
                    && ProtocolDispatchProfile.ACTION_UAV_BATTERY_CHECK.equals(action.actionCode())) {
                sequence++;
                continue;
            }
            if (waylineMission && (WaylineKmzBuilder.isFileAction(action.actionCode())
                    || WaylineKmzBuilder.WAYPOINT_ANCHOR.equals(action.actionCode())
                    || WaylineKmzBuilder.MISSION_HEAD.equals(action.actionCode()))) {
                if (!waylineInserted) {
                    pieces.add(DownlinkPiece.direct(flightTaskPrepare(plan, stored, returnHomeAltitude)));
                    pieces.add(DownlinkPiece.direct(flightTaskExecute(plan)));
                    waylineInserted = true;
                }
                sequence++;
                continue;
            }
            Attempt attempt = attempt(plan, action, sequence, actionNames);
            blocking.addAll(attempt.blocking());
            warnings.addAll(attempt.warnings());
            if (!attempt.blocking().isEmpty()) {
                sequence++;
                continue;
            }
            Filled filled = attempt.filled();
            if (ProtocolCodes.isDjiCloud(plan.protocolVersion())) {
                flushPackage(plan, pieces, buffered);
                pieces.add(DownlinkPiece.direct(toCloudDownlink(plan.deviceId(), filled)));
            } else if (isInnerLine(filled.packet())) {
                Map<String, Object> request = requestOf(filled.packet());
                writeArrivalPlace(request, arrivalRequest);
                if (hasText(request.get("pointId"))) {
                    arrivalRequest = new LinkedHashMap<>(request);
                }
                buffered.add(new InstructionPackageItem(filled.opcode(), sequence, request));
            } else {
                flushPackage(plan, pieces, buffered);
                pieces.add(DownlinkPiece.direct(toDirect(plan.deviceId(), filled)));
            }
            sequence++;
        }
        flushPackage(plan, pieces, buffered);
        List<String> notices = new ArrayList<>(grouped(blocking));
        notices.addAll(grouped(warnings));
        if (pieces.isEmpty()) {
            throw new IllegalArgumentException(notices.isEmpty() ? "没有可下发的指令" : String.join("。", notices));
        }
        if (stored == null) {
            return new MissionDownlink(pieces, null, null, notices);
        }
        return new MissionDownlink(pieces, stored.fileId(), stored.fingerprint(), notices);
    }

    /**
     * 航线动作先按这条协议已保存的对照填进该动作自己的指令空包，再交给航线文件。
     * 飞向目标点仍只提供航点坐标，不改成航点上的一个动作。
     */
    private List<DispatchAction> fillWaylineActions(DeviceMissionPlan plan) {
        List<DispatchAction> resolved = new ArrayList<>();
        for (DispatchAction action : plan.actions()) {
            if (!WaylineKmzBuilder.isFileAction(action.actionCode())) {
                resolved.add(action);
                continue;
            }
            Filled filled = fillOne(plan, action);
            Object params = filled.packet().get("params");
            if (!(params instanceof Map<?, ?> filledParams)) {
                throw new IllegalArgumentException("航线指令没有参数空包");
            }
            Map<String, Object> packet = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : filledParams.entrySet()) {
                packet.put(String.valueOf(entry.getKey()), entry.getValue());
            }
            resolved.add(new DispatchAction(action.actionId(), packet, action.actionCode()));
        }
        return resolved;
    }

    /** 下发任务只带机场能下载的地址和文件签名，不把航线正文放进报文。 */
    private DirectCommand flightTaskPrepare(DeviceMissionPlan plan, WaylineFileStore.Stored stored, double returnHomeAltitude) {
        Map<String, Object> file = new LinkedHashMap<>();
        file.put("url", stored.url());
        file.put("fingerprint", stored.fingerprint());
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("flight_id", plan.taskId());
        data.put("execute_time", System.currentTimeMillis());
        data.put("task_type", 0);
        data.put("file", file);
        data.put("rth_altitude", (int) returnHomeAltitude);
        data.put("rth_mode", 1);
        data.put("out_of_control_action", 0);
        data.put("exit_wayline_when_rc_lost", 1);
        data.put("wayline_precision_type", 1);
        Map<String, Object> packet = new LinkedHashMap<>();
        packet.put("method", "flighttask_prepare");
        packet.put("data", data);
        return toCloudService(plan.deviceId(), packet, null);
    }

    /** 航线文件交给机场之后再启动，对应东古、机器人指令包收下后的启动执行。 */
    private DirectCommand flightTaskExecute(DeviceMissionPlan plan) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("flight_id", plan.taskId());
        Map<String, Object> packet = new LinkedHashMap<>();
        packet.put("method", "flighttask_execute");
        packet.put("data", data);
        return toCloudService(plan.deviceId(), packet, null);
    }

    /**
     * 有字段说明时，必填缺失先记下，非必填缺失只告警。没有说明书时仍按缺来源直接失败。
     */
    private Attempt attempt(
            DeviceMissionPlan plan,
            DispatchAction action,
            int sequence,
            Map<Long, String> actionNames
    ) {
        String actionName = actionNames.computeIfAbsent(
                action.actionId(), id -> catalog.describeActionForMessage(action));
        Optional<MappingRow> found = catalog.find(action.actionId(), plan.protocolVersion());
        if (found.isEmpty()) {
            return Attempt.blocked(List.of(new Gap(
                    sequence,
                    actionName,
                    "在" + protocolLabel(plan.protocolVersion()) + "下没有指令对照，未写入指令包")), List.of());
        }
        if (instructionCatalog == null) {
            return Attempt.ready(fillOne(plan, action));
        }
        MappingRow mapping = found.get();
        InstructionTemplate template = catalog.loadInstruction(mapping.instructionId());
        Map<String, FieldDescriptionRow> book = bookOf(template.opcode(), plan.protocolVersion());
        if (book == null) {
            return Attempt.ready(fillOne(plan, action));
        }
        Map<String, Object> resolved = paramResolver.resolve(plan, action, mapping.slots());
        Map<String, Object> packet = InstructionPacketFiller.deepCopy(template.outbound());
        List<Gap> blocking = new ArrayList<>();
        List<Gap> warnings = new ArrayList<>();
        List<String> seen = new ArrayList<>();
        if (mapping.slots() != null) {
            for (SlotMapping slot : mapping.slots()) {
                seen.add(slot.path());
                FieldDescriptionRow row = book.get(slot.path());
                try {
                    Object raw = resolved.get(slot.slot());
                    if (raw == null) {
                        throw new IllegalArgumentException(
                                "这一步缺少" + sourceLabel(slot.sourceKind()) + "来源字段「" + slot.slot() + "」");
                    }
                    Object value = InstructionPacketFiller.readSource(raw, slot.sourcePath(), slot.slot());
                    InstructionPacketFiller.writeExistingPath(packet, slot.path(), value);
                    if (!hasText(value)) {
                        recordGap(blocking, warnings, sequence, actionName, row, "缺必填：" + (row == null ? slot.path() : row.label()));
                    }
                } catch (IllegalArgumentException ex) {
                    recordGap(blocking, warnings, sequence, actionName, row, ex.getMessage());
                }
            }
        }
        Object request = packet.get("request");
        if (request instanceof Map<?, ?> requestMap) {
            for (FieldDescriptionRow row : book.values()) {
                String key = requestKey(row.path());
                if (key == null || seen.contains(row.path()) || !requestMap.containsKey(key)) {
                    continue;
                }
                if (hasText(requestMap.get(key))) {
                    continue;
                }
                recordGap(blocking, warnings, sequence, actionName, row, "缺必填：" + row.label());
            }
        }
        if (!blocking.isEmpty()) {
            return Attempt.blocked(blocking, warnings);
        }
        return Attempt.ready(
                new Filled(template.opcode(), packet, template.communicationProtocol(), template.mqttTopic()),
                warnings);
    }

    private static void recordGap(
            List<Gap> blocking,
            List<Gap> warnings,
            int sequence,
            String actionName,
            FieldDescriptionRow row,
            String fallback
    ) {
        if (row == null) {
            blocking.add(new Gap(sequence, actionName, fallback));
            return;
        }
        String text = row.required()
                ? "缺必填：" + row.label() + "，未写入指令包"
                : "没有值：" + row.label();
        Gap gap = new Gap(sequence, actionName, text);
        if (row.required()) {
            blocking.add(gap);
        } else {
            warnings.add(gap);
        }
    }

    private Map<String, FieldDescriptionRow> bookOf(int opcode, String protocolVersion) {
        CatalogLookup lookup = instructionCatalog.findMainPacket(opcode, protocolVersion);
        if (lookup == null || !lookup.ok()) {
            return null;
        }
        Map<String, FieldDescriptionRow> book = new LinkedHashMap<>();
        for (FieldDescriptionRow row : lookup.rows()) {
            book.put(row.path(), row);
        }
        return book;
    }

    private static String requestKey(String path) {
        if (path == null || !path.startsWith("request.")) {
            return null;
        }
        String key = path.substring("request.".length());
        if (key.isEmpty() || key.contains(".")) {
            return null;
        }
        return key;
    }

    private static String sourceLabel(String sourceKind) {
        return switch (sourceKind == null ? "" : sourceKind) {
            case "INTERACTION" -> "界面操作";
            case "EXECUTOR_MOUNT" -> "执行设备";
            case "SYSTEM" -> "系统生成";
            case "REFERENCE_RESOURCE" -> "资源文件";
            default -> "动作参数";
        };
    }

    private static List<String> grouped(List<Gap> gaps) {
        Map<String, List<Integer>> sequences = new LinkedHashMap<>();
        Map<String, String> lines = new LinkedHashMap<>();
        for (Gap gap : gaps) {
            String key = gap.actionName() + "\0" + gap.text();
            sequences.computeIfAbsent(key, ignored -> new ArrayList<>()).add(gap.sequence());
            lines.putIfAbsent(key, gap.actionName() + gap.text());
        }
        List<String> result = new ArrayList<>();
        for (Map.Entry<String, List<Integer>> entry : sequences.entrySet()) {
            List<Integer> nums = entry.getValue();
            String shown = nums.stream().limit(12).map(String::valueOf).collect(Collectors.joining("、"));
            String prefix = nums.size() > 12
                    ? "序号 " + shown + " 等共 " + nums.size() + " 个 "
                    : "序号 " + shown + " ";
            result.add(prefix + lines.get(entry.getKey()));
        }
        return result;
    }

    private record Gap(int sequence, String actionName, String text) {
    }

    private record Attempt(Filled filled, List<Gap> blocking, List<Gap> warnings) {
        static Attempt ready(Filled filled) {
            return new Attempt(filled, List.of(), List.of());
        }

        static Attempt ready(Filled filled, List<Gap> warnings) {
            return new Attempt(filled, List.of(), warnings);
        }

        static Attempt blocked(List<Gap> blocking, List<Gap> warnings) {
            return new Attempt(null, blocking, warnings);
        }
    }

    private Filled fillOne(DeviceMissionPlan plan, DispatchAction action) {
        String protocolVersion = plan.protocolVersion();
        MappingRow mapping = catalog.find(action.actionId(), protocolVersion)
                .orElseThrow(() -> new IllegalArgumentException(
                        catalog.describeActionForMessage(action) + " 在"
                                + protocolLabel(protocolVersion)
                                + "下没有指令对照，请到指令协议对接配置"));
        InstructionTemplate template = catalog.loadInstruction(mapping.instructionId());
        Map<String, Object> resolved = paramResolver.resolve(plan, action, mapping.slots());
        Map<String, Object> filled = InstructionPacketFiller.fill(
                template.outbound(), mapping.slots(), resolved);
        return new Filled(template.opcode(), filled, template.communicationProtocol(), template.mqttTopic());
    }

    private void flushPackage(
            DeviceMissionPlan plan,
            List<DownlinkPiece> pieces,
            List<InstructionPackageItem> buffered
    ) {
        if (buffered.isEmpty()) {
            return;
        }
        pieces.add(DownlinkPiece.commandPackage(new CommandSendEnvelope(
                newMsgId(),
                plan.deviceId(),
                plan.templateId(),
                envelopeJsonCodec.encodePackagesJson(buffered)
        )));
        buffered.clear();
    }

    /**
     * 上云 MQTT 空包补上空着的 tid、bid、timestamp，发到这条指令自己写的主题。
     * 没写主题的仍是服务下发。HTTPS、WebSocket、JSBridge 不改发到 services。
     * <p>不套地面站操作码，不把 deviceId 写进报文。deviceId 在这条协议里是网关序列号，只用来拼主题。
     */
    private DirectCommand toCloudDownlink(String gatewaySn, Filled filled) {
        String communication = filled.communicationProtocol();
        if ("https".equals(communication) || "websocket".equals(communication) || "jsbridge".equals(communication)) {
            throw new IllegalArgumentException(communicationLabel(communication)
                    + "指令已经对照到动作。协议网关目前只代发 MQTT，不能把它发到 services");
        }
        return toCloudService(gatewaySn, filled.packet(), filled.mqttTopic());
    }

    private static String communicationLabel(String communication) {
        return switch (communication) {
            case "https" -> "HTTPS";
            case "websocket" -> "WebSocket";
            case "jsbridge" -> "JSBridge";
            default -> communication;
        };
    }

    private DirectCommand toCloudService(String gatewaySn, Map<String, Object> packet, String topicTemplate) {
        Object method = packet.get("method");
        if (!(method instanceof String text) || text.isBlank()) {
            throw new IllegalArgumentException("上云指令空包没有 method，无法下发");
        }
        putIfBlank(packet, "tid", newMsgId());
        putIfBlank(packet, "bid", newMsgId());
        if (!packet.containsKey("data") || packet.get("data") == null) {
            packet.put("data", new LinkedHashMap<>());
        }
        if (!packet.containsKey("timestamp") || packet.get("timestamp") == null
                || (packet.get("timestamp") instanceof String stamp && stamp.isBlank())) {
            packet.put("timestamp", System.currentTimeMillis());
        }
        Object tid = packet.get("tid");
        if (!(tid instanceof String tidText) || tidText.isBlank()) {
            throw new IllegalArgumentException("上云指令空包没有 tid，无法对上答卷");
        }
        String topic = topicTemplate == null || topicTemplate.isBlank()
                ? "thing/product/{gateway_sn}/services"
                : topicTemplate;
        topic = topic.replace("{gateway_sn}", gatewaySn);
        return new DirectCommand(0, tidText, envelopeJsonCodec.encodeObject(packet), topic);
    }

    /**
     * 空包没有内层 request：这条指令自己就是要发出的报文。
     * 设备编号和消息编号只补空包里已经有、且还空着的那两格，不从动作参数里取。
     */
    private DirectCommand toDirect(String deviceId, Filled filled) {
        Map<String, Object> packet = filled.packet();
        fillBlank(packet, "deviceId", deviceId);
        fillBlank(packet, "msgId", newMsgId());
        Object msgId = packet.get("msgId");
        if (!(msgId instanceof String text) || text.isBlank()) {
            throw new IllegalArgumentException("这条指令空包没有消息编号，无法单独下发");
        }
        return new DirectCommand(filled.opcode(), text, envelopeJsonCodec.encodeObject(packet));
    }

    private static void fillBlank(Map<String, Object> packet, String key, String value) {
        if (!packet.containsKey(key)) {
            return;
        }
        Object current = packet.get(key);
        if (current == null || (current instanceof String text && text.isBlank())) {
            packet.put(key, value);
        }
    }

    /** 上云报文必须带 tid、bid。空包里没有这两格时补上，已有非空值不改。 */
    private static void putIfBlank(Map<String, Object> packet, String key, String value) {
        if (!packet.containsKey(key)) {
            packet.put(key, value);
            return;
        }
        fillBlank(packet, key, value);
    }

    /**
     * 拍照这一条已经有张数、点位编号还空：写入同一次移动的点位编号、经度、纬度。
     * 地面高度不写。张数还空的内层报文不写地点。
     */
    private static void writeArrivalPlace(Map<String, Object> request, Map<String, Object> arrival) {
        if (arrival == null || !request.containsKey("number") || !hasText(request.get("number"))) {
            return;
        }
        if (hasText(request.get("pointId"))) {
            return;
        }
        copyPlace(request, arrival, "pointId");
        copyPlace(request, arrival, "lat");
        copyPlace(request, arrival, "lng");
    }

    private static void copyPlace(Map<String, Object> request, Map<String, Object> arrival, String key) {
        if (!request.containsKey(key) || !arrival.containsKey(key) || !hasText(arrival.get(key))) {
            return;
        }
        request.put(key, arrival.get(key));
    }

    private static boolean hasText(Object value) {
        return value != null && !String.valueOf(value).isBlank();
    }

    private static boolean isInnerLine(Map<String, Object> packet) {
        return packet.get("request") instanceof Map<?, ?>;
    }

    private record Filled(int opcode, Map<String, Object> packet, String communicationProtocol, String mqttTopic) {
    }

    private static String protocolLabel(String protocolVersion) {
        return ProtocolCodes.label(protocolVersion);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> requestOf(Map<String, Object> packet) {
        Object request = packet.get("request");
        if (request instanceof Map<?, ?> map) {
            return new LinkedHashMap<>((Map<String, Object>) map);
        }
        return new LinkedHashMap<>();
    }

    private static String newMsgId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
