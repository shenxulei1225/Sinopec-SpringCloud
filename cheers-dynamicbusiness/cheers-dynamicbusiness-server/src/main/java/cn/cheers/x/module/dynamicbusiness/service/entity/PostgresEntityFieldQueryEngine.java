package cn.cheers.x.module.dynamicbusiness.service.entity;

import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.FieldFilterReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityFieldIndexDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntityFieldIndexMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.field.FieldMapper;
import cn.cheers.x.module.dynamicbusiness.service.entity.core.EntityCoreService;
import cn.cheers.x.module.dynamicbusiness.service.entity.index.FieldIndexService;
import cn.cheers.x.module.dynamicbusiness.service.entity.relation.EntityRelationService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Postgres 实体字段查询引擎：
 * - keyword：索引表 value_string + name 双通道
 * - filter：relation 走关系表，non-relation 走索引表类型列
 */
@Service
public class PostgresEntityFieldQueryEngine implements EntityFieldQueryEngine {

    @Resource
    private EntityFieldIndexMapper entityFieldIndexMapper;

    @Resource
    private EntityCoreService entityCoreService;

    @Resource
    private EntityRelationService entityRelationService;

    @Resource
    private FieldMapper fieldMapper;

    @Resource
    private FieldIndexService fieldIndexService;

    /**
     * 全局关键词搜索（keyword）。
     *
     * <p>语义说明：</p>
     * <ul>
     *   <li>keyword 是“全局检索入口”，不限定单一 fieldCode；</li>
     *   <li>当前实现覆盖：索引表 value_string（仅可搜索字段）+ 实体 name；</li>
     *   <li>与字段级 CONTAINS 的区别：字段级 CONTAINS 是高级筛选，必须指定 fieldCode。</li>
     * </ul>
     */
    @Override
    public Set<Long> searchEntityIdsByKeyword(String entityTypeCode, String keyword, List<Long> candidateEntityIds) {
        return searchEntityIdsByKeyword(entityTypeCode, keyword, candidateEntityIds, null);
    }

    @Override
    public Set<Long> searchEntityIdsByKeyword(String entityTypeCode, String keyword,
                                              List<Long> candidateEntityIds, List<String> searchFieldCodes) {
        if (entityTypeCode == null || entityTypeCode.isBlank()) {
            return Collections.emptySet();
        }
        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptySet();
        }
        if (candidateEntityIds == null || candidateEntityIds.isEmpty()) {
            return Collections.emptySet();
        }

        String k = keyword.trim().toLowerCase(Locale.ROOT);
        Set<Long> candidateSet = new HashSet<>(candidateEntityIds);
        Set<Long> matched = new HashSet<>();
        Set<String> allowed = normalizeSearchFieldCodes(searchFieldCodes);
        boolean restrict = allowed != null;

        // 索引命中必须再校验该行 model+field 仍可搜索，避免不可搜索字段残留索引被 keyword 命中
        if (!restrict || !allowed.isEmpty()) {
            List<EntityFieldIndexDO> indexHits = entityFieldIndexMapper.selectRowsByKeyword(k);
            if (indexHits != null) {
                for (EntityFieldIndexDO row : indexHits) {
                    if (row == null || row.getEntityId() == null || !candidateSet.contains(row.getEntityId())) {
                        continue;
                    }
                    if (restrict && (row.getFieldCode() == null || !allowed.contains(row.getFieldCode()))) {
                        continue;
                    }
                    if (fieldIndexService.isFieldSearchable(row.getModelId(), row.getFieldCode())) {
                        matched.add(row.getEntityId());
                    }
                }
            }
        }

