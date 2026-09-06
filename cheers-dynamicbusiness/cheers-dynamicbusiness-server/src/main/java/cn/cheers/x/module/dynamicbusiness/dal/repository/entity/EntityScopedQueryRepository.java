package cn.cheers.x.module.dynamicbusiness.dal.repository.entity;

import cn.cheers.x.framework.mybatis.core.dataobject.BaseDO;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryEntityLinkDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityCategoryRelationDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytypescope.EntityTypeScopeDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.category.CategoryEntityLinkMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntityCategoryRelationMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytypescope.EntityTypeScopeMapper;
import cn.cheers.x.module.dynamicbusiness.framework.entitytype.EntityTypeScopeContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 按分类范围（expanded categoryIds）+ 可选 modelIds / domain / 划分成员 收集有序 entityId。
 *
 * <p>实体列只认分类-实体关联（relation / link）；型号列另走分类-型号关联，不在此展开实体。</p>
 */
@Repository
@RequiredArgsConstructor
public class EntityScopedQueryRepository {

    private static final int DEFAULT_SORT_FALLBACK = Integer.MAX_VALUE;

    private final EntityCategoryRelationMapper entityCategoryRelationMapper;
    private final CategoryEntityLinkMapper categoryEntityLinkMapper;
    private final EntityTypeScopeMapper entityTypeScopeMapper;
    private final EntityRepository entityRepository;

    /**
     * @param expandedCategoryIds 已展开子树的分类 ID（顺序即 cat_rank）
     * @param entityTypeCode      实际存储类型编码；关联表过滤与物理表取数都用它，
     *                            注册编码（如 task_patrol）不参与查询
     * @param domain              业务域；有值时关联层先按 relation.domain 过滤，取数后再按 entity.domain 复核
     * @param scopeRegistryCode   划分入口编码；有值时只保留成员表中的实体
     */
    public List<Long> listOrderedEntityIdsByCategoryScope(List<Long> expandedCategoryIds,
                                                          String entityTypeCode,
                                                          List<Long> modelIds,
                                                          String domain,
                                                          String scopeRegistryCode) {
        if (expandedCategoryIds == null || expandedCategoryIds.isEmpty()) {
            return List.of();
        }
        Map<Long, Integer> categoryRank = buildCategoryRank(expandedCategoryIds);
        Set<Long> modelIdFilter = toModelIdFilter(modelIds);
        String normalizedDomain = EntityTypeScopeContext.normalizeDomain(domain);

        Map<Long, ScopeCandidate> bestByEntityId = new HashMap<>();
        mergeRelationCandidates(bestByEntityId, categoryRank, expandedCategoryIds, entityTypeCode,
                normalizedDomain, modelIdFilter);
        mergeLinkCandidates(bestByEntityId, categoryRank, expandedCategoryIds, entityTypeCode,
                normalizedDomain, modelIdFilter);
        // 无 domain/scope 时也要走一遍：剔除已软删实体，避免 total 含死 id、本页行数 < pageSize
        retainByDomainAndScope(bestByEntityId, entityTypeCode, normalizedDomain, scopeRegistryCode);

        return bestByEntityId.values().stream()
                .sorted(Comparator.comparingInt(ScopeCandidate::catRank)
                        .thenComparingInt(ScopeCandidate::src)
                        .thenComparingInt(ScopeCandidate::sortKey)
                        .thenComparingLong(ScopeCandidate::tieId))
                .map(ScopeCandidate::entityId)
                .toList();
    }

    /** 兼容旧调用：无业务域 / 划分约束。 */
    public List<Long> listOrderedEntityIdsByCategoryScope(List<Long> expandedCategoryIds,
                                                          String entityTypeCode,
                                                          List<Long> modelIds) {
        return listOrderedEntityIdsByCategoryScope(expandedCategoryIds, entityTypeCode, modelIds, null, null);
    }

