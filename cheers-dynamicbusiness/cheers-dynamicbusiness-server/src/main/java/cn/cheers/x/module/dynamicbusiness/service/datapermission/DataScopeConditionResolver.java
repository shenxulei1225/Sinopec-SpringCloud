package cn.cheers.x.module.dynamicbusiness.service.datapermission;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datapermission.CatalogOwnershipDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.framework.entity.EntityTableNameHandler;
import cn.cheers.x.module.dynamicbusiness.framework.facility.FacilityOwningFieldCodes;
import cn.cheers.x.module.dynamicbusiness.framework.tenant.TenantPhysicalTableNames;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityDedicatedColumnService;
import jakarta.annotation.Resource;

import java.sql.Array;
import java.sql.SQLException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 把一个范围起点（任职所在的管理单元，或右上角选中的站）换算成某本目录上的条件，并按条件查记录编号。
 * 例：起点是「组织机构 · 厦门作业区 · 含下级」，算设备目录时：
 * 厦门作业区 → 所属作业区是它的管线段 → 挂在这些段上的场站 → 条件「设备的所属场站在这些站里」。
 * 只读数据，不写任何表，不补声明或归属值。
 */
@Component
class DataScopeConditionResolver {

    private static final String RELATION_BASE_TABLE = "dynamic_entity_relation";

    @Resource
    private CatalogOwnershipQueryService catalogOwnershipQueryService;
    @Resource
    private EntityTypeMapper entityTypeMapper;
    @Resource
    private EntityTableNameHandler entityTableNameHandler;
    @Resource
    private EntityDedicatedColumnService entityDedicatedColumnService;
    @Resource
    private JdbcTemplate jdbcTemplate;

    /**
     * 这个起点在这本目录上的条件，条件之间取并集。
     * 这本目录就是起点所在目录时，只有一条落在记录编号上的条件。
     * 否则每条能逐跳到达起点所在目录的归属声明各给一条条件：例如起点是厦门作业区时，
     * 应急事件「所属管线段在厦门作业区的段上」和「所属场站在这些段的站里」两条都算，命中任一条可见。
     * 起点是一座站时，所属管线段到不了场站，只剩所属场站一条。
     *
     * @return 条件；这本目录逐跳到不了起点所在目录时返回空列表（这个起点对它不起作用）
     */
    List<DataScopeCondition> conditionsFor(String registryCode, ScopeNode row) {
        String scopeCatalog = row.registryCode().trim();
        if (registryCode.equals(scopeCatalog)) {
            return List.of(new DataScopeCondition(null, false, scopeNodeIds(row)));
        }
        return routesToward(registryCode, row, 0);
    }

    /** 这本目录上每条能到达起点所在目录的归属声明，各换成一条条件。到不了的声明不参与。 */
    private List<DataScopeCondition> routesToward(String registryCode, ScopeNode row, int depth) {
        String scopeCatalog = row.registryCode().trim();
        List<DataScopeCondition> routes = new ArrayList<>();
        for (CatalogOwnershipDO declaration : catalogOwnershipQueryService.listByRegistryCode(registryCode)) {
            String target = declaration.getTargetRegistryCode();
            if (catalogOwnershipQueryService.hopsToward(target, scopeCatalog, new HashSet<>()) < 0) {
                continue;
            }
            routes.add(new DataScopeCondition(declaration.getFieldCode(),
                    Boolean.TRUE.equals(declaration.getMultiValued()), reachIds(target, row, depth + 1)));
        }
        return routes;
    }

    /** 起点所在目录里覆盖的记录编号：起点本身，含下级时加上上级路径经过它的记录。 */
    private Set<Long> scopeNodeIds(ScopeNode row) {
        Set<Long> ids = new LinkedHashSet<>();
        ids.add(row.entityId());
        if (row.includeDescendants()) {
            String table = tableOf(row.registryCode());
            ids.addAll(jdbcTemplate.queryForList(
                    "SELECT id FROM " + table + " WHERE deleted = false AND tree_path LIKE ?",
                    Long.class, "%/" + row.entityId() + "/%"));
        }
        return ids;
    }

    /** 某本目录里落在这个起点范围内的记录编号（逐跳往下展开，每一跳各条路线取并集）。 */
    private Set<Long> reachIds(String registryCode, ScopeNode row, int depth) {
        if (depth > CatalogOwnershipQueryService.MAX_HOPS) {
            throw new ServiceException(400, "归属声明跳数过多，可能配成了环：" + registryCode);
        }
        String code = registryCode.trim();
        if (code.equals(row.registryCode().trim())) {
            return scopeNodeIds(row);
        }
        List<DataScopeCondition> routes = routesToward(code, row, depth);
        if (routes.isEmpty()) {
            return Set.of();
        }
        return new LinkedHashSet<>(selectIds(code, routes, null));
    }

