package cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmPageLayoutRefRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datamgmt.DmPageLayoutRefDO;
import cn.cheers.x.module.dynamicbusiness.service.datamgmt.DmWorkbenchLayoutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 页面 → 布局引用（整页 layoutId / 页内嵌入块 dataLayoutId）。
 * 只读已配置引用；不按约定键自动造布局。
 */
@Tag(name = "管理后台 - 页面布局引用")
@RestController
@RequestMapping("/dynamicbusiness/data-mgmt/page-layout-ref")
@Validated
public class DmPageLayoutRefController {

    @Resource
    private DmWorkbenchLayoutService dmWorkbenchLayoutService;

    @GetMapping
    @Operation(summary = "按 pageKey 查询页面布局引用")
    @Parameter(name = "pageKey", description = "页面身份键，如 view:{viewConfigId}")
    @PreAuthorize("@ss.hasPermission('system:entity-type:query')")
    public CommonResult<DmPageLayoutRefRespVO> get(@RequestParam("pageKey") String pageKey) {
        if (!StringUtils.hasText(pageKey)) {
            throw new ServiceException(400, "pageKey 不能为空");
        }
        DmPageLayoutRefDO ref = dmWorkbenchLayoutService.requirePageRef(pageKey.trim());
        DmPageLayoutRefRespVO vo = new DmPageLayoutRefRespVO();
        vo.setId(ref.getId());
        vo.setPageKey(ref.getPageKey());
        vo.setLayoutId(ref.getLayoutId());
        vo.setDataLayoutId(ref.getDataLayoutId());
        return success(vo);
    }
}
