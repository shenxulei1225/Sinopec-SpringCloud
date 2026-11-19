package cn.cheers.x.module.dynamicbusiness.framework.security;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.security.core.util.SecurityFrameworkUtils;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.pagegrant.PageRoleGrantDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.pagegrant.PageRoleGrantMapper;
import cn.cheers.x.module.dynamicbusiness.service.datapermission.PostAssignmentQueryService;
import cn.cheers.x.system.api.permission.RoleApi;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 数据目录以外的页面授权，给 {@code @PreAuthorize} 和业务服务用。
 * 例如报修受理、我的报修、工单中心、我的工单：角色页上勾了这一页的「查看」或「维护」，才放行对应接口。
 * 权威：dynamic_page_role_grant（角色、页面编码、动作）。页面和动作的登记见 PagePermissionRegistry。
 * 角色取两个来源：系统里直接分配给人的角色，加上有效任职的职务绑定的角色。页面不属于任何目录，和管理单元无关。
 * 超级管理员也要有勾选，不直接放行。
 * 不负责：数据目录授权（见 CatalogAccess）。不核对记录属于哪个场站。
 */
@Component("pageAccess")
public class PageAccess {

    @Resource
    private PageRoleGrantMapper pageRoleGrantMapper;
    @Resource
    private PostAssignmentQueryService postAssignmentQueryService;
    @Resource
    private RoleApi roleApi;

    /** 当前登录人的角色勾了这一页的这个动作时返回 true。没登录、页面或动作为空都返回 false。 */
    public boolean allow(String pageCode, String action) {
        return allowAny(action, pageCode);
    }

    /** 这几页里任一页勾了这个动作就返回 true。同一个接口被几页共用时用它，例如脉络在报修页和工单页都显示。 */
    public boolean allowAny(String action, String... pageCodes) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        if (userId == null || !StringUtils.hasText(action) || pageCodes == null || pageCodes.length == 0) {
            return false;
        }
        Collection<Long> roleIds = pageRoleIds(userId);
        if (roleIds.isEmpty()) {
            return false;
        }
        for (PageRoleGrantDO grant : pageRoleGrantMapper.selectListByRoleIds(roleIds)) {
            if (!action.equals(grant.getAction())) {
                continue;
            }
            for (String pageCode : pageCodes) {
                if (StringUtils.hasText(pageCode) && pageCode.equals(grant.getPageCode())) {
                    return true;
                }
            }
        }
        return false;
    }

    /** 直接分配的角色和有效任职带来的角色。 */
    private Set<Long> pageRoleIds(Long userId) {
        Set<Long> roleIds = new LinkedHashSet<>();
        Collection<Long> direct = roleApi.getUserRoleIdListByUserId(userId).getCheckedData();
        if (direct != null) {
            roleIds.addAll(direct);
        }
        roleIds.addAll(postAssignmentQueryService.listActivePostRoleIds(userId));
        return roleIds;
    }

    /** 服务里按配置决定页面时用：没有这项勾选就抛 403。 */
    public void require(String pageCode, String action) {
        if (!allow(pageCode, action)) {
            throw new ServiceException(403, "没有该页面的操作权限");
        }
    }
}
