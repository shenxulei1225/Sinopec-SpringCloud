package cn.cheers.x.device.protocolgateway.instructiondispatch;

import cn.cheers.x.device.protocolgateway.api.mission.DispatchAction;
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
import java.util.Optional;

/**
 * 从指令协议对接目录读对照和空包。
 * <p>禁止：没有对照时猜指令；读失败假装有对照。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EntityRpcProtocolMappingCatalog implements ProtocolMappingCatalog {

    static final String MAPPING_TYPE = "protocol_mapping";
    static final String ACTION_TYPE = "action";
    static final String PROTOCOL_TYPE = "data_protocol";
    static final String ACTION_REF = "action_ref";
    static final String PROTOCOL_VERSION = "mapped_protocol_version";
    static final String MAPPING_STATUS = "protocol_mapping_status";
    static final String STATUS_CONFIGURED = "CONFIGURED";
    static final String INSTRUCTION_REF = "protocol_instruction_ref";
    static final String SLOT_MAPPING = "param_slot_mapping_json";
    static final String COMMAND_JSON = "command_json";
    static final String OPCODE = "opcode";

    private final EntityRpcApi entityRpcApi;
    private final ObjectMapper objectMapper;
    private final MappingCache cache = new MappingCache();

    @Override
    public Optional<MappingRow> find(long actionId, String protocolVersion) {
        if (protocolVersion == null || protocolVersion.isBlank()) {
            return Optional.empty();
        }
        String version = protocolVersion.trim();
        List<MappingRow> rows;
        try {
            rows = cache.getOrLoad(this::loadRows);
        } catch (Exception ex) {
            log.error("[device-protocol] 读不到指令对照 actionId={} version={}", actionId, version, ex);
            throw new IllegalStateException("读不到指令对照", ex);
        }
        return selectUnique(rows, actionId, version);
    }

    /**
     * 一个动作在一个协议版本下只能对上一条指令。对上多条时无法确定下发用哪条，直接拒绝。
     */
    static Optional<MappingRow> selectUnique(List<MappingRow> rows, long actionId, String version) {
        MappingRow found = null;
        if (rows != null) {
            for (MappingRow row : rows) {
                if (row.actionId() != actionId || !version.equals(row.protocolVersion())) {
                    continue;
                }
                if (found != null) {
                    throw new IllegalStateException(
                            "动作库编号 " + actionId + " 在"
                                    + cn.cheers.x.device.protocolgateway.api.protocol.ProtocolCodes.label(version)
                                    + "下有多条指令对照，无法确定用哪一条");
                }
                found = row;
            }
        }
        return Optional.ofNullable(found);
    }

    @Override
    public String describeActionForMessage(DispatchAction action) {
        if (action == null) {
            return "未知动作";
        }
        String code = action.actionCode();
        String name = resolveActionDisplayName(action.actionId(), code);
        if (name != null && !name.isBlank()) {
            if (code != null && !code.isBlank()) {
                return "「" + name.trim() + "」（" + code.trim() + "，动作库编号 " + action.actionId() + "）";
            }
            return "「" + name.trim() + "」（动作库编号 " + action.actionId() + "）";
        }
        return ProtocolMappingCatalog.super.describeActionForMessage(action);
    }

    private String resolveActionDisplayName(long actionId, String actionCode) {
        EntityRespDTO entity = null;
        if (actionCode != null && !actionCode.isBlank()) {
            entity = optionalEntity(entityRpcApi.getEntityByCode(actionCode.trim(), ACTION_TYPE));
        }
        if (entity == null) {
            entity = optionalEntity(entityRpcApi.getEntity(actionId, ACTION_TYPE));
        }
        if (entity == null) {
            return null;
        }
        Object name = bagOf(entity).get("name");
        return name == null ? null : String.valueOf(name).trim();
    }

    private static EntityRespDTO optionalEntity(CommonResult<EntityRespDTO> result) {
        if (result == null || !result.isSuccess() || result.getData() == null) {
            return null;
        }
        return result.getData();
    }

    @Override
    public InstructionTemplate loadInstruction(long instructionId) {
        CommonResult<EntityRespDTO> result = entityRpcApi.getEntity(instructionId, PROTOCOL_TYPE);
        if (result == null || !result.isSuccess() || result.getData() == null) {
            throw new IllegalStateException("读不到协议指令：" + instructionId);
        }
        return parseTemplate(result.getData());
    }

    private List<MappingRow> loadRows() {
        CommonResult<List<EntityRespDTO>> result = entityRpcApi.listEntities(MAPPING_TYPE, null);
        if (result == null || !result.isSuccess() || result.getData() == null) {
            throw new IllegalStateException(result == null ? "对照目录无响应" : result.getMsg());
        }
        List<MappingRow> rows = new ArrayList<>();
        for (EntityRespDTO entity : result.getData()) {
            MappingRow row = parseRow(entity);
            if (row != null) {
                rows.add(row);
            }
        }
        return List.copyOf(rows);
    }

    MappingRow parseRow(EntityRespDTO entity) {
        if (entity == null) {
            return null;
        }
        Map<String, Object> bag = bagOf(entity);
        String status = stringOf(bag.get(MAPPING_STATUS));
        if ("UNSUPPORTED".equalsIgnoreCase(status)) {
            return null;
        }
        if (!STATUS_CONFIGURED.equalsIgnoreCase(status)) {
            throw new IllegalStateException("指令协议对照没有处理状态：" + entity.getId());
        }
        Long actionId = readRefId(bag.get(ACTION_REF));
        Long instructionId = readRefId(bag.get(INSTRUCTION_REF));
        String version = stringOf(bag.get(PROTOCOL_VERSION));
        if (actionId == null || instructionId == null || version.isBlank()) {
            throw new IllegalStateException("已配置的指令协议对照不完整：" + entity.getId());
        }
        return new MappingRow(actionId, version, instructionId, readSlots(bag.get(SLOT_MAPPING)));
    }

    InstructionTemplate parseTemplate(EntityRespDTO entity) {
        Map<String, Object> bag = bagOf(entity);
        Map<String, Object> outbound = readOutbound(bag.get(COMMAND_JSON));
        Integer opcode = readOpcode(bag.get(OPCODE));
        if (opcode == null && outbound.get(OPCODE) instanceof Number number) {
            opcode = number.intValue();
        }
        if (opcode == null) {
            if (hasDispatchIdentity(outbound)) {
                return new InstructionTemplate(0, outbound, communicationOf(bag), topicOf(bag));
            }
            throw new IllegalStateException("协议指令没有操作码");
        }
        return new InstructionTemplate(opcode, outbound, communicationOf(bag), topicOf(bag));
    }

    private Map<String, Object> readOutbound(Object raw) {
        Object parsed = raw;
        if (raw instanceof String text && !text.isBlank()) {
            try {
                parsed = objectMapper.readValue(text, new TypeReference<Map<String, Object>>() {
                });
            } catch (Exception ex) {
                throw new IllegalStateException("指令 JSON 无法解析", ex);
            }
        }
        if (parsed instanceof Map<?, ?> map && map.get("outbound") instanceof Map<?, ?> outbound) {
            return castMap(outbound);
        }
        throw new IllegalStateException("协议指令没有主包空包");
    }

    private List<SlotMapping> readSlots(Object raw) {
        Object parsed = raw;
        if (raw instanceof String text && !text.isBlank()) {
            try {
                parsed = objectMapper.readValue(text, new TypeReference<List<Map<String, Object>>>() {
                });
            } catch (Exception ex) {
                return List.of();
            }
        }
        if (!(parsed instanceof List<?> list)) {
            return List.of();
        }
        List<SlotMapping> slots = new ArrayList<>();
        for (Object item : list) {
            if (item instanceof Map<?, ?> map) {
                String idFrom = stringOf(map.get("idFrom"));
                // 旧对照里按业务记录来源写的行不是动作参数。协议不再保存这种行，残留的也不拿去填包。
                if (!idFrom.isBlank()) {
                    continue;
                }
                String slot = stringOf(map.get("slot"));
                String path = stringOf(map.get("path"));
                if (!slot.isBlank() && !path.isBlank()) {
                    slots.add(new SlotMapping(
                            slot,
                            stringOf(map.get("sourcePath")),
                            path,
                            stringOf(map.get("sourceKind"))));
                }
            }
        }
        return slots;
    }

    private static Map<String, Object> bagOf(EntityRespDTO entity) {
        Map<String, Object> bag = new LinkedHashMap<>();
        if (entity.getBaseFields() != null) {
            bag.putAll(entity.getBaseFields());
        }
        if (entity.getCustomFields() != null) {
            bag.putAll(entity.getCustomFields());
        }
        return bag;
    }

    private static Long readRefId(Object raw) {
        if (raw instanceof Number number) {
            return number.longValue() > 0 ? number.longValue() : null;
        }
        if (raw instanceof Map<?, ?> map) {
            Object id = map.get("id");
            if (id instanceof Number number && number.longValue() > 0) {
                return number.longValue();
            }
        }
        return null;
    }

    private static boolean hasDispatchIdentity(Map<String, Object> outbound) {
        return textOf(outbound.get("method"))
                || textOf(outbound.get("httpMethod"))
                || textOf(outbound.get("bridge"))
                || textOf(outbound.get("biz_code"));
    }

    private static boolean textOf(Object raw) {
        return raw instanceof String text && !text.isBlank();
    }

    private static String communicationOf(Map<String, Object> bag) {
        return stringOf(bag.get("communication_protocol"));
    }

    private static String topicOf(Map<String, Object> bag) {
        return stringOf(bag.get("mqtt_topic"));
    }

    private static Integer readOpcode(Object raw) {
        if (raw instanceof Number number) {
            return number.intValue();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> castMap(Map<?, ?> raw) {
        return new LinkedHashMap<>((Map<String, Object>) raw);
    }

    private static String stringOf(Object raw) {
        return raw == null ? "" : String.valueOf(raw).trim();
    }

    static final class MappingCache {
        private static final long TTL_MS = 60_000L;
        private volatile List<MappingRow> rows = List.of();
        private volatile long expireAtEpochMs = 0L;

        synchronized List<MappingRow> getOrLoad(java.util.function.Supplier<List<MappingRow>> loader) {
            long now = System.currentTimeMillis();
            if (now < expireAtEpochMs && !rows.isEmpty()) {
                return rows;
            }
            List<MappingRow> loaded = loader.get();
            rows = loaded == null ? List.of() : List.copyOf(loaded);
            expireAtEpochMs = now + TTL_MS;
            return rows;
        }
    }
}
