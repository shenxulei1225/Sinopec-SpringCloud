package cn.cheers.x.module.dynamicbusiness.service.model.relation;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelEntityAssociationRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelEntityRelationDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelEntityRelationMapper;
import cn.cheers.x.module.dynamicbusiness.framework.entitytype.EntityTypeScopeContext;
import cn.cheers.x.module.dynamicbusiness.framework.entitytype.EntityTypeScopeResolver;
import cn.cheers.x.module.dynamicbusiness.framework.tenant.TenantAssociationTableService;
import cn.cheers.x.module.dynamicbusiness.service.entity.core.EntityCoreService;
import cn.cheers.x.module.dynamicbusiness.service.model.core.ModelCoreService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 型号—实体多对多关联实现。
 */
@Service
@Validated
@Slf4j
public class ModelEntityRelationServiceImpl implements ModelEntityRelationService {

    @Resource
    private ModelEntityRelationMapper relationMapper;

    @Resource
    private EntityCoreService entityCoreService;

    @Resource
    private ModelCoreService modelCoreService;

    @Resource
    private EntityTypeScopeResolver entityTypeScopeResolver;

    @Resource
    private TenantAssociationTableService tenantAssociationTableService;

    private void ensureTenantTable() {
        tenantAssociationTableService.ensureCurrentTenantAssociationTables();
    }

    private String resolveStorage(String entityTypeCode, String label) {
        if (!StringUtils.hasText(entityTypeCode)) {
            throw new ServiceException(400, label + " 不能为空");
        }
        String storage = entityTypeScopeResolver.resolveStorageEntityTypeCode(entityTypeCode.trim());
        if (!StringUtils.hasText(storage)) {
            throw new ServiceException(400, label + " 不能为空");
        }
        return storage.trim();
    }

    private void requireModelExists(Long modelId, String modelEntityTypeCode) {
        if (modelId == null) {
            throw new ServiceException(400, "modelId 不能为空");
        }
        Set<Long> existing = modelCoreService.filterExistingModelIds(List.of(modelId), modelEntityTypeCode);
        if (existing == null || !existing.contains(modelId)) {
            throw new ServiceException(404, "型号不存在或不属于类型 " + modelEntityTypeCode);
        }
    }

    private String requireEntityDomain(Long entityId, String entityTypeCode) {
        EntityDO entity = entityCoreService.get(entityId, entityTypeCode);
        if (entity == null) {
            throw new ServiceException(404, "实体不存在");
        }
        return EntityTypeScopeContext.normalizeDomain(entity.getDomain());
    }

    private int nextSort(Long modelId, String modelEntityTypeCode, String entityTypeCode) {
        Integer max = relationMapper.maxSort(modelId, modelEntityTypeCode, entityTypeCode);
        return (max == null ? 0 : max) + SparseStep.STEP;
    }

