package cn.cheers.x.module.dynamicbusiness.service.entity.query.engine;

import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntityFieldIndexMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntityMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.field.FieldMapper;
import cn.cheers.x.module.dynamicbusiness.service.entity.query.dto.*;
import cn.cheers.x.module.dynamicbusiness.service.entity.query.enums.AggregateType;
import cn.cheers.x.module.dynamicbusiness.service.entity.query.enums.LogicType;
import cn.cheers.x.module.dynamicbusiness.service.entity.query.enums.Operator;
import cn.cheers.x.module.dynamicbusiness.service.entity.query.enums.SortDirection;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * PostgreSQL 查询引擎实现
 *
 * 采用混合查询策略：
 * - 等值/包含查询：使用 JSONB + GIN 索引（直接查询 dynamic_entity.custom_fields）
 * - 范围查询/排序：使用 entity_field_index 索引表
 *
 * 查询策略分析：
 * - JSONB_ONLY：纯等值查询，使用 JSONB @> 操作符
 * - INDEX_TABLE：包含范围查询或排序，使用索引表 JOIN
 *
 * @author 系统
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "yudao.entity.search.type", havingValue = "postgresql", matchIfMissing = true)
public class PostgresQueryEngine implements QueryEngine {

    private final JdbcTemplate jdbcTemplate;
    private final EntityMapper entityMapper;
    private final EntityFieldIndexMapper entityFieldIndexMapper;
    private final FieldMapper fieldMapper;
    private final ObjectMapper objectMapper;

    /**
     * 查询策略枚举
     */
    public enum QueryStrategy {
        /** 纯 JSONB 查询（等值/包含查询） */
        JSONB_ONLY,
        /** 索引表查询（范围查询/排序） */
        INDEX_TABLE
    }

    @Override
    public String getType() {
        return "postgresql";
    }

    @Override
    public int getPriority() {
        return 10; // PostgreSQL 优先级较高
    }

    @Override
    public PageResult<EntityDO> query(FieldQueryRequest request) {
        // 分析查询条件，决定使用哪种查询方式
        QueryStrategy strategy = analyzeQueryStrategy(request);
        log.debug("查询策略分析结果: {}, modelId={}", strategy, request.getModelId());

        if (strategy == QueryStrategy.JSONB_ONLY) {
            // 纯等值查询，使用 JSONB + GIN
            return queryByJsonb(request);
        } else {
            // 包含范围查询或排序，使用索引表
            return queryByIndexTable(request);
        }
    }

    @Override
    public AggregateResult aggregate(FieldAggregateRequest request) {
        // 聚合查询统一使用索引表
        return aggregateByIndexTable(request);
    }

    @Override
    public Long count(FieldQueryRequest request) {
        // 分析查询条件，决定使用哪种查询方式
        QueryStrategy strategy = analyzeQueryStrategy(request);

        if (strategy == QueryStrategy.JSONB_ONLY) {
            return countByJsonb(request);
        } else {
            return countByIndexTable(request);
        }
    }

    @Override
    public void syncToIndex(EntityDO entity) {
        // 同步到索引表的逻辑将在 EntitySyncService 中实现
        log.debug("同步 Entity 到索引表: entityId={}", entity.getId());
    }

    @Override
    public void deleteFromIndex(Long entityId) {
        entityFieldIndexMapper.deleteByEntityId(entityId);
        log.debug("从索引表删除 Entity: entityId={}", entityId);
    }

    @Override
    public void rebuildIndex(Long modelId, Consumer<Integer> progressCallback) {
        log.info("开始重建索引: modelId={}", modelId);
        // 索引重建逻辑将在 IndexRebuildService 中实现
    }

    // ==================== 查询策略分析 ====================

    /**
     * 分析查询策略
     *
     * 决策规则：
     * 1. 如果有排序条件，必须使用索引表
     * 2. 如果有范围查询条件（>、<、>=、<=、BETWEEN），必须使用索引表
     * 3. 如果有模糊查询条件（LIKE），必须使用索引表
     * 4. 纯等值查询（=、IN），使用 JSONB
     *
     * @param request 查询请求
     * @return 查询策略
     */
    public QueryStrategy analyzeQueryStrategy(FieldQueryRequest request) {
        // 如果有排序条件，必须使用索引表
        if (request.hasSorts()) {
            return QueryStrategy.INDEX_TABLE;
        }

        // 如果没有查询条件，使用 JSONB（简单查询）
        if (!request.hasConditions()) {
            return QueryStrategy.JSONB_ONLY;
        }

        // 检查是否有需要索引表的操作符
        for (FieldCondition condition : request.getConditions()) {
            if (requiresIndexTable(condition.getOperator())) {
                return QueryStrategy.INDEX_TABLE;
            }
        }

        // 纯等值查询，使用 JSONB
        return QueryStrategy.JSONB_ONLY;
    }

