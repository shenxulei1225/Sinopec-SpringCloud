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
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityCategoryRelationDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntityCategoryRelationMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntityRelationMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytypescope.EntityTypeScopeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.field.FieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelFieldAssignmentMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.DataMgmtEntityQueryRepository;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.EntityRepository;
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
import cn.cheers.x.module.dynamicbusiness.service.entity.sync.EntitySyncService;
import cn.cheers.x.module.dynamicbusiness.service.model.ModelService;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryEntityLinkService;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryTypeService;
import cn.cheers.x.module.dynamicbusiness.util.DataMgmtCategoryReservedNodes;
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
    private EntityRelationMapper entityRelationMapper;

    @Resource
    private EntityRepository entityRepository;

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
    @Lazy
    private ModelService modelService;

    @Resource
    @Lazy
    private EntityRelationService entityRelationService;

    @Resource
    private EntityRelationSyncService entityRelationSyncService;

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
    private CategoryViaRefQueryService categoryViaRefQueryService;

    @Resource
    private ObjectProvider<EntityServiceImpl> selfProvider;

    /**
     * 创建实体（本体落库 + 关系同步 + 缓存失效 + 事件发布）。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(EntityCreateReqVO reqVO) {
        // 1) 本体准备与落库：仅处理实体表数据（校验/加密/DO转换由 helper 完成）
        EntityDO data = entityBusinessHelper.prepareCreateEntity(reqVO);
        Long entityId = entityCoreService.create(data);

        // 2) 关系同步：把 customFields 中的 ref/multi-ref 同步到关系表，保证关联查询可用
        ModelDO model = modelMapper.selectById(EntityFieldMapsSupport.getRequiredModelId(reqVO.getBaseFields()));
        Map<String, Object> customFieldsMap = entityBusinessHelper.emptyIfNull(reqVO.getCustomFields());
        entityRelationSyncService.syncRelationsOnCreate(data, model, customFieldsMap);

        // 3) 缓存失效：写后清理树/列表缓存，避免读到旧数据
        entityCacheEvictionService.evictEntityCaches(
                EntityFieldMapsSupport.getRequiredModelId(reqVO.getBaseFields()),
                EntityFieldMapsSupport.getRequiredEntityTypeCode(reqVO.getBaseFields()));

        // 4) 事件发布：通知预计算/同步链路，作为跨模块副作用入口
        entityLifecycleEventPublisher.publishEntityCreatedEvent(
                EntityFieldMapsSupport.getRequiredModelId(reqVO.getBaseFields()),
                entityId,
                EntityFieldMapsSupport.getRequiredEntityTypeCode(reqVO.getBaseFields()),
                data);

        return entityId;
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

        // 2) 本体更新：仅更新实体表
        EntityDO data = entityBusinessHelper.prepareUpdateEntity(reqVO, oldEntity);
        entityCoreService.update(data);

        Long modelId = data.getModelId() != null ? data.getModelId() : oldEntity.getModelId();

        // 3) 关系差异同步：根据新旧 customFields 计算并更新关系表
        ModelDO model = modelMapper.selectById(modelId);
        Map<String, Object> customFieldsMap = entityBusinessHelper.emptyIfNull(reqVO.getCustomFields());
        Map<String, Object> oldCustomFieldsMap = entityBusinessHelper.emptyIfNull(oldEntity.getCustomFields());
        entityRelationSyncService.syncRelationsOnUpdate(data, model, customFieldsMap, oldCustomFieldsMap);

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

        // 2) 先清理关系，再删本体，避免关系悬挂
        cleanupAssociationsOnEntityDelete(reqVO.getId(), reqVO.getEntityTypeCode());
        entityCoreService.delete(reqVO.getId(), reqVO.getEntityTypeCode());

        // 3) 写后副作用：缓存失效 + 删除事件
        entityCacheEvictionService.evictEntityCaches(existingEntity.getModelId(), reqVO.getEntityTypeCode());
        entityLifecycleEventPublisher.publishEntityDeletedEvent(existingEntity.getModelId(), reqVO.getId(), reqVO.getEntityTypeCode());
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
        cleanupAssociationsOnEntityDelete(reqVO.getId(), reqVO.getEntityTypeCode());
        entityCoreService.delete(reqVO.getId(), reqVO.getEntityTypeCode());
        categoryEntityLinkService.unlinkCategoryEntity(boundLink.getCategoryId());
        categoryService.deleteCategoryNodeAfterEntityRemoved(
                boundLink.getCategoryId(), category.getCategoryTypeCode());

        entityCacheEvictionService.evictEntityCaches(existingEntity.getModelId(), reqVO.getEntityTypeCode());
        entityLifecycleEventPublisher.publishEntityDeletedEvent(
                existingEntity.getModelId(), reqVO.getId(), reqVO.getEntityTypeCode());
    }

    /**
     * 实体删除前级联清理：实体–实体关系、分类–实体关联、节点绑实体、划分成员。
     * 关联清理带 storage 类型，避免跨表同 id 误删。
     */
    private void cleanupAssociationsOnEntityDelete(Long entityId, String entityTypeCode) {
        entityRelationSyncService.syncRelationsOnDelete(entityId, entityTypeCode);
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
        String code = entityTypeCode.trim();
        String key = fieldKey.trim();
        String trimmedValue = value.trim();
        if (!"name".equals(key)) {
            return new EntityFieldAvailabilityRespVO(true, null);
        }
        if (modelId == null) {
            return new EntityFieldAvailabilityRespVO(false, "缺少 modelId，无法校验名称唯一性");
        }
        boolean exists = entityRepository.existsByExactName(code, modelId, trimmedValue, excludeId);
        if (exists) {
            return new EntityFieldAvailabilityRespVO(false, "名称已存在");
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
        EntityDO entity = entityCoreService.get(id, entityTypeCode);
        if (entity == null) {
            return null;
        }
        EntityRespVO vo = EntityDoVoHelper.toRespVO(entity, customFieldValidationService);
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
            List<Long> modelIds, List<Long> categoryIds, List<CategoryIdGroupReqVO> categoryIdGroups, String categoryViaRefPathCode,
            Long entityId, Long rootEntityId, String entitySourceEntityType,
            Integer pageNo, Integer pageSize, String keyword, String domain,
            List<FieldFilterReqVO> filters) {

        if (scene == null) throw new ServiceException(400, "查询场景 scene 参数不能为空");

        Integer effectivePageNo = (pageNo == null || pageNo < 1) ? 1 : pageNo;
        Integer effectivePageSize = (pageSize == null || pageSize < 1) ? 20 : pageSize;
        int fullPageSize = Integer.MAX_VALUE;

        // 按入口解析：SCOPE → storage + 成员过滤；DOMAIN registry → storage + domain；其余用请求 domain
        ResolvedQueryType resolved = resolveQueryEntityType(entityTypeCode, domain);
        String storageEntityTypeCode = resolved.entityTypeCode();
        String normalizedDomain = resolved.domain();
        String normalizedScopeCode = resolved.scopeRegistryCode();

        EntityQueryResultShape shape = EntityQueryResultShape.ofNullable(resultShape);
        EntityQueryResultDetail detail = EntityQueryResultDetail.ofNullable(resultDetail);
        switch (scene) {
            case ENTITIES_BY_CATEGORY: {
                String queryEntityTypeCode = resolved.entityTypeCode();
                if (queryEntityTypeCode == null || queryEntityTypeCode.isBlank()) {
                    throw new ServiceException(400, "ENTITIES_BY_CATEGORY 场景下 entityTypeCode 不能为空");
                }
                String viaRefPathCode = trimToNull(categoryViaRefPathCode);
                if (viaRefPathCode != null) {
                    return queryEntitiesByCategoryViaRef(
                            viaRefPathCode, categoryTypeCode, categoryIds, queryEntityTypeCode,
                            normalizedDomain, normalizedScopeCode, keyword, filters,
                            shape, detail, effectivePageNo, effectivePageSize);
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
                            normalizedDomain, normalizedScopeCode);
                    return EntitySceneQueryRespVO.page(applyResultDetail(paged, detail), detail.getCode());
                }
                PageResult<EntityRespVO> full = queryDataMgmtEntitiesByCategoryModel(
                        categoryIds, categoryIdGroups, resolvedCategoryTypeCode, storageEntityTypeCode, modelIds,
                        keyword, filters, null, null, false,
                        normalizedDomain, normalizedScopeCode);
                List<EntityRespVO> entities = full.getList();
                if (shape == EntityQueryResultShape.TREE) {
                    return EntitySceneQueryRespVO.tree(
                            applyResultDetail(EntityTreeBuilder.buildTree(entities, EntityTreeBuilder.SortMode.LOCAL_SIBLING_SORT), detail),
                            detail.getCode());
                }
                return EntitySceneQueryRespVO.list(applyResultDetail(entities, detail), detail.getCode());
            }

            case ENTITIES_BY_MODEL: {
                List<Long> normalizedModelIds = normalizeModelIds(modelIds);
                if (!normalizedModelIds.isEmpty()) {
                    if (shape == EntityQueryResultShape.TREE && normalizedModelIds.size() == 1
                            && !StringUtils.hasText(normalizedDomain) && !StringUtils.hasText(normalizedScopeCode)) {
                        return EntitySceneQueryRespVO.tree(
                                applyResultDetail(getEntityTreeByModelId(storageEntityTypeCode, normalizedModelIds.get(0)), detail),
                                detail.getCode());
                    }
                    PageResult<EntityRespVO> modelPage = handlePatternBModelEntities(
                            normalizedModelIds, storageEntityTypeCode, keyword, filters,
                            shape == EntityQueryResultShape.PAGE ? effectivePageNo : 1,
                            shape == EntityQueryResultShape.PAGE ? effectivePageSize : fullPageSize,
                            normalizedDomain, normalizedScopeCode);
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
                if (shape == EntityQueryResultShape.TREE) {
                    List<EntityRespVO> treeRoots = buildEntityHierarchySubtree(
                            storageEntityTypeCode, null, keyword, filters, detail,
                            effectivePageNo, resolveTreeRootPageSize(pageSize));
                    return EntitySceneQueryRespVO.tree(applyResultDetail(treeRoots, detail), detail.getCode());
                }
                List<Long> allEntityIds = collectPatternAbcAllCandidateEntityIds(
                        storageEntityTypeCode, modelIds, normalizedDomain, normalizedScopeCode);
                PageResult<EntityRespVO> result = queryEntitiesByOrderedCandidateIds(
                        allEntityIds, storageEntityTypeCode, keyword, filters,
                        shape == EntityQueryResultShape.PAGE ? effectivePageNo : null,
                        shape == EntityQueryResultShape.PAGE ? effectivePageSize : null);
                if (shape == EntityQueryResultShape.PAGE) {
                    return EntitySceneQueryRespVO.page(applyResultDetail(result, detail), detail.getCode());
                }
                return EntitySceneQueryRespVO.list(applyResultDetail(result.getList(), detail), detail.getCode());
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
     * 数据管理三栏：分类定范围（含子树，空 categoryIds 回退根分类）+ 可选 modelIds 过滤。
     * <p>右栏实体只取分类范围内的 relation / link 关联，不做 modelId 全局扫表。</p>
     * <p>{@code categoryIdGroups} 非空时按多独立栏求交（组间 AND）。</p>
     */
    private PageResult<EntityRespVO> queryDataMgmtEntitiesByCategoryModel(List<Long> categoryIds,
                                                                          List<CategoryIdGroupReqVO> categoryIdGroups,
                                                                          String categoryTypeCode,
                                                                          String entityTypeCode, List<Long> modelIds,
                                                                          String keyword, List<FieldFilterReqVO> filters,
                                                                          Integer pageNo, Integer pageSize, boolean allowDirectPaging,
                                                                          String domain, String scopeRegistryCode) {
        if (entityTypeCode == null || entityTypeCode.isBlank()) {
            return new PageResult<>(new ArrayList<>(), 0L);
        }

        List<Long> orderedCandidateEntityIds;
        if (categoryIdGroups != null && !categoryIdGroups.isEmpty()) {
            List<List<Long>> expandedGroups = new ArrayList<>();
            for (CategoryIdGroupReqVO group : categoryIdGroups) {
                if (group == null) {
                    continue;
                }
                String groupTypeCode = (group.getCategoryTypeCode() == null || group.getCategoryTypeCode().isBlank())
                        ? categoryTypeCode
                        : group.getCategoryTypeCode().trim();
                List<Long> normalized = normalizeCategoryIdsOrUseRootCategory(group.getCategoryIds(), groupTypeCode);
                if (normalized.isEmpty()) {
                    continue;
                }
                List<Long> expanded = expandCategoryIdsWithDescendants(normalized, groupTypeCode);
                if (!expanded.isEmpty()) {
                    expandedGroups.add(expanded);
                }
            }
            if (expandedGroups.isEmpty()) {
                return new PageResult<>(new ArrayList<>(), 0L);
            }
            orderedCandidateEntityIds = dataMgmtEntityQueryRepository.listOrderedEntityIdsByIntersectingCategoryGroups(
                    expandedGroups, entityTypeCode, normalizeModelIds(modelIds), domain, scopeRegistryCode);
        } else {
            List<Long> normalizedCategoryIds = normalizeCategoryIdsOrUseRootCategory(categoryIds, categoryTypeCode);
            if (normalizedCategoryIds.isEmpty()) {
                return new PageResult<>(new ArrayList<>(), 0L);
            }
            List<Long> expandedCategoryIds = expandCategoryIdsWithDescendants(normalizedCategoryIds, categoryTypeCode);
            if (expandedCategoryIds.isEmpty()) {
                return new PageResult<>(new ArrayList<>(), 0L);
            }
            orderedCandidateEntityIds = dataMgmtEntityQueryRepository.listOrderedEntityIdsByCategoryScope(
                    expandedCategoryIds, entityTypeCode, normalizeModelIds(modelIds), domain, scopeRegistryCode);
        }

        if (allowDirectPaging && canPageDirectly(keyword, filters)) {
            Integer pn = normalizePageNo(pageNo);
            Integer ps = normalizePageSize(pageSize);
            return pageByOrderedIds(orderedCandidateEntityIds, entityTypeCode, pn, ps);
        }
        return queryEntitiesByOrderedCandidateIds(
                orderedCandidateEntityIds, entityTypeCode, keyword, filters, pageNo, pageSize);
    }

    /** 兼容旧调用：无 categoryIdGroups */
    private PageResult<EntityRespVO> queryDataMgmtEntitiesByCategoryModel(List<Long> categoryIds, String categoryTypeCode,
                                                                          String entityTypeCode, List<Long> modelIds,
                                                                          String keyword, List<FieldFilterReqVO> filters,
                                                                          Integer pageNo, Integer pageSize, boolean allowDirectPaging,
                                                                          String domain, String scopeRegistryCode) {
        return queryDataMgmtEntitiesByCategoryModel(categoryIds, null, categoryTypeCode, entityTypeCode, modelIds,
                keyword, filters, pageNo, pageSize, allowDirectPaging, domain, scopeRegistryCode);
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
        if (orderedCandidateEntityIds == null || orderedCandidateEntityIds.isEmpty()) {
            return new PageResult<>(new ArrayList<>(), 0L);
        }
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        boolean hasFilters = filters != null && !filters.isEmpty();

        // 性能优化：无 keyword/filters 且请求分页时，直接在有序 ID 上分页切片后回查，
        // 避免“先全量转 VO 再内存分页”导致大分类场景响应过慢。
        if (!hasKeyword && !hasFilters && pageNo != null && pageSize != null) {
            return pageByOrderedIds(orderedCandidateEntityIds, entityTypeCode, pageNo, pageSize);
        }

        List<EntityRespVO> searchedAndFilteredEntities;
        if (!hasKeyword && !hasFilters) {
            searchedAndFilteredEntities = fetchEntitiesByOrderedIds(orderedCandidateEntityIds, entityTypeCode);
        } else {
            // 有搜索/筛选时，直接返回过滤后的 VO 列表，避免后续再按 ID 二次回查实体
            searchedAndFilteredEntities = filterCandidateEntities(orderedCandidateEntityIds, entityTypeCode, filters, keyword);
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
                                                                    String domain, String scopeRegistryCode) {
        boolean hasFilters = filters != null && !filters.isEmpty();
        boolean hasScope = StringUtils.hasText(scopeRegistryCode);
        // 无业务 fieldFilters、无划分：可走实体表直分页（含 domain / keyword）
        if (!hasFilters && !hasScope) {
            return pageEntitiesByModelIds(modelIds, keyword, pageNo, pageSize, domain);
        }
        List<Long> candidateIds = collectCandidateEntityIdsByModelIds(modelIds, entityTypeCode);
        candidateIds = dataMgmtEntityQueryRepository.retainOrderedIdsByDomainAndScope(
                candidateIds, entityTypeCode, domain, scopeRegistryCode);
        if (candidateIds.isEmpty()) {
            return new PageResult<>(new ArrayList<>(), 0L);
        }
        if (hasFilters || (keyword != null && !keyword.trim().isEmpty())) {
            candidateIds = filterCandidateEntityIds(candidateIds, entityTypeCode, filters, keyword);
            if (candidateIds.isEmpty()) {
                return new PageResult<>(new ArrayList<>(), 0L);
            }
        }
        return pageByOrderedIds(candidateIds, entityTypeCode, pageNo, pageSize);
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
        List<Long> normalizedModelIds = requireModelIds(modelIds, "modelIds 不能为空");
        String normalizedDomain = EntityTypeScopeContext.normalizeDomain(domain);

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
                    .domain(normalizedDomain)
                    .pageNo(pageNo)
                    .pageSize(pageSize)
                    .build();
            PageResult<EntityDO> pageResult = entityRepository.findPage(query);
            List<EntityRespVO> list = pageResult.getList().stream()
                    .map(entityDO -> EntityDoVoHelper.toRespVO(entityDO, customFieldValidationService))
                    .toList();
            return new PageResult<>(list, pageResult.getTotal());
        }

        List<ModelDO> models = modelMapper.selectByIds(normalizedModelIds);
        if (models == null || models.isEmpty()) {
            throw new ServiceException(404, "模型不存在");
        }
        String entityTypeCode = models.get(0).getEntityTypeCode();
        if (entityTypeCode == null || entityTypeCode.isBlank()) {
            throw new ServiceException(400, "业务类型不能为空");
        }

        PageResult<EntityDO> pageResult = entityRepository.findPageByModelIds(
                normalizedModelIds,
                entityTypeCode,
                null,
                keyword,
                normalizedDomain,
                pageNo,
                pageSize
        );
        List<EntityRespVO> list = pageResult.getList().stream()
                .map(entityDO -> EntityDoVoHelper.toRespVO(entityDO, customFieldValidationService))
                .toList();
        return new PageResult<>(list, pageResult.getTotal());
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
     *   <li>keyword 搜索：只在文本域（name/customFields）上匹配。</li>
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
        // Step 1) 判空：候选为空直接返回。
        if (candidateIds == null || candidateIds.isEmpty()) {
            return Collections.emptyList();
        }

        // Step 2) 关联字段筛选：通过 dynamic_entity_relation 反查 source_entity_id，再与候选做交集（保序）。
        if (filters != null && !filters.isEmpty()) {
            for (FieldFilterReqVO filter : filters) {
                if (filter == null || filter.getFieldCode() == null || filter.getFieldCode().isBlank()) {
                    continue;
                }
                if (!Boolean.TRUE.equals(filter.getRelationField())) {
                    continue;
                }
                if (!isRelationFilterSupported(filter.getOp())) {
                    continue;
                }
                List<Long> targetEntityIds = normalizeEntityIds(filter.getValue());
                if (targetEntityIds.isEmpty()) {
                    return Collections.emptyList();
                }
                List<Long> matchedSourceIds = entityRelationService
                        .listEntityIdsByRelationFieldAndRelatedIds(filter.getFieldCode(), targetEntityIds);
                if (matchedSourceIds == null || matchedSourceIds.isEmpty()) {
                    return Collections.emptyList();
                }
                Set<Long> matchedSet = new HashSet<>(matchedSourceIds);
                candidateIds = candidateIds.stream()
                        .filter(id -> id != null && matchedSet.contains(id))
                        .toList();
                if (candidateIds.isEmpty()) {
                    return Collections.emptyList();
                }
            }
        }

        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        boolean hasNonRelationFilters = filters != null && filters.stream().anyMatch(f -> f != null && !Boolean.TRUE.equals(f.getRelationField()));
        if (!hasKeyword && !hasNonRelationFilters) {
            return candidateIds;
        }

        // Step 3) 非关联字段/keyword 存在时，回查实体详情进行匹配。
        List<EntityDO> entities = entityCoreService.listByIds(candidateIds, entityTypeCode);
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, EntityRespVO> byId = new HashMap<>();
        for (EntityDO entityDO : entities) {
            EntityRespVO vo = EntityDoVoHelper.toRespVO(entityDO, customFieldValidationService);
            if (vo != null && vo.getId() != null) {
                byId.put(vo.getId(), vo);
            }
        }

        String k = hasKeyword ? Objects.requireNonNull(keyword).trim().toLowerCase() : null;
        List<Long> filteredIds = new ArrayList<>();
        for (Long id : candidateIds) {
            EntityRespVO vo = byId.get(id);
            if (vo == null) {
                continue;
            }
            // Step 3.1) 非关联字段过滤（AND 语义）。
            if (hasNonRelationFilters) {
                boolean pass = true;
                for (FieldFilterReqVO filter : Objects.requireNonNullElse(filters, Collections.<FieldFilterReqVO>emptyList())) {
                    if (filter == null || filter.getFieldCode() == null || filter.getFieldCode().isBlank()) {
                        continue;
                    }
                    if (Boolean.TRUE.equals(filter.getRelationField())) {
                        continue;
                    }
                    FieldDO fieldDO = fieldMapper.selectByCode(filter.getFieldCode());
                    String fieldType = fieldDO == null ? null : fieldDO.getType();
                    String op = filter.getOp() == null ? "" : filter.getOp().trim().toUpperCase();
                    if (!matchNonRelationFilter(vo, filter.getFieldCode(), fieldType, op, filter.getValue())) {
                        pass = false;
                        break;
                    }
                }
                if (!pass) {
                    continue;
                }
            }
            // Step 3.2) keyword 搜索（只匹配文本：name/customFields）。
            if (k != null) {
                boolean hit = (vo.getName() != null && vo.getName().toLowerCase().contains(k))
                        || (vo.getCustomFields() != null && JSON.toJSONString(vo.getCustomFields()).toLowerCase().contains(k));
                if (!hit) {
                    continue;
                }
            }
            filteredIds.add(id);
        }
        return filteredIds;
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
        if (candidateIds == null || candidateIds.isEmpty()) {
            return new ArrayList<>();
        }

        Set<Long> matchedIds = entityFieldQueryEngine.searchAndFilterEntityIds(
                entityTypeCode,
                keyword,
                filters,
                candidateIds
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
        return fetchEntityPageByOrderedIds(pagedEntityIds, entityTypeCode);
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

        EntityDO entity = entityCoreService.get(link.getEntityId(), entityTypeCode);
        if (entity == null) {
            return null;
        }
        return EntityDoVoHelper.toRespVO(entity, customFieldValidationService);
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
            Integer pageSize) {
        categoryViaRefQueryService.assertSubjectTypeMatches(categoryViaRefPathCode, subjectEntityTypeCode);
        categoryViaRefQueryService.assertDimensionCategoryTypeMatches(categoryViaRefPathCode, categoryTypeCode);
        CategoryViaRefQueryPath path = categoryViaRefQueryService.requirePath(categoryViaRefPathCode);
        List<Long> normalizedCategoryIds = normalizeCategoryIdsOrUseRootCategory(
                categoryIds, path.dimensionCategoryTypeCode());
        List<Long> subjectEntityIds = categoryViaRefQueryService.listSubjectEntityIds(
                categoryViaRefPathCode, normalizedCategoryIds);
        subjectEntityIds = dataMgmtEntityQueryRepository.retainOrderedIdsByDomainAndScope(
                subjectEntityIds, subjectEntityTypeCode, domain, scopeRegistryCode);
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
        if (pagedEntityIds == null || pagedEntityIds.getList() == null || pagedEntityIds.getList().isEmpty()) {
            return new PageResult<>(new ArrayList<>(), pagedEntityIds == null ? 0L : pagedEntityIds.getTotal());
        }
        List<EntityRespVO> result = convertOrderedEntityIdsToRespList(pagedEntityIds.getList(), entityTypeCode);
        return new PageResult<>(result, pagedEntityIds.getTotal());
    }

    /**
     * 将有序实体ID列表转换为VO列表，并保持输入顺序。
     */
    private List<EntityRespVO> convertOrderedEntityIdsToRespList(List<Long> orderedEntityIds, String entityTypeCode) {
        if (orderedEntityIds == null || orderedEntityIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<EntityDO> entities = entityCoreService.listByIds(orderedEntityIds, entityTypeCode);
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Long, EntityDO> byId = entities.stream()
                .filter(e -> e.getId() != null)
                .collect(Collectors.toMap(EntityDO::getId, e -> e, (a, b) -> a));

        List<EntityRespVO> result = new ArrayList<>();
        for (Long id : orderedEntityIds) {
            EntityDO entity = byId.get(id);
            if (entity != null) {
                result.add(EntityDoVoHelper.toRespVO(entity, customFieldValidationService));
            }
        }
        return result;
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

        List<EntityRespVO> list = pageResult.getList().stream()
                .map(entityDO -> EntityDoVoHelper.toRespVO(entityDO, customFieldValidationService))
                .toList();
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

                cleanupAssociationsOnEntityDelete(id, entityTypeCode);
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
                reqVO.getEntityIds(), reqVO.getCategoryId(), reqVO.getEntityTypeCode());
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

        for (Long id : ids) {
            EntityRespVO entity = null;
            try {
                EntityDO entityDO = entityCoreService.get(id, entityTypeCode);
                if (entityDO != null) {
                    entity = EntityDoVoHelper.toRespVO(entityDO, customFieldValidationService);
                }
            } catch (Exception e) {
                log.debug("获取实体失败: id={}, entityTypeCode={}", id, entityTypeCode);
            }

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
        List<EntityRespVO> entities = detail == EntityQueryResultDetail.LIGHT
                ? EntityDoVoHelper.toLightRespVOList(rawEntities)
                : EntityDoVoHelper.toRespVOList(rawEntities, customFieldValidationService);
        entities = filterEntityRespList(entities, entityTypeCode, keyword, filters);

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
        List<EntityRespVO> respVOList = EntityDoVoHelper.toRespVOList(entities, customFieldValidationService);
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
        if (detail != EntityQueryResultDetail.LIGHT) {
            return entities;
        }
        return entities.stream().map(this::toLightRespVO).toList();
    }

    /**
     * 轻量结果仅保留下拉/选择常用字段，避免返回完整明细。
     */
    private EntityRespVO toLightRespVO(EntityRespVO source) {
        if (source == null) {
            return null;
        }
        EntityRespVO light = new EntityRespVO();
        light.setId(source.getId());
        light.setSort(source.getSort());
        if (source.getBaseFields() != null) {
            light.setBaseFields(new LinkedHashMap<>(source.getBaseFields()));
        } else {
            Map<String, Object> base = new LinkedHashMap<>();
            putIfNotNull(base, "entityTypeCode", source.getEntityTypeCode());
            putIfNotNull(base, "modelId", source.getModelId());
            putIfNotNull(base, "name", source.getName());
            putIfNotNull(base, "status", source.getStatus());
            putIfNotNull(base, "parentId", source.getParentId());
            light.setBaseFields(base);
        }
        light.setChildren(applyResultDetail(source.getChildren(), EntityQueryResultDetail.LIGHT));
        return light;
    }

    private static void putIfNotNull(Map<String, Object> target, String key, Object value) {
        if (value != null) {
            target.put(key, value);
        }
    }

}
