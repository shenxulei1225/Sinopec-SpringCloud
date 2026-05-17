package cn.iocoder.yudao.module.scene.platform.service.geo;

import cn.iocoder.yudao.module.scene.platform.controller.admin.geo.vo.GeoLayerDetailRespVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.geo.vo.GeoLayerRespVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.geo.vo.GeoLayerSaveReqVO;

import java.util.List;

public interface GeoLayerConfigService {

    List<GeoLayerRespVO> getSceneGeoLayers(String sceneCode);

    List<GeoLayerRespVO> getSceneGeoLayersBySceneId(Long sceneId);

    Long createSceneGeoLayer(String sceneCode, GeoLayerSaveReqVO reqVO);

    void updateSceneGeoLayer(String sceneCode, String layerKey, GeoLayerSaveReqVO reqVO);

    void deleteSceneGeoLayer(String sceneCode, String layerKey);

    GeoLayerDetailRespVO getSceneGeoLayerDetail(String sceneCode, String layerKey);
}
