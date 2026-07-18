package cn.cheers.x.scene.platform.controller.admin.component;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.scene.platform.controller.admin.component.vo.WidgetComponentRespVO;
import cn.cheers.x.scene.platform.controller.admin.component.vo.WidgetComponentSaveReqVO;
import cn.cheers.x.scene.platform.dal.dataobject.component.WidgetComponentDO;
import cn.cheers.x.scene.platform.service.component.WidgetComponentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - Widget 组件")
@RestController
@RequestMapping("/scene-platform/widget-components")
@Validated
public class WidgetComponentController {

    @Resource
    private WidgetComponentService widgetComponentService;

    @GetMapping
    @Operation(summary = "获得 Widget 组件列表")
    public CommonResult<List<WidgetComponentRespVO>> getWidgetComponentList() {
        return success(widgetComponentService.getWidgetComponentList().stream().map(this::convert).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "获得 Widget 组件详情")
    public CommonResult<WidgetComponentRespVO> getWidgetComponent(@PathVariable Long id) {
        return success(convert(widgetComponentService.getWidgetComponent(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新 Widget 组件")
    public CommonResult<Boolean> updateWidgetComponent(@PathVariable Long id,
                                                       @Valid @RequestBody WidgetComponentSaveReqVO reqVO) {
        widgetComponentService.updateWidgetComponent(id, reqVO.toDO());
        return success(true);
    }

    @PutMapping("/{id}/defaults")
    @Operation(summary = "更新 Widget 组件默认值")
    public CommonResult<Boolean> updateWidgetComponentDefaults(@PathVariable Long id,
                                                                @Valid @RequestBody WidgetComponentSaveReqVO reqVO) {
        widgetComponentService.updateWidgetComponentDefaults(id, reqVO.toDO());
        return success(true);
    }

    @PutMapping("/{id}/schema")
    @Operation(summary = "更新 Widget 组件属性定义")
    public CommonResult<Boolean> updateWidgetComponentSchema(@PathVariable Long id,
                                                             @Valid @RequestBody WidgetComponentSaveReqVO reqVO) {
        widgetComponentService.updateWidgetComponentSchema(id, reqVO.toDO());
        return success(true);
    }

    private WidgetComponentRespVO convert(WidgetComponentDO item) {
        WidgetComponentRespVO vo = new WidgetComponentRespVO();
        vo.setId(item.getId());
        vo.setComponentCode(item.getComponentCode());
        vo.setDisplayName(item.getDisplayName());
        vo.setWidgetCode(item.getWidgetCode());
        vo.setUiConfigJson(item.getUiConfigJson());
        vo.setDrawAtDesiredSize(item.getDrawAtDesiredSize());
        vo.setReceiveHardwareInput(item.getReceiveHardwareInput());
        vo.setMetadataJson(item.getMetadataJson());
        return vo;
    }
}
