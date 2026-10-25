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
 * 超级管理员放行，不读本表。
 * 租户管理员和自定义角色都读本表。没有对应授权行，就没有这项操作。
 * 实体接口只认 subject=entity。分类、模型和详情的行只给角色页保存，不在这里放行实体。
 * 禁止用存数编码反推目录注册编码。
 */
@Service
public class CatalogRoleGrantServiceImpl implements CatalogRoleGrantService {

    static final Set<String> OPERATIONS = Set.of("query", "create", "update", "delete");

    /** 角色页横着的四列：分类、模型、实体、详情。 */
    static final Set<String> SUBJECTS = Set.of("category", "model", "entity", "detail");

    static final String ENTITY_SUBJECT = "entity";

    @Resource
    private CatalogRoleGrantMapper catalogRoleGrantMapper;
    @Resource
    private EntityTypeMapper entityTypeMapper;
    @Resource
    private PermissionApi permissionApi;
    @Resource
    private RoleApi roleApi;
    @Resource
    private CatalogStationScopeService catalogStationScopeService;

    @Override
    public List<CatalogRoleGrantItem> listByRoleId(Long roleId) {
        Map<String, LinkedHashSet<String>> grouped = new LinkedHashMap<>();
        Map<String, String> codes = new LinkedHashMap<>();
        Map<String, String> subjects = new LinkedHashMap<>();
        for (CatalogRoleGrantDO row : catalogRoleGrantMapper.selectListByRoleId(roleId)) {
            if (!StringUtils.hasText(row.getRegistryCode()) || !SUBJECTS.contains(row.getSubject())
                    || !OPERATIONS.contains(row.getOperation())) {
                continue;
            }
            String key = row.getRegistryCode().trim() + "\n" + row.getSubject();
            codes.put(key, row.getRegistryCode().trim());
            subjects.put(key, row.getSubject());
            grouped.computeIfAbsent(key, ignored -> new LinkedHashSet<>()).add(row.getOperation());
        }
        List<CatalogRoleGrantItem> items = new ArrayList<>();
        grouped.forEach((key, ops) -> items.add(new CatalogRoleGrantItem(codes.get(key), subjects.get(key), List.copyOf(ops))));
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
            if (!SUBJECTS.contains(item.subject())) {
                throw new ServiceException(400, "对象只允许分类、模型、实体、详情");
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
                row.setSubject(item.subject());
                row.setOperation(operation);
                catalogRoleGrantMapper.insert(row);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRoleCatalogPermission(Long roleId, List<CatalogRoleGrantItem> items,
                                          List<CatalogStationScope> stationScopes) {
        replaceRoleGrants(roleId, items);
        if (stationScopes != null) {
            catalogStationScopeService.replaceRoleScopes(roleId, stationScopes);
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
        Boolean elevated = permissionApi.hasAnyRoles(userId, RoleCodeEnum.SUPER_ADMIN.getCode()).getCheckedData();
        if (Boolean.TRUE.equals(elevated)) {
            return true;
        }
        Collection<Long> roleIds = roleApi.getUserRoleIdListByUserId(userId).getCheckedData();
        return catalogRoleGrantMapper.existsGrant(roleIds, code, ENTITY_SUBJECT, operation);
    }
}
