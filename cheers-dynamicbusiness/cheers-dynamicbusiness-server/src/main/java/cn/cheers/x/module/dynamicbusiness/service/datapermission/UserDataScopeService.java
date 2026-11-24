package cn.cheers.x.module.dynamicbusiness.service.datapermission;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datapermission.UserDataScopeDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datapermission.UserDataScopeMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 整份保存一个人的数据范围。
 * 保存的是他可以访问的管理单元，可以同时选组织、场站和管线。
 * 不写职务，不按部门推断。空列表表示这个人在受约束的目录里看不到记录。
 */
@Service
public class UserDataScopeService {

    @Resource
    private UserDataScopeMapper userDataScopeMapper;
    @Resource
    private ManagementUnitCatalogQueryService managementUnitCatalogQueryService;
    @Resource
    private DataScopeConditionResolver conditionResolver;

    public record UnitRef(String registryCode, Long unitId) {
    }

    @Transactional(rollbackFor = Exception.class)
    public void replace(Long userId, List<UnitRef> units) {
        if (userId == null) {
            throw new ServiceException(400, "请选择人员");
        }
        Set<String> seen = new LinkedHashSet<>();
        List<UnitRef> accepted = new java.util.ArrayList<>();
        for (UnitRef unit : units == null ? List.<UnitRef>of() : units) {
            if (unit == null || unit.unitId() == null || !StringUtils.hasText(unit.registryCode())) {
                throw new ServiceException(400, "请选择管理单元");
            }
            String registry = unit.registryCode().trim();
            if (!managementUnitCatalogQueryService.isRegistryAdded(registry)) {
                throw new ServiceException(400, "这本目录还没有加为管理单元：" + registry);
            }
            if (!conditionResolver.recordExists(registry, unit.unitId())) {
                throw new ServiceException(400, "管理单元不存在：" + registry + " #" + unit.unitId());
            }
            String key = registry + ":" + unit.unitId();
            if (seen.add(key)) {
                accepted.add(new UnitRef(registry, unit.unitId()));
            }
        }
        userDataScopeMapper.deleteByUserId(userId);
        for (UnitRef unit : accepted) {
            UserDataScopeDO row = new UserDataScopeDO();
            row.setUserId(userId);
            row.setRegistryCode(unit.registryCode());
            row.setUnitId(unit.unitId());
            userDataScopeMapper.insert(row);
        }
    }
}
