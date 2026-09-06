package cn.cheers.x.module.dynamicbusiness.service.entity;
import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.*;
import cn.cheers.x.module.dynamicbusiness.convert.entity.EntityDoVoHelper;
import cn.cheers.x.module.dynamicbusiness.convert.entity.EntityFieldMapsSupport;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryEntityLinkDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityRelationDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelFieldAssignmentDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.category.CategoryMapper;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityCategoryRelationDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntityCategoryRelationMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntityRelationMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytypescope.EntityTypeScopeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeBaseFieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.field.FieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelFieldAssignmentMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeBaseFieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.EntityRepository;
import cn.cheers.x.module.dynamicbusiness.service.entity.core.EntityCoreService;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryService;
import cn.cheers.x.module.dynamicbusiness.framework.hierarchy.IdTreeHierarchy;
import cn.cheers.x.module.dynamicbusiness.service.field.CustomFieldValidationService;
import cn.cheers.x.module.dynamicbusiness.enums.field.FieldTypeEnum;
import cn.cheers.x.module.dynamicbusiness.enums.entity.EntityQueryScene;
import cn.cheers.x.module.dynamicbusiness.service.entity.relation.EntityCategoryRelationService;
import cn.cheers.x.module.dynamicbusiness.service.entity.refcategory.EntityRefCategoryProjectionService;
import cn.cheers.x.module.dynamicbusiness.service.entity.refdisplay.EntityRefDisplayEnrichService;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryEntityLinkService;
import cn.cheers.x.module.dynamicbusiness.util.SparseSortUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import jakarta.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
import cn.cheers.x.module.dynamicbusiness.service.entity.scene.EntitySceneQueryService;

/**
 * 实体服务实现：CRUD、关联块、批量与排序；按场景列表/树查询委托 {@link EntitySceneQueryService}。
 *
 * @author 基础服务模块
 */
@Service
@Validated
@Slf4j
public class EntityServiceImpl implements EntityService {

    private static final String ENTITY_NOT_EXISTS = "实体不存在";
    @Resource
    private EntityCoreService entityCoreService;
    @Resource
    private cn.cheers.x.module.dynamicbusiness.service.category.hierarchy.AdvancedCategoryEntityHierarchyService advancedCategoryEntityHierarchyService;
    @Resource
    private EntityBusinessHelper entityBusinessHelper;
    @Resource
    private EntityCacheEvictionService entityCacheEvictionService;
    @Resource
    private EntityLifecycleEventPublisher entityLifecycleEventPublisher;
    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private ModelMapper modelMapper;
    @Resource
    private EntityCategoryRelationMapper entityCategoryRelationMapper;
    @Resource
    private EntityRelationMapper entityRelationMapper;
    @Resource
    private EntityRepository entityRepository;
    @Resource
    private EntityTypeBaseFieldMapper entityTypeBaseFieldMapper;
    @Resource
    private CustomFieldValidationService customFieldValidationService;
    @Resource
    private FieldMapper fieldMapper;
    @Resource
    private ModelFieldAssignmentMapper modelFieldAssignmentMapper;
    @Resource
    @Lazy
    private EntityCategoryRelationService entityCategoryRelationService;
    @Resource
    private EntityRelationSyncService entityRelationSyncService;
    @Resource
    private EntityRefCategoryProjectionService entityRefCategoryProjectionService;
    @Resource
    private EntityRefDisplayEnrichService entityRefDisplayEnrichService;
    @Resource
    private EntityDedicatedColumnService entityDedicatedColumnService;
    @Resource
    private CategoryEntityLinkService categoryEntityLinkService;
    @Resource
    private EntityTypeScopeMapper entityTypeScopeMapper;
    @Resource
    private EntityTypeMapper entityTypeMapper;
    @Resource
    @Lazy
    private CategoryService categoryService;

    @Resource
    private EntitySceneQueryService entitySceneQueryService;

    @Override
    public EntitySceneQueryRespVO queryEntities(EntityQueryScene scene, String resultShape, String resultDetail, String categoryTypeCode, String entityTypeCode,
            List<Long> modelIds, String modelEntityTypeCode, List<Long> categoryIds, List<CategoryIdGroupReqVO> categoryIdGroups, String categoryViaRefPathCode,
            String categoryFilterMode,
            Long entityId, Long rootEntityId, String entitySourceEntityType,
            Integer pageNo, Integer pageSize, String keyword, String domain,
            List<FieldFilterReqVO> filters, String orderByColumn, Boolean isAsc,
            List<String> searchFieldCodes) {
        return entitySceneQueryService.queryEntities(scene, resultShape, resultDetail, categoryTypeCode, entityTypeCode,
                modelIds, modelEntityTypeCode, categoryIds, categoryIdGroups, categoryViaRefPathCode,
                categoryFilterMode, entityId, rootEntityId, entitySourceEntityType,
                pageNo, pageSize, keyword, domain, filters, orderByColumn, isAsc, searchFieldCodes);
    }

    @Override
    public EntityRespVO getCategoryLinkedEntity(Long categoryId, String entityTypeCode) {
        return entitySceneQueryService.getCategoryLinkedEntity(categoryId, entityTypeCode);
    }

    @Override
    public List<EntityRespVO> getEntityTreeByModelId(String entityTypeCode, Long modelId) {
        return entitySceneQueryService.getEntityTreeByModelId(entityTypeCode, modelId);
    }

    @Override
    public List<Long> listDistinctModelIdsByCategoryScope(String entityTypeCode,
                                                          List<Long> categoryIds,
                                                          List<CategoryIdGroupReqVO> categoryIdGroups,
                                                          String categoryTypeCode,
                                                          String domain) {
        return listDistinctModelIdsByCategoryScope(
                entityTypeCode, categoryIds, categoryIdGroups, categoryTypeCode, domain, true);
    }

    @Override
    public List<Long> listDistinctModelIdsByCategoryScope(String entityTypeCode,
                                                          List<Long> categoryIds,
                                                          List<CategoryIdGroupReqVO> categoryIdGroups,
                                                          String categoryTypeCode,
                                                          String domain,
                                                          Boolean includeDescendants) {
        return entitySceneQueryService.listDistinctModelIdsByCategoryScope(
                entityTypeCode, categoryIds, categoryIdGroups, categoryTypeCode, domain, includeDescendants);
    }

