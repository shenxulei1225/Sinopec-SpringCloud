package cn.cheers.x.module.dynamicbusiness.service.model;

import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;

import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeBaseFieldRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelFieldAssignmentRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelFieldGroupCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelPageReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelAvailableFieldRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelBatchSortReqVO;
import cn.cheers.x.module.dynamicbusiness.convert.model.ModelConvert;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelCategoryRelationDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.category.CategoryMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntityMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelCategoryRelationMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelFieldAssignmentMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryDO;
import cn.cheers.x.module.dynamicbusiness.enums.entitytype.StorageTypeEnum;
import cn.cheers.x.module.dynamicbusiness.service.entitytype.EntityTypeBaseFieldService;
import cn.cheers.x.module.dynamicbusiness.service.entitytype.EntityTypeService;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryService;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryTypeService;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.service.dynamictable.DynamicTableService;
import cn.cheers.x.module.dynamicbusiness.service.model.core.ModelCoreService;
import cn.cheers.x.module.dynamicbusiness.util.SparseSortUtils;
import cn.cheers.x.module.dynamicbusiness.framework.entitytype.EntityTypeScopeContext;
import cn.cheers.x.module.dynamicbusiness.event.ModelCreatedEvent;
import cn.cheers.x.module.dynamicbusiness.event.RelationTargetCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 业务模型 Service 实现类
 *
 * @author yudao
 */
@Service
@Validated
@Slf4j
public class ModelServiceImpl implements ModelService {

    @Resource
    private ModelCoreService modelCoreService;
    @Resource
    private ModelFieldAssignmentMapper modelFieldAssignmentMapper;
    @Resource
    private ModelCategoryRelationMapper modelCategoryRelationMapper;
    @Resource
    private ModelMapper modelMapper;
    @Resource
    private EntityMapper entityMapper;
    @Resource
    private CategoryService categoryService;
    @Resource
    private CategoryTypeService categoryTypeService;
    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private EntityTypeBaseFieldService entityTypeBaseFieldService;
    @Resource
    private EntityTypeMapper entityTypeMapper;
    @Resource
    @Lazy // 避免与 EntityTypeServiceImpl 循环依赖
    private EntityTypeService entityTypeService;
    @Resource
    @Lazy // 避免循环依赖
    private ModelFieldAssignmentService modelFieldAssignmentService;

    @Resource
    @Lazy // 避免循环依赖
    private DynamicTableService dynamicTableService;

    @Resource
    private ApplicationEventPublisher eventPublisher;

    @Resource
    @Lazy // 避免循环依赖
    private cn.cheers.x.module.dynamicbusiness.service.model.relation.ModelCategoryRelationService modelCategoryRelationService;

    @Resource
    private ModelFieldGroupService modelFieldGroupService;


    /**
     * 检查模型是否存在
     *
     * @param modelId 模型ID
     * @return 是否存在
     */
    @Override
    public boolean existsById(Long modelId) {
        if (modelId == null) {
            return false;
        }
        // 与 Entity 侧保持一致：基础存在性检查统一走 Core
        ModelDO model = modelCoreService.get(modelId);
        return model != null;
    }

    // ==================== 基础 CRUD 操作 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createModel(ModelCreateReqVO reqVO) {
        // 校验业务类型编码是否存在（租户隔离由 MyBatis Plus 租户插件自动处理,EntityTypeDO 继承 TenantBaseDO）
        if (reqVO.getEntityTypeCode() != null) {
            EntityTypeDO entityType = 
                    entityTypeMapper.selectByCode(reqVO.getEntityTypeCode());
            if (entityType == null) {
                throw new ServiceException(404, "业务类型不存在,编码：" + reqVO.getEntityTypeCode());
            }
        }
        
        // 校验模型名称唯一性（租户隔离由 MyBatis Plus 租户插件自动处理）
        try {
            ModelDO existModel = modelCoreService.getByNameInEntityType(reqVO.getName(), reqVO.getEntityTypeCode());
            if (existModel != null) {
                throw new ServiceException(400, "模型名称已存在：" + reqVO.getName());
            }
        } catch (TooManyResultsException e) {
            // 如果有多条同名记录,也视为名称已存在
            throw new ServiceException(400, "模型名称已存在：" + reqVO.getName());
        }

        // 创建模型（租户插件会自动填充 tenantId）
        ModelDO model = ModelConvert.INSTANCE.convert(reqVO);
        model.setCode(generateCode());
        model.setDataScope(EntityTypeScopeContext.normalizeScope(reqVO.getDataScope()));
        if (model.getSort() == null) {
            Integer maxSort = modelMapper.selectMaxSortByEntityTypeCode(reqVO.getEntityTypeCode());
            model.setSort(SparseSortUtils.next(maxSort));
        }
        modelCoreService.create(model);
        
        // 在创建模型时,将模型与一个或多个分类建立关联（多对多关系）
        // 优先使用 categoryIds 列表,如果没有则使用 categoryId（兼容旧版）
        List<Long> categoryIdsToBindCreate = new ArrayList<>();
        if (reqVO.getCategoryIds() != null && !reqVO.getCategoryIds().isEmpty()) {
            categoryIdsToBindCreate.addAll(reqVO.getCategoryIds());
        } else if (reqVO.getCategoryId() != null) {
            categoryIdsToBindCreate.add(reqVO.getCategoryId());
        }
        
