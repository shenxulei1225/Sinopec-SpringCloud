package cn.cheers.x.scene.platform.service.basemap;

import cn.cheers.x.scene.platform.controller.admin.basemap.vo.GisProviderConnectionSaveReqVO;

/**
 * GIS 管理页保存地图服务连接。只写当前客户的 gis_provider_connection 表。
 * 不负责：读取回显（见 GisProviderConnectionQueryService）、取瓦片。
 */
public interface GisProviderConnectionService {

    /**
     * 保存一种地图服务，整行以本次提交为准，别的服务不动。
     * 天地图、Cesium 令牌为空，或本地瓦片服务地址模板为空，表示清除这一种。
     *
     * @param providerCode 地图服务编码，不认识时报错
     */
    void saveConnection(String providerCode, GisProviderConnectionSaveReqVO reqVO);
}
