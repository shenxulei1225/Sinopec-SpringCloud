package cn.cheers.x.module.dynamicbusiness.dal.repository.entity;

import cn.hutool.core.collection.CollUtil;
import cn.cheers.x.framework.common.pojo.PageParam;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.framework.mybatis.core.type.JsonbMapTypeHandler;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntityMapper;
import cn.cheers.x.module.dynamicbusiness.framework.entity.EntityTableNameContext;
import cn.cheers.x.module.dynamicbusiness.framework.entity.EntityTableNameHandler;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityDedicatedColumnService;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityDedicatedColumnService.PhysicalFieldSpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * 实体 Repository 实现。
 *
 * <p>职责：统一封装 EntityDO 的数据库访问，并通过 entityTypeCode 动态路由到对应业务表。</p>
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class EntityRepositoryImpl implements EntityRepository {

    /** 实体基础 Mapper（实际表名由动态表名上下文决定）。 */
    private final EntityMapper entityMapper;
    private final EntityTableNameHandler entityTableNameHandler;
    private final JdbcTemplate jdbcTemplate;
    private final EntityDedicatedColumnService entityDedicatedColumnService;

    // ==================== 写入操作 ====================

    /**
     * 保存单个实体。
     */
    @Override
    public Long save(EntityDO entity) {
        return save(entity, null);
    }

    @Override
    public Long save(EntityDO entity, Map<String, Object> physicalColumns) {
        if (physicalColumns != null && !physicalColumns.isEmpty()) {
            return entityDedicatedColumnService.insertEntityRow(entity, physicalColumns);
        }
        return withTableName(entity.getEntityTypeCode(), () -> {
            entityMapper.insert(entity);
            return entity.getId();
        });
    }

    /**
     * 批量保存实体（默认按首条记录的 entityTypeCode 路由）。
     */
    @Override
    public void saveBatch(List<EntityDO> entities) {
        if (CollUtil.isEmpty(entities)) {
            return;
        }
        String entityTypeCode = entities.get(0).getEntityTypeCode();
        withTableName(entityTypeCode, () -> {
            entityMapper.insertBatch(entities);
            return null;
        });
    }

    // ==================== 读取操作 ====================

    /**
     * 按主键查询实体。
     */
    @Override
    public EntityDO findById(Long id, String entityTypeCode) {
        return withTableName(entityTypeCode, () -> entityMapper.selectById(id));
    }

    /**
     * 按 ID 列表批量查询实体。
     */
    @Override
    public List<EntityDO> findByIds(List<Long> ids, String entityTypeCode) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return withTableName(entityTypeCode, () ->
                entityMapper.selectList(new LambdaQueryWrapperX<EntityDO>()
                        .in(EntityDO::getId, ids))
        );
    }

    /**
     * 一次 SELECT：核心列 + 全部启用基础字段物理列；按 orderedIds 保序（含 custom_fields）。
     */
    @Override
    public List<EntityDO> findByIdsWithDedicatedBaseFields(List<Long> orderedIds, String entityTypeCode) {
        return findByIdsWithDedicatedBaseFields(orderedIds, entityTypeCode, true);
    }

    /**
     * 一次 SELECT：核心列 + 启用基础字段物理列；列表热路径可省略 custom_fields。
     */
    @Override
    public List<EntityDO> findByIdsWithDedicatedBaseFields(List<Long> orderedIds, String entityTypeCode,
                                                           boolean includeCustomFields) {
        if (CollUtil.isEmpty(orderedIds) || !org.springframework.util.StringUtils.hasText(entityTypeCode)) {
            return Collections.emptyList();
        }
        List<Long> ids = orderedIds.stream().filter(Objects::nonNull).toList();
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }
        String typeCode = entityTypeCode.trim();
        List<PhysicalFieldSpec> specs = entityDedicatedColumnService.listEnabledPhysicalFields(typeCode);
        String table = resolvePhysicalTableName(typeCode);

        StringBuilder select = new StringBuilder(
                "SELECT id, entity_type_code, model_id, name, code, status, parent_id, domain, sort");
        if (includeCustomFields) {
            select.append(", custom_fields");
        }
        for (PhysicalFieldSpec spec : specs) {
            select.append(", ").append(spec.columnName());
        }
        String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
        select.append(" FROM ").append(table)
                .append(" WHERE deleted = false AND id IN (").append(placeholders).append(")");

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(select.toString(), ids.toArray());
        Map<Long, EntityDO> byId = new HashMap<>(Math.max(16, rows.size() * 2));
        for (Map<String, Object> row : rows) {
            if (row == null) {
                continue;
            }
            EntityDO entity = mapCoreRow(row, includeCustomFields);
            if (entity.getId() == null) {
                continue;
            }
            Map<String, Object> baseValues = new LinkedHashMap<>();
            for (PhysicalFieldSpec spec : specs) {
                Object dbVal = readColumn(row, spec.columnName());
                if (dbVal == null) {
                    continue;
                }
                // 与 VO 组装同一收口：jsonb 驱动值先收成业务 JSON，再进字段袋
                Object normalized = EntityDedicatedColumnService.normalizePhysicalDbValue(dbVal);
                if (normalized != null) {
                    baseValues.put(spec.fieldCode(), normalized);
                }
            }
            entity.setDedicatedBaseFieldValues(baseValues);
            byId.put(entity.getId(), entity);
        }

        List<EntityDO> ordered = new ArrayList<>(ids.size());
        for (Long id : ids) {
            EntityDO entity = byId.get(id);
            if (entity != null) {
                ordered.add(entity);
            }
        }
        return ordered;
    }

    private static EntityDO mapCoreRow(Map<String, Object> row, boolean includeCustomFields) {
        EntityDO entity = new EntityDO();
        entity.setId(toLong(readColumn(row, "id")));
        entity.setEntityTypeCode(toStringVal(readColumn(row, "entity_type_code")));
        entity.setModelId(toLong(readColumn(row, "model_id")));
        entity.setName(toStringVal(readColumn(row, "name")));
        entity.setCode(toStringVal(readColumn(row, "code")));
        entity.setStatus(toInteger(readColumn(row, "status")));
        entity.setParentId(toLong(readColumn(row, "parent_id")));
        entity.setDomain(toStringVal(readColumn(row, "domain")));
        entity.setSort(toInteger(readColumn(row, "sort")));
        if (includeCustomFields) {
            Object custom = readColumn(row, "custom_fields");
            if (custom != null) {
                entity.setCustomFields(JsonbMapTypeHandler.parse(String.valueOf(custom)));
            }
        }
        return entity;
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

    private static Long toLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        try {
            String text = String.valueOf(value).trim();
            if (text.isEmpty()) {
                return null;
            }
            return Long.parseLong(text);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static Integer toInteger(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        try {
            String text = String.valueOf(value).trim();
            if (text.isEmpty()) {
                return null;
            }
            return Integer.parseInt(text);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static String toStringVal(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    /**
     * 按通用查询条件获取实体列表。
     */
    @Override
    public List<EntityDO> findAll(EntityQuery query) {
        return withTableName(query.getEntityTypeCode(), () -> {
            LambdaQueryWrapperX<EntityDO> wrapper = buildQueryWrapper(query);
            return entityMapper.selectList(wrapper);
        });
    }

    /**
     * 按通用查询条件分页查询实体。
     */
    @Override
    public PageResult<EntityDO> findPage(EntityQuery query) {
        return withTableName(query.getEntityTypeCode(), () -> {
            LambdaQueryWrapperX<EntityDO> wrapper = buildQueryWrapper(query);
            PageParam pageParam = new PageParam();
            pageParam.setPageNo(query.getPageNo() != null ? query.getPageNo() : 1);
            pageParam.setPageSize(query.getPageSize() != null ? query.getPageSize() : 10);
            return entityMapper.selectPage(pageParam, wrapper);
        });
    }

    /**
     * 分页只查 id，避免先装全行再丢弃。
     */
    @Override
    public PageResult<Long> findPageIds(EntityQuery query) {
        return withTableName(query.getEntityTypeCode(), () -> {
            LambdaQueryWrapperX<EntityDO> wrapper = buildQueryWrapper(query);
            wrapper.select(EntityDO::getId);
            PageParam pageParam = new PageParam();
            pageParam.setPageNo(query.getPageNo() != null ? query.getPageNo() : 1);
            pageParam.setPageSize(query.getPageSize() != null ? query.getPageSize() : 10);
            PageResult<EntityDO> page = entityMapper.selectPage(pageParam, wrapper);
            List<Long> ids = page.getList() == null
                    ? Collections.emptyList()
                    : page.getList().stream()
                    .map(EntityDO::getId)
                    .filter(Objects::nonNull)
                    .toList();
            return new PageResult<>(ids, page.getTotal());
        });
    }

    /**
     * 按模型 ID 查询实体列表。
     */
    @Override
    public List<EntityDO> findByModelId(Long modelId, String entityTypeCode) {
        return withTableName(entityTypeCode, () ->
                entityMapper.selectList(new LambdaQueryWrapperX<EntityDO>()
                        .eq(EntityDO::getModelId, modelId)
                        .eq(EntityDO::getDeleted, false)
                        .orderByAsc(EntityDO::getSort))
        );
    }

    /**
     * 按模型 ID 列表查询实体列表。
     */
    @Override
    public List<EntityDO> findByModelIds(List<Long> modelIds, String entityTypeCode) {
        if (CollUtil.isEmpty(modelIds)) {
            return Collections.emptyList();
        }
        return withTableName(entityTypeCode, () ->
                entityMapper.selectList(new LambdaQueryWrapperX<EntityDO>()
                        .in(EntityDO::getModelId, modelIds)
                        .eq(EntityDO::getDeleted, false)
                        .orderByAsc(EntityDO::getSort))
        );
    }

    /**
     * 按模型 ID 列表分页查询实体（默认按 sort）。
     */
    @Override
    public PageResult<EntityDO> findPageByModelIds(List<Long> modelIds, String entityTypeCode,
                                                    Integer status, String keyword, String domain,
                                                    Integer pageNo, Integer pageSize) {
        return findPageByModelIds(modelIds, entityTypeCode, status, keyword, domain,
                pageNo, pageSize, null, null);
    }

    /**
     * 按模型 ID 列表分页；可按核心列库内 ORDER BY + LIMIT。
     */
    @Override
    public PageResult<EntityDO> findPageByModelIds(List<Long> modelIds, String entityTypeCode,
                                                    Integer status, String keyword, String domain,
                                                    Integer pageNo, Integer pageSize,
                                                    String orderByColumn, Boolean orderAsc) {
        if (CollUtil.isEmpty(modelIds)) {
            return new PageResult<>(Collections.emptyList(), 0L);
        }
        return withTableName(entityTypeCode, () -> {
            LambdaQueryWrapperX<EntityDO> wrapper = buildModelIdsPageWrapper(
                    modelIds, status, keyword, domain, orderByColumn, orderAsc, null, null, null);
            PageParam pageParam = new PageParam();
            pageParam.setPageNo(pageNo != null && pageNo > 0 ? pageNo : 1);
            pageParam.setPageSize(pageSize != null && pageSize > 0 ? pageSize : 20);
            return entityMapper.selectPage(pageParam, wrapper);
        });
    }

    @Override
    public PageResult<Long> findPageIdsByModelIds(List<Long> modelIds, String entityTypeCode,
                                                   Integer status, String keyword, String domain,
                                                   Integer pageNo, Integer pageSize,
                                                   String orderByColumn, Boolean orderAsc) {
        return findPageIdsByModelIds(modelIds, entityTypeCode, status, keyword, domain,
                pageNo, pageSize, orderByColumn, orderAsc, null);
    }

    @Override
    public PageResult<Long> findPageIdsByModelIds(List<Long> modelIds, String entityTypeCode,
                                                   Integer status, String keyword, String domain,
                                                   Integer pageNo, Integer pageSize,
                                                   String orderByColumn, Boolean orderAsc,
                                                   List<PhysicalColumnFilter> physicalFilters) {
        return findPageIdsByModelIds(modelIds, entityTypeCode, status, keyword, domain,
                pageNo, pageSize, orderByColumn, orderAsc, physicalFilters, null);
    }

    @Override
    public PageResult<Long> findPageIdsByModelIds(List<Long> modelIds, String entityTypeCode,
                                                   Integer status, String keyword, String domain,
                                                   Integer pageNo, Integer pageSize,
                                                   String orderByColumn, Boolean orderAsc,
                                                   List<PhysicalColumnFilter> physicalFilters,
                                                   KeywordSearchSpec keywordSearch) {
        return findPageIdsByModelIds(modelIds, entityTypeCode, status, keyword, domain,
                pageNo, pageSize, orderByColumn, orderAsc, physicalFilters, keywordSearch, null);
    }

    @Override
    public PageResult<Long> findPageIdsByModelIds(List<Long> modelIds, String entityTypeCode,
                                                   Integer status, String keyword, String domain,
                                                   Integer pageNo, Integer pageSize,
                                                   String orderByColumn, Boolean orderAsc,
                                                   List<PhysicalColumnFilter> physicalFilters,
                                                   KeywordSearchSpec keywordSearch,
                                                   String scopeRegistryCode) {
        if (CollUtil.isEmpty(modelIds)) {
            return new PageResult<>(Collections.emptyList(), 0L);
        }
        return withTableName(entityTypeCode, () -> {
            LambdaQueryWrapperX<EntityDO> wrapper = buildModelIdsPageWrapper(
                    modelIds, status, keyword, domain, orderByColumn, orderAsc,
                    physicalFilters, keywordSearch, scopeRegistryCode);
            wrapper.select(EntityDO::getId);
            PageParam pageParam = new PageParam();
            pageParam.setPageNo(pageNo != null && pageNo > 0 ? pageNo : 1);
            pageParam.setPageSize(pageSize != null && pageSize > 0 ? pageSize : 20);
            PageResult<EntityDO> page = entityMapper.selectPage(pageParam, wrapper);
            List<Long> ids = page.getList() == null
                    ? Collections.emptyList()
                    : page.getList().stream()
                    .map(EntityDO::getId)
                    .filter(Objects::nonNull)
                    .toList();
            return new PageResult<>(ids, page.getTotal());
        });
    }

    private LambdaQueryWrapperX<EntityDO> buildModelIdsPageWrapper(List<Long> modelIds,
                                                                   Integer status,
                                                                   String keyword,
                                                                   String domain,
                                                                   String orderByColumn,
                                                                   Boolean orderAsc,
                                                                   List<PhysicalColumnFilter> physicalFilters,
                                                                   KeywordSearchSpec keywordSearch,
                                                                   String scopeRegistryCode) {
        LambdaQueryWrapperX<EntityDO> wrapper = new LambdaQueryWrapperX<>();
        wrapper.in(EntityDO::getModelId, modelIds)
                .eqIfPresent(EntityDO::getStatus, status)
                .eqIfPresent(EntityDO::getDomain, domain != null ? domain.trim() : null)
                .eq(EntityDO::getDeleted, false);
        KeywordSearchSql.applyToWrapper(wrapper, keyword, keywordSearch);
        applyPhysicalFilters(wrapper, physicalFilters);
        applyScopeMembershipExists(wrapper, scopeRegistryCode);
        applyCoreOrSortOrder(wrapper, orderByColumn, orderAsc);
        return wrapper;
    }

    /**
     * 按树路径前缀查询实体（用于子树场景）。
     */
    @Override
    public List<EntityDO> findByTreePathStartsWith(String treePath, String entityTypeCode) {
        return withTableName(entityTypeCode, () ->
                entityMapper.selectList(new LambdaQueryWrapperX<EntityDO>()
                        .likeRight(EntityDO::getTreePath, treePath)));
    }

    // ==================== 更新操作 ====================

    /**
     * 更新单个实体。
     */
    @Override
    public void update(EntityDO entity) {
        withTableName(entity.getEntityTypeCode(), () -> {
            entityMapper.updateById(entity);
            return null;
        });
    }

    /**
     * 批量更新实体（默认按首条记录的 entityTypeCode 路由）。
     */
    @Override
    public void updateBatch(List<EntityDO> entities) {
        if (CollUtil.isEmpty(entities)) {
            return;
        }
        String entityTypeCode = entities.get(0).getEntityTypeCode();
        withTableName(entityTypeCode, () -> {
            entityMapper.updateBatch(entities);
            return null;
        });
    }

    /**
     * 批量改写某型号下全部实体的业务域。
     *
     * <p>用整表更新而不是逐行 updateById：型号跨业务域迁移可能涉及上千实体，
     * 逐行更新既慢又会把无关字段一起写回。</p>
     */
    @Override
    public int updateDomainByModelId(Long modelId, String entityTypeCode, String domain) {
        if (modelId == null || !org.springframework.util.StringUtils.hasText(entityTypeCode)) {
            return 0;
        }
        return withTableName(entityTypeCode, () -> entityMapper.update(null,
                new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<EntityDO>()
                        .set(EntityDO::getDomain, domain)
                        .eq(EntityDO::getModelId, modelId)
                        .eq(EntityDO::getDeleted, false)));
    }

    // ==================== 删除操作 ====================

    /**
     * 删除单个实体。
     */
    @Override
    public void delete(Long id, String entityTypeCode) {
        withTableName(entityTypeCode, () -> {
            entityMapper.deleteById(id);
            return null;
        });
    }

    /**
     * 按 ID 列表批量删除实体。
     */
    @Override
    public void deleteBatch(List<Long> ids, String entityTypeCode) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        withTableName(entityTypeCode, () -> {
            entityMapper.delete(new LambdaQueryWrapperX<EntityDO>()
                    .in(EntityDO::getId, ids));
            return null;
        });
    }

    // ==================== 统计操作 ====================

    /**
     * 判断指定实体是否存在（且未删除）。
     */
    @Override
    public boolean exists(Long id, String entityTypeCode) {
        return withTableName(entityTypeCode, () -> {
            EntityDO entity = entityMapper.selectById(id);
            return entity != null && !Boolean.TRUE.equals(entity.getDeleted());
        });
    }

    /**
     * 按查询条件统计实体数量。
     */
    @Override
    public long count(EntityQuery query) {
        return withTableName(query.getEntityTypeCode(), () -> {
            LambdaQueryWrapperX<EntityDO> wrapper = buildQueryWrapper(query);
            return entityMapper.selectCount(wrapper);
        });
    }

    /**
     * 按业务类型统计实体数量。
     */
    @Override
    public long countByEntityTypeCode(String entityTypeCode) {
        return withTableName(entityTypeCode, () ->
                entityMapper.selectCount(new LambdaQueryWrapperX<EntityDO>()
                        .eq(EntityDO::getEntityTypeCode, entityTypeCode)
                        .eq(EntityDO::getDeleted, false))
        );
    }

    /**
     * 判断指定父实体下是否存在直接子实体。
     *
     * <p>用途：删除前快速做“有无子实体”检查，避免全量查询子节点列表。</p>
     */
    @Override
    public boolean existsByParentId(Long parentId, String entityTypeCode) {
        if (parentId == null) {
            return false;
        }
        return withTableName(entityTypeCode, () ->
                entityMapper.selectOne(new LambdaQueryWrapperX<EntityDO>()
                        .eq(EntityDO::getParentId, parentId)
                        .eq(EntityDO::getDeleted, false)
                        .last("LIMIT 1")) != null
        );
    }

    @Override
    public boolean existsByExactName(String entityTypeCode, Long modelId, String name, Long excludeId) {
        if (!org.springframework.util.StringUtils.hasText(entityTypeCode)
                || modelId == null
                || !org.springframework.util.StringUtils.hasText(name)) {
            return false;
        }
        return withTableName(entityTypeCode, () ->
                entityMapper.selectOne(new LambdaQueryWrapperX<EntityDO>()
                        .eq(EntityDO::getModelId, modelId)
                        .eq(EntityDO::getName, name.trim())
                        .neIfPresent(EntityDO::getId, excludeId)
                        .eq(EntityDO::getDeleted, false)
                        .last("LIMIT 1")) != null
        );
    }

    @Override
    public boolean existsByExactCode(String entityTypeCode, String code, Long excludeId) {
        if (!org.springframework.util.StringUtils.hasText(entityTypeCode)
                || !org.springframework.util.StringUtils.hasText(code)) {
            return false;
        }
        return withTableName(entityTypeCode, () ->
                entityMapper.selectOne(new LambdaQueryWrapperX<EntityDO>()
                        .eq(EntityDO::getCode, code.trim())
                        .neIfPresent(EntityDO::getId, excludeId)
                        .eq(EntityDO::getDeleted, false)
                        .last("LIMIT 1")) != null
        );
    }

    @Override
    public EntityDO findByExactCode(String entityTypeCode, String code) {
        if (!org.springframework.util.StringUtils.hasText(entityTypeCode)
                || !org.springframework.util.StringUtils.hasText(code)) {
            return null;
        }
        return withTableName(entityTypeCode, () ->
                entityMapper.selectOne(new LambdaQueryWrapperX<EntityDO>()
                        .eq(EntityDO::getCode, code.trim())
                        .eq(EntityDO::getDeleted, false)
                        .last("LIMIT 1"))
        );
    }

    @Override
    public boolean existsByModelId(Long modelId, String entityTypeCode) {
        if (modelId == null || !org.springframework.util.StringUtils.hasText(entityTypeCode)) {
            return false;
        }
        return withTableName(entityTypeCode, () ->
                entityMapper.selectOne(new LambdaQueryWrapperX<EntityDO>()
                        .eq(EntityDO::getModelId, modelId)
                        .eq(EntityDO::getDeleted, false)
                        .last("LIMIT 1")) != null
        );
    }

    /**
     * 在实体所属的专用表内聚合设施覆盖范围。
     *
     * <p>{@code facility_id} 是实体归属设施的权威列；这里直接做库内去重计数，
     * 不装载实体，也不读取型号表上的发起设施。</p>
     */
    @Override
    public long countDistinctFacilityIdsByModelId(Long modelId, String entityTypeCode) {
        if (modelId == null || !org.springframework.util.StringUtils.hasText(entityTypeCode)) {
            throw new IllegalArgumentException("modelId 与 entityTypeCode 不能为空");
        }
        String entityTable = resolvePhysicalTableName(entityTypeCode);
        String sql = "SELECT COUNT(DISTINCT facility_id) FROM " + entityTable
                + " WHERE deleted = false AND model_id = ? AND facility_id IS NOT NULL";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, modelId);
        return count == null ? 0L : count;
    }

    @Override
    public boolean hasFacilityIdColumn(String entityTypeCode) {
        String entityTable = resolvePhysicalTableName(entityTypeCode);
        Boolean present = jdbcTemplate.queryForObject(
                "SELECT EXISTS (SELECT 1 FROM information_schema.columns "
                        + "WHERE table_schema = current_schema() AND table_name = ? AND column_name = 'facility_id')",
                Boolean.class, entityTable);
        return Boolean.TRUE.equals(present);
    }

    @Override
    public List<cn.cheers.x.module.dynamicbusiness.service.entity.dto.EntityAggregationCountDTO<Integer>>
            countGroupByStatus(String entityTypeCode, Long modelId, Integer status, String keyword,
                               List<Long> entityIds) {
        return withTableName(entityTypeCode, () -> {
            com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<EntityDO> query =
                    new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
            query.select("status AS key", "COUNT(1) AS cnt");
            query.eq("deleted", false);
            if (modelId != null) {
                query.eq("model_id", modelId);
            }
            if (status != null) {
                query.eq("status", status);
            }
            if (org.springframework.util.StringUtils.hasText(keyword)) {
                query.like("name", keyword.trim());
            }
            if (CollUtil.isNotEmpty(entityIds)) {
                query.in("id", entityIds);
            }
            query.groupBy("status");
            List<java.util.Map<String, Object>> rows = entityMapper.selectMaps(query);
            if (CollUtil.isEmpty(rows)) {
                return Collections.emptyList();
            }
            return rows.stream()
                    .map(cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntityAggregationMapper::toStatusCountDTO)
                    .filter(java.util.Objects::nonNull)
                    .toList();
        });
    }

    @Override
    public List<cn.cheers.x.module.dynamicbusiness.service.entity.dto.EntityAggregationCountDTO<Long>>
            countGroupByModelId(String entityTypeCode, Long modelId, Integer status, String keyword,
                                List<Long> entityIds) {
        return withTableName(entityTypeCode, () -> {
            com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<EntityDO> query =
                    new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
            query.select("model_id AS key", "COUNT(1) AS cnt");
            query.eq("deleted", false);
            if (modelId != null) {
                query.eq("model_id", modelId);
            }
            if (status != null) {
                query.eq("status", status);
            }
            if (org.springframework.util.StringUtils.hasText(keyword)) {
                query.like("name", keyword.trim());
            }
            if (CollUtil.isNotEmpty(entityIds)) {
                query.in("id", entityIds);
            }
            query.groupBy("model_id");
            List<java.util.Map<String, Object>> rows = entityMapper.selectMaps(query);
            if (CollUtil.isEmpty(rows)) {
                return Collections.emptyList();
            }
            return rows.stream()
                    .map(cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntityAggregationMapper::toModelCountDTO)
                    .filter(java.util.Objects::nonNull)
                    .toList();
        });
    }

    @Override
    public List<Long> listDistinctModelIdsPreservingEntityOrder(List<Long> orderedEntityIds, String entityTypeCode) {
        if (CollUtil.isEmpty(orderedEntityIds) || !org.springframework.util.StringUtils.hasText(entityTypeCode)) {
            return Collections.emptyList();
        }
        List<Long> normalizedIds = orderedEntityIds.stream()
                .filter(java.util.Objects::nonNull)
                .filter(id -> id > 0)
                .distinct()
                .toList();
        if (normalizedIds.isEmpty()) {
            return Collections.emptyList();
        }
        // 与分类范围列表同一套物理表解析，避免 MP selectMaps 键名/动态表名边缘问题
        String entityTable = resolvePhysicalTableName(entityTypeCode);
        java.util.Map<Long, Long> modelIdByEntityId = new java.util.HashMap<>();
        final int chunkSize = 1000;
        for (int i = 0; i < normalizedIds.size(); i += chunkSize) {
            List<Long> chunk = normalizedIds.subList(i, Math.min(i + chunkSize, normalizedIds.size()));
            String placeholders = String.join(",", java.util.Collections.nCopies(chunk.size(), "?"));
            String sql = "SELECT id, model_id FROM " + entityTable
                    + " WHERE deleted = false AND id IN (" + placeholders + ")";
            List<Object> args = new ArrayList<>(chunk);
            jdbcTemplate.query(sql, args.toArray(), rs -> {
                long entityId = rs.getLong("id");
                long modelId = rs.getLong("model_id");
                if (!rs.wasNull() && modelId > 0) {
                    modelIdByEntityId.put(entityId, modelId);
                }
            });
        }
        if (modelIdByEntityId.isEmpty()) {
            return Collections.emptyList();
        }
        java.util.LinkedHashSet<Long> orderedModelIds = new java.util.LinkedHashSet<>();
        for (Long entityId : orderedEntityIds) {
            if (entityId == null) {
                continue;
            }
            Long modelId = modelIdByEntityId.get(entityId);
            if (modelId != null) {
                orderedModelIds.add(modelId);
            }
        }
        return new ArrayList<>(orderedModelIds);
    }

    @Override
    public String resolvePhysicalTableName(String entityTypeCode) {
        if (!org.springframework.util.StringUtils.hasText(entityTypeCode)) {
            throw new IllegalArgumentException("entityTypeCode 不能为空：实体访问必须经 EntityRepository 并指定存储类型");
        }
        return entityTableNameHandler.resolvePhysicalTableName(entityTypeCode.trim());
    }

    // ==================== 私有方法 ====================

    /**
     * 设置动态表名上下文并执行操作。
     */
    private <T> T withTableName(String entityTypeCode, Supplier<T> action) {
        if (!org.springframework.util.StringUtils.hasText(entityTypeCode)) {
            throw new IllegalArgumentException("entityTypeCode 不能为空：实体访问必须经 EntityRepository 并指定存储类型");
        }
        try {
            EntityTableNameContext.set(entityTypeCode.trim());
            return action.get();
        } finally {
            EntityTableNameContext.clear();
        }
    }

    private static final Set<String> CORE_ORDER_BY_COLUMNS = Set.of("name", "code", "status", "id");
    private static final java.util.regex.Pattern SAFE_PHYSICAL_COLUMN =
            java.util.regex.Pattern.compile("^[a-z][a-z0-9_]*$");

    /**
     * 根据 EntityQuery 构建通用查询条件。
     */
    private LambdaQueryWrapperX<EntityDO> buildQueryWrapper(EntityQuery query) {
        LambdaQueryWrapperX<EntityDO> wrapper = new LambdaQueryWrapperX<>();
        wrapper.eqIfPresent(EntityDO::getEntityTypeCode, query.getEntityTypeCode());
        wrapper.eqIfPresent(EntityDO::getModelId, query.getModelId());
        wrapper.eqIfPresent(EntityDO::getStatus, query.getStatus());
        wrapper.eqIfPresent(EntityDO::getDomain,
                query.getDomain() != null && !query.getDomain().isBlank() ? query.getDomain().trim() : null);
        KeywordSearchSql.applyToWrapper(wrapper, query.getKeyword(), query.getKeywordSearch());
        wrapper.eq(EntityDO::getDeleted, false);
        applyPhysicalFilters(wrapper, query.getPhysicalFilters());
        applyScopeMembershipExists(wrapper, query.getScopeRegistryCode());
        applyCoreOrSortOrder(wrapper, query.getOrderByColumn(), query.getOrderAsc());
        if (Boolean.TRUE.equals(query.getRootOnly())) {
            wrapper.isNull(EntityDO::getParentId);
        } else {
            wrapper.eqIfPresent(EntityDO::getParentId, query.getParentId());
        }
        return wrapper;
    }

    /**
     * 划分成员标准收窄：EXISTS dynamic_entity_type_scope。
     * 与分类直查同一套成员表收窄；库内过滤，禁止先拉候选再走字段索引慢路径。
     * <p>禁止写裸 {@code id}：子查询里会解析成成员表自己的 {@code s.id}
     *（成员表也有 id 列），变成 {@code s.entity_id = s.id}，结果恒空。</p>
     * 必须用当前实体物理表限定外层主键（与 CategoryScoped 路径的 {@code e.id} 同义）。
     */
    private void applyScopeMembershipExists(LambdaQueryWrapperX<EntityDO> wrapper,
                                            String scopeRegistryCode) {
        if (scopeRegistryCode == null || scopeRegistryCode.isBlank()) {
            return;
        }
        String entityTypeCode = EntityTableNameContext.get();
        if (entityTypeCode == null || entityTypeCode.isBlank()) {
            throw new IllegalStateException(
                    "划分 EXISTS 需要 EntityTableNameContext（须经 EntityRepository.withTableName）");
        }
        String physicalTable = entityTableNameHandler.resolvePhysicalTableName(entityTypeCode.trim());
        if (!SAFE_PHYSICAL_COLUMN.matcher(physicalTable).matches()) {
            throw new IllegalStateException("非法实体物理表名: " + physicalTable);
        }
        wrapper.apply("""
                EXISTS (
                    SELECT 1
                    FROM dynamic_entity_type_scope s
                    WHERE s.deleted = false
                      AND s.entity_type_code = {0}
                      AND s.entity_id = %s.id
                )
                """.formatted(physicalTable), scopeRegistryCode.trim());
    }

    private void applyPhysicalFilters(LambdaQueryWrapperX<EntityDO> wrapper,
                                      List<PhysicalColumnFilter> physicalFilters) {
        if (physicalFilters == null || physicalFilters.isEmpty()) {
            return;
        }
        for (PhysicalColumnFilter filter : physicalFilters) {
            if (filter == null || filter.column() == null
                    || !SAFE_PHYSICAL_COLUMN.matcher(filter.column()).matches()) {
                continue;
            }
            String col = filter.column();
            String op = filter.op() == null ? "" : filter.op().trim().toUpperCase(Locale.ROOT);
            if ("EQ".equals(op)) {
                wrapper.apply(col + " = {0}", filter.value());
            } else if (("IN".equals(op) || "NOT_IN".equals(op))
                    && filter.value() instanceof Collection<?> collection) {
                List<Object> values = new ArrayList<>();
                for (Object v : collection) {
                    if (v != null) {
                        values.add(v);
                    }
                }
                if (values.isEmpty()) {
                    // IN () → 无命中；NOT_IN () → 不过滤
                    if ("IN".equals(op)) {
                        wrapper.apply("1 = 0");
                    }
                    continue;
                }
                StringBuilder sql = new StringBuilder(col)
                        .append("NOT_IN".equals(op) ? " NOT IN (" : " IN (");
                for (int i = 0; i < values.size(); i++) {
                    if (i > 0) {
                        sql.append(", ");
                    }
                    sql.append("{").append(i).append("}");
                }
                sql.append(")");
                wrapper.apply(sql.toString(), values.toArray());
            }
        }
    }

    /**
     * 核心列或已校验物理列走库内排序并加 id 稳定次序；否则按 sort。
     * 非核心物理列须由上层先校验为可排序基础字段列名。
     */
    private void applyCoreOrSortOrder(LambdaQueryWrapperX<EntityDO> wrapper,
                                      String orderByColumn, Boolean orderAsc) {
        String column = orderByColumn != null ? orderByColumn.trim() : "";
        boolean asc = orderAsc == null || orderAsc;
        if (CORE_ORDER_BY_COLUMNS.contains(column)) {
            switch (column) {
                case "name" -> {
                    if (asc) {
                        wrapper.orderByAsc(EntityDO::getName);
                    } else {
                        wrapper.orderByDesc(EntityDO::getName);
                    }
                }
                case "code" -> {
                    if (asc) {
                        wrapper.orderByAsc(EntityDO::getCode);
                    } else {
                        wrapper.orderByDesc(EntityDO::getCode);
                    }
                }
                case "status" -> {
                    if (asc) {
                        wrapper.orderByAsc(EntityDO::getStatus);
                    } else {
                        wrapper.orderByDesc(EntityDO::getStatus);
                    }
                }
                case "id" -> {
                    if (asc) {
                        wrapper.orderByAsc(EntityDO::getId);
                    } else {
                        wrapper.orderByDesc(EntityDO::getId);
                    }
                }
                default -> wrapper.orderByAsc(EntityDO::getSort);
            }
            if (!"id".equals(column)) {
                wrapper.orderByAsc(EntityDO::getId);
            }
            return;
        }
        String physical = column.toLowerCase(Locale.ROOT);
        if (SAFE_PHYSICAL_COLUMN.matcher(physical).matches()) {
            String dir = asc ? "ASC" : "DESC";
            wrapper.last("ORDER BY " + physical + " " + dir + " NULLS LAST, id ASC");
            return;
        }
        wrapper.orderByAsc(EntityDO::getSort);
    }
}
