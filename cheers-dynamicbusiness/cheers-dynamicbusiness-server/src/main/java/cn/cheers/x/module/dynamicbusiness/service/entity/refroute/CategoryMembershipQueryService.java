package cn.cheers.x.module.dynamicbusiness.service.entity.refroute;

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
import java.util.Optional;
import java.util.Set;

/**
 * 数据页左侧点了分类（或树上点了某座场站、某段管线）之后，右侧列表里该出现哪些记录。
 *
 * <p>一条记录挂在分类下有三种来源：</p>
 * <ol>
 *   <li>拖进分类、多选引用、扩展字段引用 → 分类-实体关联表里有一行。</li>
 *   <li>记录本身就是这个分类（分类即实体，如场站自己）→ 分类绑定表里有一行。</li>
 *   <li>记录的单值引用专用列指向一个分类即实体（如设备「所属场站」= 某站）→ 只看专用列，不抄关联行。</li>
 * </ol>
 *
 * <p>性能约束：每组分类只走一条路径。</p>
 * <ul>
 *   <li>这组分类下没有分类-实体关联行 → 只用专用列和自身编号：{@code e.列 IN (...) OR e.id IN (...)}，全走索引。</li>
 *   <li>有关联行 → {@code e.id IN (关联行 UNION ALL 绑定行 UNION ALL 专用列反查)}，按集合半连接。</li>
 * </ul>
 * 禁止把关联表 EXISTS 和专用列用 OR 拼在一起（会让专用列索引失效）。
 *
 * 只读。不写分类-实体关联表，不补绑定行。
 */
@Service
public class CategoryMembershipQueryService {

    private static final String ECR_TABLE = "dynamic_entity_category_relation";
    private static final String LINK_TABLE = "dynamic_category_entity_link";

    @Resource
    private EntityRefColumnRouteQueryService routeQueryService;
    @Resource
    private EntityTableNameHandler tableNameHandler;
    @Resource
    private JdbcTemplate jdbcTemplate;

    /** 拼进 {@code WHERE} 的一段条件（实体表别名固定为 {@code e}），带自己的参数。 */
    public record SqlCondition(String sql, List<Object> args) {
    }

    /** 经专用列挂到分类下的一条记录：记录编号 + 它落在哪个分类上（用于按分类顺序排）。 */
    public record ColumnMember(long entityId, long categoryId) {
    }

    /** 分类绑定的那条记录（分类即实体）。 */
    private record BoundRecord(long categoryId, String entityTypeCode, long entityId) {
    }

    /**
     * 这组分类（已含子树）的成员条件。
     *
     * @param storageTypeCode 列表记录的存数类型（分类-实体关联表里的类型编码也是它）
     * @return 条件；分类为空时返回恒假
     */
    public SqlCondition inCategoriesCondition(String storageTypeCode, List<Long> categoryIds) {
        List<Long> cats = cleanIds(categoryIds);
        if (cats.isEmpty() || !StringUtils.hasText(storageTypeCode)) {
            return new SqlCondition("1=0", List.of());
        }
        String subject = storageTypeCode.trim();
        List<BoundRecord> bound = loadBoundRecords(cats);
        if (hasRelationRows(subject, cats)) {
            return relationSetCondition(subject, cats, bound);
        }
        return columnOnlyCondition(subject, bound);
    }