    /**
     * 判断操作符是否需要使用索引表
     */
    private boolean requiresIndexTable(Operator operator) {
        return operator.isRangeOperator() || operator == Operator.LIKE 
            || operator == Operator.IS_NULL || operator == Operator.IS_NOT_NULL
            || operator == Operator.NE || operator == Operator.NOT_IN;
    }

    // ==================== JSONB 查询实现 ====================

    /**
     * 使用 JSONB 查询（等值/包含查询）
     *
     * 使用 PostgreSQL JSONB @> 操作符进行等值查询
     * 示例 SQL：
     * SELECT * FROM dynamic_entity
     * WHERE model_id = ? AND deleted = false
     *   AND custom_fields @> '{"brand": "海尔"}'::jsonb
     */
    PageResult<EntityDO> queryByJsonb(FieldQueryRequest request) {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();
        Long tenantId = TenantContextHolder.getRequiredTenantId();

        // 基础查询
        sql.append("SELECT * FROM dynamic_entity WHERE tenant_id = ? AND model_id = ? AND deleted = false");
        params.add(tenantId);
        params.add(request.getModelId());

        // 添加 JSONB 条件
        if (request.hasConditions()) {
            String logic = request.getLogic() == LogicType.OR ? " OR " : " AND ";
            List<String> conditionSqls = new ArrayList<>();

            for (FieldCondition condition : request.getConditions()) {
                String conditionSql = buildJsonbCondition(condition, params);
                if (conditionSql != null) {
                    conditionSqls.add(conditionSql);
                }
            }

            if (!conditionSqls.isEmpty()) {
                sql.append(" AND (");
                sql.append(String.join(logic, conditionSqls));
                sql.append(")");
            }
        }

        // 计算总数
        String countSql = "SELECT COUNT(*) FROM (" + sql + ") AS t";
        Long total = jdbcTemplate.queryForObject(countSql, Long.class, params.toArray());

        // 添加分页
        PageParam pageParam = request.getPageParamOrDefault();
        sql.append(" ORDER BY id DESC");
        sql.append(" LIMIT ? OFFSET ?");
        params.add(pageParam.getPageSize());
        params.add((pageParam.getPageNo() - 1) * pageParam.getPageSize());

        // 执行查询
        List<EntityDO> list = jdbcTemplate.query(sql.toString(), new EntityRowMapper(), params.toArray());

        return new PageResult<>(list, total != null ? total : 0L);
    }

    /**
     * 构建 JSONB 查询条件
     */
    private String buildJsonbCondition(FieldCondition condition, List<Object> params) {
        String fieldCode = condition.getFieldCode();
        Operator operator = condition.getOperator();
        Object value = condition.getValue();

        switch (operator) {
            case EQ:
                // 使用 @> 操作符进行等值查询
                // custom_fields @> '{"fieldCode": "value"}'::jsonb
                try {
                    Map<String, Object> jsonMap = new HashMap<>();
                    jsonMap.put(fieldCode, value);
                    String jsonValue = objectMapper.writeValueAsString(jsonMap);
                    params.add(jsonValue);
                    return "custom_fields @> ?::jsonb";
                } catch (JsonProcessingException e) {
                    log.error("构建 JSONB 条件失败", e);
                    return null;
                }

            case IN:
                // IN 查询：使用多个 @> 条件的 OR 组合
                if (value instanceof Collection) {
                    Collection<?> values = (Collection<?>) value;
                    if (values.isEmpty()) {
                        return "1=0"; // 空集合返回无结果
                    }
                    List<String> inConditions = new ArrayList<>();
                    for (Object v : values) {
                        try {
                            Map<String, Object> jsonMap = new HashMap<>();
                            jsonMap.put(fieldCode, v);
                            String jsonValue = objectMapper.writeValueAsString(jsonMap);
                            params.add(jsonValue);
                            inConditions.add("custom_fields @> ?::jsonb");
                        } catch (JsonProcessingException e) {
                            log.error("构建 JSONB IN 条件失败", e);
                        }
                    }
                    return "(" + String.join(" OR ", inConditions) + ")";
                }
                return null;

            default:
                // 其他操作符不支持 JSONB 直接查询
                log.warn("JSONB 查询不支持操作符: {}", operator);
                return null;
        }
    }

