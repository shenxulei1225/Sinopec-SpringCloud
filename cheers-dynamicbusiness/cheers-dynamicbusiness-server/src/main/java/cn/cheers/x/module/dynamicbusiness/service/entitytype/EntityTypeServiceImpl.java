package cn.cheers.x.module.dynamicbusiness.service.entitytype;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.*;
import cn.cheers.x.module.dynamicbusiness.controller.admin.field.vo.FieldCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelFieldAssignmentRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeRelationDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.group.GroupMapper;
import cn.cheers.x.module.dynamicbusiness.enums.group.GroupTypeEnum;
import cn.cheers.x.module.dynamicbusiness.service.group.GroupService;
import cn.cheers.x.module.dynamicbusiness.enums.entitytype.EntityTypeEntryKindEnum;
import cn.cheers.x.module.dynamicbusiness.enums.entitytype.StorageTypeEnum;
import cn.cheers.x.module.dynamicbusiness.framework.entitytype.EntityTypeScopeContext;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeRelationMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.EntityRepository;
import cn.cheers.x.module.dynamicbusiness.service.field.CustomFieldValidationService;
import cn.cheers.x.module.dynamicbusiness.service.field.FieldService;
import cn.cheers.x.module.dynamicbusiness.service.model.ModelFieldAssignmentService;
import cn.cheers.x.module.dynamicbusiness.service.model.ModelService;
import cn.cheers.x.module.dynamicbusiness.service.dynamictable.DynamicTableService;
import cn.cheers.x.module.dynamicbusiness.util.PhysicalColumnMappingUtils;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Validated
@Slf4j
public class EntityTypeServiceImpl implements EntityTypeService {

    @Resource
    private EntityTypeMapper entityTypeMapper;

    @Resource
    private EntityTypeRelationMapper entityTypeRelationMapper;

    @Resource
    private FieldService fieldService;

    @Resource
    private ModelService modelService;

    @Resource
    private ModelFieldAssignmentService modelFieldAssignmentService;

    @Resource
    private CustomFieldValidationService customFieldValidationService;

    @Resource
    private ModelMapper modelMapper;

    @Resource
    private EntityRepository entityRepository;

    @Resource
    private DynamicTableService dynamicTableService;

    @Resource
    private EntityTypeCategoryBootstrapService entityTypeCategoryBootstrapService;

    @Resource
    private EntityTypeOrchestrationBootstrapService entityTypeOrchestrationBootstrapService;

    @Resource
    private FacilityOwningFieldEnsureService facilityOwningFieldEnsureService;

    @Resource
    private GroupService groupService;

    @Resource
    private GroupMapper groupMapper;

    @Resource
    private cn.cheers.x.module.dynamicbusiness.framework.tenant.TenantAssociationTableService tenantAssociationTableService;

