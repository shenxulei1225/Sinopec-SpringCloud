package cn.cheers.x.module.dynamicbusiness.service.entity;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
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
import org.springframework.util.StringUtils;

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
        List<PhysicalField> fields = resolvePhysicalFields(entityType);
        if (fields.isEmpty()) {
            return physical;
        }
        for (PhysicalField field : fields) {
            if (!customFields.containsKey(field.fieldCode())) {
                continue;
            }
            Object raw = customFields.remove(field.fieldCode());
            physical.put(field.column(), toDbValue(field.meta(), raw));
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
     * 单实体读：把已建固定列合并进 baseFields（详情/单条路径）。
     *
     * <p>按配置列一次 SELECT；Task 4 将改走本页一次加载后移除本合并路径。</p>
     */
    public void mergePhysicalColumnsIntoBaseFields(EntityDO entity, Map<String, Object> baseFields) {
        if (entity == null || entity.getId() == null || StrUtil.isBlank(entity.getEntityTypeCode()) || baseFields == null) {
            return;
        }
        EntityRespVO stub = new EntityRespVO();
        stub.setId(entity.getId());
        stub.setBaseFields(baseFields);
        if (baseFields.get("entityTypeCode") == null) {
            baseFields.put("entityTypeCode", entity.getEntityTypeCode());
        }
        mergePhysicalColumnsIntoBaseFields(List.of(stub));
    }

    /**
     * 列表读：按类型对本页 id 一次 SELECT 固定列，写入各 VO 的 baseFields。
     *
     * <p>列集合仅来自基础字段配置；Task 4 将拆除本过渡合并。</p>
     */
    public void mergePhysicalColumnsIntoBaseFields(List<EntityRespVO> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return;
        }
        Map<String, List<EntityRespVO>> byType = new LinkedHashMap<>();
        for (EntityRespVO vo : entities) {
            if (vo == null || vo.getId() == null) {
                continue;
            }
            String typeCode = vo.getEntityTypeCode();
            if (!StringUtils.hasText(typeCode) && vo.getBaseFields() != null) {
                Object raw = vo.getBaseFields().get("entityTypeCode");
                if (raw != null) {
                    typeCode = String.valueOf(raw).trim();
                }
            }
            if (!StringUtils.hasText(typeCode)) {
                continue;
            }
            byType.computeIfAbsent(typeCode.trim(), k -> new ArrayList<>()).add(vo);
        }
        for (Map.Entry<String, List<EntityRespVO>> entry : byType.entrySet()) {
            mergeForType(entry.getKey(), entry.getValue());
        }
    }

    public void applyAfterPersist(EntityDO entity, Map<String, Object> physicalColumns) {
        if (entity == null || entity.getId() == null || CollectionUtils.isEmpty(physicalColumns)) {
            return;
        }
        writePhysicalColumns(entity.getEntityTypeCode(), entity.getId(), physicalColumns);
    }

    /**
     * 批量读取某一基础字段固定列值，供列表字段排序。
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
     * 历史 API：探列缓存已删除；建列后无需再清缓存。保留空实现以免改 DDL 调用方。
     */
    public void invalidateTableColumns(String tableName) {
        // no-op：列集合改为仅信基础字段配置
    }

    private void mergeForType(String entityTypeCode, List<EntityRespVO> vos) {
        EntityTypeDO entityType = entityTypeMapper.selectByCode(entityTypeCode);
        if (!isDedicated(entityType)) {
            return;
        }
        List<PhysicalField> fields = resolvePhysicalFields(entityType);
        if (fields.isEmpty()) {
            return;
        }
        List<Long> ids = vos.stream().map(EntityRespVO::getId).filter(Objects::nonNull).distinct().toList();
        if (ids.isEmpty()) {
            return;
        }
        String table = resolveTableName(entityType);
        StringBuilder select = new StringBuilder("SELECT id");
        for (PhysicalField field : fields) {
            select.append(", ").append(field.column());
        }
        select.append(" FROM ").append(table)
                .append(" WHERE deleted = false AND id IN (")
                .append(String.join(",", Collections.nCopies(ids.size(), "?")))
                .append(")");
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(select.toString(), ids.toArray());
        Map<Long, Map<String, Object>> rowById = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            Long id = toLong(row.get("id"));
            if (id != null) {
                rowById.put(id, row);
            }
        }
        for (EntityRespVO vo : vos) {
            Map<String, Object> row = rowById.get(vo.getId());
            if (row == null) {
                continue;
            }
            Map<String, Object> base = vo.getBaseFields();
            if (base == null) {
                base = new LinkedHashMap<>();
                vo.setBaseFields(base);
            }
            for (PhysicalField field : fields) {
                Object dbVal = readColumn(row, field.column());
                if (dbVal == null) {
                    continue;
                }
                base.put(field.fieldCode(), toApiValue(field.meta(), dbVal));
            }
            stripPhysicalKeysFromCustom(vo, base);
        }
    }

    private static void stripPhysicalKeysFromCustom(EntityRespVO vo, Map<String, Object> base) {
        if (vo.getCustomFields() == null || base == null) {
            return;
        }
        for (String fieldCode : base.keySet()) {
            if (fieldCode != null && fieldCode.startsWith("FLD-")) {
                vo.getCustomFields().remove(fieldCode);
            }
        }
    }

    /**
     * 启用基础字段 → 物理列；不探表列是否存在。
     */
    private List<PhysicalField> resolvePhysicalFields(EntityTypeDO entityType) {
        String typeCode = entityType.getCode();
        if (StrUtil.isBlank(typeCode)) {
            return List.of();
        }
        List<EntityTypeBaseFieldDO> configured = baseFieldMapper.selectByEntityTypeCode(typeCode.trim());
        if (CollectionUtils.isEmpty(configured)) {
            return List.of();
        }
        List<PhysicalField> out = new ArrayList<>();
        for (EntityTypeBaseFieldDO field : configured) {
            if (field == null || StrUtil.isBlank(field.getFieldCode()) || !field.isEnabled()) {
                continue;
            }
            String fieldCode = field.getFieldCode().trim();
            String column = EntityBaseFieldColumnNames.toColumnName(fieldCode);
            if (column == null) {
                continue;
            }
            out.add(new PhysicalField(fieldCode, column, field));
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

    private static String resolveTableName(EntityTypeDO entityType) {
        if (entityType == null) {
            throw new ServiceException(400, "业务类型不存在");
        }
        if (StrUtil.isNotBlank(entityType.getDedicatedTableName())) {
            return cn.cheers.x.module.dynamicbusiness.framework.tenant.TenantPhysicalTableNames
                    .ensureTenantSuffix(entityType.getDedicatedTableName().trim());
        }
        String code = StrUtil.isNotBlank(entityType.getBaseEntityTypeCode())
                ? entityType.getBaseEntityTypeCode()
                : entityType.getCode();
        return cn.cheers.x.module.dynamicbusiness.framework.tenant.TenantPhysicalTableNames
                .entityPhysicalTable(code);
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

    private record PhysicalField(String fieldCode, String column, EntityTypeBaseFieldDO meta) {
    }
}
