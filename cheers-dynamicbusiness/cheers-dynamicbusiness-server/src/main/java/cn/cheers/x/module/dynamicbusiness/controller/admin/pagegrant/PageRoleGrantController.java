package cn.cheers.x.module.dynamicbusiness.controller.admin.pagegrant;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.pagegrant.vo.PageRoleGrantItemReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.pagegrant.vo.PageRoleGrantItemRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.pagegrant.vo.PageRoleGrantSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.service.pagegrant.PageRoleGrantItem;
import cn.cheers.x.module.dynamicbusiness.service.pagegrant.PageRoleGrantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 平台配置「角色与权限」：保存数据目录以外的页面勾选。
 * 不分配旧菜单。不改数据目录授权。不判断这些页面的接口。
 */
@Tag(name = "管理后台 - 页面角色授权")
@RestController
@RequestMapping("/dynamicbusiness/page-role-grant")
@Validated
public class PageRoleGrantController {

    @Resource
    private PageRoleGrantService pageRoleGrantService;

    @GetMapping("/list")
    @Operation(summary = "读取角色已勾选的页面动作")
    @PreAuthorize("@ss.hasPermission('system:role:query')")
    public CommonResult<List<PageRoleGrantItemRespVO>> list(@RequestParam("roleId") Long roleId) {
        return success(toResp(pageRoleGrantService.listByRoleId(roleId)));
    }

    @PutMapping("/save")
    @Operation(summary = "整份保存角色的页面动作")
    @PreAuthorize("@ss.hasPermission('system:permission:assign-role-menu')")
    public CommonResult<Boolean> save(@Valid @RequestBody PageRoleGrantSaveReqVO reqVO) {
        pageRoleGrantService.replaceRoleGrants(reqVO.getRoleId(), toItems(reqVO.getItems()));
        return success(true);
    }

    private static List<PageRoleGrantItem> toItems(List<PageRoleGrantItemReqVO> items) {
        if (items == null) {
            return List.of();
        }
        List<PageRoleGrantItem> result = new ArrayList<>();
        for (PageRoleGrantItemReqVO item : items) {
            result.add(new PageRoleGrantItem(item.getPageCode(), item.getActions()));
        }
        return result;
    }

    private static List<PageRoleGrantItemRespVO> toResp(List<PageRoleGrantItem> items) {
        List<PageRoleGrantItemRespVO> result = new ArrayList<>();
        for (PageRoleGrantItem item : items) {
            PageRoleGrantItemRespVO row = new PageRoleGrantItemRespVO();
            row.setPageCode(item.pageCode());
            row.setActions(item.actions());
            result.add(row);
        }
        return result;
    }
}