        if (!categoryIdsToBindCreate.isEmpty()) {
            for (Long categoryIdToBind : categoryIdsToBindCreate) {
                // 验证分类是否存在（租户隔离由 MyBatis Plus 租户插件自动处理,CategoryDO 继承 TenantBaseDO）
                CategoryDO category = categoryMapper.selectById(categoryIdToBind);
                if (category == null) {
                    throw new ServiceException(404, "分类不存在,ID：" + categoryIdToBind);
                }
                // 创建关联关系（租户插件会自动填充 tenantId）
                ModelCategoryRelationDO relation = ModelCategoryRelationDO.builder()
                        .modelId(model.getId())
                        .modelCode(model.getCode())
                        .categoryId(categoryIdToBind)
                        .categoryCode(category.getCode())
                        .build();
                modelCategoryRelationMapper.insert(relation);
            }
        }
        
        // 为新模型自动创建默认“基础信息”分组（避免前端出现“未分组”）
        ModelFieldGroupCreateReqVO defaultGroupReq = new ModelFieldGroupCreateReqVO();
        defaultGroupReq.setModelId(model.getId());
        defaultGroupReq.setName("基础信息");
        defaultGroupReq.setColor("#409eff");
        modelFieldGroupService.createModelFieldGroup(defaultGroupReq);

        // 发布 Model 创建事件（用于事件驱动机制）
        // 需求：FR-BDA-005, FR-BDA-023, FR-BDA-072
        publishModelCreatedEvent(model);
        
        // 发布关联目标创建事件（用于更新关联字段库状态）
        // 需求：FR-BDA-014
        publishRelationTargetCreatedEvent(model);
        