    /**
     * 多独立栏归类求交：每组已展开的分类 id 在关联表上 INTERSECT，再按 domain / 划分 / 型号收窄。
     * 单组时回落到 {@link #listOrderedEntityIdsByCategoryScope}。
     */
    public List<Long> listOrderedEntityIdsByIntersectingCategoryGroups(List<List<Long>> expandedGroups,
                                                                       String entityTypeCode,
                                                                       List<Long> modelIds,
                                                                       String domain,
                                                                       String scopeRegistryCode) {
        if (expandedGroups == null || expandedGroups.isEmpty()) {
            return List.of();
        }
        List<List<Long>> normalizedGroups = expandedGroups.stream()
                .filter(Objects::nonNull)
                .map(group -> group.stream().filter(Objects::nonNull).distinct().toList())
                .filter(group -> !group.isEmpty())
                .toList();
        if (normalizedGroups.isEmpty()) {
            return List.of();
        }
        if (normalizedGroups.size() == 1) {
            return listOrderedEntityIdsByCategoryScope(
                    normalizedGroups.get(0), entityTypeCode, modelIds, domain, scopeRegistryCode);
        }

        String storage = entityTypeCode == null ? null : entityTypeCode.trim();
        if (!StringUtils.hasText(storage)) {
            return List.of();
        }
        String normalizedDomain = EntityTypeScopeContext.normalizeDomain(domain);
        List<Long> intersected = entityCategoryRelationMapper.selectEntityIdsIntersectingCategoryGroups(
                storage, StringUtils.hasText(normalizedDomain) ? normalizedDomain : null, normalizedGroups);
        if (intersected == null || intersected.isEmpty()) {
            return List.of();
        }

        List<Long> ordered = retainOrderedIdsByDomainAndScope(
                intersected, storage, normalizedDomain, scopeRegistryCode);
        Set<Long> modelIdFilter = toModelIdFilter(modelIds);
        if (modelIdFilter != null && !modelIdFilter.isEmpty() && !ordered.isEmpty()) {
            Set<Long> modelMatched = filterEntityIdsByModelIds(ordered, storage, modelIdFilter);
            ordered = ordered.stream().filter(modelMatched::contains).toList();
        }
        return ordered;
    }