    /**
     * 创建实体（本体落库 + 关系同步 + 缓存失效 + 事件发布）。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(EntityCreateReqVO reqVO) {
        EntityDO data = entityBusinessHelper.prepareCreateEntity(reqVO);
        // prepare 后 customFields 已含全部非核心字段（含 REF）；抽固定列前先留下，供写关联表
        Map<String, Object> fieldValues = new LinkedHashMap<>(
                entityBusinessHelper.emptyIfNull(data.getCustomFields()));
        Map<String, Object> physicalColumns = entityDedicatedColumnService.extractPhysicalValuesAndStrip(
                data.getEntityTypeCode(), data.getCustomFields());
        Long entityId;
        try {
            // 固定列与核心列同 INSERT，避免专用表 NOT NULL 列在「先插后更」时失败
            entityId = entityCoreService.create(data, physicalColumns);
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            throw translateDataIntegrityError(ex, data.getEntityTypeCode());
        }
        data.setId(entityId);

        ModelDO model = modelMapper.selectById(EntityFieldMapsSupport.getRequiredModelId(reqVO.getBaseFields()));
        entityRelationSyncService.syncRelationsOnCreate(data, model, fieldValues);
        // REF 实体–实体同步之后：凡 REF 目标为分类即实体，投影分类–实体（场景 2）
        entityRefCategoryProjectionService.projectOnCreate(data, fieldValues);

        entityCacheEvictionService.evictEntityCaches(
                EntityFieldMapsSupport.getRequiredModelId(reqVO.getBaseFields()),
                EntityFieldMapsSupport.getRequiredEntityTypeCode(reqVO.getBaseFields()));
        entityLifecycleEventPublisher.publishEntityCreatedEvent(
                EntityFieldMapsSupport.getRequiredModelId(reqVO.getBaseFields()),
                entityId,
                EntityFieldMapsSupport.getRequiredEntityTypeCode(reqVO.getBaseFields()),
                data);
        return entityId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long cloneEntity(EntityCloneReqVO reqVO) {
        String entityTypeCode = reqVO.getEntityTypeCode() == null ? "" : reqVO.getEntityTypeCode().trim();
        if (!StringUtils.hasText(entityTypeCode)) {
            throw new ServiceException(400, "业务类型编码不能为空");
        }
        EntityRespVO source = get(reqVO.getSourceEntityId(), entityTypeCode);
        if (source == null) {
            throw new ServiceException(404, "源实体不存在");
        }

        Map<String, Object> baseFields = new LinkedHashMap<>(
                EntityFieldMapsSupport.normalizeMap(source.getBaseFields()));
        Map<String, Object> customFields = source.getCustomFields() == null
                ? new LinkedHashMap<>()
                : new LinkedHashMap<>(source.getCustomFields());

        // 生成新实例：清掉身份字段，强制沿用源型号与类型
        for (String key : List.of("id", "code", "guid", "createTime", "updateTime", "creator", "updater")) {
            baseFields.remove(key);
            customFields.remove(key);
        }
        baseFields.put("entityTypeCode", entityTypeCode);
        baseFields.put("modelId", source.getModelId());
        baseFields.put("name", reqVO.getName().trim());
        if (reqVO.getDescription() != null) {
            baseFields.put("description", reqVO.getDescription());
        } else if (!baseFields.containsKey("description") && customFields.containsKey("description")) {
            // 保持源描述在 custom 侧不动
        }
        if (baseFields.get("status") == null) {
            baseFields.put("status", source.getStatus() != null ? source.getStatus() : 1);
        }

        EntityCreateReqVO createReq = new EntityCreateReqVO();
        createReq.setBaseFields(baseFields);
        createReq.setCustomFields(customFields.isEmpty() ? null : customFields);
        Long newId = create(createReq);

        LinkedHashSet<Long> categoryIds = new LinkedHashSet<>();
        List<EntityCategoryRelationDO> sourceRels =
                entityCategoryRelationMapper.selectByEntityId(reqVO.getSourceEntityId());
        if (sourceRels != null) {
            for (EntityCategoryRelationDO rel : sourceRels) {
                if (rel != null && rel.getCategoryId() != null && rel.getCategoryId() > 0) {
                    categoryIds.add(rel.getCategoryId());
                }
            }
        }
        if (reqVO.getCategoryIds() != null) {
            for (Long categoryId : reqVO.getCategoryIds()) {
                if (categoryId != null && categoryId > 0) {
                    categoryIds.add(categoryId);
                }
            }
        }
        for (Long categoryId : categoryIds) {
            if (entityCategoryRelationService.existsRelation(newId, categoryId, entityTypeCode)) {
                continue;
            }
            entityCategoryRelationService.associate(newId, categoryId, entityTypeCode);
        }

        log.info("[cloneEntity][sourceId={}][newId={}][name={}][categories={}]",
                reqVO.getSourceEntityId(), newId, reqVO.getName().trim(), categoryIds.size());
        return newId;
    }

    /**
     * 更新实体（本体更新 + 关系差异同步 + 缓存失效 + 更新事件）。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(EntityUpdateReqVO reqVO) {
        doUpdate(reqVO, false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateIncludingModelChange(EntityUpdateReqVO reqVO) {
        doUpdate(reqVO, true);
    }

    private void doUpdate(EntityUpdateReqVO reqVO, boolean allowModelIdChange) {
        // 1) 读取旧值：用于不存在校验与关系差异计算
        EntityDO oldEntity = entityCoreService.get(reqVO.getId(), EntityFieldMapsSupport.getRequiredEntityTypeCode(reqVO.getBaseFields()));
        if (oldEntity == null) {
            throw new ServiceException(404, ENTITY_NOT_EXISTS);
        }

        Long requestedModelId = EntityFieldMapsSupport.getModelId(reqVO.getBaseFields());
        if (!allowModelIdChange
                && requestedModelId != null
                && oldEntity.getModelId() != null
                && !Objects.equals(requestedModelId, oldEntity.getModelId())) {
            throw new ServiceException(400, "请使用「变更模型」接口修改 modelId，避免字段数据丢失");
        }

        EntityDO data = entityBusinessHelper.prepareUpdateEntity(reqVO, oldEntity);
        Long requestedParentId = IdTreeHierarchy.normalizeParentId(data.getParentId());
        Long oldParentId = IdTreeHierarchy.normalizeParentId(oldEntity.getParentId());
        boolean parentChanged = !Objects.equals(requestedParentId, oldParentId)
                && reqVO.getBaseFields() != null
                && (reqVO.getBaseFields().containsKey("parentId") || reqVO.getBaseFields().containsKey("parent_id"));
        // 改上级必须走 moveEntity（递归 treePath + 分类投影），禁止裸 update 留下脏路径
        if (parentChanged) {
            data.setParentId(oldParentId);
        }
        Map<String, Object> fieldValues = new LinkedHashMap<>(
                entityBusinessHelper.emptyIfNull(data.getCustomFields()));
        Map<String, Object> physicalColumns = entityDedicatedColumnService.extractPhysicalValuesAndStrip(
                data.getEntityTypeCode() != null ? data.getEntityTypeCode() : oldEntity.getEntityTypeCode(),
                data.getCustomFields());
        try {
            entityCoreService.update(data);
            if (data.getId() == null) {
                data.setId(reqVO.getId());
            }
            entityDedicatedColumnService.applyAfterPersist(data, physicalColumns);
            if (parentChanged) {
                moveEntity(reqVO.getId(),
                        data.getEntityTypeCode() != null ? data.getEntityTypeCode() : oldEntity.getEntityTypeCode(),
                        requestedParentId);
                data.setParentId(requestedParentId);
            }
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            throw translateDataIntegrityError(ex,
                    data.getEntityTypeCode() != null ? data.getEntityTypeCode() : oldEntity.getEntityTypeCode());
        }

        Long modelId = data.getModelId() != null ? data.getModelId() : oldEntity.getModelId();
        ModelDO model = modelMapper.selectById(modelId);
        Map<String, Object> oldFieldValues = new LinkedHashMap<>(
                entityBusinessHelper.emptyIfNull(oldEntity.getCustomFields()));
        entityDedicatedColumnService.mergePhysicalColumnsIntoBaseFields(oldEntity, oldFieldValues);
        entityRelationSyncService.syncRelationsOnUpdate(data, model, fieldValues, oldFieldValues);
        // REF 实体–实体同步之后：凡本次请求中的 REF，目标为分类即实体则投影分类–实体
        entityRefCategoryProjectionService.projectOnUpdate(data, fieldValues, oldFieldValues);

        // 4) 缓存失效 + 事件通知：确保读取一致性并通知下游链路
        String entityTypeCode = EntityFieldMapsSupport.getRequiredEntityTypeCode(reqVO.getBaseFields());
        entityCacheEvictionService.evictEntityCaches(modelId, entityTypeCode);
        if (allowModelIdChange
                && oldEntity.getModelId() != null
                && modelId != null
                && !Objects.equals(oldEntity.getModelId(), modelId)) {
            entityCacheEvictionService.evictEntityCaches(oldEntity.getModelId(), entityTypeCode);
        }
        List<String> changedFields = entityBusinessHelper.extractFieldCodes(reqVO.getBaseFields(), reqVO.getCustomFields());
        entityLifecycleEventPublisher.publishEntityUpdatedEvent(
                modelId,
                reqVO.getId(),
                entityTypeCode,
                changedFields,
                data);
    }

    /**
     * 删除实体（删除前约束校验 + 关系清理 + 本体删除 + 缓存/事件处理）。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(EntityDeleteReqVO reqVO) {
        // 1) 存在性与删除前约束校验
        EntityDO existingEntity = entityCoreService.get(reqVO.getId(), reqVO.getEntityTypeCode());
        if (existingEntity == null) {
            throw new ServiceException(404, ENTITY_NOT_EXISTS);
        }

        // 非强制删除时，做最小化保护：存在子实体则拒绝删除
        if (!Boolean.TRUE.equals(reqVO.getForceDelete())
                && entityCoreService.existsByParentId(reqVO.getId(), reqVO.getEntityTypeCode())) {
            throw new ServiceException(400, "存在子实体，无法删除。请先删除子实体或使用强制删除");
        }

        // 分类即实体：删实体 → 断 link → 删分类节点（与从分类侧删除语义对齐）
        CategoryEntityLinkDO boundLink = categoryEntityLinkService.getLinkByEntityIdAndEntityTypeCode(
                reqVO.getId(), reqVO.getEntityTypeCode());
        if (boundLink != null && boundLink.getCategoryId() != null) {
            deleteCategoryBoundEntity(existingEntity, reqVO, boundLink);
            return;
        }

        // 2) 先清理关系（含自身发出的 REF），再删本体，避免关系悬挂
        boolean clearInbound = Boolean.TRUE.equals(reqVO.getForceDelete());
        cleanupAssociationsOnEntityDelete(reqVO.getId(), reqVO.getEntityTypeCode(), clearInbound);
        entityCoreService.delete(reqVO.getId(), reqVO.getEntityTypeCode());

        // 3) 写后副作用：缓存失效 + 删除事件
        entityCacheEvictionService.evictEntityCaches(existingEntity.getModelId(), reqVO.getEntityTypeCode());
        entityCacheEvictionService.evictEntity(reqVO.getId());
        entityLifecycleEventPublisher.publishEntityDeletedEvent(existingEntity.getModelId(), reqVO.getId(), reqVO.getEntityTypeCode());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEntityWithAssociationCleanup(Long id, String entityTypeCode, boolean clearInboundRelations) {
        if (id == null || !StringUtils.hasText(entityTypeCode)) {
            return;
        }
        EntityDO existingEntity = entityCoreService.get(id, entityTypeCode.trim());
        if (existingEntity == null) {
            return;
        }
        cleanupAssociationsOnEntityDelete(id, entityTypeCode.trim(), clearInboundRelations);
        entityCoreService.delete(id, entityTypeCode.trim());
        entityCacheEvictionService.evictEntityCaches(existingEntity.getModelId(), entityTypeCode.trim());
        entityCacheEvictionService.evictEntity(id);
        entityLifecycleEventPublisher.publishEntityDeletedEvent(
                existingEntity.getModelId(), id, entityTypeCode.trim());
    }

    /**
     * 分类即实体：实体列删除 = 删实体 → 断 link → 删分类节点。
     * 简单分类若误有 link，直接报错。
     */
    private void deleteCategoryBoundEntity(EntityDO existingEntity, EntityDeleteReqVO reqVO,
                                         CategoryEntityLinkDO boundLink) {
        CategoryDO category = categoryMapper.selectById(boundLink.getCategoryId());
        if (category == null) {
            throw new ServiceException(400, "分类即实体绑定的分类不存在，拒绝删除以免留下脏数据");
        }
        boolean clearInbound = Boolean.TRUE.equals(reqVO.getForceDelete());
        cleanupAssociationsOnEntityDelete(reqVO.getId(), reqVO.getEntityTypeCode(), clearInbound);
        entityCoreService.delete(reqVO.getId(), reqVO.getEntityTypeCode());
        categoryEntityLinkService.unlinkCategoryEntity(boundLink.getCategoryId());
        categoryService.deleteCategoryNodeAfterEntityRemoved(
                boundLink.getCategoryId(), category.getCategoryTypeCode());

        entityCacheEvictionService.evictEntityCaches(existingEntity.getModelId(), reqVO.getEntityTypeCode());
        entityCacheEvictionService.evictEntity(reqVO.getId());
        entityLifecycleEventPublisher.publishEntityDeletedEvent(
                existingEntity.getModelId(), reqVO.getId(), reqVO.getEntityTypeCode());
    }

