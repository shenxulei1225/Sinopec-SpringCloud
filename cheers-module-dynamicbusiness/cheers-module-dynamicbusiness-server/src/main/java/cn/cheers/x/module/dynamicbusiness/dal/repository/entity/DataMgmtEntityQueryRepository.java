package cn.cheers.x.module.dynamicbusiness.dal.repository.entity;

import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryEntityLinkDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityCategoryRelationDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelCategoryRelationDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.category.CategoryEntityLinkMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelCategoryRelationMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntityCategoryRelationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 数据管理三栏：按分类范围（expanded categoryIds）+ 可选 modelIds 收集有序 entityId。
 *
 * <p>只合并分类范围内的实体关联，不做 modelId 全局扫表：</p>
 * <ul>
 *   <li>Pattern A：{@code dynamic_entity_category_relation}</li>
 *   <li>Pattern C：{@code dynamic_category_entity_link}</li>
 *   <li>模型挂分类：{@code dynamic_model_category_relation} → 实体按 modelId 归入分类范围</li>
 * </ul>
 */
@Repository
@RequiredArgsConstructor
public class DataMgmtEntityQueryRepository {

    private static final int DEFAULT_SORT_FALLBACK = Integer.MAX_VALUE;

    private final EntityCategoryRelationMapper entityCategoryRelationMapper;
    private final CategoryEntityLinkMapper categoryEntityLinkMapper;
    private final ModelCategoryRelationMapper modelCategoryRelationMapper;
    private final EntityRepository entityRepository;

    /**
     * @param expandedCategoryIds 已展开子树的分类 ID（顺序即 cat_rank）
     */
    public List<Long> listOrderedEntityIdsByCategoryScope(List<Long> expandedCategoryIds,
                                                          String entityTypeCode,
                                                          List<Long> modelIds) {
        if (expandedCategoryIds == null || expandedCategoryIds.isEmpty()) {
            return List.of();
        }
        Map<Long, Integer> categoryRank = buildCategoryRank(expandedCategoryIds);
        Set<Long> modelIdFilter = toModelIdFilter(modelIds);

        Map<Long, ScopeCandidate> bestByEntityId = new HashMap<>();
        mergeRelationCandidates(bestByEntityId, categoryRank, expandedCategoryIds, entityTypeCode, modelIdFilter);
        mergeLinkCandidates(bestByEntityId, categoryRank, expandedCategoryIds, modelIdFilter);
        mergeModelCategoryCandidates(bestByEntityId, categoryRank, expandedCategoryIds, entityTypeCode, modelIdFilter);

        return bestByEntityId.values().stream()
                .sorted(Comparator.comparingInt(ScopeCandidate::catRank)
                        .thenComparingInt(ScopeCandidate::src)
                        .thenComparingInt(ScopeCandidate::sortKey)
                        .thenComparingLong(ScopeCandidate::tieId))
                .map(ScopeCandidate::entityId)
                .toList();
    }

