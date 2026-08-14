package cn.cheers.x.module.dynamicbusiness.service.entity;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.PageResult;
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
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.category.CategoryMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.category.CategoryEntityLinkMapper;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityCategoryRelationDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityFieldIndexDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntityCategoryRelationMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntityFieldIndexMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntityRelationMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytypescope.EntityTypeScopeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeBaseFieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.field.FieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelFieldAssignmentMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeBaseFieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.CategoryScopedEntityQueryRepository;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.DataMgmtEntityQueryRepository;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.EntityRepository;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.EvaFieldOrderEntityQueryRepository;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.PhysicalColumnFilter;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.KeywordSearchSpec;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.UncategorizedEntityQueryRepository;
import cn.cheers.x.module.dynamicbusiness.framework.entitytype.EntityTypeScopeContext;
import cn.cheers.x.module.dynamicbusiness.service.entity.core.EntityCoreService;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryService;
import cn.cheers.x.module.dynamicbusiness.service.entity.dto.EntityAggregationCountDTO;
import cn.cheers.x.module.dynamicbusiness.service.field.CustomFieldValidationService;
import cn.cheers.x.module.dynamicbusiness.enums.field.FieldTypeEnum;
import cn.cheers.x.module.dynamicbusiness.enums.entity.EntityQueryScene;
import cn.cheers.x.module.dynamicbusiness.enums.entity.EntityQueryResultDetail;
import cn.cheers.x.module.dynamicbusiness.enums.entity.EntityQueryResultShape;
import cn.cheers.x.module.dynamicbusiness.service.entity.relation.EntityCategoryRelationService;
import cn.cheers.x.module.dynamicbusiness.service.entity.relation.EntityRelationService;
import cn.cheers.x.module.dynamicbusiness.service.entity.categoryviaref.CategoryViaRefQueryPath;
import cn.cheers.x.module.dynamicbusiness.service.entity.categoryviaref.CategoryViaRefQueryService;
import cn.cheers.x.module.dynamicbusiness.service.entity.refcategory.EntityRefCategoryProjectionService;
import cn.cheers.x.module.dynamicbusiness.service.entity.refdisplay.EntityRefDisplayEnrichService;
import cn.cheers.x.module.dynamicbusiness.service.entity.index.FieldIndexService;
import cn.cheers.x.module.dynamicbusiness.service.entity.sync.EntitySyncService;
import cn.cheers.x.module.dynamicbusiness.service.model.ModelService;
import cn.cheers.x.module.dynamicbusiness.service.model.relation.ModelEntityRelationService;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryEntityLinkService;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryTypeService;
import cn.cheers.x.module.dynamicbusiness.util.DataMgmtCategoryReservedNodes;
import cn.cheers.x.module.dynamicbusiness.util.SparseSortUtils;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 实体服务实现类
 *
 * @author 基础服务模块
 */
@Service
@Validated
@Slf4j
public class EntityServiceImpl implements EntityService {

    private static final String ENTITY_NOT_EXISTS = "实体不存在";

    /** 实体表上可按列排序的核心字段（与能力投影 whitelist 对齐） */
    private static final Set<String> CORE_ORDER_BY_COLUMNS = Set.of("name", "code", "status", "id");

    @Resource
    private EntityCoreService entityCoreService;

    @Resource
    private EntityFieldQueryEngine entityFieldQueryEngine;

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
    private CategoryEntityLinkMapper categoryEntityLinkMapper;

    @Resource
    private EntityRelationMapper entityRelationMapper;

    @Resource
    private EntityRepository entityRepository;

    @Resource
    private EntityTypeBaseFieldMapper entityTypeBaseFieldMapper;

    @Resource
    private EntityFieldIndexMapper entityFieldIndexMapper;

    @Resource
    private CustomFieldValidationService customFieldValidationService;

    @Resource
    private FieldMapper fieldMapper;

    @Resource
    private ModelFieldAssignmentMapper modelFieldAssignmentMapper;

    @Resource
    private FieldIndexService fieldIndexService;

    @Resource
    @Lazy
    private EntityCategoryRelationService entityCategoryRelationService;

    @Resource
    @Lazy
    private ModelService modelService;

    @Resource
    private ModelEntityRelationService modelEntityRelationService;

    @Resource
    @Lazy
    private EntityRelationService entityRelationService;

    @Resource
    private EntityRelationSyncService entityRelationSyncService;

    @Resource
    private EntityRefCategoryProjectionService entityRefCategoryProjectionService;

    @Resource
    private EntityRefDisplayEnrichService entityRefDisplayEnrichService;

    @Resource
    private EntityDedicatedColumnService entityDedicatedColumnService;

    @Resource
    private EntitySyncService entitySyncService;

    @Resource
    private CategoryTypeService categoryTypeService;

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
    private DataMgmtEntityQueryRepository dataMgmtEntityQueryRepository;

    @Resource
    private UncategorizedEntityQueryRepository uncategorizedEntityQueryRepository;

    @Resource
    private CategoryScopedEntityQueryRepository categoryScopedEntityQueryRepository;

    @Resource
    private EvaFieldOrderEntityQueryRepository evaFieldOrderEntityQueryRepository;

    @Resource
    private CategoryViaRefQueryService categoryViaRefQueryService;

    @Resource
    private ObjectProvider<EntityServiceImpl> selfProvider;

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


    // ================================= 实体查询 ==========================================
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
    /** ======================= 统一场景查询入口（按 scene 分发到对应查询流程）。 =========== */
    /**
     * 统一场景查询入口（按 scene 分发到对应查询流程）。
     *
     * 1 按分类查询实体（用户点击的是分类节点）
     * 1.1 场景一： 模式A/C 分类视图下，点击分类查看实体列表
     *      点选是单个分类（前端只支持单分类的点击）
     *      勾选多分类（前端支持勾选多个分类）
     * 1.2 场景二：模式B 分类视图下，点击分类查看实体汇总列表
     *      点选单分类（前端只支持单分类的点击）
     *      勾选多分类（前端支持勾选多个分类）
     *
     * 2 按模型查询实体（用户点击的是模型节点）
     * 2.1 场景三：点击模型查看实体列表
     *      点选单模型（前端只支持单模型的点击）
     *      勾选多模型（前端支持勾选多个模型）
     *
     * 3 查看分类即实体的详情（用户点击的是分类节点）
     * 3.1 场景四：点击分类节点显示分类即实体详情（左侧为分类树）
     *
     * 4 查看实体详情（用户点击的是实体节点）
     * 4.1 场景五：单条实体详情（{@link EntityQueryScene#ENTITIES_DETAIL}，详情内携带跨业务关联关系摘要）
     *
     * <p><b>补充说明</b>：</p>
     * <ul>
     *   <li>分类/模型的“单选或多选”属于参数形态差异，不扩展新的 Scene 枚举。</li>
     *   <li>{@code only}/{@code withDescendants} 属于服务层内部查询能力，不直接暴露为用户场景。</li>
     *   <li>分类相关结果顺序规则：{@code categoryIds} 输入顺序 -> 分类内 {@code sort} -> 关系ID -> 稳定去重。</li>
     * </ul>
     */

    @Override
    public EntitySceneQueryRespVO queryEntities(EntityQueryScene scene, String resultShape, String resultDetail, String categoryTypeCode, String entityTypeCode,
            List<Long> modelIds, String modelEntityTypeCode, List<Long> categoryIds, List<CategoryIdGroupReqVO> categoryIdGroups, String categoryViaRefPathCode,
            Long entityId, Long rootEntityId, String entitySourceEntityType,
            Integer pageNo, Integer pageSize, String keyword, String domain,
            List<FieldFilterReqVO> filters, String orderByColumn, Boolean isAsc,
            List<String> searchFieldCodes) {

        if (scene == null) throw new ServiceException(400, "查询场景 scene 参数不能为空");

        Integer effectivePageNo = (pageNo == null || pageNo < 1) ? 1 : pageNo;
        Integer effectivePageSize = (pageSize == null || pageSize < 1) ? 20 : pageSize;
        int fullPageSize = Integer.MAX_VALUE;

        EntityQueryResultShape shape = EntityQueryResultShape.ofNullable(resultShape);
        EntityQueryResultDetail detail = EntityQueryResultDetail.ofNullable(resultDetail);
        // 未传排序列时：PAGE 列表默认按名称；拖拽人工序由前端显式传 orderByColumn=sort
        String normalizedOrderByColumn = normalizeOrderByColumn(orderByColumn);
        if (!StringUtils.hasText(normalizedOrderByColumn) && shape == EntityQueryResultShape.PAGE) {
            normalizedOrderByColumn = "name";
        }
        boolean orderAsc = isAsc == null || Boolean.TRUE.equals(isAsc);
        boolean manualSortOrder = "sort".equals(normalizedOrderByColumn);

        // 按入口解析：SCOPE → storage + 成员过滤；DOMAIN registry → storage + domain；其余用请求 domain
        ResolvedQueryType resolved = resolveQueryEntityType(entityTypeCode, domain);
        String storageEntityTypeCode = resolved.entityTypeCode();
        String normalizedDomain = resolved.domain();
        String normalizedScopeCode = resolved.scopeRegistryCode();
        KeywordSearchSpec keywordSearch = resolveKeywordSearchSpec(storageEntityTypeCode, searchFieldCodes);

        switch (scene) {
            case ENTITIES_BY_CATEGORY: {
                String queryEntityTypeCode = resolved.entityTypeCode();
                if (queryEntityTypeCode == null || queryEntityTypeCode.isBlank()) {
                    throw new ServiceException(400, "ENTITIES_BY_CATEGORY 场景下 entityTypeCode 不能为空");
                }
                if (StringUtils.hasText(normalizedOrderByColumn) && !manualSortOrder) {
                    validateOrderByColumn(queryEntityTypeCode, normalizedOrderByColumn);
                }
                String fieldOrderColumn = manualSortOrder ? null : normalizedOrderByColumn;
                String viaRefPathCode = trimToNull(categoryViaRefPathCode);
                if (viaRefPathCode != null) {
                    return queryEntitiesByCategoryViaRef(
                            viaRefPathCode, categoryTypeCode, categoryIds, queryEntityTypeCode,
                            normalizedDomain, normalizedScopeCode, keyword, filters,
                            shape, detail, effectivePageNo, effectivePageSize,
                            fieldOrderColumn, orderAsc);
                }
                // 禁止用 storage 冒充分类种类：同源也须显式传 categoryTypeCode，避免跨视角漏传时查错树
                if (categoryTypeCode == null || categoryTypeCode.isBlank()) {
                    throw new ServiceException(400, "ENTITIES_BY_CATEGORY 场景下 categoryTypeCode 不能为空");
                }
                String resolvedCategoryTypeCode = categoryTypeCode.trim();
                if (shape == EntityQueryResultShape.PAGE) {
                    PageResult<EntityRespVO> paged = queryDataMgmtEntitiesByCategoryModel(
                            categoryIds, categoryIdGroups, resolvedCategoryTypeCode, storageEntityTypeCode, modelIds,
                            keyword, filters, effectivePageNo, effectivePageSize, true,
                            normalizedDomain, normalizedScopeCode, fieldOrderColumn, orderAsc, keywordSearch, detail);
                    return EntitySceneQueryRespVO.page(applyResultDetail(paged, detail), detail.getCode());
                }
                PageResult<EntityRespVO> full = queryDataMgmtEntitiesByCategoryModel(
                        categoryIds, categoryIdGroups, resolvedCategoryTypeCode, storageEntityTypeCode, modelIds,
                        keyword, filters, null, null, false,
                        normalizedDomain, normalizedScopeCode, fieldOrderColumn, orderAsc, keywordSearch, detail);
                List<EntityRespVO> entities = full.getList();
                if (shape == EntityQueryResultShape.TREE) {
                    return EntitySceneQueryRespVO.tree(
                            applyResultDetail(EntityTreeBuilder.buildTree(entities, EntityTreeBuilder.SortMode.LOCAL_SIBLING_SORT), detail),
                            detail.getCode());
                }
                return EntitySceneQueryRespVO.list(applyResultDetail(entities, detail), detail.getCode());
            }

            case ENTITIES_BY_MODEL: {
                if (StringUtils.hasText(normalizedOrderByColumn) && !manualSortOrder) {
                    validateOrderByColumn(storageEntityTypeCode, normalizedOrderByColumn);
                }
                List<Long> normalizedModelIds = normalizeModelIds(modelIds);
                if (!normalizedModelIds.isEmpty()) {
                    // 场景 8 跨类型：modelIds 为外类型型号；本类实体经挂钩表取 id，再装本类实体（含 name）。
                    // 禁止用外类型 modelId 筛本类实体表 model_id（本类 model_id 只表示本类型号归属）。
                    if (isCrossTypeModelEntityQuery(modelEntityTypeCode, storageEntityTypeCode)) {
                        PageResult<EntityRespVO> relatedPage = handleCrossTypeModelRelatedEntities(
                                normalizedModelIds, modelEntityTypeCode.trim(), storageEntityTypeCode,
                                keyword, filters,
                                shape == EntityQueryResultShape.PAGE ? effectivePageNo : 1,
                                shape == EntityQueryResultShape.PAGE ? effectivePageSize : fullPageSize,
                                normalizedDomain, normalizedScopeCode,
                                manualSortOrder ? null : normalizedOrderByColumn, orderAsc, keywordSearch);
                        if (shape == EntityQueryResultShape.PAGE) {
                            return EntitySceneQueryRespVO.page(applyResultDetail(relatedPage, detail), detail.getCode());
                        }
                        if (shape == EntityQueryResultShape.TREE) {
                            return EntitySceneQueryRespVO.tree(
                                    applyResultDetail(EntityTreeBuilder.buildTree(relatedPage.getList(),
                                            EntityTreeBuilder.SortMode.LOCAL_SIBLING_SORT), detail),
                                    detail.getCode());
                        }
                        return EntitySceneQueryRespVO.list(applyResultDetail(relatedPage.getList(), detail), detail.getCode());
                    }
                    if (shape == EntityQueryResultShape.TREE && normalizedModelIds.size() == 1
                            && !StringUtils.hasText(normalizedDomain) && !StringUtils.hasText(normalizedScopeCode)
                            && (manualSortOrder || !StringUtils.hasText(normalizedOrderByColumn))) {
                        return EntitySceneQueryRespVO.tree(
                                applyResultDetail(getEntityTreeByModelId(storageEntityTypeCode, normalizedModelIds.get(0)), detail),
                                detail.getCode());
                    }
                    PageResult<EntityRespVO> modelPage = handlePatternBModelEntities(
                            normalizedModelIds, storageEntityTypeCode, keyword, filters,
                            shape == EntityQueryResultShape.PAGE ? effectivePageNo : 1,
                            shape == EntityQueryResultShape.PAGE ? effectivePageSize : fullPageSize,
                            normalizedDomain, normalizedScopeCode,
                            manualSortOrder ? null : normalizedOrderByColumn, orderAsc, keywordSearch, detail);
                    if (shape == EntityQueryResultShape.PAGE) {
                        return EntitySceneQueryRespVO.page(applyResultDetail(modelPage, detail), detail.getCode());
                    }
                    if (shape == EntityQueryResultShape.TREE) {
                        return EntitySceneQueryRespVO.tree(
                                applyResultDetail(EntityTreeBuilder.buildTree(modelPage.getList(),
                                        EntityTreeBuilder.SortMode.LOCAL_SIBLING_SORT), detail),
                                detail.getCode());
                    }
                    return EntitySceneQueryRespVO.list(applyResultDetail(modelPage.getList(), detail), detail.getCode());
                }
                // 未传 modelIds：按类型范围（+ Domain / 划分）
                if (shape == EntityQueryResultShape.TREE
                        && (manualSortOrder || !StringUtils.hasText(normalizedOrderByColumn))) {
                    List<EntityRespVO> treeRoots = buildEntityHierarchySubtree(
                            storageEntityTypeCode, null, keyword, filters, detail,
                            effectivePageNo, resolveTreeRootPageSize(pageSize));
                    return EntitySceneQueryRespVO.tree(applyResultDetail(treeRoots, detail), detail.getCode());
                }
                // 类型全量 + 可下推排序 + 筛可下推/无筛 + 无划分：库内 ORDER BY + LIMIT（keyword 用 name LIKE）
                List<PhysicalColumnFilter> typePushFilters =
                        resolvePushablePhysicalFilters(storageEntityTypeCode, filters);
                boolean typeFiltersOk = typePushFilters != null;
                String typeDbOrder = !manualSortOrder
                        ? resolveDbOrderColumn(storageEntityTypeCode, normalizedOrderByColumn)
                        : null;
                boolean typeNoPushFilters = typeFiltersOk && typePushFilters.isEmpty();
                String typeEvaValueCol = !manualSortOrder && !StringUtils.hasText(typeDbOrder) && typeNoPushFilters
                        ? resolveEvaIndexValueColumn(storageEntityTypeCode, normalizedOrderByColumn)
                        : null;
                boolean typeKeywordPushOk = !StringUtils.hasText(keyword) || keywordSearch.canPushFullyToEntityTable();
                boolean typeKeywordEvaOk = !StringUtils.hasText(keyword) || keywordSearch.isNameOnlyLike();
                if (shape == EntityQueryResultShape.PAGE
                        && typeFiltersOk
                        && typeKeywordPushOk
                        && !StringUtils.hasText(normalizedScopeCode)
                        && (StringUtils.hasText(typeDbOrder)
                            || (StringUtils.hasText(typeEvaValueCol) && typeKeywordEvaOk))) {
                    long t0 = System.nanoTime();
                    PageResult<EntityRespVO> typedPage;
                    String pathTag;
                    if (StringUtils.hasText(typeDbOrder)) {
                        typedPage = pageEntitiesByEntityTypeDbOrder(
                                storageEntityTypeCode, keyword, normalizedDomain,
                                effectivePageNo, effectivePageSize,
                                typeDbOrder, orderAsc, typePushFilters, keywordSearch, detail);
                        pathTag = "TYPE_DB_ORDER_PAGE";
                    } else {
                        typedPage = pageEntitiesByEvaOrder(
                                storageEntityTypeCode, null, keyword, normalizedDomain,
                                normalizedOrderByColumn.trim(), typeEvaValueCol,
                                effectivePageNo, effectivePageSize, orderAsc, detail);
                        pathTag = "TYPE_EVA_ORDER_PAGE";
                    }
                    long t1 = System.nanoTime();
                    PageResult<EntityRespVO> shaped = applyResultDetail(typedPage, detail);
                    long t2 = System.nanoTime();
                    log.info("[query-by-scene timing] path={} type={} order={} page={}/{} rows={} "
                                    + "pageAndConvert={}ms enrichAndLight={}ms total={}ms detail={}",
                            pathTag, storageEntityTypeCode,
                            StringUtils.hasText(typeDbOrder) ? typeDbOrder : normalizedOrderByColumn,
                            effectivePageNo, effectivePageSize,
                            shaped.getList() != null ? shaped.getList().size() : 0,
                            (t1 - t0) / 1_000_000L, (t2 - t1) / 1_000_000L, (t2 - t0) / 1_000_000L,
                            detail);
                    return EntitySceneQueryRespVO.page(shaped, detail.getCode());
                }
                List<Long> allEntityIds = collectPatternAbcAllCandidateEntityIds(
                        storageEntityTypeCode, modelIds, normalizedDomain, normalizedScopeCode);
                allEntityIds = applyFieldOrderToCandidateIds(
                        allEntityIds, storageEntityTypeCode,
                        manualSortOrder ? null : normalizedOrderByColumn, orderAsc);
                PageResult<EntityRespVO> result = queryEntitiesByOrderedCandidateIds(
                        allEntityIds, storageEntityTypeCode, keyword, filters,
                        shape == EntityQueryResultShape.PAGE ? effectivePageNo : null,
                        shape == EntityQueryResultShape.PAGE ? effectivePageSize : null,
                        keywordSearch);
                if (shape == EntityQueryResultShape.PAGE) {
                    return EntitySceneQueryRespVO.page(applyResultDetail(result, detail), detail.getCode());
                }
                return EntitySceneQueryRespVO.list(applyResultDetail(result.getList(), detail), detail.getCode());
            }

            case ENTITIES_UNCATEGORIZED: {
                String queryEntityTypeCode = resolved.entityTypeCode();
                if (queryEntityTypeCode == null || queryEntityTypeCode.isBlank()) {
                    throw new ServiceException(400, "ENTITIES_UNCATEGORIZED 场景下 entityTypeCode 不能为空");
                }
                if (categoryTypeCode == null || categoryTypeCode.isBlank()) {
                    throw new ServiceException(400, "ENTITIES_UNCATEGORIZED 场景下 categoryTypeCode 不能为空");
                }
                if (StringUtils.hasText(normalizedOrderByColumn) && !manualSortOrder) {
                    validateOrderByColumn(queryEntityTypeCode, normalizedOrderByColumn);
                }
                String fieldOrderColumn = manualSortOrder ? null : normalizedOrderByColumn;
                PageResult<EntityRespVO> uncategorizedPage = queryUncategorizedEntitiesByCategoryType(
                        categoryTypeCode.trim(), storageEntityTypeCode, modelIds,
                        keyword, filters,
                        shape == EntityQueryResultShape.PAGE ? effectivePageNo : null,
                        shape == EntityQueryResultShape.PAGE ? effectivePageSize : null,
                        shape == EntityQueryResultShape.PAGE,
                        normalizedDomain, normalizedScopeCode, fieldOrderColumn, orderAsc);
                if (shape == EntityQueryResultShape.PAGE) {
                    return EntitySceneQueryRespVO.page(applyResultDetail(uncategorizedPage, detail), detail.getCode());
                }
                if (shape == EntityQueryResultShape.TREE) {
                    return EntitySceneQueryRespVO.tree(
                            applyResultDetail(EntityTreeBuilder.buildTree(uncategorizedPage.getList(),
                                    EntityTreeBuilder.SortMode.LOCAL_SIBLING_SORT), detail),
                            detail.getCode());
                }
                return EntitySceneQueryRespVO.list(
                        applyResultDetail(uncategorizedPage.getList(), detail), detail.getCode());
            }

            case ENTITIES_BY_CATEGORY_LINK: {
                if (categoryIds == null || categoryIds.isEmpty() || categoryIds.get(0) == null) {
                    throw new ServiceException(400, "ENTITIES_BY_CATEGORY_LINK 场景下 categoryIds 不能为空");
                }
                EntityRespVO linked = getCategoryLinkedEntity(categoryIds.get(0), storageEntityTypeCode);
                if (linked == null) {
                    throw new ServiceException(404, ENTITY_NOT_EXISTS);
                }
                EntityRespVO shapedOne = applyResultDetail(Collections.singletonList(linked), detail).get(0);
                if (shape == EntityQueryResultShape.PAGE) {
                    return EntitySceneQueryRespVO.page(
                            new PageResult<>(Collections.singletonList(shapedOne), 1L), detail.getCode());
                }
                if (shape == EntityQueryResultShape.TREE) {
                    return EntitySceneQueryRespVO.tree(Collections.singletonList(shapedOne), detail.getCode());
                }
                return EntitySceneQueryRespVO.list(Collections.singletonList(shapedOne), detail.getCode());
            }

            case ENTITIES_DETAIL: {
                EntityRespVO detailVo = buildPatternDEntityDetailedInfo(entityId, storageEntityTypeCode);
                if (detailVo == null) {
                    throw new ServiceException(404, ENTITY_NOT_EXISTS);
                }
                EntityRespVO shapedOne = applyResultDetail(Collections.singletonList(detailVo), detail).get(0);
                if (shape == EntityQueryResultShape.PAGE) {
                    return EntitySceneQueryRespVO.page(
                            new PageResult<>(Collections.singletonList(shapedOne), 1L), detail.getCode());
                }
                if (shape == EntityQueryResultShape.TREE) {
                    return EntitySceneQueryRespVO.tree(Collections.singletonList(shapedOne), detail.getCode());
                }
                return EntitySceneQueryRespVO.list(Collections.singletonList(shapedOne), detail.getCode());
            }

            case ROOT_ENTITY_SUBTREE: {
                if (storageEntityTypeCode == null || storageEntityTypeCode.isBlank()) {
                    throw new ServiceException(400, "ROOT_ENTITY_SUBTREE 场景下 entityTypeCode 不能为空");
                }
                List<EntityRespVO> subtreeRoots = buildEntityHierarchySubtree(
                        storageEntityTypeCode, rootEntityId, keyword, filters, detail,
                        rootEntityId == null ? effectivePageNo : null,
                        rootEntityId == null ? resolveTreeRootPageSize(pageSize) : null);
                if (shape == EntityQueryResultShape.PAGE) {
                    PageResult<EntityRespVO> paged = buildEntityRespPageResult(
                            flattenEntityTree(subtreeRoots),
                            effectivePageNo, effectivePageSize);
                    return EntitySceneQueryRespVO.page(applyResultDetail(paged, detail), detail.getCode());
                }
                if (shape == EntityQueryResultShape.TREE) {
                    return EntitySceneQueryRespVO.tree(
                            applyResultDetail(subtreeRoots, detail), detail.getCode());
                }
                return EntitySceneQueryRespVO.list(
                        applyResultDetail(flattenEntityTree(subtreeRoots), detail), detail.getCode());
            }
            default:
                throw new ServiceException(400, "不支持的查询场景: " + scene.getCode());
        }
    }

