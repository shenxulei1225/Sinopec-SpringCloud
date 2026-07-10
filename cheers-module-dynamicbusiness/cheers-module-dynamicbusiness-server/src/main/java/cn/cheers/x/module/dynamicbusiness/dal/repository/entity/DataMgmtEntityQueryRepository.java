package cn.cheers.x.module.dynamicbusiness.dal.repository.entity;

import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryEntityLinkDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityCategoryRelationDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.category.CategoryEntityLinkMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntityCategoryRelationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.HashMap;
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
 * </ul>
 * <p>中栏模型列表仍由 model_category_relation 驱动；右栏实体必须在上述关联表内且落在 expandedCategoryIds 中。</p>
 */
@Repository
@RequiredArgsConstructor
public class DataMgmtEntityQueryRepository {

    private static final int DEFAULT_SORT_FALLBACK = Integer.MAX_VALUE;

    private final EntityCategoryRelationMapper entityCategoryRelationMapper;
    private final CategoryEntityLinkMapper categoryEntityLinkMapper;
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

        return bestByEntityId.values().stream()
                .sorted(Comparator.comparingInt(ScopeCandidate::catRank)
                        .thenComparingInt(ScopeCandidate::src)
                        .thenComparingInt(ScopeCandidate::sortKey)
                        .thenComparingLong(ScopeCandidate::tieId))
                .map(ScopeCandidate::entityId)
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
