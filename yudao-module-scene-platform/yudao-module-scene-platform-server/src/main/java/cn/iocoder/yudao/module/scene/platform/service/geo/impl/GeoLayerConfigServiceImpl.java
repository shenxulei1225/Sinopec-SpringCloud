package cn.iocoder.yudao.module.scene.platform.service.geo.impl;

import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.module.scene.platform.controller.admin.geo.vo.GeoLayerDetailRespVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.geo.vo.GeoLayerRespVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.geo.vo.GeoLayerSaveReqVO;
import cn.iocoder.yudao.module.scene.platform.service.geo.GeoLayerConfigService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.BAD_REQUEST;

/**
 * 临时关闭 GEO 图层实现，避免阻塞当前 facility ↔ actorInstance 主线联调。
 * 后续恢复 GEO 子系统时，再补齐 SceneAssetBinding 等缺失模型后启用。
 */
@Service
@ConditionalOnProperty(value = "scene.geo.enabled", havingValue = "true")
public class GeoLayerConfigServiceImpl implements GeoLayerConfigService {

    private RuntimeException disabled() {
        return ServiceExceptionUtil.exception(BAD_REQUEST, "GEO 图层能力当前未启用");
    }

    @Override
    public List<GeoLayerRespVO> getSceneGeoLayers(String sceneCode) {
        throw disabled();
    }

    @Override
    public List<GeoLayerRespVO> getSceneGeoLayersBySceneId(Long sceneId) {
        throw disabled();
    }

    @Override
    public Long createSceneGeoLayer(String sceneCode, GeoLayerSaveReqVO reqVO) {
        throw disabled();
    }

    @Override
    public void updateSceneGeoLayer(String sceneCode, String layerKey, GeoLayerSaveReqVO reqVO) {
        throw disabled();
    }

    @Override
    public void deleteSceneGeoLayer(String sceneCode, String layerKey) {
        throw disabled();
    }

    @Override
    public GeoLayerDetailRespVO getSceneGeoLayerDetail(String sceneCode, String layerKey) {
        throw disabled();
    }
}
