package cn.iocoder.yudao.module.facility.management.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.facility.management.controller.admin.vo.site.SiteTypeCreateReqVO;
import cn.iocoder.yudao.module.facility.management.controller.admin.vo.site.SiteTypeUpdateReqVO;
import cn.iocoder.yudao.module.facility.management.controller.admin.vo.site.SiteTypeRespVO;
import cn.iocoder.yudao.module.facility.management.dal.dataobject.SiteTypeDO;
import cn.iocoder.yudao.module.facility.management.service.sitetype.SiteTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 站场类型管理
 */
@Tag(name = "管理后台 - 站场类型管理")
@RestController
@RequestMapping("/site-type")
public class SiteTypeController {

    @Resource
    private SiteTypeService siteTypeService;

    @GetMapping("/list")
    @Operation(summary = "获取站场类型列表")
    public CommonResult<List<SiteTypeRespVO>> getSiteTypeList(
            @Parameter(description = "是否只查询正常状态") @RequestParam(required = false, defaultValue = "true") Boolean normalOnly) {
        List<SiteTypeDO> list = normalOnly ? siteTypeService.getNormalSiteTypeList() : siteTypeService.getAllSiteTypeList();
        return success(toRespList(list));
    }

    @GetMapping("/get")
    @Operation(summary = "获取站场类型详情")
    public CommonResult<SiteTypeRespVO> getSiteType(@Parameter(description = "站场类型ID", required = true) @RequestParam("id") Long id) {
        SiteTypeDO siteType = siteTypeService.getSiteType(id);
        return success(toResp(siteType));
    }

    @PostMapping("/create")
    @Operation(summary = "创建站场类型")
    @PreAuthorize("@ss.hasPermission('facility:site-type:create')")
    public CommonResult<Long> createSiteType(@Valid @RequestBody SiteTypeCreateReqVO createReqVO) {
        return success(siteTypeService.createSiteType(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新站场类型")
    @PreAuthorize("@ss.hasPermission('facility:site-type:update')")
    public CommonResult<Boolean> updateSiteType(@Valid @RequestBody SiteTypeUpdateReqVO updateReqVO) {
        siteTypeService.updateSiteType(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除站场类型")
    @PreAuthorize("@ss.hasPermission('facility:site-type:delete')")
    public CommonResult<Boolean> deleteSiteType(@Parameter(description = "站场类型ID", required = true) @RequestParam("id") Long id) {
        siteTypeService.deleteSiteType(id);
        return success(true);
    }

    // ==================== 转换方法 ====================

    private SiteTypeRespVO toResp(SiteTypeDO siteType) {
        if (siteType == null) {
            return null;
        }
        SiteTypeRespVO respVO = new SiteTypeRespVO();
        respVO.setId(siteType.getId());
        respVO.setTypeCode(siteType.getTypeCode());
        respVO.setTypeName(siteType.getTypeName());
        respVO.setDescription(siteType.getDescription());
        respVO.setSortNo(siteType.getSortNo());
        respVO.setStatus(siteType.getStatus());
        respVO.setRemark(siteType.getRemark());
        return respVO;
    }

    private List<SiteTypeRespVO> toRespList(List<SiteTypeDO> list) {
        return list.stream().map(this::toResp).toList();
    }

}
