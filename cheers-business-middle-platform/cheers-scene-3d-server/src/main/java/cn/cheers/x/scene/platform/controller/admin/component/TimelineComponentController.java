package cn.cheers.x.scene.platform.controller.admin.component;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.scene.platform.controller.admin.component.vo.TimelineComponentSaveReqVO;
import cn.cheers.x.scene.platform.dal.dataobject.component.TimelineComponentDO;
import cn.cheers.x.scene.platform.service.component.TimelineComponentService;
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

@Tag(name = "管理后台 - Timeline 组件")
@RestController
@RequestMapping("/scene-platform/timeline-components")
@Validated
public class TimelineComponentController {

    @Resource
    private TimelineComponentService timelineComponentService;

    @GetMapping
    @Operation(summary = "获得 Timeline 组件列表")
    public CommonResult<List<TimelineComponentDO>> getTimelineComponentList() {
        return success(timelineComponentService.getTimelineComponentList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "获得 Timeline 组件详情")
    public CommonResult<TimelineComponentDO> getTimelineComponent(@PathVariable Long id) {
        return success(timelineComponentService.getTimelineComponent(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新 Timeline 组件")
    public CommonResult<Boolean> updateTimelineComponent(@PathVariable Long id,
                                                         @Valid @RequestBody TimelineComponentSaveReqVO reqVO) {
        timelineComponentService.updateTimelineComponent(id, reqVO.toDO());
        return success(true);
    }

    @PutMapping("/{id}/defaults")
    @Operation(summary = "更新 Timeline 组件默认值")
    public CommonResult<Boolean> updateTimelineComponentDefaults(@PathVariable Long id,
                                                                  @Valid @RequestBody TimelineComponentSaveReqVO reqVO) {
        timelineComponentService.updateTimelineComponentDefaults(id, reqVO.toDO());
        return success(true);
    }

    @PutMapping("/{id}/schema")
    @Operation(summary = "更新 Timeline 组件属性定义")
    public CommonResult<Boolean> updateTimelineComponentSchema(@PathVariable Long id,
                                                               @Valid @RequestBody TimelineComponentSaveReqVO reqVO) {
        timelineComponentService.updateTimelineComponentSchema(id, reqVO.toDO());
        return success(true);
    }
}
