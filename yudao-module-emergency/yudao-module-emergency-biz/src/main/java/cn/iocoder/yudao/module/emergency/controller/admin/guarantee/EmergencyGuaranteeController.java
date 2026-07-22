package cn.iocoder.yudao.module.emergency.controller.admin.guarantee;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emergency.controller.admin.guarantee.vo.*;
import cn.iocoder.yudao.module.emergency.service.guarantee.EmergencyGuaranteeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@RestController
@RequestMapping("/emergency/guarantees")
@Tag(name = "管理后台 - 应急保障")
public class EmergencyGuaranteeController {

    @Resource
    private EmergencyGuaranteeService guaranteeService;

    @PostMapping
    @Operation(summary = "创建应急保障")
    public CommonResult<Long> createGuarantee(@Valid @RequestBody GuaranteeCreateReqVO createReqVO) {
        return success(guaranteeService.createGuarantee(createReqVO));
    }

    @PutMapping
    @Operation(summary = "更新应急保障")
    public CommonResult<Boolean> updateGuarantee(@Valid @RequestBody GuaranteeUpdateReqVO updateReqVO) {
        guaranteeService.updateGuarantee(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除应急保障")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteGuarantee(@PathVariable("id") Long id) {
        guaranteeService.deleteGuarantee(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得应急保障")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<GuaranteeRespVO> getGuarantee(@RequestParam("id") Long id) {
        GuaranteeRespVO guarantee = guaranteeService.getGuarantee(id);
        return success(guarantee);
    }

    @GetMapping("/page")
    @Operation(summary = "获得应急保障分页")
    public CommonResult<PageResult<GuaranteeRespVO>> getGuaranteePage(@Valid GuaranteePageReqVO pageReqVO) {
        PageResult<GuaranteeRespVO> pageResult = guaranteeService.getGuaranteePage(pageReqVO);
        return success(pageResult);
    }

    @PostMapping("/resources")
    @Operation(summary = "添加保障资源")
    public CommonResult<Long> addGuaranteeResource(@Valid @RequestBody GuaranteeResourceCreateReqVO createReqVO) {
        return success(guaranteeService.addGuaranteeResource(createReqVO));
    }

    @DeleteMapping("/resources/{id}")
    @Operation(summary = "删除保障资源")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> removeGuaranteeResource(@PathVariable("id") Long id) {
        guaranteeService.removeGuaranteeResource(id);
        return success(true);
    }
}



