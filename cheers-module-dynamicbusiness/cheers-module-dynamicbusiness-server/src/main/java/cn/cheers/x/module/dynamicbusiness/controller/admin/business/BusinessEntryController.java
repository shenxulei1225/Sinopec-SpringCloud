package cn.cheers.x.module.dynamicbusiness.controller.admin.business;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.business.vo.BusinessEntryCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.business.vo.BusinessEntryRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.business.vo.BusinessEntryUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.service.business.BusinessEntryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.*;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 业务入口（已废弃）", description = "请使用功能页面 PageConfig；本 API 仅兼容存量")
@RestController
@RequestMapping("/dynamicbusiness/business-entry")
@Validated
@Deprecated
public class BusinessEntryController {

    @Resource
    private BusinessEntryService businessEntryService;

    @PostMapping("/create")
    @Operation(summary = "创建业务入口")
    @ApiAccessLog(operateType = CREATE)
    @PreAuthorize("@ss.hasPermission('system:business:update')")
    public CommonResult<Long> create(@Valid @RequestBody BusinessEntryCreateReqVO reqVO) {
        return success(businessEntryService.create(reqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新业务入口")
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:business:update')")
    public CommonResult<Boolean> update(@Valid @RequestBody BusinessEntryUpdateReqVO reqVO) {
        businessEntryService.update(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除业务入口")
    @PreAuthorize("@ss.hasPermission('system:business:update')")
    public CommonResult<Boolean> delete(@RequestParam("id") Long id) {
        businessEntryService.delete(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取业务入口详情")
    @PreAuthorize("@ss.hasPermission('system:business:query')")
    public CommonResult<BusinessEntryRespVO> get(@RequestParam("id") Long id) {
        return success(businessEntryService.get(id));
    }

    @GetMapping("/list-by-business")
    @Operation(summary = "按业务编号列出入口")
    @Parameter(name = "businessId", description = "业务编号", required = true)
    @PreAuthorize("@ss.hasPermission('system:business:query')")
    public CommonResult<List<BusinessEntryRespVO>> listByBusiness(@RequestParam("businessId") Long businessId) {
        return success(businessEntryService.listByBusinessId(businessId));
    }
}