    /**
     * 创建业务类型。
     *
     * 适用场景：
     * - 新增业务域（如设备管理、任务管理）；
     * - 创建后自动生成关联字段模板；
     * - 专用存储业务自动建表。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(EntityTypeCreateReqVO reqVO) {
        EntityTypeEntryKindEnum entryKind = EntityTypeEntryKindEnum.fromCode(reqVO.getEntryKind());
        if (entryKind.isReuseEntry()) {
            return createReuseEntityType(reqVO);
        }
        if (entryKind.isDomainEntry()) {
            return createDomainEntityType(reqVO);
        }
        if (entryKind.isScopeEntry()) {
            return createScopeEntityType(reqVO);
        }
        if (entryKind.isCategory()) {
            return createCategoryEntityType(reqVO);
        }
        return createNativeEntityType(reqVO);
    }

    private Long createNativeEntityType(EntityTypeCreateReqVO reqVO) {
        // 1. 验证业务是否已经存在
        if (entityTypeMapper.existsByCode(reqVO.getCode())) {
            throw new ServiceException(400, "业务类型编码已存在");
        }
        EntityTypeDO entityType = new EntityTypeDO();
        copyBaseFields(entityType, reqVO);
        entityType.setTypeLevel(EntityTypeDO.TYPE_LEVEL_USER);
        entityType.setParentId(null);
        entityType.setEntryKind(EntityTypeDO.ENTRY_KIND_NATIVE);
        entityType.setBaseEntityTypeCode(null);
        entityType.setDomain(null);
        if (!StringUtils.hasText(entityType.getModelWorkbenchMode())) {
            entityType.setModelWorkbenchMode(EntityTypeDO.MODEL_WORKBENCH_MULTI);
        }
        ensureEntityTypeGroupRegistered(entityType.getGroupName());
        entityTypeMapper.insert(entityType);

        createRelationFieldForEntityType(entityType);

        StorageTypeEnum storageType = StorageTypeEnum.getByCode(entityType.getStorageType());
        if (storageType != null && storageType.isDedicated()) {
            String tableName = cn.cheers.x.module.dynamicbusiness.framework.tenant.TenantPhysicalTableNames
                    .entityPhysicalTable(entityType.getCode());
            entityType.setDedicatedTableName(tableName);
            entityTypeMapper.updateById(entityType);
            dynamicTableService.createDynamicTableForEntityType(
                    entityType.getCode(),
                    tableName,
                    entityType.getName(),
                    PhysicalColumnMappingUtils.parseMapping(entityType.getPhysicalColumnMapping())
            );
        }

        ensureTenantAssociationTablesQuietly();
        entityTypeCategoryBootstrapService.ensureForEntityTypeCode(entityType.getCode());
        entityTypeOrchestrationBootstrapService.ensureForEntityTypeCode(entityType.getCode());
        facilityOwningFieldEnsureService.ensureForEntityTypeCode(entityType.getCode());

        return entityType.getId();
    }

    private Long createDomainEntityType(EntityTypeCreateReqVO reqVO) {
        if (!StringUtils.hasText(reqVO.getBaseEntityTypeCode())) {
            throw new ServiceException(400, "子数据类型必须指定基础数据类型编码");
        }
        if (!StringUtils.hasText(reqVO.getDomain())) {
            throw new ServiceException(400, "子数据类型必须指定业务域标识");
        }
        if (entityTypeMapper.existsByCode(reqVO.getCode())) {
            throw new ServiceException(400, "业务类型编码已存在");
        }

        String baseCode = reqVO.getBaseEntityTypeCode().trim();
        String domain = EntityTypeScopeContext.normalizeDomain(reqVO.getDomain());
        EntityTypeDO baseType = requireNativeBaseEntityType(baseCode, "子数据类型");
        if (existsDomainEntry(baseCode, domain, null)) {
            throw new ServiceException(400, "该基础类型下业务域已存在：" + domain);
        }

        EntityTypeDO entityType = new EntityTypeDO();
        copyBaseFields(entityType, reqVO);
        entityType.setTypeLevel(EntityTypeDO.TYPE_LEVEL_USER);
        entityType.setParentId(null);
        entityType.setEntryKind(EntityTypeDO.ENTRY_KIND_DOMAIN);
        entityType.setBaseEntityTypeCode(baseCode);
        entityType.setDomain(domain);
        inheritStorageFromBase(entityType, baseType);
        ensureEntityTypeGroupRegistered(entityType.getGroupName());
        entityTypeMapper.insert(entityType);

        entityTypeCategoryBootstrapService.ensureForEntityTypeCode(entityType.getCode());
        entityTypeOrchestrationBootstrapService.ensureForEntityTypeCode(entityType.getCode());

        return entityType.getId();
    }

    /**
     * 使用已有数据（REUSE）：复用基础类型存储，不建新表、不打业务域、不靠成员圈选；
     * 本入口可读写底座同表，差异在目录名与默认布局/工作台。
     */
    private Long createReuseEntityType(EntityTypeCreateReqVO reqVO) {
        if (!StringUtils.hasText(reqVO.getBaseEntityTypeCode())) {
            throw new ServiceException(400, "使用已有数据必须指定基础数据类型编码");
        }
        if (entityTypeMapper.existsByCode(reqVO.getCode())) {
            throw new ServiceException(400, "业务类型编码已存在");
        }

        String baseCode = reqVO.getBaseEntityTypeCode().trim();
        EntityTypeDO baseType = requireNativeBaseEntityType(baseCode, "使用已有数据");

        EntityTypeDO entityType = new EntityTypeDO();
        copyBaseFields(entityType, reqVO);
        entityType.setTypeLevel(EntityTypeDO.TYPE_LEVEL_USER);
        entityType.setParentId(null);
        entityType.setEntryKind(EntityTypeDO.ENTRY_KIND_REUSE);
        entityType.setBaseEntityTypeCode(baseCode);
        entityType.setDomain(null);
        inheritStorageFromBase(entityType, baseType);
        ensureEntityTypeGroupRegistered(entityType.getGroupName());
        entityTypeMapper.insert(entityType);

        entityTypeCategoryBootstrapService.ensureForEntityTypeCode(entityType.getCode());
        entityTypeOrchestrationBootstrapService.ensureForEntityTypeCode(entityType.getCode());

        return entityType.getId();
    }

    /**
     * 划分数据（SCOPE）：复用基础类型存储，不建新表、不带业务域；
     * 成员关系后续按实体级加入/移出，不走旧 CATEGORY（分类即实体）管线。
     */
    private Long createScopeEntityType(EntityTypeCreateReqVO reqVO) {
        if (!StringUtils.hasText(reqVO.getBaseEntityTypeCode())) {
            throw new ServiceException(400, "划分数据必须指定基础数据类型编码");
        }
        if (entityTypeMapper.existsByCode(reqVO.getCode())) {
            throw new ServiceException(400, "业务类型编码已存在");
        }

        String baseCode = reqVO.getBaseEntityTypeCode().trim();
        EntityTypeDO baseType = requireNativeBaseEntityType(baseCode, "划分数据");

        EntityTypeDO entityType = new EntityTypeDO();
        copyBaseFields(entityType, reqVO);
        entityType.setTypeLevel(EntityTypeDO.TYPE_LEVEL_USER);
        entityType.setParentId(null);
        entityType.setEntryKind(EntityTypeDO.ENTRY_KIND_SCOPE);
        entityType.setBaseEntityTypeCode(baseCode);
        entityType.setDomain(null);
        inheritStorageFromBase(entityType, baseType);
        ensureEntityTypeGroupRegistered(entityType.getGroupName());
        entityTypeMapper.insert(entityType);

        entityTypeCategoryBootstrapService.ensureForEntityTypeCode(entityType.getCode());
        entityTypeOrchestrationBootstrapService.ensureForEntityTypeCode(entityType.getCode());

        return entityType.getId();
    }

