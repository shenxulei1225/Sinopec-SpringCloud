package cn.cheers.x.module.dynamicbusiness.controller.admin.business;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.business.vo.*;
import cn.cheers.x.module.dynamicbusiness.service.business.BusinessService;
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

@Tag(name = "管理后台 - 门户业务", description = "门户业务管理（与实体类型分层）")
@RestController
@RequestMapping("/dynamicbusiness/business")
@Validated
public class BusinessController {

    @Resource
    private BusinessService businessService;

    @PostMapping("/create")
    @Operation(summary = "创建门户业务", description = "仅创建业务元数据，不创建实体类型")
    @ApiAccessLog(operateType = CREATE)
    @PreAuthorize("@ss.hasPermission('system:business:create')")
    public CommonResult<Long> create(@Valid @RequestBody BusinessCreateReqVO reqVO) {
        return success(businessService.create(reqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新门户业务")
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:business:update')")
    public CommonResult<Boolean> update(@Valid @RequestBody BusinessUpdateReqVO reqVO) {
        businessService.update(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除门户业务")
    @Parameter(name = "id", description = "业务编号", required = true)
    @ApiAccessLog(operateType = DELETE)
    @PreAuthorize("@ss.hasPermission('system:business:delete')")
    public CommonResult<Boolean> delete(@RequestParam("id") Long id) {
        businessService.delete(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取门户业务详情")
    @PreAuthorize("@ss.hasPermission('system:business:query')")
    public CommonResult<BusinessRespVO> get(@RequestParam("id") Long id) {
        return success(businessService.get(id));
    }

    @GetMapping("/get-by-code")
    @Operation(summary = "按业务编码获取详情")
    @PreAuthorize("@ss.hasPermission('system:business:query')")
    public CommonResult<BusinessRespVO> getByCode(@RequestParam("code") String code) {
        return success(businessService.getByCode(code));
    }

    @GetMapping("/list-tree")
    @Operation(summary = "门户业务树")
    @PreAuthorize("@ss.hasPermission('system:business:query')")
    public CommonResult<List<BusinessRespVO>> listTree() {
        return success(businessService.listTree());
    }

    @GetMapping("/list-all")
    @Operation(summary = "门户业务平铺列表")
    @PreAuthorize("@ss.hasPermission('system:business:query')")
    public CommonResult<List<BusinessRespVO>> listAll() {
        return success(businessService.listAll());
    }

    @PutMapping("/update-status")
    @Operation(summary = "更新门户业务状态")
    @PreAuthorize("@ss.hasPermission('system:business:update')")
    public CommonResult<Boolean> updateStatus(@RequestParam("id") Long id,
                                              @RequestParam("status") String status) {
        businessService.updateStatus(id, status);
        return success(true);
    }
}
