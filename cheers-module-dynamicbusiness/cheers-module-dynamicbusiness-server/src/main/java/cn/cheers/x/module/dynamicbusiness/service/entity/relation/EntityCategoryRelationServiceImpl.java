package cn.cheers.x.module.dynamicbusiness.service.entity.relation;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.BatchEntityCategoryAssociationRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.common.vo.CategoryAssociationBaseRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityCategoryAssociationRespVO;
import static cn.cheers.x.module.dynamicbusiness.enums.ErrorCodeConstants.ASSOCIATION_OPERATION_FAILED;
import static cn.cheers.x.module.dynamicbusiness.enums.ErrorCodeConstants.ENTITY_NOT_EXISTS;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityCategoryRelationDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.category.CategoryMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntityCategoryRelationMapper;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryService;
import cn.cheers.x.module.dynamicbusiness.service.entity.core.EntityCoreService;
import cn.cheers.x.module.dynamicbusiness.util.SparseSortUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 实体-分类关联服务实现类
 *
 * <p>专门负责 Entity 和 Category 之间的多对多关联操作。
 * 遵循单一职责原则，不包含实体或分类的业务逻辑。</p>
 *
 * @author 基础服务模块
 */
@Service
@Validated
@Slf4j
public class EntityCategoryRelationServiceImpl implements EntityCategoryRelationService {


    @Resource
    private EntityCoreService entityCoreService;

    @Resource
    @Lazy
    private CategoryService categoryService;

    @Resource
    private EntityCategoryRelationMapper relationMapper;

    @Resource
    private CategoryMapper categoryMapper;

    // ==================== 单实体-单分类操作 ====================

    /**
     * 关联单个实体到单个分类。
     *
     * <p>处理步骤：</p>
     * <ol>
     *   <li>校验参数与业务类型编码；</li>
     *   <li>校验实体与分类存在性；</li>
     *   <li>若存在软删除记录则恢复并更新排序；</li>
     *   <li>若已存在有效关联则直接返回成功；</li>
     *   <li>否则新增关系并分配分类内排序。</li>
     * </ol>
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EntityCategoryAssociationRespVO associate(Long entityId, Long categoryId, String entityTypeCode) {
        long startTime = System.currentTimeMillis();
        /** 校验参数 */
        validateEntityIdNotNull(entityId);
        validateCategoryIdNotNull(categoryId);
        validateEntityTypeCodeNotBlank(entityTypeCode);
        validateEntityExists(entityId, entityTypeCode);
        validateCategoryExists(categoryId);

        // 如果存在软删除的关联，优先恢复软删除关联，避免唯一键冲突
        int restored = relationMapper.restoreDeletedRelation(
                entityId,
                categoryId,
                entityTypeCode,
                // 排序策略：增量关联不全量重排；恢复/新增均置于当前分类末尾。
                nextRelationSort(categoryId));
        if (restored > 0) {
            log.info("重新启用已删除的实体-分类关联并更新排序: entityId={}, categoryId={}, entityTypeCode={}",
                    entityId, categoryId, entityTypeCode);
            return EntityCategoryAssociationRespVO.builder()
                    .operationType("ASSOCIATE")
                    .entityId(entityId)
                    .entityExists(true)
                    .totalCount(1)
                    .successCount(1)
                    .failCount(0)
                    .successCategoryIds(Collections.singletonList(categoryId))
                    .failItems(Collections.emptyList())
                    .executionTime(System.currentTimeMillis() - startTime)
                    .build();
        }

        // 检查有效关联是否已存在
        if (existsRelation(entityId, categoryId, entityTypeCode)) {
            log.debug("关联已存在: entityId={}, categoryId={}, entityTypeCode={}", entityId, categoryId, entityTypeCode);
            return EntityCategoryAssociationRespVO.builder()
                    .operationType("ASSOCIATE")
                    .entityId(entityId)
                    .entityExists(true)
                    .totalCount(1)
                    .successCount(1)
                    .failCount(0)
                    .failItems(Collections.emptyList())
                    .executionTime(System.currentTimeMillis() - startTime)
                    .build();
        }

        // 创建 entity-category 关联：新增时写入 sort（分类内排序）
        EntityCategoryRelationDO relation = EntityCategoryRelationDO.builder()
                .entityId(entityId)
                .categoryId(categoryId)
                .entityTypeCode(entityTypeCode)
                .sort(nextRelationSort(categoryId))
                .build();
        relationMapper.insert(relation);

