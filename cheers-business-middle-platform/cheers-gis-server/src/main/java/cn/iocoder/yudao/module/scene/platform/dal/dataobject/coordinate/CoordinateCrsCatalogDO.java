package cn.iocoder.yudao.module.scene.platform.dal.dataobject.coordinate;

import cn.cheers.x.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.JsonStringTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName(value = "coordinate_crs_catalog", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class CoordinateCrsCatalogDO extends BaseDO {

    @TableId
    private Long id;

    private String crsCode;

    private String crsName;

    private String crsType;

    private String datumCode;

    private String ellipsoidCode;

    private String areaScope;

    private String recommendedEngine;

    private String recommendedSceneType;

    private String warningMessage;

    private Boolean enabledFlag;

    private Integer sortNo;

    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String metadataJson;
}