    /**
     * 在已有候选 id 上收窄：始终剔除已软删/不存在实体；可选再按业务域与划分成员过滤。
     * <p>划分用成员表按候选集批量查，等价 EXISTS，不先拉全员 id。</p>
     */
    public List<Long> retainOrderedIdsByDomainAndScope(List<Long> orderedEntityIds,
                                                       String entityTypeCode,
                                                       String domain,
                                                       String scopeRegistryCode) {
        if (orderedEntityIds == null || orderedEntityIds.isEmpty()) {
            return List.of();
        }
        // findByIds 受 TableLogic 约束，只回未删除行 → 顺带去掉悬挂关联上的死 id，保证分页能填满 pageSize
        List<EntityDO> aliveEntities = entityRepository.findByIds(orderedEntityIds, entityTypeCode);
        Set<Long> allowed = aliveEntities.stream()
                .map(EntityDO::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(HashSet::new));
        if (allowed.isEmpty()) {
            return List.of();
        }

        if (StringUtils.hasText(domain)) {
            String normalizedDomain = EntityTypeScopeContext.normalizeDomain(domain);
            Set<Long> domainMatched = aliveEntities.stream()
                    .filter(entity -> EntityTypeScopeContext.domainsEqual(entity.getDomain(), normalizedDomain))
                    .map(EntityDO::getId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            allowed.retainAll(domainMatched);
        }
        if (StringUtils.hasText(scopeRegistryCode) && !allowed.isEmpty()) {
            String scopeCode = scopeRegistryCode.trim();
            Set<Long> members = entityTypeScopeMapper
                    .selectByCodeAndEntityIds(scopeCode, allowed)
                    .stream()
                    .map(EntityTypeScopeDO::getEntityId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            allowed.retainAll(members);
        }
        if (allowed.isEmpty()) {
            return List.of();
        }
        return orderedEntityIds.stream().filter(allowed::contains).toList();
    }

    private void retainByDomainAndScope(Map<Long, ScopeCandidate> bestByEntityId,
                                       String entityTypeCode,
                                       String domain,
                                       String scopeRegistryCode) {
        if (bestByEntityId.isEmpty()) {
            return;
        }
        List<Long> retained = retainOrderedIdsByDomainAndScope(
                bestByEntityId.keySet().stream().toList(), entityTypeCode, domain, scopeRegistryCode);
        if (retained.size() == bestByEntityId.size()) {
            return;
        }
        Set<Long> keep = new HashSet<>(retained);
        bestByEntityId.keySet().removeIf(id -> !keep.contains(id));
    }

    /**
     * @param storageEntityTypeCode 实际存储类型（如 task）：既过滤关联表，也用于按型号过滤时路由 ent_* 表
     * @param domain                业务域；有值时只取该业务域的关联行，NATIVE 总账传 null 表示不限业务域
     */
    private void mergeRelationCandidates(Map<Long, ScopeCandidate> bestByEntityId,
                                         Map<Long, Integer> categoryRank,
                                         List<Long> expandedCategoryIds,
                                         String storageEntityTypeCode,
                                         String domain,
                                         Set<Long> modelIdFilter) {
        LambdaQueryWrapperX<EntityCategoryRelationDO> query = new LambdaQueryWrapperX<EntityCategoryRelationDO>()
                .in(EntityCategoryRelationDO::getCategoryId, expandedCategoryIds)
                .eq(BaseDO::getDeleted, false);
        if (StringUtils.hasText(storageEntityTypeCode)) {
            query.eq(EntityCategoryRelationDO::getEntityTypeCode, storageEntityTypeCode.trim());
        }
        if (StringUtils.hasText(domain)) {
            query.eq(EntityCategoryRelationDO::getDomain, domain);
        }
        List<EntityCategoryRelationDO> relations = entityCategoryRelationMapper.selectList(query);
        if (relations.isEmpty()) {
            return;
        }
        Set<Long> modelMatchedEntityIds = null;
        if (modelIdFilter != null) {
            List<Long> entityIds = relations.stream()
                    .map(EntityCategoryRelationDO::getEntityId)
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();
            modelMatchedEntityIds = filterEntityIdsByModelIds(entityIds, storageEntityTypeCode, modelIdFilter);
        }
        for (EntityCategoryRelationDO relation : relations) {
            Long entityId = relation.getEntityId();
            Long categoryId = relation.getCategoryId();
            if (entityId == null || categoryId == null) {
                continue;
            }
            if (modelMatchedEntityIds != null && !modelMatchedEntityIds.contains(entityId)) {
                continue;
            }
            int sortKey = relation.getSort() != null ? relation.getSort() : DEFAULT_SORT_FALLBACK;
            long tieId = relation.getId() != null ? relation.getId() : Long.MAX_VALUE;
            offerCandidate(bestByEntityId, new ScopeCandidate(
                    entityId,
                    categoryRank.getOrDefault(categoryId, Integer.MAX_VALUE),
                    sortKey,
                    tieId,
                    1));
        }
    }

    /** 分类即实体 1:1 link 与 relation 同口径：按 entityTypeCode + 业务域收窄，避免跨表同 ID 串行。 */
    private void mergeLinkCandidates(Map<Long, ScopeCandidate> bestByEntityId,
                                     Map<Long, Integer> categoryRank,
                                     List<Long> expandedCategoryIds,
                                     String entityTypeCode,
                                     String domain,
                                     Set<Long> modelIdFilter) {
        LambdaQueryWrapperX<CategoryEntityLinkDO> linkQuery = new LambdaQueryWrapperX<CategoryEntityLinkDO>()
                .in(CategoryEntityLinkDO::getCategoryId, expandedCategoryIds)
                .eq(BaseDO::getDeleted, false);
        if (StringUtils.hasText(entityTypeCode)) {
            linkQuery.eq(CategoryEntityLinkDO::getEntityTypeCode, entityTypeCode.trim());
        }
        if (StringUtils.hasText(domain)) {
            linkQuery.eq(CategoryEntityLinkDO::getDomain, domain);
        }
        List<CategoryEntityLinkDO> links = categoryEntityLinkMapper.selectList(linkQuery);
        for (CategoryEntityLinkDO link : links) {
            Long entityId = link.getEntityId();
            Long categoryId = link.getCategoryId();
            if (entityId == null || categoryId == null) {
                continue;
            }
            if (modelIdFilter != null) {
                Long entityModelId = link.getEntityModelId();
                if (entityModelId == null || !modelIdFilter.contains(entityModelId)) {
                    continue;
                }
            }
            long tieId = link.getId() != null ? link.getId() : Long.MAX_VALUE;
            offerCandidate(bestByEntityId, new ScopeCandidate(
                    entityId,
                    categoryRank.getOrDefault(categoryId, Integer.MAX_VALUE),
                    0,
                    tieId,
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
