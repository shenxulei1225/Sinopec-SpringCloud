package cn.cheers.x.maintenance.controller.admin;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.maintenance.controller.admin.vo.standard.*;
import cn.cheers.x.maintenance.service.standard.FieldWorkStandardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 现场作业标准")
@RestController
@RequestMapping("/maintenance/standards")
@Validated
public class FieldWorkStandardController {

    @Resource
    private FieldWorkStandardService fieldWorkStandardService;

    @PostMapping
    @Operation(summary = "创建现场作业标准（草稿）")
    @PreAuthorize("@ss.hasPermission('maintenance:standard:create')")
    public CommonResult<Long> createStandard(@Valid @RequestBody FieldWorkStandardCreateReqVO createReqVO) {
        return success(fieldWorkStandardService.createStandard(createReqVO));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@ss.hasPermission('maintenance:standard:update')")
    public CommonResult<Boolean> updateStandard(@PathVariable("id") Long id,
                                                @Valid @RequestBody FieldWorkStandardUpdateReqVO updateReqVO) {
        fieldWorkStandardService.updateStandard(id, updateReqVO);
        return success(true);
    }

    @GetMapping("/page")
    @PreAuthorize("@ss.hasPermission('maintenance:standard:query')")
    public CommonResult<PageResult<FieldWorkStandardRespVO>> getStandardPage(@Valid FieldWorkStandardPageReqVO pageReqVO) {
        return success(fieldWorkStandardService.getStandardPage(pageReqVO));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@ss.hasPermission('maintenance:standard:query')")
    public CommonResult<FieldWorkStandardRespVO> getStandard(@PathVariable("id") Long id) {
        return success(fieldWorkStandardService.getStandard(id));
    }

    @PostMapping("/{id}/publish")
    @PreAuthorize("@ss.hasPermission('maintenance:standard:publish')")
    public CommonResult<Long> publishStandard(@PathVariable("id") Long id) {
        return success(fieldWorkStandardService.publishStandard(id));
    }
}
