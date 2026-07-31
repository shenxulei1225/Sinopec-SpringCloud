package cn.cheers.x.module.dynamicbusiness.dal.repository.entity;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.framework.entity.EntityTableNameHandler;
import cn.cheers.x.module.dynamicbusiness.framework.entitytype.EntityTypeScopeContext;
import cn.cheers.x.module.dynamicbusiness.framework.tenant.TenantPhysicalTableNames;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 分类范围内实体：EXISTS（relation ∪ link）+ 库内 ORDER BY + LIMIT。
 *
 * <p>用于数据管理点分类后按字段排序分页，避免先拉全量候选再内存重排。</p>
 */
@Repository
@RequiredArgsConstructor
public class CategoryScopedEntityQueryRepository {

    private static final Set<String> SQL_ORDER_COLUMNS = Set.of("name", "code", "status", "id", "sort");
    private static final Pattern SAFE_PHYSICAL_COLUMN = Pattern.compile("^[a-z][a-z0-9_]*$");

    private final JdbcTemplate jdbcTemplate;
    private final EntityTableNameHandler entityTableNameHandler;

    /**
     * 无不可下推筛时的快路径：COUNT + ORDER BY + LIMIT，只回本页 id。
     *
     * @param physicalFilters 已校验的专用列/核心列 EQ·IN；可为 null
     */
    public PageResult<Long> pageEntityIdsByCategoryScope(
            List<Long> expandedCategoryIds,
            String entityTypeCode,
            List<Long> modelIds,
            String domain,
            String scopeRegistryCode,
            String orderByColumn,
            boolean orderAsc,
            List<PhysicalColumnFilter> physicalFilters,
            String keyword,
            int pageNo,
            int pageSize) {
        return pageEntityIdsByCategoryScope(
                expandedCategoryIds, entityTypeCode, modelIds, domain, scopeRegistryCode,
                orderByColumn, orderAsc, physicalFilters, keyword, null, pageNo, pageSize);
    }

    public PageResult<Long> pageEntityIdsByCategoryScope(
            List<Long> expandedCategoryIds,
            String entityTypeCode,
            List<Long> modelIds,
            String domain,
            String scopeRegistryCode,
            String orderByColumn,
            boolean orderAsc,
            List<PhysicalColumnFilter> physicalFilters,
            String keyword,
            KeywordSearchSpec keywordSearch,
            int pageNo,
            int pageSize) {
        if (expandedCategoryIds == null || expandedCategoryIds.isEmpty()
                || !StringUtils.hasText(entityTypeCode)) {
            return new PageResult<>(List.of(), 0L);
        }
        String sqlOrderColumn = resolveSqlOrderColumn(orderByColumn);
        if (sqlOrderColumn == null) {
            throw new IllegalArgumentException("分类范围 DB 分页不支持按该字段排序: " + orderByColumn);
        }

        QueryParts parts = buildQueryParts(
                expandedCategoryIds, entityTypeCode.trim(), modelIds, domain, scopeRegistryCode,
                physicalFilters, keyword, keywordSearch);
        if (parts == null) {
            return new PageResult<>(List.of(), 0L);
        }

        Long total = jdbcTemplate.queryForObject(parts.countSql, Long.class, parts.args.toArray());
        long totalCount = total == null ? 0L : total;
        if (totalCount <= 0) {
            return new PageResult<>(List.of(), 0L);
        }

        int pn = Math.max(pageNo, 1);
        int ps = Math.max(pageSize, 1);
        int offset = (pn - 1) * ps;
        if (offset >= totalCount) {
            return new PageResult<>(List.of(), totalCount);
        }

        String dir = orderAsc ? "ASC" : "DESC";
        String pageSql = parts.selectSql
                + " ORDER BY e." + sqlOrderColumn + " " + dir + " NULLS LAST, e.id ASC"
                + " LIMIT ? OFFSET ?";
        List<Object> pageArgs = new ArrayList<>(parts.args);
        pageArgs.add(ps);
        pageArgs.add(offset);
        List<Long> ids = jdbcTemplate.query(pageSql, (rs, rowNum) -> rs.getLong(1), pageArgs.toArray());
        return new PageResult<>(ids != null ? ids : List.of(), totalCount);
    }