    /**
     * 实体删除前级联清理（关联清理带 storage 类型，避免跨表同 id 误删）：
     * <ol>
     *   <li>本实体发出的 REF 关系镜像（如分区所属设施）——必清，属实体删除内建步骤</li>
     *   <li>可选：其它实体指向本实体的入站关系镜像</li>
     *   <li>实体–分类挂靠、分类即实体 link、划分成员</li>
     * </ol>
     * 专用表行上的自身 REF 列随后续 {@code entityCoreService.delete} 整行删除，无需单独置空。
     */
    private void cleanupAssociationsOnEntityDelete(Long entityId, String entityTypeCode,
                                                   boolean clearInboundRelations) {
        // 自身发出的 REF（所属设施等）→ 关系镜像表按 source 删除
        entityRelationSyncService.syncRelationsOnDelete(entityId, entityTypeCode);
        if (clearInboundRelations) {
            entityRelationMapper.deleteByTargetEntityId(entityId);
        }
        entityCategoryRelationService.deleteAllByEntityIdInBusiness(entityId, entityTypeCode);
        categoryEntityLinkService.unlinkEntityCategory(entityId, entityTypeCode);
        deleteScopeMembershipForStorageEntity(entityId, entityTypeCode);
    }

    /** 只清 base=storage 的 SCOPE 入口成员，避免跨表同 id。 */
    private void deleteScopeMembershipForStorageEntity(Long entityId, String storageEntityTypeCode) {
        if (entityId == null || !StringUtils.hasText(storageEntityTypeCode)) {
            return;
        }
        List<EntityTypeDO> scopeTypes = entityTypeMapper.selectList(
                new cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX<EntityTypeDO>()
                        .eq(EntityTypeDO::getEntryKind, EntityTypeDO.ENTRY_KIND_SCOPE)
                        .eq(EntityTypeDO::getBaseEntityTypeCode, storageEntityTypeCode.trim())
                        .eq(EntityTypeDO::getDeleted, false));
        if (scopeTypes == null || scopeTypes.isEmpty()) {
            return;
        }
        List<String> codes = scopeTypes.stream()
                .map(EntityTypeDO::getCode)
                .filter(StringUtils::hasText)
                .map(String::trim)
                .toList();
        entityTypeScopeMapper.deleteByEntityIdAndCodes(entityId, codes);
    }

