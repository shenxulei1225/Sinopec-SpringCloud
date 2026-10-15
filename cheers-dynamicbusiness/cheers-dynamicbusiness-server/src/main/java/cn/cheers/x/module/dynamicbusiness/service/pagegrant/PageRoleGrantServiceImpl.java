package cn.cheers.x.module.dynamicbusiness.service.pagegrant;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.pagegrant.PageRoleGrantDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.pagegrant.PageRoleGrantMapper;
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
 * 页面授权的写入。
 * 只接受角色页上已经列出的页面和动作。没有对应行，角色页上这项就是未勾选。
 * 不在这里放行业务接口。
 */
@Service
public class PageRoleGrantServiceImpl implements PageRoleGrantService {

    /** 与角色页分组一致。页面编码对应它允许的动作。 */
    static final Map<String, Set<String>> PAGES = Map.ofEntries(
            Map.entry("live-monitor", Set.of("view", "command")),
            Map.entry("inspection-task", Set.of("view", "maintain", "enable")),
            Map.entry("inspection-schedule", Set.of("view")),
            Map.entry("emergency-event", Set.of("view", "create", "confirm", "assess", "start", "supplement", "command")),
            Map.entry("emergency-resource", Set.of("view", "maintain")),
            Map.entry("emergency-guarantee", Set.of("view", "maintain")),
            Map.entry("emergency-command-template", Set.of("view", "maintain", "enable")),
            Map.entry("analysis-capability", Set.of("view", "maintain", "enable")),
            Map.entry("analysis-job", Set.of("view")),
            Map.entry("analysis-provider", Set.of("view", "maintain", "enable")),
            Map.entry("road-network", Set.of("view")),
            Map.entry("facility-gis", Set.of("view")),
            Map.entry("stats", Set.of("view")),
            Map.entry("catalog-admin", Set.of("view", "create", "update", "delete")),
            Map.entry("organization", Set.of("view", "maintain-dept", "maintain-user", "reset-password")),
            Map.entry("portal", Set.of("view", "maintain")),
            Map.entry("role-permission", Set.of("view", "maintain-role", "save-grant")),
            Map.entry("system-account", Set.of("view", "maintain")),
            Map.entry("resource-library", Set.of("view", "maintain")),
            Map.entry("plugin", Set.of("view", "maintain")),
            Map.entry("capability-config", Set.of("view", "maintain")),
            Map.entry("station-config", Set.of("view", "maintain")),
            Map.entry("gis-admin", Set.of("view", "maintain")),
            Map.entry("business-list", Set.of("view", "maintain")),
            Map.entry("mind-map", Set.of("view", "maintain")),
            Map.entry("workflow", Set.of("view", "maintain"))
    );

    @Resource
    private PageRoleGrantMapper pageRoleGrantMapper;

    @Override
    public List<PageRoleGrantItem> listByRoleId(Long roleId) {
        Map<String, LinkedHashSet<String>> grouped = new LinkedHashMap<>();
        for (PageRoleGrantDO row : pageRoleGrantMapper.selectListByRoleId(roleId)) {
            Set<String> allowed = PAGES.get(row.getPageCode());
            if (allowed == null || !allowed.contains(row.getAction())) {
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
        if (items == null) {
            return;
        }
        for (PageRoleGrantItem item : items) {
            if (item == null || !StringUtils.hasText(item.pageCode())) {
                throw new ServiceException(400, "请选择页面");
            }
            String pageCode = item.pageCode().trim();
            Set<String> allowed = PAGES.get(pageCode);
            if (allowed == null) {
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
        }
    }
}
