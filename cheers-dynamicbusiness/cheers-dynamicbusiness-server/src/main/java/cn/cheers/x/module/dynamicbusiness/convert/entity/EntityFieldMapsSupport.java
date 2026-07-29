package cn.cheers.x.module.dynamicbusiness.convert.entity;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.framework.common.exception.ServiceException;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * 实体 Write/Read 与 DO 之间的 baseFields / customFields 分桶转换。
 *
 * <p>固定列（含 entityTypeCode、modelId、name、code、status、parentId、domain）在 API 层进入 {@code baseFields}；
 * DO 表列存核心固定列。业务类型基础字段若已在专用表建列（列名=字段编码规范化），由
 * {@link cn.cheers.x.module.dynamicbusiness.service.entity.EntityDedicatedColumnService} 读写，并从 customFields 剥离；
 * 其余未建列的 base 扩展键仍合并进 customFields JSONB。</p>
 */
public final class EntityFieldMapsSupport {

    private static final Set<String> CORE_BASE_KEYS = Set.of(
            "entitytypecode", "entity_type_code",
            "businesstypecode", "business_type_code",
            "modelid", "model_id",
            "name", "code", "status", "parentid", "parent_id",
            "domain");

    private EntityFieldMapsSupport() {
    }

    public static Map<String, Object> normalizeMap(Map<String, Object> source) {
        return source == null ? new LinkedHashMap<>() : new LinkedHashMap<>(source);
    }

    public static boolean isCoreBaseFieldKey(String fieldCode) {
        if (fieldCode == null || fieldCode.isBlank()) {
            return false;
        }
        return CORE_BASE_KEYS.contains(fieldCode.trim().toLowerCase());
    }

    public static String getEntityTypeCode(Map<String, Object> baseFields) {
        return asString(firstPresent(baseFields,
                "entityTypeCode", "entity_type_code", "businessTypeCode", "business_type_code"));
    }

    public static Long getModelId(Map<String, Object> baseFields) {
        return asLong(firstPresent(baseFields, "modelId", "model_id"));
    }

    public static Long getRequiredModelId(Map<String, Object> baseFields) {
        Long modelId = getModelId(baseFields);
        if (modelId == null) {
            throw new ServiceException(400, "baseFields.modelId 不能为空");
        }
        return modelId;
    }

    public static String getRequiredEntityTypeCode(Map<String, Object> baseFields) {
        String code = getEntityTypeCode(baseFields);
        if (code == null || code.isBlank()) {
            throw new ServiceException(400, "baseFields.entityTypeCode 不能为空");
        }
        return code.trim();
    }

    /**
     * Write Req → DO：核心列落表，非核心 base 键并入 customFields。
     */
    public static void applyWriteMapsToEntityDO(EntityDO entity, Map<String, Object> baseFields, Map<String, Object> customFields) {
        Map<String, Object> base = normalizeMap(baseFields);
        Map<String, Object> custom = normalizeMap(customFields);

        entity.setEntityTypeCode(getEntityTypeCode(base));
        entity.setModelId(getModelId(base));
        entity.setName(asString(firstPresent(base, "name")));
        String code = asString(firstPresent(base, "code"));
        if (code == null) {
            code = asString(custom.get("code"));
        }
        entity.setCode(code);
        entity.setStatus(asInteger(firstPresent(base, "status")));
        entity.setParentId(asLong(firstPresent(base, "parentId", "parent_id")));

        Map<String, Object> mergedCustom = new LinkedHashMap<>(custom);
        for (Map.Entry<String, Object> entry : base.entrySet()) {
            if (!isCoreBaseFieldKey(entry.getKey())) {
                mergedCustom.put(entry.getKey(), entry.getValue());
            }
        }
        // code 已提升为表列时，勿再留在 JSONB，避免双写语义分裂
        mergedCustom.remove("code");
        entity.setCustomFields(mergedCustom.isEmpty() ? null : mergedCustom);
    }

    /**
     * DO → Read Resp：核心列组装 baseFields；customFields 原样（含扩展 base 键，decode 时由 form defs 分桶）。
     */
    public static Map<String, Object> buildBaseFieldsFromEntityDO(EntityDO entity) {
        Map<String, Object> base = new LinkedHashMap<>();
        if (entity == null) {
            return base;
        }
        putIfNotNull(base, "entityTypeCode", entity.getEntityTypeCode());
        putIfNotNull(base, "modelId", entity.getModelId());
        putIfNotNull(base, "name", entity.getName());
        putIfNotNull(base, "code", entity.getCode());
        putIfNotNull(base, "status", entity.getStatus());
        putIfNotNull(base, "parentId", entity.getParentId());
        putIfNotNull(base, "domain", entity.getDomain());
        return base;
    }

    private static Object firstPresent(Map<String, Object> map, String... keys) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        for (String key : keys) {
            if (map.containsKey(key)) {
                return map.get(key);
            }
        }
        return null;
    }

    private static void putIfNotNull(Map<String, Object> target, String key, Object value) {
        if (value != null) {
            target.put(key, value);
        }
    }

    private static String asString(Object value) {
        if (value == null) {
            return null;
        }
        String text = String.valueOf(value).trim();
        return text.isEmpty() ? null : text;
    }

    private static Long asLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        try {
            return Long.parseLong(String.valueOf(value).trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static Long asLong(Object primary, Object secondary) {
        Long value = asLong(primary);
        return value != null ? value : asLong(secondary);
    }

    private static Integer asInteger(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(value).trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    /** 供校验/日志提取变更字段编码。 */
    public static Set<String> extractFieldCodes(Map<String, Object> baseFields, Map<String, Object> customFields) {
        Set<String> codes = new LinkedHashSet<>();
        codes.addAll(normalizeMap(baseFields).keySet());
        codes.addAll(normalizeMap(customFields).keySet());
        return codes;
    }

    public static String asStringFromMap(Map<String, Object> map, String key) {
        if (map == null || key == null) {
            return null;
        }
        return asString(map.get(key));
    }

    public static Long asLongFromMap(Map<String, Object> map, String key) {
        if (map == null || key == null) {
            return null;
        }
        return asLong(map.get(key));
    }

    public static Integer asIntegerFromMap(Map<String, Object> map, String key) {
        if (map == null || key == null) {
            return null;
        }
        return asInteger(map.get(key));
    }
}
