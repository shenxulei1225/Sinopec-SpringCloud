package cn.cheers.x.module.dynamicbusiness.service.entity.core;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.EntityRepository;
import cn.cheers.x.module.dynamicbusiness.framework.hierarchy.IdTreeHierarchy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Entity Core Service 实现
 *
 * <p>管什么：实体 CRUD 与实体树 parentId/treePath 移动（算法见 {@link IdTreeHierarchy}）。</p>
 * <p>不负责：分类树投影对齐（由高级分类层级服务在门面层调用）。</p>
 */
@Service
@Slf4j
public class EntityCoreServiceImpl implements EntityCoreService {

    @Resource
    private EntityRepository entityRepository;

    @Override
    public boolean existsById(Long entityId, String entityTypeCode) {
        if (entityId == null || entityTypeCode == null || entityTypeCode.isEmpty()) {
            return false;
        }
        try {
            return entityRepository.exists(entityId, entityTypeCode);
        } catch (Exception e) {
            log.warn("检查实体存在性失败: entityId={}, entityTypeCode={}, error={}", entityId, entityTypeCode, e.getMessage());
            return false;
        }
    }

    @Override
    public Set<Long> filterExistingEntityIds(List<Long> entityIds, String entityTypeCode) {
        if (entityIds == null || entityIds.isEmpty() || entityTypeCode == null || entityTypeCode.isEmpty()) {
            return new HashSet<>();
        }

        try {
            List<EntityDO> existingEntities = entityRepository.findByIds(entityIds, entityTypeCode);
            return existingEntities.stream().map(EntityDO::getId).collect(Collectors.toSet());
        } catch (Exception e) {
            log.warn("批量检查实体存在性失败: entityIds={}, entityTypeCode={}, error={}", entityIds, entityTypeCode, e.getMessage());
            Set<Long> existingIds = new HashSet<>();
            for (Long entityId : entityIds) {
                if (existsById(entityId, entityTypeCode)) {
                    existingIds.add(entityId);
                }
            }
            return existingIds;
        }
    }

    @Override
    public Long create(EntityDO entity) {
        return create(entity, null);
    }

    @Override
    public Long create(EntityDO entity, Map<String, Object> physicalColumns) {
        if (entity == null) {
            throw new ServiceException(400, "实体数据不能为空");
        }
        // 1. 保存以获取 ID（有固定列时与核心列同 INSERT）
        entityRepository.save(entity, physicalColumns);
        Long entityId = entity.getId();

        // 2. 按统一树核心生成 treePath
        String treePath = IdTreeHierarchy.buildPath(null, entityId);

        // 若有父节点，则拼接父节点 treePath
        if (entity.getParentId() != null && entity.getParentId() != 0) {
            EntityDO parent = entityRepository.findById(entity.getParentId(), entity.getEntityTypeCode());
            if (parent == null) {
                throw new ServiceException(404, "父实体不存在");
            }
            String parentTreePath = parent.getTreePath();
            if (parentTreePath == null || parentTreePath.isBlank()) {
                parentTreePath = IdTreeHierarchy.buildPath(null, parent.getId());
            }
            if (IdTreeHierarchy.wouldCreateCycle(entityId, parentTreePath)) {
                throw new ServiceException(400, "不能将实体挂到自己的子孙节点下");
            }
            treePath = IdTreeHierarchy.buildPath(parentTreePath, entityId);
        }

        entity.setTreePath(treePath);
        entityRepository.update(entity);
        return entityId;
    }

    @Override
    public void update(EntityDO entity) {
        if (entity == null || entity.getId() == null) {
            throw new ServiceException(400, "实体数据不能为空");
        }
        entityRepository.update(entity);
    }

    @Override
    public void updateBatch(List<EntityDO> entities) {
        if (entities == null || entities.isEmpty()) {
            return;
        }
        entityRepository.updateBatch(entities);
    }

    @Override
    public void delete(Long id, String entityTypeCode) {
        if (id == null || entityTypeCode == null || entityTypeCode.isEmpty()) {
            throw new ServiceException(400, "参数不完整");
        }
        entityRepository.delete(id, entityTypeCode);
    }

    @Override
    public EntityDO get(Long id, String entityTypeCode) {
        if (id == null || entityTypeCode == null || entityTypeCode.isEmpty()) {
            return null;
        }
        return entityRepository.findById(id, entityTypeCode);
    }

