package cn.iocoder.yudao.module.facility.management.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.facility.management.controller.admin.vo.facility.*;
import cn.iocoder.yudao.module.facility.management.service.facility.FacilityService;
import cn.iocoder.yudao.module.facility.management.service.query.FacilityQueryService;
import cn.iocoder.yudao.module.facility.management.service.query.model.FacilityView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 设施工作台接口
 * <p>
 * 主要为前端 legacy 接口提供支持
 */
@Tag(name = "管理后台 - 设施工作台")
@RestController
@RequestMapping("/facility")
public class FacilityWorkbenchController {

    @Resource
    private FacilityQueryService facilityQueryService;
    @Resource
    private FacilityService facilityService;

    // ==================== Legacy 接口支持 ====================

    @GetMapping("/category-tree")
    @Operation(summary = "获取设施分类树（legacy接口）")
    public CommonResult<FacilityCategoryTreeResponse> getCategoryTree(
            @Parameter(description = "站点ID") @RequestParam(required = false) Long siteId,
            @Parameter(description = "用户ID") @RequestParam(required = false) Long userId) {
        List<FacilityTreeNodeVO> tree = facilityQueryService.getFacilityTree(siteId);
        FacilityCategoryTreeResponse response = new FacilityCategoryTreeResponse();
        response.setTree(tree);
        response.setFacilitiesType(facilityQueryService.getFacilityTypes());
        return success(response);
    }

    @GetMapping("/workbench/search-list")
    @Operation(summary = "搜索设施列表（legacy接口）")
    public CommonResult<PageResult<FacilityListItemVO>> searchFacilityList(
            @Parameter(description = "站点ID") @RequestParam(required = false) Long siteId,
            @Parameter(description = "用户ID") @RequestParam(required = false) Long userId,
            @Parameter(description = "设备类型") @RequestParam(required = false) String equipmentType,
            @Parameter(description = "设施类型") @RequestParam(required = false) String facilitiesType,
            @Parameter(description = "使用状态") @RequestParam(required = false) String usageState,
            @Parameter(description = "分类ID") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "关键字") @RequestParam(required = false) String keyword,
            @Parameter(description = "当前页") @RequestParam(required = false, defaultValue = "1") Integer pageNo,
            @Parameter(description = "每页条数") @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        FacilitySearchReqVO reqVO = new FacilitySearchReqVO();
        reqVO.setSiteId(siteId);
        reqVO.setUserId(userId);
        reqVO.setEquipmentType(equipmentType);
        reqVO.setFacilitiesType(facilitiesType);
        reqVO.setUsageState(usageState);
        reqVO.setCategoryId(categoryId);
        reqVO.setKeyword(keyword);
        reqVO.setPageNo(pageNo);
        reqVO.setPageSize(pageSize);
        PageResult<FacilityView> pageResult = facilityQueryService.searchFacilities(reqVO);
        // 转换为 Legacy 格式
        PageResult<FacilityListItemVO> legacyResult = new PageResult<>(
                pageResult.getList().stream().map(this::toListItemVO).collect(Collectors.toList()),
                pageResult.getTotal()
        );
        return success(legacyResult);
    }

    @GetMapping("/workbench/{facilityId}/detail")
    @Operation(summary = "获取设施详情（legacy接口）")
    public CommonResult<FacilityDetailVO> getFacilityDetail(
            @Parameter(description = "设施ID") @PathVariable("facilityId") Long facilityId) {
        FacilityView view = facilityQueryService.getFacilityView(facilityId);
        return success(toDetailVO(view));
    }

    @GetMapping("/workbench/{facilityId}/result")
    @Operation(summary = "获取设施巡检结果（legacy接口）")
    public CommonResult<FacilityResultVO> getFacilityResult(
            @Parameter(description = "设施ID") @PathVariable("facilityId") Long facilityId) {
        // TODO: 后续接入巡检模块后实现
        FacilityResultVO result = new FacilityResultVO();
        result.setNormal(0);
        result.setAbnormalAlert(0);
        result.setGeneralAlert(0);
        result.setCriticalAlert(0);
        return success(result);
    }

    @PostMapping("/workbench/spatial")
    @Operation(summary = "保存设施空间信息（legacy接口）")
    public CommonResult<Boolean> saveFacilitySpatial(@Valid @RequestBody FacilitySpatialSaveReqVO reqVO) {
        facilityService.saveSpatialInfo(reqVO);
        return success(true);
    }

    @PutMapping("/{facilityId}/status")
    @Operation(summary = "更新设施状态（legacy接口）")
    public CommonResult<Boolean> updateFacilityStatus(
            @Parameter(description = "设施ID") @PathVariable("facilityId") Long facilityId,
            @Parameter(description = "状态") @RequestParam("status") Integer status) {
        facilityService.updateFacilityStatus(facilityId, status);
        return success(true);
    }

    // ==================== 转换方法 ====================

    /**
     * 转换为 Legacy 列表项 VO
     */
    private FacilityListItemVO toListItemVO(FacilityView view) {
        if (view == null) {
            return null;
        }
        FacilityListItemVO vo = new FacilityListItemVO();
        vo.setFacilityId(view.getId());
        vo.setFacilityCode(view.getFacilityCode());
        vo.setFacilityName(view.getFacilityName());
        vo.setFacilityTypeDesc(view.getCategoryName());
        vo.setCategoryId(view.getCategoryId());
        vo.setCategoryName(view.getCategoryName());
        vo.setSiteId(view.getSiteId());
        vo.setSiteName(view.getSiteName());
        vo.setStatus(view.getStatus());
        vo.setDescription(view.getRemark());
        return vo;
    }

    /**
     * 转换为 Legacy 详情 VO
     */
    private FacilityDetailVO toDetailVO(FacilityView view) {
        if (view == null) {
            return null;
        }
        FacilityDetailVO vo = new FacilityDetailVO();
        vo.setFacilityId(view.getId());
        vo.setFacilityCode(view.getFacilityCode());
        vo.setFacilityName(view.getFacilityName());
        vo.setFacilityTypeDesc(view.getCategoryName());
        vo.setCategoryId(view.getCategoryId());
        vo.setCategoryName(view.getCategoryName());
        vo.setSiteId(view.getSiteId());
        vo.setSiteName(view.getSiteName());
        vo.setStatus(view.getStatus());
        vo.setDescription(view.getRemark());
        return vo;
    }

}
