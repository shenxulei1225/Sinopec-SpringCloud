package cn.cheers.x.module.dynamicbusiness.service.model.relation;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.BatchModelCategoryAssociationRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.common.vo.CategoryAssociationBaseRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelCategoryAssociationRespVO;
import static cn.cheers.x.module.dynamicbusiness.enums.ErrorCodeConstants.ASSOCIATION_OPERATION_FAILED;
import static cn.cheers.x.module.dynamicbusiness.enums.ErrorCodeConstants.ENTITY_NOT_EXISTS;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelCategoryRelationDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.category.CategoryMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelCategoryRelationMapper;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryService;
import cn.cheers.x.module.dynamicbusiness.service.model.core.ModelCoreService;
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
 * 模型-分类关联服务实现类。
 *
 * <p>专门负责 Model 与 Category 的多对多关联关系维护。
 * 仅处理关系本身，不负责模型/分类详情组装，也不负责分类树解析（树展开由 CategoryService 负责）。</p>
 *
 * <p>实现约定：</p>
 * <ul>
 *   <li>所有关联写操作均显式使用 entityTypeCode 进行业务隔离与索引命中；</li>
 *   <li>查询类方法仅返回 modelId / categoryId 序列，不返回详情对象；</li>
 *   <li>批量方法在“同批次同 entityTypeCode”前提下执行，跨业务类型需拆批调用。</li>
 * </ul>
 *
 * @author 基础服务模块
 */
@Service
@Validated
@Slf4j
public class ModelCategoryRelationServiceImpl implements ModelCategoryRelationService {


    @Resource
    private ModelCoreService modelCoreService;


    @Resource
    @Lazy
    private CategoryService categoryService;

    @Resource
    private ModelCategoryRelationMapper relationMapper;

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private ModelRelationSortService modelRelationSortService;

    // ==================== 单模型-单分类操作 ====================

    /**
     * 关联单个模型到单个分类。
     *
     * <p>处理步骤：</p>
     * <ol>
     *   <li>校验参数（modelId/categoryId/entityTypeCode）与主数据存在性；</li>
     *   <li>优先恢复软删除关联（避免唯一约束冲突）；</li>
     *   <li>若有效关联已存在则直接返回成功；</li>
     *   <li>否则创建新关联并分配分类内排序（sort）。</li>
     * </ol>
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ModelCategoryAssociationRespVO associate(Long modelId, Long categoryId, String entityTypeCode) {
        long startTime = System.currentTimeMillis();
        /** 校验参数 */
        validateModelIdNotNull(modelId);
        validateCategoryIdNotNull(categoryId);
        validateEntityTypeCodeNotBlank(entityTypeCode);
        validateModelExists(modelId, entityTypeCode);
        validateCategoryExists(categoryId);

        // 如果存在软删除的关联，优先恢复软删除关联，避免唯一键冲突
        int restored = relationMapper.restoreDeletedRelation(
                modelId,
                categoryId,
                entityTypeCode,
                // 排序策略：增量关联不全量重排；恢复/新增均置于当前分类末尾。
                nextRelationSort(categoryId));
        if (restored > 0) {
            log.info("重新启用已删除的实体-分类关联并更新排序: modelId={}, categoryId={}, entityTypeCode={}",
                    modelId, categoryId, entityTypeCode);
            return ModelCategoryAssociationRespVO.builder()
                    .operationType("ASSOCIATE")
                    .modelId(modelId)
                    .modelExists(true)
                    .totalCount(1)
                    .successCount(1)
                    .failCount(0)
                    .successCategoryIds(Collections.singletonList(categoryId))
                    .failItems(Collections.emptyList())
                    .executionTime(System.currentTimeMillis() - startTime)
                    .build();
        }

        // 检查有效关联是否已存在
        if (existsRelation(modelId, categoryId, entityTypeCode)) {
            log.debug("关联已存在: modelId={}, categoryId={}, entityTypeCode={}", modelId, categoryId, entityTypeCode);
            return ModelCategoryAssociationRespVO.builder()
                    .operationType("ASSOCIATE")
                    .modelId(modelId)
                    .modelExists(true)
                    .totalCount(1)
                    .successCount(1)
                    .failCount(0)
                    .successCategoryIds(Collections.singletonList(categoryId))
                    .failItems(Collections.emptyList())
                    .executionTime(System.currentTimeMillis() - startTime)
                    .build();
        }

        // 创建 model-category 关联：新增时写入 sort（分类内排序）
        ModelCategoryRelationDO relation = ModelCategoryRelationDO.builder()
                .modelId(modelId)
                .categoryId(categoryId)
                .entityTypeCode(entityTypeCode)
                .sort(nextRelationSort(categoryId))
                .build();
        syncRelationIdentity(relation);
        try {
            relationMapper.insert(relation);
        } catch (org.springframework.dao.DuplicateKeyException ex) {
            // 并发/重复请求兜底：唯一键冲突按幂等成功处理
            if (!existsRelation(modelId, categoryId, entityTypeCode)) {
                throw ex;
            }
            log.info("关联并发冲突已按幂等成功处理: modelId={}, categoryId={}, entityTypeCode={}",
                    modelId, categoryId, entityTypeCode);
        }

