package cn.cheers.x.maintenance.controller.admin.binding;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.maintenance.api.dto.BindingResolveReqDTO;
import cn.cheers.x.maintenance.api.dto.BindingResolveRespDTO;
import cn.cheers.x.maintenance.controller.admin.vo.binding.*;
import cn.cheers.x.maintenance.service.binding.BindingRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 绑定规则")
@RestController
@RequestMapping("/maintenance/binding-rules")
@Validated
public class BindingRuleController {

    @Resource private BindingRuleService bindingRuleService;

    @PostMapping
    @PreAuthorize("@ss.hasPermission('maintenance:binding:create')")
    public CommonResult<Long> create(@Valid @RequestBody BindingRuleCreateReqVO reqVO) {
        return success(bindingRuleService.create(reqVO));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@ss.hasPermission('maintenance:binding:update')")
    public CommonResult<Boolean> update(@PathVariable Long id, @Valid @RequestBody BindingRuleUpdateReqVO reqVO) {
        bindingRuleService.update(id, reqVO);
        return success(true);
    }

    @GetMapping("/page")
    @PreAuthorize("@ss.hasPermission('maintenance:binding:query')")
    public CommonResult<PageResult<BindingRuleRespVO>> page(@Valid BindingRulePageReqVO reqVO) {
        return success(bindingRuleService.page(reqVO));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@ss.hasPermission('maintenance:binding:query')")
    public CommonResult<BindingRuleRespVO> get(@PathVariable Long id) {
        return success(bindingRuleService.get(id));
    }

    @PostMapping("/{id}/publish")
    @PreAuthorize("@ss.hasPermission('maintenance:binding:publish')")
    public CommonResult<Long> publish(@PathVariable Long id) {
        return success(bindingRuleService.publish(id));
    }

    @PostMapping("/resolve-preview")
    @Operation(summary = "绑定解析预览（管理端）")
    @PreAuthorize("@ss.hasPermission('maintenance:binding:query')")
    public CommonResult<BindingResolveRespDTO> resolvePreview(@Valid @RequestBody BindingResolvePreviewReqVO reqVO) {
        return success(bindingRuleService.resolve(BindingResolveReqDTO.builder()
                .assetId(reqVO.getAssetId())
                .assetTypeCode(reqVO.getAssetTypeCode())
                .frequencyCode(reqVO.getFrequencyCode())
                .scope(reqVO.getScope())
                .build()));
    }
}
