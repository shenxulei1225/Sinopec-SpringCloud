package cn.cheers.x.module.platformresource.controller.admin.component;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.cheers.x.module.platformresource.controller.admin.component.vo.*;
import cn.cheers.x.module.platformresource.service.component.ComponentPropsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 组件 Props")
@RestController
@RequestMapping("/platformresource/component-props")
public class ComponentPropsController {

    @Resource
    private ComponentPropsService componentPropsService;

    @GetMapping("/list")
    @Operation(summary = "Props 列表（按 componentCode、isTemplate 筛选）")
    public CommonResult<List<ComponentPropsRespVO>> getPropsList(@Valid ComponentPropsListReqVO reqVO) {
        return success(componentPropsService.getPropsList(reqVO));
    }

    @GetMapping("/{propsId}")
    @Operation(summary = "按 propsId 获取 Props（模板或实例）")
    public CommonResult<ComponentPropsRespVO> getProps(@PathVariable("propsId") Long propsId) {
        return success(componentPropsService.getProps(propsId));
    }

    @PostMapping("/template")
    @Operation(summary = "创建 Props 模板")
    public CommonResult<Long> createTemplate(@Valid @RequestBody ComponentPropsCreateTemplateReqVO reqVO) {
        return success(componentPropsService.createTemplate(reqVO));
    }

    @PostMapping("/instance")
    @Operation(summary = "基于模板创建 Props 实例")
    public CommonResult<Long> createInstance(@Valid @RequestBody ComponentPropsCreateInstanceReqVO reqVO) {
        return success(componentPropsService.createInstance(reqVO));
    }

    @PutMapping("/{propsId}")
    @Operation(summary = "保存 Props（模板写 propsJson，实例写 propsOverride）")
    public CommonResult<Boolean> saveProps(
            @PathVariable("propsId") Long propsId,
            @Valid @RequestBody ComponentPropsSaveReqVO reqVO) {
        componentPropsService.saveProps(propsId, reqVO);
        return success(true);
    }

    @DeleteMapping("/{propsId}")
    @Operation(summary = "删除 Props")
    public CommonResult<Boolean> deleteProps(@PathVariable("propsId") Long propsId) {
        componentPropsService.deleteProps(propsId);
        return success(true);
    }
}
