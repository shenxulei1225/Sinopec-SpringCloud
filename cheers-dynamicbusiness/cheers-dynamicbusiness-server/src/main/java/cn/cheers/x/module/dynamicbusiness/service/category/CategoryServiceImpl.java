package cn.cheers.x.module.dynamicbusiness.service.category;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryBatchDeleteRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryDeleteReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryTreeRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryTreeWithModelsRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryDragReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityWriteReqMaps;
import cn.cheers.x.module.dynamicbusiness.convert.entity.EntityFieldMapsSupport;
import cn.cheers.x.module.dynamicbusiness.convert.category.CategoryConvert;
import cn.cheers.x.module.dynamicbusiness.convert.model.ModelConvert;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryEntityLinkDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelCategoryRelationDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.category.CategoryMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.category.CategoryTypeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelCategoryRelationMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntityRelationMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytypescope.EntityTypeScopeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.convert.entity.EntityDoVoHelper;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityBusinessHelper;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityCacheEvictionService;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityLifecycleEventPublisher;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityRelationSyncService;
import cn.cheers.x.module.dynamicbusiness.service.field.CustomFieldValidationService;
import cn.cheers.x.module.dynamicbusiness.service.entity.core.EntityCoreService;
import cn.cheers.x.module.dynamicbusiness.service.entity.relation.EntityCategoryRelationService;
import cn.cheers.x.module.dynamicbusiness.service.model.relation.ModelCategoryRelationService;
import cn.cheers.x.module.dynamicbusiness.util.SensitiveDataEncryptor;
import cn.cheers.x.module.dynamicbusiness.framework.category.service.AbstractCategoryService;
import cn.cheers.x.module.dynamicbusiness.framework.category.utils.CategoryUtils;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;
 
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Comparator;

