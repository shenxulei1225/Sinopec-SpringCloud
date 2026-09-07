package cn.cheers.x.module.dynamicbusiness.service.model;

import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;
import cn.cheers.x.framework.security.core.util.SecurityFrameworkUtils;

import cn.hutool.core.util.IdUtil;
import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeBaseFieldRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.CategoryIdGroupReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelCloneReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelDomainChangePreviewRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelFieldAssignmentRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelFieldGroupCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelPageReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelAvailableFieldRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelSortSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.convert.model.ModelConvert;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelCategoryRelationDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelFieldAssignmentDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.category.CategoryMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelCategoryRelationMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelFieldAssignmentMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryDO;
import cn.cheers.x.module.dynamicbusiness.enums.entitytype.StorageTypeEnum;
import cn.cheers.x.module.dynamicbusiness.service.entitytype.BaseFieldLibrarySyncService;
import cn.cheers.x.module.dynamicbusiness.service.entitytype.EntityTypeBaseFieldService;
import cn.cheers.x.module.dynamicbusiness.service.entitytype.EntityTypeService;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryService;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryTypeService;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.service.dynamictable.DynamicTableService;
import cn.cheers.x.module.dynamicbusiness.service.model.core.ModelCoreService;
import cn.cheers.x.module.dynamicbusiness.service.model.governance.MasterDataCapabilityChecker;
import cn.cheers.x.module.dynamicbusiness.service.model.governance.ModelGovernanceCommandService;
import cn.cheers.x.module.dynamicbusiness.service.model.governance.ModelGovernanceQueryService;
import cn.cheers.x.module.dynamicbusiness.util.SparseSortUtils;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntityCategoryRelationMapper;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.EntityRepository;
import cn.cheers.x.module.dynamicbusiness.service.entity.relation.EntityCategoryRelationService;
import cn.cheers.x.module.dynamicbusiness.framework.entitytype.EntityTypeScopeContext;
import cn.cheers.x.module.dynamicbusiness.framework.entitytype.EntityTypeScopeResolver;
import cn.cheers.x.module.dynamicbusiness.event.ModelCreatedEvent;
import cn.cheers.x.module.dynamicbusiness.event.RelationTargetCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
    private ModelGovernanceQueryService modelGovernanceQueryService;
    @Resource
    private ModelGovernanceCommandService modelGovernanceCommandService;
    @Resource
    private MasterDataCapabilityChecker masterDataCapabilityChecker;
    @Resource
    private ModelFieldAssignmentMapper modelFieldAssignmentMapper;
    @Resource
    private ModelCategoryRelationMapper modelCategoryRelationMapper;
    @Resource
    private ModelMapper modelMapper;
    @Resource
    private CategoryService categoryService;
    @Resource
    private CategoryTypeService categoryTypeService;
    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private EntityTypeBaseFieldService entityTypeBaseFieldService;
    @Resource
    private BaseFieldLibrarySyncService baseFieldLibrarySyncService;
    @Resource
    @Lazy
    private cn.cheers.x.module.dynamicbusiness.service.hierarchy.OrgTreeParentFieldEnsureService orgTreeParentFieldEnsureService;
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

    @Resource
    private EntityTypeScopeResolver entityTypeScopeResolver;

    @Resource
    private EntityRepository entityRepository;

    @Resource
    private EntityCategoryRelationMapper entityCategoryRelationMapper;

    @Resource
    @Lazy // 避免与实体侧服务循环依赖
    private EntityCategoryRelationService entityCategoryRelationService;

    @Resource
    @Lazy
    private cn.cheers.x.module.dynamicbusiness.service.capability.BusinessCapabilityService businessCapabilityService;


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

        // 业务域登记在实际存储类型下：从子数据类型入口创建时，reqVO 里可能是注册编码（如 task_patrol）
        String modelDomain = EntityTypeScopeContext.normalizeDomain(reqVO.getDomain());
        if (StringUtils.hasText(modelDomain)
                && !entityTypeService.isRegisteredDomain(
                        resolveStorageEntityTypeCode(reqVO.getEntityTypeCode()), modelDomain)) {
            throw new ServiceException(400,
                    "业务域未登记为该数据类型下的子数据类型，请先创建对应子数据类型：" + modelDomain);
        }

        // 创建模型（租户插件会自动填充 tenantId）
        ModelDO model = ModelConvert.INSTANCE.convert(reqVO);
        model.setCode(generateCode());
        model.setDomain(modelDomain);
        // 治理命令服务是创建身份的唯一权威；普通创建不得沿用数据库 COMPANY 默认值。
        modelGovernanceCommandService.prepareForCreate(
                model,
                reqVO.getGovernanceStatus(),
                reqVO.getEffectiveFacilityId(),
                SecurityFrameworkUtils.getLoginUserId());
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
                        .entityTypeCode(reqVO.getEntityTypeCode())
                        .build();
                modelCategoryRelationMapper.insert(relation);
            }
        }
        
        // 型号表单默认「扩展信息」分组（类型基础字段不进型号分组，只在型号侧展示）
        ModelFieldGroupCreateReqVO defaultGroupReq = new ModelFieldGroupCreateReqVO();
        defaultGroupReq.setModelId(model.getId());
        defaultGroupReq.setName("扩展信息");
        defaultGroupReq.setColor("#409eff");
        modelFieldGroupService.createModelFieldGroup(defaultGroupReq);

        // 高级分类：先确保系统组织上级已挂到类型，再物化进本型号的模型字段
        if (reqVO.getEntityTypeCode() != null) {
            orgTreeParentFieldEnsureService.ensureForEntityTypeCode(reqVO.getEntityTypeCode());
        }
        // 物化该业务类型已有固定列到本模型字段表（读路径不再虚合并 BASE）
        baseFieldLibrarySyncService.assignAllBaseFieldsToModel(model.getId());

        // 发布 Model 创建事件（用于事件驱动机制）
        // 需求：FR-BDA-005, FR-BDA-023, FR-BDA-072
        publishModelCreatedEvent(model);
        
        // 发布关联目标创建事件（用于更新关联字段库状态）
        // 需求：FR-BDA-014
        publishRelationTargetCreatedEvent(model);
        
        return model.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long cloneModel(ModelCloneReqVO reqVO) {
        ModelDO source = getVisibleModel(reqVO.getSourceModelId(), reqVO.getEffectiveFacilityId(), "源模型不存在");
        String entityTypeCode = source.getEntityTypeCode();
        if (!StringUtils.hasText(entityTypeCode)) {
            throw new ServiceException(400, "源模型缺少业务类型编码");
        }

        String newName = reqVO.getName().trim();
        try {
            ModelDO existModel = modelCoreService.getByNameInEntityType(newName, entityTypeCode);
            if (existModel != null) {
                throw new ServiceException(400, "模型名称已存在：" + newName);
            }
        } catch (TooManyResultsException e) {
            throw new ServiceException(400, "模型名称已存在：" + newName);
        }

        ModelDO model = new ModelDO();
        model.setCode(generateCode());
        model.setName(newName);
        model.setEntityTypeCode(entityTypeCode);
        model.setDomain(source.getDomain());
        model.setDescription(reqVO.getDescription() != null ? reqVO.getDescription() : source.getDescription());
        model.setStatus(reqVO.getStatus() != null ? reqVO.getStatus() : source.getStatus());
        Integer maxSort = modelMapper.selectMaxSortByEntityTypeCode(entityTypeCode);
        model.setSort(SparseSortUtils.next(maxSort));
        // 分组 JSON 内 string id 不变 → 分配行 fieldGroupId（hash）无需 remap
        model.setFieldGroupsConfig(source.getFieldGroupsConfig());
        // 复制同样属于创建：必须重新按当前调用方能力和有效站场定稿治理身份。
        modelGovernanceCommandService.prepareForCreate(
                model,
                reqVO.getGovernanceStatus(),
                reqVO.getEffectiveFacilityId(),
                SecurityFrameworkUtils.getLoginUserId());
        modelCoreService.create(model);

        List<ModelFieldAssignmentDO> sourceAssignments =
                modelFieldAssignmentMapper.selectByModelId(source.getId());
        for (ModelFieldAssignmentDO src : sourceAssignments) {
            if (src == null || src.getFieldId() == null) {
                continue;
            }
            ModelFieldAssignmentDO copy = ModelFieldAssignmentDO.builder()
                    .modelId(model.getId())
                    .modelCode(model.getCode())
                    .fieldId(src.getFieldId())
                    .fieldCode(src.getFieldCode())
                    .required(src.getRequired())
                    .isSearchable(src.getIsSearchable())
                    .isFilterable(src.getIsFilterable())
                    .isSortable(src.getIsSortable())
                    .defaultValue(src.getDefaultValue())
                    .validationRules(src.getValidationRules())
                    .sort(src.getSort())
                    .fieldGroupId(src.getFieldGroupId())
                    .fieldSource(src.getFieldSource())
                    .refLibraryId(src.getRefLibraryId())
                    .modelRelationId(null)
                    .targetEntityType(src.getTargetEntityType())
                    .build();
            modelFieldAssignmentMapper.insert(copy);
        }
        // 源型号若缺固定列分配，克隆后补齐（只插缺的，不覆盖已复制行的规则）
        baseFieldLibrarySyncService.assignAllBaseFieldsToModel(model.getId());

        LinkedHashSet<Long> categoryIds = new LinkedHashSet<>();
        List<ModelCategoryRelationDO> sourceRels =
                modelCategoryRelationMapper.selectByModelId(source.getId());
        if (sourceRels != null) {
            for (ModelCategoryRelationDO rel : sourceRels) {
                if (rel != null && rel.getCategoryId() != null) {
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
        for (Long categoryIdToBind : categoryIds) {
            CategoryDO category = categoryMapper.selectById(categoryIdToBind);
            if (category == null) {
                throw new ServiceException(404, "分类不存在,ID：" + categoryIdToBind);
            }
            ModelCategoryRelationDO relation = ModelCategoryRelationDO.builder()
                    .modelId(model.getId())
                    .modelCode(model.getCode())
                    .categoryId(categoryIdToBind)
                    .categoryCode(category.getCode())
                    .entityTypeCode(entityTypeCode)
                    .build();
            modelCategoryRelationMapper.insert(relation);
        }

        try {
            businessCapabilityService.refreshModelCrudFormDefinition(model.getId());
        } catch (Exception e) {
            log.warn("[cloneModel][刷新 CRUD 表单失败，不影响克隆主流程][newModelId={}][err={}]",
                    model.getId(), e.getMessage());
        }

        publishModelCreatedEvent(model);
        publishRelationTargetCreatedEvent(model);
        log.info("[cloneModel][sourceId={}][newId={}][name={}][assignments={}][categories={}]",
                source.getId(), model.getId(), newName,
                sourceAssignments != null ? sourceAssignments.size() : 0,
                categoryIds.size());
        return model.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateModel(ModelUpdateReqVO reqVO) {
        // 更新入口与详情、复制共用同一治理可见性门禁，禁止凭主键绕过本地型号隔离。
        ModelDO existModel = getVisibleModel(reqVO.getId(), reqVO.getEffectiveFacilityId(), "模型不存在");
        modelGovernanceCommandService.validateRegularUpdate(
                existModel, reqVO.getGovernanceStatus(), reqVO.getStatus());

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
        // 普通更新只维护型号正文；治理身份只能由治理命令写入。
        model.setGovernanceStatus(null);
        if (model.getSort() == null) {
            model.setSort(existModel.getSort());
        }
        // 业务域始终保持既有值：改业务域必须走 changeDomain，才能连带迁移实体与分类关联。
        model.setDomain(existModel.getDomain());
        modelCoreService.update(model);

        if (reqVO.getDomain() != null) {
            String requestedDomain = EntityTypeScopeContext.normalizeDomain(reqVO.getDomain());
            if (!EntityTypeScopeContext.domainsEqual(requestedDomain, existModel.getDomain())) {
                changeDomain(reqVO.getId(), requestedDomain);
            }
        }

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

    // ==================== 业务域迁移 ====================

    @Override
    public ModelDomainChangePreviewRespVO previewDomainChange(Long modelId, String targetDomain) {
        return buildDomainChangePlan(modelId, targetDomain).toPreview();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ModelDomainChangePreviewRespVO changeDomain(Long modelId, String targetDomain) {
        DomainChangePlan plan = buildDomainChangePlan(modelId, targetDomain);
        if (!plan.changed()) {
            return plan.toPreview();
        }

        // 权威链自上而下：型号 → 实体 → 分类关联；同一事务，任一步失败整体回滚。
        ModelDO update = new ModelDO();
        update.setId(plan.model().getId());
        update.setDomain(plan.targetDomain());
        modelCoreService.update(update);

        if (!plan.entityIds().isEmpty()) {
            entityRepository.updateDomainByModelId(
                    plan.model().getId(), plan.storageEntityTypeCode(), plan.targetDomain());
            entityCategoryRelationService.syncRelationDomainByEntityIds(
                    plan.entityIds(), plan.storageEntityTypeCode(), plan.targetDomain());
        }

        log.info("型号业务域迁移完成: modelId={}, {} -> {}, entityCount={}, relationCount={}",
                plan.model().getId(), plan.currentDomain(), plan.targetDomain(),
                plan.entityIds().size(), plan.relationCount());
        return plan.toPreview();
    }

    /**
     * 汇总一次业务域迁移的目标与影响面：校验目标业务域已登记，统计该型号下的实体与分类关联。
     */
    private DomainChangePlan buildDomainChangePlan(Long modelId, String targetDomain) {
        if (modelId == null) {
            throw new ServiceException(400, "modelId 不能为空");
        }
        ModelDO model = modelCoreService.get(modelId);
        if (model == null) {
            throw new ServiceException(404, "模型不存在");
        }
        // 拖到「未划域」分组时前端复用同一个筛选标记；这里统一按清空业务域处理
        String normalizedTarget = EntityTypeScopeContext.isNoneDomainFilter(targetDomain)
                ? null
                : EntityTypeScopeContext.normalizeDomain(targetDomain);
        String currentDomain = EntityTypeScopeContext.normalizeDomain(model.getDomain());
        // 存储类型是业务域的宿主：注册编码（如 task_patrol）不能作为型号归属
        String storageEntityTypeCode = resolveStorageEntityTypeCode(model.getEntityTypeCode());
        if (StringUtils.hasText(normalizedTarget)
                && !entityTypeService.isRegisteredDomain(storageEntityTypeCode, normalizedTarget)) {
            throw new ServiceException(400,
                    "业务域未登记为该数据类型下的子数据类型，请先创建对应子数据类型：" + normalizedTarget);
        }

        List<Long> entityIds = entityRepository.findByModelId(modelId, storageEntityTypeCode).stream()
                .map(EntityDO::getId)
                .filter(Objects::nonNull)
                .toList();
        long relationCount = entityCategoryRelationMapper.countByEntityIds(entityIds, storageEntityTypeCode);
        boolean changed = !EntityTypeScopeContext.domainsEqual(currentDomain, normalizedTarget);
        return new DomainChangePlan(model, storageEntityTypeCode, currentDomain, normalizedTarget,
                changed, entityIds, (int) relationCount);
    }

    /** 型号业务域迁移的目标与影响面。 */
    private record DomainChangePlan(ModelDO model,
                                    String storageEntityTypeCode,
                                    String currentDomain,
                                    String targetDomain,
                                    boolean changed,
                                    List<Long> entityIds,
                                    int relationCount) {

        ModelDomainChangePreviewRespVO toPreview() {
            return ModelDomainChangePreviewRespVO.builder()
                    .modelId(model.getId())
                    .modelName(model.getName())
                    .entityTypeCode(storageEntityTypeCode)
                    .currentDomain(currentDomain)
                    .targetDomain(targetDomain)
                    .changed(changed)
                    .entityCount(entityIds.size())
                    .relationCount(relationCount)
                    .build();
        }
    }

    /** 型号归属的实际存储类型；注册编码只用于侧边栏入口。 */
    private String resolveStorageEntityTypeCode(String entityTypeCode) {
        if (!StringUtils.hasText(entityTypeCode)) {
            throw new ServiceException(400, "模型缺少 entityTypeCode");
        }
        return entityTypeScopeResolver.resolveStorageEntityTypeCode(entityTypeCode);
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
        // model_code / category_code 为库表非空列，须与创建路径一致一并写入（勿只写 id）
        if (!notExisting.isEmpty()) {
            ModelDO model = modelCoreService.get(modelId);
            if (model == null || model.getCode() == null || model.getCode().isBlank()) {
                throw new ServiceException(404, "模型不存在或缺少业务编码,ID：" + modelId);
            }
            Map<Long, String> categoryCodeById = categories.stream()
                    .collect(Collectors.toMap(CategoryDO::getId, CategoryDO::getCode, (a, b) -> a));
            List<ModelCategoryRelationDO> relationsToInsert = new ArrayList<>();
            for (Long categoryId : notExisting) {
                String categoryCode = categoryCodeById.get(categoryId);
                if (categoryCode == null || categoryCode.isBlank()) {
                    throw new ServiceException(404, "分类缺少业务编码,ID：" + categoryId);
                }
                ModelCategoryRelationDO relation = ModelCategoryRelationDO.builder()
                        .modelId(modelId)
                        .modelCode(model.getCode())
                        .categoryId(categoryId)
                        .categoryCode(categoryCode)
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
    public void deleteModel(Long id, Long effectiveFacilityId) {
        // 删除入口按治理身份分流，不发明第二套治理：
        // - 公司规格 / 无发起站场 → 禁止硬删，提示走停用
        // - 本地型号缺站场 → 明确要求站场
        // - 本地型号有站场 → 仅走 ModelGovernanceCommandService.deleteOwnLocal
        ModelDO model = modelCoreService.get(id);
        if (model == null) {
            throw new ServiceException(404, "模型不存在");
        }
        String governance = model.getGovernanceStatus() == null
                ? ""
                : model.getGovernanceStatus().trim().toUpperCase();
        boolean companyOrNoOrigin = "COMPANY".equals(governance) || model.getOriginFacilityId() == null;
        if (companyOrNoOrigin) {
            throw new ServiceException(400, "公司规格请用停用");
        }
        if (effectiveFacilityId == null) {
            throw new ServiceException(400, "删除本地型号必须指定当前有效站场");
        }
        modelGovernanceCommandService.deleteOwnLocal(
                id, effectiveFacilityId, SecurityFrameworkUtils.getLoginUserId());
    }

    @Override
    public void deactivateCompanyModel(Long id) {
        modelGovernanceCommandService.deactivateCompany(id);
    }

    @Override
    public ModelRespVO getModel(Long id, Long effectiveFacilityId) {
        ModelDO model = getVisibleModel(id, effectiveFacilityId, "模型不存在");
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
     * 用户入口读取型号的统一门禁：Core 只负责按主键取数，治理查询服务负责可见性。
     * 禁止详情、更新或复制入口直接使用 Core 结果继续执行业务。
     */
    private ModelDO getVisibleModel(Long id, Long effectiveFacilityId, String notFoundMessage) {
        ModelDO model = modelCoreService.get(id);
        if (model == null) {
            throw new ServiceException(404, notFoundMessage);
        }
        return modelGovernanceQueryService.assertVisible(
                model, effectiveFacilityId, masterDataCapabilityChecker.canManageNetworkModelData());
    }

   

    /**
     * 根据业务类型编码获取模型列表
     */
    @Override
    public List<ModelRespVO> listModelsByEntityType(String entityTypeCode) {
        return listModelsByEntityType(entityTypeCode, null, null);
    }

    @Override
    public List<ModelRespVO> listModelsByEntityType(String entityTypeCode, String domain) {
        return listModelsByEntityType(entityTypeCode, domain, null);
    }

    @Override
    public List<ModelRespVO> listModelsByEntityType(String entityTypeCode, String domain, String categoryTypeCode) {
        return listModelsByEntityType(entityTypeCode, domain, categoryTypeCode, null);
    }

    @Override
    public List<ModelRespVO> listModelsByEntityType(
            String entityTypeCode, String domain, String categoryTypeCode, Long effectiveFacilityId) {
        // 治理查询服务是列表可见性的唯一权威；本服务只提供候选集和有效站场。
        List<ModelDO> visibleModels = modelGovernanceQueryService.filterVisible(
                modelCoreService.listByEntityTypeCode(entityTypeCode),
                effectiveFacilityId,
                masterDataCapabilityChecker.canManageNetworkModelData());
        List<ModelDO> list = filterModelDosByDomain(visibleModels, domain);
        if (list.isEmpty()) {
            return List.of();
        }

        List<ModelRespVO> result = ModelConvert.INSTANCE.convertList(list);
        fillModelCategoryIds(result);
        if (StringUtils.hasText(categoryTypeCode)) {
            return reorderModelsByCategoryTree(result, categoryTypeCode.trim(), entityTypeCode);
        }
        return result;
    }

    /**
     * 按分类树展示序分桶重排型号：根→子（同级 sort/id）展开后的分类—型号序，未挂分类的追加末尾。
     * 分类体系无根或不存在时保持原序（不静默编造）。
     */
    private List<ModelRespVO> reorderModelsByCategoryTree(
            List<ModelRespVO> models, String categoryTypeCode, String entityTypeCode) {
        var categoryType = categoryTypeService.getCategoryTypeByCode(categoryTypeCode);
        Long rootCategoryId = categoryType == null ? null : categoryType.getTopLevelCategoryId();
        if (rootCategoryId == null) {
            return models;
        }
        List<Long> orderedIds = modelCategoryRelationService.listModelIdsByCategoryIdWithDescendants(
                rootCategoryId, entityTypeCode);
        if (orderedIds == null || orderedIds.isEmpty()) {
            return models;
        }
        Map<Long, ModelRespVO> byId = new java.util.LinkedHashMap<>();
        for (ModelRespVO model : models) {
            if (model.getId() != null) {
                byId.putIfAbsent(model.getId(), model);
            }
        }
        List<ModelRespVO> ordered = new ArrayList<>(models.size());
        Set<Long> seen = new HashSet<>();
        for (Long modelId : orderedIds) {
            ModelRespVO vo = byId.get(modelId);
            if (vo != null && seen.add(modelId)) {
                ordered.add(vo);
            }
        }
        for (ModelRespVO model : models) {
            Long id = model.getId();
            if (id != null && seen.add(id)) {
                ordered.add(model);
            }
        }
        return ordered;
    }

    private List<ModelDO> filterModelDosByDomain(List<ModelDO> models, String domain) {
        if (models == null || models.isEmpty() || !org.springframework.util.StringUtils.hasText(domain)) {
            return models == null ? List.of() : models;
        }
        String normalized = domain.trim();
        return models.stream()
                .filter(model -> EntityTypeScopeContext.matchesDomainFilter(model.getDomain(), normalized))
                .toList();
    }

    private List<ModelRespVO> filterModelVosByDomain(List<ModelRespVO> models, String domain) {
        if (models == null || models.isEmpty() || !org.springframework.util.StringUtils.hasText(domain)) {
            return models == null ? List.of() : models;
        }
        String normalized = domain.trim();
        return models.stream()
                .filter(model -> EntityTypeScopeContext.matchesDomainFilter(model.getDomain(), normalized))
                .toList();
    }

    @Override
    public List<ModelRespVO> listUncategorizedModelsByCategoryType(String categoryTypeCode, String entityTypeCode) {
        return listUncategorizedModelsByCategoryType(categoryTypeCode, entityTypeCode, null);
    }

    @Override
    public List<ModelRespVO> listUncategorizedModelsByCategoryType(
            String categoryTypeCode, String entityTypeCode, String domain) {
        if (entityTypeCode == null || entityTypeCode.isBlank()) {
            throw new ServiceException(400, "entityTypeCode 不能为空");
        }
        if (categoryTypeCode == null || categoryTypeCode.isBlank()) {
            throw new ServiceException(400, "categoryTypeCode 不能为空");
        }
        List<ModelRespVO> allModels = listModelsByEntityType(entityTypeCode, domain);
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
    public List<ModelRespVO> listCategorizedModelsByCategoryType(
            String categoryTypeCode, String entityTypeCode, String domain) {
        if (entityTypeCode == null || entityTypeCode.isBlank()) {
            throw new ServiceException(400, "entityTypeCode 不能为空");
        }
        if (categoryTypeCode == null || categoryTypeCode.isBlank()) {
            throw new ServiceException(400, "categoryTypeCode 不能为空");
        }
        // 带 categoryTypeCode：先按分类树序分桶排序（与「全部」同序），再筛「已挂节点」
        List<ModelRespVO> allModels = listModelsByEntityType(entityTypeCode, domain, categoryTypeCode);
        if (allModels.isEmpty()) {
            return allModels;
        }
        List<Long> categorizedModelIds = modelCategoryRelationMapper.selectDistinctModelIdsByCategoryTypeCode(
                categoryTypeCode, entityTypeCode);
        Set<Long> categorized = categorizedModelIds == null ? Set.of()
                : categorizedModelIds.stream().filter(Objects::nonNull).collect(Collectors.toSet());
        if (categorized.isEmpty()) {
            return List.of();
        }
        return allModels.stream()
                .filter(model -> model.getId() != null && categorized.contains(model.getId()))
                .toList();
    }

    @Override
    public List<ModelRespVO> filterModelsByDomain(List<ModelRespVO> models, String domain) {
        return filterModelVosByDomain(models, domain);
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
        Integer status = reqVO.getStatus() != null ? reqVO.getStatus() : 1;
        List<ModelDO> orderedCandidates;
        if (Boolean.TRUE.equals(reqVO.getIncludeChildren()) && reqVO.getEntityTypeCode() != null && !reqVO.getEntityTypeCode().isBlank()) {
            List<String> entityTypeCodes = collectEntityTypeCodesWithChildren(reqVO.getEntityTypeCode());
            List<ModelDO> allModels = modelMapper.selectList(new LambdaQueryWrapperX<ModelDO>()
                    .in(ModelDO::getEntityTypeCode, entityTypeCodes)
                    .eq(org.apache.commons.lang3.StringUtils.isNotBlank(reqVO.getDomain())
                                    && !EntityTypeScopeContext.isNoneDomainFilter(reqVO.getDomain()),
                            ModelDO::getDomain, EntityTypeScopeContext.normalizeDomain(reqVO.getDomain()))
                    .and(EntityTypeScopeContext.isNoneDomainFilter(reqVO.getDomain()),
                            q -> q.isNull(ModelDO::getDomain).or().eq(ModelDO::getDomain, ""))
                    .eq(ModelDO::getStatus, status)
                    .and(org.apache.commons.lang3.StringUtils.isNotBlank(reqVO.getKeyword()),
                            q -> q.like(ModelDO::getName, reqVO.getKeyword())
                                    .or().like(ModelDO::getDescription, reqVO.getKeyword())));

            java.util.Map<String, Integer> entityTypeOrder = new java.util.HashMap<>();
            for (int i = 0; i < entityTypeCodes.size(); i++) {
                entityTypeOrder.put(entityTypeCodes.get(i), i);
            }

            orderedCandidates = allModels.stream()
                    .sorted(Comparator
                            .comparing((ModelDO m) -> entityTypeOrder.getOrDefault(m.getEntityTypeCode(), Integer.MAX_VALUE))
                            .thenComparing((ModelDO m) -> m.getSort() == null ? Integer.MAX_VALUE : m.getSort())
                            .thenComparing(ModelDO::getCreateTime, Comparator.nullsLast(Comparator.reverseOrder()))
                            .thenComparing((ModelDO m) -> m.getId() == null ? Long.MAX_VALUE : m.getId()))
                    .toList();
        } else {
            String normalizedDomain = EntityTypeScopeContext.normalizeDomain(reqVO.getDomain());
            boolean unassignedDomain = EntityTypeScopeContext.isNoneDomainFilter(reqVO.getDomain());
            boolean namedDomain = StringUtils.hasText(normalizedDomain) && !unassignedDomain;
            orderedCandidates = modelMapper.selectList(new LambdaQueryWrapperX<ModelDO>()
                    .eq(StringUtils.hasText(reqVO.getEntityTypeCode()),
                            ModelDO::getEntityTypeCode, reqVO.getEntityTypeCode())
                    .eq(namedDomain, ModelDO::getDomain, normalizedDomain)
                    .and(unassignedDomain,
                            q -> q.isNull(ModelDO::getDomain).or().eq(ModelDO::getDomain, ""))
                    .eq(ModelDO::getStatus, status)
                    .and(org.apache.commons.lang3.StringUtils.isNotBlank(reqVO.getKeyword()),
                            q -> q.like(ModelDO::getName, reqVO.getKeyword())
                                    .or().like(ModelDO::getDescription, reqVO.getKeyword()))
                    .orderByAsc(ModelDO::getSort)
                    .orderByDesc(ModelDO::getCreateTime));
        }

        // 可见性必须先于分页执行，否则会出现空页和错误总数。
        PageResult<ModelDO> pageResult = modelGovernanceQueryService.filterVisiblePage(
                orderedCandidates,
                reqVO.getEffectiveFacilityId(),
                masterDataCapabilityChecker.canManageNetworkModelData(),
                reqVO.getPageNo(),
                reqVO.getPageSize());
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
     * 保存型号列表拖拽顺序。
     *
     * <p>权威分流（与实体栏对齐）：
     * <ul>
     *   <li>请求带 {@code categoryId} → 只写分类—型号关联 sort（该分类下展示序）</li>
     *   <li>不带分类 → 写型号主表 sort（类型内全局序）</li>
     * </ul>
     * 禁止：有选中分类时仍只改主表 sort，导致数据页按关联序回读时「已保存却不变」。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveModelSort(ModelSortSaveReqVO reqVO) {
        if (reqVO == null || reqVO.getItems() == null || reqVO.getItems().isEmpty()) {
            throw new ServiceException(400, "模型排序列表不能为空");
        }
        String entityTypeCode = reqVO.getEntityTypeCode() == null ? "" : reqVO.getEntityTypeCode().trim();
        if (!StringUtils.hasText(entityTypeCode)) {
            throw new ServiceException(400, "业务类型编码不能为空");
        }

        List<ModelSortSaveReqVO.Item> orderedItems = reqVO.getItems().stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(ModelSortSaveReqVO.Item::getIndex))
                .toList();

        List<Long> orderedModelIds = orderedItems.stream()
                .map(ModelSortSaveReqVO.Item::getModelId)
                .filter(Objects::nonNull)
                .toList();
        if (orderedModelIds.isEmpty()) {
            throw new ServiceException(400, "模型排序列表不能为空");
        }

        for (Long modelId : orderedModelIds) {
            ModelDO model = modelCoreService.get(modelId);
            if (model == null) {
                throw new ServiceException(404, "模型不存在: " + modelId);
            }
            if (!entityTypeCode.equals(model.getEntityTypeCode())) {
                throw new ServiceException(400, "模型不属于指定业务类型: " + modelId);
            }
        }

        if (reqVO.getCategoryId() != null) {
            modelCategoryRelationService.reindexModelSortInCategory(
                    reqVO.getCategoryId(), entityTypeCode, orderedModelIds);
            return;
        }

        int idx = 0;
        for (Long modelId : orderedModelIds) {
            ModelDO updateDO = new ModelDO();
            updateDO.setId(modelId);
            updateDO.setSort(SparseSortUtils.reindexSortByPosition(idx++));
            modelCoreService.update(updateDO);
        }
    }

    /**
     * 收集业务类型编码及其全部子业务编码（广度优先，保序去重）
     */
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
            // 跳过固定列 / 系统字段（已在上面处理或不应重复出现在候选）
            if (ModelFieldAssignmentRespVO.FIELD_SOURCE_BASE.equals(assignment.getFieldSource())
                    || ModelFieldAssignmentRespVO.FIELD_SOURCE_SYSTEM.equals(assignment.getFieldSource())) {
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
        List<ModelRespVO> loaded = ModelConvert.INSTANCE.convertList(models);

        // 批量查询所有模型的分类关联
        List<Long> modelIds = loaded.stream().map(ModelRespVO::getId).toList();
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
        loaded.forEach(model -> {
            List<Long> categoryIds = modelCategoryIdsMap.get(model.getId());
            if (categoryIds != null && !categoryIds.isEmpty()) {
                model.setCategoryIds(categoryIds);
            }
        });

        // 按入参 ids 顺序回放（find-by-category 分桶序依赖此顺序，不能按 DB 返回序）
        Map<Long, ModelRespVO> byId = new java.util.LinkedHashMap<>();
        for (ModelRespVO model : loaded) {
            if (model.getId() != null) {
                byId.putIfAbsent(model.getId(), model);
            }
        }
        List<ModelRespVO> result = new ArrayList<>(ids.size());
        Set<Long> seen = new HashSet<>();
        for (Long id : ids) {
            if (id == null || !seen.add(id)) {
                continue;
            }
            ModelRespVO vo = byId.get(id);
            if (vo != null) {
                result.add(vo);
            }
        }
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

    @Override
    public List<ModelRespVO> listModelsByIntersectingCategoryGroups(
            List<CategoryIdGroupReqVO> categoryIdGroups,
            String entityTypeCode,
            String domain,
            Boolean includeDescendants) {
        if (!StringUtils.hasText(entityTypeCode)) {
            throw new ServiceException(400, "entityTypeCode 不能为空");
        }
        if (categoryIdGroups == null || categoryIdGroups.isEmpty()) {
            throw new ServiceException(400, "categoryIdGroups 不能为空");
        }

        boolean withDescendants = includeDescendants == null || includeDescendants;
        LinkedHashSet<Long> intersection = null;
        for (CategoryIdGroupReqVO group : categoryIdGroups) {
            if (group == null) {
                throw new ServiceException(400, "categoryIdGroups 不能包含空组");
            }
            List<Long> categoryIds = group.getCategoryIds() == null
                    ? List.of()
                    : group.getCategoryIds().stream()
                            .filter(Objects::nonNull)
                            .filter(id -> id > 0)
                            .distinct()
                            .toList();

            List<Long> groupModelIds;
            if (categoryIds.isEmpty()) {
                if (!StringUtils.hasText(group.getCategoryTypeCode())) {
                    throw new ServiceException(400, "空分类组必须提供 categoryTypeCode");
                }
                groupModelIds = queryOrderedModelIdsByCategoriesInBusiness(
                        List.of(), group.getCategoryTypeCode().trim(), entityTypeCode.trim(), null, null).getList();
            } else if (withDescendants) {
                groupModelIds = categoryIds.size() == 1
                        ? modelCategoryRelationService.listModelIdsByCategoryIdWithDescendants(
                                categoryIds.get(0), entityTypeCode.trim())
                        : modelCategoryRelationService.listModelIdsByCategoryIdsWithDescendants(
                                categoryIds, entityTypeCode.trim());
            } else {
                groupModelIds = categoryIds.size() == 1
                        ? modelCategoryRelationService.listModelIdsByCategoryIdOnly(
                                categoryIds.get(0), entityTypeCode.trim())
                        : modelCategoryRelationService.listModelIdsByCategoryIdsOnly(
                                categoryIds, entityTypeCode.trim());
            }

            if (intersection == null) {
                intersection = new LinkedHashSet<>(groupModelIds);
            } else {
                intersection.retainAll(new HashSet<>(groupModelIds));
            }
            if (intersection.isEmpty()) {
                return List.of();
            }
        }

        if (intersection == null || intersection.isEmpty()) {
            return List.of();
        }
        List<ModelRespVO> models = getModelsByIds(new ArrayList<>(intersection));
        return filterModelsByDomain(models, domain);
    }
}
