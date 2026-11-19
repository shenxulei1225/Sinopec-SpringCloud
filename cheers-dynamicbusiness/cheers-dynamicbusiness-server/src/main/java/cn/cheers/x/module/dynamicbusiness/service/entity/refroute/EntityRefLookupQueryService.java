package cn.cheers.x.module.dynamicbusiness.service.entity.refroute;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityRelationDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntityRelationMapper;
import cn.cheers.x.module.dynamicbusiness.framework.entity.EntityTableNameHandler;
import cn.cheers.x.module.dynamicbusiness.framework.tenant.TenantPhysicalTableNames;
import jakarta.annotation.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 按引用字段查记录：谁引用了这些记录（反查），两类记录之间互相挂了哪些（互查）。
 *
 * <p>引用值只有一处权威（见 {@link EntityRefColumnRouteQueryService}）：</p>
 * <ul>
 *   <li>单值专用列引用 → 查引用方专用表的那一列（列上有索引）。</li>
 *   <li>多选引用、扩展字段引用、手工建的实体关联 → 查实体关联表。</li>
 * </ul>
 * 两处各查各的再合并编号，不会互相补写。
 *
 * <p>场景速查：</p>
 * <ul>
 *   <li>「经引用查」：按某字段 + 被引用编号找引用方 → {@link #listSubjectIds}</li>
 *   <li>详情页「谁指向这条」→ {@link #listReferencingSubjectIds}</li>
 *   <li>删除前「还有谁在用」→ {@link #countInbound}</li>
 *   <li>数据页实体—实体筛选：两类记录互相挂了哪些 → {@link #listCounterpartIdsGrouped}</li>
 * </ul>
 *
 * 只读。不写实体关联表，不写分类-实体关联表。
 */
@Service
public class EntityRefLookupQueryService {

    private static final String DER_REF = "ENTITY_REF";
    private static final String DER_REF_MULTI = "ENTITY_REF_MULTI";

    @Resource
    private EntityRefColumnRouteQueryService routeQueryService;
    @Resource
    private EntityRelationMapper entityRelationMapper;
    @Resource
    private EntityTableNameHandler tableNameHandler;
    @Resource
    private JdbcTemplate jdbcTemplate;

    /** 被引用记录被某本目录的某个字段引用了多少条。删除拦截的提示按这个分组列出。 */
    public record InboundRefCount(String subjectTypeCode, String fieldCode, long count) {
    }

    /**
     * 引用方目录里，字段值指向这些编号的记录编号。
     * 引用方目录不写时只能查实体关联表（不知道去哪张专用表查列）。
     */
    public List<Long> listSubjectIds(String subjectTypeCode, String fieldCode, Collection<Long> targetIds) {
        List<Long> ids = cleanIds(targetIds);
        if (ids.isEmpty() || !StringUtils.hasText(fieldCode)) {
            return List.of();
        }
        if (!StringUtils.hasText(subjectTypeCode)) {
            return entityRelationMapper.selectSourceEntityIdsByFieldCodeTargetIdsAndSourceType(fieldCode.trim(), ids, null);
        }
        var route = routeQueryService.findRoute(subjectTypeCode, fieldCode);
        if (route.isPresent()) {
            return selectSubjectIdsByColumn(route.get(), ids, null);
        }
        // 关联行上的源类型可能是入口目录编码，也可能是存数类型，两个都认
        Set<Long> out = new LinkedHashSet<>();
        Set<String> sourceCodes = new LinkedHashSet<>(List.of(
                subjectTypeCode.trim(), routeQueryService.storageCodeOf(subjectTypeCode)));
        for (String sourceCode : sourceCodes) {
            out.addAll(entityRelationMapper.selectSourceEntityIdsByFieldCodeTargetIdsAndSourceType(
                    fieldCode.trim(), ids, sourceCode));
        }
        return new ArrayList<>(out);
    }

    /**
     * 引用方目录里，任一引用字段指向这条记录的记录编号（最多 limit 条，按编号顺序）。
     * 详情页「指向当前记录的对方」用。
     */
    public List<Long> listReferencingSubjectIds(String subjectTypeCode, String targetTypeCode, Long targetId, int limit) {
        if (!StringUtils.hasText(subjectTypeCode) || !StringUtils.hasText(targetTypeCode) || targetId == null) {
            return List.of();
        }
        Set<Long> out = new LinkedHashSet<>();
        for (RefColumnRoute route : routeQueryService.listRoutes(subjectTypeCode, targetTypeCode)) {
            out.addAll(selectSubjectIdsByColumn(route, List.of(targetId), limit));
        }
        for (EntityRelationDO row : entityRelationMapper.selectByTargetEntityIdAndSourceEntityTypeCode(
                targetId, subjectTypeCode.trim())) {
            if (row != null && row.getSourceEntityId() != null
                    && targetTypeCode.trim().equals(text(row.getTargetEntityTypeCode()))) {
                out.add(row.getSourceEntityId());
            }
        }
        return out.stream().limit(limit).toList();
    }

    /**
     * 这些记录被谁的哪个字段引用、各多少条。专用列引用逐列计数，其余按实体关联表里的引用行计数。
     * 手工建的实体关联（没有字段编码）不算引用，不在这里。
     */
    public List<InboundRefCount> countInbound(String targetTypeCode, Collection<Long> targetIds) {
        List<Long> ids = cleanIds(targetIds);
        if (ids.isEmpty() || !StringUtils.hasText(targetTypeCode)) {
            return List.of();
        }
        List<InboundRefCount> out = new ArrayList<>();
        for (RefColumnRoute route : routeQueryService.listRoutesTo(targetTypeCode)) {
            String sql = "SELECT COUNT(*) FROM " + tableNameHandler.resolvePhysicalTableName(route.subjectTypeCode())
                    + " WHERE deleted = false AND " + route.column() + " IN (" + placeholders(ids.size()) + ")";
            Long count = jdbcTemplate.queryForObject(sql, Long.class, ids.toArray());
            if (count != null && count > 0) {
                out.add(new InboundRefCount(route.subjectTypeCode(), route.fieldCode(), count));
            }
        }
        out.addAll(countInboundRelationRows(targetTypeCode, ids));
        return out;
    }

    /**
     * 两类记录互相挂了哪些：上游这批记录（related）各自对应的列表类记录（list）。
     *
     * <p>四个来源合并：上游的专用列指向列表类、列表类的专用列指向上游、实体关联表两个方向。</p>
     *
     * @return 上游编号 → 对端编号；每个上游编号都有一项，没挂的为空列表
     */
    public Map<Long, List<Long>> listCounterpartIdsGrouped(Collection<Long> relatedIds,
                                                         String relatedTypeCode,
                                                         String listTypeCode) {
        List<Long> ids = cleanIds(relatedIds);
        if (ids.isEmpty() || !StringUtils.hasText(relatedTypeCode) || !StringUtils.hasText(listTypeCode)) {
            return Map.of();
        }
        Map<Long, Set<Long>> grouped = new LinkedHashMap<>();
        ids.forEach(id -> grouped.put(id, new LinkedHashSet<>()));
        // 1. 上游自己的专用列指向列表类：例 选中设备后列场站，设备.所属场站就是对端
        for (RefColumnRoute route : routeQueryService.listRoutes(relatedTypeCode, listTypeCode)) {
            String sql = "SELECT id, " + route.column() + " FROM "
                    + tableNameHandler.resolvePhysicalTableName(route.subjectTypeCode())
                    + " WHERE deleted = false AND " + route.column() + " IS NOT NULL AND id IN ("
                    + placeholders(ids.size()) + ")";
            jdbcTemplate.query(sql, rs -> {
                grouped.get(rs.getLong(1)).add(rs.getLong(2));
            }, ids.toArray());
        }
        // 2. 列表类的专用列指向上游：例 选中场站后列设备，设备.所属场站 IN (选中的场站)
        for (RefColumnRoute route : routeQueryService.listRoutes(listTypeCode, relatedTypeCode)) {
            String sql = "SELECT " + route.column() + ", id FROM "
                    + tableNameHandler.resolvePhysicalTableName(route.subjectTypeCode())
                    + " WHERE deleted = false AND " + route.column() + " IN (" + placeholders(ids.size()) + ")";
            jdbcTemplate.query(sql, rs -> {
                grouped.get(rs.getLong(1)).add(rs.getLong(2));
            }, ids.toArray());
        }
        // 3. 实体关联表两个方向（多选引用、扩展字段引用、手工关联）
        entityRelationMapper.selectCounterpartEntityIdsGrouped(ids, relatedTypeCode.trim(), listTypeCode.trim())
                .forEach((id, values) -> grouped.computeIfAbsent(id, k -> new LinkedHashSet<>()).addAll(values));
        Map<Long, List<Long>> out = new LinkedHashMap<>();
        grouped.forEach((id, values) -> out.put(id, List.copyOf(values)));
        return out;
    }

    /** 同 {@link #listCounterpartIdsGrouped}，只要对端编号的并集。 */
    public List<Long> listCounterpartIds(Collection<Long> relatedIds, String relatedTypeCode, String listTypeCode) {
        Set<Long> out = new LinkedHashSet<>();
        listCounterpartIdsGrouped(relatedIds, relatedTypeCode, listTypeCode).values().forEach(out::addAll);
        return new ArrayList<>(out);
    }

    private List<Long> selectSubjectIdsByColumn(RefColumnRoute route, List<Long> targetIds, Integer limit) {
        String sql = "SELECT id FROM " + tableNameHandler.resolvePhysicalTableName(route.subjectTypeCode())
                + " WHERE deleted = false AND " + route.column() + " IN (" + placeholders(targetIds.size()) + ")"
                + (limit == null ? "" : " ORDER BY id LIMIT " + Math.max(limit, 0));
        return jdbcTemplate.queryForList(sql, Long.class, targetIds.toArray());
    }

    private List<InboundRefCount> countInboundRelationRows(String targetTypeCode, List<Long> targetIds) {
        List<String> targetCodes = new ArrayList<>(routeQueryService.catalogCodesSharingStorage(targetTypeCode));
        String sql = "SELECT source_entity_type_code, field_code, COUNT(DISTINCT source_entity_id) FROM "
                + TenantPhysicalTableNames.requirePhysical("dynamic_entity_relation")
                + " WHERE deleted = false AND relation_type IN (?, ?) AND field_code IS NOT NULL"
                + " AND target_entity_type_code IN (" + placeholders(targetCodes.size()) + ")"
                + " AND target_entity_id IN (" + placeholders(targetIds.size()) + ")"
                + " GROUP BY source_entity_type_code, field_code";
        List<Object> args = new ArrayList<>();
        args.add(DER_REF);
        args.add(DER_REF_MULTI);
        args.addAll(targetCodes);
        args.addAll(targetIds);
        return jdbcTemplate.query(sql, (rs, i) -> new InboundRefCount(rs.getString(1), rs.getString(2), rs.getLong(3)),
                args.toArray());
    }

    private static List<Long> cleanIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return ids.stream().filter(Objects::nonNull).filter(id -> id > 0).distinct().toList();
    }

    private static String placeholders(int size) {
        return String.join(",", Collections.nCopies(size, "?"));
    }

    private static String text(String value) {
        return value == null ? "" : value.trim();
    }
}