    /**
     * 分类绑定实体（CATEGORY）：自有存储；自动同名高级分类 + 默认「分类|详情」布局。
     * 不要求基础类型（与 SCOPE 划分成员入口不同）。
     */
    private Long createCategoryEntityType(EntityTypeCreateReqVO reqVO) {
        if (entityTypeMapper.existsByCode(reqVO.getCode())) {
            throw new ServiceException(400, "业务类型编码已存在");
        }
        EntityTypeDO entityType = new EntityTypeDO();
        copyBaseFields(entityType, reqVO);
        entityType.setTypeLevel(EntityTypeDO.TYPE_LEVEL_USER);
        entityType.setParentId(null);
        entityType.setEntryKind(EntityTypeDO.ENTRY_KIND_CATEGORY);
        entityType.setBaseEntityTypeCode(null);
        entityType.setDomain(null);
        ensureEntityTypeGroupRegistered(entityType.getGroupName());
        entityTypeMapper.insert(entityType);

        createRelationFieldForEntityType(entityType);

        StorageTypeEnum storageType = StorageTypeEnum.getByCode(entityType.getStorageType());
        if (storageType != null && storageType.isDedicated()) {
            String tableName = cn.cheers.x.module.dynamicbusiness.framework.tenant.TenantPhysicalTableNames
                    .entityPhysicalTable(entityType.getCode());
            entityType.setDedicatedTableName(tableName);
            entityTypeMapper.updateById(entityType);
            dynamicTableService.createDynamicTableForEntityType(
                    entityType.getCode(),
                    tableName,
                    entityType.getName(),
                    PhysicalColumnMappingUtils.parseMapping(entityType.getPhysicalColumnMapping())
            );
        }

        ensureTenantAssociationTablesQuietly();
        entityTypeCategoryBootstrapService.ensureForEntityTypeCode(entityType.getCode());
        entityTypeOrchestrationBootstrapService.ensureForEntityTypeCode(entityType.getCode());
        facilityOwningFieldEnsureService.ensureForEntityTypeCode(entityType.getCode());

        return entityType.getId();
    }

    private EntityTypeDO requireNativeBaseEntityType(String baseCode, String productKindLabel) {
        EntityTypeDO baseType = entityTypeMapper.selectByCode(baseCode);
        if (baseType == null) {
            throw new ServiceException(404, "基础数据类型不存在：" + baseCode);
        }
        EntityTypeEntryKindEnum baseKind = EntityTypeEntryKindEnum.fromCode(baseType.getEntryKind());
        if (baseKind != EntityTypeEntryKindEnum.NATIVE) {
            throw new ServiceException(400, productKindLabel + "只能基于「新建数据」类型创建，请选择独立数据");
        }
        return baseType;
    }

    private void inheritStorageFromBase(EntityTypeDO entityType, EntityTypeDO baseType) {
        entityType.setStorageType(baseType.getStorageType());
        // 子类型复用基础类型存储，但物理表必须是「当前租户」后缀，禁止抄到其它租户的 _t{id}
        StorageTypeEnum inheritedStorage = StorageTypeEnum.getByCode(baseType.getStorageType());
        if (inheritedStorage != null && inheritedStorage.isDedicated()) {
            String baseCode = StringUtils.hasText(baseType.getCode()) ? baseType.getCode() : entityType.getBaseEntityTypeCode();
            entityType.setDedicatedTableName(
                    cn.cheers.x.module.dynamicbusiness.framework.tenant.TenantPhysicalTableNames
                            .entityPhysicalTable(baseCode));
        } else {
            entityType.setDedicatedTableName(baseType.getDedicatedTableName());
        }
        entityType.setPhysicalColumnMapping(baseType.getPhysicalColumnMapping());
        entityType.setEnableRuleEngine(baseType.getEnableRuleEngine());
    }

    private void ensureTenantAssociationTablesQuietly() {
        try {
            tenantAssociationTableService.ensureCurrentTenantAssociationTables();
        } catch (Exception e) {
            log.warn("[tenant-table] 创建类型后补齐关联表失败: {}", e.getMessage());
        }
    }

    private boolean existsDomainEntry(String baseEntityTypeCode, String domain, Long excludeId) {
        LambdaQueryWrapperX<EntityTypeDO> query = new LambdaQueryWrapperX<EntityTypeDO>()
                .eq(EntityTypeDO::getEntryKind, EntityTypeDO.ENTRY_KIND_DOMAIN)
                .eq(EntityTypeDO::getBaseEntityTypeCode, baseEntityTypeCode)
                .eq(EntityTypeDO::getDomain, domain)
                .eq(EntityTypeDO::getDeleted, false);
        if (excludeId != null) {
            query.ne(EntityTypeDO::getId, excludeId);
        }
        return entityTypeMapper.selectCount(query) > 0;
    }

