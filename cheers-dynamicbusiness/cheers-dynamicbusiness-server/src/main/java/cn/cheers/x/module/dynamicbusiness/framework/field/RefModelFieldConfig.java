package cn.cheers.x.module.dynamicbusiness.framework.field;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 引用型号字段的一份配置。
 *
 * 负责：把字段 options 收成数据目录和型号。
 * 不管什么：详情上勾了哪些字段；主云台当前挂了哪台设备；型号上那些字段的数值。
 * 禁止：缺目录时补一个默认型号。字段库不保存要显示的字段名单。
 */
public final class RefModelFieldConfig {

    private RefModelFieldConfig() {
    }

    /**
     * entityTypeCode：数据目录。
     * sourceModelId：要读的型号。空表示读取方用主云台当前挂着的那台设备的型号。
     * sourceFieldIds：字段库不再使用。留空。详情各自保存要显示的字段。
     */
    public record Value(String entityTypeCode, Long sourceModelId, List<Long> sourceFieldIds) {
    }

    /**
     * 读已保存的配置。有数据目录就返回。字段名单可空。
     * 不是这份 JSON，或型号编号不是正整数，返回 null。
     */
    public static Value parse(String optionsJson) {
        if (!JSONUtil.isTypeJSONObject(optionsJson)) {
            return null;
        }
        JSONObject object = JSONUtil.parseObj(optionsJson);
        String entityTypeCode = text(object.get("entityTypeCode"));
        if (entityTypeCode == null) {
            return null;
        }
        Long sourceModelId = positiveLong(object.get("sourceModelId"));
        if (object.get("sourceModelId") != null && sourceModelId == null && !isBlank(object.get("sourceModelId"))) {
            return null;
        }
        List<Long> sourceFieldIds = fieldIds(object.get("sourceFieldIds"));
        if (sourceFieldIds == null) {
            return null;
        }
        return new Value(entityTypeCode, sourceModelId, List.of());
    }

    /**
     * 保存前收成一份。缺数据目录就拒绝。不要求勾字段。
     */
    public static Value require(String optionsJson, String targetEntityType) {
        if (!JSONUtil.isTypeJSONObject(optionsJson)) {
            throw new ServiceException(400, "引用型号字段要写数据目录");
        }
        JSONObject object = JSONUtil.parseObj(optionsJson);
        String fromOptions = text(object.get("entityTypeCode"));
        String fromTarget = text(targetEntityType);
        if (fromOptions != null && fromTarget != null && !fromOptions.equals(fromTarget)) {
            throw new ServiceException(400, "数据目录写了两份，而且不一致");
        }
        String entityTypeCode = fromOptions != null ? fromOptions : fromTarget;
        if (entityTypeCode == null) {
            throw new ServiceException(400, "请选择数据目录");
        }
        if (object.get("sourceModelId") != null
                && !isBlank(object.get("sourceModelId"))
                && positiveLong(object.get("sourceModelId")) == null) {
            throw new ServiceException(400, "型号编号不正确");
        }
        if (fieldIds(object.get("sourceFieldIds")) == null) {
            throw new ServiceException(400, "要点名的字段格式不正确");
        }
        return new Value(entityTypeCode, positiveLong(object.get("sourceModelId")), List.of());
    }

    /** 写成字段 options 里的那一份 JSON。sourceModelId 空就写 null。 */
    public static String write(Value value) {
        JSONObject object = new JSONObject(true);
        object.set("entityTypeCode", value.entityTypeCode());
        object.set("sourceModelId", value.sourceModelId());
        object.set("sourceFieldIds", List.of());
        return object.toString();
    }

    private static List<Long> fieldIds(Object raw) {
        JSONArray array;
        if (raw instanceof JSONArray jsonArray) {
            array = jsonArray;
        } else if (raw instanceof Iterable<?> iterable && !(raw instanceof JSONObject)) {
            array = new JSONArray();
            for (Object item : iterable) {
                array.add(item);
            }
        } else if (raw == null) {
            return List.of();
        } else {
            return null;
        }
        Set<Long> ordered = new LinkedHashSet<>();
        for (Object item : array) {
            Long id = positiveLong(item);
            if (id == null) {
                return null;
            }
            ordered.add(id);
        }
        return new ArrayList<>(ordered);
    }

    private static Long positiveLong(Object raw) {
        if (raw == null || isBlank(raw)) {
            return null;
        }
        try {
            long value = Long.parseLong(String.valueOf(raw).trim());
            return value > 0 ? value : null;
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static String text(Object raw) {
        if (raw == null) {
            return null;
        }
        String value = String.valueOf(raw).trim();
        return value.isEmpty() ? null : value;
    }

    private static boolean isBlank(Object raw) {
        return raw == null || String.valueOf(raw).trim().isEmpty() || "null".equalsIgnoreCase(String.valueOf(raw).trim());
    }
}