    /**
     * 将请求 entityTypeCode + domain 解析为存储编码、业务域、划分入口编码。
     * <ul>
     *   <li>SCOPE 入口：storage=base，scopeRegistry=自身 code</li>
     *   <li>DOMAIN 入口：storage=base，domain=入口域（请求域若传则须一致）</li>
     *   <li>REUSE 入口：storage=base，无 domain / scope 过滤（同表可读写）</li>
     *   <li>其余：storage=请求 code，domain=请求域</li>
     * </ul>
     */
    private ResolvedQueryType resolveQueryEntityType(String entityTypeCode, String requestDomain) {
        String reqDomain = EntityTypeScopeContext.normalizeDomain(requestDomain);
        if (!StringUtils.hasText(entityTypeCode)) {
            return new ResolvedQueryType(null, reqDomain, null);
        }
        String code = entityTypeCode.trim();
        EntityTypeDO type = entityTypeMapper.selectByCode(code);
        EntityTypeScopeContext ctx = EntityTypeScopeContext.from(type);
        if (ctx == null) {
            return new ResolvedQueryType(code, reqDomain, null);
        }
        if (ctx.isScopeEntry()) {
            return new ResolvedQueryType(ctx.getStorageEntityTypeCode(), reqDomain, ctx.getRegistryCode());
        }
        if (ctx.isDomainEntry()) {
            String entryDomain = ctx.getDomain();
            if (reqDomain != null && entryDomain != null
                    && !EntityTypeScopeContext.domainsEqual(reqDomain, entryDomain)) {
                throw new ServiceException(400, "请求业务域与入口业务域不一致");
            }
            return new ResolvedQueryType(
                    ctx.getStorageEntityTypeCode(),
                    reqDomain != null ? reqDomain : entryDomain,
                    null);
        }
        return new ResolvedQueryType(ctx.getStorageEntityTypeCode(), reqDomain, null);
    }

    private record ResolvedQueryType(String entityTypeCode, String domain, String scopeRegistryCode) {
    }

    /** 规范化模型 ID：去 null、去重（保留顺序）。 */
    private List<Long> normalizeModelIds(List<Long> modelIds) {
        if (modelIds == null || modelIds.isEmpty()) {
            return List.of();
        }
        return modelIds.stream().filter(Objects::nonNull).distinct().toList();
    }

    /**
     * ENTITIES_BY_MODEL 候选实体 ID：未限定 modelIds 时整业务类型；限定后仅取这些模型下的实体。
     * 可叠加业务域 / 划分成员收窄。
     */
    private List<Long> collectPatternAbcAllCandidateEntityIds(String entityTypeCode, List<Long> modelIds,
                                                              String domain, String scopeRegistryCode) {
        List<Long> normalizedModelIds = normalizeModelIds(modelIds);
        List<Long> candidates;
        if (normalizedModelIds.isEmpty()) {
            candidates = collectAllEntityIdsByEntityType(entityTypeCode, domain);
        } else {
            candidates = collectCandidateEntityIdsByModelIds(normalizedModelIds, entityTypeCode);
            candidates = dataMgmtEntityQueryRepository.retainOrderedIdsByDomainAndScope(
                    candidates, entityTypeCode, domain, null);
        }
        return dataMgmtEntityQueryRepository.retainOrderedIdsByDomainAndScope(
                candidates, entityTypeCode, null, scopeRegistryCode);
    }

    /**
     * 规范化模型 ID 且要求非空，否则抛出业务异常。
     */
    private List<Long> requireModelIds(List<Long> modelIds, String emptyMessage) {
        List<Long> normalized = normalizeModelIds(modelIds);
        if (normalized.isEmpty()) {
            throw new ServiceException(400, emptyMessage);
        }
        return normalized;
    }

    /**
     * 是否可以直接走“原有 DB 前置分页”逻辑。
     * <p>规则（第一阶段）：</p>
     * <ul>
     *   <li>keyword 为空</li>
     *   <li>filters 为空</li>
     * </ul>
     *
     * <p>说明：当存在 keyword/filters 时，需要先收敛候选集合再分页，否则会出现“先分页后过滤”导致的空页与 total 不准确。</p>
     */
    private boolean canPageDirectly(String keyword, List<FieldFilterReqVO> filters) {
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        boolean hasFilters = filters != null && !filters.isEmpty();
        return !hasKeyword && !hasFilters;
    }

    /**-----------------1 按分类查询实体（用户点击的是分类节点）---------------------------*/

    /**
     * 按分类统一查询实体（LIST/PAGE 共用流程）。
     */
    private PageResult<EntityRespVO> queryEntitiesByCategoryIds(List<Long> categoryIds, String categoryTypeCode,
                                                                String entityTypeCode, String keyword, List<FieldFilterReqVO> filters,
                                                                Integer pageNo, Integer pageSize, boolean allowDirectPaging) {
        if (entityTypeCode == null || entityTypeCode.isBlank()) {
            return new PageResult<>(new ArrayList<>(), 0L);
        }
        // 规范化分类ID列表；若未选择分类则使用分类类型的根分类ID （针对用户在界面上没选分类时的处理方法）

        List<Long> normalizedCategoryIds = normalizeCategoryIdsOrUseRootCategory(categoryIds, categoryTypeCode);
        if (normalizedCategoryIds.isEmpty()) {
            return new PageResult<>(new ArrayList<>(), 0L);
        }

        // 仅分页场景允许 DB 前置分页，避免 list 场景被提前裁剪
        if (allowDirectPaging && canPageDirectly(keyword, filters)) {
            Integer pn = normalizePageNo(pageNo);
            Integer ps = normalizePageSize(pageSize);
            PageResult<Long> pagedEntityIds;
            if (normalizedCategoryIds.size() == 1) {
                // 点选是单个分类
                pagedEntityIds = entityCategoryRelationService.pageEntityIdsByCategoryIdWithDescendants(
                        normalizedCategoryIds.get(0), categoryTypeCode, entityTypeCode, pn, ps);
            } else {
                // 点选是单个分类
                pagedEntityIds = entityCategoryRelationService.pageEntityIdsByCategoryIdsWithDescendantsDb(
                        normalizedCategoryIds, entityTypeCode, pn, ps);
            }
            return fetchEntityPageByOrderedIds(pagedEntityIds, entityTypeCode);
        }

        List<Long> orderedCandidateEntityIds = listOrderedEntityIdsByCategoriesInBusiness(normalizedCategoryIds, categoryTypeCode, entityTypeCode);
        return queryEntitiesByOrderedCandidateIds(
                orderedCandidateEntityIds, entityTypeCode, keyword, filters, pageNo, pageSize);
    }

    /**
     * 数据管理「未分类」：类型范围内实体 − 已挂接当前 categoryTypeCode 任一节点（relation ∪ link）。
     * 可叠 modelIds / domain / 划分；保留桶节点（code 含 UNCATEGORIZED）不算「已分类」。
     * <p>无搜无筛且排序可下推时走 {@link UncategorizedEntityQueryRepository} 库内 NOT EXISTS + LIMIT。</p>
     */
    private PageResult<EntityRespVO> queryUncategorizedEntitiesByCategoryType(
            String categoryTypeCode,
            String entityTypeCode,
            List<Long> modelIds,
            String keyword,
            List<FieldFilterReqVO> filters,
            Integer pageNo,
            Integer pageSize,
            boolean allowDirectPaging,
            String domain,
            String scopeRegistryCode,
            String orderByColumn,
            boolean orderAsc) {
        if (entityTypeCode == null || entityTypeCode.isBlank()
                || categoryTypeCode == null || categoryTypeCode.isBlank()) {
            return new PageResult<>(new ArrayList<>(), 0L);
        }
        String dbOrder = resolveDbOrderColumn(entityTypeCode, orderByColumn);
        if (allowDirectPaging && canPageDirectly(keyword, filters)
                && StringUtils.hasText(dbOrder)
                && uncategorizedEntityQueryRepository.supportsSqlOrder(dbOrder)) {
            Integer pn = normalizePageNo(pageNo);
            Integer ps = normalizePageSize(pageSize);
            long t0 = System.nanoTime();
            PageResult<Long> pageIds = uncategorizedEntityQueryRepository.pageUncategorizedEntityIds(
                    categoryTypeCode, entityTypeCode, modelIds, domain, scopeRegistryCode,
                    dbOrder, orderAsc, pn, ps);
            List<Long> orderedIds = pageIds.getList() != null ? pageIds.getList() : List.of();
            List<EntityRespVO> list = convertOrderedEntityIdsToRespList(orderedIds, entityTypeCode);
            if (log.isInfoEnabled()) {
                log.info("[query-by-scene timing] path=UNCATEGORIZED_DB_PAGE type={} order={} "
                                + "page={}/{} rows={} total={} elapsed={}ms",
                        entityTypeCode, dbOrder, pn, ps, list.size(), pageIds.getTotal(),
                        (System.nanoTime() - t0) / 1_000_000L);
            }
            return new PageResult<>(list, pageIds.getTotal());
        }

        // 慢路径：先取未分类 id（不装行），再内存排序 / 过滤 / 切页
        List<Long> uncategorizedIds = uncategorizedEntityQueryRepository.listUncategorizedEntityIds(
                categoryTypeCode, entityTypeCode, modelIds, domain, scopeRegistryCode);
        if (uncategorizedIds.isEmpty()) {
            return new PageResult<>(new ArrayList<>(), 0L);
        }
        uncategorizedIds = applyFieldOrderToCandidateIds(
                uncategorizedIds, entityTypeCode, orderByColumn, orderAsc);
        if (allowDirectPaging && canPageDirectly(keyword, filters)) {
            Integer pn = normalizePageNo(pageNo);
            Integer ps = normalizePageSize(pageSize);
            return pageByOrderedIds(uncategorizedIds, entityTypeCode, pn, ps);
        }
        return queryEntitiesByOrderedCandidateIds(
                uncategorizedIds, entityTypeCode, keyword, filters, pageNo, pageSize);
    }