    @Override
    public EntityFieldAvailabilityRespVO checkFieldUnique(
            String entityTypeCode,
            Long modelId,
            String fieldKey,
            String value,
            Long excludeId) {
        if (!org.springframework.util.StringUtils.hasText(entityTypeCode)
                || !org.springframework.util.StringUtils.hasText(fieldKey)
                || !org.springframework.util.StringUtils.hasText(value)) {
            return new EntityFieldAvailabilityRespVO(true, null);
        }
        String typeCode = entityTypeCode.trim();
        String key = fieldKey.trim();
        String trimmedValue = value.trim();
        if ("name".equals(key)) {
            if (modelId == null) {
                return new EntityFieldAvailabilityRespVO(false, "缺少 modelId，无法校验名称唯一性");
            }
            boolean exists = entityRepository.existsByExactName(typeCode, modelId, trimmedValue, excludeId);
            if (exists) {
                return new EntityFieldAvailabilityRespVO(false, "名称已存在");
            }
            return new EntityFieldAvailabilityRespVO(true, null);
        }
        if ("code".equals(key)) {
            // 编码唯一：同实体类型物理表（租户表）内，不按型号收窄
            boolean exists = entityRepository.existsByExactCode(typeCode, trimmedValue, excludeId);
            if (exists) {
                return new EntityFieldAvailabilityRespVO(false, "编码已存在");
            }
            return new EntityFieldAvailabilityRespVO(true, null);
        }
        return new EntityFieldAvailabilityRespVO(true, null);
    }

    /** =======================单实体详情获取：按主键获取实体详情。 ====================== */
    /**
     *  单实体详情获取：按主键获取实体详情。
     *
     * @return 实体详情；不存在时返回 null
     */
    @Override
    public EntityRespVO get(Long id, String entityTypeCode) {
        return get(id, entityTypeCode, false, null);
    }

    @Override
    public EntityRespVO get(Long id, String entityTypeCode, boolean includeAssociations,
            List<AssociationCategoryViewReqVO> associationCategoryViews) {
        if (id == null) {
            return null;
        }
        List<EntityDO> loaded = entityCoreService.listByIdsWithDedicatedBaseFields(
                Collections.singletonList(id), entityTypeCode);
        if (loaded == null || loaded.isEmpty()) {
            return null;
        }
        EntityRespVO vo = EntityDoVoHelper.toRespVO(
                loaded.get(0), customFieldValidationService, entityDedicatedColumnService);
        // 详情读路径与列表一致：REF 只存 id，展示名在此补齐（不写库）
        entityRefDisplayEnrichService.enrich(Collections.singletonList(vo));
        if (includeAssociations) {
            fillAssociations(vo, id, associationCategoryViews);
        }
        return vo;
    }

    /**
     * 填充 {@link EntityRespVO#setAssociations}：每个 REF/REFMulti 一块，views 的 key 为 categoryTypeCode 或 default。
     */
    private void fillAssociations(EntityRespVO vo, Long entityId, List<AssociationCategoryViewReqVO> associationCategoryViews) {
        List<EntityRelationDO> relations = entityRelationMapper.selectByEntityId(entityId);
        if (relations == null || relations.isEmpty()) {
            return;
        }
        Map<String, List<EntityRelationDO>> byField = relations.stream()
                .filter(r -> r.getFieldCode() != null && !r.getFieldCode().isBlank())
                .collect(Collectors.groupingBy(EntityRelationDO::getFieldCode, LinkedHashMap::new, Collectors.toList()));
        if (byField.isEmpty()) {
            return;
        }
        vo.setAssociations(buildAssociationsVoList(vo, entityId, byField, associationCategoryViews));
    }

