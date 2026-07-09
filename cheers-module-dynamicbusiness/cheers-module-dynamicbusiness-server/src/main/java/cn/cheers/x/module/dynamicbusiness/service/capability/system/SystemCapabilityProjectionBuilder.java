package cn.cheers.x.module.dynamicbusiness.service.capability.system;

import cn.cheers.x.module.dynamicbusiness.service.capability.projection.CapabilityBlockProjectionBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cn.iocoder.yudao.framework.common.exception.ServiceException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 系统业务能力 JSON 构建（capability_full + component_interface 能力块模型）。
 */
public final class SystemCapabilityProjectionBuilder {

    private static final Set<String> SUPPORTED_COMPONENT_CODES = Set.of("list", "tree", "table", "card");

    private SystemCapabilityProjectionBuilder() {
    }

    public static String buildCapabilityFullJson(
            SystemCapabilityDefinition definition, long version, ObjectMapper objectMapper) {
        Map<String, Object> root = new LinkedHashMap<>();
        root.put("entityTypeCode", definition.getEntityTypeCode());
        root.put("businessCategory", "system");
        root.put("capabilityVersion", version);
        root.put("components", List.copyOf(SUPPORTED_COMPONENT_CODES));
        root.put("readUrl", definition.getReadUrl());
        root.put("paginated", definition.isPaginated());
        return toJson(root, objectMapper);
    }

    public static String buildProjectionJson(
            SystemCapabilityDefinition definition,
            String componentCode,
            long version,
            ObjectMapper objectMapper) {
        List<Map<String, Object>> displayFields = toDisplayFields(definition, componentCode);
        List<Map<String, Object>> filterFields = toFilterFields(definition);
        List<String> searchableFieldKeys = definition.getFilterFields().stream()
                .filter(SystemCapabilityDefinition.SystemFieldDefinition::isSearchable)
                .map(SystemCapabilityDefinition.SystemFieldDefinition::getFieldKey)
                .toList();
        Map<String, Object> projection = CapabilityBlockProjectionBuilder.buildSystem(
                definition,
                componentCode,
                version,
                displayFields,
                filterFields,
                searchableFieldKeys);
        return toJson(projection, objectMapper);
    }

    private static List<Map<String, Object>> toDisplayFields(
            SystemCapabilityDefinition definition,
            String componentCode) {
        List<Map<String, Object>> displayFields = new ArrayList<>();
        int order = 0;
        for (SystemCapabilityDefinition.SystemFieldDefinition field : definition.getDisplayFields()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", field.getFieldKey());
            item.put("fieldKey", field.getFieldKey());
            item.put("label", field.getLabel());
            item.put("renderAs", "text");
            item.put("sortOrder", order++);
            item.put("defaultVisible", true);
            item.put("applicableViews", List.of(componentCode));
            displayFields.add(item);
        }
        return displayFields;
    }

    private static List<Map<String, Object>> toFilterFields(SystemCapabilityDefinition definition) {
        List<Map<String, Object>> filters = new ArrayList<>();
        int order = 0;
        for (SystemCapabilityDefinition.SystemFieldDefinition field : definition.getFilterFields()) {
            Map<String, Object> filter = new LinkedHashMap<>();
            filter.put("id", field.getFieldKey());
            filter.put("fieldKey", field.getFieldKey());
            filter.put("label", field.getLabel());
            filter.put("control", "input");
            filter.put("sortOrder", order++);
            filter.put("bindTo", "field-filter");
            filter.put("searchable", field.isSearchable());
            filter.put("sortable", false);
            filter.put("defaultVisible", true);
            filters.add(filter);
        }
        return filters;
    }

    private static String toJson(Object value, ObjectMapper objectMapper) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            throw new ServiceException(500, "系统能力 JSON 序列化失败");
        }
    }
}
