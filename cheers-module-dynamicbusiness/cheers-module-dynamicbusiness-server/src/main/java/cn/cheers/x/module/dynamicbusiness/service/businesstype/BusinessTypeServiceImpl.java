package cn.cheers.x.module.dynamicbusiness.service.businesstype;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.controller.admin.businesstype.vo.*;
import cn.cheers.x.module.dynamicbusiness.controller.admin.field.vo.FieldCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelFieldAssignmentRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.businesstype.BusinessTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.businesstype.BusinessTypeRelationDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.businesstype.BusinessTypeMapper;
import cn.cheers.x.module.dynamicbusiness.enums.businesstype.StorageTypeEnum;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.businesstype.BusinessTypeRelationMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.EntityRepository;
import cn.cheers.x.module.dynamicbusiness.service.field.CustomFieldValidationService;
import cn.cheers.x.module.dynamicbusiness.service.field.FieldService;
import cn.cheers.x.module.dynamicbusiness.service.model.ModelFieldAssignmentService;
import cn.cheers.x.module.dynamicbusiness.service.model.ModelService;
import cn.cheers.x.module.dynamicbusiness.service.dynamictable.DynamicTableService;
import cn.cheers.x.module.dynamicbusiness.util.PhysicalColumnMappingUtils;
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
public class BusinessTypeServiceImpl implements BusinessTypeService {

    @Resource
    private BusinessTypeMapper businessTypeMapper;

