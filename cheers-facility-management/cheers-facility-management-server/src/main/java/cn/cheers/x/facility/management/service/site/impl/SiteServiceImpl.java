package cn.cheers.x.facility.management.service.site.impl;

import cn.cheers.x.framework.common.util.object.ObjectUtils;
import cn.cheers.x.facility.management.controller.admin.vo.site.SiteCreateReqVO;
import cn.cheers.x.facility.management.controller.admin.vo.site.SiteUpdateReqVO;
import cn.cheers.x.facility.management.dal.dataobject.SiteDO;
import cn.cheers.x.facility.management.dal.dataobject.SiteTypeDO;
import cn.cheers.x.facility.management.dal.mysql.SiteMapper;
import cn.cheers.x.facility.management.dal.mysql.SiteTypeMapper;
import cn.cheers.x.facility.management.service.site.SiteService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.cheers.x.facility.management.enums.ErrorCodeConstants.*;

/**
 * 站场管理服务实现
 */
@Slf4j
@Service
public class SiteServiceImpl implements SiteService {

    @Resource
    private SiteMapper siteMapper;
    @Resource
    private SiteTypeMapper siteTypeMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createSite(SiteCreateReqVO createReqVO) {
        // 校验父节点是否存在
        if (!createReqVO.getParentId().equals(0L)) {
            SiteDO parent = siteMapper.selectById(createReqVO.getParentId());
            if (parent == null) {
                throw exception(SITE_NOT_EXISTS);
            }
        }

        // 校验编码唯一性
        SiteDO existingSite = siteMapper.selectByCode(createReqVO.getSiteCode());
        if (existingSite != null) {
            throw exception(SITE_CODE_DUPLICATE);
        }

        // 校验名称唯一性（在同级节点下）
        SiteDO existingByName = siteMapper.selectByNameAndParentId(createReqVO.getSiteName(), createReqVO.getParentId());
        if (existingByName != null) {
            throw exception(SITE_NAME_DUPLICATE);
        }

        // 构建路径
        String path = buildPath(createReqVO.getParentId(), createReqVO.getSiteCode());

        // 计算层级
        int level = calculateLevel(createReqVO.getParentId());

        // 创建站场
        SiteDO site = new SiteDO();
        site.setSiteCode(createReqVO.getSiteCode());
        site.setSiteName(createReqVO.getSiteName());
        site.setParentId(createReqVO.getParentId());
        site.setSortNo(ObjectUtils.defaultIfNull(createReqVO.getSortNo(), 0));
        site.setNodeType(createReqVO.getNodeType());
        site.setSiteTypeId(createReqVO.getSiteTypeId());
        site.setLevel(level);
        site.setPath(path);
        site.setStatus(0); // 默认正常
        site.setProvinceCode(createReqVO.getProvinceCode());
        site.setCityCode(createReqVO.getCityCode());
        site.setAreaCode(createReqVO.getAreaCode());
        site.setAddress(createReqVO.getAddress());
        site.setDirector(createReqVO.getDirector());
        site.setPhone(createReqVO.getPhone());
        site.setEmail(createReqVO.getEmail());
        site.setRoutingUrl(createReqVO.getRoutingUrl());
        site.setLongitude(createReqVO.getLongitude());
        site.setLatitude(createReqVO.getLatitude());
        site.setRemark(createReqVO.getRemark());
        site.setOwnerUserId(createReqVO.getOwnerUserId());
        siteMapper.insert(site);
        return site.getSiteId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSite(SiteUpdateReqVO updateReqVO) {
        // 校验站场是否存在
        SiteDO site = siteMapper.selectById(updateReqVO.getSiteId());
        if (site == null) {
            throw exception(SITE_NOT_EXISTS);
        }

        // 校验编码唯一性（排除自己）
        SiteDO existingByCode = siteMapper.selectByCode(updateReqVO.getSiteCode());
        if (existingByCode != null && !existingByCode.getSiteId().equals(updateReqVO.getSiteId())) {
            throw exception(SITE_CODE_DUPLICATE);
        }

        // 校验名称唯一性（排除自己）
        SiteDO existingByName = siteMapper.selectByNameAndParentId(updateReqVO.getSiteName(), updateReqVO.getParentId());
        if (existingByName != null && !existingByName.getSiteId().equals(updateReqVO.getSiteId())) {
            throw exception(SITE_NAME_DUPLICATE);
        }

        // 记录旧的路径和层级
        String oldPath = site.getPath();
        int oldLevel = site.getLevel();

        // 计算新的路径和层级（如果父节点发生变化）
        String newPath;
        int newLevel;
        if (!updateReqVO.getParentId().equals(site.getParentId())) {
            // 父节点发生变化
            if (updateReqVO.getParentId().equals(site.getSiteId())) {
                // 父节点不能是自己
                throw exception(SITE_PARENT_NOT_SELF);
            }
            if (isChildNode(site.getSiteId(), updateReqVO.getParentId())) {
                // 父节点不能是自己的子节点
                throw exception(SITE_PARENT_NOT_CHILD);
            }
            newPath = buildPath(updateReqVO.getParentId(), updateReqVO.getSiteCode());
            newLevel = calculateLevel(updateReqVO.getParentId());
        } else {
            newPath = oldPath;
            newLevel = oldLevel;
        }

        // 更新站场
        site.setSiteCode(updateReqVO.getSiteCode());
        site.setSiteName(updateReqVO.getSiteName());
        site.setParentId(updateReqVO.getParentId());
        site.setSortNo(ObjectUtils.defaultIfNull(updateReqVO.getSortNo(), 0));
        site.setNodeType(updateReqVO.getNodeType());
        site.setSiteTypeId(updateReqVO.getSiteTypeId());
        site.setPath(newPath);
        site.setLevel(newLevel);
        site.setStatus(ObjectUtils.defaultIfNull(updateReqVO.getStatus(), 0));
        site.setProvinceCode(updateReqVO.getProvinceCode());
        site.setCityCode(updateReqVO.getCityCode());
        site.setAreaCode(updateReqVO.getAreaCode());
        site.setAddress(updateReqVO.getAddress());
        site.setDirector(updateReqVO.getDirector());
        site.setPhone(updateReqVO.getPhone());
        site.setEmail(updateReqVO.getEmail());
        site.setRoutingUrl(updateReqVO.getRoutingUrl());
        site.setLongitude(updateReqVO.getLongitude());
        site.setLatitude(updateReqVO.getLatitude());
        site.setRemark(updateReqVO.getRemark());
        site.setOwnerUserId(updateReqVO.getOwnerUserId());
        siteMapper.updateById(site);

        // 如果路径或层级发生变化，需要更新子节点
        if (!newPath.equals(oldPath) || newLevel != oldLevel) {
            int levelDiff = newLevel - oldLevel;
            updateChildrenPath(site.getSiteId(), oldPath, newPath, levelDiff);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSite(Long id) {
        // 校验站场是否存在
        SiteDO site = siteMapper.selectById(id);
        if (site == null) {
            throw exception(SITE_NOT_EXISTS);
        }

        // 校验是否有子节点
        List<SiteDO> children = siteMapper.selectByParentId(id);
        if (!children.isEmpty()) {
            throw exception(SITE_HAS_CHILDREN);
        }

        // 删除站场
        siteMapper.deleteById(id);
    }

    /**
     * 构建路径
     */
    private String buildPath(Long parentId, String siteCode) {
        if (parentId == null || parentId.equals(0L)) {
            return "/" + siteCode;
        }
        SiteDO parent = siteMapper.selectById(parentId);
        if (parent == null) {
            return "/" + siteCode;
        }
        return parent.getPath() + "/" + siteCode;
    }

    /**
     * 计算层级
     */
    private int calculateLevel(Long parentId) {
        if (parentId == null || parentId.equals(0L)) {
            return 1;
        }
        SiteDO parent = siteMapper.selectById(parentId);
        if (parent == null) {
            return 1;
        }
        return parent.getLevel() + 1;
    }

    /**
     * 判断目标节点是否是当前节点的子节点
     */
    private boolean isChildNode(Long currentId, Long targetParentId) {
        SiteDO target = siteMapper.selectById(targetParentId);
        if (target == null) {
            return false;
        }
        return target.getPath().contains("/" + siteMapper.selectById(currentId).getSiteCode() + "/");
    }

    /**
     * 递归更新子节点的路径和层级
     */
    private void updateChildrenPath(Long parentId, String oldPath, String newPath, int levelDiff) {
        List<SiteDO> children = siteMapper.selectByParentId(parentId);
        for (SiteDO child : children) {
            child.setPath(child.getPath().replace(oldPath, newPath));
            child.setLevel(child.getLevel() + levelDiff);
            siteMapper.updateById(child);
            updateChildrenPath(child.getSiteId(), oldPath, newPath, levelDiff);
        }
    }

}