    /**
     * 按条件查这本目录的记录编号，条件之间取并集。
     * onlyId 不为空时只核对这一条记录。
     */
    List<Long> selectIds(String registryCode, List<DataScopeCondition> conditions, Long onlyId) {
        List<String> predicates = new ArrayList<>();
        List<Object> args = new ArrayList<>();
        for (DataScopeCondition condition : conditions) {
            if (condition.allowedIds().isEmpty()) {
                continue;
            }
            predicates.add(predicate(registryCode, condition, args));
        }
        if (predicates.isEmpty()) {
            return List.of();
        }
        StringBuilder sql = new StringBuilder("SELECT id FROM ").append(tableOf(registryCode))
                .append(" WHERE deleted = false AND (").append(String.join(" OR ", predicates)).append(")");
        if (onlyId != null) {
            sql.append(" AND id = ?");
            args.add(onlyId);
        }
        return jdbcTemplate.queryForList(sql.toString(), Long.class, args.toArray());
    }

    private String predicate(String registryCode, DataScopeCondition condition, List<Object> args) {
        if (condition.onRecordId()) {
            return "id IN (" + placeholders(condition.allowedIds(), args) + ")";
        }
        if (condition.multiValued()) {
            args.add(condition.fieldCode());
            args.add(registryCode);
            args.add(storageCodeOf(registryCode));
            // 关系表按来源目录过滤：不同目录的记录编号会重复，只按字段编码反查会把别的目录的记录算进来
            return "id IN (SELECT r.source_entity_id FROM "
                    + TenantPhysicalTableNames.requirePhysical(RELATION_BASE_TABLE)
                    + " r WHERE r.deleted = false AND r.field_code = ? AND r.source_entity_type_code IN (?, ?)"
                    + " AND r.target_entity_id IN (" + placeholders(condition.allowedIds(), args) + "))";
        }
        String column = columnOf(registryCode, condition.fieldCode());
        return column + " IN (" + placeholders(condition.allowedIds(), args) + ")";
    }

    /**
     * 库里这条记录在多值归属字段上现在挂着哪些记录（读实体关系表，和列表反查同一份数据）。
     * 修改时核对「只增删自己范围里的段」用。
     */
    Set<Long> selectStoredMultiRefIds(String registryCode, String fieldCode, Long entityId) {
        String sql = "SELECT r.target_entity_id FROM " + TenantPhysicalTableNames.requirePhysical(RELATION_BASE_TABLE)
                + " r WHERE r.deleted = false AND r.field_code = ? AND r.source_entity_type_code IN (?, ?)"
                + " AND r.source_entity_id = ?";
        return new LinkedHashSet<>(jdbcTemplate.queryForList(sql, Long.class,
                fieldCode, registryCode, storageCodeOf(registryCode), entityId));
    }

    /** 按编号读启用记录的编号、名称、编码，按名称排序。右上角选站列表用。 */
    List<ScopeRecord> selectEnabledRecords(String registryCode, Collection<Long> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        List<Object> args = new ArrayList<>();
        String sql = "SELECT id, name, code FROM " + tableOf(registryCode)
                + " WHERE deleted = false AND status = 1 AND id IN (" + placeholders(ids, args) + ") ORDER BY name, id";
        return jdbcTemplate.query(sql, (rs, i) -> new ScopeRecord(rs.getLong("id"), rs.getString("name"),
                rs.getString("code")), args.toArray());
    }

    /**
     * 这些启用记录的上级编号。上级为空或 0 时不放进结果，调用方就停在这一层。
     */
    Map<Long, Long> selectEnabledParentIds(String registryCode, Collection<Long> ids) {
        if (ids.isEmpty()) {
            return Map.of();
        }
        List<Object> args = new ArrayList<>();
        String sql = "SELECT id, parent_id FROM " + tableOf(registryCode)
                + " WHERE deleted = false AND status = 1 AND id IN (" + placeholders(ids, args) + ")";
        Map<Long, Long> parents = new LinkedHashMap<>();
        jdbcTemplate.query(sql, rs -> {
            long parentId = rs.getLong("parent_id");
            if (!rs.wasNull() && parentId > 0) {
                parents.put(rs.getLong("id"), parentId);
            }
        }, args.toArray());
        return parents;
    }

