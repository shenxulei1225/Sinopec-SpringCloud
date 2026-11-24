package cn.cheers.x.module.dynamicbusiness.service.datapermission;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datapermission.DutyPostRoleDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datapermission.PostAssignmentDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datapermission.DutyPostRoleMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datapermission.PostAssignmentMapper;
import cn.cheers.x.system.api.permission.RoleApi;
import cn.cheers.x.system.api.user.AdminUserApi;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 任职与职务绑定角色的写入。
 *
 * <p>场景速查：</p>
 * <ul>
 *   <li>组织架构页选中一个管理单元、选一个职务，一次往里加一批人 → {@link #createBatch}</li>
 *   <li>改一条任职的起止日期 → {@link #updateValidity}</li>
 *   <li>撤销一条任职 → {@link #delete}</li>
 *   <li>角色页「绑定职务」整份保存 → {@link #replaceRoleDutyPosts}</li>
 * </ul>
 *
 * <p>任职直接生效：功能计算时读任职和职务上的人员，换成职务绑定的角色。
 * 能看哪些单元读人员数据范围，不读任职所在单元。</p>
 *
 * 不负责：读取（见 {@link PostAssignmentQueryService}）；所属部门（只表示审批归哪里）。
 * 禁止：往系统角色关系里写角色；任职换人、换单元、换职务（撤销后重新加）。
 */
@Service
public class PostAssignmentService {

    /** 职务沿用现有的职务目录。 */
    public static final String DUTY_POST_CATALOG = "duty_post";

    @Resource
    private PostAssignmentMapper postAssignmentMapper;
    @Resource
    private DutyPostRoleMapper dutyPostRoleMapper;
    @Resource
    private DataScopeConditionResolver conditionResolver;
    @Resource
    private ManagementUnitCatalogQueryService managementUnitCatalogQueryService;
    @Resource
    private RoleApi roleApi;
    @Resource
    private AdminUserApi adminUserApi;

    /** 一批任职的输入。registryCode 是管理单元所在目录，空则按组织目录。 */
    public record Batch(Long managementUnitId, String registryCode, Long dutyPostId, List<Long> userIds,
                        LocalDate validFrom, LocalDate validTo) {
    }

    /**
     * 往一个管理单元按职务加一批人。
     * 核对：单元在管理单元目录里、它的类型已登记；职务存在；人存在；同一人在同一单元已有这个职务时拒绝。
     *
     * @return 新任职的编号，顺序同 userIds
     */
    @Transactional(rollbackFor = Exception.class)
    public List<Long> createBatch(Batch input) {
        if (input == null) {
            throw new ServiceException(400, "请填写任职");
        }
        String registry = requireManagementUnit(input.registryCode(), input.managementUnitId());
        requireDutyPost(input.dutyPostId());
        validateDates(input.validFrom(), input.validTo());
        Set<Long> userIds = new LinkedHashSet<>(input.userIds() == null ? List.of() : input.userIds());
        userIds.remove(null);
        if (userIds.isEmpty()) {
            throw new ServiceException(400, "请选择人员");
        }
        for (Long userId : userIds) {
            requirePerson(userId);
        }
        List<Long> ids = new ArrayList<>();
        for (Long userId : userIds) {
            if (postAssignmentMapper.existsSame(userId, registry, input.managementUnitId(), input.dutyPostId())) {
                throw new ServiceException(400, "人员 #" + userId + " 已在这个单元任这个职务");
            }
            PostAssignmentDO row = new PostAssignmentDO();
            row.setUserId(userId);
            row.setManagementUnitRegistry(registry);
            row.setManagementUnitId(input.managementUnitId());
            row.setDutyPostId(input.dutyPostId());
            row.setValidFrom(input.validFrom());
            row.setValidTo(input.validTo());
            postAssignmentMapper.insert(row);
            ids.add(row.getId());
        }
        return ids;
    }

    /**
     * 数据资源管理给一个人加职务：先点一个管理单元，再勾这个单元上的职务。
     * 只写任职。数据范围另存。
     */
    public record UnitPostsBatch(Long userId, Long managementUnitId, String registryCode, List<Long> dutyPostIds,
                                 LocalDate validFrom, LocalDate validTo) {
    }

    /**
     * 给一个人在一个组织上任几个职务。每个职务的核对同 {@link #createBatch}，
     * 全部在一个事务里：任何一个职务重复或不合格，一条都不写。
     *
     * @return 新任职的编号，顺序同 dutyPostIds
     */
    @Transactional(rollbackFor = Exception.class)
    public List<Long> createAtUnit(UnitPostsBatch input) {
        if (input == null || input.userId() == null) {
            throw new ServiceException(400, "请选择人员");
        }
        if (input.managementUnitId() == null) {
            throw new ServiceException(400, "请选择组织");
        }
        Set<Long> dutyPostIds = new LinkedHashSet<>(input.dutyPostIds() == null ? List.of() : input.dutyPostIds());
        dutyPostIds.remove(null);
        if (dutyPostIds.isEmpty()) {
            throw new ServiceException(400, "请至少勾一个职务");
        }
        List<Long> ids = new ArrayList<>();
        for (Long dutyPostId : dutyPostIds) {
            ids.addAll(createBatch(new Batch(input.managementUnitId(), input.registryCode(), dutyPostId,
                    List.of(input.userId()), input.validFrom(), input.validTo())));
        }
        return ids;
    }

    /** 改一条任职的起止日期。截止日期含当天，空表示长期。 */
    @Transactional(rollbackFor = Exception.class)
    public void updateValidity(Long id, LocalDate validFrom, LocalDate validTo) {
        PostAssignmentDO existing = requirePost(id);
        validateDates(validFrom, validTo);
        PostAssignmentDO row = new PostAssignmentDO();
        row.setId(existing.getId());
        row.setValidFrom(validFrom);
        row.setValidTo(validTo);
        postAssignmentMapper.updateById(row);
    }

    /** 撤销任职。 */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        requirePost(id);
        postAssignmentMapper.deleteById(id);
    }

    /** 角色页整份保存这个角色绑定的职务。已在任的人下一次请求起就得到或失去这个角色。 */
    @Transactional(rollbackFor = Exception.class)
    public void replaceRoleDutyPosts(Long roleId, List<Long> dutyPostIds) {
        if (roleId == null) {
            throw new ServiceException(400, "请选择角色");
        }
        roleApi.validRoleList(List.of(roleId)).getCheckedData();
        Set<Long> next = new LinkedHashSet<>(dutyPostIds == null ? List.of() : dutyPostIds);
        for (Long dutyPostId : next) {
            requireDutyPost(dutyPostId);
        }
        dutyPostRoleMapper.deleteByRoleId(roleId);
        for (Long dutyPostId : next) {
            DutyPostRoleDO row = new DutyPostRoleDO();
            row.setDutyPostId(dutyPostId);
            row.setRoleId(roleId);
            dutyPostRoleMapper.insert(row);
        }
    }

    /**
     * 职务管理整份保存这个职务绑定的角色。
     * 只改这一条职务，不改这个角色在其它职务上的绑定。
     */
    @Transactional(rollbackFor = Exception.class)
    public void replaceRolesOfDutyPost(Long dutyPostId, List<Long> roleIds) {
        requireDutyPost(dutyPostId);
        Set<Long> next = new LinkedHashSet<>(roleIds == null ? List.of() : roleIds);
        next.remove(null);
        if (!next.isEmpty()) {
            roleApi.validRoleList(new ArrayList<>(next)).getCheckedData();
        }
        dutyPostRoleMapper.deleteByDutyPostId(dutyPostId);
        for (Long roleId : next) {
            DutyPostRoleDO row = new DutyPostRoleDO();
            row.setDutyPostId(dutyPostId);
            row.setRoleId(roleId);
            dutyPostRoleMapper.insert(row);
        }
    }

    /**
     * 单元必须是管理单元目录里的记录，而且它的类型已经登记。
     * 只有数据资源管理「按人员」加任职时调用。职务管理分配人员不走这里。
     */
    /**
     * 管理单元必须是已添加目录里的一条记录。
     * 组织目录还要求这条记录的型号已登记为管理单元类型。场站、管线整本目录都是管理单元，不再逐个型号登记。
     *
     * @return 实际使用的目录编码
     */
    private String requireManagementUnit(String registryCode, Long managementUnitId) {
        if (managementUnitId == null) {
            throw new ServiceException(400, "请选择管理单元");
        }
        String registry = org.springframework.util.StringUtils.hasText(registryCode)
                ? registryCode.trim()
                : managementUnitCatalogQueryService.findManagementUnitRegistryCode();
        if (!org.springframework.util.StringUtils.hasText(registry)) {
            throw new ServiceException(400, "还没有管理单元，不能添加任职");
        }
        if (!managementUnitCatalogQueryService.isRegistryAdded(registry)) {
            throw new ServiceException(400, "这本目录还没有加为管理单元：" + registry);
        }
        if (!conditionResolver.recordExists(registry, managementUnitId)) {
            throw new ServiceException(400, "管理单元不存在：" + registry + " #" + managementUnitId);
        }
        if ("organization".equals(registry)) {
            Long modelId = conditionResolver.selectModelIds(registry, List.of(managementUnitId)).get(managementUnitId);
            if (!managementUnitCatalogQueryService.isModelRegistered(modelId)) {
                throw new ServiceException(400, "这个单元的类型没有登记为管理单元类型，不能在这里任职");
            }
        }
        return registry;
    }

    /**
     * 人必须在用户目录里，或仍是系统用户表里的登录账号。
     * 组织架构页点的人是用户目录记录。老数据可能存的是系统用户编号。
     */
    private void requirePerson(Long userId) {
        if (userId == null) {
            throw new ServiceException(400, "请选择人员");
        }
        if (conditionResolver.recordExists("user_account", userId)) {
            return;
        }
        adminUserApi.validateUserList(Set.of(userId)).getCheckedData();
    }

    private void requireDutyPost(Long dutyPostId) {
        if (dutyPostId == null || !conditionResolver.recordExists(DUTY_POST_CATALOG, dutyPostId)) {
            throw new ServiceException(400, "职务不存在：" + dutyPostId);
        }
    }

    private static void validateDates(LocalDate validFrom, LocalDate validTo) {
        if (validFrom != null && validTo != null && validTo.isBefore(validFrom)) {
            throw new ServiceException(400, "截止日期不能早于起始日期");
        }
    }

    private PostAssignmentDO requirePost(Long id) {
        PostAssignmentDO existing = id == null ? null : postAssignmentMapper.selectById(id);
        if (existing == null) {
            throw new ServiceException(404, "任职不存在");
        }
        return existing;
    }
}
