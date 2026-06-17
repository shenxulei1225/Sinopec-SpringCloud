package cn.cheers.x.module.dynamicbusiness.service.entity;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.*;
import cn.cheers.x.module.dynamicbusiness.convert.entity.EntityDoVoHelper;
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
import cn.cheers.x.module.dynamicbusiness.dal.mysql.field.FieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntityAggregationMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelFieldAssignmentMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.EntityRepository;
import cn.cheers.x.module.dynamicbusiness.framework.entity.EntityTableNameContext;
import cn.cheers.x.module.dynamicbusiness.service.entity.core.EntityCoreService;
import cn.cheers.x.module.dynamicbusiness.service.entity.dto.EntityAggregationCountDTO;
import cn.cheers.x.module.dynamicbusiness.service.field.CustomFieldValidationService;
import cn.cheers.x.module.dynamicbusiness.enums.field.FieldTypeEnum;
import cn.cheers.x.module.dynamicbusiness.enums.entity.EntityQueryScene;
import cn.cheers.x.module.dynamicbusiness.enums.entity.EntityQueryResultDetail;
import cn.cheers.x.module.dynamicbusiness.enums.entity.EntityQueryResultShape;
import cn.cheers.x.module.dynamicbusiness.service.entity.relation.EntityCategoryRelationService;
import cn.cheers.x.module.dynamicbusiness.service.entity.relation.EntityRelationService;
import cn.cheers.x.module.dynamicbusiness.service.entity.sync.EntitySyncService;
import cn.cheers.x.module.dynamicbusiness.service.model.ModelService;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryEntityLinkService;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryTypeService;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private EntityAggregationMapper entityAggregationMapper;

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
        ModelDO model = modelMapper.selectById(reqVO.getModelId());
        Map<String, Object> customFieldsMap = entityBusinessHelper.emptyIfNull(reqVO.getCustomFields());
        entityRelationSyncService.syncRelationsOnCreate(data, model, customFieldsMap);

        // 3) 缓存失效：写后清理树/列表缓存，避免读到旧数据
        entityCacheEvictionService.evictEntityCaches(reqVO.getModelId(), reqVO.getBusinessTypeCode());

        // 4) 事件发布：通知预计算/同步链路，作为跨模块副作用入口
        entityLifecycleEventPublisher.publishEntityCreatedEvent(reqVO.getModelId(), entityId, reqVO.getBusinessTypeCode(), data);

        return entityId;
    }

    /**
     * 更新实体（本体更新 + 关系差异同步 + 缓存失效 + 更新事件）。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(EntityUpdateReqVO reqVO) {
        // 1) 读取旧值：用于不存在校验与关系差异计算
        EntityDO oldEntity = entityCoreService.get(reqVO.getId(), reqVO.getBusinessTypeCode());
        if (oldEntity == null) {
            throw new ServiceException(404, ENTITY_NOT_EXISTS);
        }

        Long modelId = reqVO.getModelId() != null ? reqVO.getModelId() : oldEntity.getModelId();

        // 2) 本体更新：仅更新实体表
        EntityDO data = entityBusinessHelper.prepareUpdateEntity(reqVO, oldEntity);
        entityCoreService.update(data);

        // 3) 关系差异同步：根据新旧 customFields 计算并更新关系表
        ModelDO model = modelMapper.selectById(modelId);
        Map<String, Object> customFieldsMap = entityBusinessHelper.emptyIfNull(reqVO.getCustomFields());
        Map<String, Object> oldCustomFieldsMap = entityBusinessHelper.emptyIfNull(oldEntity.getCustomFields());
        entityRelationSyncService.syncRelationsOnUpdate(data, model, customFieldsMap, oldCustomFieldsMap);

        // 4) 缓存失效 + 事件通知：确保读取一致性并通知下游链路
        entityCacheEvictionService.evictEntityCaches(modelId, reqVO.getBusinessTypeCode());
        List<String> changedFields = entityBusinessHelper.extractFieldCodes(reqVO.getCustomFields());
        entityLifecycleEventPublisher.publishEntityUpdatedEvent(
                modelId,
                reqVO.getId(),
                reqVO.getBusinessTypeCode(),
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
        EntityDO existingEntity = entityCoreService.get(reqVO.getId(), reqVO.getBusinessTypeCode());
        if (existingEntity == null) {
            throw new ServiceException(404, ENTITY_NOT_EXISTS);
        }

        // 非强制删除时，做最小化保护：存在子实体则拒绝删除
        if (!Boolean.TRUE.equals(reqVO.getForceDelete())
                && entityCoreService.existsByParentId(reqVO.getId(), reqVO.getBusinessTypeCode())) {
            throw new ServiceException(400, "存在子实体，无法删除。请先删除子实体或使用强制删除");
        }

        // 2) 先清理关系，再删本体，避免关系悬挂
        entityRelationSyncService.syncRelationsOnDelete(reqVO.getId(), reqVO.getBusinessTypeCode());
        entityCoreService.delete(reqVO.getId(), reqVO.getBusinessTypeCode());

        // 3) 写后副作用：缓存失效 + 删除事件
        entityCacheEvictionService.evictEntityCaches(existingEntity.getModelId(), reqVO.getBusinessTypeCode());
        entityLifecycleEventPublisher.publishEntityDeletedEvent(existingEntity.getModelId(), reqVO.getId(), reqVO.getBusinessTypeCode());
    }


    // ================================= 实体查询 ==========================================
    /** =======================单实体详情获取：按主键获取实体详情。 ====================== */
    /**
     *  单实体详情获取：按主键获取实体详情。
     *
     * @return 实体详情；不存在时返回 null
     */
    @Override
    public EntityRespVO get(Long id, String businessTypeCode) {
        return get(id, businessTypeCode, false, null);
    }

    @Override
    public EntityRespVO get(Long id, String businessTypeCode, boolean includeAssociations,
            List<AssociationCategoryViewReqVO> associationCategoryViews) {
        EntityDO entity = entityCoreService.get(id, businessTypeCode);
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
     * 4.1 场景五：单条实体详情（{@link EntityQueryScene#PATTERN_D_ENTITY_DETAILED_INFO}，详情内携带跨业务关联关系摘要）
     *
     * <p><b>补充说明</b>：</p>
     * <ul>
     *   <li>分类/模型的“单选或多选”属于参数形态差异，不扩展新的 Scene 枚举。</li>
     *   <li>{@code only}/{@code withDescendants} 属于服务层内部查询能力，不直接暴露为用户场景。</li>
     *   <li>分类相关结果顺序规则：{@code categoryIds} 输入顺序 -> 分类内 {@code sort} -> 关系ID -> 稳定去重。</li>
     * </ul>
     */

    @Override
    public EntitySceneQueryRespVO queryEntities(EntityQueryScene scene, String resultShape, String resultDetail, String categoryTypeCode, String businessTypeCode,
            List<Long> modelIds, List<Long> categoryIds, Long entityId, Long rootEntityId, String entitySourceBusinessType,
            Integer pageNo, Integer pageSize, String keyword, List<FieldFilterReqVO> filters) {

        if (scene == null) throw new ServiceException(400, "查询场景 scene 参数不能为空");

        Integer effectivePageNo = (pageNo == null || pageNo < 1) ? 1 : pageNo;
        Integer effectivePageSize = (pageSize == null || pageSize < 1) ? 20 : pageSize;
        int fullPageSize = Integer.MAX_VALUE;

        EntityQueryResultShape shape = EntityQueryResultShape.ofNullable(resultShape);
        EntityQueryResultDetail detail = EntityQueryResultDetail.ofNullable(resultDetail);
        switch (scene) {
            // 1 按分类查询实体列表
            /** 1.1 场景一： pattern A和C：按分类查询实体列表（单/多分类分流）, 支持搜索功能
                Pattern A和C 是查询直接关联到分类的实体
            */
            case PATTERN_A_C_ENTITIES_BY_CATEGORY:
                /** 方案内部拆解为
                 *   点选单分类：categoryIds 传单个代表 点选是单个分类 （前端只支持单分类的点击）
                 *   勾选多分类：categoryIIds 传多个个代表勾选多个分类（前端支持勾选多个分类）
                 *   无分类：无分类时，默认将视图中分类的根节点作为CategoryId
                 */
                // Page分页查看实体列表
                if (shape == EntityQueryResultShape.PAGE) {
                    PageResult<EntityRespVO> pagedCategoryEntities = queryEntitiesByCategoryIds(
                            categoryIds, categoryTypeCode, businessTypeCode, keyword, filters,
                            effectivePageNo, effectivePageSize, true);
                    return EntitySceneQueryRespVO.page(applyResultDetail(pagedCategoryEntities, detail), detail.getCode());
                }
                // LIST/TREE 不分页：传 null 分页参数，返回的是全量有序结果（仅借用 PageResult 容器承载 list+total）
                PageResult<EntityRespVO> fullCategoryEntitiesResult = queryEntitiesByCategoryIds(
                        categoryIds, categoryTypeCode, businessTypeCode, keyword, filters,
                        null, null, false);
                List<EntityRespVO> patternACCategoryEntities = fullCategoryEntitiesResult.getList();

                // 在List的数据结果上进行树形结构构建
                boolean isTreeShape = shape == EntityQueryResultShape.TREE;
                List<EntityRespVO> shapedCategoryEntities = isTreeShape
                        ? EntityTreeBuilder.buildTree(patternACCategoryEntities, EntityTreeBuilder.SortMode.LOCAL_SIBLING_SORT)
                        : patternACCategoryEntities;
                List<EntityRespVO> detailedCategoryEntities = applyResultDetail(shapedCategoryEntities, detail);
                // 返回树形结构或List平铺结构
                return isTreeShape
                        ? EntitySceneQueryRespVO.tree(detailedCategoryEntities, detail.getCode())
                        : EntitySceneQueryRespVO.list(detailedCategoryEntities, detail.getCode());
            
                // 1.2 场景二： Pattern B：按分类查询模型下的实体列表（单/多分类分流），支持搜索功能
            case PATTERN_B_ENTITIES_BY_CATEGORY:
                List<Long> patternBOrderedCandidateEntityIds = collectPatternBCategoryOrderedEntityIds(
                        categoryIds, categoryTypeCode, businessTypeCode);
                PageResult<EntityRespVO> patternBCategoryResult = queryEntitiesByOrderedCandidateIds(
                        patternBOrderedCandidateEntityIds, businessTypeCode, keyword, filters,
                        shape == EntityQueryResultShape.PAGE ? effectivePageNo : null,
                        shape == EntityQueryResultShape.PAGE ? effectivePageSize : null);
                if (shape == EntityQueryResultShape.PAGE) {
                    return EntitySceneQueryRespVO.page(applyResultDetail(patternBCategoryResult, detail), detail.getCode());
                }
                List<EntityRespVO> patternBCategoryEntities = patternBCategoryResult.getList();
                if (shape == EntityQueryResultShape.TREE) {
                    return EntitySceneQueryRespVO.tree(
                            applyResultDetail(EntityTreeBuilder.buildTree(patternBCategoryEntities, EntityTreeBuilder.SortMode.LOCAL_SIBLING_SORT), detail),
                            detail.getCode());
                }
                return EntitySceneQueryRespVO.list(
                        applyResultDetail(patternBCategoryEntities, detail),
                        detail.getCode());

            case PATTERN_A_C_UNCATEGORIZED_ENTITIES_BY_CATEGORY_TYPE: {
                List<Long> uncategorizedEntityIds = collectUncategorizedEntityIdsByCategoryType(categoryTypeCode, businessTypeCode);
                PageResult<EntityRespVO> result = queryEntitiesByOrderedCandidateIds(
                        uncategorizedEntityIds, businessTypeCode, keyword, filters,
                        shape == EntityQueryResultShape.PAGE ? effectivePageNo : null,
                        shape == EntityQueryResultShape.PAGE ? effectivePageSize : null);
                if (shape == EntityQueryResultShape.PAGE) {
                    return EntitySceneQueryRespVO.page(applyResultDetail(result, detail), detail.getCode());
                }
                List<EntityRespVO> entities = result.getList();
                if (shape == EntityQueryResultShape.TREE) {
                    return EntitySceneQueryRespVO.tree(
                            applyResultDetail(EntityTreeBuilder.buildTree(entities, EntityTreeBuilder.SortMode.LOCAL_SIBLING_SORT), detail),
                            detail.getCode());
                }
                return EntitySceneQueryRespVO.list(applyResultDetail(entities, detail), detail.getCode());
            }

            case PATTERN_B_UNCATEGORIZED_ENTITIES_BY_CATEGORY_TYPE: {
                List<Long> uncategorizedPatternBEntityIds = collectPatternBUncategorizedEntityIdsByCategoryType(categoryTypeCode, businessTypeCode);
                PageResult<EntityRespVO> result = queryEntitiesByOrderedCandidateIds(
                        uncategorizedPatternBEntityIds, businessTypeCode, keyword, filters,
                        shape == EntityQueryResultShape.PAGE ? effectivePageNo : null,
                        shape == EntityQueryResultShape.PAGE ? effectivePageSize : null);
                if (shape == EntityQueryResultShape.PAGE) {
                    return EntitySceneQueryRespVO.page(applyResultDetail(result, detail), detail.getCode());
                }
                List<EntityRespVO> entities = result.getList();
                if (shape == EntityQueryResultShape.TREE) {
                    return EntitySceneQueryRespVO.tree(
                            applyResultDetail(EntityTreeBuilder.buildTree(entities, EntityTreeBuilder.SortMode.LOCAL_SIBLING_SORT), detail),
                            detail.getCode());
                }
                return EntitySceneQueryRespVO.list(applyResultDetail(entities, detail), detail.getCode());
            }

            case PATTERN_ABC_ALL_ENTITIES_BY_BUSINESS_TYPE: {
                List<Long> allEntityIds = collectAllEntityIdsByBusinessType(businessTypeCode);
                PageResult<EntityRespVO> result = queryEntitiesByOrderedCandidateIds(
                        allEntityIds, businessTypeCode, keyword, filters,
                        shape == EntityQueryResultShape.PAGE ? effectivePageNo : null,
                        shape == EntityQueryResultShape.PAGE ? effectivePageSize : null);
                if (shape == EntityQueryResultShape.PAGE) {
                    return EntitySceneQueryRespVO.page(applyResultDetail(result, detail), detail.getCode());
                }
                List<EntityRespVO> entities = result.getList();
                if (shape == EntityQueryResultShape.TREE) {
                    return EntitySceneQueryRespVO.tree(
                            applyResultDetail(EntityTreeBuilder.buildTree(entities, EntityTreeBuilder.SortMode.LOCAL_SIBLING_SORT), detail),
                            detail.getCode());
                }
                return EntitySceneQueryRespVO.list(applyResultDetail(entities, detail), detail.getCode());
            }

            // 2 按模型查询实体列表
            /** 2.1 场景三： 点击模型查看实体列表*/
            case PATTERN_B_ENTITIES_BY_MODEL:
                // 规范化模型ID列表 去空 去重 保留顺序，至少有一个id，否则抛出业务异常
                List<Long> selectedModelIds = requireModelIds(
                        modelIds, "PATTERN_B_ENTITIES_BY_MODEL 场景下 modelIds 至少传一个");
                if (shape == EntityQueryResultShape.TREE && selectedModelIds.size() == 1) {
                    return EntitySceneQueryRespVO.tree(applyResultDetail(getEntityTreeByModelId(businessTypeCode, selectedModelIds.get(0)), detail),
                            detail.getCode());
                }
                PageResult<EntityRespVO> modelPage = handlePatternBModelEntities(selectedModelIds, businessTypeCode, keyword, filters,
                        shape == EntityQueryResultShape.PAGE ? effectivePageNo : 1,
                        shape == EntityQueryResultShape.PAGE ? effectivePageSize : fullPageSize);
                if (shape == EntityQueryResultShape.PAGE) {
                    return EntitySceneQueryRespVO.page(applyResultDetail(modelPage, detail), detail.getCode());
                }
                return EntitySceneQueryRespVO.list(applyResultDetail(modelPage.getList(), detail), detail.getCode());

            // 4. 查看单条实体详情（详情内携带跨业务关联关系摘要，非“关联实体列表”作为主结果）
            case PATTERN_D_ENTITY_DETAILED_INFO: {
                EntityRespVO patternDDetail = buildPatternDEntityDetailedInfo(entityId, businessTypeCode);
                if (patternDDetail == null) {
                    throw new ServiceException(404, ENTITY_NOT_EXISTS);
                }
                EntityRespVO shapedOne = applyResultDetail(Collections.singletonList(patternDDetail), detail).get(0);
                if (shape == EntityQueryResultShape.PAGE) {
                    return EntitySceneQueryRespVO.page(
                            new PageResult<>(Collections.singletonList(shapedOne), 1L), detail.getCode());
                }
                if (shape == EntityQueryResultShape.TREE) {
                    return EntitySceneQueryRespVO.tree(
                            Collections.singletonList(shapedOne), detail.getCode());
                }
                return EntitySceneQueryRespVO.list(Collections.singletonList(shapedOne), detail.getCode());
            }
            default:
                throw new ServiceException(400, "不支持的查询场景: " + scene.getCode());
        }
    }

    /**
     * 规范化模型 ID：去 null、去重（保留顺序）。
     * <p>允许返回空列表，供“候选收集”等允许无模型入参的路径使用。</p>
     */
    private List<Long> normalizeModelIds(List<Long> modelIds) {
        if (modelIds == null || modelIds.isEmpty()) {
            return List.of();
        }
        return modelIds.stream().filter(Objects::nonNull).distinct().toList();
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
                                                                String businessTypeCode, String keyword, List<FieldFilterReqVO> filters,
                                                                Integer pageNo, Integer pageSize, boolean allowDirectPaging) {
        if (businessTypeCode == null || businessTypeCode.isBlank()) {
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
                        normalizedCategoryIds.get(0), categoryTypeCode, businessTypeCode, pn, ps);
            } else {
                // 点选是单个分类
                pagedEntityIds = entityCategoryRelationService.pageEntityIdsByCategoryIdsWithDescendantsDb(
                        normalizedCategoryIds, businessTypeCode, pn, ps);
            }
            return fetchEntityPageByOrderedIds(pagedEntityIds, businessTypeCode);
        }

        List<Long> orderedCandidateEntityIds = listOrderedEntityIdsByCategoriesInBusiness(normalizedCategoryIds, categoryTypeCode, businessTypeCode);
        return queryEntitiesByOrderedCandidateIds(
                orderedCandidateEntityIds, businessTypeCode, keyword, filters, pageNo, pageSize);
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
    private List<Long> collectPatternBCategoryOrderedEntityIds(List<Long> categoryIds, String categoryTypeCode,
                                                               String businessTypeCode) {
        // Step 1) 参数校验
        if (businessTypeCode == null || businessTypeCode.isBlank()) {
            return new ArrayList<>();
        }

        /** Step 2: 按分类拿到“有顺序的模型ID列表
        *  按照输入categoryIds 的分类顺序 -> 获取分类内排序的模型列表 orderedModelIds）
        */
        List<Long> orderedModelIds = modelService
                .queryOrderedModelIdsByCategoriesInBusiness(categoryIds, categoryTypeCode, businessTypeCode, null, null)
                .getList();
        log.info("[PATTERN_B_TRACE] collectPatternBCategoryOrderedEntityIds model stage: categoryTypeCode={}, businessTypeCode={}, categoryIds={}, orderedModelCount={}",
                categoryTypeCode, businessTypeCode, categoryIds, orderedModelIds == null ? 0 : orderedModelIds.size());

        if (orderedModelIds.isEmpty()) {
            return new ArrayList<>();
        }

        // Step 5) 按模型顺序获取候选 entityIds（模型内按实体ID升序稳定排序）
        List<EntityDO> entities = entityRepository.findByModelIds(orderedModelIds, businessTypeCode);
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }
        Map<Long, List<EntityDO>> entitiesByModelId = entities.stream()
                .filter(e -> e.getModelId() != null)
                .collect(Collectors.groupingBy(EntityDO::getModelId));

        List<Long> orderedEntityIds = new ArrayList<>();
        for (Long modelId : orderedModelIds) {
            List<EntityDO> modelEntities = entitiesByModelId.get(modelId);
            if (modelEntities == null || modelEntities.isEmpty()) {
                continue;
            }
            modelEntities.sort(Comparator.comparing(e -> e.getId() == null ? Long.MAX_VALUE : e.getId()));
            for (EntityDO entity : modelEntities) {
                if (entity.getId() != null) {
                    orderedEntityIds.add(entity.getId());
                }
            }
        }
        if (orderedEntityIds.isEmpty()) {
            return new ArrayList<>();
        }
        log.info("[PATTERN_B_TRACE] collectPatternBCategoryOrderedEntityIds entity stage: businessTypeCode={}, orderedEntityCount={}",
                businessTypeCode, orderedEntityIds.size());
        return orderedEntityIds;
    }

    /**
     * 全部实体：按业务类型返回全部实体 ID（有序）。
     */
    private List<Long> collectAllEntityIdsByBusinessType(String businessTypeCode) {
        if (businessTypeCode == null || businessTypeCode.isBlank()) {
            return new ArrayList<>();
        }
        List<EntityDO> allEntities = entityCoreService.listEntities(businessTypeCode, null, null);
        if (allEntities == null || allEntities.isEmpty()) {
            return new ArrayList<>();
        }
        return allEntities.stream()
                .map(EntityDO::getId)
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * A/C 未分类：在当前分类体系（categoryTypeCode）下未绑定任何分类的实体。
     */
    private List<Long> collectUncategorizedEntityIdsByCategoryType(String categoryTypeCode, String businessTypeCode) {
        if (businessTypeCode == null || businessTypeCode.isBlank() || categoryTypeCode == null || categoryTypeCode.isBlank()) {
            return new ArrayList<>();
        }

        List<EntityDO> allEntities = entityCoreService.listEntities(businessTypeCode, null, null);
        if (allEntities == null || allEntities.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> orderedAllEntityIds = allEntities.stream()
                .map(EntityDO::getId)
                .filter(Objects::nonNull)
                .toList();

        List<CategoryDO> categories = categoryMapper.selectByCategoryTypeCode(categoryTypeCode);
        if (categories == null || categories.isEmpty()) {
            return new ArrayList<>(orderedAllEntityIds);
        }
        List<Long> categoryIds = categories.stream().map(CategoryDO::getId).filter(Objects::nonNull).toList();
        if (categoryIds.isEmpty()) {
            return new ArrayList<>(orderedAllEntityIds);
        }

        Set<Long> categorizedEntityIds = entityCategoryRelationMapper
                .selectRelationsByCategoryIdsForOrdering(categoryIds, businessTypeCode)
                .stream()
                .map(EntityCategoryRelationDO::getEntityId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        return orderedAllEntityIds.stream()
                .filter(id -> !categorizedEntityIds.contains(id))
                .toList();
    }

    /**
     * B 未分类：在当前分类体系（categoryTypeCode）下未绑定任何分类的模型，其下实体。
     */
    private List<Long> collectPatternBUncategorizedEntityIdsByCategoryType(String categoryTypeCode, String businessTypeCode) {
        if (businessTypeCode == null || businessTypeCode.isBlank() || categoryTypeCode == null || categoryTypeCode.isBlank()) {
            return new ArrayList<>();
        }

        List<ModelRespVO> allModels = modelService.listModelsByBusinessType(businessTypeCode);
        if (allModels == null || allModels.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> allModelIds = allModels.stream().map(ModelRespVO::getId).filter(Objects::nonNull).toList();

        List<CategoryDO> categories = categoryMapper.selectByCategoryTypeCode(categoryTypeCode);
        List<Long> categoryIds = (categories == null) ? List.of() : categories.stream().map(CategoryDO::getId).filter(Objects::nonNull).toList();

        Set<Long> categorizedModelIds = new HashSet<>();
        if (!categoryIds.isEmpty()) {
            categorizedModelIds.addAll(modelService
                    .queryOrderedModelIdsByCategoriesInBusiness(categoryIds, categoryTypeCode, businessTypeCode, null, null)
                    .getList());
        }

        List<Long> uncategorizedModelIds = allModelIds.stream()
                .filter(id -> !categorizedModelIds.contains(id))
                .toList();

        return collectCandidateEntityIdsByModelIds(uncategorizedModelIds, businessTypeCode);
    }

    /**
     * 通用后处理：基于“前置已确定顺序的候选实体ID”执行搜索/筛选，再输出分页或全量结果。
     */
    private PageResult<EntityRespVO> queryEntitiesByOrderedCandidateIds(List<Long> orderedCandidateEntityIds,
                                                                        String businessTypeCode, String keyword, List<FieldFilterReqVO> filters,
                                                                        Integer pageNo, Integer pageSize) {
        if (orderedCandidateEntityIds == null || orderedCandidateEntityIds.isEmpty()) {
            return new PageResult<>(new ArrayList<>(), 0L);
        }
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        boolean hasFilters = filters != null && !filters.isEmpty();

        // 性能优化：无 keyword/filters 且请求分页时，直接在有序 ID 上分页切片后回查，
        // 避免“先全量转 VO 再内存分页”导致大分类场景响应过慢。
        if (!hasKeyword && !hasFilters && pageNo != null && pageSize != null) {
            return pageByOrderedIds(orderedCandidateEntityIds, businessTypeCode, pageNo, pageSize);
        }

        List<EntityRespVO> searchedAndFilteredEntities;
        if (!hasKeyword && !hasFilters) {
            searchedAndFilteredEntities = fetchEntitiesByOrderedIds(orderedCandidateEntityIds, businessTypeCode);
        } else {
            // 有搜索/筛选时，直接返回过滤后的 VO 列表，避免后续再按 ID 二次回查实体
            searchedAndFilteredEntities = filterCandidateEntities(orderedCandidateEntityIds, businessTypeCode, filters, keyword);
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
    private PageResult<EntityRespVO> handlePatternBModelEntities(List<Long> modelIds, String businessTypeCode,
                                                                    String keyword, List<FieldFilterReqVO> filters,
                                                                    Integer pageNo, Integer pageSize) {
        // Step 1) 无筛选：保留原有按模型分页能力（含 keyword）。
        // pageEntitiesByModelIds 直接出分页结果
        if (filters == null || filters.isEmpty()) {
            return pageEntitiesByModelIds(modelIds, keyword, pageNo, pageSize);
        }
        // Step 2) 有筛选：先收集候选 IDs（按模型顺序展开）。
        List<Long> candidateIds = collectCandidateEntityIdsByModelIds(modelIds);
        if (candidateIds.isEmpty()) {
            return new PageResult<>(new ArrayList<>(), 0L);
        }
        // Step 3) 对候选 IDs 执行 filters + keyword（先过滤后分页）。
        candidateIds = filterCandidateEntityIds(candidateIds, businessTypeCode, filters, keyword);
        if (candidateIds.isEmpty()) {
            return new PageResult<>(new ArrayList<>(), 0L);
        }
        // Step 4) 对过滤后的有序 IDs 分页并回查实体详情。
        return pageByOrderedIds(candidateIds, businessTypeCode, pageNo, pageSize);
    }

    /**----3. 分类即实体（Category Link Entity）视图下的分类详情查询
     * 3.1 场景四 ：Pattern C：分类即实体（Category Link Entity）视图下的分类详情查询。
     *
     * 区别在于该场景用于“分类详情 = 分类链接实体详情”。</p>
     */


    /**
     * 场景五 模式 D：单条实体详情并包含关联块（default 扁平行；多视角见 get + associationCategoryViews）。
     */
    private EntityRespVO buildPatternDEntityDetailedInfo(Long entityId, String businessTypeCode) {
        if (entityId == null || businessTypeCode == null) {
            throw new ServiceException(400, "参数缺失");
        }
        return get(entityId, businessTypeCode, true, null);
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
            String peerBtc = asSource ? r.getTargetBusinessTypeCode() : r.getSourceBusinessTypeCode();
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
        row.setBusinessTypeCode(p.peerBtc);
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

    private void resolveCategoryForPeer(Long peerEntityId, String peerBusinessTypeCode, String categoryTypeCode,
            AssociationEntityRowVO row) {
        if (peerEntityId == null || peerBusinessTypeCode == null || peerBusinessTypeCode.isBlank()
                || categoryTypeCode == null || categoryTypeCode.isBlank()) {
            return;
        }
        List<Long> catIds = entityCategoryRelationService.listCategoryIdsByEntityId(peerEntityId, peerBusinessTypeCode);
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
                                                                String keyword, Integer pageNo, Integer pageSize) {
        List<Long> normalizedModelIds = requireModelIds(modelIds, "modelIds 不能为空");

        if (normalizedModelIds.size() == 1) {
            Long singleModelId = normalizedModelIds.get(0);
            ModelDO model = modelMapper.selectById(singleModelId);
            if (model == null) {
                throw new ServiceException(404, "模型不存在");
            }
            PageResult<EntityDO> pageResult = entityCoreService.pageEntities(
                    model.getBusinessTypeCode(),
                    singleModelId,
                    null,
                    keyword,
                    pageNo,
                    pageSize
            );
            List<EntityRespVO> list = pageResult.getList().stream()
                    .map(entityDO -> EntityDoVoHelper.toRespVO(entityDO, customFieldValidationService))
                    .toList();
            return new PageResult<>(list, pageResult.getTotal());
        }

        List<ModelDO> models = modelMapper.selectByIds(normalizedModelIds);
        if (models == null || models.isEmpty()) {
            throw new ServiceException(404, "模型不存在");
        }
        String businessTypeCode = models.get(0).getBusinessTypeCode();
        if (businessTypeCode == null || businessTypeCode.isBlank()) {
            throw new ServiceException(400, "业务类型不能为空");
        }

        PageResult<EntityDO> pageResult = entityCoreService.pageEntitiesByModelIds(
                businessTypeCode,
                normalizedModelIds,
                null,
                keyword,
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

    private List<Long> collectCandidateEntityIdsByModelIds(List<Long> modelIds, String businessTypeCode) {
        List<Long> normalizedModelIds = normalizeModelIds(modelIds);
        if (normalizedModelIds.isEmpty()) {
            return List.of();
        }

        List<Long> orderedEntityIds = new ArrayList<>();
        for (Long mid : normalizedModelIds) {
            List<Long> ids = entityCoreService.getEntityIdsByModelId(mid, businessTypeCode);
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
            String businessTypeCode,
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
        List<EntityDO> entities = entityCoreService.listByIds(candidateIds, businessTypeCode);
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
            String businessTypeCode,
            List<FieldFilterReqVO> filters,
            String keyword
    ) {
        if (candidateIds == null || candidateIds.isEmpty()) {
            return new ArrayList<>();
        }

        Set<Long> matchedIds = entityFieldQueryEngine.searchAndFilterEntityIds(
                businessTypeCode,
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
        return fetchEntitiesByOrderedIds(orderedMatchedIds, businessTypeCode);
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
    private PageResult<EntityRespVO> pageByOrderedIds(List<Long> orderedIds, String businessTypeCode, Integer pageNo, Integer pageSize) {
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
        return fetchEntityPageByOrderedIds(pagedEntityIds, businessTypeCode);
    }

    private List<EntityRespVO> fetchEntitiesByOrderedIds(List<Long> orderedIds, String businessTypeCode) {
        if (orderedIds == null || orderedIds.isEmpty()) {
            return new ArrayList<>();
        }
        return convertOrderedEntityIdsToRespList(orderedIds, businessTypeCode);
    }

    @Override
    public EntityRespVO getCategoryLinkedEntity(Long categoryId, String businessTypeCode) {
        if (categoryId == null) {
            return null;
        }

        CategoryEntityLinkDO link = categoryEntityLinkService.getLinkByCategoryId(categoryId);
        if (link == null || link.getEntityId() == null) {
            return null;
        }

        EntityDO entity = entityCoreService.get(link.getEntityId(), businessTypeCode);
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
    private List<Long> listOrderedEntityIdsByCategoriesInBusiness(List<Long> categoryIds, String categoryTypeCode, String businessTypeCode) {
        return categoryIds.size() == 1
                ? entityCategoryRelationService.listEntityIdsByCategoryIdWithDescendants(categoryIds.get(0), categoryTypeCode, businessTypeCode)
                : entityCategoryRelationService.listEntityIdsByCategoryIdsWithDescendants(categoryIds, businessTypeCode);
    }

    /** ----------------按有序 entityId 分页结果回查实体详情并保序返回。-----------
     */
    private PageResult<EntityRespVO> fetchEntityPageByOrderedIds(PageResult<Long> pagedEntityIds, String businessTypeCode) {
        if (pagedEntityIds == null || pagedEntityIds.getList() == null || pagedEntityIds.getList().isEmpty()) {
            return new PageResult<>(new ArrayList<>(), pagedEntityIds == null ? 0L : pagedEntityIds.getTotal());
        }
        List<EntityRespVO> result = convertOrderedEntityIdsToRespList(pagedEntityIds.getList(), businessTypeCode);
        return new PageResult<>(result, pagedEntityIds.getTotal());
    }

    /**
     * 将有序实体ID列表转换为VO列表，并保持输入顺序。
     */
    private List<EntityRespVO> convertOrderedEntityIdsToRespList(List<Long> orderedEntityIds, String businessTypeCode) {
        if (orderedEntityIds == null || orderedEntityIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<EntityDO> entities = entityCoreService.listByIds(orderedEntityIds, businessTypeCode);
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
     *   <li>核心字段（id/modelId/status/parentId/businessTypeCode/name）</li>
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
            case "business_type_code", "businessTypeCode" -> entity.getBusinessTypeCode();
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
                reqVO.getBusinessTypeCode(),
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
    public void moveEntity(Long entityId, String businessTypeCode, Long newParentId) {
        entityCoreService.moveEntity(entityId, businessTypeCode, newParentId);
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
            EntityCreateReqVO createReq = new EntityCreateReqVO();
            createReq.setBusinessTypeCode(base.getBusinessTypeCode());
            createReq.setModelId(base.getModelId());

            String name = item.getName();
            if (name == null || name.isBlank()) {
                name = base.getName();
                if (sequenceEnabled && name != null && !name.isBlank()) {
                    int currentNo = startNo + i * step;
                    String seq = padLength > 0
                            ? String.format("%0" + padLength + "d", currentNo)
                            : String.valueOf(currentNo);
                    name = name + separator + seq;
                }
            }
            createReq.setName(name);

            Long parentId = item.getParentId() != null
                    ? item.getParentId()
                    : (scope != null ? scope.getParentEntityId() : base.getParentId());
            createReq.setParentId(parentId);
            createReq.setBaseFields(item.getBaseFields() != null ? item.getBaseFields() : base.getBaseFields());
            createReq.setCustomFields(item.getCustomFields() != null ? item.getCustomFields() : base.getCustomFields());
            createReq.setStatus(item.getStatus() != null ? item.getStatus() : base.getStatus());

            Long id = create(createReq);
            ids.add(id);

            if (scope != null && scope.getCategoryId() != null) {
                entityCategoryRelationService.associate(id, scope.getCategoryId(), base.getBusinessTypeCode());
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
                EntityUpdateReqVO updateReq = new EntityUpdateReqVO();
                updateReq.setId(id);
                updateReq.setBusinessTypeCode(reqVO.getBusinessTypeCode());
                if (reqVO.getStatus() != null) {
                    updateReq.setStatus(reqVO.getStatus());
                }
                if (reqVO.getName() != null) {
                    updateReq.setName(reqVO.getName());
                }
                if (reqVO.getModelId() != null) {
                    updateReq.setModelId(reqVO.getModelId());
                }
                if (reqVO.getParentId() != null) {
                    updateReq.setParentId(reqVO.getParentId());
                }
                if (reqVO.getCustomFields() != null) {
                    updateReq.setCustomFields(reqVO.getCustomFields());
                }
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
    public EntityBatchOperationRespVO batchUpdateStatus(String businessTypeCode, List<Long> ids, Integer status) {
        List<EntityBatchOperationRespVO.FailItem> failItems = new ArrayList<>();
        if (ids == null || ids.isEmpty()) {
            return EntityBatchOperationRespVO.builder()
                    .operationType("UPDATE_STATUS").totalCount(0).successCount(0).failCount(0)
                    .async(false).failItems(failItems).build();
        }

        List<EntityDO> existings = entityCoreService.listByIds(ids, businessTypeCode);
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

        String businessTypeCode = reqVO.getBusinessTypeCode();
        List<Long> ids = reqVO.getIds();
        Boolean forceDelete = reqVO.getForceDelete();

        for (Long id : ids) {
            try {
                EntityDO existing = entityCoreService.get(id, businessTypeCode);
                if (existing == null) {
                    failItems.add(EntityBatchOperationRespVO.FailItem.builder()
                            .entityId(id).reason(ENTITY_NOT_EXISTS).errorCode(404).build());
                    continue;
                }

                if (Boolean.FALSE.equals(forceDelete)
                        && entityCoreService.existsByParentId(id, businessTypeCode)) {
                    failItems.add(EntityBatchOperationRespVO.FailItem.builder()
                            .entityId(id).entityName(existing.getName()).reason("存在子实体，无法删除")
                            .errorCode(400).build());
                    continue;
                }

                entityRelationSyncService.syncRelationsOnDelete(id, businessTypeCode);
                entityCoreService.delete(id, businessTypeCode);
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

        String businessTypeCode = reqVO.getBusinessTypeCode();
        List<Long> ids = reqVO.getIds();
        Long targetCategoryId = reqVO.getTargetCategoryId();
        boolean replaceExisting = Boolean.TRUE.equals(reqVO.getReplaceExisting());

        for (Long id : ids) {
            try {
                if (replaceExisting) {
                    entityCategoryRelationService.updateAssociation(id, List.of(targetCategoryId), businessTypeCode);
                } else {
                    entityCategoryRelationService.associate(id, targetCategoryId, businessTypeCode);
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
                reqVO.getEntityIds(), reqVO.getCategoryId(), reqVO.getBusinessTypeCode());
    }

    /**
     * 批量取消实体与目标分类的关联（不影响实体其它分类关联）。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BatchEntityCategoryAssociationRespVO batchRemoveCategory(EntityBatchCategoryRelationReqVO reqVO) {
        return entityCategoryRelationService.batchDisassociateEntitiesFromCategory(
                reqVO.getEntityIds(), reqVO.getCategoryId(), reqVO.getBusinessTypeCode());
    }

    /**
     * 批量覆盖实体分类集合（用于导入覆盖等场景）。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BatchEntityCategoryAssociationRespVO batchReplaceCategories(EntityBatchReplaceCategoriesReqVO reqVO) {
        return entityCategoryRelationService.batchUpdateAssociation(
                reqVO.getEntityIds(), reqVO.getCategoryIds(), reqVO.getBusinessTypeCode());
    }

    @Override
    public EntityBatchOperationRespVO getBatchOperationPreview(String businessTypeCode, List<Long> ids) {
        List<EntityBatchOperationRespVO.FailItem> previewItems = new ArrayList<>();
        int validCount = 0;

        for (Long id : ids) {
            EntityRespVO entity = null;
            try {
                EntityDO entityDO = entityCoreService.get(id, businessTypeCode);
                if (entityDO != null) {
                    entity = EntityDoVoHelper.toRespVO(entityDO, customFieldValidationService);
                }
            } catch (Exception e) {
                log.debug("获取实体失败: id={}, businessTypeCode={}", id, businessTypeCode);
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
        if (reqVO == null || reqVO.getBusinessTypeCode() == null || reqVO.getBusinessTypeCode().isEmpty()) {
            return aggregation;
        }

        Long[] entityIdsArray = null;
        int entityIdsSize = 0;
        if (filteredEntityIds != null && !filteredEntityIds.isEmpty()) {
            entityIdsArray = filteredEntityIds.toArray(new Long[0]);
            entityIdsSize = entityIdsArray.length;
        }

        try {
            EntityTableNameContext.set(reqVO.getBusinessTypeCode());
            List<EntityAggregationCountDTO<Integer>> statusCounts = entityAggregationMapper.selectStatusCount(
                    reqVO.getModelId(), reqVO.getStatus(), reqVO.getKeyword(), entityIdsArray, entityIdsSize);
            Map<Integer, Long> statusMap = new HashMap<>();
            for (EntityAggregationCountDTO<Integer> row : statusCounts) {
                if (row != null) statusMap.put(row.getKey(), row.getCnt());
            }
            aggregation.setStatusCount(statusMap);

            List<EntityAggregationCountDTO<Long>> modelCounts = entityAggregationMapper.selectModelCount(
                    reqVO.getModelId(), reqVO.getStatus(), reqVO.getKeyword(), entityIdsArray, entityIdsSize);
            Map<Long, Long> modelMap = new HashMap<>();
            for (EntityAggregationCountDTO<Long> row : modelCounts) {
                if (row != null) modelMap.put(row.getKey(), row.getCnt());
            }
            aggregation.setModelCount(modelMap);
        } catch (Exception e) {
            log.warn("构建聚合统计信息失败: businessTypeCode={}, error={}", reqVO.getBusinessTypeCode(), e.getMessage());
        } finally {
            EntityTableNameContext.clear();
        }
        return aggregation;
    }







    /**
     * 高级搜索入口（内部复用分页搜索并封装搜索响应）。
     */
    @Override
    public EntitySearchRespVO searchAdvanced(EntitySearchReqVO reqVO) {
        EntityPageReqVO pageReqVO = new EntityPageReqVO();
        pageReqVO.setBusinessTypeCode(reqVO.getBusinessTypeCode());
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
     * 按模型获取实体树（同层按 sort 排序）。
     * 前提是实体有数型结构的情况下使用，如果实体不支持树形结构，使用实体列表的查询接口
     */
    @Override
    public List<EntityRespVO> getEntityTreeByModelId(String businessTypeCode, Long modelId) {
        List<EntityDO> entities = entityCoreService.listTreeEntities(businessTypeCode, modelId);
        List<EntityRespVO> respVOList = EntityDoVoHelper.toRespVOList(entities, customFieldValidationService);
        // 模型树场景：采用“父节点内局部排序（sort）”策略
        return EntityTreeBuilder.buildTree(respVOList, EntityTreeBuilder.SortMode.LOCAL_SIBLING_SORT);
    }

    /**
     * 获取实体从根到当前节点的路径。
     * 前提是实体有数型结构的情况下使用 （todo:这个待分析是否要优化）
     */

    @Override
    public List<String> getEntityPath(Long entityId, String businessTypeCode) {
        return entityCoreService.getEntityPath(entityId, businessTypeCode);
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
        light.setName(source.getName());
        light.setParentId(source.getParentId());
        light.setModelId(source.getModelId());
        light.setBusinessTypeCode(source.getBusinessTypeCode());
        light.setChildren(applyResultDetail(source.getChildren(), EntityQueryResultDetail.LIGHT));
        return light;
    }

}
