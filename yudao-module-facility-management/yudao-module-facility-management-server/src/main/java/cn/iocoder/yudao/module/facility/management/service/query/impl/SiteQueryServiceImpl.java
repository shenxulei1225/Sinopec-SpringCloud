package cn.iocoder.yudao.module.facility.management.service.query.impl;

import cn.iocoder.yudao.module.facility.management.dal.dataobject.SiteDO;
import cn.iocoder.yudao.module.facility.management.dal.dataobject.SiteTypeDO;
import cn.iocoder.yudao.module.facility.management.dal.mysql.SiteMapper;
import cn.iocoder.yudao.module.facility.management.dal.mysql.SiteTypeMapper;
import cn.iocoder.yudao.module.facility.management.service.query.SiteQueryService;
import cn.iocoder.yudao.module.facility.management.service.query.model.SiteView;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 站场查询服务实现
 */
@Slf4j
@Service
public class SiteQueryServiceImpl implements SiteQueryService {

    @Resource
    private SiteMapper siteMapper;
    @Resource
    private SiteTypeMapper siteTypeMapper;

    @Override
    public List<SiteView> getSiteTree() {
        List<SiteDO> allSites = siteMapper.selectList(null);
        // 预加载所有站场类型
        Map<Long, String> siteTypeNameMap = buildSiteTypeNameMap();
        return buildTree(allSites, 0L, siteTypeNameMap);
    }

    @Override
    public SiteView getSiteView(Long siteId) {
        SiteDO site = siteMapper.selectById(siteId);
        return toView(site);
    }

    @Override
    public List<Long> getChildIds(Long parentId) {
        List<SiteDO> children = siteMapper.selectByParentId(parentId);
        List<Long> allChildIds = new ArrayList<>();
        collectChildIds(children, allChildIds);
        return allChildIds;
    }

    @Override
    public List<SiteView> searchSites(String siteName, String siteCode, Long parentId, Integer nodeType, Integer status) {
        // 预加载所有站场类型
        Map<Long, String> siteTypeNameMap = buildSiteTypeNameMap();
        return siteMapper.selectList(new cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX<SiteDO>()
                .likeIfPresent(SiteDO::getSiteName, siteName)
                .likeIfPresent(SiteDO::getSiteCode, siteCode)
                .eqIfPresent(SiteDO::getParentId, parentId)
                .eqIfPresent(SiteDO::getNodeType, nodeType)
                .eqIfPresent(SiteDO::getStatus, status)
                .eq(SiteDO::getDeleted, false)
                .orderByAsc(SiteDO::getLevel)
                .orderByAsc(SiteDO::getSortNo)
                .orderByAsc(SiteDO::getSiteId))
                .stream()
                .map(site -> toView(site, siteTypeNameMap))
                .collect(Collectors.toList());
    }

    @Override
    public boolean checkSiteCodeUnique(String siteCode, Long siteId) {
        SiteDO site = siteMapper.selectByCode(siteCode);
        if (site == null) {
            return true;
        }
        // 排除自己
        return site.getSiteId().equals(siteId);
    }

    @Override
    public boolean checkSiteNameUnique(String siteName, Long parentId, Long siteId) {
        SiteDO site;
        if (parentId != null) {
            site = siteMapper.selectByNameAndParentId(siteName, parentId);
        } else {
            site = siteMapper.selectByName(siteName);
        }
        if (site == null) {
            return true;
        }
        // 排除自己
        return site.getSiteId().equals(siteId);
    }

    @Override
    public boolean checkSiteAccess(Long siteId) {
        // 检查站场是否存在
        SiteDO site = siteMapper.selectById(siteId);
        return site != null;
    }

    /**
     * 构建站场类型名称映射
     */
    private Map<Long, String> buildSiteTypeNameMap() {
        List<SiteTypeDO> siteTypes = siteTypeMapper.selectAllList();
        Map<Long, String> map = new HashMap<>();
        for (SiteTypeDO siteType : siteTypes) {
            map.put(siteType.getId(), siteType.getTypeName());
        }
        return map;
    }

    /**
     * 递归构建树结构
     */
    private List<SiteView> buildTree(List<SiteDO> allSites, Long parentId, Map<Long, String> siteTypeNameMap) {
        return allSites.stream()
                .filter(site -> Objects.equals(parentId, site.getParentId()))
                .map(site -> {
                    SiteView view = toView(site, siteTypeNameMap);
                    view.setChildren(buildTree(allSites, site.getSiteId(), siteTypeNameMap));
                    return view;
                })
                .collect(Collectors.toList());
    }

    /**
     * 递归收集所有子节点ID
     */
    private void collectChildIds(List<SiteDO> sites, List<Long> allChildIds) {
        for (SiteDO site : sites) {
            allChildIds.add(site.getSiteId());
            List<SiteDO> children = siteMapper.selectByParentId(site.getSiteId());
            if (!children.isEmpty()) {
                collectChildIds(children, allChildIds);
            }
        }
    }

    /**
     * 转换为视图对象
     */
    private SiteView toView(SiteDO site) {
        if (site == null) {
            return null;
        }
        // 预加载所有站场类型
        Map<Long, String> siteTypeNameMap = buildSiteTypeNameMap();
        return toView(site, siteTypeNameMap);
    }

    /**
     * 转换为视图对象（带站场类型名称映射）
     */
    private SiteView toView(SiteDO site, Map<Long, String> siteTypeNameMap) {
        if (site == null) {
            return null;
        }
        SiteView view = new SiteView();
        view.setSiteId(site.getSiteId());
        view.setSiteCode(site.getSiteCode());
        view.setSiteName(site.getSiteName());
        view.setParentId(site.getParentId());
        view.setSortNo(site.getSortNo());
        view.setNodeType(site.getNodeType());
        view.setNodeTypeDesc(getNodeTypeDesc(site.getNodeType()));
        view.setSiteTypeId(site.getSiteTypeId());
        view.setSiteTypeName(siteTypeNameMap.get(site.getSiteTypeId()));
        view.setLevel(site.getLevel());
        view.setPath(site.getPath());
        view.setStatus(site.getStatus());
        view.setProvinceCode(site.getProvinceCode());
        view.setCityCode(site.getCityCode());
        view.setAreaCode(site.getAreaCode());
        view.setAddress(site.getAddress());
        view.setDirector(site.getDirector());
        view.setPhone(site.getPhone());
        view.setEmail(site.getEmail());
        view.setRoutingUrl(site.getRoutingUrl());
        view.setLongitude(site.getLongitude());
        view.setLatitude(site.getLatitude());
        view.setOwnerUserId(site.getOwnerUserId());
        view.setRemark(site.getRemark());
        view.setCreateTime(site.getCreateTime());
        view.setUpdateTime(site.getUpdateTime());
        return view;
    }

    /**
     * 获取节点类型描述
     */
    private String getNodeTypeDesc(Integer nodeType) {
        if (nodeType == null) {
            return null;
        }
        return switch (nodeType) {
            case 1 -> "分组";
            case 2 -> "站场";
            default -> "未知";
        };
    }

}
