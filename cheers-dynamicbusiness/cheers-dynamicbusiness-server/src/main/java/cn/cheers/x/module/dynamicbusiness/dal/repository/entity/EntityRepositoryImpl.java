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
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
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
     * 一次 SELECT：核心列 + 全部启用基础字段物理列；按 orderedIds 保序。
     */
    @Override
    public List<EntityDO> findByIdsWithDedicatedBaseFields(List<Long> orderedIds, String entityTypeCode) {
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
                "SELECT id, entity_type_code, model_id, name, code, status, parent_id, domain, sort, custom_fields");
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
            EntityDO entity = mapCoreRow(row);
            if (entity.getId() == null) {
                continue;
            }
            Map<String, Object> baseValues = new LinkedHashMap<>();
            for (PhysicalFieldSpec spec : specs) {
                Object dbVal = readColumn(row, spec.columnName());
                if (dbVal != null) {
                    baseValues.put(spec.fieldCode(), dbVal);
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

    private static EntityDO mapCoreRow(Map<String, Object> row) {
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
        Object custom = readColumn(row, "custom_fields");
        if (custom != null) {
            entity.setCustomFields(JsonbMapTypeHandler.parse(String.valueOf(custom)));
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
     * 按模型 ID 列表分页查询实体。
     */
    @Override
    public PageResult<EntityDO> findPageByModelIds(List<Long> modelIds, String entityTypeCode,
                                                    Integer status, String keyword, String domain,
                                                    Integer pageNo, Integer pageSize) {
        if (CollUtil.isEmpty(modelIds)) {
            return new PageResult<>(Collections.emptyList(), 0L);
        }
        return withTableName(entityTypeCode, () -> {
            LambdaQueryWrapperX<EntityDO> wrapper = new LambdaQueryWrapperX<>();
            wrapper.in(EntityDO::getModelId, modelIds)
                    .eqIfPresent(EntityDO::getStatus, status)
                    .eqIfPresent(EntityDO::getDomain, domain != null ? domain.trim() : null)
                    .likeIfPresent(EntityDO::getName, keyword)
                    .eq(EntityDO::getDeleted, false);
            wrapper.orderByAsc(EntityDO::getSort);
            PageParam pageParam = new PageParam();
            pageParam.setPageNo(pageNo != null && pageNo > 0 ? pageNo : 1);
            pageParam.setPageSize(pageSize != null && pageSize > 0 ? pageSize : 20);
            return entityMapper.selectPage(pageParam, wrapper);
        });
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
        wrapper.likeIfPresent(EntityDO::getName, query.getKeyword());
        wrapper.eq(EntityDO::getDeleted, false);
        wrapper.orderByAsc(EntityDO::getSort);
        if (Boolean.TRUE.equals(query.getRootOnly())) {
            wrapper.isNull(EntityDO::getParentId);
        } else {
            wrapper.eqIfPresent(EntityDO::getParentId, query.getParentId());
        }
        return wrapper;
    }
}
