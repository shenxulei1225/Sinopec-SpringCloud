package cn.cheers.x.module.dynamicbusiness.service.pagegrant;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.pagegrant.PageRoleGrantDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.pagegrant.PageRoleGrantMapper;
import cn.cheers.x.module.dynamicbusiness.service.cataloggrant.CatalogRoleGrantItem;
import cn.cheers.x.module.dynamicbusiness.service.cataloggrant.CatalogRoleGrantService;
import jakarta.annotation.Resource;
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
 * 页面授权的写入与登记读取。
 * 只接受 {@link PagePermissionRegistry} 已列出的页面和动作。没有对应行，角色页上这项就是未勾选。
 * 保存页面勾选时，按 {@link PageCatalogBinding} 把用到的数据目录写成同一笔目录授权。
 * 本租户没有的目录跳过，不拦这次保存。不在这里放行业务接口。
 */
@Service
public class PageRoleGrantServiceImpl implements PageRoleGrantService {

    @Resource
    private PageRoleGrantMapper pageRoleGrantMapper;
    @Resource
    private CatalogRoleGrantService catalogRoleGrantService;
    @Resource
    private EntityTypeMapper entityTypeMapper;

    @Override
    public List<PagePermissionRegistry.GroupDef> listGroups() {
        return PagePermissionRegistry.groups();
    }

    @Override
    public List<PageRoleGrantItem> listByRoleId(Long roleId) {
        Map<String, LinkedHashSet<String>> grouped = new LinkedHashMap<>();
        for (PageRoleGrantDO row : pageRoleGrantMapper.selectListByRoleId(roleId)) {
            Set<String> allowed = PagePermissionRegistry.actionsOf(row.getPageCode());
            if (allowed.isEmpty() || !allowed.contains(row.getAction())) {
                continue;
            }
            grouped.computeIfAbsent(row.getPageCode(), key -> new LinkedHashSet<>()).add(row.getAction());
        }
        List<PageRoleGrantItem> items = new ArrayList<>();
        grouped.forEach((pageCode, actions) -> items.add(new PageRoleGrantItem(pageCode, List.copyOf(actions))));
        return items;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void replaceRoleGrants(Long roleId, List<PageRoleGrantItem> items) {
        if (roleId == null) {
            throw new ServiceException(400, "请选择角色");
        }
        pageRoleGrantMapper.deleteByRoleId(roleId);
        List<PageRoleGrantItem> saved = new ArrayList<>();
        if (items != null) {
            saved.addAll(insertPages(roleId, items));
        }
        catalogRoleGrantService.replaceRoleGrants(roleId, catalogsPresent(PageCatalogBinding.derive(saved)));
    }

    private List<PageRoleGrantItem> insertPages(Long roleId, List<PageRoleGrantItem> items) {
        List<PageRoleGrantItem> saved = new ArrayList<>();
        for (PageRoleGrantItem item : items) {
            if (item == null || !StringUtils.hasText(item.pageCode())) {
                throw new ServiceException(400, "请选择页面");
            }
            String pageCode = item.pageCode().trim();
            Set<String> allowed = PagePermissionRegistry.actionsOf(pageCode);
            if (allowed.isEmpty()) {
                throw new ServiceException(400, "页面不在角色页分组里：" + pageCode);
            }
            Set<String> actions = new LinkedHashSet<>();
            if (item.actions() != null) {
                for (String action : item.actions()) {
                    if (!allowed.contains(action)) {
                        throw new ServiceException(400, "这个页面没有这项勾选");
                    }
                    actions.add(action);
                }
            }
            for (String action : actions) {
                PageRoleGrantDO row = new PageRoleGrantDO();
                row.setRoleId(roleId);
                row.setPageCode(pageCode);
                row.setAction(action);
                pageRoleGrantMapper.insert(row);
            }
            if (!actions.isEmpty()) {
                saved.add(new PageRoleGrantItem(pageCode, List.copyOf(actions)));
            }
        }
        return saved;
    }

    /** 本租户没有的目录不写入。目录授权表要求目录真实存在。 */
    private List<CatalogRoleGrantItem> catalogsPresent(List<CatalogRoleGrantItem> derived) {
        List<CatalogRoleGrantItem> present = new ArrayList<>();
        Set<String> found = new LinkedHashSet<>();
        Set<String> missing = new LinkedHashSet<>();
        for (CatalogRoleGrantItem item : derived) {
            String code = item.registryCode();
            if (missing.contains(code)) {
                continue;
            }
            if (!found.contains(code)) {
                EntityTypeDO catalog = entityTypeMapper.selectByCode(code);
                if (catalog == null) {
                    missing.add(code);
                    continue;
                }
                found.add(code);
            }
            present.add(item);
        }
        return present;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void mergeViewActions(Long roleId, List<String> pageCodes) {
        if (roleId == null) {
            throw new ServiceException(400, "请选择角色");
        }
        if (pageCodes == null || pageCodes.isEmpty()) {
            return;
        }
        Map<String, LinkedHashSet<String>> grouped = new LinkedHashMap<>();
        for (PageRoleGrantItem existing : listByRoleId(roleId)) {
            grouped.put(existing.pageCode(), new LinkedHashSet<>(existing.actions()));
        }
        for (String raw : pageCodes) {
            if (!StringUtils.hasText(raw)) {
                continue;
            }
            String pageCode = raw.trim();
            Set<String> allowed = PagePermissionRegistry.actionsOf(pageCode);
            if (allowed.isEmpty() || !allowed.contains("view")) {
                continue;
            }
            grouped.computeIfAbsent(pageCode, key -> new LinkedHashSet<>()).add("view");
        }
        List<PageRoleGrantItem> merged = new ArrayList<>();
        grouped.forEach((pageCode, actions) -> merged.add(new PageRoleGrantItem(pageCode, List.copyOf(actions))));
        replaceRoleGrants(roleId, merged);
    }
}