@Service
@Validated
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private static final int DEFAULT_MAX_LEVEL = 5;
    /**
     * 创建分类时，因并发导致排序冲突的最大重试次数。
     */
    private static final int MAX_SORT_RETRY = 3;

    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private CategoryTypeMapper categoryTypeMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private ModelCategoryRelationMapper modelCategoryRelationMapper;
    @Resource
    private ModelMapper modelMapper;

    @Resource
    private EntityRelationMapper entityRelationMapper;
    @Resource
    private EntityCoreService entityCoreService;

    @Resource
    private EntityBusinessHelper entityBusinessHelper;

    @Resource
    private EntityCacheEvictionService entityCacheEvictionService;

    @Resource
    private EntityLifecycleEventPublisher entityLifecycleEventPublisher;

    @Resource
    private CustomFieldValidationService customFieldValidationService;

    @Resource
    @Lazy
    private EntityRelationSyncService entityRelationSyncService;
    
    @Resource
    private EntityCategoryRelationService entityCategoryRelationService;
    
    @Resource
    private ModelCategoryRelationService modelCategoryRelationService;

    @Resource
    private CategoryEntityLinkService categoryEntityLinkService;

    @Resource
    private EntityTypeScopeMapper entityTypeScopeMapper;

    @Resource
    private cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper entityTypeMapper;

    private final CategoryCoreService core = new CategoryCoreService();

    // ==================== 存在性检查方法 ====================

    /**
     * 检查分类是否存在
     * 
     * @param categoryId 分类ID
     * @return 是否存在
     */
    @Override
    public boolean existsById(Long categoryId) {
        if (categoryId == null) {
            return false;
        }
        return categoryMapper.selectById(categoryId) != null;
    }

    /**
     * 批量检查分类是否存在
     * 
     * <p>使用 IN 查询优化性能，避免 N+1 问题。
     * 返回存在的分类ID集合，不存在的ID不会出现在结果中。</p>
     * 
     * @param categoryIds 分类ID列表
     * @return 存在的分类ID集合
     */
    @Override
    public Set<Long> filterExistingCategoryIds(List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return new HashSet<>();
        }
        List<CategoryDO> existingCategories = categoryMapper.selectByIds(categoryIds);
        return existingCategories.stream()
                .map(CategoryDO::getId)
                .collect(Collectors.toSet());
    }

    // ==================== 基础 CRUD 操作 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCategory(CategoryCreateReqVO reqVO) {
        // 高级分类（ADVANCED）= 产品名；实现仍走既有分类即实体（isEntity + entityModelId），不另开管线
        alignCreateParamsWithCategoryMode(reqVO);

        // 验证模式C参数有效性（使用前端传递的 isEntity 和 entityModelId）
        // 判断是否为实体分类：如果 entityModelId 不为空，则认为是实体分类
        validatePatternCParams(reqVO.getIsEntity(), reqVO.getEntityModelId());

        // 如果未传 parentId（或传 0），则挂到 CategoryType 的顶层节点
        if (reqVO.getParentId() == null || Objects.equals(reqVO.getParentId(), 0L)) {
            final String categoryTypeCode = reqVO.getCategoryTypeCode();
            CategoryTypeDO type = categoryTypeMapper.selectByCategoryTypeCode(categoryTypeCode);
            if (type == null || type.getTopLevelCategoryId() == null) {
                throw new ServiceException(400, "分类类型未配置顶层节点");
            }
            reqVO.setParentId(type.getTopLevelCategoryId());
        }
        
        // 验证：高级分类下，若父节点已是业务实体节点，子节点也必须是实体
        String mode = resolveCategoryMode(reqVO.getCategoryTypeCode());
        if (CategoryModeSupport.isAdvanced(mode)) {
            validateEntityCategoryInheritance(reqVO.getParentId(), reqVO.getIsEntity(), reqVO.getEntityModelId());
        }
        
        // 注意：如果指定了 entityModelId，Entity 的创建验证会在 EntityCoreService 的实现中进行
        // 由于整个方法在 @Transactional 中，如果 Entity 创建失败，整个事务会回滚，Category 也不会被创建
        // 因此不需要在这里重复验证，由底层服务统一处理验证逻辑
        
        // 高级分类：按 mode 创建 Entity + link（不是「发现有 modelId 才当高级」）
        Long entityId = null;
        if (CategoryModeSupport.isAdvanced(mode)) {
            entityId = createEntityForCategory(reqVO);
        }

        // 为避免前端传入的 sort 与同父节点下已有数据冲突，这里统一由后端生成排序；
        // 同时在并发场景下，如果出现“同一父分类下排序已存在”的业务异常，则自动重试若干次。
        Long categoryId = null;
        ServiceException lastSortException = null;
        for (int i = 0; i < MAX_SORT_RETRY; i++) {
            CategoryDO category = CategoryConvert.INSTANCE.convert(reqVO);
            // 如果请求中提供了code（GUID），使用请求中的code；否则自动生成
            if (reqVO.getCode() == null || reqVO.getCode().isEmpty()) {
                category.setCode(generateCode());
            } else {
                category.setCode(reqVO.getCode());
            }
            // 加密敏感字段：description
            if (category.getDescription() != null) {
                category.setDescription(SensitiveDataEncryptor.encrypt(category.getDescription()));
            }
            // 统一由后端生成下一个可用的排序值（忽略前端传入的 sort）
            assignNextSort(category);
            try {
                categoryId = core.createCategory(category);
                lastSortException = null;
                break;
            } catch (ServiceException e) {
                // 捕获“同一父分类下排序已存在”的并发冲突，重新计算排序后重试
                if (isSortConflictException(e) && i < MAX_SORT_RETRY - 1) {
                    lastSortException = e;
                    continue;
                }
                throw e;
            }
        }
        if (categoryId == null && lastSortException != null) {
            throw lastSortException;
        }
        
        // 高级分类：Category 落库后写 1:1 link（关联数据，不是 mode 判定依据）
        if (entityId != null) {
            linkCategoryEntityWithStorage(categoryId, entityId, reqVO.getEntityModelId());
        }
        
        return categoryId;
    }

    /**
     * 统一计算并设置分类在同一父节点下的下一个排序值。
     * 采用“最大 sort + 1”的策略，保证不会与现有记录冲突。
     */
    private void assignNextSort(CategoryDO category) {
        Long parentId = category.getParentId();
        String categoryTypeCode = category.getCategoryTypeCode();
        List<CategoryDO> siblings = categoryMapper.selectByParentIdAndCategoryTypeCode(parentId, categoryTypeCode);
        Integer maxSort = siblings.stream()
                .map(CategoryDO::getSort)
                .filter(Objects::nonNull)
                .max(Integer::compareTo)
                .orElse(0);
        int nextSort = (maxSort == null ? 1 : maxSort + 1);
        category.setSort(nextSort);
    }

    /**
     * 判断是否为“同一父分类下排序已存在”的业务异常。
     */
    private boolean isSortConflictException(ServiceException e) {
        if (e == null || e.getMessage() == null) {
            return false;
        }
        return e.getMessage().contains("同一父分类下排序已存在");
    }
    
    /**
     * 按种类 categoryMode 解析建立方式；种类不存在则失败（不静默默认）。
     */
    private CategoryTypeDO requireCategoryType(String categoryTypeCode) {
        if (StrUtil.isBlank(categoryTypeCode)) {
            throw new ServiceException(400, "分类类型编码不能为空");
        }
        CategoryTypeDO type = categoryTypeMapper.selectByCategoryTypeCode(categoryTypeCode);
        if (type == null) {
            throw new ServiceException(404, "分类种类不存在：" + categoryTypeCode);
        }
        return type;
    }

    private String resolveCategoryMode(String categoryTypeCode) {
        return CategoryModeSupport.resolveFromType(requireCategoryType(categoryTypeCode));
    }

    private boolean isStructuralRoot(CategoryDO category, CategoryTypeDO type) {
        if (category == null) {
            return false;
        }
        if (category.getParentId() == null) {
            return true;
        }
        return type != null
                && type.getTopLevelCategoryId() != null
                && Objects.equals(type.getTopLevelCategoryId(), category.getId());
    }

    /**
     * 将种类上的 categoryMode 与节点创建参数对齐。
     * ADVANCED ≡ 分类即实体：非根业务节点必须 isEntity + entityModelId。
     * SIMPLE：禁止实体参数。分支依据是种类 mode，不是 link。
     */
    private void alignCreateParamsWithCategoryMode(CategoryCreateReqVO reqVO) {
        CategoryTypeDO type = requireCategoryType(reqVO.getCategoryTypeCode());
        String mode = CategoryModeSupport.resolveFromType(type);

        boolean wantsEntity =
                Boolean.TRUE.equals(reqVO.getIsEntity()) || reqVO.getEntityModelId() != null;

        if (CategoryModeSupport.isAdvanced(mode)) {
            if (reqVO.getEntityModelId() == null) {
                throw new ServiceException(400, "高级分类新建节点须选择模型（分类即实体）");
            }
            reqVO.setIsEntity(true);
            return;
        }

        if (wantsEntity) {
            throw new ServiceException(400, "简单分类不能绑定实体；请使用高级分类或去掉模型参数");
        }
        reqVO.setIsEntity(null);
        reqVO.setEntityModelId(null);
    }

    /**
     * 更新侧按种类 mode 对齐参数；禁止靠请求里的 isEntity 在简单/高级之间切换节点语义。
     */
    private void alignUpdateParamsWithCategoryMode(CategoryDO existing, CategoryUpdateReqVO reqVO) {
        CategoryTypeDO type = requireCategoryType(reqVO.getCategoryTypeCode());
        String mode = CategoryModeSupport.resolveFromType(type);
        boolean root = isStructuralRoot(existing, type);

        if (!CategoryModeSupport.isAdvanced(mode) || root) {
            // 简单分类，或高级分类的结构根：只维护分类树字段
            if (Boolean.TRUE.equals(reqVO.getIsEntity()) || reqVO.getEntityModelId() != null) {
                throw new ServiceException(400,
                        root && CategoryModeSupport.isAdvanced(mode)
                                ? "高级分类顶层节点不可绑定实体"
                                : "简单分类不能绑定实体");
            }
            reqVO.setIsEntity(false);
            reqVO.setEntityModelId(null);
            return;
        }

        // 高级分类业务节点：始终走分类即实体
        CategoryEntityLinkDO link = categoryEntityLinkService.getLinkByCategoryId(existing.getId());
        if (link == null || link.getEntityId() == null) {
            if (reqVO.getEntityModelId() == null) {
                throw new ServiceException(400, "高级分类节点缺少实体关联，且未提供模型，无法更新");
            }
            reqVO.setIsEntity(true);
            return;
        }
        reqVO.setIsEntity(true);
        if (reqVO.getEntityModelId() == null) {
            reqVO.setEntityModelId(link.getEntityModelId());
        }
    }

    /**
     * 验证模式C参数有效性
     * <p>
     * 使用前端传递的 isEntity 和 entityModelId 进行验证。
     * 判断是否为实体分类：如果 entityModelId 不为空，则认为是实体分类。
     * 
     * <p>有效状态：</p>
     * <ul>
     *   <li>isEntity=false/null, entityModelId=null: 纯分类节点</li>
     *   <li>isEntity=false/null, entityModelId!=null: ❌ 无效状态（前端传递不一致）</li>
     *   <li>isEntity=true, entityModelId=null: 实体节点，仅有名称，无扩展字段（暂不支持）</li>
     *   <li>isEntity=true, entityModelId!=null: 实体节点，有扩展字段 ✅</li>
     * </ul>
     * 
     * @param isEntity 前端传递的是否为实体分类标识
     * @param entityModelId 实体模型ID（如果指定，则认为是实体分类）
     */
    private void validatePatternCParams(Boolean isEntity, Long entityModelId) {
        // 如果 entityModelId 不为空，则必须是实体分类（isEntity 应该为 true）
        if (entityModelId != null && !Boolean.TRUE.equals(isEntity)) {
            throw new ServiceException(400, "指定了 entityModelId 时，isEntity 必须为 true");
        }
        
        // 如果指定了 entityModelId，验证 Model 是否存在
        if (entityModelId != null) {
            ModelDO model = modelMapper.selectById(entityModelId);
            if (model == null) {
                throw new ServiceException(404, "指定的 Model 不存在");
            }
        }
    }
    
    /**
     * 验证实体分类的继承规则
     * <p>
     * 规则：如果父分类是实体分类，子分类也必须是实体分类（isEntity=true 且 entityModelId 不为空）
     * 
     * @param parentId 父分类ID
     * @param isEntity 当前分类是否为实体分类
     * @param entityModelId 当前分类的实体模型ID
     */
    private void validateEntityCategoryInheritance(Long parentId, Boolean isEntity, Long entityModelId) {
        if (parentId == null) {
            // 根节点，无需验证
            return;
        }
        
        // 检查父分类是否是实体分类
        boolean parentIsEntity = categoryEntityLinkService.isEntityCategory(parentId);
        
        if (parentIsEntity) {
            // 父分类是实体分类，子分类也必须是实体分类
            if (!Boolean.TRUE.equals(isEntity)) {
                throw new ServiceException(400, "父分类是实体分类，子分类也必须是实体分类（isEntity=true）");
            }
            if (entityModelId == null) {
                throw new ServiceException(400, "父分类是实体分类，子分类必须指定 entityModelId");
            }
        }
    }
    
    /**
     * 为分类节点创建关联的 Entity 记录（模式C）
     * <p>
     * 在创建 Category 之前先创建 Entity，提前验证 Entity 创建参数，避免创建 Category 后 Entity 创建失败。
     * Entity 创建所需的信息（name, status, customFields）都在 reqVO 中，不需要等 Category 创建完成。
     * 
     * @param reqVO 分类创建请求（包含 Entity 创建所需的所有信息）
     * @return Entity ID
     */
    private Long createEntityForCategory(CategoryCreateReqVO reqVO) {
        ModelDO model = modelMapper.selectById(reqVO.getEntityModelId());
        
        Map<String, Object> baseOverlay = new LinkedHashMap<>();
        if (reqVO.getEntityBaseFields() != null && !reqVO.getEntityBaseFields().isEmpty()) {
            baseOverlay.putAll(reqVO.getEntityBaseFields());
        }
        if (StrUtil.isNotBlank(reqVO.getCode())) {
            baseOverlay.put("code", reqVO.getCode().trim());
        }
        EntityCreateReqVO entityReqVO = EntityWriteReqMaps.createReq(
                model.getEntityTypeCode(),
                reqVO.getEntityModelId(),
                reqVO.getName(),
                reqVO.getStatus(),
                null,
                baseOverlay.isEmpty() ? null : baseOverlay,
                reqVO.getCustomFields());
        
        // 1. 使用 Helper 准备实体数据（验证、转换、加密）
        EntityDO data = entityBusinessHelper.prepareCreateEntity(entityReqVO);

        // 2. 使用 Repository 保存 to get ID
        entityCoreService.create(data);
        Long entityId = data.getId();

        // 3. 设置 treePath
        entityCoreService.moveEntity(entityId, data.getEntityTypeCode(), data.getParentId());

        // 4. 同步关联关系
        // 使用已查询的 model 变量，无需再次查询
        Map<String, Object> customFieldsMap = entityBusinessHelper.emptyIfNull(entityReqVO.getCustomFields());
        entityRelationSyncService.syncRelationsOnCreate(data, model, customFieldsMap);

        // 5. 清除缓存
        entityCacheEvictionService.evictEntityCaches(
                EntityFieldMapsSupport.getRequiredModelId(entityReqVO.getBaseFields()),
                EntityFieldMapsSupport.getRequiredEntityTypeCode(entityReqVO.getBaseFields()));

        // 6. 发布事件
        entityLifecycleEventPublisher.publishEntityCreatedEvent(
                EntityFieldMapsSupport.getRequiredModelId(entityReqVO.getBaseFields()),
                entityId,
                EntityFieldMapsSupport.getRequiredEntityTypeCode(entityReqVO.getBaseFields()),
                data);

        return entityId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCategory(CategoryUpdateReqVO reqVO) {
        // 1) 获取现有分类（使用 categoryTypeCode 精确查询）
        CategoryDO existingCategory = categoryMapper.selectByIdAndCategoryTypeCode(reqVO.getId(), reqVO.getCategoryTypeCode());
        if (existingCategory == null) {
            throw new ServiceException(404, "分类不存在");
        }

        CategoryTypeDO type = requireCategoryType(reqVO.getCategoryTypeCode());
        String mode = CategoryModeSupport.resolveFromType(type);
        // 2) 按种类 mode 对齐：简单/结构根禁止实体；高级业务节点强制分类即实体
        alignUpdateParamsWithCategoryMode(existingCategory, reqVO);

        // 3) 校验参数组合 + 模型存在性
        validatePatternCParams(reqVO.getIsEntity(), reqVO.getEntityModelId());

        // 4) 高级分类业务节点：父为高级业务节点时，子也必须是实体（结构根下的子仍须带实体）
        Long parentIdToValidate = reqVO.getParentId() != null ? reqVO.getParentId() : existingCategory.getParentId();
        if (CategoryModeSupport.isAdvanced(mode) && !isStructuralRoot(existingCategory, type)) {
            validateEntityCategoryInheritance(parentIdToValidate, reqVO.getIsEntity(), reqVO.getEntityModelId());
        }

        // 5) 实体联动：仅 ADVANCED 业务节点；SIMPLE 若误有 link 则显式失败
        if (CategoryModeSupport.isAdvanced(mode) && !isStructuralRoot(existingCategory, type)) {
            handleAdvancedCategoryUpdate(existingCategory.getId(), reqVO);
        } else {
            assertNoEntityLinkForSimpleOrRoot(existingCategory.getId(), mode, isStructuralRoot(existingCategory, type));
        }

        // 6) 更新分类树字段
        CategoryDO category = buildCategoryUpdateDO(reqVO);
        core.updateCategory(category.getId(), category);
    }
    
    private CategoryDO buildCategoryUpdateDO(CategoryUpdateReqVO reqVO) {
        // 分类字段更新（纯分类字段）统一走该对象，由 core.updateCategory 落库
        CategoryDO category = CategoryConvert.INSTANCE.convert(reqVO);
        // 加密敏感字段：description
        if (category.getDescription() != null) {
            category.setDescription(SensitiveDataEncryptor.encrypt(category.getDescription()));
        }
        return category;
    }

    /**
     * 高级分类业务节点更新：按种类 mode 走分类即实体，禁止降级为纯分类。
     * link 仅作关联数据读写，不用于判断「是不是高级分类」。
     */
    private void handleAdvancedCategoryUpdate(Long existingCategoryId, CategoryUpdateReqVO reqVO) {
        CategoryEntityLinkDO link = categoryEntityLinkService.getLinkByCategoryId(existingCategoryId);
        if (link == null || link.getEntityId() == null) {
            if (reqVO.getEntityModelId() == null) {
                throw new ServiceException(400, "高级分类节点缺少实体关联");
            }
            CategoryDO existingCategory = categoryMapper.selectById(existingCategoryId);
            CategoryCreateReqVO createReqVO = new CategoryCreateReqVO();
            createReqVO.setName(reqVO.getName() != null ? reqVO.getName() : existingCategory.getName());
            createReqVO.setStatus(reqVO.getStatus() != null ? reqVO.getStatus() : existingCategory.getStatus());
            createReqVO.setEntityModelId(reqVO.getEntityModelId());
            createReqVO.setCustomFields(reqVO.getCustomFields());
            Long entityId = createEntityForCategory(createReqVO);
            linkCategoryEntityWithStorage(existingCategoryId, entityId, reqVO.getEntityModelId());
            return;
        }
        syncBoundEntityIfNeeded(existingCategoryId, reqVO);
    }

    /**
     * 简单分类 / 结构根：不得存在 1:1 实体 link；有则视为数据异常并拒绝（不静默清理）。
     */
    private void assertNoEntityLinkForSimpleOrRoot(Long categoryId, String mode, boolean root) {
        if (categoryEntityLinkService.isEntityCategory(categoryId)) {
            throw new ServiceException(400,
                    root
                            ? "高级分类顶层节点不应绑定实体，数据异常"
                            : "简单分类节点不应绑定实体，数据异常（categoryMode=" + mode + "）");
        }
    }

    private void syncBoundEntityIfNeeded(Long categoryId, CategoryUpdateReqVO reqVO) {
        // 高级分类业务节点：名称 / 状态 / 自定义字段同步到关联 Entity
        if (reqVO.getCustomFields() == null && reqVO.getName() == null && reqVO.getStatus() == null) {
            return;
        }
        CategoryEntityLinkDO link = categoryEntityLinkService.getLinkByCategoryId(categoryId);
        if (link == null || link.getEntityId() == null) {
            throw new ServiceException(400, "高级分类节点缺少实体关联，无法同步实体");
        }
        ModelDO model = modelMapper.selectById(link.getEntityModelId());
        if (model == null) {
            throw new ServiceException(404, "高级分类关联的 Model 不存在");
        }
        EntityDO entityDO = entityCoreService.get(link.getEntityId(), model.getEntityTypeCode());
        EntityRespVO existingEntity = entityDO != null ? EntityDoVoHelper.toRespVO(entityDO, customFieldValidationService) : null;
        if (existingEntity == null) {
            throw new ServiceException(404, "高级分类关联的实体不存在");
        }

        EntityUpdateReqVO entityUpdateReqVO = EntityWriteReqMaps.updateReq(
                link.getEntityId(),
                existingEntity.getEntityTypeCode(),
                existingEntity.getModelId(),
                reqVO.getName() != null ? reqVO.getName() : existingEntity.getName(),
                reqVO.getStatus() != null ? reqVO.getStatus() : existingEntity.getStatus(),
                existingEntity.getParentId(),
                null,
                reqVO.getCustomFields() != null ? reqVO.getCustomFields() : existingEntity.getCustomFields());

        // Replicate the update logic from EntityServiceImpl
        EntityDO db = entityCoreService.get(entityUpdateReqVO.getId(), existingEntity.getEntityTypeCode());
        if (db == null) {
            throw new ServiceException(404, "实体不存在");
        }
        String oldName = db.getName();
        Map<String, Object> oldCustomFields = entityBusinessHelper.emptyIfNull(db.getCustomFields());

        EntityDO update = entityBusinessHelper.prepareUpdateEntity(entityUpdateReqVO, db);
        entityCoreService.update(update);

        Map<String, Object> newCustomFields = entityBusinessHelper.emptyIfNull(entityUpdateReqVO.getCustomFields());
        entityRelationSyncService.syncRelationsOnUpdate(update, model, newCustomFields, oldCustomFields);

        entityCacheEvictionService.evictEntityCaches(model.getId(), model.getEntityTypeCode());
        entityCacheEvictionService.evictEntity(entityUpdateReqVO.getId());

        List<String> changedFields = entityBusinessHelper.extractFieldCodes(
                entityUpdateReqVO.getBaseFields(), entityUpdateReqVO.getCustomFields());
        entityLifecycleEventPublisher.publishEntityUpdatedEvent(model.getId(), entityUpdateReqVO.getId(), model.getEntityTypeCode(), changedFields, update);

        String newName = EntityFieldMapsSupport.asStringFromMap(entityUpdateReqVO.getBaseFields(), "name");
        if (newName != null && !newName.equals(oldName)) {
            entityLifecycleEventPublisher.publishEntityNameChangedEvent(
                    entityUpdateReqVO.getId(), oldName, newName,
                    model.getEntityTypeCode(), model.getCode(), db.getTenantId());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(CategoryDeleteReqVO reqVO) {
        Long id = reqVO.getId();
        boolean cascade = Boolean.TRUE.equals(reqVO.getCascade());
        String categoryTypeCode = reqVO.getCategoryTypeCode();
        
        // 获取分类信息
        CategoryDO category = categoryMapper.selectById(id);
        if (category == null) {
            throw new ServiceException(404, "分类不存在");
        }
        if (StrUtil.isBlank(categoryTypeCode)) {
            categoryTypeCode = category.getCategoryTypeCode();
        }
        CategoryTypeDO type = requireCategoryType(categoryTypeCode);
        String mode = CategoryModeSupport.resolveFromType(type);
        
        // 收集要删除的分类ID（包括子分类，如果是级联删除）
        List<Long> categoryIdsToDelete = new ArrayList<>();
        categoryIdsToDelete.add(id);
        
        if (cascade) {
            // 级联删除：收集所有子分类ID
            collectChildCategoryIds(id, categoryTypeCode, categoryIdsToDelete);
        }
        
        // 删除前检查（仅在非级联模式下检查）
        if (!cascade) {
            // 检查是否有子分类
            List<CategoryDO> children = categoryMapper.selectByCategoryTypeCode(categoryTypeCode).stream()
                    .filter(c -> Objects.equals(c.getParentId(), id))
                    .toList();
            if (!children.isEmpty()) {
                throw new ServiceException(400, "分类存在子分类，禁止删除（请使用级联删除或先删除/移动子分类）");
            }
            
            // 检查是否有模型关联
            List<ModelCategoryRelationDO> modelRelations =
                    modelCategoryRelationMapper.selectByCategoryId(id);
            if (modelRelations != null && !modelRelations.isEmpty()) {
                throw new ServiceException(400, buildCategoryDeleteBlockedByModelsMessage(modelRelations));
            }
        }
        
        // 级联删除关联关系（在删除分类之前）
        // - Model-Category 关联：批量清理
        modelCategoryRelationService.deleteAllByCategoryIds(categoryIdsToDelete);
        // - Entity-Category 关联：批量清理（用于删除分类树时一次性解除所有下级分类与实体的关联）
        entityCategoryRelationService.deleteAllByCategoryIds(categoryIdsToDelete);

        // 按种类 mode 处理 1:1 实体：高级删实体；简单禁止存在 link
        deleteEntityLinksByCategoryMode(categoryIdsToDelete, type, mode);
        
        core.deleteCategory(id, cascade);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategoryNodeAfterEntityRemoved(Long categoryId, String categoryTypeCode) {
        if (categoryId == null) {
            return;
        }
        CategoryDO category = categoryMapper.selectById(categoryId);
        if (category == null) {
            return;
        }
        String typeCode = StrUtil.isNotBlank(categoryTypeCode) ? categoryTypeCode : category.getCategoryTypeCode();
        CategoryTypeDO type = requireCategoryType(typeCode);
        String mode = CategoryModeSupport.resolveFromType(type);
        if (!CategoryModeSupport.isAdvanced(mode)) {
            throw new ServiceException(400, "简单分类节点不应绑定实体，数据异常，拒绝删除");
        }
        if (isStructuralRoot(category, type)) {
            throw new ServiceException(400, "高级分类顶层节点不应绑定实体，数据异常，拒绝删除");
        }
        List<CategoryDO> children = categoryMapper.selectByCategoryTypeCode(typeCode).stream()
                .filter(c -> Objects.equals(c.getParentId(), categoryId))
                .toList();
        if (!children.isEmpty()) {
            throw new ServiceException(400, "分类存在子分类，禁止删除（请先删除/移动子分类）");
        }
        List<ModelCategoryRelationDO> modelRelations = modelCategoryRelationMapper.selectByCategoryId(categoryId);
        if (modelRelations != null && !modelRelations.isEmpty()) {
            throw new ServiceException(400, buildCategoryDeleteBlockedByModelsMessage(modelRelations));
        }
        // link / 实体已由调用方清理；此处只清分类–型号/分类–实体关联后删节点
        modelCategoryRelationService.deleteAllByCategoryIds(List.of(categoryId));
        entityCategoryRelationService.deleteAllByCategoryIds(List.of(categoryId));
        CategoryEntityLinkDO leftover = categoryEntityLinkService.getLinkByCategoryId(categoryId);
        if (leftover != null) {
            categoryEntityLinkService.unlinkCategoryEntity(categoryId);
        }
        core.deleteCategory(categoryId, false);
    }

    /**
     * 删除时的实体联动：分支依据是种类 categoryMode，不是「扫到 link 再决定」。
     * 结构根（顶层节点）允许无实体；高级业务节点有实体则一并删除；
     * 高级业务节点缺 link 视为历史脏数据，允许删节点本体，避免永久锁死。
     * 简单节点不得有 1:1 link。
     */
    private void deleteEntityLinksByCategoryMode(List<Long> categoryIdsToDelete, CategoryTypeDO type, String mode) {
        Map<Long, CategoryDO> categoryMap = categoryMapper.selectByIds(categoryIdsToDelete).stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(CategoryDO::getId, c -> c, (a, b) -> a));
        Map<Long, CategoryEntityLinkDO> linkMap = categoryEntityLinkService.getLinksByCategoryIds(categoryIdsToDelete).stream()
                .filter(Objects::nonNull)
                .filter(link -> link.getCategoryId() != null)
                .collect(Collectors.toMap(CategoryEntityLinkDO::getCategoryId, link -> link, (a, b) -> a));

        boolean advanced = CategoryModeSupport.isAdvanced(mode);
        for (Long categoryId : categoryIdsToDelete) {
            CategoryDO node = categoryMap.get(categoryId);
            boolean root = isStructuralRoot(node, type);
            CategoryEntityLinkDO link = linkMap.get(categoryId);

            if (!advanced || root) {
                if (link != null && link.getEntityId() != null) {
                    throw new ServiceException(400,
                            root
                                    ? "高级分类顶层节点不应绑定实体，数据异常，拒绝删除"
                                    : "简单分类节点不应绑定实体，数据异常，拒绝删除");
                }
                continue;
            }

            if (link == null || link.getEntityId() == null) {
                // 脏数据：种类已是 ADVANCED，但历史节点未写 1:1 link。
                // 拒绝删除会永久锁死；允许删节点本体以清理，新建仍强制选模型+建实体。
                log.warn(
                        "高级分类业务节点缺少实体关联，按脏数据清理分类节点: categoryId={}, categoryTypeCode={}",
                        categoryId,
                        type != null ? type.getCategoryTypeCode() : null);
                continue;
            }
            ModelDO model = modelMapper.selectById(link.getEntityModelId());
            if (model == null) {
                throw new ServiceException(404, "高级分类关联的 Model 不存在");
            }
            deleteEntityForCategory(link.getEntityId(), model.getEntityTypeCode(), false);
            categoryEntityLinkService.unlinkCategoryEntity(categoryId);
        }
    }

    private String buildCategoryDeleteBlockedByModelsMessage(List<ModelCategoryRelationDO> modelRelations) {
        List<Long> modelIds = modelRelations.stream()
                .map(ModelCategoryRelationDO::getModelId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (modelIds.isEmpty()) {
            return "该分类下仍有关联模型，暂不能删除";
        }
        List<ModelDO> models = modelMapper.selectBatchIds(modelIds);
        String names = models == null ? "" : models.stream()
                .filter(Objects::nonNull)
                .map(ModelDO::getName)
                .filter(name -> name != null && !name.isBlank())
                .collect(Collectors.joining("、"));
        if (names.isBlank()) {
            return "该分类下仍关联 " + modelIds.size() + " 个模型，暂不能删除";
        }
        return "该分类下仍关联模型：" + names + "。删除前需先解除关联，或确认后一并解除关联（不会删除模型）";
    }
    
    /**
     * 递归收集所有子分类ID
     * 
     * @param parentId 父分类ID
     * @param categoryTypeCode 分类类型编码
     * @param result 结果列表
     */
    private void collectChildCategoryIds(Long parentId, String categoryTypeCode, List<Long> result) {
        List<CategoryDO> children = categoryMapper.selectByCategoryTypeCode(categoryTypeCode).stream()
                .filter(c -> Objects.equals(c.getParentId(), parentId))
                .toList();
        for (CategoryDO child : children) {
            result.add(child.getId());
            // 递归收集子分类的子分类
            collectChildCategoryIds(child.getId(), categoryTypeCode, result);
        }
    }

    @Override
    public CategoryRespVO getCategoryVO(Long id) {
        CategoryDO doObj = categoryMapper.selectById(id);
        if (doObj == null) {
            throw new ServiceException(404, "分类不存在");
        }
        // 解密敏感字段：description
        if (doObj.getDescription() != null) {
            doObj.setDescription(SensitiveDataEncryptor.decrypt(doObj.getDescription()));
        }
        CategoryRespVO respVO = CategoryConvert.INSTANCE.convert(doObj);

        CategoryEntityLinkDO link = categoryEntityLinkService.getLinkByCategoryId(id);
        if (link != null && link.getEntityId() != null) {
            respVO.setIsEntity(true);
            respVO.setEntityModelId(link.getEntityModelId());
            try {
                // 模式C：通过 Model 获取真正的 entityTypeCode 进行路由
                ModelDO model = modelMapper.selectById(link.getEntityModelId());
                if (model != null) {
                    EntityDO entityDO = entityCoreService.get(link.getEntityId(), model.getEntityTypeCode());
                    if (entityDO != null) {
                        EntityRespVO entityRespVO = EntityDoVoHelper.toRespVO(entityDO, customFieldValidationService);
                        respVO.setCustomFields(entityRespVO.getCustomFields());
                    }
                }
            } catch (ServiceException e) {
                // 如果实体不存在（可能已被删除），记录警告但不影响分类信息的返回
                // 这种情况可能是因为实体被删除但 Link 没有同步删除
                // 或者实体存储在动态表中，但数据不一致
                // 注意：这里不抛出异常，允许分类信息正常返回，只是没有 customFields
            }
        }

        return respVO;
    }

    /**
     * 补齐树/列表返回中的实体关联展示字段。
     * <p>link 只表示「该节点绑了哪条实体」，不用于推断种类是简单还是高级；
     * 种类语义以 CategoryType.categoryMode 为准。</p>
     */
    private void fillEntityCategoryFlags(List<CategoryTreeRespVO> flat) {
        if (flat == null || flat.isEmpty()) {
            return;
        }
        List<Long> ids = flat.stream()
                .map(CategoryTreeRespVO::getId)
                .filter(Objects::nonNull)
                .toList();
        if (ids.isEmpty()) {
            return;
        }
        Map<Long, CategoryEntityLinkDO> linkMap = categoryEntityLinkService.getLinksByCategoryIds(ids).stream()
                .filter(Objects::nonNull)
                .filter(link -> link.getCategoryId() != null)
                .collect(Collectors.toMap(CategoryEntityLinkDO::getCategoryId, link -> link, (a, b) -> a));
        for (CategoryTreeRespVO vo : flat) {
            if (vo.getId() == null) {
                continue;
            }
            CategoryEntityLinkDO link = linkMap.get(vo.getId());
            if (link != null) {
                vo.setIsEntity(true);
                vo.setEntityModelId(link.getEntityModelId());
                vo.setEntityId(link.getEntityId());
            } else if (vo.getIsEntity() == null) {
                // 兼容：此前接口一直返回 null，这里仅对非实体节点填充 false，避免前端三态判断
                vo.setIsEntity(false);
            } else {
                // 缓存里可能残留旧的 isEntity=true；无 link 时清掉，避免前端误开详情
                vo.setIsEntity(false);
                vo.setEntityId(null);
                vo.setEntityModelId(null);
            }
        }
    }

    /** 深度优先展平分类树，供缓存命中后重新补齐 link 展示字段。 */
    private void flattenCategoryTree(List<CategoryTreeRespVO> nodes, List<CategoryTreeRespVO> out) {
        if (nodes == null || nodes.isEmpty()) {
            return;
        }
        for (CategoryTreeRespVO node : nodes) {
            if (node == null) {
                continue;
            }
            out.add(node);
            flattenCategoryTree(node.getChildren(), out);
        }
    }

    @Override
    public List<CategoryTreeRespVO> getCategoryTreeByType(String categoryTypeCode, Integer status) {
        String cacheKey = status == null ? "type:" + categoryTypeCode : "type:" + categoryTypeCode + ":status:" + status;
        List<CategoryTreeRespVO> cached = null;
        try {
            cached = CategoryCacheHelper.getCachedTree(stringRedisTemplate, cacheKey);
        } catch (Exception e) {
            // 如果缓存访问失败（Redis 异常等），记录警告并继续走 DB 查询流程，避免缓存层异常导致整个接口不可用
            log.warn("[getCategoryTreeByType] 读取分类树缓存失败，忽略缓存并回退到 DB 查询，cacheKey={}", cacheKey, e);
            cached = null;
        }
        if (cached != null) {
            // 缓存中的数据也需要解密（缓存中存储的是加密后的数据）
            cached.forEach(item -> {
                if (item.getDescription() != null) {
                    item.setDescription(SensitiveDataEncryptor.decrypt(item.getDescription()));
                }
            });
            // link 可能在缓存写入后变更（补建实体关联等）；返回前按最新 link 补齐展示字段，避免点树看不到详情
            List<CategoryTreeRespVO> cachedFlat = new ArrayList<>();
            flattenCategoryTree(cached, cachedFlat);
            fillEntityCategoryFlags(cachedFlat);
            return cached;
        }
        List<CategoryDO> list = categoryMapper.selectByCategoryTypeCode(categoryTypeCode);
        if (status != null) {
            list = list.stream().filter(item -> status.equals(item.getStatus())).toList();
        }
        // 解密敏感字段：description
        list.forEach(item -> {
            if (item.getDescription() != null) {
                item.setDescription(SensitiveDataEncryptor.decrypt(item.getDescription()));
            }
        });
        List<CategoryTreeRespVO> flat = CategoryConvert.INSTANCE.convertTreeList(list);
        fillEntityCategoryFlags(flat);
        List<CategoryTreeRespVO> tree = CategoryUtils.buildTreeByVO(flat);
        // 注意：缓存前需要重新加密（缓存中应该存储加密数据）
        tree.forEach(item -> {
            if (item.getDescription() != null) {
                item.setDescription(SensitiveDataEncryptor.encrypt(item.getDescription()));
            }
        });
        CategoryCacheHelper.cacheTree(stringRedisTemplate, cacheKey, tree);
        // 返回前解密（返回给调用方的应该是明文）
        tree.forEach(item -> {
            if (item.getDescription() != null) {
                item.setDescription(SensitiveDataEncryptor.decrypt(item.getDescription()));
            }
        });
        return tree;
    }

    @Override
    public List<CategoryTreeWithModelsRespVO> getCategoryTreeWithModels(String categoryTypeCode, Integer status) {
        // 1) 分类树（单次查询）
        List<CategoryDO> categories = categoryMapper.selectByCategoryTypeCode(categoryTypeCode);
        if (status != null) {
            categories = categories.stream().filter(item -> status.equals(item.getStatus())).toList();
        }
        if (categories.isEmpty()) {
            return List.of();
        }

        categories.forEach(item -> {
            if (item.getDescription() != null) {
                item.setDescription(SensitiveDataEncryptor.decrypt(item.getDescription()));
            }
        });

        // 2) 批量取关系（分类->模型），按 sort,id 已排序
        List<Long> categoryIds = categories.stream().map(CategoryDO::getId).filter(Objects::nonNull).toList();
        List<ModelCategoryRelationDO> relations = modelCategoryRelationMapper
                .selectRelationsByCategoryIdsForOrdering(categoryIds, categoryTypeCode);

        // 3) 批量取模型详情
        List<Long> modelIds = relations.stream().map(ModelCategoryRelationDO::getModelId).filter(Objects::nonNull).distinct().toList();
        Map<Long, ModelRespVO> modelById = modelIds.isEmpty()
                ? Collections.emptyMap()
                : modelMapper.selectBatchIds(modelIds).stream()
                        .filter(Objects::nonNull)
                        .collect(Collectors.toMap(ModelDO::getId, ModelConvert.INSTANCE::convert, (a, b) -> a));

        // 4) 组装 categoryId -> models[]（按 relations 原始顺序）
        Map<Long, List<ModelRespVO>> modelsByCategoryId = new LinkedHashMap<>();
        for (ModelCategoryRelationDO relation : relations) {
            if (relation == null || relation.getCategoryId() == null || relation.getModelId() == null) {
                continue;
            }
            ModelRespVO model = modelById.get(relation.getModelId());
            if (model == null) {
                continue;
            }
            modelsByCategoryId.computeIfAbsent(relation.getCategoryId(), k -> new ArrayList<>()).add(model);
        }

        // 5) 一次性生成前端可直接渲染的树：每个分类节点自带 models + children
        List<CategoryTreeWithModelsRespVO> flat = categories.stream().map(category -> {
            CategoryTreeWithModelsRespVO vo = new CategoryTreeWithModelsRespVO();
            vo.setId(category.getId());
            vo.setName(category.getName());
            vo.setCode(category.getCode());
            vo.setParentId(category.getParentId());
            vo.setCategoryTypeCode(category.getCategoryTypeCode());
            vo.setSort(category.getSort());
            vo.setStatus(category.getStatus());
            vo.setDescription(category.getDescription());
            vo.setCreateTime(category.getCreateTime());
            vo.setModels(modelsByCategoryId.getOrDefault(category.getId(), new ArrayList<>()));
            return vo;
        }).toList();

        return CategoryUtils.buildTreeByVO(flat);
    }

    /**
     * 用途：获取分类树并挂载模型列表；关键点：模型按关联 sort 排序。
     */
    @Override
    public List<Long> getAllCategoryIdsIncludingChildren(Long categoryId, String categoryTypeCode) {
        CategoryDO root = categoryMapper.selectByIdAndCategoryTypeCode(categoryId, categoryTypeCode);
        // categoryTypeCode 与节点不一致时，勿静默缩成单节点（会漏子孙）
        if (root == null && categoryTypeCode != null && !categoryTypeCode.isBlank()) {
            root = categoryMapper.selectByIdAndCategoryTypeCode(categoryId, null);
        }
        if (root == null) {
            return List.of(categoryId);
        }

        String effectiveCategoryTypeCode = root.getCategoryTypeCode();
        if (effectiveCategoryTypeCode == null || effectiveCategoryTypeCode.isBlank()) {
            return List.of(categoryId);
        }

        // 以 parent_id 为准展开（与分类树 UI 一致）。
        // tree_path 可能脏数据（如枪型摄像机 tree_path=/283/ 缺少祖先前缀）；
        // 若仅用 path 前缀且部分子节点 path 正常、部分异常，会提前返回不完整子树，导致上级分类看不到下级型号/实体。
        return collectCategoryIdsByParentLink(categoryId, effectiveCategoryTypeCode);
    }

    private List<Long> collectCategoryIdsByParentLink(Long rootCategoryId, String categoryTypeCode) {
        List<CategoryDO> all = categoryMapper.selectByCategoryTypeCode(categoryTypeCode);
        if (all == null || all.isEmpty()) {
            return List.of(rootCategoryId);
        }
        Map<Long, List<CategoryDO>> childrenMap = all.stream()
                .collect(Collectors.groupingBy(item -> item.getParentId() == null ? 0L : item.getParentId()));
        LinkedHashSet<Long> orderedIds = new LinkedHashSet<>();
        orderedIds.add(rootCategoryId);
        Set<Long> descendantIds = new HashSet<>();
        collectDescendantIds(rootCategoryId, childrenMap, descendantIds);
        orderedIds.addAll(descendantIds);
        return new ArrayList<>(orderedIds);
    }

    @Override
    public Map<Long, List<Long>> getAllCategoryIdsIncludingChildrenBatch(List<Long> categoryIds, String categoryTypeCode) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Long, List<Long>> result = new LinkedHashMap<>();
        for (Long categoryId : categoryIds) {
            if (categoryId == null) {
                continue;
            }
            result.put(categoryId, getAllCategoryIdsIncludingChildren(categoryId, categoryTypeCode));
        }
        return result;
    }

    @Override
    public void moveCategory(Long id, Long targetParentId, String categoryTypeCode) {
        CategoryDO doObj = categoryMapper.selectById(id);
        if (doObj == null) {
            throw new ServiceException(404, "分类不存在");
        }
        core.moveCategory(id, targetParentId);
    }

    @Override
    public void sortCategories(Long parentId, List<Long> orderedIds, String categoryTypeCode) {
        if (orderedIds == null || orderedIds.isEmpty()) {
            return;
        }
        
        // 验证所有分类是否属于同一个父分类
        for (Long id : orderedIds) {
            CategoryDO category = categoryMapper.selectByIdAndCategoryTypeCode(id, categoryTypeCode);
            if (category == null) {
                throw new ServiceException(404, "分类不存在: " + id);
            }
            if (!Objects.equals(category.getParentId(), parentId)) {
                throw new ServiceException(400, 
                    String.format("分类 %d 的父分类ID (%s) 与指定的父分类ID (%s) 不匹配", 
                        id, category.getParentId(), parentId));
            }
        }
        
        // 按顺序更新 sort 字段
        int sort = 1;
        for (Long id : orderedIds) {
            CategoryDO update = new CategoryDO();
            update.setId(id);
            update.setSort(sort++);
            categoryMapper.updateById(update);
        }
        
        evictCache(categoryTypeCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void dragCategory(CategoryDragReqVO reqVO) {
        CategoryDragReqVO.Position position = reqVO.getPosition();
        // 注意：这里的categoryTypeCode用于限定分类树范围，确保拖动在同一分类类型内部进行
        String categoryTypeCode = reqVO.getCategoryTypeCode();

        if (position == null) {
            throw new ServiceException(400, "拖拽位置不能为空");
        }

        // 使用categoryTypeCode查询分类，确保只操作指定分类类型下的分类
        CategoryDO drag = categoryMapper.selectByIdAndCategoryTypeCode(reqVO.getDragId(), categoryTypeCode);
        if (drag == null) {
            throw new ServiceException(404, "拖拽的分类不存在");
        }

        CategoryDO target = null;
        if (reqVO.getTargetId() != null) {
            // 同样使用categoryTypeCode确保目标分类在同一分类类型下
            target = categoryMapper.selectByIdAndCategoryTypeCode(reqVO.getTargetId(), categoryTypeCode);
            if (target == null) {
                throw new ServiceException(404, "目标分类不存在");
            }
        }

        // 拖动前补全/修正涉及节点的 tree_path 与 level，避免历史脏数据导致移动失败
        core.ensureTreeMetadata(drag.getId(), categoryTypeCode);
        drag = categoryMapper.selectByIdAndCategoryTypeCode(reqVO.getDragId(), categoryTypeCode);
        if (target != null) {
            core.ensureTreeMetadata(target.getId(), categoryTypeCode);
            target = categoryMapper.selectByIdAndCategoryTypeCode(reqVO.getTargetId(), categoryTypeCode);
        }

        Long resolvedParentId;
        if (position == CategoryDragReqVO.Position.BEFORE || position == CategoryDragReqVO.Position.AFTER) {
            if (target == null) {
                throw new ServiceException(400, "BEFORE/AFTER 需要提供 targetId");
            }
            resolvedParentId = target.getParentId();
        } else if (position == CategoryDragReqVO.Position.INNER) {
            resolvedParentId = reqVO.getTargetParentId() != null
                    ? reqVO.getTargetParentId()
                    : (target != null ? target.getId() : null);
        } else {
            throw new ServiceException(400, "不支持的拖拽位置");
        }

        List<CategoryDO> all = categoryMapper.selectByCategoryTypeCode(categoryTypeCode);

        // 校验目标父节点合法性与层级限制
        if (resolvedParentId != null) {
            if (Objects.equals(resolvedParentId, drag.getId())) {
                throw new ServiceException(400, "不能拖拽到自身节点下");
            }
            if (isDescendantOf(drag.getId(), resolvedParentId, all)) {
                throw new ServiceException(400, "不能拖拽到自己的子节点下");
            }
            CategoryDO parent = all.stream()
                    .filter(item -> Objects.equals(item.getId(), resolvedParentId))
                    .findFirst()
                    .orElse(null);
            if (parent == null) {
                throw new ServiceException(404, "目标父分类不存在");
            }
            core.ensureTreeMetadata(resolvedParentId, categoryTypeCode);
            parent = categoryMapper.selectByIdAndCategoryTypeCode(resolvedParentId, categoryTypeCode);
            int newLevel = (parent.getLevel() == null ? 0 : parent.getLevel()) + 1;
            if (newLevel > DEFAULT_MAX_LEVEL) {
                throw new ServiceException(400,
                        "拖拽后分类层级为 " + newLevel + "，超过上限 " + DEFAULT_MAX_LEVEL
                                + "（目标父分类「" + parent.getName() + "」当前 level=" + parent.getLevel() + "）");
            }
        } else {
            // 拖到根节点，层级为 1，不需要额外校验
        }

        boolean parentChanged = !Objects.equals(drag.getParentId(), resolvedParentId);
        if (parentChanged) {
            core.moveCategory(drag.getId(), resolvedParentId);
            drag = categoryMapper.selectByIdAndCategoryTypeCode(drag.getId(), categoryTypeCode);
        }

        if (position == CategoryDragReqVO.Position.INNER) {
            reorderSiblings(resolvedParentId, categoryTypeCode, drag.getId(), null, position);
        } else {
            // Fix potential NPE: BEFORE/AFTER requires target to be non-null
            if (target == null) {
                throw new ServiceException(400, "非 INNER 拖拽必须指定目标节点");
            }
            reorderSiblings(resolvedParentId, categoryTypeCode, drag.getId(), target.getId(), position);
        }

        evictCache(categoryTypeCode);
    }

    @Override
    public List<CategoryRespVO> searchCategoryList(String keyword, String categoryTypeCode) {
        List<CategoryDO> list = categoryMapper.searchLikeByCategoryType(keyword, categoryTypeCode);
        return CategoryConvert.INSTANCE.convertList(list);
    }

    @Override
    public List<CategoryTreeRespVO> searchCategoryTree(String keyword, String categoryTypeCode) {
        // 1) 查询该分类体系下的所有分类（用于补齐祖先链与子孙树）
        List<CategoryDO> all = categoryMapper.selectByCategoryTypeCode(categoryTypeCode);
        // 解密敏感字段：description
        all.forEach(item -> {
            if (item.getDescription() != null) {
                item.setDescription(SensitiveDataEncryptor.decrypt(item.getDescription()));
            }
        });

        if (all.isEmpty()) {
            return List.of();
        }

        // 2) 命中节点（name like keyword）
        String k = keyword == null ? null : keyword.trim();
        Set<Long> selectedIds = all.stream()
                .filter(item -> k != null && !k.isEmpty()
                        && item.getName() != null
                        && item.getName().contains(k))
                .map(CategoryDO::getId)
                .collect(Collectors.toSet());

        // 关键词为空：按全量树返回（等同于 getCategoryTreeByType(categoryTypeCode, null) 的语义）
        if (k == null || k.isEmpty()) {
            List<CategoryTreeRespVO> flatAll = CategoryConvert.INSTANCE.convertTreeList(all);
            fillEntityCategoryFlags(flatAll);
            return CategoryUtils.buildTreeByVO(flatAll);
        }

        if (selectedIds.isEmpty()) {
            return List.of();
        }

        // 3) 构建父子关系索引
        Map<Long, CategoryDO> idMap = all.stream()
                .filter(item -> item.getId() != null)
                .collect(Collectors.toMap(CategoryDO::getId, item -> item, (a, b) -> a));
        Map<Long, List<CategoryDO>> childrenMap = all.stream()
                .collect(Collectors.groupingBy(item -> item.getParentId() == null ? 0L : item.getParentId()));

        // 4) 补齐祖先链（向上）
        Set<Long> ancestorIds = new HashSet<>();
        for (Long id : selectedIds) {
            Long cur = id;
            while (cur != null) {
                CategoryDO node = idMap.get(cur);
                if (node == null) {
                    break;
                }
                Long parentId = node.getParentId();
                if (parentId == null) {
                    break;
                }
                if (!ancestorIds.add(parentId)) {
                    // 已经处理过该祖先
                    cur = parentId;
                    continue;
                }
                cur = parentId;
            }
        }

        // 5) 补齐子孙树（向下）
        Set<Long> descendantIds = new HashSet<>();
        for (Long id : selectedIds) {
            collectDescendantIds(id, childrenMap, descendantIds);
        }

        // 6) 合并得到最终需要的节点集合
        Set<Long> finalIds = new HashSet<>();
        finalIds.addAll(selectedIds);
        finalIds.addAll(ancestorIds);
        finalIds.addAll(descendantIds);

        List<CategoryDO> finalList = all.stream()
                .filter(item -> finalIds.contains(item.getId()))
                .sorted(Comparator.comparing(CategoryDO::getSort, Comparator.nullsLast(Integer::compareTo))
                        .thenComparing(CategoryDO::getId, Comparator.nullsLast(Long::compareTo)))
                .toList();

        List<CategoryTreeRespVO> flat = CategoryConvert.INSTANCE.convertTreeList(finalList);
        fillEntityCategoryFlags(flat);
        return CategoryUtils.buildTreeByVO(flat);
    }

    private void collectDescendantIds(Long id, Map<Long, List<CategoryDO>> childrenMap, Set<Long> result) {
        List<CategoryDO> children = childrenMap.get(id);
        if (children == null || children.isEmpty()) {
            return;
        }
        for (CategoryDO child : children) {
            if (child.getId() == null) {
                continue;
            }
            if (result.add(child.getId())) {
                collectDescendantIds(child.getId(), childrenMap, result);
            }
        }
    }

    @Override
    public void updateStatus(Long id, Integer status, String categoryTypeCode) {
        CategoryDO db = categoryMapper.selectById(id);
        if (db == null) {
            throw new ServiceException(404, "分类不存在");
        }
        db.setStatus(status);
        categoryMapper.updateById(db);
        // 清除相关缓存
        evictCache(db.getCategoryTypeCode());
    }

    @Override
    public List<CategoryRespVO> listByParent(String categoryTypeCode, Long parentId, Integer status) {
        List<CategoryDO> list = categoryMapper.selectByParentIdAndCategoryTypeCode(parentId, categoryTypeCode);
        list = list.stream()
                .filter(item -> status == null || status.equals(item.getStatus()))
                .peek(item -> {
                    // 解密敏感字段：description
                    if (item.getDescription() != null) {
                        item.setDescription(SensitiveDataEncryptor.decrypt(item.getDescription()));
                    }
                })
                .toList();
        return CategoryConvert.INSTANCE.convertList(list);
    }

    @Override
    public List<CategoryRespVO> listByParentRecursive(String categoryTypeCode, Long parentId, Integer status) {
        // 1) 获取根节点，拿到 treePath
        CategoryDO root = categoryMapper.selectByIdAndCategoryTypeCode(parentId, categoryTypeCode);
        if (root == null || root.getTreePath() == null) {
            return List.of();
        }

        // 2) 利用 treePath 前缀索引查询整棵子树（包含自身）
        List<CategoryDO> list = categoryMapper.selectSubtreeByPath(root.getTreePath(), categoryTypeCode, status);
        if (list.isEmpty()) {
            return List.of();
        }

        // 3) 排除自身（语义是“子分类列表”）并解密
        list = list.stream()
                .filter(item -> item.getId() != null && !Objects.equals(item.getId(), parentId))
                .peek(item -> {
                    if (item.getDescription() != null) {
                        item.setDescription(SensitiveDataEncryptor.decrypt(item.getDescription()));
                    }
                })
                .toList();
        return CategoryConvert.INSTANCE.convertList(list);
    }

    @Override
    public List<CategoryRespVO> getPath(Long id, String categoryTypeCode) {
        CategoryDO target = categoryMapper.selectById(id);
        if (target == null) {
            throw new ServiceException(404, "分类不存在");
        }
        String resolvedTypeCode = categoryTypeCode != null ? categoryTypeCode : target.getCategoryTypeCode();
        List<CategoryDO> all = categoryMapper.selectByCategoryTypeCode(resolvedTypeCode);
        Map<Long, CategoryDO> idMap = all.stream()
                .filter(item -> item.getId() != null)
                .collect(Collectors.toMap(CategoryDO::getId, item -> item, (a, b) -> a));

        List<CategoryDO> pathNodes = new ArrayList<>();
        Long currentId = target.getId();
        while (currentId != null) {
            CategoryDO node = idMap.get(currentId);
            if (node == null) {
                break;
            }
            pathNodes.add(0, node);
            currentId = node.getParentId();
        }
        return pathNodes.stream().map(CategoryConvert.INSTANCE::convert).toList();
    }

    private void deleteEntityForCategory(Long id, String entityTypeCode, Boolean forceDelete) {
        // 1. 使用 CoreService 查询实体
        EntityDO db = entityCoreService.get(id, entityTypeCode);
        if (db == null) {
            log.warn("分类关联的实体不存在，无需删除: entityId={}, entityTypeCode={}", id, entityTypeCode);
            return;
        }

        // 2. 检查关联关系（如果不是强制删除）
        if (!Boolean.TRUE.equals(forceDelete)) {
            Long relationCount = entityRelationMapper.countByEntityId(id);
            if (relationCount != null && relationCount > 0) {
                throw new ServiceException(400, "实体存在关联关系，无法删除。如需强制删除，请设置 forceDelete=true");
            }
        }

        // 3. 删除关联关系（带 storage 类型，避免跨表同 id）
        entityCategoryRelationService.deleteAllByEntityIdInBusiness(id, entityTypeCode);
        categoryEntityLinkService.unlinkEntityCategory(id, entityTypeCode);
        entityTypeScopeMapper.deleteByEntityIdAndCodes(id, listScopeCodesForStorage(entityTypeCode));
        entityRelationMapper.deleteByEntityId(id);

        // 4. 使用 CoreService 删除实体
        entityCoreService.delete(id, entityTypeCode);

        // 5. 清除缓存
        entityCacheEvictionService.evictEntityCaches(db.getModelId(), entityTypeCode);
        entityCacheEvictionService.evictEntity(id);

        // 6. 发布事件
        entityLifecycleEventPublisher.publishEntityDeletedEvent(db.getModelId(), id, entityTypeCode);
    }

    private void linkCategoryEntityWithStorage(Long categoryId, Long entityId, Long entityModelId) {
        ModelDO model = entityModelId == null ? null : modelMapper.selectById(entityModelId);
        String storage = model != null ? model.getEntityTypeCode() : null;
        String domain = model != null ? model.getDomain() : null;
        categoryEntityLinkService.linkCategoryToEntity(categoryId, entityId, entityModelId, storage, domain);
    }

    private List<String> listScopeCodesForStorage(String storageEntityTypeCode) {
        if (StrUtil.isBlank(storageEntityTypeCode)) {
            return List.of();
        }
        return entityTypeMapper.selectList(
                        new cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX<
                                cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO>()
                                .eq(cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO::getEntryKind,
                                        cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO.ENTRY_KIND_SCOPE)
                                .eq(cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO::getBaseEntityTypeCode,
                                        storageEntityTypeCode.trim())
                                .eq(cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO::getDeleted, false))
                .stream()
                .map(cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO::getCode)
                .filter(StrUtil::isNotBlank)
                .map(String::trim)
                .toList();
    }

    private String generateCode() {
        return "CAT-" + IdUtil.fastSimpleUUID();
    }

    private void evictCache(String categoryTypeCode) {
        if (categoryTypeCode == null || categoryTypeCode.isEmpty()) {
            return;
        }
        // 与 getCategoryTreeByType 中使用的 cacheKey 规则保持一致：
        // - 不带 status: type:{categoryTypeCode}
        // - 带 status:   type:{categoryTypeCode}:status:{status}
        CategoryCacheHelper.evict(stringRedisTemplate, "type:" + categoryTypeCode);
        // 目前分类状态只用到 0/1，两种状态的缓存一并清理
        CategoryCacheHelper.evict(stringRedisTemplate, "type:" + categoryTypeCode + ":status:0");
        CategoryCacheHelper.evict(stringRedisTemplate, "type:" + categoryTypeCode + ":status:1");
    }

    @Override
    public List<CategoryRespVO> batchCreateCategory(List<CategoryCreateReqVO> categories) {
        List<CategoryRespVO> result = new ArrayList<>();
        String categoryTypeCode = null;
        for (CategoryCreateReqVO reqVO : categories) {
            Long id = createCategory(reqVO);
            if (categoryTypeCode == null) {
                categoryTypeCode = reqVO.getCategoryTypeCode();
            }
            result.add(getCategoryVO(id));
        }
        if (categoryTypeCode != null) {
            evictCache(categoryTypeCode);
        }
        return result;
    }

    @Override
    public List<CategoryRespVO> batchUpdateCategory(List<CategoryUpdateReqVO> categories) {
        List<CategoryRespVO> result = new ArrayList<>();
        String categoryTypeCode = null;
        for (CategoryUpdateReqVO reqVO : categories) {
            updateCategory(reqVO);
            // 从 reqVO 获取 categoryTypeCode，因为 CategoryBaseVO 已包含该字段
            if (categoryTypeCode == null) {
                categoryTypeCode = reqVO.getCategoryTypeCode();
            }
            if (categoryTypeCode != null) {
                result.add(getCategoryVO(reqVO.getId()));
            }
        }
        if (categoryTypeCode != null) {
            evictCache(categoryTypeCode);
        }
        return result;
    }

    @Override
    public CategoryBatchDeleteRespVO batchDeleteCategory(List<Long> ids, boolean cascade, String categoryTypeCode) {
        CategoryBatchDeleteRespVO respVO = new CategoryBatchDeleteRespVO();
        List<CategoryBatchDeleteRespVO.CategoryBatchDeleteFailItem> failItems = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;

        for (Long id : ids) {
            try {
                CategoryDeleteReqVO reqVO = new CategoryDeleteReqVO();
                reqVO.setId(id);
                reqVO.setCascade(cascade);
                reqVO.setCategoryTypeCode(categoryTypeCode);
                deleteCategory(reqVO);
                successCount++;
            } catch (Exception e) {
                failCount++;
                CategoryBatchDeleteRespVO.CategoryBatchDeleteFailItem failItem = new CategoryBatchDeleteRespVO.CategoryBatchDeleteFailItem();
                failItem.setId(id);
                failItem.setReason(e.getMessage());
                failItems.add(failItem);
            }
        }

        respVO.setSuccessCount(successCount);
        respVO.setFailCount(failCount);
        respVO.setFailItems(failItems);
        return respVO;
    }



    /**
     * 判断 targetId 是否在 rootId 的子树中
     */
    private boolean isDescendantOf(Long rootId, Long targetId, List<CategoryDO> all) {
        if (rootId == null || targetId == null || all == null || all.isEmpty()) {
            return false;
        }
        return listDescendantIds(rootId, all).contains(targetId);
    }

    private List<Long> listDescendantIds(Long rootId, List<CategoryDO> all) {
        List<Long> ids = new ArrayList<>();
        for (CategoryDO item : all) {
            if (Objects.equals(rootId, item.getParentId())) {
                ids.add(item.getId());
                ids.addAll(listDescendantIds(item.getId(), all));
            }
        }
        return ids;
    }

    /**
     * 重新排序同一父级下的节点
     */
    private void reorderSiblings(Long parentId, String categoryTypeCode, Long dragId, Long targetId, CategoryDragReqVO.Position position) {
        List<CategoryDO> siblings = categoryMapper.selectByParentIdAndCategoryTypeCode(parentId, categoryTypeCode);
        siblings.sort(Comparator.comparing(CategoryDO::getSort, Comparator.nullsLast(Integer::compareTo))
                .thenComparing(CategoryDO::getId));

        List<Long> ordered = new ArrayList<>();
        if (position == CategoryDragReqVO.Position.INNER) {
            // INNER：移动到目标父节点下，放到末尾
            for (CategoryDO item : siblings) {
                if (!Objects.equals(item.getId(), dragId)) {
                    ordered.add(item.getId());
                }
            }
            ordered.add(dragId);
        } else {
            for (CategoryDO item : siblings) {
                if (Objects.equals(item.getId(), dragId)) {
                    continue;
                }
                if (Objects.equals(item.getId(), targetId) && position == CategoryDragReqVO.Position.BEFORE) {
                    ordered.add(dragId);
                }
                ordered.add(item.getId());
                if (Objects.equals(item.getId(), targetId) && position == CategoryDragReqVO.Position.AFTER) {
                    ordered.add(dragId);
                }
            }
            if (!ordered.contains(dragId)) {
                ordered.add(dragId);
            }
        }

        int sort = 1;
        for (Long id : ordered) {
            CategoryDO update = new CategoryDO();
            update.setId(id);
            update.setSort(sort++);
            categoryMapper.updateById(update);
        }
    }

    @Override
    public CategoryTreeRespVO getCategorySubtreeWithRoot(Long id, String categoryTypeCode, Integer status) {
        // 1) 获取根节点信息，拿到 treePath
        CategoryDO root = categoryMapper.selectByIdAndCategoryTypeCode(id, categoryTypeCode);
        if (root == null) {
            return null;
        }

        // 2) 利用 treePath 前缀索引查询整棵子树（包含自身）
        List<CategoryDO> list = categoryMapper.selectSubtreeByPath(root.getTreePath(), categoryTypeCode, status);
        if (list.isEmpty()) {
            return null;
        }

        // 3) 构建局部树
        List<CategoryTreeRespVO> flat = CategoryConvert.INSTANCE.convertTreeList(list);
        fillEntityCategoryFlags(flat);
        List<CategoryTreeRespVO> tree = CategoryUtils.buildTreeByVO(flat);

        // 4) 返回指定的根节点
        return tree.stream()
                .filter(node -> Objects.equals(node.getId(), id))
                .findFirst()
                .orElse(null);
    }


    private class CategoryCoreService extends AbstractCategoryService<CategoryMapper, CategoryDO> {
        @Override
        protected CategoryMapper getMapper() {
            return categoryMapper;
        }

        @Override
        protected int getMaxLevel() {
            return DEFAULT_MAX_LEVEL;
        }

        @Override
        protected void evictCache(String categoryTypeCode) {
            // 统一走外部 Service 的缓存失效逻辑，确保与 getCategoryTreeByType 使用的 cacheKey 规则一致
            CategoryServiceImpl.this.evictCache(categoryTypeCode);
        }
    }
}

