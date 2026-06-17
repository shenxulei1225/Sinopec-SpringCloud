package cn.cheers.x.module.platformresource.controller.admin.component;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.cheers.x.module.platformresource.controller.admin.component.vo.ComponentCreateReqVO;
import cn.cheers.x.module.platformresource.controller.admin.component.vo.ComponentRespVO;
import cn.cheers.x.module.platformresource.controller.admin.component.vo.ComponentUpdateReqVO;
import cn.cheers.x.module.platformresource.service.component.ComponentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 组件库")
@RestController
@RequestMapping("/platformresource/components")
public class ComponentController {

    @Resource
    private ComponentService componentService;

    @GetMapping
    @Operation(summary = "获取所有启用的组件定义")
    public CommonResult<Map<String, ComponentRespVO>> getEnabledComponents() {
        return success(componentService.getEnabledComponents());
    }

    @GetMapping("/list")
    @Operation(summary = "管理后台 - 组件列表（含禁用）")
    public CommonResult<List<ComponentRespVO>> getComponentList() {
        return success(componentService.getComponentList());
    }

    @GetMapping("/{componentCode}")
    @Operation(summary = "获取组件定义详情")
    public CommonResult<ComponentRespVO> getComponent(@PathVariable("componentCode") String componentCode) {
        return success(componentService.getComponent(componentCode));
    }

    @PostMapping
    @Operation(summary = "创建组件定义")
    public CommonResult<Long> createComponent(@Valid @RequestBody ComponentCreateReqVO reqVO) {
        return success(componentService.createComponent(reqVO));
    }

    @PutMapping("/{componentCode}")
    @Operation(summary = "更新组件定义")
    public CommonResult<Boolean> updateComponent(
            @PathVariable("componentCode") String componentCode,
            @Valid @RequestBody ComponentUpdateReqVO reqVO) {
        componentService.updateComponent(componentCode, reqVO);
        return success(true);
    }

    @DeleteMapping("/{componentCode}")
    @Operation(summary = "删除组件定义")
    public CommonResult<Boolean> deleteComponent(@PathVariable("componentCode") String componentCode) {
        componentService.deleteComponent(componentCode);
        return success(true);
    }
}
