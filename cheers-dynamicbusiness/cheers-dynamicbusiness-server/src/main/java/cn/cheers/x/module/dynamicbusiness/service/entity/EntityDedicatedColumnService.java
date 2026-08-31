package cn.cheers.x.module.dynamicbusiness.service.entity;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.util.json.JsonUtils;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeBaseFieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeBaseFieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.field.FieldMapper;
import cn.cheers.x.module.dynamicbusiness.enums.entitytype.StorageTypeEnum;
import cn.cheers.x.module.dynamicbusiness.framework.entity.EntityBaseFieldColumnNames;
import cn.cheers.x.module.dynamicbusiness.framework.tenant.TenantPhysicalTableNames;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PGobject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 专用表基础字段固定列读写：列名跟字段编码对齐；REF 列存目标实体 id。
 *
 * <p><b>管什么</b>：固定列读写；读出时把 JDBC jsonb 驱动值收成业务可用的 JSON（数组/对象），
 * 再进入 {@code dedicatedBaseFieldValues} / {@code baseFields}。</p>
 * <p><b>不管什么</b>：SOP/动作等业务如何解析树与参数——它们只消费已收干净的内容。</p>
 * <p><b>禁止</b>：{@code information_schema} / 探列跳过；读路径编造业务字段；把驱动包装对象泄漏给业务层；
 * JDBC 写基表名（无 {@code _t{tenantId}}）——必须与 MyBatis 动态表名同一租户物理表，否则读不到、创建撞唯一。</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EntityDedicatedColumnService {

    private static final String DYNAMIC_ENTITY_PROVIDER_PREFIX = "dynamic-entity:";

    private final EntityTypeMapper entityTypeMapper;
    private final EntityTypeBaseFieldMapper baseFieldMapper;
    private final FieldMapper fieldMapper;
    private final JdbcTemplate jdbcTemplate;

    /**
     * 启用基础字段对应的物理列规格（字段编码 → 列名）；不查元数据表。
     */
    public record PhysicalFieldSpec(String fieldCode, String columnName, String dataType) {
    }

    /**
     * 可下推库内 ORDER BY 的基础字段物理列名；不可排序 / MultiRef / 未启用则 null。
     */
    public String resolveSortablePhysicalColumn(String entityTypeCode, String fieldCode) {
        String column = resolvePhysicalColumn(entityTypeCode, fieldCode);
        if (column == null) {
            return null;
        }
        EntityTypeBaseFieldDO field = baseFieldMapper.selectByEntityTypeCodeAndFieldCode(
                entityTypeCode.trim(), fieldCode.trim());
        if (field == null || Boolean.FALSE.equals(field.getIsSortable())) {
            return null;
        }
        return column;
    }

    /**
     * 启用基础字段对应的物理列名（筛选用，不要求 isSortable）；MultiRef / 未启用则 null。
     */
    public String resolvePhysicalColumn(String entityTypeCode, String fieldCode) {
        if (StrUtil.isBlank(entityTypeCode) || StrUtil.isBlank(fieldCode)) {
            return null;
        }
        EntityTypeBaseFieldDO field = baseFieldMapper.selectByEntityTypeCodeAndFieldCode(
                entityTypeCode.trim(), fieldCode.trim());
        if (field == null || !field.isEnabled()) {
            return null;
        }
        if (isMultiRefType(field.getDataType())) {
            return null;
        }
        String column = EntityBaseFieldColumnNames.toColumnName(field.getFieldCode());
        if (column == null || !SAFE_PHYSICAL_COLUMN.matcher(column).matches()) {
            return null;
        }
        return column;
    }

    /**
     * 类型是否配置了启用的多选关联基础字段（列表仍可能依赖 custom_fields 提升）。
     */
    public boolean hasEnabledMultiRefBaseField(String entityTypeCode) {
        if (StrUtil.isBlank(entityTypeCode)) {
            return false;
        }
        List<EntityTypeBaseFieldDO> configured = baseFieldMapper.selectByEntityTypeCode(entityTypeCode.trim());
        if (CollectionUtils.isEmpty(configured)) {
            return false;
        }
        for (EntityTypeBaseFieldDO field : configured) {
            if (field != null && field.isEnabled() && isMultiRefType(field.getDataType())) {
                return true;
            }
        }
        return false;
    }

    private static final java.util.regex.Pattern SAFE_PHYSICAL_COLUMN =
            java.util.regex.Pattern.compile("^[a-z][a-z0-9_]*$");

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
            // 多选关联不落单列；编码可为 equipment_ids，存关联表
            if (isMultiRefType(field.getDataType())) {
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
     * 将本页已加载的 {@link EntityDO#getDedicatedBaseFieldValues()} 写入 baseFields（含 REF API 形态）。
     * 不做二次 SELECT；列表热路径专用。
     */
    public void applyDedicatedBaseFieldValues(EntityDO entity, Map<String, Object> baseFields) {
        applyDedicatedBaseFieldValues(entity, baseFields, null, null);
    }

    /**
     * @param metaByCodeOrNull 同一类型本页可复用的字段元数据；null 时按类型现查
     */
    public void applyDedicatedBaseFieldValues(EntityDO entity,
                                              Map<String, Object> baseFields,
                                              Map<String, EntityTypeBaseFieldDO> metaByCodeOrNull) {
        applyDedicatedBaseFieldValues(entity, baseFields, metaByCodeOrNull, null);
    }

    /**
     * @param metaByCodeOrNull     同一类型本页可复用的字段元数据；null 时按类型现查
     * @param refTargetsOrNull     同一类型本页可复用的 REF 目标类型；null 时按元数据现查
     */
    public void applyDedicatedBaseFieldValues(EntityDO entity,
                                              Map<String, Object> baseFields,
                                              Map<String, EntityTypeBaseFieldDO> metaByCodeOrNull,
                                              Map<Long, String> refTargetsOrNull) {
        if (entity == null || baseFields == null) {
            return;
        }
        Map<String, Object> dedicated = entity.getDedicatedBaseFieldValues();
        if (dedicated == null || dedicated.isEmpty()) {
            return;
        }
        String typeCode = entity.getEntityTypeCode() == null ? "" : entity.getEntityTypeCode().trim();
        Map<String, EntityTypeBaseFieldDO> metaByCode = metaByCodeOrNull != null
                ? metaByCodeOrNull
                : indexEnabledBaseFields(typeCode);
        Map<Long, String> targetByLibraryFieldId = refTargetsOrNull != null
                ? refTargetsOrNull
                : loadRefTargetsByLibraryFieldId(metaByCode.values());
        for (Map.Entry<String, Object> e : dedicated.entrySet()) {
            if (e.getKey() == null || e.getValue() == null) {
                continue;
            }
            baseFields.put(e.getKey(), toApiValue(
                    metaByCode.get(e.getKey()), e.getValue(), targetByLibraryFieldId));
        }
    }

    /** 列表组装时按类型复用：启用基础字段元数据（fieldCode → DO）。 */
    public Map<String, EntityTypeBaseFieldDO> loadEnabledBaseFieldMeta(String entityTypeCode) {
        if (StrUtil.isBlank(entityTypeCode)) {
            return Map.of();
        }
        return indexEnabledBaseFields(entityTypeCode.trim());
    }

    /** 列表组装时按类型复用：REF 字段库 → 目标实体类型编码。 */
    public Map<Long, String> loadRefTargetsForBaseFieldMeta(Map<String, EntityTypeBaseFieldDO> metaByCode) {
        if (metaByCode == null || metaByCode.isEmpty()) {
            return Map.of();
        }
        return loadRefTargetsByLibraryFieldId(metaByCode.values());
    }

    /**
     * 写路径 / 单实体旧值回读：按配置列一次 SELECT 写入字段袋（无 information_schema、不探列）。
     * <p>列表与 VO 组装禁止调用；列表须本页一次加载 + {@link #applyDedicatedBaseFieldValues}。</p>
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
        Map<Long, String> targetByLibraryFieldId = loadRefTargetsByLibraryFieldId(metaByCode.values());
        for (PhysicalFieldSpec spec : specs) {
            Object dbVal = readColumn(row, spec.columnName());
            if (dbVal == null) {
                continue;
            }
            baseFields.put(spec.fieldCode(), toApiValue(
                    metaByCode.get(spec.fieldCode()), dbVal, targetByLibraryFieldId));
        }
    }

    public void applyAfterPersist(EntityDO entity, Map<String, Object> physicalColumns) {
        if (entity == null || entity.getId() == null || CollectionUtils.isEmpty(physicalColumns)) {
            return;
        }
        writePhysicalColumns(entity.getEntityTypeCode(), entity.getId(), physicalColumns);
    }

    /**
     * 创建时一条 INSERT 写入核心列 + 固定列（满足专用表 NOT NULL）。
     *
     * @param physicalColumns 列名 → 库值（与 {@link #extractPhysicalValuesAndStrip} 一致）
     * @return 新实体 id
     */
    public Long insertEntityRow(EntityDO entity, Map<String, Object> physicalColumns) {
        if (entity == null || StrUtil.isBlank(entity.getEntityTypeCode())) {
            throw new ServiceException(400, "实体数据不能为空");
        }
        EntityTypeDO entityType = entityTypeMapper.selectByCode(entity.getEntityTypeCode().trim());
        if (!isDedicated(entityType)) {
            throw new ServiceException(400, "非专用表实体不能走固定列 INSERT");
        }
        String table = resolveTableName(entityType);
        if (!table.matches("^[a-zA-Z_][a-zA-Z0-9_]*(\\.[a-zA-Z_][a-zA-Z0-9_]*)?$")) {
            throw new ServiceException(500, "专用表名非法");
        }

        LinkedHashMap<String, Object> columns = new LinkedHashMap<>();
        columns.put("tenant_id", entity.getTenantId() != null ? entity.getTenantId() : 0L);
        columns.put("entity_type_code", entity.getEntityTypeCode());
        columns.put("model_id", entity.getModelId());
        columns.put("name", entity.getName());
        columns.put("code", entity.getCode());
        columns.put("status", entity.getStatus() != null ? entity.getStatus() : 1);
        columns.put("parent_id", entity.getParentId() != null ? entity.getParentId() : 0L);
        columns.put("sort", entity.getSort() != null ? entity.getSort() : 0);
        columns.put("domain", entity.getDomain());
        columns.put("custom_fields", entity.getCustomFields() == null
                ? "{}"
                : com.alibaba.fastjson2.JSON.toJSONString(entity.getCustomFields()));
        columns.put("deleted", false);
        Long loginUserId = cn.cheers.x.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId();
        String operator = loginUserId != null ? String.valueOf(loginUserId) : null;
        columns.put("creator", operator);
        columns.put("updater", operator);

        if (physicalColumns != null) {
            for (Map.Entry<String, Object> e : physicalColumns.entrySet()) {
                String col = e.getKey() == null ? "" : e.getKey().trim().toLowerCase(Locale.ROOT);
                if (!SAFE_PHYSICAL_COLUMN.matcher(col).matches()) {
                    throw new ServiceException(500, "固定列名非法: " + e.getKey());
                }
                if (columns.containsKey(col)) {
                    continue;
                }
                columns.put(col, e.getValue());
            }
        }

        StringBuilder colSql = new StringBuilder();
        StringBuilder valSql = new StringBuilder();
        List<Object> args = new ArrayList<>(columns.size());
        boolean first = true;
        for (Map.Entry<String, Object> e : columns.entrySet()) {
            if (!first) {
                colSql.append(", ");
                valSql.append(", ");
            }
            first = false;
            colSql.append(e.getKey());
            if ("custom_fields".equals(e.getKey())) {
                valSql.append("?::jsonb");
            } else {
                valSql.append("?");
            }
            args.add(e.getValue());
        }
        colSql.append(", create_time, update_time");
        valSql.append(", CURRENT_TIMESTAMP, CURRENT_TIMESTAMP");

        String sql = "INSERT INTO " + table + " (" + colSql + ") VALUES (" + valSql + ") RETURNING id";
        Long id = jdbcTemplate.queryForObject(sql, Long.class, args.toArray());
        if (id == null) {
            throw new ServiceException(500, "写入实体失败：未返回主键");
        }
        entity.setId(id);
        return id;
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

    /**
     * 解析当前租户下的专用表物理名（与 {@link cn.cheers.x.module.dynamicbusiness.framework.entity.EntityTableNameHandler} 一致）。
     * 权威：{@code dedicated_table_name} 或 {@code ent_{code}}，再加 {@code _t{tenantId}}。
     * 禁止返回无租户后缀的基表名——否则 JDBC 写入与 MyBatis 查询不在同一张表。
     */
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
        return TenantPhysicalTableNames.ensureTenantSuffix(table);
    }

    /**
     * 业务值 → JDBC 参数。jsonb 固定列须 {@link PGobject}，禁止裸 String 导致 500。
     */
    private static Object toDbValue(EntityTypeBaseFieldDO field, Object raw) {
        if (raw == null) {
            return null;
        }
        if (field != null && isRefType(field.getDataType())) {
            return extractRefId(raw);
        }
        if (isJsonbStorageField(field, raw)) {
            return toJsonbPgObject(raw);
        }
        return raw;
    }

    /** 字段元数据或命名约定表明值应落 jsonb 列（含 metadata 标 TEXT 但列实为 jsonb 的 *_json 字段）。 */
    private static boolean isJsonbStorageField(EntityTypeBaseFieldDO field, Object raw) {
        if (field != null && StrUtil.isNotBlank(field.getDataType())) {
            String type = field.getDataType().trim().toUpperCase(Locale.ROOT).replace('-', '_');
            if ("JSON".equals(type) || "JSONB".equals(type)) {
                return true;
            }
        }
        if (field != null && StrUtil.isNotBlank(field.getFieldCode())) {
            String code = field.getFieldCode().trim().toLowerCase(Locale.ROOT);
            if (code.endsWith("_json") || code.endsWith("_jsonb")) {
                return true;
            }
        }
        return raw instanceof Map || raw instanceof Collection;
    }

    private static PGobject toJsonbPgObject(Object raw) {
        try {
            PGobject pg = new PGobject();
            pg.setType("jsonb");
            if (raw instanceof String text) {
                pg.setValue(text);
            } else {
                pg.setValue(JsonUtils.toJsonString(raw));
            }
            return pg;
        } catch (SQLException ex) {
            throw new ServiceException(400, "JSON 字段格式无效");
        }
    }

    /**
     * 批量解析 REF 目标类型：优先字段库 provider_code（dynamic-entity:{type}），
     * 避免 method_template_id 被编码推断成 method_template。
     */
    private Map<Long, String> loadRefTargetsByLibraryFieldId(Collection<EntityTypeBaseFieldDO> fields) {
        if (CollectionUtils.isEmpty(fields)) {
            return Map.of();
        }
        Set<Long> libraryIds = new HashSet<>();
        for (EntityTypeBaseFieldDO field : fields) {
            if (field == null || field.getLibraryFieldId() == null || !isRefType(field.getDataType())) {
                continue;
            }
            libraryIds.add(field.getLibraryFieldId());
        }
        if (libraryIds.isEmpty()) {
            return Map.of();
        }
        List<FieldDO> rows = fieldMapper.selectBatchIds(libraryIds);
        if (CollectionUtils.isEmpty(rows)) {
            return Map.of();
        }
        Map<Long, String> out = new HashMap<>();
        for (FieldDO row : rows) {
            if (row == null || row.getId() == null) {
                continue;
            }
            String target = resolveTargetEntityTypeFromProvider(row.getProviderCode());
            if (StrUtil.isNotBlank(target)) {
                out.put(row.getId(), target);
            }
        }
        return out;
    }

    private static String resolveTargetEntityTypeFromProvider(String providerCode) {
        if (StrUtil.isBlank(providerCode) || !providerCode.startsWith(DYNAMIC_ENTITY_PROVIDER_PREFIX)) {
            return null;
        }
        String code = providerCode.substring(DYNAMIC_ENTITY_PROVIDER_PREFIX.length()).trim();
        return StrUtil.isNotBlank(code) ? code : null;
    }

    /**
     * 专用列 JDBC 读出值 → 写入字段袋前的业务值。
     *
     * <p>json/jsonb 列经驱动常为 {@link PGobject}；此处收成 JSON 数组或对象（与 {@code custom_fields} 一样），
     * 避免业务层再碰驱动类型。非 jsonb 原样返回。禁止编造业务内容。</p>
     *
     * @param dbVal {@link #readColumn} 或 ResultSet 取出的原值
     * @return 业务可用值；jsonb 空/null 文本 → null
     */
    public static Object normalizePhysicalDbValue(Object dbVal) {
        if (dbVal == null) {
            return null;
        }
        if (!(dbVal instanceof PGobject pg)) {
            return dbVal;
        }
        String pgType = pg.getType() == null ? "" : pg.getType().trim();
        if (!"jsonb".equalsIgnoreCase(pgType) && !"json".equalsIgnoreCase(pgType)) {
            return pg.getValue();
        }
        String text = pg.getValue();
        if (StrUtil.isBlank(text) || "null".equalsIgnoreCase(text.trim())) {
            return null;
        }
        try {
            return JsonUtils.parseObject(text, Object.class);
        } catch (Exception ex) {
            // 非法 JSON：暴露缺口，仍交业务层看到原文，禁止静默换成空结构
            return text;
        }
    }

    private static Object toApiValue(EntityTypeBaseFieldDO field,
                                    Object dbVal,
                                    Map<Long, String> targetByLibraryFieldId) {
        if (dbVal == null) {
            return null;
        }
        Object value = normalizePhysicalDbValue(dbVal);
        if (field == null || !isRefType(field.getDataType())) {
            return value;
        }
        Long id = toLong(value);
        if (id == null) {
            return null;
        }
        String target = null;
        if (field.getLibraryFieldId() != null && targetByLibraryFieldId != null) {
            target = targetByLibraryFieldId.get(field.getLibraryFieldId());
        }
        if (StrUtil.isBlank(target)) {
            // 常规 region_id → region；非常规编码（如 method_template_id）须靠字段库 provider
            target = EntityBaseFieldColumnNames.inferRefTargetEntityType(field.getFieldCode());
        }
        Map<String, Object> ref = new LinkedHashMap<>();
        if (StrUtil.isNotBlank(target)) {
            ref.put("entityTypeCode", target);
        }
        ref.put("id", id);
        return ref;
    }

    private static boolean isMultiRefType(String dataType) {
        if (dataType == null) {
            return false;
        }
        String t = dataType.trim().toUpperCase(Locale.ROOT).replace('-', '_');
        return "REF_MULTI".equals(t)
                || "ENTITY_REF_MULTI".equals(t)
                || "BATCH_ENTITY_REF".equals(t);
    }

    private static boolean isRefType(String dataType) {
        if (dataType == null) {
            return false;
        }
        String t = dataType.trim().toUpperCase(Locale.ROOT).replace('-', '_');
        return "REF".equals(t)
                || "ENTITY_REF".equals(t)
                || "REFERENCE".equals(t)
                || isMultiRefType(dataType);
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
