package cn.cheers.x.facility.management.service.query.impl;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.facility.management.controller.admin.vo.facility.FacilityCategoryTreeResponse;
import cn.cheers.x.facility.management.controller.admin.vo.facility.FacilityPageReqVO;
import cn.cheers.x.facility.management.controller.admin.vo.facility.FacilitySearchReqVO;
import cn.cheers.x.facility.management.controller.admin.vo.facility.FacilityTreeNodeVO;
import cn.cheers.x.facility.management.dal.dataobject.FacilityDO;
import cn.cheers.x.facility.management.dal.mysql.FacilityMapper;
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
    public List<FacilityView> getFacilitySimpleViewList(Long stationId, Long categoryId, String keyword) {
        List<FacilityDO> facilities = facilityMapper.selectSimpleList(stationId, categoryId, keyword);
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
    public List<FacilityTreeNodeVO> getFacilityTree(Long stationId) {
        List<FacilityDO> facilities;
        if (stationId != null && stationId != 0) {
            facilities = facilityMapper.selectList(new LambdaQueryWrapper<FacilityDO>()
                    .eq(FacilityDO::getStationId, stationId)
                    .orderByAsc(FacilityDO::getSortNo));
        } else {
            facilities = facilityMapper.selectList(new LambdaQueryWrapper<FacilityDO>()
                    .orderByAsc(FacilityDO::getSortNo));
        }
        List<FacilityTreeNodeVO> result = new ArrayList<>(facilities.size());
        for (FacilityDO facility : facilities) {
            FacilityTreeNodeVO node = new FacilityTreeNodeVO();
            node.setId(facility.getId());
            node.setParentId(facility.getStationId());
            node.setCode(facility.getFacilityCode());
            node.setName(facility.getFacilityName());
            node.setTitleId(facility.getId());
            node.setTitle(facility.getFacilityName());
            node.setType("facility");
            node.setChildren(Collections.emptyList());
            result.add(node);
        }
        return result;
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
        view.setStationId(facility.getStationId());
        view.setStationName(facility.getStationName());
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
