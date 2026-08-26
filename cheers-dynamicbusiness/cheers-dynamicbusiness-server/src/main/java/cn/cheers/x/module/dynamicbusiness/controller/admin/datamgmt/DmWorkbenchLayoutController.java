package cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt;

import cn.cheers.x.framework.apilog.core.annotation.ApiAccessLog;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmWorkbenchLayoutSettingsRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmWorkbenchLayoutSettingsUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.service.datamgmt.DmWorkbenchLayoutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.apilog.core.enums.OperateTypeEnum.UPDATE;
import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 工作台布局")
@RestController
@RequestMapping("/dynamicbusiness/data-mgmt/workbench-layout")
@Validated
public class DmWorkbenchLayoutController {

    @Resource
    private DmWorkbenchLayoutService dmWorkbenchLayoutService;

    @GetMapping("/{id}/settings")
    @Operation(summary = "读取工作台布局头设置")
    @PreAuthorize("@ss.hasPermission('system:entity-type:query')")
    public CommonResult<DmWorkbenchLayoutSettingsRespVO> getSettings(@PathVariable("id") Long id) {
        return success(dmWorkbenchLayoutService.getSettings(id));
    }

    @PutMapping("/{id}/settings")
    @Operation(summary = "更新工作台布局头的区段隐藏设置")
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:entity-type:update')")
    public CommonResult<DmWorkbenchLayoutSettingsRespVO> updateSettings(
            @PathVariable("id") Long id,
            @Valid @RequestBody DmWorkbenchLayoutSettingsUpdateReqVO reqVO) {
        return success(dmWorkbenchLayoutService.updateSettings(id, reqVO));
    }
}