    /** 稀疏排序步长（与分类关联同量级即可） */
    private static final class SparseStep {
        static final int STEP = 1024;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ModelEntityAssociationRespVO associate(Long modelId, Long entityId,
                                                  String modelEntityTypeCode, String entityTypeCode) {
        long start = System.currentTimeMillis();
        ensureTenantTable();
        String modelType = resolveStorage(modelEntityTypeCode, "modelEntityTypeCode");
        String entityType = resolveStorage(entityTypeCode, "entityTypeCode");
        if (modelType.equals(entityType)) {
            throw new ServiceException(400,
                    "本类型实体归属请写实体表 model_id，勿写入型号—实体关联表（modelEntityTypeCode 与 entityTypeCode 相同）");
        }
        if (entityId == null) {
            throw new ServiceException(400, "entityId 不能为空");
        }
        requireModelExists(modelId, modelType);
        String domain = requireEntityDomain(entityId, entityType);

        if (existsRelation(modelId, entityId, modelType, entityType)) {
            return ok("ASSOCIATE", modelId, entityId, 1, 0, List.of(entityId), List.of(), start);
        }

        int sort = nextSort(modelId, modelType, entityType);
        int restored = relationMapper.restoreDeleted(modelId, entityId, modelType, entityType, domain, sort);
        if (restored > 0) {
            log.info("恢复型号—实体关联: modelId={}, entityId={}, modelType={}, entityType={}",
                    modelId, entityId, modelType, entityType);
            return ok("ASSOCIATE", modelId, entityId, 1, 0, List.of(entityId), List.of(), start);
        }

        ModelEntityRelationDO row = ModelEntityRelationDO.builder()
                .modelId(modelId)
                .modelEntityTypeCode(modelType)
                .entityId(entityId)
                .entityTypeCode(entityType)
                .domain(domain)
                .sort(sort)
                .build();
        relationMapper.insert(row);
        log.info("创建型号—实体关联: modelId={}, entityId={}, modelType={}, entityType={}",
                modelId, entityId, modelType, entityType);
        return ok("ASSOCIATE", modelId, entityId, 1, 0, List.of(entityId), List.of(), start);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ModelEntityAssociationRespVO disassociate(Long modelId, Long entityId,
                                                     String modelEntityTypeCode, String entityTypeCode) {
        long start = System.currentTimeMillis();
        ensureTenantTable();
        String modelType = resolveStorage(modelEntityTypeCode, "modelEntityTypeCode");
        String entityType = resolveStorage(entityTypeCode, "entityTypeCode");
        if (modelId == null || entityId == null) {
            return ok("DISASSOCIATE", modelId, entityId, 0, 1, List.of(),
                    List.of("modelId/entityId 不能为空"), start);
        }
        relationMapper.softDelete(modelId, entityId, modelType, entityType);
        return ok("DISASSOCIATE", modelId, entityId, 1, 0, List.of(entityId), List.of(), start);
    }

    @Override
    public boolean existsRelation(Long modelId, Long entityId,
                                  String modelEntityTypeCode, String entityTypeCode) {
        if (modelId == null || entityId == null
                || !StringUtils.hasText(modelEntityTypeCode) || !StringUtils.hasText(entityTypeCode)) {
            return false;
        }
        ensureTenantTable();
        String modelType = resolveStorage(modelEntityTypeCode, "modelEntityTypeCode");
        String entityType = resolveStorage(entityTypeCode, "entityTypeCode");
        return relationMapper.selectActive(modelId, entityId, modelType, entityType) != null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ModelEntityAssociationRespVO batchAssociateEntitiesToModel(Long modelId, List<Long> entityIds,
                                                                      String modelEntityTypeCode,
                                                                      String entityTypeCode) {
        long start = System.currentTimeMillis();
        ensureTenantTable();
        String modelType = resolveStorage(modelEntityTypeCode, "modelEntityTypeCode");
        String entityType = resolveStorage(entityTypeCode, "entityTypeCode");
        if (modelType.equals(entityType)) {
            throw new ServiceException(400,
                    "本类型实体归属请写实体表 model_id，勿写入型号—实体关联表");
        }
        requireModelExists(modelId, modelType);
        List<Long> ids = normalizeIds(entityIds);
        if (ids.isEmpty()) {
            return ok("BATCH_ASSOCIATE", modelId, null, 0, 0, List.of(), List.of(), start);
        }

        Map<Long, EntityDO> entities = entityCoreService.listByIds(ids, entityType).stream()
                .filter(Objects::nonNull)
                .collect(java.util.stream.Collectors.toMap(EntityDO::getId, e -> e, (a, b) -> a));

        List<Long> okIds = new ArrayList<>();
        List<String> fails = new ArrayList<>();
        for (Long entityId : ids) {
            EntityDO entity = entities.get(entityId);
            if (entity == null) {
                fails.add("实体不存在: " + entityId);
                continue;
            }
            try {
                associate(modelId, entityId, modelType, entityType);
                okIds.add(entityId);
            } catch (ServiceException ex) {
                fails.add(entityId + ": " + ex.getMessage());
            }
        }
        return ok("BATCH_ASSOCIATE", modelId, null, okIds.size(), fails.size(), okIds, fails, start);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ModelEntityAssociationRespVO batchDisassociateEntitiesFromModel(Long modelId, List<Long> entityIds,
                                                                           String modelEntityTypeCode,
                                                                           String entityTypeCode) {
        long start = System.currentTimeMillis();
        ensureTenantTable();
        String modelType = resolveStorage(modelEntityTypeCode, "modelEntityTypeCode");
        String entityType = resolveStorage(entityTypeCode, "entityTypeCode");
        List<Long> ids = normalizeIds(entityIds);
        List<Long> okIds = new ArrayList<>();
        for (Long entityId : ids) {
            relationMapper.softDelete(modelId, entityId, modelType, entityType);
            okIds.add(entityId);
        }
        return ok("BATCH_DISASSOCIATE", modelId, null, okIds.size(), 0, okIds, List.of(), start);
    }

    @Override
    public List<Long> listEntityIdsByModelId(Long modelId, String modelEntityTypeCode, String entityTypeCode) {
        if (modelId == null) {
            return List.of();
        }
        return listEntityIdsByModelIds(List.of(modelId), modelEntityTypeCode, entityTypeCode);
    }

    @Override
    public List<Long> listEntityIdsByModelIds(List<Long> modelIds, String modelEntityTypeCode,
                                              String entityTypeCode) {
        List<Long> ids = normalizeIds(modelIds);
        if (ids.isEmpty() || !StringUtils.hasText(modelEntityTypeCode) || !StringUtils.hasText(entityTypeCode)) {
            return List.of();
        }
        ensureTenantTable();
        String modelType = resolveStorage(modelEntityTypeCode, "modelEntityTypeCode");
        String entityType = resolveStorage(entityTypeCode, "entityTypeCode");
        List<ModelEntityRelationDO> rows = relationMapper.selectByModels(ids, modelType, entityType);
        LinkedHashSet<Long> ordered = new LinkedHashSet<>();
        // 按请求型号顺序展开
        Map<Long, List<ModelEntityRelationDO>> byModel = rows.stream()
                .collect(java.util.stream.Collectors.groupingBy(ModelEntityRelationDO::getModelId,
                        java.util.LinkedHashMap::new, java.util.stream.Collectors.toList()));
        for (Long mid : ids) {
            List<ModelEntityRelationDO> group = byModel.get(mid);
            if (group == null) {
                continue;
            }
            for (ModelEntityRelationDO row : group) {
                if (row.getEntityId() != null) {
                    ordered.add(row.getEntityId());
                }
            }
        }
        return new ArrayList<>(ordered);
    }

    @Override
    public List<Long> listModelIdsByEntityId(Long entityId, String entityTypeCode, String modelEntityTypeCode) {
        if (entityId == null || !StringUtils.hasText(entityTypeCode)) {
            return List.of();
        }
        ensureTenantTable();
        String entityType = resolveStorage(entityTypeCode, "entityTypeCode");
        String modelType = StringUtils.hasText(modelEntityTypeCode)
                ? resolveStorage(modelEntityTypeCode, "modelEntityTypeCode")
                : null;
        return relationMapper.selectByEntity(entityId, entityType, modelType).stream()
                .map(ModelEntityRelationDO::getModelId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    private static List<Long> normalizeIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return ids.stream().filter(Objects::nonNull).distinct().toList();
    }

    private static ModelEntityAssociationRespVO ok(String op, Long modelId, Long entityId,
                                                   int success, int fail, List<Long> successIds,
                                                   List<String> fails, long start) {
        return ModelEntityAssociationRespVO.builder()
                .operationType(op)
                .modelId(modelId)
                .entityId(entityId)
                .successCount(success)
                .failCount(fail)
                .totalCount(success + fail)
                .successEntityIds(successIds)
                .failMessages(fails)
                .executionTime(System.currentTimeMillis() - start)
                .build();
    }
}