    private List<AssociationsVO> buildAssociationsVoList(EntityRespVO vo, Long entityId,
            Map<String, List<EntityRelationDO>> byField,
            List<AssociationCategoryViewReqVO> associationCategoryViews) {
        Map<String, List<EntityRelationDO>> pending = new LinkedHashMap<>(byField);
        List<AssociationsVO> ordered = new ArrayList<>();
        Long modelId = vo.getModelId();
        if (modelId != null) {
            List<ModelFieldAssignmentDO> assignments = modelFieldAssignmentMapper.selectByModelId(modelId);
            for (ModelFieldAssignmentDO assignment : assignments) {
                if (assignment.getFieldId() == null) {
                    continue;
                }
                FieldDO field = fieldMapper.selectById(assignment.getFieldId());
                if (field == null || !FieldTypeEnum.isEntityRef(field.getType())) {
                    continue;
                }
                String code = field.getCode();
                List<EntityRelationDO> rels = pending.remove(code);
                if (rels == null) {
                    continue;
                }
                ordered.add(buildAssociationsBlock(field, code, rels, entityId, associationCategoryViews));
            }
        }
        List<String> restKeys = new ArrayList<>(pending.keySet());
        Collections.sort(restKeys);
        for (String code : restKeys) {
            FieldDO field = fieldMapper.selectByCode(code);
            ordered.add(buildAssociationsBlock(field, code, pending.get(code), entityId, associationCategoryViews));
        }
        return ordered;
    }

    private AssociationsVO buildAssociationsBlock(FieldDO fieldOrNull, String fieldCode, List<EntityRelationDO> rels,
            Long entityId, List<AssociationCategoryViewReqVO> associationCategoryViews) {
        AssociationsVO block = new AssociationsVO();
        block.setFieldCode(fieldCode);
        if (fieldOrNull != null) {
            block.setFieldName(fieldOrNull.getName());
            block.setFieldType(fieldOrNull.getType());
        }
        List<PeerEdge> peers = listPeerEdges(entityId, rels);
        Map<String, List<AssociationEntityRowVO>> views = new LinkedHashMap<>();
        List<AssociationCategoryViewReqVO> reqsForField = associationCategoryViews == null ? List.of()
                : associationCategoryViews.stream().filter(v -> fieldCode.equals(v.getFieldCode())).toList();
        if (reqsForField.isEmpty()) {
            views.put("default", buildAssociationRows(peers, null));
        } else {
            for (AssociationCategoryViewReqVO req : reqsForField) {
                if (req.getCategoryTypeCode() == null || req.getCategoryTypeCode().isBlank()) {
                    continue;
                }
                views.put(req.getCategoryTypeCode(), buildAssociationRows(peers, req.getCategoryTypeCode()));
            }
            if (views.isEmpty()) {
                views.put("default", buildAssociationRows(peers, null));
            }
        }
        block.setViews(views);
        return block;
    }

    private List<PeerEdge> listPeerEdges(Long entityId, List<EntityRelationDO> rels) {
        Set<String> seen = new HashSet<>();
        List<PeerEdge> out = new ArrayList<>();
        for (EntityRelationDO r : rels) {
            boolean asSource = entityId.equals(r.getSourceEntityId());
            Long peerId = asSource ? r.getTargetEntityId() : r.getSourceEntityId();
            if (peerId == null) {
                continue;
            }
            String peerBtc = asSource ? r.getTargetEntityTypeCode() : r.getSourceEntityTypeCode();
            String dedupe = peerId + ":" + (peerBtc == null ? "" : peerBtc);
            if (!seen.add(dedupe)) {
                continue;
            }
            PeerEdge e = new PeerEdge();
            e.peerId = peerId;
            e.peerBtc = peerBtc;
            out.add(e);
        }
        return out;
    }

    private List<AssociationEntityRowVO> buildAssociationRows(List<PeerEdge> peers, String categoryTypeCodeOrNull) {
        List<AssociationEntityRowVO> rows = new ArrayList<>();
        for (PeerEdge p : peers) {
            rows.add(toAssociationRow(p, categoryTypeCodeOrNull));
        }
        return rows;
    }

    private AssociationEntityRowVO toAssociationRow(PeerEdge p, String categoryTypeCodeOrNull) {
        AssociationEntityRowVO row = new AssociationEntityRowVO();
        row.setEntityId(p.peerId);
        row.setEntityTypeCode(p.peerBtc);
        if (p.peerBtc != null && !p.peerBtc.isBlank()) {
            EntityRespVO peerVo = get(p.peerId, p.peerBtc);
            if (peerVo != null) {
                row.setName(peerVo.getName());
            }
        }
        if (categoryTypeCodeOrNull != null && !categoryTypeCodeOrNull.isBlank()) {
            resolveCategoryForPeer(p.peerId, p.peerBtc, categoryTypeCodeOrNull, row);
        }
        return row;
    }

    private void resolveCategoryForPeer(Long peerEntityId, String peerEntityTypeCode, String categoryTypeCode,
            AssociationEntityRowVO row) {
        if (peerEntityId == null || peerEntityTypeCode == null || peerEntityTypeCode.isBlank()
                || categoryTypeCode == null || categoryTypeCode.isBlank()) {
            return;
        }
        List<Long> catIds = entityCategoryRelationService.listCategoryIdsByEntityId(peerEntityId, peerEntityTypeCode);
        if (catIds == null || catIds.isEmpty()) {
            return;
        }
        for (Long cid : catIds) {
            CategoryDO c = categoryMapper.selectById(cid);
            if (c != null && categoryTypeCode.equals(c.getCategoryTypeCode())) {
                row.setCategoryId(cid);
                row.setCategoryName(c.getName());
                return;
            }
        }
    }

    private static final class PeerEdge {
        private Long peerId;
        private String peerBtc;
    }