    /**
     * 更新业务类型元数据。
     *
     * 适用场景：
     * - 名称/编码/排序/状态调整；
     * - 存储配置变更（不含历史数据迁移）。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(EntityTypeUpdateReqVO reqVO) {
        EntityTypeDO oldEntityType = entityTypeMapper.selectById(reqVO.getId());
        if (oldEntityType == null) {
            throw new ServiceException(404, "业务类型不存在");
        }

        String oldCode = oldEntityType.getCode();

        // 业务类型的增量更新
        EntityTypeDO newEntityType = new EntityTypeDO();
        newEntityType.setId(oldEntityType.getId());
        // 从旧数据中获取基础信息
        newEntityType.setCode(oldEntityType.getCode());
        newEntityType.setName(oldEntityType.getName());
        newEntityType.setSort(oldEntityType.getSort());
        newEntityType.setStatus(oldEntityType.getStatus());
        newEntityType.setDescription(oldEntityType.getDescription());
        newEntityType.setIcon(oldEntityType.getIcon());
        newEntityType.setAlias(oldEntityType.getAlias());
        newEntityType.setAssociationFields(oldEntityType.getAssociationFields());
        newEntityType.setParentId(null);
        newEntityType.setGroupName(oldEntityType.getGroupName());
        newEntityType.setStorageType(oldEntityType.getStorageType());
        newEntityType.setDedicatedTableName(oldEntityType.getDedicatedTableName());
        newEntityType.setPhysicalColumnMapping(oldEntityType.getPhysicalColumnMapping());
        newEntityType.setEnableRuleEngine(oldEntityType.getEnableRuleEngine());
        newEntityType.setWorkScope(oldEntityType.getWorkScope());

        updateBTFromVO(newEntityType, reqVO);

        if (Boolean.TRUE.equals(reqVO.getGroupNameSpecified())) {
            newEntityType.setGroupName(normalizeGroupName(reqVO.getGroupName()));
            ensureEntityTypeGroupRegistered(newEntityType.getGroupName());
        }

        // 验证
        if (!Objects.equals(oldCode, newEntityType.getCode()) && entityTypeMapper.existsByCodeExcludeId(newEntityType.getCode(), newEntityType.getId())) {
            throw new ServiceException(400, "业务类型编码已存在");
        }

        // 持久化保存
        entityTypeMapper.updateById(newEntityType);
        facilityOwningFieldEnsureService.ensureForEntityTypeCode(newEntityType.getCode());
    }

    private void updateBTFromVO(EntityTypeDO entityType, EntityTypeUpdateReqVO reqVO) {
        if (StringUtils.hasText(reqVO.getName())) {
            entityType.setName(reqVO.getName());
        }
        if (StringUtils.hasText(reqVO.getCode())) {
            entityType.setCode(reqVO.getCode());
        }
        if (reqVO.getSort() != null) {
            entityType.setSort(reqVO.getSort());
        }
        if (reqVO.getStatus() != null) {
            entityType.setStatus(reqVO.getStatus());
        }
        if (reqVO.getDescription() != null) {
            entityType.setDescription(StringUtils.hasText(reqVO.getDescription()) ? reqVO.getDescription() : null);
        }
        if (reqVO.getIcon() != null) {
            entityType.setIcon(reqVO.getIcon());
        }
        if (reqVO.getAlias() != null) {
            entityType.setAlias(StringUtils.hasText(reqVO.getAlias()) ? reqVO.getAlias() : null);
        }
        if (reqVO.getAssociationFields() != null) {
            entityType.setAssociationFields(normalizeJsonFieldString(reqVO.getAssociationFields()));
        }
        if (reqVO.getGroupName() != null) {
            entityType.setGroupName(normalizeGroupName(reqVO.getGroupName()));
            ensureEntityTypeGroupRegistered(entityType.getGroupName());
        }
        // Update storage config fields
        if (StringUtils.hasText(reqVO.getStorageType())) {
            entityType.setStorageType(reqVO.getStorageType());
        }
        if (reqVO.getDedicatedTableName() != null) {
            entityType.setDedicatedTableName(reqVO.getDedicatedTableName());
        }
        if (reqVO.getPhysicalColumnMapping() != null) {
            entityType.setPhysicalColumnMapping(normalizeJsonFieldString(reqVO.getPhysicalColumnMapping()));
        }
        if (reqVO.getEnableRuleEngine() != null) {
            entityType.setEnableRuleEngine(reqVO.getEnableRuleEngine());
        }
        if (StringUtils.hasText(reqVO.getModelWorkbenchMode())) {
            String mode = reqVO.getModelWorkbenchMode().trim().toUpperCase();
            if (EntityTypeDO.MODEL_WORKBENCH_SINGLE.equals(mode)
                    || EntityTypeDO.MODEL_WORKBENCH_MULTI.equals(mode)) {
                entityType.setModelWorkbenchMode(mode);
            }
        }
        if (reqVO.getWorkScope() != null) {
            entityType.setWorkScope(resolveWorkScope(reqVO.getWorkScope()));
        }
    }

    /**
     * 删除业务类型。
     *
     * 适用场景：
     * - 清理用户自定义业务类型；
     * - 删除前校验：非系统级、无子业务。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        EntityTypeDO entityType = entityTypeMapper.selectById(id);
        if (entityType == null) {
            throw new ServiceException(404, "业务类型不存在");
        }

        if (EntityTypeDO.TYPE_LEVEL_SYSTEM.equals(entityType.getTypeLevel())) {
            throw new ServiceException(403, "系统级业务类型不可删除");
        }

        entityTypeMapper.deleteById(id);
    }

    /**
     * 按 ID 获取业务类型详情。
     *
     * 适用场景：
     * - 业务类型详情页；
     * - 编辑前回填。
     */
    @Override
    public EntityTypeRespVO get(Long id) {
        EntityTypeDO entityType = entityTypeMapper.selectById(id);
        if (entityType == null) {
            throw new ServiceException(404, "业务类型不存在");
        }
        return convertToVO(entityType);
    }