    /**
     * 启用记录的编号、名称、上级编号。上级为空或 0 时上级编号为空，数据页把它放在树的最外层。
     */
    List<PipelineSegmentNode> selectEnabledTreeRecords(String registryCode, Collection<Long> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        List<Object> args = new ArrayList<>();
        String sql = "SELECT id, name, parent_id FROM " + tableOf(registryCode)
                + " WHERE deleted = false AND status = 1 AND id IN ("
                + placeholders(ids, args) + ") ORDER BY name, id";
        return jdbcTemplate.query(sql, (rs, i) -> {
            long id = rs.getLong("id");
            String name = rs.getString("name");
            long parentId = rs.getLong("parent_id");
            boolean parentMissing = rs.wasNull() || parentId <= 0;
            return new PipelineSegmentNode(id, name, parentMissing ? null : parentId);
        }, args.toArray());
    }

    /**
     * 可见场站加上经纬度和所属管线段。经纬度是场站专用列。管线段只读关系表里的 pipeline_segment_ids。
     */
    List<MapStation> selectMapStations(Collection<Long> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        List<Object> args = new ArrayList<>();
        String sql = "SELECT f.id, f.name, f.longitude, f.latitude, f.segment_seq, "
                + "COALESCE(array_agg(r.target_entity_id) FILTER (WHERE r.target_entity_id IS NOT NULL), ARRAY[]::bigint[]) AS segment_ids "
                + "FROM " + tableOf(FacilityOwningFieldCodes.TARGET_ENTITY_TYPE) + " f "
                + "LEFT JOIN " + TenantPhysicalTableNames.requirePhysical(RELATION_BASE_TABLE) + " r "
                + "ON r.source_entity_id = f.id AND r.deleted = false AND r.field_code = 'pipeline_segment_ids' "
                + "AND r.source_entity_type_code = 'facility' "
                + "WHERE f.deleted = false AND f.id IN (" + placeholders(ids, args) + ") "
                + "GROUP BY f.id, f.name, f.longitude, f.latitude, f.segment_seq ORDER BY f.name, f.id";
        return jdbcTemplate.query(sql, (rs, i) -> new MapStation(
                rs.getLong("id"),
                rs.getString("name"),
                readDouble(rs, "longitude"),
                readDouble(rs, "latitude"),
                readLongList(rs.getArray("segment_ids")),
                readInteger(rs, "segment_seq")), args.toArray());
    }

    private static Double readDouble(java.sql.ResultSet rs, String column) throws SQLException {
        double value = rs.getDouble(column);
        return rs.wasNull() ? null : value;
    }

    private static Integer readInteger(java.sql.ResultSet rs, String column) throws SQLException {
        int value = rs.getInt(column);
        return rs.wasNull() ? null : value;
    }

    private static List<Long> readLongList(Array sqlArray) throws SQLException {
        if (sqlArray == null) {
            return List.of();
        }
        Object raw = sqlArray.getArray();
        List<Long> ids = new ArrayList<>();
        if (raw instanceof long[] primitives) {
            for (long value : primitives) {
                if (value > 0) {
                    ids.add(value);
                }
            }
            return List.copyOf(ids);
        }
        if (raw instanceof Object[] objects) {
            for (Object item : objects) {
                if (item instanceof Number number && number.longValue() > 0) {
                    ids.add(number.longValue());
                }
            }
        }
        return List.copyOf(ids);
    }

    /**
     * 管理单元的类型：按编号读这些单元的型号编号。已删除的单元不返回。
     * 判断任职能不能管某本目录、保存可管目录时核对已有任职用。
     */
    Map<Long, Long> selectModelIds(String registryCode, Collection<Long> ids) {
        Map<Long, Long> models = new HashMap<>();
        if (ids.isEmpty()) {
            return models;
        }
        List<Object> args = new ArrayList<>();
        String sql = "SELECT id, model_id FROM " + tableOf(registryCode) + " WHERE deleted = false AND id IN ("
                + placeholders(ids, args) + ")";
        jdbcTemplate.query(sql, rs -> {
            long modelId = rs.getLong("model_id");
            if (!rs.wasNull()) {
                models.put(rs.getLong("id"), modelId);
            }
        }, args.toArray());
        return models;
    }

    /**
     * 按目录分组读范围记录的名称，人员详情列表补展示名称用。
     * 已删除的记录不返回名称，界面显示为缺失。
     */
    Map<String, Map<Long, String>> selectNamesByCatalog(List<Map.Entry<String, Long>> refs) {
        Map<String, Set<Long>> grouped = new LinkedHashMap<>();
        for (Map.Entry<String, Long> ref : refs) {
            grouped.computeIfAbsent(ref.getKey(), k -> new LinkedHashSet<>()).add(ref.getValue());
        }
        Map<String, Map<Long, String>> names = new HashMap<>();
        grouped.forEach((catalog, ids) -> {
            List<Object> args = new ArrayList<>();
            String sql = "SELECT id, name FROM " + tableOf(catalog) + " WHERE deleted = false AND id IN ("
                    + placeholders(ids, args) + ")";
            Map<Long, String> byId = new HashMap<>();
            jdbcTemplate.query(sql, rs -> {
                byId.put(rs.getLong("id"), rs.getString("name"));
            }, args.toArray());
            names.put(catalog, byId);
        });
        return names;
    }

