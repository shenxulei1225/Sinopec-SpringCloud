package cn.cheers.x.inspection.task.service.task;

import cn.cheers.x.inspection.task.service.execution.steptree.HostSopParamPack;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRpcDtoSupport;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 一页已生成任务的实时核对读快照。
 *
 * <p>实体和适用关系由页级批量查询一次装入。本对象只做内存读取。</p>
 * <p>不负责补缺失实体，也不改变任务或标准库。</p>
 */
public final class GeneratedCheckInputReviewReadContext {

    private static final String FIELD_HOST_PACK = "host_sop_param_pack";

    private final Map<Long, EntityRespDTO> equipmentById;
    private final Map<Long, EntityRespDTO> itemById;
    private final Map<String, EntityRespDTO> actionByCode;
    private final Map<Long, Set<Long>> applicableItemIdsByEquipment;
    private final ObjectMapper objectMapper;

    GeneratedCheckInputReviewReadContext(
            Map<Long, EntityRespDTO> equipmentById,
            Map<Long, EntityRespDTO> itemById,
            Map<String, EntityRespDTO> actionByCode,
            Map<Long, Set<Long>> applicableItemIdsByEquipment,
            ObjectMapper objectMapper) {
        this.equipmentById = Map.copyOf(equipmentById);
        this.itemById = Map.copyOf(itemById);
        this.actionByCode = Map.copyOf(actionByCode);
        this.applicableItemIdsByEquipment = Map.copyOf(applicableItemIdsByEquipment);
        this.objectMapper = objectMapper;
    }

    public EntityRespDTO equipment(Long id) {
        return id == null ? null : equipmentById.get(id);
    }

    public EntityRespDTO item(Long id) {
        return id == null ? null : itemById.get(id);
    }

    public EntityRespDTO action(String code) {
        return code == null ? null : actionByCode.get(code.trim());
    }

    public Set<Long> applicableItemIds(Long equipmentId) {
        return applicableItemIdsByEquipment.getOrDefault(equipmentId, Set.of());
    }

    public HostSopParamPack hostPack(Long equipmentId) {
        EntityRespDTO equipment = equipment(equipmentId);
        return equipment == null
                ? HostSopParamPack.empty()
                : HostSopParamPack.parse(bagOf(equipment).get(FIELD_HOST_PACK), objectMapper);
    }

    public Long equipmentModelId(Long equipmentId) {
        return EntityRpcDtoSupport.readModelId(equipment(equipmentId));
    }

    public List<Long> mountedModelIds(Long equipmentId) {
        EntityRespDTO host = equipment(equipmentId);
        if (host == null) {
            return List.of();
        }
        Object raw = bagOf(host).get("payload_mounts");
        Object value = raw;
        if (raw instanceof String text && !text.isBlank()) {
            try {
                value = objectMapper.readValue(text, new TypeReference<List<Map<String, Object>>>() {
                });
            } catch (Exception ex) {
                return List.of();
            }
        }
        if (!(value instanceof List<?> rows)) {
            return List.of();
        }
        List<Long> modelIds = new ArrayList<>();
        for (Object row : rows) {
            if (!(row instanceof Map<?, ?> map)) {
                continue;
            }
            Long childId = longOf(map.get("equipmentId"));
            Long modelId = equipmentModelId(childId);
            if (modelId != null && !modelIds.contains(modelId)) {
                modelIds.add(modelId);
            }
        }
        return modelIds;
    }

    static Set<Long> mountedEquipmentIds(EntityRespDTO host, ObjectMapper objectMapper) {
        if (host == null) {
            return Set.of();
        }
        Object raw = bagOf(host).get("payload_mounts");
        Object value = raw;
        if (raw instanceof String text && !text.isBlank()) {
            try {
                value = objectMapper.readValue(text, new TypeReference<List<Map<String, Object>>>() {
                });
            } catch (Exception ex) {
                return Set.of();
            }
        }
        if (!(value instanceof List<?> rows)) {
            return Set.of();
        }
        Set<Long> ids = new LinkedHashSet<>();
        for (Object row : rows) {
            if (row instanceof Map<?, ?> map) {
                Long id = longOf(map.get("equipmentId"));
                if (id != null) {
                    ids.add(id);
                }
            }
        }
        return ids;
    }

    static Map<String, Object> bagOf(EntityRespDTO entity) {
        Map<String, Object> bag = new LinkedHashMap<>();
        if (entity != null && entity.getBaseFields() != null) {
            bag.putAll(entity.getBaseFields());
        }
        if (entity != null && entity.getCustomFields() != null) {
            bag.putAll(entity.getCustomFields());
        }
        return bag;
    }

    private static Long longOf(Object raw) {
        if (raw instanceof Number number && number.longValue() > 0) {
            return number.longValue();
        }
        if (raw instanceof String text && !text.isBlank()) {
            try {
                long value = Long.parseLong(text.trim());
                return value > 0 ? value : null;
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }
}
