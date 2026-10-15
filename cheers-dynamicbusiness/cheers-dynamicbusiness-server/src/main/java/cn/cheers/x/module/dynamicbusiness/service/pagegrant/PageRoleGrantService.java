package cn.cheers.x.module.dynamicbusiness.service.pagegrant;

import java.util.List;

/**
 * 数据目录以外的页面授权。
 * 负责：按角色整份替换这些页面上的勾选。
 * 不负责：放行各业务页面的接口。不读写数据目录授权。
 */
public interface PageRoleGrantService {

    List<PageRoleGrantItem> listByRoleId(Long roleId);

    void replaceRoleGrants(Long roleId, List<PageRoleGrantItem> items);
}
