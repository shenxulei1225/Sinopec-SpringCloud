package cn.cheers.x.module.dynamicbusiness.framework.field;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 实体类型级字段显示名称（别名）读写，存储在 {@code dynamic_entity_type.physical_column_mapping.fieldLabels}。
 */
public final class EntityTypeFieldLabelHelper {

    private static final String FIELD_LABELS_KEY = "fieldLabels";

    private EntityTypeFieldLabelHelper() {
    }

    public static Map<String, String> readLabels(EntityTypeDO entityType) {
        Map<String, String> labels = new LinkedHashMap<>();
        if (entityType == null || !StringUtils.hasText(entityType.getPhysicalColumnMapping())) {
            return labels;
        }
        try {
            JSONObject root = JSON.parseObject(entityType.getPhysicalColumnMapping());
            if (root == null) {
                return labels;
            }
            JSONObject fieldLabels = root.getJSONObject(FIELD_LABELS_KEY);
            if (fieldLabels == null) {
                return labels;
            }
            for (String key : fieldLabels.keySet()) {
                String value = fieldLabels.getString(key);
                if (StringUtils.hasText(key) && StringUtils.hasText(value)) {
                    labels.put(key.trim(), value.trim());
                }
            }
        } catch (Exception ignored) {
            return labels;
        }
        return labels;
    }

    public static String resolveLabel(EntityTypeDO entityType, String fieldCode, String defaultLabel) {
        if (!StringUtils.hasText(fieldCode)) {
            return defaultLabel;
        }
        String alias = readLabels(entityType).get(fieldCode.trim());
        return StringUtils.hasText(alias) ? alias : defaultLabel;
    }

    public static String writeLabel(EntityTypeDO entityType, String fieldCode, String label) {
        if (entityType == null || !StringUtils.hasText(fieldCode)) {
            return entityType != null ? entityType.getPhysicalColumnMapping() : null;
        }
        JSONObject root;
        if (StringUtils.hasText(entityType.getPhysicalColumnMapping())) {
            root = JSON.parseObject(entityType.getPhysicalColumnMapping());
            if (root == null) {
                root = new JSONObject();
            }
        } else {
            root = new JSONObject();
        }
        JSONObject fieldLabels = root.getJSONObject(FIELD_LABELS_KEY);
        if (fieldLabels == null) {
            fieldLabels = new JSONObject();
        }
        String trimmedCode = fieldCode.trim();
        if (StringUtils.hasText(label)) {
            fieldLabels.put(trimmedCode, label.trim());
        } else {
            fieldLabels.remove(trimmedCode);
        }
        root.put(FIELD_LABELS_KEY, fieldLabels);
        String json = root.toJSONString();
        entityType.setPhysicalColumnMapping(json);
        return json;
    }
}
