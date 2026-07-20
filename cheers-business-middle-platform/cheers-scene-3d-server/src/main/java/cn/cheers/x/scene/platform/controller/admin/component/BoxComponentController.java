package cn.cheers.x.scene.platform.controller.admin.component;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.scene.platform.controller.admin.component.vo.BoxComponentSaveReqVO;
import cn.cheers.x.scene.platform.dal.dataobject.component.BoxComponentDO;
import cn.cheers.x.scene.platform.service.component.BoxComponentService;
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

@Tag(name = "管理后台 - Box 组件")
@RestController
@RequestMapping("/scene-3d/box-components")
@Validated
public class BoxComponentController {

    @Resource
    private BoxComponentService boxComponentService;

    @GetMapping
    @Operation(summary = "获得 Box 组件列表")
    public CommonResult<List<BoxComponentDO>> getBoxComponentList() {
        return success(boxComponentService.getBoxComponentList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "获得 Box 组件详情")
    public CommonResult<BoxComponentDO> getBoxComponent(@PathVariable Long id) {
        return success(boxComponentService.getBoxComponent(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新 Box 组件")
    public CommonResult<Boolean> updateBoxComponent(@PathVariable Long id,
                                                    @Valid @RequestBody BoxComponentSaveReqVO reqVO) {
        boxComponentService.updateBoxComponent(id, reqVO.toDO());
        return success(true);
    }

    @PutMapping("/{id}/defaults")
    @Operation(summary = "更新 Box 组件默认值")
    public CommonResult<Boolean> updateBoxComponentDefaults(@PathVariable Long id,
                                                             @Valid @RequestBody BoxComponentSaveReqVO reqVO) {
        boxComponentService.updateBoxComponentDefaults(id, reqVO.toDO());
        return success(true);
    }

    @PutMapping("/{id}/schema")
    @Operation(summary = "更新 Box 组件属性定义")
    public CommonResult<Boolean> updateBoxComponentSchema(@PathVariable Long id,
                                                          @Valid @RequestBody BoxComponentSaveReqVO reqVO) {
        boxComponentService.updateBoxComponentSchema(id, reqVO.toDO());
        return success(true);
    }
}
