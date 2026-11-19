package cn.cheers.x.scene.platform.dal.dataobject.basemap;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 一个客户的一种地图服务连接（GIS 管理页保存）。
 * 这是地图令牌和瓦片地址的唯一存放处。前端服务器和浏览器都不另存一份。
 */
@TableName("gis_provider_connection")
@Data
@EqualsAndHashCode(callSuper = true)
public class GisProviderConnectionDO extends TenantBaseDO {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 地图服务编码，见 GisProviderCodeEnum */
    private String providerCode;

    /** 访问令牌 */
    private String token;

    /** 瓦片地址模板（只用于 GeoServer、超图、ArcGIS） */
    private String urlTemplate;

    /** 本地瓦片服务的令牌参数名 */
    private String tokenQueryParam;

    /** 界面显示名 */
    private String label;

    /** Cesium Ion 影像资源编号（只用于 Cesium） */
    private Integer imageryAssetId;
}
