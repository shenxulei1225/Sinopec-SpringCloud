package cn.cheers.x.module.dynamicbusiness.service.tenantmenu;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;
import cn.cheers.x.framework.tenant.core.util.TenantUtils;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.tenantmenu.TenantPortalMenuDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.tenantmenu.TenantPortalMenuMapper;
import cn.cheers.x.module.dynamicbusiness.service.pagegrant.PagePermissionRegistry;
import cn.cheers.x.module.dynamicbusiness.service.tenantcapability.BusinessCapabilityPackDefinition;
import cn.cheers.x.module.dynamicbusiness.service.tenantcapability.BusinessCapabilityPackRegistry;
import cn.cheers.x.module.dynamicbusiness.service.tenantcapability.TenantCapabilityPackService;
import cn.cheers.x.module.dynamicbusiness.service.tenantcapability.TenantPackPageSelection;
import jakarta.annotation.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 本租户顶栏菜单的读写。
 * 权威是 tenant_portal_menu。没有行时，按统一排法现排出菜单，只返回、不插入。已保存的菜单按父子读出，安防下面可以再有分组。
 * 不按角色的查看动作决定出不出现。页面没开通时，这一项不返回，也不删已保存的行。
 */
@Service
public class TenantPortalMenuServiceImpl implements TenantPortalMenuService {

    @Resource
    private TenantPortalMenuMapper tenantPortalMenuMapper;
    @Resource
    private TenantCapabilityPackService tenantCapabilityPackService;
    @Resource
    private JdbcTemplate jdbcTemplate;

