package cn.iocoder.yudao.module.scene.platform.controller.admin.coordinate;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.scene.platform.controller.admin.coordinate.vo.CoordinateCrsCatalogRespVO;
import cn.iocoder.yudao.module.scene.platform.service.coordinate.CoordinateCrsCatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 常用坐标系目录")
@RestController
@RequestMapping("/scene-platform/coordinate/crs-catalog")
@Validated
public class CoordinateCrsCatalogController {

    @Resource
    private CoordinateCrsCatalogService coordinateCrsCatalogService;

    @GetMapping
    @Operation(summary = "获得常用坐标系目录")
    public CommonResult<List<CoordinateCrsCatalogRespVO>> getCatalogList(@RequestParam(value = "crsType", required = false) String crsType) {
        return success(coordinateCrsCatalogService.getEnabledCatalogList(crsType));
    }
}