    @Override
    public List<EntityDO> listEntities(String entityTypeCode, Long modelId, Integer status) {
        EntityRepository.EntityQuery query = EntityRepository.EntityQuery.builder()
                .entityTypeCode(entityTypeCode)
                .modelId(modelId)
                .status(status)
                .build();
        return entityRepository.findAll(query);
    }

    @Override
    public PageResult<EntityDO> pageEntities(String entityTypeCode, Long modelId, Integer status, String keyword, Integer pageNo, Integer pageSize) {
        EntityRepository.EntityQuery query = EntityRepository.EntityQuery.builder()
                .entityTypeCode(entityTypeCode)
                .modelId(modelId)
                .status(status)
                .keyword(keyword)
                .pageNo(pageNo)
                .pageSize(pageSize)
                .build();
        return entityRepository.findPage(query);
    }

    @Override
    public PageResult<EntityDO> pageEntitiesByModelIds(String entityTypeCode, List<Long> modelIds, Integer status,
                                                        String keyword, Integer pageNo, Integer pageSize) {
        if (entityTypeCode == null || entityTypeCode.isEmpty() || modelIds == null || modelIds.isEmpty()) {
            return new PageResult<>(Collections.emptyList(), 0L);
        }
        return entityRepository.findPageByModelIds(modelIds, entityTypeCode, status, keyword, null, pageNo, pageSize);
    }

    @Override
    public void moveEntity(Long entityId, String entityTypeCode, Long newParentId) {
        if (entityTypeCode == null || entityTypeCode.isEmpty()) {
            throw new ServiceException(400, "entityTypeCode 不能为空");
        }
        Long normalizedParent = IdTreeHierarchy.normalizeParentId(newParentId);
        if (Objects.equals(entityId, normalizedParent)) {
            throw new ServiceException(400, "不能将实体移动到自身下方");
        }

        EntityDO entity = entityRepository.findById(entityId, entityTypeCode);
        if (entity == null) {
            throw new ServiceException(404, "实体不存在");
        }

        String oldTreePath = entity.getTreePath();
        String parentTreePath = null;
        if (normalizedParent != null) {
            EntityDO newParent = entityRepository.findById(normalizedParent, entityTypeCode);
            if (newParent == null) {
                throw new ServiceException(404, "新的父实体不存在");
            }
            parentTreePath = newParent.getTreePath();
            if (parentTreePath == null || parentTreePath.isBlank()) {
                parentTreePath = IdTreeHierarchy.buildPath(null, newParent.getId());
            }
            if (IdTreeHierarchy.wouldCreateCycle(entityId, parentTreePath)) {
                throw new ServiceException(400, "不能将实体移动到自己的子孙节点下");
            }
        }

        IdTreeHierarchy.MovePlan plan = IdTreeHierarchy.planMove(entity.getId(), normalizedParent, parentTreePath);
        entity.setParentId(plan.newParentId());
        entity.setTreePath(plan.newTreePath());
        entityRepository.update(entity);

        updateChildrenTreePaths(entity.getId(), plan.newTreePath(), oldTreePath, entityTypeCode);
    }

    private void updateChildrenTreePaths(Long parentId, String newTreePath, String oldTreePath, String entityTypeCode) {
        if (oldTreePath == null || oldTreePath.isEmpty()) {
            return;
        }
        List<EntityDO> children = entityRepository.findByTreePathStartsWith(oldTreePath, entityTypeCode);
        if (children == null || children.isEmpty()) {
            return;
        }

        List<EntityDO> updates = new ArrayList<>();
        for (EntityDO child : children) {
            if (child.getId().equals(parentId)) {
                continue;
            }
            String childTreePath = child.getTreePath();
            if (childTreePath == null || !childTreePath.startsWith(oldTreePath)) {
                continue;
            }
            child.setTreePath(IdTreeHierarchy.replaceSubtreePrefix(childTreePath, oldTreePath, newTreePath));
            updates.add(child);
        }

        if (!updates.isEmpty()) {
            entityRepository.updateBatch(updates);
        }
    }

    @Override
    public List<EntityDO> listTreeEntities(String entityTypeCode, Long modelId) {
        if (entityTypeCode == null || entityTypeCode.isEmpty()) {
            throw new ServiceException(400, "查询实体树必须提供 entityTypeCode");
        }
        EntityRepository.EntityQuery query = EntityRepository.EntityQuery.builder()
                .entityTypeCode(entityTypeCode)
                .modelId(modelId)
                .build();
        return entityRepository.findAll(query);
    }

