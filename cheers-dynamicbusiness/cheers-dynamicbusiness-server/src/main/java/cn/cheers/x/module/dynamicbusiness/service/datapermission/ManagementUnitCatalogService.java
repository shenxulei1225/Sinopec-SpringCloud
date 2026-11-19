package cn.cheers.x.module.dynamicbusiness.service.datapermission;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datapermission.ManagementUnitCatalogDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datapermission.PostAssignmentDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datapermission.ManagementUnitCatalogMapper;
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

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 「数据资源管理」页整份保存可管目录：登记哪些管理单元类型，每类能管哪些数据目录。
 *
 * <p>保存前核对，任何一项不满足就拒绝保存，不替它修：</p>
 * <ul>
 *   <li>登记的型号都存在，而且都在同一本目录里。这本目录就是管理单元目录。</li>
 *   <li>这本目录是原生数据类型，记录的上级路径完整（数据范围按上级路径展开下级单元）。</li>
 *   <li>能管的数据目录都存在。</li>
 *   <li>已有任职的单元，它的类型不能被撤掉，管理单元目录也不能换成另一本：否则这些任职就落空。</li>
 * </ul>
 *
 * 删除数据目录时：它是管理单元目录就拒绝删除；否则从各类型能管的目录里去掉它。
 *
 * 不负责：读取和推出管理单元目录（见 {@link ManagementUnitCatalogQueryService}）；归属声明（见 {@link CatalogOwnershipService}）；
 * 给角色勾功能（见目录授权）。
 * 禁止：按具体目录编码或型号名写分支；在启动、查询、打开页面时补登记。
 */
@Service
public class ManagementUnitCatalogService {

    @Resource
    private ManagementUnitCatalogMapper managementUnitCatalogMapper;
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

    /** 一个管理单元类型的登记：型号编号和它能管的数据目录。 */
    public record TypeRegistration(Long modelId, List<String> registryCodes) {
    }

    /** 整份替换。传空列表表示撤掉全部登记，租户就没有管理单元目录了（已有任职时拒绝）。 */
    @Transactional(rollbackFor = Exception.class)
    public void replaceAll(List<TypeRegistration> registrations) {
        Map<Long, Set<String>> next = normalize(registrations);
        String unitCatalog = requireSingleNativeCatalog(next.keySet());
        assertPostsStillCovered(unitCatalog, next.keySet());
        managementUnitCatalogMapper.deleteAllRows();
        next.forEach((modelId, codes) -> {
            for (String code : codes) {
                ManagementUnitCatalogDO row = new ManagementUnitCatalogDO();
                row.setManagementUnitModelId(modelId);
                row.setRegistryCode(code);
                managementUnitCatalogMapper.insert(row);
            }
        });
    }

    /**
     * 删除数据目录时调用。
     * 它是管理单元目录时拒绝删除：任职和全部受约束目录的范围都落在它的记录上，要先在「数据资源管理」撤掉管理单元类型。
     * 否则从各类型能管的目录里去掉它。
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteRegistrationsOfDeletedCatalog(String registryCode) {
        if (!StringUtils.hasText(registryCode)) {
            throw new ServiceException(400, "缺少数据目录注册编码");
        }
        String code = registryCode.trim();
        if (code.equals(managementUnitCatalogQueryService.findManagementUnitRegistryCode())) {
            throw new ServiceException(400, "这本目录是管理单元目录，先在数据资源管理里撤掉它的管理单元类型");
        }
        managementUnitCatalogMapper.deleteByRegistryCode(code);
    }

    private Map<Long, Set<String>> normalize(List<TypeRegistration> registrations) {
        Map<Long, Set<String>> next = new LinkedHashMap<>();
        for (TypeRegistration item : registrations == null ? List.<TypeRegistration>of() : registrations) {
            if (item == null || item.modelId() == null) {
                throw new ServiceException(400, "请选择管理单元类型");
            }
            if (next.containsKey(item.modelId())) {
                throw new ServiceException(400, "同一个管理单元类型只能登记一次：型号 " + item.modelId());
            }
            Set<String> codes = new LinkedHashSet<>();
            for (String raw : item.registryCodes() == null ? List.<String>of() : item.registryCodes()) {
                if (!StringUtils.hasText(raw)) {
                    throw new ServiceException(400, "请选择数据目录");
                }
                String code = raw.trim();
                if (entityTypeMapper.selectByCode(code) == null) {
                    throw new ServiceException(400, "数据目录不存在：" + code);
                }
                codes.add(code);
            }
            next.put(item.modelId(), codes);
        }
        return next;
    }

    /** 登记的型号都在同一本原生目录里，且这本目录上级路径完整。没有登记时返回 null。 */
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

    /** 已有任职的单元必须仍在管理单元目录里，且它的类型仍登记着。 */
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
