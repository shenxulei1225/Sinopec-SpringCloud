package cn.cheers.x.scene.platform.controller.admin.geo;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.scene.platform.controller.admin.geo.vo.GeoLayerDetailRespVO;
import cn.cheers.x.scene.platform.controller.admin.geo.vo.GeoLayerRespVO;
import cn.cheers.x.scene.platform.controller.admin.geo.vo.GeoLayerSaveReqVO;
import cn.cheers.x.scene.platform.service.geo.GeoLayerConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - GEO 图层配置")
@RestController
@RequestMapping("/gis/scenes/{sceneCode}/geo-layers")
@Validated
@ConditionalOnProperty(value = "scene.geo.enabled", havingValue = "true")
public class GeoLayerConfigController {

    @Resource
    private GeoLayerConfigService geoLayerConfigService;

    @GetMapping
    @Operation(summary = "获得场景 GEO 图层列表")
    public CommonResult<List<GeoLayerRespVO>> getSceneGeoLayers(@PathVariable String sceneCode) {
        return success(geoLayerConfigService.getSceneGeoLayers(sceneCode));
    }

    @GetMapping("/{layerKey}")
    @Operation(summary = "获得场景 GEO 图层详情")
    public CommonResult<GeoLayerDetailRespVO> getSceneGeoLayerDetail(@PathVariable String sceneCode,
                                                                     @PathVariable String layerKey) {
        return success(geoLayerConfigService.getSceneGeoLayerDetail(sceneCode, layerKey));
    }

    @PostMapping
    @Operation(summary = "创建场景 GEO 图层")
    public CommonResult<Long> createSceneGeoLayer(@PathVariable String sceneCode,
                                                  @Valid @RequestBody GeoLayerSaveReqVO reqVO) {
        return success(geoLayerConfigService.createSceneGeoLayer(sceneCode, reqVO));
    }

    @PutMapping("/{layerKey}")
    @Operation(summary = "更新场景 GEO 图层")
    public CommonResult<Boolean> updateSceneGeoLayer(@PathVariable String sceneCode,
                                                     @PathVariable String layerKey,
                                                     @Valid @RequestBody GeoLayerSaveReqVO reqVO) {
        geoLayerConfigService.updateSceneGeoLayer(sceneCode, layerKey, reqVO);
        return success(Boolean.TRUE);
    }

    @DeleteMapping("/{layerKey}")
    @Operation(summary = "删除场景 GEO 图层")
    public CommonResult<Boolean> deleteSceneGeoLayer(@PathVariable String sceneCode,
                                                     @PathVariable String layerKey) {
        geoLayerConfigService.deleteSceneGeoLayer(sceneCode, layerKey);
        return success(Boolean.TRUE);
    }
}
