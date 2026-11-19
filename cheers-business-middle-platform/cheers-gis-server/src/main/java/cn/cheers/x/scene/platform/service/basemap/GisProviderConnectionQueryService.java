package cn.cheers.x.scene.platform.service.basemap;

import cn.cheers.x.scene.platform.controller.admin.basemap.vo.GisProviderConnectionRespVO;

import java.util.List;

/**
 * 读当前客户的地图服务连接。只读，不写。没配置就如实返回没配置，不从环境变量或别处补令牌。
 */
public interface GisProviderConnectionQueryService {

    /** 五种地图服务各一行（固定顺序：天地图、Cesium、GeoServer、超图、ArcGIS）。 */
    List<GisProviderConnectionRespVO> listConnections();
}