        List<EntityDO> entities = entityCoreService.listByIds(candidateEntityIds, entityTypeCode);
        if (entities != null) {
            for (EntityDO e : entities) {
                if (e == null || e.getId() == null) {
                    continue;
                }
                if (matchesCoreKeyword(e, k, allowed, restrict)) {
                    matched.add(e.getId());
                }
            }
        }
        return matched;
    }

    private static Set<String> normalizeSearchFieldCodes(List<String> searchFieldCodes) {
        if (searchFieldCodes == null || searchFieldCodes.isEmpty()) {
            return null;
        }
        Set<String> out = new HashSet<>();
        for (String raw : searchFieldCodes) {
            if (raw == null || raw.isBlank()) {
                continue;
            }
            out.add(raw.trim());
            out.add(raw.trim().toLowerCase(Locale.ROOT));
        }
        return out.isEmpty() ? null : out;
    }

    private static boolean matchesCoreKeyword(EntityDO e, String keywordLower,
                                              Set<String> allowed, boolean restrict) {
        if (!restrict || allowedContains(allowed, "name")) {
            if (e.getName() != null && e.getName().toLowerCase(Locale.ROOT).contains(keywordLower)) {
                return true;
            }
        }
        if (!restrict || allowedContains(allowed, "code")) {
            if (e.getCode() != null && e.getCode().toLowerCase(Locale.ROOT).contains(keywordLower)) {
                return true;
            }
        }
        if (!restrict || allowedContains(allowed, "status")) {
            if (e.getStatus() != null && String.valueOf(e.getStatus()).contains(keywordLower)) {
                return true;
            }
        }
        if (!restrict || allowedContains(allowed, "id")) {
            if (e.getId() != null && String.valueOf(e.getId()).contains(keywordLower)) {
                return true;
            }
        }
        return false;
    }

    private static boolean allowedContains(Set<String> allowed, String code) {
        return allowed != null && (allowed.contains(code) || allowed.contains(code.toLowerCase(Locale.ROOT)));
    }

    @Override
    public Set<Long> filterEntityIdsByFilters(String entityTypeCode, List<FieldFilterReqVO> filters, List<Long> candidateEntityIds) {
        if (entityTypeCode == null || entityTypeCode.isBlank()) {
            return Collections.emptySet();
        }
        if (candidateEntityIds == null || candidateEntityIds.isEmpty()) {
            return Collections.emptySet();
        }
        if (filters == null || filters.isEmpty()) {
            return new HashSet<>(candidateEntityIds);
        }

        // result 表示“当前仍命中的实体ID集合”，初始值为 scene 候选集合。
        // 每个 filter 都会与 result 做一次交集（AND 语义）。
        Set<Long> result = new HashSet<>(candidateEntityIds);
        Map<String, FieldDO> fieldCache = new HashMap<>();
        List<FieldFilterReqVO> orderedFilters = orderFiltersBySelectivity(filters);

        for (FieldFilterReqVO filter : orderedFilters) {
            if (filter == null || filter.getFieldCode() == null || filter.getFieldCode().isBlank() || !isEffectiveFilter(filter)) {
                continue;
            }

            String op = filter.getOp() == null ? "" : filter.getOp().trim().toUpperCase(Locale.ROOT);
            boolean isNegativeOp = "NE".equals(op) || "NOT_IN".equals(op);

            // ==================== relation filter ====================
            if (Boolean.TRUE.equals(filter.getRelationField())) {
                List<Long> relatedIds = normalizeEntityIds(filter.getValue());
                if (relatedIds.isEmpty()) {
                    return Collections.emptySet();
                }
                List<Long> matched = entityRelationService
                        .listEntityIdsByRelationFieldAndRelatedIds(filter.getFieldCode(), relatedIds);
                if (matched == null || matched.isEmpty()) {
                    if (isNegativeOp) {
                        continue;
                    }
                    return Collections.emptySet();
                }
                Set<Long> matchedQueryable = retainQueryableByField(
                        entityTypeCode, filter.getFieldCode(), new HashSet<>(matched), result);
                if (matchedQueryable.isEmpty()) {
                    if (isNegativeOp) {
                        continue;
                    }
                    return Collections.emptySet();
                }
                if (isNegativeOp) {
                    result.removeAll(matchedQueryable);
                } else {
                    result.retainAll(matchedQueryable);
                }
                if (result.isEmpty()) {
                    return Collections.emptySet();
                }
                continue;
            }

            // ==================== non-relation filter ====================
            FieldFilterReqVO effectiveFilter = filter;
            if (isNegativeOp) {
                effectiveFilter = cloneFilterWithOp(filter, "NOT_IN".equals(op) ? "IN" : "EQ");
            }
            Set<Long> matchedByField = matchEntityIdsByFieldFilter(entityTypeCode, effectiveFilter, fieldCache, result);
            if (matchedByField == null) {
                continue;
            }
            if (matchedByField.isEmpty()) {
                if (isNegativeOp) {
                    continue;
                }
                return Collections.emptySet();
            }
            if (isNegativeOp) {
                result.removeAll(matchedByField);
            } else {
                result.retainAll(matchedByField);
            }
            if (result.isEmpty()) {
                return Collections.emptySet();
            }
        }

        return result;
    }

    /**
     * 组合查询策略：先 filter，后 keyword。
     *
     * <p>优先级说明：</p>
     * <ol>
     *   <li><b>先 filter：</b>结构化条件（关系/范围）通常选择性更强，能先把候选集快速收敛；</li>
     *   <li><b>后 keyword：</b>在收敛后的子集上执行关键词匹配，减少字符串匹配与回表成本；</li>
     *   <li><b>兼容性：</b>当 filters 为空时，自动退化为纯 keyword；当 keyword 为空时，返回 filter 结果。</li>
     * </ol>
     *
     * <p>注：当前采用固定顺序（filter -> keyword）。后续可基于统计信息做自适应顺序优化。</p>
     */
    @Override
    public Set<Long> searchAndFilterEntityIds(String entityTypeCode, String keyword, List<FieldFilterReqVO> filters, List<Long> candidateEntityIds) {
        return searchAndFilterEntityIds(entityTypeCode, keyword, filters, candidateEntityIds, null);
    }

    @Override
    public Set<Long> searchAndFilterEntityIds(String entityTypeCode, String keyword, List<FieldFilterReqVO> filters,
                                              List<Long> candidateEntityIds, List<String> searchFieldCodes) {
        Set<Long> byFilter = filterEntityIdsByFilters(entityTypeCode, filters, candidateEntityIds);
        if (byFilter.isEmpty()) {
            return Collections.emptySet();
        }
        if (keyword == null || keyword.trim().isEmpty()) {
            return byFilter;
        }
        Set<Long> byKeyword = searchEntityIdsByKeyword(
                entityTypeCode, keyword, List.copyOf(byFilter), searchFieldCodes);
        if (byKeyword.isEmpty()) {
            return Collections.emptySet();
        }
        Set<Long> result = new HashSet<>();
        for (Long id : byFilter) {
            if (id != null && byKeyword.contains(id)) {
                result.add(id);
            }
        }
        return result;
    }

    /**
     * 按单个字段筛选条件，命中实体 ID 集合。
     *
     * <p><b>功能定位</b>：</p>
     * <ul>
     *   <li>仅处理 <b>non-relation</b> 字段筛选（relation 字段由关系表路径处理）。</li>
     *   <li>按 fieldCode 定位字段元信息，再根据字段类型路由到索引表对应值列（number/date/datetime）。</li>
     * </ul>
     *
     * <p><b>应用场景</b>：</p>
     * <ul>
     *   <li>Pattern A/B 场景中，用户在实体列表配置结构化筛选（如数值范围、日期区间）。</li>
     *   <li>在候选实体集合上执行“字段级命中”，再由上层做候选交集与分页。</li>
     * </ul>
     *
     * <p><b>返回约定</b>：</p>
     * <ul>
     *   <li>返回 <code>null</code>：当前字段类型/操作符暂不支持（上层可选择兼容跳过）。</li>
     *   <li>返回空集合：支持该条件但无命中。</li>
     *   <li>返回非空集合：命中实体 ID。</li>
     * </ul>
     */
    private Set<Long> matchEntityIdsByFieldFilter(
            String entityTypeCode,
            FieldFilterReqVO filter,
            Map<String, FieldDO> fieldCache,
            Set<Long> candidateIds) {
        FieldDO field = fieldCache.computeIfAbsent(filter.getFieldCode(), fieldMapper::selectByCode);
        if (field == null || field.getType() == null) {
            return null;
        }
        String fieldType = field.getType().trim().toUpperCase(Locale.ROOT);
        String op = filter.getOp() == null ? "" : filter.getOp().trim().toUpperCase(Locale.ROOT);

        Set<Long> matched;
        if (isNumberType(fieldType)) {
            BigDecimal[] range = normalizeNumberRange(op, filter.getValue());
            if (range == null) {
                return null;
            }
            List<Long> ids = entityFieldIndexMapper.selectEntityIdsByNumberRange(filter.getFieldCode(), range[0], range[1]);
            matched = new HashSet<>(ids);
        } else if ("DATE".equals(fieldType)) {
            LocalDate[] range = normalizeDateRange(op, filter.getValue());
            if (range == null) {
                return null;
            }
            List<Long> ids = entityFieldIndexMapper.selectEntityIdsByDateRange(filter.getFieldCode(), range[0], range[1]);
            matched = new HashSet<>(ids);
        } else if ("DATETIME".equals(fieldType) || "TIMESTAMP".equals(fieldType)) {
            LocalDateTime[] range = normalizeDateTimeRange(op, filter.getValue());
            if (range == null) {
                return null;
            }
            List<Long> ids = entityFieldIndexMapper.selectEntityIdsByDateTimeRange(filter.getFieldCode(), range[0], range[1]);
            matched = new HashSet<>(ids);
        } else if ("BOOLEAN".equals(fieldType) || "BOOL".equals(fieldType)) {
            if (!"EQ".equals(op)) {
                return null;
            }
            Boolean boolVal = normalizeBoolean(filter.getValue());
            if (boolVal == null) {
                return null;
            }
            List<Long> ids = entityFieldIndexMapper.selectEntityIdsByBooleanEquals(filter.getFieldCode(), boolVal);
            matched = new HashSet<>(ids);
        } else if (isStringStorageType(fieldType)) {
            if (isTextLikeType(fieldType)) {
                matched = matchStringWithTextOps(filter, op);
            } else if (isSingleOptionType(fieldType)) {
                matched = matchStringExactOnly(filter, op);
            } else if (isMultiOptionType(fieldType)) {
                matched = matchStringWithMultiSelectOps(filter, op, candidateIds);
            } else if (isReferenceType(fieldType)) {
                matched = matchStringExactOnly(filter, op);
            } else {
                matched = matchStringExactOnly(filter, op);
            }
            if (matched == null) {
                return null;
            }
        } else {
            return null;
        }

        // 不可搜索字段不得按字段命中（含索引残留、跨模型同 fieldCode）
        return retainQueryableByField(entityTypeCode, filter.getFieldCode(), matched, candidateIds);
    }

    /**
     * 仅保留：在候选内、且该实体所属模型上该字段仍可搜索的实体。
     */
    private Set<Long> retainQueryableByField(
            String entityTypeCode,
            String fieldCode,
            Set<Long> matchedIds,
            Set<Long> candidateIds) {
        if (matchedIds == null || matchedIds.isEmpty() || candidateIds == null || candidateIds.isEmpty()) {
            return Collections.emptySet();
        }
        Set<Long> scoped = new HashSet<>();
        for (Long id : matchedIds) {
            if (id != null && candidateIds.contains(id)) {
                scoped.add(id);
            }
        }
        if (scoped.isEmpty()) {
            return Collections.emptySet();
        }
        List<EntityDO> entities = entityCoreService.listByIds(List.copyOf(scoped), entityTypeCode);
        if (entities == null || entities.isEmpty()) {
            return Collections.emptySet();
        }
        Set<Long> allowed = new HashSet<>();
        for (EntityDO entity : entities) {
            if (entity == null || entity.getId() == null) {
                continue;
            }
            if (fieldIndexService.isFieldSearchable(entity.getModelId(), fieldCode)) {
                allowed.add(entity.getId());
            }
        }
        return allowed;
    }

    private FieldFilterReqVO cloneFilterWithOp(FieldFilterReqVO origin, String op) {
        FieldFilterReqVO copy = new FieldFilterReqVO();
        copy.setFieldCode(origin.getFieldCode());
        copy.setRelationField(origin.getRelationField());
        copy.setValue(origin.getValue());
        copy.setOp(op);
        return copy;
    }

    private boolean isNumberType(String type) {
        return "NUMBER".equals(type) || "INTEGER".equals(type) || "LONG".equals(type)
                || "DECIMAL".equals(type) || "DOUBLE".equals(type) || "FLOAT".equals(type);
    }

    private boolean isStringStorageType(String type) {
        return isTextLikeType(type) || isSingleOptionType(type) || isMultiOptionType(type) || isReferenceType(type);
    }

    private boolean isTextLikeType(String type) {
        return "STRING".equals(type) || "TEXT".equals(type);
    }

    private boolean isSingleOptionType(String type) {
        return "SELECT".equals(type) || "ENUM".equals(type) || "OPTION".equals(type);
    }

    private boolean isMultiOptionType(String type) {
        return "MULTI_SELECT".equals(type);
    }

    private boolean isReferenceType(String type) {
        return "REFERENCE".equals(type) || "ENTITY_REF".equals(type);
    }

    private Set<Long> matchStringExactOnly(FieldFilterReqVO filter, String op) {
        if ("EQ".equals(op)) {
            String v = normalizeSingleString(filter.getValue());
            if (v == null) {
                return null;
            }
            return new HashSet<>(entityFieldIndexMapper.selectEntityIdsByStringEquals(filter.getFieldCode(), v));
        }
        if ("IN".equals(op)) {
            List<String> values = normalizeStringList(filter.getValue());
            if (values.isEmpty()) {
                return Collections.emptySet();
            }
            return new HashSet<>(entityFieldIndexMapper.selectEntityIdsByStringIn(filter.getFieldCode(), values));
        }
        return null;
    }

    private Set<Long> matchStringWithTextOps(FieldFilterReqVO filter, String op) {
        if ("CONTAINS".equals(op) || "LIKE".equals(op)) {
            String v = normalizeSingleString(filter.getValue());
            if (v == null) {
                return null;
            }
            return new HashSet<>(entityFieldIndexMapper.selectEntityIdsByStringContains(filter.getFieldCode(), v));
        }
        return matchStringExactOnly(filter, op);
    }

    private Set<Long> matchStringWithMultiSelectOps(FieldFilterReqVO filter, String op, Set<Long> candidateIds) {
        String fieldCode = filter.getFieldCode();
        if ("EQ".equals(op)) {
            String token = normalizeSingleString(filter.getValue());
            if (token == null) {
                return null;
            }
            return matchMultiSelectByExactTokens(fieldCode, List.of(token), candidateIds);
        }
        if ("IN".equals(op)) {
            List<String> tokens = normalizeStringList(filter.getValue());
            if (tokens.isEmpty()) {
                return Collections.emptySet();
            }
            return matchMultiSelectByExactTokens(fieldCode, tokens, candidateIds);
        }
        if ("CONTAINS".equals(op) || "LIKE".equals(op)) {
            // contains/like 对多选语义定义为“包含任一 token”
            List<String> tokens = normalizeStringListOrSingle(filter.getValue());
            if (tokens.isEmpty()) {
                return Collections.emptySet();
            }
            return matchMultiSelectByExactTokens(fieldCode, tokens, candidateIds);
        }
        return null;
    }

    private List<Long> normalizeEntityIds(Object raw) {
        if (raw == null) {
            return Collections.emptyList();
        }
        if (raw instanceof List<?> list) {
            return list.stream()
                    .map(v -> toLong(v))
                    .filter(v -> v != null)
                    .toList();
        }
        Long single = toLong(raw);
        return single == null ? Collections.emptyList() : List.of(single);
    }

    private Long toLong(Object v) {
        if (v == null) {
            return null;
        }
        if (v instanceof Number n) {
            return n.longValue();
        }
        try {
            return Long.parseLong(String.valueOf(v));
        } catch (Exception e) {
            return null;
        }
    }

    private BigDecimal[] normalizeNumberRange(String op, Object value) {
        try {
            if ("EQ".equals(op)) {
                BigDecimal x = new BigDecimal(String.valueOf(value));
                return new BigDecimal[] {x, x};
            }
            if ("GTE".equals(op) || "GT".equals(op)) {
                BigDecimal x = new BigDecimal(String.valueOf(value));
                return new BigDecimal[] {x, null};
            }
            if ("LTE".equals(op) || "LT".equals(op)) {
                BigDecimal x = new BigDecimal(String.valueOf(value));
                return new BigDecimal[] {null, x};
            }
            if ("BETWEEN".equals(op) && value instanceof List<?> list && list.size() >= 2) {
                BigDecimal min = new BigDecimal(String.valueOf(list.get(0)));
                BigDecimal max = new BigDecimal(String.valueOf(list.get(1)));
                return new BigDecimal[] {min, max};
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private LocalDate[] normalizeDateRange(String op, Object value) {
        try {
            if ("EQ".equals(op)) {
                LocalDate x = LocalDate.parse(String.valueOf(value));
                return new LocalDate[] {x, x};
            }
            if ("GTE".equals(op) || "GT".equals(op)) {
                LocalDate x = LocalDate.parse(String.valueOf(value));
                return new LocalDate[] {x, null};
            }
            if ("LTE".equals(op) || "LT".equals(op)) {
                LocalDate x = LocalDate.parse(String.valueOf(value));
                return new LocalDate[] {null, x};
            }
            if ("BETWEEN".equals(op) && value instanceof List<?> list && list.size() >= 2) {
                LocalDate min = LocalDate.parse(String.valueOf(list.get(0)));
                LocalDate max = LocalDate.parse(String.valueOf(list.get(1)));
                return new LocalDate[] {min, max};
            }
        } catch (DateTimeParseException ignored) {
        }
        return null;
    }

    private LocalDateTime[] normalizeDateTimeRange(String op, Object value) {
        try {
            if ("EQ".equals(op)) {
                LocalDateTime x = LocalDateTime.parse(String.valueOf(value));
                return new LocalDateTime[] {x, x};
            }
            if ("GTE".equals(op) || "GT".equals(op)) {
                LocalDateTime x = LocalDateTime.parse(String.valueOf(value));
                return new LocalDateTime[] {x, null};
            }
            if ("LTE".equals(op) || "LT".equals(op)) {
                LocalDateTime x = LocalDateTime.parse(String.valueOf(value));
                return new LocalDateTime[] {null, x};
            }
            if ("BETWEEN".equals(op) && value instanceof List<?> list && list.size() >= 2) {
                LocalDateTime min = LocalDateTime.parse(String.valueOf(list.get(0)));
                LocalDateTime max = LocalDateTime.parse(String.valueOf(list.get(1)));
                return new LocalDateTime[] {min, max};
            }
        } catch (DateTimeParseException ignored) {
        }
        return null;
    }

    private List<FieldFilterReqVO> orderFiltersBySelectivity(List<FieldFilterReqVO> filters) {
        return filters.stream()
                .filter(f -> f != null)
                .sorted((a, b) -> Integer.compare(filterCost(a), filterCost(b)))
                .toList();
    }

    private int filterCost(FieldFilterReqVO filter) {
        if (filter == null) {
            return 100;
        }
        if (Boolean.TRUE.equals(filter.getRelationField())) {
            return 1;
        }
        String op = filter.getOp() == null ? "" : filter.getOp().trim().toUpperCase(Locale.ROOT);
        return switch (op) {
            case "EQ", "IN", "BETWEEN" -> 2;
            case "GTE", "LTE", "GT", "LT" -> 3;
            case "CONTAINS", "LIKE" -> 5;
            default -> 10;
        };
    }

    private boolean isEffectiveFilter(FieldFilterReqVO filter) {
        if (filter == null) {
            return false;
        }
        Object value = filter.getValue();
        if (value == null) {
            return false;
        }
        if (value instanceof String s) {
            return !s.isBlank();
        }
        if (value instanceof List<?> list) {
            return !list.isEmpty();
        }
        return true;
    }

    private Boolean normalizeBoolean(Object raw) {
        if (raw == null) {
            return null;
        }
        if (raw instanceof Boolean b) {
            return b;
        }
        String s = String.valueOf(raw).trim().toLowerCase(Locale.ROOT);
        if ("true".equals(s) || "1".equals(s) || "yes".equals(s)) {
            return true;
        }
        if ("false".equals(s) || "0".equals(s) || "no".equals(s)) {
            return false;
        }
        return null;
    }

    private String normalizeSingleString(Object raw) {
        if (raw == null) {
            return null;
        }
        String s = String.valueOf(raw).trim();
        return s.isBlank() ? null : s;
    }

    private List<String> normalizeStringList(Object raw) {
        if (!(raw instanceof List<?> list) || list.isEmpty()) {
            return Collections.emptyList();
        }
        return list.stream()
                .map(v -> v == null ? null : String.valueOf(v).trim())
                .filter(v -> v != null && !v.isBlank())
                .toList();
    }

    private List<String> normalizeStringListOrSingle(Object raw) {
        if (raw instanceof List<?>) {
            return normalizeStringList(raw);
        }
        String single = normalizeSingleString(raw);
        return single == null ? Collections.emptyList() : List.of(single);
    }

    /**
     * MULTI_SELECT token 精确匹配：
     * - 把 value_string 解析为 token 集合（支持 JSON 数组 / 逗号分隔 / 分号分隔）
     * - 仅当 token 完整相等时命中，避免 "1" 误命中 "11"
     */
    private Set<Long> matchMultiSelectByExactTokens(String fieldCode, List<String> expectedTokens, Set<Long> candidateIds) {
        if (fieldCode == null || fieldCode.isBlank() || expectedTokens == null || expectedTokens.isEmpty()
                || candidateIds == null || candidateIds.isEmpty()) {
            return Collections.emptySet();
        }

        Set<String> expected = new HashSet<>(expectedTokens);
        List<EntityFieldIndexDO> rows = entityFieldIndexMapper.selectByFieldCodeAndEntityIds(fieldCode, candidateIds);
        if (rows == null || rows.isEmpty()) {
            return Collections.emptySet();
        }

        Set<Long> matched = new HashSet<>();
        for (EntityFieldIndexDO row : rows) {
            if (row == null || row.getEntityId() == null) {
                continue;
            }
            String raw = row.getValueString();
            if (raw == null || raw.isBlank()) {
                continue;
            }
            Set<String> tokens = parseMultiSelectTokens(raw);
            if (tokens.isEmpty()) {
                continue;
            }
            boolean hit = false;
            for (String token : expected) {
                if (tokens.contains(token)) {
                    hit = true;
                    break;
                }
            }
            if (hit) {
                matched.add(row.getEntityId());
            }
        }
        return matched;
    }

    private Set<String> parseMultiSelectTokens(String raw) {
        String s = raw == null ? "" : raw.trim();
        if (s.isBlank()) {
            return Collections.emptySet();
        }

        // 尝试 JSON 数组格式：["a","b"] 或 [1,2]
        if (s.startsWith("[") && s.endsWith("]")) {
            try {
                Object parsed = com.alibaba.fastjson2.JSON.parse(s);
                if (parsed instanceof List<?> list) {
                    Set<String> tokens = new HashSet<>();
                    for (Object item : list) {
                        String t = item == null ? null : String.valueOf(item).trim();
                        if (t != null && !t.isBlank()) {
                            tokens.add(t);
                        }
                    }
                    return tokens;
                }
            } catch (Exception ignored) {
            }
        }

        // 兼容逗号/分号分隔
        String normalized = s.replace(';', ',');
        String[] arr = normalized.split(",");
        Set<String> tokens = new HashSet<>();
        for (String item : arr) {
            String t = item == null ? null : item.trim();
            if (t != null && !t.isBlank()) {
                tokens.add(t);
            }
        }
        return tokens;
    }
}