        log.info("创建实体-分类关联: entityId={}, categoryId={}, entityTypeCode={}", entityId, categoryId, entityTypeCode);
        return EntityCategoryAssociationRespVO.builder()
                .operationType("ASSOCIATE")
                .entityId(entityId)
                .entityExists(true)
                .totalCount(1)
                .successCount(1)
                .failCount(0)
                .failItems(Collections.emptyList())
                .executionTime(System.currentTimeMillis() - startTime)
                .build();
    }

    /**
     * 解除单个实体与单个分类的关联。
     *
     * <p>处理步骤：</p>
     * <ol>
     *   <li>参数空值校验；</li>
     *   <li>校验业务类型编码；</li>
     *   <li>按 entityId + categoryId + entityTypeCode 删除关联；</li>
     *   <li>返回统一响应结构（含统计与耗时）。</li>
     * </ol>
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EntityCategoryAssociationRespVO disassociate(Long entityId, Long categoryId, String entityTypeCode) {
        long startTime = System.currentTimeMillis();
        if (entityId == null || categoryId == null) {
            return EntityCategoryAssociationRespVO.builder()
                    .operationType("DISASSOCIATE")
                    .entityId(entityId)
                    .entityExists(true)
                    .totalCount(1)
                    .successCount(0)
                    .failCount(1)
                    .failItems(List.of(CategoryAssociationBaseRespVO.FailItem.builder()
                            .categoryId(categoryId)
                            .reason("参数不能为空")
                            .errorCode(400)
                            .build()))
                    .executionTime(System.currentTimeMillis() - startTime)
                    .build();
        }
        validateEntityTypeCodeNotBlank(entityTypeCode);
        markEntitiesExcludedFromCategory(List.of(entityId), categoryId, entityTypeCode);
        log.info("删除实体-分类关联: entityId={}, categoryId={}, entityTypeCode={}", entityId, categoryId, entityTypeCode);
        return EntityCategoryAssociationRespVO.builder()
                .operationType("DISASSOCIATE")
                .entityId(entityId)
                .entityExists(true)
                .totalCount(1)
                .successCount(1)
                .failCount(0)
                .failItems(Collections.emptyList())
                .executionTime(System.currentTimeMillis() - startTime)
                .build();
    }

    /**
     * 判断实体与分类关联是否存在。
     */
    @Override
    public boolean existsRelation(Long entityId, Long categoryId, String entityTypeCode) {
        if (entityId == null || categoryId == null) {
            return false;
        }
        validateEntityTypeCodeNotBlank(entityTypeCode);
        return relationMapper.selectByEntityAndCategory(entityId, categoryId, entityTypeCode) != null;
    }

    // ==================== 单实体-多分类操作 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EntityCategoryAssociationRespVO batchAssociateEntityToCategories(
            Long entityId, List<Long> categoryIds, String entityTypeCode) {
        // 排序策略：批量增量关联不全量重排；仅对恢复/新增记录分配新的尾部 sort。
        long startTime = System.currentTimeMillis();
        // 1. 验证实体存在
        // Step 1: 校验实体存在性（不存在直接返回失败结构）
        boolean entityExists = isEntityExists(entityId, entityTypeCode);
        if (!entityExists) {
            return EntityCategoryAssociationRespVO.builder()
                    .operationType("ASSOCIATE")
                    .entityId(entityId)
                    .entityExists(false)
                    .totalCount(categoryIds != null ? categoryIds.size() : 0)
                    .successCount(0)
                    .failCount(categoryIds != null ? categoryIds.size() : 0)
                    .successCategoryIds(Collections.emptyList())
                    .failItems(List.of(CategoryAssociationBaseRespVO.FailItem.builder()
                            .categoryId(null)
                            .reason("实体不存在 (ID: " + entityId + ", entityTypeCode: " + entityTypeCode + ")")
                            .errorCode(ENTITY_NOT_EXISTS.getCode())
                            .build()))
                    .executionTime(System.currentTimeMillis() - startTime)
                    .build();
        }

        // 2. 执行关联操作（传入实体业务类型用于跨业务关联验证）
        BatchAssociateResult associateResult = batchAssociateInternal(entityId, categoryIds, entityTypeCode);

        return EntityCategoryAssociationRespVO.builder()
                .operationType("ASSOCIATE")
                .entityId(entityId)
                .entityExists(true)
                .totalCount(categoryIds != null ? categoryIds.size() : 0)
                .successCount(associateResult.successCount())
                .failCount(associateResult.failItems().size())
                .successCategoryIds(associateResult.successCategoryIds())
                .failItems(associateResult.failItems())
                .executionTime(System.currentTimeMillis() - startTime)
                .build();
    }

    /**
     * 批量解除单个实体与指定的多个分类的关联。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EntityCategoryAssociationRespVO batchDisassociateEntityFromCategories(Long entityId, List<Long> categoryIds, String entityTypeCode) {
        long startTime = System.currentTimeMillis();
        if (entityId == null || categoryIds == null || categoryIds.isEmpty()) {
            return EntityCategoryAssociationRespVO.builder()
                    .operationType("BATCH_DISASSOCIATE")
                    .entityId(entityId)
                    .entityExists(true)
                    .totalCount(categoryIds != null ? categoryIds.size() : 0)
                    .successCount(0)
                    .failCount(categoryIds != null ? categoryIds.size() : 0)
                    .successCategoryIds(Collections.emptyList())
                    .failItems(List.of(CategoryAssociationBaseRespVO.FailItem.builder()
                            .categoryId(null)
                            .reason("参数不能为空")
                            .errorCode(400)
                            .build()))
                    .executionTime(System.currentTimeMillis() - startTime)
                    .build();
        }

        validateEntityTypeCodeNotBlank(entityTypeCode);
        for (Long categoryId : categoryIds) {
            // 仅删除 entity-category 关联（按业务类型过滤）
            relationMapper.deleteByEntityAndCategory(entityId, categoryId, entityTypeCode);
        }

        log.info("批量取消实体与分类的关联: entityId={}, categoryIds={}, entityTypeCode={}", entityId, categoryIds, entityTypeCode);
        return EntityCategoryAssociationRespVO.builder()
                .operationType("BATCH_DISASSOCIATE")
                .entityId(entityId)
                .entityExists(true)
                .totalCount(categoryIds.size())
                .successCount(categoryIds.size())
                .failCount(0)
                .successCategoryIds(categoryIds.stream().filter(Objects::nonNull).distinct().toList())
                .failItems(Collections.emptyList())
                .executionTime(System.currentTimeMillis() - startTime)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EntityCategoryAssociationRespVO updateAssociation(
            Long entityId, List<Long> categoryIds, String entityTypeCode) {
        // 排序策略：更新实体关联的分类时，不重排未变关联；仅对恢复/新增关联分配新的 sort。
        long startTime = System.currentTimeMillis();
        List<CategoryAssociationBaseRespVO.FailItem> failItems = new ArrayList<>();

        // 1. 验证实体存在
        boolean entityExists = isEntityExists(entityId, entityTypeCode);
        if (!entityExists) {
            return EntityCategoryAssociationRespVO.builder()
                    .operationType("UPDATE")
                    .entityId(entityId)
                    .entityExists(false)
                    .totalCount(categoryIds != null ? categoryIds.size() : 0)
                    .successCount(0)
                    .failCount(categoryIds != null ? categoryIds.size() : 0)
                    .successCategoryIds(Collections.emptyList())
                    .failItems(List.of(EntityCategoryAssociationRespVO.FailItem.builder()
                            .categoryId(null)
                            .reason("实体不存在 (ID: " + entityId + ", entityTypeCode: " + entityTypeCode + ")")
                            .errorCode(ENTITY_NOT_EXISTS.getCode())
                            .build()))
                    .executionTime(System.currentTimeMillis() - startTime)
                    .build();
        }

        // Step 2: 标准化输入（空列表/去重保序）并校验分类存在性
        List<Long> targetCategoryIds = categoryIds == null ? Collections.emptyList() : categoryIds;
        List<Long> deduplicatedTarget = deduplicateKeepOrder(targetCategoryIds);

        Set<Long> existingCategoryIds = categoryService.filterExistingCategoryIds(deduplicatedTarget);
        List<Long> validCategoryIds = deduplicatedTarget.stream().filter(existingCategoryIds::contains).toList();
        for (Long categoryId : deduplicatedTarget) {
            if (!existingCategoryIds.contains(categoryId)) {
                failItems.add(EntityCategoryAssociationRespVO.FailItem.builder()
                        .categoryId(categoryId)
                        .reason("分类不存在 (ID: " + categoryId + ")")
                        .errorCode(404)
                        .build());
            }
        }

        // Step 3: 读取当前有效关联（按 entityId，再在内存中过滤 entityTypeCode）
        List<EntityCategoryRelationDO> activeRelations = relationMapper.selectByEntityId(entityId);
        Set<Long> activeCategoryIds = activeRelations.stream()
                .filter(rel -> entityTypeCode.equals(rel.getEntityTypeCode()))
                .map(EntityCategoryRelationDO::getCategoryId)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        // Step 4: 读取目标分类上的历史关联（含 deleted）并提取恢复候选
        List<EntityCategoryRelationDO> existingRelationsOnTarget = validCategoryIds.isEmpty()
                ? Collections.emptyList()
                : relationMapper.selectByEntityAndCategoryIdsIncludingDeleted(entityId, validCategoryIds, entityTypeCode);
        Set<Long> restoreCandidates = existingRelationsOnTarget.stream()
                .filter(rel -> Boolean.TRUE.equals(rel.getDeleted()))
                .map(EntityCategoryRelationDO::getCategoryId)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        // Step 5: 差集分流（删除 / 恢复 / 新增）
        List<Long> toDelete = activeCategoryIds.stream().filter(id -> !validCategoryIds.contains(id)).toList();
        List<Long> toRestore = validCategoryIds.stream().filter(restoreCandidates::contains).toList();
        List<Long> toInsert = validCategoryIds.stream()
                .filter(id -> !activeCategoryIds.contains(id) && !restoreCandidates.contains(id))
                .toList();

        // Step 6: 删除移除项（仅删除差集，不影响 toKeep）
        if (!toDelete.isEmpty()) {
            relationMapper.deleteByEntityAndCategoryIds(entityId, toDelete);
        }
        // Step 7: 先完成状态变更（恢复 + 新增），不在此阶段分段编号
        if (!toRestore.isEmpty()) {
            relationMapper.restoreDeletedRelationsBatch(entityId, toRestore, entityTypeCode);
        }
        if (!toInsert.isEmpty()) {
            List<EntityCategoryRelationDO> relationsToInsert = new ArrayList<>();
            for (Long categoryId : toInsert) {
                relationsToInsert.add(EntityCategoryRelationDO.builder()
                        .entityId(entityId)
                        .categoryId(categoryId)
                        .entityTypeCode(entityTypeCode)
                        .sort(0)
                        .build());
            }
            relationMapper.insertBatchRelations(relationsToInsert);
        }

        // Step 8: 按目标顺序统一编号（仅对变更项：恢复+新增）
        List<Long> changedOrdered = validCategoryIds.stream()
                .filter(id -> toRestore.contains(id) || toInsert.contains(id))
                .toList();
        if (!changedOrdered.isEmpty()) {
            Map<Long, Integer> baseSortMap = calcBaseSortByCategoryIds(changedOrdered);
            Map<Long, Integer> sortOffsetMap = new HashMap<>();
            for (Long categoryId : changedOrdered) {
                relationMapper.updateSortByEntityAndCategory(entityId, categoryId,
                        calculateSortWithBase(baseSortMap, sortOffsetMap, categoryId), entityTypeCode);
            }
        }

        // Step 9: 返回执行结果（成功=有效目标分类数，失败=无效分类数）
        int successCount = validCategoryIds.size();
        return EntityCategoryAssociationRespVO.builder()
                .operationType("UPDATE")
                .entityId(entityId)
                .entityExists(true)
                .totalCount(targetCategoryIds.size())
                .successCount(successCount)
                .failCount(failItems.size())
                .successCategoryIds(validCategoryIds)
                .failItems(failItems)
                .executionTime(System.currentTimeMillis() - startTime)
                .build();
    }

    // ==================== 多实体-单分类操作 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BatchEntityCategoryAssociationRespVO batchAssociateEntitiesToCategory(
            List<Long> entityIds, Long categoryId, String entityTypeCode) {
        long startTime = System.currentTimeMillis();

        // 验证分类存在
        if (!categoryService.existsById(categoryId)) {
            // Step 3: 汇总并返回批量执行结果
        return BatchEntityCategoryAssociationRespVO.builder()
                    .operationType("BATCH_ASSOCIATE")
                    .totalEntityCount(entityIds != null ? entityIds.size() : 0)
                    .totalCategoryCount(1)
                    .successEntityCount(0)
                    .failEntityCount(entityIds != null ? entityIds.size() : 0)
                    .notFoundCategoryIds(List.of(categoryId))
                    .executionTime(System.currentTimeMillis() - startTime)
                    .build();
        }

        // 转换为多对多操作
        return batchAssociateEntitiesToCategories(entityIds, List.of(categoryId), entityTypeCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BatchEntityCategoryAssociationRespVO batchDisassociateEntitiesFromCategory(
            List<Long> entityIds, Long categoryId, String entityTypeCode) {
        long startTime = System.currentTimeMillis();

        validateEntityTypeCodeNotBlank(entityTypeCode);

        if (entityIds == null || entityIds.isEmpty() || categoryId == null) {
            return BatchEntityCategoryAssociationRespVO.builder()
                    .operationType("BATCH_DISASSOCIATE")
                    .totalEntityCount(0)
                    .totalCategoryCount(1)
                    .successEntityCount(0)
                    .failEntityCount(0)
                    .executionTime(System.currentTimeMillis() - startTime)
                    .build();
        }

        int successCount = markEntitiesExcludedFromCategory(entityIds, categoryId, entityTypeCode);

        log.info("批量取消实体与分类的关联: entityIds={}, categoryId={}, entityTypeCode={}", entityIds, categoryId, entityTypeCode);

        return BatchEntityCategoryAssociationRespVO.builder()
                .operationType("BATCH_DISASSOCIATE")
                .totalEntityCount(entityIds.size())
                .totalCategoryCount(1)
                .successEntityCount(successCount)
                .failEntityCount(0)
                .executionTime(System.currentTimeMillis() - startTime)
                .build();
    }

    /**
     * 解除实体与分类关联：删除有效关联；若无有效关联则写入 deleted=true 排除标记，
     * 防止实体仍通过「型号挂分类」出现在该分类范围内。
     */
    private int markEntitiesExcludedFromCategory(List<Long> entityIds, Long categoryId, String entityTypeCode) {
        int successCount = 0;
        for (Long entityId : entityIds) {
            if (entityId == null) {
                continue;
            }
            EntityCategoryRelationDO active = relationMapper.selectByEntityAndCategory(entityId, categoryId, entityTypeCode);
            if (active != null) {
                relationMapper.deleteByEntityAndCategory(entityId, categoryId, entityTypeCode);
            } else {
                List<EntityCategoryRelationDO> deletedRelations = relationMapper
                        .selectByEntityAndCategoryIdsIncludingDeleted(entityId, List.of(categoryId), entityTypeCode);
                if (deletedRelations.isEmpty()) {
                    relationMapper.insert(EntityCategoryRelationDO.builder()
                            .entityId(entityId)
                            .categoryId(categoryId)
                            .entityTypeCode(entityTypeCode)
                            .sort(0)
                            .build());
                    relationMapper.deleteByEntityAndCategory(entityId, categoryId, entityTypeCode);
                }
            }
            successCount++;
        }
        return successCount;
    }


    // ==================== 多实体-多分类操作 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BatchEntityCategoryAssociationRespVO batchAssociateEntitiesToCategories(
            List<Long> entityIds, List<Long> categoryIds, String entityTypeCode) {
        // 排序策略：批量增量关联不做全量重排；对恢复/新增记录分配尾部 sort。
        long startTime = System.currentTimeMillis();

        validateEntityTypeCodeNotBlank(entityTypeCode);

        if (entityIds == null || entityIds.isEmpty() || categoryIds == null || categoryIds.isEmpty()) {
            return BatchEntityCategoryAssociationRespVO.builder()
                    .operationType("BATCH_ASSOCIATE")
                    .totalEntityCount(entityIds != null ? entityIds.size() : 0)
                    .totalCategoryCount(categoryIds != null ? categoryIds.size() : 0)
                    .successEntityCount(0)
                    .failEntityCount(0)
                    .executionTime(System.currentTimeMillis() - startTime)
                    .build();
        }

        // 1. 批量验证实体存在性
        List<Long> notFoundEntityIds = new ArrayList<>();
        Set<Long> existingEntityIds = collectExistingEntityIds(entityIds, entityTypeCode, notFoundEntityIds);

        // 2. 批量验证分类存在性
        List<Long> notFoundCategoryIds = new ArrayList<>();
        List<Long> validCategoryIds = collectValidCategoryIds(categoryIds, notFoundCategoryIds);

        // 3. 批量查询现有关联（优化：避免 N+1 查询问题）
        List<EntityCategoryRelationDO> existingRelations = relationMapper.selectByEntityIdsAndCategoryIds(
                new ArrayList<>(existingEntityIds),
                validCategoryIds);

        // 4. 构建现有关联索引 + 分类 max(sort)
        Map<Long, Set<Long>> existingByEntity = new HashMap<>();
        Map<Long, Integer> baseSortMap = new HashMap<>();
        for (EntityCategoryRelationDO relation : existingRelations) {
            existingByEntity
                    .computeIfAbsent(relation.getEntityId(), k -> new HashSet<>())
                    .add(relation.getCategoryId());
            if (relation.getCategoryId() != null && relation.getSort() != null) {
                Integer current = baseSortMap.get(relation.getCategoryId());
                int next = (current == null) ? relation.getSort() : Math.max(current, relation.getSort());
                baseSortMap.put(relation.getCategoryId(), next);
            }
        }

        // 5. 计算需要插入的新关联（同时统计每个实体的成功/失败）
        List<EntityCategoryRelationDO> relationsToInsert = new ArrayList<>();
        Map<Long, EntityResultData> entityResultDataMap = new HashMap<>();
        Map<Long, Integer> insertCountByEntity = new HashMap<>();

        for (Long entityId : entityIds) {
            if (!existingEntityIds.contains(entityId)) {
                entityResultDataMap.put(entityId, new EntityResultData(
                        entityId, false, 0, categoryIds.size(), "实体不存在"));
                continue;
            }

            Set<Long> existedCategories = existingByEntity.getOrDefault(entityId, Collections.emptySet());
            int entitySuccessCount = validCategoryIds.size();
            int entityFailCount = notFoundCategoryIds.size();

            for (Long categoryId : validCategoryIds) {
                if (!existedCategories.contains(categoryId)) {
                    relationsToInsert.add(EntityCategoryRelationDO.builder()
                            .entityId(entityId)
                            .categoryId(categoryId)
                            .entityTypeCode(entityTypeCode)
                            .build());
                    Integer currentInsert = insertCountByEntity.get(entityId);
                    insertCountByEntity.put(entityId, currentInsert == null ? 1 : currentInsert + 1);
                }
            }

            entityResultDataMap.put(entityId, new EntityResultData(
                    entityId, true, entitySuccessCount, entityFailCount, null));
        }

        // 6. 批量插入新关联（按分类分组分配 sort）
        if (!relationsToInsert.isEmpty()) {
            Map<Long, Integer> sortOffsetMap = new HashMap<>();
            for (EntityCategoryRelationDO relation : relationsToInsert) {
                relation.setSort(calculateSortWithBase(baseSortMap, sortOffsetMap, relation.getCategoryId()));
            }
            try {
                relationMapper.insertBatchRelations(relationsToInsert);
            } catch (Exception e) {
                log.error("批量插入关联失败: {}", e.getMessage(), e);
                for (Map.Entry<Long, Integer> entry : insertCountByEntity.entrySet()) {
                    EntityResultData data = entityResultDataMap.get(entry.getKey());
                    if (data != null && data.entityExists) {
                        entityResultDataMap.put(entry.getKey(), new EntityResultData(
                                entry.getKey(), true, data.successCount - entry.getValue(),
                                data.failCount + entry.getValue(), null));
                    }
                }
            }
        }

        // 7. 构建返回结果
        List<BatchEntityCategoryAssociationRespVO.EntityResult> entityResults = new ArrayList<>();
        int successEntityCount = 0;
        int failEntityCount = 0;

        for (Long entityId : entityIds) {
            EntityResultData data = entityResultDataMap.get(entityId);
            if (data != null) {
                BatchEntityCategoryAssociationRespVO.EntityResult result = BatchEntityCategoryAssociationRespVO.EntityResult.builder()
                        .entityId(data.entityId)
                        .entityExists(data.entityExists)
                        .successCount(data.successCount)
                        .failCount(data.failCount)
                        .errorMessage(data.errorMessage)
                        .build();
                entityResults.add(result);
                if (data.entityExists && data.failCount == 0) {
                    successEntityCount++;
                } else {
                    failEntityCount++;
                }
            }
        }

        log.info("批量关联实体到分类: entityIds={}, categoryIds={}, successEntityCount={}, failEntityCount={}",
                entityIds, categoryIds, successEntityCount, failEntityCount);

        return BatchEntityCategoryAssociationRespVO.builder()
                .operationType("BATCH_ASSOCIATE")
                .totalEntityCount(entityIds.size())
                .totalCategoryCount(categoryIds.size())
                .successEntityCount(successEntityCount)
                .failEntityCount(failEntityCount)
                .notFoundEntityIds(notFoundEntityIds)
                .notFoundCategoryIds(notFoundCategoryIds)
                .entityResults(entityResults)
                .executionTime(System.currentTimeMillis() - startTime)
                .build();
    }

    /**
     * 批量解除“多个实体 × 多个分类”的关联。
     *
     * <p>实现策略：不做关联存在性预校验，直接执行删除（幂等）。</p>
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BatchEntityCategoryAssociationRespVO batchDisassociateEntitiesFromCategories(
            List<Long> entityIds, List<Long> categoryIds, String entityTypeCode) {
        long startTime = System.currentTimeMillis();

        if (entityIds == null || entityIds.isEmpty() || categoryIds == null || categoryIds.isEmpty()) {
            return BatchEntityCategoryAssociationRespVO.builder()
                    .operationType("BATCH_DISASSOCIATE")
                    .totalEntityCount(entityIds != null ? entityIds.size() : 0)
                    .totalCategoryCount(categoryIds != null ? categoryIds.size() : 0)
                    .successEntityCount(0)
                    .failEntityCount(0)
                    .executionTime(System.currentTimeMillis() - startTime)
                    .build();
        }

        // 执行删除操作（不验证存在性，直接删除）
        List<BatchEntityCategoryAssociationRespVO.EntityResult> entityResults = new ArrayList<>();
        int successEntityCount = 0;

        for (Long entityId : entityIds) {
            int entitySuccessCount = 0;
            for (Long categoryId : categoryIds) {
                relationMapper.deleteByEntityAndCategory(entityId, categoryId, entityTypeCode);
                entitySuccessCount++;
            }

            entityResults.add(BatchEntityCategoryAssociationRespVO.EntityResult.builder()
                    .entityId(entityId)
                    .entityExists(true)
                    .successCount(entitySuccessCount)
                    .failCount(0)
                    .build());
            successEntityCount++;
        }

        log.info("批量取消实体与分类的关联: entityIds={}, categoryIds={}, entityTypeCode={}", entityIds, categoryIds, entityTypeCode);

        return BatchEntityCategoryAssociationRespVO.builder()
                .operationType("BATCH_DISASSOCIATE")
                .totalEntityCount(entityIds.size())
                .totalCategoryCount(categoryIds.size())
                .successEntityCount(successEntityCount)
                .failEntityCount(0)
                .entityResults(entityResults)
                .executionTime(System.currentTimeMillis() - startTime)
                .build();
    }

    /**
     * 批量替换多个实体的分类关联。
     *
     * <p>处理流程：先批量校验实体/分类存在性，再按实体维度执行“先清空旧关联，再写入新关联”。</p>
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BatchEntityCategoryAssociationRespVO batchUpdateAssociation(
            List<Long> entityIds, List<Long> categoryIds, String entityTypeCode) {
        // 排序策略：批量更新关联时，不重排未变关联；仅对恢复/新增关联分配新的 sort。
        long startTime = System.currentTimeMillis();

        if (entityIds == null || entityIds.isEmpty()) {
            return BatchEntityCategoryAssociationRespVO.builder()
                    .operationType("BATCH_UPDATE")
                    .totalEntityCount(0)
                    .totalCategoryCount(categoryIds != null ? categoryIds.size() : 0)
                    .successEntityCount(0)
                    .failEntityCount(0)
                    .executionTime(System.currentTimeMillis() - startTime)
                    .build();
        }

        // Step 1: 标准化输入并初始化聚合结果容器
        List<Long> targetCategoryIds = categoryIds == null ? Collections.emptyList() : categoryIds;
        List<BatchEntityCategoryAssociationRespVO.EntityResult> entityResults = new ArrayList<>();
        List<Long> notFoundEntityIds = new ArrayList<>();
        List<Long> notFoundCategoryIds = new ArrayList<>();

        int successEntityCount = 0;
        int failEntityCount = 0;

        // Step 2: 逐实体复用 updateAssociation（统一差集逻辑）
        for (Long entityId : entityIds) {
            EntityCategoryAssociationRespVO single = updateAssociation(entityId, targetCategoryIds, entityTypeCode);
            int entityFailCount = single.getFailCount();
            int entitySuccessCount = single.getSuccessCount();
            boolean entityExists = Boolean.TRUE.equals(single.getEntityExists());

            if (!entityExists) {
                notFoundEntityIds.add(entityId);
            }
            if (single.getFailItems() != null) {
                single.getFailItems().stream()
                        .map(CategoryAssociationBaseRespVO.FailItem::getCategoryId)
                        .filter(Objects::nonNull)
                        .forEach(notFoundCategoryIds::add);
            }

            entityResults.add(BatchEntityCategoryAssociationRespVO.EntityResult.builder()
                    .entityId(entityId)
                    .entityExists(entityExists)
                    .successCount(entitySuccessCount)
                    .failCount(entityFailCount)
                    .errorMessage(entityExists ? null : "实体不存在")
                    .build());

            if (entityExists && entityFailCount == 0) {
                successEntityCount++;
            } else {
                failEntityCount++;
            }
        }

        return BatchEntityCategoryAssociationRespVO.builder()
                .operationType("BATCH_UPDATE")
                .totalEntityCount(entityIds.size())
                .totalCategoryCount(targetCategoryIds.size())
                .successEntityCount(successEntityCount)
                .failEntityCount(failEntityCount)
                .notFoundEntityIds(deduplicateKeepOrder(notFoundEntityIds))
                .notFoundCategoryIds(deduplicateKeepOrder(notFoundCategoryIds))
                .entityResults(entityResults)
                .executionTime(System.currentTimeMillis() - startTime)
                .build();
    }


    // ==================== 查询操作 ====================

    /**
     * 获取实体在指定业务类型下关联的分类ID列表。
     *
     * <p>实现方式：先查实体的全部分类关联，再按 entityTypeCode 做反向校验过滤。</p>
     */
    @Override
    public List<Long> listCategoryIdsByEntityId(Long entityId, String entityTypeCode) {
        if (entityId == null || entityTypeCode == null || entityTypeCode.isBlank()) {
            return new ArrayList<>();
        }

        List<EntityCategoryRelationDO> relations = relationMapper.selectByEntityId(entityId);
        if (relations == null || relations.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> categoryIds = relations.stream()
                .map(EntityCategoryRelationDO::getCategoryId)
                .filter(Objects::nonNull)
                .toList();
        if (categoryIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 通过分类 -> entityIds(按业务过滤) 反向校验，避免返回其它业务实体关联到的分类
        List<Long> result = new ArrayList<>();
        for (Long categoryId : categoryIds) {
            List<Long> entityIds = listEntityIdsByCategoryIdOnly(categoryId, entityTypeCode);
            if (entityIds.contains(entityId)) {
                result.add(categoryId);
            }
        }
        return result;
    }

    /**
     * 查询“仅当前分类节点”的实体ID列表（不含子分类）。
     *
     * <p><b>调用场景</b>：右侧实体列表只绑定当前选中分类，不做子树扩展时。</p>
     *
     * <p><b>排序语义</b>：由 Mapper 保证 relation.sort ASC，relation.id ASC 兜底。</p>
     *
     * <p><b>注意</b>：若需要“当前分类 + 子分类”范围，请不要调用本方法，
     * 应先展开 categoryIds 再调用 {@link #listEntityIdsByCategoryIdsWithDescendants(List, String)}。</p>
     */
    @Override
    public List<Long> listEntityIdsByCategoryIdOnly(Long categoryId, String entityTypeCode) {
        if (categoryId == null || entityTypeCode == null || entityTypeCode.isBlank()) {
            return new ArrayList<>();
        }

        // 单分类快路径：直接使用 Mapper 的“分类内 sort,id”排序结果，避免多分类编排开销。
        List<EntityCategoryRelationDO> relations = relationMapper.selectByCategoryIdAndEntityType(categoryId, entityTypeCode);
        if (relations == null || relations.isEmpty()) {
            return new ArrayList<>();
        }
        // 把关系数据转换为entityId列表，并去重
        List<Long> orderedEntityIds = new ArrayList<>();
        Set<Long> seen = new HashSet<>();
        for (EntityCategoryRelationDO relation : relations) {
            Long entityId = relation.getEntityId();
            if (entityId != null && seen.add(entityId)) {
                orderedEntityIds.add(entityId);
            }
        }
        return orderedEntityIds;
    }

    /**
     * 单分类（含子树）查询实体ID列表。
     *
     * <p>步骤：先由 CategoryService 展开子树分类ID，再交给关系层做排序与去重。</p>
     */
    @Override
    public List<Long> listEntityIdsByCategoryIdWithDescendants(Long categoryId, String categoryTypeCode, String entityTypeCode) {
        if (categoryId == null || entityTypeCode == null || entityTypeCode.isBlank()) {
            return new ArrayList<>();
        }
        // 单分类含子树：使用调用方（页面视图）传入的 categoryTypeCode 做子树展开。
        List<Long> allCategoryIds = categoryService.getAllCategoryIdsIncludingChildren(categoryId, categoryTypeCode);
        return resolveOrderedEntityIds(allCategoryIds, entityTypeCode);
    }


    // ==================== 级联删除操作 ====================

    /**
     * 删除单个实体的所有分类关联（级联清理入口）。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAllByEntityId(Long entityId) {
        if (entityId == null) {
            return;
        }

        relationMapper.deleteByEntityId(entityId);
        log.info("删除实体的所有分类关联: entityId={}", entityId);
    }

    /**
     * 批量删除多个实体的所有分类关联（级联清理入口）。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAllByEntityIds(List<Long> entityIds) {
        if (entityIds == null || entityIds.isEmpty()) {
            return;
        }

        relationMapper.deleteByEntityIds(entityIds);
        log.info("批量删除实体的所有分类关联: entityIds={}", entityIds);
    }

    /**
     * 删除单个分类的所有实体关联（全业务清理，级联清理入口）。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAllByCategoryId(Long categoryId) {
        if (categoryId == null) {
            return;
        }

        relationMapper.deleteByCategoryId(categoryId);
        log.info("删除分类的所有实体关联(全业务): categoryId={}", categoryId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAllByCategoryIdInBusiness(Long categoryId, String entityTypeCode) {
        if (categoryId == null || entityTypeCode == null || entityTypeCode.isBlank()) {
            return;
        }

        relationMapper.deleteByCategoryId(categoryId, entityTypeCode);
        log.info("删除分类的所有实体关联(按业务隔离): categoryId={}, entityTypeCode={}", categoryId, entityTypeCode);
    }

    /**
     * 批量删除多个分类的所有实体关联（全业务清理级联清理入口）。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAllByCategoryIds(List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return;
        }

        relationMapper.deleteByCategoryIds(categoryIds);
        log.info("批量删除分类的所有实体关联(全业务): categoryIds={}", categoryIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAllByCategoryIdsInBusiness(List<Long> categoryIds, String entityTypeCode) {
        if (categoryIds == null || categoryIds.isEmpty() || entityTypeCode == null || entityTypeCode.isBlank()) {
            return;
        }

        relationMapper.deleteByCategoryIds(categoryIds, entityTypeCode);
        log.info("批量删除分类的所有实体关联(按业务隔离): categoryIds={}, entityTypeCode={}", categoryIds, entityTypeCode);
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 批量关联内部实现
     *
     * @param entityId 实体ID
     * @param categoryIds 分类ID列表
     * @param entityEntityType 实体所属业务类型（用于跨业务关联验证）
     * @return 关联执行结果
     */
    private BatchAssociateResult batchAssociateInternal(Long entityId, List<Long> categoryIds, String entityEntityType) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return new BatchAssociateResult(0, Collections.emptyList(), Collections.emptyList());
        }

        List<EntityCategoryAssociationRespVO.FailItem> failItems = new ArrayList<>();

        // 1) 分类Id的存在性校验（ 一次查询所有分类存在性 ）
        Set<Long> existingCategoryIds = categoryService.filterExistingCategoryIds(categoryIds);
        List<Long> validCategoryIds = new ArrayList<>();
        // 为了精确的失败明细（哪些分类不存在）
        for (Long categoryId : categoryIds) {
            if (existingCategoryIds.contains(categoryId)) {
                validCategoryIds.add(categoryId);
            } else {
                failItems.add(EntityCategoryAssociationRespVO.FailItem.builder()
                        .categoryId(categoryId)
                        .reason("分类不存在 (ID: " + categoryId + ")")
                        .errorCode(404)
                        .build());
            }
        }
        if (validCategoryIds.isEmpty()) {
            return new BatchAssociateResult(0, Collections.emptyList(), failItems);
        }

        // 2) 查询目标分类上的现有关联（含 deleted），一次分流出三集合
        List<EntityCategoryRelationDO> existingRelations = relationMapper
                // mybatis-plus 的配置过滤了 deleted=true 的记录，所以需要显式查询包含 deleted=true 的记录
                .selectByEntityAndCategoryIdsIncludingDeleted(entityId, validCategoryIds, entityEntityType);

        Set<Long> alreadyActive = new LinkedHashSet<>();
        Set<Long> restoreCandidates = new LinkedHashSet<>();
        for (EntityCategoryRelationDO relation : existingRelations) {
            Long categoryId = relation.getCategoryId();
            if (Boolean.TRUE.equals(relation.getDeleted())) {
                restoreCandidates.add(categoryId);
            } else {
                alreadyActive.add(categoryId);
            }
        }

        // 3) 三集合分流：已存在(不调整sort) / 可恢复 / 需新增
        List<Long> toRestore = new ArrayList<>();
        List<Long> toInsert = new ArrayList<>();
        int successCount = 0;
        List<Long> successCategoryIds = new ArrayList<>();
        for (Long categoryId : validCategoryIds) {
            if (alreadyActive.contains(categoryId)) {
                // 已存在关联：详情页批量更新场景不调整 sort
                successCount++;
                successCategoryIds.add(categoryId);
            } else if (restoreCandidates.contains(categoryId)) {
                toRestore.add(categoryId);
            } else {
                toInsert.add(categoryId);
            }
        }

        // 4) 批量恢复软删除（恢复后需重新编号 sort）
        if (!toRestore.isEmpty()) {
            int restored = relationMapper.restoreDeletedRelationsBatch(entityId, toRestore, entityEntityType);
            successCount += restored;
            successCategoryIds.addAll(toRestore);

            // 恢复记录重新编号 sort（不影响 alreadyActive）
            Map<Long, Integer> baseSortMap = calcBaseSortByCategoryIds(toRestore);
            Map<Long, Integer> sortOffsetMap = new HashMap<>();
            for (Long categoryId : toRestore) {
                relationMapper.updateSortByEntityAndCategory(
                        entityId,
                        categoryId,
                        calculateSortWithBase(baseSortMap, sortOffsetMap, categoryId),
                        entityEntityType
                );
            }
        }

        // 5) 批量新增剩余关联
        if (!toInsert.isEmpty()) {
            Map<Long, Integer> baseSortMap = calcBaseSortByCategoryIds(toInsert);
            Map<Long, Integer> sortOffsetMap = new HashMap<>();
            List<EntityCategoryRelationDO> relationsToInsert = new ArrayList<>();
            for (Long categoryId : toInsert) {
                relationsToInsert.add(EntityCategoryRelationDO.builder()
                        .entityId(entityId)
                        .categoryId(categoryId)
                        .entityTypeCode(entityEntityType)
                        .sort(calculateSortWithBase(baseSortMap, sortOffsetMap, categoryId))
                        .build());
            }
            try {
                relationMapper.insertBatchRelations(relationsToInsert);
                successCount += relationsToInsert.size();
                successCategoryIds.addAll(toInsert);
            } catch (Exception e) {
                for (Long categoryId : toInsert) {
                    failItems.add(EntityCategoryAssociationRespVO.FailItem.builder()
                            .categoryId(categoryId)
                            .reason("关联失败: " + e.getMessage())
                            .errorCode(ASSOCIATION_OPERATION_FAILED.getCode())
                            .build());
                }
            }
        }

        List<Long> distinctSuccessCategoryIds = successCategoryIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        log.info("批量关联实体到分类: entityId={}, categoryIds={}, successCount={}, failCount={}",
                entityId, categoryIds, successCount, failItems.size());
        return new BatchAssociateResult(successCount, distinctSuccessCategoryIds, failItems);
    }

    private record BatchAssociateResult(int successCount,
                                    List<Long> successCategoryIds,
                                    List<EntityCategoryAssociationRespVO.FailItem> failItems) {
    }

    // ==================== 按分类和业务类型查询实体 ====================

    /**
     * 分类关联排序结果载体（仅承载 ID 结果，不含实体详情）。
     *
     * <p>字段说明：</p>
     * <ul>
     *   <li>orderedEntityIds：已经按分类上下文排序后的实体ID序列；</li>
     *   <li>该序列可直接用于“先排序后分页”链路中的分页切片。</li>
     * </ul>
     *
     * <p>约束：</p>
     * <ul>
     *   <li>列表元素去重（同一实体命中多个分类关系时保留首个出现次序）；</li>
     *   <li>保持 Mapper 查询结果顺序（relation.sort ASC, relation.id ASC）。</li>
     * </ul>
     */
    private static class RelationOrderData {
        final List<Long> orderedEntityIds;

        RelationOrderData(List<Long> orderedEntityIds) {
            this.orderedEntityIds = orderedEntityIds;
        }
    }

    /**
     * 标准化函数：从分类范围构建“关联排序数据”。
     *
     * <p><b>这是“先排序后分页”的核心前置步骤</b>：</p>
     * <ol>
     *   <li>查询分类范围内所有关联关系（Mapper 已按 relation.sort + relation.id 排序）</li>
     *   <li>提取去重后的有序 entityId 列表（作为全局排序后的主序列）</li>
     *   <li>构建 entityId -> relation.sort 映射（供 VO 回填和防御性排序）</li>
     * </ol>
     *
     * <p>说明：同一实体若命中多个分类关系，当前策略取首个出现顺序（即最先命中的排序）。</p>
     */
    /**
     * 查询“多分类范围”的候选实体ID列表（含子树或搜索多命中场景）。
     *
     * <p><b>调用场景</b>：</p>
     * <ul>
     *   <li>Category 搜索命中多个分类节点</li>
     *   <li>单个分类但需要包含子分类范围（先由 CategoryService 展开 categoryIds）</li>
     * </ul>
     *
     * <p><b>输出语义</b>：返回保序去重后的 entityId 列表，顺序来自 relation.sort。</p>
     */
    @Override
    public List<Long> listEntityIdsByCategoryIdsOnly(List<Long> categoryIds, String entityTypeCode) {
        return resolveOrderedEntityIds(categoryIds, entityTypeCode);
    }

    @Override
    public List<Long> listEntityIdsByCategoryIdsWithDescendants(List<Long> categoryIds, String entityTypeCode) {
        if (categoryIds == null || categoryIds.isEmpty() || entityTypeCode == null || entityTypeCode.isBlank()) {
            return new ArrayList<>();
        }

        // 多分类含子树：对每个输入分类做子树展开，并按输入顺序稳定合并
        List<Long> expandedCategoryIds = expandCategoryIdsWithDescendants(categoryIds);
        return resolveOrderedEntityIds(expandedCategoryIds, entityTypeCode);
    }

    @Override
    public PageResult<Long> pageEntityIdsByCategoryIdOnly(Long categoryId, String entityTypeCode,
                                                            Integer pageNo, Integer pageSize) {
        List<Long> ordered = listEntityIdsByCategoryIdOnly(categoryId, entityTypeCode);
        return pageOrderedIds(ordered, pageNo, pageSize);
    }

    @Override
    public PageResult<Long> pageEntityIdsByCategoryIdWithDescendants(Long categoryId, String categoryTypeCode, String entityTypeCode,
                                                                        Integer pageNo, Integer pageSize) {
        List<Long> ordered = listEntityIdsByCategoryIdWithDescendants(categoryId, categoryTypeCode, entityTypeCode);
        return pageOrderedIds(ordered, pageNo, pageSize);
    }

    @Override
    public PageResult<Long> pageEntityIdsByCategoryIdsOnly(List<Long> categoryIds, String entityTypeCode,
                                                            Integer pageNo, Integer pageSize) {
        List<Long> ordered = listEntityIdsByCategoryIdsOnly(categoryIds, entityTypeCode);
        return pageOrderedIds(ordered, pageNo, pageSize);
    }

    @Override
    public PageResult<Long> pageEntityIdsByCategoryIdsWithDescendants(List<Long> categoryIds, String entityTypeCode,
                                                                        Integer pageNo, Integer pageSize) {
        List<Long> ordered = listEntityIdsByCategoryIdsWithDescendants(categoryIds, entityTypeCode);
        return pageOrderedIds(ordered, pageNo, pageSize);
    }

    @Override
    public PageResult<Long> pageEntityIdsByCategoryIdsOnlyDb(List<Long> categoryIds, String entityTypeCode,
                                                                Integer pageNo, Integer pageSize) {
        // 与 pageEntityIdsByCategoryIdsOnly 共用 Lambda 查询 + Service 层稳定排序分页，避免 Mapper 写原生 SQL。
        return pageEntityIdsByCategoryIdsOnly(categoryIds, entityTypeCode, pageNo, pageSize);
    }

    @Override
    public PageResult<Long> pageEntityIdsByCategoryIdsWithDescendantsDb(List<Long> categoryIds, String entityTypeCode,
                                                                            Integer pageNo, Integer pageSize) {
        if (categoryIds == null || categoryIds.isEmpty() || entityTypeCode == null || entityTypeCode.isBlank()) {
            return new PageResult<>(new ArrayList<>(), 0L);
        }
        List<Long> expandedCategoryIds = expandCategoryIdsWithDescendants(categoryIds);
        return pageEntityIdsByCategoryIdsOnlyDb(expandedCategoryIds, entityTypeCode, pageNo, pageSize);
    }

    /**
     * 统一解析“分类范围实体ID查询”结果（不负责子树展开）。
     *
     * @param categoryIds 分类ID集合（可为单个或多个）
     * @param entityTypeCode 业务类型编码
     * @return 稳定有序实体ID列表
     */
    private List<Long> resolveOrderedEntityIds(List<Long> categoryIds, String entityTypeCode) {
        if (categoryIds == null || categoryIds.isEmpty() || entityTypeCode == null || entityTypeCode.isBlank()) {
            return new ArrayList<>();
        }
        // 快路径：仅1个分类时直接走单分类查询，避免走多分类编排
        if (categoryIds.size() == 1) {
            return listEntityIdsByCategoryIdOnly(categoryIds.get(0), entityTypeCode);
        }
        // 多分类路径：按“categoryIds顺序 -> 分类内sort,id -> 稳定去重”输出
        RelationOrderData orderData = buildRelationOrderData(categoryIds, entityTypeCode);
        return orderData == null ? new ArrayList<>() : orderData.orderedEntityIds;
    }

    /**
     * 对多个输入分类做“含子树”展开，并按输入顺序稳定合并去重。
     */
    private List<Long> expandCategoryIdsWithDescendants(List<Long> categoryIds) {
        List<Long> mergedDescendantCategoryIds = new ArrayList<>();
        Set<Long> seen = new HashSet<>();
        if (categoryIds == null || categoryIds.isEmpty()) {
            return mergedDescendantCategoryIds;
        }

        // 批量展开：减少逐个 categoryId 调用带来的 N 次数据库往返
        Map<Long, List<Long>> descendantsByRoot = categoryService.getAllCategoryIdsIncludingChildrenBatch(categoryIds, null);

        // 按输入顺序稳定合并，保持跨分类优先级
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
     * 归一化页码：最小为 1。
     */
    private Integer normalizePageNo(Integer pageNo) {
        return (pageNo == null || pageNo < 1) ? 1 : pageNo;
    }

    /**
     * 归一化分页大小：最小为 1，默认 20。
     */
    private Integer normalizePageSize(Integer pageSize) {
        return (pageSize == null || pageSize < 1) ? 20 : pageSize;
    }

    /**
     * 对已排序 entityId 列表执行内存分页。
     */
    private PageResult<Long> pageOrderedIds(List<Long> ordered, Integer pageNo, Integer pageSize) {
        int pn = normalizePageNo(pageNo);
        int ps = normalizePageSize(pageSize);
        int from = (pn - 1) * ps;
        if (ordered == null || ordered.isEmpty() || from >= ordered.size()) {
            return new PageResult<>(new ArrayList<>(), ordered == null ? 0L : (long) ordered.size());
        }
        int to = Math.min(from + ps, ordered.size());
        return new PageResult<>(ordered.subList(from, to), (long) ordered.size());
    }
    // ==================== 校验方法 ====================
    private void validateEntityIdNotNull(Long entityId) {
        if (entityId == null) {
            throw new ServiceException(400, "entityId 不能为空");
        }
    }

    /**
     * 校验分类ID不能为空
     */
    private void validateCategoryIdNotNull(Long categoryId) {
        if (categoryId == null) {
            throw new ServiceException(400, "categoryId 不能为空");
        }
    }
    /**
     * 校验业务类型编码不能为空
     */
    private void validateEntityTypeCodeNotBlank(String entityTypeCode) {
        if (entityTypeCode == null || entityTypeCode.isBlank()) {
            throw new ServiceException(400, "entityTypeCode 不能为空");
        }
    }

    /**
     * 检查实体是否存在。
     */
    private boolean isEntityExists(Long entityId, String entityTypeCode) {
        return entityCoreService.existsById(entityId, entityTypeCode);
    }

    /**
     * 校验实体是否存在
     */
    private void validateEntityExists(Long entityId, String entityTypeCode) {
        if (!isEntityExists(entityId, entityTypeCode)) {
            throw new ServiceException(404, "实体不存在");
        }
    }

    /**
     * 校验分类是否存在
     */
    private void validateCategoryExists(Long categoryId) {
        CategoryDO category = categoryMapper.selectById(categoryId);
        if (category == null) {
            throw new ServiceException(404, "分类不存在");
        }
    }

    /**
     * 批量收集存在的实体ID，并输出不存在实体列表。
     */
    private Set<Long> collectExistingEntityIds(List<Long> entityIds, String entityTypeCode, List<Long> notFoundEntityIds) {
        Set<Long> existingEntityIds = entityCoreService.filterExistingEntityIds(entityIds, entityTypeCode);
        for (Long entityId : entityIds) {
            if (!existingEntityIds.contains(entityId)) {
                notFoundEntityIds.add(entityId);
            }
        }
        return existingEntityIds;
    }

    /**
     * 批量收集有效分类ID，并输出不存在分类列表（保持输入顺序）。
     */
    private List<Long> collectValidCategoryIds(List<Long> categoryIds, List<Long> notFoundCategoryIds) {
        Set<Long> existingCategoryIds = categoryService.filterExistingCategoryIds(categoryIds);
        List<Long> validCategoryIds = new ArrayList<>();
        for (Long categoryId : categoryIds) {
            if (existingCategoryIds.contains(categoryId)) {
                validCategoryIds.add(categoryId);
            } else {
                notFoundCategoryIds.add(categoryId);
            }
        }
        return validCategoryIds;
    }

    /**
     * 去重并保持输入顺序。
     */
    private List<Long> deduplicateKeepOrder(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(new LinkedHashSet<>(ids));
    }

    /**
     * 基于分类基线排序和分类内偏移，计算本次写入 sort。
     */
    private int calculateSortWithBase(Map<Long, Integer> baseSortMap, Map<Long, Integer> offsetMap, Long categoryId) {
        int base = baseSortMap.getOrDefault(categoryId, 0);
        int offset = offsetMap.getOrDefault(categoryId, 0) + SparseSortUtils.STEP;
        offsetMap.put(categoryId, offset);
        return base + offset;
    }

    /**
     * 批量计算分类当前基准排序值（max sort）。
     */
    private Map<Long, Integer> calcBaseSortByCategoryIds(Collection<Long> categoryIds) {
        Map<Long, Integer> baseSortMap = new HashMap<>();
        if (categoryIds == null || categoryIds.isEmpty()) {
            return baseSortMap;
        }
        // 去重并保持输入顺序
        List<Long> dedupCategoryIds = deduplicateKeepOrder(categoryIds);
        // 查询分类的当前最大排序值
        List<Map<String, Object>> rows = relationMapper.selectMaxSortByCategoryIds(dedupCategoryIds);
        for (Map<String, Object> row : rows) {
            Long categoryId = ((Number) row.get("categoryId")).longValue();
            int maxSort = ((Number) row.get("maxSort")).intValue();
            baseSortMap.put(categoryId, maxSort);
        }

        // 对于当前无历史关联的分类，补 0 基线
        for (Long categoryId : dedupCategoryIds) {
            baseSortMap.putIfAbsent(categoryId, 0);
        }

        return baseSortMap;
    }

    /**
     * 计算分类内下一条关联排序值。
     *
     * <p>策略：同分类下取当前 max(sort) + STEP，避免每次插入都触发全量重排。</p>
     */
    private Integer nextRelationSort(Long categoryId) {
        validateCategoryIdNotNull(categoryId);
        List<EntityCategoryRelationDO> existing = relationMapper.selectByCategoryId(categoryId);
        int max = existing.stream()
                .map(EntityCategoryRelationDO::getSort)
                .filter(Objects::nonNull)
                .max(Integer::compareTo)
                .orElse(0);
        return SparseSortUtils.next(max);
    }

    /**
     * 从“分类范围 + 业务类型”构建有序实体ID序列。
     *
     * <p><b>该方法是分类侧到实体侧的桥接核心</b>，用于输出可直接交给 EntityService 的 ID 结果。</p>
     *
     * <p>处理步骤：</p>
     * <ol>
     *   <li>按 categoryIds + entityTypeCode 查询关联关系；</li>
     *   <li>先按“分类顺序”分组（以入参 categoryIds 的顺序为准）；</li>
     *   <li>在每个分类内按 relation.sort 升序、relation.id 升序；</li>
     *   <li>按“分类顺序 -> 分类内顺序”拼接实体ID，并做稳定去重。</li>
     * </ol>
     *
     * <p>排序语义（重要）：</p>
     * <ul>
     *   <li>sort 仅在“同一分类内”生效，不跨分类比较；</li>
     *   <li>跨分类时优先级由 categoryIds 输入顺序决定；</li>
     *   <li>同一实体命中多个分类时，仅保留首次出现位置。</li>
     * </ul>
     *
     * <p>返回空列表而非 null，便于上层统一空值处理。</p>
     */
    private RelationOrderData buildRelationOrderData(List<Long> categoryIds, String entityTypeCode) {
        // ========== 步骤0：入参校验 ==========
        // 目标：只要关键参数缺失，就返回空结果，不抛异常，方便上层统一处理。
        if (categoryIds == null || categoryIds.isEmpty() || entityTypeCode == null || entityTypeCode.isBlank()) {
            return new RelationOrderData(Collections.emptyList());
        }

        // ========== 步骤1：单分类快速路径 ==========
        // 说明：当仅有一个 categoryId 时，直接复用 Mapper 的单分类排序能力（sort -> id）。
        // 注意：selectByCategoryId 未带 businessType 过滤，这里在 Service 层补一次过滤。
        if (categoryIds.size() == 1) {
            Long singleCategoryId = categoryIds.get(0);
            List<EntityCategoryRelationDO> singleRelations = relationMapper.selectByCategoryId(singleCategoryId);
            if (singleRelations == null || singleRelations.isEmpty()) {
                return new RelationOrderData(Collections.emptyList());
            }

            List<Long> orderedEntityIds = singleRelations.stream()
                    .filter(r -> r.getEntityId() != null)
                    .filter(r -> entityTypeCode.equals(r.getEntityTypeCode()))
                    .map(EntityCategoryRelationDO::getEntityId)
                    .distinct()
                    .toList();
            return new RelationOrderData(orderedEntityIds);
        }

        // ========== 步骤2：查询多分类原始关系数据 ==========
        // 说明：这里拿到的是“候选关系记录集合”，不能直接作为最终顺序。
        // 原因：Mapper 层无法保证“先 categoryIds 输入顺序，再分类内 sort”的完整语义。
        List<EntityCategoryRelationDO> relations = relationMapper.selectRelationsByCategoryIdsForOrdering(categoryIds, entityTypeCode);
        if (relations == null || relations.isEmpty()) {
            return new RelationOrderData(Collections.emptyList());
        }

        // ========== 步骤3：按分类分桶（categoryId -> relations） ==========
        // 目标：把“跨分类混合数据”拆成“每个分类自己的小集合”，便于执行“分类内排序”。
        Map<Long, List<EntityCategoryRelationDO>> grouped = relations.stream()
                .filter(r -> r.getCategoryId() != null && r.getEntityId() != null)
                .collect(Collectors.groupingBy(EntityCategoryRelationDO::getCategoryId));

        // ========== 步骤4：分类内排序（sort -> id） ==========
        // 规则：sort 只在同一分类内生效；sort 为空时视为最大值，排在最后。
        // 兜底：id 升序，确保同 sort 下结果稳定。
        for (List<EntityCategoryRelationDO> bucket : grouped.values()) {
            bucket.sort(Comparator
                    .comparing((EntityCategoryRelationDO r) -> r.getSort() == null ? Integer.MAX_VALUE : r.getSort())
                    .thenComparing(r -> r.getId() == null ? Long.MAX_VALUE : r.getId()));
        }

        // ========== 步骤5：按 categoryIds 输入顺序回放 ==========
        // 目标：保证跨分类顺序由“调用方给定的 categoryIds 顺序”决定。
        // 这一步是多分类稳定排序的关键，不能被全局 sort 替代。
        List<Long> orderedEntityIds = new ArrayList<>();

        // ========== 步骤6：稳定去重 ==========
        // 规则：同一 entity 命中多个分类时，仅保留第一次出现的位置。
        Set<Long> seen = new HashSet<>();
        for (Long categoryId : categoryIds) {
            List<EntityCategoryRelationDO> bucket = grouped.get(categoryId);
            if (bucket == null || bucket.isEmpty()) {
                continue;
            }
            for (EntityCategoryRelationDO relation : bucket) {
                Long entityId = relation.getEntityId();
                if (entityId != null && seen.add(entityId)) {
                    orderedEntityIds.add(entityId);
                }
            }
        }

        // ========== 步骤7：封装输出 ==========
        return new RelationOrderData(orderedEntityIds);
    }

    /**
     * 批量实体关联操作的聚合结果载体。
     *
     * <p>用途：在批量处理循环中先汇总“每个实体”的执行结果，
     * 最后再转换为响应 VO，避免在循环中直接拼装复杂 Builder 造成可读性下降。</p>
     *
     * <p>字段语义：</p>
     * <ul>
     *   <li>entityId：当前处理实体ID；</li>
     *   <li>entityExists：实体是否存在（用于区分 404 与业务失败）；</li>
     *   <li>successCount：该实体在本次批处理中的成功关联数；</li>
     *   <li>failCount：该实体在本次批处理中的失败关联数；</li>
     *   <li>errorMessage：失败时的简要原因（存在时优先展示）。</li>
     * </ul>
     */
    private static class EntityResultData {
        final Long entityId;
        final Boolean entityExists;
        final Integer successCount;
        final Integer failCount;
        final String errorMessage;

        EntityResultData(Long entityId, Boolean entityExists, Integer successCount, Integer failCount, String errorMessage) {
            this.entityId = entityId;
            this.entityExists = entityExists;
            this.successCount = successCount;
            this.failCount = failCount;
            this.errorMessage = errorMessage;
        }
    }

}
