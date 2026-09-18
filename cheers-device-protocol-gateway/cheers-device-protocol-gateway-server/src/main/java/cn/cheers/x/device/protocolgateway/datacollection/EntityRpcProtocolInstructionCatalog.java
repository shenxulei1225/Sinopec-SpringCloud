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
        List<InstructionSpec> specs;
        try {
            specs = cache.getOrLoad(this::loadSpecs);
        } catch (Exception ex) {
            log.error("[device-protocol] 读不到指令字段说明 opcode={}", opcode, ex);
            return CatalogLookup.failed("读不到指令字段说明");
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
        if (matched.isEmpty()) {
            return CatalogLookup.failed(version.isBlank()
                    ? "找不到该指令的字段说明"
                    : "找不到该协议版本的字段说明");
        }
        String fingerprint = matched.get(0).fingerprint;
        for (InstructionSpec spec : matched) {
            if (!Objects.equals(fingerprint, spec.fingerprint)) {
                return CatalogLookup.failed(version.isBlank()
                        ? "同一操作码有多份不同说明书，设备未标明协议版本"
                        : "同一协议版本下该操作码有多份不同说明书");
            }
        }
        return CatalogLookup.found(matched.get(0).rows);
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
        List<FieldDescriptionRow> rows = readMainPacketRows(bag.get(FIELD_DESCRIPTION));
        if (rows.isEmpty()) {
            return null;
        }
        return new InstructionSpec(opcode, rows, fingerprint(rows), readVersions(bag.get("protocol_version")));
    }

    static List<String> readVersions(Object raw) {
        return EntityRpcDeviceProtocolVersionCatalog.readVersions(raw);
    }

    private List<FieldDescriptionRow> readMainPacketRows(Object raw) {
        Object volume = readVolume(raw);
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

    private Object readVolume(Object raw) {
        Object parsed = raw;
        if (raw instanceof String text && !text.isBlank()) {
            try {
                parsed = objectMapper.readValue(text, new TypeReference<Map<String, Object>>() {
                });
            } catch (Exception ex) {
                return null;
            }
        }
        if (parsed instanceof Map<?, ?> map) {
            return map.get(MAIN_VOLUME);
        }
        return null;
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
        StringBuilder builder = new StringBuilder();
        rows.stream()
                .map(row -> row.path() + "|" + row.type() + "|" + row.required())
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
            List<String> protocolVersions
    ) {
        InstructionSpec {
            protocolVersions = protocolVersions == null ? List.of() : List.copyOf(protocolVersions);
        }
    }
}
