package cn.cheers.x.module.dynamicbusiness.service.tenantcapability;

import cn.cheers.x.framework.security.core.util.SecurityFrameworkUtils;
import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.pagegrant.PageRoleGrantDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.pagegrant.PageRoleGrantMapper;
import cn.cheers.x.module.dynamicbusiness.framework.entity.EntityTableNameHandler;
import cn.cheers.x.module.dynamicbusiness.service.datapermission.PostAssignmentQueryService;
import cn.cheers.x.module.dynamicbusiness.service.datapermission.UserDataScopeQueryService;
import cn.cheers.x.system.api.permission.RoleApi;
import cn.cheers.x.module.dynamicbusiness.service.tenantmenu.TenantPortalMenuNode;
import cn.cheers.x.module.dynamicbusiness.service.tenantmenu.TenantPortalMenuView;
import jakarta.annotation.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 登录人顶栏和右上角管理单元选择。
 *
 * <p>顶栏不看右上角当前选中的那一条。一项页面出现，要同时满足：</p>
 * <ul>
 *   <li>这个人直接分配的角色，以及担任的职务换成的角色，勾了这一页。</li>
 *   <li>按单元开通的业务，数据范围盖住的单元里至少有一个开通过。</li>
 *   <li>这项业务按管理单元开通时，任职覆盖到的单元里至少有一条开通了这项业务。</li>
 * </ul>
 *
 * <p>维修工程师的角色没有巡检页，这座站即使开通了巡检，顶栏也不出现「巡检任务」。</p>
 * <p>点进某一页之后，右上角只列这次任职覆盖到、并且开通了该页所属业务的那一种管理单元。
 * 场站业务列站，管线业务列管线。不把两种选择同时摆在右上角。</p>
 *
 * <p>不负责：改数据范围；改页面接口的角色判断（仍走页面授权）；菜单管理里的全量已开通页面。</p>
 */
@Service
public class LoginBusinessAccessQueryService {

    private static final List<String> SELECTOR_ORDER = List.of("facility", "pipeline", "pipeline_pipe", "organization");

    @Resource
    private UserDataScopeQueryService userDataScopeQueryService;
    @Resource
    private RoleApi roleApi;
    @Resource
    private PostAssignmentQueryService postAssignmentQueryService;
    @Resource
    private PageRoleGrantMapper pageRoleGrantMapper;
    @Resource
    private EntityTableNameHandler entityTableNameHandler;
    @Resource
    private JdbcTemplate jdbcTemplate;

    /** 右上角的一条可选管理单元。 */
    public record WorkUnit(String registryCode, Long id, String name) {
    }

    /**
     * 数据目录场站列要不要再按业务开通收窄。
     * restrict 为 false：这本目录不按场站开通业务，场站列只保留原来的任职范围。
     * restrict 为 true：只留下 facilityIds 里的站，这些站开通了包含这本目录的业务。
     */
    public record CatalogFacilityEnablement(boolean restrict, List<Long> facilityIds) {
    }

    /**
     * 顶栏要留下的页面。
     * 租户管理员、超级管理员返回 null，表示不裁顶栏。
     * 没有登录人时返回空集，顶栏不出现业务页。
     */
    public Set<String> navigationPageCodes() {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        if (userId == null) {
            return Set.of();
        }
        if (isTenantOperator(userId)) {
            return null;
        }
        Set<Long> roleIds = capabilityRoleIds(userId);
        if (roleIds.isEmpty()) {
            return Set.of();
        }
        Map<Long, Set<String>> pagesByRole = pagesByRoleIds(roleIds);
        Set<String> opened = enabledPackKeys();
        Map<String, Set<Long>> covered = coveredByDataScope(userId);
        Set<String> pages = new LinkedHashSet<>();
        for (Long roleId : roleIds) {
            for (String pageCode : pagesByRole.getOrDefault(roleId, Set.of())) {
                if (pageAllowed(pageCode, covered, opened)) {
                    pages.add(pageCode);
                }
            }
        }
        return pages;
    }

