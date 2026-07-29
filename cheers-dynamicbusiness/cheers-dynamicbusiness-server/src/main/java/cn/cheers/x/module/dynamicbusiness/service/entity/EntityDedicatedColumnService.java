package cn.cheers.x.module.dynamicbusiness.service.entity;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeBaseFieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeBaseFieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.enums.entitytype.StorageTypeEnum;
import cn.cheers.x.module.dynamicbusiness.framework.entity.EntityBaseFieldColumnNames;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * 专用表基础字段固定列读写：列名跟字段编码对齐；REF 列存目标实体 id。
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EntityDedicatedColumnService {

    private final EntityTypeMapper entityTypeMapper;
    private final EntityTypeBaseFieldMapper baseFieldMapper;
    private final JdbcTemplate jdbcTemplate;

    /**
     * 写前：从 customFields（含由 baseFields 并入的键）抽出应落固定列的值，并从 Map 中移除，避免 JSON 双写。
     *
     * @return columnName → 库值（REF 为 Long id）
     */
    public Map<String, Object> extractPhysicalValuesAndStrip(String entityTypeCode, Map<String, Object> customFields) {
        Map<String, Object> physical = new LinkedHashMap<>();
        if (StrUtil.isBlank(entityTypeCode) || CollectionUtils.isEmpty(customFields)) {
            return physical;
        }
        EntityTypeDO entityType = entityTypeMapper.selectByCode(entityTypeCode.trim());
        if (!isDedicated(entityType)) {
            return physical;
        }
        List<EntityTypeBaseFieldDO> baseFields = baseFieldMapper.selectByEntityTypeCode(entityTypeCode.trim());
        if (CollectionUtils.isEmpty(baseFields)) {
            return physical;
        }
        for (EntityTypeBaseFieldDO baseField : baseFields) {
            if (baseField == null || StrUtil.isBlank(baseField.getFieldCode()) || !baseField.isEnabled()) {
                continue;
            }
            String fieldCode = baseField.getFieldCode().trim();
            if (!customFields.containsKey(fieldCode)) {
                continue;
            }
            String column = EntityBaseFieldColumnNames.toColumnName(fieldCode);
            if (column == null || !columnExists(resolveTableName(entityType), column)) {
                continue;
            }
            Object raw = customFields.remove(fieldCode);
            physical.put(column, toDbValue(baseField, raw));
        }
        return physical;
    }

    public void writePhysicalColumns(String entityTypeCode, Long entityId, Map<String, Object> columnValues) {
        if (entityId == null || CollectionUtils.isEmpty(columnValues)) {
            return;
        }
        EntityTypeDO entityType = entityTypeMapper.selectByCode(entityTypeCode);
        if (!isDedicated(entityType)) {
            return;
        }
        String table = resolveTableName(entityType);
        StringBuilder sql = new StringBuilder("UPDATE ").append(table).append(" SET ");
        Object[] args = new Object[columnValues.size() + 1];
        int i = 0;
        for (Map.Entry<String, Object> e : columnValues.entrySet()) {
            if (i > 0) {
                sql.append(", ");
            }
            sql.append(e.getKey()).append(" = ?");
            args[i++] = e.getValue();
        }
        sql.append(" WHERE id = ? AND deleted = false");
        args[i] = entityId;
        jdbcTemplate.update(sql.toString(), args);
    }

    /**
     * 读：把专用表上存在的基础字段固定列合并进 baseFields（键为字段编码）。
     */
    public void mergePhysicalColumnsIntoBaseFields(EntityDO entity, Map<String, Object> baseFields) {
        if (entity == null || entity.getId() == null || StrUtil.isBlank(entity.getEntityTypeCode()) || baseFields == null) {
            return;
        }
        EntityTypeDO entityType = entityTypeMapper.selectByCode(entity.getEntityTypeCode());
        if (!isDedicated(entityType)) {
            return;
        }
        List<EntityTypeBaseFieldDO> fields = baseFieldMapper.selectByEntityTypeCode(entity.getEntityTypeCode());
        if (CollectionUtils.isEmpty(fields)) {
            return;
        }
        String table = resolveTableName(entityType);
        StringBuilder select = new StringBuilder("SELECT ");
        List<EntityTypeBaseFieldDO> selected = new java.util.ArrayList<>();
        for (EntityTypeBaseFieldDO field : fields) {
            if (field == null || StrUtil.isBlank(field.getFieldCode()) || !field.isEnabled()) {
                continue;
            }
            String column = EntityBaseFieldColumnNames.toColumnName(field.getFieldCode());
            if (column == null || !columnExists(table, column)) {
                continue;
            }
            if (!selected.isEmpty()) {
                select.append(", ");
            }
            select.append(column);
            selected.add(field);
        }
        if (selected.isEmpty()) {
            return;
        }
        select.append(" FROM ").append(table).append(" WHERE id = ? AND deleted = false");
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(select.toString(), entity.getId());
        if (rows.isEmpty()) {
            return;
        }
        Map<String, Object> row = rows.get(0);
        for (EntityTypeBaseFieldDO field : selected) {
            String column = EntityBaseFieldColumnNames.toColumnName(field.getFieldCode());
            Object dbVal = row.get(column);
            if (dbVal == null) {
                // JDBC 可能返回小写/原名；再试一遍
                dbVal = row.get(column.toLowerCase(Locale.ROOT));
            }
            if (dbVal == null) {
                continue;
            }
            baseFields.put(field.getFieldCode(), toApiValue(field, dbVal));
        }
    }

    public void applyAfterPersist(EntityDO entity, Map<String, Object> physicalColumns) {
        if (entity == null || entity.getId() == null || CollectionUtils.isEmpty(physicalColumns)) {
            return;
        }
        writePhysicalColumns(entity.getEntityTypeCode(), entity.getId(), physicalColumns);
    }

    /**
     * 批量读取基础字段固定列值，供列表字段排序。
     *
     * @return entityId → 库值（REF 为 Long id；无列/无行则缺席）
     */
    public Map<Long, Object> loadPhysicalFieldValues(String entityTypeCode,
                                                     String fieldCode,
                                                     Collection<Long> entityIds) {
        Map<Long, Object> out = new LinkedHashMap<>();
        if (StrUtil.isBlank(entityTypeCode)
                || StrUtil.isBlank(fieldCode)
                || CollectionUtils.isEmpty(entityIds)) {
            return out;
        }
        EntityTypeDO entityType = entityTypeMapper.selectByCode(entityTypeCode.trim());
        if (!isDedicated(entityType)) {
            return out;
        }
        String table = resolveTableName(entityType);
        String column = EntityBaseFieldColumnNames.toColumnName(fieldCode.trim());
        if (column == null || !columnExists(table, column)) {
            return out;
        }
        List<Long> ids = entityIds.stream().filter(Objects::nonNull).distinct().toList();
        if (ids.isEmpty()) {
            return out;
        }
        String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
        String sql = "SELECT id, " + column + " AS sort_val FROM " + table
                + " WHERE deleted = false AND id IN (" + placeholders + ")";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, ids.toArray());
        for (Map<String, Object> row : rows) {
            if (row == null) {
                continue;
            }
            Long id = toLong(row.get("id"));
            if (id == null) {
                continue;
            }
            Object val = row.get("sort_val");
            if (val == null) {
                val = row.get("SORT_VAL");
            }
            out.put(id, val);
        }
        return out;
    }

    private static boolean isDedicated(EntityTypeDO entityType) {
        if (entityType == null) {
            return false;
        }
        StorageTypeEnum storage = StorageTypeEnum.getByCode(entityType.getStorageType());
        return storage != null && storage.isDedicated();
    }

    private static String resolveTableName(EntityTypeDO entityType) {
        if (entityType == null) {
            throw new ServiceException(400, "业务类型不存在");
        }
        String table = entityType.getDedicatedTableName();
        if (StrUtil.isBlank(table)) {
            table = "ent_" + entityType.getCode();
        }
        if (!table.startsWith("ent_")) {
            table = "ent_" + table;
        }
        return table;
    }

    private boolean columnExists(String tableName, String columnName) {
        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*) FROM information_schema.columns
                WHERE table_name = ?
                  AND column_name = ?
                  AND table_schema IN (current_schema(), 'dynamicbusiness')
                """,
                Integer.class,
                tableName,
                columnName);
        return count != null && count > 0;
    }

    private static Object toDbValue(EntityTypeBaseFieldDO field, Object raw) {
        if (raw == null) {
            return null;
        }
        if (isRefType(field.getDataType())) {
            return extractRefId(raw);
        }
        return raw;
    }

    private static Object toApiValue(EntityTypeBaseFieldDO field, Object dbVal) {
        if (dbVal == null) {
            return null;
        }
        if (!isRefType(field.getDataType())) {
            return dbVal;
        }
        Long id = toLong(dbVal);
        if (id == null) {
            return null;
        }
        String target = EntityBaseFieldColumnNames.inferRefTargetEntityType(field.getFieldCode());
        Map<String, Object> ref = new LinkedHashMap<>();
        if (StrUtil.isNotBlank(target)) {
            ref.put("entityTypeCode", target);
        }
        ref.put("id", id);
        return ref;
    }

    private static boolean isRefType(String dataType) {
        if (dataType == null) {
            return false;
        }
        String t = dataType.trim().toUpperCase(Locale.ROOT).replace('-', '_');
        return "REF".equals(t)
                || "ENTITY_REF".equals(t)
                || "REF_MULTI".equals(t)
                || "ENTITY_REF_MULTI".equals(t)
                || "BATCH_ENTITY_REF".equals(t)
                || "REFERENCE".equals(t);
    }

    @SuppressWarnings("unchecked")
    private static Long extractRefId(Object raw) {
        if (raw instanceof Number number) {
            return number.longValue();
        }
        if (raw instanceof Map<?, ?> map) {
            Object id = map.get("id");
            if (id == null) {
                id = map.get("entityId");
            }
            return toLong(id);
        }
        if (raw instanceof String text) {
            String t = text.trim();
            if (t.isEmpty()) {
                return null;
            }
            try {
                return Long.parseLong(t);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private static Long toLong(Object value) {
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
}
