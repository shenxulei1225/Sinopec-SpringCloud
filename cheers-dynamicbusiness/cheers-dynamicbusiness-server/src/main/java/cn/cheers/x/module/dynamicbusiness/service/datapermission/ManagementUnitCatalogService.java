package cn.cheers.x.module.dynamicbusiness.service.datapermission;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datapermission.ManagementUnitCatalogDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datapermission.ManagementUnitTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datapermission.PostAssignmentDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datapermission.ManagementUnitRegistryDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datapermission.ManagementUnitCatalogMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datapermission.ManagementUnitRegistryMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datapermission.ManagementUnitTypeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datapermission.PostAssignmentMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.enums.entitytype.EntityTypeEntryKindEnum;
import cn.cheers.x.module.dynamicbusiness.framework.entity.EntityTableNameHandler;
import jakarta.annotation.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 添加管理单元目录；登记管理单元类型；按管理点保存可管目录。
 *
 * <p>不负责读取与推出管理单元目录（见 {@link ManagementUnitCatalogQueryService}）。</p>
 */
@Service
public class ManagementUnitCatalogService {

    @Resource
    private ManagementUnitCatalogMapper managementUnitCatalogMapper;
    @Resource
    private ManagementUnitTypeMapper managementUnitTypeMapper;
    @Resource
    private ManagementUnitRegistryMapper managementUnitRegistryMapper;
    @Resource
    private ModelMapper modelMapper;
    @Resource
    private EntityTypeMapper entityTypeMapper;
    @Resource
    private EntityTableNameHandler entityTableNameHandler;
    @Resource
    private PostAssignmentMapper postAssignmentMapper;
    @Resource
    private DataScopeConditionResolver conditionResolver;
    @Resource
    private JdbcTemplate jdbcTemplate;
    @Resource
    private ManagementUnitCatalogQueryService managementUnitCatalogQueryService;

    /** 兼容旧保存体：只更新已登记型号，忽略 types 里的 registryCodes。 */
    public record TypeRegistration(Long modelId, List<String> registryCodes) {
    }

    @Transactional(rollbackFor = Exception.class)
    public void replaceRegisteredTypes(List<Long> modelIds) {
        Set<Long> next = new LinkedHashSet<>();
        if (modelIds != null) {
            for (Long id : modelIds) {
                if (id != null) {
                    next.add(id);
                }
            }
        }
        String unitCatalog = requireSingleNativeCatalog(next);
        assertPostsStillCovered(unitCatalog, next);
        managementUnitTypeMapper.deleteAllRows();
        for (Long modelId : next) {
            ManagementUnitTypeDO row = new ManagementUnitTypeDO();
            row.setModelId(modelId);
            managementUnitTypeMapper.insert(row);
        }
    }

    /**
     * 整份保存已添加的管理单元目录。顺序就是第二步标签从左到右的顺序。
     * 不检查这些目录谁挂谁。空列表表示全部撤下。
     */
    @Transactional(rollbackFor = Exception.class)
    public void replaceRegistries(List<String> registryCodes) {
        Set<String> next = new LinkedHashSet<>();
        if (registryCodes != null) {
            for (String raw : registryCodes) {
                if (!StringUtils.hasText(raw)) {
                    continue;
                }
                String code = raw.trim();
                if (entityTypeMapper.selectByCode(code) == null) {
                    throw new ServiceException(400, "数据目录不存在：" + code);
                }
                next.add(code);
            }
        }
        managementUnitRegistryMapper.deleteAllRows();
        int sort = 0;
        for (String code : next) {
            ManagementUnitRegistryDO row = new ManagementUnitRegistryDO();
            row.setRegistryCode(code);
            row.setSortOrder(sort++);
            managementUnitRegistryMapper.insert(row);
        }
    }