    /** 有不可下推筛时：只取范围内 id（保 id 序），再上层过滤/排序。 */
    public List<Long> listEntityIdsByCategoryScope(
            List<Long> expandedCategoryIds,
            String entityTypeCode,
            List<Long> modelIds,
            String domain,
            String scopeRegistryCode) {
        if (expandedCategoryIds == null || expandedCategoryIds.isEmpty()
                || !StringUtils.hasText(entityTypeCode)) {
            return List.of();
        }
        QueryParts parts = buildQueryParts(
                expandedCategoryIds, entityTypeCode.trim(), modelIds, domain, scopeRegistryCode,
                null, null, null);
        if (parts == null) {
            return List.of();
        }
        String sql = parts.selectSql + " ORDER BY e.id ASC";
        List<Long> ids = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong(1), parts.args.toArray());
        return ids != null ? ids : List.of();
    }

    public boolean supportsSqlOrder(String orderByColumn) {
        return resolveSqlOrderColumn(orderByColumn) != null;
    }

    private QueryParts buildQueryParts(
            List<Long> expandedCategoryIds,
            String entityTypeCode,
            List<Long> modelIds,
            String domain,
            String scopeRegistryCode,
            List<PhysicalColumnFilter> physicalFilters,
            String keyword,
            KeywordSearchSpec keywordSearch) {
        List<Long> cats = normalizeIds(expandedCategoryIds);
        if (cats.isEmpty()) {
            return null;
        }
        String entityTable = entityTableNameHandler.resolvePhysicalTableName(entityTypeCode);
        String relationTable = TenantPhysicalTableNames.requirePhysical("dynamic_entity_category_relation");
        String linkTable = TenantPhysicalTableNames.requirePhysical("dynamic_category_entity_link");

        List<Object> args = new ArrayList<>();
        StringBuilder where = new StringBuilder(" WHERE e.deleted = false");

        String normalizedDomain = EntityTypeScopeContext.normalizeDomain(domain);
        if (StringUtils.hasText(normalizedDomain)) {
            where.append(" AND e.domain = ?");
            args.add(normalizedDomain);
        }

        List<Long> normalizedModelIds = normalizeIds(modelIds);
        if (!normalizedModelIds.isEmpty()) {
            where.append(" AND e.model_id IN (").append(placeholders(normalizedModelIds.size())).append(")");
            args.addAll(normalizedModelIds);
        }

        if (StringUtils.hasText(scopeRegistryCode)) {
            where.append("""
                     AND EXISTS (
                        SELECT 1
                        FROM dynamic_entity_type_scope s
                        WHERE s.deleted = false
                          AND s.entity_type_code = ?
                          AND s.entity_id = e.id
                    )
                    """);
            args.add(scopeRegistryCode.trim());
        }

        String inClause = placeholders(cats.size());
        where.append("""
                 AND (
                    EXISTS (
                        SELECT 1
                        FROM %s ecr
                        WHERE ecr.deleted = false
                          AND ecr.entity_id = e.id
                          AND ecr.entity_type_code = ?
                          AND ecr.category_id IN (%s)
                    )
                    OR EXISTS (
                        SELECT 1
                        FROM %s cel
                        WHERE cel.deleted = false
                          AND cel.entity_id = e.id
                          AND cel.entity_type_code = ?
                          AND cel.category_id IN (%s)
                    )
                )
                """.formatted(relationTable, inClause, linkTable, inClause));
        args.add(entityTypeCode);
        args.addAll(cats);
        args.add(entityTypeCode);
        args.addAll(cats);

        KeywordSearchSql.appendToNativeWhere(where, args, keyword, keywordSearch);

        appendPhysicalFilters(where, args, physicalFilters);

        String fromWhere = " FROM " + entityTable + " e" + where;
        return new QueryParts("SELECT e.id" + fromWhere, "SELECT COUNT(*)" + fromWhere, args);
    }

    static void appendPhysicalFilters(StringBuilder where, List<Object> args,
                                      List<PhysicalColumnFilter> physicalFilters) {
        if (physicalFilters == null || physicalFilters.isEmpty()) {
            return;
        }
        for (PhysicalColumnFilter filter : physicalFilters) {
            if (filter == null || !SAFE_PHYSICAL_COLUMN.matcher(filter.column()).matches()) {
                continue;
            }
            String col = filter.column();
            String op = filter.op() == null ? "" : filter.op().trim().toUpperCase(Locale.ROOT);
            if ("EQ".equals(op)) {
                where.append(" AND e.").append(col).append(" = ?");
                args.add(filter.value());
            } else if ("IN".equals(op) && filter.value() instanceof Collection<?> collection) {
                List<Object> values = new ArrayList<>();
                for (Object v : collection) {
                    if (v != null) {
                        values.add(v);
                    }
                }
                if (values.isEmpty()) {
                    where.append(" AND 1=0");
                    continue;
                }
                where.append(" AND e.").append(col).append(" IN (")
                        .append(placeholders(values.size())).append(")");
                args.addAll(values);
            }
        }
    }

    private static String resolveSqlOrderColumn(String orderByColumn) {
        if (!StringUtils.hasText(orderByColumn)) {
            return "name";
        }
        String column = orderByColumn.trim().toLowerCase(Locale.ROOT);
        if (SQL_ORDER_COLUMNS.contains(column)) {
            return column;
        }
        if (SAFE_PHYSICAL_COLUMN.matcher(column).matches()) {
            return column;
        }
        return null;
    }

    private static List<Long> normalizeIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return ids.stream().filter(id -> id != null && id > 0).distinct().toList();
    }

    private static String placeholders(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append("?");
        }
        return sb.toString();
    }

    private record QueryParts(String selectSql, String countSql, List<Object> args) {
    }
}