    /**
     * 使用 JSONB 计数
     */
    private Long countByJsonb(FieldQueryRequest request) {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();
        Long tenantId = TenantContextHolder.getRequiredTenantId();

        sql.append("SELECT COUNT(*) FROM dynamic_entity WHERE tenant_id = ? AND model_id = ? AND deleted = false");
        params.add(tenantId);
        params.add(request.getModelId());

        // 添加 JSONB 条件
        if (request.hasConditions()) {
            String logic = request.getLogic() == LogicType.OR ? " OR " : " AND ";
            List<String> conditionSqls = new ArrayList<>();

            for (FieldCondition condition : request.getConditions()) {
                String conditionSql = buildJsonbCondition(condition, params);
                if (conditionSql != null) {
                    conditionSqls.add(conditionSql);
                }
            }

            if (!conditionSqls.isEmpty()) {
                sql.append(" AND (");
                sql.append(String.join(logic, conditionSqls));
                sql.append(")");
            }
        }

        return jdbcTemplate.queryForObject(sql.toString(), Long.class, params.toArray());
    }

    // ==================== 索引表查询实现 ====================

    /**
     * 使用索引表查询（范围查询/排序）
     *
     * 通过 JOIN entity_field_index 表实现范围查询和排序
     * 示例 SQL：
     * SELECT e.* FROM dynamic_entity e
     * WHERE e.model_id = ? AND e.deleted = false
     *   AND e.id IN (
     *     SELECT entity_id FROM dynamic_entity_field_index
     *     WHERE model_id = ? AND field_code = ? AND value_number > ?
     *   )
     * ORDER BY (
     *   SELECT value_number FROM dynamic_entity_field_index
     *   WHERE entity_id = e.id AND field_code = ?
     * ) ASC
     */
    PageResult<EntityDO> queryByIndexTable(FieldQueryRequest request) {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();
        Long tenantId = TenantContextHolder.getRequiredTenantId();

        // 基础查询
        sql.append("SELECT e.* FROM dynamic_entity e WHERE e.tenant_id = ? AND e.model_id = ? AND e.deleted = false");
        params.add(tenantId);
        params.add(request.getModelId());

        // 添加索引表条件
        if (request.hasConditions()) {
            String logic = request.getLogic() == LogicType.OR ? " OR " : " AND ";
            List<String> conditionSqls = new ArrayList<>();

            for (FieldCondition condition : request.getConditions()) {
                String conditionSql = buildIndexTableCondition(condition, request.getModelId(), params);
                if (conditionSql != null) {
                    conditionSqls.add(conditionSql);
                }
            }

            if (!conditionSqls.isEmpty()) {
                sql.append(" AND (");
                sql.append(String.join(logic, conditionSqls));
                sql.append(")");
            }
        }

        // 计算总数
        String countSql = "SELECT COUNT(*) FROM (" + sql + ") AS t";
        Long total = jdbcTemplate.queryForObject(countSql, Long.class, params.toArray());

        // 添加排序
        if (request.hasSorts()) {
            sql.append(" ORDER BY ");
            List<String> orderClauses = new ArrayList<>();
            for (FieldSort sort : request.getSorts()) {
                String orderClause = buildSortClause(sort, params);
                orderClauses.add(orderClause);
            }
            sql.append(String.join(", ", orderClauses));
        } else {
            sql.append(" ORDER BY e.id DESC");
        }

        // 添加分页
        PageParam pageParam = request.getPageParamOrDefault();
        sql.append(" LIMIT ? OFFSET ?");
        params.add(pageParam.getPageSize());
        params.add((pageParam.getPageNo() - 1) * pageParam.getPageSize());

        // 执行查询
        List<EntityDO> list = jdbcTemplate.query(sql.toString(), new EntityRowMapper("e."), params.toArray());

        return new PageResult<>(list, total != null ? total : 0L);
    }