        log.info("创建实体-分类关联: modelId={}, categoryId={}, entityTypeCode={}", modelId, categoryId, entityTypeCode);
        return ModelCategoryAssociationRespVO.builder()
                .operationType("ASSOCIATE")
                .modelId(modelId)
                .modelExists(true)
                .totalCount(1)
                .successCount(1)
                .failCount(0)
                .successCategoryIds(Collections.singletonList(categoryId))
                .failItems(Collections.emptyList())
                .executionTime(System.currentTimeMillis() - startTime)
                .build();
    }

    /**
     * 解除单个模型与单个分类的关联。
     *
     * <p>处理步骤：</p>
     * <ol>
     *   <li>参数空值校验；</li>
     *   <li>校验 entityTypeCode；</li>
     *   <li>按 modelId + categoryId + entityTypeCode 删除关联；</li>
     *   <li>返回统一响应结构（含统计与耗时）。</li>
     * </ol>
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ModelCategoryAssociationRespVO disassociate(Long modelId, Long categoryId, String entityTypeCode) {
        long startTime = System.currentTimeMillis();
        if (modelId == null || categoryId == null) {
            return ModelCategoryAssociationRespVO.builder()
                    .operationType("DISASSOCIATE")
                    .modelId(modelId)
                    .modelExists(true)
                    .totalCount(1)
                    .successCount(0)
                    .failCount(1)
                    .successCategoryIds(Collections.emptyList())
                    .failItems(List.of(CategoryAssociationBaseRespVO.FailItem.builder()
                            .categoryId(categoryId)
                            .reason("参数不能为空")
                            .errorCode(400)
                            .build()))
                    .executionTime(System.currentTimeMillis() - startTime)
                    .build();
        }
        validateEntityTypeCodeNotBlank(entityTypeCode);
        relationMapper.deleteByModelAndCategory(modelId, categoryId, entityTypeCode);
        log.info("删除实体-分类关联: modelId={}, categoryId={}, entityTypeCode={}", modelId, categoryId, entityTypeCode);
        return ModelCategoryAssociationRespVO.builder()
                .operationType("DISASSOCIATE")
                .modelId(modelId)
                .modelExists(true)
                .totalCount(1)
                .successCount(1)
                .failCount(0)
                .successCategoryIds(Collections.singletonList(categoryId))
                .failItems(Collections.emptyList())
                .executionTime(System.currentTimeMillis() - startTime)
                .build();
    }

    /**
     * 判断实体与分类关联是否存在。
     */
    @Override
    public boolean existsRelation(Long modelId, Long categoryId, String entityTypeCode) {
        if (modelId == null || categoryId == null) {
            return false;
        }
        validateEntityTypeCodeNotBlank(entityTypeCode);
        return relationMapper.selectByModelAndCategory(modelId, categoryId, entityTypeCode) != null;
    }

    // ==================== 单模型-多分类操作 ====================

    /**
     * 批量关联单模型到多分类（带校验）。
     *
     * <p>处理步骤：</p>
     * <ol>
     *   <li>校验模型存在性（不存在直接返回失败结构）；</li>
     *   <li>校验分类存在性，构建失败明细；</li>
     *   <li>对已存在/可恢复/需新增三类关系分流处理；</li>
     *   <li>汇总 successCategoryIds 与 failItems 返回。</li>
     * </ol>
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ModelCategoryAssociationRespVO batchAssociateModelToCategories(
            Long modelId, List<Long> categoryIds, String entityTypeCode) {
        // 排序策略：批量增量关联不全量重排；仅对恢复/新增记录分配新的尾部 sort。
        long startTime = System.currentTimeMillis();

        // 1. 验证实体存在
        // Step 1: 校验实体存在性（不存在直接返回失败结构）
        boolean modelExists = isModelExists(modelId, entityTypeCode);
        if (!modelExists) {
            return ModelCategoryAssociationRespVO.builder()
                    .operationType("ASSOCIATE")
                    .modelId(modelId)
                    .modelExists(false)
                    .totalCount(categoryIds != null ? categoryIds.size() : 0)
                    .successCount(0)
                    .failCount(categoryIds != null ? categoryIds.size() : 0)
                    .successCategoryIds(Collections.emptyList())
                    .failItems(List.of(CategoryAssociationBaseRespVO.FailItem.builder()
                            .categoryId(null)
                            .reason("实体不存在 (ID: " + modelId + ", entityTypeCode: " + entityTypeCode + ")")
                            .errorCode(ENTITY_NOT_EXISTS.getCode())
                            .build()))
                    .executionTime(System.currentTimeMillis() - startTime)
                    .build();
        }

        // 2. 执行关联操作（传入实体业务类型用于跨业务关联验证）
        BatchAssociateResult associateResult = batchAssociateInternal(modelId, categoryIds, entityTypeCode);

        return ModelCategoryAssociationRespVO.builder()
                .operationType("ASSOCIATE")
                .modelId(modelId)
                .modelExists(true)
                .totalCount(categoryIds != null ? categoryIds.size() : 0)
                .successCount(associateResult.successCount())
                .failCount(associateResult.failItems().size())
                .successCategoryIds(associateResult.successCategoryIds())
                .failItems(associateResult.failItems())
                .executionTime(System.currentTimeMillis() - startTime)
                .build();
    }

    /**
     * 批量解除单个实体与多个分类的关联。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ModelCategoryAssociationRespVO batchDisassociateModelFromCategories(Long modelId, List<Long> categoryIds, String entityTypeCode) {
        long startTime = System.currentTimeMillis();
        if (modelId == null || categoryIds == null || categoryIds.isEmpty()) {
            return ModelCategoryAssociationRespVO.builder()
                    .operationType("BATCH_DISASSOCIATE")
                    .modelId(modelId)
                    .modelExists(true)
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
        int successCount = 0;
        for (Long categoryId : categoryIds) {
            // 仅删除 model-category 关联
            relationMapper.deleteByModelAndCategory(modelId, categoryId, entityTypeCode);
            successCount++;
        }

        log.info("批量取消实体与分类的关联: modelId={}, categoryIds={}, entityTypeCode={}", modelId, categoryIds, entityTypeCode);
        return ModelCategoryAssociationRespVO.builder()
                .operationType("BATCH_DISASSOCIATE")
                .modelId(modelId)
                .modelExists(true)
                .totalCount(categoryIds.size())
                .successCount(successCount)
                .failCount(0)
                .successCategoryIds(categoryIds.stream().filter(Objects::nonNull).distinct().toList())
                .failItems(Collections.emptyList())
                .executionTime(System.currentTimeMillis() - startTime)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ModelCategoryAssociationRespVO updateAssociation(
            Long modelId, List<Long> categoryIds, String entityTypeCode) {
        // 排序策略：更新实体关联的分类时，不重排未变关联；仅对恢复/新增关联分配新的 sort。
        long startTime = System.currentTimeMillis();
        List<CategoryAssociationBaseRespVO.FailItem> failItems = new ArrayList<>();

        // 1. 验证实体存在
        boolean modelExists = isModelExists(modelId, entityTypeCode);
        if (!modelExists) {
            return ModelCategoryAssociationRespVO.builder()
                    .operationType("UPDATE")
                    .modelId(modelId)
                    .modelExists(false)
                    .totalCount(categoryIds != null ? categoryIds.size() : 0)
                    .successCount(0)
                    .failCount(categoryIds != null ? categoryIds.size() : 0)
                    .successCategoryIds(Collections.emptyList())
                    .failItems(List.of(ModelCategoryAssociationRespVO.FailItem.builder()
                            .categoryId(null)
                            .reason("实体不存在 (ID: " + modelId + ", entityTypeCode: " + entityTypeCode + ")")
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
                failItems.add(ModelCategoryAssociationRespVO.FailItem.builder()
                        .categoryId(categoryId)
                        .reason("分类不存在 (ID: " + categoryId + ")")
                        .errorCode(404)
                        .build());
            }
        }

        // Step 3: 读取当前有效关联（按 modelId，再在内存中过滤 entityTypeCode）
        List<ModelCategoryRelationDO> activeRelations = relationMapper.selectByModelId(modelId);
        Set<Long> activeCategoryIds = activeRelations.stream()
                .filter(rel -> entityTypeCode.equals(rel.getEntityTypeCode()))
                .map(ModelCategoryRelationDO::getCategoryId)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        // Step 4: 读取目标分类上的历史关联（含 deleted）并提取恢复候选
        List<ModelCategoryRelationDO> existingRelationsOnTarget = validCategoryIds.isEmpty()
                ? Collections.emptyList()
                : relationMapper.selectByModelAndCategoryIdsIncludingDeleted(modelId, validCategoryIds, entityTypeCode);
        Set<Long> restoreCandidates = existingRelationsOnTarget.stream()
                .filter(rel -> Boolean.TRUE.equals(rel.getDeleted()))
                .map(ModelCategoryRelationDO::getCategoryId)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        // Step 5: 差集分流（删除 / 恢复 / 新增）
        List<Long> toDelete = activeCategoryIds.stream().filter(id -> !validCategoryIds.contains(id)).toList();
        List<Long> toRestore = validCategoryIds.stream().filter(restoreCandidates::contains).toList();
        List<Long> toInsert = validCategoryIds.stream()
                .filter(id -> !activeCategoryIds.contains(id) && !restoreCandidates.contains(id))
                .toList();

        // Step 6: 删除移除项（仅删除差集，不影响 toKeep）
        if (!toDelete.isEmpty()) {
            relationMapper.deleteByModelAndCategoryIds(modelId, toDelete);
        }
        // Step 7: 先完成状态变更（恢复 + 新增），不在此阶段分段编号
        if (!toRestore.isEmpty()) {
            relationMapper.restoreDeletedRelationsBatch(modelId, toRestore, entityTypeCode);
        }
        if (!toInsert.isEmpty()) {
            List<ModelCategoryRelationDO> relationsToInsert = new ArrayList<>();
            for (Long categoryId : toInsert) {
                relationsToInsert.add(ModelCategoryRelationDO.builder()
                        .modelId(modelId)
                        .categoryId(categoryId)
                        .entityTypeCode(entityTypeCode)
                        .sort(0)
                        .build());
            }
            enrichRelationCodes(relationsToInsert);
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
                relationMapper.updateSortByModelAndCategory(modelId, categoryId,
                        calculateSortWithBase(baseSortMap, sortOffsetMap, categoryId), entityTypeCode);
            }
        }

        // Step 9: 返回执行结果（成功=有效目标分类数，失败=无效分类数）
        int successCount = validCategoryIds.size();
        return ModelCategoryAssociationRespVO.builder()
                .operationType("UPDATE")
                .modelId(modelId)
                .modelExists(true)
                .totalCount(targetCategoryIds.size())
                .successCount(successCount)
                .failCount(failItems.size())
                .successCategoryIds(validCategoryIds)
                .failItems(failItems)
                .executionTime(System.currentTimeMillis() - startTime)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reorderModelInCategory(Long sourceModelId, Long targetModelId, Long categoryId, String position) {
        String pos = position == null ? "" : position.trim().toUpperCase();
        if (!"BEFORE".equals(pos) && !"AFTER".equals(pos)) {
            throw new ServiceException(400, "reorder 仅支持 BEFORE/AFTER");
        }

        List<ModelCategoryRelationDO> allInCategory = relationMapper.selectByCategoryId(categoryId);
        if (allInCategory.isEmpty()) {
            throw new ServiceException(404, "分类下无模型关系");
        }

        ModelCategoryRelationDO sourceRel = allInCategory.stream()
                .filter(r -> Objects.equals(r.getModelId(), sourceModelId))
                .findFirst()
                .orElseThrow(() -> new ServiceException(404, "源模型不在目标分类下"));

        String entityTypeCode = sourceRel.getEntityTypeCode();
        List<ModelCategoryRelationDO> ordered = allInCategory.stream()
                .filter(r -> Objects.equals(entityTypeCode, r.getEntityTypeCode()))
                .sorted(Comparator.comparing(ModelCategoryRelationDO::getSort, Comparator.nullsLast(Integer::compareTo))
                        .thenComparing(ModelCategoryRelationDO::getId, Comparator.nullsLast(Long::compareTo)))
                .collect(Collectors.toCollection(ArrayList::new));

        int sourceIndex = indexOfModel(ordered, sourceModelId);
        int targetIndex = indexOfModel(ordered, targetModelId);
        if (sourceIndex < 0 || targetIndex < 0) {
            throw new ServiceException(404, "源或目标模型不存在于该分类");
        }

        ModelCategoryRelationDO source = ordered.remove(sourceIndex);
        int insertIndex = "BEFORE".equals(pos) ? targetIndex : targetIndex + 1;
        if (sourceIndex < targetIndex) insertIndex -= 1;
        insertIndex = Math.max(0, Math.min(insertIndex, ordered.size()));
        ordered.add(insertIndex, source);

        Integer prevSort = insertIndex > 0 ? modelRelationSortService.safeSort(ordered.get(insertIndex - 1).getSort()) : null;
        Integer nextSort = insertIndex < ordered.size() - 1 ? modelRelationSortService.safeSort(ordered.get(insertIndex + 1).getSort()) : null;
        Integer newSort = modelRelationSortService.computeSparseSort(prevSort, nextSort);

        if (newSort == null) {
            rebalanceSorts(ordered, entityTypeCode, categoryId);
            prevSort = insertIndex > 0 ? modelRelationSortService.safeSort(ordered.get(insertIndex - 1).getSort()) : null;
            nextSort = insertIndex < ordered.size() - 1 ? modelRelationSortService.safeSort(ordered.get(insertIndex + 1).getSort()) : null;
            newSort = modelRelationSortService.computeSparseSort(prevSort, nextSort);
        }

        relationMapper.updateSortByModelAndCategory(sourceModelId, categoryId, newSort, entityTypeCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void moveOrBindModelToCategory(Long modelId, Long sourceCategoryId, Long targetCategoryId) {
        List<ModelCategoryRelationDO> sourceRelations = relationMapper.selectByModelId(modelId);
        if (sourceRelations.isEmpty()) {
            throw new ServiceException(404, "模型未绑定任何分类，无法推断业务类型");
        }

        String entityTypeCode = sourceRelations.get(0).getEntityTypeCode();
        if (sourceCategoryId != null && !Objects.equals(sourceCategoryId, targetCategoryId)) {
            disassociate(modelId, sourceCategoryId, entityTypeCode);
        }

        ModelCategoryRelationDO existingTarget = relationMapper.selectByModelIdAndCategoryId(modelId, targetCategoryId, entityTypeCode);
        if (existingTarget == null) {
            associate(modelId, targetCategoryId, entityTypeCode);
        }
    }

    private int indexOfModel(List<ModelCategoryRelationDO> list, Long modelId) {
        for (int i = 0; i < list.size(); i++) {
            if (Objects.equals(list.get(i).getModelId(), modelId)) {
                return i;
            }
        }
        return -1;
    }

    private void rebalanceSorts(List<ModelCategoryRelationDO> ordered, String entityTypeCode, Long categoryId) {
        for (int i = 0; i < ordered.size(); i++) {
            ModelCategoryRelationDO relation = ordered.get(i);
            int sort = modelRelationSortService.rebalanceSortByIndex(i);
            relation.setSort(sort);
            relationMapper.updateSortByModelAndCategory(relation.getModelId(), categoryId, sort, entityTypeCode);
        }
    }

    // ==================== 多模型-单分类操作 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BatchModelCategoryAssociationRespVO batchAssociateModelsToCategory(
            List<Long> modelIds, Long categoryId, String entityTypeCode) {
        long startTime = System.currentTimeMillis();

        // 验证分类存在
        if (!categoryService.existsById(categoryId)) {
            // Step 3: 汇总并返回批量执行结果
        return BatchModelCategoryAssociationRespVO.builder()
                    .operationType("BATCH_ASSOCIATE")
                    .totalModelCount(modelIds != null ? modelIds.size() : 0)
                    .totalCategoryCount(1)
                    .successModelCount(0)
                    .failModelCount(modelIds != null ? modelIds.size() : 0)
                    .notFoundCategoryIds(List.of(categoryId))
                    .executionTime(System.currentTimeMillis() - startTime)
                    .build();
        }

        // 转换为多对多操作
        return batchAssociateModelsToCategories(modelIds, List.of(categoryId), entityTypeCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BatchModelCategoryAssociationRespVO batchDisassociateModelsFromCategory(
            List<Long> modelIds, Long categoryId, String entityTypeCode) {
        long startTime = System.currentTimeMillis();


        validateEntityTypeCodeNotBlank(entityTypeCode);

        if (modelIds == null || modelIds.isEmpty() || categoryId == null) {
            return BatchModelCategoryAssociationRespVO.builder()
                    .operationType("BATCH_DISASSOCIATE")
                    .totalModelCount(0)
                    .totalCategoryCount(1)
                    .successModelCount(0)
                    .failModelCount(0)
                    .executionTime(System.currentTimeMillis() - startTime)
                    .build();
        }

        int successCount = 0;
        for (Long modelId : modelIds) {
            relationMapper.deleteByModelAndCategory(modelId, categoryId, entityTypeCode);
            successCount++;
        }

        log.info("批量取消实体与分类的关联: modelIds={}, categoryId={}, entityTypeCode={}", modelIds, categoryId, entityTypeCode);

        return BatchModelCategoryAssociationRespVO.builder()
                .operationType("BATCH_DISASSOCIATE")
                .totalModelCount(modelIds.size())
                .totalCategoryCount(1)
                .successModelCount(successCount)
                .failModelCount(0)
                .executionTime(System.currentTimeMillis() - startTime)
                .build();
    }


    // ==================== 多实体-多分类操作 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BatchModelCategoryAssociationRespVO batchAssociateModelsToCategories(
            List<Long> modelIds, List<Long> categoryIds, String entityTypeCode) {
        // 排序策略：批量增量关联不做全量重排；对恢复/新增记录分配尾部 sort。
        long startTime = System.currentTimeMillis();

        validateEntityTypeCodeNotBlank(entityTypeCode);

        if (modelIds == null || modelIds.isEmpty() || categoryIds == null || categoryIds.isEmpty()) {
            return BatchModelCategoryAssociationRespVO.builder()
                    .operationType("BATCH_ASSOCIATE")
                    .totalModelCount(modelIds != null ? modelIds.size() : 0)
                    .totalCategoryCount(categoryIds != null ? categoryIds.size() : 0)
                    .successModelCount(0)
                    .failModelCount(0)
                    .executionTime(System.currentTimeMillis() - startTime)
                    .build();
        }

        // 1. 批量验证实体存在性
        List<Long> notFoundModelIds = new ArrayList<>();
        Set<Long> existingModelIds = collectExistingModelIds(modelIds, entityTypeCode, notFoundModelIds);

        // 2. 批量验证分类存在性
        List<Long> notFoundCategoryIds = new ArrayList<>();
        List<Long> validCategoryIds = collectValidCategoryIds(categoryIds, notFoundCategoryIds);

        // 3. 批量查询现有关联（优化：避免 N+1 查询问题）
        List<ModelCategoryRelationDO> existingRelations = relationMapper.selectByModelIdsAndCategoryIds(
                new ArrayList<>(existingModelIds),
                validCategoryIds);

        // 4. 构建现有关联索引 + 分类 max(sort)
        Map<Long, Set<Long>> existingByModel = new HashMap<>();
        Map<Long, Integer> baseSortMap = new HashMap<>();
        for (ModelCategoryRelationDO relation : existingRelations) {
            existingByModel
                    .computeIfAbsent(relation.getModelId(), k -> new HashSet<>())
                    .add(relation.getCategoryId());
            if (relation.getCategoryId() != null && relation.getSort() != null) {
                Integer current = baseSortMap.get(relation.getCategoryId());
                int next = (current == null) ? relation.getSort() : Math.max(current, relation.getSort());
                baseSortMap.put(relation.getCategoryId(), next);
            }
        }

        // 5. 计算需要插入的新关联（同时统计每个实体的成功/失败）
        List<ModelCategoryRelationDO> relationsToInsert = new ArrayList<>();
        Map<Long, ModelResultData> modelResultDataMap = new HashMap<>();
        Map<Long, Integer> insertCountByModel = new HashMap<>();

        for (Long modelId : modelIds) {
            if (!existingModelIds.contains(modelId)) {
                modelResultDataMap.put(modelId, new ModelResultData(
                        modelId, false, 0, categoryIds.size(), "实体不存在"));
                continue;
            }

            Set<Long> existedCategories = existingByModel.getOrDefault(modelId, Collections.emptySet());
            int modelSuccessCount = validCategoryIds.size();
            int modelFailCount = notFoundCategoryIds.size();

            for (Long categoryId : validCategoryIds) {
                if (!existedCategories.contains(categoryId)) {
                    relationsToInsert.add(ModelCategoryRelationDO.builder()
                            .modelId(modelId)
                            .categoryId(categoryId)
                            .entityTypeCode(entityTypeCode)
                            .build());
                    Integer currentInsert = insertCountByModel.get(modelId);
                    insertCountByModel.put(modelId, currentInsert == null ? 1 : currentInsert + 1);
                }
            }

            modelResultDataMap.put(modelId, new ModelResultData(
                    modelId, true, modelSuccessCount, modelFailCount, null));
        }

        // 6. 批量插入新关联（按分类分组分配 sort）
        if (!relationsToInsert.isEmpty()) {
            Map<Long, Integer> sortOffsetMap = new HashMap<>();
            for (ModelCategoryRelationDO relation : relationsToInsert) {
                relation.setSort(calculateSortWithBase(baseSortMap, sortOffsetMap, relation.getCategoryId()));
            }
            try {
                enrichRelationCodes(relationsToInsert);
                relationMapper.insertBatchRelations(relationsToInsert);
            } catch (Exception e) {
                log.error("批量插入关联失败: {}", e.getMessage(), e);
                for (Map.Entry<Long, Integer> entry : insertCountByModel.entrySet()) {
                    ModelResultData data = modelResultDataMap.get(entry.getKey());
                    if (data != null && data.modelExists) {
                        modelResultDataMap.put(entry.getKey(), new ModelResultData(
                                entry.getKey(), true, data.successCount - entry.getValue(),
                                data.failCount + entry.getValue(), null));
                    }
                }
            }
        }

        // 7. 构建返回结果
        List<BatchModelCategoryAssociationRespVO.ModelResult> modelResults = new ArrayList<>();
        int successModelCount = 0;
        int failModelCount = 0;

        for (Long modelId : modelIds) {
            ModelResultData data = modelResultDataMap.get(modelId);
            if (data != null) {
                BatchModelCategoryAssociationRespVO.ModelResult result = BatchModelCategoryAssociationRespVO.ModelResult.builder()
                        .modelId(data.modelId)
                        .modelExists(data.modelExists)
                        .successCount(data.successCount)
                        .failCount(data.failCount)
                        .errorMessage(data.errorMessage)
                        .build();
                modelResults.add(result);
                if (data.modelExists && data.failCount == 0) {
                    successModelCount++;
                } else {
                    failModelCount++;
                }
            }
        }

        log.info("批量关联实体到分类: modelIds={}, categoryIds={}, successModelCount={}, failModelCount={}",
                modelIds, categoryIds, successModelCount, failModelCount);

        return BatchModelCategoryAssociationRespVO.builder()
                .operationType("BATCH_ASSOCIATE")
                .totalModelCount(modelIds.size())
                .totalCategoryCount(categoryIds.size())
                .successModelCount(successModelCount)
                .failModelCount(failModelCount)
                .notFoundModelIds(notFoundModelIds)
                .notFoundCategoryIds(notFoundCategoryIds)
                .modelResults(modelResults)
                .executionTime(System.currentTimeMillis() - startTime)
                .build();
    }

    /**
     * 批量解除“多个模型 × 多个分类”的关联。
     *
     * <p>实现策略：</p>
     * <ul>
     *   <li>不做关联存在性预校验，直接删除（幂等）；</li>
     *   <li>按 modelId + categoryId + entityTypeCode 精确删除；</li>
     *   <li>按模型维度汇总成功数量。</li>
     * </ul>
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BatchModelCategoryAssociationRespVO batchDisassociateModelsFromCategories(
            List<Long> modelIds, List<Long> categoryIds, String entityTypeCode) {
        long startTime = System.currentTimeMillis();

        if (modelIds == null || modelIds.isEmpty() || categoryIds == null || categoryIds.isEmpty()) {
            return BatchModelCategoryAssociationRespVO.builder()
                    .operationType("BATCH_DISASSOCIATE")
                    .totalModelCount(modelIds != null ? modelIds.size() : 0)
                    .totalCategoryCount(categoryIds != null ? categoryIds.size() : 0)
                    .successModelCount(0)
                    .failModelCount(0)
                    .executionTime(System.currentTimeMillis() - startTime)
                    .build();
        }

        // 执行删除操作（不验证存在性，直接删除）
        List<BatchModelCategoryAssociationRespVO.ModelResult> modelResults = new ArrayList<>();
        int successModelCount = 0;

        for (Long modelId : modelIds) {
            int modelSuccessCount = 0;
            for (Long categoryId : categoryIds) {
                relationMapper.deleteByModelAndCategory(modelId, categoryId, entityTypeCode);
                modelSuccessCount++;
            }

            modelResults.add(BatchModelCategoryAssociationRespVO.ModelResult.builder()
                    .modelId(modelId)
                    .modelExists(true)
                    .successCount(modelSuccessCount)
                    .failCount(0)
                    .build());
            successModelCount++;
        }

        log.info("批量取消实体与分类的关联: modelIds={}, categoryIds={}, entityTypeCode={}", modelIds, categoryIds, entityTypeCode);

        return BatchModelCategoryAssociationRespVO.builder()
                .operationType("BATCH_DISASSOCIATE")
                .totalModelCount(modelIds.size())
                .totalCategoryCount(categoryIds.size())
                .successModelCount(successModelCount)
                .failModelCount(0)
                .modelResults(modelResults)
                .executionTime(System.currentTimeMillis() - startTime)
                .build();
    }

    /**
     * 批量替换多个模型的分类关联。
     *
     * <p>处理流程：先批量校验模型/分类存在性，再按模型维度执行“差集删除 + 恢复 + 新增”。</p>
     *
     * <p>排序策略：不重排未变关联，仅对恢复/新增关联分配新的 sort。</p>
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BatchModelCategoryAssociationRespVO batchUpdateAssociation(
            List<Long> modelIds, List<Long> categoryIds, String entityTypeCode) {
        // 排序策略：批量更新关联时，不重排未变关联；仅对恢复/新增关联分配新的 sort。
        long startTime = System.currentTimeMillis();

        if (modelIds == null || modelIds.isEmpty()) {
            return BatchModelCategoryAssociationRespVO.builder()
                    .operationType("BATCH_UPDATE")
                    .totalModelCount(0)
                    .totalCategoryCount(categoryIds != null ? categoryIds.size() : 0)
                    .successModelCount(0)
                    .failModelCount(0)
                    .executionTime(System.currentTimeMillis() - startTime)
                    .build();
        }

        // Step 1: 标准化输入并初始化聚合结果容器
        List<Long> targetCategoryIds = categoryIds == null ? Collections.emptyList() : categoryIds;
        List<BatchModelCategoryAssociationRespVO.ModelResult> modelResults = new ArrayList<>();
        List<Long> notFoundModelIds = new ArrayList<>();
        List<Long> notFoundCategoryIds = new ArrayList<>();

        int successModelCount = 0;
        int failModelCount = 0;

        // Step 2: 逐实体复用 updateAssociation（统一差集逻辑）
        for (Long modelId : modelIds) {
            ModelCategoryAssociationRespVO single = updateAssociation(modelId, targetCategoryIds, entityTypeCode);
            int modelFailCount = single.getFailCount();
            int modelSuccessCount = single.getSuccessCount();
            boolean modelExists = Boolean.TRUE.equals(single.getModelExists());

            if (!modelExists) {
                notFoundModelIds.add(modelId);
            }
            if (single.getFailItems() != null) {
                single.getFailItems().stream()
                        .map(CategoryAssociationBaseRespVO.FailItem::getCategoryId)
                        .filter(Objects::nonNull)
                        .forEach(notFoundCategoryIds::add);
            }

            modelResults.add(BatchModelCategoryAssociationRespVO.ModelResult.builder()
                    .modelId(modelId)
                    .modelExists(modelExists)
                    .successCount(modelSuccessCount)
                    .failCount(modelFailCount)
                    .errorMessage(modelExists ? null : "实体不存在")
                    .build());

            if (modelExists && modelFailCount == 0) {
                successModelCount++;
            } else {
                failModelCount++;
            }
        }

        return BatchModelCategoryAssociationRespVO.builder()
                .operationType("BATCH_UPDATE")
                .totalModelCount(modelIds.size())
                .totalCategoryCount(targetCategoryIds.size())
                .successModelCount(successModelCount)
                .failModelCount(failModelCount)
                .notFoundModelIds(deduplicateKeepOrder(notFoundModelIds))
                .notFoundCategoryIds(deduplicateKeepOrder(notFoundCategoryIds))
                .modelResults(modelResults)
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
    public List<Long> listCategoryIdsByModelId(Long modelId, String entityTypeCode) {
        if (modelId == null || entityTypeCode == null || entityTypeCode.isBlank()) {
            return new ArrayList<>();
        }

        List<ModelCategoryRelationDO> relations = relationMapper.selectByModelId(modelId);
        if (relations == null || relations.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> categoryIds = relations.stream()
                .map(ModelCategoryRelationDO::getCategoryId)
                .filter(Objects::nonNull)
                .toList();
        if (categoryIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 通过分类 -> modelIds(按业务过滤) 反向校验，避免返回其它业务实体关联到的分类
        List<Long> result = new ArrayList<>();
        for (Long categoryId : categoryIds) {
            List<Long> modelIds = listModelIdsByCategoryIdOnly(categoryId, entityTypeCode);
            if (modelIds.contains(modelId)) {
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
     * 应先展开 categoryIds 再调用 {@link #listModelIdsByCategoryIdsWithDescendants(List, String)}。</p>
     */
    @Override
    public List<Long> listModelIdsByCategoryIdOnly(Long categoryId, String entityTypeCode) {
        if (categoryId == null || entityTypeCode == null || entityTypeCode.isBlank()) {
            return new ArrayList<>();
        }

        // 单分类快路径：直接使用 Mapper 的“分类内 sort,id”排序结果，避免多分类编排开销。
        List<ModelCategoryRelationDO> relations = relationMapper.selectByCategoryIdAndEntityType(categoryId, entityTypeCode);
        if (relations == null || relations.isEmpty()) {
            return new ArrayList<>();
        }
        // 把关系数据转换为modelId列表，并去重
        List<Long> orderedModelIds = new ArrayList<>();
        Set<Long> seen = new HashSet<>();
        for (ModelCategoryRelationDO relation : relations) {
            Long modelId = relation.getModelId();
            if (modelId != null && seen.add(modelId)) {
                orderedModelIds.add(modelId);
            }
        }
        return orderedModelIds;
    }

    /**
     * 单分类（含子树）查询实体ID列表。
     *
     * <p>步骤：先由 CategoryService 展开子树分类ID，再交给关系层做排序与去重。</p>
     */
    @Override
    public List<Long> listModelIdsByCategoryIdWithDescendants(Long categoryId, String entityTypeCode) {
        if (categoryId == null || entityTypeCode == null || entityTypeCode.isBlank()) {
            return new ArrayList<>();
        }
        // 单分类含子树：子树展开由 CategoryService 统一处理（含容错/降级），
        // 关系服务只负责基于展开结果做有序关系查询。
        List<Long> allCategoryIds = categoryService.getAllCategoryIdsIncludingChildren(categoryId, null);
        return resolveOrderedModelIds(allCategoryIds, entityTypeCode);
    }



    // ==================== 级联删除操作 ====================

    /**
     * 删除单个实体的所有分类关联（级联清理入口）。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAllByModelId(Long modelId) {
        if (modelId == null) {
            return;
        }

        relationMapper.deleteByModelId(modelId);
        log.info("删除实体的所有分类关联: modelId={}", modelId);
    }

    /**
     * 批量删除多个实体的所有分类关联（级联清理入口）。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAllByModelIds(List<Long> modelIds) {
        if (modelIds == null || modelIds.isEmpty()) {
            return;
        }

        relationMapper.deleteByModelIds(modelIds);
        log.info("批量删除实体的所有分类关联: modelIds={}", modelIds);
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
     * @param modelId 实体ID
     * @param categoryIds 分类ID列表
     * @param modelEntityType 实体所属业务类型（用于跨业务关联验证）
     * @return 关联执行结果
     */
    private BatchAssociateResult batchAssociateInternal(Long modelId, List<Long> categoryIds, String modelEntityType) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return new BatchAssociateResult(0, Collections.emptyList(), Collections.emptyList());
        }

        List<ModelCategoryAssociationRespVO.FailItem> failItems = new ArrayList<>();

        // 1) 分类Id的存在性校验（ 一次查询所有分类存在性 ）
        Set<Long> existingCategoryIds = categoryService.filterExistingCategoryIds(categoryIds);
        List<Long> validCategoryIds = new ArrayList<>();
        // 为了精确的失败明细（哪些分类不存在）
        for (Long categoryId : categoryIds) {
            if (existingCategoryIds.contains(categoryId)) {
                validCategoryIds.add(categoryId);
            } else {
                failItems.add(ModelCategoryAssociationRespVO.FailItem.builder()
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
        List<ModelCategoryRelationDO> existingRelations = relationMapper
                // mybatis-plus 的配置过滤了 deleted=true 的记录，所以需要显式查询包含 deleted=true 的记录
                .selectByModelAndCategoryIdsIncludingDeleted(modelId, validCategoryIds, modelEntityType);

        Set<Long> alreadyActive = new LinkedHashSet<>();
        Set<Long> restoreCandidates = new LinkedHashSet<>();
        for (ModelCategoryRelationDO relation : existingRelations) {
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
            int restored = relationMapper.restoreDeletedRelationsBatch(modelId, toRestore, modelEntityType);
            successCount += restored;
            successCategoryIds.addAll(toRestore);

            // 恢复记录重新编号 sort（不影响 alreadyActive）
            Map<Long, Integer> baseSortMap = calcBaseSortByCategoryIds(toRestore);
            Map<Long, Integer> sortOffsetMap = new HashMap<>();
            for (Long categoryId : toRestore) {
                relationMapper.updateSortByModelAndCategory(
                        modelId,
                        categoryId,
                        calculateSortWithBase(baseSortMap, sortOffsetMap, categoryId),
                        modelEntityType
                );
            }
        }

        // 5) 批量新增剩余关联
        if (!toInsert.isEmpty()) {
            Map<Long, Integer> baseSortMap = calcBaseSortByCategoryIds(toInsert);
            Map<Long, Integer> sortOffsetMap = new HashMap<>();
            List<ModelCategoryRelationDO> relationsToInsert = new ArrayList<>();
            for (Long categoryId : toInsert) {
                relationsToInsert.add(ModelCategoryRelationDO.builder()
                        .modelId(modelId)
                        .categoryId(categoryId)
                        .entityTypeCode(modelEntityType)
                        .sort(calculateSortWithBase(baseSortMap, sortOffsetMap, categoryId))
                        .build());
            }
            try {
                enrichRelationCodes(relationsToInsert);
                relationMapper.insertBatchRelations(relationsToInsert);
                successCount += relationsToInsert.size();
                successCategoryIds.addAll(toInsert);
            } catch (Exception e) {
                for (Long categoryId : toInsert) {
                    failItems.add(ModelCategoryAssociationRespVO.FailItem.builder()
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

        log.info("批量关联实体到分类: modelId={}, categoryIds={}, successCount={}, failCount={}",
                modelId, categoryIds, successCount, failItems.size());
        return new BatchAssociateResult(successCount, distinctSuccessCategoryIds, failItems);
    }

    private record BatchAssociateResult(int successCount,
                                        List<Long> successCategoryIds,
                                        List<ModelCategoryAssociationRespVO.FailItem> failItems) {
    }

    // ==================== 按分类和业务类型查询实体 ====================

    /**
     * 分类关联排序结果载体（仅承载 ID 结果，不含实体详情）。
     *
     * <p>字段说明：</p>
     * <ul>
     *   <li>orderedModelIds：已经按分类上下文排序后的实体ID序列；</li>
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
        final List<Long> orderedModelIds;

        RelationOrderData(List<Long> orderedModelIds) {
            this.orderedModelIds = orderedModelIds;
        }
    }

    /**
     * 标准化函数：从分类范围构建“关联排序数据”。
     *
     * <p><b>这是“先排序后分页”的核心前置步骤</b>：</p>
     * <ol>
     *   <li>查询分类范围内所有关联关系（Mapper 已按 relation.sort + relation.id 排序）</li>
     *   <li>提取去重后的有序 modelId 列表（作为全局排序后的主序列）</li>
     *   <li>构建 modelId -> relation.sort 映射（供 VO 回填和防御性排序）</li>
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
     * <p><b>输出语义</b>：返回保序去重后的 modelId 列表，顺序来自 relation.sort。</p>
     */
    @Override
    public List<Long> listModelIdsByCategoryIdsOnly(List<Long> categoryIds, String entityTypeCode) {
        return resolveOrderedModelIds(categoryIds, entityTypeCode);
    }

    @Override
    public List<Long> listModelIdsByCategoryIdsWithDescendants(List<Long> categoryIds, String entityTypeCode) {
        if (categoryIds == null || categoryIds.isEmpty() || entityTypeCode == null || entityTypeCode.isBlank()) {
            return new ArrayList<>();
        }

        // 多分类含子树：对每个输入分类做子树展开，并按输入顺序稳定合并
        List<Long> expandedCategoryIds = expandCategoryIdsWithDescendants(categoryIds);
        return resolveOrderedModelIds(expandedCategoryIds, entityTypeCode);
    }

    @Override
    public PageResult<Long> pageModelIdsByCategoryIdOnly(Long categoryId, String entityTypeCode,
                                                            Integer pageNo, Integer pageSize) {
        List<Long> ordered = listModelIdsByCategoryIdOnly(categoryId, entityTypeCode);
        return pageOrderedIds(ordered, pageNo, pageSize);
    }

    @Override
    public PageResult<Long> pageModelIdsByCategoryIdWithDescendants(Long categoryId, String entityTypeCode,
                                                                        Integer pageNo, Integer pageSize) {
        List<Long> ordered = listModelIdsByCategoryIdWithDescendants(categoryId, entityTypeCode);
        return pageOrderedIds(ordered, pageNo, pageSize);
    }

    @Override
    public PageResult<Long> pageModelIdsByCategoryIdsOnly(List<Long> categoryIds, String entityTypeCode,
                                                            Integer pageNo, Integer pageSize) {
        List<Long> ordered = listModelIdsByCategoryIdsOnly(categoryIds, entityTypeCode);
        return pageOrderedIds(ordered, pageNo, pageSize);
    }

    @Override
    public PageResult<Long> pageModelIdsByCategoryIdsWithDescendants(List<Long> categoryIds, String entityTypeCode,
                                                                        Integer pageNo, Integer pageSize) {
        List<Long> ordered = listModelIdsByCategoryIdsWithDescendants(categoryIds, entityTypeCode);
        return pageOrderedIds(ordered, pageNo, pageSize);
    }

    @Override
    public PageResult<Long> pageModelIdsByCategoryIdsOnlyDb(List<Long> categoryIds, String entityTypeCode,
                                                                Integer pageNo, Integer pageSize) {
        if (categoryIds == null || categoryIds.isEmpty() || entityTypeCode == null || entityTypeCode.isBlank()) {
            return new PageResult<>(new ArrayList<>(), 0L);
        }
        int pn = normalizePageNo(pageNo);
        int ps = normalizePageSize(pageSize);
        int offset = (pn - 1) * ps;

        List<Long> pageIds;
        long total;

        // 单分类不需要 rank，直接走分类内排序分页
        if (categoryIds.size() == 1) {
            Long categoryId = categoryIds.get(0);
            pageIds = relationMapper.selectModelIdsBySingleCategoryPaged(categoryId, entityTypeCode, offset, ps);
            total = relationMapper.countModelIdsBySingleCategory(categoryId, entityTypeCode);
        } else {
            pageIds = relationMapper.selectModelIdsByCategoryIdsRankPaged(categoryIds, entityTypeCode, offset, ps);
            total = relationMapper.countModelIdsByCategoryIdsRank(categoryIds, entityTypeCode);
        }

        return new PageResult<>(pageIds == null ? new ArrayList<>() : pageIds, total);
    }

    @Override
    public PageResult<Long> pageModelIdsByCategoryIdsWithDescendantsDb(List<Long> categoryIds, String entityTypeCode,
                                                                            Integer pageNo, Integer pageSize) {
        if (categoryIds == null || categoryIds.isEmpty() || entityTypeCode == null || entityTypeCode.isBlank()) {
            return new PageResult<>(new ArrayList<>(), 0L);
        }
        List<Long> expandedCategoryIds = expandCategoryIdsWithDescendants(categoryIds);
        return pageModelIdsByCategoryIdsOnlyDb(expandedCategoryIds, entityTypeCode, pageNo, pageSize);
    }

    /**
     * 统一解析“分类范围实体ID查询”结果（不负责子树展开）。
     *
     * @param categoryIds 分类ID集合（可为单个或多个）
     * @param entityTypeCode 业务类型编码
     * @return 稳定有序实体ID列表
     */
    private List<Long> resolveOrderedModelIds(List<Long> categoryIds, String entityTypeCode) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return new ArrayList<>();
        }
        // 兼容模式：entityTypeCode 为空表示不按业务过滤（仅用于兼容旧查询接口）
        if (entityTypeCode == null || entityTypeCode.isBlank()) {
            List<ModelCategoryRelationDO> relations = relationMapper.selectRelationsByCategoryIdsForOrdering(categoryIds, null);
            if (relations == null || relations.isEmpty()) {
                return new ArrayList<>();
            }
            List<Long> orderedModelIds = new ArrayList<>();
            Set<Long> seen = new HashSet<>();
            for (Long categoryId : categoryIds) {
                for (ModelCategoryRelationDO relation : relations) {
                    if (!Objects.equals(categoryId, relation.getCategoryId())) {
                        continue;
                    }
                    Long modelId = relation.getModelId();
                    if (modelId != null && seen.add(modelId)) {
                        orderedModelIds.add(modelId);
                    }
                }
            }
            return orderedModelIds;
        }
        // 快路径：仅1个分类时直接走单分类查询，避免走多分类编排
        if (categoryIds.size() == 1) {
            return listModelIdsByCategoryIdOnly(categoryIds.get(0), entityTypeCode);
        }
        // 多分类路径：按“categoryIds顺序 -> 分类内sort,id -> 稳定去重”输出
        RelationOrderData orderData = buildRelationOrderData(categoryIds, entityTypeCode);
        return orderData == null ? new ArrayList<>() : orderData.orderedModelIds;
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
     * 对已排序 modelId 列表执行内存分页。
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
    private void validateModelIdNotNull(Long modelId) {
        if (modelId == null) {
            throw new ServiceException(400, "modelId 不能为空");
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
    private boolean isModelExists(Long modelId, String entityTypeCode) {
        return modelCoreService.existsById(modelId, entityTypeCode);
    }

    /**
     * 校验实体是否存在
     */
    private void validateModelExists(Long modelId, String entityTypeCode) {
        if (!isModelExists(modelId, entityTypeCode)) {
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

    /** 写入/更新关联时同步 model_code + category_code（迁移以 code 为幂等键）。 */
    private void syncRelationIdentity(ModelCategoryRelationDO relation) {
        if (relation == null) {
            return;
        }
        enrichRelationCodes(List.of(relation));
    }

    private void enrichRelationCodes(List<ModelCategoryRelationDO> relations) {
        if (relations == null || relations.isEmpty()) {
            return;
        }
        List<Long> modelIds = relations.stream()
                .map(ModelCategoryRelationDO::getModelId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        List<Long> categoryIds = relations.stream()
                .map(ModelCategoryRelationDO::getCategoryId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        Map<Long, String> modelCodeById = modelCoreService.listByIds(modelIds).stream()
                .collect(Collectors.toMap(ModelDO::getId, ModelDO::getCode, (a, b) -> a));
        Map<Long, String> categoryCodeById = new HashMap<>();
        for (Long categoryId : categoryIds) {
            CategoryDO category = categoryMapper.selectById(categoryId);
            if (category != null) {
                categoryCodeById.put(categoryId, category.getCode());
            }
        }
        for (ModelCategoryRelationDO relation : relations) {
            if (relation.getModelId() != null) {
                relation.setModelCode(modelCodeById.get(relation.getModelId()));
            }
            if (relation.getCategoryId() != null) {
                relation.setCategoryCode(categoryCodeById.get(relation.getCategoryId()));
            }
        }
    }

    /**
     * 批量收集存在的实体ID，并输出不存在实体列表。
     */
    private Set<Long> collectExistingModelIds(List<Long> modelIds, String entityTypeCode, List<Long> notFoundModelIds) {
        Set<Long> existingModelIds = modelCoreService.filterExistingModelIds(modelIds, entityTypeCode);
        for (Long modelId : modelIds) {
            if (!existingModelIds.contains(modelId)) {
                notFoundModelIds.add(modelId);
            }
        }
        return existingModelIds;
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
        List<ModelCategoryRelationDO> existing = relationMapper.selectByCategoryId(categoryId);
        int max = existing.stream()
                .map(ModelCategoryRelationDO::getSort)
                .filter(Objects::nonNull)
                .max(Integer::compareTo)
                .orElse(0);
        return SparseSortUtils.next(max);
    }

    /**
     * 从“分类范围 + 业务类型”构建有序实体ID序列。
     *
     * <p><b>该方法是分类侧到实体侧的桥接核心</b>，用于输出可直接交给 ModelService 的 ID 结果。</p>
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
            List<ModelCategoryRelationDO> singleRelations = relationMapper.selectByCategoryId(singleCategoryId);
            if (singleRelations == null || singleRelations.isEmpty()) {
                return new RelationOrderData(Collections.emptyList());
            }

            List<Long> orderedModelIds = singleRelations.stream()
                    .filter(r -> r.getModelId() != null)
                    .filter(r -> entityTypeCode.equals(r.getEntityTypeCode()))
                    .map(ModelCategoryRelationDO::getModelId)
                    .distinct()
                    .toList();
            return new RelationOrderData(orderedModelIds);
        }

        // ========== 步骤2：查询多分类原始关系数据 ==========
        // 说明：这里拿到的是“候选关系记录集合”，不能直接作为最终顺序。
        // 原因：Mapper 层无法保证“先 categoryIds 输入顺序，再分类内 sort”的完整语义。
        List<ModelCategoryRelationDO> relations = relationMapper.selectRelationsByCategoryIdsForOrdering(categoryIds, entityTypeCode);
        if (relations == null || relations.isEmpty()) {
            return new RelationOrderData(Collections.emptyList());
        }

        // ========== 步骤3：按分类分桶（categoryId -> relations） ==========
        // 目标：把“跨分类混合数据”拆成“每个分类自己的小集合”，便于执行“分类内排序”。
        Map<Long, List<ModelCategoryRelationDO>> grouped = relations.stream()
                .filter(r -> r.getCategoryId() != null && r.getModelId() != null)
                .collect(Collectors.groupingBy(ModelCategoryRelationDO::getCategoryId));

        // ========== 步骤4：分类内排序（sort -> id） ==========
        // 规则：sort 只在同一分类内生效；sort 为空时视为最大值，排在最后。
        // 兜底：id 升序，确保同 sort 下结果稳定。
        for (List<ModelCategoryRelationDO> bucket : grouped.values()) {
            bucket.sort(Comparator
                    .comparing((ModelCategoryRelationDO r) -> r.getSort() == null ? Integer.MAX_VALUE : r.getSort())
                    .thenComparing(r -> r.getId() == null ? Long.MAX_VALUE : r.getId()));
        }

        // ========== 步骤5：按 categoryIds 输入顺序回放 ==========
        // 目标：保证跨分类顺序由“调用方给定的 categoryIds 顺序”决定。
        // 这一步是多分类稳定排序的关键，不能被全局 sort 替代。
        List<Long> orderedModelIds = new ArrayList<>();

        // ========== 步骤6：稳定去重 ==========
        // 规则：同一 model 命中多个分类时，仅保留第一次出现的位置。
        Set<Long> seen = new HashSet<>();
        for (Long categoryId : categoryIds) {
            List<ModelCategoryRelationDO> bucket = grouped.get(categoryId);
            if (bucket == null || bucket.isEmpty()) {
                continue;
            }
            for (ModelCategoryRelationDO relation : bucket) {
                Long modelId = relation.getModelId();
                if (modelId != null && seen.add(modelId)) {
                    orderedModelIds.add(modelId);
                }
            }
        }

        // ========== 步骤7：封装输出 ==========
        return new RelationOrderData(orderedModelIds);
    }

    /**
     * 批量实体关联操作的聚合结果载体。
     *
     * <p>用途：在批量处理循环中先汇总“每个实体”的执行结果，
     * 最后再转换为响应 VO，避免在循环中直接拼装复杂 Builder 造成可读性下降。</p>
     *
     * <p>字段语义：</p>
     * <ul>
     *   <li>modelId：当前处理实体ID；</li>
     *   <li>modelExists：实体是否存在（用于区分 404 与业务失败）；</li>
     *   <li>successCount：该实体在本次批处理中的成功关联数；</li>
     *   <li>failCount：该实体在本次批处理中的失败关联数；</li>
     *   <li>errorMessage：失败时的简要原因（存在时优先展示）。</li>
     * </ul>
     */
    private static class ModelResultData {
        final Long modelId;
        final Boolean modelExists;
        final Integer successCount;
        final Integer failCount;
        final String errorMessage;

        ModelResultData(Long modelId, Boolean modelExists, Integer successCount, Integer failCount, String errorMessage) {
            this.modelId = modelId;
            this.modelExists = modelExists;
            this.successCount = successCount;
            this.failCount = failCount;
            this.errorMessage = errorMessage;
        }
    }

}
