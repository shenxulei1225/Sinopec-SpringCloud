package cn.iocoder.yudao.module.facility.management.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.facility.management.controller.admin.vo.facility.*;
import cn.iocoder.yudao.module.facility.management.dal.dataobject.FacilityDO;
import cn.iocoder.yudao.module.facility.management.dal.mysql.FacilityMapper;
import cn.iocoder.yudao.module.facility.management.service.query.FacilityQueryService;
import cn.iocoder.yudao.module.facility.management.service.query.model.FacilityView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "管理后台 - 设施管理")
@RestController
@RequestMapping("/facility/facility")
public class FacilityController {

    @Resource
    private FacilityMapper facilityMapper;

    @Resource
    private FacilityQueryService facilityQueryService;

    // ==================== 设施管理接口 ====================

    @PostMapping("/create")
    @Operation(summary = "创建设施")
    @PreAuthorize("@ss.hasPermission('facility:facility:create')")
    public CommonResult<Long> createFacility(@Valid @RequestBody FacilityCreateReqVO createReqVO) {
        FacilityDO facility = BeanUtils.toBean(createReqVO, FacilityDO.class);
        facilityMapper.insert(facility);
        return CommonResult.success(facility.getId());
    }

    @PutMapping("/update")
    @Operation(summary = "更新设施")
    @PreAuthorize("@ss.hasPermission('facility:facility:update')")
    public CommonResult<Boolean> updateFacility(@Valid @RequestBody FacilityUpdateReqVO updateReqVO) {
        FacilityDO facility = BeanUtils.toBean(updateReqVO, FacilityDO.class);
        facilityMapper.updateById(facility);
        return CommonResult.success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除设施")
    @PreAuthorize("@ss.hasPermission('facility:facility:delete')")
    public CommonResult<Boolean> deleteFacility(@RequestParam("id") Long id) {
        facilityMapper.deleteById(id);
        return CommonResult.success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取设施详情")
    @PreAuthorize("@ss.hasPermission('facility:facility:query')")
    public CommonResult<FacilityRespVO> getFacility(@RequestParam("id") Long id) {
        FacilityView view = facilityQueryService.getFacilityView(id);
        return CommonResult.success(BeanUtils.toBean(view, FacilityRespVO.class));
    }

    @GetMapping("/get-by-code")
    @Operation(summary = "获取设施详情（根据编码）")
    public CommonResult<FacilityRespVO> getFacilityByCode(@RequestParam("code") String code) {
        FacilityView view = facilityQueryService.getFacilityViewByCode(code);
        return CommonResult.success(BeanUtils.toBean(view, FacilityRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获取设施分页")
    @PreAuthorize("@ss.hasPermission('facility:facility:query')")
    public CommonResult<PageResult<FacilityRespVO>> getFacilityPage(@Valid FacilityPageReqVO pageReqVO) {
        PageResult<FacilityView> pageResult = facilityQueryService.getFacilityViewPage(pageReqVO);
        return CommonResult.success(BeanUtils.toBean(pageResult, FacilityRespVO.class));
    }

    // ==================== 巡检对象选择相关接口 ====================

    @GetMapping("/list-simple")
    @Operation(summary = "获取简单设施列表（用于巡检对象选择）")
    public CommonResult<List<FacilityRespVO>> getSimpleFacilities(
            @RequestParam(value = "facilityName", required = false) String facilityName,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "facilityModel", required = false) String facilityModel) {
        List<FacilityView> views = facilityQueryService.getFacilitySimpleViewList(null, categoryId, facilityName);
        return CommonResult.success(BeanUtils.toBean(views, FacilityRespVO.class));
    }

    @GetMapping("/list-by-codes")
    @Operation(summary = "根据编码列表获取设施")
    public CommonResult<List<FacilityRespVO>> getFacilitiesByCodes(@RequestParam("codes") List<String> codes) {
        List<FacilityView> views = facilityQueryService.getFacilityViewListByCodes(codes);
        return CommonResult.success(BeanUtils.toBean(views, FacilityRespVO.class));
    }

}
