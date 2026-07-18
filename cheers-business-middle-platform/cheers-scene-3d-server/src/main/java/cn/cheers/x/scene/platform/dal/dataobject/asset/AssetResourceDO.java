package cn.cheers.x.scene.platform.dal.dataobject.asset;

import cn.cheers.x.framework.mybatis.core.dataobject.BaseDO;
import cn.cheers.x.scene.platform.dal.dataobject.JsonStringTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName(value = "asset_resource", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class AssetResourceDO extends BaseDO {

    @TableId
    private Long id;

    private String assetCode;

    private String assetName;

    private String assetType;

    private String assetUrl;

    private String previewUrl;

    private String format;

    private String engineProfile;

    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String metadataJson;

    private Integer status;
}
