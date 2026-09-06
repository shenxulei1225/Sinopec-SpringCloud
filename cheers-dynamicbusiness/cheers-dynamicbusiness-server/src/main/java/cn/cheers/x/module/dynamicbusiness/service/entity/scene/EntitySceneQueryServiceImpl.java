package cn.cheers.x.module.dynamicbusiness.service.entity.scene;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.*;
import cn.cheers.x.module.dynamicbusiness.convert.entity.EntityDoVoHelper;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryEntityLinkDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeBaseFieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.field.FieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeBaseFieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.CategoryScopedEntityQueryRepository;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.EntityScopedQueryRepository;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.EntityRepository;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.PhysicalColumnFilter;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.KeywordSearchSpec;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.UncategorizedEntityQueryRepository;
import cn.cheers.x.module.dynamicbusiness.framework.entitytype.EntityTypeScopeContext;
import cn.cheers.x.module.dynamicbusiness.framework.facility.FacilityOwningFieldCodes;
import cn.cheers.x.module.dynamicbusiness.service.entity.core.EntityCoreService;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryService;
import cn.cheers.x.module.dynamicbusiness.service.field.CustomFieldValidationService;
import cn.cheers.x.module.dynamicbusiness.enums.entity.EntityQueryScene;
import cn.cheers.x.module.dynamicbusiness.enums.entity.EntityQueryResultDetail;
import cn.cheers.x.module.dynamicbusiness.enums.entity.EntityQueryResultShape;
import cn.cheers.x.module.dynamicbusiness.service.entity.categoryviaref.CategoryViaRefQueryPath;
import cn.cheers.x.module.dynamicbusiness.service.entity.categoryviaref.CategoryViaRefQueryService;
import cn.cheers.x.module.dynamicbusiness.service.entity.refdisplay.EntityRefDisplayEnrichService;
import cn.cheers.x.module.dynamicbusiness.service.model.ModelService;
import cn.cheers.x.module.dynamicbusiness.service.model.relation.ModelEntityRelationService;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryEntityLinkService;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryTypeService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.util.StringUtils;
import jakarta.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityDedicatedColumnService;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityFieldQueryEngine;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityService;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityTableDirectPagingGate;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityTreeBuilder;

/**
 * 实体按场景查询编排实现。
 *
 * <p><b>管什么</b>：按 scene 分发列表/树/详情查询；分类与型号范围、过滤排序、表内直分页、结果粒度。</p>
 * <p><b>不管什么</b>：实体 CRUD、associations 关联块、批量操作与排序保存。</p>
 * <p><b>禁止</b>：读路径补权威数据；详情场景绕过 {@link EntityService#get} 另写一套详情。</p>
 */
@Service
@Validated
public class EntitySceneQueryServiceImpl implements EntitySceneQueryService {

    private static final String ENTITY_NOT_EXISTS = "实体不存在";

    /** 实体表上可直接 ORDER BY 的列：name / code / status / id / sort */
    private static final Set<String> CORE_ORDER_BY_COLUMNS = Set.of("name", "code", "status", "id", "sort");

    @Resource
    private EntityCoreService entityCoreService;
    @Resource
    private EntityFieldQueryEngine entityFieldQueryEngine;
    @Resource
    private ModelMapper modelMapper;
    @Resource
    private EntityRepository entityRepository;
    @Resource
    private CustomFieldValidationService customFieldValidationService;
    @Resource
    private FieldMapper fieldMapper;
    @Resource
    @Lazy
    private ModelService modelService;
    @Resource
    private ModelEntityRelationService modelEntityRelationService;
    @Resource
    private EntityRefDisplayEnrichService entityRefDisplayEnrichService;
    @Resource
    private EntityDedicatedColumnService entityDedicatedColumnService;
    @Resource
    private CategoryTypeService categoryTypeService;
    @Resource
    private CategoryEntityLinkService categoryEntityLinkService;
    @Resource
    private EntityTypeMapper entityTypeMapper;
    @Resource
    private EntityTypeBaseFieldMapper entityTypeBaseFieldMapper;
    @Resource
    @Lazy
    private CategoryService categoryService;
    @Resource
    private EntityScopedQueryRepository entityScopedQueryRepository;
    @Resource
    private UncategorizedEntityQueryRepository uncategorizedEntityQueryRepository;
    @Resource
    private CategoryScopedEntityQueryRepository categoryScopedEntityQueryRepository;
    @Resource
    private CategoryViaRefQueryService categoryViaRefQueryService;

    @Resource
    @Lazy
    private EntityService entityService;

    /**
     * 阅读索引（按职责）：
     * 1) queryEntities：唯一入口（scene 分发）
     * 2) listByCategory / listByModel / listUncategorized / listByCategoryViaRef：按场景圈实体ID
     * 3) pageByType / pageByModel：库内快路径（可下推时）
     * 4) listByScopedEntityIds / listByOrderedEntityIds / queryByOrderedEntityIds：慢路径共用收尾
     * 5) convertOrderedEntityIdsToRespList / applyResultDetail / toLightRespVO：结果转换
     */
    private record ResolvedQueryType(String entityTypeCode, String domain, String scopeRegistryCode) {
    }

    private record FacilityFilterPeel(
            PhysicalColumnFilter physical, List<FieldFilterReqVO> remainingFilters) {
    }

    /**
     * {@link #prepareEntityTableDirectPaging} 的组装结果：进门判定 + 已解析的筛/序/词，供直分页调用。
     */
    private record PreparedEntityTablePaging(
            EntityTableDirectPagingGate.Decision decision,
            List<PhysicalColumnFilter> pushFilters,
            String dbOrderColumn,
            KeywordSearchSpec keywordSearch,
            boolean hasScope
    ) {
    }

    /**
     * scene 总入口（所有前端 query-by-scene 都从这里进）。
     *
     * <p><b>解决的问题</b>：把同一套查询参数路由到不同 scene，避免前端拆多套接口。</p>
     * <p><b>必要步骤</b>：分页/排序归一、类型归一、搜索范围归一，然后再分发 scene。</p>
     * <p><b>可选步骤</b>：仅在请求带排序/关键词/筛选时才启用对应链路。</p>
     *
     * <p><b>Scene 调用链总览（必要步骤 / 可选步骤）</b></p>
     * <ul>
     *   <li><b>ENTITIES_BY_CATEGORY（按分类）</b><br/>
     *       必要：resolveQueryEntityType -> validateOrderByColumn(有排序时) -> respondShape。<br/>
     *       分支A（直接分类）：listByCategory（必要）。<br/>
     *       分支B（via-ref）：listByCategoryViaRef（当 categoryViaRefPathCode 非空时可选，命中后替代分支A）。</li>
     *   <li><b>ENTITIES_BY_MODEL（按型号）</b><br/>
     *       必要：resolveQueryEntityType -> validateOrderByColumn(有排序时) -> respondShape。<br/>
     *       可选1：shouldResolveModelIdsFromCategory + resolveModelIdsFromCategory（仅 modelIds 为空且有分类过滤意图）。<br/>
     *       可选2：isCrossTypeModelQuery -> listByCrossTypeModel（仅跨类型型号场景）。<br/>
     *       可选3：getEntityTreeByModelId（仅 TREE + 单 modelId + 无 domain/scope + sort 树捷径）。<br/>
     *       常规：listByModel（有 modelIds）；或 collectCandidateEntityIdsByType + listByOrderedEntityIds（无 modelIds 且走慢路径）。<br/>
     *       快路径：prepareEntityTableDirectPaging -> pageByType（仅 PAGE 且判定可直分页）。</li>
     *   <li><b>ENTITIES_UNCATEGORIZED（未分类）</b><br/>
     *       必要：resolveQueryEntityType -> validateOrderByColumn(有排序时) -> listUncategorized -> respondShape。<br/>
     *       可选：listUncategorized 内部可走 SQL 直分页，否则走慢路径收尾。</li>
     *   <li><b>ENTITIES_BY_CATEGORY_LINK（分类绑定单实体）</b><br/>
     *       必要：getCategoryLinkedEntity -> respondOne。</li>
     *   <li><b>ENTITIES_DETAIL（实体详情）</b><br/>
     *       必要：entityService.get -> respondOne。<br/>
     *       说明：该场景是单对象读取，不走列表快慢路径。</li>
     * </ul>
     */