    /**
     * 数据管理三栏：分类定范围（含子树，空 categoryIds 回退根分类）+ 可选 modelIds 过滤。
     * <p>右栏实体只取分类范围内的 relation / link 关联，不做 modelId 全局扫表。</p>
     * <p>{@code categoryIdGroups} 非空时按多独立栏求交（组间 AND）。可下推时走库内 EXISTS + ORDER BY + LIMIT（多组同快路径）。</p>
     */
    private PageResult<EntityRespVO> queryDataMgmtEntitiesByCategoryModel(List<Long> categoryIds,
                                                                          List<CategoryIdGroupReqVO> categoryIdGroups,
                                                                          String categoryTypeCode,
                                                                          String entityTypeCode, List<Long> modelIds,
                                                                          String keyword, List<FieldFilterReqVO> filters,
                                                                          Integer pageNo, Integer pageSize, boolean allowDirectPaging,
                                                                          String domain, String scopeRegistryCode,
                                                                          String orderByColumn, boolean orderAsc,
                                                                          KeywordSearchSpec keywordSearch) {
        return queryDataMgmtEntitiesByCategoryModel(
                categoryIds, categoryIdGroups, categoryTypeCode, entityTypeCode, modelIds,
                keyword, filters, pageNo, pageSize, allowDirectPaging, domain, scopeRegistryCode,
                orderByColumn, orderAsc, keywordSearch, EntityQueryResultDetail.FULL);
    }

    private PageResult<EntityRespVO> queryDataMgmtEntitiesByCategoryModel(List<Long> categoryIds,
                                                                          List<CategoryIdGroupReqVO> categoryIdGroups,
                                                                          String categoryTypeCode,
                                                                          String entityTypeCode, List<Long> modelIds,
                                                                          String keyword, List<FieldFilterReqVO> filters,
                                                                          Integer pageNo, Integer pageSize, boolean allowDirectPaging,
                                                                          String domain, String scopeRegistryCode,
                                                                          String orderByColumn, boolean orderAsc,
                                                                          KeywordSearchSpec keywordSearch,
                                                                          EntityQueryResultDetail detail) {
        if (entityTypeCode == null || entityTypeCode.isBlank()) {
            return new PageResult<>(new ArrayList<>(), 0L);
        }

        // 单组 / 多组求交：可下推排序 + 筛可下推 → 库内 EXISTS(AND) + ORDER BY + LIMIT
        List<List<Long>> expandedGroups = expandCategoryIdGroups(
                categoryIds, categoryIdGroups, categoryTypeCode);
        if (expandedGroups.isEmpty()) {
            return new PageResult<>(new ArrayList<>(), 0L);
        }
        List<PhysicalColumnFilter> pushFilters = resolvePushablePhysicalFilters(entityTypeCode, filters);
        String dbOrder = resolveDbOrderColumn(entityTypeCode, orderByColumn);
        KeywordSearchSpec effectiveKeywordSearch =
                keywordSearch != null ? keywordSearch : KeywordSearchSpec.nameOnly();
        boolean keywordPushOk = !StringUtils.hasText(keyword) || effectiveKeywordSearch.canPushFullyToEntityTable();
        EntityQueryResultDetail effectiveDetail =
                detail != null ? detail : EntityQueryResultDetail.FULL;
        if (allowDirectPaging
                && pushFilters != null
                && keywordPushOk
                && StringUtils.hasText(dbOrder)
                && categoryScopedEntityQueryRepository.supportsSqlOrder(dbOrder)) {
            Integer pn = normalizePageNo(pageNo);
            Integer ps = normalizePageSize(pageSize);
            long t0 = System.nanoTime();
            PageResult<Long> pageIds = categoryScopedEntityQueryRepository.pageEntityIdsByIntersectingCategoryGroups(
                    expandedGroups, entityTypeCode, normalizeModelIds(modelIds),
                    domain, scopeRegistryCode, dbOrder, orderAsc, pushFilters, keyword,
                    effectiveKeywordSearch, pn, ps);
            List<Long> orderedIds = pageIds.getList() != null ? pageIds.getList() : List.of();
            List<EntityRespVO> list = convertOrderedEntityIdsToRespList(orderedIds, entityTypeCode, effectiveDetail);
            if (log.isInfoEnabled()) {
                log.info("[query-by-scene timing] path={} type={} groups={} order={} "
                                + "page={}/{} rows={} total={} elapsed={}ms",
                        expandedGroups.size() > 1 ? "CATEGORY_GROUPS_DB_ORDER_PAGE" : "CATEGORY_DB_ORDER_PAGE",
                        entityTypeCode, expandedGroups.size(), dbOrder, pn, ps, list.size(), pageIds.getTotal(),
                        (System.nanoTime() - t0) / 1_000_000L);
            }
            return new PageResult<>(list, pageIds.getTotal());
        }

        List<Long> orderedCandidateEntityIds;
        if (expandedGroups.size() == 1) {
            orderedCandidateEntityIds = dataMgmtEntityQueryRepository.listOrderedEntityIdsByCategoryScope(
                    expandedGroups.get(0), entityTypeCode, normalizeModelIds(modelIds), domain, scopeRegistryCode);
        } else {
            orderedCandidateEntityIds = dataMgmtEntityQueryRepository.listOrderedEntityIdsByIntersectingCategoryGroups(
                    expandedGroups, entityTypeCode, normalizeModelIds(modelIds), domain, scopeRegistryCode);
        }

        orderedCandidateEntityIds = applyFieldOrderToCandidateIds(
                orderedCandidateEntityIds, entityTypeCode, orderByColumn, orderAsc);

        if (allowDirectPaging && canPageDirectly(keyword, filters)) {
            Integer pn = normalizePageNo(pageNo);
            Integer ps = normalizePageSize(pageSize);
            return pageByOrderedIds(orderedCandidateEntityIds, entityTypeCode, pn, ps, effectiveDetail);
        }
        return queryEntitiesByOrderedCandidateIds(
                orderedCandidateEntityIds, entityTypeCode, keyword, filters, pageNo, pageSize,
                effectiveKeywordSearch, effectiveDetail);
    }

    private List<List<Long>> expandCategoryIdGroups(List<Long> categoryIds,
                                                    List<CategoryIdGroupReqVO> categoryIdGroups,
                                                    String categoryTypeCode) {
        return expandCategoryIdGroups(categoryIds, categoryIdGroups, categoryTypeCode, true);
    }

    private List<List<Long>> expandCategoryIdGroups(List<Long> categoryIds,
                                                    List<CategoryIdGroupReqVO> categoryIdGroups,
                                                    String categoryTypeCode,
                                                    boolean includeDescendants) {
        List<List<Long>> expandedGroups = new ArrayList<>();
        if (categoryIdGroups != null && !categoryIdGroups.isEmpty()) {
            for (CategoryIdGroupReqVO group : categoryIdGroups) {
                if (group == null) {
                    continue;
                }
                String groupTypeCode = (group.getCategoryTypeCode() == null || group.getCategoryTypeCode().isBlank())
                        ? categoryTypeCode
                        : group.getCategoryTypeCode().trim();
                List<Long> normalized = includeDescendants
                        ? normalizeCategoryIdsOrUseRootCategory(group.getCategoryIds(), groupTypeCode)
                        : normalizePositiveCategoryIds(group.getCategoryIds());
                if (normalized.isEmpty()) {
                    continue;
                }
                List<Long> expanded = includeDescendants
                        ? expandCategoryIdsWithDescendants(normalized, groupTypeCode)
                        : normalized;
                if (!expanded.isEmpty()) {
                    expandedGroups.add(expanded);
                }
            }
            return expandedGroups;
        }
        List<Long> normalizedCategoryIds = includeDescendants
                ? normalizeCategoryIdsOrUseRootCategory(categoryIds, categoryTypeCode)
                : normalizePositiveCategoryIds(categoryIds);
        if (normalizedCategoryIds.isEmpty()) {
            return List.of();
        }
        List<Long> expandedCategoryIds = includeDescendants
                ? expandCategoryIdsWithDescendants(normalizedCategoryIds, categoryTypeCode)
                : normalizedCategoryIds;
        if (expandedCategoryIds.isEmpty()) {
            return List.of();
        }
        return List.of(expandedCategoryIds);
    }

    private List<Long> normalizePositiveCategoryIds(List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return List.of();
        }
        LinkedHashSet<Long> out = new LinkedHashSet<>();
        for (Long id : categoryIds) {
            if (id != null && id > 0) {
                out.add(id);
            }
        }
        return new ArrayList<>(out);
    }

    /** 兼容旧调用：无 categoryIdGroups / 无字段排序 */
    private PageResult<EntityRespVO> queryDataMgmtEntitiesByCategoryModel(List<Long> categoryIds, String categoryTypeCode,
                                                                          String entityTypeCode, List<Long> modelIds,
                                                                          String keyword, List<FieldFilterReqVO> filters,
                                                                          Integer pageNo, Integer pageSize, boolean allowDirectPaging,
                                                                          String domain, String scopeRegistryCode) {
        return queryDataMgmtEntitiesByCategoryModel(categoryIds, null, categoryTypeCode, entityTypeCode, modelIds,
                keyword, filters, pageNo, pageSize, allowDirectPaging, domain, scopeRegistryCode, null, true,
                null);
    }

    /** 保留 primary 顺序，追加 supplemental 中未出现的 id。 */
    private List<Long> mergeDistinctOrderedEntityIds(List<Long> primary, List<Long> supplemental) {
        if (supplemental == null || supplemental.isEmpty()) {
            return primary == null ? new ArrayList<>() : primary;
        }
        if (primary == null || primary.isEmpty()) {
            return new ArrayList<>(supplemental);
        }
        LinkedHashSet<Long> merged = new LinkedHashSet<>(primary);
        merged.addAll(supplemental);
        return new ArrayList<>(merged);
    }

    /**
     * 对多个输入分类做“含子树”展开，并按输入顺序稳定合并去重。
     */
    private List<Long> expandCategoryIdsWithDescendants(List<Long> categoryIds, String categoryTypeCode) {
        List<Long> mergedDescendantCategoryIds = new ArrayList<>();
        Set<Long> seen = new HashSet<>();
        if (categoryIds == null || categoryIds.isEmpty()) {
            return mergedDescendantCategoryIds;
        }
        Map<Long, List<Long>> descendantsByRoot = categoryService.getAllCategoryIdsIncludingChildrenBatch(categoryIds, categoryTypeCode);
        for (Long categoryId : categoryIds) {
            if (categoryId == null) {
                continue;
            }
            List<Long> descendants = descendantsByRoot.get(categoryId);
            if (descendants == null || descendants.isEmpty()) {
                if (seen.add(categoryId)) {
                    mergedDescendantCategoryIds.add(categoryId);
                }
                continue;
            }
            for (Long id : descendants) {
                if (id != null && seen.add(id)) {
                    mergedDescendantCategoryIds.add(id);
                }
            }
        }
        return mergedDescendantCategoryIds;
    }