    /**
     * 移动实体到新的父节点。（更新parentId 和 TreePath ，递归更新子实体）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void moveEntity(Long entityId, String entityTypeCode, Long newParentId) {
        entityCoreService.moveEntity(entityId, entityTypeCode, newParentId);
        advancedCategoryEntityHierarchyService.syncCategoryAfterEntityMove(entityId, entityTypeCode, newParentId);
    }

    /**
     * 批量/关联操作决策树（对外入口）
     *
     * <p>1) 批量改实体字段：{@link #batchUpdate(EntityBatchUpdateReqVO)}</p>
     * <p>2) 批量删实体：{@link #batchDelete(EntityBatchDeleteReqVO)}</p>
     * <p>3) 批量迁移到目标分类（拖拽常见）：{@link #batchRelocateCategory(EntityBatchMoveReqVO)}
     *    （仅改实体-分类关系，不改 parentId/treePath）</p>
     * <p>4) 先看影响范围再执行：{@link #getBatchOperationPreview(String, List)}</p>
     *
     * <p>说明：追加关联、取消关联、单实体全量替换、批量导入覆盖、分类合并迁移等场景，
     * 均应由 Service 层统一编排后调用关系服务原子方法，Controller 不直接调用关系服务。</p>
     */
    /**
     * 批量更新实体状态。
     */

