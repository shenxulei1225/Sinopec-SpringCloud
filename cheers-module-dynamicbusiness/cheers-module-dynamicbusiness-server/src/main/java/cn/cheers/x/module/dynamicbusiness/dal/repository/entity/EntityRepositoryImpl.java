package cn.cheers.x.module.dynamicbusiness.dal.repository.entity;

import cn.hutool.core.collection.CollUtil;
import cn.cheers.x.framework.common.pojo.PageParam;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntityMapper;
import cn.cheers.x.module.dynamicbusiness.framework.entity.EntityTableNameContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
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
                                                    Integer status, String keyword, Integer pageNo, Integer pageSize) {
        if (CollUtil.isEmpty(modelIds)) {
            return new PageResult<>(Collections.emptyList(), 0L);
        }
        return withTableName(entityTypeCode, () -> {
            LambdaQueryWrapperX<EntityDO> wrapper = new LambdaQueryWrapperX<>();
            wrapper.in(EntityDO::getModelId, modelIds)
                    .eqIfPresent(EntityDO::getStatus, status)
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

    // ==================== 私有方法 ====================

    /**
     * 设置动态表名上下文并执行操作。
     */
    private <T> T withTableName(String entityTypeCode, Supplier<T> action) {
        try {
            EntityTableNameContext.set(entityTypeCode);
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