    /**
     * 查询跨业务类型平铺列表（含启用/停用）。
     *
     * 适用场景：
     * - 管理端业务类型总览；
     * - 需要“父子相邻、先父后子”的平铺顺序输出。
     *
     * 执行步骤：
     * 1) 先构建有序树（同层按 sort asc、id asc）；
     * 2) 再对树执行 DFS 展平；
     * 3) 返回平铺列表（结构平铺，顺序保留层级语义）。
     */
    @Override
    public List<EntityTypeRespVO> listAcrossEntityTypes() {
        // 先构建有序树，再 DFS 展平为平铺列表（顺序与树遍历一致）
        List<EntityTypeRespVO> flattened = new java.util.ArrayList<>();
        flattenDfs(loadOrderedEntityTypeTree(), flattened);
        return flattened;
    }

    /**
     * 兼容旧命名：平铺返回跨业务类型列表。
     *
     * 适用场景：
     * - 历史调用方暂未迁移；
     * - 对外保持向后兼容。
     */
    @Override
    @Deprecated
    public List<EntityTypeRespVO> listAll() {
        return listAcrossEntityTypes();
    }

    /**
     * 查询业务类型简版列表（下拉场景）。
     *
     * 适用场景：
     * - 表单下拉选择业务类型；
     * - 轻量展示（code/name/description）。
     */
    @Override
    public List<EntityTypeSimpleVO> listSimple() {
        return entityTypeMapper.selectAllList().stream()
                .map(this::convertToSimpleVO)
                .toList();
    }

    @Override
    public List<EntityTypeDomainOptionVO> listDomainOptions(String baseEntityTypeCode) {
        if (!StringUtils.hasText(baseEntityTypeCode)) {
            return List.of();
        }
        String base = baseEntityTypeCode.trim();
        List<EntityTypeDO> rows = entityTypeMapper.selectList(new LambdaQueryWrapperX<EntityTypeDO>()
                .eq(EntityTypeDO::getEntryKind, EntityTypeDO.ENTRY_KIND_DOMAIN)
                .eq(EntityTypeDO::getBaseEntityTypeCode, base)
                .eq(EntityTypeDO::getDeleted, false)
                .orderByAsc(EntityTypeDO::getSort)
                .orderByAsc(EntityTypeDO::getId));
        Map<String, EntityTypeDomainOptionVO> byDomain = new java.util.LinkedHashMap<>();
        for (EntityTypeDO row : rows) {
            String domain = EntityTypeScopeContext.normalizeDomain(row.getDomain());
            if (!StringUtils.hasText(domain) || byDomain.containsKey(domain)) {
                continue;
            }
            String name = StringUtils.hasText(row.getName()) ? row.getName().trim() : domain;
            byDomain.put(domain, new EntityTypeDomainOptionVO(domain, name, row.getCode()));
        }
        return List.copyOf(byDomain.values());
    }

    @Override
    public boolean isRegisteredDomain(String baseEntityTypeCode, String domain) {
        String normalized = EntityTypeScopeContext.normalizeDomain(domain);
        if (!StringUtils.hasText(normalized)) {
            return true;
        }
        if (!StringUtils.hasText(baseEntityTypeCode)) {
            return false;
        }
        return existsDomainEntry(baseEntityTypeCode.trim(), normalized, null);
    }

    /**
     * 查询业务类型树。
     *
     * 适用场景：
     * - 左侧业务导航树；
     * - 树形权限/可视化配置入口。
     *
     * 执行步骤：
     * 1) 从 Mapper 拉取未删除业务类型平铺列表（SQL 基础排序）；
     * 2) 在 Service 层按 parentId 组织树结构；
     * 3) 每层子节点按 sort asc、id asc 排序；
     * 4) 返回树结构（children 递归）。
     */
    @Override
    public List<EntityTypeRespVO> listTree() {
        return loadOrderedEntityTypeTree();
    }

    /**
     * 加载并构建“按层级排序”的业务类型树。
     *
     * 规则：同层按 sort asc、id asc，父先于子。
     *
     * 执行步骤：
     * 1) 查询全部业务类型平铺数据；
     * 2) 转为 VO；
     * 3) 递归按 parentId 组装为树；
     * 4) 对每层做稳定排序（sort、id）。
     */
    private List<EntityTypeRespVO> loadOrderedEntityTypeTree() {
        List<EntityTypeRespVO> list = entityTypeMapper.selectAllList().stream()
                .map(this::convertToVO)
                .toList();
        return buildTree(list, null);
    }

    /**
     * 递归构建指定父节点下的子树。
     *
     * 执行步骤：
     * 1) 筛选 parentId 命中的直接子节点；
     * 2) 同层按 sort asc、id asc 排序；
     * 3) 递归填充每个节点 children；
     * 4) 返回当前层结果。
     */
    private List<EntityTypeRespVO> buildTree(List<EntityTypeRespVO> list, Long parentId) {
        return list.stream()
                .filter(vo -> Objects.equals(vo.getParentId(), parentId))
                .sorted(java.util.Comparator
                        .comparing((EntityTypeRespVO vo) -> vo.getSort() == null ? Integer.MAX_VALUE : vo.getSort())
                        .thenComparing(vo -> vo.getId() == null ? Long.MAX_VALUE : vo.getId()))
                .map(vo -> {
                    vo.setChildren(buildTree(list, vo.getId()));
                    return vo;
                })
                .toList();
    }

