package cn.iocoder.yudao.module.scene.platform.controller.admin.scene;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.scene.platform.controller.admin.scene.vo.FacilitySceneBindingRespVO;
import cn.iocoder.yudao.module.scene.platform.service.scene.FacilitySceneBindingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 设施场景绑定")
@RestController
@RequestMapping("/scene-platform/facility-scenes")
@Validated
public class FacilitySceneBindingController {

    @Resource
    private FacilitySceneBindingService facilitySceneBindingService;

    @GetMapping("/{facilityId}")
    @Operation(summary = "按设施编号查询场景绑定")
    @Parameter(name = "facilityId", description = "设施编号", required = true)
    public CommonResult<FacilitySceneBindingRespVO> getByFacilityId(@PathVariable("facilityId") Long facilityId) {
        return success(facilitySceneBindingService.getByFacilityId(facilityId));
    }
}
