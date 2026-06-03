package cn.cheers.x.module.platformresource.controller.admin.view;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.cheers.x.module.platformresource.controller.admin.view.vo.*;
import cn.cheers.x.module.platformresource.service.view.ViewConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 视图配置")
@RestController
@RequestMapping("/platformresource/view-config")
public class ViewConfigController {

    @Resource
    private ViewConfigService viewConfigService;

    @GetMapping("/list")
    @Operation(summary = "视图配置列表（按 viewType / isTemplate 筛选）")
    public CommonResult<List<ViewConfigRespVO>> getViewConfigList(@Valid ViewConfigListReqVO reqVO) {
        return success(viewConfigService.getViewConfigList(reqVO));
    }

    @GetMapping("/{viewId}")
    @Operation(summary = "按 viewId 获取视图配置（返回 resolvedConfig）")
    public CommonResult<ViewConfigRespVO> getViewConfig(@PathVariable("viewId") Long viewId) {
        return success(viewConfigService.getViewConfig(viewId));
    }

    @GetMapping("/by-code")
    @Operation(summary = "按 viewCode 获取视图配置")
    public CommonResult<ViewConfigRespVO> getViewConfigByCode(@RequestParam("code") String code) {
        return success(viewConfigService.getViewConfigByCode(code));
    }

    @PostMapping("/template")
    @Operation(summary = "创建视图模板")
    public CommonResult<Long> createTemplate(@Valid @RequestBody ViewConfigCreateTemplateReqVO reqVO) {
        return success(viewConfigService.createTemplate(reqVO));
    }

    @PostMapping("/instance")
    @Operation(summary = "基于视图模板创建实例")
    public CommonResult<Long> createInstance(@Valid @RequestBody ViewConfigCreateInstanceReqVO reqVO) {
        return success(viewConfigService.createInstance(reqVO));
    }

    @PutMapping("/{viewId}")
    @Operation(summary = "保存视图配置（模板写 configJson，实例写 configOverride）")
    public CommonResult<Boolean> saveViewConfig(
            @PathVariable("viewId") Long viewId,
            @Valid @RequestBody ViewConfigSaveReqVO reqVO) {
        viewConfigService.saveViewConfig(viewId, reqVO);
        return success(true);
    }

    @DeleteMapping("/{viewId}")
    @Operation(summary = "删除视图配置（模板有实例时禁止删除）")
    public CommonResult<Boolean> deleteViewConfig(@PathVariable("viewId") Long viewId) {
        viewConfigService.deleteViewConfig(viewId);
        return success(true);
    }
}
