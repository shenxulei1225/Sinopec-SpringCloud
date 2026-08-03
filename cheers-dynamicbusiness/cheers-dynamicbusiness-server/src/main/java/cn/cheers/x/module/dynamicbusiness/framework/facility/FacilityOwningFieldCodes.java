package cn.cheers.x.module.dynamicbusiness.framework.facility;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 站场级业务「所属场站」系统字段约定（编码 = 物理列名 = facility_id；
 * 引用设施类型 {@code facility}，展示名统一为所属场站）。
 */
public final class FacilityOwningFieldCodes {

    public static final String FIELD_CODE = "facility_id";
    public static final String DISPLAY_NAME = "所属场站";
    public static final String TARGET_ENTITY_TYPE = "facility";

    private FacilityOwningFieldCodes() {
    }

    /**
     * 从裸 id / REF 对象解析目标实体 id；无法解析则 null。
     */
    public static Long extractId(Object raw) {
        if (raw == null) {
            return null;
        }
        if (raw instanceof Number number) {
            long id = number.longValue();
            return id > 0 ? id : null;
        }
        if (raw instanceof Map<?, ?> map) {
            Object id = map.get("id");
            if (id == null) {
                id = map.get("entityId");
            }
            return extractId(id);
        }
        String text = String.valueOf(raw).trim();
        if (text.isEmpty()) {
            return null;
        }
        try {
            long id = Long.parseLong(text);
            return id > 0 ? id : null;
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    /** 写路径 / API 用的单选 REF 形态。 */
    public static Map<String, Object> toApiRef(long facilityId) {
        Map<String, Object> ref = new LinkedHashMap<>(2);
        ref.put("entityTypeCode", TARGET_ENTITY_TYPE);
        ref.put("id", facilityId);
        return ref;
    }
}