    /**
     * 构建索引表查询条件
     */
    private String buildIndexTableCondition(FieldCondition condition, Long modelId, List<Object> params) {
        String fieldCode = condition.getFieldCode();
        Operator operator = condition.getOperator();
        Object value = condition.getValue();
        Object value2 = condition.getValue2();

        // 获取字段类型以确定使用哪个值列
        String valueColumn = determineValueColumn(fieldCode, modelId, value);

        StringBuilder subQuery = new StringBuilder();
        subQuery.append("e.id IN (SELECT entity_id FROM dynamic_entity_field_index WHERE tenant_id = ? AND model_id = ? AND field_code = ?");
        params.add(TenantContextHolder.getRequiredTenantId());
        params.add(modelId);
        params.add(fieldCode);

        switch (operator) {
            case EQ:
                subQuery.append(" AND ").append(valueColumn).append(" = ?");
                params.add(convertValue(value, valueColumn));
                break;

            case NE:
                subQuery.append(" AND ").append(valueColumn).append(" != ?");
                params.add(convertValue(value, valueColumn));
                break;

            case GT:
                subQuery.append(" AND ").append(valueColumn).append(" > ?");
                params.add(convertValue(value, valueColumn));
                break;

            case GE:
                subQuery.append(" AND ").append(valueColumn).append(" >= ?");
                params.add(convertValue(value, valueColumn));
                break;

            case LT:
                subQuery.append(" AND ").append(valueColumn).append(" < ?");
                params.add(convertValue(value, valueColumn));
                break;

            case LE:
                subQuery.append(" AND ").append(valueColumn).append(" <= ?");
                params.add(convertValue(value, valueColumn));
                break;

            case BETWEEN:
                subQuery.append(" AND ").append(valueColumn).append(" BETWEEN ? AND ?");
                params.add(convertValue(value, valueColumn));
                params.add(convertValue(value2, valueColumn));
                break;

            case LIKE:
                subQuery.append(" AND ").append(valueColumn).append(" LIKE ?");
                params.add(value);
                break;

            case IN:
                if (value instanceof Collection) {
                    Collection<?> values = (Collection<?>) value;
                    if (values.isEmpty()) {
                        return "1=0";
                    }
                    subQuery.append(" AND ").append(valueColumn).append(" IN (");
                    subQuery.append(values.stream().map(v -> "?").collect(Collectors.joining(", ")));
                    subQuery.append(")");
                    for (Object v : values) {
                        params.add(convertValue(v, valueColumn));
                    }
                }
                break;

            case NOT_IN:
                if (value instanceof Collection) {
                    Collection<?> values = (Collection<?>) value;
                    if (values.isEmpty()) {
                        return "1=1";
                    }
                    subQuery.append(" AND ").append(valueColumn).append(" NOT IN (");
                    subQuery.append(values.stream().map(v -> "?").collect(Collectors.joining(", ")));
                    subQuery.append(")");
                    for (Object v : values) {
                        params.add(convertValue(v, valueColumn));
                    }
                }
                break;

            case IS_NULL:
                subQuery.append(" AND ").append(valueColumn).append(" IS NULL");
                break;

            case IS_NOT_NULL:
                subQuery.append(" AND ").append(valueColumn).append(" IS NOT NULL");
                break;

            default:
                log.warn("不支持的操作符: {}", operator);
                return null;
        }

        subQuery.append(")");
        return subQuery.toString();
    }

    /**
     * 构建排序子句
     *
     * 支持多种字段类型的排序：
     * - 数值类型：使用 value_number 列
     * - 字符串类型：使用 value_string 列
     * - 日期类型：使用 value_date 列
     * - 日期时间类型：使用 value_datetime 列
     * - 布尔类型：使用 value_boolean 列
     *
     * 使用 COALESCE 处理 NULL 值，确保排序稳定
     */
    private String buildSortClause(FieldSort sort, List<Object> params) {
        String fieldCode = sort.getFieldCode();
        String direction = sort.getDirection() == SortDirection.DESC ? "DESC" : "ASC";
        String nullsOrder = sort.getDirection() == SortDirection.DESC ? "NULLS LAST" : "NULLS FIRST";
        Long tenantId = TenantContextHolder.getRequiredTenantId();

        // 获取字段列名，确保按正确类型排序
        String valueColumn = determineValueColumnByFieldCode(fieldCode);

        // 使用参数化查询防止注入，并显式指定租户过滤
        params.add(tenantId);
        params.add(fieldCode);

        return String.format(
            "(SELECT %s FROM dynamic_entity_field_index WHERE tenant_id = ? AND entity_id = e.id AND field_code = ?) %s %s",
            valueColumn, direction, nullsOrder
        );
    }