    /**
     * 「未分类」：不在这个分类种类的任何节点下（保留的「未分类」桶节点不算已分类）。
     * 三种来源都要排除：关联行、自身绑定、专用列指向该种类下的分类即实体。
     */
    public SqlCondition notInCategoryTypeCondition(String storageTypeCode, String categoryTypeCode) {
        String subject = storageTypeCode.trim();
        String ecr = TenantPhysicalTableNames.requirePhysical(ECR_TABLE);
        String link = TenantPhysicalTableNames.requirePhysical(LINK_TABLE);
        StringBuilder sql = new StringBuilder();
        List<Object> args = new ArrayList<>();
        sql.append("""
                NOT EXISTS (SELECT 1 FROM %s ecr
                    INNER JOIN dynamic_category c ON c.id = ecr.category_id AND c.deleted = false
                    WHERE ecr.deleted = false AND ecr.entity_id = e.id AND ecr.entity_type_code = ?
                      AND c.category_type_code = ? AND UPPER(c.code) NOT LIKE '%%UNCATEGORIZED%%')
                AND NOT EXISTS (SELECT 1 FROM %s cel
                    INNER JOIN dynamic_category c2 ON c2.id = cel.category_id AND c2.deleted = false
                    WHERE cel.deleted = false AND cel.entity_id = e.id AND cel.entity_type_code = ?
                      AND c2.category_type_code = ? AND UPPER(c2.code) NOT LIKE '%%UNCATEGORIZED%%')
                """.formatted(ecr, link));
        args.add(subject);
        args.add(categoryTypeCode);
        args.add(subject);
        args.add(categoryTypeCode);
        for (String target : boundTypesOfCategoryType(categoryTypeCode)) {
            for (RefColumnRoute route : routeQueryService.listRoutes(subject, target)) {
                // 专用列为空 → 没挂；有值 → 看它指向的记录是否绑在该种类的非保留节点上
                sql.append("""
                         AND (e.%1$s IS NULL OR NOT EXISTS (SELECT 1 FROM %2$s cr
                            INNER JOIN dynamic_category c3 ON c3.id = cr.category_id AND c3.deleted = false
                            WHERE cr.deleted = false AND cr.entity_id = e.%1$s AND cr.entity_type_code = ?
                              AND c3.category_type_code = ? AND UPPER(c3.code) NOT LIKE '%%UNCATEGORIZED%%'))
                        """.formatted(route.column(), link));
                args.add(target);
                args.add(categoryTypeCode);
            }
        }
        return new SqlCondition(sql.toString(), args);
    }

    /**
     * 经专用列挂到这些分类下的记录。内存编排（按分类顺序排、按关联行 sort 排）用它补上第 3 种来源。
     * 同一记录落在多个分类上时各出一项，由调用方按分类顺序取最靠前的。
     *
     * @param domain 业务域；有值时只要这个业务域的记录（子数据类型入口），null 表示不限
     */
    public List<ColumnMember> listColumnMembers(String storageTypeCode, List<Long> categoryIds, String domain) {
        List<Long> cats = cleanIds(categoryIds);
        if (cats.isEmpty() || !StringUtils.hasText(storageTypeCode)) {
            return List.of();
        }
        String subject = storageTypeCode.trim();
        List<ColumnMember> out = new ArrayList<>();
        Map<String, Map<Long, List<Long>>> categoriesByTarget = groupCategoriesByTarget(loadBoundRecords(cats));
        for (Map.Entry<String, Map<Long, List<Long>>> entry : categoriesByTarget.entrySet()) {
            Map<Long, List<Long>> categoriesByTargetId = entry.getValue();
            List<Long> targetIds = new ArrayList<>(categoriesByTargetId.keySet());
            for (RefColumnRoute route : routeQueryService.listRoutes(subject, entry.getKey())) {
                boolean byDomain = StringUtils.hasText(domain);
                String sql = "SELECT id, " + route.column() + " FROM " + tableNameHandler.resolvePhysicalTableName(subject)
                        + " WHERE deleted = false AND " + route.column() + " IN (" + placeholders(targetIds.size()) + ")"
                        + (byDomain ? " AND domain = ?" : "") + " ORDER BY id";
                List<Object> args = new ArrayList<>(targetIds);
                if (byDomain) {
                    args.add(domain.trim());
                }
                jdbcTemplate.query(sql, rs -> {
                    long entityId = rs.getLong(1);
                    for (Long categoryId : categoriesByTargetId.getOrDefault(rs.getLong(2), List.of())) {
                        out.add(new ColumnMember(entityId, categoryId));
                    }
                }, args.toArray());
            }
        }
        return out;
    }