    /**
     * 这本目录有多少条记录在某个管理字段上还没填。改管理方式前提示用：这些记录保存后谁都看不到，直到补上。
     * fieldCode 为空表示字段还没挂到目录上，所有记录都算没填。
     * 子数据类型、划分数据、使用已有数据按底座的表数，和范围计算读的是同一张表。
     */
    long countRecordsWithoutValue(String registryCode, String fieldCode, boolean multiValued) {
        String table = tableOf(registryCode);
        if (!StringUtils.hasText(fieldCode)) {
            Long total = jdbcTemplate.queryForObject("SELECT count(*) FROM " + table + " WHERE deleted = false", Long.class);
            return total == null ? 0L : total;
        }
        if (multiValued) {
            Long count = jdbcTemplate.queryForObject("SELECT count(*) FROM " + table + " e WHERE e.deleted = false"
                    + " AND NOT EXISTS (SELECT 1 FROM " + TenantPhysicalTableNames.requirePhysical(RELATION_BASE_TABLE)
                    + " r WHERE r.deleted = false AND r.field_code = ? AND r.source_entity_type_code IN (?, ?)"
                    + " AND r.source_entity_id = e.id)", Long.class,
                    fieldCode, registryCode, storageCodeOf(registryCode));
            return count == null ? 0L : count;
        }
        Long count = jdbcTemplate.queryForObject("SELECT count(*) FROM " + table + " WHERE deleted = false AND "
                + columnOf(registryCode, fieldCode) + " IS NULL", Long.class);
        return count == null ? 0L : count;
    }

    /**
     * 「按范围看」：一个单元从顶层到它自己的编号（读它的上级路径）。
     * 任职在这些单元上的人都能看到它，因为任职的范围总含下级。
     * 上级路径缺失是登记管理单元目录时就该拦住的脏数据，这里报错，不按「只有它自己」继续。
     */
    List<Long> selectPathIds(String registryCode, Long entityId) {
        List<String> paths = jdbcTemplate.queryForList(
                "SELECT tree_path FROM " + tableOf(registryCode) + " WHERE deleted = false AND id = ?",
                String.class, entityId);
        if (paths.isEmpty()) {
            throw new ServiceException(400, "管理单元不存在：#" + entityId);
        }
        List<Long> ids = new ArrayList<>();
        String path = paths.get(0);
        if (StringUtils.hasText(path)) {
            for (String part : path.split("/")) {
                if (!part.isBlank()) {
                    ids.add(Long.valueOf(part.trim()));
                }
            }
        }
        if (ids.isEmpty() || !ids.get(ids.size() - 1).equals(entityId)) {
            throw new ServiceException(400, "管理单元 #" + entityId + " 的上级路径缺失或不完整，请先修正组织数据");
        }
        return ids;
    }

    /** 记录是否存在且未删除。保存任职时核对职务。 */
    boolean recordExists(String registryCode, Long entityId) {
        Long count = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM " + tableOf(registryCode) + " WHERE deleted = false AND id = ?",
                Long.class, entityId);
        return count != null && count > 0;
    }

    /** 归属字段在专用表上的列名。没有专用列说明声明配错了（单值归属必须是专用列）。 */
    String columnOf(String registryCode, String fieldCode) {
        String column = entityDedicatedColumnService.resolvePhysicalColumn(storageCodeOf(registryCode), fieldCode);
        if (column == null) {
            throw new ServiceException(400,
                    "数据目录 " + registryCode + " 的归属字段 " + fieldCode + " 不是启用的单值引用字段");
        }
        return column;
    }

    /** 子数据类型、划分数据、使用已有数据的记录存在底座类型上；基础字段也登记在底座类型上。 */
    String storageCodeOf(String registryCode) {
        EntityTypeDO type = entityTypeMapper.selectByCode(registryCode);
        if (type == null) {
            throw new ServiceException(400, "数据目录不存在：" + registryCode);
        }
        return StringUtils.hasText(type.getBaseEntityTypeCode()) ? type.getBaseEntityTypeCode().trim() : registryCode;
    }

    private String tableOf(String registryCode) {
        return entityTableNameHandler.resolvePhysicalTableName(registryCode.trim());
    }

    private static String placeholders(Collection<Long> ids, List<Object> args) {
        StringBuilder out = new StringBuilder();
        for (Long id : ids) {
            out.append(out.isEmpty() ? "?" : ", ?");
            args.add(id);
        }
        return out.toString();
    }
}
