package cn.cheers.x.facility.management.service.query.impl;

import cn.hutool.core.collection.CollUtil;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.facility.management.controller.admin.vo.facility.FacilityCategoryTreeResponse;
import cn.cheers.x.facility.management.controller.admin.vo.facility.FacilityPageReqVO;
import cn.cheers.x.facility.management.controller.admin.vo.facility.FacilitySearchReqVO;
import cn.cheers.x.facility.management.controller.admin.vo.facility.FacilityTreeNodeVO;
import cn.cheers.x.facility.management.dal.dataobject.FacilityDO;
import cn.cheers.x.facility.management.dal.dataobject.SiteDO;
import cn.cheers.x.facility.management.dal.mysql.FacilityMapper;
import cn.cheers.x.facility.management.dal.mysql.SiteMapper;
import cn.cheers.x.facility.management.service.query.FacilityQueryService;
import cn.cheers.x.facility.management.service.query.model.FacilityView;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 设施查询服务实现
 */
@Slf4j
@Service
public class FacilityQueryServiceImpl implements FacilityQueryService {

    @Resource
    private FacilityMapper facilityMapper;
    @Resource
    private SiteMapper siteMapper;

    @Override
    public FacilityView getFacilityView(Long id) {
        FacilityDO facility = facilityMapper.selectById(id);
        return toView(facility);
    }

    @Override
    public FacilityView getFacilityViewByCode(String code) {
        FacilityDO facility = facilityMapper.selectByCode(code);
        return toView(facility);
    }

    @Override
    public List<FacilityView> getFacilitySimpleViewList(Long siteId, Long categoryId, String keyword) {
        List<FacilityDO> facilities = facilityMapper.selectSimpleList(siteId, categoryId, keyword);
        return facilities.stream().map(this::toView).collect(Collectors.toList());
    }