        return model.getId();
    }

    @Override
    public void updateModel(ModelUpdateReqVO reqVO) {
        // 校验模型存在
        ModelDO existModel = modelCoreService.get(reqVO.getId());
        if (existModel == null) {
            throw new ServiceException(404, "模型不存在");
        }

        // 校验模型名称唯一性（同一租户内唯一,排除自己）
        // 注意：selectByName 会自动添加租户条件（ModelDO 继承 TenantBaseDO,MyBatis Plus 租户插件会自动处理）
        // 逻辑说明：
        // 1. 如果名称没变：selectByName 会找到自己,nameModel.getId().equals(reqVO.getId()) 为 true,允许更新
        // 2. 如果名称改变且不冲突：selectByName 返回 null（同一租户内没有同名模型）,允许更新
        // 3. 如果名称改变但与其他模型冲突：selectByName 找到同一租户内的其他模型,nameModel.getId() != reqVO.getId(),抛出异常
        try {
            ModelDO nameModel = modelCoreService.getByNameInEntityType(reqVO.getName(), reqVO.getEntityTypeCode());
            if (nameModel != null && !nameModel.getId().equals(reqVO.getId())) {
                throw new ServiceException(400, "模型名称已存在：" + reqVO.getName());
            }
        } catch (TooManyResultsException e) {
            // 如果有多条同名记录（理论上不应该发生,因为应该有唯一索引）,也视为名称已存在
            throw new ServiceException(400, "模型名称已存在：" + reqVO.getName());
        }

        // 更新模型（租户插件会自动添加 WHERE tenant_id = ? 条件）
        ModelDO model = ModelConvert.INSTANCE.convert(reqVO);
        if (model.getSort() == null) {
            model.setSort(existModel.getSort());
        }
        modelCoreService.update(model);

        // 处理分类绑定：如果 categoryIds 不为 null,则更新分类关联
        // - categoryIds 为 null：不更新分类关联（保持现有）
        // - replaceCategories=true：完全替换（使用 replaceModelCategories,删除所有+批量添加,复用软删除记录）
        // - replaceCategories=false：增量添加（只添加新分类,不删除现有分类）
        if (reqVO.getCategoryIds() != null) {
            Long modelId = reqVO.getId();
            boolean isReplaceMode = Boolean.TRUE.equals(reqVO.getReplaceCategories());
            
            if (isReplaceMode) {
                // 完全替换模式：使用 replaceModelCategories（删除所有+批量添加,复用软删除记录）
                modelCategoryRelationService.updateAssociation(modelId, reqVO.getCategoryIds(), reqVO.getEntityTypeCode());
            } else {
                // 增量添加模式：只添加新分类,不删除现有分类
                if (!reqVO.getCategoryIds().isEmpty()) {
                    CategoryRelationBatchResult result = addCategoryRelationsBatch(modelId, reqVO.getCategoryIds(), reqVO.getEntityTypeCode());
                    // 记录处理结果日志
                    logCategoryRelationResult(modelId, result, "增量添加模式");
                }
            }
        }
    }

    /**
     * 批量添加模型与分类的关联关系处理结果
     */
    private static class CategoryRelationBatchResult {
        private final List<Long> alreadyExists;    // 已存在且未删除的分类ID
        private final List<Long> restored;         // 已恢复的软删除分类ID
        private final List<Long> created;          // 新创建的分类ID

        public CategoryRelationBatchResult(List<Long> alreadyExists, List<Long> restored, List<Long> created) {
            this.alreadyExists = alreadyExists != null ? alreadyExists : new ArrayList<>();
            this.restored = restored != null ? restored : new ArrayList<>();
            this.created = created != null ? created : new ArrayList<>();
        }

        public List<Long> getAlreadyExists() {
            return alreadyExists;
        }

        public List<Long> getRestored() {
            return restored;
        }

        public List<Long> getCreated() {
            return created;
        }
    }

    /**
     * 批量添加模型与分类的关联关系（优化版本,减少数据库查询次数）
     * 
     * <p>处理逻辑：</p>
     * <ol>
     *   <li>批量验证分类是否存在</li>
     *   <li>批量查询现有关联（包括软删除的）</li>
     *   <li>将分类ID分为三类：
     *     <ul>
     *       <li>已存在且未删除：跳过（不做任何操作）</li>
     *       <li>已存在但软删除：批量恢复</li>
     *       <li>不存在：批量创建新关联</li>
     *     </ul>
     *   </li>
     *   <li>批量恢复软删除的关联（一次SQL）</li>
     *   <li>批量创建新的关联关系（一次SQL）</li>
     * </ol>
     * 
     * @param modelId 模型ID
     * @param categoryIds 分类ID列表
     * @return 处理结果,包含已存在、已恢复、已创建的分类ID列表
     */
    private CategoryRelationBatchResult addCategoryRelationsBatch(Long modelId, List<Long> categoryIds, String entityTypeCode) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return new CategoryRelationBatchResult(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        }
        
        // 步骤1：批量验证分类是否存在
        List<CategoryDO> categories = categoryMapper.selectList(
                new LambdaQueryWrapperX<CategoryDO>()
                        .in(CategoryDO::getId, categoryIds));
        Set<Long> existingCategoryIds = categories.stream()
                .map(CategoryDO::getId)
                .collect(Collectors.toSet());
        
        // 检查是否有不存在的分类
        List<Long> notFoundCategoryIds = categoryIds.stream()
                .filter(id -> !existingCategoryIds.contains(id))
                .collect(Collectors.toList());
        if (!notFoundCategoryIds.isEmpty()) {
            throw new ServiceException(404, "分类不存在,ID：" + notFoundCategoryIds);
        }
        
        // 步骤2：批量查询现有关联（包括软删除的）
        List<ModelCategoryRelationDO> existingRelations = modelCategoryRelationMapper.selectList(
                new LambdaQueryWrapperX<ModelCategoryRelationDO>()
                        .eq(ModelCategoryRelationDO::getModelId, modelId)
                        .eq(ModelCategoryRelationDO::getEntityTypeCode, entityTypeCode)
                        .in(ModelCategoryRelationDO::getCategoryId, categoryIds));
        
        // 步骤3：将分类ID分为三类
        Set<Long> existingAndNotDeleted = new HashSet<>();  // 已存在且未删除：跳过
        Set<Long> existingButDeleted = new HashSet<>();    // 已存在但软删除：需要恢复
        Set<Long> notExisting = new HashSet<>(categoryIds); // 不存在：需要创建
        
        for (ModelCategoryRelationDO relation : existingRelations) {
            Long categoryId = relation.getCategoryId();
            if (Boolean.TRUE.equals(relation.getDeleted())) {
                existingButDeleted.add(categoryId);
            } else {
                existingAndNotDeleted.add(categoryId);
            }
            notExisting.remove(categoryId); // 从"不存在"集合中移除
        }
        
        // 步骤4：批量恢复软删除的关联（租户插件自动处理 tenantId）
        if (!existingButDeleted.isEmpty()) {
            List<Long> categoryIdsToRestore = new ArrayList<>(existingButDeleted);
            modelCategoryRelationMapper.restoreDeletedRelationsBatch(modelId, categoryIdsToRestore, entityTypeCode);
        }
        
        // 步骤5：批量创建新的关联关系（租户插件会自动填充 tenantId）
        if (!notExisting.isEmpty()) {
            List<ModelCategoryRelationDO> relationsToInsert = new ArrayList<>();
            for (Long categoryId : notExisting) {
                ModelCategoryRelationDO relation = ModelCategoryRelationDO.builder()
                        .modelId(modelId)
                        .categoryId(categoryId)
                        .entityTypeCode(entityTypeCode)
                        .build();
                relationsToInsert.add(relation);
            }
            // 使用 MyBatis Plus 的批量插入方法
            modelCategoryRelationMapper.insertBatch(relationsToInsert);
        }
        
        // 返回处理结果
        return new CategoryRelationBatchResult(
                new ArrayList<>(existingAndNotDeleted),
                new ArrayList<>(existingButDeleted),
                new ArrayList<>(notExisting)
        );
    }

    /**
     * 记录分类关联处理结果日志
     * 
     * @param modelId 模型ID
     * @param result 处理结果
     * @param mode 处理模式
     */
    private void logCategoryRelationResult(Long modelId, CategoryRelationBatchResult result, String mode) {
        if (!result.getAlreadyExists().isEmpty() || !result.getRestored().isEmpty() || !result.getCreated().isEmpty()) {
            log.info("[updateModel] 分类关联处理结果（{}）：模型ID={}, 已存在={}, 已恢复={}, 已创建={}", 
                    mode, modelId, result.getAlreadyExists(), result.getRestored(), result.getCreated());
        }
    }

    @Override
    public void deleteModel(Long id) {
        // 校验模型存在
        ModelDO model = modelCoreService.get(id);
        if (model == null) {
            throw new ServiceException(404, "模型不存在");
        }

        // 校验是否有Entity关联（如果表不存在则跳过检查）
        try {
            // 优化：使用 selectOne + LIMIT 1 只需找到第一条匹配记录即可,比 selectCount 更高效
            // 数据库只需扫描到第一条记录就返回,无需统计全部数量
            EntityDO existingEntity = entityMapper.selectOne(
                    new LambdaQueryWrapperX<EntityDO>()
                            .eq(EntityDO::getModelId, id)
                            .eq(EntityDO::getTenantId, getTenantId())
                            .eq(EntityDO::getDeleted, false)
                            .last("LIMIT 1"));
            if (existingEntity != null) {
                throw new ServiceException(400, "模型存在关联的实体,禁止删除");
            }
        } catch (BadSqlGrammarException e) {
            // 如果表不存在,跳过关联检查,允许删除
            // 这通常发生在实体功能还未完全实现时
            String errorMsg = e.getMessage();
            if (errorMsg != null && (errorMsg.contains("不存在") || errorMsg.contains("does not exist") 
                    || errorMsg.contains("dynamic_entity"))) {
                // 表不存在,跳过检查,允许删除
            } else {
                // 其他SQL语法错误,重新抛出
                throw e;
            }
        } catch (DataAccessException e) {
            // 处理其他数据库访问异常,如果是表不存在的错误,也跳过检查
            String errorMsg = e.getMessage();
            if (errorMsg != null && (errorMsg.contains("不存在") || errorMsg.contains("does not exist") 
                    || errorMsg.contains("dynamic_entity"))) {
                // 表不存在,跳过检查
            } else {
                // 其他数据库错误,重新抛出
                throw e;
            }
        }

        // 删除模型字段分配
        modelFieldAssignmentMapper.deleteByModelId(id);
        // 删除模型分类关联（使用新服务）
        modelCategoryRelationService.deleteAllByModelId(id);
        // 删除模型
        modelCoreService.delete(id);
    }

    @Override
    public ModelRespVO getModel(Long id) {
        ModelDO model = modelCoreService.get(id);
        if (model == null) {
            throw new ServiceException(404, "模型不存在");
        }
        ModelRespVO result = ModelConvert.INSTANCE.convert(model);
        
        
        // 查询模型关联的所有分类ID（多对多关系）
        List<ModelCategoryRelationDO> relations = modelCategoryRelationMapper.selectByModelId(id);
        if (!relations.isEmpty()) {
            List<Long> categoryIds = relations.stream()
                    .map(ModelCategoryRelationDO::getCategoryId)
                    .toList();
            result.setCategoryIds(categoryIds);
        }
        
        return result;
    }

   

    /**
     * 根据业务类型编码获取模型列表
     */
    @Override
    public List<ModelRespVO> listModelsByEntityType(String entityTypeCode) {
        return listModelsByEntityType(entityTypeCode, null);
    }

    @Override
    public List<ModelRespVO> listModelsByEntityType(String entityTypeCode, String dataScope) {
        List<ModelDO> list = filterModelDosByDataScope(
                modelCoreService.listByEntityTypeCode(entityTypeCode), dataScope);
        if (list.isEmpty()) {
            return List.of();
        }

        List<ModelRespVO> result = ModelConvert.INSTANCE.convertList(list);
        fillModelCategoryIds(result);
        return result;
    }

    private List<ModelDO> filterModelDosByDataScope(List<ModelDO> models, String dataScope) {
        if (models == null || models.isEmpty() || !org.springframework.util.StringUtils.hasText(dataScope)) {
            return models == null ? List.of() : models;
        }
        String normalized = dataScope.trim();
        return models.stream()
                .filter(model -> EntityTypeScopeContext.scopesEqual(model.getDataScope(), normalized))
                .toList();
    }

    private List<ModelRespVO> filterModelVosByDataScope(List<ModelRespVO> models, String dataScope) {
        if (models == null || models.isEmpty() || !org.springframework.util.StringUtils.hasText(dataScope)) {
            return models == null ? List.of() : models;
        }
        String normalized = dataScope.trim();
        return models.stream()
                .filter(model -> EntityTypeScopeContext.scopesEqual(model.getDataScope(), normalized))
                .toList();
    }

    @Override
    public List<ModelRespVO> listUncategorizedModelsByCategoryType(String categoryTypeCode, String entityTypeCode) {
        return listUncategorizedModelsByCategoryType(categoryTypeCode, entityTypeCode, null);
    }

    @Override
    public List<ModelRespVO> listUncategorizedModelsByCategoryType(
            String categoryTypeCode, String entityTypeCode, String dataScope) {
        if (entityTypeCode == null || entityTypeCode.isBlank()) {
            throw new ServiceException(400, "entityTypeCode 不能为空");
        }
        if (categoryTypeCode == null || categoryTypeCode.isBlank()) {
            throw new ServiceException(400, "categoryTypeCode 不能为空");
        }
        List<ModelRespVO> allModels = listModelsByEntityType(entityTypeCode, dataScope);
        if (allModels.isEmpty()) {
            return allModels;
        }
        List<Long> categorizedModelIds = modelCategoryRelationMapper.selectDistinctModelIdsByCategoryTypeCode(
                categoryTypeCode, entityTypeCode);
        Set<Long> categorized = categorizedModelIds == null ? Set.of()
                : categorizedModelIds.stream().filter(Objects::nonNull).collect(Collectors.toSet());
        if (categorized.isEmpty()) {
            return allModels;
        }
        return allModels.stream()
                .filter(model -> model.getId() != null && !categorized.contains(model.getId()))
                .toList();
    }

    @Override
    public List<ModelRespVO> filterModelsByDataScope(List<ModelRespVO> models, String dataScope) {
        return filterModelVosByDataScope(models, dataScope);
    }

    /**
     * 获取全部业务模型列表（不分页）
     */
    @Override
    public List<ModelRespVO> listModelsAcrossEntityTypes() {
        // Service 层能力：跨业务类型模型查询（含启用/停用）
        return listModelsAcrossEntityTypesByStatus(null);
    }

    @Override
    public List<ModelRespVO> listEnabledModelsAcrossEntityTypes() {
        // Service 层能力：跨业务类型启用模型查询（非 Core 的单业务能力）
        return listModelsAcrossEntityTypesByStatus(1);
    }

    @Override
    @Deprecated
    public List<ModelRespVO> listAllModels() {
        return listEnabledModelsAcrossEntityTypes();
    }

    private List<ModelRespVO> listModelsAcrossEntityTypesByStatus(Integer status) {
        List<EntityTypeRespVO> entityTypes = entityTypeService.listAll();
        java.util.Map<String, Integer> entityTypeOrder = new java.util.HashMap<>();
        for (EntityTypeRespVO bt : entityTypes) {
            if (bt != null && bt.getCode() != null && !bt.getCode().isBlank()) {
                entityTypeOrder.put(bt.getCode(), bt.getSort() == null ? Integer.MAX_VALUE : bt.getSort());
            }
        }

        List<ModelDO> list = modelMapper.selectList(new LambdaQueryWrapperX<ModelDO>()
                .eq(status != null, ModelDO::getStatus, status));

        if (list.isEmpty()) {
            return List.of();
        }

        List<ModelDO> ordered = list.stream()
                .sorted(Comparator
                        .comparing((ModelDO m) -> entityTypeOrder.getOrDefault(m.getEntityTypeCode(), Integer.MAX_VALUE))
                        .thenComparing((ModelDO m) -> m.getEntityTypeCode() == null ? "" : m.getEntityTypeCode())
                        .thenComparing((ModelDO m) -> m.getSort() == null ? Integer.MAX_VALUE : m.getSort())
                        .thenComparing(ModelDO::getCreateTime, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing((ModelDO m) -> m.getId() == null ? Long.MAX_VALUE : m.getId()))
                .toList();

        List<ModelRespVO> result = ModelConvert.INSTANCE.convertList(ordered);
        fillModelCategoryIds(result);
        return result;
    }


    /**
     * 填充模型关联的分类ID列表
     * <p>
     * 这个步骤通常用于**前端展示/编辑“模型归属哪些分类”**的场景，比如：
     * 模型列表里需要显示“已关联分类”标签
     * 编辑模型时需要回填已选分类（多选框/树）
     * 批量调整分类时需要知道模型当前的所有分类
     * 所以它的目的不是“关联/解除关联”，而是给前端回显关联关系。
     * </p>
     */
    private void fillModelCategoryIds(List<ModelRespVO> models) {
        if (models.isEmpty()) {
            return;
        }

        List<Long> modelIds = models.stream().map(ModelRespVO::getId).toList();
        // 批量查询所有模型的分类关联
        List<ModelCategoryRelationDO> allRelations = modelCategoryRelationMapper.selectList(
                new LambdaQueryWrapperX<ModelCategoryRelationDO>()
                        .in(ModelCategoryRelationDO::getModelId, modelIds));

        // 构建模型ID到分类ID列表的映射（多对多）
        java.util.Map<Long, List<Long>> modelCategoryIdsMap = allRelations.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        ModelCategoryRelationDO::getModelId,
                        java.util.stream.Collectors.mapping(
                                ModelCategoryRelationDO::getCategoryId,
                                java.util.stream.Collectors.toList()
                        )
                ));

        // 设置每个模型的分类ID列表
        models.forEach(model -> {
            List<Long> categoryIds = modelCategoryIdsMap.get(model.getId());
            if (categoryIds != null && !categoryIds.isEmpty()) {
                model.setCategoryIds(categoryIds);
            }
        });
    }



    @Override
    public PageResult<ModelRespVO> pageModelByEntityTypeCode(ModelPageReqVO reqVO) {
        PageResult<ModelDO> pageResult;
        Integer status = reqVO.getStatus() != null ? reqVO.getStatus() : 1;
        if (Boolean.TRUE.equals(reqVO.getIncludeChildren()) && reqVO.getEntityTypeCode() != null && !reqVO.getEntityTypeCode().isBlank()) {
            List<String> entityTypeCodes = collectEntityTypeCodesWithChildren(reqVO.getEntityTypeCode());
            List<ModelDO> allModels = modelMapper.selectList(new LambdaQueryWrapperX<ModelDO>()
                    .in(ModelDO::getEntityTypeCode, entityTypeCodes)
                    .eq(ModelDO::getStatus, status)
                    .and(org.apache.commons.lang3.StringUtils.isNotBlank(reqVO.getKeyword()),
                            q -> q.like(ModelDO::getName, reqVO.getKeyword())
                                    .or().like(ModelDO::getDescription, reqVO.getKeyword())));

            java.util.Map<String, Integer> entityTypeOrder = new java.util.HashMap<>();
            for (int i = 0; i < entityTypeCodes.size(); i++) {
                entityTypeOrder.put(entityTypeCodes.get(i), i);
            }

            List<ModelDO> ordered = allModels.stream()
                    .sorted(Comparator
                            .comparing((ModelDO m) -> entityTypeOrder.getOrDefault(m.getEntityTypeCode(), Integer.MAX_VALUE))
                            .thenComparing((ModelDO m) -> m.getSort() == null ? Integer.MAX_VALUE : m.getSort())
                            .thenComparing(ModelDO::getCreateTime, Comparator.nullsLast(Comparator.reverseOrder()))
                            .thenComparing((ModelDO m) -> m.getId() == null ? Long.MAX_VALUE : m.getId()))
                    .toList();

            int pageNo = reqVO.getPageNo() == null || reqVO.getPageNo() < 1 ? 1 : reqVO.getPageNo();
            int pageSize = reqVO.getPageSize() == null || reqVO.getPageSize() < 1 ? 20 : reqVO.getPageSize();
            int from = (pageNo - 1) * pageSize;
            List<ModelDO> pageList = from >= ordered.size()
                    ? java.util.List.of()
                    : ordered.subList(from, Math.min(from + pageSize, ordered.size()));
            pageResult = new PageResult<>(pageList, (long) ordered.size());
        } else {
            pageResult = modelCoreService.pageModels(
                    reqVO.getEntityTypeCode(),
                    EntityTypeScopeContext.normalizeScope(reqVO.getDataScope()),
                    reqVO.getKeyword(),
                    status,
                    reqVO.getPageNo(),
                    reqVO.getPageSize());
        }

        List<ModelRespVO> result = ModelConvert.INSTANCE.convertList(pageResult.getList());
        
        // 查询每个模型关联的所有分类ID（多对多关系）
        if (!result.isEmpty()) {
            List<Long> modelIds = result.stream().map(ModelRespVO::getId).toList();
            // 批量查询所有模型的分类关联
            List<ModelCategoryRelationDO> allRelations = modelCategoryRelationMapper.selectList(
                    new LambdaQueryWrapperX<ModelCategoryRelationDO>()
                            .in(ModelCategoryRelationDO::getModelId, modelIds));
            
            // 构建模型ID到分类ID列表的映射（多对多）
            java.util.Map<Long, List<Long>> modelCategoryIdsMap = allRelations.stream()
                    .collect(java.util.stream.Collectors.groupingBy(
                            ModelCategoryRelationDO::getModelId,
                            java.util.stream.Collectors.mapping(
                                    ModelCategoryRelationDO::getCategoryId,
                                    java.util.stream.Collectors.toList()
                            )
                    ));
            
            // 设置每个模型的分类ID列表
            result.forEach(model -> {
                List<Long> categoryIds = modelCategoryIdsMap.get(model.getId());
                if (categoryIds != null && !categoryIds.isEmpty()) {
                    model.setCategoryIds(categoryIds);
                }
            });
        }
        
        return new PageResult<>(result, pageResult.getTotal());
    }

    @Override
    public List<ModelRespVO> searchModels(String keyword, String entityTypeCode) {
        List<ModelDO> list;
        if (entityTypeCode == null || entityTypeCode.isBlank()) {
            return List.of();
        }
        list = modelCoreService.searchLikeInEntityType(keyword, entityTypeCode);
        List<ModelRespVO> result = ModelConvert.INSTANCE.convertList(list);
        
        // 查询每个模型关联的所有分类ID（多对多关系）
        if (!result.isEmpty()) {
            List<Long> modelIds = result.stream().map(ModelRespVO::getId).toList();
            // 批量查询所有模型的分类关联
            List<ModelCategoryRelationDO> allRelations = modelCategoryRelationMapper.selectList(
                    new LambdaQueryWrapperX<ModelCategoryRelationDO>()
                            .in(ModelCategoryRelationDO::getModelId, modelIds));
            
            // 构建模型ID到分类ID列表的映射（多对多）
            java.util.Map<Long, List<Long>> modelCategoryIdsMap = allRelations.stream()
                    .collect(java.util.stream.Collectors.groupingBy(
                            ModelCategoryRelationDO::getModelId,
                            java.util.stream.Collectors.mapping(
                                    ModelCategoryRelationDO::getCategoryId,
                                    java.util.stream.Collectors.toList()
                            )
                    ));
            
            // 设置每个模型的分类ID列表
            result.forEach(model -> {
                List<Long> categoryIds = modelCategoryIdsMap.get(model.getId());
                if (categoryIds != null && !categoryIds.isEmpty()) {
                }
            });
        }
        
        return result;
    }

    /**
     * 收集业务类型编码及其全部子业务编码（广度优先，保序去重）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateModelSort(ModelBatchSortReqVO reqVO) {
        if (reqVO == null || reqVO.getItems() == null || reqVO.getItems().isEmpty()) {
            throw new ServiceException(400, "模型排序列表不能为空");
        }

        List<ModelBatchSortReqVO.Item> orderedItems = reqVO.getItems().stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(ModelBatchSortReqVO.Item::getIndex))
                .toList();

        for (ModelBatchSortReqVO.Item item : orderedItems) {
            ModelDO model = modelCoreService.get(item.getModelId());
            if (model == null) {
                throw new ServiceException(404, "模型不存在: " + item.getModelId());
            }
            if (!reqVO.getEntityTypeCode().equals(model.getEntityTypeCode())) {
                throw new ServiceException(400, "模型不属于指定业务类型: " + item.getModelId());
            }
        }

        int idx = 0;
        for (ModelBatchSortReqVO.Item item : orderedItems) {
            ModelDO updateDO = new ModelDO();
            updateDO.setId(item.getModelId());
            updateDO.setSort(SparseSortUtils.reindexSortByPosition(idx++));
            modelCoreService.update(updateDO);
        }
    }

    private List<String> collectEntityTypeCodesWithChildren(String entityTypeCode) {
        List<EntityTypeRespVO> children =
                entityTypeService.listChildrenTreeByCode(entityTypeCode);

        LinkedHashSet<String> codes = new LinkedHashSet<>();
        codes.add(entityTypeCode);

        java.util.Deque<EntityTypeRespVO> queue = new java.util.ArrayDeque<>(children);
        while (!queue.isEmpty()) {
            EntityTypeRespVO node = queue.poll();
            if (node == null) {
                continue;
            }
            if (node.getCode() != null && !node.getCode().isBlank()) {
                codes.add(node.getCode());
            }
            if (node.getChildren() != null && !node.getChildren().isEmpty()) {
                queue.addAll(node.getChildren());
            }
        }

        return new ArrayList<>(codes);
    }

    private String generateCode() {
        return "MODEL-" + IdUtil.fastSimpleUUID();
    }

    private Long getTenantId() {
        return Objects.requireNonNullElse(TenantContextHolder.getRequiredTenantId(), 0L);
    }


    @Override
    public List<ModelAvailableFieldRespVO> listModelAvailableFields(Long modelId) {
        // 1. 校验模型存在
        ModelDO model = modelCoreService.get(modelId);
        if (model == null) {
            throw new ServiceException(404, "模型不存在");
        }

        List<ModelAvailableFieldRespVO> result = new ArrayList<>();

        // 2. 获取固定列字段（来自业务类型配置）
        if (model.getEntityTypeCode() != null) {
            EntityTypeDO entityType = entityTypeMapper.selectByCode(model.getEntityTypeCode());
            if (entityType != null) {
                StorageTypeEnum storageType = StorageTypeEnum.getByCode(entityType.getStorageType());
                if (storageType != null && storageType.isDedicated()) {
                    List<EntityTypeBaseFieldRespVO> baseFields = 
                            entityTypeBaseFieldService.listByEntityTypeCode(model.getEntityTypeCode());
                    
                    for (EntityTypeBaseFieldRespVO baseField : baseFields) {
                        ModelAvailableFieldRespVO option = ModelAvailableFieldRespVO.builder()
                                .fieldCode(baseField.getFieldCode())
                                .fieldName(baseField.getFieldName())
                                .fieldType(baseField.getDataType())
                                .fieldSource(ModelFieldAssignmentRespVO.FIELD_SOURCE_BASE)
                                .build();
                        result.add(option);
                    }
                }
            }
        }

        // 3. 获取扩展字段（用户添加的字段）
        List<ModelFieldAssignmentRespVO> customFields = modelFieldAssignmentService.getModelFields(modelId);
        for (ModelFieldAssignmentRespVO assignment : customFields) {
            // 跳过固定列字段（已在上面处理）
            if (ModelFieldAssignmentRespVO.FIELD_SOURCE_BASE.equals(assignment.getFieldSource())) {
                continue;
            }

            if (assignment.getField() != null) {
                ModelAvailableFieldRespVO option = ModelAvailableFieldRespVO.builder()
                        .fieldCode(assignment.getField().getCode())
                        .fieldName(assignment.getField().getName())
                        .fieldType(assignment.getField().getType())
                        .fieldSource(assignment.getFieldSource())
                        .build();
                result.add(option);
            }
        }

        return result;
    }

    // ========== 事件发布方法 ==========

    /**
     * 发布 Model 创建事件
     * 
     * 用于触发：
     * 1. 展开已有的 EntityType 关联到新 Model
     * 
     * 需求：FR-BDA-005, FR-BDA-023, FR-BDA-072
     */
    private void publishModelCreatedEvent(ModelDO model) {
        try {
            ModelCreatedEvent event = ModelCreatedEvent.of(
                    this,
                    model.getId(),
                    model.getCode(),
                    model.getName(),
                    model.getEntityTypeCode(),
                    model.getTenantId()
            );
            eventPublisher.publishEvent(event);
            log.debug("[publishModelCreatedEvent][发布 Model 创建事件: {}]", event);
        } catch (Exception e) {
            // 事件发布失败不应影响主流程
            log.warn("[publishModelCreatedEvent][发布 Model 创建事件失败: modelId={}, error={}]", 
                    model.getId(), e.getMessage());
        }
    }

    /**
     * 发布关联目标创建事件
     *
     * 用于更新关联字段库中字段的状态（从"待建"变为"可用"）
     *
     * 需求：FR-BDA-014
     */
    private void publishRelationTargetCreatedEvent(ModelDO model) {
        try {
            RelationTargetCreatedEvent event = RelationTargetCreatedEvent.modelCreated(
                    this,
                    model.getId(),
                    model.getCode(),
                    model.getName(),
                    model.getEntityTypeCode(),
                    model.getTenantId()
            );
            eventPublisher.publishEvent(event);
            log.debug("[publishRelationTargetCreatedEvent][发布关联目标创建事件: {}]", event);
        } catch (Exception e) {
            // 事件发布失败不应影响主流程
            log.warn("[publishRelationTargetCreatedEvent][发布关联目标创建事件失败: modelId={}, error={}]", 
                    model.getId(), e.getMessage());
        }
    }

    // ========== 批量查询接口（性能优化）==========

    @Override
    public List<ModelRespVO> getModelsByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        // 批量查询模型
        List<ModelDO> models = modelCoreService.listByIds(ids);
        if (models.isEmpty()) {
            return List.of();
        }

        // 转换为 VO 列表（entityTypeCode 已在 convert 方法中设置）
        List<ModelRespVO> result = ModelConvert.INSTANCE.convertList(models);

        // 批量查询所有模型的分类关联
        List<Long> modelIds = result.stream().map(ModelRespVO::getId).toList();
        List<ModelCategoryRelationDO> allRelations = modelCategoryRelationMapper.selectList(
                new LambdaQueryWrapperX<ModelCategoryRelationDO>()
                        .in(ModelCategoryRelationDO::getModelId, modelIds));

        // 构建模型ID到分类ID列表的映射（多对多）
        java.util.Map<Long, List<Long>> modelCategoryIdsMap = allRelations.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        ModelCategoryRelationDO::getModelId,
                        java.util.stream.Collectors.mapping(
                                ModelCategoryRelationDO::getCategoryId,
                                java.util.stream.Collectors.toList()
                        )
                ));

        // 设置每个模型的分类ID列表
        result.forEach(model -> {
            List<Long> categoryIds = modelCategoryIdsMap.get(model.getId());
            if (categoryIds != null && !categoryIds.isEmpty()) {
                model.setCategoryIds(categoryIds);
            }
        });

        return result;
    }

    @Override
    public PageResult<Long> queryOrderedModelIdsByCategoriesInBusiness(List<Long> categoryIds, String categoryTypeCode, String entityTypeCode,
                                                                       Integer pageNo, Integer pageSize) {
        if (entityTypeCode == null || entityTypeCode.isBlank()) {
            throw new ServiceException(400, "entityTypeCode 不能为空");
        }
        List<Long> normalizedCategoryIds = categoryIds == null ? new ArrayList<>()
                : categoryIds.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if (normalizedCategoryIds.isEmpty()) {
            if (categoryTypeCode == null || categoryTypeCode.isBlank()) {
                throw new ServiceException(400, "未选择分类时，categoryTypeCode 不能为空");
            }
            var categoryType = categoryTypeService.getCategoryTypeByCode(categoryTypeCode);
            Long rootCategoryId = categoryType == null ? null : categoryType.getTopLevelCategoryId();
            if (rootCategoryId == null) {
                throw new ServiceException(404, "分类类型不存在或未配置根分类: " + categoryTypeCode);
            }
            normalizedCategoryIds = List.of(rootCategoryId);
        }
        List<Long> modelIds = normalizedCategoryIds.size() == 1
                ? modelCategoryRelationService.listModelIdsByCategoryIdWithDescendants(normalizedCategoryIds.get(0), entityTypeCode)
                : modelCategoryRelationService.listModelIdsByCategoryIdsWithDescendants(normalizedCategoryIds, entityTypeCode);
        if (modelIds == null || modelIds.isEmpty()) {
            return new PageResult<>(List.of(), 0L);
        }
        List<Long> orderedModelIds = new ArrayList<>(new LinkedHashSet<>(modelIds));
        if (pageNo == null || pageSize == null) {
            return new PageResult<>(orderedModelIds, (long) orderedModelIds.size());
        }

        int effectivePageNo = (pageNo == null || pageNo < 1) ? 1 : pageNo;
        int effectivePageSize = (pageSize == null || pageSize < 1) ? 20 : pageSize;
        int from = (effectivePageNo - 1) * effectivePageSize;
        if (from >= orderedModelIds.size()) {
            return new PageResult<>(List.of(), (long) orderedModelIds.size());
        }
        int to = Math.min(from + effectivePageSize, orderedModelIds.size());
        return new PageResult<>(orderedModelIds.subList(from, to), (long) orderedModelIds.size());
    }
}
