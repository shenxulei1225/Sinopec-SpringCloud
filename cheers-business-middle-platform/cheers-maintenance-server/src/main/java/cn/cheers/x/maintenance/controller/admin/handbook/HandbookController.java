package cn.cheers.x.maintenance.controller.admin.handbook;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.maintenance.controller.admin.vo.handbook.*;
import cn.cheers.x.maintenance.service.handbook.HandbookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 维护手册")
@RestController
@RequestMapping("/maintenance/handbooks")
@Validated
public class HandbookController {

    @Resource
    private HandbookService handbookService;

    @PostMapping
    @Operation(summary = "创建维护手册（草稿）")
    @PreAuthorize("@ss.hasPermission('maintenance:handbook:create')")
    public CommonResult<Long> create(@Valid @RequestBody HandbookCreateReqVO createReqVO) {
        return success(handbookService.createHandbook(createReqVO));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@ss.hasPermission('maintenance:handbook:update')")
    public CommonResult<Boolean> update(@PathVariable("id") Long id,
                                        @Valid @RequestBody HandbookUpdateReqVO updateReqVO) {
        handbookService.updateHandbook(id, updateReqVO);
        return success(true);
    }

    @GetMapping("/page")
    @PreAuthorize("@ss.hasPermission('maintenance:handbook:query')")
    public CommonResult<PageResult<HandbookRespVO>> page(@Valid HandbookPageReqVO pageReqVO) {
        return success(handbookService.getHandbookPage(pageReqVO));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@ss.hasPermission('maintenance:handbook:query')")
    public CommonResult<HandbookRespVO> get(@PathVariable("id") Long id) {
        return success(handbookService.getHandbook(id));
    }

    @PostMapping("/{id}/publish")
    @PreAuthorize("@ss.hasPermission('maintenance:handbook:publish')")
    public CommonResult<Long> publish(@PathVariable("id") Long id) {
        return success(handbookService.publishHandbook(id));
    }
}