    @Override
    public EntitySceneQueryRespVO queryEntities(EntityQueryScene scene, String resultShape, String resultDetail, String categoryTypeCode, String entityTypeCode,
            List<Long> modelIds, String modelEntityTypeCode, List<Long> categoryIds, List<CategoryIdGroupReqVO> categoryIdGroups, String categoryViaRefPathCode,
            String categoryFilterMode,
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
        // 未传排序列时：PAGE 列表默认按名称；自定义顺序由前端显式传 orderByColumn=sort（实体表物理列）
        String normalizedOrderByColumn = normalizeOrderByColumn(orderByColumn);
        if (!StringUtils.hasText(normalizedOrderByColumn) && shape == EntityQueryResultShape.PAGE) {
            normalizedOrderByColumn = "name";
        }
        boolean orderAsc = isAsc == null || Boolean.TRUE.equals(isAsc);
        /**
         * 树形结果：未指定序或按实体表 sort 时，走「兄弟自定义序」树构建捷径；
         * 按 name/code 等字段序时不走该捷径（须按字段排平铺再组树）。
         * 注意：这只影响树怎么组，禁止再拿来否决列表表内分页。
         */
        boolean treeUsesEntitySortOrder =
                !StringUtils.hasText(normalizedOrderByColumn)
                        || "sort".equals(normalizedOrderByColumn);

        // 按入口解析：SCOPE → storage + 成员过滤；DOMAIN registry → storage + domain；其余用请求 domain
        ResolvedQueryType resolved = resolveQueryEntityType(entityTypeCode, domain);
        String storageEntityTypeCode = resolved.entityTypeCode();
        String normalizedDomain = resolved.domain();
        String normalizedScopeCode = resolved.scopeRegistryCode();
        KeywordSearchSpec keywordSearch = resolveKeywordSearchSpec(storageEntityTypeCode, searchFieldCodes);
        boolean pageShape = shape == EntityQueryResultShape.PAGE;
        Integer scenePageNo = pageShape ? effectivePageNo : null;
        Integer scenePageSize = pageShape ? effectivePageSize : null;

        switch (scene) {
            case ENTITIES_BY_CATEGORY: {
                String queryEntityTypeCode = resolved.entityTypeCode();
                if (queryEntityTypeCode == null || queryEntityTypeCode.isBlank()) {
                    throw new ServiceException(400, "ENTITIES_BY_CATEGORY 场景下 entityTypeCode 不能为空");
                }
                if (StringUtils.hasText(normalizedOrderByColumn)) {
                    validateOrderByColumn(queryEntityTypeCode, normalizedOrderByColumn);
                }
                String fieldOrderColumn = normalizedOrderByColumn;
                String viaRefPathCode = trimToNull(categoryViaRefPathCode);
                if (viaRefPathCode != null) {
                    PageResult<EntityRespVO> viaRefPage = listByCategoryViaRef(
                            viaRefPathCode, categoryTypeCode, categoryIds, queryEntityTypeCode,
                            normalizedDomain, normalizedScopeCode, keyword, filters,
                            scenePageNo, scenePageSize,
                            fieldOrderColumn, orderAsc);
                    return respondShape(shape, viaRefPage, detail);
                }
                // 禁止用 storage 冒充分类种类：同源也须显式传 categoryTypeCode，避免跨视角漏传时查错树
                if (categoryTypeCode == null || categoryTypeCode.isBlank()) {
                    throw new ServiceException(400, "ENTITIES_BY_CATEGORY 场景下 categoryTypeCode 不能为空");
                }
                PageResult<EntityRespVO> categoryPage = listByCategory(
                        categoryIds, categoryIdGroups, categoryTypeCode.trim(), storageEntityTypeCode, modelIds,
                        keyword, filters, scenePageNo, scenePageSize, pageShape,
                        normalizedDomain, normalizedScopeCode, fieldOrderColumn, orderAsc, keywordSearch, detail);
                return respondShape(shape, categoryPage, detail);
            }

            case ENTITIES_BY_MODEL: {
                if (StringUtils.hasText(normalizedOrderByColumn)) {
                    validateOrderByColumn(storageEntityTypeCode, normalizedOrderByColumn);
                }
                List<Long> normalizedModelIds = normalizeModelIds(modelIds);
                if (normalizedModelIds.isEmpty() && shouldResolveModelIdsFromCategory(categoryIds, categoryFilterMode)) {
                    normalizedModelIds = resolveModelIdsFromCategory(
                            categoryIds, categoryTypeCode, categoryFilterMode,
                            modelEntityTypeCode, storageEntityTypeCode, normalizedDomain);
                    if (normalizedModelIds.isEmpty()) {
                        return respondShape(shape, new PageResult<>(new ArrayList<>(), 0L), detail);
                    }
                }
                if (!normalizedModelIds.isEmpty()) {
                    // 场景 8 跨类型：modelIds 为外类型型号；本类实体经挂钩表取 id，再装本类实体（含 name）。
                    // 禁止用外类型 modelId 筛本类实体表 model_id（本类 model_id 只表示本类型号归属）。
                    if (isCrossTypeModelQuery(modelEntityTypeCode, storageEntityTypeCode)) {
                        PageResult<EntityRespVO> relatedPage = listByCrossTypeModel(
                                normalizedModelIds, modelEntityTypeCode.trim(), storageEntityTypeCode,
                                keyword, filters,
                                shape == EntityQueryResultShape.PAGE ? effectivePageNo : 1,
                                shape == EntityQueryResultShape.PAGE ? effectivePageSize : fullPageSize,
                                normalizedDomain, normalizedScopeCode,
                                normalizedOrderByColumn, orderAsc, keywordSearch);
                        return respondShape(shape, relatedPage, detail);
                    }
                    if (shape == EntityQueryResultShape.TREE && normalizedModelIds.size() == 1
                            && !StringUtils.hasText(normalizedDomain) && !StringUtils.hasText(normalizedScopeCode)
                            && treeUsesEntitySortOrder) {
                        return EntitySceneQueryRespVO.tree(
                                applyResultDetail(getEntityTreeByModelId(storageEntityTypeCode, normalizedModelIds.get(0)), detail),
                                detail.getCode());
                    }
                    PageResult<EntityRespVO> modelPage = listByModel(
                            normalizedModelIds, storageEntityTypeCode, keyword, filters,
                            shape == EntityQueryResultShape.PAGE ? effectivePageNo : 1,
                            shape == EntityQueryResultShape.PAGE ? effectivePageSize : fullPageSize,
                            normalizedDomain, normalizedScopeCode,
                            normalizedOrderByColumn, orderAsc, keywordSearch, detail);
                    return respondShape(shape, modelPage, detail);
                }
                // 未传 modelIds：按类型范围（+ Domain / 划分）
                if (shape == EntityQueryResultShape.TREE && treeUsesEntitySortOrder) {
                    List<EntityRespVO> treeRoots = buildEntityHierarchySubtree(
                            storageEntityTypeCode, null, keyword, filters, detail,
                            effectivePageNo, resolveTreeRootPageSize(pageSize));
                    return EntitySceneQueryRespVO.tree(applyResultDetail(treeRoots, detail), detail.getCode());
                }
                // 类型全量：统一进门（筛可下推 + 词可下推 → 实体表直分页；序只决定 ORDER BY）
                PreparedEntityTablePaging typePaging = prepareEntityTableDirectPaging(
                        storageEntityTypeCode, filters, keyword, keywordSearch,
                        normalizedOrderByColumn, normalizedScopeCode);
                if (shape == EntityQueryResultShape.PAGE && typePaging.decision().canPageOnEntityTable()) {
                    EntityTableDirectPagingGate.Decision gate = typePaging.decision();
                    PageResult<EntityRespVO> typedPage = pageByType(
                            storageEntityTypeCode, keyword, normalizedDomain,
                            effectivePageNo, effectivePageSize,
                            gate.effectiveDbOrderColumn(), orderAsc, typePaging.pushFilters(),
                            typePaging.keywordSearch(), detail,
                            normalizedScopeCode);
                    return EntitySceneQueryRespVO.page(applyResultDetail(typedPage, detail), detail.getCode());
                }
                return respondShape(shape, listByOrderedEntityIds(
                        collectCandidateEntityIdsByType(
                                storageEntityTypeCode, modelIds, normalizedDomain, normalizedScopeCode),
                        storageEntityTypeCode, keyword, filters, keywordSearch,
                        normalizedOrderByColumn, orderAsc,
                        shape == EntityQueryResultShape.PAGE ? effectivePageNo : null,
                        shape == EntityQueryResultShape.PAGE ? effectivePageSize : null,
                        detail), detail);
            }

            case ENTITIES_UNCATEGORIZED: {
                String queryEntityTypeCode = resolved.entityTypeCode();
                if (queryEntityTypeCode == null || queryEntityTypeCode.isBlank()) {
                    throw new ServiceException(400, "ENTITIES_UNCATEGORIZED 场景下 entityTypeCode 不能为空");
                }
                if (categoryTypeCode == null || categoryTypeCode.isBlank()) {
                    throw new ServiceException(400, "ENTITIES_UNCATEGORIZED 场景下 categoryTypeCode 不能为空");
                }
                if (StringUtils.hasText(normalizedOrderByColumn)) {
                    validateOrderByColumn(queryEntityTypeCode, normalizedOrderByColumn);
                }
                String fieldOrderColumn = normalizedOrderByColumn;
                PageResult<EntityRespVO> uncategorizedPage = listUncategorized(
                        categoryTypeCode.trim(), storageEntityTypeCode, modelIds,
                        keyword, filters,
                        shape == EntityQueryResultShape.PAGE ? effectivePageNo : null,
                        shape == EntityQueryResultShape.PAGE ? effectivePageSize : null,
                        shape == EntityQueryResultShape.PAGE,
                        normalizedDomain, normalizedScopeCode, fieldOrderColumn, orderAsc);
                return respondShape(shape, uncategorizedPage, detail);
            }

            case ENTITIES_BY_CATEGORY_LINK: {
                if (categoryIds == null || categoryIds.isEmpty() || categoryIds.get(0) == null) {
                    throw new ServiceException(400, "ENTITIES_BY_CATEGORY_LINK 场景下 categoryIds 不能为空");
                }
                EntityRespVO linked = getCategoryLinkedEntity(categoryIds.get(0), storageEntityTypeCode);
                if (linked == null) {
                    throw new ServiceException(404, ENTITY_NOT_EXISTS);
                }
                return respondOne(shape, linked, detail);
            }

            case ENTITIES_DETAIL: {
                EntityRespVO detailVo = entityService.get(entityId, storageEntityTypeCode, true, null);
                if (detailVo == null) {
                    throw new ServiceException(404, ENTITY_NOT_EXISTS);
                }
                return respondOne(shape, detailVo, detail);
            }
            default:
                throw new ServiceException(400, "不支持的查询场景: " + scene.getCode());
        }
    }