    /** 旧接口整份保存：只接受管理单元类型登记。 */
    @Transactional(rollbackFor = Exception.class)
    public void replaceAll(List<TypeRegistration> registrations) {
        if (registrations == null) {
            replaceRegisteredTypes(List.of());
            return;
        }
        replaceRegisteredTypes(registrations.stream().map(TypeRegistration::modelId).toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public void replaceCatalogsForUnit(String managementUnitRegistryCode, Long managementUnitId, List<String> catalogCodes) {
        if (!StringUtils.hasText(managementUnitRegistryCode)) {
            throw new ServiceException(400, "缺少管理单元目录");
        }
        if (managementUnitId == null) {
            throw new ServiceException(400, "请选择管理点");
        }
        String unitRegistry = managementUnitRegistryCode.trim();
        assertRegisteredUnitEntity(unitRegistry, managementUnitId);
        Set<String> wanted = new LinkedHashSet<>();
        if (catalogCodes != null) {
            for (String raw : catalogCodes) {
                if (!StringUtils.hasText(raw)) {
                    continue;
                }
                String code = raw.trim();
                if (entityTypeMapper.selectByCode(code) == null) {
                    throw new ServiceException(400, "数据目录不存在：" + code);
                }
                wanted.add(code);
            }
        }
        managementUnitCatalogMapper.deleteByAnchor(unitRegistry, managementUnitId);
        for (String code : wanted) {
            ManagementUnitCatalogDO row = new ManagementUnitCatalogDO();
            row.setManagementUnitRegistryCode(unitRegistry);
            row.setManagementUnitId(managementUnitId);
            row.setRegistryCode(code);
            managementUnitCatalogMapper.insert(row);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteRegistrationsOfDeletedCatalog(String registryCode) {
        if (!StringUtils.hasText(registryCode)) {
            throw new ServiceException(400, "缺少数据目录注册编码");
        }
        String code = registryCode.trim();
        if (managementUnitCatalogQueryService.isRegistryAdded(code)) {
            throw new ServiceException(400, "这本目录已添加为管理单元，请先在管理单元里移除");
        }
        if (code.equals(managementUnitCatalogQueryService.findManagementUnitRegistryCode())) {
            throw new ServiceException(400, "这本目录是管理单元目录，请先撤掉已登记的管理单元类型");
        }
        managementUnitCatalogMapper.deleteByRegistryCode(code);
    }

    private void assertRegisteredUnitEntity(String unitRegistry, Long unitId) {
        if (!managementUnitCatalogQueryService.isRegistryAdded(unitRegistry)) {
            throw new ServiceException(400, "这本目录还没添加为管理单元");
        }
        if (!conditionResolver.recordExists(unitRegistry, unitId)) {
            throw new ServiceException(400, "管理点不存在：" + unitId);
        }
    }

    private String requireSingleNativeCatalog(Set<Long> modelIds) {
        if (modelIds.isEmpty()) {
            return null;
        }
        Set<String> catalogs = new LinkedHashSet<>();
        List<ModelDO> models = modelMapper.selectBatchIds(modelIds);
        if (models.size() != modelIds.size()) {
            throw new ServiceException(400, "管理单元类型不存在，请刷新后重选");
        }
        for (ModelDO model : models) {
            catalogs.add(model.getEntityTypeCode().trim());
        }
        if (catalogs.size() > 1) {
            throw new ServiceException(400, "管理单元类型要选同一本目录里的型号，现在分在：" + catalogs);
        }
        String code = catalogs.iterator().next();
        EntityTypeDO catalog = entityTypeMapper.selectByCode(code);
        if (catalog == null || EntityTypeEntryKindEnum.fromCode(catalog.getEntryKind()) != EntityTypeEntryKindEnum.NATIVE) {
            throw new ServiceException(400, "管理单元目录只能是原生数据类型：" + code);
        }
        String table = entityTableNameHandler.resolvePhysicalTableName(code);
        Long broken = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM " + table + " WHERE deleted = false"
                        + " AND (tree_path IS NULL OR tree_path NOT LIKE '%/' || id || '/')", Long.class);
        if (broken != null && broken > 0) {
            throw new ServiceException(400, "目录 " + code + " 有 " + broken + " 条记录的上级路径不完整，不能作为管理单元目录");
        }
        return code;
    }

    private void assertPostsStillCovered(String unitCatalog, Set<Long> modelIds) {
        Set<Long> unitIds = new LinkedHashSet<>();
        for (PostAssignmentDO post : postAssignmentMapper.selectList()) {
            unitIds.add(post.getManagementUnitId());
        }
        if (unitIds.isEmpty()) {
            return;
        }
        if (unitCatalog == null) {
            throw new ServiceException(400, "还有 " + unitIds.size() + " 个单元上有任职，不能撤掉全部管理单元类型");
        }
        Map<Long, Long> unitModels = conditionResolver.selectModelIds(unitCatalog, unitIds);
        for (Long unitId : unitIds) {
            Long modelId = unitModels.get(unitId);
            if (modelId == null || !modelIds.contains(modelId)) {
                throw new ServiceException(400, "单元 #" + unitId + " 上还有任职，它的类型不能撤掉，请先撤销这些任职");
            }
        }
    }
}