    /**
     * 分类体系下：模型已挂分类 → 实体按 modelId 归入（设备管理等主路径）。
     */
    public List<Long> listEntityIdsViaModelCategoryInCategoryType(String categoryTypeCode,
                                                                  String entityTypeCode,
                                                                  List<Long> modelIds) {
        if (categoryTypeCode == null || categoryTypeCode.isBlank()
                || entityTypeCode == null || entityTypeCode.isBlank()) {
            return List.of();
        }
        List<Long> scopedModelIds = modelCategoryRelationMapper.selectDistinctModelIdsByCategoryTypeCode(
                categoryTypeCode, entityTypeCode);
        if (scopedModelIds == null || scopedModelIds.isEmpty()) {
            return List.of();
        }
        Set<Long> modelIdFilter = toModelIdFilter(modelIds);
        if (modelIdFilter != null) {
            scopedModelIds = scopedModelIds.stream()
                    .filter(modelIdFilter::contains)
                    .distinct()
                    .toList();
        }
        if (scopedModelIds.isEmpty()) {
            return List.of();
        }
        List<EntityDO> entities = entityRepository.findByModelIds(scopedModelIds, entityTypeCode);
        return entities.stream()
                .map(EntityDO::getId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    private void mergeRelationCandidates(Map<Long, ScopeCandidate> bestByEntityId,
                                         Map<Long, Integer> categoryRank,
                                         List<Long> expandedCategoryIds,
                                         String entityTypeCode,
                                         Set<Long> modelIdFilter) {
        LambdaQueryWrapperX<EntityCategoryRelationDO> query = new LambdaQueryWrapperX<EntityCategoryRelationDO>()
                .in(EntityCategoryRelationDO::getCategoryId, expandedCategoryIds)
                .eq(EntityCategoryRelationDO::getDeleted, false);
        if (entityTypeCode != null && !entityTypeCode.isBlank()) {
            query.eq(EntityCategoryRelationDO::getEntityTypeCode, entityTypeCode);
        }
        List<EntityCategoryRelationDO> relations = entityCategoryRelationMapper.selectList(query);
        if (relations.isEmpty()) {
            return;
        }

        Set<Long> allowedEntityIds = null;
        if (modelIdFilter != null) {
            List<Long> entityIds = relations.stream()
                    .map(EntityCategoryRelationDO::getEntityId)
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();
            allowedEntityIds = filterEntityIdsByModelIds(entityIds, entityTypeCode, modelIdFilter);
        }

        for (EntityCategoryRelationDO relation : relations) {
            Long entityId = relation.getEntityId();
            Long categoryId = relation.getCategoryId();
            if (entityId == null || categoryId == null) {
                continue;
            }
            if (allowedEntityIds != null && !allowedEntityIds.contains(entityId)) {
                continue;
            }
            offerCandidate(bestByEntityId, new ScopeCandidate(
                    entityId,
                    categoryRank.getOrDefault(categoryId, DEFAULT_SORT_FALLBACK),
                    relation.getSort() != null ? relation.getSort() : DEFAULT_SORT_FALLBACK,
                    relation.getId() != null ? relation.getId() : Long.MAX_VALUE,
                    1));
        }
    }

    private void mergeLinkCandidates(Map<Long, ScopeCandidate> bestByEntityId,
                                     Map<Long, Integer> categoryRank,
                                     List<Long> expandedCategoryIds,
                                     Set<Long> modelIdFilter) {
        List<CategoryEntityLinkDO> links = categoryEntityLinkMapper.selectList(
                new LambdaQueryWrapperX<CategoryEntityLinkDO>()
                        .in(CategoryEntityLinkDO::getCategoryId, expandedCategoryIds)
                        .eq(CategoryEntityLinkDO::getDeleted, false));
        for (CategoryEntityLinkDO link : links) {
            Long entityId = link.getEntityId();
            Long categoryId = link.getCategoryId();
            if (entityId == null || categoryId == null) {
                continue;
            }
            if (modelIdFilter != null) {
                Long linkModelId = link.getEntityModelId();
                if (linkModelId == null || !modelIdFilter.contains(linkModelId)) {
                    continue;
                }
            }
            offerCandidate(bestByEntityId, new ScopeCandidate(
                    entityId,
                    categoryRank.getOrDefault(categoryId, DEFAULT_SORT_FALLBACK),
                    0,
                    link.getId() != null ? link.getId() : Long.MAX_VALUE,
                    2));
        }
    }

    /**
     * 模型已挂分类、实体只挂模型：通过 model_category_relation 将实体纳入分类范围。
     */
    private void mergeModelCategoryCandidates(Map<Long, ScopeCandidate> bestByEntityId,
                                              Map<Long, Integer> categoryRank,
                                              List<Long> expandedCategoryIds,
                                              String entityTypeCode,
                                              Set<Long> modelIdFilter) {
        LambdaQueryWrapperX<ModelCategoryRelationDO> query = new LambdaQueryWrapperX<ModelCategoryRelationDO>()
                .in(ModelCategoryRelationDO::getCategoryId, expandedCategoryIds)
                .eq(ModelCategoryRelationDO::getDeleted, false);
        if (entityTypeCode != null && !entityTypeCode.isBlank()) {
            query.eq(ModelCategoryRelationDO::getEntityTypeCode, entityTypeCode);
        }
        if (modelIdFilter != null) {
            query.in(ModelCategoryRelationDO::getModelId, modelIdFilter);
        }
        List<ModelCategoryRelationDO> relations = modelCategoryRelationMapper.selectList(query);
        if (relations.isEmpty()) {
            return;
        }

        Map<Long, Integer> modelBestRank = new HashMap<>();
        Map<Long, Integer> modelBestSort = new HashMap<>();
        Map<Long, Long> modelBestTie = new HashMap<>();
        for (ModelCategoryRelationDO relation : relations) {
            Long modelId = relation.getModelId();
            Long categoryId = relation.getCategoryId();
            if (modelId == null || categoryId == null) {
                continue;
            }
            int catRank = categoryRank.getOrDefault(categoryId, DEFAULT_SORT_FALLBACK);
            int sortKey = relation.getSort() != null ? relation.getSort() : DEFAULT_SORT_FALLBACK;
            long tieId = relation.getId() != null ? relation.getId() : Long.MAX_VALUE;
            Integer existingRank = modelBestRank.get(modelId);
            if (existingRank == null || catRank < existingRank
                    || (catRank == existingRank && sortKey < modelBestSort.getOrDefault(modelId, DEFAULT_SORT_FALLBACK))) {
                modelBestRank.put(modelId, catRank);
                modelBestSort.put(modelId, sortKey);
                modelBestTie.put(modelId, tieId);
            }
        }
        if (modelBestRank.isEmpty()) {
            return;
        }

        List<Long> scopedModelIds = modelBestRank.keySet().stream().toList();
        List<EntityDO> entities = entityRepository.findByModelIds(scopedModelIds, entityTypeCode);
        if (entities.isEmpty()) {
            return;
        }

        Set<Long> candidateEntityIds = entities.stream()
                .map(EntityDO::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Set<Long> linkedOrRelatedInScope = resolveEntityIdsBoundInCategories(
                expandedCategoryIds, entityTypeCode, candidateEntityIds);
        Set<Long> linkedOrRelatedAnywhere = resolveEntityIdsWithCategoryBinding(entityTypeCode, candidateEntityIds);

        for (EntityDO entity : entities) {
            Long entityId = entity.getId();
            Long modelId = entity.getModelId();
            if (entityId == null || modelId == null) {
                continue;
            }
            // Pattern C：实体已挂其它分类时，不可因「同型号」越界纳入当前分类范围；仅保留模型挂分类、实体只挂型号的场景。
            if (!linkedOrRelatedInScope.contains(entityId) && linkedOrRelatedAnywhere.contains(entityId)) {
                continue;
            }
            Integer rank = modelBestRank.get(modelId);
            if (rank == null) {
                continue;
            }
            offerCandidate(bestByEntityId, new ScopeCandidate(
                    entityId,
                    rank,
                    modelBestSort.getOrDefault(modelId, DEFAULT_SORT_FALLBACK),
                    modelBestTie.getOrDefault(modelId, Long.MAX_VALUE),
                    3));
        }
    }

    /** 分类范围内已有 entity↔category 绑定（link ∪ relation）的实体 id。 */
    private Set<Long> resolveEntityIdsBoundInCategories(List<Long> expandedCategoryIds,
                                                        String entityTypeCode,
                                                        Set<Long> candidateEntityIds) {
        if (expandedCategoryIds == null || expandedCategoryIds.isEmpty()
                || candidateEntityIds == null || candidateEntityIds.isEmpty()) {
            return Set.of();
        }
        Set<Long> bound = new HashSet<>();
        List<CategoryEntityLinkDO> links = categoryEntityLinkMapper.selectList(
                new LambdaQueryWrapperX<CategoryEntityLinkDO>()
                        .in(CategoryEntityLinkDO::getCategoryId, expandedCategoryIds)
                        .in(CategoryEntityLinkDO::getEntityId, candidateEntityIds)
                        .eq(CategoryEntityLinkDO::getDeleted, false));
        for (CategoryEntityLinkDO link : links) {
            if (link.getEntityId() != null) {
                bound.add(link.getEntityId());
            }
        }

        LambdaQueryWrapperX<EntityCategoryRelationDO> relationQuery = new LambdaQueryWrapperX<EntityCategoryRelationDO>()
                .in(EntityCategoryRelationDO::getCategoryId, expandedCategoryIds)
                .in(EntityCategoryRelationDO::getEntityId, candidateEntityIds)
                .eq(EntityCategoryRelationDO::getDeleted, false);
        if (entityTypeCode != null && !entityTypeCode.isBlank()) {
            relationQuery.eq(EntityCategoryRelationDO::getEntityTypeCode, entityTypeCode);
        }
        List<EntityCategoryRelationDO> relations = entityCategoryRelationMapper.selectList(relationQuery);
        for (EntityCategoryRelationDO relation : relations) {
            if (relation.getEntityId() != null) {
                bound.add(relation.getEntityId());
            }
        }
        return bound;
    }

    /** 实体是否已在当前业务类型下挂接任意分类（link ∪ relation）。 */
    private Set<Long> resolveEntityIdsWithCategoryBinding(String entityTypeCode, Set<Long> candidateEntityIds) {
        if (candidateEntityIds == null || candidateEntityIds.isEmpty()) {
            return Set.of();
        }
        Set<Long> bound = new HashSet<>();
        List<CategoryEntityLinkDO> links = categoryEntityLinkMapper.selectList(
                new LambdaQueryWrapperX<CategoryEntityLinkDO>()
                        .in(CategoryEntityLinkDO::getEntityId, candidateEntityIds)
                        .eq(CategoryEntityLinkDO::getDeleted, false));
        for (CategoryEntityLinkDO link : links) {
            if (link.getEntityId() != null) {
                bound.add(link.getEntityId());
            }
        }

        LambdaQueryWrapperX<EntityCategoryRelationDO> relationQuery = new LambdaQueryWrapperX<EntityCategoryRelationDO>()
                .in(EntityCategoryRelationDO::getEntityId, candidateEntityIds)
                .eq(EntityCategoryRelationDO::getDeleted, false);
        if (entityTypeCode != null && !entityTypeCode.isBlank()) {
            relationQuery.eq(EntityCategoryRelationDO::getEntityTypeCode, entityTypeCode);
        }
        List<EntityCategoryRelationDO> relations = entityCategoryRelationMapper.selectList(relationQuery);
        for (EntityCategoryRelationDO relation : relations) {
            if (relation.getEntityId() != null) {
                bound.add(relation.getEntityId());
            }
        }
        return bound;
    }

    private Set<Long> filterEntityIdsByModelIds(List<Long> entityIds,
                                                String entityTypeCode,
                                                Set<Long> modelIdFilter) {
        if (entityIds == null || entityIds.isEmpty()) {
            return Set.of();
        }
        List<EntityDO> entities = entityRepository.findByIds(entityIds, entityTypeCode);
        return entities.stream()
                .filter(entity -> entity.getModelId() != null && modelIdFilter.contains(entity.getModelId()))
                .map(EntityDO::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private void offerCandidate(Map<Long, ScopeCandidate> bestByEntityId, ScopeCandidate candidate) {
        bestByEntityId.merge(candidate.entityId(), candidate, (existing, incoming) ->
                compareCandidate(existing, incoming) <= 0 ? existing : incoming);
    }

    private int compareCandidate(ScopeCandidate left, ScopeCandidate right) {
        if (left.catRank() != right.catRank()) {
            return Integer.compare(left.catRank(), right.catRank());
        }
        if (left.src() != right.src()) {
            return Integer.compare(left.src(), right.src());
        }
        if (left.sortKey() != right.sortKey()) {
            return Integer.compare(left.sortKey(), right.sortKey());
        }
        return Long.compare(left.tieId(), right.tieId());
    }

    private Map<Long, Integer> buildCategoryRank(List<Long> expandedCategoryIds) {
        Map<Long, Integer> categoryRank = new HashMap<>();
        for (int i = 0; i < expandedCategoryIds.size(); i++) {
            Long categoryId = expandedCategoryIds.get(i);
            if (categoryId != null) {
                categoryRank.putIfAbsent(categoryId, i);
            }
        }
        return categoryRank;
    }

    private Set<Long> toModelIdFilter(List<Long> modelIds) {
        if (modelIds == null || modelIds.isEmpty()) {
            return null;
        }
        return modelIds.stream().filter(Objects::nonNull).collect(Collectors.toSet());
    }

    private record ScopeCandidate(long entityId, int catRank, int sortKey, long tieId, int src) {
    }
}
