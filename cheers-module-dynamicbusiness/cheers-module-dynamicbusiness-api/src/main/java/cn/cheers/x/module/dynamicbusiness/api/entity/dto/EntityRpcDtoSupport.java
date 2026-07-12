package cn.cheers.x.module.dynamicbusiness.api.entity.dto;

import java.util.Map;

/**
 * 从通用 {@link EntityRespDTO} 信封读取业务字段（跨模块消费方使用）。
 */
public final class EntityRpcDtoSupport {

    private EntityRpcDtoSupport() {
    }

    public static String readCode(EntityRespDTO dto) {
        String code = readField(dto, "code");
        return code != null ? code : (dto != null && dto.getId() != null ? String.valueOf(dto.getId()) : null);
    }

    public static String readName(EntityRespDTO dto) {
        return readField(dto, "name");
    }

    public static Long readModelId(EntityRespDTO dto) {
        Object value = readObject(dto, "modelId", "model_id");
        if (value instanceof Number number) {
            return number.longValue();
        }
        if (value == null) {
            return null;
        }
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public static String readField(EntityRespDTO dto, String... fieldCodes) {
        Object value = readObject(dto, fieldCodes);
        return value == null ? null : String.valueOf(value);
    }

    private static Object readObject(EntityRespDTO dto, String... fieldCodes) {
        if (dto == null || fieldCodes == null) {
            return null;
        }
        for (String fieldCode : fieldCodes) {
            Object fromBase = getIgnoreCase(dto.getBaseFields(), fieldCode);
            if (fromBase != null) {
                return fromBase;
            }
            Object fromCustom = getIgnoreCase(dto.getCustomFields(), fieldCode);
            if (fromCustom != null) {
                return fromCustom;
            }
        }
        return null;
    }

    private static Object getIgnoreCase(Map<String, Object> map, String key) {
        if (map == null || key == null) {
            return null;
        }
        if (map.containsKey(key)) {
            return map.get(key);
        }
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getKey() != null && entry.getKey().equalsIgnoreCase(key)) {
                return entry.getValue();
            }
        }
        return null;
    }

}