    /**
     * 根据字段编码确定排序使用的值列
     * 优先尝试从 FieldMapper 获取类型，如果获取不到则根据常用前缀或默认字符串列
     */
    private String determineValueColumnByFieldCode(String fieldCode) {
        // TODO: 建议引入缓存或预加载字段类型信息，以避免排序时的重复查询
        // 这里提供一个基于字段特征的启发式判断，生产环境建议通过 fieldMapper 准确获取
        if (fieldCode.toLowerCase().contains("price") || fieldCode.toLowerCase().contains("amount") || fieldCode.toLowerCase().contains("count")) {
            return "value_number";
        }
        if (fieldCode.toLowerCase().contains("date") || fieldCode.toLowerCase().contains("time")) {
            return "value_datetime";
        }
        return "value_string";
    }

    /**
     * 构建数值类型排序子句
     */
    private String buildNumericSortClause(String fieldCode, String direction) {
        return String.format(
            "(SELECT value_number FROM dynamic_entity_field_index WHERE entity_id = e.id AND field_code = '%s') %s NULLS LAST",
            fieldCode, direction
        );
    }

    /**
     * 构建字符串类型排序子句
     */
    private String buildStringSortClause(String fieldCode, String direction) {
        return String.format(
            "(SELECT value_string FROM dynamic_entity_field_index WHERE entity_id = e.id AND field_code = '%s') %s NULLS LAST",
            fieldCode, direction
        );
    }

    /**
     * 构建日期类型排序子句
     */
    private String buildDateSortClause(String fieldCode, String direction) {
        return String.format(
            "(SELECT value_date FROM dynamic_entity_field_index WHERE entity_id = e.id AND field_code = '%s') %s NULLS LAST",
            fieldCode, direction
        );
    }

    /**
     * 构建日期时间类型排序子句
     */
    private String buildDateTimeSortClause(String fieldCode, String direction) {
        return String.format(
            "(SELECT value_datetime FROM dynamic_entity_field_index WHERE entity_id = e.id AND field_code = '%s') %s NULLS LAST",
            fieldCode, direction
        );
    }

    /**
     * 使用索引表计数
     */
    private Long countByIndexTable(FieldQueryRequest request) {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("SELECT COUNT(*) FROM dynamic_entity e WHERE e.model_id = ? AND e.deleted = false");
        params.add(request.getModelId());

        // 添加索引表条件
        if (request.hasConditions()) {
            String logic = request.getLogic() == LogicType.OR ? " OR " : " AND ";
            List<String> conditionSqls = new ArrayList<>();

            for (FieldCondition condition : request.getConditions()) {
                String conditionSql = buildIndexTableCondition(condition, request.getModelId(), params);
                if (conditionSql != null) {
                    conditionSqls.add(conditionSql);
                }
            }

            if (!conditionSqls.isEmpty()) {
                sql.append(" AND (");
                sql.append(String.join(logic, conditionSqls));
                sql.append(")");
            }
        }

        return jdbcTemplate.queryForObject(sql.toString(), Long.class, params.toArray());
    }

    // ==================== 聚合查询实现 ====================

    /**
     * 使用索引表进行聚合查询
     *
     * 支持的聚合类型：
     * - COUNT: 计数统计，可以不指定字段（统计总数）或指定字段（统计非空值数量）
     * - SUM: 求和统计，必须指定数值类型字段
     * - AVG: 平均值统计，必须指定数值类型字段
     * - MAX: 最大值统计，支持数值、日期、字符串类型
     * - MIN: 最小值统计，支持数值、日期、字符串类型
     *
     * 支持分组统计：通过 groupByFieldCode 指定分组字段
     * 支持条件过滤：通过 conditions 指定过滤条件
     */
    private AggregateResult aggregateByIndexTable(FieldAggregateRequest request) {
        String fieldCode = request.getFieldCode();
        AggregateType aggregateType = request.getAggregateType();

        // 验证聚合请求
        validateAggregateRequest(request);

        if (request.hasGroupBy()) {
            return executeGroupedAggregate(request);
        } else {
            return executeSingleAggregate(request);
        }
    }