    // ==================== 场景参数归一（类型/分类/型号） ====================
    /**
     * 将请求入口解析为“实际查询类型 + 业务域 + 划分入口编码”。
     *
     * <p><b>解决的问题</b>：同一个入口可能是 SCOPE/DOMAIN/REUSE，不先归一会查错范围。</p>
     * <p><b>必要步骤</b>：所有 scene 都要先经过这里，后续查询统一使用归一结果。</p>
     * <p><b>可选步骤</b>：仅 DOMAIN 入口时校验请求 domain 与入口 domain 一致。</p>
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

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    /** 规范化模型 ID：去 null、去重（保留顺序）。 */
    private List<Long> normalizeModelIds(List<Long> modelIds) {
        if (modelIds == null || modelIds.isEmpty()) {
            return List.of();
        }
        return modelIds.stream().filter(Objects::nonNull).distinct().toList();
    }

    /**
     * 判断 ENTITIES_BY_MODEL 是否需要“先按分类反查 modelIds”。
     *
     * <p><b>解决的问题</b>：前端未传 modelIds 时，仍能按分类语义补齐型号范围。</p>
     * <p><b>必要步骤</b>：仅 modelIds 为空时进入；有 modelIds 则不是必要步骤。</p>
     */
    private boolean shouldResolveModelIdsFromCategory(List<Long> categoryIds, String categoryFilterMode) {
        if (!normalizeModelIds(categoryIds).isEmpty()) {
            return true;
        }
        String mode = trimToNull(categoryFilterMode);
        return "CATEGORIZED".equalsIgnoreCase(mode) || "UNCATEGORIZED".equalsIgnoreCase(mode);
    }

