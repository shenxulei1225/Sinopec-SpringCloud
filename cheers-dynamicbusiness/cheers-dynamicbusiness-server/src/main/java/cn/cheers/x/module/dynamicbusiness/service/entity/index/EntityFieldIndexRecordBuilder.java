package cn.cheers.x.module.dynamicbusiness.service.entity.index;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityFieldIndexDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

/**
 * 把实体扩展 JSON 里的一个值写成索引表一行。
 *
 * <p>不负责：判断该不该进索引；不扫扩展 JSON 做筛选。</p>
 */
@Slf4j
public final class EntityFieldIndexRecordBuilder {

    public static final int INDEX_INSERT_BATCH_SIZE = 500;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter ISO_DATETIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private EntityFieldIndexRecordBuilder() {
    }

    /**
     * 从扩展 JSON 取出该字段值；没有值则返回 null，调用方跳过空行。
     */
    public static EntityFieldIndexDO tryBuild(EntityDO entity, FieldDO field, Map<String, Object> customFields) {
        if (entity == null || entity.getId() == null || field == null || field.getCode() == null) {
            return null;
        }
        Map<String, Object> values = customFields != null ? customFields : Map.of();
        Object value = values.get(field.getCode());
        if (value == null && field.getSemanticType() != null && !field.getSemanticType().isBlank()) {
            value = values.get(field.getSemanticType().trim());
        }
        if (value == null) {
            return null;
        }
        EntityFieldIndexDO record = EntityFieldIndexDO.builder()
                .entityId(entity.getId())
                .modelId(entity.getModelId())
                .fieldCode(field.getCode())
                .build();
        fillValue(record, field.getType(), value);
        return record;
    }

    static void fillValue(EntityFieldIndexDO record, String fieldType, Object value) {
        if (record == null || value == null) {
            return;
        }
        String upperType = fieldType == null ? "" : fieldType.trim().toUpperCase();
        if ("MULTI_SELECT".equals(upperType)) {
            String jsonArray = toMultiSelectIndexString(value);
            if (jsonArray != null) {
                if (jsonArray.length() > 500) {
                    jsonArray = jsonArray.substring(0, 500);
                }
                record.setValueString(jsonArray);
            }
            return;
        }

        String valueStr = value.toString();
        switch (upperType) {
            case "NUMBER", "INTEGER", "DECIMAL" -> {
                try {
                    record.setValueNumber(new BigDecimal(valueStr));
                } catch (NumberFormatException e) {
                    log.warn("数值转换失败，存储为字符串: fieldCode={}, value={}",
                            record.getFieldCode(), valueStr);
                    record.setValueString(valueStr);
                }
            }
            case "DATE" -> {
                try {
                    record.setValueDate(parseDate(valueStr));
                } catch (DateTimeParseException e) {
                    log.warn("日期转换失败，存储为字符串: fieldCode={}, value={}",
                            record.getFieldCode(), valueStr);
                    record.setValueString(valueStr);
                }
            }
            case "DATETIME" -> {
                try {
                    record.setValueDatetime(parseDateTime(valueStr));
                } catch (DateTimeParseException e) {
                    log.warn("日期时间转换失败，存储为字符串: fieldCode={}, value={}",
                            record.getFieldCode(), valueStr);
                    record.setValueString(valueStr);
                }
            }
            case "BOOLEAN" -> {
                try {
                    record.setValueBoolean(Boolean.parseBoolean(valueStr));
                } catch (Exception e) {
                    record.setValueString(valueStr);
                }
            }
            default -> {
                if (valueStr.length() > 500) {
                    valueStr = valueStr.substring(0, 500);
                }
                record.setValueString(valueStr);
            }
        }
    }

    private static String toMultiSelectIndexString(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof List<?> || value.getClass().isArray()) {
            return JSON.toJSONString(value);
        }
        if (value instanceof String s) {
            String trimmed = s.trim();
            if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
                return trimmed;
            }
            if (trimmed.isEmpty()) {
                return null;
            }
            return JSON.toJSONString(List.of(trimmed));
        }
        return JSON.toJSONString(List.of(String.valueOf(value)));
    }

    private static LocalDate parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            return LocalDate.parse(dateStr);
        }
    }

    private static LocalDateTime parseDateTime(String dateTimeStr) {
        try {
            return LocalDateTime.parse(dateTimeStr, DATETIME_FORMATTER);
        } catch (DateTimeParseException e) {
            try {
                return LocalDateTime.parse(dateTimeStr, ISO_DATETIME_FORMATTER);
            } catch (DateTimeParseException e2) {
                return parseDate(dateTimeStr).atStartOfDay();
            }
        }
    }
}