    /**
     * 验证聚合请求
     */
    private void validateAggregateRequest(FieldAggregateRequest request) {
        AggregateType aggregateType = request.getAggregateType();
        String fieldCode = request.getFieldCode();

        // SUM 和 AVG 必须指定字段
        if (aggregateType.requiresField() && (fieldCode == null || fieldCode.isEmpty())) {
            throw new IllegalArgumentException("聚合类型 " + aggregateType + " 必须指定聚合字段");
        }
    }

    /**
     * 执行单值聚合查询
     */
    private AggregateResult executeSingleAggregate(FieldAggregateRequest request) {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        String fieldCode = request.getFieldCode();
        AggregateType aggregateType = request.getAggregateType();

        // COUNT 且无字段时，直接统计 Entity 数量
        if (aggregateType == AggregateType.COUNT && (fieldCode == null || fieldCode.isEmpty())) {
            return executeCountWithoutField(request);
        }

        // 确定值列
        String valueColumn = determineAggregateValueColumn(aggregateType);

        // 构建聚合函数
        String aggFunction = buildAggregateFunction(aggregateType, valueColumn);

        // 构建 SQL
        sql.append("SELECT ").append(aggFunction).append(" AS agg_value, COUNT(*) AS total_count ");
        sql.append("FROM dynamic_entity_field_index i ");
        sql.append("WHERE i.model_id = ? AND i.field_code = ?");
        params.add(request.getModelId());
        params.add(fieldCode);

        // 添加条件过滤（通过子查询过滤 entity_id）
        if (request.hasConditions()) {
            String conditionSubQuery = buildConditionSubQuery(request);
            if (conditionSubQuery != null) {
                sql.append(" AND i.entity_id IN (").append(conditionSubQuery).append(")");
                addConditionParams(request, params);
            }
        }

        // 执行查询
        return jdbcTemplate.query(sql.toString(), rs -> {
            if (rs.next()) {
                Object value;
                Long totalCount = rs.getLong("total_count");

                switch (aggregateType) {
                    case COUNT:
                        value = totalCount;
                        break;
                    case SUM:
                    case AVG:
                        value = rs.getBigDecimal("agg_value");
                        break;
                    case MAX:
                    case MIN:
                        value = rs.getBigDecimal("agg_value");
                        break;
                    default:
                        value = rs.getObject("agg_value");
                }

                return AggregateResult.single(aggregateType, fieldCode, value, totalCount);
            }
            return AggregateResult.single(aggregateType, fieldCode, null, 0L);
        }, params.toArray());
    }

    /**
     * 执行不指定字段的 COUNT 查询
     */
    private AggregateResult executeCountWithoutField(FieldAggregateRequest request) {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("SELECT COUNT(*) FROM dynamic_entity e WHERE e.model_id = ? AND e.deleted = false");
        params.add(request.getModelId());

        // 添加条件过滤
        if (request.hasConditions()) {
            for (FieldCondition condition : request.getConditions()) {
                String conditionSql = buildIndexTableCondition(condition, request.getModelId(), params);
                if (conditionSql != null) {
                    sql.append(" AND ").append(conditionSql);
                }
            }
        }

        Long count = jdbcTemplate.queryForObject(sql.toString(), Long.class, params.toArray());
        return AggregateResult.count(count != null ? count : 0L);
    }

