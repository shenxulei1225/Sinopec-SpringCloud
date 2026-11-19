package cn.cheers.x.module.dynamicbusiness.service.datapermission;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datapermission.DutyPostRoleDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datapermission.PostAssignmentDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.cataloggrant.CatalogRoleGrantMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datapermission.DutyPostRoleMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datapermission.PostAssignmentMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.system.api.user.AdminUserApi;
import cn.cheers.x.system.api.user.dto.AdminUserRespDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 任职与职务绑定角色的读取。
 *
 * <p>场景速查：</p>
 * <ul>
 *   <li>人员详情只读列出这个人的任职，以及每条任职能看哪些目录 → {@link #listViewsByUserId}</li>
 *   <li>组织架构页选中一个单元，列出单元里的任职 → {@link #listViewsByManagementUnitId}</li>
 *   <li>判断目录功能和算数据范围：登录人此刻有效的任职，带各自的角色和单元类型 → {@link #listActivePosts}</li>
 *   <li>页面授权：有效任职带来的全部角色 → {@link #listActivePostRoleIds}</li>
 *   <li>角色页回显这个角色绑定的职务 → {@link #listDutyPostIdsByRoleId}</li>
 * </ul>
 *
 * 不负责：写入（见 {@link PostAssignmentService}）；系统里直接分配给人的角色（调用方自己读系统角色关系）。
 */
@Service
public class PostAssignmentQueryService {

    private static final String ENTITY = "entity";
    private static final String QUERY = "query";

    @Resource
    private PostAssignmentMapper postAssignmentMapper;
    @Resource
    private DutyPostRoleMapper dutyPostRoleMapper;
    @Resource
    private DataScopeConditionResolver conditionResolver;
    @Resource
    private ManagementUnitCatalogQueryService managementUnitCatalogQueryService;
    @Resource
    private CatalogRoleGrantMapper catalogRoleGrantMapper;
    @Resource
    private EntityTypeMapper entityTypeMapper;
    @Resource
    private AdminUserApi adminUserApi;

    /**
     * 一条此刻有效的任职，判断功能和范围用。
     *
     * @param managementUnitId 任职所在的单元
     * @param unitModelId      这个单元的类型（型号编号），判断这本目录有没有登记在它的可管目录里
     * @param roleIds          职务绑定的角色
     */
    public record ActivePost(Long id, Long managementUnitId, Long unitModelId, Set<Long> roleIds) {
    }

    /** 一本目录的编码和名称。 */
    public record CatalogName(String registryCode, String name) {
    }

    /**
     * 一条任职和展示用的名称，读取时补齐，不落库。
     *
     * @param visibleCatalogs 这条任职能看的目录：职务绑定的角色有查看授权，并且单元类型登记了这本目录
     */
    public record PostAssignmentView(PostAssignmentDO row, String dutyPostName, String managementUnitName,
                                     String userName, List<CatalogName> visibleCatalogs) {
    }

    /**
     * 登录人此刻有效的任职。租户还没有管理单元目录、或单元已删除的任职不返回：它们不给任何范围。
     */
    public List<ActivePost> listActivePosts(Long userId) {
        if (userId == null) {
            return List.of();
        }
        List<PostAssignmentDO> rows = postAssignmentMapper.selectActiveListByUserId(userId, LocalDate.now());
        String unitCatalog = managementUnitCatalogQueryService.findManagementUnitRegistryCode();
        if (rows.isEmpty() || unitCatalog == null) {
            return List.of();
        }
        Map<Long, Long> unitModels = conditionResolver.selectModelIds(unitCatalog,
                rows.stream().map(PostAssignmentDO::getManagementUnitId).toList());
        Map<Long, Set<Long>> rolesByDuty = rolesByDutyPost(rows);
        List<ActivePost> posts = new ArrayList<>();
        for (PostAssignmentDO row : rows) {
            Long modelId = unitModels.get(row.getManagementUnitId());
            if (modelId != null) {
                posts.add(new ActivePost(row.getId(), row.getManagementUnitId(), modelId,
                        rolesByDuty.getOrDefault(row.getDutyPostId(), Set.of())));
            }
        }
        return posts;
    }

    /** 有效任职带来的全部角色。页面授权用：页面不属于任何目录，和单元无关。 */
    public Set<Long> listActivePostRoleIds(Long userId) {
        if (userId == null) {
            return Set.of();
        }
        Set<Long> roleIds = new LinkedHashSet<>();
        rolesByDutyPost(postAssignmentMapper.selectActiveListByUserId(userId, LocalDate.now()))
                .values().forEach(roleIds::addAll);
        return roleIds;
    }

    /** 人员详情里列出这个人全部任职，含已结束的。 */
    public List<PostAssignmentView> listViewsByUserId(Long userId) {
        if (userId == null) {
            return List.of();
        }
        return toViews(postAssignmentMapper.selectListByUserId(userId), true);
    }

    /** 组织架构页：这个单元里的全部任职，含已结束的，按职务排。 */
    public List<PostAssignmentView> listViewsByManagementUnitId(Long managementUnitId) {
        if (managementUnitId == null) {
            return List.of();
        }
        return toViews(postAssignmentMapper.selectListByManagementUnitId(managementUnitId), false);
    }

    /**
     * 数据资源管理「按范围看」：能看到这个单元的任职，含已结束的。
     * 任职在它本身的，和任职在它上级的都算（任职范围总含下级），按上级在前排。
     * 调用方用每条的单元编号和点中的单元比，分出「本组织」和「上级」。
     */
    public List<PostAssignmentView> listViewsCoveringUnit(Long managementUnitId) {
        if (managementUnitId == null) {
            return List.of();
        }
        String unitCatalog = managementUnitCatalogQueryService.findManagementUnitRegistryCode();
        if (unitCatalog == null) {
            return List.of();
        }
        List<Long> pathIds = conditionResolver.selectPathIds(unitCatalog, managementUnitId);
        List<PostAssignmentDO> rows = new ArrayList<>(postAssignmentMapper.selectListByManagementUnitIds(pathIds));
        rows.sort(Comparator.comparingInt(row -> pathIds.indexOf(row.getManagementUnitId())));
        return toViews(rows, false);
    }

    /**
     * 「改权限」弹窗：这个职务绑定的一个角色，以及绑定这个角色的全部职务。
     * 职务多于一个时，改这个角色会同时改其它职务的权限，弹窗据此提示。
     */
    public record DutyPostRoleBinding(Long roleId, List<DutyPostRef> dutyPosts) {
    }

    /** 一个职务的编号和名称。 */
    public record DutyPostRef(Long id, String name) {
    }

    /** 这个职务绑定的角色，每个角色带上绑定它的全部职务（含这个职务本身）。职务已删除的名称为空。 */
    public List<DutyPostRoleBinding> listRoleBindingsOfDutyPost(Long dutyPostId) {
        if (dutyPostId == null) {
            return List.of();
        }
        List<Long> roleIds = dutyPostRoleMapper.selectListByDutyPostId(dutyPostId).stream()
                .map(DutyPostRoleDO::getRoleId).distinct().toList();
        Map<Long, List<Long>> postsByRole = new HashMap<>();
        List<Map.Entry<String, Long>> refs = new ArrayList<>();
        for (Long roleId : roleIds) {
            List<Long> postIds = dutyPostRoleMapper.selectListByRoleId(roleId).stream()
                    .map(DutyPostRoleDO::getDutyPostId).distinct().toList();
            postsByRole.put(roleId, postIds);
            postIds.forEach(id -> refs.add(Map.entry(PostAssignmentService.DUTY_POST_CATALOG, id)));
        }
        Map<Long, String> names = refs.isEmpty() ? Map.of()
                : conditionResolver.selectNamesByCatalog(refs).getOrDefault(PostAssignmentService.DUTY_POST_CATALOG, Map.of());
        return roleIds.stream().map(roleId -> new DutyPostRoleBinding(roleId, postsByRole.get(roleId).stream()
                .map(id -> new DutyPostRef(id, names.get(id))).toList())).toList();
    }

    /** 某个角色绑定了哪些职务。角色页「绑定职务」回显。 */
    public List<Long> listDutyPostIdsByRoleId(Long roleId) {
        List<Long> ids = new ArrayList<>();
        if (roleId == null) {
            return ids;
        }
        for (DutyPostRoleDO row : dutyPostRoleMapper.selectListByRoleId(roleId)) {
            ids.add(row.getDutyPostId());
        }
        return ids;
    }

    private List<PostAssignmentView> toViews(List<PostAssignmentDO> rows, boolean withCatalogs) {
        if (rows.isEmpty()) {
            return List.of();
        }
        String unitCatalog = managementUnitCatalogQueryService.findManagementUnitRegistryCode();
        List<Map.Entry<String, Long>> refs = new ArrayList<>();
        for (PostAssignmentDO row : rows) {
            refs.add(Map.entry(PostAssignmentService.DUTY_POST_CATALOG, row.getDutyPostId()));
            if (unitCatalog != null) {
                refs.add(Map.entry(unitCatalog, row.getManagementUnitId()));
            }
        }
        Map<String, Map<Long, String>> names = conditionResolver.selectNamesByCatalog(refs);
        Map<Long, String> dutyNames = names.getOrDefault(PostAssignmentService.DUTY_POST_CATALOG, Map.of());
        Map<Long, String> unitNames = unitCatalog == null ? Map.of() : names.getOrDefault(unitCatalog, Map.of());
        Map<Long, String> userNames = userNames(rows);
        Map<Long, Long> unitModels = unitCatalog == null || !withCatalogs ? Map.of()
                : conditionResolver.selectModelIds(unitCatalog, rows.stream().map(PostAssignmentDO::getManagementUnitId).toList());
        Map<Long, Set<String>> catalogsByModel = withCatalogs ? managementUnitCatalogQueryService.loadCatalogsByModelId() : Map.of();
        Map<Long, Set<Long>> rolesByDuty = withCatalogs ? rolesByDutyPost(rows) : Map.of();
        List<PostAssignmentView> views = new ArrayList<>();
        for (PostAssignmentDO row : rows) {
            List<CatalogName> visible = withCatalogs
                    ? visibleCatalogs(catalogsByModel.getOrDefault(unitModels.get(row.getManagementUnitId()), Set.of()),
                    rolesByDuty.getOrDefault(row.getDutyPostId(), Set.of()))
                    : List.of();
            views.add(new PostAssignmentView(row, dutyNames.get(row.getDutyPostId()),
                    unitNames.get(row.getManagementUnitId()), userNames.get(row.getUserId()), visible));
        }
        return views;
    }

    /** 单元类型登记了、并且职务绑定的角色有实体查看授权的目录。 */
    private List<CatalogName> visibleCatalogs(Set<String> registered, Set<Long> roleIds) {
        List<CatalogName> out = new ArrayList<>();
        if (roleIds.isEmpty()) {
            return out;
        }
        for (String code : registered) {
            if (catalogRoleGrantMapper.existsGrant(roleIds, code, ENTITY, QUERY)) {
                EntityTypeDO type = entityTypeMapper.selectByCode(code);
                out.add(new CatalogName(code, type == null ? null : type.getName()));
            }
        }
        return out;
    }

    private Map<Long, Set<Long>> rolesByDutyPost(List<PostAssignmentDO> rows) {
        Map<Long, Set<Long>> byDuty = new HashMap<>();
        if (rows.isEmpty()) {
            return byDuty;
        }
        Set<Long> dutyIds = new LinkedHashSet<>();
        rows.forEach(row -> dutyIds.add(row.getDutyPostId()));
        for (DutyPostRoleDO binding : dutyPostRoleMapper.selectListByDutyPostIds(dutyIds)) {
            byDuty.computeIfAbsent(binding.getDutyPostId(), id -> new LinkedHashSet<>()).add(binding.getRoleId());
        }
        return byDuty;
    }

    /** 人员姓名优先用登录账号昵称。组织架构里的人在用户目录，登录账号里没有时再用目录名称。 */
    private Map<Long, String> userNames(List<PostAssignmentDO> rows) {
        Set<Long> userIds = new LinkedHashSet<>();
        rows.forEach(row -> userIds.add(row.getUserId()));
        Map<Long, String> names = new HashMap<>();
        List<AdminUserRespDTO> users = adminUserApi.getUserList(userIds).getCheckedData();
        if (users != null) {
            for (AdminUserRespDTO user : users) {
                names.put(user.getId(), user.getNickname() != null && !user.getNickname().isBlank()
                        ? user.getNickname() : user.getUsername());
            }
        }
        List<Long> missing = userIds.stream().filter(id -> !names.containsKey(id)).toList();
        if (!missing.isEmpty()) {
            List<Map.Entry<String, Long>> refs = new ArrayList<>();
            missing.forEach(id -> refs.add(Map.entry("user_account", id)));
            names.putAll(conditionResolver.selectNamesByCatalog(refs).getOrDefault("user_account", Map.of()));
        }
        return names;
    }
}
