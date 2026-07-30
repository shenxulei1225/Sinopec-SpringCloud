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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * 专用表基础字段固定列读写：列名跟字段编码对齐；REF 列存目标实体 id。
 *
 * <p>列集合仅由类型已挂基础字段配置解析；读/写路径均假定专用表已有对应列，
 * 禁止 {@code information_schema} / 探列跳过。缺列视为加删基础字段时建列同步故障。</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EntityDedicatedColumnService {

    private final EntityTypeMapper entityTypeMapper;
    private final EntityTypeBaseFieldMapper baseFieldMapper;
    private final JdbcTemplate jdbcTemplate;

    /**
     * 启用基础字段对应的物理列规格（字段编码 → 列名）；不查元数据表。
     */
    public record PhysicalFieldSpec(String fieldCode, String columnName, String dataType) {
    }

    /**
     * 按类型基础字段配置解析启用字段的物理列；不得探 {@code information_schema}。
     */
    public List<PhysicalFieldSpec> listEnabledPhysicalFields(String entityTypeCode) {
        if (StrUtil.isBlank(entityTypeCode)) {
            return List.of();
        }
        EntityTypeDO entityType = entityTypeMapper.selectByCode(entityTypeCode.trim());
        if (!isDedicated(entityType)) {
            return List.of();
        }
        List<EntityTypeBaseFieldDO> configured = baseFieldMapper.selectByEntityTypeCode(entityTypeCode.trim());
        if (CollectionUtils.isEmpty(configured)) {
            return List.of();
        }
        List<PhysicalFieldSpec> out = new ArrayList<>();
        for (EntityTypeBaseFieldDO field : configured) {
            if (field == null || StrUtil.isBlank(field.getFieldCode()) || !field.isEnabled()) {
                continue;
            }
            String fieldCode = field.getFieldCode().trim();
            String column = EntityBaseFieldColumnNames.toColumnName(fieldCode);
            if (column == null) {
                continue;
            }
            out.add(new PhysicalFieldSpec(fieldCode, column, field.getDataType()));
        }
        return out;
    }

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
        List<PhysicalFieldSpec> specs = listEnabledPhysicalFields(entityTypeCode.trim());
        if (specs.isEmpty()) {
            return physical;
        }
        Map<String, EntityTypeBaseFieldDO> metaByCode = indexEnabledBaseFields(entityTypeCode.trim());
        for (PhysicalFieldSpec spec : specs) {
            if (!customFields.containsKey(spec.fieldCode())) {
                continue;
            }
            Object raw = customFields.remove(spec.fieldCode());
            EntityTypeBaseFieldDO meta = metaByCode.get(spec.fieldCode());
            physical.put(spec.columnName(), toDbValue(meta, raw));
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
     * 单实体读：按配置列一次 SELECT 写入 baseFields（详情/单条；无 information_schema）。
     * 列表路径不得调用本方法做「按本页 id 二次补列」；Task 2/3 改走本页一次加载。
     */
    public void mergePhysicalColumnsIntoBaseFields(EntityDO entity, Map<String, Object> baseFields) {
        if (entity == null || entity.getId() == null || StrUtil.isBlank(entity.getEntityTypeCode()) || baseFields == null) {
            return;
        }
        EntityTypeDO entityType = entityTypeMapper.selectByCode(entity.getEntityTypeCode());
        if (!isDedicated(entityType)) {
            return;
        }
        List<PhysicalFieldSpec> specs = listEnabledPhysicalFields(entity.getEntityTypeCode().trim());
        if (specs.isEmpty()) {
            return;
        }
        String table = resolveTableName(entityType);
        StringBuilder select = new StringBuilder("SELECT ");
        for (int i = 0; i < specs.size(); i++) {
            if (i > 0) {
                select.append(", ");
            }
            select.append(specs.get(i).columnName());
        }
        select.append(" FROM ").append(table).append(" WHERE id = ? AND deleted = false");
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(select.toString(), entity.getId());
        if (rows.isEmpty()) {
            return;
        }
        Map<String, Object> row = rows.get(0);
        Map<String, EntityTypeBaseFieldDO> metaByCode = indexEnabledBaseFields(entity.getEntityTypeCode().trim());
        for (PhysicalFieldSpec spec : specs) {
            Object dbVal = readColumn(row, spec.columnName());
            if (dbVal == null) {
                continue;
            }
            baseFields.put(spec.fieldCode(), toApiValue(metaByCode.get(spec.fieldCode()), dbVal));
        }
    }

    public void applyAfterPersist(EntityDO entity, Map<String, Object> physicalColumns) {
        if (entity == null || entity.getId() == null || CollectionUtils.isEmpty(physicalColumns)) {
            return;
        }
        writePhysicalColumns(entity.getEntityTypeCode(), entity.getId(), physicalColumns);
    }

    /**
     * 批量读取某一基础字段固定列值，供列表字段排序（非 VO 二次补列）。
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
        if (column == null) {
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

    /**
     * 历史 API：探列已删除后无需清缓存；保留空实现以免改 DDL 调用方（Task 4 可再清）。
     */
    public void invalidateTableColumns(String tableName) {
        // no-op
    }

    private Map<String, EntityTypeBaseFieldDO> indexEnabledBaseFields(String entityTypeCode) {
        List<EntityTypeBaseFieldDO> configured = baseFieldMapper.selectByEntityTypeCode(entityTypeCode);
        Map<String, EntityTypeBaseFieldDO> out = new LinkedHashMap<>();
        if (CollectionUtils.isEmpty(configured)) {
            return out;
        }
        for (EntityTypeBaseFieldDO field : configured) {
            if (field == null || StrUtil.isBlank(field.getFieldCode()) || !field.isEnabled()) {
                continue;
            }
            out.put(field.getFieldCode().trim(), field);
        }
        return out;
    }

    private static Object readColumn(Map<String, Object> row, String column) {
        Object dbVal = row.get(column);
        if (dbVal == null) {
            dbVal = row.get(column.toLowerCase(Locale.ROOT));
        }
        if (dbVal == null) {
            dbVal = row.get(column.toUpperCase(Locale.ROOT));
        }
        return dbVal;
    }

    private static boolean isDedicated(EntityTypeDO entityType) {
        if (entityType == null) {
            return false;
        }
        StorageTypeEnum storage = StorageTypeEnum.getByCode(entityType.getStorageType());
        return storage != null && storage.isDedicated();
    }

    /** BASE 行为：dedicatedTableName 或 ent_{code}，保证 ent_ 前缀；不引入租户表名助手。 */
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

    private static Object toDbValue(EntityTypeBaseFieldDO field, Object raw) {
        if (raw == null) {
            return null;
        }
        if (field != null && isRefType(field.getDataType())) {
            return extractRefId(raw);
        }
        return raw;
    }

    private static Object toApiValue(EntityTypeBaseFieldDO field, Object dbVal) {
        if (dbVal == null) {
            return null;
        }
        if (field == null || !isRefType(field.getDataType())) {
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