    private boolean isEntityTypeDescendant(Long nodeId, Long candidateParentId) {
        if (nodeId == null || candidateParentId == null) {
            return false;
        }
        if (Objects.equals(nodeId, candidateParentId)) {
            return true;
        }
        EntityTypeDO current = entityTypeMapper.selectById(candidateParentId);
        while (current != null && current.getParentId() != null) {
            if (Objects.equals(current.getParentId(), nodeId)) {
                return true;
            }
            current = entityTypeMapper.selectById(current.getParentId());
        }
        return false;
    }

    /**
     * 按 DFS（先父后子）将树展平为列表。
     *
     * 说明：
     * - 当调用方需要“平铺结构 + 层级顺序”时使用；
     * - 与 listTree 的区别在于返回结构不同（平铺 vs 树）。
     */
    private void flattenDfs(List<EntityTypeRespVO> nodes, List<EntityTypeRespVO> out) {
        if (nodes == null || nodes.isEmpty()) {
            return;
        }
        for (EntityTypeRespVO node : nodes) {
            out.add(node);
            flattenDfs(node.getChildren(), out);
        }
    }

    /**
     * 校验业务类型编码是否存在。
     *
     * 适用场景：
     * - 前端输入编码即时校验；
     * - 业务联动前的防御性检查。
     */
    @Override
    public boolean checkEntityTypeExists(String entityTypeCode) {
        return StringUtils.hasText(entityTypeCode) && entityTypeMapper.existsByCode(entityTypeCode.trim());
    }

    /**
     * 按业务编码获取详情。
     *
     * 适用场景：
     * - 业务编码直达详情；
     * - 其他服务按 code 读取业务元信息。
     */
    @Override
    public EntityTypeRespVO getByCode(String code) {
        EntityTypeDO entityType = entityTypeMapper.selectByCode(code);
        return entityType != null ? convertToVO(entityType) : null;
    }

    /**
     * 按业务编码返回其子业务树（不包含根节点本身）。
     *
     * 适用场景：
     * - 进入某业务后展示其下属业务入口；
     * - includeChildren 语义的业务码收集前置。
     */
    @Override
    public List<EntityTypeRespVO> listChildrenTreeByCode(String entityTypeCode) {
        if (!StringUtils.hasText(entityTypeCode)) {
            throw new ServiceException(400, "业务类型编码不能为空");
        }
        EntityTypeDO root = entityTypeMapper.selectByCode(entityTypeCode.trim());
        if (root == null) {
            throw new ServiceException(404, "业务类型不存在");
        }

        List<EntityTypeRespVO> all = listAll();
        // 只构建以 root 为起点的子树
        return buildTree(all, root.getId());
    }

    /**
     * 查询某业务的“配置型子业务”列表（门禁关系驱动）。
     *
     * 适用场景：
     * - 主业务配置面板中加载可关联的资源/配置子业务；
     * - 按 relation(source->target) 做精确过滤。
     */
    @Override
    public List<EntityTypeRespVO> listConfigChildrenByCode(String entityTypeCode) {
        if (!StringUtils.hasText(entityTypeCode)) {
            throw new ServiceException(400, "业务类型编码不能为空");
        }
        // 基于 EntityTypeRelationDO 定义的门禁关系,按 sourceCode=主业务、relationType=CONFIG 过滤
        List<EntityTypeRelationDO> relations = entityTypeRelationMapper.selectList(
                new LambdaQueryWrapperX<EntityTypeRelationDO>()
                        .eq(EntityTypeRelationDO::getSourceEntityTypeCode, entityTypeCode.trim())
                        .eq(EntityTypeRelationDO::getDeleted, false)
        );
        if (relations.isEmpty()) {
            return List.of();
        }
        Set<String> targetCodes = relations.stream()
                .map(EntityTypeRelationDO::getTargetEntityTypeCode)
                .filter(StringUtils::hasText)
                .collect(Collectors.toSet());
        if (targetCodes.isEmpty()) {
            return List.of();
        }
        List<EntityTypeDO> children = entityTypeMapper.selectList(
                new LambdaQueryWrapperX<EntityTypeDO>()
                        .inIfPresent(EntityTypeDO::getCode, targetCodes)
                        .eq(EntityTypeDO::getDeleted, false)
        );
        return children.stream().map(this::convertToVO).toList();
    }

    /**
     * 判断业务类型是否采用专用存储。
     *
     * 适用场景：
     * - 决定实体读写走通用表还是专用表；
     * - 动态建表/迁移流程前置判断。
     */
    @Override
    public boolean isDedicatedStorage(String entityTypeCode) {
        EntityTypeDO entityType = entityTypeMapper.selectByCode(entityTypeCode);
        return entityType != null && "DEDICATED".equals(entityType.getStorageType());
    }