    /**
     * 把记录挂到这个分类上，是否就是写记录的某一列引用（例：设备拖到「漳州站」= 设备.所属场站 写成漳州站）。
     *
     * <p>条件：分类绑了一条记录（分类即实体），列表这本目录恰有一列单值专用列引用指向那本目录。
     * 有两列指向同一本（如管线的起点站、终点站）时不知道写哪列 → 返回空，仍按分类-实体关联行处理。</p>
     *
     * @return 要写的那一列；空表示挂这个分类仍写分类-实体关联行
     */
    public Optional<RefColumnRoute> columnRouteForCategory(String storageTypeCode, Long categoryId) {
        if (!StringUtils.hasText(storageTypeCode) || categoryId == null) {
            return Optional.empty();
        }
        List<BoundRecord> bound = loadBoundRecords(List.of(categoryId));
        if (bound.size() != 1 || !StringUtils.hasText(bound.get(0).entityTypeCode())) {
            return Optional.empty();
        }
        List<RefColumnRoute> routes = routeQueryService.listRoutes(storageTypeCode.trim(), bound.get(0).entityTypeCode());
        return routes.size() == 1 ? Optional.of(routes.get(0)) : Optional.empty();
    }

    /** 这条记录经单值引用专用列挂上的分类（指向的分类即实体所绑的分类）。 */
    public List<Long> listColumnCategoryIds(String storageTypeCode, Long entityId) {
        if (!StringUtils.hasText(storageTypeCode) || entityId == null) {
            return List.of();
        }
        String subject = storageTypeCode.trim();
        List<RefColumnRoute> routes = routeQueryService.listRoutes(subject);
        if (routes.isEmpty()) {
            return List.of();
        }
        String columns = String.join(", ", routes.stream().map(RefColumnRoute::column).toList());
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT " + columns + " FROM "
                + tableNameHandler.resolvePhysicalTableName(subject) + " WHERE deleted = false AND id = ?", entityId);
        if (rows.isEmpty()) {
            return List.of();
        }
        Set<Long> categoryIds = new LinkedHashSet<>();
        Map<String, Object> row = rows.get(0);
        for (RefColumnRoute route : routes) {
            Object value = row.get(route.column());
            if (value instanceof Number number) {
                categoryIds.addAll(jdbcTemplate.queryForList("SELECT category_id FROM "
                                + TenantPhysicalTableNames.requirePhysical(LINK_TABLE)
                                + " WHERE deleted = false AND entity_type_code = ? AND entity_id = ?",
                        Long.class, route.targetTypeCode(), number.longValue()));
            }
        }
        return new ArrayList<>(categoryIds);
    }

    private SqlCondition columnOnlyCondition(String subject, List<BoundRecord> bound) {
        List<String> parts = new ArrayList<>();
        List<Object> args = new ArrayList<>();
        Map<String, List<Long>> idsByTarget = idsByTarget(bound);
        for (Map.Entry<String, List<Long>> entry : idsByTarget.entrySet()) {
            List<Long> ids = entry.getValue();
            if (routeQueryService.storageCodeOf(entry.getKey()).equals(subject)) {
                parts.add("e.id IN (" + placeholders(ids.size()) + ")");
                args.addAll(ids);
            }
            for (RefColumnRoute route : routeQueryService.listRoutes(subject, entry.getKey())) {
                parts.add("e." + route.column() + " IN (" + placeholders(ids.size()) + ")");
                args.addAll(ids);
            }
        }
        if (parts.isEmpty()) {
            return new SqlCondition("1=0", List.of());
        }
        return new SqlCondition("(" + String.join(" OR ", parts) + ")", args);
    }

    private SqlCondition relationSetCondition(String subject, List<Long> cats, List<BoundRecord> bound) {
        String inCats = placeholders(cats.size());
        List<String> branches = new ArrayList<>();
        List<Object> args = new ArrayList<>();
        branches.add("SELECT entity_id FROM " + TenantPhysicalTableNames.requirePhysical(ECR_TABLE)
                + " WHERE deleted = false AND entity_type_code = ? AND category_id IN (" + inCats + ")");
        args.add(subject);
        args.addAll(cats);
        branches.add("SELECT entity_id FROM " + TenantPhysicalTableNames.requirePhysical(LINK_TABLE)
                + " WHERE deleted = false AND entity_type_code = ? AND category_id IN (" + inCats + ")");
        args.add(subject);
        args.addAll(cats);
        String subjectTable = tableNameHandler.resolvePhysicalTableName(subject);
        for (Map.Entry<String, List<Long>> entry : idsByTarget(bound).entrySet()) {
            List<Long> ids = entry.getValue();
            for (RefColumnRoute route : routeQueryService.listRoutes(subject, entry.getKey())) {
                branches.add("SELECT id FROM " + subjectTable + " WHERE deleted = false AND "
                        + route.column() + " IN (" + placeholders(ids.size()) + ")");
                args.addAll(ids);
            }
        }
        return new SqlCondition("e.id IN (" + String.join(" UNION ALL ", branches) + ")", args);
    }

    private boolean hasRelationRows(String subject, List<Long> cats) {
        List<Integer> hit = jdbcTemplate.queryForList("SELECT 1 FROM " + TenantPhysicalTableNames.requirePhysical(ECR_TABLE)
                        + " WHERE deleted = false AND entity_type_code = ? AND category_id IN (" + placeholders(cats.size())
                        + ") LIMIT 1", Integer.class,
                prepend(subject, cats));
        return !hit.isEmpty();
    }

    private List<BoundRecord> loadBoundRecords(List<Long> cats) {
        return jdbcTemplate.query("SELECT category_id, entity_type_code, entity_id FROM "
                        + TenantPhysicalTableNames.requirePhysical(LINK_TABLE)
                        + " WHERE deleted = false AND category_id IN (" + placeholders(cats.size()) + ")",
                (rs, i) -> new BoundRecord(rs.getLong(1), rs.getString(2), rs.getLong(3)), cats.toArray());
    }

    private List<String> boundTypesOfCategoryType(String categoryTypeCode) {
        return jdbcTemplate.queryForList("SELECT DISTINCT cel.entity_type_code FROM "
                + TenantPhysicalTableNames.requirePhysical(LINK_TABLE) + " cel"
                + " INNER JOIN dynamic_category c ON c.id = cel.category_id AND c.deleted = false"
                + " WHERE cel.deleted = false AND c.category_type_code = ?", String.class, categoryTypeCode);
    }

    private static Map<String, List<Long>> idsByTarget(List<BoundRecord> bound) {
        Map<String, Set<Long>> grouped = new LinkedHashMap<>();
        for (BoundRecord record : bound) {
            if (StringUtils.hasText(record.entityTypeCode())) {
                grouped.computeIfAbsent(record.entityTypeCode().trim(), k -> new LinkedHashSet<>()).add(record.entityId());
            }
        }
        Map<String, List<Long>> out = new LinkedHashMap<>();
        grouped.forEach((type, ids) -> out.put(type, new ArrayList<>(ids)));
        return out;
    }

    private static Map<String, Map<Long, List<Long>>> groupCategoriesByTarget(List<BoundRecord> bound) {
        Map<String, Map<Long, List<Long>>> out = new LinkedHashMap<>();
        for (BoundRecord record : bound) {
            if (StringUtils.hasText(record.entityTypeCode())) {
                out.computeIfAbsent(record.entityTypeCode().trim(), k -> new LinkedHashMap<>())
                        .computeIfAbsent(record.entityId(), k -> new ArrayList<>())
                        .add(record.categoryId());
            }
        }
        return out;
    }

    private static Object[] prepend(Object first, List<Long> rest) {
        List<Object> args = new ArrayList<>(rest.size() + 1);
        args.add(first);
        args.addAll(rest);
        return args.toArray();
    }

    private static List<Long> cleanIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return ids.stream().filter(Objects::nonNull).filter(id -> id > 0).distinct().toList();
    }

    private static String placeholders(int size) {
        return String.join(",", Collections.nCopies(size, "?"));
    }
}
