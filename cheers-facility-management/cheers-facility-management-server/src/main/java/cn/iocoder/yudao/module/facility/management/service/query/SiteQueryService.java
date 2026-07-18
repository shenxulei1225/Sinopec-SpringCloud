package cn.iocoder.yudao.module.facility.management.service.query;

import cn.iocoder.yudao.module.facility.management.service.query.model.SiteView;

import java.util.List;

/**
 * 站场查询服务接口
 */
public interface SiteQueryService {

    /**
     * 获取站场树
     */
    List<SiteView> getSiteTree();

    /**
     * 获取站场详情
     */
    SiteView getSiteView(Long siteId);

    /**
     * 获取子节点ID列表
     */
    List<Long> getChildIds(Long parentId);

    /**
     * 搜索站场列表
     */
    List<SiteView> searchSites(String siteName, String siteCode, Long parentId, Integer nodeType, Integer status);

    /**
     * 校验站场编码唯一性
     *
     * @param siteCode 站场编码
     * @param siteId   排除的站场ID（用于更新时排除自己）
     * @return 是否唯一
     */
    boolean checkSiteCodeUnique(String siteCode, Long siteId);

    /**
     * 校验站场名称唯一性
     *
     * @param siteName 站场名称
     * @param parentId 父节点ID
     * @param siteId   排除的站场ID（用于更新时排除自己）
     * @return 是否唯一
     */
    boolean checkSiteNameUnique(String siteName, Long parentId, Long siteId);

    /**
     * 检查站场访问权限
     *
     * @param siteId 站场ID
     * @return 是否有权限
     */
    boolean checkSiteAccess(Long siteId);

}