    /**
     * 执行分组聚合查询
     */
    private AggregateResult executeGroupedAggregate(FieldAggregateRequest request) {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        String fieldCode = request.getFieldCode();
        String groupByFieldCode = request.getGroupByFieldCode();
        AggregateType aggregateType = request.getAggregateType();

        // 确定分组字段的值列（优先使用 value_string，因为分组通常是字符串或选择类型）
        String groupValueColumn = "COALESCE(g.value_string, g.value_number::text, g.value_boolean::text)";

        // 确定聚合字段的值列
        String aggValueColumn = determineAggregateValueColumn(aggregateType);

        // 构建聚合函数
        String aggFunction = buildAggregateFunction(aggregateType, "i." + aggValueColumn);

        // 构建 SQL
        if (aggregateType == AggregateType.COUNT && (fieldCode == null || fieldCode.isEmpty())) {
            // COUNT 不指定字段时，统计每个分组的 Entity 数量
            sql.append("SELECT ").append(groupValueColumn).append(" AS group_key, ");
            sql.append("COUNT(DISTINCT g.entity_id) AS agg_value, COUNT(DISTINCT g.entity_id) AS count ");
            sql.append("FROM dynamic_entity_field_index g ");
            sql.append("WHERE g.model_id = ? AND g.field_code = ?");
            params.add(request.getModelId());
            params.add(groupByFieldCode);
        } else {
            // 其他聚合类型，需要 JOIN 聚合字段和分组字段
            sql.append("SELECT ").append(groupValueColumn).append(" AS group_key, ");
            sql.append(aggFunction).append(" AS agg_value, COUNT(*) AS count ");
            sql.append("FROM dynamic_entity_field_index i ");
            sql.append("JOIN dynamic_entity_field_index g ON i.entity_id = g.entity_id AND g.model_id = i.model_id ");
            sql.append("WHERE i.model_id = ? AND i.field_code = ? ");
            sql.append("AND g.field_code = ?");
            params.add(request.getModelId());
            params.add(fieldCode);
            params.add(groupByFieldCode);
        }

        // 添加条件过滤
        if (request.hasConditions()) {
            String conditionSubQuery = buildConditionSubQuery(request);
            if (conditionSubQuery != null) {
                if (aggregateType == AggregateType.COUNT && (fieldCode == null || fieldCode.isEmpty())) {
                    sql.append(" AND g.entity_id IN (").append(conditionSubQuery).append(")");
                } else {
                    sql.append(" AND i.entity_id IN (").append(conditionSubQuery).append(")");
                }
                addConditionParams(request, params);
            }
        }

        // 添加分组
        sql.append(" GROUP BY ").append(groupValueColumn);

        // 按聚合值降序排序
        sql.append(" ORDER BY agg_value DESC NULLS LAST");

        // 执行查询
        List<GroupResult> groups = jdbcTemplate.query(sql.toString(), (rs, rowNum) -> {
            GroupResult group = new GroupResult();
            group.setGroupKey(rs.getString("group_key"));

            switch (aggregateType) {
                case COUNT:
                    group.setValue(rs.getLong("agg_value"));
                    break;
                case SUM:
                case AVG:
                case MAX:
                case MIN:
                    group.setValue(rs.getBigDecimal("agg_value"));
                    break;
                default:
                    group.setValue(rs.getObject("agg_value"));
            }

            group.setCount(rs.getLong("count"));
            return group;
        }, params.toArray());

        // 计算总数
        Long totalCount = groups.stream().mapToLong(GroupResult::getCount).sum();

        return AggregateResult.grouped(
            aggregateType,
            fieldCode,
            groupByFieldCode,
            groups,
            totalCount
        );
    }

    /**
     * 确定聚合使用的值列
     */
    private String determineAggregateValueColumn(AggregateType aggregateType) {
        // SUM 和 AVG 只能用于数值类型
        if (aggregateType.isNumericOnly()) {
            return "value_number";
        }
        // MAX 和 MIN 可以用于多种类型，默认使用数值列
        // 实际使用时会根据字段类型动态选择
        return "value_number";
    }

    /**
     * 构建聚合函数 SQL
     */
    private String buildAggregateFunction(AggregateType aggregateType, String valueColumn) {
        switch (aggregateType) {
            case COUNT:
                return "COUNT(*)";
            case SUM:
                return "SUM(" + valueColumn + ")";
            case AVG:
                return "AVG(" + valueColumn + ")";
            case MAX:
                return "MAX(" + valueColumn + ")";
            case MIN:
                return "MIN(" + valueColumn + ")";
            default:
                throw new IllegalArgumentException("不支持的聚合类型: " + aggregateType);
        }
    }