    /**
     * 进入这一页后，右上角可选的管理单元。
     * 只返回一种目录：优先场站，其次管线，最后组织。没有要选的单元时返回空列表。
     */
    public List<WorkUnit> unitsForPage(String pageCode) {
        if (!StringUtils.hasText(pageCode)) {
            return List.of();
        }
        BusinessCapabilityPackDefinition pack = BusinessCapabilityPackRegistry.findByPageCode(pageCode).orElse(null);
        if (pack == null || !pack.requiresUnitEnablement()) {
            return List.of();
        }
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        if (userId == null) {
            return List.of();
        }
        Set<String> opened = enabledPackKeys();
        Map<String, Set<Long>> covered = isTenantOperator(userId)
                ? allEnabledUnits(pack.code(), opened)
                : coveredUnitsForUser(userId, pageCode, opened);
        String catalog = selectorCatalog(pack, covered.keySet());
        if (catalog == null) {
            return List.of();
        }
        return namedUnits(catalog, covered.getOrDefault(catalog, Set.of()));
    }

    /**
     * 打开这本数据目录时，场站列还要去掉没开通这项业务的站。
     * 这本目录不在任何「按场站开通」的业务里时，不额外收窄。
     */
    public CatalogFacilityEnablement facilitiesEnabledForCatalog(String registryCode) {
        if (!StringUtils.hasText(registryCode)) {
            return new CatalogFacilityEnablement(false, List.of());
        }
        String catalog = registryCode.trim();
        List<String> packs = new ArrayList<>();
        for (BusinessCapabilityPackDefinition pack : BusinessCapabilityPackRegistry.all()) {
            if (pack.catalogCodes().contains(catalog)
                    && pack.unitEnablementRegistryCodes().contains("facility")) {
                packs.add(pack.code());
            }
        }
        if (packs.isEmpty()) {
            return new CatalogFacilityEnablement(false, List.of());
        }
        Long tenantId = TenantContextHolder.getTenantId();
        if (tenantId == null) {
            return new CatalogFacilityEnablement(true, List.of());
        }
        List<Object> args = new ArrayList<>();
        args.add(tenantId);
        String sql = """
                SELECT DISTINCT management_unit_id
                FROM dynamicbusiness.management_unit_capability_pack
                WHERE tenant_id = ? AND deleted = false
                  AND management_unit_registry_code = 'facility'
                  AND pack_code IN (
                """ + placeholdersForStrings(packs, args) + ")";
        List<Long> ids = jdbcTemplate.queryForList(sql, Long.class, args.toArray());
        return new CatalogFacilityEnablement(true, ids);
    }

    /** 顶栏树去掉这个人看不到的页面。分组下面一页都不剩时，分组也不留。 */
    public TenantPortalMenuView filterNavigation(TenantPortalMenuView view) {
        Set<String> pages = navigationPageCodes();
        if (pages == null) {
            return view;
        }
        return new TenantPortalMenuView(
                view.businessTenant(),
                view.saved(),
                filterNodes(view.nodes(), pages),
                List.of(),
                view.pageGroups());
    }

    static List<TenantPortalMenuNode> filterNodes(List<TenantPortalMenuNode> nodes, Set<String> pages) {
        List<TenantPortalMenuNode> kept = new ArrayList<>();
        if (nodes == null) {
            return kept;
        }
        for (TenantPortalMenuNode node : nodes) {
            List<TenantPortalMenuNode> children = filterNodes(node.children(), pages);
            boolean group = !StringUtils.hasText(node.pageCode());
            if (group) {
                if (!children.isEmpty()) {
                    kept.add(new TenantPortalMenuNode(node.title(), node.pageCode(), children));
                }
                continue;
            }
            if (pages.contains(node.pageCode().trim())) {
                kept.add(new TenantPortalMenuNode(node.title(), node.pageCode(), children));
            }
        }
        return kept;
    }

    /** 这一页的业务在覆盖到的单元上开通了，页面才能出现。不按单元拆的业务，角色勾了就出现。 */
    private boolean pageAllowed(String pageCode, Map<String, Set<Long>> covered, Set<String> opened) {
        BusinessCapabilityPackDefinition pack = BusinessCapabilityPackRegistry.findByPageCode(pageCode).orElse(null);
        if (pack == null) {
            return false;
        }
        if (!pack.requiresUnitEnablement()) {
            return true;
        }
        for (String catalog : pack.unitEnablementRegistryCodes()) {
            for (Long id : covered.getOrDefault(catalog, Set.of())) {
                if (opened.contains(key(pack.code(), catalog, id))) {
                    return true;
                }
            }
        }
        return false;
    }

