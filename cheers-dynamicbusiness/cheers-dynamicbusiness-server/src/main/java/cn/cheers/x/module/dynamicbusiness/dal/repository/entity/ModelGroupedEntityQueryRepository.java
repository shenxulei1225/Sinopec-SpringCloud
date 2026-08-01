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
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/**
 * 按「已排好的型号序」成组取实体 id：一次 SQL（型号序 + 组内字段 + 可选 LIMIT）。
 *
 * <p>调用方负责算 {@code orderedModelIds} 与范围语义；本类不关心场景 1 / 2 编排。</p>
 * <p>有 keyword/filters 时请用 {@link #listOrderedEntityIds}（无 LIMIT），再上层过滤后切页；
 * 禁止对带过滤的请求直接走 {@link #pageEntityIds}。</p>
 */
@Repository
@RequiredArgsConstructor
public class ModelGroupedEntityQueryRepository {

    private static final Set<String> SQL_ORDER_COLUMNS = Set.of("name", "code", "status", "id", "sort");

    private final JdbcTemplate jdbcTemplate;
    private final EntityTableNameHandler entityTableNameHandler;

    /** 实体范围：仅型号 / 未分类差集 / 分类范围内。 */
    public enum ScopeKind {
        /** 仅这些型号下的实体（场景 1） */
        BY_MODELS,
        /** 未挂当前种类（relation ∪ link，保留桶不算已分类） */
        UNCATEGORIZED,
        /** 已挂 expandedCategoryIds（relation ∪ link） */
        BY_CATEGORY
    }

    /**
     * @param categoryTypeCode     {@link ScopeKind#UNCATEGORIZED} 必填
     * @param expandedCategoryIds  {@link ScopeKind#BY_CATEGORY} 必填（已含子树）
     * @param restrictToModels     true：只保留 orderedModelIds 内实体（请求显式选了型号）；
     *                             false：LEFT JOIN 成组，范围外型号的实体仍可出现在末尾
     */
    public record EntityScope(
            ScopeKind kind,
            String categoryTypeCode,
            List<Long> expandedCategoryIds,
            boolean restrictToModels) {

        public static EntityScope byModels() {
            return new EntityScope(ScopeKind.BY_MODELS, null, List.of(), true);
        }

        public static EntityScope uncategorized(String categoryTypeCode, boolean restrictToModels) {
            return new EntityScope(ScopeKind.UNCATEGORIZED, categoryTypeCode, List.of(), restrictToModels);
        }

        public static EntityScope byCategory(List<Long> expandedCategoryIds, boolean restrictToModels) {
            return new EntityScope(ScopeKind.BY_CATEGORY, null,
                    expandedCategoryIds == null ? List.of() : expandedCategoryIds, restrictToModels);
        }
    }

    /** 无 keyword/filters：COUNT + ORDER BY 型号序, 字段 + LIMIT。 */
    public PageResult<Long> pageEntityIds(
            List<Long> orderedModelIds,
            String entityTypeCode,
            String domain,
            String scopeRegistryCode,
            EntityScope scope,
            String orderByColumn,
            boolean orderAsc,
            int pageNo,
            int pageSize) {
        QueryParts parts = buildQueryParts(orderedModelIds, entityTypeCode, domain, scopeRegistryCode, scope);
        if (parts == null) {
            return new PageResult<>(List.of(), 0L);
        }
        String sqlOrderColumn = requireSqlOrderColumn(orderByColumn);

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
                + " ORDER BY " + modelOrdExpr(parts.innerJoin) + ", e." + sqlOrderColumn + " " + dir
                + " NULLS LAST, e.id ASC"
                + " LIMIT ? OFFSET ?";
        List<Object> pageArgs = new ArrayList<>(parts.args);
        pageArgs.add(ps);
        pageArgs.add(offset);
        List<Long> ids = jdbcTemplate.query(pageSql, (rs, rowNum) -> rs.getLong(1), pageArgs.toArray());
        return new PageResult<>(ids != null ? ids : List.of(), totalCount);
    }