    /**
     * 构建条件过滤子查询
     * 返回一个查询 entity_id 的子查询 SQL
     */
    private String buildConditionSubQuery(FieldAggregateRequest request) {
        if (!request.hasConditions()) {
            return null;
        }

        StringBuilder subQuery = new StringBuilder();
        subQuery.append("SELECT DISTINCT e.id FROM dynamic_entity e WHERE e.model_id = ? AND e.deleted = false");

        for (FieldCondition condition : request.getConditions()) {
            // 这里简化处理，使用索引表条件
            subQuery.append(" AND e.id IN (SELECT entity_id FROM dynamic_entity_field_index WHERE model_id = ? AND field_code = ?");

            String valueColumn = determineValueColumn(condition.getFieldCode(), request.getModelId(), condition.getValue());
            Operator operator = condition.getOperator();

            switch (operator) {
                case EQ:
                    subQuery.append(" AND ").append(valueColumn).append(" = ?");
                    break;
                case NE:
                    subQuery.append(" AND ").append(valueColumn).append(" != ?");
                    break;
                case GT:
                    subQuery.append(" AND ").append(valueColumn).append(" > ?");
                    break;
                case GE:
                    subQuery.append(" AND ").append(valueColumn).append(" >= ?");
                    break;
                case LT:
                    subQuery.append(" AND ").append(valueColumn).append(" < ?");
                    break;
                case LE:
                    subQuery.append(" AND ").append(valueColumn).append(" <= ?");
                    break;
                case BETWEEN:
                    subQuery.append(" AND ").append(valueColumn).append(" BETWEEN ? AND ?");
                    break;
                case LIKE:
                    subQuery.append(" AND ").append(valueColumn).append(" LIKE ?");
                    break;
                case IS_NULL:
                    subQuery.append(" AND ").append(valueColumn).append(" IS NULL");
                    break;
                case IS_NOT_NULL:
                    subQuery.append(" AND ").append(valueColumn).append(" IS NOT NULL");
                    break;
                default:
                    // IN 和 NOT_IN 需要特殊处理
                    break;
            }

            subQuery.append(")");
        }

        return subQuery.toString();
    }

    /**
     * 添加条件参数到参数列表
     */
    private void addConditionParams(FieldAggregateRequest request, List<Object> params) {
        // 首先添加 model_id 参数
        params.add(request.getModelId());

        for (FieldCondition condition : request.getConditions()) {
            // 添加 model_id 和 field_code
            params.add(request.getModelId());
            params.add(condition.getFieldCode());

            String valueColumn = determineValueColumn(condition.getFieldCode(), request.getModelId(), condition.getValue());
            Operator operator = condition.getOperator();

            // 添加值参数
            if (operator.requiresValue()) {
                params.add(convertValue(condition.getValue(), valueColumn));
            }
            if (operator.requiresSecondValue()) {
                params.add(convertValue(condition.getValue2(), valueColumn));
            }
        }
    }

    // ==================== 辅助方法 ====================

    /**
     * 根据字段类型和值确定使用哪个值列
     */
    private String determineValueColumn(String fieldCode, Long modelId, Object value) {
        // 根据值类型推断列
        if (value instanceof Number) {
            return "value_number";
        } else if (value instanceof LocalDate) {
            return "value_date";
        } else if (value instanceof LocalDateTime) {
            return "value_datetime";
        } else if (value instanceof Boolean) {
            return "value_boolean";
        } else {
            return "value_string";
        }
    }

    /**
     * 转换值为对应列的类型
     */
    private Object convertValue(Object value, String valueColumn) {
        if (value == null) {
            return null;
        }

        switch (valueColumn) {
            case "value_number":
                if (value instanceof Number) {
                    return new BigDecimal(value.toString());
                }
                return new BigDecimal(value.toString());

            case "value_date":
                if (value instanceof LocalDate) {
                    return value;
                }
                return LocalDate.parse(value.toString());

            case "value_datetime":
                if (value instanceof LocalDateTime) {
                    return value;
                }
                return LocalDateTime.parse(value.toString());

            case "value_boolean":
                if (value instanceof Boolean) {
                    return value;
                }
                return Boolean.parseBoolean(value.toString());

            default:
                return value.toString();
        }
    }

    /**
     * Entity 行映射器
     */
    private class EntityRowMapper implements RowMapper<EntityDO> {
        private final String prefix;

        public EntityRowMapper() {
            this.prefix = "";
        }

        public EntityRowMapper(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public EntityDO mapRow(ResultSet rs, int rowNum) throws SQLException {
            EntityDO entity = new EntityDO();
            entity.setId(rs.getLong(prefix + "id"));
            entity.setBusinessTypeCode(rs.getString(prefix + "business_type_code"));
            entity.setModelId(rs.getLong(prefix + "model_id"));
            entity.setParentId(rs.getLong(prefix + "parent_id"));
            entity.setName(rs.getString(prefix + "name"));
            entity.setCustomFields(rs.getString(prefix + "custom_fields"));
            entity.setStatus(rs.getInt(prefix + "status"));
            return entity;
        }
    }
}