    @Override
    public List<EntityDO> listEntitiesByParentId(String entityTypeCode, Long parentId) {
        if (entityTypeCode == null || entityTypeCode.isEmpty()) {
            throw new ServiceException(400, "entityTypeCode 不能为空");
        }
        EntityRepository.EntityQuery.EntityQueryBuilder builder = EntityRepository.EntityQuery.builder()
                .entityTypeCode(entityTypeCode);
        if (parentId == null) {
            builder.rootOnly(true);
        } else {
            builder.parentId(parentId);
        }
        return entityRepository.findAll(builder.build());
    }

    @Override
    public PageResult<EntityDO> pageEntitiesByParentId(String entityTypeCode, Long parentId, Integer pageNo, Integer pageSize) {
        if (entityTypeCode == null || entityTypeCode.isEmpty()) {
            throw new ServiceException(400, "entityTypeCode 不能为空");
        }
        EntityRepository.EntityQuery.EntityQueryBuilder builder = EntityRepository.EntityQuery.builder()
                .entityTypeCode(entityTypeCode)
                .pageNo(pageNo == null || pageNo < 1 ? 1 : pageNo)
                .pageSize(pageSize == null || pageSize < 1 ? 200 : Math.min(pageSize, 500));
        if (parentId == null) {
            builder.rootOnly(true);
        } else {
            builder.parentId(parentId);
        }
        return entityRepository.findPage(builder.build());
    }

    @Override
    public List<String> getEntityPath(Long entityId, String entityTypeCode) {
        if (entityTypeCode == null || entityTypeCode.isEmpty()) {
            throw new ServiceException(400, "entityTypeCode 不能为空");
        }
        EntityDO entity = entityRepository.findById(entityId, entityTypeCode);
        if (entity == null) {
            throw new ServiceException(404, "实体不存在");
        }
        List<String> path = new ArrayList<>();
        path.add(entity.getName());

        Long currentParentId = entity.getParentId();
        int maxDepth = 100;
        int depth = 0;

        while (currentParentId != null && currentParentId > 0 && depth < maxDepth) {
            EntityDO parentEntity = entityRepository.findById(currentParentId, entityTypeCode);
            if (parentEntity == null) {
                break;
            }
            path.add(0, parentEntity.getName());
            currentParentId = parentEntity.getParentId();
            depth++;
        }

        return path;
    }

    @Override
    public List<EntityDO> listByIds(List<Long> ids, String entityTypeCode) {
        if (ids == null || ids.isEmpty() || entityTypeCode == null || entityTypeCode.isEmpty()) {
            return Collections.emptyList();
        }
        return entityRepository.findByIds(ids, entityTypeCode);
    }

    @Override
    public List<EntityDO> listByIdsWithDedicatedBaseFields(List<Long> orderedIds, String entityTypeCode) {
        return listByIdsWithDedicatedBaseFields(orderedIds, entityTypeCode, true);
    }

    @Override
    public List<EntityDO> listByIdsWithDedicatedBaseFields(List<Long> orderedIds, String entityTypeCode,
                                                           boolean includeCustomFields) {
        if (orderedIds == null || orderedIds.isEmpty() || entityTypeCode == null || entityTypeCode.isEmpty()) {
            return Collections.emptyList();
        }
        return entityRepository.findByIdsWithDedicatedBaseFields(
                orderedIds, entityTypeCode, includeCustomFields);
    }

    @Override
    public List<Long> getEntityIdsByModelId(Long modelId, String entityTypeCode) {
        if (modelId == null) {
            return Collections.emptyList();
        }
        if (entityTypeCode == null || entityTypeCode.isEmpty()) {
            throw new ServiceException(400, "entityTypeCode 不能为空");
        }
        List<EntityDO> entities = entityRepository.findByModelId(modelId, entityTypeCode);
        return entities.stream().map(EntityDO::getId).collect(Collectors.toList());
    }

    @Override
    public boolean existsByParentId(Long parentId, String entityTypeCode) {
        if (parentId == null) {
            return false;
        }
        if (entityTypeCode == null || entityTypeCode.isEmpty()) {
            throw new ServiceException(400, "entityTypeCode 不能为空");
        }
        return entityRepository.existsByParentId(parentId, entityTypeCode);
    }
}