    /**
     * 批量创建实体（逐条复用单条 create 流程）。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Long> batchCreate(EntityBatchCreateReqVO reqVO) {
        if (reqVO == null || reqVO.getItems() == null || reqVO.getItems().isEmpty() || reqVO.getBase() == null) {
            return new ArrayList<>();
        }

        EntityCreateReqVO base = reqVO.getBase();
        EntityBatchCreateReqVO.Scope scope = reqVO.getScope();
        EntityBatchCreateReqVO.NamingRule namingRule = reqVO.getNamingRule();

        String separator = namingRule != null && namingRule.getSeparator() != null ? namingRule.getSeparator() : "-";
        int startNo = namingRule != null && namingRule.getStartNo() != null ? namingRule.getStartNo() : 1;
        int step = namingRule != null && namingRule.getStep() != null ? namingRule.getStep() : 1;
        int padLength = namingRule != null && namingRule.getPadLength() != null ? namingRule.getPadLength() : 0;
        boolean sequenceEnabled = namingRule != null && Boolean.TRUE.equals(namingRule.getSequenceEnabled());

        List<Long> ids = new ArrayList<>(reqVO.getItems().size());

        for (int i = 0; i < reqVO.getItems().size(); i++) {
            EntityBatchCreateReqVO.Item item = reqVO.getItems().get(i);
            String name = item.getName();
            if (name == null || name.isBlank()) {
                name = EntityFieldMapsSupport.asStringFromMap(base.getBaseFields(), "name");
                if (sequenceEnabled && name != null && !name.isBlank()) {
                    int currentNo = startNo + i * step;
                    String seq = padLength > 0
                            ? String.format("%0" + padLength + "d", currentNo)
                            : String.valueOf(currentNo);
                    name = name + separator + seq;
                }
            }

            Long parentId = item.getParentId() != null
                    ? item.getParentId()
                    : (scope != null ? scope.getParentEntityId()
                            : EntityFieldMapsSupport.asLongFromMap(base.getBaseFields(), "parentId"));
            Integer status = item.getStatus() != null
                    ? item.getStatus()
                    : EntityFieldMapsSupport.asIntegerFromMap(base.getBaseFields(), "status");

            Map<String, Object> baseOverlay = EntityWriteReqMaps.mergeBase(base.getBaseFields(), item.getBaseFields());
            EntityCreateReqVO createReq = EntityWriteReqMaps.createReq(
                    EntityFieldMapsSupport.getRequiredEntityTypeCode(base.getBaseFields()),
                    EntityFieldMapsSupport.getRequiredModelId(base.getBaseFields()),
                    name,
                    status,
                    parentId,
                    baseOverlay,
                    item.getCustomFields() != null ? item.getCustomFields() : base.getCustomFields());

            Long id = create(createReq);
            ids.add(id);

            if (scope != null && scope.getCategoryId() != null) {
                entityCategoryRelationService.associate(
                        id,
                        scope.getCategoryId(),
                        EntityFieldMapsSupport.getRequiredEntityTypeCode(base.getBaseFields()));
            }

            // TODO 后续完善：补充批量创建+分类关联失败时的审计与补偿策略。
        }
        return ids;
    }

    /**
     * 批量更新实体（逐条构造更新请求并复用 update 流程）。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EntityBatchOperationRespVO batchUpdate(EntityBatchUpdateReqVO reqVO) {
        long startTime = System.currentTimeMillis();
        List<EntityBatchOperationRespVO.FailItem> failItems = new ArrayList<>();
        int successCount = 0;

        if (reqVO == null || reqVO.getIds() == null || reqVO.getIds().isEmpty()) {
            return EntityBatchOperationRespVO.builder()
                    .operationType("BATCH_UPDATE")
                    .totalCount(0)
                    .successCount(0)
                    .failCount(0)
                    .async(false)
                    .executionTime(System.currentTimeMillis() - startTime)
                    .build();
        }

        for (Long id : reqVO.getIds()) {
            try {
                EntityUpdateReqVO updateReq = EntityWriteReqMaps.updateReq(
                        id,
                        reqVO.getEntityTypeCode(),
                        reqVO.getModelId(),
                        reqVO.getName(),
                        reqVO.getStatus(),
                        reqVO.getParentId(),
                        null,
                        reqVO.getCustomFields());
                update(updateReq);
                successCount++;
            } catch (Exception e) {
                failItems.add(EntityBatchOperationRespVO.FailItem.builder()
                        .entityId(id)
                        .reason(e.getMessage())
                        .errorCode(500)
                        .build());
            }
        }

        return EntityBatchOperationRespVO.builder()
                .operationType("BATCH_UPDATE")
                .totalCount(reqVO.getIds().size())
                .successCount(successCount)
                .failCount(failItems.size())
                .async(Boolean.TRUE.equals(reqVO.getAsync()))
                .failItems(failItems)
                .executionTime(System.currentTimeMillis() - startTime)
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    public EntityBatchOperationRespVO batchUpdateStatus(String entityTypeCode, List<Long> ids, Integer status) {
        List<EntityBatchOperationRespVO.FailItem> failItems = new ArrayList<>();
        if (ids == null || ids.isEmpty()) {
            return EntityBatchOperationRespVO.builder()
                    .operationType("UPDATE_STATUS").totalCount(0).successCount(0).failCount(0)
                    .async(false).failItems(failItems).build();
        }

        List<EntityDO> existings = entityCoreService.listByIds(ids, entityTypeCode);
        Map<Long, EntityDO> existingMap = existings.stream()
                .filter(e -> e.getId() != null)
                .collect(Collectors.toMap(EntityDO::getId, e -> e, (a, b) -> a));

        List<EntityDO> toUpdate = new ArrayList<>();
        for (Long id : ids) {
            EntityDO existing = existingMap.get(id);
            if (existing == null) {
                failItems.add(EntityBatchOperationRespVO.FailItem.builder()
                        .entityId(id).reason(ENTITY_NOT_EXISTS).errorCode(404).build());
                continue;
            }
            existing.setStatus(status);
            toUpdate.add(existing);
        }

        entityCoreService.updateBatch(toUpdate);

        int successCount = toUpdate.size();
        return EntityBatchOperationRespVO.builder()
                .operationType("UPDATE_STATUS").totalCount(ids.size()).successCount(successCount).failCount(failItems.size())
                .async(false).failItems(failItems).build();
    }

    /**
     * 批量删除编排入口。
     *
     * <p>逐条调用单删流程（delete），汇总成功/失败明细，保证错误可追踪。</p>
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EntityBatchOperationRespVO batchDelete(EntityBatchDeleteReqVO reqVO) {
        long startTime = System.currentTimeMillis();
        List<EntityBatchOperationRespVO.FailItem> failItems = new ArrayList<>();
        int successCount = 0;

        String entityTypeCode = reqVO.getEntityTypeCode();
        List<Long> ids = reqVO.getIds();
        Boolean forceDelete = reqVO.getForceDelete();

        for (Long id : ids) {
            try {
                EntityDO existing = entityCoreService.get(id, entityTypeCode);
                if (existing == null) {
                    failItems.add(EntityBatchOperationRespVO.FailItem.builder()
                            .entityId(id).reason(ENTITY_NOT_EXISTS).errorCode(404).build());
                    continue;
                }

                if (Boolean.FALSE.equals(forceDelete)
                        && entityCoreService.existsByParentId(id, entityTypeCode)) {
                    failItems.add(EntityBatchOperationRespVO.FailItem.builder()
                            .entityId(id).entityName(existing.getName()).reason("存在子实体，无法删除")
                            .errorCode(400).build());
                    continue;
                }

                cleanupAssociationsOnEntityDelete(id, entityTypeCode, Boolean.TRUE.equals(forceDelete));
                entityCoreService.delete(id, entityTypeCode);
                successCount++;
            } catch (Exception e) {
                failItems.add(EntityBatchOperationRespVO.FailItem.builder()
                        .entityId(id).reason(e.getMessage()).errorCode(500).build());
            }
        }

        return EntityBatchOperationRespVO.builder()
                .operationType("DELETE").totalCount(ids.size()).successCount(successCount).failCount(failItems.size())
                .async(false).failItems(failItems).executionTime(System.currentTimeMillis() - startTime).build();
    }

    /**
     * 批量调整实体分类关联（目标分类）编排入口。
     * <p>说明：当前仅调整实体-分类关系，不做实体树 parentId/treePath 变更。</p>
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EntityBatchOperationRespVO batchRelocateCategory(EntityBatchMoveReqVO reqVO) {
        long startTime = System.currentTimeMillis();
        List<EntityBatchOperationRespVO.FailItem> failItems = new ArrayList<>();
        int successCount = 0;

        String entityTypeCode = reqVO.getEntityTypeCode();
        List<Long> ids = reqVO.getIds();
        Long targetCategoryId = reqVO.getTargetCategoryId();
        boolean replaceExisting = Boolean.TRUE.equals(reqVO.getReplaceExisting());

        for (Long id : ids) {
            try {
                if (replaceExisting) {
                    entityCategoryRelationService.updateAssociation(id, List.of(targetCategoryId), entityTypeCode);
                } else {
                    entityCategoryRelationService.associate(id, targetCategoryId, entityTypeCode);
                }
                successCount++;
            } catch (Exception e) {
                failItems.add(EntityBatchOperationRespVO.FailItem.builder()
                        .entityId(id).reason(e.getMessage()).errorCode(500).build());
            }
        }

        return EntityBatchOperationRespVO.builder()
                .operationType("RELOCATE_CATEGORY").totalCount(ids.size()).successCount(successCount).failCount(failItems.size())
                .async(Boolean.TRUE.equals(reqVO.getAsync())).failItems(failItems).executionTime(System.currentTimeMillis() - startTime).build();
    }

    /**
     * 批量追加实体到目标分类（保留实体原有关联）。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BatchEntityCategoryAssociationRespVO batchAppendCategory(EntityBatchCategoryRelationReqVO reqVO) {
        return entityCategoryRelationService.batchAssociateEntitiesToCategory(
                reqVO.getEntityIds(), reqVO.getCategoryId(), reqVO.getEntityTypeCode(),
                reqVO.getEntityAssociationMode());
    }

    /**
     * 批量取消实体与目标分类的关联（不影响实体其它分类关联）。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BatchEntityCategoryAssociationRespVO batchRemoveCategory(EntityBatchCategoryRelationReqVO reqVO) {
        return entityCategoryRelationService.batchDisassociateEntitiesFromCategory(
                reqVO.getEntityIds(), reqVO.getCategoryId(), reqVO.getEntityTypeCode());
    }

    /**
     * 批量覆盖实体分类集合（用于导入覆盖等场景）。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BatchEntityCategoryAssociationRespVO batchReplaceCategories(EntityBatchReplaceCategoriesReqVO reqVO) {
        return entityCategoryRelationService.batchUpdateAssociation(
                reqVO.getEntityIds(), reqVO.getCategoryIds(), reqVO.getEntityTypeCode());
    }

    @Override
    public EntityBatchOperationRespVO getBatchOperationPreview(String entityTypeCode, List<Long> ids) {
        List<EntityBatchOperationRespVO.FailItem> previewItems = new ArrayList<>();
        int validCount = 0;

        List<EntityDO> loaded = entityCoreService.listByIdsWithDedicatedBaseFields(ids, entityTypeCode);
        Map<Long, EntityRespVO> byId = new HashMap<>();
        for (EntityRespVO vo : EntityDoVoHelper.toRespVOList(
                loaded, customFieldValidationService, entityDedicatedColumnService)) {
            if (vo != null && vo.getId() != null) {
                byId.put(vo.getId(), vo);
            }
        }

        for (Long id : ids) {
            EntityRespVO entity = byId.get(id);

            if (entity == null) {
                previewItems.add(EntityBatchOperationRespVO.FailItem.builder()
                        .entityId(id).reason(ENTITY_NOT_EXISTS).errorCode(404).build());
            } else {
                Long relationCount = entityRelationMapper.countByEntityId(id);
                if (relationCount != null && relationCount > 0) {
                    previewItems.add(EntityBatchOperationRespVO.FailItem.builder()
                            .entityId(id).entityName(entity.getName())
                            .reason(String.format("存在 %d 个关联关系", relationCount))
                            .errorCode(0).build());
                }
                validCount++;
            }
        }

        return EntityBatchOperationRespVO.builder()
                .operationType("PREVIEW").totalCount(ids.size()).successCount(validCount).failCount(ids.size() - validCount)
                .async(false).failItems(previewItems).build();
    }

    /**
     * 获取实体从根到当前节点的路径。
     * 前提是实体有数型结构的情况下使用 （todo:这个待分析是否要优化）
     */

