package cn.cheers.x.facility.management.api.impl;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.common.util.object.BeanUtils;
import cn.cheers.x.facility.management.api.FacilityApi;
import cn.cheers.x.facility.management.api.dto.FacilityPageReqDTO;
import cn.cheers.x.facility.management.api.dto.FacilityRespDTO;
import cn.cheers.x.facility.management.controller.admin.vo.facility.FacilityPageReqVO;
import cn.cheers.x.facility.management.service.query.FacilityQueryService;
import cn.cheers.x.facility.management.service.query.model.FacilityView;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 设施 API 实现类。
 *
 * <p>使用 FacilityQueryService 实现设施相关的查询逻辑。</p>
 */
/**
 * @deprecated 读 Facade 已迁移至 {@code dynamicbusiness-server} 的 {@link cn.cheers.x.module.dynamicbusiness.api.facility.FacilityApiImpl}。
 */
@Deprecated
@Slf4j
// @Service — 已停用，避免与 dynamicbusiness Facade 重复注册
public class FacilityApiImpl implements FacilityApi {

    @Resource
    private FacilityQueryService facilityQueryService;

    @Override
    public CommonResult<FacilityRespDTO> getFacility(Long id) {
        FacilityView view = facilityQueryService.getFacilityView(id);
        return CommonResult.success(toDTO(view));
    }

    @Override
    public CommonResult<FacilityRespDTO> getFacilityByCode(String code) {
        FacilityView view = facilityQueryService.getFacilityViewByCode(code);
        return CommonResult.success(toDTO(view));
    }

    @Override
    public CommonResult<List<FacilityRespDTO>> getSimpleFacilities(String facilityName, Long categoryId, String facilityModel) {
        List<FacilityView> views = facilityQueryService.getFacilitySimpleViewList(null, categoryId, facilityName);
        return CommonResult.success(toDTOList(views));
    }

    @Override
    public CommonResult<List<FacilityRespDTO>> getFacilitiesByCodes(List<String> codes) {
        List<FacilityView> views = facilityQueryService.getFacilityViewListByCodes(codes);
        return CommonResult.success(toDTOList(views));
    }

    @Override
    public CommonResult<List<FacilityRespDTO>> getFacilitiesByIds(List<Long> ids) {
        List<FacilityView> views = facilityQueryService.getFacilityViewListByIds(ids);
        return CommonResult.success(toDTOList(views));
    }

    @Override
    public CommonResult<PageResult<FacilityRespDTO>> getFacilityPage(FacilityPageReqDTO reqDTO) {
        FacilityPageReqVO pageReqVO = BeanUtils.toBean(reqDTO, FacilityPageReqVO.class);
        PageResult<FacilityView> pageResult = facilityQueryService.getFacilityViewPage(pageReqVO);
        List<FacilityRespDTO> dtoList = toDTOList(pageResult.getList());
        return CommonResult.success(new PageResult<>(dtoList, pageResult.getTotal()));
    }

    private FacilityRespDTO toDTO(FacilityView view) {
        if (view == null) {
            return null;
        }
        FacilityRespDTO dto = new FacilityRespDTO();
        dto.setId(view.getId());
        dto.setFacilityCode(view.getFacilityCode());
        dto.setFacilityName(view.getFacilityName());
        dto.setCategoryId(view.getCategoryId());
        dto.setCategoryName(view.getCategoryName());
        dto.setStationId(view.getStationId());
        dto.setStationName(view.getStationName());
        dto.setSortNo(view.getSortNo());
        dto.setStatus(view.getStatus());
        dto.setManufacturer(view.getManufacturer());
        dto.setModel(view.getModel());
        dto.setInstallDate(view.getInstallDate());
        dto.setLocation(view.getLocation());
        dto.setRemark(view.getRemark());
        return dto;
    }

    private List<FacilityRespDTO> toDTOList(List<FacilityView> views) {
        return views.stream().map(this::toDTO).collect(java.util.stream.Collectors.toList());
    }
}