    @Resource
    private BusinessTypeRelationMapper businessTypeRelationMapper;

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
    public Long create(BusinessTypeCreateReqVO reqVO) {
        // 1. 验证业务是否已经存在
        if (businessTypeMapper.existsByCode(reqVO.getCode())) {
            throw new ServiceException(400, "业务类型编码已存在");
        }
        BusinessTypeDO parentBusinessType = null;
        if (reqVO.getParentId() != null) {
            parentBusinessType = businessTypeMapper.selectById(reqVO.getParentId());
            if (parentBusinessType == null) {
                throw new ServiceException(400, "父级业务类型不存在");
            }
        }

        // 2. 创建业务 (包括存储配置)
        BusinessTypeDO businessType = new BusinessTypeDO();
        copyBaseFields(businessType, reqVO);
        businessType.setTypeLevel(BusinessTypeDO.TYPE_LEVEL_USER); //用户创建的业务
        businessTypeMapper.insert(businessType);


        // 3. 创建业务的关联字段
        createRelationFieldForBusinessType(businessType);

        // 4.为专用存储创建专用表
        StorageTypeEnum storageType = StorageTypeEnum.getByCode(businessType.getStorageType());
        if (storageType != null && storageType.isDedicated()) {
            String tableName = StringUtils.hasText(businessType.getDedicatedTableName())
                    ? businessType.getDedicatedTableName()
                    : "biz_" + businessType.getCode().toLowerCase();
            dynamicTableService.createDynamicTableForBusinessType(
                    businessType.getCode(),
                    tableName,
                    businessType.getName(),
                    PhysicalColumnMappingUtils.parseMapping(businessType.getPhysicalColumnMapping())
            );
        }

        return businessType.getId();
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
    public void update(BusinessTypeUpdateReqVO reqVO) {
        BusinessTypeDO oldBT = businessTypeMapper.selectById(reqVO.getId());
        if (oldBT == null) {
            throw new ServiceException(404, "业务类型不存在");
        }

        String oldCode = oldBT.getCode();

        // 业务类型的增量更新
        BusinessTypeDO newBT = new BusinessTypeDO();
        newBT.setId(oldBT.getId());
        // 从旧数据中获取基础信息
        newBT.setCode(oldBT.getCode());
        newBT.setName(oldBT.getName());
        newBT.setSort(oldBT.getSort());
        newBT.setStatus(oldBT.getStatus());
        newBT.setDescription(oldBT.getDescription());
        newBT.setIcon(oldBT.getIcon());
        newBT.setAlias(oldBT.getAlias());
        newBT.setAssociationFields(oldBT.getAssociationFields());
        newBT.setParentId(oldBT.getParentId());
        newBT.setStorageType(oldBT.getStorageType());
        newBT.setDedicatedTableName(oldBT.getDedicatedTableName());
        newBT.setPhysicalColumnMapping(oldBT.getPhysicalColumnMapping());
        newBT.setEnableRuleEngine(oldBT.getEnableRuleEngine());

        updateBTFromVO(newBT, reqVO);

        // 验证
        if (!Objects.equals(oldCode, newBT.getCode()) && businessTypeMapper.existsByCodeExcludeId(newBT.getCode(), newBT.getId())) {
            throw new ServiceException(400, "业务类型编码已存在");
        }
        if (newBT.getParentId() != null && businessTypeMapper.selectById(newBT.getParentId()) == null) {
            throw new ServiceException(400, "父级业务类型不存在");
        }

        // 持久化保存
        businessTypeMapper.updateById(newBT);
    }

    private void updateBTFromVO(BusinessTypeDO businessType, BusinessTypeUpdateReqVO reqVO) {
        if (StringUtils.hasText(reqVO.getName())) {
            businessType.setName(reqVO.getName());
        }
        if (StringUtils.hasText(reqVO.getCode())) {
            businessType.setCode(reqVO.getCode());
        }
        if (reqVO.getSort() != null) {
            businessType.setSort(reqVO.getSort());
        }
        if (reqVO.getStatus() != null) {
            businessType.setStatus(reqVO.getStatus());
        }
        if (reqVO.getDescription() != null) {
            businessType.setDescription(StringUtils.hasText(reqVO.getDescription()) ? reqVO.getDescription() : null);
        }
        if (reqVO.getIcon() != null) {
            businessType.setIcon(reqVO.getIcon());
        }
        if (reqVO.getAlias() != null) {
            businessType.setAlias(StringUtils.hasText(reqVO.getAlias()) ? reqVO.getAlias() : null);
        }
        if (reqVO.getAssociationFields() != null) {
            businessType.setAssociationFields(reqVO.getAssociationFields());
        }
        if (reqVO.getParentId() != null) {
            businessType.setParentId(reqVO.getParentId());
        }
        // Update storage config fields
        if (StringUtils.hasText(reqVO.getStorageType())) {
            businessType.setStorageType(reqVO.getStorageType());
        }
        if (reqVO.getDedicatedTableName() != null) {
            businessType.setDedicatedTableName(reqVO.getDedicatedTableName());
        }
        if (reqVO.getPhysicalColumnMapping() != null) {
            businessType.setPhysicalColumnMapping(reqVO.getPhysicalColumnMapping());
        }
        if (reqVO.getEnableRuleEngine() != null) {
            businessType.setEnableRuleEngine(reqVO.getEnableRuleEngine());
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
        BusinessTypeDO businessType = businessTypeMapper.selectById(id);
        if (businessType == null) {
            throw new ServiceException(404, "业务类型不存在");
        }

        if (BusinessTypeDO.TYPE_LEVEL_SYSTEM.equals(businessType.getTypeLevel())) {
            throw new ServiceException(403, "系统级业务类型不可删除");
        }

        if (businessTypeMapper.selectCountByParentId(id) > 0) {
            throw new ServiceException(400, "请先删除子业务类型");
        }

        businessTypeMapper.deleteById(id);
    }

    /**
     * 按 ID 获取业务类型详情。
     *
     * 适用场景：
     * - 业务类型详情页；
     * - 编辑前回填。
     */
    @Override
    public BusinessTypeRespVO get(Long id) {
        BusinessTypeDO businessType = businessTypeMapper.selectById(id);
        if (businessType == null) {
            throw new ServiceException(404, "业务类型不存在");
        }
        return convertToVO(businessType);
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
    public List<BusinessTypeRespVO> listAcrossBusinessTypes() {
        // 先构建有序树，再 DFS 展平为平铺列表（顺序与树遍历一致）
        List<BusinessTypeRespVO> flattened = new java.util.ArrayList<>();
        flattenDfs(loadOrderedBusinessTypeTree(), flattened);
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
    public List<BusinessTypeRespVO> listAll() {
        return listAcrossBusinessTypes();
    }

    /**
     * 查询业务类型简版列表（下拉场景）。
     *
     * 适用场景：
     * - 表单下拉选择业务类型；
     * - 轻量展示（code/name/description）。
     */
    @Override
    public List<BusinessTypeSimpleVO> listSimple() {
        return businessTypeMapper.selectAllList().stream()
                .map(this::convertToSimpleVO)
                .toList();
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
    public List<BusinessTypeRespVO> listTree() {
        return loadOrderedBusinessTypeTree();
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
    private List<BusinessTypeRespVO> loadOrderedBusinessTypeTree() {
        List<BusinessTypeRespVO> list = businessTypeMapper.selectAllList().stream()
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
    private List<BusinessTypeRespVO> buildTree(List<BusinessTypeRespVO> list, Long parentId) {
        return list.stream()
                .filter(vo -> Objects.equals(vo.getParentId(), parentId))
                .sorted(java.util.Comparator
                        .comparing((BusinessTypeRespVO vo) -> vo.getSort() == null ? Integer.MAX_VALUE : vo.getSort())
                        .thenComparing(vo -> vo.getId() == null ? Long.MAX_VALUE : vo.getId()))
                .map(vo -> {
                    vo.setChildren(buildTree(list, vo.getId()));
                    return vo;
                })
                .toList();
    }

    /**
     * 按 DFS（先父后子）将树展平为列表。
     *
     * 说明：
     * - 当调用方需要“平铺结构 + 层级顺序”时使用；
     * - 与 listTree 的区别在于返回结构不同（平铺 vs 树）。
     */
    private void flattenDfs(List<BusinessTypeRespVO> nodes, List<BusinessTypeRespVO> out) {
        if (nodes == null || nodes.isEmpty()) {
            return;
        }
        for (BusinessTypeRespVO node : nodes) {
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
    public boolean checkBusinessTypeExists(String businessTypeCode) {
        return StringUtils.hasText(businessTypeCode) && businessTypeMapper.existsByCode(businessTypeCode.trim());
    }

    /**
     * 按业务编码获取详情。
     *
     * 适用场景：
     * - 业务编码直达详情；
     * - 其他服务按 code 读取业务元信息。
     */
    @Override
    public BusinessTypeRespVO getByCode(String code) {
        BusinessTypeDO businessType = businessTypeMapper.selectByCode(code);
        return businessType != null ? convertToVO(businessType) : null;
    }

    /**
     * 按业务编码返回其子业务树（不包含根节点本身）。
     *
     * 适用场景：
     * - 进入某业务后展示其下属业务入口；
     * - includeChildren 语义的业务码收集前置。
     */
    @Override
    public List<BusinessTypeRespVO> listChildrenTreeByCode(String businessTypeCode) {
        if (!StringUtils.hasText(businessTypeCode)) {
            throw new ServiceException(400, "业务类型编码不能为空");
        }
        BusinessTypeDO root = businessTypeMapper.selectByCode(businessTypeCode.trim());
        if (root == null) {
            throw new ServiceException(404, "业务类型不存在");
        }

        List<BusinessTypeRespVO> all = listAll();
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
    public List<BusinessTypeRespVO> listConfigChildrenByCode(String businessTypeCode) {
        if (!StringUtils.hasText(businessTypeCode)) {
            throw new ServiceException(400, "业务类型编码不能为空");
        }
        // 基于 BusinessTypeRelationDO 定义的门禁关系,按 sourceCode=主业务、relationType=CONFIG 过滤
        List<BusinessTypeRelationDO> relations = businessTypeRelationMapper.selectList(
                new LambdaQueryWrapperX<BusinessTypeRelationDO>()
                        .eq(BusinessTypeRelationDO::getSourceBusinessTypeCode, businessTypeCode.trim())
                        .eq(BusinessTypeRelationDO::getDeleted, false)
        );
        if (relations.isEmpty()) {
            return List.of();
        }
        Set<String> targetCodes = relations.stream()
                .map(BusinessTypeRelationDO::getTargetBusinessTypeCode)
                .filter(StringUtils::hasText)
                .collect(Collectors.toSet());
        if (targetCodes.isEmpty()) {
            return List.of();
        }
        List<BusinessTypeDO> children = businessTypeMapper.selectList(
                new LambdaQueryWrapperX<BusinessTypeDO>()
                        .inIfPresent(BusinessTypeDO::getCode, targetCodes)
                        .eq(BusinessTypeDO::getDeleted, false)
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
    public boolean isDedicatedStorage(String businessTypeCode) {
        BusinessTypeDO businessType = businessTypeMapper.selectByCode(businessTypeCode);
        return businessType != null && "DEDICATED".equals(businessType.getStorageType());
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
        BusinessTypeDO businessType = businessTypeMapper.selectById(id);
        if (businessType == null) {
            throw new ServiceException(404, "业务类型不存在");
        }
        businessTypeMapper.updateById(BusinessTypeDO.builder()
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
    public List<BusinessTypeRespVO> listSystemBusinessTypes() {
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
    public List<BusinessTypeRespVO> listUserBusinessTypes() {
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
    public List<ModelRespVO> getModels(String businessTypeCode) {
        return modelService.listModelsByBusinessType(businessTypeCode);
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
    public void validateCustomFields(Long modelId, String customFieldsJson) {
        customFieldValidationService.validateCustomFields(modelId, customFieldsJson);
    }

    /**
     * 规范化并加密自定义字段 JSON。
     *
     * 适用场景：
     * - 入库前统一数据格式与敏感数据处理；
     * - 保持字段序列化一致性。
     */
    @Override
    public String normalizeAndEncryptCustomFields(String customFieldsJson, Long modelId) {
        return customFieldValidationService.normalizeAndEncryptCustomFields(customFieldsJson, modelId);
    }

    /**
     * 解密自定义字段 JSON。
     *
     * 适用场景：
     * - 出参回显；
     * - 导出前展示可读字段值。
     */
    @Override
    public String decryptCustomFields(String customFieldsJson, Long modelId) {
        return customFieldValidationService.decryptCustomFields(customFieldsJson, modelId);
    }

    /**
     * 获取业务类型统计信息。
     *
     * 适用场景：
     * - 业务类型看板（模型数/实体数）；
     * - 运维巡检指标聚合。
     */
    @Override
    public Map<String, Object> getBusinessTypeStatistics(String businessTypeCode) {
        Map<String, Object> stats = new HashMap<>();

        Long modelCount = modelMapper.selectCountByBusinessTypeCode(businessTypeCode);
        stats.put("modelCount", modelCount);

        Long entityCount = entityRepository.countByBusinessTypeCode(businessTypeCode);
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
    public boolean isSystemBusinessType(String businessTypeCode) {
        return listAll().stream()
                .filter(bt -> businessTypeCode.equals(bt.getCode()))
                .anyMatch(this::isSystemType);
    }

    private boolean isSystemType(BusinessTypeRespVO businessType) {
        return BusinessTypeDO.TYPE_LEVEL_SYSTEM.equals(businessType.getTypeLevel());
    }

    private void copyBaseFields(BusinessTypeDO businessType, BusinessTypeBaseVO reqVO) {
        businessType.setCode(reqVO.getCode());
        businessType.setName(reqVO.getName());
        businessType.setSort(reqVO.getSort());
        businessType.setStatus(reqVO.getStatus());
        businessType.setDescription(reqVO.getDescription());
        businessType.setIcon(reqVO.getIcon());
        businessType.setAlias(reqVO.getAlias());
        businessType.setAssociationFields(reqVO.getAssociationFields());
        businessType.setParentId(reqVO.getParentId());
        // Copy storage config fields
        businessType.setStorageType(reqVO.getStorageType());
        businessType.setDedicatedTableName(reqVO.getDedicatedTableName());
        businessType.setPhysicalColumnMapping(reqVO.getPhysicalColumnMapping());
        businessType.setEnableRuleEngine(reqVO.getEnableRuleEngine());
    }

    private BusinessTypeSimpleVO convertToSimpleVO(BusinessTypeDO businessType) {
        BusinessTypeSimpleVO vo = new BusinessTypeSimpleVO();
        vo.setCode(businessType.getCode());
        vo.setName(businessType.getName());
        vo.setDescription(businessType.getDescription());
        return vo;
    }

    private BusinessTypeRespVO convertToVO(BusinessTypeDO businessType) {
        BusinessTypeRespVO vo = new BusinessTypeRespVO();
        vo.setId(businessType.getId());
        vo.setName(businessType.getName());
        vo.setCode(businessType.getCode());
        vo.setSort(businessType.getSort());
        vo.setStatus(businessType.getStatus());
        vo.setDescription(businessType.getDescription());
        vo.setIcon(businessType.getIcon());
        vo.setAlias(businessType.getAlias());
        vo.setAssociationFields(businessType.getAssociationFields());
        vo.setTypeLevel(businessType.getTypeLevel());
        vo.setParentId(businessType.getParentId());
        vo.setCreateTime(businessType.getCreateTime());
        // Copy storage config fields
        vo.setStorageType(businessType.getStorageType());
        vo.setDedicatedTableName(businessType.getDedicatedTableName());
        vo.setPhysicalColumnMapping(businessType.getPhysicalColumnMapping());
        vo.setEnableRuleEngine(businessType.getEnableRuleEngine());
        return vo;
    }

    private void createRelationFieldForBusinessType(BusinessTypeDO businessType) {
        try {
            FieldCreateReqVO fieldReqVO = new FieldCreateReqVO();
            fieldReqVO.setName("关联" + businessType.getName());
            fieldReqVO.setType("REF_Multi");
            fieldReqVO.setDescription("系统自动创建的关联字段模板,用于关联" + businessType.getName());
            fieldReqVO.setSource("SYSTEM");
            fieldReqVO.setStatus(1);
            fieldReqVO.setTargetBusinessType(businessType.getCode());
            fieldReqVO.setTargetBusinessTypeName(businessType.getName());
            fieldService.createField(fieldReqVO);
        } catch (Exception e) {
            log.warn("[createRelationFieldForBusinessType][关联字段模板创建失败: {}, error={}]",
                    businessType.getCode(), e.getMessage());
        }
    }
}
