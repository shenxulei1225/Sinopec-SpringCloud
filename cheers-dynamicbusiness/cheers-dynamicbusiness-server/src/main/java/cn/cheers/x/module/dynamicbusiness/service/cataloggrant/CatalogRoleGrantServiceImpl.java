package cn.cheers.x.module.dynamicbusiness.service.cataloggrant;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.cataloggrant.CatalogRoleGrantDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.cataloggrant.CatalogRoleGrantMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.service.datapermission.CatalogOwnershipQueryService;
import cn.cheers.x.module.dynamicbusiness.service.datapermission.ManagementUnitCatalogQueryService;
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
 * 判断时角色分两个来源（权威：docs/中台/能力定义/数据权限能力/数据权限能力（权威）.md 第 2.4、2.6 节）：
 * <ul>
 *   <li>受约束的目录（管理单元目录本身，或写了归属声明的目录）：只认任职。逐条看任职，
 *       职务绑定的角色有这项操作，并且任职所在单元的类型登记了这本目录，才放行。直接分配的角色不算。</li>
 *   <li>不受约束的目录：直接分配给人的角色有这项操作就放行；任职带来的角色要单元类型登记了这本目录才算。</li>
 * </ul>
 * 所有角色都读本表，包括租户管理员。没有对应授权行，就没有这项操作。
 * 超级管理员只管租户和业务开通，不处理租户里的具体业务，所以不放行。
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
    private CatalogOwnershipQueryService catalogOwnershipQueryService;
    @Resource
    private ManagementUnitCatalogQueryService managementUnitCatalogQueryService;
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
        if (!catalogOwnershipQueryService.isRestricted(code)) {
            Collection<Long> direct = roleApi.getUserRoleIdListByUserId(userId).getCheckedData();
            if (direct != null && !direct.isEmpty()
                    && catalogRoleGrantMapper.existsGrant(direct, code, subject, operation)) {
                return true;
            }
        }
        return anyPostAllows(userId, code, subject, operation);
    }

    /** 逐条任职：单元类型登记了这本目录，并且职务绑定的角色有这项操作。 */
    private boolean anyPostAllows(Long userId, String code, String subject, String operation) {
        List<PostAssignmentQueryService.ActivePost> posts = postAssignmentQueryService.listActivePosts(userId);
        if (posts.isEmpty()) {
            return false;
        }
        Map<Long, Set<String>> catalogsByModel = managementUnitCatalogQueryService.loadCatalogsByModelId();
        for (PostAssignmentQueryService.ActivePost post : posts) {
            if (!post.roleIds().isEmpty()
                    && catalogsByModel.getOrDefault(post.unitModelId(), Set.of()).contains(code)
                    && catalogRoleGrantMapper.existsGrant(post.roleIds(), code, subject, operation)) {
                return true;
            }
        }
        return false;
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