    /**
     * 更新业务类型状态。
     *
     * 适用场景：
     * - 管理台启停业务类型；
     * - 灰度下线某业务入口。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, String status) {
        EntityTypeDO entityType = entityTypeMapper.selectById(id);
        if (entityType == null) {
            throw new ServiceException(404, "业务类型不存在");
        }
        entityTypeMapper.updateById(EntityTypeDO.builder()
                .id(id)
                .status(status)
                .build());
    }

    /**
     * 查询系统级业务类型列表。
     *
     * 适用场景：
     * - 系统内置业务管理；
     * - 系统级能力开关控制。
     */
    @Override
    public List<EntityTypeRespVO> listSystemEntityTypes() {
        return listAll().stream()
                .filter(this::isSystemType)
                .toList();
    }

    /**
     * 查询用户自定义业务类型列表。
     *
     * 适用场景：
     * - 用户扩展业务管理；
     * - 与系统内置业务分组展示。
     */
    @Override
    public List<EntityTypeRespVO> listUserEntityTypes() {
        return listAll().stream()
                .filter(bt -> !isSystemType(bt))
                .toList();
    }

    /**
     * 获取某业务类型下的模型列表（单业务）。
     *
     * 适用场景：
     * - 业务类型详情页展示“关联模型”；
     * - 模型选择联动。
     */
    @Override
    public List<ModelRespVO> getModels(String entityTypeCode) {
        return modelService.listModelsByEntityType(entityTypeCode);
    }

    /**
     * 获取模型字段分配结果。
     *
     * 适用场景：
     * - 业务类型配置页查看模型字段绑定；
     * - 字段联调检查。
     */
    @Override
    public List<ModelFieldAssignmentRespVO> getModelFields(Long modelId) {
        return modelFieldAssignmentService.getModelFields(modelId);
    }

    /**
     * 校验自定义字段内容是否合法。
     *
     * 适用场景：
     * - 实体创建/更新前校验 customFields；
     * - 导入数据前预校验。
     */
    @Override
    public void validateCustomFields(Long modelId, Map<String, Object> customFields) {
        customFieldValidationService.validateCustomFields(modelId, customFields);
    }

    @Override
    public Map<String, Object> normalizeAndEncryptCustomFields(Map<String, Object> customFields, Long modelId) {
        return customFieldValidationService.normalizeAndEncryptCustomFields(customFields, modelId);
    }

    @Override
    public Map<String, Object> decryptCustomFields(Map<String, Object> customFields, Long modelId) {
        return customFieldValidationService.decryptCustomFields(customFields, modelId);
    }

    /**
     * 获取业务类型统计信息。
     *
     * 适用场景：
     * - 业务类型看板（模型数/实体数）；
     * - 运维巡检指标聚合。
     */
    @Override
    public Map<String, Object> getEntityTypeStatistics(String entityTypeCode) {
        Map<String, Object> stats = new HashMap<>();

        Long modelCount = modelMapper.selectCountByEntityTypeCode(entityTypeCode);
        stats.put("modelCount", modelCount);

        Long entityCount = entityRepository.countByEntityTypeCode(entityTypeCode);
        stats.put("entityCount", entityCount);

        return stats;
    }

    /**
     * 判断某业务类型是否为系统内置类型。
     *
     * 适用场景：
     * - 删除/编辑权限控制；
     * - 前端按钮显隐控制。
     */
    @Override
    public boolean isSystemEntityType(String entityTypeCode) {
        return listAll().stream()
                .filter(bt -> entityTypeCode.equals(bt.getCode()))
                .anyMatch(this::isSystemType);
    }

    private boolean isSystemType(EntityTypeRespVO entityType) {
        return EntityTypeDO.TYPE_LEVEL_SYSTEM.equals(entityType.getTypeLevel());
    }

    private void copyBaseFields(EntityTypeDO entityType, EntityTypeBaseVO reqVO) {
        entityType.setCode(reqVO.getCode());
        entityType.setName(reqVO.getName());
        entityType.setSort(reqVO.getSort());
        entityType.setStatus(reqVO.getStatus());
        entityType.setDescription(reqVO.getDescription());
        entityType.setIcon(reqVO.getIcon());
        entityType.setAlias(reqVO.getAlias());
        entityType.setAssociationFields(normalizeJsonFieldString(reqVO.getAssociationFields()));
        entityType.setGroupName(normalizeGroupName(reqVO.getGroupName()));
        entityType.setEntryKind(
                StringUtils.hasText(reqVO.getEntryKind())
                        ? EntityTypeEntryKindEnum.fromCode(reqVO.getEntryKind()).getCode()
                        : EntityTypeDO.ENTRY_KIND_NATIVE);
        entityType.setWorkScope(resolveWorkScope(reqVO.getWorkScope()));
        entityType.setBaseEntityTypeCode(
                StringUtils.hasText(reqVO.getBaseEntityTypeCode()) ? reqVO.getBaseEntityTypeCode().trim() : null);
        entityType.setDomain(EntityTypeScopeContext.normalizeDomain(reqVO.getDomain()));
        // Copy storage config fields（仅 DEDICATED；禁止 GENERIC / dynamic_entity）
        entityType.setStorageType(reqVO.getStorageType());
        entityType.setDedicatedTableName(reqVO.getDedicatedTableName());
        entityType.setPhysicalColumnMapping(normalizeJsonFieldString(reqVO.getPhysicalColumnMapping()));
        entityType.setEnableRuleEngine(reqVO.getEnableRuleEngine());
        if (StringUtils.hasText(reqVO.getModelWorkbenchMode())) {
            String mode = reqVO.getModelWorkbenchMode().trim().toUpperCase();
            if (EntityTypeDO.MODEL_WORKBENCH_SINGLE.equals(mode)
                    || EntityTypeDO.MODEL_WORKBENCH_MULTI.equals(mode)) {
                entityType.setModelWorkbenchMode(mode);
            }
        }
        requireDedicatedStorage(entityType);
    }