    /**
     * 根据分类条件反查型号 ID，供 ENTITIES_BY_MODEL 主链继续查实体。
     *
     * <p><b>解决的问题</b>：把分类过滤意图转换成型号集合，避免后续实体查数无范围。</p>
     * <p><b>必要步骤</b>：仅在需要按分类补 modelIds 时调用。</p>
     * <p><b>可选步骤</b>：按 mode 走 CATEGORIZED / UNCATEGORIZED / 通用分类分支。</p>
     */
    private List<Long> resolveModelIdsFromCategory(List<Long> categoryIds,
                                                        String categoryTypeCode,
                                                        String categoryFilterMode,
                                                        String modelEntityTypeCode,
                                                        String storageEntityTypeCode,
                                                        String domain) {
        String expandTypeCode = StringUtils.hasText(modelEntityTypeCode)
                ? modelEntityTypeCode.trim()
                : storageEntityTypeCode;
        if (!StringUtils.hasText(expandTypeCode)) {
            return List.of();
        }
        String mode = trimToNull(categoryFilterMode);
        if ("CATEGORIZED".equalsIgnoreCase(mode)) {
            if (!StringUtils.hasText(categoryTypeCode)) {
                return List.of();
            }
            List<ModelRespVO> models = modelService.listCategorizedModelsByCategoryType(
                    categoryTypeCode.trim(), expandTypeCode, domain);
            return extractModelIds(models);
        }
        if ("UNCATEGORIZED".equalsIgnoreCase(mode)) {
            if (!StringUtils.hasText(categoryTypeCode)) {
                return List.of();
            }
            List<ModelRespVO> models = modelService.listUncategorizedModelsByCategoryType(
                    categoryTypeCode.trim(), expandTypeCode, domain);
            return extractModelIds(models);
        }
        List<Long> normalizedCategoryIds = normalizePositiveCategoryIds(categoryIds);
        if (normalizedCategoryIds.isEmpty()) {
            return List.of();
        }
        PageResult<Long> ordered = modelService.queryOrderedModelIdsByCategoriesInBusiness(
                normalizedCategoryIds,
                StringUtils.hasText(categoryTypeCode) ? categoryTypeCode.trim() : null,
                expandTypeCode,
                null,
                null);
        return ordered.getList() != null ? ordered.getList() : List.of();
    }

    private List<Long> extractModelIds(List<ModelRespVO> models) {
        if (models == null || models.isEmpty()) {
            return List.of();
        }
        LinkedHashSet<Long> out = new LinkedHashSet<>();
        for (ModelRespVO model : models) {
            if (model != null && model.getId() != null && model.getId() > 0) {
                out.add(model.getId());
            }
        }
        return new ArrayList<>(out);
    }

    // ==================== 场景实体ID圈定（分类/未分类/型号） ====================
    /**
     * 生成 ENTITIES_BY_MODEL 候选实体 ID（未传 modelIds 时使用）。
     *
     * <p><b>解决的问题</b>：统一“按类型全量取数”与“按型号限定取数”的候选 ID 收集口径。</p>
     * <p><b>必要步骤</b>：仅该分支进入慢路径时必要。</p>
     */
    private List<Long> collectCandidateEntityIdsByType(String entityTypeCode, List<Long> modelIds,
                                                              String domain, String scopeRegistryCode) {
        List<Long> normalizedModelIds = normalizeModelIds(modelIds);
        List<Long> candidates;
        if (normalizedModelIds.isEmpty()) {
            candidates = listAllEntityIdsByType(entityTypeCode, domain);
        } else {
            candidates = collectEntityIdsByModelIds(normalizedModelIds, entityTypeCode);
            candidates = entityScopedQueryRepository.retainOrderedIdsByDomainAndScope(
                    candidates, entityTypeCode, domain, null);
        }
        return entityScopedQueryRepository.retainOrderedIdsByDomainAndScope(
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
     * ENTITIES_UNCATEGORIZED 主链。
     *
     * <p><b>解决的问题</b>：返回“未挂当前分类体系”的实体集合。</p>
     * <p><b>必要步骤</b>：先确定未分类 ID 集，再进入统一收尾链路。</p>
     * <p><b>可选步骤</b>：满足下推条件时可直接 SQL 分页。</p>
     */
    private PageResult<EntityRespVO> listUncategorized(
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
        boolean hasKeyword = StringUtils.hasText(keyword);
        boolean hasFilters = filters != null && !filters.isEmpty();
        List<PhysicalColumnFilter> pushFilters = resolvePushablePhysicalFilters(entityTypeCode, filters);
        boolean allFiltersPushable = !hasFilters || pushFilters != null;
        List<PhysicalColumnFilter> sqlPhysicalFilters =
                pushFilters != null ? pushFilters : List.of();

        if (allowDirectPaging && !hasKeyword && allFiltersPushable
                && StringUtils.hasText(dbOrder)
                && uncategorizedEntityQueryRepository.supportsSqlOrder(dbOrder)) {
            Integer pn = normalizePageNo(pageNo);
            Integer ps = normalizePageSize(pageSize);
            return toRespPage(uncategorizedEntityQueryRepository.pageUncategorizedEntityIds(
                    categoryTypeCode, entityTypeCode, modelIds, domain, scopeRegistryCode,
                    sqlPhysicalFilters,
                    dbOrder, orderAsc, pn, ps), entityTypeCode, EntityQueryResultDetail.FULL);
        }

        // 慢路径：先取未分类 id（可下推 fieldFilters 已进 SQL），再 keyword / 不可下推筛选
        List<Long> uncategorizedIds = uncategorizedEntityQueryRepository.listUncategorizedEntityIds(
                categoryTypeCode, entityTypeCode, modelIds, domain, scopeRegistryCode,
                sqlPhysicalFilters);
        if (uncategorizedIds.isEmpty()) {
            return new PageResult<>(new ArrayList<>(), 0L);
        }
        List<FieldFilterReqVO> remainingFilters = allFiltersPushable ? null : filters;
        return listByOrderedEntityIds(
                uncategorizedIds, entityTypeCode, keyword, remainingFilters, null,
                orderByColumn, orderAsc, pageNo, pageSize, EntityQueryResultDetail.FULL);
    }

    /**
     * ENTITIES_BY_CATEGORY 主链（支持多组分类求交）。
     *
     * <p><b>解决的问题</b>：把分类选择（含子树）转成实体集合，并统一输出分页/列表/树数据。</p>
     * <p><b>必要步骤</b>：分类组展开 -> 候选实体 ID 获取 -> 统一收尾。</p>
     * <p><b>可选步骤</b>：满足下推条件时走 SQL 快路径。</p>
     */
    private PageResult<EntityRespVO> listByCategory(List<Long> categoryIds,
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
                categoryIds, categoryIdGroups, categoryTypeCode, true);
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
            return toRespPage(categoryScopedEntityQueryRepository.pageEntityIdsByIntersectingCategoryGroups(
                    expandedGroups, entityTypeCode, normalizeModelIds(modelIds),
                    domain, scopeRegistryCode, dbOrder, orderAsc, pushFilters, keyword,
                    effectiveKeywordSearch, pn, ps), entityTypeCode, effectiveDetail);
        }

        List<Long> orderedCandidateEntityIds;
        if (expandedGroups.size() == 1) {
            orderedCandidateEntityIds = entityScopedQueryRepository.listOrderedEntityIdsByCategoryScope(
                    expandedGroups.get(0), entityTypeCode, normalizeModelIds(modelIds), domain, scopeRegistryCode);
        } else {
            orderedCandidateEntityIds = entityScopedQueryRepository.listOrderedEntityIdsByIntersectingCategoryGroups(
                    expandedGroups, entityTypeCode, normalizeModelIds(modelIds), domain, scopeRegistryCode);
        }

        return listByOrderedEntityIds(
                orderedCandidateEntityIds, entityTypeCode, keyword, filters, effectiveKeywordSearch,
                orderByColumn, orderAsc, pageNo, pageSize, effectiveDetail);
    }