    /**
     * 有 keyword/filters 或全量装有序候选：同一 WHERE，按型号成组排序，不 LIMIT。
     */
    public List<Long> listOrderedEntityIds(
            List<Long> orderedModelIds,
            String entityTypeCode,
            String domain,
            String scopeRegistryCode,
            EntityScope scope,
            String orderByColumn,
            boolean orderAsc) {
        QueryParts parts = buildQueryParts(orderedModelIds, entityTypeCode, domain, scopeRegistryCode, scope);
        if (parts == null) {
            return List.of();
        }
        String sqlOrderColumn = requireSqlOrderColumn(orderByColumn);
        String dir = orderAsc ? "ASC" : "DESC";
        String sql = parts.selectSql
                + " ORDER BY " + modelOrdExpr(parts.innerJoin) + ", e." + sqlOrderColumn + " " + dir
                + " NULLS LAST, e.id ASC";
        List<Long> ids = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong(1), parts.args.toArray());
        return ids != null ? ids : List.of();
    }

    public boolean supportsSqlOrder(String orderByColumn) {
        return resolveSqlOrderColumn(orderByColumn) != null;
    }

    private static String modelOrdExpr(boolean innerJoin) {
        if (innerJoin) {
            return "m.ord ASC";
        }
        return "COALESCE(m.ord, 2147483647) ASC";
    }

    private QueryParts buildQueryParts(
            List<Long> orderedModelIds,
            String entityTypeCode,
            String domain,
            String scopeRegistryCode,
            EntityScope scope) {
        if (scope == null || !StringUtils.hasText(entityTypeCode)) {
            return null;
        }
        List<Long> models = normalizeModelIds(orderedModelIds);
        if (models.isEmpty()) {
            return null;
        }
        if (scope.kind() == ScopeKind.UNCATEGORIZED && !StringUtils.hasText(scope.categoryTypeCode())) {
            return null;
        }
        if (scope.kind() == ScopeKind.BY_CATEGORY) {
            List<Long> cats = normalizeModelIds(scope.expandedCategoryIds());
            if (cats.isEmpty()) {
                return null;
            }
        }

        String entityTable = entityTableNameHandler.resolvePhysicalTableName(entityTypeCode.trim());
        List<Object> args = new ArrayList<>();

        StringBuilder valuesSql = new StringBuilder();
        for (int i = 0; i < models.size(); i++) {
            if (i > 0) {
                valuesSql.append(", ");
            }
            // 字面量序位，避免再占位；model_id 用参数
            valuesSql.append("(?::bigint, ").append(i + 1).append(")");
            args.add(models.get(i));
        }
        boolean innerJoin = scope.kind() == ScopeKind.BY_MODELS || scope.restrictToModels();
        String joinType = innerJoin ? "INNER" : "LEFT";
        String from = " FROM " + entityTable + " e "
                + joinType + " JOIN (VALUES " + valuesSql + ") AS m(model_id, ord) ON e.model_id = m.model_id";

        StringBuilder where = new StringBuilder(" WHERE e.deleted = false");
        if (innerJoin) {
            // INNER JOIN 已限型号；显式 IN 与成组列表一致（防御）
            where.append(" AND e.model_id IN (");
            for (int i = 0; i < models.size(); i++) {
                if (i > 0) {
                    where.append(", ");
                }
                where.append("?");
                args.add(models.get(i));
            }
            where.append(")");
        }

        String normalizedDomain = EntityTypeScopeContext.normalizeDomain(domain);
        if (StringUtils.hasText(normalizedDomain)) {
            where.append(" AND e.domain = ?");
            args.add(normalizedDomain);
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

        if (scope.kind() == ScopeKind.UNCATEGORIZED) {
            appendUncategorizedPredicates(where, args, entityTypeCode.trim(), scope.categoryTypeCode().trim());
        } else if (scope.kind() == ScopeKind.BY_CATEGORY) {
            appendCategoryScopePredicates(where, args, entityTypeCode.trim(),
                    normalizeModelIds(scope.expandedCategoryIds()));
        }

        String fromWhere = from + where;
        return new QueryParts("SELECT e.id" + fromWhere, "SELECT COUNT(*)" + fromWhere, args, innerJoin);
    }

    private void appendUncategorizedPredicates(
            StringBuilder where, List<Object> args, String entityTypeCode, String categoryTypeCode) {
        String relationTable = TenantPhysicalTableNames.requirePhysical("dynamic_entity_category_relation");
        String linkTable = TenantPhysicalTableNames.requirePhysical("dynamic_category_entity_link");
        where.append("""
                 AND NOT EXISTS (
                    SELECT 1
                    FROM %s ecr
                    INNER JOIN dynamic_category c ON c.id = ecr.category_id AND c.deleted = false
                    WHERE ecr.deleted = false
                      AND ecr.entity_id = e.id
                      AND ecr.entity_type_code = ?
                      AND c.category_type_code = ?
                      AND UPPER(c.code) NOT LIKE '%%UNCATEGORIZED%%'
                )
                """.formatted(relationTable));
        args.add(entityTypeCode);
        args.add(categoryTypeCode);
        where.append("""
                 AND NOT EXISTS (
                    SELECT 1
                    FROM %s cel
                    INNER JOIN dynamic_category c2 ON c2.id = cel.category_id AND c2.deleted = false
                    WHERE cel.deleted = false
                      AND cel.entity_id = e.id
                      AND cel.entity_type_code = ?
                      AND c2.category_type_code = ?
                      AND UPPER(c2.code) NOT LIKE '%%UNCATEGORIZED%%'
                )
                """.formatted(linkTable));
        args.add(entityTypeCode);
        args.add(categoryTypeCode);
    }

    private void appendCategoryScopePredicates(
            StringBuilder where, List<Object> args, String entityTypeCode, List<Long> expandedCategoryIds) {
        String relationTable = TenantPhysicalTableNames.requirePhysical("dynamic_entity_category_relation");
        String linkTable = TenantPhysicalTableNames.requirePhysical("dynamic_category_entity_link");
        String inClause = placeholders(expandedCategoryIds.size());
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
        args.addAll(expandedCategoryIds);
        args.add(entityTypeCode);
        args.addAll(expandedCategoryIds);
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

    private static String requireSqlOrderColumn(String orderByColumn) {
        String column = resolveSqlOrderColumn(orderByColumn);
        if (column == null) {
            throw new IllegalArgumentException("型号成组 DB 排序不支持该字段: " + orderByColumn);
        }
        return column;
    }

    private static String resolveSqlOrderColumn(String orderByColumn) {
        if (!StringUtils.hasText(orderByColumn)) {
            return "name";
        }
        String column = orderByColumn.trim().toLowerCase(Locale.ROOT);
        if (!SQL_ORDER_COLUMNS.contains(column)) {
            return null;
        }
        return column;
    }

    private static List<Long> normalizeModelIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return ids.stream().filter(Objects::nonNull).filter(id -> id > 0).distinct().toList();
    }

    private record QueryParts(String selectSql, String countSql, List<Object> args, boolean innerJoin) {
    }
}