    @Override
    public List<FacilityView> getFacilityViewListByIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        List<FacilityDO> facilities = facilityMapper.selectList(FacilityDO::getId, ids);
        return facilities.stream().map(this::toView).collect(Collectors.toList());
    }

    @Override
    public List<FacilityView> getFacilityViewListByCodes(Collection<String> codes) {
        if (codes == null || codes.isEmpty()) {
            return Collections.emptyList();
        }
        List<FacilityDO> facilities = facilityMapper.selectByCodes(codes);
        return facilities.stream().map(this::toView).collect(Collectors.toList());
    }

    @Override
    public PageResult<FacilityView> getFacilityViewPage(FacilityPageReqVO pageReqVO) {
        PageResult<FacilityDO> pageResult = facilityMapper.selectPage(pageReqVO);
        List<FacilityView> views = pageResult.getList().stream().map(this::toView).collect(Collectors.toList());
        return new PageResult<>(views, pageResult.getTotal());
    }

    // ==================== Legacy 接口支持 ====================

    @Override
    public List<FacilityTreeNodeVO> getFacilityTree(Long siteId) {
        List<FacilityTreeNodeVO> result = new ArrayList<>();
        
        // 获取所有站点作为树的第一层
        List<SiteDO> sites = siteMapper.selectList(new LambdaQueryWrapper<SiteDO>()
                .eq(siteId != null, SiteDO::getParentId, siteId != null && siteId != 0 ? siteId : 0)
                .orderByAsc(SiteDO::getSortNo));
        
        for (SiteDO site : sites) {
            FacilityTreeNodeVO node = new FacilityTreeNodeVO();
            node.setId(site.getSiteId());
            node.setParentId(site.getParentId());
            node.setCode(site.getSiteCode());
            node.setName(site.getSiteName());
            node.setTitleId(site.getSiteId());
            node.setTitle(site.getSiteName());
            node.setType("site");
            node.setChildren(buildSiteChildren(site.getSiteId()));
            result.add(node);
        }
        
        // 获取所有设施
        List<FacilityDO> facilities;
        if (siteId != null && siteId != 0) {
            facilities = facilityMapper.selectList(new LambdaQueryWrapper<FacilityDO>()
                    .eq(FacilityDO::getSiteId, siteId)
                    .orderByAsc(FacilityDO::getSortNo));
        } else {
            facilities = facilityMapper.selectList(new LambdaQueryWrapper<FacilityDO>()
                    .orderByAsc(FacilityDO::getSortNo));
        }
        
        // 按站点分组
        if (siteId == null || siteId == 0) {
            // 按站点分组添加到对应站点下
            for (FacilityDO facility : facilities) {
                FacilityTreeNodeVO node = new FacilityTreeNodeVO();
                node.setId(facility.getId());
                node.setParentId(facility.getSiteId());
                node.setCode(facility.getFacilityCode());
                node.setName(facility.getFacilityName());
                node.setTitleId(facility.getId());
                node.setTitle(facility.getFacilityName());
                node.setType("facility");
                
                // 查找父节点并添加
                addFacilityToTree(result, facility.getSiteId(), node);
            }
        }
        
        return result;
    }

    private List<FacilityTreeNodeVO> buildSiteChildren(Long siteId) {
        List<FacilityTreeNodeVO> children = new ArrayList<>();
        
        // 添加子站点
        List<SiteDO> childSites = siteMapper.selectList(new LambdaQueryWrapper<SiteDO>()
                .eq(SiteDO::getParentId, siteId)
                .orderByAsc(SiteDO::getSortNo));
        
        for (SiteDO childSite : childSites) {
            FacilityTreeNodeVO node = new FacilityTreeNodeVO();
            node.setId(childSite.getSiteId());
            node.setParentId(childSite.getParentId());
            node.setCode(childSite.getSiteCode());
            node.setName(childSite.getSiteName());
            node.setTitleId(childSite.getSiteId());
            node.setTitle(childSite.getSiteName());
            node.setType("site");
            node.setChildren(buildSiteChildren(childSite.getSiteId()));
            children.add(node);
        }
        
        // 添加该站点下的设施
        List<FacilityDO> facilities = facilityMapper.selectList(new LambdaQueryWrapper<FacilityDO>()
                .eq(FacilityDO::getSiteId, siteId)
                .orderByAsc(FacilityDO::getSortNo));
        
        for (FacilityDO facility : facilities) {
            FacilityTreeNodeVO node = new FacilityTreeNodeVO();
            node.setId(facility.getId());
            node.setParentId(facility.getSiteId());
            node.setCode(facility.getFacilityCode());
            node.setName(facility.getFacilityName());
            node.setTitleId(facility.getId());
            node.setTitle(facility.getFacilityName());
            node.setType("facility");
            children.add(node);
        }
        
        return children;
    }

    private void addFacilityToTree(List<FacilityTreeNodeVO> nodes, Long parentId, FacilityTreeNodeVO facilityNode) {
        for (FacilityTreeNodeVO node : nodes) {
            if (node.getId().equals(parentId)) {
                if (node.getChildren() == null) {
                    node.setChildren(new ArrayList<>());
                }
                node.getChildren().add(facilityNode);
                return;
            }
            if (node.getChildren() != null) {
                addFacilityToTree(node.getChildren(), parentId, facilityNode);
            }
        }
    }

    @Override
    public List<FacilityCategoryTreeResponse.FacilityTypeVO> getFacilityTypes() {
        // 获取所有不同的设施类型
        List<FacilityDO> facilities = facilityMapper.selectList(null);
        return facilities.stream()
                .filter(f -> f.getCategoryName() != null && !f.getCategoryName().isEmpty())
                .map(f -> {
                    FacilityCategoryTreeResponse.FacilityTypeVO type = new FacilityCategoryTreeResponse.FacilityTypeVO();
                    type.setCode(f.getCategoryId() != null ? f.getCategoryId().toString() : "");
                    type.setDesc(f.getCategoryName());
                    return type;
                })
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<FacilityView> searchFacilities(FacilitySearchReqVO reqVO) {
        // 使用 Mapper 的标准分页方法
        PageResult<FacilityDO> pageResult = facilityMapper.selectSearchPage(reqVO);
        List<FacilityView> views = pageResult.getList().stream().map(this::toView).collect(Collectors.toList());
        return new PageResult<>(views, pageResult.getTotal());
    }

    /**
     * 转换为视图对象
     */
    private FacilityView toView(FacilityDO facility) {
        if (facility == null) {
            return null;
        }
        FacilityView view = new FacilityView();
        view.setId(facility.getId());
        view.setFacilityCode(facility.getFacilityCode());
        view.setFacilityName(facility.getFacilityName());
        view.setCategoryId(facility.getCategoryId());
        view.setCategoryName(facility.getCategoryName());
        view.setSiteId(facility.getSiteId());
        view.setSiteName(facility.getSiteName());
        view.setSortNo(facility.getSortNo());
        view.setStatus(facility.getStatus());
        view.setManufacturer(facility.getManufacturer());
        view.setModel(facility.getModel());
        view.setInstallDate(facility.getInstallDate());
        view.setLocation(facility.getLocation());
        view.setRemark(facility.getRemark());
        return view;
    }

}