    @Override
    public TenantPortalMenuView loadCurrent() {
        Long tenantId = TenantContextHolder.getTenantId();
        if (!isBusinessTenant(tenantId)) {
            return new TenantPortalMenuView(false, false, List.of(), List.of(), List.of());
        }
        List<TenantPortalMenuDO> rows = TenantUtils.execute(
                tenantId, () -> tenantPortalMenuMapper.selectByTenant(tenantId));
        List<TenantPortalMenuPageGroup> groups = pageGroups(tenantId);
        Map<String, String> opened = namesOf(groups);
        if (rows.isEmpty()) {
            return new TenantPortalMenuView(true, false, generate(groups), List.of(), groups);
        }
        List<TenantPortalMenuNode> nodes = toTree(rows, opened.keySet());
        return new TenantPortalMenuView(true, true, nodes, unplaced(opened, nodes), groups);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveCurrent(List<TenantPortalMenuNode> nodes) {
        Long tenantId = TenantContextHolder.getTenantId();
        if (!isBusinessTenant(tenantId)) {
            throw new ServiceException(400, "平台侧不配置业务菜单");
        }
        Map<String, String> opened = openedPages(tenantId);
        List<TenantPortalMenuNode> clean = validate(nodes, opened.keySet());
        TenantUtils.execute(tenantId, () -> {
            tenantPortalMenuMapper.deleteByTenant(tenantId);
            int sort = 0;
            for (TenantPortalMenuNode node : clean) {
                TenantPortalMenuDO parent = insert(tenantId, null, node, sort++);
                int childSort = 0;
                for (TenantPortalMenuNode child : childrenOf(node)) {
                    insert(tenantId, parent.getId(), child, childSort++);
                }
            }
            return null;
        });
    }

    /**
     * 还没保存过时，各租户用同一份排法。只排出这次已经开通的页面。
     * 租户管理员之后拖进分组或拖到分组外，保存的那份才改层级。这里不按页面个数临时改层级。
     */
    static List<TenantPortalMenuNode> generate(List<TenantPortalMenuPageGroup> groups) {
        Map<String, String> opened = new LinkedHashMap<>();
        if (groups != null) {
            for (TenantPortalMenuPageGroup group : groups) {
                if (group == null || group.pages() == null) {
                    continue;
                }
                for (TenantPortalMenuPage page : group.pages()) {
                    if (page == null || page.code() == null || page.code().isBlank()) {
                        continue;
                    }
                    opened.putIfAbsent(page.code().trim(), page.name());
                }
            }
        }
        return TenantPortalMenuTemplate.apply(opened);
    }

    /** 左边的页面按已开通业务分组。顺序跟业务定义一致。只开通目录、没有页面的业务不出现。 */
    private List<TenantPortalMenuPageGroup> pageGroups(Long tenantId) {
        Map<String, List<String>> pagesByPack = new LinkedHashMap<>();
        for (TenantPackPageSelection selection : tenantCapabilityPackService.listOpenedSelections(tenantId)) {
            pagesByPack.put(selection.packCode(), selection.pageCodes() == null ? List.of() : selection.pageCodes());
        }
        List<TenantPortalMenuPageGroup> groups = new ArrayList<>();
        for (BusinessCapabilityPackDefinition pack : BusinessCapabilityPackRegistry.all()) {
            List<String> codes = pagesByPack.get(pack.code());
            if (codes == null || codes.isEmpty()) {
                continue;
            }
            List<TenantPortalMenuPage> pages = new ArrayList<>();
            Set<String> seen = new LinkedHashSet<>();
            for (String pageCode : codes) {
                if (!StringUtils.hasText(pageCode) || !seen.add(pageCode.trim())) {
                    continue;
                }
                String code = pageCode.trim();
                pages.add(new TenantPortalMenuPage(code, PagePermissionRegistry.pageName(code)));
            }
            if (!pages.isEmpty()) {
                groups.add(new TenantPortalMenuPageGroup(pack.code(), pack.name(), pages));
            }
        }
        return groups;
    }

    private static Map<String, String> namesOf(List<TenantPortalMenuPageGroup> groups) {
        Map<String, String> opened = new LinkedHashMap<>();
        for (TenantPortalMenuPageGroup group : groups) {
            for (TenantPortalMenuPage page : group.pages()) {
                opened.putIfAbsent(page.code(), page.name());
            }
        }
        return opened;
    }

    private Map<String, String> openedPages(Long tenantId) {
        Map<String, String> opened = new LinkedHashMap<>();
        for (TenantPackPageSelection selection : tenantCapabilityPackService.listOpenedSelections(tenantId)) {
            if (selection.pageCodes() == null) {
                continue;
            }
            for (String pageCode : selection.pageCodes()) {
                if (!StringUtils.hasText(pageCode)) {
                    continue;
                }
                String code = pageCode.trim();
                opened.putIfAbsent(code, PagePermissionRegistry.pageName(code));
            }
        }
        return opened;
    }

    /** 已关闭的页面留在表里，这次不返回。分组没有还能打开的页面时，这一组不出现。 */
    private List<TenantPortalMenuNode> toTree(List<TenantPortalMenuDO> rows, Set<String> opened) {
        Map<Long, List<TenantPortalMenuDO>> byParent = new LinkedHashMap<>();
        List<TenantPortalMenuDO> roots = new ArrayList<>();
        for (TenantPortalMenuDO row : rows) {
            if (row.getParentId() == null) {
                roots.add(row);
                continue;
            }
            byParent.computeIfAbsent(row.getParentId(), key -> new ArrayList<>()).add(row);
        }
        List<TenantPortalMenuNode> nodes = new ArrayList<>();
        for (TenantPortalMenuDO root : roots) {
            TenantPortalMenuNode node = toNode(root, byParent, opened);
            if (node != null) {
                nodes.add(node);
            }
        }
        return nodes;
    }

    /** 页面只在仍开通时出现。分组只在下面还有页面时出现。 */
    private TenantPortalMenuNode toNode(
            TenantPortalMenuDO row,
            Map<Long, List<TenantPortalMenuDO>> byParent,
            Set<String> opened) {
        String pageCode = blankToNull(row.getPageCode());
        if (pageCode != null) {
            if (!opened.contains(pageCode)) {
                return null;
            }
            return new TenantPortalMenuNode(row.getTitle(), pageCode, List.of());
        }
        List<TenantPortalMenuNode> children = new ArrayList<>();
        for (TenantPortalMenuDO child : byParent.getOrDefault(row.getId(), List.of())) {
            TenantPortalMenuNode node = toNode(child, byParent, opened);
            if (node != null) {
                children.add(node);
            }
        }
        if (children.isEmpty()) {
            return null;
        }
        return new TenantPortalMenuNode(row.getTitle(), null, children);
    }

    private List<TenantPortalMenuPage> unplaced(Map<String, String> opened, List<TenantPortalMenuNode> nodes) {
        Set<String> placed = new LinkedHashSet<>();
        collectPlaced(nodes, placed);
        List<TenantPortalMenuPage> unplaced = new ArrayList<>();
        for (Map.Entry<String, String> entry : opened.entrySet()) {
            if (!placed.contains(entry.getKey())) {
                unplaced.add(new TenantPortalMenuPage(entry.getKey(), entry.getValue()));
            }
        }
        return unplaced;
    }

    private List<TenantPortalMenuNode> validate(List<TenantPortalMenuNode> nodes, Set<String> opened) {
        List<TenantPortalMenuNode> clean = new ArrayList<>();
        Set<String> used = new LinkedHashSet<>();
        if (nodes == null) {
            return clean;
        }
        for (TenantPortalMenuNode node : nodes) {
            String title = requireTitle(node.title());
            String pageCode = blankToNull(node.pageCode());
            List<TenantPortalMenuNode> children = childrenOf(node);
            if (pageCode != null && !children.isEmpty()) {
                throw new ServiceException(400, "挂了页面的一级菜单下面不能再放页面");
            }
            if (pageCode == null && children.isEmpty()) {
                throw new ServiceException(400, "一级分组下面要放页面");
            }
            if (pageCode != null) {
                claim(pageCode, opened, used);
                clean.add(new TenantPortalMenuNode(title, pageCode, List.of()));
                continue;
            }
            List<TenantPortalMenuNode> cleanChildren = new ArrayList<>();
            for (TenantPortalMenuNode child : children) {
                if (!childrenOf(child).isEmpty()) {
                    throw new ServiceException(400, "菜单只有两级");
                }
                String childPage = blankToNull(child.pageCode());
                if (childPage == null) {
                    throw new ServiceException(400, "二级菜单要选择一个页面");
                }
                claim(childPage, opened, used);
                cleanChildren.add(new TenantPortalMenuNode(requireTitle(child.title()), childPage, List.of()));
            }
            clean.add(new TenantPortalMenuNode(title, null, cleanChildren));
        }
        return clean;
    }

    private void claim(String pageCode, Set<String> opened, Set<String> used) {
        if (!opened.contains(pageCode)) {
            throw new ServiceException(400, "这个页面还没有开通");
        }
        if (!used.add(pageCode)) {
            throw new ServiceException(400, "这个页面已经在菜单里");
        }
    }

    private String requireTitle(String title) {
        if (!StringUtils.hasText(title)) {
            throw new ServiceException(400, "菜单名称不能为空");
        }
        String trimmed = title.trim();
        if (trimmed.length() > 32) {
            throw new ServiceException(400, "菜单名称不能超过 32 个字");
        }
        return trimmed;
    }

    private TenantPortalMenuDO insert(Long tenantId, Long parentId, TenantPortalMenuNode node, int sort) {
        TenantPortalMenuDO row = new TenantPortalMenuDO();
        row.setTenantId(tenantId);
        row.setParentId(parentId);
        row.setTitle(node.title());
        row.setPageCode(blankToNull(node.pageCode()));
        row.setSort(sort);
        row.setDeleted(false);
        tenantPortalMenuMapper.insert(row);
        return row;
    }

    private static void collectPlaced(List<TenantPortalMenuNode> nodes, Set<String> placed) {
        for (TenantPortalMenuNode node : nodes) {
            if (node == null) {
                continue;
            }
            if (StringUtils.hasText(node.pageCode())) {
                placed.add(node.pageCode().trim());
            }
            collectPlaced(childrenOf(node), placed);
        }
    }

    private static List<TenantPortalMenuNode> childrenOf(TenantPortalMenuNode node) {
        return node.children() == null ? List.of() : node.children();
    }

    private static String blankToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    /** 与开通里对平台租户的判断同一条：scope 为 PLATFORM 的不是业务租户。 */
    private boolean isBusinessTenant(Long tenantId) {
        if (tenantId == null || tenantId <= 0) {
            return false;
        }
        String scope = jdbcTemplate.query(
                "SELECT scope FROM public.system_tenant WHERE id = ? AND deleted = false",
                rs -> rs.next() ? rs.getString(1) : null,
                tenantId);
        return scope != null && !"PLATFORM".equalsIgnoreCase(scope.trim());
    }
}
