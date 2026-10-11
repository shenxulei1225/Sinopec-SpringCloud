package cn.cheers.x.device.protocolgateway.datacollection;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.api.entity.EntityRpcApi;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 从数据协议管理读字段说明。权威在协议目录，这里只缓存读结果。
 * <p>不负责：核报文、改说明书。
 * <p>禁止：读失败时编造字段行；同一操作码多份不同说明书时猜一份。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EntityRpcProtocolInstructionCatalog implements ProtocolInstructionCatalog {

    static final String DATA_PROTOCOL_TYPE = "data_protocol";
    static final String FIELD_DESCRIPTION = "field_description_json";
    static final String OPCODE = "opcode";
    static final String MAIN_VOLUME = "outbound";

    private final EntityRpcApi entityRpcApi;
    private final ObjectMapper objectMapper;
    private final DeviceProtocolCatalogCache cache = new DeviceProtocolCatalogCache();

    @Override
    public CatalogLookup findMainPacket(int opcode, String protocolVersion) {
        List<InstructionSpec> matched = matchOpcode(opcode, protocolVersion);
        if (matched == null) {
            return CatalogLookup.failed("读不到指令字段说明");
        }
        List<InstructionSpec> withOutbound = new ArrayList<>();
        for (InstructionSpec spec : matched) {
            if (!spec.rows.isEmpty()) {
                withOutbound.add(spec);
            }
        }
        if (withOutbound.isEmpty()) {
            return missing(protocolVersion);
        }
        String fingerprint = withOutbound.get(0).fingerprint;
        for (InstructionSpec spec : withOutbound) {
            if (!Objects.equals(fingerprint, spec.fingerprint)) {
                return ambiguous(protocolVersion);
            }
        }
        return CatalogLookup.found(withOutbound.get(0).rows);
    }

    @Override
    public CatalogLookup findVolume(int opcode, String protocolVersion, String volume) {
        String key = volume == null ? "" : volume.trim();
        if (key.isBlank()) {
            return CatalogLookup.failed("未指定字段说明分卷");
        }
        List<InstructionSpec> matched = matchOpcode(opcode, protocolVersion);
        if (matched == null) {
            return CatalogLookup.failed("读不到指令字段说明");
        }
        if (matched.isEmpty()) {
            return missing(protocolVersion);
        }
        List<InstructionSpec> withVolume = new ArrayList<>();
        for (InstructionSpec spec : matched) {
            List<FieldDescriptionRow> rows = spec.volumes.getOrDefault(key, List.of());
            if (!rows.isEmpty()) {
                withVolume.add(spec);
            }
        }
        if (withVolume.isEmpty()) {
            return CatalogLookup.failed("字段说明里没有这一卷");
        }
        String fingerprint = labeledFingerprint(withVolume.get(0).volumes.get(key));
        for (InstructionSpec spec : withVolume) {
            if (!Objects.equals(fingerprint, labeledFingerprint(spec.volumes.get(key)))) {
                return ambiguous(protocolVersion);
            }
        }
        return CatalogLookup.found(withVolume.get(0).volumes.get(key));
    }

    /**
     * 按操作码和协议版本筛说明书。读目录失败返回 null，调用方报「读不到」。
     */
    private List<InstructionSpec> matchOpcode(int opcode, String protocolVersion) {
        List<InstructionSpec> specs;
        try {
            specs = cache.getOrLoad(this::loadSpecs);
        } catch (Exception ex) {
            log.error("[device-protocol] 读不到指令字段说明 opcode={}", opcode, ex);
            return null;
        }
        List<InstructionSpec> matched = new ArrayList<>();
        String version = protocolVersion == null ? "" : protocolVersion.trim();
        for (InstructionSpec spec : specs) {
            if (spec.opcode != opcode) {
                continue;
            }
            if (!version.isBlank() && !spec.protocolVersions.contains(version)) {
                continue;
            }
            matched.add(spec);
        }
        return matched;
    }

    private static CatalogLookup missing(String protocolVersion) {
        String version = protocolVersion == null ? "" : protocolVersion.trim();
        return CatalogLookup.failed(version.isBlank()
                ? "找不到该指令的字段说明"
                : "找不到该协议版本的字段说明");
    }

    private static CatalogLookup ambiguous(String protocolVersion) {
        String version = protocolVersion == null ? "" : protocolVersion.trim();
        return CatalogLookup.failed(version.isBlank()
                ? "同一操作码有多份不同说明书，设备未标明协议版本"
                : "同一协议版本下该操作码有多份不同说明书");
    }

    private List<InstructionSpec> loadSpecs() {
        CommonResult<List<EntityRespDTO>> result = entityRpcApi.listEntities(DATA_PROTOCOL_TYPE, null);
        if (result == null || !result.isSuccess() || result.getData() == null) {
            throw new IllegalStateException(result == null ? "说明书目录无响应" : result.getMsg());
        }
        List<InstructionSpec> specs = new ArrayList<>();
        for (EntityRespDTO entity : result.getData()) {
            InstructionSpec spec = parseSpec(entity);
            if (spec != null) {
                specs.add(spec);
            }
        }
        return List.copyOf(specs);
    }

    InstructionSpec parseSpec(EntityRespDTO entity) {
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
        Integer opcode = readOpcode(bag.get(OPCODE));
        if (opcode == null) {
            return null;
        }
        Map<String, List<FieldDescriptionRow>> volumes = readVolumes(bag.get(FIELD_DESCRIPTION));
        if (volumes.isEmpty()) {
            return null;
        }
        List<FieldDescriptionRow> outbound = volumes.getOrDefault(MAIN_VOLUME, List.of());
        return new InstructionSpec(
                opcode,
                outbound,
                fingerprint(outbound),
                readVersions(bag.get("protocol_version")),
                volumes
        );
    }

    static List<String> readVersions(Object raw) {
        return EntityRpcDeviceProtocolVersionCatalog.readVersions(raw);
    }

    private Map<String, List<FieldDescriptionRow>> readVolumes(Object raw) {
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
        Map<String, List<FieldDescriptionRow>> volumes = new LinkedHashMap<>();
        for (String key : List.of(VOLUME_OUTBOUND, VOLUME_INBOUND_SUCCESS, VOLUME_INBOUND_FAILURE)) {
            List<FieldDescriptionRow> rows = readRows(map.get(key));
            if (!rows.isEmpty()) {
                volumes.put(key, rows);
            }
        }
        return volumes;
    }

    private static List<FieldDescriptionRow> readRows(Object volume) {
        if (!(volume instanceof List<?> list)) {
            return List.of();
        }
        List<FieldDescriptionRow> rows = new ArrayList<>();
        for (Object item : list) {
            if (item instanceof Map<?, ?> map) {
                String path = stringOf(map.get("path"));
                if (path.isBlank()) {
                    continue;
                }
                rows.add(new FieldDescriptionRow(
                        path,
                        stringOf(map.get("type")),
                        stringOf(map.get("label")),
                        boolOf(map.get("required"), true)
                ));
            }
        }
        return rows;
    }

    private static Integer readOpcode(Object raw) {
        if (raw instanceof Number number) {
            return number.intValue();
        }
        if (raw instanceof String text && !text.isBlank()) {
            try {
                return Integer.parseInt(text.trim());
            } catch (NumberFormatException ex) {
                return null;
            }
        }
        return null;
    }

    private static String fingerprint(List<FieldDescriptionRow> rows) {
        return fingerprint(rows, false);
    }

    /** 监控对名字敏感：中文名不同也算两份说明书，不能拿其中一份的名字去标另一份的值。 */
    private static String labeledFingerprint(List<FieldDescriptionRow> rows) {
        return fingerprint(rows, true);
    }

    private static String fingerprint(List<FieldDescriptionRow> rows, boolean includeLabel) {
        StringBuilder builder = new StringBuilder();
        rows.stream()
                .map(row -> includeLabel
                        ? row.path() + "|" + row.type() + "|" + row.label() + "|" + row.required()
                        : row.path() + "|" + row.type() + "|" + row.required())
                .sorted()
                .forEach(item -> builder.append(item).append(';'));
        return builder.toString();
    }

    private static String stringOf(Object raw) {
        return raw == null ? "" : String.valueOf(raw).trim();
    }

    private static boolean boolOf(Object raw, boolean defaultValue) {
        if (raw instanceof Boolean flag) {
            return flag;
        }
        if (raw == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(String.valueOf(raw));
    }

    record InstructionSpec(
            int opcode,
            List<FieldDescriptionRow> rows,
            String fingerprint,
            List<String> protocolVersions,
            Map<String, List<FieldDescriptionRow>> volumes
    ) {
        InstructionSpec {
            protocolVersions = protocolVersions == null ? List.of() : List.copyOf(protocolVersions);
            volumes = volumes == null ? Map.of() : Map.copyOf(volumes);
        }
    }
}
