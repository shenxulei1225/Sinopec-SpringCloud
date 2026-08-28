package cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt;

import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmDataTabLayoutBundleRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmDataTabLayoutRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmDataTabLayoutSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.service.datamgmt.DmDataTabLayoutService;
import cn.cheers.x.framework.apilog.core.annotation.ApiAccessLog;
import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.cheers.x.framework.apilog.core.enums.OperateTypeEnum.UPDATE;
import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 数据 Tab 布局")
@RestController
@RequestMapping("/dynamicbusiness/data-mgmt/data-tab-layout")
@Validated
public class DmDataTabLayoutController {

    @Resource
    private DmDataTabLayoutService dmDataTabLayoutService;

    @GetMapping
    @Operation(summary = "按布局 id 或目录编码查询工作台栏布局")
    @Parameter(name = "layoutId", description = "工作台布局实例/模版 id（优先）")
    @Parameter(name = "entityTypeCode", description = "目录编码；无 layoutId 时解析其 dataLayoutId")
    @Parameter(name = "includeColumnRelations", description = "true 时返回 layouts + columnRelations 合并包，少一次栏间关系 GET")
    @PreAuthorize("@ss.hasPermission('system:entity-type:query')")
    public CommonResult<?> list(
            @RequestParam(value = "layoutId", required = false) Long layoutId,
            @RequestParam(value = "entityTypeCode", required = false) String entityTypeCode,
            @RequestParam(value = "includeColumnRelations", required = false, defaultValue = "false")
            boolean includeColumnRelations) {
        if (includeColumnRelations) {
            if (layoutId != null) {
                return success(dmDataTabLayoutService.listBundleByLayoutId(layoutId));
            }
            if (StringUtils.hasText(entityTypeCode)) {
                return success(dmDataTabLayoutService.listBundleByEntityTypeCode(entityTypeCode));
            }
            throw new ServiceException(400, "layoutId 与 entityTypeCode 不能同时为空");
        }
        if (layoutId != null) {
            return success(dmDataTabLayoutService.listByLayoutId(layoutId));
        }
        if (StringUtils.hasText(entityTypeCode)) {
            return success(dmDataTabLayoutService.listByEntityTypeCode(entityTypeCode));
        }
        throw new ServiceException(400, "layoutId 与 entityTypeCode 不能同时为空");
    }

    @PutMapping
    @Operation(summary = "批量保存数据 Tab 布局（写实例，禁写模版）")
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:entity-type:update')")
    public CommonResult<Boolean> save(@Valid @RequestBody DmDataTabLayoutSaveReqVO reqVO) {
        dmDataTabLayoutService.saveLayouts(reqVO);
        return success(true);
    }
}
