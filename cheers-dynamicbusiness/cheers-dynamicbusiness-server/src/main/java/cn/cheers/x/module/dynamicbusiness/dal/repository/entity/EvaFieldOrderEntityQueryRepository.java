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

/**
 * 扩展字段（EVA 索引表）库内排序分页：实体表 LEFT JOIN {@code dynamic_entity_field_index}。
 *
 * <p><b>非列表入口</b>：数据管理实体列表禁止按扩展字段排序（扩展字段不进列表）；
 * 本仓储保留供将来非列表场景复用，勿当作列表热路径。</p>
 *
 * <p>仅覆盖已同步到索引表的可搜索/可排序扩展字段；缺索引行按 NULLS LAST，
 * 与「再扫 customFields 补缺」的内存序可能不同——缺索引应修同步，不在此静默双源。</p>
 */
@Repository
@RequiredArgsConstructor
public class EvaFieldOrderEntityQueryRepository {

    private static final Set<String> VALUE_COLUMNS = Set.of(
            "value_string", "value_number", "value_date", "value_datetime", "value_boolean");

    private final JdbcTemplate jdbcTemplate;
    private final EntityTableNameHandler entityTableNameHandler;

    /**
     * @param indexValueColumn 索引值列名（白名单）
     */
    public PageResult<Long> pageEntityIdsByEvaOrder(
            String entityTypeCode,
            List<Long> modelIds,
            String domain,
            String fieldCode,
            String indexValueColumn,
            boolean orderAsc,
            String keyword,
            int pageNo,
            int pageSize) {
        if (!StringUtils.hasText(entityTypeCode) || !StringUtils.hasText(fieldCode)
                || !VALUE_COLUMNS.contains(normalizeValueColumn(indexValueColumn))) {
            return new PageResult<>(List.of(), 0L);
        }
        String valueCol = normalizeValueColumn(indexValueColumn);
        String entityTable = entityTableNameHandler.resolvePhysicalTableName(entityTypeCode.trim());
        String indexTable = TenantPhysicalTableNames.requirePhysical("dynamic_entity_field_index");

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

        if (StringUtils.hasText(keyword)) {
            where.append(" AND e.name ILIKE ?");
            args.add("%" + keyword.trim() + "%");
        }

        // JOIN 条件用独立参数
        List<Object> joinArgs = new ArrayList<>();
        joinArgs.add(fieldCode.trim());

        String from = " FROM " + entityTable + " e"
                + " LEFT JOIN " + indexTable + " i"
                + " ON i.deleted = false AND i.entity_id = e.id AND i.field_code = ?";

        // args 顺序：JOIN 参数在 FROM 之后、WHERE 之前——JDBC 按出现序绑定
        List<Object> allArgs = new ArrayList<>(joinArgs);
        allArgs.addAll(args);

        String fromWhere = from + where;
        String countSql = "SELECT COUNT(*)" + fromWhere;
        String selectSql = "SELECT e.id" + fromWhere;

        Long total = jdbcTemplate.queryForObject(countSql, Long.class, allArgs.toArray());
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
        String pageSql = selectSql
                + " ORDER BY i." + valueCol + " " + dir + " NULLS LAST, e.id ASC"
                + " LIMIT ? OFFSET ?";
        List<Object> pageArgs = new ArrayList<>(allArgs);
        pageArgs.add(ps);
        pageArgs.add(offset);
        List<Long> ids = jdbcTemplate.query(pageSql, (rs, rowNum) -> rs.getLong(1), pageArgs.toArray());
        return new PageResult<>(ids != null ? ids : List.of(), totalCount);
    }

    private static String normalizeValueColumn(String indexValueColumn) {
        if (!StringUtils.hasText(indexValueColumn)) {
            return null;
        }
        return indexValueColumn.trim().toLowerCase(Locale.ROOT);
    }

    private static List<Long> normalizeModelIds(List<Long> modelIds) {
        if (modelIds == null || modelIds.isEmpty()) {
            return List.of();
        }
        return modelIds.stream().filter(id -> id != null && id > 0).distinct().toList();
    }
}