    /**
     * ENTITIES_BY_CATEGORY 的 via-ref 变体链路。
     *
     * <p><b>解决的问题</b>：分类不是直接关联主体实体时，通过 ref 路径先拿主体实体 ID。</p>
     * <p><b>必要步骤</b>：校验路径契约（主体类型/维度分类类型）后再查主体 ID。</p>
     * <p><b>可选步骤</b>：后续排序/筛选/分页由统一收尾处理。</p>
     */
    private PageResult<EntityRespVO> listByCategoryViaRef(
            String categoryViaRefPathCode,
            String categoryTypeCode,
            List<Long> categoryIds,
            String subjectEntityTypeCode,
            String domain,
            String scopeRegistryCode,
            String keyword,
            List<FieldFilterReqVO> filters,
            Integer pageNo,
            Integer pageSize,
            String orderByColumn,
            boolean orderAsc) {
        categoryViaRefQueryService.assertSubjectTypeMatches(categoryViaRefPathCode, subjectEntityTypeCode);
        categoryViaRefQueryService.assertDimensionCategoryTypeMatches(categoryViaRefPathCode, categoryTypeCode);
        CategoryViaRefQueryPath path = categoryViaRefQueryService.requirePath(categoryViaRefPathCode);
        List<Long> normalizedCategoryIds = normalizeCategoryIdsOrRoot(
                categoryIds, path.dimensionCategoryTypeCode());
        List<Long> subjectEntityIds = categoryViaRefQueryService.listSubjectEntityIds(
                categoryViaRefPathCode, normalizedCategoryIds);
        return listByScopedEntityIds(
                subjectEntityIds, subjectEntityTypeCode, domain, scopeRegistryCode,
                keyword, filters, null,
                orderByColumn, orderAsc, pageNo, pageSize, EntityQueryResultDetail.FULL);
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
                        ? normalizeCategoryIdsOrRoot(group.getCategoryIds(), groupTypeCode)
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
                ? normalizeCategoryIdsOrRoot(categoryIds, categoryTypeCode)
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

    private List<Long> listAllEntityIdsByType(String entityTypeCode, String domain) {
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

    // ==================== 排序规则与慢路径收尾 ====================

    private static String normalizeOrderByColumn(String orderByColumn) {
        if (orderByColumn == null || orderByColumn.isBlank()) {
            return null;
        }
        return orderByColumn.trim();
    }

    /**
     * 校验排序列是否合法（scene 列表主链通用）。
     *
     * <p><b>解决的问题</b>：拦住不可排序字段，避免 SQL 与慢路径排序口径不一致。</p>
     * <p><b>必要步骤</b>：请求传了 orderByColumn 就必须执行。</p>
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
     * 分页前按字段重排候选 ID；空列或 sort 原样返回。仅核心列与可排序基础字段（扩展字段已被校验拒绝）。
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
        // 列表禁止扩展字段序（validateOrderByColumn 已拒绝）；未知列保持候选原序
        return Map.of();
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

    // ==================== 慢路径共用收尾（排序/筛选/分页/转VO） ====================
    /**
     * 所有 scene 的慢路径共用收尾。
     *
     * <p><b>解决的问题</b>：统一处理字段序、关键词、筛选、分页、VO 转换，避免重复实现。</p>
     * <p><b>必要步骤</b>：前置必须已拿到候选实体 ID（dynamic_entity.id）。</p>
     * <p><b>可选步骤</b>：SQL 能直分页时不应进入该方法。</p>
     */
    private PageResult<EntityRespVO> listByOrderedEntityIds(
            List<Long> orderedCandidateEntityIds,
            String entityTypeCode,
            String keyword,
            List<FieldFilterReqVO> filters,
            KeywordSearchSpec keywordSearch,
            String orderByColumn,
            boolean orderAsc,
            Integer pageNo,
            Integer pageSize,
            EntityQueryResultDetail detail) {
        if (orderedCandidateEntityIds == null || orderedCandidateEntityIds.isEmpty()) {
            return new PageResult<>(new ArrayList<>(), 0L);
        }
        List<Long> ordered = applyFieldOrderToCandidateIds(
                orderedCandidateEntityIds, entityTypeCode, orderByColumn, orderAsc);
        return queryByOrderedEntityIds(
                ordered, entityTypeCode, keyword, filters, pageNo, pageSize, keywordSearch, detail);
    }

    /**
     * 候选实体ID先按 domain/scope 收窄，再走统一列表收尾。
     *
     * <p>这里的“候选实体ID”指的是：各 scene 前置查询已经圈定出的 dynamic_entity.id 列表，
     * 例如「按分类查到的实体ID」「按型号查到的实体ID」。</p>
     */
    private PageResult<EntityRespVO> listByScopedEntityIds(
            List<Long> candidateEntityIds,
            String entityTypeCode,
            String domain,
            String scopeRegistryCode,
            String keyword,
            List<FieldFilterReqVO> filters,
            KeywordSearchSpec keywordSearch,
            String orderByColumn,
            boolean orderAsc,
            Integer pageNo,
            Integer pageSize,
            EntityQueryResultDetail detail) {
        List<Long> scopedIds = entityScopedQueryRepository.retainOrderedIdsByDomainAndScope(
                candidateEntityIds, entityTypeCode, domain, scopeRegistryCode);
        return listByOrderedEntityIds(
                scopedIds, entityTypeCode, keyword, filters, keywordSearch,
                orderByColumn, orderAsc, pageNo, pageSize, detail);
    }

    /**
     * 对“已排序候选实体 ID”执行关键词/筛选/分页并返回 VO。
     *
     * <p><b>解决的问题</b>：把慢路径最终数据裁剪逻辑集中，确保各 scene 行为一致。</p>
     * <p><b>必要步骤</b>：所有进入慢路径的 scene 都会经过这里。</p>
     * <p><b>可选步骤</b>：无关键词且无筛选时可直接切页回查。</p>
     */
    private PageResult<EntityRespVO> queryByOrderedEntityIds(List<Long> orderedCandidateEntityIds,
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
            List<Long> filteredIds = filterCandidateEntityIds(
                    orderedCandidateEntityIds, entityTypeCode, filters, keyword, keywordSearch);
            searchedAndFilteredEntities = convertOrderedEntityIdsToRespList(
                    filteredIds, entityTypeCode, effectiveDetail);
        }
        if (pageNo == null || pageSize == null) {
            return new PageResult<>(searchedAndFilteredEntities, (long) searchedAndFilteredEntities.size());
        }
        Integer pn = normalizePageNo(pageNo);
        Integer ps = normalizePageSize(pageSize);
        int from = (pn - 1) * ps;
        if (from >= searchedAndFilteredEntities.size()) {
            return new PageResult<>(new ArrayList<>(), (long) searchedAndFilteredEntities.size());
        }
        int to = Math.min(from + ps, searchedAndFilteredEntities.size());
        return new PageResult<>(new ArrayList<>(searchedAndFilteredEntities.subList(from, to)),
                (long) searchedAndFilteredEntities.size());
    }

    /**
     * ENTITIES_BY_MODEL 主链（按型号查实体）。
     *
     * <p><b>解决的问题</b>：统一型号场景的快慢路径分流，并叠加 domain/scope/筛选/排序。</p>
     * <p><b>必要步骤</b>：先调用 {@link #prepareEntityTableDirectPaging} 做可下推判定。</p>
     * <p><b>可选步骤</b>：可直分页走 {@link #pageByModel}，否则走候选 ID 慢路径。</p>
     */
    private PageResult<EntityRespVO> listByModel(List<Long> modelIds, String entityTypeCode,
                                                                    String keyword, List<FieldFilterReqVO> filters,
                                                                    Integer pageNo, Integer pageSize,
                                                                    String domain, String scopeRegistryCode,
                                                                    String orderByColumn, boolean orderAsc,
                                                                    KeywordSearchSpec keywordSearch,
                                                                    EntityQueryResultDetail detail) {
        PreparedEntityTablePaging paging = prepareEntityTableDirectPaging(
                entityTypeCode, filters, keyword, keywordSearch, orderByColumn, scopeRegistryCode);
        EntityQueryResultDetail effectiveDetail =
                detail != null ? detail : EntityQueryResultDetail.FULL;
        if (paging.decision().canPageOnEntityTable()) {
            EntityTableDirectPagingGate.Decision gate = paging.decision();
            return pageByModel(
                    modelIds, keyword, pageNo, pageSize, domain,
                    gate.effectiveDbOrderColumn(), orderAsc, paging.pushFilters(),
                    paging.keywordSearch(), effectiveDetail,
                    scopeRegistryCode);
        }
        List<Long> candidateIds = collectEntityIdsByModelIds(modelIds, entityTypeCode);
        return listByScopedEntityIds(
                candidateIds, entityTypeCode, domain, scopeRegistryCode,
                keyword, filters, paging.keywordSearch(),
                orderByColumn, orderAsc, pageNo, pageSize, effectiveDetail);
    }

    /**
     * 判断是否跨类型型号查询（场景8）。
     *
     * <p><b>解决的问题</b>：区分“本类型 model_id 直查”与“外类型型号挂钩反查”两条链路。</p>
     */
    private boolean isCrossTypeModelQuery(String modelEntityTypeCode, String entityTypeCode) {
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
     * 跨类型型号查询主链：外类型 modelIds -> 本类型实体 IDs。
     *
     * <p><b>解决的问题</b>：型号上下文与实体类型不一致时，避免错误使用本表 model_id 过滤。</p>
     * <p><b>必要步骤</b>：先查挂钩关系，再进入统一收尾。</p>
     */
    private PageResult<EntityRespVO> listByCrossTypeModel(
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
        ResolvedQueryType modelResolved = resolveQueryEntityType(modelEntityTypeCode, null);
        String modelStorage = modelResolved.entityTypeCode();
        if (!StringUtils.hasText(modelStorage)) {
            throw new ServiceException(400, "modelEntityTypeCode 无法解析为存储类型");
        }
        List<Long> candidateIds = modelEntityRelationService.listEntityIdsByModelIds(
                modelIds, modelStorage, entityTypeCode);
        return listByScopedEntityIds(
                candidateIds, entityTypeCode, domain, scopeRegistryCode,
                keyword, filters, keywordSearch,
                orderByColumn, orderAsc, pageNo, pageSize, EntityQueryResultDetail.FULL);
    }

    /**
     * 按型号 SQL 直分页执行器（快路径）。
     *
     * <p><b>解决的问题</b>：能下推时在数据库内完成过滤/排序/分页，避免慢路径全量处理。</p>
     * <p><b>必要步骤</b>：仅 gate 判定可直分页时调用。</p>
     */
    private PageResult<EntityRespVO> pageByModel(List<Long> modelIds,
                                                                String keyword, Integer pageNo, Integer pageSize,
                                                                String domain, String orderByColumn, boolean orderAsc,
                                                                List<PhysicalColumnFilter> physicalFilters,
                                                                KeywordSearchSpec keywordSearch,
                                                                EntityQueryResultDetail detail,
                                                                String scopeRegistryCode) {
        List<Long> normalizedModelIds = requireModelIds(modelIds, "modelIds 不能为空");
        String normalizedDomain = EntityTypeScopeContext.normalizeDomain(domain);
        String coreOrder = StringUtils.hasText(orderByColumn) ? orderByColumn.trim() : null;
        KeywordSearchSpec effectiveKeywordSearch =
                keywordSearch != null ? keywordSearch : KeywordSearchSpec.nameOnly();
        EntityQueryResultDetail effectiveDetail =
                detail != null ? detail : EntityQueryResultDetail.FULL;
        String normalizedScope =
                StringUtils.hasText(scopeRegistryCode) ? scopeRegistryCode.trim() : null;

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
                    .scopeRegistryCode(normalizedScope)
                    .pageNo(pageNo)
                    .pageSize(pageSize)
                    .orderByColumn(coreOrder)
                    .orderAsc(orderAsc)
                    .physicalFilters(physicalFilters)
                    .build();
            return toRespPage(entityRepository.findPageIds(query), model.getEntityTypeCode(), effectiveDetail);
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
                effectiveKeywordSearch,
                normalizedScope
        );
        return toRespPage(pageIds, entityTypeCode, effectiveDetail);
    }

    /**
     * 按类型 SQL 直分页执行器（ENTITIES_BY_MODEL 未传 modelIds 分支）。
     *
     * <p><b>解决的问题</b>：类型全量场景下，把筛选/排序/分页尽量下推到数据库。</p>
     * <p><b>必要步骤</b>：仅 gate 判定可直分页时调用。</p>
     */
    private PageResult<EntityRespVO> pageByType(String entityTypeCode,
                                                                     String keyword,
                                                                     String domain,
                                                                     Integer pageNo,
                                                                     Integer pageSize,
                                                                     String orderByColumn,
                                                                     boolean orderAsc,
                                                                     List<PhysicalColumnFilter> physicalFilters,
                                                                     KeywordSearchSpec keywordSearch,
                                                                     EntityQueryResultDetail detail,
                                                                     String scopeRegistryCode) {
        if (!StringUtils.hasText(entityTypeCode) || !StringUtils.hasText(orderByColumn)) {
            return new PageResult<>(new ArrayList<>(), 0L);
        }
        EntityRepository.EntityQuery query = EntityRepository.EntityQuery.builder()
                .entityTypeCode(entityTypeCode.trim())
                .keyword(keyword)
                .keywordSearch(keywordSearch != null ? keywordSearch : KeywordSearchSpec.nameOnly())
                .domain(EntityTypeScopeContext.normalizeDomain(domain))
                .scopeRegistryCode(StringUtils.hasText(scopeRegistryCode) ? scopeRegistryCode.trim() : null)
                .pageNo(pageNo)
                .pageSize(pageSize)
                .orderByColumn(orderByColumn.trim())
                .orderAsc(orderAsc)
                .physicalFilters(physicalFilters)
                .build();
        return toRespPage(entityRepository.findPageIds(query), entityTypeCode.trim(), detail);
    }

    private List<Long> collectEntityIdsByModelIds(List<Long> modelIds, String entityTypeCode) {
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

    private List<Long> filterCandidateEntityIds(
            List<Long> candidateIds,
            String entityTypeCode,
            List<FieldFilterReqVO> filters,
            String keyword,
            KeywordSearchSpec keywordSearch
    ) {
        if (candidateIds == null || candidateIds.isEmpty()) {
            return Collections.emptyList();
        }
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        boolean hasFilters = filters != null && !filters.isEmpty();
        if (!hasKeyword && !hasFilters) {
            return candidateIds;
        }

        List<FieldFilterReqVO> indexFilters = filters;
        List<Long> scopedIds = candidateIds;
        FacilityFilterPeel peeled = peelFacilityOwningFilter(entityTypeCode, filters);
        if (peeled != null) {
            scopedIds = entityRepository.retainOrderedIdsByPhysicalFilters(
                    candidateIds, entityTypeCode, List.of(peeled.physical()));
            if (scopedIds.isEmpty()) {
                return Collections.emptyList();
            }
            indexFilters = peeled.remainingFilters();
        }

        boolean hasIndexFilters = indexFilters != null && !indexFilters.isEmpty();
        if (!hasKeyword && !hasIndexFilters) {
            return scopedIds;
        }

        Set<Long> matchedIds = entityFieldQueryEngine.searchAndFilterEntityIds(
                entityTypeCode,
                keyword,
                hasIndexFilters ? indexFilters : List.of(),
                scopedIds,
                toSearchFieldCodes(keywordSearch));
        if (matchedIds == null || matchedIds.isEmpty()) {
            return Collections.emptyList();
        }
        return scopedIds.stream()
                .filter(id -> id != null && matchedIds.contains(id))
                .toList();
    }

    /**
     * 从 fieldFilters 抽出所属场站 EQ/IN，转为实体表物理条件；其余条件原样留下给索引。
     * 站场级类型若带了 facility_id 却解析不出物理列 → 报错（缺系统基础字段），禁止进索引。
     */
    private FacilityFilterPeel peelFacilityOwningFilter(
            String entityTypeCode, List<FieldFilterReqVO> filters) {
        if (filters == null || filters.isEmpty() || !StringUtils.hasText(entityTypeCode)) {
            return null;
        }
        FieldFilterReqVO owning = null;
        List<FieldFilterReqVO> remaining = new ArrayList<>(filters.size());
        for (FieldFilterReqVO filter : filters) {
            if (filter != null
                    && StringUtils.hasText(filter.getFieldCode())
                    && FacilityOwningFieldCodes.FIELD_CODE.equals(filter.getFieldCode().trim())
                    && !Boolean.TRUE.equals(filter.getRelationField())) {
                owning = filter;
            } else {
                remaining.add(filter);
            }
        }
        if (owning == null) {
            return null;
        }
        String op = owning.getOp() == null ? "" : owning.getOp().trim().toUpperCase(Locale.ROOT);
        if (!"EQ".equals(op) && !"IN".equals(op) && !"NOT_IN".equals(op)) {
            throw new ServiceException(400, "所属场站筛选仅支持 EQ / IN / NOT_IN");
        }
        String column = entityDedicatedColumnService.resolvePhysicalColumn(
                entityTypeCode.trim(), FacilityOwningFieldCodes.FIELD_CODE);
        if (!StringUtils.hasText(column)) {
            throw new ServiceException(400,
                    "站场级类型缺少所属场站（facility_id）专用表列；请先 ensure 系统基础字段，禁止用字段索引冒充");
        }
        Object value = owning.getValue();
        if ("EQ".equals(op) && value == null) {
            throw new ServiceException(400, "所属场站筛选值无效");
        }
        if (("IN".equals(op) || "NOT_IN".equals(op)) && value == null) {
            value = List.of();
        }
        return new FacilityFilterPeel(new PhysicalColumnFilter(column, op, value), remaining);
    }

    private PageResult<EntityRespVO> pageByOrderedIds(List<Long> orderedIds, String entityTypeCode, Integer pageNo, Integer pageSize,
                                                      EntityQueryResultDetail detail) {
        Integer pn = normalizePageNo(pageNo);
        Integer ps = normalizePageSize(pageSize);
        int from = (pn - 1) * ps;
        if (from >= orderedIds.size()) {
            return new PageResult<>(new ArrayList<>(), (long) orderedIds.size());
        }
        int to = Math.min(from + ps, orderedIds.size());
        List<Long> pageIds = orderedIds.subList(from, to);
        EntityQueryResultDetail effectiveDetail =
                detail != null ? detail : EntityQueryResultDetail.FULL;
        List<EntityRespVO> result = convertOrderedEntityIdsToRespList(pageIds, entityTypeCode, effectiveDetail);
        return new PageResult<>(result, (long) orderedIds.size());
    }

    // ==================== 单对象读取（scene 外复用） ====================
    @Override
    /**
     * ENTITIES_BY_CATEGORY_LINK 主链：按分类绑定关系取单实体。
     *
     * <p><b>解决的问题</b>：支持“分类绑定一个实体”的场景返回单对象。</p>
     * <p><b>必要步骤</b>：先查绑定，再按实体 ID 回查并组装 VO。</p>
     */
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
    private List<Long> normalizeCategoryIdsOrRoot(List<Long> categoryIds, String categoryTypeCode) {
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

    // ==================== 结果转换（DO -> VO） ====================
    private List<EntityRespVO> convertOrderedEntityIdsToRespList(List<Long> orderedEntityIds,
                                                                  String entityTypeCode,
                                                                  EntityQueryResultDetail detail) {
        if (orderedEntityIds == null || orderedEntityIds.isEmpty()) {
            return new ArrayList<>();
        }
        boolean includeCustom = entityDedicatedColumnService.hasEnabledMultiRefBaseField(entityTypeCode);
        List<EntityDO> ordered = entityCoreService.listByIdsWithDedicatedBaseFields(
                orderedEntityIds, entityTypeCode, includeCustom);
        if (ordered == null || ordered.isEmpty()) {
            return new ArrayList<>();
        }
        boolean light = detail == EntityQueryResultDetail.LIGHT;
        List<EntityRespVO> vos = light
                ? EntityDoVoHelper.toLightRespVOList(ordered, entityDedicatedColumnService)
                : EntityDoVoHelper.toRespVOListSkipCustomPresent(ordered, entityDedicatedColumnService);
        return vos;
    }

    /**
     * 解析 searchFieldCodes，产出关键词查询范围配置。
     *
     * <p><b>解决的问题</b>：将前端搜索范围转换为“可 SQL 下推列 + 扩展字段码”。</p>
     * <p><b>必要步骤</b>：有 keyword 时建议执行；无 keyword 时不是必要步骤。</p>
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
     * 可下推到实体表 ORDER BY 的列名：核心列（含 sort）原样；可排序基础字段 → 物理列名；否则 null。
     * <p>{@code sort} 与 name/id 一样是实体表物理列，禁止再当成「不准表内分页」的特殊开关。</p>
     */
    private String resolveDbOrderColumn(String entityTypeCode, String orderByColumn) {
        if (!StringUtils.hasText(orderByColumn)) {
            return null;
        }
        String column = orderByColumn.trim();
        if ("sort".equals(column) || CORE_ORDER_BY_COLUMNS.contains(column)) {
            return column;
        }
        if (!StringUtils.hasText(entityTypeCode)) {
            return null;
        }
        return entityDedicatedColumnService.resolveSortablePhysicalColumn(entityTypeCode.trim(), column);
    }

    /**
     * SQL 快慢路径进门判定封装（scene 分流核心）。
     *
     * <p><b>解决的问题</b>：一次性判定筛选/关键词/排序/scope 是否可下推。</p>
     * <p><b>必要步骤</b>：所有可能走 SQL 直分页的 scene 都应先执行。</p>
     */
    private PreparedEntityTablePaging prepareEntityTableDirectPaging(
            String entityTypeCode,
            List<FieldFilterReqVO> filters,
            String keyword,
            KeywordSearchSpec keywordSearch,
            String orderByColumn,
            String scopeRegistryCode
    ) {
        KeywordSearchSpec effectiveKeywordSearch =
                keywordSearch != null ? keywordSearch : KeywordSearchSpec.nameOnly();
        List<PhysicalColumnFilter> pushFilters =
                resolvePushablePhysicalFilters(entityTypeCode, filters);
        boolean hasOrderByColumn = StringUtils.hasText(orderByColumn);
        String dbOrderColumn = hasOrderByColumn
                ? resolveDbOrderColumn(entityTypeCode, orderByColumn)
                : null;
        boolean keywordPushable = !StringUtils.hasText(keyword)
                || effectiveKeywordSearch.canPushFullyToEntityTable();
        boolean hasScope = StringUtils.hasText(scopeRegistryCode);
        EntityTableDirectPagingGate.Decision decision = EntityTableDirectPagingGate.decide(
                pushFilters != null,
                keywordPushable,
                hasOrderByColumn,
                dbOrderColumn,
                hasScope);
        return new PreparedEntityTablePaging(
                decision,
                pushFilters,
                dbOrderColumn,
                effectiveKeywordSearch,
                hasScope);
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

    private Integer normalizePageNo(Integer pageNo) {
        return (pageNo == null || pageNo < 1) ? 1 : pageNo;
    }

    /**
     * 每页条数为空时，使用默认值 20。
     */
    private Integer normalizePageSize(Integer pageSize) {
        return (pageSize == null || pageSize < 1) ? 20 : pageSize;
    }

    // ==================== 树形查询辅助 ====================
    /**
     * 树形场景子树查询（按父节点懒加载）。
     *
     * <p><b>解决的问题</b>：树结构不能简单复用平铺分页，需要按父子关系逐层返回。</p>
     * <p><b>必要步骤</b>：tree 快捷分支命中时必须经过这里。</p>
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
        List<Long> filteredIds = filterCandidateEntityIds(orderedIds, entityTypeCode, filters, keyword, null);
        if (filteredIds.isEmpty()) {
            return new ArrayList<>();
        }
        Set<Long> allowed = new HashSet<>(filteredIds);
        return entities.stream()
                .filter(entity -> entity.getId() != null && allowed.contains(entity.getId()))
                .toList();
    }

    /**
     * 单型号树查询快捷路径。
     *
     * <p><b>解决的问题</b>：单 model 的树形查询直接返回树，减少平铺后再组装成本。</p>
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

    // ==================== 场景响应形状输出（PAGE/TREE/LIST） ====================
    /**
     * scene 统一出参成形（PAGE/TREE/LIST）。
     *
     * <p><b>解决的问题</b>：统一返回结构，防止不同 scene 自己拼 shape 导致契约不一致。</p>
     * <p><b>必要步骤</b>：除单对象响应外，所有 scene 最终都应走这里。</p>
     */
    private EntitySceneQueryRespVO respondShape(EntityQueryResultShape shape,
                                                PageResult<EntityRespVO> page,
                                                EntityQueryResultDetail detail) {
        if (shape == EntityQueryResultShape.PAGE) {
            return EntitySceneQueryRespVO.page(applyResultDetail(page, detail), detail.getCode());
        }
        List<EntityRespVO> list = page.getList() != null ? page.getList() : List.of();
        if (shape == EntityQueryResultShape.TREE) {
            return EntitySceneQueryRespVO.tree(
                    applyResultDetail(EntityTreeBuilder.buildTree(list,
                            EntityTreeBuilder.SortMode.LOCAL_SIBLING_SORT), detail),
                    detail.getCode());
        }
        return EntitySceneQueryRespVO.list(applyResultDetail(list, detail), detail.getCode());
    }

    /**
     * 单对象统一出参成形（ENTITIES_BY_CATEGORY_LINK / ENTITIES_DETAIL）。
     *
     * <p><b>解决的问题</b>：单对象也遵循 PAGE/TREE/LIST 三种响应形状契约。</p>
     */
    private EntitySceneQueryRespVO respondOne(EntityQueryResultShape shape,
                                              EntityRespVO one,
                                              EntityQueryResultDetail detail) {
        EntityRespVO shaped = applyResultDetail(Collections.singletonList(one), detail).get(0);
        if (shape == EntityQueryResultShape.PAGE) {
            return EntitySceneQueryRespVO.page(
                    new PageResult<>(Collections.singletonList(shaped), 1L), detail.getCode());
        }
        if (shape == EntityQueryResultShape.TREE) {
            return EntitySceneQueryRespVO.tree(Collections.singletonList(shaped), detail.getCode());
        }
        return EntitySceneQueryRespVO.list(Collections.singletonList(shaped), detail.getCode());
    }

    private PageResult<EntityRespVO> toRespPage(PageResult<Long> pageIds, String entityTypeCode,
                                                EntityQueryResultDetail detail) {
        List<Long> orderedIds = pageIds.getList() != null ? pageIds.getList() : List.of();
        EntityQueryResultDetail effective = detail != null ? detail : EntityQueryResultDetail.FULL;
        return new PageResult<>(
                convertOrderedEntityIdsToRespList(orderedIds, entityTypeCode, effective),
                pageIds.getTotal());
    }

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
        // 列表查询增强：REF 契约对象批量补 name（LIGHT / FULL 均需要）
        entityRefDisplayEnrichService.enrich(entities);
        if (detail != EntityQueryResultDetail.LIGHT) {
            return entities;
        }
        return entities.stream().map(this::toLightRespVO).toList();
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
            return list.stream().anyMatch(EntitySceneQueryServiceImpl::looksLikeRefApiValue);
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
            orderedEntityIds = entityScopedQueryRepository.listOrderedEntityIdsByIntersectingCategoryGroups(
                    expandedGroups, storage, null, domain, null);
        }
        if (orderedEntityIds == null || orderedEntityIds.isEmpty()) {
            return List.of();
        }
        return entityRepository.listDistinctModelIdsPreservingEntityOrder(orderedEntityIds, storage);
    }

}
