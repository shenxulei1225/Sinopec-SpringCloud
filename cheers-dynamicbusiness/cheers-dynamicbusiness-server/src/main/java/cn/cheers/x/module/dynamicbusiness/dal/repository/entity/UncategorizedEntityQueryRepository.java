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
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 数据管理「未分类」：实体表 LEFT ANTI JOIN（NOT EXISTS relation ∪ link）+ DB 侧排序分页。
 *
 * <p>语义与旧内存差集一致：类型范围内实体 − 已挂当前 {@code categoryTypeCode} 任一非保留桶节点。</p>
 */
@Repository
@RequiredArgsConstructor
public class UncategorizedEntityQueryRepository {

    private static final Set<String> SQL_ORDER_COLUMNS = Set.of("name", "code", "status", "id", "sort");
    /** 调用方已校验的专用表物理列（字段编码规范化后） */
    private static final Pattern SAFE_PHYSICAL_COLUMN = Pattern.compile("^[a-z][a-z0-9_]*$");

    private final JdbcTemplate jdbcTemplate;
    private final EntityTableNameHandler entityTableNameHandler;

    /**
     * 无 keyword/filters 时的快路径：COUNT + ORDER BY + LIMIT/OFFSET，只回本页 id。
     */
    public PageResult<Long> pageUncategorizedEntityIds(
            String categoryTypeCode,
            String entityTypeCode,
            List<Long> modelIds,
            String domain,
            String scopeRegistryCode,
            String orderByColumn,
            boolean orderAsc,
            int pageNo,
            int pageSize) {
        if (!StringUtils.hasText(categoryTypeCode) || !StringUtils.hasText(entityTypeCode)) {
            return new PageResult<>(List.of(), 0L);
        }
        String sqlOrderColumn = resolveSqlOrderColumn(orderByColumn);
        if (sqlOrderColumn == null) {
            throw new IllegalArgumentException("未分类 DB 分页不支持按该字段排序: " + orderByColumn);
        }

        QueryParts parts = buildQueryParts(
                categoryTypeCode.trim(), entityTypeCode.trim(), modelIds, domain, scopeRegistryCode);

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

    /**
     * 有 keyword/filters 或非核心排序列：只取未分类 id（不装整行），再交上层过滤/排序。
     */
    public List<Long> listUncategorizedEntityIds(
            String categoryTypeCode,
            String entityTypeCode,
            List<Long> modelIds,
            String domain,
            String scopeRegistryCode) {
        if (!StringUtils.hasText(categoryTypeCode) || !StringUtils.hasText(entityTypeCode)) {
            return List.of();
        }
        QueryParts parts = buildQueryParts(
                categoryTypeCode.trim(), entityTypeCode.trim(), modelIds, domain, scopeRegistryCode);
        String sql = parts.selectSql + " ORDER BY e.id ASC";
        List<Long> ids = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong(1), parts.args.toArray());
        return ids != null ? ids : List.of();
    }

    /**
     * 核心列 / sort / 已白名单物理列可下推 SQL。
     * 非核心物理列须由调用方先校验为可排序基础字段，再传入规范化列名。
     */
    public boolean supportsSqlOrder(String orderByColumn) {
        return resolveSqlOrderColumn(orderByColumn) != null;
    }

    private static String resolveSqlOrderColumn(String orderByColumn) {
        if (!StringUtils.hasText(orderByColumn)) {
            // 与 query-by-scene PAGE 默认 name 对齐：空列时按 name
            return "name";
        }
        String column = orderByColumn.trim().toLowerCase(Locale.ROOT);
        if (SQL_ORDER_COLUMNS.contains(column)) {
            return column;
        }
        // 专用表基础字段列（调用方已校验可排序）
        if (SAFE_PHYSICAL_COLUMN.matcher(column).matches()) {
            return column;
        }
        return null;
    }

    private QueryParts buildQueryParts(
            String categoryTypeCode,
            String entityTypeCode,
            List<Long> modelIds,
            String domain,
            String scopeRegistryCode) {
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

        List<Long> normalizedModelIds = normalizeModelIds(modelIds);
        if (!normalizedModelIds.isEmpty()) {
            where.append(" AND e.model_id IN (");
            for (int i = 0; i < normalizedModelIds.size(); i++) {
                if (i > 0) {
                    where.append(", ");
                }
                where.append("?");
                args.add(normalizedModelIds.get(i));
            }
            where.append(")");
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

        // relation 已挂（保留桶不算已分类）
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

        // link 已挂
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

        String fromWhere = " FROM " + entityTable + " e" + where;
        return new QueryParts(
                "SELECT e.id" + fromWhere,
                "SELECT COUNT(*)" + fromWhere,
                args);
    }

    private static List<Long> normalizeModelIds(List<Long> modelIds) {
        if (modelIds == null || modelIds.isEmpty()) {
            return List.of();
        }
        return modelIds.stream().filter(id -> id != null && id > 0).distinct().toList();
    }

    private record QueryParts(String selectSql, String countSql, List<Object> args) {
    }
}
