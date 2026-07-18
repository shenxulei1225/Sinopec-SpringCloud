package cn.iocoder.yudao.module.scene.platform.controller.admin.component;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.scene.platform.controller.admin.component.vo.CameraComponentRespVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.component.vo.CameraComponentSaveReqVO;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.component.CameraComponentDO;
import cn.iocoder.yudao.module.scene.platform.service.component.CameraComponentService;
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

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - Camera 组件")
@RestController
@RequestMapping("/scene-platform/camera-components")
@Validated
public class CameraComponentController {

    @Resource
    private CameraComponentService cameraComponentService;

    @GetMapping
    @Operation(summary = "获得 Camera 组件列表")
    public CommonResult<List<CameraComponentRespVO>> getCameraComponentList() {
        return success(cameraComponentService.getCameraComponentList().stream().map(this::convert).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "获得 Camera 组件详情")
    public CommonResult<CameraComponentRespVO> getCameraComponent(@PathVariable Long id) {
        return success(convert(cameraComponentService.getCameraComponent(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新 Camera 组件")
    public CommonResult<Boolean> updateCameraComponent(@PathVariable Long id,
                                                       @Valid @RequestBody CameraComponentSaveReqVO reqVO) {
        cameraComponentService.updateCameraComponent(id, reqVO.toDO());
        return success(true);
    }

    @PutMapping("/{id}/defaults")
    @Operation(summary = "更新 Camera 组件默认值")
    public CommonResult<Boolean> updateCameraComponentDefaults(@PathVariable Long id,
                                                               @Valid @RequestBody CameraComponentSaveReqVO reqVO) {
        cameraComponentService.updateCameraComponentDefaults(id, reqVO.toDO());
        return success(true);
    }

    @PutMapping("/{id}/schema")
    @Operation(summary = "更新 Camera 组件属性定义")
    public CommonResult<Boolean> updateCameraComponentSchema(@PathVariable Long id,
                                                              @Valid @RequestBody CameraComponentSaveReqVO reqVO) {
        cameraComponentService.updateCameraComponentSchema(id, reqVO.toDO());
        return success(true);
    }

    private CameraComponentRespVO convert(CameraComponentDO item) {
        CameraComponentRespVO vo = new CameraComponentRespVO();
        vo.setId(item.getId());
        vo.setComponentCode(item.getComponentCode());
        vo.setDisplayName(item.getDisplayName());
        vo.setFieldOfView(item.getFieldOfView());
        vo.setAspectRatio(item.getAspectRatio());
        vo.setNearClipPlane(item.getNearClipPlane());
        vo.setFarClipPlane(item.getFarClipPlane());
        vo.setConstrainAspectRatio(item.getConstrainAspectRatio());
        vo.setAutoActivate(item.getAutoActivate());
        vo.setMetadataJson(item.getMetadataJson());
        return vo;
    }
}
