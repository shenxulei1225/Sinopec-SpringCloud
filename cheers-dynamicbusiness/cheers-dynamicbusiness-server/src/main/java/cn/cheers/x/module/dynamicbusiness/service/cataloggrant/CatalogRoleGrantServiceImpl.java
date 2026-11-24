package cn.cheers.x.module.dynamicbusiness.service.cataloggrant;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.security.core.util.SecurityFrameworkUtils;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.cataloggrant.CatalogRoleGrantDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.cataloggrant.CatalogRoleGrantMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.service.datapermission.PostAssignmentQueryService;
import cn.cheers.x.system.api.permission.RoleApi;
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
 * 功能授权只认角色，不认职务本身，也不认这条任职落在哪个单元、那个单元的可管目录有没有这本目录。
 * 角色有两个来源，合在一起查目录授权表：系统里直接分配给账号的角色（含租户管理员），
 * 以及账号担任的职务换成的角色。管理单元目录同样认直接分配的角色。
 * 没有对应授权行，就没有这项操作。数据范围按这个人单独保存的管理单元算，不在这里判断。
 * 超级管理员不放行业务目录。用户目录除外：租户管理页要按租户列出、新建登录账号，不走管理点数据范围。
 * 判断时对象必须一致：分类的勾选不放行实体，实体的勾选也不放行型号。
 * 禁止用存数编码反推目录注册编码。
 */
@Service
public class CatalogRoleGrantServiceImpl implements CatalogRoleGrantService {

    static final Set<String> OPERATIONS = Set.of("query", "create", "update", "delete");

    /** 角色页横着的四列：分类、模型、实体、详情。 */
    static final Set<String> SUBJECTS = Set.of("category", "model", "entity", "detail");

    @Resource
    private CatalogRoleGrantMapper catalogRoleGrantMapper;
    @Resource
    private EntityTypeMapper entityTypeMapper;
    @Resource
    private PostAssignmentQueryService postAssignmentQueryService;
    @Resource
    private RoleApi roleApi;

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
    public void deleteGrantsOfDeletedCatalog(String registryCode) {
        if (!StringUtils.hasText(registryCode)) {
            throw new ServiceException(400, "缺少数据目录注册编码");
        }
        catalogRoleGrantMapper.deleteByRegistryCode(registryCode.trim());
    }

    @Override
    public boolean allow(Long userId, String registryCode, String subject, String operation) {
        if (userId == null || !StringUtils.hasText(registryCode)
                || !SUBJECTS.contains(subject) || !OPERATIONS.contains(operation)) {
            return false;
        }
        String code = registryCode.trim();
        if (entityTypeMapper.selectByCode(code) == null) {
            return false;
        }
        // 租户管理只按租户读写登录账号。超级管理员没有任职，不能按管理点可管目录拦截用户目录。
        if ("user_account".equals(code) && SecurityFrameworkUtils.isSuperAdmin()) {
            return true;
        }
        Set<Long> roleIds = new LinkedHashSet<>();
        Collection<Long> direct = roleApi.getUserRoleIdListByUserId(userId).getCheckedData();
        if (direct != null) {
            roleIds.addAll(direct);
        }
        // 职务只用来换成角色。换成之后和直接分配的角色一样，只查目录授权表。
        roleIds.addAll(postAssignmentQueryService.listActivePostRoleIds(userId));
        return catalogRoleGrantMapper.existsGrant(roleIds, code, subject, operation);
    }

    @Override
    public CatalogEntityOperations myEntityOperations(Long userId, String registryCode) {
        return new CatalogEntityOperations(
                allow(userId, registryCode, "entity", "query"),
                allow(userId, registryCode, "entity", "create"),
                allow(userId, registryCode, "entity", "update"),
                allow(userId, registryCode, "entity", "delete"));
    }

    @Override
    public boolean allowAnyQuery(Long userId, String registryCode) {
        for (String subject : SUBJECTS) {
            if (allow(userId, registryCode, subject, "query")) {
                return true;
            }
        }
        return false;
    }
}