    /**
     * 解析目录作用域：空 → FACILITY；仅允许 NETWORK / FACILITY；非法值 400。
     */
    private String resolveWorkScope(String raw) {
        if (!StringUtils.hasText(raw)) {
            return EntityTypeDO.WORK_SCOPE_FACILITY;
        }
        String scope = raw.trim().toUpperCase();
        if (EntityTypeDO.WORK_SCOPE_NETWORK.equals(scope)
                || EntityTypeDO.WORK_SCOPE_FACILITY.equals(scope)) {
            return scope;
        }
        throw new ServiceException(400, "目录作用域仅支持 NETWORK 或 FACILITY");
    }

    /** 动态业务仅允许专用表；显式 GENERIC 拒绝，缺省补为 DEDICATED。 */
    private void requireDedicatedStorage(EntityTypeDO entityType) {
        String raw = entityType.getStorageType();
        if ("GENERIC".equalsIgnoreCase(raw)) {
            throw new ServiceException(400, "已废止通用表存储（GENERIC），仅支持专用表 DEDICATED");
        }
        StorageTypeEnum storageType = StorageTypeEnum.getByCode(raw);
        if (storageType == null || !storageType.isDedicated()) {
            entityType.setStorageType(StorageTypeEnum.DEDICATED.getCode());
        }
    }

    private EntityTypeSimpleVO convertToSimpleVO(EntityTypeDO entityType) {
        EntityTypeSimpleVO vo = new EntityTypeSimpleVO();
        vo.setCode(entityType.getCode());
        vo.setName(entityType.getName());
        vo.setDescription(entityType.getDescription());
        return vo;
    }

    private EntityTypeRespVO convertToVO(EntityTypeDO entityType) {
        EntityTypeRespVO vo = new EntityTypeRespVO();
        vo.setId(entityType.getId());
        vo.setName(entityType.getName());
        vo.setCode(entityType.getCode());
        vo.setSort(entityType.getSort());
        vo.setStatus(entityType.getStatus());
        vo.setDescription(entityType.getDescription());
        vo.setIcon(entityType.getIcon());
        vo.setAlias(entityType.getAlias());
        vo.setAssociationFields(entityType.getAssociationFields());
        vo.setTypeLevel(entityType.getTypeLevel());
        vo.setParentId(entityType.getParentId());
        vo.setGroupName(entityType.getGroupName());
        vo.setEntryKind(entityType.getEntryKind());
        vo.setWorkScope(
                StringUtils.hasText(entityType.getWorkScope())
                        ? entityType.getWorkScope()
                        : EntityTypeDO.WORK_SCOPE_FACILITY);
        vo.setBaseEntityTypeCode(entityType.getBaseEntityTypeCode());
        vo.setDomain(entityType.getDomain());
        vo.setCreateTime(entityType.getCreateTime());
        // Copy storage config fields
        vo.setStorageType(entityType.getStorageType());
        vo.setDedicatedTableName(entityType.getDedicatedTableName());
        vo.setPhysicalColumnMapping(entityType.getPhysicalColumnMapping());
        vo.setEnableRuleEngine(entityType.getEnableRuleEngine());
        vo.setModelWorkbenchMode(
                StringUtils.hasText(entityType.getModelWorkbenchMode())
                        ? entityType.getModelWorkbenchMode()
                        : EntityTypeDO.MODEL_WORKBENCH_MULTI);
        return vo;
    }

    private void createRelationFieldForEntityType(EntityTypeDO entityType) {
        try {
            FieldCreateReqVO fieldReqVO = new FieldCreateReqVO();
            fieldReqVO.setName("关联" + entityType.getName());
            fieldReqVO.setType("REF_Multi");
            fieldReqVO.setDescription("系统自动创建的关联字段模板,用于关联" + entityType.getName());
            fieldReqVO.setSource("SYSTEM");
            fieldReqVO.setStatus(1);
            fieldReqVO.setTargetEntityType(entityType.getCode());
            fieldReqVO.setTargetEntityTypeName(entityType.getName());
            fieldService.createField(fieldReqVO);
        } catch (Exception e) {
            log.warn("[createRelationFieldForEntityType][关联字段模板创建失败: {}, error={}]",
                    entityType.getCode(), e.getMessage());
        }
    }

    private void ensureEntityTypeGroupRegistered(String groupName) {
        String name = normalizeGroupName(groupName);
        if (name == null) {
            return;
        }
        if (groupMapper.selectByTypeAndName(GroupTypeEnum.ENTITY_TYPE.getCode(), name) != null) {
            return;
        }
        groupService.createGroup(GroupTypeEnum.ENTITY_TYPE.getCode(), name, null, null, null, 1, "ETG");
    }

    private String normalizeGroupName(String groupName) {
        if (!StringUtils.hasText(groupName)) {
            return null;
        }
        return groupName.trim();
    }

    private String normalizeJsonFieldString(String json) {
        if (!StringUtils.hasText(json)) {
            return null;
        }
        return JSONUtil.toJsonStr(JSONUtil.parse(json));
    }
}