/**
     * 将有序实体ID列表统一转换为分页结果。
     *
     * <p>当 pageNo/pageSize 为空时返回全量；否则按标准分页参数返回切片。</p>
     */
    private PageResult<EntityRespVO> buildEntityRespPageResult(List<EntityRespVO> orderedEntities, Integer pageNo, Integer pageSize) {
        if (orderedEntities == null || orderedEntities.isEmpty()) {
            return new PageResult<>(new ArrayList<>(), 0L);
        }
        Integer pn = normalizePageNo(pageNo);
        Integer ps = normalizePageSize(pageSize);
        int from = (pn - 1) * ps;
        if (from >= orderedEntities.size()) {
            return new PageResult<>(new ArrayList<>(), (long) orderedEntities.size());
        }
        int to = Math.min(from + ps, orderedEntities.size());
        return new PageResult<>(new ArrayList<>(orderedEntities.subList(from, to)), (long) orderedEntities.size());
    }

    /** ----1.2 场景二： patternB的视图，按分类查询模型下的实体列表（单/多分类分流）, 支持搜索功能-----------
     *
     * <p><b>你可以把它理解成 6 步：</b></p>
     * <ol>
     *   <li>检查参数（分类、业务类型）并规范分页参数；</li>
     *   <li>先按分类拿到“有顺序的模型ID列表”；</li>
     *   <li>按这些模型ID一次性查出实体候选；</li>
     *   <li>按模型顺序重排实体（模型内再按实体ID升序）；</li>
     *   <li>如果有 keyword，按名称/customFields 做过滤；</li>
     *   <li>最后做内存分页并返回。</li>
     * </ol>
     *
     * <p><b>顺序规则</b>：分类顺序 -> 模型顺序 -> 实体ID顺序。</p>
     */
    /**
     * Pattern B：按分类查实体时，必须在分类范围内取 entity↔category 关联（relation ∪ link），
     * 再与分类下可用模型交叉过滤；不可仅按 modelId 扫全表实体。
     */
    private List<Long> collectPatternBCategoryOrderedEntityIds(List<Long> categoryIds, String categoryTypeCode,
                                                               String entityTypeCode) {
        if (entityTypeCode == null || entityTypeCode.isBlank()) {
            return new ArrayList<>();
        }

        List<Long> normalizedCategoryIds = normalizeCategoryIdsOrUseRootCategory(categoryIds, categoryTypeCode);
        if (normalizedCategoryIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> expandedCategoryIds = expandCategoryIdsWithDescendants(normalizedCategoryIds, categoryTypeCode);
        if (expandedCategoryIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> orderedModelIds = modelService
                .queryOrderedModelIdsByCategoriesInBusiness(categoryIds, categoryTypeCode, entityTypeCode, null, null)
                .getList();
        log.info("[PATTERN_B_TRACE] collectPatternBCategoryOrderedEntityIds model stage: categoryTypeCode={}, entityTypeCode={}, categoryIds={}, orderedModelCount={}",
                categoryTypeCode, entityTypeCode, categoryIds, orderedModelIds == null ? 0 : orderedModelIds.size());

        List<Long> modelFilter = (orderedModelIds == null || orderedModelIds.isEmpty()) ? null : orderedModelIds;
        List<Long> orderedEntityIds = dataMgmtEntityQueryRepository.listOrderedEntityIdsByCategoryScope(
                expandedCategoryIds, entityTypeCode, modelFilter);

        log.info("[PATTERN_B_TRACE] collectPatternBCategoryOrderedEntityIds entity stage: entityTypeCode={}, orderedEntityCount={}",
                entityTypeCode, orderedEntityIds == null ? 0 : orderedEntityIds.size());
        return orderedEntityIds == null ? new ArrayList<>() : orderedEntityIds;
    }

    /**
     * 全部实体：按业务类型返回全部实体 ID（有序）；可按业务域早过滤。
     */
    private List<Long> collectAllEntityIdsByEntityType(String entityTypeCode) {
        return collectAllEntityIdsByEntityType(entityTypeCode, null);
    }

    private List<Long> collectAllEntityIdsByEntityType(String entityTypeCode, String domain) {
        if (entityTypeCode == null || entityTypeCode.isBlank()) {
            return new ArrayList<>();
        }
        EntityRepository.EntityQuery query = EntityRepository.EntityQuery.builder()
                .entityTypeCode(entityTypeCode)
                .domain(domain)
                .build();
        List<EntityDO> allEntities = entityRepository.findAll(query);
        if (allEntities == null || allEntities.isEmpty()) {
            return new ArrayList<>();
        }
        return allEntities.stream()
                .map(EntityDO::getId)
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * 通用后处理：基于“前置已确定顺序的候选实体ID”执行搜索/筛选，再输出分页或全量结果。
     */
    private PageResult<EntityRespVO> queryEntitiesByOrderedCandidateIds(List<Long> orderedCandidateEntityIds,
                                                                        String entityTypeCode, String keyword, List<FieldFilterReqVO> filters,
                                                                        Integer pageNo, Integer pageSize) {
        return queryEntitiesByOrderedCandidateIds(
                orderedCandidateEntityIds, entityTypeCode, keyword, filters, pageNo, pageSize, null);
    }

    private PageResult<EntityRespVO> queryEntitiesByOrderedCandidateIds(List<Long> orderedCandidateEntityIds,
                                                                        String entityTypeCode, String keyword, List<FieldFilterReqVO> filters,
                                                                        Integer pageNo, Integer pageSize,
                                                                        KeywordSearchSpec keywordSearch) {
        return queryEntitiesByOrderedCandidateIds(
                orderedCandidateEntityIds, entityTypeCode, keyword, filters, pageNo, pageSize,
                keywordSearch, EntityQueryResultDetail.FULL);
    }

    private PageResult<EntityRespVO> queryEntitiesByOrderedCandidateIds(List<Long> orderedCandidateEntityIds,
                                                                        String entityTypeCode, String keyword, List<FieldFilterReqVO> filters,
                                                                        Integer pageNo, Integer pageSize,
                                                                        KeywordSearchSpec keywordSearch,
                                                                        EntityQueryResultDetail detail) {
        if (orderedCandidateEntityIds == null || orderedCandidateEntityIds.isEmpty()) {
            return new PageResult<>(new ArrayList<>(), 0L);
        }
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        boolean hasFilters = filters != null && !filters.isEmpty();
        EntityQueryResultDetail effectiveDetail =
                detail != null ? detail : EntityQueryResultDetail.FULL;

        // 性能优化：无 keyword/filters 且请求分页时，直接在有序 ID 上分页切片后回查，
        // 避免“先全量转 VO 再内存分页”导致大分类场景响应过慢。
        if (!hasKeyword && !hasFilters && pageNo != null && pageSize != null) {
            return pageByOrderedIds(orderedCandidateEntityIds, entityTypeCode, pageNo, pageSize, effectiveDetail);
        }

        List<EntityRespVO> searchedAndFilteredEntities;
        if (!hasKeyword && !hasFilters) {
            searchedAndFilteredEntities = convertOrderedEntityIdsToRespList(
                    orderedCandidateEntityIds, entityTypeCode, effectiveDetail);
        } else {
            // 有搜索/筛选时，直接返回过滤后的 VO 列表，避免后续再按 ID 二次回查实体
            searchedAndFilteredEntities = filterCandidateEntities(
                    orderedCandidateEntityIds, entityTypeCode, filters, keyword, keywordSearch);
        }
        if (pageNo == null || pageSize == null) {
            return new PageResult<>(searchedAndFilteredEntities, (long) searchedAndFilteredEntities.size());
        }
        return buildEntityRespPageResult(searchedAndFilteredEntities, pageNo, pageSize);
    }

    /**----------------- 2 按模型查询实体（用户点击的是模型节点）---------------------------*/
    /** ----2.1 场景三：（模型列表）：按模型进行实体分页查询。-----------
     *
     * <p><b>用途</b>：</p>
     * <ul>
     *   <li>处理“点击模型/勾选多个模型”后的实体列表展示。</li>
     *   <li>在保留原有分页逻辑的前提下，按需叠加 filters 与 keyword。</li>
     * </ul>
     *
     * <p><b>执行步骤</b>：</p>
     * <ol>
     *   <li>若无 filters：直接复用原分页能力 {@link #pageEntitiesByModelIds(List, String, Integer, Integer)}。</li>
     *   <li>若有 filters：先收集候选 entityIds，再执行筛选与搜索，最后对过滤后的 IDs 分页回查详情。</li>
     * </ol>
     */
    private PageResult<EntityRespVO> handlePatternBModelEntities(List<Long> modelIds, String entityTypeCode,
                                                                    String keyword, List<FieldFilterReqVO> filters,
                                                                    Integer pageNo, Integer pageSize,
                                                                    String domain, String scopeRegistryCode,
                                                                    String orderByColumn, boolean orderAsc,
                                                                    KeywordSearchSpec keywordSearch) {
        return handlePatternBModelEntities(
                modelIds, entityTypeCode, keyword, filters, pageNo, pageSize, domain, scopeRegistryCode,
                orderByColumn, orderAsc, keywordSearch, EntityQueryResultDetail.FULL);
    }

    private PageResult<EntityRespVO> handlePatternBModelEntities(List<Long> modelIds, String entityTypeCode,
                                                                    String keyword, List<FieldFilterReqVO> filters,
                                                                    Integer pageNo, Integer pageSize,
                                                                    String domain, String scopeRegistryCode,
                                                                    String orderByColumn, boolean orderAsc,
                                                                    KeywordSearchSpec keywordSearch,
                                                                    EntityQueryResultDetail detail) {
        boolean hasScope = StringUtils.hasText(scopeRegistryCode);
        boolean hasFieldOrder = StringUtils.hasText(orderByColumn);
        List<PhysicalColumnFilter> pushFilters = resolvePushablePhysicalFilters(entityTypeCode, filters);
        String dbOrder = hasFieldOrder ? resolveDbOrderColumn(entityTypeCode, orderByColumn) : null;
        boolean noPushFilters = pushFilters != null && pushFilters.isEmpty();
        String evaValueCol = hasFieldOrder && !StringUtils.hasText(dbOrder) && noPushFilters
                ? resolveEvaIndexValueColumn(entityTypeCode, orderByColumn) : null;
        KeywordSearchSpec effectiveKeywordSearch =
                keywordSearch != null ? keywordSearch : KeywordSearchSpec.nameOnly();
        boolean keywordPushOk = !StringUtils.hasText(keyword) || effectiveKeywordSearch.canPushFullyToEntityTable();
        boolean keywordEvaOk = !StringUtils.hasText(keyword) || effectiveKeywordSearch.isNameOnlyLike();
        EntityQueryResultDetail effectiveDetail =
                detail != null ? detail : EntityQueryResultDetail.FULL;
        // 筛可下推（或无筛）、无划分：实体表 / EVA 直分页（keyword 多列 OR，默认可 name）。
        if (pushFilters != null && !hasScope && keywordPushOk
                && (!hasFieldOrder || StringUtils.hasText(dbOrder)
                    || (StringUtils.hasText(evaValueCol) && keywordEvaOk))) {
            long t0 = System.nanoTime();
            PageResult<EntityRespVO> page;
            String path;
            if (StringUtils.hasText(evaValueCol) && !StringUtils.hasText(dbOrder) && keywordEvaOk) {
                page = pageEntitiesByEvaOrder(
                        entityTypeCode, modelIds, keyword, domain,
                        orderByColumn.trim(), evaValueCol, pageNo, pageSize, orderAsc, effectiveDetail);
                path = "MODEL_EVA_ORDER_PAGE";
            } else {
                page = pageEntitiesByModelIds(
                        modelIds, keyword, pageNo, pageSize, domain,
                        dbOrder, orderAsc, pushFilters, effectiveKeywordSearch, effectiveDetail);
                path = "MODEL_IDS_DB_PAGE";
            }
            if (log.isInfoEnabled()) {
                int n = page.getList() != null ? page.getList().size() : 0;
                log.info("[query-by-scene timing] path={} models={} keyword={} "
                                + "order={} rows={} total={} elapsed={}ms",
                        path, modelIds != null ? modelIds.size() : 0,
                        keyword != null && !keyword.isBlank(),
                        dbOrder != null ? dbOrder : (evaValueCol != null ? orderByColumn : "sort"),
                        n, page.getTotal(), (System.nanoTime() - t0) / 1_000_000L);
            }
            return page;
        }
        List<Long> candidateIds = collectCandidateEntityIdsByModelIds(modelIds, entityTypeCode);
        candidateIds = dataMgmtEntityQueryRepository.retainOrderedIdsByDomainAndScope(
                candidateIds, entityTypeCode, domain, scopeRegistryCode);
        if (candidateIds.isEmpty()) {
            return new PageResult<>(new ArrayList<>(), 0L);
        }
        boolean hasFilters = filters != null && !filters.isEmpty();
        if (hasFilters || (keyword != null && !keyword.trim().isEmpty())) {
            candidateIds = filterCandidateEntityIds(
                    candidateIds, entityTypeCode, filters, keyword, effectiveKeywordSearch);
            if (candidateIds.isEmpty()) {
                return new PageResult<>(new ArrayList<>(), 0L);
            }
        }
        candidateIds = applyFieldOrderToCandidateIds(candidateIds, entityTypeCode, orderByColumn, orderAsc);
        return pageByOrderedIds(candidateIds, entityTypeCode, pageNo, pageSize);
    }

    /**
     * 场景 8：外类型型号上下文 vs 本类实体（如 equipment 型号 vs inspection_item）。
     */
    private boolean isCrossTypeModelEntityQuery(String modelEntityTypeCode, String entityTypeCode) {
        if (!StringUtils.hasText(modelEntityTypeCode) || !StringUtils.hasText(entityTypeCode)) {
            return false;
        }
        ResolvedQueryType modelResolved = resolveQueryEntityType(modelEntityTypeCode.trim(), null);
        String modelStorage = modelResolved.entityTypeCode();
        if (!StringUtils.hasText(modelStorage)) {
            return false;
        }
        return !modelStorage.equalsIgnoreCase(entityTypeCode.trim());
    }

    /**
     * 外类型型号 → 本类实体：挂钩表取 id，再按本类实体表装 VO（含 name）。
     * <p>本类 {@code model_id} 只表示本类型号归属，禁止用外类型 modelId 去筛。</p>
     */
    private PageResult<EntityRespVO> handleCrossTypeModelRelatedEntities(
            List<Long> modelIds,
            String modelEntityTypeCode,
            String entityTypeCode,
            String keyword,
            List<FieldFilterReqVO> filters,
            Integer pageNo,
            Integer pageSize,
            String domain,
            String scopeRegistryCode,
            String orderByColumn,
            boolean orderAsc,
            KeywordSearchSpec keywordSearch) {
        long t0 = System.nanoTime();
        ResolvedQueryType modelResolved = resolveQueryEntityType(modelEntityTypeCode, null);
        String modelStorage = modelResolved.entityTypeCode();
        if (!StringUtils.hasText(modelStorage)) {
            throw new ServiceException(400, "modelEntityTypeCode 无法解析为存储类型");
        }
        List<Long> candidateIds = modelEntityRelationService.listEntityIdsByModelIds(
                modelIds, modelStorage, entityTypeCode);
        candidateIds = dataMgmtEntityQueryRepository.retainOrderedIdsByDomainAndScope(
                candidateIds, entityTypeCode, domain, scopeRegistryCode);
        if (candidateIds.isEmpty()) {
            return new PageResult<>(new ArrayList<>(), 0L);
        }
        boolean hasFilters = filters != null && !filters.isEmpty();
        if (hasFilters || (keyword != null && !keyword.trim().isEmpty())) {
            candidateIds = filterCandidateEntityIds(
                    candidateIds, entityTypeCode, filters, keyword, keywordSearch);
            if (candidateIds.isEmpty()) {
                return new PageResult<>(new ArrayList<>(), 0L);
            }
        }
        candidateIds = applyFieldOrderToCandidateIds(candidateIds, entityTypeCode, orderByColumn, orderAsc);
        PageResult<EntityRespVO> page = queryEntitiesByOrderedCandidateIds(
                candidateIds, entityTypeCode, null, null, pageNo, pageSize, keywordSearch);
        if (log.isInfoEnabled()) {
            int n = page.getList() != null ? page.getList().size() : 0;
            log.info("[query-by-scene timing] path=CROSS_TYPE_MODEL_RELATION models={} entityType={} "
                            + "rows={} total={} elapsed={}ms",
                    modelIds != null ? modelIds.size() : 0, entityTypeCode,
                    n, page.getTotal(), (System.nanoTime() - t0) / 1_000_000L);
        }
        return page;
    }

    /**----3. 分类即实体（Category Link Entity）视图下的分类详情查询
     * 3.1 场景四 ：Pattern C：分类即实体（Category Link Entity）视图下的分类详情查询。
     *
     * 区别在于该场景用于“分类详情 = 分类链接实体详情”。</p>
     */


    /**
     * 场景五 模式 D：单条实体详情并包含关联块（default 扁平行；多视角见 get + associationCategoryViews）。
     */
    private EntityRespVO buildPatternDEntityDetailedInfo(Long entityId, String entityTypeCode) {
        if (entityId == null || entityTypeCode == null) {
            throw new ServiceException(400, "参数缺失");
        }
        return get(entityId, entityTypeCode, true, null);
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


/**---------------------------------------------标准能力---------------------------------------------*/
    /** ----------------标准能力：按 modelIds 分页查询实体。-----------
     *
     * <p>统一处理 modelIds 参数清洗、单/多模型分流、业务类型校验与结果转换，
     * 供 Pattern B 等调用方复用。</p>
     */
    private PageResult<EntityRespVO> pageEntitiesByModelIds(List<Long> modelIds,
                                                                String keyword, Integer pageNo, Integer pageSize,
                                                                String domain) {
        return pageEntitiesByModelIds(modelIds, keyword, pageNo, pageSize, domain, null, true);
    }

    /**
     * 按型号直分页；orderByColumn 为可下推列（核心列或物理列名）时走库内 ORDER BY + LIMIT。
     */
    private PageResult<EntityRespVO> pageEntitiesByModelIds(List<Long> modelIds,
                                                                String keyword, Integer pageNo, Integer pageSize,
                                                                String domain, String orderByColumn, boolean orderAsc) {
        return pageEntitiesByModelIds(modelIds, keyword, pageNo, pageSize, domain,
                orderByColumn, orderAsc, null);
    }

    private PageResult<EntityRespVO> pageEntitiesByModelIds(List<Long> modelIds,
                                                                String keyword, Integer pageNo, Integer pageSize,
                                                                String domain, String orderByColumn, boolean orderAsc,
                                                                List<PhysicalColumnFilter> physicalFilters) {
        return pageEntitiesByModelIds(modelIds, keyword, pageNo, pageSize, domain,
                orderByColumn, orderAsc, physicalFilters, null);
    }

    private PageResult<EntityRespVO> pageEntitiesByModelIds(List<Long> modelIds,
                                                                String keyword, Integer pageNo, Integer pageSize,
                                                                String domain, String orderByColumn, boolean orderAsc,
                                                                List<PhysicalColumnFilter> physicalFilters,
                                                                KeywordSearchSpec keywordSearch) {
        return pageEntitiesByModelIds(modelIds, keyword, pageNo, pageSize, domain,
                orderByColumn, orderAsc, physicalFilters, keywordSearch, EntityQueryResultDetail.FULL);
    }

    private PageResult<EntityRespVO> pageEntitiesByModelIds(List<Long> modelIds,
                                                                String keyword, Integer pageNo, Integer pageSize,
                                                                String domain, String orderByColumn, boolean orderAsc,
                                                                List<PhysicalColumnFilter> physicalFilters,
                                                                KeywordSearchSpec keywordSearch,
                                                                EntityQueryResultDetail detail) {
        List<Long> normalizedModelIds = requireModelIds(modelIds, "modelIds 不能为空");
        String normalizedDomain = EntityTypeScopeContext.normalizeDomain(domain);
        String coreOrder = StringUtils.hasText(orderByColumn) ? orderByColumn.trim() : null;
        KeywordSearchSpec effectiveKeywordSearch =
                keywordSearch != null ? keywordSearch : KeywordSearchSpec.nameOnly();
        EntityQueryResultDetail effectiveDetail =
                detail != null ? detail : EntityQueryResultDetail.FULL;

        if (normalizedModelIds.size() == 1) {
            Long singleModelId = normalizedModelIds.get(0);
            ModelDO model = modelMapper.selectById(singleModelId);
            if (model == null) {
                throw new ServiceException(404, "模型不存在");
            }
            EntityRepository.EntityQuery query = EntityRepository.EntityQuery.builder()
                    .entityTypeCode(model.getEntityTypeCode())
                    .modelId(singleModelId)
                    .keyword(keyword)
                    .keywordSearch(effectiveKeywordSearch)
                    .domain(normalizedDomain)
                    .pageNo(pageNo)
                    .pageSize(pageSize)
                    .orderByColumn(coreOrder)
                    .orderAsc(orderAsc)
                    .physicalFilters(physicalFilters)
                    .build();
            PageResult<Long> pageIds = entityRepository.findPageIds(query);
            List<Long> orderedIds = pageIds.getList() != null ? pageIds.getList() : List.of();
            List<EntityRespVO> list = convertOrderedEntityIdsToRespList(
                    orderedIds, model.getEntityTypeCode(), effectiveDetail);
            return new PageResult<>(list, pageIds.getTotal());
        }

        List<ModelDO> models = modelMapper.selectByIds(normalizedModelIds);
        if (models == null || models.isEmpty()) {
            throw new ServiceException(404, "模型不存在");
        }
        String entityTypeCode = models.get(0).getEntityTypeCode();
        if (entityTypeCode == null || entityTypeCode.isBlank()) {
            throw new ServiceException(400, "业务类型不能为空");
        }

        PageResult<Long> pageIds = entityRepository.findPageIdsByModelIds(
                normalizedModelIds,
                entityTypeCode,
                null,
                keyword,
                normalizedDomain,
                pageNo,
                pageSize,
                coreOrder,
                orderAsc,
                physicalFilters,
                effectiveKeywordSearch
        );
        List<Long> orderedIds = pageIds.getList() != null ? pageIds.getList() : List.of();
        List<EntityRespVO> list = convertOrderedEntityIdsToRespList(orderedIds, entityTypeCode, effectiveDetail);
        return new PageResult<>(list, pageIds.getTotal());
    }

    /**
     * 未限定型号时：整业务类型库内按可下推列排序分页（ORDER BY + LIMIT）。
     * {@code orderByColumn} 须已是核心列名或专用表物理列名。
     */
    private PageResult<EntityRespVO> pageEntitiesByEntityTypeDbOrder(String entityTypeCode,
                                                                     String keyword,
                                                                     String domain,
                                                                     Integer pageNo,
                                                                     Integer pageSize,
                                                                     String orderByColumn,
                                                                     boolean orderAsc,
                                                                     List<PhysicalColumnFilter> physicalFilters,
                                                                     KeywordSearchSpec keywordSearch) {
        return pageEntitiesByEntityTypeDbOrder(
                entityTypeCode, keyword, domain, pageNo, pageSize, orderByColumn, orderAsc,
                physicalFilters, keywordSearch, EntityQueryResultDetail.FULL);
    }

    private PageResult<EntityRespVO> pageEntitiesByEntityTypeDbOrder(String entityTypeCode,
                                                                     String keyword,
                                                                     String domain,
                                                                     Integer pageNo,
                                                                     Integer pageSize,
                                                                     String orderByColumn,
                                                                     boolean orderAsc,
                                                                     List<PhysicalColumnFilter> physicalFilters,
                                                                     KeywordSearchSpec keywordSearch,
                                                                     EntityQueryResultDetail detail) {
        if (!StringUtils.hasText(entityTypeCode) || !StringUtils.hasText(orderByColumn)) {
            return new PageResult<>(new ArrayList<>(), 0L);
        }
        EntityRepository.EntityQuery query = EntityRepository.EntityQuery.builder()
                .entityTypeCode(entityTypeCode.trim())
                .keyword(keyword)
                .keywordSearch(keywordSearch != null ? keywordSearch : KeywordSearchSpec.nameOnly())
                .domain(EntityTypeScopeContext.normalizeDomain(domain))
                .pageNo(pageNo)
                .pageSize(pageSize)
                .orderByColumn(orderByColumn.trim())
                .orderAsc(orderAsc)
                .physicalFilters(physicalFilters)
                .build();
        long t0 = System.nanoTime();
        PageResult<Long> pageIds = entityRepository.findPageIds(query);
        long t1 = System.nanoTime();
        List<Long> orderedIds = pageIds.getList() != null ? pageIds.getList() : List.of();
        List<EntityRespVO> list = convertOrderedEntityIdsToRespList(
                orderedIds, entityTypeCode.trim(), detail != null ? detail : EntityQueryResultDetail.FULL);
        long t2 = System.nanoTime();
        log.info("[query-by-scene timing] stage=pageEntitiesByEntityTypeDbOrder findPageIds={}ms "
                        + "convert={}ms order={} ids={} totalRows={}",
                (t1 - t0) / 1_000_000L, (t2 - t1) / 1_000_000L, orderByColumn,
                orderedIds.size(), pageIds.getTotal());
        return new PageResult<>(list, pageIds.getTotal());
    }

    private PageResult<EntityRespVO> pageEntitiesByEvaOrder(String entityTypeCode,
                                                            List<Long> modelIds,
                                                            String keyword,
                                                            String domain,
                                                            String fieldCode,
                                                            String indexValueColumn,
                                                            Integer pageNo,
                                                            Integer pageSize,
                                                            boolean orderAsc) {
        return pageEntitiesByEvaOrder(
                entityTypeCode, modelIds, keyword, domain, fieldCode, indexValueColumn,
                pageNo, pageSize, orderAsc, EntityQueryResultDetail.FULL);
    }

    private PageResult<EntityRespVO> pageEntitiesByEvaOrder(String entityTypeCode,
                                                            List<Long> modelIds,
                                                            String keyword,
                                                            String domain,
                                                            String fieldCode,
                                                            String indexValueColumn,
                                                            Integer pageNo,
                                                            Integer pageSize,
                                                            boolean orderAsc,
                                                            EntityQueryResultDetail detail) {
        if (!StringUtils.hasText(entityTypeCode) || !StringUtils.hasText(fieldCode)
                || !StringUtils.hasText(indexValueColumn)) {
            return new PageResult<>(new ArrayList<>(), 0L);
        }
        PageResult<Long> pageIds = evaFieldOrderEntityQueryRepository.pageEntityIdsByEvaOrder(
                entityTypeCode.trim(), modelIds, domain, fieldCode.trim(), indexValueColumn,
                orderAsc, keyword, normalizePageNo(pageNo), normalizePageSize(pageSize));
        List<Long> orderedIds = pageIds.getList() != null ? pageIds.getList() : List.of();
        List<EntityRespVO> list = convertOrderedEntityIdsToRespList(
                orderedIds, entityTypeCode.trim(), detail != null ? detail : EntityQueryResultDetail.FULL);
        return new PageResult<>(list, pageIds.getTotal());
    }

    // ==================== 列表查询辅助函数（按阶段） ====================

    /**
     * 收集模型查询的候选 entityIds（不做过滤、不做分页）。
     *
     * <p><b>用途</b>：把“模型选择”转换成“实体候选集合”，供后续筛选与分页复用。</p>
     * <p><b>输入</b>：modelIds（允许单模型或多模型，允许重复和空值）。</p>
     * <p><b>输出</b>：有序 entityId 列表（按模型输入顺序展开）。</p>
     *
     * <p><b>执行步骤</b>：</p>
     * <ol>
     *   <li>清洗 modelIds（去空、去重）。</li>
     *   <li>逐个模型读取实体 IDs。</li>
     *   <li>按模型顺序拼接返回。</li>
     * </ol>
     */
    private List<Long> collectCandidateEntityIdsByModelIds(List<Long> modelIds) {
        List<Long> normalizedModelIds = normalizeModelIds(modelIds);
        if (normalizedModelIds.isEmpty()) {
            return List.of();
        }

        // 逐模型收集实体ID，并按模型输入顺序拼接结果。
        List<Long> orderedEntityIds = new ArrayList<>();
        for (Long mid : normalizedModelIds) {
            List<Long> ids = entityCoreService.getEntityIdsByModelId(mid, null);
            if (ids == null || ids.isEmpty()) {
                continue;
            }
            orderedEntityIds.addAll(ids);
        }
        return orderedEntityIds;
    }

    private List<Long> collectCandidateEntityIdsByModelIds(List<Long> modelIds, String entityTypeCode) {
        List<Long> normalizedModelIds = normalizeModelIds(modelIds);
        if (normalizedModelIds.isEmpty()) {
            return List.of();
        }

        List<Long> orderedEntityIds = new ArrayList<>();
        for (Long mid : normalizedModelIds) {
            List<Long> ids = entityCoreService.getEntityIdsByModelId(mid, entityTypeCode);
            if (ids == null || ids.isEmpty()) {
                continue;
            }
            orderedEntityIds.addAll(ids);
        }
        return orderedEntityIds;
    }

    /**
     * 对候选 entityIds 执行筛选与搜索（先关联字段，再非关联字段，最后 keyword）。
     *
     * <p><b>用途</b>：在候选 ID 集合上统一处理 filters + keyword，输出可分页的最终 ID 集合。</p>
     *
     * <p><b>固定执行顺序</b>：</p>
     * <ol>
     *   <li>关联字段筛选（relationField=true）：走关系表反查 source_entity_id 并做交集。</li>
     *   <li>非关联字段筛选：按字段类型和操作符做结构化匹配。</li>
     *   <li>keyword 搜索：只在名称 + 该模型可搜索字段上匹配（不可搜索字段不参与）。</li>
     * </ol>
     *
     * <p><b>实现说明</b>：当前非关联筛选会回查实体详情进行匹配；后续可下沉索引层优化性能。</p>
     */
    private List<Long> filterCandidateEntityIds(
            List<Long> candidateIds,
            String entityTypeCode,
            List<FieldFilterReqVO> filters,
            String keyword
    ) {
        return filterCandidateEntityIds(candidateIds, entityTypeCode, filters, keyword, null);
    }

    private List<Long> filterCandidateEntityIds(
            List<Long> candidateIds,
            String entityTypeCode,
            List<FieldFilterReqVO> filters,
            String keyword,
            KeywordSearchSpec keywordSearch
    ) {
        // 与分类有筛路径一致：走字段查询引擎（索引表 + keyword），避免型号路径再装全量 VO 内存匹配。
        if (candidateIds == null || candidateIds.isEmpty()) {
            return Collections.emptyList();
        }
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        boolean hasFilters = filters != null && !filters.isEmpty();
        if (!hasKeyword && !hasFilters) {
            return candidateIds;
        }
        Set<Long> matchedIds = entityFieldQueryEngine.searchAndFilterEntityIds(
                entityTypeCode, keyword, filters, candidateIds, toSearchFieldCodes(keywordSearch));
        if (matchedIds == null || matchedIds.isEmpty()) {
            return Collections.emptyList();
        }
        return candidateIds.stream()
                .filter(id -> id != null && matchedIds.contains(id))
                .toList();
    }

    private List<Long> retainCandidatesQueryableByField(List<Long> candidateIds, String entityTypeCode, String fieldCode) {
        if (candidateIds == null || candidateIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<EntityDO> entities = entityCoreService.listByIds(candidateIds, entityTypeCode);
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, Long> modelByEntityId = new HashMap<>();
        for (EntityDO entity : entities) {
            if (entity != null && entity.getId() != null) {
                modelByEntityId.put(entity.getId(), entity.getModelId());
            }
        }
        return candidateIds.stream()
                .filter(id -> id != null && fieldIndexService.isFieldSearchable(modelByEntityId.get(id), fieldCode))
                .toList();
    }

    private boolean isFieldQueryableForEntity(
            EntityRespVO vo,
            String fieldCode,
            Map<Long, Set<String>> searchableCodesByModel) {
        if (vo == null || fieldCode == null || fieldCode.isBlank()) {
            return false;
        }
        Long modelId = vo.getModelId();
        if (modelId == null) {
            return fieldIndexService.isFieldSearchable(null, fieldCode);
        }
        Set<String> codes = searchableCodesByModel.computeIfAbsent(modelId, id -> {
            Set<String> set = fieldIndexService.getSearchableFields(id).stream()
                    .map(FieldDO::getCode)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toCollection(HashSet::new));
            // 核心列始终可检索
            set.add("id");
            set.add("name");
            set.add("code");
            set.add("status");
            set.add("modelId");
            set.add("model_id");
            set.add("parentId");
            set.add("parent_id");
            set.add("entityTypeCode");
            set.add("entity_type_code");
            return set;
        });
        return codes.contains(fieldCode);
    }

    private boolean matchesKeywordOnQueryableFields(
            EntityRespVO vo,
            String keywordLower,
            Map<Long, Set<String>> searchableCodesByModel) {
        if (vo == null || keywordLower == null || keywordLower.isEmpty()) {
            return false;
        }
        if (vo.getName() != null && vo.getName().toLowerCase().contains(keywordLower)) {
            return true;
        }
        if (matchesKeywordInFieldMap(vo, vo.getBaseFields(), keywordLower, searchableCodesByModel)) {
            return true;
        }
        return matchesKeywordInFieldMap(vo, vo.getCustomFields(), keywordLower, searchableCodesByModel);
    }

    private boolean matchesKeywordInFieldMap(
            EntityRespVO vo,
            Map<String, Object> fields,
            String keywordLower,
            Map<Long, Set<String>> searchableCodesByModel) {
        if (fields == null || fields.isEmpty()) {
            return false;
        }
        for (Map.Entry<String, Object> entry : fields.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null) {
                continue;
            }
            if (!isFieldQueryableForEntity(vo, entry.getKey(), searchableCodesByModel)) {
                continue;
            }
            if (String.valueOf(entry.getValue()).toLowerCase().contains(keywordLower)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 对候选 entityIds 执行筛选与搜索，并直接返回按候选顺序排列的实体 VO 列表。
     */
    private List<EntityRespVO> filterCandidateEntities(
            List<Long> candidateIds,
            String entityTypeCode,
            List<FieldFilterReqVO> filters,
            String keyword
    ) {
        return filterCandidateEntities(candidateIds, entityTypeCode, filters, keyword, null);
    }

    private List<EntityRespVO> filterCandidateEntities(
            List<Long> candidateIds,
            String entityTypeCode,
            List<FieldFilterReqVO> filters,
            String keyword,
            KeywordSearchSpec keywordSearch
    ) {
        if (candidateIds == null || candidateIds.isEmpty()) {
            return new ArrayList<>();
        }

        Set<Long> matchedIds = entityFieldQueryEngine.searchAndFilterEntityIds(
                entityTypeCode,
                keyword,
                filters,
                candidateIds,
                toSearchFieldCodes(keywordSearch)
        );
        if (matchedIds == null || matchedIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> orderedMatchedIds = candidateIds.stream()
                .filter(id -> id != null && matchedIds.contains(id))
                .toList();
        if (orderedMatchedIds.isEmpty()) {
            return new ArrayList<>();
        }
        return fetchEntitiesByOrderedIds(orderedMatchedIds, entityTypeCode);
    }

    /**
     * 按“有序 IDs”分页并回查实体详情（保持顺序不变）。
     *
     * <p><b>用途</b>：把“过滤后的 ID 列表”转换成最终分页结果。</p>
     *
     * <p><b>执行步骤</b>：</p>
     * <ol>
     *   <li>标准化分页参数。</li>
     *   <li>在有序 ID 列表上做分页切片。</li>
     *   <li>按切片 IDs 回查实体详情，并按切片顺序回放返回。</li>
     * </ol>
     *
     * <p><b>返回约定</b>：total 使用过滤后的 ID 总数，确保分页总数准确。</p>
     */
    private PageResult<EntityRespVO> pageByOrderedIds(List<Long> orderedIds, String entityTypeCode, Integer pageNo, Integer pageSize) {
        return pageByOrderedIds(orderedIds, entityTypeCode, pageNo, pageSize, EntityQueryResultDetail.FULL);
    }

    private PageResult<EntityRespVO> pageByOrderedIds(List<Long> orderedIds, String entityTypeCode, Integer pageNo, Integer pageSize,
                                                      EntityQueryResultDetail detail) {
        // Step 1) 标准化分页参数。
        Integer pn = normalizePageNo(pageNo);
        Integer ps = normalizePageSize(pageSize);

        // Step 2) 在有序 ID 列表上做分页切片。
        int from = (pn - 1) * ps;
        if (from >= orderedIds.size()) {
            return new PageResult<>(new ArrayList<>(), (long) orderedIds.size());
        }
        int to = Math.min(from + ps, orderedIds.size());
        List<Long> pageIds = orderedIds.subList(from, to);

        // Step 3) 回查实体详情并按切片顺序返回。
        PageResult<Long> pagedEntityIds = new PageResult<>(pageIds, (long) orderedIds.size());
        return fetchEntityPageByOrderedIds(pagedEntityIds, entityTypeCode, detail);
    }

    private List<EntityRespVO> fetchEntitiesByOrderedIds(List<Long> orderedIds, String entityTypeCode) {
        if (orderedIds == null || orderedIds.isEmpty()) {
            return new ArrayList<>();
        }
        return convertOrderedEntityIdsToRespList(orderedIds, entityTypeCode);
    }

    @Override
    public EntityRespVO getCategoryLinkedEntity(Long categoryId, String entityTypeCode) {
        if (categoryId == null) {
            return null;
        }

        CategoryEntityLinkDO link = categoryEntityLinkService.getLinkByCategoryId(categoryId);
        if (link == null || link.getEntityId() == null) {
            return null;
        }

        List<EntityDO> loaded = entityCoreService.listByIdsWithDedicatedBaseFields(
                Collections.singletonList(link.getEntityId()), entityTypeCode);
        if (loaded == null || loaded.isEmpty()) {
            return null;
        }
        return EntityDoVoHelper.toRespVO(
                loaded.get(0), customFieldValidationService, entityDedicatedColumnService);
    }

    /** ----------------通过 categoryTypeCode 获取根分类ID（顶层分类）。-----------
     *
     * <p>仅在“未选择任何分类”时使用，用于把“查看全部”翻译成根分类查询。</p>
     */
    private Long resolveRootCategoryId(String categoryTypeCode) {
        if (categoryTypeCode == null || categoryTypeCode.isBlank()) {
            return null;
        }
        var categoryType = categoryTypeService.getCategoryTypeByCode(categoryTypeCode);
        return categoryType == null ? null : categoryType.getTopLevelCategoryId();
    }

    /**
     * {@link EntityQueryScene#ENTITIES_BY_CATEGORY} 的 Category-via-Ref 分支：只做分流，不重复实现分类/SQL。
     *
     * <p><strong>与直接挂靠的区别</strong>：主体实体不必挂在维度分类下；列表来自
     * 「分类 → 目标实体 → 主体 REF」三步编排（{@link CategoryViaRefQueryService}）。</p>
     *
     * <p><strong>本方法职责</strong>：路径与请求类型校验、分类范围归一（含未选节点≡整树）、
     * 业务域/划分成员收窄、复用 {@link #queryEntitiesByOrderedCandidateIds} 分页装 VO。</p>
     *
     * <p><strong>不写库</strong>：不创建 {@code dynamic_entity_category_relation}，不改 REF 源数据。</p>
     */
    private EntitySceneQueryRespVO queryEntitiesByCategoryViaRef(
            String categoryViaRefPathCode,
            String categoryTypeCode,
            List<Long> categoryIds,
            String subjectEntityTypeCode,
            String domain,
            String scopeRegistryCode,
            String keyword,
            List<FieldFilterReqVO> filters,
            EntityQueryResultShape shape,
            EntityQueryResultDetail detail,
            Integer pageNo,
            Integer pageSize,
            String orderByColumn,
            boolean orderAsc) {
        categoryViaRefQueryService.assertSubjectTypeMatches(categoryViaRefPathCode, subjectEntityTypeCode);
        categoryViaRefQueryService.assertDimensionCategoryTypeMatches(categoryViaRefPathCode, categoryTypeCode);
        CategoryViaRefQueryPath path = categoryViaRefQueryService.requirePath(categoryViaRefPathCode);
        List<Long> normalizedCategoryIds = normalizeCategoryIdsOrUseRootCategory(
                categoryIds, path.dimensionCategoryTypeCode());
        List<Long> subjectEntityIds = categoryViaRefQueryService.listSubjectEntityIds(
                categoryViaRefPathCode, normalizedCategoryIds);
        subjectEntityIds = dataMgmtEntityQueryRepository.retainOrderedIdsByDomainAndScope(
                subjectEntityIds, subjectEntityTypeCode, domain, scopeRegistryCode);
        subjectEntityIds = applyFieldOrderToCandidateIds(
                subjectEntityIds, subjectEntityTypeCode, orderByColumn, orderAsc);
        PageResult<EntityRespVO> viaRefResult = queryEntitiesByOrderedCandidateIds(
                subjectEntityIds, subjectEntityTypeCode, keyword, filters,
                shape == EntityQueryResultShape.PAGE ? pageNo : null,
                shape == EntityQueryResultShape.PAGE ? pageSize : null);
        if (shape == EntityQueryResultShape.PAGE) {
            return EntitySceneQueryRespVO.page(applyResultDetail(viaRefResult, detail), detail.getCode());
        }
        List<EntityRespVO> viaRefEntities = viaRefResult.getList();
        if (shape == EntityQueryResultShape.TREE) {
            return EntitySceneQueryRespVO.tree(
                    applyResultDetail(EntityTreeBuilder.buildTree(viaRefEntities,
                            EntityTreeBuilder.SortMode.LOCAL_SIBLING_SORT), detail),
                    detail.getCode());
        }
        return EntitySceneQueryRespVO.list(applyResultDetail(viaRefEntities, detail), detail.getCode());
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    /**
     * 规范化分类ID列表；若未选择分类则使用分类类型的根分类ID。
     * （针对用户在界面上没选分类时的处理方法）
     * <p>处理规则：</p>
     * <ol>
     *   <li>对传入 categoryIds 做清洗：移除 null、按输入顺序去重；</li>
     *   <li>若清洗后非空，直接返回该列表；</li>
     *   <li>若清洗后为空，尝试通过 categoryTypeCode 解析根分类ID并返回单元素列表；</li>
     *   <li>若无法解析根分类（例如 categoryTypeCode 为空或配置缺失），返回空列表。</li>
     * </ol>
     *
     * <p>该方法只负责“分类范围确定”，不做业务类型校验、不访问实体关系数据。</p>
     */
    private List<Long> normalizeCategoryIdsOrUseRootCategory(List<Long> categoryIds, String categoryTypeCode) {
        List<Long> normalized = categoryIds == null ? new ArrayList<>() : categoryIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));
        if (!normalized.isEmpty()) {
            return normalized;
        }
        Long rootCategoryId = resolveRootCategoryId(categoryTypeCode);
        return rootCategoryId == null ? new ArrayList<>() : List.of(rootCategoryId);
    }

    /**
     * 按分类范围（含子分类）获取完整有序实体ID列表。
     */
    private List<Long> listOrderedEntityIdsByCategoriesInBusiness(List<Long> categoryIds, String categoryTypeCode, String entityTypeCode) {
        return categoryIds.size() == 1
                ? entityCategoryRelationService.listEntityIdsByCategoryIdWithDescendants(categoryIds.get(0), categoryTypeCode, entityTypeCode)
                : entityCategoryRelationService.listEntityIdsByCategoryIdsWithDescendants(categoryIds, entityTypeCode);
    }

    /** ----------------按有序 entityId 分页结果回查实体详情并保序返回。-----------
     */
    private PageResult<EntityRespVO> fetchEntityPageByOrderedIds(PageResult<Long> pagedEntityIds, String entityTypeCode) {
        return fetchEntityPageByOrderedIds(pagedEntityIds, entityTypeCode, EntityQueryResultDetail.FULL);
    }

    private PageResult<EntityRespVO> fetchEntityPageByOrderedIds(PageResult<Long> pagedEntityIds,
                                                                 String entityTypeCode,
                                                                 EntityQueryResultDetail detail) {
        if (pagedEntityIds == null || pagedEntityIds.getList() == null || pagedEntityIds.getList().isEmpty()) {
            return new PageResult<>(new ArrayList<>(), pagedEntityIds == null ? 0L : pagedEntityIds.getTotal());
        }
        List<EntityRespVO> result = convertOrderedEntityIdsToRespList(
                pagedEntityIds.getList(), entityTypeCode, detail);
        return new PageResult<>(result, pagedEntityIds.getTotal());
    }

    /**
     * 将有序实体ID列表转换为VO列表，并保持输入顺序。
     * 本页行一次加载核心列 + 基础字段列，再组装 VO（不再按行 merge）。
     */
    /**
     * 列表装 VO：本页一次带基础字段；不查型号扩展字段配置（列表不展示扩展字段）。
     * <p>无启用 MultiRef 基础字段时不取 {@code custom_fields}（单选 REF 已在专用列）；
     * 有 MultiRef 时仍取 JSON，供 LIGHT 提升进 baseFields。</p>
     * <p>{@code detail=LIGHT} 时直接装轻量 VO，避免 MapStruct 全量转换后再剥字段。</p>
     */
    private List<EntityRespVO> convertOrderedEntityIdsToRespList(List<Long> orderedEntityIds, String entityTypeCode) {
        return convertOrderedEntityIdsToRespList(orderedEntityIds, entityTypeCode, EntityQueryResultDetail.FULL);
    }

    private List<EntityRespVO> convertOrderedEntityIdsToRespList(List<Long> orderedEntityIds,
                                                                  String entityTypeCode,
                                                                  EntityQueryResultDetail detail) {
        if (orderedEntityIds == null || orderedEntityIds.isEmpty()) {
            return new ArrayList<>();
        }
        long t0 = System.nanoTime();
        boolean includeCustom = entityDedicatedColumnService.hasEnabledMultiRefBaseField(entityTypeCode);
        List<EntityDO> ordered = entityCoreService.listByIdsWithDedicatedBaseFields(
                orderedEntityIds, entityTypeCode, includeCustom);
        long t1 = System.nanoTime();
        if (ordered == null || ordered.isEmpty()) {
            return new ArrayList<>();
        }
        boolean light = detail == EntityQueryResultDetail.LIGHT;
        List<EntityRespVO> vos = light
                ? EntityDoVoHelper.toLightRespVOList(ordered, entityDedicatedColumnService)
                : EntityDoVoHelper.toRespVOListSkipCustomPresent(ordered, entityDedicatedColumnService);
        long t2 = System.nanoTime();
        if (log.isInfoEnabled() && orderedEntityIds.size() >= 20) {
            log.info("[query-by-scene timing] stage=convertOrderedEntityIds loadDedicated={}ms toVO={}ms "
                            + "ids={} includeCustomFields={} detail={}",
                    (t1 - t0) / 1_000_000L, (t2 - t1) / 1_000_000L, orderedEntityIds.size(), includeCustom,
                    detail != null ? detail.getCode() : "FULL");
        }
        return vos;
    }

    /**
     * 将配置器搜索范围解析为可下推列 + 扩展字段；空范围默认仅 name。
     */
    private KeywordSearchSpec resolveKeywordSearchSpec(String entityTypeCode, List<String> searchFieldCodes) {
        if (searchFieldCodes == null || searchFieldCodes.isEmpty()) {
            return KeywordSearchSpec.nameOnly();
        }
        List<String> likeColumns = new ArrayList<>();
        List<String> extensionFieldCodes = new ArrayList<>();
        boolean matchId = false;
        for (String raw : searchFieldCodes) {
            if (raw == null || raw.isBlank()) {
                continue;
            }
            String code = raw.trim();
            String lower = code.toLowerCase(Locale.ROOT);
            if ("id".equals(lower)) {
                matchId = true;
                continue;
            }
            if ("name".equals(lower) || "code".equals(lower) || "status".equals(lower)) {
                if (!likeColumns.contains(lower)) {
                    likeColumns.add(lower);
                }
                continue;
            }
            String physical = null;
            if (StringUtils.hasText(entityTypeCode)) {
                physical = entityDedicatedColumnService.resolvePhysicalColumn(entityTypeCode.trim(), code);
            }
            if (StringUtils.hasText(physical)) {
                String col = physical.trim().toLowerCase(Locale.ROOT);
                if (!likeColumns.contains(col)) {
                    likeColumns.add(col);
                }
            } else if (!extensionFieldCodes.contains(code)) {
                extensionFieldCodes.add(code);
            }
        }
        if (!matchId && likeColumns.isEmpty() && extensionFieldCodes.isEmpty()) {
            return KeywordSearchSpec.nameOnly();
        }
        return new KeywordSearchSpec(likeColumns, matchId, extensionFieldCodes);
    }

    private List<String> toSearchFieldCodes(KeywordSearchSpec keywordSearch) {
        if (keywordSearch == null) {
            return null;
        }
        List<String> codes = new ArrayList<>();
        if (keywordSearch.matchId()) {
            codes.add("id");
        }
        if (keywordSearch.likeColumns() != null) {
            codes.addAll(keywordSearch.likeColumns());
        }
        if (keywordSearch.extensionFieldCodes() != null) {
            codes.addAll(keywordSearch.extensionFieldCodes());
        }
        return codes.isEmpty() ? null : codes;
    }

    /**
     * 可下推库内 ORDER BY 的列名：核心列原样；可排序基础字段 → 物理列名；否则 null（走内存/慢路径）。
     */
    private String resolveDbOrderColumn(String entityTypeCode, String orderByColumn) {
        if (!StringUtils.hasText(orderByColumn)) {
            return null;
        }
        String column = orderByColumn.trim();
        if ("sort".equals(column)) {
            return null;
        }
        if (CORE_ORDER_BY_COLUMNS.contains(column)) {
            return column;
        }
        if (!StringUtils.hasText(entityTypeCode)) {
            return null;
        }
        return entityDedicatedColumnService.resolveSortablePhysicalColumn(entityTypeCode.trim(), column);
    }

    /**
     * 全部 fieldFilters 可下推到实体表物理列（EQ/IN/NOT_IN）时返回筛选列表；无筛返回空列表；
     * 任一不可下推返回 null（整单走慢路径，禁止半下推）。
     */
    private List<PhysicalColumnFilter> resolvePushablePhysicalFilters(
            String entityTypeCode, List<FieldFilterReqVO> filters) {
        if (filters == null || filters.isEmpty()) {
            return List.of();
        }
        if (!StringUtils.hasText(entityTypeCode)) {
            return null;
        }
        List<PhysicalColumnFilter> out = new ArrayList<>();
        for (FieldFilterReqVO filter : filters) {
            if (filter == null || !StringUtils.hasText(filter.getFieldCode())
                    || !StringUtils.hasText(filter.getOp())) {
                return null;
            }
            if (Boolean.TRUE.equals(filter.getRelationField())) {
                return null;
            }
            String op = filter.getOp().trim().toUpperCase(Locale.ROOT);
            if (!"EQ".equals(op) && !"IN".equals(op) && !"NOT_IN".equals(op)) {
                return null;
            }
            String fieldCode = filter.getFieldCode().trim();
            String column;
            if (CORE_ORDER_BY_COLUMNS.contains(fieldCode)) {
                column = fieldCode;
            } else {
                column = entityDedicatedColumnService.resolvePhysicalColumn(entityTypeCode.trim(), fieldCode);
            }
            if (!StringUtils.hasText(column)) {
                return null;
            }
            Object value = filter.getValue();
            if ("IN".equals(op) || "NOT_IN".equals(op)) {
                List<Object> values = normalizeFilterInValues(value);
                // IN 空集 → 无命中；NOT_IN 空集 → 不过滤（仍下推，由仓储跳过条件）
                if ("IN".equals(op) && values.isEmpty()) {
                    out.add(new PhysicalColumnFilter(column, op, List.of()));
                    continue;
                }
                out.add(new PhysicalColumnFilter(column, op, values));
            } else {
                if (value == null) {
                    return null;
                }
                out.add(new PhysicalColumnFilter(column, op, value));
            }
        }
        return out;
    }

    private static List<Object> normalizeFilterInValues(Object value) {
        if (value == null) {
            return List.of();
        }
        if (value instanceof Collection<?> collection) {
            return collection.stream().filter(Objects::nonNull).map(v -> (Object) v).toList();
        }
        if (value.getClass().isArray()) {
            int len = java.lang.reflect.Array.getLength(value);
            List<Object> out = new ArrayList<>(len);
            for (int i = 0; i < len; i++) {
                Object item = java.lang.reflect.Array.get(value, i);
                if (item != null) {
                    out.add(item);
                }
            }
            return out;
        }
        return List.of(value);
    }

    /**
     * 可下推 EVA 索引排序时返回索引值列名；字段未标记可排序或不在索引策略内则 null。
     * <p><b>非列表入口预留</b>：{@link #validateOrderByColumn} 已禁止列表按扩展字段排序，
     * query-by-scene 列表路径不会走到此方法；实现与 {@code EvaFieldOrderEntityQueryRepository} 保留供将来其它场景复用。</p>
     */
    private String resolveEvaIndexValueColumn(String entityTypeCode, String orderByColumn) {
        if (!StringUtils.hasText(orderByColumn) || !StringUtils.hasText(entityTypeCode)) {
            return null;
        }
        String column = orderByColumn.trim();
        if ("sort".equals(column) || CORE_ORDER_BY_COLUMNS.contains(column)) {
            return null;
        }
        // 基础字段走物理列，不走 EVA
        if (entityDedicatedColumnService.resolvePhysicalColumn(entityTypeCode.trim(), column) != null) {
            return null;
        }
        FieldDO field = fieldMapper.selectByCode(column);
        if (field == null || field.getId() == null) {
            return null;
        }
        List<ModelFieldAssignmentDO> assignments = modelFieldAssignmentMapper.selectByFieldId(field.getId());
        boolean sortable = false;
        if (assignments != null) {
            for (ModelFieldAssignmentDO assignment : assignments) {
                if (assignment == null || !Boolean.TRUE.equals(assignment.getIsSortable())) {
                    continue;
                }
                if (assignment.getModelId() == null) {
                    sortable = true;
                    break;
                }
                ModelDO model = modelMapper.selectById(assignment.getModelId());
                if (model != null && entityTypeCode.trim().equals(model.getEntityTypeCode())) {
                    sortable = true;
                    break;
                }
            }
        }
        if (!sortable) {
            return null;
        }
        String type = field.getType() == null ? "" : field.getType().trim().toUpperCase(Locale.ROOT);
        return switch (type) {
            case "NUMBER", "INTEGER", "DECIMAL", "LONG", "DOUBLE", "FLOAT" -> "value_number";
            case "DATE" -> "value_date";
            case "DATETIME", "TIMESTAMP" -> "value_datetime";
            case "BOOLEAN", "BOOL" -> "value_boolean";
            default -> "value_string";
        };
    }

    private boolean matchNonRelationFilter(EntityRespVO entity, String fieldCode, String fieldType, String op, Object expectedValue) {
        Object actualValue = resolveEntityFieldValue(entity, fieldCode);
        if (actualValue == null) {
            return false;
        }

        // 文本字段不走结构化筛选，统一走 keyword
        if (isTextType(fieldType)) {
            return false;
        }

        return switch (op) {
            case "EQ" -> compareEquals(actualValue, expectedValue, fieldType);
            case "IN" -> compareIn(actualValue, expectedValue, fieldType);
            case "GTE" -> compareRange(actualValue, expectedValue, fieldType, true, true);
            case "LTE" -> compareRange(actualValue, expectedValue, fieldType, false, true);
            case "GT" -> compareRange(actualValue, expectedValue, fieldType, true, false);
            case "LT" -> compareRange(actualValue, expectedValue, fieldType, false, false);
            case "BETWEEN" -> compareBetween(actualValue, expectedValue, fieldType);
            default -> false;
        };
    }

    /**
     * 解析实体中某个字段编码对应的实际值。
     *
     * <p>解析顺序：</p>
     * <ol>
     *   <li>核心字段（id/modelId/status/parentId/entityTypeCode/name）</li>
     *   <li>baseFields JSON</li>
     *   <li>customFields JSON</li>
     * </ol>
     *
     * <p>说明：这是“取字段值”而不是“查实体”。</p>
     */
    private Object resolveEntityFieldValue(EntityRespVO entity, String fieldCode) {
        if (entity == null || fieldCode == null || fieldCode.isBlank()) {
            return null;
        }
        Object fromCore = resolveCoreFieldValue(entity, fieldCode);
        if (fromCore != null) {
            return fromCore;
        }

        Object fromBase = resolveJsonFieldValue(entity.getBaseFields(), fieldCode);
        if (fromBase != null) {
            return fromBase;
        }
        return resolveJsonFieldValue(entity.getCustomFields(), fieldCode);
    }

    /**
     * 从实体核心属性中读取字段值（只处理固定核心字段，不解析 JSON）。
     */
    private Object resolveCoreFieldValue(EntityRespVO entity, String fieldCode) {
        return switch (fieldCode) {
            case "id" -> entity.getId();
            case "model_id", "modelId" -> entity.getModelId();
            case "status" -> entity.getStatus();
            case "parent_id", "parentId" -> entity.getParentId();
            case "entity_type_code", "entityTypeCode" -> entity.getEntityTypeCode();
            case "name" -> entity.getName();
            default -> null;
        };
    }

    /**
     * 从字段 Map 中读取字段值。
     */
    private Object resolveJsonFieldValue(Map<String, Object> fields, String fieldCode) {
        if (fields == null || fields.isEmpty() || fieldCode == null || fieldCode.isBlank()) {
            return null;
        }
        return fields.get(fieldCode);
    }

    private boolean isTextType(String fieldType) {
        if (fieldType == null) {
            return false;
        }
        String normalized = fieldType.trim().toUpperCase();
        return "TEXT".equals(normalized) || "LONG_TEXT".equals(normalized) || "JSON".equals(normalized);
    }

    private boolean compareEquals(Object actualValue, Object expectedValue, String fieldType) {
        if (expectedValue == null) {
            return false;
        }
        if (isNumberType(fieldType)) {
            BigDecimal actual = toBigDecimal(actualValue);
            BigDecimal expected = toBigDecimal(expectedValue);
            return actual != null && expected != null && actual.compareTo(expected) == 0;
        }
        if (isDateType(fieldType)) {
            LocalDateTime actual = toDateTime(actualValue);
            LocalDateTime expected = toDateTime(expectedValue);
            return actual != null && expected != null && actual.isEqual(expected);
        }
        return String.valueOf(actualValue).equals(String.valueOf(expectedValue));
    }

    private boolean compareIn(Object actualValue, Object expectedValue, String fieldType) {
        if (!(expectedValue instanceof Collection<?> collection) || collection.isEmpty()) {
            return false;
        }
        for (Object value : collection) {
            if (compareEquals(actualValue, value, fieldType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 比较区间边界（大于/大于等于/小于/小于等于）。
     *
     * <p>按字段类型执行：</p>
     * <ul>
     *   <li>数字类型：转 BigDecimal 比较</li>
     *   <li>日期类型：转 LocalDateTime 比较</li>
     * </ul>
     *
     * @param greaterSide true=比较下限（actual >= expected）; false=比较上限（actual <= expected）
     * @param includeBoundary true=包含边界；false=严格比较
     */
    private boolean compareRange(Object actualValue, Object expectedValue, String fieldType, boolean greaterSide, boolean includeBoundary) {
        if (isNumberType(fieldType)) {
            BigDecimal actual = toBigDecimal(actualValue);
            BigDecimal expected = toBigDecimal(expectedValue);
            if (actual == null || expected == null) {
                return false;
            }
            int cmp = actual.compareTo(expected);
            if (greaterSide) {
                return includeBoundary ? cmp >= 0 : cmp > 0;
            }
            return includeBoundary ? cmp <= 0 : cmp < 0;
        }
        if (isDateType(fieldType)) {
            LocalDateTime actual = toDateTime(actualValue);
            LocalDateTime expected = toDateTime(expectedValue);
            if (actual == null || expected == null) {
                return false;
            }
            if (greaterSide) {
                return includeBoundary ? !actual.isBefore(expected) : actual.isAfter(expected);
            }
            return includeBoundary ? !actual.isAfter(expected) : actual.isBefore(expected);
        }
        return false;
    }

    /**
     * BETWEEN 比较。
     *
     * <p>expectedValue 必须是长度为 2 的列表：[start, end]，
     * 并按“闭区间”匹配，即 start <= actual <= end。</p>
     */
    private boolean compareBetween(Object actualValue, Object expectedValue, String fieldType) {
        if (!(expectedValue instanceof List<?> list) || list.size() != 2) {
            return false;
        }
        Object start = list.get(0);
        Object end = list.get(1);
        return compareRange(actualValue, start, fieldType, true, true)
                && compareRange(actualValue, end, fieldType, false, true);
    }

    /**
     * 判断字段类型是否属于数值类型。
     */
    private boolean isNumberType(String fieldType) {
        if (fieldType == null) {
            return false;
        }
        String normalized = fieldType.trim().toUpperCase();
        return "NUMBER".equals(normalized) || "INTEGER".equals(normalized) || "DECIMAL".equals(normalized);
    }

    /**
     * 判断字段类型是否属于时间类型。
     */
    private boolean isDateType(String fieldType) {
        if (fieldType == null) {
            return false;
        }
        String normalized = fieldType.trim().toUpperCase();
        return "DATE".equals(normalized) || "DATETIME".equals(normalized) || "TIMESTAMP".equals(normalized);
    }

    /**
     * 将值转换为 BigDecimal（用于数值类比较）。
     *
     * <p>支持 Number/String；转换失败返回 null。</p>
     */
    private BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof BigDecimal bigDecimal) {
            return bigDecimal;
        }
        if (value instanceof Number number) {
            return new BigDecimal(String.valueOf(number));
        }
        if (value instanceof String str) {
            String trimmed = str.trim();
            if (trimmed.isEmpty()) {
                return null;
            }
            try {
                return new BigDecimal(trimmed);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    /**
     * 将值转换为 LocalDateTime（用于时间类比较）。
     *
     * <p>支持 LocalDateTime/String；转换失败返回 null。</p>
     */
    private LocalDateTime toDateTime(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof LocalDateTime localDateTime) {
            return localDateTime;
        }
        if (value instanceof String str) {
            String trimmed = str.trim();
            if (trimmed.isEmpty()) {
                return null;
            }
            try {
                return LocalDateTime.parse(trimmed);
            } catch (DateTimeParseException ignored) {
                return null;
            }
        }
        return null;
    }

    /**
     * 判断关联字段筛选支持的操作符。
     *
     * <p>当前仅支持：</p>
     * <ul>
     *   <li>EQ：单值等于</li>
     *   <li>IN：多值包含</li>
     * </ul>
     */
    private boolean isRelationFilterSupported(String op) {
        if (op == null) {
            return false;
        }
        String normalized = op.trim().toUpperCase();
        return "IN".equals(normalized) || "EQ".equals(normalized);
    }

    /**
     * 将筛选值统一规范成“实体ID列表”。
     *
     * <p>用途：关联字段筛选时，前端 value 可能是单值或数组，
     * 该方法把输入统一规范为 List<Long>，便于关系表查询。</p>
     *
     * <p>示例：</p>
     * <ul>
     *   <li>123 -> [123]</li>
     *   <li>"123" -> [123]</li>
     *   <li>[123, "456"] -> [123, 456]</li>
     * </ul>
     */
    private List<Long> normalizeEntityIds(Object value) {
        if (value == null) {
            return Collections.emptyList();
        }
        if (value instanceof Collection<?> collection) {
            return collection.stream()
                    .map(this::toEntityIdOrNull)
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();
        }
        Long single = toEntityIdOrNull(value);
        return single == null ? Collections.emptyList() : List.of(single);
    }

    /**
     * 将单个输入值转换为实体ID；无法转换时返回 null。
     *
     * <p>支持 Number/String；不访问数据库，不做实体查询。</p>
     */
    private Long toEntityIdOrNull(Object item) {
        if (item == null) {
            return null;
        }
        if (item instanceof Number number) {
            return number.longValue();
        }
        if (item instanceof String str) {
            String trimmed = str.trim();
            if (trimmed.isEmpty()) {
                return null;
            }
            try {
                return Long.valueOf(trimmed);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }




    /**
     * 3.3 模型维度分页搜索实体列表（完整字段）。
     *
     * @return 分页搜索结果（PageResult<EntityRespVO>）
     */
    @Override
    public PageResult<EntityRespVO> pageSearchEntities(EntityPageReqVO reqVO) {
        // 说明：前端传合法 pageNo/pageSize 时保持原值；仅对 null 或 <=0 参数进行默认化。
        Integer pageNo = normalizePageNo(reqVO.getPageNo());
        Integer pageSize = normalizePageSize(reqVO.getPageSize());

        PageResult<EntityDO> pageResult = entityCoreService.pageEntities(
                reqVO.getEntityTypeCode(),
                reqVO.getModelId(),
                reqVO.getStatus(),
                reqVO.getKeyword(),
                pageNo,
                pageSize);

        List<Long> orderedIds = pageResult.getList().stream()
                .map(EntityDO::getId)
                .filter(Objects::nonNull)
                .toList();
        List<EntityRespVO> list = convertOrderedEntityIdsToRespList(orderedIds, reqVO.getEntityTypeCode());
        return new PageResult<>(list, pageResult.getTotal());
    }





   
    /**
     * 页码为空时，使用默认值 1。
     */
    private Integer normalizePageNo(Integer pageNo) {
        return (pageNo == null || pageNo < 1) ? 1 : pageNo;
    }

    /**
     * 每页条数为空时，使用默认值 20。
     */
    private Integer normalizePageSize(Integer pageSize) {
        return (pageSize == null || pageSize < 1) ? 20 : pageSize;
    }


    /**
     * 移动实体到新的父节点。（更新parentId 和 TreePath ，递归更新子实体）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void moveEntity(Long entityId, String entityTypeCode, Long newParentId) {
        entityCoreService.moveEntity(entityId, entityTypeCode, newParentId);
    }

    // ==================== 批量操作相关方法 ====================
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


    // ==================== 构建工具相关方法 ====================




    /**
     * 构建搜索聚合统计（状态分布、模型分布）。
     *
     * <p>聚合查询为增强信息，失败时仅记录日志，不影响主查询结果返回。</p>
     * <p>当前状态：历史高级搜索能力配套方法，现阶段主链路未强依赖。</p>
     * <p>TODO：待高级搜索能力统一后，决定是否恢复主流程使用或迁移到独立聚合服务。</p>
     */
    private EntitySearchRespVO.AggregationInfo buildAggregation(EntitySearchReqVO reqVO, List<Long> filteredEntityIds) {
        EntitySearchRespVO.AggregationInfo aggregation = new EntitySearchRespVO.AggregationInfo();
        if (reqVO == null || reqVO.getEntityTypeCode() == null || reqVO.getEntityTypeCode().isEmpty()) {
            return aggregation;
        }

        try {
            List<EntityAggregationCountDTO<Integer>> statusCounts = entityRepository.countGroupByStatus(
                    reqVO.getEntityTypeCode(), reqVO.getModelId(), reqVO.getStatus(), reqVO.getKeyword(),
                    filteredEntityIds);
            Map<Integer, Long> statusMap = new HashMap<>();
            for (EntityAggregationCountDTO<Integer> row : statusCounts) {
                if (row != null) statusMap.put(row.getKey(), row.getCnt());
            }
            aggregation.setStatusCount(statusMap);

            List<EntityAggregationCountDTO<Long>> modelCounts = entityRepository.countGroupByModelId(
                    reqVO.getEntityTypeCode(), reqVO.getModelId(), reqVO.getStatus(), reqVO.getKeyword(),
                    filteredEntityIds);
            Map<Long, Long> modelMap = new HashMap<>();
            for (EntityAggregationCountDTO<Long> row : modelCounts) {
                if (row != null) modelMap.put(row.getKey(), row.getCnt());
            }
            aggregation.setModelCount(modelMap);
        } catch (Exception e) {
            log.warn("构建聚合统计信息失败: entityTypeCode={}, error={}", reqVO.getEntityTypeCode(), e.getMessage());
        }
        return aggregation;
    }







    /**
     * 高级搜索入口（内部复用分页搜索并封装搜索响应）。
     */
    @Override
    public EntitySearchRespVO searchAdvanced(EntitySearchReqVO reqVO) {
        EntityPageReqVO pageReqVO = new EntityPageReqVO();
        pageReqVO.setEntityTypeCode(reqVO.getEntityTypeCode());
        pageReqVO.setModelId(reqVO.getModelId());
        pageReqVO.setStatus(reqVO.getStatus());
        pageReqVO.setKeyword(reqVO.getKeyword());
        pageReqVO.setPageNo(reqVO.getPageNo());
        pageReqVO.setPageSize(reqVO.getPageSize());
        PageResult<EntityRespVO> pageResult = pageSearchEntities(pageReqVO);

        EntitySearchRespVO resp = new EntitySearchRespVO();
        resp.setList(pageResult.getList());
        resp.setTotal(pageResult.getTotal());
        resp.setPageNo(reqVO.getPageNo());
        resp.setPageSize(reqVO.getPageSize());
        int totalPages = reqVO.getPageSize() != null && reqVO.getPageSize() > 0
                ? (int) Math.ceil((double) pageResult.getTotal() / reqVO.getPageSize())
                : 0;
        resp.setTotalPages(totalPages);
        resp.setSearchTime(0L);
        return resp;
    }



    /**
     * 实体层级树子树：rootEntityId 为空时返回根节点（供 Tree 首屏懒加载）；否则返回该节点的直接子节点。
     */
    private List<EntityRespVO> buildEntityHierarchySubtree(String entityTypeCode, Long rootEntityId,
            String keyword, List<FieldFilterReqVO> filters, EntityQueryResultDetail detail,
            Integer pageNo, Integer pageSize) {
        List<EntityDO> rawEntities;
        if (rootEntityId == null) {
            int resolvedPageNo = pageNo == null || pageNo < 1 ? 1 : pageNo;
            int resolvedPageSize = resolveTreeRootPageSize(pageSize);
            rawEntities = entityCoreService.pageEntitiesByParentId(
                    entityTypeCode, null, resolvedPageNo, resolvedPageSize).getList();
        } else {
            rawEntities = entityCoreService.listEntitiesByParentId(entityTypeCode, rootEntityId);
        }
        if (rawEntities == null || rawEntities.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> orderedIds = rawEntities.stream()
                .map(EntityDO::getId)
                .filter(Objects::nonNull)
                .toList();
        List<EntityDO> loaded = entityCoreService.listByIdsWithDedicatedBaseFields(orderedIds, entityTypeCode);
        List<EntityRespVO> entities = detail == EntityQueryResultDetail.LIGHT
                ? EntityDoVoHelper.toLightRespVOList(loaded, entityDedicatedColumnService)
                : EntityDoVoHelper.toRespVOList(loaded, customFieldValidationService, entityDedicatedColumnService);
        entities = filterEntityRespList(entities, entityTypeCode, keyword, filters);
        entityRefDisplayEnrichService.enrich(entities);

        Comparator<EntityRespVO> comparator = Comparator
                .comparing((EntityRespVO v) -> v.getSort() == null ? Integer.MAX_VALUE : v.getSort())
                .thenComparing(v -> v.getId() == null ? Long.MAX_VALUE : v.getId());
        entities = entities.stream().sorted(comparator).toList();
        for (EntityRespVO entity : entities) {
            entity.setChildren(null);
        }
        return entities;
    }

    /** Tree 首屏根节点默认分页，避免万级实体一次性拉全量导致超时/500。 */
    private static int resolveTreeRootPageSize(Integer pageSize) {
        if (pageSize == null || pageSize < 1) {
            return 200;
        }
        return Math.min(pageSize, 500);
    }

    private List<EntityRespVO> filterEntityRespList(List<EntityRespVO> entities, String entityTypeCode,
            String keyword, List<FieldFilterReqVO> filters) {
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        boolean hasFilters = filters != null && !filters.isEmpty();
        if (!hasKeyword && !hasFilters) {
            return entities;
        }
        List<Long> orderedIds = entities.stream()
                .map(EntityRespVO::getId)
                .filter(Objects::nonNull)
                .toList();
        List<Long> filteredIds = filterCandidateEntityIds(orderedIds, entityTypeCode, filters, keyword);
        if (filteredIds.isEmpty()) {
            return new ArrayList<>();
        }
        Set<Long> allowed = new HashSet<>(filteredIds);
        return entities.stream()
                .filter(entity -> entity.getId() != null && allowed.contains(entity.getId()))
                .toList();
    }

    private List<EntityRespVO> flattenEntityTree(List<EntityRespVO> treeRoots) {
        if (treeRoots == null || treeRoots.isEmpty()) {
            return new ArrayList<>();
        }
        List<EntityRespVO> flat = new ArrayList<>();
        Deque<EntityRespVO> stack = new ArrayDeque<>(treeRoots);
        while (!stack.isEmpty()) {
            EntityRespVO current = stack.pop();
            flat.add(current);
            if (current.getChildren() != null && !current.getChildren().isEmpty()) {
                for (int i = current.getChildren().size() - 1; i >= 0; i--) {
                    stack.push(current.getChildren().get(i));
                }
            }
        }
        return flat;
    }

    /**
     * 按模型获取实体树（同层按 sort 排序）。
     * 前提是实体有数型结构的情况下使用，如果实体不支持树形结构，使用实体列表的查询接口
     */
    @Override
    public List<EntityRespVO> getEntityTreeByModelId(String entityTypeCode, Long modelId) {
        List<EntityDO> entities = entityCoreService.listTreeEntities(entityTypeCode, modelId);
        List<Long> orderedIds = entities == null ? List.of() : entities.stream()
                .map(EntityDO::getId)
                .filter(Objects::nonNull)
                .toList();
        List<EntityDO> loaded = entityCoreService.listByIdsWithDedicatedBaseFields(orderedIds, entityTypeCode);
        List<EntityRespVO> respVOList = EntityDoVoHelper.toRespVOList(
                loaded, customFieldValidationService, entityDedicatedColumnService);
        // 模型树场景：采用“父节点内局部排序（sort）”策略
        return EntityTreeBuilder.buildTree(respVOList, EntityTreeBuilder.SortMode.LOCAL_SIBLING_SORT);
    }

    /**
     * 获取实体从根到当前节点的路径。
     * 前提是实体有数型结构的情况下使用 （todo:这个待分析是否要优化）
     */

    @Override
    public List<String> getEntityPath(Long entityId, String entityTypeCode) {
        return entityCoreService.getEntityPath(entityId, entityTypeCode);
    }

    // ==================== 结果粒度转换相关方法 ====================
    private PageResult<EntityRespVO> applyResultDetail(PageResult<EntityRespVO> page, EntityQueryResultDetail detail) {
        if (page == null) {
            return new PageResult<>(new ArrayList<>(), 0L);
        }
        return new PageResult<>(applyResultDetail(page.getList(), detail), page.getTotal());
    }

    private List<EntityRespVO> applyResultDetail(List<EntityRespVO> entities, EntityQueryResultDetail detail) {
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }
        long t0 = System.nanoTime();
        // 列表查询增强：REF 契约对象批量补 name（LIGHT / FULL 均需要）
        entityRefDisplayEnrichService.enrich(entities);
        long t1 = System.nanoTime();
        if (detail != EntityQueryResultDetail.LIGHT) {
            if (log.isInfoEnabled() && entities.size() >= 20) {
                log.info("[query-by-scene timing] stage=applyResultDetail enrich={}ms rows={} detail={}",
                        (t1 - t0) / 1_000_000L, entities.size(), detail);
            }
            return entities;
        }
        List<EntityRespVO> light = entities.stream().map(this::toLightRespVO).toList();
        long t2 = System.nanoTime();
        if (log.isInfoEnabled() && entities.size() >= 20) {
            log.info("[query-by-scene timing] stage=applyResultDetail enrich={}ms toLight={}ms rows={} detail=LIGHT",
                    (t1 - t0) / 1_000_000L, (t2 - t1) / 1_000_000L, entities.size());
        }
        return light;
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

    private static String normalizeOrderByColumn(String orderByColumn) {
        if (orderByColumn == null || orderByColumn.isBlank()) {
            return null;
        }
        return orderByColumn.trim();
    }

    /**
     * 列表场景校验 orderByColumn：仅核心列、可排序基础字段（专用表固定列）、或手动 sort。
     * <p>扩展字段不进实体列表，禁止按扩展字段排序（400）。EVA 库内排序实现仍保留，供将来非列表入口复用，
     * 不由本校验放行。</p>
     */
    private void validateOrderByColumn(String entityTypeCode, String orderByColumn) {
        if (!StringUtils.hasText(orderByColumn)) {
            return;
        }
        String column = orderByColumn.trim();
        if ("sort".equals(column)) {
            return;
        }
        if (CORE_ORDER_BY_COLUMNS.contains(column)) {
            return;
        }
        if (!StringUtils.hasText(entityTypeCode)) {
            throw new ServiceException(400, "按字段排序时 entityTypeCode 不能为空");
        }
        String typeCode = entityTypeCode.trim();
        EntityTypeBaseFieldDO baseField = entityTypeBaseFieldMapper
                .selectByEntityTypeCodeAndFieldCode(typeCode, column);
        if (baseField != null) {
            if (Boolean.FALSE.equals(baseField.getIsSortable())) {
                throw new ServiceException(400, "该字段未标记为可排序: " + column);
            }
            return;
        }
        // 非基础字段 = 型号扩展字段（或未知编码）：列表场景一律拒绝
        FieldDO field = fieldMapper.selectByCode(column);
        if (field != null) {
            throw new ServiceException(400,
                    "列表不支持按扩展字段排序（扩展字段不进实体列表）: " + column);
        }
        throw new ServiceException(400, "不支持按该字段排序: " + column);
    }

    /**
     * 在分页前按字段重排候选实体 ID；orderByColumn 为空时原样返回（保留 relation.sort / entity.sort）。
     * <ul>
     *   <li>核心列：实体表 name/code/status/id</li>
     *   <li>基础字段：专用表固定列</li>
     *   <li>扩展字段：优先 EVA 索引表，缺省回退 customFields</li>
     * </ul>
     */
    private List<Long> applyFieldOrderToCandidateIds(List<Long> orderedCandidateEntityIds,
                                                     String entityTypeCode,
                                                     String orderByColumn,
                                                     boolean orderAsc) {
        if (!StringUtils.hasText(orderByColumn)
                || "sort".equals(orderByColumn.trim())
                || orderedCandidateEntityIds == null
                || orderedCandidateEntityIds.isEmpty()
                || !StringUtils.hasText(entityTypeCode)) {
            return orderedCandidateEntityIds;
        }
        String column = orderByColumn.trim();
        String typeCode = entityTypeCode.trim();
        Map<Long, Comparable<?>> sortValues = resolveSortValues(orderedCandidateEntityIds, typeCode, column);
        Comparator<Long> byValue = Comparator.comparing(
                id -> sortValues.get(id),
                Comparator.nullsLast(this::compareSortValues));
        if (!orderAsc) {
            byValue = byValue.reversed();
        }
        Comparator<Long> stable = byValue.thenComparing(id -> id, Comparator.nullsLast(Long::compareTo));
        return orderedCandidateEntityIds.stream()
                .filter(Objects::nonNull)
                .sorted(stable)
                .toList();
    }

    private Map<Long, Comparable<?>> resolveSortValues(List<Long> entityIds, String entityTypeCode, String fieldCode) {
        if (CORE_ORDER_BY_COLUMNS.contains(fieldCode)) {
            return resolveCoreColumnSortValues(entityIds, entityTypeCode, fieldCode);
        }
        EntityTypeBaseFieldDO baseField = entityTypeBaseFieldMapper
                .selectByEntityTypeCodeAndFieldCode(entityTypeCode, fieldCode);
        if (baseField != null) {
            Map<Long, Object> physical = entityDedicatedColumnService
                    .loadPhysicalFieldValues(entityTypeCode, fieldCode, entityIds);
            Map<Long, Comparable<?>> out = new HashMap<>(physical.size() * 2);
            for (Map.Entry<Long, Object> e : physical.entrySet()) {
                out.put(e.getKey(), toComparableSortValue(e.getValue()));
            }
            return out;
        }
        return resolveEvaOrCustomFieldSortValues(entityIds, entityTypeCode, fieldCode);
    }

    private Map<Long, Comparable<?>> resolveCoreColumnSortValues(List<Long> entityIds,
                                                                 String entityTypeCode,
                                                                 String fieldCode) {
        List<EntityDO> rows = entityRepository.findByIds(entityIds, entityTypeCode);
        Map<Long, Comparable<?>> out = new HashMap<>();
        if (rows == null) {
            return out;
        }
        for (EntityDO row : rows) {
            if (row == null || row.getId() == null) {
                continue;
            }
            Comparable<?> value = switch (fieldCode) {
                case "name" -> row.getName();
                case "code" -> row.getCode();
                case "status" -> row.getStatus();
                case "id" -> row.getId();
                default -> null;
            };
            out.put(row.getId(), value);
        }
        return out;
    }

    private Map<Long, Comparable<?>> resolveEvaOrCustomFieldSortValues(List<Long> entityIds,
                                                                       String entityTypeCode,
                                                                       String fieldCode) {
        Map<Long, Comparable<?>> out = new HashMap<>();
        List<EntityFieldIndexDO> indexRows =
                entityFieldIndexMapper.selectByFieldCodeAndEntityIds(fieldCode, entityIds);
        if (indexRows != null) {
            for (EntityFieldIndexDO index : indexRows) {
                if (index == null || index.getEntityId() == null) {
                    continue;
                }
                Comparable<?> value = firstNonNullComparable(
                        index.getValueNumber(),
                        index.getValueDatetime(),
                        index.getValueDate(),
                        index.getValueBoolean(),
                        index.getValueString());
                if (value != null) {
                    out.put(index.getEntityId(), value);
                }
            }
        }
        // 索引未覆盖的实体：回退 customFields
        List<Long> missing = entityIds.stream()
                .filter(id -> id != null && !out.containsKey(id))
                .toList();
        if (missing.isEmpty()) {
            return out;
        }
        List<EntityDO> rows = entityRepository.findByIds(missing, entityTypeCode);
        if (rows == null) {
            return out;
        }
        for (EntityDO row : rows) {
            if (row == null || row.getId() == null) {
                continue;
            }
            Map<String, Object> custom = row.getCustomFields();
            if (custom == null || !custom.containsKey(fieldCode)) {
                continue;
            }
            out.put(row.getId(), toComparableSortValue(custom.get(fieldCode)));
        }
        return out;
    }

    @SafeVarargs
    private final Comparable<?> firstNonNullComparable(Comparable<?>... values) {
        if (values == null) {
            return null;
        }
        for (Comparable<?> value : values) {
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    private Comparable<?> toComparableSortValue(Object raw) {
        if (raw == null) {
            return null;
        }
        if (raw instanceof Comparable<?> comparable && !(raw instanceof Map)) {
            return comparable;
        }
        if (raw instanceof Map<?, ?> map) {
            Object id = map.get("id");
            if (id == null) {
                id = map.get("entityId");
            }
            if (id instanceof Number number) {
                return number.longValue();
            }
            if (id != null) {
                try {
                    return Long.parseLong(String.valueOf(id).trim());
                } catch (NumberFormatException ignored) {
                    return String.valueOf(id);
                }
            }
        }
        if (raw instanceof Collection<?> collection) {
            return collection.stream().map(String::valueOf).sorted().collect(Collectors.joining(","));
        }
        return String.valueOf(raw);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private int compareSortValues(Comparable<?> left, Comparable<?> right) {
        if (left == null && right == null) {
            return 0;
        }
        if (left == null) {
            return -1;
        }
        if (right == null) {
            return 1;
        }
        if (left.getClass().isInstance(right) || right.getClass().isInstance(left)) {
            return ((Comparable) left).compareTo(right);
        }
        return String.CASE_INSENSITIVE_ORDER.compare(String.valueOf(left), String.valueOf(right));
    }

    /**
     * 轻量结果仅保留下拉/选择常用字段，避免返回完整明细。
     * <p>REF 若只写在 customFields（固定列未回填），仍提升进 baseFields，否则列表列为空。</p>
     */
    private EntityRespVO toLightRespVO(EntityRespVO source) {
        if (source == null) {
            return null;
        }
        EntityRespVO light = new EntityRespVO();
        light.setId(source.getId());
        light.setSort(source.getSort());
        Map<String, Object> base;
        if (source.getBaseFields() != null) {
            base = new LinkedHashMap<>(source.getBaseFields());
        } else {
            base = new LinkedHashMap<>();
            putIfNotNull(base, "entityTypeCode", source.getEntityTypeCode());
            putIfNotNull(base, "modelId", source.getModelId());
            putIfNotNull(base, "name", source.getName());
            putIfNotNull(base, "status", source.getStatus());
            putIfNotNull(base, "parentId", source.getParentId());
        }
        promoteRefFieldsIntoBase(source.getCustomFields(), base);
        light.setBaseFields(base);
        light.setChildren(applyResultDetail(source.getChildren(), EntityQueryResultDetail.LIGHT));
        return light;
    }

    /**
     * 把 customFields 中的 REF / MultiRef 契约提升到 baseFields（不覆盖已有非空值）。
     */
    private static void promoteRefFieldsIntoBase(Map<String, Object> customFields, Map<String, Object> baseFields) {
        if (customFields == null || customFields.isEmpty() || baseFields == null) {
            return;
        }
        for (Map.Entry<String, Object> entry : customFields.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (key == null || key.isBlank() || value == null) {
                continue;
            }
            Object existing = baseFields.get(key);
            if (existing != null && !(existing instanceof String s && s.isBlank())) {
                continue;
            }
            if (looksLikeRefApiValue(value)) {
                baseFields.put(key, value);
            }
        }
    }

    private static boolean looksLikeRefApiValue(Object value) {
        if (value instanceof List<?> list) {
            if (list.isEmpty()) {
                // 空 MultiRef：也提升，便于列表与筛选识别「已配置该列」
                return true;
            }
            return list.stream().anyMatch(EntityServiceImpl::looksLikeRefApiValue);
        }
        if (!(value instanceof Map<?, ?> map)) {
            return false;
        }
        Object id = map.get("id");
        if (id == null) {
            id = map.get("entityId");
        }
        Object type = map.get("entityTypeCode");
        if (type == null) {
            type = map.get("bizCode");
        }
        return id != null && type != null && StringUtils.hasText(String.valueOf(type));
    }

    private static void putIfNotNull(Map<String, Object> target, String key, Object value) {
        if (value != null) {
            target.put(key, value);
        }
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
        if (!StringUtils.hasText(entityTypeCode)) {
            throw new ServiceException(400, "entityTypeCode 不能为空");
        }
        String storage = entityTypeCode.trim();
        boolean withDescendants = includeDescendants == null || includeDescendants;
        List<List<Long>> expandedGroups = expandCategoryIdGroups(
                categoryIds, categoryIdGroups, categoryTypeCode, withDescendants);
        if (expandedGroups.isEmpty()) {
            return List.of();
        }
        List<Long> orderedEntityIds;
        if (expandedGroups.size() == 1) {
            // 与实体列分类快路径同一套库内 EXISTS，避免关联 Mapper 与列表口径漂移
            orderedEntityIds = categoryScopedEntityQueryRepository.listEntityIdsByCategoryScope(
                    expandedGroups.get(0), storage, null, domain, null);
        } else {
            orderedEntityIds = dataMgmtEntityQueryRepository.listOrderedEntityIdsByIntersectingCategoryGroups(
                    expandedGroups, storage, null, domain, null);
        }
        if (orderedEntityIds == null || orderedEntityIds.isEmpty()) {
            return List.of();
        }
        return entityRepository.listDistinctModelIdsPreservingEntityOrder(orderedEntityIds, storage);
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
