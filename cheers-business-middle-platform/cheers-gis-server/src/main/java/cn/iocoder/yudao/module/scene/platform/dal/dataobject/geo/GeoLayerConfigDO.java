package cn.iocoder.yudao.module.scene.platform.dal.dataobject.geo;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.JsonStringTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName(value = "geo_layer_config", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class GeoLayerConfigDO extends TenantBaseDO {

    @TableId
    private Long id;

    private Long sceneId;

    private String layerKey;

    private String layerName;

    private String layerType;

    private String providerType;

    private String engineProfile;

    private Boolean enabledFlag;

    private Integer sortNo;

    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String configJson;

    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String metadataJson;
}
