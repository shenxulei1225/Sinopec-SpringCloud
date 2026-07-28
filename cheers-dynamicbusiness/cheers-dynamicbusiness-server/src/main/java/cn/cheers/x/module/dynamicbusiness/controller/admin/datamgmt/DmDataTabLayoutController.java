package cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt;

import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmDataTabLayoutRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmDataTabLayoutSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.service.datamgmt.DmDataTabLayoutService;
import cn.cheers.x.framework.apilog.core.annotation.ApiAccessLog;
import cn.cheers.x.framework.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @Operation(summary = "按 entityTypeCode 查询数据 Tab 布局")
    @Parameter(name = "entityTypeCode", description = "数据类型编码", required = true)
    @PreAuthorize("@ss.hasPermission('system:entity-type:query')")
    public CommonResult<List<DmDataTabLayoutRespVO>> list(@RequestParam("entityTypeCode") String entityTypeCode) {
        return success(dmDataTabLayoutService.listByEntityTypeCode(entityTypeCode));
    }

    @PutMapping
    @Operation(summary = "批量保存数据 Tab 布局")
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:entity-type:update')")
    public CommonResult<Boolean> save(@Valid @RequestBody DmDataTabLayoutSaveReqVO reqVO) {
        dmDataTabLayoutService.saveLayouts(reqVO);
        return success(true);
    }
}
