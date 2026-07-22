package cn.cheers.x.scene.platform.controller.admin.scene;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.scene.platform.controller.admin.scene.vo.FacilitySceneBindingRespVO;
import cn.cheers.x.scene.platform.service.scene.FacilitySceneBindingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 设施场景成员")
@RestController
@RequestMapping("/scene-3d/facility-scenes")
@Validated
public class FacilitySceneBindingController {

    @Resource
    private FacilitySceneBindingService facilitySceneBindingService;

    @GetMapping("/{facilityId}")
    @Operation(summary = "按设施编号查询所属场景")
    @Parameter(name = "facilityId", description = "设施编号（对内）", required = true)
    public CommonResult<FacilitySceneBindingRespVO> getByFacilityId(@PathVariable("facilityId") Long facilityId) {
        return success(facilitySceneBindingService.getByFacilityId(facilityId));
    }

    @GetMapping("/by-code/{facilityCode}")
    @Operation(summary = "按设施编码查询所属场景")
    @Parameter(name = "facilityCode", description = "设施编码（对外/导入导出）", required = true)
    public CommonResult<FacilitySceneBindingRespVO> getByFacilityCode(
            @PathVariable("facilityCode") String facilityCode) {
        return success(facilitySceneBindingService.getByFacilityCode(facilityCode));
    }

    @GetMapping("/by-scene/{sceneCode}")
    @Operation(summary = "按场景编码列出成员设施")
    @Parameter(name = "sceneCode", description = "场景编码", required = true)
    public CommonResult<List<FacilitySceneBindingRespVO>> listBySceneCode(
            @PathVariable("sceneCode") String sceneCode) {
        return success(facilitySceneBindingService.listBySceneCode(sceneCode));
    }
}
