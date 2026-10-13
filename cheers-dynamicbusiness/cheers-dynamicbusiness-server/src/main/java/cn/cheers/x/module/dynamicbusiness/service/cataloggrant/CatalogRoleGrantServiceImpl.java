package cn.cheers.x.module.dynamicbusiness.service.cataloggrant;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.cataloggrant.CatalogRoleGrantDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.cataloggrant.CatalogRoleGrantMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.system.api.permission.PermissionApi;
import cn.cheers.x.system.api.permission.RoleApi;
import cn.cheers.x.system.enums.permission.RoleCodeEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 数据目录授权的写入与判断。
 * 超级管理员、租户管理员在目录属于本租户时放行，不读本表。
 * 自定义角色必须有对应授权行。
 * 禁止用存数编码反推目录注册编码。
 */
@Service
public class CatalogRoleGrantServiceImpl implements CatalogRoleGrantService {

    static final Set<String> OPERATIONS = Set.of("query", "create", "update", "delete");

    @Resource
    private CatalogRoleGrantMapper catalogRoleGrantMapper;
    @Resource
    private EntityTypeMapper entityTypeMapper;
    @Resource
    private PermissionApi permissionApi;
    @Resource
    private RoleApi roleApi;

    @Override
    public List<CatalogRoleGrantItem> listByRoleId(Long roleId) {
        Map<String, LinkedHashSet<String>> grouped = new LinkedHashMap<>();
        for (CatalogRoleGrantDO row : catalogRoleGrantMapper.selectListByRoleId(roleId)) {
            if (!StringUtils.hasText(row.getRegistryCode()) || !OPERATIONS.contains(row.getOperation())) {
                continue;
            }
            grouped.computeIfAbsent(row.getRegistryCode(), key -> new LinkedHashSet<>()).add(row.getOperation());
        }
        List<CatalogRoleGrantItem> items = new ArrayList<>();
        grouped.forEach((code, ops) -> items.add(new CatalogRoleGrantItem(code, List.copyOf(ops))));
        return items;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void replaceRoleGrants(Long roleId, List<CatalogRoleGrantItem> items) {
        if (roleId == null) {
            throw new ServiceException(400, "请选择角色");
        }
        catalogRoleGrantMapper.deleteByRoleId(roleId);
        if (items == null) {
            return;
        }
        for (CatalogRoleGrantItem item : items) {
            if (item == null || !StringUtils.hasText(item.registryCode())) {
                throw new ServiceException(400, "请选择数据目录");
            }
            String registryCode = item.registryCode().trim();
            EntityTypeDO catalog = entityTypeMapper.selectByCode(registryCode);
            if (catalog == null) {
                throw new ServiceException(400, "数据目录不存在：" + registryCode);
            }
            Set<String> operations = new LinkedHashSet<>();
            if (item.operations() != null) {
                for (String operation : item.operations()) {
                    if (!OPERATIONS.contains(operation)) {
                        throw new ServiceException(400, "目录操作只允许查看、新增、修改、删除");
                    }
                    operations.add(operation);
                }
            }
            for (String operation : operations) {
                CatalogRoleGrantDO row = new CatalogRoleGrantDO();
                row.setRoleId(roleId);
                row.setRegistryCode(registryCode);
                row.setOperation(operation);
                catalogRoleGrantMapper.insert(row);
            }
        }
    }

    @Override
    public boolean allow(Long userId, String registryCode, String operation) {
        if (userId == null || !StringUtils.hasText(registryCode) || !OPERATIONS.contains(operation)) {
            return false;
        }
        String code = registryCode.trim();
        if (entityTypeMapper.selectByCode(code) == null) {
            return false;
        }
        Boolean elevated = permissionApi.hasAnyRoles(userId,
                RoleCodeEnum.SUPER_ADMIN.getCode(),
                RoleCodeEnum.TENANT_ADMIN.getCode()).getCheckedData();
        if (Boolean.TRUE.equals(elevated)) {
            return true;
        }
        Collection<Long> roleIds = roleApi.getUserRoleIdListByUserId(userId).getCheckedData();
        return catalogRoleGrantMapper.existsGrant(roleIds, code, operation);
    }
}