    @Override
    public List<String> getEntityPath(Long entityId, String entityTypeCode) {
        return entityCoreService.getEntityPath(entityId, entityTypeCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveEntitySort(EntitySortSaveReqVO reqVO) {
        if (reqVO == null || reqVO.getItems() == null || reqVO.getItems().isEmpty()) {
            throw new ServiceException(400, "实体排序列表不能为空");
        }
        String entityTypeCode = reqVO.getEntityTypeCode() == null ? "" : reqVO.getEntityTypeCode().trim();
        if (!StringUtils.hasText(entityTypeCode)) {
            throw new ServiceException(400, "业务类型编码不能为空");
        }

        List<EntitySortSaveReqVO.Item> orderedItems = reqVO.getItems().stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(EntitySortSaveReqVO.Item::getIndex))
                .toList();

        List<Long> orderedEntityIds = orderedItems.stream()
                .map(EntitySortSaveReqVO.Item::getEntityId)
                .filter(Objects::nonNull)
                .toList();
        if (orderedEntityIds.isEmpty()) {
            throw new ServiceException(400, "实体排序列表不能为空");
        }

        if (reqVO.getCategoryId() != null) {
            for (Long entityId : orderedEntityIds) {
                EntityDO existing = entityCoreService.get(entityId, entityTypeCode);
                if (existing == null) {
                    throw new ServiceException(404, "实体不存在: " + entityId);
                }
                if (!entityTypeCode.equals(existing.getEntityTypeCode())) {
                    throw new ServiceException(400, "实体不属于指定业务类型: " + entityId);
                }
            }
            entityCategoryRelationService.reindexEntitySortInCategory(
                    reqVO.getCategoryId(), entityTypeCode, orderedEntityIds);
            return;
        }

        List<EntityDO> toUpdate = new ArrayList<>(orderedItems.size());
        int idx = 0;
        for (EntitySortSaveReqVO.Item item : orderedItems) {
            EntityDO existing = entityCoreService.get(item.getEntityId(), entityTypeCode);
            if (existing == null) {
                throw new ServiceException(404, "实体不存在: " + item.getEntityId());
            }
            if (!entityTypeCode.equals(existing.getEntityTypeCode())) {
                throw new ServiceException(400, "实体不属于指定业务类型: " + item.getEntityId());
            }
            EntityDO update = new EntityDO();
            update.setId(item.getEntityId());
            update.setEntityTypeCode(entityTypeCode);
            update.setSort(SparseSortUtils.reindexSortByPosition(idx++));
            toUpdate.add(update);
        }
        entityCoreService.updateBatch(toUpdate);
    }

    /**
     * 将库约束异常转成可读业务错误（仍优先靠提交前校验拦住；此处兜底未覆盖的约束）。
     */
    private ServiceException translateDataIntegrityError(
            org.springframework.dao.DataIntegrityViolationException ex, String entityTypeCode) {
        Throwable root = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause() : ex;
        String raw = root.getMessage() == null ? "" : root.getMessage();
        String lower = raw.toLowerCase(Locale.ROOT);
        if (lower.contains("violates not-null constraint") || lower.contains("not-null")) {
            String column = extractQuotedToken(raw, "column \"");
            if (column == null) {
                column = extractQuotedToken(raw, "列\"");
            }
            String label = resolvePhysicalColumnLabel(entityTypeCode, column);
            if (label != null) {
                return new ServiceException(400, "必填字段未填写：" + label);
            }
            if (column != null) {
                return new ServiceException(400, "必填字段未填写：" + column);
            }
            return new ServiceException(400, "存在必填字段未填写");
        }
        if (lower.contains("duplicate key") || lower.contains("unique constraint") || lower.contains("唯一")) {
            String column = extractQuotedToken(raw, "Key (");
            if (column != null) {
                return new ServiceException(400, "字段值已存在，请更换：" + column);
            }
            return new ServiceException(400, "字段值与已有数据冲突，请检查编码等唯一字段");
        }
        if (lower.contains("foreign key") || lower.contains("violates foreign key")) {
            return new ServiceException(400, "关联目标不存在或不可用，请检查引用字段");
        }
        log.error("[translateDataIntegrityError] 未识别的数据完整性异常 entityTypeCode={}", entityTypeCode, ex);
        return new ServiceException(500, "数据保存失败，请检查必填项与唯一约束后重试");
    }

    private String resolvePhysicalColumnLabel(String entityTypeCode, String column) {
        if (!StringUtils.hasText(entityTypeCode) || !StringUtils.hasText(column)) {
            return null;
        }
        String code = column.trim();
        EntityTypeBaseFieldDO field = entityTypeBaseFieldMapper.selectByEntityTypeCodeAndFieldCode(
                entityTypeCode.trim(), code);
        if (field == null || !StringUtils.hasText(field.getFieldName())) {
            return null;
        }
        return field.getFieldName().trim();
    }

    private static String extractQuotedToken(String raw, String marker) {
        if (raw == null || marker == null) {
            return null;
        }
        int start = raw.indexOf(marker);
        if (start < 0) {
            return null;
        }
        start += marker.length();
        int end = raw.indexOf('"', start);
        if (end < 0) {
            end = raw.indexOf(')', start);
        }
        if (end <= start) {
            return null;
        }
        return raw.substring(start, end).trim();
    }
}