    private Map<String, Set<Long>> coveredUnitsForUser(Long userId, String pageCode, Set<String> opened) {
        if (!roleHasPage(userId, pageCode)) {
            return Map.of();
        }
        Map<String, Set<Long>> covered = coveredByDataScope(userId);
        BusinessCapabilityPackDefinition pack = BusinessCapabilityPackRegistry.findByPageCode(pageCode).orElse(null);
        if (pack == null) {
            return Map.of();
        }
        Map<String, Set<Long>> enabled = new LinkedHashMap<>();
        for (Map.Entry<String, Set<Long>> entry : covered.entrySet()) {
            Set<Long> ids = new LinkedHashSet<>();
            for (Long id : entry.getValue()) {
                if (opened.contains(key(pack.code(), entry.getKey(), id))) {
                    ids.add(id);
                }
            }
            if (!ids.isEmpty()) {
                enabled.put(entry.getKey(), ids);
            }
        }
        return enabled;
    }

    private boolean roleHasPage(Long userId, String pageCode) {
        Map<Long, Set<String>> pages = pagesByRoleIds(capabilityRoleIds(userId));
        for (Set<String> codes : pages.values()) {
            if (codes.contains(pageCode)) {
                return true;
            }
        }
        return false;
    }

    /** 数据范围里的单元，含下级。选中组织时，一并带上所属组织落在这些组织上的场站和管线。 */
    private Map<String, Set<Long>> coveredByDataScope(Long userId) {
        Map<String, Set<Long>> covered = new LinkedHashMap<>();
        for (var row : userDataScopeQueryService.listForLoginUser(userId)) {
            if (row.getUnitId() == null || !StringUtils.hasText(row.getRegistryCode())) {
                continue;
            }
            String registry = row.getRegistryCode().trim();
            Set<Long> ids = descendantIds(registry, Set.of(row.getUnitId()));
            covered.computeIfAbsent(registry, key -> new LinkedHashSet<>()).addAll(ids);
            if ("organization".equals(registry)) {
                covered.computeIfAbsent("facility", key -> new LinkedHashSet<>())
                        .addAll(idsByOrganization("facility", ids));
                covered.computeIfAbsent("pipeline", key -> new LinkedHashSet<>())
                        .addAll(idsByOrganization("pipeline", ids));
            }
        }
        return covered;
    }

    private Set<Long> capabilityRoleIds(Long userId) {
        Set<Long> roleIds = new LinkedHashSet<>();
        java.util.Collection<Long> direct = roleApi.getUserRoleIdListByUserId(userId).getCheckedData();
        if (direct != null) {
            roleIds.addAll(direct);
        }
        roleIds.addAll(postAssignmentQueryService.listActivePostRoleIds(userId));
        return roleIds;
    }

    private Map<String, Set<Long>> allEnabledUnits(String packCode, Set<String> opened) {
        Map<String, Set<Long>> units = new LinkedHashMap<>();
        String prefix = packCode + "|";
        for (String token : opened) {
            if (!token.startsWith(prefix)) {
                continue;
            }
            String[] parts = token.split("\\|", 3);
            if (parts.length != 3) {
                continue;
            }
            units.computeIfAbsent(parts[1], key -> new LinkedHashSet<>()).add(Long.valueOf(parts[2]));
        }
        return units;
    }

    private Set<Long> descendantIds(String registryCode, Set<Long> roots) {
        Set<Long> all = new LinkedHashSet<>(roots);
        List<Long> frontier = new ArrayList<>(roots);
        String table = physicalTable(registryCode);
        if (table == null) {
            return all;
        }
        for (int depth = 0; depth < 12 && !frontier.isEmpty(); depth++) {
            List<Object> args = new ArrayList<>();
            String sql = "SELECT id FROM " + table + " WHERE deleted = false AND parent_id IN ("
                    + placeholders(frontier, args) + ")";
            List<Long> children = jdbcTemplate.queryForList(sql, Long.class, args.toArray());
            frontier = new ArrayList<>();
            for (Long child : children) {
                if (child != null && all.add(child)) {
                    frontier.add(child);
                }
            }
        }
        return all;
    }

