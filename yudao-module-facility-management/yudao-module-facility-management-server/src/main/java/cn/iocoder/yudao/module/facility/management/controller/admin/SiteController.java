package cn.iocoder.yudao.module.facility.management.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.facility.management.controller.admin.vo.site.SiteCreateReqVO;
import cn.iocoder.yudao.module.facility.management.controller.admin.vo.site.SiteUpdateReqVO;
import cn.iocoder.yudao.module.facility.management.controller.admin.vo.site.SiteRespVO;
import cn.iocoder.yudao.module.facility.management.service.query.SiteQueryService;
import cn.iocoder.yudao.module.facility.management.service.query.model.SiteView;
import cn.iocoder.yudao.module.facility.management.service.site.SiteService;
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
 * 站场管理（读写 {@code fac_site}）。
 *
 * @deprecated 已废弃。站场/设施层级改由动态业务 {@code ent_facility}、{@code ent_region} 与分类树维护。
 */
@Deprecated
@Tag(name = "管理后台 - 站场管理（已废弃）")
@RestController
@RequestMapping("/site")
public class SiteController {

    @Resource
    private SiteService siteService;
    @Resource
    private SiteQueryService siteQueryService;

    @GetMapping("/tree")
    @Operation(summary = "获取站场树")
    public CommonResult<List<SiteView>> getSiteTree() {
        return success(siteQueryService.getSiteTree());
    }

    @GetMapping("/search")
    @Operation(summary = "搜索站场列表")
    public CommonResult<List<SiteView>> searchSites(
            @Parameter(description = "站场名称", example = "原油站") @RequestParam(required = false) String siteName,
            @Parameter(description = "站场编码", example = "SITE001") @RequestParam(required = false) String siteCode,
            @Parameter(description = "父节点ID") @RequestParam(required = false) Long parentId,
            @Parameter(description = "节点类型") @RequestParam(required = false) Integer nodeType,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        return success(siteQueryService.searchSites(siteName, siteCode, parentId, nodeType, status));
    }

    @GetMapping("/get")
    @Operation(summary = "获取站场详情")
    public CommonResult<SiteRespVO> getSite(@Parameter(description = "站场ID", required = true) @RequestParam("id") Long id) {
        SiteView view = siteQueryService.getSiteView(id);
        return success(toRespVO(view));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取站场详情")
    public CommonResult<SiteRespVO> getSiteById(@PathVariable("id") Long id) {
        SiteView view = siteQueryService.getSiteView(id);
        return success(toRespVO(view));
    }

    @PostMapping("/create")
    @Operation(summary = "创建站场")
    @PreAuthorize("@ss.hasPermission('facility:site:create')")
    public CommonResult<Long> createSite(@Valid @RequestBody SiteCreateReqVO createReqVO) {
        return success(siteService.createSite(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新站场")
    @PreAuthorize("@ss.hasPermission('facility:site:update')")
    public CommonResult<Boolean> updateSite(@Valid @RequestBody SiteUpdateReqVO updateReqVO) {
        siteService.updateSite(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除站场")
    @PreAuthorize("@ss.hasPermission('facility:site:delete')")
    public CommonResult<Boolean> deleteSite(@Parameter(description = "站场ID", required = true) @RequestParam("id") Long id) {
        siteService.deleteSite(id);
        return success(true);
    }

    @GetMapping("/list-child-ids")
    @Operation(summary = "获取子站场ID列表")
    public CommonResult<List<Long>> listChildIds(@Parameter(description = "父节点ID", required = true) @RequestParam("parentId") Long parentId) {
        return success(siteQueryService.getChildIds(parentId));
    }

    @GetMapping("/check-site-code")
    @Operation(summary = "校验站场编码唯一性")
    public CommonResult<Boolean> checkSiteCode(
            @Parameter(description = "站场编码", required = true) @RequestParam("siteCode") String siteCode,
            @Parameter(description = "站场ID（用于排除自己）") @RequestParam(required = false) Long siteId) {
        return success(siteQueryService.checkSiteCodeUnique(siteCode, siteId));
    }

    @GetMapping("/check-site-name")
    @Operation(summary = "校验站场名称唯一性")
    public CommonResult<Boolean> checkSiteName(
            @Parameter(description = "站场名称", required = true) @RequestParam("siteName") String siteName,
            @Parameter(description = "父节点ID") @RequestParam(required = false) Long parentId,
            @Parameter(description = "站场ID（用于排除自己）") @RequestParam(required = false) Long siteId) {
        return success(siteQueryService.checkSiteNameUnique(siteName, parentId, siteId));
    }

    @GetMapping("/access")
    @Operation(summary = "检查站场访问权限")
    public CommonResult<Boolean> checkSiteAccess(@Parameter(description = "站场ID", required = true) @RequestParam("siteId") Long siteId) {
        return success(siteQueryService.checkSiteAccess(siteId));
    }

    private SiteRespVO toRespVO(SiteView view) {
        if (view == null) {
            return null;
        }
        SiteRespVO respVO = new SiteRespVO();
        respVO.setSiteId(view.getSiteId());
        respVO.setSiteCode(view.getSiteCode());
        respVO.setSiteName(view.getSiteName());
        respVO.setParentId(view.getParentId());
        respVO.setParentName(view.getParentName());
        respVO.setSortNo(view.getSortNo());
        respVO.setNodeType(view.getNodeType());
        respVO.setNodeTypeDesc(view.getNodeTypeDesc());
        respVO.setSiteTypeId(view.getSiteTypeId());
        respVO.setSiteTypeName(view.getSiteTypeName());
        respVO.setLevel(view.getLevel());
        respVO.setPath(view.getPath());
        respVO.setStatus(view.getStatus());
        respVO.setProvinceCode(view.getProvinceCode());
        respVO.setCityCode(view.getCityCode());
        respVO.setAreaCode(view.getAreaCode());
        respVO.setAddress(view.getAddress());
        respVO.setDirector(view.getDirector());
        respVO.setPhone(view.getPhone());
        respVO.setEmail(view.getEmail());
        respVO.setRoutingUrl(view.getRoutingUrl());
        respVO.setLongitude(view.getLongitude());
        respVO.setLatitude(view.getLatitude());
        respVO.setRemark(view.getRemark());
        respVO.setOwnerUserId(view.getOwnerUserId());
        respVO.setCreateTime(view.getCreateTime());
        respVO.setUpdateTime(view.getUpdateTime());
        return respVO;
    }

}
