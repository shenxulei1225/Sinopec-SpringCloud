package cn.cheers.x.module.dynamicbusiness.controller.admin.page;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.common.util.object.BeanUtils;
import cn.cheers.x.module.dynamicbusiness.controller.admin.page.vo.PagePageReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.page.vo.PageRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.page.vo.PageSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.page.vo.PageStatusUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.page.PageDO;
import cn.cheers.x.module.dynamicbusiness.service.page.PageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 页面管理", description = "页面元数据与 A2UI 配置管理")
@RestController
@RequestMapping("/dynamicbusiness/page")
@Validated
public class PageController {

    @Resource
    private PageService pageService;

    @PostMapping("/create")
    @Operation(summary = "创建页面")
    @PreAuthorize("@ss.hasPermission('system:page:create')")
    public CommonResult<Long> create(@Valid @RequestBody PageSaveReqVO reqVO) {
        return success(pageService.create(reqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新页面")
    @PreAuthorize("@ss.hasPermission('system:page:update')")
    public CommonResult<Boolean> update(@Valid @RequestBody PageSaveReqVO reqVO) {
        pageService.update(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除页面")
    @Parameter(name = "id", description = "页面编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:page:delete')")
    public CommonResult<Boolean> delete(@RequestParam("id") Long id) {
        pageService.delete(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得页面详情")
    @Parameter(name = "id", description = "页面编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:page:query')")
    public CommonResult<PageRespVO> get(@RequestParam("id") Long id) {
        PageDO page = pageService.get(id);
        return success(BeanUtils.toBean(page, PageRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得页面分页")
    @PreAuthorize("@ss.hasPermission('system:page:query')")
    public CommonResult<PageResult<PageRespVO>> page(@Valid PagePageReqVO reqVO) {
        PageResult<PageDO> pageResult = pageService.getPage(reqVO);
        return success(BeanUtils.toBean(pageResult, PageRespVO.class));
    }

    @GetMapping("/list-by-parent-menu")
    @Operation(summary = "根据父菜单获取页面列表")
    @Parameter(name = "parentMenuId", description = "父菜单ID", required = true, example = "100")
    @PreAuthorize("@ss.hasPermission('system:page:query')")
    public CommonResult<List<PageRespVO>> listByParentMenu(@RequestParam("parentMenuId") Long parentMenuId,
                                                           @RequestParam(value = "status", required = false) Integer status) {
        List<PageDO> list = pageService.getListByParentMenuId(parentMenuId, status);
        return success(BeanUtils.toBean(list, PageRespVO.class));
    }

    @PutMapping("/update-status")
    @Operation(summary = "更新页面状态", description = "用于发布或停用页面")
    @PreAuthorize("@ss.hasPermission('system:page:update')")
    public CommonResult<Boolean> updateStatus(@Valid @RequestBody PageStatusUpdateReqVO reqVO) {
        pageService.updateStatus(reqVO.getId(), reqVO.getStatus());
        return success(true);
    }
}
