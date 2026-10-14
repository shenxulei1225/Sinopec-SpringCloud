package cn.cheers.x.device.protocolgateway.protocol.monitor;

import cn.cheers.x.device.protocolgateway.api.dto.ProtocolMonitorInstructionDTO;
import cn.cheers.x.device.protocolgateway.api.protocol.ProtocolCodes;
import cn.cheers.x.device.protocolgateway.datacollection.EntityRpcDeviceProtocolVersionCatalog;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.api.entity.EntityRpcApi;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 从协议指令读下发空包。空包在扩展字段 command_json 里。
 * <p>负责：按设备默认协议列出能下发的指令，并取出地面站下发空包。
 * <p>不负责：填设备编号、写出报文。
 * <p>禁止：物理列是空对象时盖掉扩展字段里的空包；同一操作码有多份不同空包时猜一份。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProtocolMonitorDownlinkCatalog {

    static final String DATA_PROTOCOL_TYPE = "data_protocol";
    static final String DIRECTION_DOWNLINK = "downlink";

    private final EntityRpcApi entityRpcApi;
    private final ObjectMapper objectMapper;
    private final Cache cache = new Cache();

    public List<ProtocolMonitorInstructionDTO> list(String protocol) {
        String version = protocol == null ? "" : protocol.trim();
        List<Prepared> rows = new ArrayList<>();
        if (ProtocolCodes.isDjiCloud(version)) {
            rows.addAll(cloudInstructions(version));
        } else {
            rows.addAll(assemble(load(version)));
        }
        return rows.stream().map(Prepared::toDto).toList();
    }

    public Prepared require(String protocol, int opcode) {
        List<Prepared> matched = new ArrayList<>();
        for (Prepared row : load(protocol)) {
            if (row.opcode == opcode) {
                matched.add(row);
            }
        }
        if (matched.isEmpty()) {
            throw new IllegalArgumentException("这份协议里没有这条下发指令");
        }
        Prepared prepared = assemble(matched).get(0);
        if (!prepared.sendAlone) {
            throw new IllegalArgumentException(prepared.blockReason);
        }
        return prepared;
    }

    List<Prepared> load(String protocol) {
        String version = protocol == null ? "" : protocol.trim();
        if (version.isBlank()) {
            return List.of();
        }
        List<Spec> specs = cachedSpecs();
        List<Prepared> rows = new ArrayList<>();
        for (Spec spec : specs) {
            if (!DIRECTION_DOWNLINK.equals(spec.direction) || !spec.versions.contains(version) || spec.outbound.isEmpty()) {
                continue;
            }
            rows.add(Prepared.alone(spec));
        }
        rows.sort(Comparator.comparingInt(row -> row.opcode));
        return rows;
    }

    /**
     * 大疆机场和 Pilot 只列下发。上报不从这里发出。
     * 操作码都是 0，不能按操作码收成一条。不从地面站连接发出。
     */
    private List<Prepared> cloudInstructions(String protocol) {
        List<Prepared> rows = new ArrayList<>();
        java.util.Set<String> keys = new java.util.HashSet<>();
        for (Spec spec : cachedSpecs()) {
            if (spec.direction.isBlank() || "uplink".equals(spec.direction) || "ack".equals(spec.direction)) {
                continue;
            }
            if (!spec.versions.contains(protocol) || spec.communication.isBlank()) {
                continue;
            }
            rows.add(unique(keys, Prepared.cloud(spec)));
        }
        rows.sort(Comparator.comparing((Prepared row) -> row.communication)
                .thenComparing(row -> row.direction)
                .thenComparing(row -> row.name));
        return rows;
    }

    private static Prepared unique(java.util.Set<String> keys, Prepared row) {
        String key = row.instructionKey;
        int suffix = 2;
        while (!keys.add(key)) {
            key = row.instructionKey + ":" + suffix;
            suffix++;
        }
        if (key.equals(row.instructionKey)) {
            return row;
        }
        return new Prepared(
                row.name, row.opcode, row.outbound, row.sendAlone, row.blockReason,
                row.communication, key, row.direction);
    }

    private List<Spec> cachedSpecs() {
        try {
            return cache.getOrLoad(this::loadSpecs);
        } catch (Exception ex) {
            log.error("[device-protocol] 读不到下发指令", ex);
            throw new IllegalArgumentException("读不到协议指令");
        }
    }

    private static List<Prepared> assemble(List<Prepared> rows) {
        Map<Integer, List<Prepared>> grouped = new LinkedHashMap<>();
        for (Prepared row : rows) {
            grouped.computeIfAbsent(row.opcode, key -> new ArrayList<>()).add(row);
        }
        List<Prepared> shown = new ArrayList<>();
        for (List<Prepared> group : grouped.values()) {
            Prepared first = group.get(0);
            boolean samePacket = group.stream().allMatch(row -> Objects.equals(row.outbound, first.outbound));
            if (group.size() > 1 && !samePacket) {
                shown.add(new Prepared(
                        first.name,
                        first.opcode,
                        Map.of(),
                        false,
                        "同一协议下这条指令有多份不同空包",
                        "",
                        String.valueOf(first.opcode),
                        first.direction
                ));
                continue;
            }
            shown.add(first);
        }
        return shown;
    }

    private List<Spec> loadSpecs() {
        CommonResult<List<EntityRespDTO>> result = entityRpcApi.listEntities(DATA_PROTOCOL_TYPE, null);
        if (result == null || !result.isSuccess() || result.getData() == null) {
            throw new IllegalStateException(result == null ? "说明书目录无响应" : result.getMsg());
        }
        List<Spec> specs = new ArrayList<>();
        for (EntityRespDTO entity : result.getData()) {
            Spec spec = parseSpec(entity);
            if (spec != null) {
                specs.add(spec);
            }
        }
        return List.copyOf(specs);
    }

    Spec parseSpec(EntityRespDTO entity) {
        if (entity == null) {
            return null;
        }
        Map<String, Object> bag = new LinkedHashMap<>();
        if (entity.getBaseFields() != null) {
            bag.putAll(entity.getBaseFields());
        }
        if (entity.getCustomFields() != null) {
            bag.putAll(entity.getCustomFields());
        }
        Integer opcode = readOpcode(bag.get("opcode"));
        String communication = stringOf(bag.get("communication_protocol"));
        Map<String, Object> outbound = readOutbound(bag.get("command_json"));
        if (opcode == null || (outbound.isEmpty() && communication.isBlank())) {
            return null;
        }
        String name = stringOf(bag.get("name"));
        if (name.isBlank()) {
            name = String.valueOf(opcode);
        }
        String topic = stringOf(bag.get("mqtt_topic"));
        if (topic.isBlank()) {
            String method = stringOf(bag.get("http_method"));
            String path = stringOf(bag.get("http_path"));
            if (!method.isBlank() || !path.isBlank()) {
                topic = (method + " " + path).trim();
            }
        }
        return new Spec(
                name,
                opcode,
                stringOf(bag.get("command_direction")),
                EntityRpcDeviceProtocolVersionCatalog.readVersions(bag.get("protocol_version")),
                outbound,
                communication,
                topic
        );
    }

    private Map<String, Object> readOutbound(Object raw) {
        Object parsed = raw;
        if (raw instanceof String text && !text.isBlank()) {
            try {
                parsed = objectMapper.readValue(text, new TypeReference<Map<String, Object>>() {
                });
            } catch (Exception ex) {
                return Map.of();
            }
        }
        if (!(parsed instanceof Map<?, ?> map)) {
            return Map.of();
        }
        Object outbound = map.get("outbound");
        if (!(outbound instanceof Map<?, ?> packet) || packet.isEmpty()) {
            return Map.of();
        }
        Map<String, Object> copy = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : packet.entrySet()) {
            if (entry.getKey() != null) {
                copy.put(String.valueOf(entry.getKey()), entry.getValue());
            }
        }
        return copy;
    }

    private static Integer readOpcode(Object raw) {
        if (raw instanceof Number number) {
            return number.intValue();
        }
        String text = stringOf(raw);
        if (text.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static String stringOf(Object raw) {
        return raw == null ? "" : String.valueOf(raw).trim();
    }

    record Spec(
            String name,
            int opcode,
            String direction,
            List<String> versions,
            Map<String, Object> outbound,
            String communication,
            String topic
    ) {
    }

    record Prepared(
            String name,
            int opcode,
            Map<String, Object> outbound,
            boolean sendAlone,
            String blockReason,
            String communication,
            String instructionKey,
            String direction
    ) {

        static Prepared alone(Spec spec) {
            String key = String.valueOf(spec.opcode);
            if (ProtocolMonitorDownlinkFiller.isInnerStep(spec.outbound)) {
                return new Prepared(
                        spec.name, spec.opcode, spec.outbound, false,
                        "这条要放进巡检任务指令包，不能单独下发", "", key, spec.direction);
            }
            return new Prepared(spec.name, spec.opcode, spec.outbound, true, "", "", key, spec.direction);
        }

        static Prepared cloud(Spec spec) {
            String where = switch (spec.communication) {
                case "wayline" -> "这条写进航线文件";
                case "https" -> "这条走 HTTPS" + detail(spec.topic);
                case "jsbridge" -> "这条走遥控器里的 JSBridge";
                case "websocket" -> "这条走 Pilot 的 WebSocket";
                default -> "这条走 MQTT" + (spec.topic.isBlank() ? "" : "，主题是 " + spec.topic);
            };
            String reason = "uplink".equals(spec.direction)
                    ? where + "。这是上报，不从地面站连接发出"
                    : where + "，不从地面站连接发出";
            String key = spec.communication + ":" + spec.direction + ":" + spec.name;
            return new Prepared(spec.name, spec.opcode, spec.outbound, false, reason, spec.communication, key, spec.direction);
        }

        private static String detail(String topic) {
            return topic.isBlank() ? "" : " " + topic;
        }

        ProtocolMonitorInstructionDTO toDto() {
            return new ProtocolMonitorInstructionDTO(
                    name,
                    opcode,
                    sendAlone,
                    blockReason.isBlank() ? null : blockReason,
                    instructionKey,
                    communication,
                    direction
            );
        }
    }

    static final class Cache {
        private static final long TTL_MS = 60_000L;
        private volatile List<Spec> specs = List.of();
        private volatile long expireAtEpochMs = 0L;

        synchronized List<Spec> getOrLoad(java.util.function.Supplier<List<Spec>> loader) {
            long now = System.currentTimeMillis();
            if (now < expireAtEpochMs && !specs.isEmpty()) {
                return specs;
            }
            List<Spec> loaded = loader.get();
            specs = loaded == null ? List.of() : List.copyOf(loaded);
            expireAtEpochMs = now + TTL_MS;
            return specs;
        }
    }
}