    private Set<Long> idsByOrganization(String registryCode, Set<Long> orgIds) {
        String table = physicalTable(registryCode);
        if (table == null || orgIds.isEmpty()) {
            return Set.of();
        }
        List<Object> args = new ArrayList<>();
        String sql = "SELECT id FROM " + table + " WHERE deleted = false AND organization_id IN ("
                + placeholders(orgIds, args) + ")";
        return new LinkedHashSet<>(jdbcTemplate.queryForList(sql, Long.class, args.toArray()));
    }

    private List<WorkUnit> namedUnits(String registryCode, Set<Long> ids) {
        String table = physicalTable(registryCode);
        if (table == null || ids.isEmpty()) {
            return List.of();
        }
        List<Object> args = new ArrayList<>();
        String sql = "SELECT id, name FROM " + table + " WHERE deleted = false AND id IN ("
                + placeholders(ids, args) + ") ORDER BY name, id";
        return jdbcTemplate.query(sql, (rs, row) -> new WorkUnit(registryCode, rs.getLong("id"), rs.getString("name")),
                args.toArray());
    }

    private String physicalTable(String registryCode) {
        try {
            String table = entityTableNameHandler.resolvePhysicalTableName(registryCode);
            if (!StringUtils.hasText(table) || table.indexOf('.') >= 0 || !table.matches("[A-Za-z_][A-Za-z0-9_]*")) {
                return null;
            }
            return table;
        } catch (RuntimeException ex) {
            return null;
        }
    }

    private Map<Long, Set<String>> pagesByRoleIds(Set<Long> roleIds) {
        if (roleIds.isEmpty()) {
            return Map.of();
        }
        Map<Long, Set<String>> pages = new LinkedHashMap<>();
        for (PageRoleGrantDO grant : pageRoleGrantMapper.selectListByRoleIds(roleIds)) {
            if (StringUtils.hasText(grant.getPageCode())) {
                pages.computeIfAbsent(grant.getRoleId(), key -> new LinkedHashSet<>()).add(grant.getPageCode().trim());
            }
        }
        return pages;
    }

    private Set<String> enabledPackKeys() {
        Long tenantId = TenantContextHolder.getTenantId();
        if (tenantId == null) {
            return Set.of();
        }
        List<String> rows = jdbcTemplate.query(
                """
                        SELECT pack_code, management_unit_registry_code, management_unit_id
                        FROM dynamicbusiness.management_unit_capability_pack
                        WHERE tenant_id = ? AND deleted = false
                        """,
                (rs, row) -> key(rs.getString(1), rs.getString(2), rs.getLong(3)),
                tenantId);
        return new LinkedHashSet<>(rows);
    }

    private boolean isTenantOperator(Long userId) {
        Integer count = jdbcTemplate.queryForObject(
                """
                        SELECT COUNT(1)
                        FROM public.system_user_role ur
                        JOIN public.system_role r ON r.id = ur.role_id
                        WHERE ur.user_id = ? AND ur.deleted = false AND r.deleted = false
                          AND r.code IN ('super_admin', 'tenant_admin')
                        """,
                Integer.class,
                userId);
        return count != null && count > 0;
    }

    static String selectorCatalog(BusinessCapabilityPackDefinition pack, Collection<String> catalogs) {
        for (String catalog : SELECTOR_ORDER) {
            if (pack.unitEnablementRegistryCodes().contains(catalog) && catalogs.contains(catalog)) {
                return catalog;
            }
        }
        return null;
    }

    private static String key(String packCode, String registryCode, Long id) {
        return packCode + "|" + registryCode + "|" + id;
    }

    private static String placeholdersForStrings(Collection<String> values, List<Object> args) {
        StringBuilder out = new StringBuilder();
        for (String value : values) {
            if (!out.isEmpty()) {
                out.append(", ");
            }
            out.append("?");
            args.add(value);
        }
        return out.toString();
    }

    private static String placeholders(Collection<Long> ids, List<Object> args) {
        StringBuilder out = new StringBuilder();
        for (Long id : ids) {
            if (!out.isEmpty()) {
                out.append(", ");
            }
            out.append("?");
            args.add(id);
        }
        return out.toString();
    }
}
